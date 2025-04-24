package net.shirojr.fallflyingrestrictions.config.structure;

import net.minecraft.network.PacketByteBuf;

@SuppressWarnings({"FieldMayBeFinal"})
public class GlobalZoneRestrictionData {
    private boolean preventStartFlying, interruptFlying;

    public GlobalZoneRestrictionData() {
        this.preventStartFlying = false;
        this.interruptFlying = false;
    }

    public boolean preventStartFlying() {
        return preventStartFlying;
    }

    public boolean interruptFlying() {
        return interruptFlying;
    }

    public static GlobalZoneRestrictionData fromPacketByteBuf(PacketByteBuf buf) {
        GlobalZoneRestrictionData data = new GlobalZoneRestrictionData();
        data.preventStartFlying = buf.readBoolean();
        data.interruptFlying = buf.readBoolean();
        return data;
    }

    public static void toPacketByteBuf(PacketByteBuf buf, GlobalZoneRestrictionData data) {
        buf.writeBoolean(data.preventStartFlying);
        buf.writeBoolean(data.interruptFlying);
    }
}
