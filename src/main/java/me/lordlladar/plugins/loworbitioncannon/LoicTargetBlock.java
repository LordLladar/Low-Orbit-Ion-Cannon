
package me.lordlladar.plugins.loworbitioncannon;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class LoicTargetBlock {
  private Location loc;
  private double viewHeight;
  private int maxDistance;
  private int[] blockToIgnore;
  private double checkDistance;
  private double curDistance;
  private double xRotation;
  private double yRotation;
  private Vector targetPos = new Vector();
  private Vector targetPosDouble = new Vector();
  private Vector prevPos = new Vector();
  private Vector offset = new Vector();

  public LoicTargetBlock(Player player) {
    setValues(player.getLocation(), 300, 1.65D, 0.2D, null);
  }

  public LoicTargetBlock(Location loc) {
    setValues(loc, 300, 0.0D, 0.2D, null);
  }

  public LoicTargetBlock(Player player, int maxDistance, double checkDistance) {
    setValues(player.getLocation(), maxDistance, 1.65D, checkDistance, null);
  }

  public LoicTargetBlock(Location loc, int maxDistance, double checkDistance) {
    setValues(loc, maxDistance, 0.0D, checkDistance, null);
  }

  public LoicTargetBlock(Player player, int maxDistance, double checkDistance, int[] blocksToIgnore) {
    setValues(player.getLocation(), maxDistance, 1.65D, checkDistance, blocksToIgnore);
  }

  public LoicTargetBlock(Location loc, int maxDistance, double checkDistance, int[] blocksToIgnore) {
    setValues(loc, maxDistance, 0.0D, checkDistance, blocksToIgnore);
  }

  public LoicTargetBlock(Player player, int maxDistance, double checkDistance, ArrayList<String> blocksToIgnore) {
    int[] bti = convertStringArraytoIntArray(blocksToIgnore);
    setValues(player.getLocation(), maxDistance, 1.65D, checkDistance, bti);
  }

  public LoicTargetBlock(Location loc, int maxDistance, double checkDistance, ArrayList<String> blocksToIgnore) {
    int[] bti = convertStringArraytoIntArray(blocksToIgnore);
    setValues(loc, maxDistance, 0.0D, checkDistance, bti);
  }

  private void setValues(Location loc, int maxDistance, double viewHeight, double checkDistance, int[] blocksToIgnore) {
    this.loc = loc;
    this.maxDistance = maxDistance;
    this.viewHeight = viewHeight;
    this.checkDistance = checkDistance;
    this.blockToIgnore = blocksToIgnore;
    this.curDistance = 0.0D;
    this.xRotation = ((loc.getYaw() + 90.0F) % 360.0F);
    this.yRotation = (loc.getPitch() * -1.0F);

    double h = checkDistance * Math.cos(Math.toRadians(this.yRotation));
    this.offset.setY(checkDistance * Math.sin(Math.toRadians(this.yRotation)));
    this.offset.setX(h * Math.cos(Math.toRadians(this.xRotation)));
    this.offset.setZ(h * Math.sin(Math.toRadians(this.xRotation)));

    this.targetPosDouble = new Vector(loc.getX(), loc.getY() + viewHeight, loc.getZ());
    this.targetPos = new Vector(this.targetPosDouble.getBlockX(), this.targetPosDouble.getBlockY(), this.targetPosDouble.getBlockZ());
    this.prevPos = this.targetPos.clone();
  }

  public void reset() {
    this.targetPosDouble = new Vector(this.loc.getX(), this.loc.getY() + this.viewHeight, this.loc.getZ());
    this.targetPos = new Vector(this.targetPosDouble.getBlockX(), this.targetPosDouble.getBlockY(), this.targetPosDouble.getBlockZ());
    this.prevPos = this.targetPos.clone();
    this.curDistance = 0.0D;
  }

  public double getDistanceToBlock() {
    Vector blockUnderPlayer = new Vector(
      (int)Math.floor(this.loc.getX() + 0.5D), 
      (int)Math.floor(this.loc.getY() - 0.5D), 
      (int)Math.floor(this.loc.getZ() + 0.5D));

    Block blk = getTargetBlock();
    double x = blk.getX() - blockUnderPlayer.getBlockX();
    double y = blk.getY() - blockUnderPlayer.getBlockY();
    double z = blk.getZ() - blockUnderPlayer.getBlockZ();

    return Math.sqrt(Math.pow(x, 2.0D) + Math.pow(y, 2.0D) + Math.pow(z, 2.0D));
  }

  public int getDistanceToBlockRounded() {
    Vector blockUnderPlayer = new Vector(
      (int)Math.floor(this.loc.getX() + 0.5D), 
      (int)Math.floor(this.loc.getY() - 0.5D), 
      (int)Math.floor(this.loc.getZ() + 0.5D));

    Block blk = getTargetBlock();
    double x = blk.getX() - blockUnderPlayer.getBlockX();
    double y = blk.getY() - blockUnderPlayer.getBlockY();
    double z = blk.getZ() - blockUnderPlayer.getBlockZ();

    return (int)Math.round(Math.sqrt(Math.pow(x, 2.0D) + Math.pow(y, 2.0D) + Math.pow(z, 2.0D)));
  }

  public int getXDistanceToBlock() {
    reset();
    return (int)Math.floor(getTargetBlock().getX() - this.loc.getBlockX() + 0.5D);
  }

  public int getYDistanceToBlock() {
    reset();
    return (int)Math.floor(getTargetBlock().getY() - this.loc.getBlockY() + this.viewHeight);
  }

  public int getZDistanceToBlock() {
    reset();
    return (int)Math.floor(getTargetBlock().getZ() - this.loc.getBlockZ() + 0.5D);
  }

  public Block getTargetBlock() {
    reset();
    while ((getNextBlock() != null) && ((getCurrentBlock().getTypeId() == 0) || (blockToIgnoreHasValue(getCurrentBlock().getTypeId()))));
    return getCurrentBlock();
  }

  public boolean setTargetBlock(int typeID) {
    if (Material.getMaterial(typeID) != null)
    {
      reset();
      while ((getNextBlock() != null) && (getCurrentBlock().getTypeId() == 0));
      if (getCurrentBlock() != null)
      {
        Block blk = this.loc.getWorld().getBlockAt(this.targetPos.getBlockX(), this.targetPos.getBlockY(), this.targetPos.getBlockZ());
        blk.setTypeId(typeID);
        return true;
      }
    }
    return false;
  }

  public boolean setTargetBlock(Material type) {
    reset();
    while ((getNextBlock() != null) && ((getCurrentBlock().getTypeId() == 0) || (blockToIgnoreHasValue(getCurrentBlock().getTypeId()))));
    
    if (getCurrentBlock() != null) {
      Block blk = this.loc.getWorld().getBlockAt(this.targetPos.getBlockX(), this.targetPos.getBlockY(), this.targetPos.getBlockZ());
      blk.setType(type);
      return true;
    }
    return false;
  }

  public boolean setTargetBlock(String type) {
    Material mat = Material.valueOf(type);
    
    if (mat != null) {
      reset();
      while ((getNextBlock() != null) && ((getCurrentBlock().getTypeId() == 0) || (blockToIgnoreHasValue(getCurrentBlock().getTypeId()))));
      
      if (getCurrentBlock() != null) {
        Block blk = this.loc.getWorld().getBlockAt(this.targetPos.getBlockX(), this.targetPos.getBlockY(), this.targetPos.getBlockZ());
        blk.setType(mat);
        return true;
      }
    }
    return false;
  }

  public Block getFaceBlock() {
    while ((getNextBlock() != null) && ((getCurrentBlock().getTypeId() == 0) || (blockToIgnoreHasValue(getCurrentBlock().getTypeId()))));
    
    if (getCurrentBlock() != null) {
      return getPreviousBlock();
    }

    return null;
  }

  public boolean setFaceBlock(int typeID) {
      
    if (Material.getMaterial(typeID) != null){
        
      if (getCurrentBlock() != null) {
        Block blk = this.loc.getWorld().getBlockAt(this.prevPos.getBlockX(), this.prevPos.getBlockY(), this.prevPos.getBlockZ());
        blk.setTypeId(typeID);
        return true;
      }
    }
    return false;
  }

  public boolean setFaceBlock(Material type) {
      
    if (getCurrentBlock() != null) {
      Block blk = this.loc.getWorld().getBlockAt(this.prevPos.getBlockX(), this.prevPos.getBlockY(), this.prevPos.getBlockZ());
      blk.setType(type);
      return true;
    }
    return false;
  }

  public boolean setFaceBlock(String type) {
    Material mat = Material.valueOf(type);
    
    if (mat != null) {
        
      if (getCurrentBlock() != null) {
        Block blk = this.loc.getWorld().getBlockAt(this.prevPos.getBlockX(), this.prevPos.getBlockY(), this.prevPos.getBlockZ());
        blk.setType(mat);
        return true;
      }
    }
    return false;
  }

  public Block getNextBlock() {
    this.prevPos = this.targetPos.clone();
    
    do {
      this.curDistance += this.checkDistance;

      this.targetPosDouble.setX(this.offset.getX() + this.targetPosDouble.getX());
      this.targetPosDouble.setY(this.offset.getY() + this.targetPosDouble.getY());
      this.targetPosDouble.setZ(this.offset.getZ() + this.targetPosDouble.getZ());
      this.targetPos = new Vector(this.targetPosDouble.getBlockX(), this.targetPosDouble.getBlockY(), this.targetPosDouble.getBlockZ());
    }
    while ((this.curDistance <= this.maxDistance) && (this.targetPos.getBlockX() == this.prevPos.getBlockX()) && (this.targetPos.getBlockY() == this.prevPos.getBlockY()) && (this.targetPos.getBlockZ() == this.prevPos.getBlockZ()));
    
    if (this.curDistance > this.maxDistance) {
      return null;
    }

    return this.loc.getWorld().getBlockAt(this.targetPos.getBlockX(), this.targetPos.getBlockY(), this.targetPos.getBlockZ());
  }

  public Block getCurrentBlock()
  {
      
    if (this.curDistance > this.maxDistance) {
      return null;
    }

    return this.loc.getWorld().getBlockAt(this.targetPos.getBlockX(), this.targetPos.getBlockY(), this.targetPos.getBlockZ());
  }

  public boolean setCurrentBlock(int typeID) {
      
    if (Material.getMaterial(typeID) != null) {
      Block blk = getCurrentBlock();
      
      if (blk != null) {
        blk.setTypeId(typeID);
        return true;
      }
    }
    return false;
  }

  public boolean setCurrentBlock(Material type) {
    Block blk = getCurrentBlock();
    
    if (blk != null) {
      blk.setType(type);
      return true;
    }
    return false;
  }

  public boolean setCurrentBlock(String type) {
    Material mat = Material.valueOf(type);
    
    if (mat != null) {
      Block blk = getCurrentBlock();
      
      if (blk != null) {
        blk.setType(mat);
        return true;
      }
    }
    return false;
  }

  public Block getPreviousBlock() {
    return this.loc.getWorld().getBlockAt(this.prevPos.getBlockX(), this.prevPos.getBlockY(), this.prevPos.getBlockZ());
  }

  public boolean setPreviousBlock(int typeID) {
      
    if (Material.getMaterial(typeID) != null) {
      Block blk = getPreviousBlock();
      
      if (blk != null) {
        blk.setTypeId(typeID);
        return true;
      }
    }
    return false;
  }

  public boolean setPreviousBlock(Material type) {
    Block blk = getPreviousBlock();
    
    if (blk != null) {
      blk.setType(type);
      return true;
    }
    return false;
  }

  public boolean setPreviousBlock(String type) {
    Material mat = Material.valueOf(type);
    
    if (mat != null) {
      Block blk = getPreviousBlock();
      
      if (blk != null) {
        blk.setType(mat);
        return true;
      }
    }
    return false;
  }

  private int[] convertStringArraytoIntArray(ArrayList<String> array) {
      
    if (array != null) {
      int[] intarray = new int[array.size()];
      
      for (int i = 0; i < array.size(); i++) {
          
        try {
          intarray[i] = Integer.parseInt((String)array.get(i));
        }
        
        catch (NumberFormatException nfe) {
          intarray[i] = 0;
        }
      }
      return intarray;
    }
    return null;
  }

  private boolean blockToIgnoreHasValue(int value) {
      
    if (this.blockToIgnore != null) {
        
      if (this.blockToIgnore.length > 0) {
          
        for (int i : this.blockToIgnore) {
          if (i == value)
            return true;
        }
      }
    }
    return false;
  }
}
