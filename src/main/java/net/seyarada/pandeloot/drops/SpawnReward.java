package net.seyarada.pandeloot.drops;

import java.util.Map;
import java.util.UUID;
import net.seyarada.pandeloot.StringLib;
import net.seyarada.pandeloot.items.LootBalloon;
import net.seyarada.pandeloot.nms.NMSManager;
import net.seyarada.pandeloot.rewards.Reward;
import net.seyarada.pandeloot.rewards.RewardLine;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpawnReward {
  public SpawnReward(Reward reward, Location location) {
    RewardLine rewardLine = reward.rewardLine;
    Player player = reward.player;
    ItemStack itemToDrop = reward.getItemStack(player);
    if (itemToDrop == null)
      if (rewardLine.origin.equals("lootballoon") || rewardLine.origin.equals("lbn")) {
        for (int i = 0; i < reward.rewardLine.amount; i++)
          (new LootBalloon(rewardLine.item, reward)).spawnBalloon(location); 
        return;
      }  
    StringLib.warn("+++++ Preparing to spawn the item...");
    if (itemToDrop == null) {
      StringLib.badItemStack(rewardLine);
      return;
    } 
    if (itemToDrop.getType() == Material.AIR)
      return; 
    itemToDrop.setAmount(rewardLine.amount);
    if (player != null) {
      boolean shouldGoToInventory = Boolean.parseBoolean((String)reward.options.get("toinventory"));
      if (shouldGoToInventory && player.getInventory().firstEmpty() >= 0) {
        player.getInventory().addItem(new ItemStack[] { itemToDrop });
        return;
      } 
    } 
    if (!Boolean.parseBoolean((String)reward.options.get("stackable")))
      itemToDrop = NMSManager.addNBT(itemToDrop, "PandeLoot.preventStack", UUID.randomUUID().toString()); 
    if (Boolean.parseBoolean((String)reward.options.get("preventpickup")))
      itemToDrop = NMSManager.addNBT(itemToDrop, "PandeLoot.preventPickUp", "true"); 
    if (Boolean.parseBoolean((String)reward.options.get("playonpickup")))
      itemToDrop = NMSManager.addNBT(itemToDrop, "PandeLoot.playOnPickup", buildBaseLineFromReward(reward)); 
    if (reward.options.get("skin") != null)
      itemToDrop = NMSManager.addNBT(itemToDrop, "PandeLoot.Skin", buildBaseLineFromReward(reward)); 
    if (player != null)
      itemToDrop = NMSManager.addNBT(itemToDrop, "PandeLoot", player.getName()); 
    StringLib.warn("+++++ Reward spawned!" + itemToDrop);
    reward.item = location.getWorld().dropItemNaturally(location, itemToDrop);
    StringLib.warn("+++++ Reward spawned!");
  }
  
  private String buildBaseLineFromReward(Reward reward) {
    StringBuilder a = new StringBuilder(reward.rewardLine.origin + ":" + reward.rewardLine.origin);
    if (reward.options.size() > 1) {
      a.append("{");
      for (Map.Entry<String, String> optionKV : (Iterable<Map.Entry<String, String>>)reward.options.entrySet())
        a.append((String)optionKV.getKey() + "=" + (String)optionKV.getValue() + ";"); 
      a.append("}");
    } 
    return a.toString();
  }
}
