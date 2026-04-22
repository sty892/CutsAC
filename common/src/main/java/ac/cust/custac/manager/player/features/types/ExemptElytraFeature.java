package ac.cust.custac.manager.player.features.types;

import ac.grim.grimac.api.config.ConfigManager;
import ac.grim.grimac.api.feature.FeatureState;
import ac.cust.custac.player.CustACPlayer;

public class ExemptElytraFeature implements CustACFeature {

    @Override
    public String getName() {
        return "ExemptElytra";
    }

    @Override
    public void setState(CustACPlayer player, ConfigManager config, FeatureState state) {
        switch (state) {
            case ENABLED -> player.setExemptElytra(true);
            case DISABLED -> player.setExemptElytra(false);
            default -> player.setExemptElytra(isEnabledInConfig(player, config));
        }
    }

    @Override
    public boolean isEnabled(CustACPlayer player) {
        return player.isExemptElytra();
    }

    @Override
    public boolean isEnabledInConfig(CustACPlayer player, ConfigManager config) {
        return config.getBooleanElse("exempt-elytra", false);
    }

}
