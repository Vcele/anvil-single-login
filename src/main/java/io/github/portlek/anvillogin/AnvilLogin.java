package io.github.portlek.anvillogin;

import net.wesjd.anvilgui.AnvilGUI;
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

    @Override
    public void onEnable() {
        //saves the default config if it doesn't exist
        this.saveDefaultConfig();
        //loads the config
        this.password = this.getConfig().getString("password");
        this.insert = this.c(this.getConfig().getString("insert"));
        this.wrongPassword = this.c(this.getConfig().getString("wrong-password"));
        //registers the listener
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getOnlinePlayers().forEach(this::ask);
    }

    private String c(final String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private void ask(final Player player) {
            this.openLogin(player); // todo: check if player already logged in once
    }

    private void openLogin(final Player p) {
        final AnvilGUI.Builder builder = new AnvilGUI.Builder()
            .onComplete((player, s) -> {
                if (!s.equals(this.password)) {
                    return AnvilGUI.Response.text(this.wrongPassword);
                }
                this.authmeApi.forceLogin(player);
                return AnvilGUI.Response.close();
            })
            .preventClose()
            .text(this.insert)
            .plugin(this);

        this.getServer().getScheduler().runTask(this, () -> builder.open(p));
    }

    @EventHandler
    public void join(final PlayerJoinEvent event) {
        this.ask(event.getPlayer());
    }

}
