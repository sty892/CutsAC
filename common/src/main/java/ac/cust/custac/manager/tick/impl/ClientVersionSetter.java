package ac.cust.custac.manager.tick.impl;

import ac.cust.custac.CustACAPI;
import ac.cust.custac.manager.tick.Tickable;
import ac.cust.custac.player.CustACPlayer;
import com.github.retrooper.packetevents.netty.channel.ChannelHelper;

public class ClientVersionSetter implements Tickable {
    @Override
    public void tick() {
        for (CustACPlayer player : CustACAPI.INSTANCE.getPlayerDataManager().getEntries()) {
            // channel was somehow closed without us getting a disconnect event
            if (!ChannelHelper.isOpen(player.user.getChannel())) {
                CustACAPI.INSTANCE.getPlayerDataManager().onDisconnect(player.user);
                continue;
            }

            player.pollData();
        }
    }
}
