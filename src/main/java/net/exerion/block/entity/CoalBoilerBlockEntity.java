package net.exerion.block.entity;

import net.exerion.block.CoalBoilerBlock;
import net.exerion.block.entity.ModBlockEntityTypes;
import net.exerion.item.ModItems;
import net.exerion.menu.CoalBoilerMenu;
import net.exerion.util.ExerionCrashGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class CoalBoilerBlockEntity
extends BlockEntity
implements MenuProvider {
    private final SimpleContainer inventory = new SimpleContainer(3) {
        @Override
        public void setChanged() {
            super.setChanged();
            CoalBoilerBlockEntity.this.setChanged();
        }
    };
    private int burnTime = 0;
    private int maxBurnTime = 0;
    private int steamTimer = 0;
    private static final int STEAM_TICKS = 40;
    protected final ContainerData dataAccess = new ContainerData() {
        

        public int get(int index) {
            return switch (index) {
                case 0 -> CoalBoilerBlockEntity.this.burnTime;
                case 1 -> CoalBoilerBlockEntity.this.maxBurnTime;
                case 2 -> CoalBoilerBlockEntity.this.steamTimer;
                default -> 0;
            };
        }

        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    CoalBoilerBlockEntity.this.burnTime = value;
                    break;
                }
                case 1: {
                    CoalBoilerBlockEntity.this.maxBurnTime = value;
                    break;
                }
                case 2: {
                    CoalBoilerBlockEntity.this.steamTimer = value;
                }
            }
        }

        public int getCount() {
            return 3;
        }
    };

    public CoalBoilerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.COAL_BOILER_ENTITY, pos, state);
    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }

    public int getBurnTime() {
        return this.burnTime;
    }

    public int getMaxBurnTime() {
        return this.maxBurnTime;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        ExerionCrashGuard.safeTick(level, pos, state, this, () -> {
            boolean isBurning;
            boolean wasBurning;
            if (level.isClientSide()) {
                return;
            }
            boolean bl = wasBurning = this.burnTime > 0;
            if (this.burnTime > 0) {
                --this.burnTime;
            }
            if (this.burnTime == 0) {
                ItemStack fuelSlot = this.inventory.getItem(0);
                ItemStack waterSlot = this.inventory.getItem(1);
                if (!fuelSlot.isEmpty() && (fuelSlot.is(Items.COAL) || fuelSlot.is(Items.CHARCOAL)) && !waterSlot.isEmpty() && waterSlot.is(Items.WATER_BUCKET)) {
                    this.burnTime = 200;
                    this.maxBurnTime = 200;
                    fuelSlot.shrink(1);
                    this.inventory.setItem(1, new ItemStack((ItemLike)Items.BUCKET));
                    this.setChanged();
                }
            }
            if (this.burnTime > 0) {
                ++this.steamTimer;
                if (this.steamTimer >= 40) {
                    this.steamTimer = 0;
                    ItemStack outputSlot = this.inventory.getItem(2);
                    if (outputSlot.isEmpty()) {
                        this.inventory.setItem(2, new ItemStack((ItemLike)ModItems.STEAM_CRYSTAL));
                        this.setChanged();
                    } else if (outputSlot.is(ModItems.STEAM_CRYSTAL) && outputSlot.getCount() < outputSlot.getMaxStackSize()) {
                        outputSlot.grow(1);
                        this.setChanged();
                    }
                }
            }
            boolean bl2 = isBurning = this.burnTime > 0;
            if (isBurning && level.getRandom().nextFloat() < 0.1f && level instanceof ServerLevel) {
                ServerLevel serverLevel = (ServerLevel)level;
                serverLevel.sendParticles((ParticleOptions)ParticleTypes.LARGE_SMOKE, (double)pos.getX() + 0.5, (double)pos.getY() + 1.0, (double)pos.getZ() + 0.5, 1, 0.1, 0.1, 0.1, 0.0);
                serverLevel.sendParticles((ParticleOptions)ParticleTypes.FLAME, (double)pos.getX() + 0.5, (double)pos.getY() + 1.0, (double)pos.getZ() + 0.5, 1, 0.1, 0.1, 0.1, 0.0);
            }
            if (wasBurning != isBurning) {
                level.setBlock(pos, (BlockState)state.setValue(CoalBoilerBlock.BURNING, isBurning), 3);
            }
        });
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
        return Component.translatable((String)"container.exerion.coal_boiler");
    }

    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CoalBoilerMenu(containerId, playerInventory, (Container)this.inventory, this.dataAccess);
    }

    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("BurnTime", this.burnTime);
        output.putInt("MaxBurnTime", this.maxBurnTime);
        output.putInt("SteamTimer", this.steamTimer);
        ValueOutput invOutput = output.child("Inventory");
        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            if (this.inventory.getItem(i).isEmpty()) continue;
            invOutput.store("Slot" + i, ItemStack.CODEC, this.inventory.getItem(i));
        }
    }

    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.burnTime = input.getIntOr("BurnTime", 0);
        this.maxBurnTime = input.getIntOr("MaxBurnTime", 0);
        this.steamTimer = input.getIntOr("SteamTimer", 0);
        ValueInput invInput = input.childOrEmpty("Inventory");
        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack stack = invInput.read("Slot" + i, ItemStack.CODEC).orElse(ItemStack.EMPTY);
            this.inventory.setItem(i, stack);
        }
    }
}

