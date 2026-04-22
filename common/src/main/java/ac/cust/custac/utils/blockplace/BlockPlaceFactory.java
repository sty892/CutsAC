package ac.cust.custac.utils.blockplace;

import ac.cust.custac.player.CustACPlayer;
import ac.cust.custac.utils.anticheat.update.BlockPlace;

public interface BlockPlaceFactory {
    void applyBlockPlaceToWorld(CustACPlayer player, BlockPlace place);
}
