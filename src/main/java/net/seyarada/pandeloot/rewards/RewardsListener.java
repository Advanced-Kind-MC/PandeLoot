package net.seyarada.pandeloot.rewards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import net.seyarada.pandeloot.Config;
import net.seyarada.pandeloot.StringLib;
import net.seyarada.pandeloot.damage.DamageTracker;
import net.seyarada.pandeloot.damage.DamageUtil;
import net.seyarada.pandeloot.damage.MobOptions;
import net.seyarada.pandeloot.drops.StartDrops;
import net.seyarada.pandeloot.items.LootBag;
import net.seyarada.pandeloot.nms.NMSManager;
import net.seyarada.pandeloot.options.OptionType;
import net.seyarada.pandeloot.options.Options;
import net.seyarada.pandeloot.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class RewardsListener implements Listener {
  static Map<UUID, Long> preventUseWhenDrop = new HashMap<>();
  
  @EventHandler
  public void onPlayerDropItem(PlayerDropItemEvent e) {
    if (NMSManager.hasTag(e.getItemDrop().getItemStack(), "PandeLoot.LootBag"))
      preventUseWhenDrop.put(e.getPlayer().getUniqueId(), Long.valueOf(System.currentTimeMillis())); 
  }
  
  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent e) {
    if (e.getAction() == Action.PHYSICAL)
      return; 
    if (e.getHand() == EquipmentSlot.HAND) {
      UUID uuid = e.getPlayer().getUniqueId();
      if (preventUseWhenDrop.containsKey(uuid)) {
        long oldTime = ((Long)preventUseWhenDrop.get(uuid)).longValue();
        if (System.currentTimeMillis() - oldTime < 100L) {
          preventUseWhenDrop.remove(uuid);
          return;
        } 
      } 
      ItemStack iS = e.getPlayer().getInventory().getItemInMainHand();
      if (NMSManager.hasTag(iS, "PandeLoot.LootBag")) {
        String lootBag = NMSManager.getTag(iS, "PandeLoot.LootBag");
        iS.setAmount(iS.getAmount() - 1);
        Reward reward = new Reward(new RewardLine("lootbag:" + lootBag), e.getPlayer(), null);
        List<RewardLine> rewardsStringList = (new RewardContainer(lootBag, reward)).getDrops();
        List<String> rewards = (List<String>)rewardsStringList.stream().map(rL -> rL.baseLine).collect(Collectors.toList());
        new StartDrops(Collections.singletonList(e.getPlayer()), rewards, null, null);
        e.setCancelled(true);
        return;
      } 
      if (e.getClickedBlock() == null)
        return; 
      Location loc = e.getClickedBlock().getLocation();
      for (Entity i : loc.getWorld().getNearbyEntities(loc, 1.5D, 1.5D, 1.5D)) {
        if (i instanceof Item) {
          if (NMSManager.hasTag(((Item)i).getItemStack(), "PandeLoot")) {
            StringLib.warn("+ " + NMSManager.getTag(((Item)i).getItemStack(), "PandeLoot"));
            if (!NMSManager.getTag(((Item)i).getItemStack(), "PandeLoot").equals(e.getPlayer().getName()))
              continue; 
          } 
          if (!NMSManager.hasTag(((Item)i).getItemStack(), "PandeLoot.LootBag") || 
            NMSManager.hasTag(((Item)i).getItemStack(), "PandeLoot.onUse"))
            continue; 
          String lootBag = NMSManager.getTag(((Item)i).getItemStack(), "PandeLoot.LootBag");
          LootBag LootBag = new LootBag(lootBag, new Reward(new RewardLine("air"), e.getPlayer(), null));
          LootBag.doGroundDrop(e.getPlayer(), (Item)i);
        } 
      } 
    } 
  }
  
  @EventHandler
  public void onPickup(EntityPickupItemEvent e) {
    if (e.getEntity() instanceof Player) {
      Player player = (Player)e.getEntity();
      ItemStack iS = e.getItem().getItemStack();
      if (NMSManager.hasTag(iS, "PandeLoot") && !NMSManager.getTag(iS, "PandeLoot").equals(player.getName())) {
        e.setCancelled(true);
        return;
      } 
      if (NMSManager.hasTag(iS, "PandeLoot.playOnPickup")) {
        StringLib.warn("+ Applying playOnPickup");
        RewardLine a = new RewardLine(NMSManager.getTag(iS, "PandeLoot.playOnPickup"));
        Reward b = new Reward(a, player, null);
        b.item = e.getItem();
        Options.callOptions(b, OptionType.GENERAL);
        Options.callOptions(b, OptionType.PLAYER);
      } 
      if (NMSManager.hasTag(iS, "PandeLoot.preventPickUp") || NMSManager.hasTag(iS, "PandeLoot.onUse")) {
        StringLib.warn("+ Applying preventPickup");
        e.setCancelled(true);
        return;
      } 
      if (NMSManager.hasTag(iS, "PandeLoot.Skin")) {
        RewardLine a = new RewardLine(NMSManager.getTag(iS, "PandeLoot.Skin"));
        Reward b = new Reward(a, player, null);
        b.options.put("skin", null);
        b.getItemStack(player);
        iS = b.getItemStack(player);
        iS.setAmount(a.amount);
      } 
      iS = NMSManager.removeNBT(iS, "PandeLoot.playOnPickup");
      iS = NMSManager.removeNBT(iS, "PandeLoot.preventStack");
      iS = NMSManager.removeNBT(iS, "PandeLoot");
      e.getItem().setItemStack(iS);
      player.updateInventory();
    } 
  }
  
  @EventHandler(priority = EventPriority.MONITOR)
  public void onSpawn(EntitySpawnEvent e) {
    ConfigurationSection mobConfig = Config.getMob(e.getEntity());
    if (mobConfig != null) {
      MobOptions mobOptions = new MobOptions();
      mobOptions.resetPlayers = mobConfig.getBoolean("Options.ResetPlayers");
      mobOptions.resetHeal = mobConfig.getBoolean("Options.ResetHeal");
      DamageTracker.loadedMobs.put(e.getEntity().getUniqueId(), mobOptions);
    } 
  }
  
  @EventHandler(priority = EventPriority.MONITOR)
  public void onDeath(EntityDeathEvent e) {
    LivingEntity livingEntity = e.getEntity();
    UUID uuid = livingEntity.getUniqueId();
    StringLib.warn("+ Detected death of mob: " + livingEntity);
    if (!DamageTracker.loadedMobs.containsKey(uuid))
      return; 
    DamageTracker.loadedMobs.remove(uuid);
    if (!DamageTracker.damageTracker.containsKey(uuid))
      return; 
    if (DamageTracker.get(uuid).size() == 0)
      return; 
    StringLib.warn("++ Starting lootbag drop...");
    ConfigurationSection config = Config.getMob((Entity)livingEntity);
    if (config == null)
      return; 
    boolean rank = config.getBoolean("Options.ScoreMessage");
    boolean score = config.getBoolean("Options.ScoreHologram");
    List<String> stringRewards = config.getStringList("Rewards");
    DamageUtil damageUtil = new DamageUtil(uuid);
    StringLib.warn("+++ The option rank is: " + rank);
    StringLib.warn("+++ The option score is: " + score);
    StringLib.warn("++ The Rewards to drop are:");
    for (String reward : stringRewards)
      StringLib.warn("+++ " + reward); 
    if (DamageTracker.lastHits.containsKey(uuid)) {
      damageUtil.lastHit = (UUID)DamageTracker.lastHits.get(uuid);
      StringLib.warn("++ Stored " + Bukkit.getPlayer((UUID)DamageTracker.lastHits.get(uuid)).getName() + " as lasthit");
      DamageTracker.lastHits.remove(uuid);
    } 
    List<Player> dropPlayers = new ArrayList<>();
    for (UUID playerUUID : damageUtil.getPlayers())
      dropPlayers.add(Bukkit.getPlayer(playerUUID)); 
    new StartDrops(dropPlayers, stringRewards, damageUtil, livingEntity.getLocation());
    if (rank)
      ChatUtil.announceChatRank(damageUtil); 
    if (score)
      NMSManager.spawnHologram(damageUtil); 
  }
}
