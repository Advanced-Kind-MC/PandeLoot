package net.seyarada.pandeloot.damage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;

public class DamageUtil {

    private final double totalHP;
    private final LinkedList<Map.Entry<Player, Double>> rankedPlayers;
    private final Map<Player, Double> playerDamage;
    private final Player[] player;
    private final Double[] damage;
    private final UUID uuid;

    public final Entity entity;

    private final Location location;

    public DamageUtil(UUID uuid) {

        this.uuid = uuid;
        this.entity = Bukkit.getEntity(uuid);
        playerDamage = DamageTracker.get(uuid);
        totalHP = playerDamage.values().stream().mapToDouble(Double::valueOf).sum();

        // Gives a sorted list of players with more damage -> players with less damage
        Comparator<Map.Entry<Player, Double>> comparator = Map.Entry.comparingByValue();
        LinkedList<Map.Entry<Player, Double>> linkedList = new LinkedList<>(playerDamage.entrySet());
        linkedList.sort(comparator.reversed());
        rankedPlayers = linkedList;

        List<Player> players = new ArrayList<>();
        List<Double> damages = new ArrayList<>();

        for(Map.Entry<Player, Double> i : rankedPlayers) {
            players.add(i.getKey());
            damages.add(i.getValue());
        }

        player = players.toArray(new Player[0]);
        damage = damages.toArray(new Double[0]);

        location = entity.getLocation();

    }

    public double getTotalHP() {
        return totalHP;
    }

    public LinkedList<Map.Entry<Player, Double>> getRankedPlayers() {
        return rankedPlayers;
    }

    public Player[] getPlayers() {
        return player;
    }

    public double getPlayerDamage(Player player) {
        return playerDamage.get(player);
    }

    public Player getPlayer(int index) {
        return player[index];
    }

    public double getDamage(int index) {
        double dmg = ( damage[index] / getTotalHP() ) * 100.0;
        DecimalFormat df = new DecimalFormat("#.##");

        return Double.parseDouble(df.format(dmg));
    }

    public int getPlayerRank(Player p) {
        int k = 1;
        for(Player i : player) {
            if(i.equals(p))
                return k;
            k++;
        }
        return 1;
    }

    public double getEntityHealth() {
        return ((LivingEntity) entity ).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
    }

    public String getEntityName() {
        if(entity.getCustomName()!=null)
            return entity.getCustomName();
        return entity.getName();
    }

    public double getPercentageDamage(Player player) {
        return playerDamage.get(player)/totalHP;
    }

    public Location getLocation() {
        return location;
    }

    public UUID getUUID() {
        return uuid;
    }

}
