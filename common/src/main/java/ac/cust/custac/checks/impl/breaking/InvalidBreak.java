package ac.cust.custac.checks.impl.breaking;

import ac.cust.custac.checks.Check;
import ac.cust.custac.checks.CheckData;
import ac.cust.custac.checks.type.BlockBreakCheck;
import ac.cust.custac.player.CustACPlayer;
import ac.cust.custac.utils.anticheat.update.BlockBreak;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;

@CheckData(name = "InvalidBreak", description = "Sent impossible block face id")
public class InvalidBreak extends Check implements BlockBreakCheck {
    public InvalidBreak(CustACPlayer player) {
        super(player);
    }

    @Override
    public void onBlockBreak(BlockBreak blockBreak) {
        if (blockBreak.faceId == 255 && blockBreak.action == DiggingAction.CANCELLED_DIGGING && player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_7_10)) {
            return;
        }

        if (blockBreak.faceId < 0 || blockBreak.faceId > 5) {
            // ban
            if (flagAndAlert("face=" + blockBreak.faceId + ", action=" + blockBreak.action) && shouldModifyPackets()) {
                blockBreak.cancel();
            }
        }
    }
}
