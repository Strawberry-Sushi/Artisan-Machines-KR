package com.wdiscute.artisan.registry;

import com.wdiscute.artisan.Artisan;
import com.wdiscute.artisan.upgrades.AbstractUpgrade;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.function.Supplier;

public interface ArtisanDataAttachments
{
    DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(
            NeoForgeRegistries.ATTACHMENT_TYPES, Artisan.MOD_ID);

    Supplier<AttachmentType<List<AbstractUpgrade>>> UPGRADES = ATTACHMENT_TYPES.register(
            "timeless_hearts", () -> AttachmentType.builder(() -> List.<AbstractUpgrade>of())
                    .serialize(AbstractUpgrade.UPGRADE_CODEC.listOf())
                    .copyOnDeath()
                    .build()
    );

    static void register(IEventBus eventBus)
    {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
