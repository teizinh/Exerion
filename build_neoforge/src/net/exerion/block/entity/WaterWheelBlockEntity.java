package net.exerion.block.entity;

import net.exerion.block.WaterWheelBlock;
import net.exerion.block.entity.ModBlockEntityTypes;
import net.exerion.mechanical.IFMSource;
import net.exerion.util.ExerionCrashGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class WaterWheelBlockEntity
extends BlockEntity
implements IFMSource {
    private static final Direction[] DIRECTIONS = Direction.values();
    public static final int FM_OUTPUT = 25;
    private boolean hasWater = false;
    private int checkTimer = CHECK_INTERVAL;
    private static final int CHECK_INTERVAL = 10;
    public float visualRotationAngle = 0.0f;
    public static final float ROTATION_SPEED = 4.0f;

    public WaterWheelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.WATER_WHEEL_ENTITY, pos, state);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if (level != null && !level.isClientSide()) {
            this.checkTimer = CHECK_INTERVAL;
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

    public boolean hasWater() {
        return this.hasWater;
    }

    @Override
    public int getOutputFM() {
        return this.hasWater ? FM_OUTPUT : 0;
    }

    @Override
    public boolean isGeneratingFM() {
        return this.hasWater;
    }

    private boolean checkWaterAround(Level level, BlockPos pos) {
        for (Direction dir : DIRECTIONS) {
            if (level.getFluidState(pos.relative(dir)).is(FluidTags.WATER)) {
                return true;
            }
        }
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (level.getFluidState(pos.offset(dx, -1, dz)).is(FluidTags.WATER)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void triggerWaterCheck(Level level, BlockPos pos, BlockState state) {
        if (level == null || level.isClientSide()) return;
        boolean waterFound = this.checkWaterAround(level, pos);
        boolean currentSpin = state.hasProperty(WaterWheelBlock.SPINNING) && (Boolean)state.getValue(WaterWheelBlock.SPINNING);

        if (this.hasWater != waterFound || currentSpin != waterFound) {
            this.hasWater = waterFound;
            this.setChanged();
            net.exerion.mechanical.GlobalKineticNetwork.updateSource(level, pos, this.getOutputFM(), this.isGeneratingFM());
            if (currentSpin != waterFound) {
                level.setBlock(pos, (BlockState)state.setValue(WaterWheelBlock.SPINNING, waterFound), 3);
            }
        }
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        ExerionCrashGuard.safeTick(level, pos, state, this, () -> {
            if (level.isClientSide()) {
                if (((Boolean)state.getValue(WaterWheelBlock.SPINNING)).booleanValue()) {
                    this.visualRotationAngle = (float)level.getGameTime() * ROTATION_SPEED % 360.0f;
                }
                return;
            }
            ++this.checkTimer;
            if (this.checkTimer >= CHECK_INTERVAL) {
                this.checkTimer = 0;
                this.triggerWaterCheck(level, pos, state);
                if (this.hasWater && level instanceof ServerLevel) {
                    ServerLevel serverLevel = (ServerLevel)level;
                    serverLevel.sendParticles((ParticleOptions)ParticleTypes.SPLASH, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 8, 0.4, 0.2, 0.4, 0.0);
                }
            }
        });
    }

    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("HasWater", this.hasWater);
    }

    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.hasWater = input.getBooleanOr("HasWater", false);
    }
}

