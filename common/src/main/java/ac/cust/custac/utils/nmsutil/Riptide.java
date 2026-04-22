package ac.cust.custac.utils.nmsutil;

import ac.cust.custac.player.CustACPlayer;
import ac.cust.custac.utils.math.CustACMath;
import ac.cust.custac.utils.math.Vector3dm;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class Riptide {
    @Contract("_ -> new")
    public static @NotNull Vector3dm getRiptideVelocity(@NotNull CustACPlayer player) {
        ItemStack main = player.inventory.getHeldItem();
        ItemStack off = player.inventory.getOffHand();

        final int riptideLevel;
        if (main.getType() == ItemTypes.TRIDENT) {
            riptideLevel = main.getEnchantmentLevel(EnchantmentTypes.RIPTIDE);
        } else if (off.getType() == ItemTypes.TRIDENT) {
            riptideLevel = off.getEnchantmentLevel(EnchantmentTypes.RIPTIDE);
        } else {
            return new Vector3dm(); // Can't riptide
        }

        float yaw = CustACMath.radians(player.yaw);
        float pitch = CustACMath.radians(player.pitch);
        float pitchCos = player.trigHandler.cos(pitch);
        float x = -player.trigHandler.sin(yaw) * pitchCos;
        float y = -player.trigHandler.sin(pitch);
        float z = player.trigHandler.cos(yaw) * pitchCos;
        float multiplier = (3f * ((1f + riptideLevel) / 4f)) / ((float) Math.sqrt(x * x + y * y + z * z));

        // If the player collided vertically with the 1.199999F pushing movement, then the Y additional movement was added
        // (We switched the order around as our prediction engine isn't designed for the proper implementation)
        return new Vector3dm(x * multiplier, player.verticalCollision ? 0 : y * multiplier, z * multiplier);
    }
}
