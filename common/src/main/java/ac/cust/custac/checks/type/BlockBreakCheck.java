package ac.cust.custac.checks.type;

import ac.cust.custac.utils.anticheat.update.BlockBreak;

public interface BlockBreakCheck extends PostPredictionCheck {
    default void onBlockBreak(final BlockBreak blockBreak) {
    }

    default void onPostFlyingBlockBreak(final BlockBreak blockBreak) {
    }
}
