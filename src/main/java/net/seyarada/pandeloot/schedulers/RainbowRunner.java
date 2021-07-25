package net.seyarada.pandeloot.schedulers;

import java.util.concurrent.atomic.AtomicInteger;
import net.seyarada.pandeloot.Config;
import net.seyarada.pandeloot.PandeLoot;
import net.seyarada.pandeloot.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class RainbowRunner {
  private static final String[] rainbowColors = new String[] { "DARK_RED", "RED", "GOLD", "YELLOW", "GREEN", "DARK_GREEN", "AQUA", "DARK_AQUA", "DARK_PURPLE", "LIGHT_PURPLE" };
  
  private int id;
  
  public RainbowRunner(Item item, Location location, double beam) {
    PandeLoot pandeLoot = PandeLoot.getInstance();
    AtomicInteger tick = new AtomicInteger(-1);
    int frequency = Config.getRainbowFrequency();
    this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)pandeLoot, () -> {
          tick.addAndGet(1);
          if (tick.get() >= rainbowColors.length)
            tick.set(0); 
          String color = rainbowColors[tick.get()];
          Color rgb = ColorUtil.getRGB(color);
          if (item.isValid()) {
            ColorUtil.setItemColor(item, color, null);
            if (item.isOnGround()) {
              double modHeight;
              for (modHeight = beam; modHeight > 0.0D; modHeight -= 0.1D) {
                Particle.DustOptions dustOptions = new Particle.DustOptions(rgb, 1.0F);
                location.getWorld().spawnParticle(Particle.REDSTONE, item.getLocation().add(0.0D, modHeight, 0.0D), 1, dustOptions);
              } 
            } else {
              Particle.DustOptions dustOptions = new Particle.DustOptions(rgb, 1.0F);
              location.getWorld().spawnParticle(Particle.REDSTONE, item.getLocation(), 1, dustOptions);
            } 
          } else {
            Bukkit.getScheduler().cancelTask(this.id);
          } 
        }, 0L, frequency);
  }
  
  public RainbowRunner(Item item, Player player, double beam) {
    PandeLoot pandeLoot = PandeLoot.getInstance();
    AtomicInteger tick = new AtomicInteger(-1);
    int frequency = Config.getRainbowFrequency();
    this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)pandeLoot, () -> {
          tick.addAndGet(1);
          if (tick.get() >= rainbowColors.length)
            tick.set(0); 
          String color = rainbowColors[tick.get()];
          Color rgb = ColorUtil.getRGB(color);
          if (item.isValid()) {
            ColorUtil.setItemColor(item, color, player);
            if (item.isOnGround()) {
              double modHeight;
              for (modHeight = beam; modHeight > 0.0D; modHeight -= 0.1D) {
                Particle.DustOptions dustOptions = new Particle.DustOptions(rgb, 1.0F);
                player.spawnParticle(Particle.REDSTONE, item.getLocation().add(0.0D, modHeight, 0.0D), 1, dustOptions);
              } 
            } else {
              Particle.DustOptions dustOptions = new Particle.DustOptions(rgb, 1.0F);
              player.spawnParticle(Particle.REDSTONE, item.getLocation(), 1, dustOptions);
            } 
          } else {
            Bukkit.getScheduler().cancelTask(this.id);
          } 
        }, 0L, frequency);
  }
}
