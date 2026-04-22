package ac.cust.custac.events.packets;

import ac.cust.custac.CustACAPI;
import ac.cust.custac.platform.api.player.PlatformPlayer;
import ac.cust.custac.utils.anticheat.LogUtil;
import com.github.retrooper.packetevents.event.*;
import com.github.retrooper.packetevents.netty.channel.ChannelHelper;
import com.github.retrooper.packetevents.protocol.ConnectionState;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class PacketPlayerJoinQuit extends PacketListenerAbstract {

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Login.Server.LOGIN_SUCCESS) {
            // Do this after send to avoid sending packets before the PLAY state
            event.getTasksAfterSend().add(() -> CustACAPI.INSTANCE.getPlayerDataManager().addUser(event.getUser()));
        }
    }

    @Override
    public void onUserConnect(UserConnectEvent event) {
        // Player connected too soon, perhaps late bind is off
        // Don't kick everyone on reload
        if (event.getUser().getConnectionState() == ConnectionState.PLAY && !CustACAPI.INSTANCE.getPlayerDataManager().exemptUsers.contains(event.getUser())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onUserLogin(UserLoginEvent event) {
        Object nativePlayerObject = Objects.requireNonNull(event.getPlayer());

        // This will never throw a NPE because code is run in OnUserConnect -> onPacketSend -> OnUserLogin order
        // And the user will be added to the map before the getPlayer() method call
        @NotNull PlatformPlayer platformPlayer = CustACAPI.INSTANCE.getPlatformPlayerFactory().getFromNativePlayerType(nativePlayerObject);

        if (CustACAPI.INSTANCE.getConfigManager().getConfig().getBooleanElse("debug-pipeline-on-join", false)) {
            LogUtil.info("Pipeline: " + ChannelHelper.pipelineHandlerNamesAsString(event.getUser().getChannel()));
        }
        if (platformPlayer.hasPermission("custac.alerts.enable-on-join") && platformPlayer.hasPermission("custac.alerts")) {
            CustACAPI.INSTANCE.getAlertManager().toggleAlerts(platformPlayer, platformPlayer.hasPermission("custac.alerts.enable-on-join.silent"));
        }
        if (platformPlayer.hasPermission("custac.verbose.enable-on-join") && platformPlayer.hasPermission("custac.verbose")) {
            CustACAPI.INSTANCE.getAlertManager().toggleVerbose(platformPlayer, platformPlayer.hasPermission("custac.verbose.enable-on-join.silent"));
        }
        if (platformPlayer.hasPermission("custac.brand.enable-on-join") && platformPlayer.hasPermission("custac.brand")) {
            CustACAPI.INSTANCE.getAlertManager().toggleBrands(platformPlayer, platformPlayer.hasPermission("custac.brand.enable-on-join.silent"));
        }
        if (platformPlayer.hasPermission("custac.spectate") && CustACAPI.INSTANCE.getConfigManager().getConfig().getBooleanElse("spectators.hide-regardless", false)) {
            CustACAPI.INSTANCE.getSpectateManager().onLogin(platformPlayer.getUniqueId());
        }
    }

    @Override
    public void onUserDisconnect(UserDisconnectEvent event) {
        CustACAPI.INSTANCE.getPlayerDataManager().onDisconnect(event.getUser());
    }
}
