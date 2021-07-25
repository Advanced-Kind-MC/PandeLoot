package net.seyarada.pandeloot.options.mechanics;

import net.milkbowl.vault.economy.Economy;
import net.seyarada.pandeloot.PandeLoot;
import net.seyarada.pandeloot.StringLib;
import net.seyarada.pandeloot.options.MechanicEvent;
import net.seyarada.pandeloot.rewards.Reward;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class MoneyMechanic implements MechanicEvent {
  public void onCall(Reward reward, String value) {
    StringLib.warn("+ Applying money effect with value " + value);
    if (value != null && !value.isEmpty()) {
      int money = Integer.parseInt(value);
      if (money > 0 && Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
        StringLib.warn("++++++ Applying money effect with value " + value);
        Economy economy = PandeLoot.getEconomy();
        economy.depositPlayer((OfflinePlayer)reward.player, money);
      } 
    } 
  }
}
