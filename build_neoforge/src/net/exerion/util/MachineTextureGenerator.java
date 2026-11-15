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

public class MachineTextureGenerator {
    private static void drawNoise(Graphics2D g, int x, int y, int w, int h, Color base, int variance, Random rand) {
        for (int i = x; i < x + w; ++i) {
            for (int j = y; j < y + h; ++j) {
                int r = MachineTextureGenerator.clamp(base.getRed() + rand.nextInt(variance * 2) - variance);
                int gr = MachineTextureGenerator.clamp(base.getGreen() + rand.nextInt(variance * 2) - variance);
                int b = MachineTextureGenerator.clamp(base.getBlue() + rand.nextInt(variance * 2) - variance);
                g.setColor(new Color(r, gr, b));
                g.fillRect(i, j, 1, 1);
            }
        }
    }

    private static int clamp(int val) {
        return Math.max(0, Math.min(255, val));
    }

    private static void generateGeothermal(File dir) throws Exception {
        BufferedImage img = new BufferedImage(16, 16, 2);
        Graphics2D g = img.createGraphics();
        Random r = new Random(1L);
        MachineTextureGenerator.drawNoise(g, 0, 0, 16, 16, new Color(40, 40, 45), 10, r);
        g.setColor(new Color(30, 30, 30));
        g.fillRect(2, 2, 12, 12);
        MachineTextureGenerator.drawNoise(g, 3, 5, 10, 9, new Color(220, 80, 0), 20, r);
        g.setColor(new Color(255, 180, 0));
        g.fillRect(4, 10, 8, 2);
        g.setColor(new Color(60, 60, 65));
        for (int i = 3; i < 13; i += 3) {
            g.drawLine(i, 4, i, 13);
        }
        g.dispose();
        ImageIO.write((RenderedImage)img, "PNG", new File(dir, "geothermal_generator.png"));
    }

    private static void generateSteamTurbine(File dir) throws Exception {
        BufferedImage img = new BufferedImage(16, 16, 2);
        Graphics2D g = img.createGraphics();
        Random r = new Random(2L);
        MachineTextureGenerator.drawNoise(g, 0, 0, 16, 16, new Color(180, 110, 50), 15, r);
        g.setColor(new Color(100, 60, 20));
        g.drawRect(0, 0, 15, 15);
        g.drawRect(1, 1, 13, 13);
        g.setColor(new Color(40, 40, 40));
        g.fillOval(3, 3, 10, 10);
        g.setColor(new Color(150, 150, 150));
        g.fillArc(4, 4, 8, 8, 0, 45);
        g.fillArc(4, 4, 8, 8, 90, 45);
        g.fillArc(4, 4, 8, 8, 180, 45);
        g.fillArc(4, 4, 8, 8, 270, 45);
        g.setColor(new Color(255, 200, 0));
        g.fillRect(7, 7, 2, 2);
        g.dispose();
        ImageIO.write((RenderedImage)img, "PNG", new File(dir, "steam_turbine.png"));
    }

    private static void generateCrusher(File dir) throws Exception {
        BufferedImage img = new BufferedImage(16, 16, 2);
        Graphics2D g = img.createGraphics();
        Random r = new Random(3L);
        MachineTextureGenerator.drawNoise(g, 0, 0, 16, 16, new Color(110, 110, 110), 15, r);
        g.setColor(new Color(20, 20, 20));
        g.fillRect(2, 4, 12, 8);
        g.setColor(new Color(180, 180, 180));
        for (int i = 3; i < 13; i += 3) {
            g.fillPolygon(new int[]{i, i + 1, i + 2}, new int[]{4, 7, 4}, 3);
            g.fillPolygon(new int[]{i, i + 1, i + 2}, new int[]{11, 8, 11}, 3);
        }
        g.dispose();
        ImageIO.write((RenderedImage)img, "PNG", new File(dir, "crusher.png"));
        ImageIO.write((RenderedImage)img, "PNG", new File(dir, "electric_crusher.png"));
    }

    private static void generateSolar(File dir) throws Exception {
        BufferedImage img = new BufferedImage(16, 16, 2);
        Graphics2D g = img.createGraphics();
        Random r = new Random(4L);
        MachineTextureGenerator.drawNoise(g, 0, 0, 16, 16, new Color(20, 50, 200), 20, r);
        g.setColor(new Color(200, 200, 200));
        for (int i = 0; i <= 16; i += 4) {
            g.drawLine(i, 0, i, 15);
            g.drawLine(0, i, 15, i);
        }
        g.dispose();
        ImageIO.write((RenderedImage)img, "PNG", new File(dir, "copper_solar_panel_top.png"));
        ImageIO.write((RenderedImage)img, "PNG", new File(dir, "steel_solar_panel_top.png"));
        ImageIO.write((RenderedImage)img, "PNG", new File(dir, "exerion_solar_panel_top.png"));
    }

    private static void generateBatBox(File dir) throws Exception {
        BufferedImage img = new BufferedImage(16, 16, 2);
        Graphics2D g = img.createGraphics();
        Random r = new Random(5L);
        MachineTextureGenerator.drawNoise(g, 0, 0, 16, 16, new Color(130, 90, 50), 15, r);
        g.setColor(new Color(20, 20, 20));
        g.fillRect(4, 4, 8, 8);
        g.setColor(new Color(0, 200, 0));
        g.fillRect(5, 5, 6, 6);
        g.setColor(new Color(255, 255, 255));
        g.drawLine(8, 8, 10, 6);
        g.fillOval(7, 7, 3, 3);
        g.dispose();
        ImageIO.write((RenderedImage)img, "PNG", new File(dir, "batbox_front.png"));
    }

    private static void generateGearPress(File dir) throws Exception {
        BufferedImage img = new BufferedImage(16, 16, 2);
        Graphics2D g = img.createGraphics();
        Random r = new Random(6L);
        MachineTextureGenerator.drawNoise(g, 0, 0, 16, 16, new Color(80, 80, 85), 10, r);
        g.setColor(new Color(30, 30, 30));
        g.fillRect(3, 2, 10, 12);
        g.setColor(new Color(150, 150, 150));
        g.fillRect(4, 2, 8, 4);
        g.setColor(new Color(200, 150, 50));
        g.fillRect(7, 9, 2, 2);
        g.drawLine(6, 10, 9, 10);
        g.drawLine(7, 9, 7, 11);
        g.dispose();
        ImageIO.write((RenderedImage)img, "PNG", new File(dir, "gear_press.png"));
    }

    private static void generateMiningLaser(File dir) throws Exception {
        BufferedImage img = new BufferedImage(16, 16, 2);
        Graphics2D g = img.createGraphics();
        Random r = new Random(7L);
        MachineTextureGenerator.drawNoise(g, 0, 0, 16, 16, new Color(25, 25, 30), 5, r);
        g.setColor(new Color(0, 255, 255));
        g.drawRect(1, 1, 13, 13);
        g.setColor(new Color(200, 0, 0));
        g.fillOval(5, 5, 6, 6);
        g.setColor(new Color(255, 100, 100));
        g.fillOval(6, 6, 4, 4);
        g.setColor(new Color(255, 255, 255));
        g.fillOval(7, 7, 2, 2);
        g.dispose();
        ImageIO.write((RenderedImage)img, "PNG", new File(dir, "mining_laser.png"));
    }

    public static void main(String[] args) throws Exception {
        File dir = new File("src/main/resources/assets/exerion/textures/block/");
        dir.mkdirs();
        MachineTextureGenerator.generateGeothermal(dir);
        MachineTextureGenerator.generateSteamTurbine(dir);
        MachineTextureGenerator.generateCrusher(dir);
        MachineTextureGenerator.generateSolar(dir);
        MachineTextureGenerator.generateBatBox(dir);
        MachineTextureGenerator.generateGearPress(dir);
        MachineTextureGenerator.generateMiningLaser(dir);
        System.out.println("Unique Machine Textures Generated!");
    }
}

