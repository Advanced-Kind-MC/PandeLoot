package net.seyarada.pandeloot.options.mechanics;

import net.seyarada.pandeloot.StringLib;
import net.seyarada.pandeloot.options.MechanicEvent;
import net.seyarada.pandeloot.rewards.Reward;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class CommandMechanic implements MechanicEvent {
  public void onCall(Reward reward, String value) {
    if (value != null && !value.isEmpty()) {
      StringLib.warn("++++++ Applying command effect with value " + value);
      Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), value);
    } 
  }
}
