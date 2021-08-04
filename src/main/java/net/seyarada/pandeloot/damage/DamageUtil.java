package net.seyarada.pandeloot.damage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class DamageUtil {
  private final double totalHP;
  
  private final LinkedList<Map.Entry<UUID, Double>> rankedPlayers;
  
  private final Map<UUID, Double> playerDamage;
  
  private final UUID[] player;
  
  private final Double[] damage;
  
  private final UUID uuid;
  
  public UUID lastHit;
  
  public final Entity entity;
  
  private final Location location;
  
  public DamageUtil(UUID uuid) {
    this.uuid = uuid;
    this.entity = Bukkit.getEntity(uuid);
    this.playerDamage = DamageTracker.get(uuid);
    this.totalHP = this.playerDamage.values().stream().mapToDouble(Double::valueOf).sum();
    Comparator<Map.Entry<UUID, Double>> comparator = Map.Entry.comparingByValue();
    LinkedList<Map.Entry<UUID, Double>> linkedList = new LinkedList<>(this.playerDamage.entrySet());
    linkedList.sort(comparator.reversed());
    this.rankedPlayers = linkedList;
    List<UUID> players = new ArrayList<>();
    List<Double> damages = new ArrayList<>();
    for (Map.Entry<UUID, Double> i : this.rankedPlayers) {
      players.add(i.getKey());
      damages.add(i.getValue());
    } 
    this.player = players.<UUID>toArray(new UUID[0]);
    this.damage = damages.<Double>toArray(new Double[0]);
    this.location = this.entity.getLocation();
  }
  
  public double getTotalHP() {
    return this.totalHP;
  }
  
  public LinkedList<Map.Entry<UUID, Double>> getRankedPlayers() {
    return this.rankedPlayers;
  }
  
  public UUID[] getPlayers() {
    return this.player;
  }
  
  public double getPlayerDamage(Player player) {
    return ((Double)this.playerDamage.get(player.getUniqueId())).doubleValue();
  }

  public Player getPlayer(int index) {
    return Bukkit.getPlayer(this.player[index]);
  }

  public OfflinePlayer getOfflinePlayer(int index) {
    return Bukkit.getOfflinePlayer(this.player[index]);
  }
  
  public double getDamage(int index) {
    double dmg = this.damage[index].doubleValue() / getTotalHP() * 100.0D;
    DecimalFormat df = new DecimalFormat("#.##");
    return Double.parseDouble(df.format(dmg).replace(",", "."));
  }
  
  public Player getLasthit() {
    return Bukkit.getPlayer(this.lastHit);
  }
  
  public int getPlayerRank(Player p) {
    int k = 1;
    for (UUID i : this.player) {
      if (i.equals(p.getUniqueId()))
        return k; 
      k++;
    } 
    return 1;
  }
  
  public double getEntityHealth() {
    return ((LivingEntity)this.entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
  }
  
  public String getEntityName() {
    if (this.entity.getCustomName() != null)
      return this.entity.getCustomName(); 
    return this.entity.getName();
  }
  
  public double getPercentageDamage(Player player) {
    return ((Double)this.playerDamage.get(player.getUniqueId())).doubleValue() / this.totalHP;
  }
  
  public Location getLocation() {
    return this.location;
  }
  
  public UUID getUUID() {
    return this.uuid;
  }
}
