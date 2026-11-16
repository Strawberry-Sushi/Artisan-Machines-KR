package com.wdiscute.artisan;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ArtisanConfig
{
    private static final ModConfigSpec.Builder BUILDER_CLIENT = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue BOB_AMOUNT = BUILDER_CLIENT
            .comment("Controls much machines bob whilst working")
            .translation("artisan_machines.configuration.bob")
            .defineInRange("bob", 0.4d, 0d, 99d);

    public static final ModConfigSpec.DoubleValue BOB_AMOUNT_UPGRADED = BUILDER_CLIENT
            .comment("Controls much upgraded machines bob whilst working")
            .translation("artisan_machines.configuration.bob_upgrade")
            .defineInRange("bob_upgrade", 0.4d, 0d, 99d);

    static final ModConfigSpec SPEC = BUILDER_CLIENT.build();


    private static final ModConfigSpec.Builder BUILDER_SERVER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue TICK_DELAY = BUILDER_SERVER
            .comment("Controls how many ticks each machine waits before running ticking logic, higher means better performance")
            .translation("artisan_machines.configuration.tick_delay")
            .defineInRange("tick_delay", 20, 1, 9999);

    static final ModConfigSpec SPEC_SERVER = BUILDER_SERVER.build();

}
