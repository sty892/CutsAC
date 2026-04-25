package ac.cust.custac.platform.bukkit.suspect;

import ac.cust.custac.CustACAPI;
import ac.cust.custac.manager.suspect.SuspectFlag;
import ac.cust.custac.manager.suspect.SuspectProfile;
import ac.cust.custac.manager.suspect.SuspectUiHandler;
import ac.cust.custac.platform.api.sender.Sender;
import ac.cust.custac.platform.bukkit.utils.convert.BukkitConversionUtils;
import ac.cust.custac.utils.math.Location;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class BukkitSuspectMenu implements Listener, SuspectUiHandler {
    private static final String TITLE = "CustAC Suspects";

    @Override
    public boolean openSuspects(Sender sender) {
        if (!(sender.getNativeSender() instanceof Player viewer)) {
            return false;
        }

        List<SuspectProfile> suspects = CustACAPI.INSTANCE.getSuspectManager().getRankedSuspects();
        if (suspects.isEmpty()) {
            return false;
        }
        int size = Math.max(9, Math.min(54, ((Math.max(1, suspects.size()) + 8) / 9) * 9));
        SuspectMenuHolder holder = new SuspectMenuHolder();
        Inventory inventory = Bukkit.createInventory(holder, size, TITLE);
        holder.inventory = inventory;

        for (int slot = 0; slot < Math.min(size, suspects.size()); slot++) {
            SuspectProfile profile = suspects.get(slot);
            inventory.setItem(slot, createHead(profile));
            holder.slots.put(slot, profile.getUuid());
        }

        viewer.openInventory(inventory);
        return true;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof SuspectMenuHolder holder)) {
            return;
        }
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player viewer)) {
            return;
        }
        if (event.getClickedInventory() == null || event.getClickedInventory() != event.getInventory()) {
            return;
        }
        UUID uuid = holder.slots.get(event.getSlot());
        if (uuid == null) {
            return;
        }

        if (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.SHIFT_LEFT) {
            teleportToSuspect(viewer, uuid);
        }
    }

    private ItemStack createHead(SuspectProfile profile) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta rawMeta = item.getItemMeta();
        if (rawMeta instanceof SkullMeta skullMeta) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(profile.getUuid());
            skullMeta.setOwningPlayer(offlinePlayer);
            skullMeta.setDisplayName(displayName(profile));
            skullMeta.setLore(createLore(profile));
            item.setItemMeta(skullMeta);
        } else if (rawMeta != null) {
            rawMeta.setDisplayName(displayName(profile));
            rawMeta.setLore(createLore(profile));
            item.setItemMeta(rawMeta);
        }
        return item;
    }

    private String displayName(SuspectProfile profile) {
        return ChatColor.AQUA + profile.getPlayerName() + " " + ChatColor.GRAY + "(" + profile.getFlagCount() + " flags)";
    }

    private List<String> createLore(SuspectProfile profile) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Last 5 flags:");
        List<SuspectFlag> flags = profile.getLastFlags(5);
        if (flags.isEmpty()) {
            lore.add(ChatColor.DARK_GRAY + "No flags");
        } else {
            for (int i = flags.size() - 1; i >= 0; i--) {
                SuspectFlag flag = flags.get(i);
                lore.add(ChatColor.YELLOW + "#" + flag.id() + " " + ChatColor.WHITE + flag.checkName() + " " + ChatColor.GRAY + "vl=" + flag.violationLevel());
                if (!flag.verbose().isBlank()) {
                    lore.add(ChatColor.DARK_GRAY + trim(flag.verbose(), 42));
                }
            }
        }
        lore.add("");
        lore.add(ChatColor.GREEN + "Left click: teleport");
        return lore;
    }

    private void teleportToSuspect(Player viewer, UUID uuid) {
        Player online = Bukkit.getPlayer(uuid);
        if (online != null && online.isOnline()) {
            viewer.teleport(online.getLocation());
            viewer.sendMessage(ChatColor.GREEN + "Teleported to online suspect " + online.getName() + ".");
            return;
        }

        SuspectProfile profile = CustACAPI.INSTANCE.getSuspectManager().getProfile(uuid);
        Location location = profile == null ? null : profile.getLastKnownLocation();
        if (location == null || location.getWorld() == null || !location.getWorld().isLoaded()) {
            viewer.sendMessage(ChatColor.RED + "No loaded logout/flag location is available for this suspect.");
            return;
        }

        viewer.teleport(BukkitConversionUtils.toBukkitLocation(location));
        viewer.sendMessage(ChatColor.GREEN + "Teleported to last recorded suspect location.");
    }

    private String trim(String value, int maxLength) {
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength - 3) + "...";
    }

    private static final class SuspectMenuHolder implements InventoryHolder {
        private final Map<Integer, UUID> slots = new HashMap<>();
        private Inventory inventory;

        @Override
        public @NotNull Inventory getInventory() {
            return inventory;
        }
    }
}
