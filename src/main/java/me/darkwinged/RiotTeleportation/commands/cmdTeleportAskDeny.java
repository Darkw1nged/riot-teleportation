package me.darkwinged.RiotTeleportation.commands;

import me.darkwinged.RiotTeleportation.Main;
import me.darkwinged.RiotTeleportation.libaries.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class cmdTeleportAskDeny implements CommandExecutor {

    private final Main plugin = Main.getInstance;

    public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {
        if (cmd.getName().equalsIgnoreCase("teleportdeny")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("console")));
                return true;
            }
            Player player = (Player)sender;
            if (Utils.teleport_ask.containsKey(player.getUniqueId())) {
                Utils.teleport_ask.remove(player.getUniqueId());
                player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                        plugin.fileMessage.getConfig().getString("teleport-request-denied")));
            } else
                player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("no-pending-requests")));
        }
        return false;
    }
}
