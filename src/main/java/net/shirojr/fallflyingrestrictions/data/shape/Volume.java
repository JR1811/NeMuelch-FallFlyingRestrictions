package net.shirojr.fallflyingrestrictions.data.shape;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public interface Volume {
    @SuppressWarnings("unused")
    Identifier getIdentifier();

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean contains(BlockPos pos);

    void toNbt(NbtCompound nbt);

    PacketByteBuf toPacketByteBuf(PacketByteBuf buf);

    int blockCount();

    BlockPos center();

    boolean preventStartFlying();

    boolean interruptsFlying();
}
