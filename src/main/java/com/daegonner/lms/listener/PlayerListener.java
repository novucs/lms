package com.daegonner.lms.listener;

import com.daegonner.lms.LastManStandingPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerListener implements Listener {

    private final LastManStandingPlugin plugin;

    public PlayerListener(LastManStandingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handleDeath(PlayerDeathEvent event) {
        // Do nothing if there is no LMS game running.
        if (!plugin.getGameTask().hasGame()) {
            return;
        }

        // Register the player death on the game.
        plugin.getGameTask().getGame().playerDeath(event.getEntity());
    }
}