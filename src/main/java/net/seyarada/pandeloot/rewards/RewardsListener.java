package net.seyarada.pandeloot.rewards;

import net.seyarada.pandeloot.Config;
import net.seyarada.pandeloot.StringLib;
import net.seyarada.pandeloot.damage.DamageTracker;
import net.seyarada.pandeloot.damage.DamageUtil;
import net.seyarada.pandeloot.damage.MobOptions;
import net.seyarada.pandeloot.drops.DropEffects;
import net.seyarada.pandeloot.drops.DropItem;
import net.seyarada.pandeloot.drops.DropManager;
import net.seyarada.pandeloot.items.LootBag;
import net.seyarada.pandeloot.nms.NMSManager;
import net.seyarada.pandeloot.utils.ChatUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class RewardsListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(e.getAction().equals(Action.PHYSICAL)) return;

        if(e.getHand().equals(EquipmentSlot.HAND)) {

            ItemStack iS = e.getPlayer().getInventory().getItemInMainHand();
            if(NMSManager.hasTag(iS, StringLib.bag)) {
                String lootBag = NMSManager.getTag(iS, StringLib.bag);
                Location fLoc = e.getPlayer().getLocation();

                iS.setAmount(iS.getAmount()-1);

                List<RewardLine> rewards = RewardLine.StringListToRewardList(Config.getLootBagRaw(lootBag).getStringList("Rewards"));
                new DropManager(e.getPlayer(), fLoc, rewards).initDrops();

                return;

            }

            if(e.getClickedBlock()==null) return;

            Location loc = e.getClickedBlock().getLocation();
            for(Entity i : loc.getWorld().getNearbyEntities(loc, 1.5, 1.5 ,1.5)) {
                if(i instanceof Item) {

                    if ( NMSManager.hasTag(((Item)i).getItemStack(), StringLib.bag) ) {
                        if(NMSManager.hasTag(((Item)i).getItemStack(), StringLib.onUse)) continue;

                        String lootBag = NMSManager.getTag(((Item)i).getItemStack(), StringLib.bag);
                        LootBag LootBag = new LootBag(Config.getLootBagRaw(lootBag), new RewardLine(lootBag));

                        LootBag.doGroundDrop(e.getPlayer(), (Item)i);
                    }
                }
            }
        }

    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            ItemStack iS = e.getItem().getItemStack();

            if (NMSManager.hasTag(iS, StringLib.preventPickup) || NMSManager.hasTag(iS, StringLib.onUse)) {
                e.setCancelled(true);
                return;
            }

            if(NMSManager.hasTag(iS, StringLib.root) && !NMSManager.getTag(iS, StringLib.root).equals(player.getName())) {
                e.setCancelled(true);
                return;
            }

            if(NMSManager.hasTag(iS, StringLib.playOnPickup)) {
                DropItem source = DropEffects.playOnPickupStorage.get(UUID.fromString(NMSManager.getTag(iS, StringLib.playOnPickup)));
                if(source.reward.playonpickup=true)
                    source.player = player;
                new DropEffects(source, true);
            }

            iS = NMSManager.removeNBT(iS, StringLib.playOnPickup);
            e.getItem().setItemStack( iS );
            iS = NMSManager.removeNBT(iS, StringLib.preventStack);
            e.getItem().setItemStack( iS );
            iS = NMSManager.removeNBT(iS, StringLib.root);
            e.getItem().setItemStack( iS );
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSpawn(EntitySpawnEvent e) {
        ConfigurationSection mobConfig = Config.getMob(e.getEntity());
        if(mobConfig!=null) {
            MobOptions mobOptions = new MobOptions();
            mobOptions.resetPlayers = mobConfig.getBoolean("Options.ResetPlayers");
            mobOptions.resetHeal = mobConfig.getBoolean("Options.ResetHeal");
            DamageTracker.loadedMobs.put(e.getEntity().getUniqueId(), mobOptions);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(EntityDeathEvent e) {
        Entity mob = e.getEntity();
        UUID uuid = mob.getUniqueId();

        // Don't drop if the mob isn't loaded or nobody has damaged it
        if(!DamageTracker.loadedMobs.containsKey(uuid)) return;
        DamageTracker.loadedMobs.remove(uuid);
        if(!DamageTracker.damageTracker.containsKey(uuid)) return;
        if(DamageTracker.get(uuid).size()==0) return;

        ConfigurationSection config = Config.getMob(mob);
        boolean rank = config.getBoolean("Options.ScoreMessage");
        boolean score = config.getBoolean("Options.ScoreHologram");

        List<String> strings = config.getStringList("Rewards");
        List<RewardLine> rewards = RewardLine.StringListToRewardList(strings);

        DamageUtil damageUtil = new DamageUtil(uuid);
        if(DamageTracker.lastHits.containsKey(uuid)) {
            damageUtil.lastHit = DamageTracker.lastHits.get(uuid);
            DamageTracker.lastHits.remove(uuid);
        }
        DropManager manager = new DropManager(Arrays.asList(damageUtil.getPlayers()), rewards);

        if(rank) ChatUtil.announceChatRank(damageUtil);
        if(score) NMSManager.spawnHologram(damageUtil);
        manager.setDamageUtil(damageUtil);
        manager.initDrops();
    }

}
