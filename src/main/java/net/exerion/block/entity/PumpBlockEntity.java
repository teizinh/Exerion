package net.exerion.block.entity;

import net.exerion.block.entity.FrequencyEnderTankBlockEntity;
import net.exerion.block.entity.ModBlockEntityTypes;
import net.exerion.fluid.GlobalFluidNetwork;
import net.exerion.mechanical.IFMConsumer;
import net.exerion.mechanical.MechanicalNetwork;
import net.exerion.util.ExerionCrashGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class PumpBlockEntity
extends BlockEntity
implements IFMConsumer {
    private static final Direction[] DIRECTIONS = Direction.values();
    private int pumpCooldown = 0;
    private int pumpDepth = 0;
    private boolean isPumping = false;

    public PumpBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.PUMP_ENTITY, pos, state);
    }

    @Override
    public int getRequiredFM() {
        return 8;
    }

    public int getPumpDepth() {
        return this.pumpDepth;
    }

    public boolean isPumping() {
        return this.isPumping;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        ExerionCrashGuard.safeTick(level, pos, state, this, () -> {
            BlockPos checkPos;
            BlockState checkState;
            if (level.isClientSide()) {
                return;
            }
            if (this.pumpCooldown > 0) {
                --this.pumpCooldown;
                if (this.pumpCooldown == 0 && this.isPumping) {
                    this.isPumping = false;
                    this.sync();
                }
                return;
            }
            if (MechanicalNetwork.getAvailableFM(level, pos) < this.getRequiredFM()) {
                if (this.isPumping) {
                    this.isPumping = false;
                    this.sync();
                }
                return;
            }
            int foundDepth = 0;
            BlockPos targetLava = null;
            int i = 1;
            while (i <= 64 && ((checkState = level.getBlockState(checkPos = pos.below(i))).isAir() || checkState.is(Blocks.LAVA))) {
                foundDepth = i++;
                if (!checkState.is(Blocks.LAVA)) continue;
                targetLava = checkPos;
                break;
            }
            boolean stateChanged = false;
            if (this.pumpDepth != foundDepth) {
                this.pumpDepth = foundDepth;
                stateChanged = true;
            }
            if (targetLava != null) {
                if (MechanicalNetwork.getAvailableFM(level, pos) < this.getRequiredFM()) {
                    if (this.isPumping) {
                        this.isPumping = false;
                        this.sync();
                    }
                    return;
                }
                boolean successfullyPumped = false;
                for (Direction dir : DIRECTIONS) {
                    FrequencyEnderTankBlockEntity tankBE;
                    int injected;
                    BlockEntity neighbor = level.getBlockEntity(pos.relative(dir));
                    if (!(neighbor instanceof FrequencyEnderTankBlockEntity) || (injected = GlobalFluidNetwork.injectLava((tankBE = (FrequencyEnderTankBlockEntity)neighbor).getFrequency(), 1000)) <= 0) continue;
                    level.setBlock(targetLava, Blocks.AIR.defaultBlockState(), 3);
                    this.pumpCooldown = 20;
                    successfullyPumped = true;
                    break;
                }
                if (successfullyPumped && !this.isPumping) {
                    this.isPumping = true;
                    stateChanged = true;
                } else if (!successfullyPumped && this.isPumping) {
                    this.isPumping = false;
                    stateChanged = true;
                }
            } else if (this.isPumping) {
                this.isPumping = false;
                stateChanged = true;
            }
            if (stateChanged) {
                this.sync();
            }
        });
    }

    private void sync() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }

    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("PumpDepth", this.pumpDepth);
        output.putBoolean("IsPumping", this.isPumping);
    }

    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.pumpDepth = input.getIntOr("PumpDepth", 0);
        this.isPumping = input.getBooleanOr("IsPumping", false);
    }
}

