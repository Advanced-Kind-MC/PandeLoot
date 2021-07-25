package net.seyarada.pandeloot.options.mechanics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.seyarada.pandeloot.StringLib;
import net.seyarada.pandeloot.nms.NMSManager;
import net.seyarada.pandeloot.options.MechanicEvent;
import net.seyarada.pandeloot.rewards.Reward;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HologramMechanic implements MechanicEvent {
  public void onCall(Reward reward, String value) {
    Map<String, String> options = reward.options;
    int abandonTime = Integer.parseInt(options.get("abandontime"));
    if (value != null && !value.isEmpty() && !value.equals("false")) {
      List<String> display, lore;
      StringLib.warn("++++++ Applying hologram effect with value " + value);
      String hologram = options.get("hologram");
      Item item = reward.item;
      Player player = reward.player;
      switch (hologram) {
        case "display":
        case "Display":
        case "DISPLAY":
          display = getItemDisplay(item);
          if (display != null)
            NMSManager.spawnLockedHologram((Entity)item, item.getLocation(), display, Collections.singletonList(player), abandonTime, reward); 
          return;
        case "lore":
        case "Lore":
        case "LORE":
          lore = getItemLore(item);
          if (!lore.isEmpty())
            NMSManager.spawnLockedHologram((Entity)item, item.getLocation(), lore, Collections.singletonList(player), abandonTime, reward); 
          return;
        case "full":
        case "Full":
        case "FULL":
          display = getItemDisplay(item);
          lore = getItemLore(item);
          if (lore == null)
            lore = new ArrayList<>(); 
          lore.addAll(display);
          NMSManager.spawnLockedHologram((Entity)item, item.getLocation(), lore, Collections.singletonList(player), abandonTime, reward);
          return;
      } 
      List<String> entries = Arrays.asList(hologram.split(","));
      NMSManager.spawnLockedHologram((Entity)item, item.getLocation(), entries, Collections.singletonList(player), abandonTime, reward);
    } else if (abandonTime > 0) {
      NMSManager.spawnLockedHologram((Entity)reward.item, reward.item.getLocation(), new ArrayList(), Collections.singletonList(reward.player), abandonTime, reward);
    } 
  }
  
  private List<String> getItemDisplay(Item item) {
    ItemStack itemStack = item.getItemStack();
    if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
      ArrayList<String> name = new ArrayList<>();
      name.add(itemStack.getItemMeta().getDisplayName());
      return name;
    } 
    return null;
  }
  
  private List<String> getItemLore(Item item) {
    ItemStack itemStack = item.getItemStack();
    if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore())
      return itemStack.getItemMeta().getLore(); 
    return new ArrayList<>();
  }
}
