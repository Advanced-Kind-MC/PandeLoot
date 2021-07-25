package net.seyarada.pandeloot.compatibility.mythicmobs;

import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitItemStack;
import io.lumine.xikage.mythicmobs.drops.Drop;
import io.lumine.xikage.mythicmobs.drops.DropMetadata;
import io.lumine.xikage.mythicmobs.drops.IMultiDrop;
import io.lumine.xikage.mythicmobs.drops.LootBag;
import io.lumine.xikage.mythicmobs.drops.droppables.ItemDrop;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.SkillCaster;
import java.util.Collections;
import java.util.UUID;
import net.seyarada.pandeloot.damage.DamageTracker;
import net.seyarada.pandeloot.damage.DamageUtil;
import net.seyarada.pandeloot.drops.Manager;
import net.seyarada.pandeloot.rewards.RewardLine;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class MythicMobsDrop extends Drop implements IMultiDrop, Listener {
  private final MythicLineConfig config;
  
  public MythicMobsDrop(MythicLineConfig config) {
    super(config.getLine(), config);
    this.config = config;
  }
  
  public LootBag get(DropMetadata metadata) {
    String i = this.config.getLine();
    int j = i.indexOf("{") + 1;
    int k = i.lastIndexOf("}");
    i = i.substring(j, k);
    RewardLine lineConfig = new RewardLine(i);
    Location location = BukkitAdapter.adapt(((SkillCaster)metadata.getDropper().get()).getLocation());
    Player p = (Player)BukkitAdapter.adapt(metadata.getCause().get());
    UUID uuid = ((SkillCaster)metadata.getDropper().get()).getEntity().getUniqueId();
    if (DamageTracker.loadedMobs.containsKey(uuid))
      (new Manager()).fromRewardLine(Collections.singletonList(p), Collections.singletonList(lineConfig), new DamageUtil(uuid), location); 
    LootBag loot = new LootBag(metadata);
    ItemStack item = new ItemStack(Material.AIR, 1);
    loot.add((Drop)new ItemDrop(getLine(), (MythicLineConfig)getConfig(), new BukkitItemStack(item)));
    return loot;
  }
}
