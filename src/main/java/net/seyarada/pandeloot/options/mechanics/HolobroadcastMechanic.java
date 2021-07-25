package net.seyarada.pandeloot.options.mechanics;

import net.mystipvp.holobroadcast.holograms.HologramPlayer;
import net.mystipvp.holobroadcast.holograms.HologramPlayersManager;
import net.seyarada.pandeloot.StringLib;
import net.seyarada.pandeloot.options.MechanicEvent;
import net.seyarada.pandeloot.rewards.Reward;
import org.bukkit.Bukkit;

public class HolobroadcastMechanic implements MechanicEvent {
  public void onCall(Reward reward, String value) {
    if (value != null && !value.isEmpty() && Bukkit.getServer().getPluginManager().getPlugin("HoloBroadcast") != null) {
      StringLib.warn("++++++ Applying holobroadcast effect with value " + value);
      HologramPlayersManager manager = HologramPlayersManager.getInstance();
      HologramPlayer holoPlayer = manager.getHologramPlayerFromUUID(reward.player.getUniqueId());
      holoPlayer.showHUD(value, -1L);
    } 
  }
}
