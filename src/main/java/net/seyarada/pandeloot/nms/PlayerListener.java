package net.seyarada.pandeloot.nms;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		NMSManager.injectPlayer(e.getPlayer());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		NMSManager.removePlayer(e.getPlayer());
	}

	@EventHandler
	public void onItemRemove(EntityPickupItemEvent e) {
		NMSManager.hideItemFromPlayerMap.remove(e.getItem().getEntityId());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onItemDeath(EntityDamageEvent e) {
		if (e.getEntity() instanceof Item && NMSManager.hideItemFromPlayerMap.containsKey(e.getEntity().getEntityId())) {
			e.setCancelled(true);
		}
	}
}
