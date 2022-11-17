package me.darkwinged.RiotTeleportation.listener;

import me.darkwinged.RiotTeleportation.Main;
import me.darkwinged.RiotTeleportation.libaries.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class cancelEvent implements Listener {

    private final Main plugin = Main.getInstance;

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getZ() != event.getTo().getZ()) {
            if (Utils.randomTeleportDelay.containsKey(player.getUniqueId())) {
                Utils.randomTeleportDelay.remove(player.getUniqueId());
                player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("moved-before-teleported")));
            }
            if (Utils.teleportAskDelay.containsKey(player.getUniqueId())) {
                Utils.teleportAskDelay.remove(player.getUniqueId());
                player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("moved-before-teleported")));
            }
        }
    }

}
