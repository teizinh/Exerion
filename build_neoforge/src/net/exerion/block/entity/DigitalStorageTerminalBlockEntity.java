package net.exerion.block.entity;

import net.exerion.block.entity.DigitalStorageContainer;
import net.exerion.block.entity.ModBlockEntityTypes;
import net.exerion.menu.DigitalStorageTerminalMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DigitalStorageTerminalBlockEntity
extends BlockEntity
implements MenuProvider {
    private DigitalStorageContainer virtualContainer;

    public DigitalStorageTerminalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.DIGITAL_STORAGE_TERMINAL_ENTITY, pos, state);
    }

    public void rebuildContainer() {
        if (this.virtualContainer == null) {
            this.virtualContainer = new DigitalStorageContainer(this.level, this.worldPosition);
        } else {
            this.virtualContainer.rebuildMappings();
        }
    }

    public DigitalStorageContainer getVirtualContainer() {
        if (this.virtualContainer == null) {
            this.rebuildContainer();
        }
        return this.virtualContainer;
    }

    public Component getDisplayName() {
        return Component.translatable((String)"block.exerion.digital_storage_terminal");
    }

    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        DigitalStorageContainer container = this.getVirtualContainer();
        return new DigitalStorageTerminalMenu(containerId, playerInventory, container);
    }
}

