package net.exerion.block.entity;

import net.exerion.block.HandCrankBlock;
import net.exerion.block.entity.ModBlockEntityTypes;
import net.exerion.mechanical.IFMSource;
import net.exerion.util.ExerionCrashGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class HandCrankBlockEntity
extends BlockEntity
implements IFMSource {
    public static final int BURST_PER_CLICK = 50;
    public static final int MAX_BUFFER = 200;
    public static final int OUTPUT_RATE = 8;
    private int fmBuffer = 0;
    private boolean isBurstMode = true;
    private long lastCrankTime = 0L;
    public float visualRotationAngle = 0.0f;
    public static final float ROTATION_SPEED = 4.0f;

    public HandCrankBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.HAND_CRANK_ENTITY, pos, state);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if (level != null && !level.isClientSide()) {
            net.exerion.mechanical.GlobalKineticNetwork.registerSource(level, this.worldPosition, this.getOutputFM(), this.isGeneratingFM());
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (this.level != null && !this.level.isClientSide()) {
            net.exerion.mechanical.GlobalKineticNetwork.removeSource(this.level, this.worldPosition);
        }
    }

    public boolean toggleMode() {
        this.isBurstMode = !this.isBurstMode;
        this.fmBuffer = 0;
        this.setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            net.exerion.mechanical.GlobalKineticNetwork.updateSource(this.level, this.worldPosition, this.getOutputFM(), this.isGeneratingFM());
        }
        return this.isBurstMode;
    }

    public boolean isBurstMode() {
        return this.isBurstMode;
    }

    public void crank(Level level) {
        if (this.isBurstMode) {
            this.fmBuffer = Math.min(this.fmBuffer + 50, 200);
        } else {
            this.lastCrankTime = level.getGameTime();
        }
        this.setChanged();
        if (level != null && !level.isClientSide()) {
            net.exerion.mechanical.GlobalKineticNetwork.updateSource(level, this.worldPosition, this.getOutputFM(), this.isGeneratingFM());
        }
    }

    public int getFmBuffer() {
        return this.fmBuffer;
    }

    public int getMaxBuffer() {
        return 200;
    }

    @Override
    public int getOutputFM() {
        if (this.isBurstMode) {
            return this.fmBuffer > 0 ? OUTPUT_RATE : 0;
        }
        if (this.level != null && this.level.getGameTime() - this.lastCrankTime <= 2L) {
            return 12;
        }
        return 0;
    }

    @Override
    public boolean isGeneratingFM() {
        if (this.isBurstMode) {
            return this.fmBuffer > 0;
        }
        return this.level != null && this.level.getGameTime() - this.lastCrankTime <= 2L;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        ExerionCrashGuard.safeTick(level, pos, state, this, () -> {
            if (level.isClientSide()) {
                if (((Boolean)state.getValue(HandCrankBlock.CRANKING)).booleanValue()) {
                    this.visualRotationAngle = (float)level.getGameTime() * 4.0f % 360.0f;
                }
                return;
            }
            if (this.isBurstMode && this.fmBuffer > 0) {
                this.fmBuffer = Math.max(0, this.fmBuffer - 1);
                this.setChanged();
            }
            boolean isGenerating = this.isBurstMode ? this.fmBuffer > 0 : level.getGameTime() - this.lastCrankTime <= 2L;
            boolean wasCranking = (Boolean)state.getValue(HandCrankBlock.CRANKING);
            if (wasCranking != isGenerating) {
                level.setBlock(pos, (BlockState)state.setValue(HandCrankBlock.CRANKING, isGenerating), 3);
                net.exerion.mechanical.GlobalKineticNetwork.updateSource(level, pos, this.getOutputFM(), isGenerating);
            }
        });
    }

    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("FmBuffer", this.fmBuffer);
        output.putBoolean("IsBurstMode", this.isBurstMode);
    }

    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.fmBuffer = input.getIntOr("FmBuffer", 0);
        this.isBurstMode = input.getBooleanOr("IsBurstMode", true);
    }
}

