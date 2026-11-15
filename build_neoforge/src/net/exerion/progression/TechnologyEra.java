/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 */
package net.exerion.progression;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum TechnologyEra {
    STONE("stone", 0),
    COPPER("copper", 1),
    IRON("iron", 2),
    MECHANICAL("mechanical", 3),
    STEEL("steel", 4),
    INDUSTRIAL("industrial", 5),
    DIGITAL("digital", 6),
    ADVANCED("advanced", 7),
    QUANTUM("quantum", 8),
    COSMIC("cosmic", 9);

    private final String id;
    private final int level;
    public static final Codec<TechnologyEra> CODEC;

    private TechnologyEra(String id, int level) {
        this.id = id;
        this.level = level;
    }

    public String getId() {
        return this.id;
    }

    public int getLevel() {
        return this.level;
    }

    public String getTranslationKey() {
        return "era.exerion." + this.id;
    }

    public MutableComponent getDisplayNameComponent() {
        return Component.translatable((String)this.getTranslationKey());
    }

    public boolean isAtLeast(TechnologyEra other) {
        return this.level >= other.level;
    }

    public static TechnologyEra fromId(String id) {
        for (TechnologyEra era : TechnologyEra.values()) {
            if (!era.id.equalsIgnoreCase(id)) continue;
            return era;
        }
        return STONE;
    }

    static {
        CODEC = Codec.STRING.xmap(TechnologyEra::fromId, TechnologyEra::getId);
    }
}

