package net.seyarada.pandeloot.schedulers;

import net.seyarada.pandeloot.PandeLoot;
import net.seyarada.pandeloot.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Beam {
  private int id;
  
  public Beam(Item item, String color, Location location, double height) {
    String fColor = ColorUtil.getColor(item, color);
    Color rgb = ColorUtil.getRGB(fColor);
    PandeLoot pandeLoot = PandeLoot.getInstance();
    this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)pandeLoot, () -> {
          if (item.isValid()) {
            if (item.isOnGround()) {
              double modHeight;
              for (modHeight = height; modHeight > 0.0D; modHeight -= 0.1D) {
                Particle.DustOptions dustOptions = new Particle.DustOptions(rgb, 1.0F);
                location.getWorld().spawnParticle(Particle.REDSTONE, item.getLocation().add(0.0D, 0.15D + modHeight, 0.0D), 1, dustOptions);
              } 
            } 
          } else {
            Bukkit.getScheduler().cancelTask(this.id);
          } 
        }0L, 1L);
  }
  
  public Beam(Item item, String color, Player player, double height) {
    String fColor = ColorUtil.getColor(item, color);
    Color rgb = ColorUtil.getRGB(fColor);
    PandeLoot pandeLoot = PandeLoot.getInstance();
    this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)pandeLoot, () -> {
          if (item.isValid()) {
            if (item.isOnGround()) {
              double modHeight;
              for (modHeight = height; modHeight > 0.0D; modHeight -= 0.1D) {
                Particle.DustOptions dustOptions = new Particle.DustOptions(rgb, 1.0F);
                player.spawnParticle(Particle.REDSTONE, item.getLocation().add(0.0D, modHeight, 0.0D), 1, dustOptions);
              } 
            } 
          } else {
            Bukkit.getScheduler().cancelTask(this.id);
          } 
        }0L, 1L);
  }
}
