
package me.lordlladar.plugins.loworbitioncannon;

import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerListener;

public class LoicPlayerListener extends PlayerListener {
    private final Loic plugin;
    boolean fail = false;
    
    public LoicPlayerListener(Loic instance) {
        this.plugin = instance;
    }
    
    public void onPlayerAnimation(PlayerAnimationEvent event) {
        this.plugin.armSwing(event.getPlayer());
    }
    
}