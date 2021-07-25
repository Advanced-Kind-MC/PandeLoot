package net.seyarada.pandeloot.schedulers;

import net.seyarada.pandeloot.PandeLoot;
import net.seyarada.pandeloot.items.LootBalloon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class BalloonScheduler {
  private int id;
  
  public BalloonScheduler(Entity balloon, Entity anchor) {
    PandeLoot pandeLoot = PandeLoot.getInstance();
    this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)pandeLoot, () -> {
          if (anchor.isValid() && balloon.isValid()) {
            balloon.teleport(anchor.getLocation().clone().add(0.0D, 4.5D, 0.0D));
            if (anchor.getFallDistance() > 0.0F)
              LootBalloon.clear(balloon.getUniqueId()); 
          } else {
            Bukkit.getScheduler().cancelTask(this.id);
          } 
        }0L, 1L);
  }
}
