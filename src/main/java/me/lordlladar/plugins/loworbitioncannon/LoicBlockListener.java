
package me.lordlladar.plugins.loworbitioncannon;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class LoicBlockListener extends BlockListener {
    private final Loic plugin;
    boolean fail = false;
    int health = 0;
    
    public LoicBlockListener(Loic instance) {
        this.plugin = instance;
    }
}