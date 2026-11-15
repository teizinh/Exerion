/*
 * Decompiled with CFR 0.152.
 */
package net.exerion.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.imageio.ImageIO;

public class ResourceGenerator {
    public static void main(String[] args) {
        String[] ores;
        String[] selfDropBlocks;
        String baseDir = "src/main/resources/data/exerion";
        File lootTableDir = new File(baseDir + "/loot_table/blocks");
        lootTableDir.mkdirs();
        for (String block : selfDropBlocks = new String[]{"exerion_casing", "exerion_block", "raw_exerion_block", "exerion_frame", "gear_press", "coal_boiler", "steam_turbine", "crusher", "progression_terminal", "steel_block", "steel_casing", "electric_generator", "copper_cable", "steel_cable", "exerion_cable", "electric_crusher", "copper_solar_panel", "steel_solar_panel", "exerion_solar_panel", "digital_storage_terminal", "batbox", "compressor", "alloy_smelter", "frequency_ender_chest", "mining_laser", "geothermal_generator", "shaft", "hand_crank", "water_wheel", "mechanical_chute", "pump", "frequency_ender_tank", "electric_furnace"}) {
            String json = "{\n  \"type\": \"minecraft:block\",\n  \"pools\": [\n    {\n      \"rolls\": 1.0,\n      \"entries\": [\n        {\n          \"type\": \"minecraft:item\",\n          \"name\": \"exerion:" + block + "\"\n        }\n      ],\n      \"conditions\": [\n        {\n          \"condition\": \"minecraft:survives_explosion\"\n        }\n      ]\n    }\n  ]\n}";
            ResourceGenerator.writeFile(new File(lootTableDir, block + ".json"), json);
        }
        for (String ore : ores = new String[]{"exerion_ore", "deepslate_exerion_ore"}) {
            String json = "{\n  \"type\": \"minecraft:block\",\n  \"pools\": [\n    {\n      \"rolls\": 1.0,\n      \"entries\": [\n        {\n          \"type\": \"minecraft:item\",\n          \"name\": \"exerion:raw_exerion\"\n        }\n      ],\n      \"conditions\": [\n        {\n          \"condition\": \"minecraft:survives_explosion\"\n        }\n      ]\n    }\n  ]\n}";
            ResourceGenerator.writeFile(new File(lootTableDir, (String)ore + ".json"), json);
        }
        File recipeDir = new File(baseDir + "/recipe");
        recipeDir.mkdirs();
        ResourceGenerator.writeShapedRecipe(recipeDir, "exerion_block", "exerion:exerion_block", 1, new String[]{"III", "III", "III"}, "I", "exerion:exerion_ingot");
        ResourceGenerator.writeSmeltingRecipe(recipeDir, "exerion_ingot", "exerion:raw_exerion", "exerion:exerion_ingot", 0.7f, 200);
        ResourceGenerator.writeBlastingRecipe(recipeDir, "exerion_ingot_blasting", "exerion:raw_exerion", "exerion:exerion_ingot", 0.7f, 100);
        ResourceGenerator.writeSmeltingRecipe(recipeDir, "steel_ingot", "exerion:steel_compound", "exerion:steel_ingot", 1.0f, 300);
        ResourceGenerator.writeBlastingRecipe(recipeDir, "steel_ingot_blasting", "exerion:steel_compound", "exerion:steel_ingot", 1.0f, 150);
        ResourceGenerator.writeShapedRecipe(recipeDir, "steel_compound", "exerion:steel_compound", 1, new String[]{" C ", " I ", "   "}, "C", "exerion:coal_dust", "I", "minecraft:iron_ingot");
        ResourceGenerator.writeShapedRecipe(recipeDir, "iron_gear", "exerion:iron_gear", 1, new String[]{" I ", "I I", " I "}, "I", "minecraft:iron_ingot");
        ResourceGenerator.writeShapedRecipe(recipeDir, "copper_gear", "exerion:copper_gear", 1, new String[]{" C ", "C C", " C "}, "C", "minecraft:copper_ingot");
        ResourceGenerator.writeShapedRecipe(recipeDir, "exerion_gear", "exerion:exerion_gear", 1, new String[]{" E ", "E E", " E "}, "E", "exerion:exerion_ingot");
        ResourceGenerator.writeShapedRecipe(recipeDir, "crusher", "exerion:crusher", 1, new String[]{"SSS", "SFS", "SSS"}, "S", "minecraft:cobblestone", "F", "minecraft:flint");
        ResourceGenerator.writeShapedRecipe(recipeDir, "gear_press", "exerion:gear_press", 1, new String[]{"SSS", "SPS", "SSS"}, "S", "minecraft:stone", "Dust", "minecraft:piston");
        ResourceGenerator.writeShapedRecipe(recipeDir, "coal_boiler", "exerion:coal_boiler", 1, new String[]{"CCC", "CFC", "CCC"}, "C", "minecraft:copper_ingot", "F", "minecraft:furnace");
        ResourceGenerator.writeShapedRecipe(recipeDir, "steam_turbine", "exerion:steam_turbine", 1, new String[]{"SGS", "GTG", "SGS"}, "S", "exerion:steel_ingot", "G", "exerion:iron_gear", "T", "exerion:exerion_core");
        ResourceGenerator.writeShapedRecipe(recipeDir, "electric_generator", "exerion:electric_generator", 1, new String[]{"CGC", "GTG", "CGC"}, "C", "minecraft:copper_ingot", "G", "exerion:iron_gear", "T", "exerion:steam_turbine");
        ResourceGenerator.writeShapedRecipe(recipeDir, "copper_cable", "exerion:copper_cable", 6, new String[]{"WWW", "CCC", "WWW"}, "W", "minecraft:white_wool", "C", "minecraft:copper_ingot");
        ResourceGenerator.writeShapedRecipe(recipeDir, "steel_cable", "exerion:steel_cable", 6, new String[]{"WWW", "SSS", "WWW"}, "W", "minecraft:grey_wool", "S", "exerion:steel_ingot");
        ResourceGenerator.writeShapedRecipe(recipeDir, "electric_crusher", "exerion:electric_crusher", 1, new String[]{"SCS", "CMC", "SCS"}, "S", "exerion:steel_ingot", "C", "exerion:circuit_board", "M", "exerion:crusher");
        ResourceGenerator.writeShapedRecipe(recipeDir, "progression_terminal", "exerion:progression_terminal", 1, new String[]{"RCR", "CGC", "RCR"}, "R", "minecraft:redstone", "C", "minecraft:diamond", "G", "exerion:exerion_core");
        ResourceGenerator.writeShapedRecipe(recipeDir, "speed_upgrade", "exerion:speed_upgrade", 1, new String[]{"RCR", "CGC", "RCR"}, "R", "minecraft:redstone", "C", "minecraft:gold_ingot", "G", "exerion:iron_gear");
        ResourceGenerator.writeShapedRecipe(recipeDir, "efficiency_upgrade", "exerion:efficiency_upgrade", 1, new String[]{"RCR", "CGC", "RCR"}, "R", "minecraft:redstone", "C", "minecraft:gold_ingot", "G", "exerion:copper_gear");
        ResourceGenerator.writeShapedRecipe(recipeDir, "steam_boots", "exerion:steam_boots", 1, new String[]{"S S", "C C", "S S"}, "S", "exerion:steel_ingot", "C", "exerion:steam_crystal");
        ResourceGenerator.writeShapedRecipe(recipeDir, "electric_plate", "exerion:electric_plate", 1, new String[]{"S S", "SCS", "PEP"}, "S", "exerion:steel_ingot", "C", "exerion:circuit_board", "Dust", "exerion:exerion_plate", "E", "exerion:exerion_core");
        ResourceGenerator.writeShapedRecipe(recipeDir, "logic_helmet", "exerion:logic_helmet", 1, new String[]{"SCS", "LSL", " E "}, "S", "exerion:steel_ingot", "C", "exerion:circuit_board", "L", "exerion:logic_chip", "E", "exerion:era_scanner");
        ResourceGenerator.writeShapedRecipe(recipeDir, "wrench", "exerion:wrench", 1, new String[]{"  I", " II", "I  "}, "I", "minecraft:iron_ingot");
        ResourceGenerator.writeShapedRecipe(recipeDir, "era_scanner", "exerion:era_scanner", 1, new String[]{" G ", " C ", " L "}, "G", "minecraft:glass", "C", "exerion:circuit_board", "L", "exerion:logic_chip");
        ResourceGenerator.writeShapedRecipe(recipeDir, "steel_casing", "exerion:steel_casing", 1, new String[]{"SSS", "SIS", "SSS"}, "S", "exerion:steel_ingot", "I", "minecraft:iron_block");
        ResourceGenerator.writeShapedRecipe(recipeDir, "exerion_casing", "exerion:exerion_casing", 1, new String[]{"PPP", "PCP", "PPP"}, "Dust", "exerion:exerion_plate", "C", "exerion:steel_casing");
        ResourceGenerator.writeShapedRecipe(recipeDir, "exerion_frame", "exerion:exerion_frame", 1, new String[]{"RER", "ERE", "RER"}, "R", "exerion:exerion_rod", "E", "exerion:exerion_ingot");
        ResourceGenerator.writeShapedRecipe(recipeDir, "exerion_cable", "exerion:exerion_cable", 6, new String[]{"EEE", "CSC", "EEE"}, "E", "exerion:exerion_ingot", "C", "exerion:steel_cable", "S", "minecraft:redstone");
        ResourceGenerator.writeShapedRecipe(recipeDir, "copper_solar_panel", "exerion:copper_solar_panel", 1, new String[]{"GGG", "CCC", "WWW"}, "G", "minecraft:glass", "C", "minecraft:copper_ingot", "W", "exerion:copper_cable");
        ResourceGenerator.writeShapedRecipe(recipeDir, "steel_solar_panel", "exerion:steel_solar_panel", 1, new String[]{"GGG", "SSS", "WWW"}, "G", "minecraft:glass", "S", "exerion:steel_ingot", "W", "exerion:steel_cable");
        ResourceGenerator.writeShapedRecipe(recipeDir, "exerion_solar_panel", "exerion:exerion_solar_panel", 1, new String[]{"GGG", "PPP", "WWW"}, "G", "minecraft:glass", "Dust", "exerion:exerion_plate", "W", "exerion:exerion_cable");
        ResourceGenerator.writeShapedRecipe(recipeDir, "exerion_guide_book", "exerion:exerion_guide_book", 1, new String[]{"   ", " BI", "   "}, "B", "minecraft:book", "I", "exerion:exerion_ingot");
        ResourceGenerator.writeShapedRecipe(recipeDir, "digital_storage_terminal", "exerion:digital_storage_terminal", 1, new String[]{"ECE", "CPC", "ECE"}, "E", "exerion:exerion_plate", "C", "exerion:circuit_board", "Dust", "exerion:progression_terminal");
        ResourceGenerator.writeShapedRecipe(recipeDir, "batbox", "exerion:batbox", 1, new String[]{"CCC", "WGW", "WWW"}, "C", "exerion:copper_cable", "W", "minecraft:oak_planks", "G", "exerion:copper_gear");
        ResourceGenerator.writeShapedRecipe(recipeDir, "electric_mining_drill", "exerion:electric_mining_drill", 1, new String[]{"SGS", "SCS", "SRS"}, "S", "exerion:steel_ingot", "G", "exerion:iron_gear", "C", "exerion:circuit_board", "R", "exerion:exerion_rod");
        ResourceGenerator.writeShapedRecipe(recipeDir, "compressor", "exerion:compressor", 1, new String[]{"SCS", "SCS", "SMS"}, "S", "exerion:steel_ingot", "C", "exerion:circuit_board", "M", "exerion:electric_crusher");
        ResourceGenerator.writeShapedRecipe(recipeDir, "alloy_smelter", "exerion:alloy_smelter", 1, new String[]{"SCS", "SCS", "SMS"}, "S", "exerion:steel_ingot", "C", "exerion:circuit_board", "M", "exerion:coal_boiler");
        ResourceGenerator.writeShapedRecipe(recipeDir, "frequency_ender_chest", "exerion:frequency_ender_chest", 1, new String[]{"SSS", "OCO", "SSS"}, "S", "exerion:steel_ingot", "O", "minecraft:obsidian", "C", "minecraft:ender_pearl");
        ResourceGenerator.writeShapedRecipe(recipeDir, "mining_laser", "exerion:mining_laser", 1, new String[]{"SCS", "SLS", "SCS"}, "S", "exerion:steel_ingot", "C", "exerion:circuit_board", "L", "exerion:era_scanner");
        ResourceGenerator.writeShapedRecipe(recipeDir, "geothermal_generator", "exerion:geothermal_generator", 1, new String[]{"SOS", "SGS", "SCS"}, "S", "exerion:steel_ingot", "O", "minecraft:obsidian", "G", "exerion:coal_boiler", "C", "exerion:circuit_board");
        ResourceGenerator.writeShapedRecipe(recipeDir, "plasma_sword", "exerion:plasma_sword", 1, new String[]{"  C", " P ", "R  "}, "C", "exerion:circuit_board", "Dust", "exerion:carbon_plate", "R", "exerion:exerion_rod");
        ResourceGenerator.writeShapedRecipe(recipeDir, "shaft", "exerion:shaft", 4, new String[]{"   ", "WWW", "   "}, "W", "minecraft:oak_log");
        ResourceGenerator.writeShapedRecipe(recipeDir, "hand_crank", "exerion:hand_crank", 1, new String[]{" I ", "IWI", " W "}, "I", "minecraft:iron_ingot", "W", "minecraft:oak_log");
        ResourceGenerator.writeShapedRecipe(recipeDir, "water_wheel", "exerion:water_wheel", 1, new String[]{"IWI", "WCW", "IWI"}, "I", "minecraft:iron_ingot", "W", "minecraft:oak_planks", "C", "minecraft:cobblestone");
        File clientItemDir = new File("src/main/resources/assets/exerion/items");
        clientItemDir.mkdirs();
        ArrayList allItems = new ArrayList();
        Collections.addAll(allItems, selfDropBlocks);
        Collections.addAll(allItems, ores);
        Collections.addAll(allItems, "exerion_core", "raw_exerion", "exerion_ingot", "exerion_nugget", "exerion_gear", "exerion_plate", "exerion_rod", "copper_gear", "iron_gear", "steel_compound", "coal_dust", "iron_dust", "copper_dust", "gear_blueprint", "steel_ingot", "wrench", "steam_crystal", "circuit_board", "era_scanner", "logic_chip", "speed_upgrade", "efficiency_upgrade", "steam_boots", "electric_plate", "logic_helmet", "exerion_guide_book", "electric_mining_drill", "carbon_plate", "plasma_sword", "shaft", "hand_crank", "water_wheel", "mechanical_chute", "pump", "frequency_ender_tank", "electric_furnace");
        for (String item : allItems) {
            String json = "{\n  \"model\": {\n    \"type\": \"minecraft:model\",\n    \"model\": \"exerion:item/" + item + "\"\n  }\n}";
            ResourceGenerator.writeFile(new File(clientItemDir, item + ".json"), json);
        }
        File langDir = new File("src/main/resources/assets/exerion/lang");
        File enUsFile = new File(langDir, "en_us.json");
        if (enUsFile.exists()) {
            try {
                String[] locales;
                String enUsContent = new String(Files.readAllBytes(enUsFile.toPath()), StandardCharsets.UTF_8);
                for (String locale : locales = new String[]{"pt_pt", "pt_br", "es_es", "es_mx", "fr_fr", "de_de", "it_it", "ru_ru", "ja_jp", "ko_kr", "zh_cn", "zh_tw", "en_gb"}) {
                    ResourceGenerator.writeFile(new File(langDir, locale + ".json"), enUsContent);
                }
            }
            catch (Exception e) {
                System.err.println("Failed to copy translation files: " + e.getMessage());
            }
        }
        ResourceGenerator.generateArmorTextures();
        ResourceGenerator.generateMachineTextures();
        ResourceGenerator.generateEra1BlockAssets();
        ResourceGenerator.generateAdvancements();
        System.out.println("All resources generated successfully!");
    }

    private static void generateAdvancements() {
        File advDir = new File("src/main/resources/data/exerion/advancements/era");
        advDir.mkdirs();
        String[] eras = new String[]{"stone", "copper", "iron", "mechanical", "steel", "industrial", "digital", "advanced", "quantum", "cosmic"};
        String[] icons = new String[]{"minecraft:stone_pickaxe", "exerion:copper_gear", "exerion:iron_gear", "exerion:water_wheel", "exerion:steam_turbine", "exerion:electric_generator", "exerion:digital_storage_terminal", "exerion:exerion_solar_panel", "exerion:frequency_ender_chest", "exerion:mining_laser"};
        for (int i = 0; i < eras.length; ++i) {
            String parent;
            String era = eras[i];
            String icon = icons[i];
            String string = parent = i == 0 ? "exerion:root" : "exerion:era/" + eras[i - 1];
            String json = i == 0 ? "{\n  \"display\": {\n    \"icon\": { \"item\": \"" + icon + "\" },\n    \"title\": { \"translate\": \"era.exerion." + era + "\" },\n    \"description\": { \"translate\": \"era.exerion." + era + ".desc\" },\n    \"frame\": \"task\",\n    \"show_toast\": true,\n    \"announce_to_chat\": true,\n    \"hidden\": false,\n    \"background\": \"minecraft:textures/gui/advancements/backgrounds/stone.png\"\n  },\n  \"criteria\": {\n    \"has_item\": {\n      \"trigger\": \"minecraft:inventory_changed\",\n      \"conditions\": {\n        \"items\": [ { \"items\": [ \"exerion:raw_exerion\" ] } ]\n      }\n    }\n  }\n}" : "{\n  \"parent\": \"" + parent + "\",\n  \"display\": {\n    \"icon\": { \"item\": \"" + icon + "\" },\n    \"title\": { \"translate\": \"era.exerion." + era + "\" },\n    \"description\": { \"translate\": \"era.exerion." + era + ".desc\" },\n    \"frame\": \"" + (i == eras.length - 1 ? "challenge" : "task") + "\",\n    \"show_toast\": true,\n    \"announce_to_chat\": true,\n    \"hidden\": false\n  },\n  \"criteria\": {\n    \"unlocked\": {\n      \"trigger\": \"minecraft:impossible\"\n    }\n  }\n}";
            ResourceGenerator.writeFile(new File(advDir, era + ".json"), json);
        }
    }

    private static File findClientJarRecursive(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    File found = ResourceGenerator.findClientJarRecursive(f);
                    if (found == null) continue;
                    return found;
                }
                if (!f.getName().equals("minecraft-client.jar")) continue;
                return f;
            }
        }
        return null;
    }

    private static void generateArmorTextures() {
        try {
            File itemTexDir = new File("src/main/resources/assets/exerion/textures/item");
            itemTexDir.mkdirs();
            File armorTexDir = new File("src/main/resources/assets/exerion/textures/models/armor");
            armorTexDir.mkdirs();
            HashMap<Character, Integer> helmetColors = new HashMap<Character, Integer>();
            helmetColors.put(Character.valueOf('.'), 0);
            helmetColors.put(Character.valueOf('D'), -13684168);
            helmetColors.put(Character.valueOf('M'), -10986138);
            helmetColors.put(Character.valueOf('L'), -6378568);
            helmetColors.put(Character.valueOf('P'), -3014446);
            helmetColors.put(Character.valueOf('C'), -16715521);
            String[] helmetGrid = new String[]{".....DDDDDD.....", "....DMMMMMMD....", "...DMMLLLLMMD...", "..DMLPPPPPPPPLMD..", "..DMPPPPPPPPMD..", ".DMLDCCCCCCDLMD.", ".DMDDCCCCCCDDMD.", ".DMDDDDDDDDDDMD.", ".DM.D......D.MD.", ".DM.D......D.MD.", "................", "................", "................", "................", "................", "................"};
            ResourceGenerator.createTexture(new File(itemTexDir, "logic_helmet.png"), helmetGrid, helmetColors);
            HashMap<Character, Integer> plateColors = new HashMap<Character, Integer>();
            plateColors.put(Character.valueOf('.'), 0);
            plateColors.put(Character.valueOf('D'), -13684168);
            plateColors.put(Character.valueOf('M'), -10986138);
            plateColors.put(Character.valueOf('L'), -6378568);
            plateColors.put(Character.valueOf('C'), -16715521);
            plateColors.put(Character.valueOf('B'), -16746616);
            String[] plateGrid = new String[]{"...D........D...", "..DM........MD..", ".DLL........LLD.", ".DMMDDDDDDDDMMD.", ".DMMLLLLLLLLMMD.", ".DMLDCCCCCCDMLD.", "..MLDCCBBCCDML..", "..MDDCBBBBCDMD..", "..MDDCCCCCCDMD..", "..MDDDDDDDDDMD..", "..MDD......DMD..", "...DD......DD...", "................", "................", "................", "................"};
            ResourceGenerator.createTexture(new File(itemTexDir, "electric_plate.png"), plateGrid, plateColors);
            HashMap<Character, Integer> bootsColors = new HashMap<Character, Integer>();
            bootsColors.put(Character.valueOf('.'), 0);
            bootsColors.put(Character.valueOf('D'), -13684168);
            bootsColors.put(Character.valueOf('M'), -10986138);
            bootsColors.put(Character.valueOf('L'), -6378568);
            bootsColors.put(Character.valueOf('O'), -28928);
            bootsColors.put(Character.valueOf('Y'), -10929);
            String[] bootsGrid = new String[]{"................", "................", "...DD......DD...", "..DMLD....DMLD..", "..DMMD....DMMD..", "..DMMD....DMMD..", "..DMMD....DMMD..", ".DLLLD....DLLLD.", ".DMMMD....DMMMD.", ".DMMMD....DMMMD.", ".DODOD....DODOD.", ".DYDYD....DYDYD.", "................", "................", "................", "................"};
            ResourceGenerator.createTexture(new File(itemTexDir, "steam_boots.png"), bootsGrid, bootsColors);
            BufferedImage layer1 = new BufferedImage(64, 32, 2);
            ResourceGenerator.drawFuturisticArmor(layer1, 1);
            ImageIO.write((RenderedImage)layer1, "PNG", new File(armorTexDir, "exerion_suit_layer_1.png"));
            System.out.println("Generated custom armor layer 1 texture.");
            BufferedImage layer2 = new BufferedImage(64, 32, 2);
            ResourceGenerator.drawFuturisticArmor(layer2, 2);
            ImageIO.write((RenderedImage)layer2, "PNG", new File(armorTexDir, "exerion_suit_layer_2.png"));
            System.out.println("Generated custom armor layer 2 texture.");
        }
        catch (Exception e) {
            System.err.println("Failed to generate armor textures: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void drawFuturisticArmor(BufferedImage img, int layer) {
        int w = img.getWidth();
        int h = img.getHeight();
        Graphics2D g = img.createGraphics();
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, w, h);
        g.setComposite(AlphaComposite.Src);
        Color darkMetal = new Color(-13684168);
        Color medMetal = new Color(-10986138);
        Color neonPurple = new Color(-3014446);
        Color neonCyan = new Color(-16715521);
        Color thrusterOrange = new Color(-28928);
        if (layer == 1) {
            g.setColor(darkMetal);
            g.fillRect(0, 0, 32, 16);
            g.setColor(medMetal);
            g.fillRect(8, 8, 8, 8);
            g.fillRect(0, 8, 8, 8);
            g.fillRect(16, 8, 8, 8);
            g.setColor(neonPurple);
            g.fillRect(9, 10, 6, 2);
            g.setColor(neonCyan);
            g.fillRect(11, 8, 2, 1);
            g.setColor(darkMetal);
            g.fillRect(16, 16, 24, 16);
            g.setColor(medMetal);
            g.fillRect(20, 20, 16, 8);
            g.setColor(neonCyan);
            g.fillOval(24, 22, 4, 4);
            g.setColor(darkMetal);
            g.fillRect(40, 16, 24, 16);
            g.setColor(neonPurple);
            g.fillRect(44, 24, 4, 2);
            g.fillRect(56, 24, 4, 2);
            g.setColor(darkMetal);
            g.fillRect(0, 16, 16, 16);
            g.setColor(thrusterOrange);
            g.fillRect(2, 30, 4, 2);
            g.fillRect(10, 30, 4, 2);
        } else {
            g.setColor(darkMetal);
            g.fillRect(0, 0, w, h);
            g.setColor(neonPurple);
            g.fillRect(4, 8, 2, 16);
            g.fillRect(20, 8, 2, 16);
            g.fillRect(36, 8, 2, 16);
            g.fillRect(52, 8, 2, 16);
        }
        g.dispose();
    }

    private static void writeFile(File file, String content) {
        if (file.exists()) {
            return;
        }
        try (FileWriter writer = new FileWriter(file);){
            writer.write(content);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeShapedRecipe(File dir, String name, String result, int count, String[] pattern, Object ... keys) {
        StringBuilder keyStr = new StringBuilder();
        for (int i = 0; i < keys.length; i += 2) {
            String symbol = (String)keys[i];
            String item = (String)keys[i + 1];
            keyStr.append("    \"").append(symbol).append("\": \"").append(item).append("\"");
            if (i + 2 >= keys.length) continue;
            keyStr.append(",\n");
        }
        String json = "{\n  \"type\": \"minecraft:crafting_shaped\",\n  \"pattern\": [\n    \"" + pattern[0] + "\",\n    \"" + pattern[1] + "\",\n    \"" + pattern[2] + "\"\n  ],\n  \"key\": {\n" + keyStr.toString() + "\n  },\n  \"result\": {\n    \"id\": \"" + result + "\",\n    \"count\": " + count + "\n  }\n}";
        ResourceGenerator.writeFile(new File(dir, name + ".json"), json);
    }

    private static void writeSmeltingRecipe(File dir, String name, String ingredient, String result, float experience, int cookingTime) {
        String json = "{\n  \"type\": \"minecraft:smelting\",\n  \"ingredient\": \"" + ingredient + "\",\n  \"result\": \"" + result + "\",\n  \"experience\": " + experience + ",\n  \"cookingtime\": " + cookingTime + "\n}";
        ResourceGenerator.writeFile(new File(dir, name + ".json"), json);
    }

    private static void writeBlastingRecipe(File dir, String name, String ingredient, String result, float experience, int cookingTime) {
        String json = "{\n  \"type\": \"minecraft:blasting\",\n  \"ingredient\": \"" + ingredient + "\",\n  \"result\": \"" + result + "\",\n  \"experience\": " + experience + ",\n  \"cookingtime\": " + cookingTime + "\n}";
        ResourceGenerator.writeFile(new File(dir, name + ".json"), json);
    }

    private static void createTexture(File dest, String[] grid, Map<Character, Integer> colors) {
        if (dest.exists()) {
            return;
        }
        try {
            BufferedImage img = new BufferedImage(16, 16, 2);
            long seed = dest.getName().hashCode();
            Random rand = new Random(seed);
            for (int y = 0; y < 16; ++y) {
                String row = grid[y];
                for (int x = 0; x < 16; ++x) {
                    char c = row.charAt(x);
                    int color = colors.getOrDefault(Character.valueOf(c), 0);
                    int alpha = color >> 24 & 0xFF;
                    if (alpha > 0) {
                        int r = color >> 16 & 0xFF;
                        int g = color >> 8 & 0xFF;
                        int b = color & 0xFF;
                        int noise = rand.nextInt(21) - 10;
                        r = Math.max(0, Math.min(255, r + noise));
                        g = Math.max(0, Math.min(255, g + noise));
                        b = Math.max(0, Math.min(255, b + noise));
                        color = alpha << 24 | r << 16 | g << 8 | b;
                    }
                    img.setRGB(x, y, color);
                }
            }
            dest.getParentFile().mkdirs();
            ImageIO.write((RenderedImage)img, "PNG", dest);
            System.out.println("Generated custom texture (with pixel noise): " + dest.getAbsolutePath());
        }
        catch (Exception e) {
            System.err.println("Failed to write texture " + dest.getName() + ": " + e.getMessage());
        }
    }

    private static void generateMachineTextures() {
        File blockTexDir = new File("src/main/resources/assets/exerion/textures/block");
        File itemTexDir = new File("src/main/resources/assets/exerion/textures/item");
        HashMap<Character, Integer> bookColors = new HashMap<Character, Integer>();
        bookColors.put(Character.valueOf('.'), 0);
        bookColors.put(Character.valueOf('K'), -15663070);
        bookColors.put(Character.valueOf('P'), -8388480);
        bookColors.put(Character.valueOf('G'), -10496);
        bookColors.put(Character.valueOf('W'), -1710619);
        String[] bookGrid = new String[]{"................", ".....KKKKK......", "....KPPPPPK.....", "...KPPPPPPPK....", "...KPPGGPKPK....", "...KPGGGGPKPK...", "...KPPGGPKPKK...", "...KPPPPPKWKK...", "...KPPPPPKWWK...", "...KPPPPPKWWK...", "...KPPPPPKWWK...", "...KKKKKKWWK....", "....KKKKKKK.....", "................", "................", "................"};
        ResourceGenerator.createTexture(new File(itemTexDir, "exerion_guide_book.png"), bookGrid, bookColors);
        HashMap<Character, Integer> crusherColors = new HashMap<Character, Integer>();
        crusherColors.put(Character.valueOf('M'), -10855846);
        crusherColors.put(Character.valueOf('D'), -12961222);
        crusherColors.put(Character.valueOf('X'), -8553091);
        crusherColors.put(Character.valueOf('.'), -14737633);
        crusherColors.put(Character.valueOf('G'), -2848000);
        String[] crusherGrid = new String[]{"MMMMMMMMMMMMMMMM", "MDDDDDDDDDDDDDDM", "MDXXXXXXXXXXDDDM", "MDX........XDDDM", "MDX.GG..GG.XDDDM", "MDX.GG..GG.XDDDM", "MDX........XDDDM", "MDX..GGGG..XDDDM", "MDX..GGGG..XDDDM", "MDX........XDDDM", "MDXXXXXXXXXXDDDM", "MDDDDDDDDDDDDDDM", "MMMMMMMMMMMMMMMM", "MMMMMMMMMMMMMMMM", "MMMMMMMMMMMMMMMM", "MMMMMMMMMMMMMMMM"};
        ResourceGenerator.createTexture(new File(blockTexDir, "crusher.png"), crusherGrid, crusherColors);
        HashMap<Character, Integer> boilerColors = new HashMap<Character, Integer>();
        boilerColors.put(Character.valueOf('C'), -4689101);
        boilerColors.put(Character.valueOf('D'), -8369122);
        boilerColors.put(Character.valueOf('X'), -2057633);
        boilerColors.put(Character.valueOf('.'), -14013910);
        boilerColors.put(Character.valueOf('K'), -11645362);
        boilerColors.put(Character.valueOf('Y'), -10496);
        boilerColors.put(Character.valueOf('R'), -51456);
        String[] boilerGrid = new String[]{"CCCCCCCCCCCCCCCC", "CDDDDDDDDDDDDDDC", "CDXXXXXXXXXXDDDC", "CDX........XDDDC", "CDX.KKKKKK.XDDDC", "CDX.KYYYYK.XDDDC", "CDX.KYRRYK.XDDDC", "CDX.KYYYYK.XDDDC", "CDX.KKKKKK.XDDDC", "CDX........XDDDC", "CDXXXXXXXXXXDDDC", "CDDDDDDDDDDDDDDC", "CCCCCCCCCCCCCCCC", "CCCCCCCCCCCCCCCC", "CCCCCCCCCCCCCCCC", "CCCCCCCCCCCCCCCC"};
        ResourceGenerator.createTexture(new File(blockTexDir, "coal_boiler.png"), boilerGrid, boilerColors);
        HashMap<Character, Integer> turbineColors = new HashMap<Character, Integer>();
        turbineColors.put(Character.valueOf('S'), -6381922);
        turbineColors.put(Character.valueOf('D'), -10395295);
        turbineColors.put(Character.valueOf('X'), -2039584);
        turbineColors.put(Character.valueOf('.'), -14013910);
        turbineColors.put(Character.valueOf('B'), -16732991);
        String[] turbineGrid = new String[]{"SSSSSSSSSSSSSSSS", "SDDDDDDDDDDDDDDS", "SDXXXXXXXXXXDDDS", "SDX...BB...XDDDS", "SDX..BBBB..XDDDS", "SDX.B..B..BXDDDS", "SDX.B.BB.BBXDDDS", "SDX..BBBB..XDDDS", "SDX...BB...XDDDS", "SDX........XDDDS", "SDXXXXXXXXXXDDDS", "SDDDDDDDDDDDDDDS", "SSSSSSSSSSSSSSSS", "SSSSSSSSSSSSSSSS", "SSSSSSSSSSSSSSSS", "SSSSSSSSSSSSSSSS"};
        ResourceGenerator.createTexture(new File(blockTexDir, "steam_turbine.png"), turbineGrid, turbineColors);
        HashMap<Character, Integer> genColors = new HashMap<Character, Integer>();
        genColors.put(Character.valueOf('S'), -6381922);
        genColors.put(Character.valueOf('D'), -10395295);
        genColors.put(Character.valueOf('X'), -2039584);
        genColors.put(Character.valueOf('.'), -14013910);
        genColors.put(Character.valueOf('G'), -16121);
        String[] genGrid = new String[]{"SSSSSSSSSSSSSSSS", "SDDDDDDDDDDDDDDS", "SDXXXXXXXXXXDDDS", "SDX........XDDDS", "SDX...GG...XDDDS", "SDX..GGG...XDDDS", "SDX.GGGGGG.XDDDS", "SDX...GGG..XDDDS", "SDX...GG...XDDDS", "SDX........XDDDS", "SDXXXXXXXXXXDDDS", "SDDDDDDDDDDDDDDS", "SSSSSSSSSSSSSSSS", "SSSSSSSSSSSSSSSS", "SSSSSSSSSSSSSSSS", "SSSSSSSSSSSSSSSS"};
        ResourceGenerator.createTexture(new File(blockTexDir, "electric_generator.png"), genGrid, genColors);
        HashMap<Character, Integer> ecColors = new HashMap<Character, Integer>();
        ecColors.put(Character.valueOf('S'), -6381922);
        ecColors.put(Character.valueOf('D'), -10395295);
        ecColors.put(Character.valueOf('X'), -2039584);
        ecColors.put(Character.valueOf('.'), -14737633);
        ecColors.put(Character.valueOf('B'), -16718337);
        String[] ecGrid = new String[]{"SSSSSSSSSSSSSSSS", "SDDDDDDDDDDDDDDS", "SDXXXXXXXXXXDDDS", "SDX........XDDDS", "SDX.BB..BB.XDDDS", "SDX.BB..BB.XDDDS", "SDX........XDDDS", "SDX..BBBB..XDDDS", "SDX..BBBB..XDDDS", "SDX........XDDDS", "SDXXXXXXXXXXDDDS", "SDDDDDDDDDDDDDDS", "SSSSSSSSSSSSSSSS", "SSSSSSSSSSSSSSSS", "SSSSSSSSSSSSSSSS", "SSSSSSSSSSSSSSSS"};
        ResourceGenerator.createTexture(new File(blockTexDir, "electric_crusher.png"), ecGrid, ecColors);
        HashMap<Character, Integer> ptfColors = new HashMap<Character, Integer>();
        ptfColors.put(Character.valueOf('P'), -8388480);
        ptfColors.put(Character.valueOf('D'), -11927478);
        ptfColors.put(Character.valueOf('X'), -3669816);
        ptfColors.put(Character.valueOf('.'), -14013910);
        ptfColors.put(Character.valueOf('B'), -16731905);
        ptfColors.put(Character.valueOf('Y'), -2817799);
        String[] ptfGrid = new String[]{"PPPPPPPPPPPPPPPP", "PDDDDDDDDDDDDDDP", "PDXXXXXXXXXXDDDP", "PDX.BBBBBB.XDDDP", "PDX.BBBBBB.XDDDP", "PDX.BBYYBB.XDDDP", "PDX.BBYYBB.XDDDP", "PDX.BBBBBB.XDDDP", "PDX.BBBBBB.XDDDP", "PDX........XDDDP", "PDXXXXXXXXXXDDDP", "PDDDDDDDDDDDDDDP", "PPPPPPPPPPPPPPPP", "PPPPPPPPPPPPPPPP", "PPPPPPPPPPPPPPPP", "PPPPPPPPPPPPPPPP"};
        ResourceGenerator.createTexture(new File(blockTexDir, "progression_terminal_front.png"), ptfGrid, ptfColors);
        HashMap<Character, Integer> ptsColors = new HashMap<Character, Integer>();
        ptsColors.put(Character.valueOf('P'), -8388480);
        ptsColors.put(Character.valueOf('D'), -11927478);
        ptsColors.put(Character.valueOf('X'), -3669816);
        ptsColors.put(Character.valueOf('.'), -14013910);
        ptsColors.put(Character.valueOf('G'), -10496);
        String[] ptsGrid = new String[]{"PPPPPPPPPPPPPPPP", "PDDDDDDDDDDDDDDP", "PDXXXXXXXXXXDDDP", "PDX........XDDDP", "PDX..GGGG..XDDDP", "PDX..G..G..XDDDP", "PDX..GGGG..XDDDP", "PDX.....G..XDDDP", "PDX..GGGG..XDDDP", "PDX........XDDDP", "PDXXXXXXXXXXDDDP", "PDDDDDDDDDDDDDDP", "PPPPPPPPPPPPPPPP", "PPPPPPPPPPPPPPPP", "PPPPPPPPPPPPPPPP", "PPPPPPPPPPPPPPPP"};
        ResourceGenerator.createTexture(new File(blockTexDir, "progression_terminal_side.png"), ptsGrid, ptsColors);
        HashMap<Character, Integer> pttColors = new HashMap<Character, Integer>();
        pttColors.put(Character.valueOf('P'), -8388480);
        pttColors.put(Character.valueOf('D'), -11927478);
        pttColors.put(Character.valueOf('X'), -3669816);
        pttColors.put(Character.valueOf('.'), -14013910);
        pttColors.put(Character.valueOf('R'), -59580);
        pttColors.put(Character.valueOf('G'), -16718218);
        pttColors.put(Character.valueOf('Y'), -5632);
        pttColors.put(Character.valueOf('K'), -13154481);
        String[] pttGrid = new String[]{"PPPPPPPPPPPPPPPP", "PDDDDDDDDDDDDDDP", "PDXXXXXXXXXXDDDP", "PDX........XDDDP", "PDX.R..G.Y.XDDDP", "PDX........XDDDP", "PDX.KKKKKK.XDDDP", "PDX.KKKKKK.XDDDP", "PDX.KKKKKK.XDDDP", "PDX........XDDDP", "PDXXXXXXXXXXDDDP", "PDDDDDDDDDDDDDDP", "PPPPPPPPPPPPPPPP", "PPPPPPPPPPPPPPPP", "PPPPPPPPPPPPPPPP", "PPPPPPPPPPPPPPPP"};
        ResourceGenerator.createTexture(new File(blockTexDir, "progression_terminal_top.png"), pttGrid, pttColors);
        HashMap<Character, Integer> dstFrontColors = new HashMap<Character, Integer>();
        dstFrontColors.put(Character.valueOf('P'), -16752781);
        dstFrontColors.put(Character.valueOf('D'), -16109771);
        dstFrontColors.put(Character.valueOf('X'), -16084074);
        dstFrontColors.put(Character.valueOf('.'), -14013910);
        dstFrontColors.put(Character.valueOf('W'), -16718337);
        dstFrontColors.put(Character.valueOf('B'), -13154481);
        String[] dstFrontGrid = new String[]{"PPPPPPPPPPPPPPPP", "PDDDDDDDDDDDDDDP", "PDXXXXXXXXXXDDDP", "PDX.WWWWWW.XDDDP", "PDX.W....W.XDDDP", "PDX.WWWWWW.XDDDP", "PDX........XDDDP", "PDX.BBBBBB.XDDDP", "PDX.B....B.XDDDP", "PDX.BBBBBB.XDDDP", "PDX.B....B.XDDDP", "PDX.BBBBBB.XDDDP", "PDXXXXXXXXXXDDDP", "PDDDDDDDDDDDDDDP", "PPPPPPPPPPPPPPPP", "PPPPPPPPPPPPPPPP"};
        ResourceGenerator.createTexture(new File(blockTexDir, "digital_storage_terminal_front.png"), dstFrontGrid, dstFrontColors);
        HashMap<Character, Integer> dstSideColors = new HashMap<Character, Integer>();
        dstSideColors.put(Character.valueOf('P'), -16752781);
        dstSideColors.put(Character.valueOf('D'), -16109771);
        dstSideColors.put(Character.valueOf('X'), -16084074);
        dstSideColors.put(Character.valueOf('.'), -14013910);
        dstSideColors.put(Character.valueOf('G'), -10496);
        String[] dstSideGrid = new String[]{"PPPPPPPPPPPPPPPP", "PDDDDDDDDDDDDDDP", "PDXXXXXXXXXXDDDP", "PDX........XDDDP", "PDX..GGGG..XDDDP", "PDX..G..G..XDDDP", "PDX..GGGG..XDDDP", "PDX..G.....XDDDP", "PDX..G.GGG.XDDDP", "PDX........XDDDP", "PDXXXXXXXXXXDDDP", "PDDDDDDDDDDDDDDP", "PPPPPPPPPPPPPPPP", "PPPPPPPPPPPPPPPP", "PPPPPPPPPPPPPPPP", "PPPPPPPPPPPPPPPP"};
        ResourceGenerator.createTexture(new File(blockTexDir, "digital_storage_terminal_side.png"), dstSideGrid, dstSideColors);
        HashMap<Character, Integer> dstTopColors = new HashMap<Character, Integer>();
        dstTopColors.put(Character.valueOf('P'), -16752781);
        dstTopColors.put(Character.valueOf('D'), -16109771);
        dstTopColors.put(Character.valueOf('X'), -16084074);
        dstTopColors.put(Character.valueOf('.'), -14013910);
        dstTopColors.put(Character.valueOf('R'), -59580);
        dstTopColors.put(Character.valueOf('G'), -16718218);
        dstTopColors.put(Character.valueOf('B'), -16731905);
        dstTopColors.put(Character.valueOf('V'), -13154481);
        String[] dstTopGrid = new String[]{"PPPPPPPPPPPPPPPP", "PDDDDDDDDDDDDDDP", "PDXXXXXXXXXXDDDP", "PDX........XDDDP", "PDX.R..G.B.XDDDP", "PDX........XDDDP", "PDX.VVVVVV.XDDDP", "PDX.VVVVVV.XDDDP", "PDX.VVVVVV.XDDDP", "PDX........XDDDP", "PDXXXXXXXXXXDDDP", "PDDDDDDDDDDDDDDP", "PPPPPPPPPPPPPPPP", "PPPPPPPPPPPPPPPP", "PPPPPPPPPPPPPPPP", "PPPPPPPPPPPPPPPP"};
        ResourceGenerator.createTexture(new File(blockTexDir, "digital_storage_terminal_top.png"), dstTopGrid, dstTopColors);
        HashMap<Character, Integer> speedColors = new HashMap<Character, Integer>();
        speedColors.put(Character.valueOf('.'), 0);
        speedColors.put(Character.valueOf('G'), -2838729);
        speedColors.put(Character.valueOf('Y'), -8090);
        speedColors.put(Character.valueOf('C'), -16718337);
        speedColors.put(Character.valueOf('B'), -15589072);
        String[] speedGrid = new String[]{".....GGGGGG.....", "....GYYYYYYG....", "...GY......YG...", "..GY...CC...YG..", "..GY..CCC...YG..", ".GY..CCCC....YG.", ".GY.CCCCC....YG.", ".GY...CCC....YG.", ".GY..CCC.....YG.", ".GY..CC......YG.", ".GY.CC.......YG.", "..GYC.......YG..", "..GYBBBBBBBBG...", "...GGGGGGGGG....", "................", "................"};
        ResourceGenerator.createTexture(new File(itemTexDir, "speed_upgrade.png"), speedGrid, speedColors);
        HashMap<Character, Integer> effColors = new HashMap<Character, Integer>();
        effColors.put(Character.valueOf('.'), 0);
        effColors.put(Character.valueOf('G'), -2838729);
        effColors.put(Character.valueOf('Y'), -8090);
        effColors.put(Character.valueOf('E'), -12976364);
        effColors.put(Character.valueOf('B'), -15585259);
        String[] effGrid = new String[]{".....GGGGGG.....", "....GYYYYYYG....", "...GY......YG...", "..GY...EE...YG..", "..GY..EEEE..YG..", ".GY..EE..EE..YG.", ".GY.EEEEEEEE.YG.", ".GY.EEEEEEEE.YG.", ".GY..EEEEEE..YG.", ".GY...EEEE...YG.", ".GY....EE....YG.", "..GY........YG..", "..GYBBBBBBBBG...", "...GGGGGGGGG....", "................", "................"};
        ResourceGenerator.createTexture(new File(itemTexDir, "efficiency_upgrade.png"), effGrid, effColors);
        HashMap<Character, Integer> bsColors = new HashMap<Character, Integer>();
        bsColors.put(Character.valueOf('W'), -7644629);
        bsColors.put(Character.valueOf('w'), -6264261);
        bsColors.put(Character.valueOf('d'), -9552870);
        bsColors.put(Character.valueOf('C'), -2592704);
        bsColors.put(Character.valueOf('c'), -28576);
        bsColors.put(Character.valueOf('S'), -12703965);
        String[] bsGrid = new String[]{"ccccCCCCCCCCcccc", "cWWWWWWWWWWWWWWc", "CWWWWWWWWWWWWWWd", "CWWwwWWWWwwWWWWd", "CWWwwWWWWwwWWWWd", "CWWWWWWWWWWWWWWd", "CWWWWWWWWWWWWWWd", "CWWWWWWWWWWWWWWd", "CWWWWWWWWWWWWWWd", "CWWwwWWWWwwWWWWd", "CWWwwWWWWwwWWWWd", "CWWWWWWWWWWWWWWd", "CWWWWWWWWWWWWWWd", "cWWWWWWWWWWWWWWd", "cddddddddddddddd", "ccccCCCCCCCCcccc"};
        ResourceGenerator.createTexture(new File(blockTexDir, "batbox_side.png"), bsGrid, bsColors);
        HashMap<Character, Integer> bfColors = new HashMap<Character, Integer>();
        bfColors.put(Character.valueOf('W'), -7644629);
        bfColors.put(Character.valueOf('w'), -6264261);
        bfColors.put(Character.valueOf('d'), -9552870);
        bfColors.put(Character.valueOf('C'), -2592704);
        bfColors.put(Character.valueOf('c'), -28576);
        bfColors.put(Character.valueOf('R'), -59580);
        bfColors.put(Character.valueOf('Y'), -5632);
        bfColors.put(Character.valueOf('S'), -14013910);
        String[] bfGrid = new String[]{"ccccCCCCCCCCcccc", "cWWWWWWWWWWWWWWc", "CWWWWWWWWWWWWWWd", "CWWWWWWWWWWWWWWd", "CWWWWYYYYWWWWWWd", "CWWWYYYYYYWWWWWd", "CWWYYYSSYYYWWWWd", "CWWYYSSSSYYWWWWd", "CWWYYSSSSYYWWWWd", "CWWYYYSSYYYWWWWd", "CWWWYYYYYYWWWWWd", "CWWWWYYYYWWWWWWd", "CWWWWWWWWWWWWWWd", "cWWWWWWWWWWWWWWd", "cddddddddddddddd", "ccccCCCCCCCCcccc"};
        ResourceGenerator.createTexture(new File(blockTexDir, "batbox_front.png"), bfGrid, bfColors);
        HashMap<Character, Integer> drillColors = new HashMap<Character, Integer>();
        drillColors.put(Character.valueOf('.'), 0);
        drillColors.put(Character.valueOf('D'), -10986138);
        drillColors.put(Character.valueOf('L'), -6378568);
        drillColors.put(Character.valueOf('S'), -13158601);
        drillColors.put(Character.valueOf('C'), -16718337);
        drillColors.put(Character.valueOf('c'), -16742230);
        drillColors.put(Character.valueOf('P'), -8388480);
        drillColors.put(Character.valueOf('p'), -3669816);
        drillColors.put(Character.valueOf('O'), -10496);
        String[] drillGrid = new String[]{"............LLDD", "...........LLDD.", "..........LLDDS.", ".........LLDDS..", "........LLDDS...", ".......LLDDS....", "......cCCCS.....", ".....cCCCC......", "....cCCCC.......", "...pPPPPc.......", "..pPPPP.........", ".pPPP...........", "pP..............", "O...............", "................", "................"};
        ResourceGenerator.createTexture(new File(itemTexDir, "electric_mining_drill.png"), drillGrid, drillColors);
        HashMap<Character, Integer> compfColors = new HashMap<Character, Integer>();
        compfColors.put(Character.valueOf('S'), -8354424);
        compfColors.put(Character.valueOf('s'), -4143672);
        compfColors.put(Character.valueOf('d'), -11512488);
        compfColors.put(Character.valueOf('O'), -14273992);
        compfColors.put(Character.valueOf('P'), -16718337);
        compfColors.put(Character.valueOf('W'), -2039584);
        compfColors.put(Character.valueOf('G'), -6381922);
        String[] compfGrid = new String[]{"ssssSSSSSSSSdddd", "sWWWWWWWWWWWWWWd", "SGGGGGGGGGGGGGGd", "SGG..........GGd", "SG..P......P..Gd", "SG..P..OO..P..Gd", "SG..W..OO..W..Gd", "SG..WWWWWWWW..Gd", "SG..WWWWWWWW..Gd", "SG..W..OO..W..Gd", "SG..P..OO..P..Gd", "SG..P......P..Gd", "SGG..........GGd", "SGGGGGGGGGGGGGGd", "sddddddddddddddd", "dddddddddddddddd"};
        ResourceGenerator.createTexture(new File(blockTexDir, "compressor_front.png"), compfGrid, compfColors);
        HashMap<Character, Integer> alloyfColors = new HashMap<Character, Integer>();
        alloyfColors.put(Character.valueOf('S'), -8354424);
        alloyfColors.put(Character.valueOf('s'), -4143672);
        alloyfColors.put(Character.valueOf('d'), -11512488);
        alloyfColors.put(Character.valueOf('O'), -15395563);
        alloyfColors.put(Character.valueOf('F'), -43230);
        alloyfColors.put(Character.valueOf('Y'), -5632);
        String[] alloyfGrid = new String[]{"ssssSSSSSSSSdddd", "sSSSSSSSSSSSSSSd", "SS............Sd", "S..OOOO..OOOO..d", "S..OYYO..OYYO..d", "S..OYYO..OYYO..d", "S..OFFO..OFFO..d", "S..OFFO..OFFO..d", "S..OOOO..OOOO..d", "S..............d", "S......OO......d", "S.....OYYO.....d", "S......OO......d", "sddddddddddddddd", "dddddddddddddddd", "dddddddddddddddd"};
        ResourceGenerator.createTexture(new File(blockTexDir, "alloy_smelter_front.png"), alloyfGrid, alloyfColors);
        HashMap<Character, Integer> enderSideColors = new HashMap<Character, Integer>();
        enderSideColors.put(Character.valueOf('K'), -16051180);
        enderSideColors.put(Character.valueOf('G'), -15457244);
        enderSideColors.put(Character.valueOf('S'), -16446966);
        enderSideColors.put(Character.valueOf('P'), -7461718);
        enderSideColors.put(Character.valueOf('p'), -2614432);
        String[] enderSideGrid = new String[]{"KKKKGGGGGGGGKKKK", "KKKGGGGGGGGGGKKK", "KKGGGPpGGGGpGGSK", "KGGGGppGGGGpGGSS", "KGGGGGGGGGGGGSSS", "KGGGGGGpGGGGSSSS", "KGGGGGGpGGGGSSSS", "KGGGGGPpGGGGSSSS", "KGGGGGGGGGGGGSSS", "KGGGGGGGGpGGSSSS", "KKGGGGGGpGGSGSSK", "KKKGGGGGGGGSGSSK", "KKKKGGGGGGSSSSKK", "KKKKKGGGGSSSKKKK", "KKKKKKGSSSKKKKKK", "KKKKKKKKKKKKKKKK"};
        ResourceGenerator.createTexture(new File(blockTexDir, "frequency_ender_chest_side.png"), enderSideGrid, enderSideColors);
        HashMap<Character, Integer> enderFrontColors = new HashMap<Character, Integer>();
        enderFrontColors.put(Character.valueOf('K'), -16051180);
        enderFrontColors.put(Character.valueOf('G'), -15457244);
        enderFrontColors.put(Character.valueOf('S'), -16446966);
        enderFrontColors.put(Character.valueOf('P'), -7461718);
        enderFrontColors.put(Character.valueOf('L'), -10496);
        enderFrontColors.put(Character.valueOf('l'), -5632);
        enderFrontColors.put(Character.valueOf('d'), -4745973);
        String[] enderFrontGrid = new String[]{"KKKKGGGGGGGGKKKK", "KKKGGGGGGGGGGKKK", "KKGGGGGGGGGGGGSK", "KGGGGGGGGGGGGpSS", "KGGGLLLLLLGGSSSS", "KGGGLllLlldGSSSS", "KGGGLlLLlldGSSSS", "KGGGLlLLlldGSSSS", "KGGGLLLLLLGGSSSS", "KGGGGGGGGGGGGSSS", "KKGGGGGGGGGGSSSK", "KKKGGGGGGGGSSSKK", "KKKKGGGGGSSSSKKK", "KKKKKGGGGSSSKKKK", "KKKKKKGGSSKKKKKK", "KKKKKKKKKKKKKKKK"};
        ResourceGenerator.createTexture(new File(blockTexDir, "frequency_ender_chest_front.png"), enderFrontGrid, enderFrontColors);
        HashMap<Character, Integer> enderTopColors = new HashMap<Character, Integer>();
        enderTopColors.put(Character.valueOf('K'), -16051180);
        enderTopColors.put(Character.valueOf('G'), -15457244);
        enderTopColors.put(Character.valueOf('S'), -16446966);
        enderTopColors.put(Character.valueOf('P'), -7461718);
        enderTopColors.put(Character.valueOf('F'), -13154481);
        enderTopColors.put(Character.valueOf('.'), -14869215);
        String[] enderTopGrid = new String[]{"KKKKGGGGGGGGKKKK", "KKKGGGGGGGGGGKKK", "KKGGGGGGGGGGGGSK", "KGGFFFFFFFFFFGSS", "KGGFFFFFFFFFFGSS", "KGF..FF..FF..FSS", "KGF..FF..FF..FSS", "KGF..FF..FF..FSS", "KGGFFFFFFFFFFGSS", "KGGFFFFFFFFFFGSS", "KKGGGGGGGGGGSSSK", "KKKGGGGGGGGSSSKK", "KKKKGGGGGSSSSKKK", "KKKKKGGGGSSSKKKK", "KKKKKKGGSSKKKKKK", "KKKKKKKKKKKKKKKK"};
        ResourceGenerator.createTexture(new File(blockTexDir, "frequency_ender_chest_top.png"), enderTopGrid, enderTopColors);
        HashMap<Character, Integer> carbonColors = new HashMap<Character, Integer>();
        carbonColors.put(Character.valueOf('.'), 0);
        carbonColors.put(Character.valueOf('K'), -15066598);
        carbonColors.put(Character.valueOf('G'), -12961222);
        carbonColors.put(Character.valueOf('S'), -15921907);
        String[] carbonGrid = new String[]{".....GGGGGG.....", "....GKKKKKKG....", "...GKKGGGGKKG...", "..GKKSGGGGKKG...", "..GKKSGKKGKKG...", ".GKKSGKKKKGKKG..", ".GKKSGKSKKGKKG..", ".GKKSGKSKKGKKG..", ".GKKSGKSKKGKKG..", ".GKKSGKKKKGKKG..", ".GKKSGKKKKGKKG..", "..GKKSGKKGKKG...", "..GKKSSGGKKKG...", "...GGGGGGGGG....", "................", "................"};
        ResourceGenerator.createTexture(new File(itemTexDir, "carbon_plate.png"), carbonGrid, carbonColors);
        HashMap<Character, Integer> laserColors = new HashMap<Character, Integer>();
        laserColors.put(Character.valueOf('S'), -12434878);
        laserColors.put(Character.valueOf('s'), -9079435);
        laserColors.put(Character.valueOf('d'), -14606047);
        laserColors.put(Character.valueOf('C'), -16718337);
        laserColors.put(Character.valueOf('c'), -16731905);
        laserColors.put(Character.valueOf('W'), -1);
        String[] laserGrid = new String[]{"ssssSSSSSSSSdddd", "sSSSSSSSSSSSSSSd", "sSS..........SSd", "sS...CCCCCC...Sd", "sS.cCCCCCCCCc.Sd", "sS.cCcWWWWcCc.Sd", "sS.cCcWWWWcCc.Sd", "sS.cCcWWWWcCc.Sd", "sS.cCcWWWWcCc.Sd", "sS.cCCCCCCCCc.Sd", "sS...CCCCCC...Sd", "sSS..........SSd", "sSSSSSSSSSSSSSSd", "sddddddddddddddd", "dddddddddddddddd", "dddddddddddddddd"};
        ResourceGenerator.createTexture(new File(blockTexDir, "mining_laser.png"), laserGrid, laserColors);
        HashMap<Character, Integer> geoColors = new HashMap<Character, Integer>();
        geoColors.put(Character.valueOf('S'), -10665929);
        geoColors.put(Character.valueOf('s'), -7508381);
        geoColors.put(Character.valueOf('d'), -12703965);
        geoColors.put(Character.valueOf('L'), -49920);
        geoColors.put(Character.valueOf('l'), -2282496);
        geoColors.put(Character.valueOf('O'), -14606047);
        String[] geoGrid = new String[]{"ssssSSSSSSSSdddd", "sOOOOOOOOOOOOOOd", "sOLlOlLlOlLlOlLd", "sOLlOlLlOlLlOlLd", "sOLlOlLlOlLlOlLd", "sOLlOlLlOlLlOlLd", "sOOOOOOOOOOOOOOd", "sOLlOlLlOlLlOlLd", "sOLlOlLlOlLlOlLd", "sOLlOlLlOlLlOlLd", "sOLlOlLlOlLlOlLd", "sOOOOOOOOOOOOOOd", "sSSSSSSSSSSSSSSd", "sddddddddddddddd", "dddddddddddddddd", "dddddddddddddddd"};
        ResourceGenerator.createTexture(new File(blockTexDir, "geothermal_generator.png"), geoGrid, geoColors);
        HashMap<Character, Integer> swordColors = new HashMap<Character, Integer>();
        swordColors.put(Character.valueOf('.'), 0);
        swordColors.put(Character.valueOf('C'), -16718337);
        swordColors.put(Character.valueOf('c'), -16731905);
        swordColors.put(Character.valueOf('W'), -1);
        swordColors.put(Character.valueOf('H'), -11922292);
        swordColors.put(Character.valueOf('h'), -8708190);
        swordColors.put(Character.valueOf('d'), -15073229);
        swordColors.put(Character.valueOf('M'), -7297874);
        String[] swordGrid = new String[]{"..............Cc", ".............Cc.", "............Cc..", "...........Wc...", "..........Wc....", ".........Wc.....", "........Wc......", ".......Wc.......", "......Wc........", ".....Wc.........", "....Wc..........", "...Wc...........", "..M.............", ".Hdd............", "HhH.............", "dH..............."};
        ResourceGenerator.createTexture(new File(itemTexDir, "plasma_sword.png"), swordGrid, swordColors);
        HashMap<Character, Integer> ccCableColors = new HashMap<Character, Integer>();
        ccCableColors.put(Character.valueOf('B'), -14013910);
        ccCableColors.put(Character.valueOf('S'), -15066598);
        ccCableColors.put(Character.valueOf('C'), -2592704);
        ccCableColors.put(Character.valueOf('H'), -28576);
        ccCableColors.put(Character.valueOf('D'), -8376304);
        ccCableColors.put(Character.valueOf('.'), 0);
        String[] ccGrid = new String[]{"CHCHHDHDCHCHHDHD", "HCHDHDCHHCHDHDCH", "CHHDHDCHCHHDHDCH", "HDHDCHCHHDHDCHCH", "HDCHCHHDHDCHCHHD", "CHCHHDHDCHCHHDHD", "HCHDHDCHHCHDHDCH", "CHHDHDCHCHHDHDCH", "HDHDCHCHHDHDCHCH", "HDCHCHHDHDCHCHHD", "CHCHHDHDCHCHHDHD", "HCHDHDCHHCHDHDCH", "CHHDHDCHCHHDHDCH", "HDHDCHCHHDHDCHCH", "HDCHCHHDHDCHCHHD", "CHCHHDHDCHCHHDHD"};
        ResourceGenerator.createTexture(new File(blockTexDir, "copper_cable.png"), ccGrid, ccCableColors);
        HashMap<Character, Integer> scColors = new HashMap<Character, Integer>();
        scColors.put(Character.valueOf('B'), -14013910);
        scColors.put(Character.valueOf('S'), -15066598);
        scColors.put(Character.valueOf('C'), -8354424);
        scColors.put(Character.valueOf('H'), -4143672);
        scColors.put(Character.valueOf('D'), -11512488);
        scColors.put(Character.valueOf('.'), 0);
        String[] scGrid = new String[]{"CHCHHDHDCHCHHDHD", "HCHDHDCHHCHDHDCH", "CHHDHDCHCHHDHDCH", "HDHDCHCHHDHDCHCH", "HDCHCHHDHDCHCHHD", "CHCHHDHDCHCHHDHD", "HCHDHDCHHCHDHDCH", "CHHDHDCHCHHDHDCH", "HDHDCHCHHDHDCHCH", "HDCHCHHDHDCHCHHD", "CHCHHDHDCHCHHDHD", "HCHDHDCHHCHDHDCH", "CHHDHDCHCHHDHDCH", "HDHDCHCHHDHDCHCH", "HDCHCHHDHDCHCHHD", "CHCHHDHDCHCHHDHD"};
        ResourceGenerator.createTexture(new File(blockTexDir, "steel_cable.png"), scGrid, scColors);
        HashMap<Character, Integer> ecCableColors = new HashMap<Character, Integer>();
        ecCableColors.put(Character.valueOf('B'), -14013910);
        ecCableColors.put(Character.valueOf('S'), -15066598);
        ecCableColors.put(Character.valueOf('C'), -3014446);
        ecCableColors.put(Character.valueOf('H'), -43521);
        ecCableColors.put(Character.valueOf('D'), -8388480);
        ecCableColors.put(Character.valueOf('.'), 0);
        String[] ecCableGrid = new String[]{"CHCHHDHDCHCHHDHD", "HCHDHDCHHCHDHDCH", "CHHDHDCHCHHDHDCH", "HDHDCHCHHDHDCHCH", "HDCHCHHDHDCHCHHD", "CHCHHDHDCHCHHDHD", "HCHDHDCHHCHDHDCH", "CHHDHDCHCHHDHDCH", "HDHDCHCHHDHDCHCH", "HDCHCHHDHDCHCHHD", "CHCHHDHDCHCHHDHD", "HCHDHDCHHCHDHDCH", "CHHDHDCHCHHDHDCH", "HDHDCHCHHDHDCHCH", "HDCHCHHDHDCHCHHD", "CHCHHDHDCHCHHDHD"};
        ResourceGenerator.createTexture(new File(blockTexDir, "exerion_cable.png"), ecCableGrid, ecCableColors);
    }

    private static void generateEra1BlockAssets() {
        File blockTexDir = new File("src/main/resources/assets/exerion/textures/block");
        File itemTexDir = new File("src/main/resources/assets/exerion/textures/item");
        File blockstatesDir = new File("src/main/resources/assets/exerion/blockstates");
        File blockModelsDir = new File("src/main/resources/assets/exerion/models/block");
        File itemModelsDir = new File("src/main/resources/assets/exerion/models/item");
        File clientItemDir = new File("src/main/resources/assets/exerion/items");
        blockTexDir.mkdirs();
        itemTexDir.mkdirs();
        blockstatesDir.mkdirs();
        blockModelsDir.mkdirs();
        itemModelsDir.mkdirs();
        clientItemDir.mkdirs();
        HashMap<Character, Integer> shaftColors = new HashMap<Character, Integer>();
        shaftColors.put(Character.valueOf('W'), -9811396);
        shaftColors.put(Character.valueOf('L'), -6393776);
        shaftColors.put(Character.valueOf('S'), -11651540);
        shaftColors.put(Character.valueOf('I'), -10461088);
        shaftColors.put(Character.valueOf('H'), -7697782);
        shaftColors.put(Character.valueOf('D'), -12961222);
        shaftColors.put(Character.valueOf('.'), 0);
        String[] shaftGrid = new String[]{"IIIIIIIIIIIIIIII", "HHHHHHHHHHHHHHHH", "WWWWWWSWWWWWWSWW", "WLLLLLWWLLLLLWWW", "WWWWWWWWWWWWWWWW", "WWWSWWWWWWWSWWWW", "WWWWLLLLWWWWLLWW", "WWWWWWWWWWWWWWWW", "WWWWWWWSWWWWWWWS", "WWLLLLWWWWLLLLWW", "WWWWWWWWWWWWWWWW", "WWSWWWWWWWSWWWWW", "WWWWLLLLWWWWLLWW", "WWWWWWWWWWWWWWWW", "DDDDDDDDDDDDDDDD", "IIIIIIIIIIIIIIII"};
        ResourceGenerator.createTexture(new File(blockTexDir, "shaft.png"), shaftGrid, shaftColors);
        ResourceGenerator.createTexture(new File(itemTexDir, "shaft.png"), shaftGrid, shaftColors);
        String shaftBlockstate = "{\n  \"variants\": {\n    \"axis=y\": { \"model\": \"exerion:block/shaft\" },\n    \"axis=x\": { \"model\": \"exerion:block/shaft\", \"x\": 90, \"y\": 90 },\n    \"axis=z\": { \"model\": \"exerion:block/shaft\", \"x\": 90 }\n  }\n}";
        ResourceGenerator.writeFile(new File(blockstatesDir, "shaft.json"), shaftBlockstate);
        String shaftModel = "{\n  \"parent\": \"minecraft:block/block\",\n  \"render_type\": \"minecraft:cutout\",\n  \"textures\": {\n    \"all\": \"exerion:block/shaft\",\n    \"particle\": \"exerion:block/shaft\"\n  },\n  \"elements\": [\n    { \"from\": [6,0,6], \"to\": [10,16,10],\n      \"faces\": {\n        \"north\": {\"texture\": \"#all\"},\n        \"south\": {\"texture\": \"#all\"},\n        \"east\":  {\"texture\": \"#all\"},\n        \"west\":  {\"texture\": \"#all\"},\n        \"up\":    {\"texture\": \"#all\"},\n        \"down\":  {\"texture\": \"#all\"}\n      }\n    }\n  ]\n}";
        ResourceGenerator.writeFile(new File(blockModelsDir, "shaft.json"), shaftModel);
        String shaftItemModel = "{\n  \"parent\": \"exerion:block/shaft\"\n}";
        ResourceGenerator.writeFile(new File(itemModelsDir, "shaft.json"), shaftItemModel);
        ResourceGenerator.writeFile(new File(clientItemDir, "shaft.json"), "{\n  \"model\": {\n    \"type\": \"minecraft:model\",\n    \"model\": \"exerion:item/shaft\"\n  }\n}");
        HashMap<Character, Integer> crankColors = new HashMap<Character, Integer>();
        crankColors.put(Character.valueOf('W'), -9811396);
        crankColors.put(Character.valueOf('L'), -6393776);
        crankColors.put(Character.valueOf('S'), -11651540);
        crankColors.put(Character.valueOf('I'), -10461088);
        crankColors.put(Character.valueOf('H'), -7697782);
        crankColors.put(Character.valueOf('D'), -12961222);
        crankColors.put(Character.valueOf('.'), 0);
        String[] crankGrid = new String[]{"IIIIIIIIIIIIIIII", "HHHHHHHHHHHHHHHH", "WWWWWWSWWWWWWSWW", "WLLLLLWWLLLLLWWW", "WWWWWWWWWWWWWWWW", "WWWSWWWWWWWSWWWW", "IIIIIIIIIIIIIIII", "HHHHHHHHHHHHHHHH", "DDDDDDDDDDDDDDDD", "WWWWWWWWWWWWWWWW", "WWWSWWWWWWWSWWWW", "WWWWLLLLWWWWLLWW", "WWWWWWWWWWWWWWWW", "IIIIIIIIIIIIIIII", "HHHHHHHHHHHHHHHH", "DDDDDDDDDDDDDDDD"};
        ResourceGenerator.createTexture(new File(blockTexDir, "hand_crank.png"), crankGrid, crankColors);
        ResourceGenerator.createTexture(new File(itemTexDir, "hand_crank.png"), crankGrid, crankColors);
        String crankBlockstate = "{\n  \"variants\": {\n    \"cranking=false,facing=north\": { \"model\": \"exerion:block/hand_crank\" },\n    \"cranking=false,facing=south\": { \"model\": \"exerion:block/hand_crank\", \"y\": 180 },\n    \"cranking=false,facing=east\": { \"model\": \"exerion:block/hand_crank\", \"y\": 90 },\n    \"cranking=false,facing=west\": { \"model\": \"exerion:block/hand_crank\", \"y\": 270 },\n    \"cranking=true,facing=north\": { \"model\": \"exerion:block/hand_crank\" },\n    \"cranking=true,facing=south\": { \"model\": \"exerion:block/hand_crank\", \"y\": 180 },\n    \"cranking=true,facing=east\": { \"model\": \"exerion:block/hand_crank\", \"y\": 90 },\n    \"cranking=true,facing=west\": { \"model\": \"exerion:block/hand_crank\", \"y\": 270 }\n  }\n}";
        ResourceGenerator.writeFile(new File(blockstatesDir, "hand_crank.json"), crankBlockstate);
        String crankBlockModel = "{\n  \"parent\": \"minecraft:block/block\",\n  \"render_type\": \"minecraft:cutout\",\n  \"textures\": {\n    \"all\": \"exerion:block/hand_crank\",\n    \"particle\": \"exerion:block/hand_crank\"\n  },\n  \"elements\": [\n    { \"from\": [2, 0, 2], \"to\": [14, 2, 14],\n      \"faces\": {\n        \"down\":  {\"texture\": \"#all\", \"uv\": [2,2,14,14]},\n        \"up\":    {\"texture\": \"#all\", \"uv\": [2,2,14,14]},\n        \"north\": {\"texture\": \"#all\", \"uv\": [2,0,14,2]},\n        \"south\": {\"texture\": \"#all\", \"uv\": [2,0,14,2]},\n        \"east\":  {\"texture\": \"#all\", \"uv\": [2,0,14,2]},\n        \"west\":  {\"texture\": \"#all\", \"uv\": [2,0,14,2]}\n      }\n    },\n    { \"from\": [6, 2, 6], \"to\": [10, 8, 10],\n      \"faces\": {\n        \"up\":    {\"texture\": \"#all\", \"uv\": [6,6,10,10]},\n        \"north\": {\"texture\": \"#all\", \"uv\": [6,2,10,8]},\n        \"south\": {\"texture\": \"#all\", \"uv\": [6,2,10,8]},\n        \"east\":  {\"texture\": \"#all\", \"uv\": [6,2,10,8]},\n        \"west\":  {\"texture\": \"#all\", \"uv\": [6,2,10,8]}\n      }\n    },\n    { \"from\": [5, 8, 5], \"to\": [11, 10, 11],\n      \"faces\": {\n        \"up\":    {\"texture\": \"#all\", \"uv\": [5,5,11,11]},\n        \"down\":  {\"texture\": \"#all\", \"uv\": [5,5,11,11]},\n        \"north\": {\"texture\": \"#all\", \"uv\": [5,8,11,10]},\n        \"south\": {\"texture\": \"#all\", \"uv\": [5,8,11,10]},\n        \"east\":  {\"texture\": \"#all\", \"uv\": [5,8,11,10]},\n        \"west\":  {\"texture\": \"#all\", \"uv\": [5,8,11,10]}\n      }\n    },\n    { \"from\": [7, 10, 2], \"to\": [9, 12, 14],\n      \"faces\": {\n        \"up\":    {\"texture\": \"#all\", \"uv\": [7,2,9,14]},\n        \"down\":  {\"texture\": \"#all\", \"uv\": [7,2,9,14]},\n        \"north\": {\"texture\": \"#all\", \"uv\": [7,10,9,12]},\n        \"south\": {\"texture\": \"#all\", \"uv\": [7,10,9,12]},\n        \"east\":  {\"texture\": \"#all\", \"uv\": [2,10,14,12]},\n        \"west\":  {\"texture\": \"#all\", \"uv\": [2,10,14,12]}\n      }\n    },\n    { \"from\": [6, 12, 2], \"to\": [10, 16, 4],\n      \"faces\": {\n        \"up\":    {\"texture\": \"#all\", \"uv\": [6,2,10,4]},\n        \"down\":  {\"texture\": \"#all\", \"uv\": [6,2,10,4]},\n        \"north\": {\"texture\": \"#all\", \"uv\": [6,12,10,16]},\n        \"south\": {\"texture\": \"#all\", \"uv\": [6,12,10,16]},\n        \"east\":  {\"texture\": \"#all\", \"uv\": [2,12,4,16]},\n        \"west\":  {\"texture\": \"#all\", \"uv\": [2,12,4,16]}\n      }\n    }\n  ]\n}";
        ResourceGenerator.writeFile(new File(blockModelsDir, "hand_crank.json"), crankBlockModel);
        String crankItemModel = "{\n  \"parent\": \"exerion:block/hand_crank\"\n}";
        ResourceGenerator.writeFile(new File(itemModelsDir, "hand_crank.json"), crankItemModel);
        ResourceGenerator.writeFile(new File(clientItemDir, "hand_crank.json"), "{\n  \"model\": {\n    \"type\": \"minecraft:model\",\n    \"model\": \"exerion:item/hand_crank\"\n  }\n}");
        HashMap<Character, Integer> wheelColors = new HashMap<Character, Integer>();
        wheelColors.put(Character.valueOf('W'), -9811396);
        wheelColors.put(Character.valueOf('L'), -6393776);
        wheelColors.put(Character.valueOf('S'), -11651540);
        wheelColors.put(Character.valueOf('I'), -10461088);
        wheelColors.put(Character.valueOf('H'), -7697782);
        wheelColors.put(Character.valueOf('D'), -12961222);
        wheelColors.put(Character.valueOf('B'), -13994541);
        wheelColors.put(Character.valueOf('b'), -9788698);
        wheelColors.put(Character.valueOf('.'), 0);
        String[] wheelGrid = new String[]{"WWWWWWWWWWWWWWWW", "WLLLLLLLLLLLLLLW", "WLIIIIIIIIIIIIIW", "WLIHHHHHHHHHHHIW", "WLIHWWWWWWSWWHIW", "WLIHWLLLLLLWWHIW", "WLIHWWWWWWWWWHIW", "WLIHWWWWWWWWWHIW", "WLIHWWWWWWWWWHIW", "WLIHWLLLLLLWWHIW", "WLIHWWWWWWSWWHIW", "WLIHDDDDDDDDDHIW", "WLIIIIIIIIIIIIIW", "WLLLLLLLLLLLLLLW", "WWWWWWWWWWWWWWWW", "................"};
        ResourceGenerator.createTexture(new File(blockTexDir, "water_wheel.png"), wheelGrid, wheelColors);
        ResourceGenerator.createTexture(new File(itemTexDir, "water_wheel.png"), wheelGrid, wheelColors);
        String wheelBlockstate = "{\n  \"variants\": {\n    \"spinning=false,facing=north\": { \"model\": \"exerion:block/water_wheel\" },\n    \"spinning=false,facing=south\": { \"model\": \"exerion:block/water_wheel\", \"y\": 180 },\n    \"spinning=false,facing=east\": { \"model\": \"exerion:block/water_wheel\", \"y\": 90 },\n    \"spinning=false,facing=west\": { \"model\": \"exerion:block/water_wheel\", \"y\": 270 },\n    \"spinning=true,facing=north\": { \"model\": \"exerion:block/water_wheel\" },\n    \"spinning=true,facing=south\": { \"model\": \"exerion:block/water_wheel\", \"y\": 180 },\n    \"spinning=true,facing=east\": { \"model\": \"exerion:block/water_wheel\", \"y\": 90 },\n    \"spinning=true,facing=west\": { \"model\": \"exerion:block/water_wheel\", \"y\": 270 }\n  }\n}";
        ResourceGenerator.writeFile(new File(blockstatesDir, "water_wheel.json"), wheelBlockstate);
        String wheelBlockModel = "{\n  \"parent\": \"minecraft:block/block\",\n  \"render_type\": \"minecraft:cutout\",\n  \"textures\": {\n    \"all\": \"exerion:block/water_wheel\",\n    \"particle\": \"exerion:block/water_wheel\"\n  },\n  \"elements\": [\n    { \"from\": [6, 6, 0], \"to\": [10, 10, 16],\n      \"faces\": {\n        \"north\": {\"texture\": \"#all\", \"uv\": [6,6,10,10]},\n        \"south\": {\"texture\": \"#all\", \"uv\": [6,6,10,10]},\n        \"east\":  {\"texture\": \"#all\", \"uv\": [0,6,16,10]},\n        \"west\":  {\"texture\": \"#all\", \"uv\": [0,6,16,10]}\n      }\n    },\n    { \"from\": [4, 4, 3], \"to\": [12, 12, 13],\n      \"faces\": {\n        \"north\": {\"texture\": \"#all\", \"uv\": [4,4,12,12]},\n        \"south\": {\"texture\": \"#all\", \"uv\": [4,4,12,12]},\n        \"up\":    {\"texture\": \"#all\", \"uv\": [4,3,12,13]},\n        \"down\":  {\"texture\": \"#all\", \"uv\": [4,3,12,13]},\n        \"east\":  {\"texture\": \"#all\", \"uv\": [3,4,13,12]},\n        \"west\":  {\"texture\": \"#all\", \"uv\": [3,4,13,12]}\n      }\n    },\n    { \"from\": [6, 12, 2], \"to\": [10, 16, 14],\n      \"faces\": {\n        \"north\": {\"texture\": \"#all\", \"uv\": [6,12,10,16]},\n        \"south\": {\"texture\": \"#all\", \"uv\": [6,12,10,16]},\n        \"east\":  {\"texture\": \"#all\", \"uv\": [2,12,14,16]},\n        \"west\":  {\"texture\": \"#all\", \"uv\": [2,12,14,16]},\n        \"up\":    {\"texture\": \"#all\", \"uv\": [6,2,10,14]},\n        \"down\":  {\"texture\": \"#all\", \"uv\": [6,2,10,14]}\n      }\n    },\n    { \"from\": [6, 0, 2], \"to\": [10, 4, 14],\n      \"faces\": {\n        \"north\": {\"texture\": \"#all\", \"uv\": [6,0,10,4]},\n        \"south\": {\"texture\": \"#all\", \"uv\": [6,0,10,4]},\n        \"east\":  {\"texture\": \"#all\", \"uv\": [2,0,14,4]},\n        \"west\":  {\"texture\": \"#all\", \"uv\": [2,0,14,4]},\n        \"up\":    {\"texture\": \"#all\", \"uv\": [6,2,10,14]},\n        \"down\":  {\"texture\": \"#all\", \"uv\": [6,2,10,14]}\n      }\n    },\n    { \"from\": [12, 6, 2], \"to\": [16, 10, 14],\n      \"faces\": {\n        \"north\": {\"texture\": \"#all\", \"uv\": [12,6,16,10]},\n        \"south\": {\"texture\": \"#all\", \"uv\": [12,6,16,10]},\n        \"up\":    {\"texture\": \"#all\", \"uv\": [12,2,16,14]},\n        \"down\":  {\"texture\": \"#all\", \"uv\": [12,2,16,14]},\n        \"east\":  {\"texture\": \"#all\", \"uv\": [2,6,14,10]},\n        \"west\":  {\"texture\": \"#all\", \"uv\": [2,6,14,10]}\n      }\n    },\n    { \"from\": [0, 6, 2], \"to\": [4, 10, 14],\n      \"faces\": {\n        \"north\": {\"texture\": \"#all\", \"uv\": [0,6,4,10]},\n        \"south\": {\"texture\": \"#all\", \"uv\": [0,6,4,10]},\n        \"up\":    {\"texture\": \"#all\", \"uv\": [0,2,4,14]},\n        \"down\":  {\"texture\": \"#all\", \"uv\": [0,2,4,14]},\n        \"east\":  {\"texture\": \"#all\", \"uv\": [2,6,14,10]},\n        \"west\":  {\"texture\": \"#all\", \"uv\": [2,6,14,10]}\n      }\n    },\n    { \"from\": [6, 12, 2], \"to\": [10, 16, 14],\n      \"rotation\": {\"origin\": [8, 8, 8], \"axis\": \"z\", \"angle\": 45},\n      \"faces\": {\n        \"north\": {\"texture\": \"#all\", \"uv\": [6,12,10,16]},\n        \"south\": {\"texture\": \"#all\", \"uv\": [6,12,10,16]},\n        \"east\":  {\"texture\": \"#all\", \"uv\": [2,12,14,16]},\n        \"west\":  {\"texture\": \"#all\", \"uv\": [2,12,14,16]},\n        \"up\":    {\"texture\": \"#all\", \"uv\": [6,2,10,14]},\n        \"down\":  {\"texture\": \"#all\", \"uv\": [6,2,10,14]}\n      }\n    },\n    { \"from\": [6, 12, 2], \"to\": [10, 16, 14],\n      \"rotation\": {\"origin\": [8, 8, 8], \"axis\": \"z\", \"angle\": -45},\n      \"faces\": {\n        \"north\": {\"texture\": \"#all\", \"uv\": [6,12,10,16]},\n        \"south\": {\"texture\": \"#all\", \"uv\": [6,12,10,16]},\n        \"east\":  {\"texture\": \"#all\", \"uv\": [2,12,14,16]},\n        \"west\":  {\"texture\": \"#all\", \"uv\": [2,12,14,16]},\n        \"up\":    {\"texture\": \"#all\", \"uv\": [6,2,10,14]},\n        \"down\":  {\"texture\": \"#all\", \"uv\": [6,2,10,14]}\n      }\n    },\n    { \"from\": [6, 0, 2], \"to\": [10, 4, 14],\n      \"rotation\": {\"origin\": [8, 8, 8], \"axis\": \"z\", \"angle\": 45},\n      \"faces\": {\n        \"north\": {\"texture\": \"#all\", \"uv\": [6,0,10,4]},\n        \"south\": {\"texture\": \"#all\", \"uv\": [6,0,10,4]},\n        \"east\":  {\"texture\": \"#all\", \"uv\": [2,0,14,4]},\n        \"west\":  {\"texture\": \"#all\", \"uv\": [2,0,14,4]},\n        \"up\":    {\"texture\": \"#all\", \"uv\": [6,2,10,14]},\n        \"down\":  {\"texture\": \"#all\", \"uv\": [6,2,10,14]}\n      }\n    },\n    { \"from\": [6, 0, 2], \"to\": [10, 4, 14],\n      \"rotation\": {\"origin\": [8, 8, 8], \"axis\": \"z\", \"angle\": -45},\n      \"faces\": {\n        \"north\": {\"texture\": \"#all\", \"uv\": [6,0,10,4]},\n        \"south\": {\"texture\": \"#all\", \"uv\": [6,0,10,4]},\n        \"east\":  {\"texture\": \"#all\", \"uv\": [2,0,14,4]},\n        \"west\":  {\"texture\": \"#all\", \"uv\": [2,0,14,4]},\n        \"up\":    {\"texture\": \"#all\", \"uv\": [6,2,10,14]},\n        \"down\":  {\"texture\": \"#all\", \"uv\": [6,2,10,14]}\n      }\n    }\n  ]\n}";
        ResourceGenerator.writeFile(new File(blockModelsDir, "water_wheel.json"), wheelBlockModel);
        String wheelItemModel = "{\n  \"parent\": \"exerion:block/water_wheel\"\n}";
        ResourceGenerator.writeFile(new File(itemModelsDir, "water_wheel.json"), wheelItemModel);
        ResourceGenerator.writeFile(new File(clientItemDir, "water_wheel.json"), "{\n  \"model\": {\n    \"type\": \"minecraft:model\",\n    \"model\": \"exerion:item/water_wheel\"\n  }\n}");
        File langDir = new File("src/main/resources/assets/exerion/lang");
        File enUsFile = new File(langDir, "en_us.json");
        if (enUsFile.exists()) {
            try {
                Object content = new String(Files.readAllBytes(enUsFile.toPath()), StandardCharsets.UTF_8).trim();
                if (((String)content).endsWith("}") && !((String)(content = ((String)content).substring(0, ((String)content).length() - 1).trim())).contains("block.exerion.shaft")) {
                    content = (String)content + ",\n  \"block.exerion.shaft\": \"Mechanical Shaft\",\n  \"item.exerion.shaft\": \"Mechanical Shaft\",\n  \"block.exerion.hand_crank\": \"Hand Crank\",\n  \"item.exerion.hand_crank\": \"Hand Crank\",\n  \"block.exerion.water_wheel\": \"Water Wheel\",\n  \"item.exerion.water_wheel\": \"Water Wheel\"\n}";
                    ResourceGenerator.writeFile(enUsFile, (String)content);
                    System.out.println("Added Era 1 translations to en_us.json");
                }
            }
            catch (Exception e) {
                System.err.println("Failed to update translations: " + e.getMessage());
            }
        }
    }
}

