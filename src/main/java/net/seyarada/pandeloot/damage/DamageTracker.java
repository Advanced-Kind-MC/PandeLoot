package net.seyarada.pandeloot.damage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.citizensnpcs.api.CitizensAPI;
import net.seyarada.pandeloot.PandeLoot;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DamageTracker implements Listener {
  public static Map<UUID, Map<UUID, Double>> damageTracker = new HashMap<>();
  
  public static Map<UUID, MobOptions> loadedMobs = new HashMap<>();
  
  public static Map<UUID, UUID> lastHits = new HashMap<>();
  
  public static void addPlayerDamage(UUID mob, Player player, Double damage) {
    if (PandeLoot.getInstance().getServer().getPluginManager().getPlugin("Citizens") != null && 
      CitizensAPI.getNPCRegistry().isNPC((Entity)player))
      return; 
    Entity entity = Bukkit.getEntity(mob);
    if (entity != null && ((LivingEntity)entity).getHealth() < damage.doubleValue()) {
      damage = Double.valueOf(((LivingEntity)entity).getHealth());
      lastHits.put(mob, player.getUniqueId());
    } 
    if (!damageTracker.containsKey(mob)) {
      HashMap<UUID, Double> hashMap = new HashMap<>();
      hashMap.put(player.getUniqueId(), damage);
      damageTracker.put(mob, hashMap);
      return;
    } 
    Map<UUID, Double> damageMap = damageTracker.get(mob);
    if (damageMap.containsKey(player.getUniqueId())) {
      damageMap.put(player.getUniqueId(), Double.valueOf(((Double)damageMap.get(player.getUniqueId())).doubleValue() + damage.doubleValue()));
    } else {
      damageMap.put(player.getUniqueId(), damage);
    } 
    damageTracker.put(mob, damageMap);
  }
  
  public static Map<UUID, Double> get(UUID uuid) {
    return damageTracker.get(uuid);
  }
  
  @EventHandler(priority = EventPriority.MONITOR)
  public void onDamaged(EntityDamageByEntityEvent e) {
    UUID uuid = e.getEntity().getUniqueId();
    if (!loadedMobs.containsKey(uuid))
      return; 
    Player player = null;
    if (e.getDamager() instanceof Player)
      player = (Player)e.getDamager(); 
    if (e.getDamager() instanceof Projectile && (
      (Projectile)e.getDamager()).getShooter() instanceof Player)
      player = (Player)((Projectile)e.getDamager()).getShooter(); 
    if (player == null)
      return; 
    addPlayerDamage(uuid, player, Double.valueOf(e.getFinalDamage()));
  }
  
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent e) {
    Player name = e.getEntity();
    remove(name);
  }
  
  @EventHandler
  public void onWorldChange(PlayerChangedWorldEvent e) {
    Player name = e.getPlayer();
    remove(name);
  }
  
  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    Player name = e.getPlayer();
    remove(name);
  }
  
  public void remove(Player player) {
    for (UUID uuid : damageTracker.keySet()) {
      Map<UUID, Double> map = damageTracker.get(uuid);
      if (loadedMobs.containsKey(uuid)) {
        MobOptions mobOptions = loadedMobs.get(uuid);
        if (map.containsKey(player.getUniqueId()))
          if (mobOptions.resetPlayers) {
            Double playerDamage = map.get(player.getUniqueId());
            map.remove(player.getUniqueId());
            if (mobOptions.resetHeal) {
              LivingEntity a = (LivingEntity)Bukkit.getEntity(uuid);
              if (a != null) {
                double health = a.getHealth() + playerDamage.doubleValue();
                double maxHP = a.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                a.setHealth(Math.min(health, maxHP));
              } 
            } 
          }  
      } 
    } 
  }
}
