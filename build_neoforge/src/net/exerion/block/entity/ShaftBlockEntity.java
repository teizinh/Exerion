package net.exerion.block.entity;

import net.exerion.block.ShaftBlock;
import net.exerion.block.entity.ModBlockEntityTypes;
import net.exerion.mechanical.MechanicalNetwork;
import net.exerion.util.ExerionCrashGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class ShaftBlockEntity
extends BlockEntity {
    public float visualRotationAngle = 0.0f;
    public static final float ROTATION_SPEED = 4.0f;

    public ShaftBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.SHAFT_ENTITY, pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        ExerionCrashGuard.safeTick(level, pos, state, this, () -> {
            if (level.isClientSide()) {
                if (((Boolean)state.getValue(ShaftBlock.SPINNING)).booleanValue()) {
                    this.visualRotationAngle = (float)level.getGameTime() * 4.0f % 360.0f;
                }
                return;
            }
            if (level.getGameTime() % 10L == 0L) {
                boolean anySourceActive = MechanicalNetwork.hasActiveSource(level, pos);
                boolean currentSpin = (Boolean)state.getValue(ShaftBlock.SPINNING);
                if (currentSpin != anySourceActive) {
                    level.setBlock(pos, (BlockState)state.setValue(ShaftBlock.SPINNING, anySourceActive), 3);
                }
            }
        });
    }
}

