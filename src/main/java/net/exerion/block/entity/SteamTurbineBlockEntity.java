package net.exerion.block.entity;

import net.exerion.block.SteamTurbineBlock;
import net.exerion.block.entity.ModBlockEntityTypes;
import net.exerion.energy.IEnergyStorage;
import net.exerion.item.ModItems;
import net.exerion.menu.SteamTurbineMenu;
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
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class SteamTurbineBlockEntity
extends BlockEntity
implements MenuProvider,
IEnergyStorage {
    private static final Direction[] DIRECTIONS = Direction.values();
    private final SimpleContainer inventory = new SimpleContainer(2) {
        @Override
        public void setChanged() {
            super.setChanged();
            SteamTurbineBlockEntity.this.setChanged();
        }
    };
    private int energy = 0;
    private final int maxEnergy = 1000;
    protected final ContainerData dataAccess = new ContainerData() {
        

        public int get(int index) {
            return switch (index) {
                case 0 -> SteamTurbineBlockEntity.this.energy;
                case 1 -> SteamTurbineBlockEntity.this.maxEnergy;
                default -> 0;
            };
        }

        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    SteamTurbineBlockEntity.this.energy = value;
                }
            }
        }

        public int getCount() {
            return 2;
        }
    };

    public SteamTurbineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.STEAM_TURBINE_ENTITY, pos, state);
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
        return 1000;
    }

    @Override
    public void setEnergy(int energy) {
        this.energy = energy;
        this.setChanged();
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        int damage;
        if (level.isClientSide()) {
            return;
        }
        boolean wasTurning = (Boolean)state.getValue(SteamTurbineBlock.TURNING);
        boolean isTurning = false;
        ItemStack crystalSlot = this.inventory.getItem(0);
        if (this.energy <= 750 && !crystalSlot.isEmpty() && crystalSlot.is(ModItems.STEAM_CRYSTAL)) {
            crystalSlot.shrink(1);
            this.energy += 250;
            this.setChanged();
        }
        ItemStack chargeSlot = this.inventory.getItem(1);
        if (this.energy > 0 && !chargeSlot.isEmpty() && (chargeSlot.is(ModItems.ERA_SCANNER) || chargeSlot.is(ModItems.SWISS_ARMY_KNIFE) || chargeSlot.is(ModItems.STEAM_BOOTS) || chargeSlot.is(ModItems.ELECTRIC_PLATE) || chargeSlot.is(ModItems.LOGIC_HELMET) || chargeSlot.is(ModItems.ELECTRIC_MINING_DRILL) || chargeSlot.is(ModItems.PLASMA_SWORD)) && (damage = chargeSlot.getDamageValue()) > 0) {
            chargeSlot.setDamageValue(damage - 1);
            --this.energy;
            isTurning = true;
            this.setChanged();
        }
        if (this.energy > 0) {
            this.pushEnergy(level, pos);
            isTurning = true;
        }
        if (wasTurning != isTurning) {
            level.setBlock(pos, (BlockState)state.setValue(SteamTurbineBlock.TURNING, isTurning), 3);
        }
    }

    private void pushEnergy(Level level, BlockPos pos) {
        int pushAmount = Math.min(this.energy, 32);
        for (Direction dir : DIRECTIONS) {
            if (pushAmount <= 0) break;
            BlockEntity target = level.getBlockEntity(pos.relative(dir));
            if (!(target instanceof IEnergyStorage targetStorage) || target instanceof SteamTurbineBlockEntity) {
                continue;
            }
            int received = targetStorage.receiveEnergy(pushAmount, false);
            if (received > 0) {
                this.energy -= received;
                pushAmount -= received;
                this.setChanged();
            }
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
        return Component.translatable((String)"container.exerion.steam_turbine");
    }

    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new SteamTurbineMenu(containerId, playerInventory, (Container)this.inventory, this.dataAccess);
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

