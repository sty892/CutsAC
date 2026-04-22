package ac.cust.custac.manager.violationdatabase;

import ac.cust.custac.CustACAPI;
import ac.cust.custac.player.CustACPlayer;
import ac.cust.custac.utils.anticheat.LogUtil;
import ac.grim.grimac.api.plugin.GrimPlugin;
import com.github.retrooper.packetevents.PacketEvents;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ViolationFileLogger {

    private final File logsFolder;
    private final GrimPlugin plugin;

    public ViolationFileLogger(GrimPlugin plugin) {
        this.plugin = plugin;
        this.logsFolder = new File(plugin.getDataFolder(), "logs/players");
        if (!logsFolder.exists()) {
            logsFolder.mkdirs();
        }
    }

    public void log(CustACPlayer player, String custacVersion, String verbose, String checkName, int vls) {
        File playerFile = new File(logsFolder, player.getUniqueId() + ".log");
        
        long timestamp = System.currentTimeMillis();
        String brand = player.getBrand();
        String clientVersion = player.getClientVersion().getReleaseName();
        String serverVersion = PacketEvents.getAPI().getServerManager().getVersion().toString();
        String serverName = CustACAPI.INSTANCE.getConfigManager().getConfig().getStringElse("history.server-name", "default");

        // Format: timestamp|checkName|vl|verbose|custacVersion|clientBrand|clientVersion|serverVersion|serverName
        String line = String.format("%d|%s|%d|%s|%s|%s|%s|%s|%s",
                timestamp,
                checkName,
                vls,
                verbose.replace("|", "/"), // Prevent pipe from breaking parsing
                custacVersion,
                brand,
                clientVersion,
                serverVersion,
                serverName
        );

        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(playerFile, true), StandardCharsets.UTF_8)))) {
            writer.println(line);
        } catch (IOException e) {
            LogUtil.error("Failed to log violation to file for " + player.getName(), e);
        }
    }

    public List<Violation> getViolations(UUID uuid, int page, int limit) {
        File playerFile = new File(logsFolder, uuid + ".log");
        if (!playerFile.exists()) {
            return Collections.emptyList();
        }

        List<Violation> allViolations = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(playerFile.toPath(), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 9);
                if (parts.length < 4) continue;

                try {
                    long timestamp = Long.parseLong(parts[0]);
                    String checkName = parts[1];
                    int vl = Integer.parseInt(parts[2]);
                    String verbose = parts[3];
                    String custacVersion = parts.length > 4 ? parts[4] : "Unknown";
                    String brand = parts.length > 5 ? parts[5] : "Unknown";
                    String clientVersion = parts.length > 6 ? parts[6] : "Unknown";
                    String serverVersion = parts.length > 7 ? parts[7] : "Unknown";
                    String serverName = parts.length > 8 ? parts[8] : "default";

                    allViolations.add(new Violation(serverName, uuid, checkName, verbose, vl, timestamp, custacVersion, brand, clientVersion, serverVersion));
                } catch (NumberFormatException e) {
                    // Skip malformed lines
                }
            }
        } catch (IOException e) {
            LogUtil.error("Failed to read violations from file for " + uuid, e);
        }

        // Return latest first
        Collections.reverse(allViolations);

        int start = (page - 1) * limit;
        if (start >= allViolations.size()) {
            return Collections.emptyList();
        }
        int end = Math.min(start + limit, allViolations.size());
        return allViolations.subList(start, end);
    }

    public int getLogCount(UUID uuid) {
        File playerFile = new File(logsFolder, uuid + ".log");
        if (!playerFile.exists()) {
            return 0;
        }

        int count = 0;
        try (BufferedReader reader = Files.newBufferedReader(playerFile.toPath(), StandardCharsets.UTF_8)) {
            while (reader.readLine() != null) {
                count++;
            }
        } catch (IOException e) {
            LogUtil.error("Failed to count violations in file for " + uuid, e);
        }
        return count;
    }
}
