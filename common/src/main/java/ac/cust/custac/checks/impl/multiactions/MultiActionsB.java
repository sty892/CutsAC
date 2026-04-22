package ac.cust.custac.checks.impl.multiactions;

import ac.cust.custac.checks.Check;
import ac.cust.custac.checks.CheckData;
import ac.cust.custac.checks.type.BlockBreakCheck;
import ac.cust.custac.player.CustACPlayer;
import ac.cust.custac.utils.anticheat.update.BlockBreak;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;

@CheckData(name = "MultiActionsB", description = "Breaking blocks while using an item", experimental = true)
public class MultiActionsB extends Check implements BlockBreakCheck {
    public MultiActionsB(CustACPlayer player) {
        super(player);
    }

    @Override
    public void onBlockBreak(BlockBreak blockBreak) {
        if (player.packetStateData.isSlowedByUsingItem() && (player.packetStateData.lastSlotSelected == player.packetStateData.getSlowedByUsingItemSlot() || player.packetStateData.itemInUseHand == InteractionHand.OFF_HAND)) {
            // this is vanilla on 1.7
            if (player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_7_10)) {
                return;
            }

            if (flagAndAlert() && shouldModifyPackets()) {
                blockBreak.cancel();
            }
        }
    }
}
