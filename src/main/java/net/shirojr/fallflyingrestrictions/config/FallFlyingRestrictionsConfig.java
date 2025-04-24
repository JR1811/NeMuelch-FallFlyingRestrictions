package net.shirojr.fallflyingrestrictions.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.shirojr.fallflyingrestrictions.config.structure.FeatureToggleData;
import net.shirojr.fallflyingrestrictions.config.structure.FlyingBlockHeightData;
import net.shirojr.fallflyingrestrictions.config.structure.GlobalZoneRestrictionData;
import net.shirojr.fallflyingrestrictions.config.structure.WarningData;

@Config(name = "fallflyingrestrictions")
public class FallFlyingRestrictionsConfig implements ConfigData {
    public WarningData displayWarning = new WarningData();
    public FeatureToggleData toggleFeatures = new FeatureToggleData();
    @Comment("Restriction Values will only be considered if their corresponding features are enabled")
    public FlyingBlockHeightData restrictionValues = new FlyingBlockHeightData();
    @Comment("Global settings are overruled by manually specified local zones")
    public GlobalZoneRestrictionData globalZoneRestrictions = new GlobalZoneRestrictionData();
}
