package ac.cust.custac.predictionengine.predictions.rideable;

import ac.cust.custac.player.CustACPlayer;
import ac.cust.custac.predictionengine.predictions.PredictionEngineNormal;
import ac.cust.custac.utils.data.VectorData;
import ac.cust.custac.utils.math.CustACMath;
import ac.cust.custac.utils.math.Vector3dm;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class PredictionEngineRideableNormal extends PredictionEngineNormal {
    private final Vector3dm movementVector;

    @Override
    public void addJumpsToPossibilities(CustACPlayer player, Set<VectorData> existingVelocities) {
        PredictionEngineRideableUtils.handleJumps(player, existingVelocities);
    }

    @Override
    public List<VectorData> applyInputsToVelocityPossibilities(CustACPlayer player, Set<VectorData> possibleVectors, float speed) {
        return PredictionEngineRideableUtils.applyInputsToVelocityPossibilities(this, movementVector, player, possibleVectors, speed);
    }

    @Override
    public Vector3dm getMovementResultFromInput(CustACPlayer player, Vector3dm inputVector, float flyingSpeed, float yRot) {
        float yRotRadians = CustACMath.radians(yRot);
        float sin = player.trigHandler.sin(yRotRadians);
        float cos = player.trigHandler.cos(yRotRadians);

        double xResult = inputVector.getX() * cos - inputVector.getZ() * sin;
        double zResult = inputVector.getZ() * cos + inputVector.getX() * sin;

        return new Vector3dm(xResult * flyingSpeed, inputVector.getY() * flyingSpeed, zResult * flyingSpeed);
    }
}
