package ac.cust.custac.platform.bukkit.utils.placeholder;

import ac.cust.custac.CustACAPI;
import ac.grim.grimac.api.GrimUser;
import ac.cust.custac.player.CustACPlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class PlaceholderAPIExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "custac";
    }

    public @NotNull String getAuthor() {
        return String.join(", ", CustACAPI.INSTANCE.getGrimPlugin().getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return CustACAPI.INSTANCE.getExternalAPI().getCustACVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull List<String> getPlaceholders() {
        Set<String> staticReplacements = CustACAPI.INSTANCE.getExternalAPI().getStaticReplacements().keySet();
        Set<String> variableReplacements = CustACAPI.INSTANCE.getExternalAPI().getVariableReplacements().keySet();
        ArrayList<String> placeholders = new ArrayList<>(staticReplacements.size() + variableReplacements.size());
        for (String s : staticReplacements) {
            placeholders.add(s.equals("%custac_version%") ? s : "%custac_" + s.replaceAll("%", "") + "%");
        }
        for (String s : variableReplacements) {
            placeholders.add(s.equals("%player%") ? "%custac_player%" : "%custac_player_" + s.replaceAll("%", "") + "%");
        }
        return placeholders;
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        for (Map.Entry<String, String> entry : CustACAPI.INSTANCE.getExternalAPI().getStaticReplacements().entrySet()) {
            String key = entry.getKey().equals("%custac_version%")
                    ? "version"
                    : entry.getKey().replaceAll("%", "");
            if (params.equalsIgnoreCase(key)) {
                return entry.getValue();
            }
        }

        if (offlinePlayer instanceof Player player) {
            CustACPlayer custacPlayer = CustACAPI.INSTANCE.getPlayerDataManager().getPlayer(player.getUniqueId());
            if (custacPlayer == null) return null;

            for (Map.Entry<String, Function<GrimUser, String>> entry : CustACAPI.INSTANCE.getExternalAPI().getVariableReplacements().entrySet()) {
                String key = entry.getKey().equals("%player%")
                        ? "player"
                        : "player_" + entry.getKey().replaceAll("%", "");
                if (params.equalsIgnoreCase(key)) {
                    return entry.getValue().apply(custacPlayer);
                }
            }
        }

        return null;
    }
}
