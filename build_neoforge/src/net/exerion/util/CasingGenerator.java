/*
 * Decompiled with CFR 0.152.
 */
package net.exerion.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class CasingGenerator {
    private static BufferedImage createBaseCasing() {
        BufferedImage img = new BufferedImage(16, 16, 2);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(90, 94, 99));
        g.fillRect(0, 0, 16, 16);
        g.setColor(new Color(50, 52, 55));
        g.drawRect(0, 0, 15, 15);
        g.setColor(new Color(110, 115, 120));
        g.drawRect(1, 1, 13, 13);
        g.setColor(new Color(40, 42, 45));
        g.fillRect(2, 2, 1, 1);
        g.fillRect(13, 2, 1, 1);
        g.fillRect(2, 13, 1, 1);
        g.fillRect(13, 13, 1, 1);
        g.setColor(new Color(40, 42, 45));
        for (int y = 4; y <= 12; y += 2) {
            g.drawLine(4, y, 11, y);
        }
        g.dispose();
        return img;
    }

    private static BufferedImage createMachineFront(BufferedImage base, String type) {
        BufferedImage img = new BufferedImage(16, 16, 2);
        Graphics2D g = img.createGraphics();
        g.drawImage((Image)base, 0, 0, null);
        g.setColor(new Color(90, 94, 99));
        g.fillRect(3, 3, 10, 10);
        if ("furnace".equals(type)) {
            g.setColor(new Color(20, 20, 20));
            g.fillRect(4, 6, 8, 6);
            g.setColor(new Color(255, 100, 0));
            g.fillRect(5, 7, 6, 4);
        } else if ("pump".equals(type)) {
            g.setColor(new Color(60, 60, 120));
            g.fillRect(6, 3, 4, 10);
            g.setColor(new Color(100, 100, 180));
            g.fillRect(7, 4, 2, 8);
            g.setColor(new Color(40, 40, 40));
            g.fillRect(5, 12, 6, 3);
        } else if ("chute".equals(type)) {
            g.setColor(new Color(30, 30, 30));
            g.fillRect(4, 4, 8, 8);
            g.setColor(new Color(10, 10, 10));
            g.fillRect(6, 6, 4, 4);
        } else if ("ender_chest".equals(type)) {
            g.setColor(new Color(100, 0, 100));
            g.fillRect(5, 5, 6, 6);
            g.setColor(new Color(200, 0, 200));
            g.fillRect(6, 6, 4, 4);
        }
        g.dispose();
        return img;
    }

    private static BufferedImage createEnderTank() {
        BufferedImage img = new BufferedImage(16, 16, 2);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(30, 20, 40));
        g.fillRect(0, 0, 16, 16);
        g.setColor(new Color(10, 5, 15));
        g.drawRect(0, 0, 15, 15);
        g.setColor(new Color(0, 200, 200, 150));
        g.fillRect(2, 2, 12, 12);
        g.setColor(new Color(0, 255, 255, 180));
        g.fillRect(4, 4, 8, 8);
        g.dispose();
        return img;
    }

    public static void main(String[] args) throws Exception {
        String[] chutes;
        String[] furnaces;
        String dir = "src/main/resources/assets/exerion/textures/block/";
        new File(dir).mkdirs();
        BufferedImage base = CasingGenerator.createBaseCasing();
        ImageIO.write((RenderedImage)base, "PNG", new File(dir + "exerion_casing.png"));
        ImageIO.write((RenderedImage)base, "PNG", new File(dir + "steel_casing.png"));
        for (String f : furnaces = new String[]{"electric_furnace", "coal_boiler", "alloy_smelter_front", "geothermal_generator"}) {
            ImageIO.write((RenderedImage)CasingGenerator.createMachineFront(base, "furnace"), "PNG", new File(dir + f + ".png"));
        }
        for (String c : chutes = new String[]{"mechanical_chute", "electric_crusher", "compressor_front", "electric_generator"}) {
            ImageIO.write((RenderedImage)CasingGenerator.createMachineFront(base, "chute"), "PNG", new File(dir + c + ".png"));
        }
        ImageIO.write((RenderedImage)CasingGenerator.createMachineFront(base, "pump"), "PNG", new File(dir + "pump.png"));
        ImageIO.write((RenderedImage)CasingGenerator.createMachineFront(base, "ender_chest"), "PNG", new File(dir + "frequency_ender_chest_front.png"));
        ImageIO.write((RenderedImage)CasingGenerator.createMachineFront(base, "ender_chest"), "PNG", new File(dir + "frequency_ender_chest_side.png"));
        ImageIO.write((RenderedImage)CasingGenerator.createEnderTank(), "PNG", new File(dir + "frequency_ender_tank.png"));
        System.out.println("Casing Textures Generated!");
    }
}

