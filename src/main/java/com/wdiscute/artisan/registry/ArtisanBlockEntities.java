package com.wdiscute.artisan.registry;

import com.wdiscute.artisan.Artisan;
import com.wdiscute.artisan.machines.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface ArtisanBlockEntities
{
    DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Artisan.MOD_ID);

    DeferredHolder<BlockEntityType<?>, BlockEntityType<LoomBlockEntity>>              LOOM = register("loom", ArtisanBlocks.LOOM, LoomBlockEntity::new);
    DeferredHolder<BlockEntityType<?>, BlockEntityType<CheesePressBlockEntity>>       CHEESE_PRESS = register("cheese_press", ArtisanBlocks.CHEESE_PRESS, CheesePressBlockEntity::new);
    DeferredHolder<BlockEntityType<?>, BlockEntityType<WineKegBlockEntity>>           WINE_KEG = register("wine_keg", ArtisanBlocks.WINE_KEG, WineKegBlockEntity::new);
    DeferredHolder<BlockEntityType<?>, BlockEntityType<AgingCaskBlockEntity>>         AGING_CASK = register("aging_cask", ArtisanBlocks.AGING_CASK, AgingCaskBlockEntity::new);
    DeferredHolder<BlockEntityType<?>, BlockEntityType<AncientCaskBlockEntity>>       ANCIENT_CASK = register("ancient_cask", ArtisanBlocks.ANCIENT_CASK, AncientCaskBlockEntity::new);
    DeferredHolder<BlockEntityType<?>, BlockEntityType<CrystalariumBlockEntity>>      CRYSTALARIUM = register("crystalarium", ArtisanBlocks.CRYSTALARIUM, CrystalariumBlockEntity::new);
    DeferredHolder<BlockEntityType<?>, BlockEntityType<DeluxeWormFarmBlockEntity>>    DELUXE_WORM_FARM = register("deluxe_worm_farm", ArtisanBlocks.DELUXE_WORM_FARM, DeluxeWormFarmBlockEntity::new);
    DeferredHolder<BlockEntityType<?>, BlockEntityType<FishSmokerBlockEntity>>        FISH_SMOKER = register("fish_smoker", ArtisanBlocks.FISH_SMOKER, FishSmokerBlockEntity::new);
    DeferredHolder<BlockEntityType<?>, BlockEntityType<DehydratorBlockEntity>>        DEHYDRATOR = register("dehydrator", ArtisanBlocks.DEHYDRATOR, DehydratorBlockEntity::new);
    DeferredHolder<BlockEntityType<?>, BlockEntityType<MayonnaiseMachineBlockEntity>> MAYONNAISE_MACHINE = register("mayonnaise_machine", ArtisanBlocks.MAYONNAISE_MACHINE, MayonnaiseMachineBlockEntity::new);
    DeferredHolder<BlockEntityType<?>, BlockEntityType<PreservesJarBlockEntity>>      PRESERVES_JAR = register("preserves_jar", ArtisanBlocks.PRESERVES_JAR, PreservesJarBlockEntity::new);
    DeferredHolder<BlockEntityType<?>, BlockEntityType<SeedMakerBlockEntity>>         SEED_MAKER = register("seed_maker", ArtisanBlocks.SEED_MAKER, SeedMakerBlockEntity::new);
    DeferredHolder<BlockEntityType<?>, BlockEntityType<RecyclingMachineBlockEntity>>  RECYCLING_MACHINE = register("recycling_machine", ArtisanBlocks.RECYCLING_MACHINE, RecyclingMachineBlockEntity::new);
    DeferredHolder<BlockEntityType<?>, BlockEntityType<BaitMakerBlockEntity>>         BAIT_MAKER = register("bait_maker", ArtisanBlocks.BAIT_MAKER, BaitMakerBlockEntity::new);
    DeferredHolder<BlockEntityType<?>, BlockEntityType<OilMakerBlockEntity>>          OIL_MAKER = register("oil_maker", ArtisanBlocks.OIL_MAKER, OilMakerBlockEntity::new);
    DeferredHolder<BlockEntityType<?>, BlockEntityType<ChargingRodBlockEntity>>       CHARGING_ROD = register("charging_rod", ArtisanBlocks.CHARGING_ROD, ChargingRodBlockEntity::new);


    static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(String name, Supplier<? extends Block> block, BlockEntityType.BlockEntitySupplier<T> factory)
    {
        return BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of(factory, block.get()).build(null));
    }

    static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
