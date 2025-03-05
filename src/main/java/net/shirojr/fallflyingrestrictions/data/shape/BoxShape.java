package net.shirojr.fallflyingrestrictions.data.shape;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.shirojr.fallflyingrestrictions.FallFlyingRestrictions;

public class BoxShape implements Volume {

    public static final Identifier IDENTIFIER = FallFlyingRestrictions.getId("box");

    private final BlockPos min;
    private final BlockPos max;
    private final boolean considerHeight;
    private final boolean preventStartFlying, interruptFlying;

    public BoxShape(BlockPos start, BlockPos end, boolean considerHeight, boolean preventStartFlying, boolean interruptFlying) {
        this.min = new BlockPos(
                Math.min(start.getX(), end.getX()),
                Math.min(start.getY(), end.getY()),
                Math.min(start.getZ(), end.getZ())
        );
        this.max = new BlockPos(
                Math.max(start.getX(), end.getX()),
                Math.max(start.getY(), end.getY()),
                Math.max(start.getZ(), end.getZ())
        );
        this.considerHeight = considerHeight;
        this.preventStartFlying = preventStartFlying;
        this.interruptFlying = interruptFlying;
    }

    @SuppressWarnings("unused")
    public BoxShape(BlockPos start, BlockPos end) {
        this(start, end, true, true, false);
    }

    public static BoxShape fromNbt(NbtCompound nbt) {
        return new BoxShape(
                BlockPos.fromLong(nbt.getLong("min")),
                BlockPos.fromLong(nbt.getLong("max")),
                nbt.getBoolean("considerHeight"),
                nbt.getBoolean("preventStartFlying"),
                nbt.getBoolean("interruptFlying")
        );
    }

    public static BoxShape fromPacketByteBuf(PacketByteBuf buf) {
        return new BoxShape(
                BlockPos.fromLong(buf.readLong()),
                BlockPos.fromLong(buf.readLong()),
                buf.readBoolean(),
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
        boolean isSmaller = pos.getX() < this.min.getX() || pos.getZ() < this.min.getZ();
        boolean isBigger = pos.getX() > this.max.getX() || pos.getZ() > this.max.getZ();
        if (this.considerHeight) {
            if (pos.getY() < this.min.getY()) {
                isSmaller = true;
            } else if (pos.getY() > this.max.getY()) {
                isBigger = true;
            }
        }
        return !isSmaller && !isBigger;
    }

    @Override
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putLong("min", this.min.asLong());
        nbt.putLong("max", this.max.asLong());
        nbt.putBoolean("considerHeight", this.considerHeight);
        nbt.putBoolean("preventStartFlying", this.preventStartFlying);
        nbt.putBoolean("interruptFlying", this.interruptFlying);
        return nbt;
    }

    @Override
    public PacketByteBuf toPacketByteBuf(PacketByteBuf buf) {
        buf.writeLong(this.min.asLong());
        buf.writeLong(this.max.asLong());
        buf.writeBoolean(this.considerHeight);
        buf.writeBoolean(this.preventStartFlying);
        buf.writeBoolean(this.interruptFlying);
        return buf;
    }

    @Override
    public int blockCount() {
        return (int) BlockPos.stream(
                new BlockBox(
                        this.min.getX(), this.min.getY(), this.min.getZ(),
                        this.max.getX(), this.max.getY(), this.max.getZ()
                )
        ).count();
    }

    @Override
    public BlockPos center() {
        BlockPos centerOffset = BlockPos.ofFloored(
                (this.max.getX() - this.min.getX()) * 0.5,
                (this.max.getY() - this.min.getY()) * 0.5,
                (this.max.getZ() - this.min.getZ()) * 0.5
        );
        return this.min.add(centerOffset);
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
