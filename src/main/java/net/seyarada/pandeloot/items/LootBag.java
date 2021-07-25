package net.seyarada.pandeloot.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.seyarada.pandeloot.Config;
import net.seyarada.pandeloot.PandeLoot;
import net.seyarada.pandeloot.StringLib;
import net.seyarada.pandeloot.drops.Manager;
import net.seyarada.pandeloot.nms.NMSManager;
import net.seyarada.pandeloot.rewards.Reward;
import net.seyarada.pandeloot.rewards.RewardContainer;
import net.seyarada.pandeloot.rewards.RewardLine;
import net.seyarada.pandeloot.utils.PlaceholderUtil;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class LootBag extends RewardContainer {
  public static List<UUID> openedBags = new ArrayList<>();
  
  public LootBag(String internalName, Reward reward) {
    super(internalName, reward);
  }
  
  public void doGroundDrop(Player player, final Item item) {
    if (openedBags.contains(item.getUniqueId()))
      return; 
    openedBags.add(item.getUniqueId());
    StringLib.warn("+ Doing ground drop for lootbag " + this.internalName + ", from " + item.getUniqueId());
    List<RewardLine> drops = getDrops();
    StringLib.warn("+++ Got for the ground drop: ");
    for (RewardLine drop : drops)
      StringLib.warn("++++ " + drop.baseLine); 
    List<Reward> baseRewards = this.reward.createNewRewards(drops);
    List<Reward> rewards = new ArrayList<>();
    ItemUtils.collectRewards(baseRewards, rewards, 1, false);
    Manager manager = new Manager();
    manager.fromReward(Collections.singletonList(player), rewards, item.getLocation());
    playArm(player);
    int delay = manager.delay;
    player.spawnParticle(Particle.FIREWORKS_SPARK, item.getLocation(), 10, 0.0D, 0.15D, 0.0D, 0.3D);
    player.spawnParticle(Particle.SMOKE_LARGE, item.getLocation(), 5, 0.0D, 0.15D, 0.0D, 0.1D);
    if (delay > 0) {
      item.setItemStack(NMSManager.addNBT(item.getItemStack(), "PandeLoot.onUse", "true"));
      (new BukkitRunnable() {
          public void run() {
            item.getItemStack().setAmount(item.getItemStack().getAmount() - 1);
            if (item.getItemStack().getAmount() <= 0) {
              item.remove();
              LootBag.openedBags.remove(item.getUniqueId());
              return;
            } 
            LootBag.openedBags.remove(item.getUniqueId());
            item.setItemStack(NMSManager.removeNBT(item.getItemStack(), "PandeLoot.onUse"));
          }
        }).runTaskLater((Plugin)PandeLoot.getInstance(), delay);
    } else {
      item.getItemStack().setAmount(item.getItemStack().getAmount() - 1);
      if (item.getItemStack().getAmount() <= 0) {
        item.remove();
        openedBags.remove(item.getUniqueId());
        return;
      } 
      openedBags.remove(item.getUniqueId());
      item.setItemStack(NMSManager.removeNBT(item.getItemStack(), "PandeLoot.onUse"));
    } 
  }
  
  public ItemStack getItemStack() {
    if (this.rewardContainer == null)
      return new ItemStack(Material.AIR, 1); 
    String display = this.rewardContainer.getString("Display");
    String material = this.rewardContainer.getString("Material");
    int model = this.rewardContainer.getInt("Model");
    ItemStack itemStack = new ItemStack(Material.valueOf(material.toUpperCase()), 1);
    ItemMeta meta = itemStack.getItemMeta();
    meta.setDisplayName(PlaceholderUtil.parse(display));
    meta.setCustomModelData(Integer.valueOf(model));
    itemStack.setItemMeta(meta);
    return NMSManager.addNBT(itemStack, "PandeLoot.LootBag", this.reward.rewardLine.item);
  }
  
  private void playArm(Player player) {
    if (player == null)
      return; 
    if (Config.getPlayArm())
      if (Config.getPlayArmEmpty()) {
        Material mainHand = player.getInventory().getItemInMainHand().getType();
        Material offHand = player.getInventory().getItemInOffHand().getType();
        if (mainHand == Material.AIR && offHand == Material.AIR)
          player.swingMainHand(); 
      } else {
        player.swingMainHand();
      }  
  }
}
