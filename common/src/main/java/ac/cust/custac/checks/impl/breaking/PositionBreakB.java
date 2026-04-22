package ac.cust.custac.checks.impl.breaking;

import ac.cust.custac.checks.Check;
import ac.cust.custac.checks.CheckData;
import ac.cust.custac.checks.type.BlockBreakCheck;
import ac.cust.custac.player.CustACPlayer;
import ac.cust.custac.utils.anticheat.update.BlockBreak;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.protocol.world.BlockFace;

@CheckData(name = "PositionBreakB")
public class PositionBreakB extends Check implements BlockBreakCheck {
    private final int releaseFace = player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_8) ? 0 : 255;
    private BlockFace lastFace;

    public PositionBreakB(CustACPlayer player) {
        super(player);
    }

    @Override
    public void onBlockBreak(BlockBreak blockBreak) {
        if (blockBreak.action == DiggingAction.START_DIGGING) {
            if (blockBreak.face == lastFace) {
                lastFace = null;
            }
        }

        if (lastFace != null) {
            flagAndAlert("lastFace=" + lastFace + ", action=" + blockBreak.action);
        }

        if (blockBreak.action == DiggingAction.CANCELLED_DIGGING) {
            lastFace = blockBreak.faceId == releaseFace ? null : blockBreak.face;
        }
    }
}
