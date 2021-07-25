package net.seyarada.pandeloot.rewards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.seyarada.pandeloot.Config;
import net.seyarada.pandeloot.StringLib;
import net.seyarada.pandeloot.compatibility.DenizenCompatibility;
import net.seyarada.pandeloot.compatibility.OraxenCompatibility;
import net.seyarada.pandeloot.compatibility.mmoitems.MIGeneratorCompatibility;
import net.seyarada.pandeloot.compatibility.mmoitems.MMOItemsCompatibility;
import net.seyarada.pandeloot.compatibility.mythicmobs.MythicMobsCompatibility;
import net.seyarada.pandeloot.damage.DamageUtil;
import net.seyarada.pandeloot.items.LootBag;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Reward {
  public final RewardLine rewardLine;
  
  public Player player;
  
  public final DamageUtil damageUtil;
  
  public final Map<String, String> specialOptions;
  
  public final Map<String, String> options = new HashMap<>(Config.defaultOptions);
  
  public Item item;
  
  public boolean isAbandoned;
  
  public boolean skipConditions;
  
  public ItemStack itemStack;
  
  public Map<Double, Integer> radialDropInformation;
  
  public int radialOrder;
  
  public int skip;
  
  public Reward(RewardLine line, Player player, DamageUtil util) {
    line.build(player, util);
    this.rewardLine = line;
    this.player = player;
    this.damageUtil = util;
    for (Map.Entry<String, String> entry : this.rewardLine.insideOptions.entrySet())
      this.options.put(((String)entry.getKey()).toLowerCase(), entry.getValue()); 
    this.specialOptions = this.rewardLine.specialOptions;
  }
  
  public ItemStack getItemStack(Player player) {
    Material material;
    if (this.itemStack != null)
      return this.itemStack; 
    String switchOrigin = this.rewardLine.origin;
    String switchItem = this.rewardLine.item;
    if (this.options.get("skin") != null) {
      String[] i = ((String)this.options.get("skin")).split(":");
      switchOrigin = i[0];
      switchItem = i[1];
    } 
    switch (switchOrigin) {
      case "minecraft":
      case "mc":
        try {
          material = Material.valueOf(switchItem.toUpperCase());
        } catch (Exception e) {
          StringLib.badItem(switchItem.toUpperCase());
          return new ItemStack(Material.AIR, 1);
        } 
        return new ItemStack(material, 1);
      case "lootbag":
      case "lb":
        return (new LootBag(switchItem, this)).getItemStack();
      case "mythicmobs":
      case "mm":
        return MythicMobsCompatibility.getItem(switchItem);
      case "mmoitems":
      case "mmoitem":
      case "mmoi":
      case "mi":
        return MMOItemsCompatibility.getItem(switchItem, this.rewardLine, player);
      case "mmoitemsgenerator":
      case "migenerator":
      case "migen":
        return MIGeneratorCompatibility.getItem(switchItem, this.rewardLine, player);
      case "oraxen":
        return OraxenCompatibility.getItem(switchItem);
      case "denizenitem":
      case "denizen":
      case "ditem":
        return DenizenCompatibility.getItem(switchItem);
    } 
    return null;
  }
  
  public String get(String option) {
    return this.options.get(option);
  }
  
  public List<Reward> createNewRewards(List<RewardLine> rewardLines) {
    List<Reward> rewards = new ArrayList<>();
    for (RewardLine rewardLine : rewardLines)
      rewards.add(new Reward(rewardLine, this.player, this.damageUtil)); 
    return rewards;
  }
  
  public static List<Reward> createNewRewards(List<RewardLine> rewardLines, Player player, DamageUtil damageUtil) {
    List<Reward> rewards = new ArrayList<>();
    for (RewardLine rewardLine : rewardLines)
      rewards.add(new Reward(rewardLine, player, damageUtil)); 
    return rewards;
  }
  
  public Reward createNewReward(String baseLine) {
    return new Reward(new RewardLine(baseLine), this.player, this.damageUtil);
  }
  
  public static List<Reward> rewardsFromStringList(List<String> stringRewardsList, Player player, DamageUtil damageUtil) {
    List<Reward> rewards = new ArrayList<>();
    for (String stringReward : stringRewardsList) {
      RewardLine rewardLine = new RewardLine(stringReward);
      Reward reward = new Reward(rewardLine, player, damageUtil);
      rewards.add(reward);
    } 
    return rewards;
  }
}
