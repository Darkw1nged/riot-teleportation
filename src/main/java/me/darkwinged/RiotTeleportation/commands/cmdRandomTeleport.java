package me.darkwinged.RiotTeleportation.commands;

import me.darkwinged.RiotTeleportation.Main;
import me.darkwinged.RiotTeleportation.libaries.TeleportUtils;
import me.darkwinged.RiotTeleportation.libaries.Utils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class cmdRandomTeleport implements CommandExecutor {

    private final Main plugin = Main.getInstance;

    private final HashMap<UUID, Integer> Cooldown = new HashMap<>();

    public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {
        if (cmd.getName().equalsIgnoreCase("randomteleport")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("console")));
                return true;
            }
            Player player = (Player) sender;
            if (player.hasPermission("RiotTeleportation.Random.Teleport") || player.hasPermission("RiotTeleportation.*")) {
                if (Utils.randomTeleportDelay.containsKey(player.getUniqueId()) || Cooldown.containsKey(player.getUniqueId())) {
                    player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("on-cooldown")));
                    return true;
                }
                if (player.hasPermission("RiotTeleportation.Bypass") || player.hasPermission("RiotTeleportation.*")) {
                    player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                            plugin.fileMessage.getConfig().getString("finding-safe-spot")));

                    Location randomLocation = TeleportUtils.findSafeLocationRandom(player);
                    player.teleport(randomLocation);

                    if (plugin.getConfig().getBoolean("Teleportation-Random.success.has-chat-message", true)) {
                        player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                                plugin.fileMessage.getConfig().getString("random-teleport")));
                    }
                    if (plugin.getConfig().getBoolean("Teleportation-Random.success.has-title", true)) {
                        player.sendTitle(Utils.chatColor(plugin.getConfig().getString("Teleportation-Random.success.title-content")),
                                Utils.chatColor(plugin.getConfig().getString("Teleportation-Random.success.subtitle-content")));
                    }

                    return true;
                } else {
                    if (plugin.getConfig().getBoolean("Teleportation-Random.require-payment", true)) {
                        if (plugin.getEconomy().hasAccount(player) && plugin.getEconomy().has(player, plugin.getConfig().getInt("Teleportation-Random.cost"))) {
                            plugin.getEconomy().withdrawPlayer(player, plugin.getConfig().getInt("Teleportation-Random.cost"));
                        } else {
                            return true;
                        }
                    }
                    player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                            plugin.fileMessage.getConfig().getString("random-teleport"))
                            .replaceAll("%delay%", "" + plugin.getConfig().getInt("Teleportation-Random.teleport-delay")));

                    Utils.randomTeleportDelay.put(player.getUniqueId(), plugin.getConfig().getInt("Teleportation-Random.teleport-delay"));
                    new BukkitRunnable() {
                        public void run() {
                            if (!Utils.randomTeleportDelay.containsKey(player.getUniqueId())) return;
                            if (Utils.randomTeleportDelay.get(player.getUniqueId()) <= 0) {
                                player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                                        plugin.fileMessage.getConfig().getString("finding-safe-spot")));

                                Location randomLocation = TeleportUtils.findSafeLocationRandom(player);
                                player.teleport(randomLocation);
                                if (plugin.getConfig().getBoolean("Teleportation-Random.success.has-chat-message", true)) {
                                    player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                                            plugin.fileMessage.getConfig().getString("random-teleport")));
                                }
                                if (plugin.getConfig().getBoolean("Teleportation-Random.success.has-title", true)) {
                                    player.sendTitle(Utils.chatColor(plugin.getConfig().getString("Teleportation-Random.success.title-content")),
                                            Utils.chatColor(plugin.getConfig().getString("Teleportation-Random.success.subtitle-content")));
                                }

                                Utils.randomTeleportDelay.remove(player.getUniqueId());
                                Cooldown.put(player.getUniqueId(), Utils.getRawTime(plugin.getConfig().getString("Teleportation-Random.teleport-cooldown")));
                                new BukkitRunnable() {
                                    public void run() {
                                        if (Cooldown.containsKey(player.getUniqueId())) {
                                            Cooldown.put(player.getUniqueId(), Cooldown.get(player.getUniqueId()) - 1);
                                            return;
                                        }
                                        Cooldown.remove(player.getUniqueId());
                                        cancel();
                                    }
                                }.runTaskTimer(plugin, 0L, 20L);
                                cancel();
                                return;
                            }
                            Utils.randomTeleportDelay.put(player.getUniqueId(), Utils.randomTeleportDelay.get(player.getUniqueId()) - 1);
                        }
                    }.runTaskTimer(plugin, 0L, 20L);
                }
            } else
                sender.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("no-permission")));
        }
        return false;
    }
}
