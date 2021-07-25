package net.seyarada.pandeloot.compatibility.mythicmobs;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.ITargetedLocationSkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import java.util.Collections;
import net.seyarada.pandeloot.damage.DamageTracker;
import net.seyarada.pandeloot.damage.DamageUtil;
import net.seyarada.pandeloot.drops.StartDrops;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class MythicMobsMechanic extends SkillMechanic implements ITargetedLocationSkill, ITargetedEntitySkill {
  public MythicMobsMechanic(MythicLineConfig config) {
    super(config.getLine(), config);
    setAsyncSafe(false);
    setTargetsCreativePlayers(false);
  }
  
  public boolean castAtLocation(SkillMetadata skillMetadata, AbstractLocation abstractLocation) {
    String i = this.config.getLine();
    int j = i.indexOf("{") + 1;
    int k = i.lastIndexOf("}");
    i = i.substring(j, k);
    Location location = BukkitAdapter.adapt(abstractLocation);
    new StartDrops(null, Collections.singletonList(i), null, location);
    return false;
  }
  
  public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
    String i = this.config.getLine();
    int j = i.indexOf("{") + 1;
    int k = i.lastIndexOf("}");
    i = i.substring(j, k);
    Entity entity = abstractEntity.getBukkitEntity();
    DamageUtil damageUtil = null;
    if (DamageTracker.loadedMobs.containsKey(skillMetadata.getCaster().getEntity().getUniqueId()))
      damageUtil = new DamageUtil(skillMetadata.getCaster().getEntity().getUniqueId()); 
    if (entity instanceof Player) {
      new StartDrops(Collections.singletonList((Player)entity), Collections.singletonList(i), damageUtil, entity.getLocation());
    } else {
      new StartDrops(null, Collections.singletonList(i), damageUtil, entity.getLocation());
    } 
    return false;
  }
}
