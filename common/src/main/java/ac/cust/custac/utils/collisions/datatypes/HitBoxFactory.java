package ac.cust.custac.utils.collisions.datatypes;

import ac.cust.custac.player.CustACPlayer;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.type.StateType;

public interface HitBoxFactory {
    CollisionBox fetch(CustACPlayer player, StateType heldItem, ClientVersion version, WrappedBlockState block, boolean isTargetBlock, int x, int y, int z);
}
