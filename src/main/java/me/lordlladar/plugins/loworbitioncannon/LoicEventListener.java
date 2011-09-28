package me.lordlladar.plugins.loworbitioncannon;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class LoicEventListener implements Listener {
    private final Loic plugin;
    
    public LoicEventListener(Loic instance) {
        this.plugin = instance;
        
    }
}
