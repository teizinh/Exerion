package net.exerion.block.entity;

import net.exerion.block.entity.ModBlockEntityTypes;
import net.exerion.energy.IEnergyStorage;
import net.exerion.menu.GeothermalGeneratorMenu;
import net.exerion.util.ExerionCrashGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class GeothermalGeneratorBlockEntity
extends BlockEntity
implements MenuProvider,
IEnergyStorage {
    private static final Direction[] DIRECTIONS = Direction.values();
    private final SimpleContainer inventory = new SimpleContainer(2) {
        @Override
        public void setChanged() {
            super.setChanged();
            GeothermalGeneratorBlockEntity.this.setChanged();
        }
    };
    private int energy = 0;
    private final int maxEnergy = 10000;
    private int lavaBuffer = 0;
    private final int maxLava = 4000;
    private int scanCooldown = 0;
    protected final ContainerData dataAccess = new ContainerData() {
        

        public int get(int index) {
            return switch (index) {
                case 0 -> GeothermalGeneratorBlockEntity.this.energy;
                case 1 -> GeothermalGeneratorBlockEntity.this.maxEnergy;
                case 2 -> GeothermalGeneratorBlockEntity.this.lavaBuffer;
                case 3 -> GeothermalGeneratorBlockEntity.this.maxLava;
                default -> 0;
            };
        }

        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    GeothermalGeneratorBlockEntity.this.energy = value;
                    break;
                }
                case 2: {
                    GeothermalGeneratorBlockEntity.this.lavaBuffer = value;
                }
            }
        }

        public int getCount() {
            return 4;
        }
    };

    public GeothermalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.GEOTHERMAL_GENERATOR_ENTITY, pos, state);
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

    public int getLavaBuffer() {
        return this.lavaBuffer;
    }

    public void setLavaBuffer(int lavaBuffer) {
        this.lavaBuffer = lavaBuffer;
        this.setChanged();
    }

    public int getMaxLavaBuffer() {
        return 4000;
    }

    public int getMaxLava() {
        return 4000;
    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        ExerionCrashGuard.safeTick(level, pos, state, this, () -> {
            boolean isWorking;
            if (level.isClientSide()) {
                return;
            }
            ItemStack inputSlot = this.inventory.getItem(0);
            ItemStack outputSlot = this.inventory.getItem(1);
            if (inputSlot.getItem() == Items.LAVA_BUCKET && this.lavaBuffer <= 3000) {
                if (outputSlot.isEmpty()) {
                    this.inventory.setItem(1, new ItemStack((ItemLike)Items.BUCKET, 1));
                    inputSlot.shrink(1);
                    this.lavaBuffer += 1000;
                    this.setChanged();
                } else if (outputSlot.getItem() == Items.BUCKET && outputSlot.getCount() < outputSlot.getMaxStackSize()) {
                    outputSlot.grow(1);
                    inputSlot.shrink(1);
                    this.lavaBuffer += 1000;
                    this.setChanged();
                }
            }
            if (this.lavaBuffer > 0 && this.energy < 10000) {
                --this.lavaBuffer;
                this.energy = Math.min(10000, this.energy + 20);
                this.setChanged();
                if (level.getRandom().nextFloat() < 0.15f && level instanceof ServerLevel) {
                    ServerLevel serverLevel = (ServerLevel)level;
                    serverLevel.sendParticles((ParticleOptions)ParticleTypes.LAVA, (double)pos.getX() + 0.5, (double)pos.getY() + 1.0, (double)pos.getZ() + 0.5, 1, 0.1, 0.1, 0.1, 0.0);
                }
            }
            if (this.energy > 0) {
                this.pushEnergy(level, pos);
            }
            ++this.scanCooldown;
            if (this.scanCooldown >= 40) {
                this.scanCooldown = 0;
                if (this.lavaBuffer <= 3000) {
                    this.scanAndAbsorbLava(level, pos);
                }
            }
            boolean bl = isWorking = this.lavaBuffer > 0;
            if (state.hasProperty(BlockStateProperties.LIT) && (Boolean)state.getValue(BlockStateProperties.LIT) != isWorking) {
                level.setBlock(pos, (BlockState)state.setValue(BlockStateProperties.LIT, isWorking), 3);
            }
        });
    }

    private void pushEnergy(Level level, BlockPos pos) {
        int pushAmount = Math.min(this.energy, 64);
        for (Direction dir : DIRECTIONS) {
            if (dir == Direction.UP || pushAmount <= 0) continue;
            BlockEntity target = level.getBlockEntity(pos.relative(dir));
            if (!(target instanceof IEnergyStorage targetStorage) || target instanceof GeothermalGeneratorBlockEntity) continue;
            int received = targetStorage.receiveEnergy(pushAmount, false);
            if (received > 0) {
                this.energy -= received;
                pushAmount -= received;
                this.setChanged();
            }
        }
    }

    private void scanAndAbsorbLava(Level level, BlockPos pos) {
        for (Direction dir : DIRECTIONS) {
            BlockPos adjacent;
            BlockState adjState;
            if (dir == Direction.UP || !(adjState = level.getBlockState(adjacent = pos.relative(dir))).is(Blocks.LAVA) || !level.getFluidState(adjacent).isSource()) continue;
            level.setBlockAndUpdate(adjacent, Blocks.OBSIDIAN.defaultBlockState());
            level.playSound(null, adjacent, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 1.5f);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, (double)adjacent.getX() + 0.5, (double)adjacent.getY() + 1.0, (double)adjacent.getZ() + 0.5, 4, 0.2, 0.2, 0.2, 0.0);
            }
            this.lavaBuffer = Math.min(4000, this.lavaBuffer + 1000);
            this.setChanged();
            break;
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
        return Component.translatable((String)"container.exerion.geothermal_generator");
    }

    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new GeothermalGeneratorMenu(containerId, playerInventory, (Container)this.inventory, this.dataAccess);
    }

    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("Energy", this.energy);
        output.putInt("LavaBuffer", this.lavaBuffer);
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
        this.lavaBuffer = input.getIntOr("LavaBuffer", 0);
        ValueInput invInput = input.childOrEmpty("Inventory");
        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack stack = invInput.read("Slot" + i, ItemStack.CODEC).orElse(ItemStack.EMPTY);
            this.inventory.setItem(i, stack);
        }
    }
}

