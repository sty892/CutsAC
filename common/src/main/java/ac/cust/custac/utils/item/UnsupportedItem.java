package ac.cust.custac.utils.item;

import ac.cust.custac.player.CustACPlayer;
import ac.cust.custac.utils.latency.CompensatedWorld;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;

public class UnsupportedItem extends ItemBehaviour {

    public static final UnsupportedItem INSTANCE = new UnsupportedItem();

    @Override
    public boolean canUse(ItemStack item, CompensatedWorld world, CustACPlayer player, InteractionHand hand) {
        return false;
    }

}
