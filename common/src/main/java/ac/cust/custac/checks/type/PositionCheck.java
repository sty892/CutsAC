package ac.cust.custac.checks.type;

import ac.grim.grimac.api.AbstractCheck;
import ac.cust.custac.utils.anticheat.update.PositionUpdate;

public interface PositionCheck extends AbstractCheck {

    default void onPositionUpdate(final PositionUpdate positionUpdate) {
    }
}
