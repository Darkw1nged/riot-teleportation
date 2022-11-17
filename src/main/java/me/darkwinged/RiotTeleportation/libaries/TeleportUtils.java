package me.darkwinged.RiotTeleportation.libaries;

import me.darkwinged.RiotTeleportation.Main;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Random;

public class TeleportUtils {

    private final static Main plugin = Main.getInstance;

    public static Location RandomTeleport(Player player) {
        Random random = new Random();
        int x, z, y;

        if (plugin.getConfig().getBoolean("Teleportation-Random.world-border.enable-limit", true)) {
            x = random.nextInt(plugin.getConfig().getInt("Teleportation-Random.world-border.range"));
            z = random.nextInt(plugin.getConfig().getInt("Teleportation-Random.world-border.range"));
        } else {
            x = random.nextInt(25000);
            z = random.nextInt(25000);
        }
        y = 150;

        Location randomLocation = new Location(player.getWorld(), x, y, z);
        y = randomLocation.getWorld().getHighestBlockYAt(randomLocation) + 1;
        randomLocation.setY(y);

        return randomLocation;
    }

    public static Location findSafeLocationRandom(Player player) {
        Location randomLocation = RandomTeleport(player);
        while (!isLocationSafeV1(randomLocation)) {
            randomLocation = RandomTeleport(player);
        }
        return randomLocation;
    }

    public static HashSet<Material> bad_blocks = new HashSet<>();

    static {
        bad_blocks.add(Material.LAVA);
        bad_blocks.add(Material.FIRE);
        bad_blocks.add(Material.CACTUS);
    }

    public static boolean isLocationSafeV1(Location location) {
        Chunk chunk = Bukkit.getWorld(location.getWorld().getName()).getChunkAt(location);
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        if (!chunk.isForceLoaded()) {
            chunk.setForceLoaded(true);
        }

        Block block = location.getWorld().getBlockAt(x, y, z);
        Block below = location.getWorld().getBlockAt(x, y - 1, z);
        Block above = location.getWorld().getBlockAt(x, y + 1, z);
        return !(bad_blocks.contains(below.getType())) || (block.getType().isSolid()) || (above.getType().isSolid());
    }

}
