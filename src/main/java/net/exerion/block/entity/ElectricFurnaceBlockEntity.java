package net.exerion.block.entity;

import net.exerion.block.entity.ModBlockEntityTypes;
import net.exerion.energy.IEnergyStorage;
import net.exerion.item.ModItems;
import net.exerion.menu.ElectricFurnaceMenu;
import net.exerion.util.ExerionCrashGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class ElectricFurnaceBlockEntity
extends BlockEntity
implements MenuProvider,
IEnergyStorage {
    private final SimpleContainer inventory = new SimpleContainer(2) {
        @Override
        public void setChanged() {
            super.setChanged();
            ElectricFurnaceBlockEntity.this.setChanged();
        }
    };
    private int energy = 0;
    private final int maxEnergy = 4000;
    private int progress = 0;
    private final int maxProgress = 100;
    protected final ContainerData dataAccess = new ContainerData() {
        

        public int get(int index) {
            return switch (index) {
                case 0 -> ElectricFurnaceBlockEntity.this.energy;
                case 1 -> ElectricFurnaceBlockEntity.this.maxEnergy;
                case 2 -> ElectricFurnaceBlockEntity.this.progress;
                case 3 -> ElectricFurnaceBlockEntity.this.maxProgress;
                default -> 0;
            };
        }

        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    ElectricFurnaceBlockEntity.this.energy = value;
                    break;
                }
                case 2: {
                    ElectricFurnaceBlockEntity.this.progress = value;
                }
            }
        }

        public int getCount() {
            return 4;
        }
    };

    public ElectricFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.ELECTRIC_FURNACE_ENTITY, pos, state);
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if (level != null && !level.isClientSide()) {
            net.exerion.energy.GlobalEnergyNetwork.registerDevice(level, this.worldPosition, false, this.getMaxEnergy() - this.getEnergy(), 50);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (this.level != null && !this.level.isClientSide()) {
            net.exerion.energy.GlobalEnergyNetwork.removeDevice(this.level, this.worldPosition);
        }
    }

    @Override
    public int getEnergy() {
        return this.energy;
    }

    @Override
    public int getMaxEnergy() {
        return 4000;
    }

    @Override
    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(4000, energy));
        this.setChanged();
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return 100;
    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        ExerionCrashGuard.safeTick(level, pos, state, this, () -> {
            boolean isWorking;
            if (level.isClientSide()) {
                return;
            }
            ItemStack input = this.inventory.getItem(0);
            ItemStack output = this.inventory.getItem(1);
            if (!input.isEmpty() && this.canProcess(input, output, level)) {
                if (this.energy >= 3) {
                    this.energy -= 3;
                    ++this.progress;
                    this.setChanged();
                    if (this.progress >= 100) {
                        this.processRecipe(input, level);
                        this.progress = 0;
                        this.setChanged();
                    }
                }
            } else if (this.progress > 0) {
                this.progress = Math.max(0, this.progress - 2);
                this.setChanged();
            }
            boolean bl = isWorking = this.progress > 0;
            if (state.hasProperty(BlockStateProperties.LIT) && (Boolean)state.getValue(BlockStateProperties.LIT) != isWorking) {
                level.setBlock(pos, (BlockState)state.setValue(BlockStateProperties.LIT, isWorking), 3);
            }
        });
    }

    private boolean canProcess(ItemStack input, ItemStack outputSlot, Level level) {
        if (input.isEmpty()) {
            return false;
        }
        ItemStack result = this.getRecipeResult(input, level);
        if (result.isEmpty()) {
            return false;
        }
        if (outputSlot.isEmpty()) {
            return true;
        }
        return outputSlot.getItem() == result.getItem() && outputSlot.getCount() + result.getCount() <= outputSlot.getMaxStackSize();
    }

    private ItemStack getRecipeResult(ItemStack input, Level level) {
        if (input.getItem() == ModItems.IRON_DUST) {
            return new ItemStack((ItemLike)Items.IRON_INGOT);
        }
        if (input.getItem() == ModItems.COPPER_DUST) {
            return new ItemStack((ItemLike)Items.COPPER_INGOT);
        }
        if (input.getItem() == ModItems.RAW_EXERION) {
            return new ItemStack((ItemLike)ModItems.EXERION_INGOT);
        }
        if (input.getItem() == Items.RAW_IRON) {
            return new ItemStack((ItemLike)Items.IRON_INGOT);
        }
        if (input.getItem() == Items.RAW_COPPER) {
            return new ItemStack((ItemLike)Items.COPPER_INGOT);
        }
        return ItemStack.EMPTY;
    }

    private void processRecipe(ItemStack input, Level level) {
        ItemStack result = this.getRecipeResult(input, level);
        if (result.isEmpty()) {
            return;
        }
        input.shrink(1);
        ItemStack outputSlot = this.inventory.getItem(1);
        if (outputSlot.isEmpty()) {
            this.inventory.setItem(1, result.copy());
        } else {
            outputSlot.grow(result.getCount());
        }
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
        return Component.translatable((String)"container.exerion.electric_furnace");
    }

    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ElectricFurnaceMenu(containerId, playerInventory, (Container)this.inventory, this.dataAccess);
    }

    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("Energy", this.energy);
        output.putInt("Progress", this.progress);
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
        this.progress = input.getIntOr("Progress", 0);
        ValueInput invInput = input.childOrEmpty("Inventory");
        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack stack = invInput.read("Slot" + i, ItemStack.CODEC).orElse(ItemStack.EMPTY);
            this.inventory.setItem(i, stack);
        }
    }
}

