package ac.cust.custac.platform.api;

import ac.grim.grimac.api.plugin.GrimPlugin;
import ac.cust.custac.platform.api.command.CommandService;
import ac.cust.custac.platform.api.manager.ItemResetHandler;
import ac.cust.custac.platform.api.manager.MessagePlaceHolderManager;
import ac.cust.custac.platform.api.manager.PermissionRegistrationManager;
import ac.cust.custac.platform.api.manager.PlatformPluginManager;
import ac.cust.custac.platform.api.player.PlatformPlayerFactory;
import ac.cust.custac.platform.api.scheduler.PlatformScheduler;
import ac.cust.custac.platform.api.sender.SenderFactory;
import com.github.retrooper.packetevents.PacketEventsAPI;
import org.jetbrains.annotations.NotNull;

public interface PlatformLoader {
    PlatformScheduler getScheduler();

    PlatformPlayerFactory getPlatformPlayerFactory();

    PacketEventsAPI<?> getPacketEvents();

    ItemResetHandler getItemResetHandler();

    CommandService getCommandService();

    SenderFactory<?> getSenderFactory();

    GrimPlugin getPlugin();

    PlatformPluginManager getPluginManager();

    PlatformServer getPlatformServer();

    // Intended for use for platform specific service/API bringup
    // Method will be called when InitManager.load() is called
    void registerAPIService();

    // Used to replace text placeholders in messages
    // Currently only supports PlaceHolderAPI on Bukkit
    @NotNull
    MessagePlaceHolderManager getMessagePlaceHolderManager();

    PermissionRegistrationManager getPermissionManager();
}
