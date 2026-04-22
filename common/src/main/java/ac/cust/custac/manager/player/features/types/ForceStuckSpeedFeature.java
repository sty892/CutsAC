package ac.cust.custac.manager.player.features.types;

import ac.grim.grimac.api.config.ConfigManager;
import ac.grim.grimac.api.feature.FeatureState;
import ac.cust.custac.player.CustACPlayer;

public class ForceStuckSpeedFeature implements CustACFeature {

    @Override
    public String getName() {
        return "ForceStuckSpeed";
    }

    @Override
    public void setState(CustACPlayer player, ConfigManager config, FeatureState state) {
        switch (state) {
            case ENABLED -> player.setForceStuckSpeed(true);
            case DISABLED -> player.setForceStuckSpeed(false);
            default -> player.setForceStuckSpeed(isEnabledInConfig(player, config));
        }
    }

    @Override
    public boolean isEnabled(CustACPlayer player) {
        return player.isForceStuckSpeed();
    }

    @Override
    public boolean isEnabledInConfig(CustACPlayer player, ConfigManager config) {
        return config.getBooleanElse("force-stuck-speed", true);
    }

}
