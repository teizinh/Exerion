package net.exerion.block.entity;

import java.util.List;
import net.exerion.block.entity.ModBlockEntityTypes;
import net.exerion.energy.IEnergyStorage;
import net.exerion.item.ModItems;
import net.exerion.menu.MiningLaserMenu;
import net.exerion.util.ExerionCrashGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class MiningLaserBlockEntity
extends BlockEntity
implements MenuProvider,
IEnergyStorage {
    private static final Direction[] DIRECTIONS = Direction.values();
    private final SimpleContainer inventory = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            MiningLaserBlockEntity.this.setChanged();
        }
    };
    private int energy = 0;
    private final int maxEnergy = 20000;
    private int tickCounter = 0;
    protected final ContainerData dataAccess = new ContainerData() {
        

        public int get(int index) {
            return switch (index) {
                case 0 -> MiningLaserBlockEntity.this.energy;
                case 1 -> MiningLaserBlockEntity.this.maxEnergy;
                default -> 0;
            };
        }

        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    MiningLaserBlockEntity.this.energy = value;
                }
            }
        }

        public int getCount() {
            return 3;
        }
    };

    public MiningLaserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.MINING_LASER_ENTITY, pos, state);
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if (level != null && !level.isClientSide()) {
            net.exerion.energy.GlobalEnergyNetwork.registerDevice(level, this.worldPosition, false, this.getMaxEnergy() - this.getEnergy(), 100);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (this.level != null && !this.level.isClientSide()) {
            net.exerion.energy.GlobalEnergyNetwork.removeDevice(this.level, this.worldPosition);
        }
    }

    @Override
    public int getEnergy() {
        return this.energy;
    }

    @Override
    public int getMaxEnergy() {
        return 20000;
    }

    @Override
    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(20000, energy));
        this.setChanged();
    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        ExerionCrashGuard.safeTick(level, pos, state, this, () -> {
            int cycleInterval;
            if (level.isClientSide()) {
                return;
            }
            ++this.tickCounter;
            boolean hasSpeedUpgrade = this.inventory.getItem(0).is(ModItems.SPEED_UPGRADE);
            int n = cycleInterval = hasSpeedUpgrade ? 20 : 40;
            if (this.tickCounter >= cycleInterval) {
                this.tickCounter = 0;
                this.performMiningCycle(level, pos);
            }
        });
    }

    private void performMiningCycle(Level level, BlockPos pos) {
        List<ItemEntity> droppedItems = level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(1.5, 1.5, 1.5));
        for (ItemEntity itemEntity : droppedItems) {
            ItemStack stack = itemEntity.getItem();
            if (stack.isEmpty()) continue;
            boolean fullyRouted = this.routeToAdjacentInventories(level, pos, stack);
            if (fullyRouted || stack.isEmpty()) {
                itemEntity.discard();
                continue;
            }
            itemEntity.setItem(stack);
        }
        BlockPos targetPos = null;
        int minY = level.getMinY();
        int y = -1;
        block1: while (pos.getY() + y >= minY) {
            for (int x = -1; x <= 1; ++x) {
                for (int z = -1; z <= 1; ++z) {
                    BlockPos current = pos.offset(x, y, z);
                    BlockState currentSt = level.getBlockState(current);
                    if (currentSt.isAir() || currentSt.is(Blocks.BEDROCK) || !currentSt.getFluidState().isEmpty()) continue;
                    targetPos = current;
                    break block1;
                }
            }
            --y;
        }
        if (targetPos != null && this.energy >= 500) {
            this.energy -= 500;
            this.setChanged();
            BlockState targetState = level.getBlockState(targetPos);
            ServerLevel serverLevel = (ServerLevel)level;
            BlockEntity targetBe = level.getBlockEntity(targetPos);
            List<ItemStack> drops = Block.getDrops(targetState, serverLevel, targetPos, targetBe);
            for (ItemStack drop : drops) {
                boolean fullyRouted;
                if (drop.isEmpty() || (fullyRouted = this.routeToAdjacentInventories(level, pos, drop)) || drop.isEmpty()) continue;
                ItemEntity entity = new ItemEntity(level, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, drop);
                level.addFreshEntity((Entity)entity);
            }
            level.destroyBlock(targetPos, false);
            double startX = (double)pos.getX() + 0.5;
            double startY = pos.getY();
            double startZ = (double)pos.getZ() + 0.5;
            double endX = (double)targetPos.getX() + 0.5;
            double endY = (double)targetPos.getY() + 0.5;
            double endZ = (double)targetPos.getZ() + 0.5;
            double distance = Math.sqrt(pos.distSqr((Vec3i)targetPos));
            int steps = (int)(distance * 2.0);
            for (int step = 0; step <= steps; ++step) {
                double t = (double)step / (double)steps;
                double px = startX + (endX - startX) * t;
                double py = startY + (endY - startY) * t;
                double pz = startZ + (endZ - startZ) * t;
                serverLevel.sendParticles((ParticleOptions)ParticleTypes.SOUL_FIRE_FLAME, px, py, pz, 1, 0.0, 0.0, 0.0, 0.0);
            }
            level.playSound(null, pos, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 0.3f, 2.0f);
        }
    }

    private boolean routeToAdjacentInventories(Level level, BlockPos pos, ItemStack stack) {
        for (Direction dir : DIRECTIONS) {
            BlockPos sidePos = pos.relative(dir);
            BlockEntity sideBe = level.getBlockEntity(sidePos);
            if (!(sideBe instanceof Container container) || sideBe instanceof MiningLaserBlockEntity) continue;
            for (int slot = 0; slot < container.getContainerSize(); ++slot) {
                ItemStack slotStack = container.getItem(slot);
                if (slotStack.isEmpty()) {
                    container.setItem(slot, stack.copy());
                    stack.setCount(0);
                    container.setChanged();
                    return true;
                }
                if (!ItemStack.isSameItemSameComponents(slotStack, stack) || slotStack.getCount() >= slotStack.getMaxStackSize()) continue;
                int add = Math.min(stack.getCount(), slotStack.getMaxStackSize() - slotStack.getCount());
                slotStack.grow(add);
                stack.shrink(add);
                container.setChanged();
                if (!stack.isEmpty()) continue;
                return true;
            }
        }
        return stack.isEmpty();
    }

    public Component getDisplayName() {
        return Component.translatable((String)"container.exerion.mining_laser");
    }

    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new MiningLaserMenu(containerId, playerInventory, (Container)this.inventory, this.dataAccess);
    }

    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("Energy", this.energy);
        ValueOutput invOutput = output.child("Inventory");
        ItemStack upgradeStack = this.inventory.getItem(0);
        if (!upgradeStack.isEmpty()) {
            invOutput.store("UpgradeSlot", ItemStack.CODEC, upgradeStack);
        }
    }

    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.energy = input.getIntOr("Energy", 0);
        ValueInput invInput = input.childOrEmpty("Inventory");
        ItemStack upgradeStack = invInput.read("UpgradeSlot", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.inventory.setItem(0, upgradeStack);
    }
}

