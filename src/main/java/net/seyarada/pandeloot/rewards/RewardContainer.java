package net.seyarada.pandeloot.rewards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.seyarada.pandeloot.Config;
import net.seyarada.pandeloot.StringLib;
import net.seyarada.pandeloot.options.Conditions;
import org.bukkit.configuration.ConfigurationSection;

public class RewardContainer {
  public ConfigurationSection rewardContainer;
  
  public List<String> rewards;
  
  public Reward reward;
  
  public String internalName;
  
  int totalItems;
  
  int minItems;
  
  int maxItems;
  
  int guaranteedItems;
  
  int rewardSize;
  
  int goalAmount;
  
  public RewardContainer(String internalName, Reward reward) {
    if (internalName == null) {
      StringLib.warn("+ NULL REWARD CONTAINER CREATED");
      return;
    } 
    this.internalName = internalName;
    this.rewardContainer = Config.getRewardContainer(internalName);
    this.reward = reward;
    this.rewards = this.rewardContainer.getStringList("Rewards");
    this.rewardSize = this.rewards.size();
    this.guaranteedItems = Math.min(this.rewardContainer.getInt("Guaranteed"), this.rewardSize);
    this.totalItems = Math.min(this.rewardContainer.getInt("TotalItems"), this.rewardSize);
    this.minItems = Math.min(this.rewardContainer.getInt("MinItems"), this.rewardSize);
    this.maxItems = Math.min(this.rewardContainer.getInt("MaxItems"), this.rewardSize);
    this.goalAmount = generateGoalAmount();
    StringLib.warn("++ Guaranteed: " + this.guaranteedItems);
    StringLib.warn("++ TotalItems: " + this.totalItems);
    StringLib.warn("++ MinItems: " + this.minItems);
    StringLib.warn("++ MaxItems: " + this.maxItems);
    StringLib.warn("++ GoalAmount: " + this.goalAmount);
  }
  
  private int generateGoalAmount() {
    return Math.max(this.totalItems, Math.max(this.minItems, this.maxItems));
  }
  
  public List<RewardLine> getDrops() {
    List<RewardLine> drops = new ArrayList<>();
    if (this.goalAmount > 0) {
      List<RewardLine> items = getRawDropsParsingConditions(false);
      List<RewardLine> itemsIgnoreChance = getRawDropsParsingConditions(true);
      List<String> recurrentBaseLines = (List<String>)itemsIgnoreChance.stream().map(rL -> rL.baseLine).collect(Collectors.toList());
      drops.addAll(items);
      StringLib.warn("+ Getting drops of goal amount");
      while (this.minItems > 0 && drops.size() < this.minItems) {
        RewardLine item = doRoll(itemsIgnoreChance);
        List<String> lS = new ArrayList<>();
        for (RewardLine m : drops)
          lS.add(m.baseLine); 
        if (item != null && Collections.frequency(lS, item.baseLine) < Collections.frequency(recurrentBaseLines, item.baseLine))
          drops.add(item); 
      } 
      while (this.maxItems > 0 && drops.size() > this.maxItems)
        drops.remove((int)(Math.random() * drops.size())); 
      while (this.totalItems > 0 && drops.size() != this.totalItems) {
        if (drops.size() < this.totalItems) {
          RewardLine item = doRoll(itemsIgnoreChance);
          List<String> lS = new ArrayList<>();
          for (RewardLine m : drops)
            lS.add(m.baseLine); 
          if (item != null && Collections.frequency(lS, item.baseLine) < Collections.frequency(recurrentBaseLines, item.baseLine))
            drops.add(item); 
          continue;
        } 
        drops.remove((int)(Math.random() * drops.size()));
      } 
    } else if (this.guaranteedItems > 0) {
      List<RewardLine> items = getRawDropsParsingConditions(false);
      List<RewardLine> itemsIgnoreChance = getRawDropsParsingConditions(true);
      drops.addAll(items);
      StringLib.warn("+ Getting drops of guaranteed");
      while (drops.size() < this.guaranteedItems && itemsIgnoreChance.size() > 0) {
        RewardLine item = doRoll(itemsIgnoreChance);
        List<String> lS = new ArrayList<>();
        for (RewardLine m : drops)
          lS.add(m.baseLine); 
        if (item != null && !lS.contains(item.baseLine))
          drops.add(item); 
      } 
    } else {
      List<RewardLine> items = getRawDropsParsingConditions(false);
      StringLib.warn("+ Getting all drops");
      drops.addAll(items);
    } 
    return drops;
  }
  
  public static void addRewardContainerOptions(RewardLine rewardLine, ConfigurationSection rewardContainer) {
    if (rewardContainer == null) {
      StringLib.warn("++++++ RewardContainer is null, skipping");
      return;
    } 
    ConfigurationSection optionsContainer = rewardContainer.getConfigurationSection("Options");
    if (optionsContainer == null) {
      StringLib.warn("++++++ OptionsContainer is null, skipping");
      return;
    } 
    for (String str : optionsContainer.getKeys(false)) {
      StringLib.warn("+++++++ Adding " + str + " with value " + optionsContainer.getString(str) + " to " + rewardLine.baseLine);
      rewardLine.insideOptions.put(str.toLowerCase(), optionsContainer.getString(str));
    } 
  }
  
  public static void addRewardContainerOptions(Reward rewardLine, ConfigurationSection rewardContainer) {
    if (rewardContainer == null) {
      StringLib.warn("++++++ RewardContainer is null, skipping");
      return;
    } 
    for (String str : rewardContainer.getKeys(false)) {
      String capitalizedStr = str;
      str = str.toLowerCase();
      if (Config.defaultOptions.containsKey(str) && (
        (String)Config.defaultOptions.get(str)).equals(rewardLine.options.get(str))) {
        StringLib.warn("+++++++ Adding " + str + " with value " + rewardContainer.getString(capitalizedStr) + " to " + rewardLine.rewardLine.baseLine);
        rewardLine.options.put(str, rewardContainer.getString(capitalizedStr));
      } 
    } 
  }
  
  private RewardLine doRoll(List<RewardLine> items) {
    if (items.size() == 0)
      return null; 
    RewardLine[] rollItems = items.<RewardLine>toArray(new RewardLine[0]);
    double totalWeight = 0.0D;
    for (RewardLine i : items) {
      i.build(this.reward.player, this.reward.damageUtil);
      StringLib.warn("+ Weight of " + i.baseLine + " is " + i.getChance(this.reward.damageUtil, this.reward.player));
      totalWeight += i.getChance(this.reward.damageUtil, this.reward.player);
    } 
    int idx = 0;
    for (double r = Math.random() * totalWeight; idx < rollItems.length - 1; idx++) {
      r -= rollItems[idx].getChance(this.reward.damageUtil, this.reward.player);
      if (r <= 0.0D)
        break; 
    } 
    items.remove(idx);
    RewardLine item = rollItems[idx];
    item.skipConditions = true;
    return item;
  }
  
  private List<RewardLine> getRawDrops() {
    List<RewardLine> drops = new ArrayList<>();
    for (String rewardString : this.rewards) {
      RewardLine rewardLine = new RewardLine(rewardString);
      rewardLine.build(this.reward.player, this.reward.damageUtil);
      addRewardContainerOptions(rewardLine, this.rewardContainer);
      drops.add(rewardLine);
    } 
    return drops;
  }
  
  private List<RewardLine> getRawDropsParsingConditions(boolean ignoreChance) {
    List<RewardLine> drops = new ArrayList<>();
    List<RewardLine> rewardLines = getRawDrops();
    List<Reward> rewardsList = this.reward.createNewRewards(rewardLines);
    if (ignoreChance)
      for (Reward reward : rewardsList)
        reward.options.put("ignorechance", "true");  
    rewardsList.removeIf(i -> (Conditions.getResult(i) && !i.skipConditions));
    for (Reward item : rewardsList) {
      item.rewardLine.chance = "1";
      drops.add(item.rewardLine);
    } 
    return drops;
  }
}
