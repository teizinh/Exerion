/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.resources.Identifier
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.neoforged.neoforge.client.network.ClientPacketDistributor
 *  net.neoforged.neoforge.network.PacketDistributor
 *  net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
 *  net.neoforged.neoforge.network.registration.PayloadRegistrar
 */
package net.exerion.network;

import net.exerion.block.entity.ProgressionTerminalBlockEntity;
import net.exerion.menu.ProgressionTerminalMenu;
import net.exerion.progression.ModAttachments;
import net.exerion.progression.TechnologyEra;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ExerionNetwork {
    public static final CustomPacketPayload.Type<AdvanceEraPayload> ADVANCE_ERA_PACKET_TYPE = new CustomPacketPayload.Type(Identifier.fromNamespaceAndPath((String)"exerion", (String)"advance_era"));
    public static final CustomPacketPayload.Type<SyncEraPayload> SYNC_ERA_PACKET_TYPE = new CustomPacketPayload.Type(Identifier.fromNamespaceAndPath((String)"exerion", (String)"sync_era"));

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("exerion").versioned("1.0.0");
        registrar.playToServer(ADVANCE_ERA_PACKET_TYPE, AdvanceEraPayload.CODEC, (payload, context) -> context.enqueueWork(() -> {
            Player player = context.player();
            if (player instanceof ServerPlayer) {
                ProgressionTerminalMenu terminalMenu;
                ProgressionTerminalBlockEntity be;
                ServerPlayer serverPlayer = (ServerPlayer)player;
                AbstractContainerMenu menu = serverPlayer.containerMenu;
                if (menu instanceof ProgressionTerminalMenu && (be = (terminalMenu = (ProgressionTerminalMenu)menu).getBlockEntity()) != null) {
                    be.tryAdvanceEra(serverPlayer);
                }
            }
        }));
        registrar.playToClient(SYNC_ERA_PACKET_TYPE, SyncEraPayload.CODEC, (payload, context) -> context.enqueueWork(() -> {
            Player player = context.player();
            if (player != null) {
                TechnologyEra era = TechnologyEra.fromId(payload.eraId());
                player.setData(ModAttachments.PLAYER_ERA, (Object)era);
            }
        }));
    }

    public static void syncEraToClient(ServerPlayer player, TechnologyEra era) {
        PacketDistributor.sendToPlayer((ServerPlayer)player, (CustomPacketPayload)new SyncEraPayload(era.getId()), (CustomPacketPayload[])new CustomPacketPayload[0]);
    }

    public static void sendAdvanceEraPacket() {
        ClientPacketDistributor.sendToServer((CustomPacketPayload)new AdvanceEraPayload(), (CustomPacketPayload[])new CustomPacketPayload[0]);
    }

    public record AdvanceEraPayload() implements CustomPacketPayload
    {
        public static final StreamCodec<FriendlyByteBuf, AdvanceEraPayload> CODEC = StreamCodec.unit((Object)new AdvanceEraPayload());

        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return ADVANCE_ERA_PACKET_TYPE;
        }
    }

    public record SyncEraPayload(String eraId) implements CustomPacketPayload
    {
        public static final StreamCodec<FriendlyByteBuf, SyncEraPayload> CODEC = CustomPacketPayload.codec((payload, buf) -> buf.writeUtf(payload.eraId()), buf -> new SyncEraPayload(buf.readUtf()));

        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return SYNC_ERA_PACKET_TYPE;
        }
    }
}

