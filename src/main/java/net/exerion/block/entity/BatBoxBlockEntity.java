package net.exerion.block.entity;

import net.exerion.block.BatBoxBlock;
import net.exerion.block.entity.ModBlockEntityTypes;
import net.exerion.energy.IEnergyStorage;
import net.exerion.item.ModItems;
import net.exerion.menu.BatBoxMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class BatBoxBlockEntity
extends BlockEntity
implements MenuProvider,
IEnergyStorage {
    private final SimpleContainer inventory = new SimpleContainer(2) {
        @Override
        public void setChanged() {
            super.setChanged();
            BatBoxBlockEntity.this.setChanged();
        }
    };
    private int energy = 0;
    private final int maxEnergy = 10000;
    private final int transferRate = 32;
    protected final ContainerData dataAccess = new ContainerData() {
        

        public int get(int index) {
            return switch (index) {
                case 0 -> BatBoxBlockEntity.this.energy;
                case 1 -> BatBoxBlockEntity.this.maxEnergy;
                default -> 0;
            };
        }

        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    BatBoxBlockEntity.this.energy = value;
                }
            }
        }

        public int getCount() {
            return 2;
        }
    };

    public BatBoxBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.BATBOX_ENTITY, pos, state);
    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }

    @Override
    public int getEnergy() {
        return this.energy;
    }

    @Override
    public int getMaxEnergy() {
        return 10000;
    }

    @Override
    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(10000, energy));
        this.setChanged();
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        int toPush;
        IEnergyStorage storage;
        int received;
        Direction outputDir;
        BlockEntity target;
        int damage;
        if (level.isClientSide()) {
            return;
        }
        ItemStack dischargeSlot = this.inventory.getItem(1);
        if (this.energy <= 9750 && !dischargeSlot.isEmpty() && dischargeSlot.is(ModItems.STEAM_CRYSTAL)) {
            dischargeSlot.shrink(1);
            this.energy += 250;
            this.setChanged();
        }
        ItemStack chargeSlot = this.inventory.getItem(0);
        if (this.energy > 0 && !chargeSlot.isEmpty() && (chargeSlot.is(ModItems.ERA_SCANNER) || chargeSlot.is(ModItems.SWISS_ARMY_KNIFE) || chargeSlot.is(ModItems.STEAM_BOOTS) || chargeSlot.is(ModItems.ELECTRIC_PLATE) || chargeSlot.is(ModItems.LOGIC_HELMET) || chargeSlot.is(ModItems.ELECTRIC_MINING_DRILL) || chargeSlot.is(ModItems.PLASMA_SWORD)) && (damage = chargeSlot.getDamageValue()) > 0) {
            int chargeAmount = Math.min(damage, 32);
            chargeSlot.setDamageValue(damage - chargeAmount);
            this.energy -= chargeAmount;
            this.setChanged();
        }
        if (this.energy > 0 && (target = level.getBlockEntity(pos.relative(outputDir = (Direction)state.getValue(BatBoxBlock.FACING)))) instanceof IEnergyStorage && (received = (storage = (IEnergyStorage)target).receiveEnergy(toPush = Math.min(32, this.energy), false)) > 0) {
            this.energy -= received;
            this.setChanged();
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
        return Component.translatable((String)"container.exerion.batbox");
    }

    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new BatBoxMenu(containerId, playerInventory, (Container)this.inventory, this.dataAccess);
    }

    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("Energy", this.energy);
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
        ValueInput invInput = input.childOrEmpty("Inventory");
        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack stack = invInput.read("Slot" + i, ItemStack.CODEC).orElse(ItemStack.EMPTY);
            this.inventory.setItem(i, stack);
        }
    }
}

