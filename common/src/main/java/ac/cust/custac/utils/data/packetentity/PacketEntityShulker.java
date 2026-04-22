package ac.cust.custac.utils.data.packetentity;

import ac.cust.custac.player.CustACPlayer;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.world.BlockFace;

import java.util.UUID;

public class PacketEntityShulker extends PacketEntity {
    public BlockFace facing = BlockFace.DOWN;

    public PacketEntityShulker(CustACPlayer player, UUID uuid, EntityType type, double x, double y, double z) {
        super(player, uuid, type, x, y, z);
    }
}
