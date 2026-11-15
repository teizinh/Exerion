package net.exerion.block.entity;

import net.exerion.block.entity.ModBlockEntityTypes;
import net.exerion.energy.IEnergyStorage;
import net.exerion.item.ModItems;
import net.exerion.menu.AlloySmelterMenu;
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

public class AlloySmelterBlockEntity
extends BlockEntity
implements MenuProvider,
IEnergyStorage {
    private final SimpleContainer inventory = new SimpleContainer(3) {
        @Override
        public void setChanged() {
            super.setChanged();
            AlloySmelterBlockEntity.this.setChanged();
        }
    };
    private int energy = 0;
    private final int maxEnergy = 4000;
    private int progress = 0;
    private final int maxProgress = 200;
    protected final ContainerData dataAccess = new ContainerData() {
        

        public int get(int index) {
            return switch (index) {
                case 0 -> AlloySmelterBlockEntity.this.energy;
                case 1 -> AlloySmelterBlockEntity.this.maxEnergy;
                case 2 -> AlloySmelterBlockEntity.this.progress;
                case 3 -> AlloySmelterBlockEntity.this.maxProgress;
                default -> 0;
            };
        }

        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    AlloySmelterBlockEntity.this.energy = value;
                    break;
                }
                case 2: {
                    AlloySmelterBlockEntity.this.progress = value;
                }
            }
        }

        public int getCount() {
            return 4;
        }
    };

    public AlloySmelterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.ALLOY_SMELTER_ENTITY, pos, state);
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
        return 200;
    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        ExerionCrashGuard.safeTick(level, pos, state, this, () -> {
            boolean isWorking;
            ItemStack output;
            ItemStack inputB;
            if (level.isClientSide()) {
                return;
            }
            ItemStack inputA = this.inventory.getItem(0);
            if (this.canProcess(inputA, inputB = this.inventory.getItem(1), output = this.inventory.getItem(2))) {
                if (this.energy >= 2) {
                    this.energy -= 2;
                    ++this.progress;
                    this.setChanged();
                    if (this.progress >= 200) {
                        this.processRecipe(inputA, inputB);
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

    private boolean canProcess(ItemStack inputA, ItemStack inputB, ItemStack output) {
        if (inputA.isEmpty() || inputB.isEmpty()) {
            return false;
        }
        ItemStack recipeResult = this.getRecipeResult(inputA, inputB);
        if (recipeResult.isEmpty()) {
            return false;
        }
        if (output.isEmpty()) {
            return true;
        }
        return output.getItem() == recipeResult.getItem() && output.getCount() + recipeResult.getCount() <= output.getMaxStackSize();
    }

    private boolean matchRecipe(ItemStack inputA, ItemStack inputB, ItemStack itemA, ItemStack itemB) {
        return inputA.getItem() == itemA.getItem() && inputB.getItem() == itemB.getItem() || inputA.getItem() == itemB.getItem() && inputB.getItem() == itemA.getItem();
    }

    private ItemStack getRecipeResult(ItemStack inputA, ItemStack inputB) {
        if (this.matchRecipe(inputA, inputB, new ItemStack((ItemLike)Items.IRON_INGOT), new ItemStack((ItemLike)ModItems.COAL_DUST))) {
            return new ItemStack((ItemLike)ModItems.STEEL_INGOT, 1);
        }
        if (this.matchRecipe(inputA, inputB, new ItemStack((ItemLike)Items.COPPER_INGOT), new ItemStack((ItemLike)Items.IRON_INGOT))) {
            return new ItemStack((ItemLike)ModItems.STEEL_INGOT, 2);
        }
        if (this.matchRecipe(inputA, inputB, new ItemStack((ItemLike)Items.COPPER_INGOT), new ItemStack((ItemLike)Items.GOLD_INGOT))) {
            return new ItemStack((ItemLike)ModItems.CIRCUIT_BOARD, 2);
        }
        if (this.matchRecipe(inputA, inputB, new ItemStack((ItemLike)ModItems.EXERION_INGOT), new ItemStack((ItemLike)Items.DIAMOND))) {
            return new ItemStack((ItemLike)ModItems.EXERION_CORE, 1);
        }
        return ItemStack.EMPTY;
    }

    private void processRecipe(ItemStack inputA, ItemStack inputB) {
        ItemStack result = this.getRecipeResult(inputA, inputB);
        if (result.isEmpty()) {
            return;
        }
        inputA.shrink(1);
        inputB.shrink(1);
        ItemStack outputSlot = this.inventory.getItem(2);
        if (outputSlot.isEmpty()) {
            this.inventory.setItem(2, result.copy());
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
        return Component.translatable((String)"container.exerion.alloy_smelter");
    }

    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new AlloySmelterMenu(containerId, playerInventory, (Container)this.inventory, this.dataAccess);
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

