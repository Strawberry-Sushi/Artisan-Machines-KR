package com.wdiscute.artisan.machines;

import com.mojang.serialization.MapCodec;
import com.wdiscute.artisan.recipe.AbstractArtisanRecipe;
import com.wdiscute.artisan.registry.ArtisanDataMaps;
import com.wdiscute.artisan.upgrades.MachineSettings;
import com.wdiscute.artisan.upgrades.MachineUpgrade;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMachineBlock extends BaseEntityBlock
{
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<State> STATE = EnumProperty.create("state", State.class);

    public AbstractMachineBlock(Properties properties)
    {
        super(properties
                .noOcclusion()
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec()
    {
        return null;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        ItemInteractionResult result = null;
        BlockState blockState = level.getBlockState(pos);

        //idle state interaction logic
        if (blockState.getValue(STATE).equals(State.IDLE))
        {
            //"place upgrade" checks ⏬
            //if the item has a machine upgrade
            MachineUpgrade machineUpgrade = ArtisanDataMaps.getOrDefault(stack);
            if (!machineUpgrade.equals(MachineUpgrade.EMPTY))
            {
                //if the machine upgrade accepts this machine type
                if (level.getBlockEntity(pos) instanceof AbstractMachineBlockEntity adbe && machineUpgrade.machines().contains(adbe.getType()))
                {
                    //if machine can use repeated upgrades, OR none of the upgrade items already inside match the item attempting to be put in
                    MachineSettings machineSettings = ArtisanDataMaps.getOrDefault(adbe);
                    if (machineSettings.canUseRepeatedUpgrades() || adbe.upgrades.stream().noneMatch(o -> o.is(stack.getItem())))
                    {
                        //if there is space for another upgrade
                        if (machineSettings.upgradeSlots() > adbe.upgrades.size())
                        {
                            //put upgrade
                            if(level instanceof ServerLevel sl)
                            {
                                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS);
                                sl.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f,
                                        20, 0.3f, 1, 0.3f, 0);
                                adbe.putUpgrade(stack, machineUpgrade);
                                stack.shrink(1);
                                return ItemInteractionResult.SUCCESS;
                            }

                            return ItemInteractionResult.SUCCESS;
                        }
                    }
                }
            }


            //store count as we decrease it later on
            int count = stack.getCount();
            //cycle for item count
            for (int i = 0; i < count; i++)
            {
                //continue if one of the previous inputs made the machine start working
                if (level.getBlockState(pos).getValue(AbstractMachineBlock.STATE).equals(State.WORKING)) continue;

                if (level.getBlockEntity(pos) instanceof AbstractMachineBlockEntity adbe)
                {
                    //get all recipes using item
                    var recipesUsingItem = level.getRecipeManager().getAllRecipesFor(adbe.getRecipeType())
                            .stream()
                            .filter(holder -> holder.value().getIngredients()
                                    .stream().anyMatch(o -> o.test(stack))
                            )
                            .map(RecipeHolder::value)
                            .toList();

                    List<ItemStack> inventory = new ArrayList<>(player.getInventory().items);
                    inventory.addAll(player.getInventory().offhand);

                    for (AbstractArtisanRecipe recipe : recipesUsingItem)
                    {
                        if (!recipe.upgradesMatches(adbe.upgrades)) continue;
                        if (recipe.hasBlacklistedUpgrade(adbe.upgrades)) continue;

                        int[] toConsume = matchIngredients(recipe.getIngredients(), inventory);
                        if (toConsume == null) continue;

                        if(level.isClientSide)
                            return ItemInteractionResult.SUCCESS;

                        //start recipe on block entity
                        adbe.startRecipe(recipe);
                        level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS);

                        //consume stacks and store on machine
                        if (!player.isCreative())
                        {
                            for (int s = 0; s < toConsume.length; s++)
                            {
                                int amount = toConsume[s];
                                if (amount <= 0) continue;

                                ItemStack stackToStore = inventory.get(s);
                                adbe.putItem(stackToStore.copyWithCount(amount));
                                stackToStore.shrink(amount);
                            }
                        }

                        return ItemInteractionResult.SUCCESS;
                    }

                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                }
            }
        }

        //working state interaction logic
        if (blockState.getValue(STATE).equals(State.WORKING))
        {
            if (!level.isClientSide && level.getBlockEntity(pos) instanceof AbstractMachineBlockEntity adbe)
            {
                //dirty check to prevent crashes/not saving the world to lock that block into negative hours remaining
                if (adbe.getHoursRemaining() < 0)
                    level.setBlockAndUpdate(pos, state.setValue(AbstractMachineBlock.STATE, AbstractMachineBlock.State.IDLE));
                else
                    displayTimeRemainingClientMessage(player, adbe);
            }

            return ItemInteractionResult.SUCCESS;
        }

        //harvestable state interaction logic
        if (blockState.getValue(STATE).equals(State.HARVESTABLE))
        {
            if (!level.isClientSide && level.getBlockEntity(pos) instanceof AbstractMachineBlockEntity adbe)
            {
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS);
                //all harvesting logic is done on block entity
                adbe.harvest(level);
            }
            return ItemInteractionResult.SUCCESS;
        }


        if (result != null)
            return result;
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    //checks if the player has enough items needed to meet all the ingredients,
    //returns null if not, int[] with the slots to be consumed later
    public static int[] matchIngredients(List<Ingredient> ingredients, List<ItemStack> stacks)
    {
        List<Ingredient> required = ingredients.stream().filter(i -> !i.isEmpty()).toList();

        List<List<Integer>> owners = new ArrayList<>(stacks.size());
        for (int i = 0; i < stacks.size(); i++)
            owners.add(new ArrayList<>());

        for (int i = 0; i < required.size(); i++)
            if (!tryAssign(required, stacks, i, owners, new boolean[stacks.size()]))
                return null;

        int[] toConsume = new int[stacks.size()];
        for (int s = 0; s < toConsume.length; s++)
            toConsume[s] = owners.get(s).size();
        return toConsume;
    }

    //algorithm to "assign" an itemstack to a consumed/checked off ingredient
    private static boolean tryAssign(List<Ingredient> required, List<ItemStack> stacks, int ingredientIdx,
                                     List<List<Integer>> owners, boolean[] visited)
    {
        Ingredient ingredient = required.get(ingredientIdx);

        for (int s = 0; s < stacks.size(); s++)
        {
            if (visited[s]) continue;

            ItemStack stack = stacks.get(s);
            if (stack.isEmpty() || !ingredient.test(stack)) continue;

            visited[s] = true;
            List<Integer> assigned = owners.get(s);

            if (assigned.size() < stack.getCount())
            {
                assigned.add(ingredientIdx);
                return true;
            }

            for (int k = 0; k < assigned.size(); k++)
            {
                if (tryAssign(required, stacks, assigned.get(k), owners, visited))
                {
                    assigned.set(k, ingredientIdx);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston)
    {
        //prevents running on setBlock() etc
        if (state.is(newState.getBlock())) return;

        if (level.getBlockEntity(pos) instanceof AbstractMachineBlockEntity ambe)
        {
            //drop result if harvestable
            if (state.getValue(AbstractMachineBlock.STATE).equals(State.HARVESTABLE))
            {
                for (ItemStack stack : ambe.getHarvestResults())
                {
                    Vec3 vec3 = Vec3.atLowerCornerWithOffset(pos, 0.5, 1.01, 0.5).offsetRandom(level.random, 0.7F);
                    ItemEntity itementity = new ItemEntity(level, vec3.x(), vec3.y(), vec3.z(), stack);
                    itementity.setDefaultPickUpDelay();
                    level.addFreshEntity(itementity);
                }
            }
            //drop items stored if working/idle
            else
            {
                for (ItemStack stack : ambe.getItems())
                {
                    Vec3 vec3 = Vec3.atLowerCornerWithOffset(pos, 0.5, 1.01, 0.5).offsetRandom(level.random, 0.7F);
                    ItemEntity itementity = new ItemEntity(level, vec3.x(), vec3.y(), vec3.z(), stack);
                    itementity.setDefaultPickUpDelay();
                    level.addFreshEntity(itementity);
                }
            }

            //drop upgrades regardless
            for (ItemStack stack : ambe.upgrades)
            {
                Vec3 vec3 = Vec3.atLowerCornerWithOffset(pos, 0.5, 1.01, 0.5).offsetRandom(level.random, 0.7F);
                ItemEntity itementity = new ItemEntity(level, vec3.x(), vec3.y(), vec3.z(), stack);
                itementity.setDefaultPickUpDelay();
                level.addFreshEntity(itementity);
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    public void displayTimeRemainingClientMessage(Player player, AbstractMachineBlockEntity adbe)
    {
        if (!adbe.getResultItem().isEmpty())
            player.displayClientMessage(Component.translatable("block.artisan_machines.machine.currently_making")
                    .append(adbe.recipeResult.getFirst().stack().getHoverName())
                    .append(Component.translatable("block.artisan_machines.machine.hours", adbe.getHoursRemaining())), true);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
        builder.add(STATE);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.INVISIBLE;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType)
    {
        return AbstractMachineBlockEntity.getTicker(level, state);
    }

    public enum State implements StringRepresentable
    {
        IDLE("idle"),
        WORKING("working"),
        HARVESTABLE("harvestable");

        String name;

        State(String name)
        {
            this.name = name;
        }

        @Override
        public String getSerializedName()
        {
            return name;
        }
    }
}
