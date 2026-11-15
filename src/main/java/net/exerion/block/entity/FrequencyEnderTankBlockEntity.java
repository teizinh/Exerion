package net.exerion.block.entity;

import net.exerion.fluid.GlobalFluidNetwork;
import net.exerion.util.ExerionCrashGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class FrequencyEnderTankBlockEntity extends BlockEntity {
    private static final Direction[] DIRECTIONS = Direction.values();
    private String frequency = "DEFAULT";
    private int extractCooldown = 0;

    public FrequencyEnderTankBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.FREQUENCY_ENDER_TANK_ENTITY, pos, state);
    }

    public String getFrequency() {
        return this.frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
        this.setChanged();
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        ExerionCrashGuard.safeTick(level, pos, state, this, () -> {
            if (level.isClientSide()) {
                return;
            }
            if (this.extractCooldown > 0) {
                --this.extractCooldown;
                return;
            }
            int currentLava = GlobalFluidNetwork.getLavaAmount(this.frequency);
            if (currentLava > 0) {
                for (Direction dir : DIRECTIONS) {
                    BlockEntity neighbor = level.getBlockEntity(pos.relative(dir));
                    if (neighbor instanceof GeothermalGeneratorBlockEntity geoGenerator) {
                        int space = geoGenerator.getMaxLavaBuffer() - geoGenerator.getLavaBuffer();
                        if (space <= 0) {
                            continue;
                        }
                        int amountToPush = Math.min(space, Math.min(100, currentLava));
                        int extracted = GlobalFluidNetwork.extractLava(this.frequency, amountToPush);
                        if (extracted > 0) {
                            geoGenerator.setLavaBuffer(geoGenerator.getLavaBuffer() + extracted);
                            this.extractCooldown = 5;
                            break;
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putString("Frequency", this.frequency);
        output.putInt("ExtractCooldown", this.extractCooldown);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.frequency = input.getStringOr("Frequency", "DEFAULT");
        this.extractCooldown = input.getIntOr("ExtractCooldown", 0);
    }
}
