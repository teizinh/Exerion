/*
 * Decompiled with CFR 0.152.
 */
package net.exerion.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.Random;
import javax.imageio.ImageIO;

public class HighResMachineTextureGenerator {
    private static void drawNoise(Graphics2D g, int x, int y, int w, int h, Color base, int variance, Random rand) {
        for (int i = x; i < x + w; ++i) {
            for (int j = y; j < y + h; ++j) {
                int r = HighResMachineTextureGenerator.clamp(base.getRed() + rand.nextInt(variance * 2) - variance);
                int gr = HighResMachineTextureGenerator.clamp(base.getGreen() + rand.nextInt(variance * 2) - variance);
                int b = HighResMachineTextureGenerator.clamp(base.getBlue() + rand.nextInt(variance * 2) - variance);
                g.setColor(new Color(r, gr, b));
                g.fillRect(i, j, 1, 1);
            }
        }
    }

    private static int clamp(int val) {
        return Math.max(0, Math.min(255, val));
    }

    private static void generateGeothermal(File dir, boolean active) throws Exception {
        BufferedImage img = new BufferedImage(64, 64, 2);
        Graphics2D g = img.createGraphics();
        Random r = new Random(101L);
        HighResMachineTextureGenerator.drawNoise(g, 0, 0, 64, 64, new Color(30, 30, 35), 8, r);
        g.setColor(new Color(20, 20, 20));
        g.fillRect(8, 8, 48, 48);
        if (active) {
            HighResMachineTextureGenerator.drawNoise(g, 12, 16, 40, 40, new Color(220, 60, 0), 30, r);
            g.setColor(new Color(255, 150, 0, 200));
            g.fillOval(20, 24, 24, 24);
            g.setColor(new Color(255, 255, 100, 200));
            g.fillOval(28, 32, 8, 8);
        }
        g.setColor(new Color(50, 50, 55));
        for (int i = 12; i < 52; i += 8) {
            g.drawLine(i, 16, i, 56);
            g.drawLine(i + 1, 16, i + 1, 56);
        }
        g.setColor(new Color(80, 80, 80));
        g.fillOval(4, 4, 4, 4);
        g.fillOval(56, 4, 4, 4);
        g.fillOval(4, 56, 4, 4);
        g.fillOval(56, 56, 4, 4);
        g.dispose();
        ImageIO.write((RenderedImage)img, "PNG", new File(dir, active ? "geothermal_generator_active.png" : "geothermal_generator.png"));
    }

    private static void generateSteamTurbine(File dir, boolean active) throws Exception {
        BufferedImage img = new BufferedImage(64, 64, 2);
        Graphics2D g = img.createGraphics();
        Random r = new Random(102L);
        HighResMachineTextureGenerator.drawNoise(g, 0, 0, 64, 64, new Color(160, 90, 40), 15, r);
        g.setColor(new Color(20, 15, 10));
        g.fillOval(8, 8, 48, 48);
        g.setColor(new Color(180, 100, 50));
        for (int i = 0; i < 8; ++i) {
            double angle = 0.7853981633974483 * (double)i + (active ? 0.3 : 0.0);
            int dx = (int)(Math.cos(angle) * 20.0);
            int dy = (int)(Math.sin(angle) * 20.0);
            g.fillPolygon(new int[]{32, 32 + dx - 4, 32 + dx + 4}, new int[]{32, 32 + dy + 4, 32 + dy - 4}, 3);
        }
        if (active) {
            g.setColor(new Color(200, 220, 255, 100));
            g.fillOval(16, 16, 32, 32);
        }
        g.dispose();
        ImageIO.write((RenderedImage)img, "PNG", new File(dir, active ? "steam_turbine_active.png" : "steam_turbine.png"));
    }

    private static void generateCrusher(File dir, boolean active) throws Exception {
        int i;
        BufferedImage img = new BufferedImage(64, 64, 2);
        Graphics2D g = img.createGraphics();
        Random r = new Random(103L);
        HighResMachineTextureGenerator.drawNoise(g, 0, 0, 64, 64, new Color(100, 100, 100), 10, r);
        g.setColor(new Color(20, 20, 20));
        g.fillRect(8, 16, 48, 32);
        if (active) {
            g.setColor(Color.YELLOW);
            g.fillOval(32, 32, 4, 4);
            g.fillOval(20, 40, 3, 3);
            g.fillOval(40, 20, 3, 3);
            g.setColor(new Color(70, 70, 70, 150));
            g.fillRect(12, 28, 40, 8);
        }
        g.setColor(new Color(60, 60, 60));
        for (i = 12; i < 52; i += 12) {
            g.fillPolygon(new int[]{i, i + 6, i + 12}, new int[]{16, 28, 16}, 3);
            g.fillPolygon(new int[]{i, i + 6, i + 12}, new int[]{48, 36, 48}, 3);
        }
        for (i = 0; i < 64; i += 8) {
            g.setColor(i % 16 == 0 ? new Color(200, 200, 0) : new Color(30, 30, 30));
            g.fillRect(i, 4, 8, 8);
            g.fillRect(i, 52, 8, 8);
        }
        g.dispose();
        ImageIO.write((RenderedImage)img, "PNG", new File(dir, active ? "crusher_active.png" : "crusher.png"));
    }

    private static void generateElectricCrusher(File dir, boolean active) throws Exception {
        int i;
        BufferedImage img = new BufferedImage(64, 64, 2);
        Graphics2D g = img.createGraphics();
        Random r = new Random(116L);
        HighResMachineTextureGenerator.drawNoise(g, 0, 0, 64, 64, new Color(110, 115, 120), 10, r);
        g.setColor(new Color(15, 15, 20));
        g.fillRect(8, 16, 48, 32);
        if (active) {
            g.setColor(Color.CYAN);
            g.fillOval(30, 30, 4, 4);
            g.fillOval(18, 38, 3, 3);
            g.fillOval(42, 22, 3, 3);
            g.setColor(new Color(0, 150, 255, 150));
            g.fillRect(12, 28, 40, 8);
        }
        g.setColor(new Color(0, 200, 200));
        for (i = 12; i < 52; i += 12) {
            g.fillPolygon(new int[]{i, i + 6, i + 12}, new int[]{16, 28, 16}, 3);
            g.fillPolygon(new int[]{i, i + 6, i + 12}, new int[]{48, 36, 48}, 3);
        }
        for (i = 0; i < 64; i += 8) {
            g.setColor(i % 16 == 0 ? new Color(0, 150, 255) : new Color(30, 30, 40));
            g.fillRect(i, 4, 8, 8);
            g.fillRect(i, 52, 8, 8);
        }
        g.dispose();
        ImageIO.write((RenderedImage)img, "PNG", new File(dir, active ? "electric_crusher_active.png" : "electric_crusher.png"));
    }

    private static void generateSolar(File dir) throws Exception {
        BufferedImage img = new BufferedImage(64, 64, 2);
        Graphics2D g = img.createGraphics();
        Random r = new Random(104L);
        HighResMachineTextureGenerator.drawNoise(g, 0, 0, 64, 64, new Color(40, 40, 40), 5, r);
        g.setColor(new Color(20, 50, 100));
        for (int i = 4; i < 60; i += 14) {
            for (int j = 4; j < 60; j += 14) {
                g.fillRect(i, j, 12, 12);
                g.setColor(new Color(40, 100, 200));
                g.drawRect(i, j, 12, 12);
                g.setColor(new Color(20, 50, 100));
            }
        }
        g.dispose();
        ImageIO.write((RenderedImage)img, "PNG", new File(dir, "copper_solar_panel_top.png"));
        ImageIO.write((RenderedImage)img, "PNG", new File(dir, "steel_solar_panel_top.png"));
        ImageIO.write((RenderedImage)img, "PNG", new File(dir, "exerion_solar_panel_top.png"));
    }

    private static void generateBatBox(File dir) throws Exception {
        BufferedImage img = new BufferedImage(64, 64, 2);
        Graphics2D g = img.createGraphics();
        Random r = new Random(105L);
        HighResMachineTextureGenerator.drawNoise(g, 0, 0, 64, 64, new Color(120, 100, 80), 10, r);
        g.setColor(new Color(50, 40, 30));
        g.fillRect(16, 16, 32, 32);
        g.setColor(Color.GREEN);
        g.fillOval(30, 30, 4, 4);
        g.setColor(Color.RED);
        g.fillRect(20, 20, 24, 4);
        g.dispose();
        ImageIO.write((RenderedImage)img, "PNG", new File(dir, "batbox.png"));
    }

    private static void generateGearPress(File dir) throws Exception {
        BufferedImage img = new BufferedImage(64, 64, 2);
        Graphics2D g = img.createGraphics();
        Random r = new Random(106L);
        HighResMachineTextureGenerator.drawNoise(g, 0, 0, 64, 64, new Color(90, 80, 70), 15, r);
        g.setColor(new Color(40, 40, 40));
        g.fillRect(16, 16, 32, 32);
        g.setColor(new Color(150, 150, 150));
        g.fillRoundRect(24, 24, 16, 16, 8, 8);
        g.dispose();
        ImageIO.write((RenderedImage)img, "PNG", new File(dir, "gear_press.png"));
    }

    private static void generateMiningLaser(File dir) throws Exception {
        BufferedImage img = new BufferedImage(64, 64, 2);
        Graphics2D g = img.createGraphics();
        Random r = new Random(108L);
        HighResMachineTextureGenerator.drawNoise(g, 0, 0, 64, 64, new Color(40, 40, 40), 10, r);
        g.setColor(Color.RED);
        g.fillOval(24, 24, 16, 16);
        g.setColor(new Color(255, 100, 100));
        g.fillOval(28, 28, 8, 8);
        g.setColor(Color.WHITE);
        g.fillOval(30, 30, 4, 4);
        g.dispose();
        ImageIO.write((RenderedImage)img, "PNG", new File(dir, "mining_laser.png"));
    }

    private static void generateCoalBoiler(File dir, boolean active) throws Exception {
        BufferedImage img = new BufferedImage(64, 64, 2);
        Graphics2D g = img.createGraphics();
        Random r = new Random(109L);
        HighResMachineTextureGenerator.drawNoise(g, 0, 0, 64, 64, new Color(50, 50, 50), 10, r);
        g.setColor(new Color(40, 50, 60));
        g.fillRect(16, 8, 32, 24);
        g.setColor(new Color(20, 20, 20));
        g.fillRect(16, 40, 32, 16);
        if (active) {
            HighResMachineTextureGenerator.drawNoise(g, 20, 44, 24, 10, new Color(255, 100, 0), 20, r);
            g.setColor(new Color(255, 200, 50, 200));
            g.fillRect(24, 48, 16, 4);
        }
        g.dispose();
        ImageIO.write((RenderedImage)img, "PNG", new File(dir, active ? "coal_boiler_active.png" : "coal_boiler.png"));
    }

    private static void generateAlloySmelter(File dir, boolean active) throws Exception {
        BufferedImage img = new BufferedImage(64, 64, 2);
        Graphics2D g = img.createGraphics();
        Random r = new Random(110L);
        HighResMachineTextureGenerator.drawNoise(g, 0, 0, 64, 64, new Color(60, 60, 60), 10, r);
        g.setColor(new Color(30, 30, 30));
        g.fillRect(16, 16, 32, 32);
        g.setColor(new Color(40, 40, 40));
        g.fillRoundRect(20, 20, 24, 24, 10, 10);
        if (active) {
            g.setColor(new Color(255, 120, 20));
            g.fillRoundRect(22, 22, 20, 20, 8, 8);
            g.setColor(new Color(255, 200, 100));
            g.fillRoundRect(26, 26, 12, 12, 4, 4);
        } else {
            g.setColor(new Color(10, 10, 10));
            g.fillRect(26, 46, 12, 10);
        }
        g.dispose();
        ImageIO.write((RenderedImage)img, "PNG", new File(dir, active ? "alloy_smelter_active.png" : "alloy_smelter.png"));
    }

    private static void generateElectricFurnace(File dir, boolean active) throws Exception {
        BufferedImage img = new BufferedImage(64, 64, 2);
        Graphics2D g = img.createGraphics();
        Random r = new Random(111L);
        HighResMachineTextureGenerator.drawNoise(g, 0, 0, 64, 64, new Color(80, 90, 100), 8, r);
        g.setColor(new Color(20, 20, 25));
        g.fillRoundRect(12, 16, 40, 32, 8, 8);
        for (int y = 20; y < 44; y += 8) {
            if (active) {
                g.setColor(new Color(255, 50, 50));
                g.fillRect(16, y, 32, 4);
                g.setColor(new Color(255, 150, 100));
                g.fillRect(18, y + 1, 28, 2);
                continue;
            }
            g.setColor(new Color(40, 40, 45));
            g.fillRect(16, y, 32, 4);
        }
        g.dispose();
        ImageIO.write((RenderedImage)img, "PNG", new File(dir, active ? "electric_furnace_active.png" : "electric_furnace.png"));
    }

    private static void generateMechanicalChute(File dir) throws Exception {
        BufferedImage img = new BufferedImage(64, 64, 2);
        Graphics2D g = img.createGraphics();
        Random r = new Random(112L);
        HighResMachineTextureGenerator.drawNoise(g, 0, 0, 64, 64, new Color(120, 110, 100), 15, r);
        g.setColor(new Color(30, 25, 20));
        g.fillRect(8, 8, 48, 48);
        g.setColor(new Color(15, 10, 10));
        g.fillRect(16, 16, 32, 32);
        g.setColor(new Color(5, 5, 5));
        g.fillRect(24, 24, 16, 16);
        g.dispose();
        ImageIO.write((RenderedImage)img, "PNG", new File(dir, "mechanical_chute.png"));
    }

    private static void generateCompressor(File dir, boolean active) throws Exception {
        BufferedImage img = new BufferedImage(64, 64, 2);
        Graphics2D g = img.createGraphics();
        Random r = new Random(113L);
        HighResMachineTextureGenerator.drawNoise(g, 0, 0, 64, 64, new Color(80, 85, 90), 10, r);
        g.setColor(new Color(20, 20, 20));
        g.fillRect(16, 16, 32, 32);
        g.setColor(new Color(150, 150, 150));
        g.fillRect(20, active ? 24 : 16, 24, 12);
        g.setColor(new Color(100, 100, 100));
        g.fillRect(20, 40, 24, 8);
        if (active) {
            g.setColor(new Color(0, 200, 255));
            g.fillRect(12, 16, 4, 32);
            g.fillRect(48, 16, 4, 32);
        }
        g.dispose();
        if (!active) {
            ImageIO.write((RenderedImage)img, "PNG", new File(dir, "compressor_front.png"));
            ImageIO.write((RenderedImage)img, "PNG", new File(dir, "compressor.png"));
        } else {
            ImageIO.write((RenderedImage)img, "PNG", new File(dir, "compressor_active.png"));
        }
    }

    private static void generateElectricGenerator(File dir, boolean active) throws Exception {
        BufferedImage img = new BufferedImage(64, 64, 2);
        Graphics2D g = img.createGraphics();
        Random r = new Random(114L);
        HighResMachineTextureGenerator.drawNoise(g, 0, 0, 64, 64, new Color(70, 75, 80), 8, r);
        g.setColor(new Color(30, 30, 30));
        g.fillRect(16, 12, 32, 40);
        g.setColor(new Color(200, 100, 20));
        for (int y = 14; y < 50; y += 6) {
            g.fillRect(18, y, 28, 4);
            if (!active) continue;
            g.setColor(new Color(100, 200, 255, 100));
            g.fillRect(20, y, 24, 4);
            g.setColor(new Color(200, 100, 20));
        }
        g.setColor(active ? new Color(0, 255, 0) : new Color(50, 50, 50));
        g.fillOval(8, 8, 6, 6);
        g.fillOval(50, 8, 6, 6);
        g.dispose();
        ImageIO.write((RenderedImage)img, "PNG", new File(dir, active ? "electric_generator_active.png" : "electric_generator.png"));
    }

    private static void generatePump(File dir) throws Exception {
        BufferedImage img = new BufferedImage(64, 64, 2);
        Graphics2D g = img.createGraphics();
        Random r = new Random(115L);
        HighResMachineTextureGenerator.drawNoise(g, 0, 0, 64, 64, new Color(60, 60, 80), 12, r);
        g.setColor(new Color(30, 30, 100));
        g.fillRect(24, 0, 16, 64);
        g.setColor(new Color(50, 50, 150));
        g.fillRect(28, 0, 8, 64);
        g.setColor(new Color(40, 40, 40));
        g.fillRect(20, 16, 24, 8);
        g.fillRect(20, 40, 24, 8);
        g.dispose();
        ImageIO.write((RenderedImage)img, "PNG", new File(dir, "pump.png"));
    }

    private static void generateFrequencyEnderChest(File dir) throws Exception {
        BufferedImage img = new BufferedImage(64, 64, 2);
        Graphics2D g = img.createGraphics();
        Random r = new Random(117L);
        HighResMachineTextureGenerator.drawNoise(g, 0, 0, 64, 64, new Color(20, 10, 25), 8, r);
        g.setColor(new Color(10, 200, 100, 150));
        g.fillRect(8, 8, 48, 4);
        g.fillRect(8, 52, 48, 4);
        g.fillRect(8, 8, 4, 48);
        g.fillRect(52, 8, 4, 48);
        g.setColor(new Color(5, 50, 20));
        g.fillRoundRect(20, 20, 24, 24, 8, 8);
        g.setColor(new Color(20, 255, 120));
        g.fillPolygon(new int[]{32, 24, 32, 40}, new int[]{22, 32, 42, 32}, 4);
        g.dispose();
        ImageIO.write((RenderedImage)img, "PNG", new File(dir, "frequency_ender_chest.png"));
    }

    private static void generateCasings(File dir) throws Exception {
        BufferedImage steelImg = new BufferedImage(64, 64, 2);
        Graphics2D gs = steelImg.createGraphics();
        Random rs = new Random(118L);
        HighResMachineTextureGenerator.drawNoise(gs, 0, 0, 64, 64, new Color(100, 105, 110), 8, rs);
        gs.setColor(new Color(60, 65, 70));
        gs.fillRect(0, 0, 64, 4);
        gs.fillRect(0, 60, 64, 4);
        gs.fillRect(0, 0, 4, 64);
        gs.fillRect(60, 0, 4, 64);
        gs.setColor(new Color(40, 45, 50));
        gs.fillRect(8, 8, 4, 4);
        gs.fillRect(52, 8, 4, 4);
        gs.fillRect(8, 52, 4, 4);
        gs.fillRect(52, 52, 4, 4);
        gs.dispose();
        ImageIO.write((RenderedImage)steelImg, "PNG", new File(dir, "steel_casing.png"));
        BufferedImage exerionImg = new BufferedImage(64, 64, 2);
        Graphics2D ge = exerionImg.createGraphics();
        Random re = new Random(119L);
        HighResMachineTextureGenerator.drawNoise(ge, 0, 0, 64, 64, new Color(20, 25, 30), 5, re);
        ge.setColor(new Color(10, 15, 20));
        ge.fillRect(0, 0, 64, 8);
        ge.fillRect(0, 56, 64, 8);
        ge.fillRect(0, 0, 8, 64);
        ge.fillRect(56, 0, 8, 64);
        ge.setColor(new Color(150, 50, 255));
        ge.fillRect(16, 16, 32, 2);
        ge.fillRect(16, 46, 32, 2);
        ge.fillRect(16, 16, 2, 32);
        ge.fillRect(46, 16, 2, 32);
        ge.setColor(new Color(0, 255, 255));
        ge.fillPolygon(new int[]{32, 24, 32, 40}, new int[]{24, 32, 40, 32}, 4);
        ge.dispose();
        ImageIO.write((RenderedImage)exerionImg, "PNG", new File(dir, "exerion_casing.png"));
    }

    public static void main(String[] args) throws Exception {
        File dir = new File("src/main/resources/assets/exerion/textures/block/");
        dir.mkdirs();
        HighResMachineTextureGenerator.generateGeothermal(dir, false);
        HighResMachineTextureGenerator.generateGeothermal(dir, true);
        HighResMachineTextureGenerator.generateSteamTurbine(dir, false);
        HighResMachineTextureGenerator.generateSteamTurbine(dir, true);
        HighResMachineTextureGenerator.generateCrusher(dir, false);
        HighResMachineTextureGenerator.generateCrusher(dir, true);
        HighResMachineTextureGenerator.generateElectricCrusher(dir, false);
        HighResMachineTextureGenerator.generateElectricCrusher(dir, true);
        HighResMachineTextureGenerator.generateSolar(dir);
        HighResMachineTextureGenerator.generateBatBox(dir);
        HighResMachineTextureGenerator.generateGearPress(dir);
        HighResMachineTextureGenerator.generateMiningLaser(dir);
        HighResMachineTextureGenerator.generateCoalBoiler(dir, false);
        HighResMachineTextureGenerator.generateCoalBoiler(dir, true);
        HighResMachineTextureGenerator.generateAlloySmelter(dir, false);
        HighResMachineTextureGenerator.generateAlloySmelter(dir, true);
        HighResMachineTextureGenerator.generateElectricFurnace(dir, false);
        HighResMachineTextureGenerator.generateElectricFurnace(dir, true);
        HighResMachineTextureGenerator.generateMechanicalChute(dir);
        HighResMachineTextureGenerator.generateCompressor(dir, false);
        HighResMachineTextureGenerator.generateCompressor(dir, true);
        HighResMachineTextureGenerator.generateElectricGenerator(dir, false);
        HighResMachineTextureGenerator.generateElectricGenerator(dir, true);
        HighResMachineTextureGenerator.generatePump(dir);
        HighResMachineTextureGenerator.generateFrequencyEnderChest(dir);
        HighResMachineTextureGenerator.generateCasings(dir);
        System.out.println("High Resolution (64x64) Unique Machine Textures Generated!");
    }
}

