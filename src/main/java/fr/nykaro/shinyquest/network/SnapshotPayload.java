package fr.nykaro.shinyquest.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** Paquet S2C transportant l'instantane de progression (encode en JSON). */
public record SnapshotPayload(String json) implements CustomPayload {

    public static final CustomPayload.Id<SnapshotPayload> ID =
        new CustomPayload.Id<>(Identifier.of("nykaroshiny", "snapshot"));

    public static final PacketCodec<PacketByteBuf, SnapshotPayload> CODEC =
        PacketCodec.tuple(PacketCodecs.STRING, SnapshotPayload::json, SnapshotPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
