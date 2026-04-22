package ac.cust.custac.platform.bukkit;

import ac.cust.custac.CustACAPI;
import ac.cust.custac.platform.api.Platform;
import ac.cust.custac.platform.api.PlatformServer;
import ac.cust.custac.platform.api.sender.Sender;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;



public class BukkitPlatformServer implements PlatformServer {

    @Override
    public String getPlatformImplementationString() {
        return Bukkit.getVersion();
    }

    @Override
    public void dispatchCommand(Sender sender, String command) {
        CommandSender commandSender = CustACBukkitLoaderPlugin.LOADER.getBukkitSenderFactory().reverse(sender);
        Bukkit.dispatchCommand(commandSender, command);
    }

    @Override
    public Sender getConsoleSender() {
        return CustACBukkitLoaderPlugin.LOADER.getBukkitSenderFactory().map(Bukkit.getConsoleSender());
    }

    @Override
    public void registerOutgoingPluginChannel(String name) {
        CustACBukkitLoaderPlugin.LOADER.getServer().getMessenger().registerOutgoingPluginChannel(CustACBukkitLoaderPlugin.LOADER, name);
    }

    @Override
    public double getTPS() {
        // Folia throws UnsupportedOperationException on calling getTPS(), there is no API for getting TPS on Folia
        if (CustACAPI.INSTANCE.getPlatform() == Platform.FOLIA) {
            return Double.NaN;
        }
        return SpigotReflectionUtil.getTPS();
    }
}
