package net.exerion.block.entity;

import java.util.UUID;
import net.exerion.block.ModBlocks;
import net.exerion.block.entity.ModBlockEntityTypes;
import net.exerion.item.ModItems;
import net.exerion.menu.ProgressionTerminalMenu;
import net.exerion.network.ExerionNetwork;
import net.exerion.progression.PlayerEraManager;
import net.exerion.progression.TechnologyEra;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class ProgressionTerminalBlockEntity
extends BlockEntity
implements MenuProvider {
    private final SimpleContainer inventory = new SimpleContainer(5) {
        @Override
        public void setChanged() {
            super.setChanged();
            ProgressionTerminalBlockEntity.this.setChanged();
        }
    };
    private UUID lastPlayerUUID = null;

    public ProgressionTerminalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.PROGRESSION_TERMINAL_ENTITY, pos, state);
    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }

    public void setLastPlayerUUID(UUID uuid) {
        this.lastPlayerUUID = uuid;
    }

    public UUID getLastPlayerUUID() {
        return this.lastPlayerUUID;
    }

    public boolean tryAdvanceEra(ServerPlayer player) {
        TechnologyEra currentEra = PlayerEraManager.getPlayerEra(player);
        ServerLevel level = player.level();
        return switch (currentEra) {
            case STONE -> this.tryUnlockEra(player, level, TechnologyEra.COPPER, 1.0f,
                "§c[Exerion] Not enough resources! Need: 64x Raw Copper + 64x Coal + 64x Raw Iron",
                new ItemStack(Items.RAW_COPPER, 64), new ItemStack(Items.COAL, 64), new ItemStack(Items.RAW_IRON, 64));
            case COPPER -> this.tryUnlockEra(player, level, TechnologyEra.IRON, 1.1f,
                "§c[Exerion] Not enough resources! Need: 64x Copper Gear + 32x Iron Block + 16x Redstone Dust",
                new ItemStack(ModItems.COPPER_GEAR, 64), new ItemStack(Items.IRON_BLOCK, 32), new ItemStack(Items.REDSTONE, 16));
            case IRON -> this.tryUnlockEra(player, level, TechnologyEra.MECHANICAL, 1.1f,
                "§c[Exerion] Not enough resources! Need: 64x Iron Gear + 32x Shaft + 16x Water Wheel",
                new ItemStack(ModItems.IRON_GEAR, 64), new ItemStack(ModBlocks.SHAFT, 32), new ItemStack(ModBlocks.WATER_WHEEL, 16));
            case MECHANICAL -> this.tryUnlockEra(player, level, TechnologyEra.STEEL, 1.2f,
                "§c[Exerion] Not enough resources! Need: 64x Steel Ingot + 64x Steam Crystal + 16x Coal Boiler",
                new ItemStack(ModItems.STEEL_INGOT, 64), new ItemStack(ModItems.STEAM_CRYSTAL, 64), new ItemStack(ModBlocks.COAL_BOILER, 16));
            case STEEL -> this.tryUnlockEra(player, level, TechnologyEra.INDUSTRIAL, 1.2f,
                "§c[Exerion] Not enough resources! Need: 64x Circuit Board + 64x Exerion Plate + 64x Steel Cable",
                new ItemStack(ModItems.CIRCUIT_BOARD, 64), new ItemStack(ModItems.EXERION_PLATE, 64), new ItemStack(ModBlocks.STEEL_CABLE, 64));
            case INDUSTRIAL -> this.tryUnlockEra(player, level, TechnologyEra.DIGITAL, 1.3f,
                "§c[Exerion] Not enough resources! Need: 64x Logic Chip + 64x Exerion Gear + 32x Electric Generator",
                new ItemStack(ModItems.LOGIC_CHIP, 64), new ItemStack(ModItems.EXERION_GEAR, 64), new ItemStack(ModBlocks.ELECTRIC_GENERATOR, 32));
            case DIGITAL -> this.tryUnlockEra(player, level, TechnologyEra.ADVANCED, 1.4f,
                "§c[Exerion] Not enough resources! Need: 64x Solar Panel + 64x Logic Chip",
                new ItemStack(ModBlocks.EXERION_SOLAR_PANEL, 64), new ItemStack(ModItems.LOGIC_CHIP, 64));
            case ADVANCED -> this.tryUnlockEra(player, level, TechnologyEra.QUANTUM, 1.5f,
                "§c[Exerion] Not enough resources! Need: 64x Exerion Core + 1x Plasma Sword",
                new ItemStack(ModItems.EXERION_CORE, 64), new ItemStack(ModItems.PLASMA_SWORD, 1));
            case QUANTUM -> this.tryUnlockEra(player, level, TechnologyEra.COSMIC, 1.6f,
                "§c[Exerion] Not enough resources! Need: 64x Exerion Core",
                new ItemStack(ModItems.EXERION_CORE, 64));
            default -> {
                player.sendSystemMessage(Component.literal("§6[Exerion] §fYou are already at the maximum era: §dCosmic Age§f!"));
                yield false;
            }
        };
    }

    private boolean tryUnlockEra(ServerPlayer player, ServerLevel level, TechnologyEra nextEra, float pitch, String missingMsg, ItemStack... requirements) {
        if (this.hasItems(requirements)) {
            this.consumeItems(requirements);
            PlayerEraManager.setPlayerEra(player, nextEra);
            level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, pitch);
            return true;
        }
        player.sendSystemMessage(Component.literal(missingMsg));
        return false;
    }

    private boolean hasItems(ItemStack ... required) {
        for (ItemStack req : required) {
            int totalFound = 0;
            for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
                ItemStack slot = this.inventory.getItem(i);
                if (!slot.is(req.getItem())) continue;
                totalFound += slot.getCount();
            }
            if (totalFound >= req.getCount()) continue;
            return false;
        }
        return true;
    }

    private void consumeItems(ItemStack ... toConsume) {
        for (ItemStack req : toConsume) {
            int remaining = req.getCount();
            for (int i = 0; i < this.inventory.getContainerSize() && remaining > 0; ++i) {
                ItemStack slot = this.inventory.getItem(i);
                if (!slot.is(req.getItem())) continue;
                int take = Math.min(remaining, slot.getCount());
                slot.shrink(take);
                remaining -= take;
                if (!slot.isEmpty()) continue;
                this.inventory.setItem(i, ItemStack.EMPTY);
            }
        }
        this.setChanged();
    }

    public void dropContents(Level level, BlockPos pos) {
        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack stack = this.inventory.getItem(i);
            if (stack.isEmpty()) continue;
            ItemEntity entity = new ItemEntity(level, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, stack);
            level.addFreshEntity((Entity)entity);
        }
        this.inventory.clearContent();
    }

    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (this.level != null) {
            this.dropContents(this.level, pos);
        }
    }

    public Component getDisplayName() {
        return Component.translatable((String)"container.exerion.progression_terminal");
    }

    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ProgressionTerminalMenu(containerId, playerInventory, (Container)this.inventory, this);
    }

    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (this.lastPlayerUUID != null) {
            output.putString("LastPlayer", this.lastPlayerUUID.toString());
        }
        ValueOutput invOutput = output.child("Inventory");
        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            if (this.inventory.getItem(i).isEmpty()) continue;
            invOutput.store("Slot" + i, ItemStack.CODEC, this.inventory.getItem(i));
        }
    }

    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.getString("LastPlayer").ifPresent(s -> {
            try {
                this.lastPlayerUUID = UUID.fromString(s);
            }
            catch (IllegalArgumentException e) {
                this.lastPlayerUUID = null;
            }
        });
        ValueInput invInput = input.childOrEmpty("Inventory");
        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack stack = invInput.read("Slot" + i, ItemStack.CODEC).orElse(ItemStack.EMPTY);
            this.inventory.setItem(i, stack);
        }
    }
}

