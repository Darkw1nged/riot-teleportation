package me.darkwinged.RiotTeleportation.commands;

import me.darkwinged.RiotTeleportation.Main;
import me.darkwinged.RiotTeleportation.libaries.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class cmdTeleportAccept implements CommandExecutor {

    private final Main plugin = Main.getInstance;

    public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {
        if (cmd.getName().equalsIgnoreCase("teleportaccept")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("console")));
                return true;
            }
            Player player = (Player) sender;
            if (Utils.teleport_ask.containsKey(player.getUniqueId())) {
                player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                        plugin.fileMessage.getConfig().getString("teleport-request-accepted")));

                Player target = Bukkit.getPlayer(Utils.teleport_ask.get(player.getUniqueId()));
                if (player.hasPermission("RiotTeleportation.Bypass") || player.hasPermission("RiotTeleportation.*")) {
                    if (target == null) return true;
                    target.teleport(player);

                    if (plugin.getConfig().getBoolean("Teleportation-Ask.success.has-chat-message", true)) {
                        target.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                                        plugin.fileMessage.getConfig().getString("teleported"))
                                .replaceAll("%player%", player.getName())
                                .replaceAll("%target%", player.getName()));
                    }
                    if (plugin.getConfig().getBoolean("Teleportation-Ask.success.has-title", true)) {
                        target.sendTitle(Utils.chatColor(plugin.getConfig().getString("Teleportation-Ask.success.title-content")),
                                Utils.chatColor(plugin.getConfig().getString("Teleportation-Ask.success.subtitle-content"))
                                        .replaceAll("%player%", player.getName())
                                        .replaceAll("%target%", player.getName()));
                    }
                    Utils.teleport_ask.remove(player.getUniqueId());
                    return true;
                }
                if (plugin.getConfig().getBoolean("Teleportation-Ask.require-payment", true)) {
                    if (plugin.getEconomy().hasAccount(player) && plugin.getEconomy().has(player, plugin.getConfig().getInt("Teleportation-Ask.cost"))) {
                        plugin.getEconomy().withdrawPlayer(player, plugin.getConfig().getInt("Teleportation-Ask.cost"));
                    } else {
                        return true;
                    }
                }

                player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                                plugin.fileMessage.getConfig().getString("teleporting"))
                        .replaceAll("%delay%", "" + plugin.getConfig().getInt("Teleportation-Ask.teleport-delay")));
                UUID requestee = Utils.teleport_ask.get(player.getUniqueId());
                Utils.teleportAskDelay.put(requestee, plugin.getConfig().getInt("Teleportation-Ask.teleport-delay"));

                new BukkitRunnable() {
                    public void run() {
                        if (!Utils.teleportAskDelay.containsKey(requestee)) return;
                        if (Utils.teleportAskDelay.get(requestee) <= 0) {
                            target.teleport(player);

                            if (plugin.getConfig().getBoolean("Teleportation-Ask.success.has-chat-message", true)) {
                                target.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                                                plugin.fileMessage.getConfig().getString("teleported"))
                                        .replaceAll("%player%", player.getName())
                                        .replaceAll("%target%", player.getName()));
                            }
                            if (plugin.getConfig().getBoolean("Teleportation-Ask.success.has-title", true)) {
                                target.sendTitle(Utils.chatColor(plugin.getConfig().getString("Teleportation-Random.success.title-content")),
                                        Utils.chatColor(plugin.getConfig().getString("Teleportation-Random.success.subtitle-content"))
                                                .replaceAll("%player%", player.getName())
                                                .replaceAll("%target%", player.getName()));
                            }

                            // Removing the player from the delay and resetting it
                            Utils.teleportAskDelay.remove(player.getUniqueId());
                            Utils.teleport_ask.remove(player.getUniqueId());
                            cancel();
                            return;
                        }
                        // Removing 1 from the count
                        Utils.teleportAskDelay.put(player.getUniqueId(), Utils.teleportAskDelay.get(player.getUniqueId()) - 1);
                    }
                }.runTaskTimer(plugin, 0L, 20L);

            } else
                player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("no-pending-requests")));
        }
        return false;
    }
}
