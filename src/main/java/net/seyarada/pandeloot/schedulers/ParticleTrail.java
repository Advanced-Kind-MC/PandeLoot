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

public class ParticleTrail {
  private int id;
  
  public ParticleTrail(Item item, String color, Location location) {
    String fColor = ColorUtil.getColor(item, color);
    Color rgb = ColorUtil.getRGB(fColor);
    PandeLoot pandeLoot = PandeLoot.getInstance();
    this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)pandeLoot, () -> {
          if (!item.isOnGround() && item.isValid()) {
            Particle.DustOptions dustOptions = new Particle.DustOptions(rgb, 1.0F);
            location.getWorld().spawnParticle(Particle.REDSTONE, item.getLocation(), 1, dustOptions);
          } else {
            Bukkit.getScheduler().cancelTask(this.id);
          } 
        }0L, 1L);
  }
  
  public ParticleTrail(Item item, String color, Player player) {
    String fColor = ColorUtil.getColor(item, color);
    Color rgb = ColorUtil.getRGB(fColor);
    PandeLoot pandeLoot = PandeLoot.getInstance();
    this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)pandeLoot, () -> {
          if (!item.isOnGround() && item.isValid()) {
            Particle.DustOptions dustOptions = new Particle.DustOptions(rgb, 1.0F);
            player.spawnParticle(Particle.REDSTONE, item.getLocation(), 1, dustOptions);
          } else {
            Bukkit.getScheduler().cancelTask(this.id);
          } 
        }0L, 1L);
  }
}
