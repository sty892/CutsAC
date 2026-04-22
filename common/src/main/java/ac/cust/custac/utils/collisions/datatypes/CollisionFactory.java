package ac.cust.custac.utils.collisions.datatypes;

import ac.cust.custac.player.CustACPlayer;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;

public interface CollisionFactory {
    CollisionBox fetch(CustACPlayer player, ClientVersion version, WrappedBlockState block, int x, int y, int z);
}
