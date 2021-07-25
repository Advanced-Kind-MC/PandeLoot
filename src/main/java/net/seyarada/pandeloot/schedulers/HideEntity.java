package net.seyarada.pandeloot.schedulers;

import java.util.List;
import net.seyarada.pandeloot.PandeLoot;
import net.seyarada.pandeloot.nms.NMSManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class HideEntity {
  private int id;
  
  public HideEntity(Entity toHide, List<Player> canView) {
    PandeLoot pandeLoot = PandeLoot.getInstance();
    this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)pandeLoot, () -> {
          if (toHide.isValid()) {
            for (Entity entity : toHide.getNearbyEntities(42.0D, 42.0D, 42.0D)) {
              if (entity instanceof Player && !canView.contains(entity))
                NMSManager.destroyEntity(toHide.getEntityId(), entity); 
            } 
          } else {
            Bukkit.getScheduler().cancelTask(this.id);
          } 
        }0L, 10L);
  }
}
