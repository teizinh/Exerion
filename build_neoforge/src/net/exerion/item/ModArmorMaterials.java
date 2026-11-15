/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.Identifier
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.equipment.ArmorMaterial
 *  net.minecraft.world.item.equipment.ArmorType
 *  net.minecraft.world.item.equipment.EquipmentAsset
 *  net.minecraft.world.item.equipment.EquipmentAssets
 */
package net.exerion.item;

import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

public class ModArmorMaterials {
    public static final ResourceKey<EquipmentAsset> EXERION_ASSET_KEY = ResourceKey.create((ResourceKey)EquipmentAssets.ROOT_ID, (Identifier)Identifier.fromNamespaceAndPath((String)"exerion", (String)"exerion_suit"));
    public static final ArmorMaterial EXERION = new ArmorMaterial(15, Map.of(ArmorType.HELMET, 3, ArmorType.CHESTPLATE, 8, ArmorType.LEGGINGS, 6, ArmorType.BOOTS, 3), 15, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0f, 0.1f, TagKey.create((ResourceKey)Registries.ITEM, (Identifier)Identifier.fromNamespaceAndPath((String)"exerion", (String)"exerion_ingots")), EXERION_ASSET_KEY);
}

