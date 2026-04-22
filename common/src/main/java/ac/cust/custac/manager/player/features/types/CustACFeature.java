package ac.cust.custac.manager.player.features.types;

import ac.grim.grimac.api.config.ConfigManager;
import ac.grim.grimac.api.feature.FeatureState;
import ac.cust.custac.player.CustACPlayer;

public interface CustACFeature {
    String getName();

    void setState(CustACPlayer player, ConfigManager config, FeatureState state);

    boolean isEnabled(CustACPlayer player);

    boolean isEnabledInConfig(CustACPlayer player, ConfigManager config);
}
