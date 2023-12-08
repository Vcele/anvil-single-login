package io.github.portlek.anvillogin;

import net.wesjd.anvilgui.AnvilGUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class AnvilLogin extends JavaPlugin implements Listener {

    private String password;

    private String insert;

    private String wrongPassword;

    private ArrayList<String> knownPlayers;

    @Override
    public void onEnable() {
        // saves the default config if it doesn't exist
        this.saveDefaultConfig();
        // loads the config
        this.password = this.getConfig().getString("password");
        this.insert = this.c(this.getConfig().getString("insert"));
        this.wrongPassword = this.c(this.getConfig().getString("wrong-password"));
        // load knownplayers from config
        this.knownPlayers = (ArrayList<String>) this.getConfig().getStringList("known-players");
        // registers the listener
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getOnlinePlayers().forEach(this::ask);
    }

    @Override
    public void onDisable() {
        // saves knownplayers to config
        this.getConfig().set("known-players", this.knownPlayers);
        this.saveConfig();
    }

    private String c(final String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private void ask(final Player player) {
        if (this.knownPlayers.contains(player.getUniqueId().toString())) { // if the player is known, don't ask for password
            return;
        }
        this.openLogin(player);
    }

    private void openLogin(final Player p) {
        new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> {
                    if (slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }

                    if (!stateSnapshot.getText().equalsIgnoreCase(this.password)) {
                        return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText(this.wrongPassword));
                    } else {
                        this.knownPlayers.add(p.getUniqueId().toString());
                        stateSnapshot.getPlayer().sendMessage(ChatColor.GREEN + "You have logged in successfully!");
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    }
                }).preventClose().text(this.insert).plugin(this).open(p);
    }

    @EventHandler
    public void join(final PlayerJoinEvent event) { // asks for password when player joins
        this.ask(event.getPlayer());
    }

}
