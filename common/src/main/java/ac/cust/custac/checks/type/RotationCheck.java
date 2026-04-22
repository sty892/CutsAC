package ac.cust.custac.checks.type;

import ac.grim.grimac.api.AbstractCheck;
import ac.cust.custac.utils.anticheat.update.RotationUpdate;

public interface RotationCheck extends AbstractCheck {

    default void process(final RotationUpdate rotationUpdate) {
    }
}
