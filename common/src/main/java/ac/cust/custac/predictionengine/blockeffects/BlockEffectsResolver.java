package ac.cust.custac.predictionengine.blockeffects;

import ac.cust.custac.player.CustACPlayer;

import java.util.List;

public interface BlockEffectsResolver {

    void applyEffectsFromBlocks(CustACPlayer player, List<CustACPlayer.Movement> movements);

}
