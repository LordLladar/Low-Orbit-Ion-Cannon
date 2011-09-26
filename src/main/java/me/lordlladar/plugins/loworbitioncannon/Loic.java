
package me.lordlladar.plugins.loworbitioncannon;

import java.util.Hashtable;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Loic extends JavaPlugin {
    
    private final LoicPlayerListener playerListener = new LoicPlayerListener(this);
    public static final Logger log = Logger.getLogger("Minecraft");
    public static String name = "Low Orbit Ion Cannon";
    public static String codename = "LOIC";
    public static String version = "0.5";
    public Listener l = new Listener() { } ;
    static Hashtable<String, Integer> cannons = new Hashtable();
    
    public boolean execture (CommandSender sender, String[] args) {
        return true;    
    }
    
    public void onEnable() {
        
        log.info("[" + name + "] Low Orbit Ion Cannon V." + version + " ...");  //Log Name + version in console
        log.info("[" + name + "] Low Orbit Ion Cannon by LordLladar.");         //Log Name + Author in console
        log.info("[" + name + "] Low Orbit Ion Cannon ENABLED!");               //Log Name + Toggle in console
        registerEvents();                                                       //Register Events
    }
    
    public void onDisable() {
        
        log.info("[" + name +"] Low Orbit Ion Cannon DISABLED!");
    }
    
    private void registerEvents() {
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_ANIMATION, this.playerListener, Priority.Normal, this);
    }
    
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
            if (sender instanceof Player);
                Player player = (Player)sender;        
        if (command.getName().toString().equalsIgnoreCase("ioncannon")) {
            if (player.hasPermission("ioncannon.fire")) {
            if (cannons.containsKey(((Player)sender).getName().toString())) {
                getServer().broadcastMessage(ChatColor.YELLOW + "ATTENTION : " + ChatColor.BLUE + "Ion Cannons has been disabled");
                cannons.remove(((Player)sender).getName().toString());
                return true;
            }
            int size = 3;
            if (args.length > 0)
                size = Integer.parseInt(args[0]);
            if (size < 1)
                size = 1;
            getServer().broadcastMessage(ChatColor.RED + "WARNING : " + ChatColor.BLUE + "Ion Cannon Primed.");
            getServer().broadcastMessage(ChatColor.YELLOW + "ATTENTION : " + ChatColor.BLUE + "Beam Size Locked at " +ChatColor.GOLD + size + ".");
            
            cannons.put(((Player)sender).getName().toString(), Integer.valueOf(size));
            return true;
        }
        
      log.info("[WARNING] Player " + sender.toString() + " was denied access to the command: " + command.toString() + ".");
      sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
      return true;   
    }
            
    return false;
    }
    
     private void placePoints(int cx, int cz, int x, int z, int y, int blockType, Player player) {
    if (x == 0) {
      player.getWorld().getBlockAt(cx, y, cz + z).setTypeId(blockType);
      player.getWorld().getBlockAt(cx, y, cz - z).setTypeId(blockType);
      player.getWorld().getBlockAt(cx + z, y, cz).setTypeId(blockType);
      player.getWorld().getBlockAt(cx - z, y, cz).setTypeId(blockType);
    }
    else if (x == z) {
      player.getWorld().getBlockAt(cx + x, y, cz + z).setTypeId(blockType);
      player.getWorld().getBlockAt(cx - x, y, cz + z).setTypeId(blockType);
      player.getWorld().getBlockAt(cx + x, y, cz - z).setTypeId(blockType);
      player.getWorld().getBlockAt(cx - x, y, cz - z).setTypeId(blockType);
    }
    else if (x < z) {
      player.getWorld().getBlockAt(cx + x, y, cz + z).setTypeId(blockType);
      player.getWorld().getBlockAt(cx - x, y, cz + z).setTypeId(blockType);
      player.getWorld().getBlockAt(cx + x, y, cz - z).setTypeId(blockType);
      player.getWorld().getBlockAt(cx - x, y, cz - z).setTypeId(blockType);
      player.getWorld().getBlockAt(cx + z, y, cz + x).setTypeId(blockType);
      player.getWorld().getBlockAt(cx - z, y, cz + x).setTypeId(blockType);
      player.getWorld().getBlockAt(cx + z, y, cz - x).setTypeId(blockType);
      player.getWorld().getBlockAt(cx - z, y, cz - x).setTypeId(blockType);
    }
  }

  private void fillUpwards(int blockType, int x, int y, int z, Player player) {
    for (int i = 0; i < 5; i++) {
      if (player.getWorld().getBlockAt(x, y + i, z).getTypeId() != 0)
        break;
      player.getWorld().getBlockAt(x, y + i, z).setTypeId(blockType);
    }
  }

  private void placeCylinderPoints(int cx, int cz, int x, int z, int y, int blockType, Player player) {
    if (x == 0) {
      fillUpwards(blockType, cx, y, cz + z, player);
      fillUpwards(blockType, cx, y, cz - z, player);
      fillUpwards(blockType, cx + z, y, cz, player);
      fillUpwards(blockType, cx - z, y, cz, player);
    }
    else if (x == z) {
      fillUpwards(blockType, cx + x, y, cz + z, player);
      fillUpwards(blockType, cx - x, y, cz + z, player);
      fillUpwards(blockType, cx + x, y, cz - z, player);
      fillUpwards(blockType, cx - x, y, cz - z, player);
    }
    else if (x < z) {
      fillUpwards(blockType, cx + x, y, cz + z, player);
      fillUpwards(blockType, cx - x, y, cz + z, player);
      fillUpwards(blockType, cx + x, y, cz - z, player);
      fillUpwards(blockType, cx - x, y, cz - z, player);
      fillUpwards(blockType, cx + z, y, cz + x, player);
      fillUpwards(blockType, cx - z, y, cz + x, player);
      fillUpwards(blockType, cx + z, y, cz - x, player);
      fillUpwards(blockType, cx - z, y, cz - x, player);
    }
  }

  public void circleMidpoint(int xCenter, int yCenter, int radius, int y, int blockType, Player player) {
    int x = 0;
    int z = radius;
    int p = (5 - radius * 4) / 4;
    if (z == 2)
      p--;
    for (int i = z; i >= 0; i--)
      placePoints(xCenter, yCenter, x, i, y, blockType, player);
    while (x < z) {
      x++;
      if (p < 0) {
        p += 2 * x + 1;
      } else {
        z--;
        p += 2 * (x - z) + 1;
      }
      for (int i = z; i >= 0; i--)
        placePoints(xCenter, yCenter, x, i, y, blockType, player);
    }
  }

  public void cylinderModpoint(int xCenter, int yCenter, int radius, int y, int blockType, Player player) {
    int x = 0;
    int z = radius;
    int p = (5 - radius * 4) / 4;
    if (z == 2)
      p--;
    placeCylinderPoints(xCenter, yCenter, x, z, y, blockType, player);
    while (x < z) {
      x++;
      if (p < 0) {
        p += 2 * x + 1;
      } else {
        z--;
        p += 2 * (x - z) + 1;
      }
      placeCylinderPoints(xCenter, yCenter, x, z, y, blockType, player);
    }
  }
    
  public void armSwing(Player player) {
    if (cannons.containsKey(player.getName().toString())) {
      LoicTargetBlock blox = new LoicTargetBlock(player);
      Block block = blox.getTargetBlock();
      if (block == null)
        return;
      getServer().broadcastMessage(ChatColor.YELLOW + "ATTENTION : " + ChatColor.BLUE + "Ion Cannon Target Locked.");
      getServer().broadcastMessage(ChatColor.RED + "WARNING    : " + ChatColor.BLUE + "Ion Cannon Firing...");
      int size = ((Integer)cannons.get(player.getName())).intValue();
      log.info("[" + name + "] IonCannon fired by: '" + player.getName().toString() + "' with size: " + size);
      cannons.remove(player.getName());
      int beamType = 46;
      int borderType = 57;
      for (int i = 0; i < 128; i++) {
        circleMidpoint(block.getX(), block.getZ(), size, i, borderType, player);
        circleMidpoint(block.getX(), block.getZ(), size - 1, i, beamType, player);
      }
      circleMidpoint(block.getX(), block.getZ(), size, 0, 7, player);
      cylinderModpoint(block.getX(), block.getZ(), size + 1, 0, 7, player);
      player.getWorld().getBlockAt(block.getX(), 127, block.getZ()).setTypeId(51);
    }
  }
}