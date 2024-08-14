package net.shirojr.fallflyingrestrictions.config.structure;
@SuppressWarnings({"FieldMayBeFinal"})
public class FeatureToggleData {
    private boolean movementChanges, flightHeightSafety, roofAboveHeadSafety, inventoryBlock, eatingBlock;

    public FeatureToggleData() {
        this.movementChanges = true;
        this.flightHeightSafety = true;
        this.roofAboveHeadSafety = true;
        this.inventoryBlock = true;
        this.eatingBlock = true;
    }

    public boolean enabledMovementChanges() {
        return movementChanges;
    }

    public boolean enabledSafeFlightHeight() {
        return flightHeightSafety;
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
}
