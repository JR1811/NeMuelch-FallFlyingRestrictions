package net.shirojr.fallflyingrestrictions.config.structure;

import net.minecraft.network.PacketByteBuf;

@SuppressWarnings({"FieldMayBeFinal"})
public class FeatureToggleData {
    private boolean movementChanges, flightHeightAboveGroundSafety, flyingTooHigh, roofAboveHeadSafety, inventoryBlock,
            eatingBlock, itemUsageBlock;

    public FeatureToggleData() {
        this.movementChanges = true;
        this.flightHeightAboveGroundSafety = true;
        this.flyingTooHigh = false;
        this.roofAboveHeadSafety = true;
        this.inventoryBlock = true;
        this.eatingBlock = false;
        this.itemUsageBlock = true;
    }

    public boolean enabledMovementChanges() {
        return movementChanges;
    }

    public boolean enabledSafeFlightHeight() {
        return flightHeightAboveGroundSafety;
    }

    public boolean enabledFlyingTooHigh() {
        return flyingTooHigh;
    }

    public boolean enabledRoofAboveHeadSafety() {
        return roofAboveHeadSafety;
    }

    public boolean enabledInventoryBlock() {
        return inventoryBlock;
    }

    public boolean enabledEatingWhileFlyingBlock() {
        return eatingBlock;
    }

    public boolean enabledItemUsageBlock() {
        return itemUsageBlock;
    }

    public static FeatureToggleData fromPacketByteBuf(PacketByteBuf buf) {
        FeatureToggleData data = new FeatureToggleData();
        data.movementChanges = buf.readBoolean();
        data.flightHeightAboveGroundSafety = buf.readBoolean();
        data.flyingTooHigh = buf.readBoolean();
        data.roofAboveHeadSafety = buf.readBoolean();
        data.inventoryBlock = buf.readBoolean();
        data.eatingBlock = buf.readBoolean();
        data.itemUsageBlock = buf.readBoolean();
        return data;
    }

    public static void toPacketByteBuf(PacketByteBuf buf, FeatureToggleData data) {
        buf.writeBoolean(data.movementChanges);
        buf.writeBoolean(data.flightHeightAboveGroundSafety);
        buf.writeBoolean(data.flyingTooHigh);
        buf.writeBoolean(data.roofAboveHeadSafety);
        buf.writeBoolean(data.inventoryBlock);
        buf.writeBoolean(data.eatingBlock);
        buf.writeBoolean(data.itemUsageBlock);
    }
}
