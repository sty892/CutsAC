package ac.cust.custac.manager.player.features.types;

import ac.grim.grimac.api.config.ConfigManager;
import ac.grim.grimac.api.feature.FeatureState;
import ac.cust.custac.player.CustACPlayer;

public class ExperimentalChecksFeature implements CustACFeature {

    @Override
    public String getName() {
        return "ExperimentalChecks";
    }

    @Override
    public void setState(CustACPlayer player, ConfigManager config, FeatureState state) {
        switch (state) {
            case ENABLED -> player.setExperimentalChecks(true);
            case DISABLED -> player.setExperimentalChecks(false);
            default -> player.setExperimentalChecks(isEnabledInConfig(player, config));
        }
    }

    @Override
    public boolean isEnabled(CustACPlayer player) {
        return player.isExperimentalChecks();
    }

    @Override
    public boolean isEnabledInConfig(CustACPlayer player, ConfigManager config) {
        return config.getBooleanElse("experimental-checks", false);
    }

}
