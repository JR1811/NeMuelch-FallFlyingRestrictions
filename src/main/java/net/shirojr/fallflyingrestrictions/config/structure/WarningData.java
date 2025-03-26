package net.shirojr.fallflyingrestrictions.config.structure;

import net.minecraft.network.PacketByteBuf;

@SuppressWarnings({"FieldMayBeFinal"})
public class WarningData {
    private boolean badWeatherCondition;
    private boolean flyingTooHigh;
    private boolean blockedInventory;
    private boolean blockedEating;
    private boolean blockedItemUsage;
    private boolean zoneTakeOff;
    private boolean zoneFlying;

    public WarningData() {
        this.badWeatherCondition = true;
        this.blockedInventory = true;
        this.blockedEating = true;
        this.flyingTooHigh = true;
        this.zoneTakeOff = true;
        this.zoneFlying = true;
        this.blockedItemUsage = true;
    }

    public boolean badWeatherConditionWarning() {
        return badWeatherCondition;
    }

    public boolean enabledBlockedInventoryWarning() {
        return blockedInventory;
    }

    public boolean enabledEatingWhileFlyingWarning() {
        return blockedEating;
    }

    public boolean enabledFlyingTooHighWarning() {
        return flyingTooHigh;
    }

    public boolean enabledZoneTakeOffWarning() {
        return zoneTakeOff;
    }

    public boolean enabledZoneFlying() {
        return zoneFlying;
    }

    public boolean enabledBlockedItemUsage() {
        return blockedItemUsage;
    }


    public static WarningData fromPacketByteBuf(PacketByteBuf buf) {
        WarningData data = new WarningData();
        data.badWeatherCondition = buf.readBoolean();
        data.flyingTooHigh = buf.readBoolean();
        data.blockedInventory = buf.readBoolean();
        data.blockedEating = buf.readBoolean();
        data.zoneTakeOff = buf.readBoolean();
        data.zoneFlying = buf.readBoolean();
        data.blockedItemUsage = buf.readBoolean();
        return data;
    }

    public static void toPacketByteBuf(PacketByteBuf buf, WarningData data) {
        buf.writeBoolean(data.badWeatherCondition);
        buf.writeBoolean(data.flyingTooHigh);
        buf.writeBoolean(data.blockedInventory);
        buf.writeBoolean(data.blockedEating);
        buf.writeBoolean(data.zoneTakeOff);
        buf.writeBoolean(data.zoneFlying);
        buf.writeBoolean(data.blockedItemUsage);
    }
}
