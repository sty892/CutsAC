package ac.cust.custac.utils.item;

import ac.cust.custac.player.CustACPlayer;
import ac.cust.custac.utils.latency.CompensatedWorld;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;

public class LegacyItem extends ItemBehaviour {

    public static final LegacyItem INSTANCE = new LegacyItem();

    @Override
    public boolean canUse(ItemStack item, CompensatedWorld world, CustACPlayer player, InteractionHand hand) {
        return false; // move legacy code that is responsible for handling item use from PacketPlayerDigging here??
    }

}
