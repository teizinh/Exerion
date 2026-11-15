package net.exerion.block.entity;

import java.util.UUID;
import net.exerion.block.GearPressBlock;
import net.exerion.block.entity.ModBlockEntityTypes;
import net.exerion.item.ModItems;
import net.exerion.mechanical.IFMConsumer;
import net.exerion.mechanical.MechanicalNetwork;
import net.exerion.menu.GearPressMenu;
import net.exerion.progression.EraProgressionRegistry;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class GearPressBlockEntity
extends BlockEntity
implements MenuProvider,
IFMConsumer {
    private final SimpleContainer inventory = new SimpleContainer(3) {
        @Override
        public void setChanged() {
            super.setChanged();
            GearPressBlockEntity.this.setChanged();
        }
    };
    private int pressTime = 0;
    @Nullable
    private UUID lastPlayerUUID;
    protected final ContainerData dataAccess = new ContainerData() {
        

        public int get(int index) {
            return switch (index) {
                case 0 -> GearPressBlockEntity.this.pressTime;
                case 1 -> GearPressBlockEntity.this.getMaxPressTime();
                default -> 0;
            };
        }

        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    GearPressBlockEntity.this.pressTime = value;
                }
            }
        }

        public int getCount() {
            return 2;
        }
    };

    public GearPressBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.GEAR_PRESS_ENTITY, pos, state);
    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }

    public int getPressTime() {
        return this.pressTime;
    }

    public int getMaxPressTime() {
        ItemStack upgrade = this.inventory.getItem(2);
        if (upgrade.is(ModItems.SPEED_UPGRADE)) {
            return 40;
        }
        return 100;
    }

    public void setLastPlayerUUID(@Nullable UUID uuid) {
        this.lastPlayerUUID = uuid;
        this.setChanged();
    }

    @Override
    public int getRequiredFM() {
        return 20;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        ExerionCrashGuard.safeTick(level, pos, state, this, () -> {
            if (level.isClientSide()) {
                return;
            }
            boolean wasPressing = (Boolean)state.getValue(GearPressBlock.PRESSING);
            boolean isPressing = false;
            int availableFM = MechanicalNetwork.getAvailableFM(level, pos);
            if (availableFM < this.getRequiredFM()) {
                if (this.pressTime > 0 && level.getGameTime() % 40L == 0L) {
                    level.playSound(null, pos, SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, 0.3f, 0.5f);
                }
                this.pressTime = 0;
                if (wasPressing) {
                    level.setBlock(pos, (BlockState)state.setValue(GearPressBlock.PRESSING, false), 3);
                }
                return;
            }
            ItemStack input = this.inventory.getItem(0);
            ItemStack result = this.getResult(input);
            if (!input.isEmpty() && !result.isEmpty()) {
                Player player;
                boolean canCraft = true;
                if (this.lastPlayerUUID != null && (player = level.getPlayerByUUID(this.lastPlayerUUID)) != null && !EraProgressionRegistry.hasUnlockedRequiredEra(player, result.getItem())) {
                    canCraft = false;
                }
                if (canCraft) {
                    ItemStack outputSlot = this.inventory.getItem(1);
                    if (this.canPress(result, outputSlot)) {
                        ++this.pressTime;
                        isPressing = true;
                        this.setChanged();
                        if (this.pressTime == 25 || this.pressTime == 75) {
                            level.playSound(null, pos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.5f, 0.7f);
                        }
                        if (this.pressTime >= this.getMaxPressTime()) {
                            this.pressTime = 0;
                            input.shrink(1);
                            if (outputSlot.isEmpty()) {
                                this.inventory.setItem(1, result.copy());
                            } else {
                                outputSlot.grow(result.getCount());
                            }
                            level.playSound(null, pos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 0.8f, 1.8f);
                            this.setChanged();
                        }
                    } else {
                        this.pressTime = 0;
                    }
                } else {
                    this.pressTime = 0;
                }
            } else {
                this.pressTime = 0;
            }
            if (wasPressing != isPressing) {
                level.setBlock(pos, (BlockState)state.setValue(GearPressBlock.PRESSING, isPressing), 3);
            }
        });
    }

    private ItemStack getResult(ItemStack input) {
        if (input.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (input.is(ModItems.EXERION_INGOT)) {
            return new ItemStack((ItemLike)ModItems.EXERION_PLATE);
        }
        if (input.is(Items.IRON_INGOT)) {
            return new ItemStack((ItemLike)ModItems.IRON_GEAR);
        }
        if (input.is(Items.COPPER_INGOT)) {
            return new ItemStack((ItemLike)ModItems.COPPER_GEAR);
        }
        if (input.is(ModItems.EXERION_PLATE)) {
            return new ItemStack((ItemLike)ModItems.EXERION_GEAR);
        }
        return ItemStack.EMPTY;
    }

    private boolean canPress(ItemStack result, ItemStack outputSlot) {
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
        return Component.translatable((String)"container.exerion.gear_press");
    }

    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new GearPressMenu(containerId, playerInventory, (Container)this.inventory, this.dataAccess);
    }

    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("PressTime", this.pressTime);
        if (this.lastPlayerUUID != null) {
            output.putString("LastPlayerUUID", this.lastPlayerUUID.toString());
        }
        ValueOutput invOutput = output.child("Inventory");
        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack stack = this.inventory.getItem(i);
            if (stack.isEmpty()) continue;
            invOutput.store("Slot" + i, ItemStack.CODEC, stack);
        }
    }

    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.pressTime = input.getIntOr("PressTime", 0);
        String uuidStr = input.getStringOr("LastPlayerUUID", "");
        if (!uuidStr.isEmpty()) {
            try {
                this.lastPlayerUUID = UUID.fromString(uuidStr);
            }
            catch (IllegalArgumentException e) {
                this.lastPlayerUUID = null;
            }
        }
        ValueInput invInput = input.childOrEmpty("Inventory");
        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack stack = invInput.read("Slot" + i, ItemStack.CODEC).orElse(ItemStack.EMPTY);
            this.inventory.setItem(i, stack);
        }
    }
}

