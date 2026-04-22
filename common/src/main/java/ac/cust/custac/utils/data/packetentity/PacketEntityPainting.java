package ac.cust.custac.utils.data.packetentity;

import ac.cust.custac.player.CustACPlayer;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.world.Direction;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PacketEntityPainting extends PacketEntity {

    private final Direction direction;

    public PacketEntityPainting(CustACPlayer player, UUID uuid, double x, double y, double z, Direction direction) {
        super(player, uuid, EntityTypes.PAINTING, x, y, z);
        this.direction = direction;
    }
}
