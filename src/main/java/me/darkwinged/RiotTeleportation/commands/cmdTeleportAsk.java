package me.darkwinged.RiotTeleportation.commands;

import me.darkwinged.RiotTeleportation.Main;
import me.darkwinged.RiotTeleportation.libaries.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class cmdTeleportAsk implements CommandExecutor {

    private final Main plugin = Main.getInstance;

    public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {
        if (cmd.getName().equalsIgnoreCase("teleportask")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("console")));
                return true;
            }
            Player player = (Player)sender;
            if (args.length == 1) {
                // ---- [ Getting target from arguments ] ----
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("no-player-found")));
                    return true;
                }
                if (target == player) {
                    player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("specify-player")));
                    return true;
                }

                // ---- [ Sending request to target ] ----
                target.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                                plugin.fileMessage.getConfig().getString("teleport-ask"))
                        .replaceAll("%player%", player.getName())
                        .replaceAll("%target%", player.getName()));

                TextComponent yesComponent = new TextComponent(Utils.chatColor("&a&l[ ACCEPT ] "));
                TextComponent noComponent = new TextComponent(Utils.chatColor(" &C&l[ DENY ]"));

                yesComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));
                noComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpadeny"));

                target.spigot().sendMessage(yesComponent, noComponent);

                // ---- [ Sending confirmation to player ] ----
                player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                                plugin.fileMessage.getConfig().getString("teleport-request-sent"))
                        .replaceAll("%player%", target.getName())
                        .replaceAll("%target%", target.getName()));

                // ---- [ Adding players to list ] ----
                Utils.teleport_ask.put(target.getUniqueId(), player.getUniqueId());
                Utils.teleportTimeout.put(target.getUniqueId(), plugin.getConfig().getInt("Teleportation-Ask.request-timeout"));

                Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                    public void run() {
                        if (Utils.teleportTimeout.containsKey(target.getUniqueId())) {
                            if (Utils.teleportTimeout.get(target.getUniqueId()) > 0) {
                                Utils.teleportTimeout.put(target.getUniqueId(), Utils.teleportTimeout.get(target.getUniqueId()) - 1);
                                return;
                            }
                            Utils.teleport_ask.remove(target.getUniqueId());
                            Utils.teleportTimeout.remove(target.getUniqueId());
                        }
                    }
                }, 0L, 20L);
            }
        }
        return false;
    }

}
