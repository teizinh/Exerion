/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 */
package net.exerion.item;

import net.exerion.client.ExerionGuideBookScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class ClientOnly {
    public static void openBook() {
        Minecraft.getInstance().setScreen((Screen)new ExerionGuideBookScreen());
    }
}

