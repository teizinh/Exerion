package net.exerion.block.entity;

import net.exerion.block.SolarPanelBlock;
import net.exerion.block.entity.ModBlockEntityTypes;
import net.exerion.energy.IEnergyStorage;
import net.exerion.menu.SolarPanelMenu;
import net.exerion.util.ExerionCrashGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class SolarPanelBlockEntity
extends BlockEntity
implements MenuProvider,
IEnergyStorage {
    private int energy = 0;
    private boolean isGenerating = false;
    protected final ContainerData dataAccess = new ContainerData() {
        

        public int get(int index) {
            return switch (index) {
                case 0 -> SolarPanelBlockEntity.this.energy;
                case 1 -> SolarPanelBlockEntity.this.getMaxEnergy();
                case 2 -> {
                    if (SolarPanelBlockEntity.this.isGenerating) {
                        yield 1;
                    }
                    yield 0;
                }
                case 3 -> SolarPanelBlockEntity.this.getGenerationRate();
                default -> 0;
            };
        }

        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    SolarPanelBlockEntity.this.energy = value;
                    break;
                }
                case 2: {
                    SolarPanelBlockEntity.this.isGenerating = value == 1;
                }
            }
        }

        public int getCount() {
            return 4;
        }
    };

    public SolarPanelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.SOLAR_PANEL_ENTITY, pos, state);
    }

    @Override
    public boolean canReceive() {
        return false;
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if (level != null && !level.isClientSide()) {
            net.exerion.energy.GlobalEnergyNetwork.registerDevice(level, this.worldPosition, true, this.getEnergy(), this.getTransferRate());
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (this.level != null && !this.level.isClientSide()) {
            net.exerion.energy.GlobalEnergyNetwork.removeDevice(this.level, this.worldPosition);
        }
    }

    public int getCapacity() {
        BlockState state;
        Block block;
        if (this.level != null && (block = (state = this.getBlockState()).getBlock()) instanceof SolarPanelBlock) {
            SolarPanelBlock block2 = (SolarPanelBlock)block;
            return block2.getMaxCapacity();
        }
        return 1000;
    }

    public int getTransferRate() {
        BlockState state;
        Block block;
        if (this.level != null && (block = (state = this.getBlockState()).getBlock()) instanceof SolarPanelBlock) {
            SolarPanelBlock block2 = (SolarPanelBlock)block;
            return block2.getMaxTransfer();
        }
        return 100;
    }

    public int getGenerationRate() {
        BlockState state;
        Block block;
        if (this.level != null && (block = (state = this.getBlockState()).getBlock()) instanceof SolarPanelBlock) {
            SolarPanelBlock block2 = (SolarPanelBlock)block;
            return block2.getGenerationRate();
        }
        return 2;
    }

    @Override
    public int getEnergy() {
        return this.energy;
    }

    @Override
    public int getMaxEnergy() {
        return this.getCapacity();
    }

    @Override
    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(this.getCapacity(), energy));
        this.setChanged();
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        ExerionCrashGuard.safeTick(level, pos, state, this, () -> {
            boolean canGenerate;
            if (level.isClientSide()) {
                return;
            }
            boolean wasActive = (Boolean)state.getValue(SolarPanelBlock.ACTIVE);
            boolean hasSky = level.canSeeSky(pos.above());
            boolean isDay = level.getOverworldClockTime() % 24000L < 12000L;
            this.isGenerating = canGenerate = isDay && hasSky;
            if (canGenerate) {
                int gen = this.getGenerationRate();
                if (this.energy < this.getCapacity()) {
                    this.energy = Math.min(this.getCapacity(), this.energy + gen);
                    this.setChanged();
                }
            }
            if (wasActive != canGenerate) {
                level.setBlock(pos, (BlockState)state.setValue(SolarPanelBlock.ACTIVE, canGenerate), 3);
            }
            if (this.energy > 0) {
                int toPush;
                IEnergyStorage storage;
                int received;
                int rate = this.getTransferRate();
                BlockEntity below = level.getBlockEntity(pos.below());
                if (below instanceof IEnergyStorage && (received = (storage = (IEnergyStorage)below).receiveEnergy(toPush = Math.min(rate, this.energy), false)) > 0) {
                    this.energy -= received;
                    this.setChanged();
                }
                if (this.energy > 0) {
                    for (Direction dir : Direction.Plane.HORIZONTAL) {
                        int toPush2;
                        IEnergyStorage storage2;
                        int received2;
                        if (this.energy <= 0) break;
                        BlockEntity side = level.getBlockEntity(pos.relative(dir));
                        if (!(side instanceof IEnergyStorage) || (received2 = (storage2 = (IEnergyStorage)side).receiveEnergy(toPush2 = Math.min(rate, this.energy), false)) <= 0) continue;
                        this.energy -= received2;
                        this.setChanged();
                    }
                }
            }
        });
    }

    public Component getDisplayName() {
        return Component.translatable((String)"container.exerion.solar_panel");
    }

    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new SolarPanelMenu(containerId, playerInventory, this, this.dataAccess);
    }

    public boolean isGenerating() {
        return this.isGenerating;
    }

    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("Energy", this.energy);
        output.putBoolean("IsGenerating", this.isGenerating);
    }

    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.energy = input.getIntOr("Energy", 0);
        this.isGenerating = input.getBooleanOr("IsGenerating", false);
    }
}

