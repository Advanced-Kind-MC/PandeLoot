package net.seyarada.pandeloot.options.conditions;

import net.seyarada.pandeloot.options.ConditionEvent;
import net.seyarada.pandeloot.rewards.Reward;

public class DamageCondition implements ConditionEvent {
  public boolean onCall(Reward reward, String value) {
    if (reward.damageUtil == null)
      return true; 
    String requiredDamage = reward.rewardLine.damage;
    if (requiredDamage == null)
      return true; 
    double totalHP = reward.damageUtil.getTotalHP();
    double playerDamage = reward.damageUtil.getPlayerDamage(reward.player);
    if (requiredDamage.contains("%")) {
      requiredDamage = requiredDamage.replace("%", "");
      if (requiredDamage.contains("to"))
        return damageRanged(requiredDamage, playerDamage, totalHP, true); 
      return (playerDamage / totalHP * 100.0D >= Double.parseDouble(requiredDamage.replace("%", "")));
    } 
    if (requiredDamage.contains("to"))
      return damageRanged(requiredDamage, playerDamage, totalHP, false); 
    return (playerDamage >= Double.parseDouble(requiredDamage));
  }
  
  private static boolean damageRanged(String damageString, double playerDamage, double totalHP, boolean isPercentage) {
    String[] values = damageString.split("to");
    if (isPercentage)
      return (Double.parseDouble(values[0]) <= playerDamage / totalHP * 100.0D && 
        Double.parseDouble(values[1]) >= playerDamage / totalHP * 100.0D); 
    return (Double.parseDouble(values[0]) <= playerDamage && Double.parseDouble(values[1]) >= playerDamage);
  }
}
