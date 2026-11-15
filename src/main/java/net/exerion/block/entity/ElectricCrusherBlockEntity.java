package net.exerion.block.entity;

import net.exerion.block.ElectricCrusherBlock;
import net.exerion.block.ModBlocks;
import net.exerion.block.entity.ModBlockEntityTypes;
import net.exerion.energy.IEnergyStorage;
import net.exerion.item.ModItems;
import net.exerion.menu.ElectricCrusherMenu;
import net.exerion.util.ExerionCrashGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class ElectricCrusherBlockEntity
extends BlockEntity
implements MenuProvider,
IEnergyStorage {
    private final SimpleContainer inventory = new SimpleContainer(3) {
        @Override
        public void setChanged() {
            super.setChanged();
            ElectricCrusherBlockEntity.this.setChanged();
        }
    };
    private int energy = 0;
    private final int maxEnergy = 5000;
    private int crushTime = 0;
    protected final ContainerData dataAccess = new ContainerData() {
        

        public int get(int index) {
            return switch (index) {
                case 0 -> ElectricCrusherBlockEntity.this.crushTime;
                case 1 -> ElectricCrusherBlockEntity.this.getMaxCrushTime();
                case 2 -> ElectricCrusherBlockEntity.this.energy;
                case 3 -> ElectricCrusherBlockEntity.this.maxEnergy;
                default -> 0;
            };
        }

        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    ElectricCrusherBlockEntity.this.crushTime = value;
                    break;
                }
                case 2: {
                    ElectricCrusherBlockEntity.this.energy = value;
                }
            }
        }

        public int getCount() {
            return 4;
        }
    };

    public ElectricCrusherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.ELECTRIC_CRUSHER_ENTITY, pos, state);
    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }

    public int getCrushTime() {
        return this.crushTime;
    }

    public int getMaxCrushTime() {
        ItemStack upgrade = this.inventory.getItem(2);
        if (upgrade.is(ModItems.SPEED_UPGRADE)) {
            return 5;
        }
        return 20;
    }

    public int getEnergyPerTick() {
        ItemStack upgrade = this.inventory.getItem(2);
        if (upgrade.is(ModItems.EFFICIENCY_UPGRADE)) {
            return 8;
        }
        return 20;
    }

    @Override
    public int getEnergy() {
        return this.energy;
    }

    @Override
    public int getMaxEnergy() {
        return 5000;
    }

    @Override
    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(5000, energy));
        this.setChanged();
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        ExerionCrashGuard.safeTick(level, pos, state, this, () -> {
            if (level.isClientSide()) {
                return;
            }
            boolean wasCrushing = (Boolean)state.getValue(ElectricCrusherBlock.CRUSHING);
            boolean isCrushing = false;
            ItemStack input = this.inventory.getItem(0);
            ItemStack result = this.getResult(input);
            int usage = this.getEnergyPerTick();
            if (!input.isEmpty() && !result.isEmpty() && this.energy >= usage) {
                ItemStack outputSlot = this.inventory.getItem(1);
                if (this.canCrush(result, outputSlot)) {
                    this.energy -= usage;
                    ++this.crushTime;
                    isCrushing = true;
                    this.setChanged();
                    if (this.crushTime % 5 == 0) {
                        level.playSound(null, pos, SoundEvents.COPPER_BULB_TURN_ON, SoundSource.BLOCKS, 0.4f, 1.4f);
                    }
                    if (this.crushTime >= this.getMaxCrushTime()) {
                        this.crushTime = 0;
                        input.shrink(1);
                        if (outputSlot.isEmpty()) {
                            this.inventory.setItem(1, result.copy());
                        } else {
                            outputSlot.grow(result.getCount());
                        }
                        level.playSound(null, pos, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.2f, 1.8f);
                        this.setChanged();
                    }
                } else {
                    this.crushTime = 0;
                }
            } else {
                this.crushTime = 0;
            }
            if (wasCrushing != isCrushing) {
                level.setBlock(pos, (BlockState)state.setValue(ElectricCrusherBlock.CRUSHING, isCrushing), 3);
            }
        });
    }

    private ItemStack getResult(ItemStack input) {
        if (input.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (input.is(Items.RAW_IRON)) {
            return new ItemStack((ItemLike)ModItems.IRON_DUST, 2);
        }
        if (input.is(Items.RAW_COPPER)) {
            return new ItemStack((ItemLike)ModItems.COPPER_DUST, 2);
        }
        if (input.is(Items.IRON_INGOT)) {
            return new ItemStack((ItemLike)ModItems.IRON_DUST);
        }
        if (input.is(Items.COPPER_INGOT)) {
            return new ItemStack((ItemLike)ModItems.COPPER_DUST);
        }
        if (input.is(Items.COAL)) {
            return new ItemStack((ItemLike)ModItems.COAL_DUST);
        }
        if (input.is(Items.IRON_ORE) || input.is(Items.DEEPSLATE_IRON_ORE)) {
            return new ItemStack((ItemLike)Items.RAW_IRON, 2);
        }
        if (input.is(Items.COPPER_ORE) || input.is(Items.DEEPSLATE_COPPER_ORE)) {
            return new ItemStack((ItemLike)Items.RAW_COPPER, 2);
        }
        if (input.getItem() == Item.byBlock((Block)ModBlocks.EXERION_ORE) || input.getItem() == Item.byBlock((Block)ModBlocks.DEEPSLATE_EXERION_ORE)) {
            return new ItemStack((ItemLike)ModItems.RAW_EXERION, 2);
        }
        return ItemStack.EMPTY;
    }

    private boolean canCrush(ItemStack result, ItemStack outputSlot) {
        if (outputSlot.isEmpty()) {
            return true;
        }
        if (!outputSlot.is(result.getItem())) {
            return false;
        }
        return outputSlot.getCount() + result.getCount() <= outputSlot.getMaxStackSize();
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
        return Component.translatable((String)"container.exerion.electric_crusher");
    }

    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ElectricCrusherMenu(containerId, playerInventory, (Container)this.inventory, this.dataAccess);
    }

    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("Energy", this.energy);
        output.putInt("CrushTime", this.crushTime);
        ValueOutput invOutput = output.child("Inventory");
        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack stack = this.inventory.getItem(i);
            if (stack.isEmpty()) continue;
            invOutput.store("Slot" + i, ItemStack.CODEC, stack);
        }
    }

    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.energy = input.getIntOr("Energy", 0);
        this.crushTime = input.getIntOr("CrushTime", 0);
        ValueInput invInput = input.childOrEmpty("Inventory");
        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack stack = invInput.read("Slot" + i, ItemStack.CODEC).orElse(ItemStack.EMPTY);
            this.inventory.setItem(i, stack);
        }
    }
}

