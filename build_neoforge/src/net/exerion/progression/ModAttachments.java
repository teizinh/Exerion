/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Registry
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.neoforge.attachment.AttachmentType
 *  net.neoforged.neoforge.registries.DeferredRegister
 *  net.neoforged.neoforge.registries.NeoForgeRegistries
 */
package net.exerion.progression;

import java.util.function.Supplier;
import net.exerion.progression.TechnologyEra;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create((Registry)NeoForgeRegistries.ATTACHMENT_TYPES, (String)"exerion");
    public static final Supplier<AttachmentType<TechnologyEra>> PLAYER_ERA = ATTACHMENT_TYPES.register("player_era", () -> AttachmentType.builder(() -> TechnologyEra.STONE).serialize(TechnologyEra.CODEC.fieldOf("era")).copyOnDeath().build());

    public static void registerAttachments(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}

