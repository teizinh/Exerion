package net.exerion.block.entity;

import net.exerion.block.ElectricGeneratorBlock;
import net.exerion.block.entity.ModBlockEntityTypes;
import net.exerion.energy.IEnergyStorage;
import net.exerion.item.ModItems;
import net.exerion.menu.ElectricGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class ElectricGeneratorBlockEntity
extends BlockEntity
implements MenuProvider,
IEnergyStorage {
    private static final Direction[] DIRECTIONS = Direction.values();
    private final SimpleContainer inventory = new SimpleContainer(2) {
        @Override
        public void setChanged() {
            super.setChanged();
            ElectricGeneratorBlockEntity.this.setChanged();
        }
    };
    private int energy = 0;
    private final int maxEnergy = 10000;
    private int burnTime = 0;
    private int maxBurnTime = 0;
    private int generationRate = 0;
    protected final ContainerData dataAccess = new ContainerData() {
        

        public int get(int index) {
            return switch (index) {
                case 0 -> ElectricGeneratorBlockEntity.this.burnTime;
                case 1 -> ElectricGeneratorBlockEntity.this.maxBurnTime;
                case 2 -> ElectricGeneratorBlockEntity.this.energy;
                case 3 -> ElectricGeneratorBlockEntity.this.maxEnergy;
                default -> 0;
            };
        }

        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    ElectricGeneratorBlockEntity.this.burnTime = value;
                    break;
                }
                case 1: {
                    ElectricGeneratorBlockEntity.this.maxBurnTime = value;
                    break;
                }
                case 2: {
                    ElectricGeneratorBlockEntity.this.energy = value;
                }
            }
        }

        public int getCount() {
            return 4;
        }
    };

    public ElectricGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.ELECTRIC_GENERATOR_ENTITY, pos, state);
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
        return 10000;
    }

    @Override
    public void setEnergy(int energy) {
        this.energy = energy;
        this.setChanged();
    }

    public int getBurnTime() {
        return this.burnTime;
    }

    public int getMaxBurnTime() {
        return this.maxBurnTime;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        int damage;
        ItemStack fuelSlot;
        if (level.isClientSide()) {
            return;
        }
        boolean wasGenerating = (Boolean)state.getValue(ElectricGeneratorBlock.GENERATING);
        boolean isGenerating = false;
        if (this.burnTime > 0) {
            --this.burnTime;
            isGenerating = true;
            if (this.energy < 10000) {
                this.energy = Math.min(10000, this.energy + this.generationRate);
            }
            this.setChanged();
        }
        if (this.burnTime == 0 && this.energy < 10000 && !(fuelSlot = this.inventory.getItem(0)).isEmpty()) {
            if (fuelSlot.is(Items.COAL) || fuelSlot.is(Items.CHARCOAL)) {
                this.burnTime = 100;
                this.maxBurnTime = 100;
                this.generationRate = 10;
                fuelSlot.shrink(1);
                this.setChanged();
            } else if (fuelSlot.is(Items.COAL_BLOCK)) {
                this.burnTime = 900;
                this.maxBurnTime = 900;
                this.generationRate = 12;
                fuelSlot.shrink(1);
                this.setChanged();
            } else if (fuelSlot.is(ModItems.STEAM_CRYSTAL)) {
                this.burnTime = 200;
                this.maxBurnTime = 200;
                this.generationRate = 20;
                fuelSlot.shrink(1);
                this.setChanged();
            } else if (fuelSlot.is(Items.LAVA_BUCKET)) {
                this.burnTime = 400;
                this.maxBurnTime = 400;
                this.generationRate = 20;
                this.inventory.setItem(0, new ItemStack((ItemLike)Items.BUCKET));
                this.setChanged();
            }
        }
        ItemStack chargeSlot = this.inventory.getItem(1);
        if (this.energy > 0 && !chargeSlot.isEmpty() && (chargeSlot.is(ModItems.ERA_SCANNER) || chargeSlot.is(ModItems.SWISS_ARMY_KNIFE) || chargeSlot.is(ModItems.STEAM_BOOTS) || chargeSlot.is(ModItems.ELECTRIC_PLATE) || chargeSlot.is(ModItems.LOGIC_HELMET) || chargeSlot.is(ModItems.ELECTRIC_MINING_DRILL) || chargeSlot.is(ModItems.PLASMA_SWORD)) && (damage = chargeSlot.getDamageValue()) > 0) {
            int chargeAmount = Math.min(damage, 2);
            chargeSlot.setDamageValue(damage - chargeAmount);
            this.energy -= chargeAmount;
            this.setChanged();
            if (level.getGameTime() % 20L == 0L) {
                level.playSound(null, pos, SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.BLOCKS, 0.4f, 1.2f);
            }
        }
        if (this.energy > 0) {
            this.pushEnergy(level, pos);
        }
        if (wasGenerating != isGenerating) {
            level.setBlock(pos, (BlockState)state.setValue(ElectricGeneratorBlock.GENERATING, isGenerating), 3);
        }
    }

    private void pushEnergy(Level level, BlockPos pos) {
        int pushAmount = Math.min(this.energy, 32);
        for (Direction dir : DIRECTIONS) {
            if (pushAmount <= 0) break;
            BlockEntity target = level.getBlockEntity(pos.relative(dir));
            if (!(target instanceof IEnergyStorage targetStorage) || target instanceof ElectricGeneratorBlockEntity) {
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
        return Component.translatable((String)"container.exerion.electric_generator");
    }

    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ElectricGeneratorMenu(containerId, playerInventory, (Container)this.inventory, this.dataAccess);
    }

    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("Energy", this.energy);
        output.putInt("BurnTime", this.burnTime);
        output.putInt("MaxBurnTime", this.maxBurnTime);
        output.putInt("GenRate", this.generationRate);
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
        this.burnTime = input.getIntOr("BurnTime", 0);
        this.maxBurnTime = input.getIntOr("MaxBurnTime", 0);
        this.generationRate = input.getIntOr("GenRate", 0);
        ValueInput invInput = input.childOrEmpty("Inventory");
        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack stack = invInput.read("Slot" + i, ItemStack.CODEC).orElse(ItemStack.EMPTY);
            this.inventory.setItem(i, stack);
        }
    }
}

