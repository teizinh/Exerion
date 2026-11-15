package net.exerion.block.entity;

import net.exerion.block.CrusherBlock;
import net.exerion.block.ModBlocks;
import net.exerion.block.entity.ModBlockEntityTypes;
import net.exerion.item.ModItems;
import net.exerion.mechanical.IFMConsumer;
import net.exerion.mechanical.MechanicalNetwork;
import net.exerion.menu.CrusherMenu;
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

public class CrusherBlockEntity
extends BlockEntity
implements MenuProvider,
IFMConsumer {
    private final SimpleContainer inventory = new SimpleContainer(3) {
        @Override
        public void setChanged() {
            super.setChanged();
            CrusherBlockEntity.this.setChanged();
        }
    };
    private int crushTime = 0;
    protected final ContainerData dataAccess = new ContainerData() {
        

        public int get(int index) {
            return switch (index) {
                case 0 -> CrusherBlockEntity.this.crushTime;
                case 1 -> CrusherBlockEntity.this.getMaxCrushTime();
                default -> 0;
            };
        }

        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    CrusherBlockEntity.this.crushTime = value;
                }
            }
        }

        public int getCount() {
            return 2;
        }
    };

    public CrusherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CRUSHER_ENTITY, pos, state);
    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }

    public int getCrushTime() {
        return this.crushTime;
    }

    public int getMaxCrushTime() {
        ItemStack upgrade = this.inventory.getItem(2);
        if (upgrade.getItem() == ModItems.SPEED_UPGRADE) {
            return 40;
        }
        return 100;
    }

    @Override
    public int getRequiredFM() {
        return 15;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        ExerionCrashGuard.safeTick(level, pos, state, this, () -> {
            if (level.isClientSide()) {
                return;
            }
            boolean wasCrushing = (Boolean)state.getValue(CrusherBlock.CRUSHING);
            boolean isCrushing = false;
            int availableFM = MechanicalNetwork.getAvailableFM(level, pos);
            if (availableFM < this.getRequiredFM()) {
                if (this.crushTime > 0 && level.getGameTime() % 40L == 0L) {
                    level.playSound(null, pos, SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, 0.3f, 0.5f);
                }
                this.crushTime = 0;
                if (wasCrushing) {
                    level.setBlock(pos, (BlockState)state.setValue(CrusherBlock.CRUSHING, false), 3);
                }
                return;
            }
            ItemStack input = this.inventory.getItem(0);
            ItemStack result = this.getResult(input);
            if (!input.isEmpty() && !result.isEmpty()) {
                ItemStack outputSlot = this.inventory.getItem(1);
                if (this.canCrush(result, outputSlot)) {
                    ++this.crushTime;
                    isCrushing = true;
                    this.setChanged();
                    if (this.crushTime % 25 == 0) {
                        level.playSound(null, pos, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 0.6f, 0.8f);
                    }
                    if (this.crushTime >= this.getMaxCrushTime()) {
                        this.crushTime = 0;
                        input.shrink(1);
                        if (outputSlot.isEmpty()) {
                            this.inventory.setItem(1, result.copy());
                        } else {
                            outputSlot.grow(result.getCount());
                        }
                        level.playSound(null, pos, SoundEvents.GRAVEL_BREAK, SoundSource.BLOCKS, 1.0f, 0.8f);
                        level.playSound(null, pos, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.3f, 1.6f);
                        this.setChanged();
                    }
                } else {
                    this.crushTime = 0;
                }
            } else {
                this.crushTime = 0;
            }
            if (wasCrushing != isCrushing) {
                level.setBlock(pos, (BlockState)state.setValue(CrusherBlock.CRUSHING, isCrushing), 3);
            }
        });
    }

    private ItemStack getResult(ItemStack input) {
        if (input.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (input.getItem() == Items.RAW_IRON) {
            return new ItemStack((ItemLike)ModItems.IRON_DUST, 2);
        }
        if (input.getItem() == Items.RAW_COPPER) {
            return new ItemStack((ItemLike)ModItems.COPPER_DUST, 2);
        }
        if (input.getItem() == Items.IRON_INGOT) {
            return new ItemStack((ItemLike)ModItems.IRON_DUST);
        }
        if (input.getItem() == Items.COPPER_INGOT) {
            return new ItemStack((ItemLike)ModItems.COPPER_DUST);
        }
        if (input.getItem() == Items.COAL) {
            return new ItemStack((ItemLike)ModItems.COAL_DUST);
        }
        if (input.getItem() == Items.IRON_ORE || input.getItem() == Items.DEEPSLATE_IRON_ORE) {
            return new ItemStack((ItemLike)Items.RAW_IRON, 2);
        }
        if (input.getItem() == Items.COPPER_ORE || input.getItem() == Items.DEEPSLATE_COPPER_ORE) {
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
        if (outputSlot.getItem() != result.getItem()) {
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
        return Component.translatable((String)"container.exerion.crusher");
    }

    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CrusherMenu(containerId, playerInventory, (Container)this.inventory, this.dataAccess);
    }

    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
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
        this.crushTime = input.getIntOr("CrushTime", 0);
        ValueInput invInput = input.childOrEmpty("Inventory");
        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack stack = invInput.read("Slot" + i, ItemStack.CODEC).orElse(ItemStack.EMPTY);
            this.inventory.setItem(i, stack);
        }
    }
}

