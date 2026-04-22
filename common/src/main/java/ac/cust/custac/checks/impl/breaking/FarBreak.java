package ac.cust.custac.checks.impl.breaking;

import ac.cust.custac.checks.Check;
import ac.cust.custac.checks.CheckData;
import ac.cust.custac.checks.type.BlockBreakCheck;
import ac.cust.custac.player.CustACPlayer;
import ac.cust.custac.utils.anticheat.update.BlockBreak;
import ac.cust.custac.utils.collisions.datatypes.SimpleCollisionBox;
import ac.cust.custac.utils.math.Vector3dm;
import ac.cust.custac.utils.math.VectorUtils;
import com.github.retrooper.packetevents.protocol.attribute.Attributes;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;

@CheckData(name = "FarBreak", description = "Breaking blocks too far away", experimental = true)
public class FarBreak extends Check implements BlockBreakCheck {
    public FarBreak(CustACPlayer player) {
        super(player);
    }

    @Override
    public void onBlockBreak(BlockBreak blockBreak) {
        if (!player.cameraEntity.isSelf() || player.inVehicle() || blockBreak.action == DiggingAction.CANCELLED_DIGGING)
            return; // falses

        double min = Double.MAX_VALUE;
        for (double d : player.getPossibleEyeHeights()) {
            SimpleCollisionBox box = new SimpleCollisionBox(blockBreak.position);
            Vector3dm best = VectorUtils.cutBoxToVector(player.x, player.y + d, player.z, box);
            min = Math.min(min, best.distanceSquared(player.x, player.y + d, player.z));
        }

        // getPickRange() determines this?
        // With 1.20.5+ the new attribute determines creative mode reach using a modifier
        double maxReach = player.compensatedEntities.self.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
        if (player.packetStateData.didLastMovementIncludePosition || player.canSkipTicks()) {
            double threshold = player.getMovementThreshold();
            maxReach += Math.hypot(threshold, threshold);
        }

        if (min > maxReach * maxReach && flagAndAlert(String.format("distance=%.2f", Math.sqrt(min))) && shouldModifyPackets()) {
            blockBreak.cancel();
        }
    }
}
