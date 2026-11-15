/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 */
package net.exerion.client;

import net.exerion.client.GuiHelper;
import net.exerion.menu.ProgressionTerminalMenu;
import net.exerion.network.ExerionNetwork;
import net.exerion.progression.PlayerEraManager;
import net.exerion.progression.TechnologyEra;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class ProgressionTerminalScreen
extends AbstractContainerScreen<ProgressionTerminalMenu> {
    public ProgressionTerminalScreen(ProgressionTerminalMenu menu, Inventory playerInventory, Component title) {
        super((AbstractContainerMenu)menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 222;
    }

    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width((FormattedText)this.title)) / 2;
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.literal((String)"\u25ba Advance Era"), btn -> ExerionNetwork.sendAdvanceEraPacket()).bounds(x + 50, y + 46, 76, 16).build());
    }

    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        GuiHelper.drawVanillaBackground(graphics, x, y, this.imageWidth, this.imageHeight);
        GuiHelper.drawPlayerInventorySlots(graphics, x, y, 140);
        for (int i = 0; i < 5; ++i) {
            GuiHelper.drawVanillaSlot(graphics, x + 8 + i * 18, y + 20);
        }
        int infoX = x + 5;
        int infoY = y + 68;
        int infoW = 166;
        int infoH = 56;
        graphics.fill(infoX - 1, infoY - 1, infoX + infoW + 1, infoY, -13158601);
        graphics.fill(infoX - 1, infoY, infoX, infoY + infoH + 1, -13158601);
        graphics.fill(infoX, infoY, infoX + infoW, infoY + infoH, -7631989);
        graphics.fill(infoX, infoY + infoH, infoX + infoW + 1, infoY + infoH + 1, -1);
        graphics.fill(infoX + infoW, infoY, infoX + infoW + 1, infoY + infoH, -1);
    }

    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, 4, -12566464, false);
        graphics.drawString(this.font, this.playerInventoryTitle, 8, 129, -12566464, false);
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            TechnologyEra era = PlayerEraManager.getPlayerEra((Player)player);
            MutableComponent eraName = era.getDisplayNameComponent();
            int eraColor = switch (era) {
                default -> throw new MatchException(null, null);
                case TechnologyEra.STONE -> -7829368;
                case TechnologyEra.COPPER -> -2525644;
                case TechnologyEra.IRON -> -2565928;
                case TechnologyEra.MECHANICAL -> -5011107;
                case TechnologyEra.STEEL -> -16755303;
                case TechnologyEra.INDUSTRIAL -> -7838208;
                case TechnologyEra.DIGITAL -> -6750055;
                case TechnologyEra.ADVANCED -> -16733526;
                case TechnologyEra.QUANTUM -> -5635926;
                case TechnologyEra.COSMIC -> -16777216;
            };
            graphics.drawString(this.font, (Component)Component.literal((String)"Current Era: "), 8, 72, -14540254, false);
            graphics.drawString(this.font, (Component)eraName, 8 + this.font.width("Current Era: "), 72, eraColor, false);
            TechnologyEra nextEra = this.getNextEra(era);
            if (nextEra != null) {
                graphics.drawString(this.font, (Component)Component.literal((String)"Next: ").append((Component)nextEra.getDisplayNameComponent()), 8, 83, -14540254, false);
                String[] reqs = this.getRequirementsText(era);
                int lineY = 94;
                for (String req : reqs) {
                    graphics.drawString(this.font, (Component)Component.literal((String)("- " + req)), 8, lineY, -13421773, false);
                    lineY += 10;
                }
            } else {
                graphics.drawString(this.font, (Component)Component.literal((String)"Max era reached!"), 8, 83, -8757248, false);
            }
        }
    }

    private TechnologyEra getNextEra(TechnologyEra current) {
        return switch (current) {
            default -> throw new MatchException(null, null);
            case TechnologyEra.STONE -> TechnologyEra.COPPER;
            case TechnologyEra.COPPER -> TechnologyEra.IRON;
            case TechnologyEra.IRON -> TechnologyEra.MECHANICAL;
            case TechnologyEra.MECHANICAL -> TechnologyEra.STEEL;
            case TechnologyEra.STEEL -> TechnologyEra.INDUSTRIAL;
            case TechnologyEra.INDUSTRIAL -> TechnologyEra.DIGITAL;
            case TechnologyEra.DIGITAL -> TechnologyEra.ADVANCED;
            case TechnologyEra.ADVANCED -> TechnologyEra.QUANTUM;
            case TechnologyEra.QUANTUM -> TechnologyEra.COSMIC;
            case TechnologyEra.COSMIC -> null;
        };
    }

    private String[] getRequirementsText(TechnologyEra current) {
        String[] stringArray;
        switch (current) {
            default: {
                throw new MatchException(null, null);
            }
            case STONE: {
                String[] stringArray2 = new String[3];
                stringArray2[0] = "64x Raw Copper";
                stringArray2[1] = "64x Coal";
                stringArray = stringArray2;
                stringArray2[2] = "64x Raw Iron";
                break;
            }
            case COPPER: {
                String[] stringArray3 = new String[3];
                stringArray3[0] = "64x Copper Gear";
                stringArray3[1] = "32x Iron Block";
                stringArray = stringArray3;
                stringArray3[2] = "16x Redstone Dust";
                break;
            }
            case IRON: {
                String[] stringArray4 = new String[3];
                stringArray4[0] = "64x Iron Gear";
                stringArray4[1] = "32x Shaft";
                stringArray = stringArray4;
                stringArray4[2] = "16x Water Wheel";
                break;
            }
            case MECHANICAL: {
                String[] stringArray5 = new String[3];
                stringArray5[0] = "64x Steel Ingot";
                stringArray5[1] = "64x Steam Crystal";
                stringArray = stringArray5;
                stringArray5[2] = "16x Coal Boiler";
                break;
            }
            case STEEL: {
                String[] stringArray6 = new String[3];
                stringArray6[0] = "64x Circuit Board";
                stringArray6[1] = "64x Exerion Plate";
                stringArray = stringArray6;
                stringArray6[2] = "64x Steel Cable";
                break;
            }
            case INDUSTRIAL: {
                String[] stringArray7 = new String[3];
                stringArray7[0] = "64x Logic Chip";
                stringArray7[1] = "64x Exerion Gear";
                stringArray = stringArray7;
                stringArray7[2] = "32x Electric Gen.";
                break;
            }
            case DIGITAL: {
                String[] stringArray8 = new String[2];
                stringArray8[0] = "64x Solar Panel";
                stringArray = stringArray8;
                stringArray8[1] = "64x Logic Chip";
                break;
            }
            case ADVANCED: {
                String[] stringArray9 = new String[2];
                stringArray9[0] = "64x Exerion Core";
                stringArray = stringArray9;
                stringArray9[1] = "1x Plasma Sword";
                break;
            }
            case QUANTUM: {
                String[] stringArray10 = new String[1];
                stringArray = stringArray10;
                stringArray10[0] = "64x Exerion Core";
                break;
            }
            case COSMIC: {
                String[] stringArray11 = new String[1];
                stringArray = stringArray11;
                stringArray11[0] = "Already at max era";
            }
        }
        return stringArray;
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}

