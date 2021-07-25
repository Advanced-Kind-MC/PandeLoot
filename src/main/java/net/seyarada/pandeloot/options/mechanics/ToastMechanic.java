package net.seyarada.pandeloot.options.mechanics;

import java.util.Map;
import net.seyarada.pandeloot.StringLib;
import net.seyarada.pandeloot.nms.NMSManager;
import net.seyarada.pandeloot.options.MechanicEvent;
import net.seyarada.pandeloot.rewards.Reward;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ToastMechanic implements MechanicEvent {
  public void onCall(Reward reward, String value) {
    StringLib.warn("++++++ Applying toast effect with value " + value);
    if (value != null && !value.isEmpty()) {
      Map<String, String> options = reward.options;
      ItemStack icon = (options.get("toasticon") == null) ? new ItemStack(Material.STONE, 1) : new ItemStack(Material.valueOf(((String)options.get("toasticon")).toUpperCase()), 1);
      String frame = (options.get("toastframe") == null) ? "GOAL" : options.get("toastframe");
      NMSManager.toast(reward.player, value, frame, icon);
    } 
  }
}
