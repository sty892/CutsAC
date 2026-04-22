package ac.cust.custac.utils.collisions.blocks;

import ac.cust.custac.player.CustACPlayer;
import ac.cust.custac.utils.collisions.datatypes.CollisionBox;
import ac.cust.custac.utils.collisions.datatypes.CollisionFactory;
import ac.cust.custac.utils.collisions.datatypes.NoCollisionBox;
import ac.cust.custac.utils.collisions.datatypes.SimpleCollisionBox;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.enums.Half;

public class TrapDoorHandler implements CollisionFactory {
    @Override
    public CollisionBox fetch(CustACPlayer player, ClientVersion version, WrappedBlockState block, int x, int y, int z) {
        double var2 = 0.1875;

        if (block.isOpen()) {
            switch (block.getFacing()) {
                case SOUTH:
                    return new SimpleCollisionBox(0.0, 0.0, 0.0, 1.0, 1.0, var2, false);
                case NORTH:
                    return new SimpleCollisionBox(0.0, 0.0, 1.0 - var2, 1.0, 1.0, 1.0, false);
                case EAST:
                    return new SimpleCollisionBox(0.0, 0.0, 0.0, var2, 1.0, 1.0, false);
                case WEST:
                    return new SimpleCollisionBox(1.0 - var2, 0.0, 0.0, 1.0, 1.0, 1.0, false);
            }
        } else {
            if (block.getHalf() == Half.BOTTOM) {
                return new SimpleCollisionBox(0.0, 0.0, 0.0, 1.0, var2, 1.0, false);
            } else {
                return new SimpleCollisionBox(0.0, 1.0 - var2, 0.0, 1.0, 1.0, 1.0, false);

            }
        }

        return NoCollisionBox.INSTANCE;
    }
}
