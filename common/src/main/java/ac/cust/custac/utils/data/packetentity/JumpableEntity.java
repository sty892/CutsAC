package ac.cust.custac.utils.data.packetentity;

import ac.cust.custac.player.CustACPlayer;
import ac.cust.custac.utils.data.VectorData;

import java.util.Set;

public interface JumpableEntity {

    boolean isJumping();

    void setJumping(boolean jumping);

    float getJumpPower();

    void setJumpPower(float jumpPower);

    boolean canPlayerJump(CustACPlayer player);

    boolean hasSaddle();

    void executeJump(CustACPlayer player, Set<VectorData> possibleVectors);

}
