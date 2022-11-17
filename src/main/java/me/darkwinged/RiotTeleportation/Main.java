package me.darkwinged.RiotTeleportation;

import me.darkwinged.RiotTeleportation.commands.*;
import me.darkwinged.RiotTeleportation.libaries.CustomConfig;
import me.darkwinged.RiotTeleportation.libaries.TeleportUtils;
import me.darkwinged.RiotTeleportation.libaries.Utils;
import me.darkwinged.RiotTeleportation.listener.cancelEvent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    public static Main getInstance;
    private static Economy econ = null;

    public CustomConfig fileMessage;

    public void onEnable() {
        // ---- [ Initializing instance of main class | manager classes ] ----
        getInstance = this;
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        new TeleportUtils();

        // ---- [ Loading Commands  | Loading YML Files ] ----
        loadCommands();
        loadEvents();
        saveDefaultConfig();

        // ---- [ Loading lang file ] ----
        fileMessage = new CustomConfig(this, "lang/" + this.getConfig().getString("Storage.Language File"), true);
        fileMessage.saveDefaultConfig();

        // ---- [ Checking if economy was found ] ----
        if (!setupEconomy() ) {
            getServer().getConsoleSender().sendMessage(Utils.chatColor(this.fileMessage.getConfig().getString("plugin-disabled")));
            getServer().getPluginManager().disablePlugin(this);
        }

        // ---- [ Startup message ] ----
        getServer().getConsoleSender().sendMessage(Utils.chatColor(this.fileMessage.getConfig().getString("startup")));
    }

    public void onDisable() {
        // ---- [ shutdown message ] ----
        getServer().getConsoleSender().sendMessage(Utils.chatColor(this.fileMessage.getConfig().getString("shutdown")));
    }

    public void loadCommands() {
        getCommand("randomteleport").setExecutor(new cmdRandomTeleport());
        getCommand("teleportask").setExecutor(new cmdTeleportAsk());
        getCommand("teleportcancel").setExecutor(new cmdTeleportAskCancel());
        getCommand("teleportaccept").setExecutor(new cmdTeleportAccept());
        getCommand("teleportdeny").setExecutor(new cmdTeleportAskDeny());
    }

    public void loadEvents() {
        getServer().getPluginManager().registerEvents(new cancelEvent(), this);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public Economy getEconomy() {
        return econ;
    }

}
