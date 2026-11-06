package net.exerion.item;

import net.exerion.energy.IEnergyStorage;
import net.exerion.mechanical.IFMConsumer;
import net.exerion.mechanical.IFMSource;
import net.exerion.progression.EraProgressionRegistry;
import net.exerion.progression.PlayerEraManager;
import net.exerion.progression.TechnologyEra;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.function.Consumer;

public class SwissArmyKnifeItem extends Item {

    public enum Mode {
        OMNI(0, "§6[Universal: Mineração & Chave]", 25.0f),
        AREA_3X3(1, "§c[Mineração em Área 3x3]", 22.0f),
        WRENCH(2, "§b[Chave Inglesa Exclusiva]", 1.0f),
        SCANNER(3, "§a[Diagnóstico & Scanner]", 1.0f);

        private final int id;
        private final String displayName;
        private final float speed;

        Mode(int id, String displayName, float speed) {
            this.id = id;
            this.displayName = displayName;
            this.speed = speed;
        }

        public int getId() {
            return id;
        }

        public String getDisplayName() {
            return displayName;
        }

        public float getSpeed() {
            return speed;
        }

        public Mode next() {
            Mode[] values = values();
            return values[(this.ordinal() + 1) % values.length];
        }

        public static Mode fromId(int id) {
            for (Mode m : values()) {
                if (m.id == id) return m;
            }
            return OMNI;
        }
    }

    public SwissArmyKnifeItem(Item.Properties properties) {
        super(properties);
    }

    public static Mode getMode(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            return Mode.fromId(tag.getIntOr("KnifeMode", 0));
        }
        return Mode.OMNI;
    }

    public static void setMode(ItemStack stack, Mode mode) {
        stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, customData -> {
            CompoundTag tag = customData.copyTag();
            tag.putInt("KnifeMode", mode.getId());
            return CustomData.of(tag);
        });
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (stack.getDamageValue() >= stack.getMaxDamage()) {
            return 1.0f;
        }
        Mode mode = getMode(stack);
        if (mode == Mode.WRENCH || mode == Mode.SCANNER) {
            return 1.0f;
        }
        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE) ||
            state.is(BlockTags.MINEABLE_WITH_AXE) ||
            state.is(BlockTags.MINEABLE_WITH_SHOVEL) ||
            state.is(BlockTags.MINEABLE_WITH_HOE) ||
            state.is(BlockTags.LEAVES) ||
            state.is(BlockTags.WOOL)) {
            return mode.getSpeed();
        }
        return 1.0f;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        if (stack.getDamageValue() >= stack.getMaxDamage()) {
            return false;
        }
        Mode mode = getMode(stack);
        if (mode == Mode.WRENCH || mode == Mode.SCANNER) {
            return false;
        }
        return state.is(BlockTags.MINEABLE_WITH_PICKAXE) ||
               state.is(BlockTags.MINEABLE_WITH_AXE) ||
               state.is(BlockTags.MINEABLE_WITH_SHOVEL) ||
               state.is(BlockTags.MINEABLE_WITH_HOE);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (player == null) {
            return InteractionResult.PASS;
        }

        if (stack.getDamageValue() >= stack.getMaxDamage()) {
            if (!level.isClientSide()) {
                player.sendSystemMessage(Component.literal("§c[Canivete Suíço] Sem energia! Recarrega na BatBox ou Peitoral."));
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.6f, 1.0f);
            }
            return InteractionResult.FAIL;
        }

        Mode mode = getMode(stack);

        // MODO SCANNER
        if (mode == Mode.SCANNER) {
            if (!level.isClientSide()) {
                TechnologyEra required = EraProgressionRegistry.getRequiredEra(state.getBlock());
                String blockName = Component.translatable(state.getBlock().getDescriptionId()).getString();
                player.sendSystemMessage(Component.literal("§b=== Diagnóstico Exerion ==="));
                player.sendSystemMessage(Component.literal("§7Bloco: §f" + blockName));
                player.sendSystemMessage(Component.literal("§7Era Requerida: §d" + required.getDisplayNameComponent().getString()));

                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof IEnergyStorage energyStorage) {
                    player.sendSystemMessage(Component.literal("§eEnergia: §6" + energyStorage.getEnergy() + " / " + energyStorage.getMaxEnergy() + " EU"));
                }
                if (be instanceof IFMSource fmSource) {
                    player.sendSystemMessage(Component.literal("§3Torque Mecânico (Output): §b" + fmSource.getOutputFM() + " FM"));
                }
                if (be instanceof IFMConsumer fmConsumer) {
                    player.sendSystemMessage(Component.literal("§3Torque Requerido: §b" + fmConsumer.getRequiredFM() + " FM"));
                }

                stack.setDamageValue(Math.min(stack.getMaxDamage(), stack.getDamageValue() + 2));
                level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.8f, 1.4f);
            }
            return InteractionResult.SUCCESS;
        }

        // MODO CHAVE INGLESA / UNIVERSAL EM MÁQUINAS EXERION
        String namespace = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getNamespace();
        if (namespace.equals("exerion")) {
            if (!level.isClientSide()) {
                if (player.isSecondaryUseActive()) {
                    // Desmontar máquina instantaneamente com drop
                    level.destroyBlock(pos, true);
                    player.sendSystemMessage(Component.literal("§a[Canivete Suíço] Máquina desmontada com sucesso!"));
                    level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.6f, 1.5f);
                    stack.setDamageValue(Math.min(stack.getMaxDamage(), stack.getDamageValue() + 1));
                } else if (state.hasProperty(BlockStateProperties.FACING)) {
                    Direction current = state.getValue(BlockStateProperties.FACING);
                    Direction next = Direction.values()[(current.ordinal() + 1) % Direction.values().length];
                    level.setBlock(pos, state.setValue(BlockStateProperties.FACING, next), 3);
                    player.sendSystemMessage(Component.literal("§b[Canivete Suíço] Orientação: " + next.getName()));
                    level.playSound(null, pos, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.BLOCKS, 0.8f, 1.2f);
                    stack.setDamageValue(Math.min(stack.getMaxDamage(), stack.getDamageValue() + 1));
                } else if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                    Direction current = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
                    Direction next = switch (current) {
                        case NORTH -> Direction.EAST;
                        case EAST -> Direction.SOUTH;
                        case SOUTH -> Direction.WEST;
                        case WEST -> Direction.NORTH;
                        default -> Direction.NORTH;
                    };
                    level.setBlock(pos, state.setValue(BlockStateProperties.HORIZONTAL_FACING, next), 3);
                    player.sendSystemMessage(Component.literal("§b[Canivete Suíço] Orientação: " + next.getName()));
                    level.playSound(null, pos, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.BLOCKS, 0.8f, 1.2f);
                    stack.setDamageValue(Math.min(stack.getMaxDamage(), stack.getDamageValue() + 1));
                }
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Shift + Clique Direito no Ar -> Trocar Modo
        if (player.isSecondaryUseActive()) {
            if (!level.isClientSide()) {
                Mode current = getMode(stack);
                Mode next = current.next();
                setMode(stack, next);
                player.sendSystemMessage(Component.literal("§6[Canivete Suíço] Modo: " + next.getDisplayName()));
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_BUTTON_CLICK, SoundSource.PLAYERS, 0.7f, 1.5f);
            }
            return InteractionResult.SUCCESS;
        }

        // Clique simples no ar no modo Scanner -> Mostra a Era do próprio jogador
        Mode mode = getMode(stack);
        if (mode == Mode.SCANNER) {
            if (!level.isClientSide()) {
                TechnologyEra currentEra = PlayerEraManager.getPlayerEra(player);
                player.sendSystemMessage(Component.literal("§eA tua Era atual: §d" + currentEra.getDisplayNameComponent().getString()));
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.6f, 1.2f);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!level.isClientSide() && state.getDestroySpeed(level, pos) != 0.0f) {
            stack.setDamageValue(Math.min(stack.getMaxDamage(), stack.getDamageValue() + 1));

            // Executar Mineração 3x3 se o modo estiver ativo
            Mode mode = getMode(stack);
            if (mode == Mode.AREA_3X3 && miner instanceof ServerPlayer player && !player.isSecondaryUseActive()) {
                Direction lookDir = Direction.orderedByNearest(player)[0];
                break3x3(level, pos, lookDir, player, stack);
            }
        }
        return true;
    }

    private void break3x3(Level level, BlockPos origin, Direction hitDirection, ServerPlayer player, ItemStack stack) {
        int xOffset = (hitDirection.getAxis() == Direction.Axis.X) ? 0 : 1;
        int yOffset = (hitDirection.getAxis() == Direction.Axis.Y) ? 0 : 1;
        int zOffset = (hitDirection.getAxis() == Direction.Axis.Z) ? 0 : 1;

        for (int x = -xOffset; x <= xOffset; x++) {
            for (int y = -yOffset; y <= yOffset; y++) {
                for (int z = -zOffset; z <= zOffset; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    BlockPos targetPos = origin.offset(x, y, z);
                    BlockState targetState = level.getBlockState(targetPos);

                    if (isCorrectToolForDrops(stack, targetState) && targetState.getDestroySpeed(level, targetPos) >= 0) {
                        level.destroyBlock(targetPos, true, player);
                        stack.setDamageValue(Math.min(stack.getMaxDamage(), stack.getDamageValue() + 1));
                        if (stack.getDamageValue() >= stack.getMaxDamage()) {
                            return;
                        }
                    }
                }
            }
        }
    }

    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide()) {
            if (stack.getDamageValue() < stack.getMaxDamage()) {
                target.hurt(attacker.damageSources().mobAttack(attacker), 11.0f);
                stack.setDamageValue(Math.min(stack.getMaxDamage(), stack.getDamageValue() + 1));
                Level level = attacker.level();
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, target.getX(), target.getY() + 1.0, target.getZ(), 6, 0.2, 0.2, 0.2, 0.1);
                    serverLevel.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY() + 1.0, target.getZ(), 4, 0.2, 0.2, 0.2, 0.05);
                }
                attacker.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 0.8f, 1.2f);
            } else {
                target.hurt(attacker.damageSources().mobAttack(attacker), 2.0f);
            }
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getDamageValue() < stack.getMaxDamage() && getMode(stack) != Mode.WRENCH;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag tooltipFlag) {
        Mode mode = getMode(stack);
        tooltip.accept(Component.literal("§7Modo Atual: " + mode.getDisplayName()));
        tooltip.accept(Component.literal("§8Shift + Clique Direito no Ar: Alternar modo"));
        int energy = Math.max(0, stack.getMaxDamage() - stack.getDamageValue());
        tooltip.accept(Component.literal("§eEnergia: §f" + energy + " §7/ §f" + stack.getMaxDamage() + " §7EU"));
        super.appendHoverText(stack, context, display, tooltip, tooltipFlag);
    }
}
