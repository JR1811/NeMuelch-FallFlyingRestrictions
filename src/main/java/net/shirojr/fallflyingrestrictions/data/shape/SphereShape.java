package net.shirojr.fallflyingrestrictions.data.shape;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.shirojr.fallflyingrestrictions.FallFlyingRestrictions;

public class SphereShape implements Volume {
    public static final Identifier IDENTIFIER = FallFlyingRestrictions.getId("sphere");

    private final BlockPos center;
    private final double distance;
    private final boolean preventStartFlying, interruptFlying;

    public SphereShape(BlockPos center, double distance, boolean preventStartFlying, boolean interruptFlying) {
        this.center = center;
        this.distance = distance;
        this.preventStartFlying = preventStartFlying;
        this.interruptFlying = interruptFlying;
    }

    public static SphereShape fromNbt(NbtCompound nbt) {
        return new SphereShape(
                BlockPos.fromLong(nbt.getLong("center")),
                nbt.getDouble("distance"),
                nbt.getBoolean("preventStartFlying"),
                nbt.getBoolean("interruptFlying")
        );
    }

    public static SphereShape fromPacketByteBuf(PacketByteBuf buf) {
        return new SphereShape(
                BlockPos.fromLong(buf.readLong()),
                buf.readDouble(),
                buf.readBoolean(),
                buf.readBoolean()
        );
    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public boolean contains(BlockPos pos) {
        return pos.isWithinDistance(this.center, this.distance);
    }

    @Override
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putLong("center", this.center.asLong());
        nbt.putDouble("distance", this.distance);
        nbt.putBoolean("preventStartFlying", this.preventStartFlying);
        nbt.putBoolean("interruptFlying", this.interruptFlying);
        return nbt;
    }

    @Override
    public PacketByteBuf toPacketByteBuf(PacketByteBuf buf) {
        buf.writeLong(this.center.asLong());
        buf.writeDouble(this.distance);
        buf.writeBoolean(this.preventStartFlying);
        buf.writeBoolean(this.interruptFlying);
        return null;
    }

    @Override
    public int blockCount() {
        int count = 0;
        int rInt = (int) Math.floor(this.distance);

        for (int x = -rInt; x <= rInt; x++) {
            for (int y = -rInt; y <= rInt; y++) {
                for (int z = -rInt; z <= rInt; z++) {
                    if (x * x + y * y + z * z <= this.distance * this.distance) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    @Override
    public BlockPos center() {
        return this.center;
    }

    @Override
    public boolean preventStartFlying() {
        return this.preventStartFlying;
    }

    @Override
    public boolean interruptFlying() {
        return this.interruptFlying;
    }
}
