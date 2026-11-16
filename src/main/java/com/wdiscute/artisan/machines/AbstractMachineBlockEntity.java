package com.wdiscute.artisan.machines;

import com.mojang.serialization.Codec;
import com.wdiscute.artisan.ArtisanConfig;
import com.wdiscute.artisan.ChancedStack;
import com.wdiscute.artisan.recipe.AbstractArtisanRecipe;
import com.wdiscute.artisan.registry.ArtisanDataMaps;
import com.wdiscute.artisan.upgrades.AbstractUpgrade;
import com.wdiscute.artisan.upgrades.MachineUpgrade;
import com.wdiscute.utils.MaybeStack;
import com.wdiscute.utils.ValueHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMachineBlockEntity extends BlockEntity
{
    public int tickOffset = -1;
    public long lastTickHour = -1;
    public int hoursRemaining = -1;

    public List<MaybeStack> upgrades = new ArrayList<>();
    public List<MaybeStack> items = new ArrayList<>();
    public List<ChancedStack> recipeResult = List.of();

    public AbstractMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState)
    {
        super(type, pos, blockState);
    }

    public boolean isWorking()
    {
        BlockState blockState = getBlockState();
        return blockState.getOptionalValue(AbstractMachineBlock.STATE)
                .orElse(AbstractMachineBlock.State.IDLE).equals(AbstractMachineBlock.State.WORKING);
    }

    public int getHoursRemaining()
    {
        return hoursRemaining;
    }

    public boolean shouldTickNonWorking()
    {
        return false;
    }

    public void putItem(ItemStack itemStack)
    {
        items.add(new MaybeStack(itemStack.copyWithCount(1)));
        sync();
    }

    public void putUpgrade(ItemStack itemStack, MachineUpgrade machineUpgrade)
    {
        upgrades.add(new MaybeStack(itemStack.copyWithCount(1)));
        invalidateUpgradeCache();
        machineUpgrade.upgrades().forEach(o -> o.onAdd(this));
        sync();
    }


    private @Nullable List<AbstractUpgrade> upgradesCache;

    public void invalidateUpgradeCache() { upgradesCache = null; }

    public List<AbstractUpgrade> getUpgrades()
    {
        if (upgradesCache == null)
        {
            List<AbstractUpgrade> list = new ArrayList<>(ArtisanDataMaps.getOrDefault(this).baseUpgrades());
            for (MaybeStack stack : upgrades)
                list.addAll(ArtisanDataMaps.getOrDefault(stack.toStack()).upgrades());
            upgradesCache = List.copyOf(list);
        }
        return upgradesCache;
    }

    public List<ItemStack> getHarvestResults()
    {
        List<ItemStack> results = new ArrayList<>();

        for (ChancedStack chancedStack : recipeResult)
            if (level.getRandom().nextFloat() < chancedStack.chance())
                results.add(chancedStack.stack().toStack());

        for (AbstractUpgrade upgrade : getUpgrades())
            results = upgrade.modifyHarvestResult(this, results);

        return results;
    }

    public void harvest(List<AbstractUpgrade> playerUpgrades)
    {
        getUpgrades().forEach(o -> o.onHarvest(this));
        playerUpgrades.forEach(o -> o.onHarvest(this));

        //spawn items
        getHarvestResults().forEach(o ->
        {
            Vec3 vec3 = Vec3.atLowerCornerWithOffset(worldPosition, 0.5, 1.01, 0.5).offsetRandom(level.random, 0.7F);
            ItemEntity itementity = new ItemEntity(level, vec3.x(), vec3.y(), vec3.z(), o);
            itementity.setDefaultPickUpDelay();
            level.addFreshEntity(itementity);
        });

        items.clear();
        recipeResult = List.of();

        hoursRemaining = -1;

        //set back to idle
        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(AbstractMachineBlock.STATE, AbstractMachineBlock.State.IDLE));
        getUpgrades().forEach(o -> o.onFinishedHarvest(this));
        sync();
    }

    public void startRecipe(AbstractArtisanRecipe recipe)
    {
        recipeResult = recipe.getResult();
        //+1 as a recipe put 1 second before daily reset shouldn't be finished
        hoursRemaining = recipe.getHours() + 1;
        lastTickHour = -1;
        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(AbstractMachineBlock.STATE, AbstractMachineBlock.State.WORKING));
        getUpgrades().forEach(o -> o.onRecipeStarted(recipe, this));

        sync();
    }

    public void sync()
    {
        setChanged();
        if (level != null)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public List<MaybeStack> getItems()
    {
        return items;
    }

    public List<ChancedStack> getResultItem()
    {
        return recipeResult;
    }

    public abstract RecipeType<? extends AbstractArtisanRecipe> getRecipeType();

    /**
     * ticks with an offset based on the tick delay config - does NOT tick every tick
     */
    public void tick()
    {
        getUpgrades().forEach(o -> o.onTick(this));
    }

    /**
     * ticks hourly, adds one to last tick hour so if the machine was unloaded all days are ticked
     */
    public void hourlyTick(long hour, BlockState state)
    {
        if (lastTickHour == -1)
            lastTickHour = hour;
        else
            lastTickHour++;

        getUpgrades().forEach(o -> o.onHourlyTick(this, lastTickHour));

        //if working
        if (state.getValue(AbstractMachineBlock.STATE).equals(AbstractMachineBlock.State.WORKING))
            hoursRemaining--;

        //if recipe finished
        if (state.getValue(AbstractMachineBlock.STATE).equals(AbstractMachineBlock.State.WORKING) && hoursRemaining <= 0)
        {
            if (level instanceof ServerLevel sl)
            {
                BlockPos pos = getBlockPos();
                sl.sendParticles(ParticleTypes.ASH, pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f,
                        20, 0.3f, 1, 0.3f, 0);
            }

            level.setBlockAndUpdate(worldPosition, state.setValue(AbstractMachineBlock.STATE, AbstractMachineBlock.State.HARVESTABLE));
        }

        sync();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        ValueHelper.store("last_tick_hour", Codec.LONG, lastTickHour, tag);
        ValueHelper.store("hours_remaining", Codec.INT, hoursRemaining, tag);

        ValueHelper.store("upgrades", MaybeStack.CODEC.listOf(), upgrades, tag);
        ValueHelper.store("items", MaybeStack.CODEC.listOf(), items, tag);
        ValueHelper.store("result", ChancedStack.CODEC.listOf(), recipeResult, tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        lastTickHour =  ValueHelper.read("last_tick_hour", Codec.LONG, tag).orElse(-1L);
        hoursRemaining = ValueHelper.read("hours_remaining", Codec.INT, tag).orElse(-1);

        upgrades = new ArrayList<>(ValueHelper.read("upgrades", MaybeStack.CODEC.listOf(), tag).orElse(List.of()));
        invalidateUpgradeCache();
        items = new ArrayList<>(ValueHelper.read("items", MaybeStack.CODEC.listOf(), tag).orElse(List.of()));
        recipeResult = new ArrayList<>(ValueHelper.read("result", ChancedStack.CODEC.listOf(), tag).orElse(List.of()));
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries)
    {
        return saveWithoutMetadata(registries);
    }

    private int getTickOffset(Level level)
    {
        if (tickOffset != -1) return tickOffset;
        tickOffset = level.getRandom().nextInt(ArtisanConfig.TICK_DELAY.get());
        return tickOffset;
    }

    public static <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level l)
    {
        //don't tick on client
        if (l.isClientSide) return null;

        return (level, pos, state, blockEntity) ->
        {
            AbstractMachineBlockEntity adbe = ((AbstractMachineBlockEntity) blockEntity);

            //only tick for working machines
            if (state.getValue(AbstractMachineBlock.STATE).equals(AbstractMachineBlock.State.WORKING) || adbe.shouldTickNonWorking())
            {
                //tick with offset
                int tickOffset = adbe.getTickOffset(level);
                if ((level.getDayTime() + tickOffset) % ArtisanConfig.TICK_DELAY.get() == 0)
                    adbe.tick();

                //daily tick
                long hour = (level.getDayTime() + tickOffset) / 1000;

                if (adbe.lastTickHour > hour)
                    adbe.lastTickHour = hour;

                if (adbe.lastTickHour < hour)
                    adbe.hourlyTick(hour, state);
            }
        };
    }
}
