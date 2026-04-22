package ac.cust.custac.manager.player.features;

import ac.cust.custac.CustACAPI;
import ac.grim.grimac.api.config.ConfigManager;
import ac.grim.grimac.api.feature.FeatureManager;
import ac.grim.grimac.api.feature.FeatureState;
import ac.cust.custac.manager.player.features.types.*;
import ac.cust.custac.player.CustACPlayer;
import ac.cust.custac.utils.common.ConfigReloadObserver;
import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FeatureManagerImpl implements FeatureManager, ConfigReloadObserver {

    private static final Map<String, CustACFeature> FEATURES;

    /// @deprecated use {@link #getFeatures()}
    @Contract(pure = true)
    @Deprecated
    public static Map<String, CustACFeature> getFEATURES() {
        return getFeatures();
    }

    @Contract(pure = true)
    public static Map<String, CustACFeature> getFeatures() {
        return FEATURES;
    }

    static {
        FeatureBuilder builder = new FeatureBuilder();
        builder.register(new ExperimentalChecksFeature());
        builder.register(new ExemptElytraFeature());
        builder.register(new ForceStuckSpeedFeature());
        builder.register(new ForceSlowMovementFeature());
        FEATURES = builder.buildMap();
    }

    private final Map<String, FeatureState> states = new HashMap<>();

    private final CustACPlayer player;

    public FeatureManagerImpl(CustACPlayer player) {
        this.player = player;
        for (CustACFeature value : FEATURES.values()) states.put(value.getName(), FeatureState.UNSET);
    }

    @Override
    public Collection<String> getFeatureKeys() {
        return ImmutableSet.copyOf(FEATURES.keySet());
    }

    @Override
    public @Nullable FeatureState getFeatureState(String key) {
        return states.get(key);
    }

    @Override
    public boolean isFeatureEnabled(String key) {
        CustACFeature feature = FEATURES.get(key);
        if (feature == null) return false;
        return feature.isEnabled(player);
    }

    @Override
    public boolean setFeatureState(String key, FeatureState tristate) {
        CustACFeature feature = FEATURES.get(key);
        if (feature == null) return false;
        states.put(key, tristate);
        return true;
    }

    @Override
    public void reload() {
        onReload(CustACAPI.INSTANCE.getExternalAPI().getConfigManager());
    }

    @Override
    public void onReload(ConfigManager config) {
        for (Map.Entry<String, FeatureState> entry : states.entrySet()) {
            String key = entry.getKey();
            FeatureState state = entry.getValue();
            CustACFeature feature = FEATURES.get(key);
            if (feature == null) continue;
            feature.setState(player, config, state);
        }
    }

}
