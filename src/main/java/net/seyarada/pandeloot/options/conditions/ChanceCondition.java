package net.seyarada.pandeloot.options.conditions;

import net.seyarada.pandeloot.StringLib;
import net.seyarada.pandeloot.options.ConditionEvent;
import net.seyarada.pandeloot.rewards.Reward;

public class ChanceCondition implements ConditionEvent {
  boolean result;
  
  public boolean onCall(Reward reward, String value) {
    if (reward.options.containsKey("ignorechance")) {
      StringLib.warn("++++++ Chance has been force ignored");
      return true;
    } 
    double chanceValue = reward.rewardLine.getChance(reward.damageUtil, reward.player);
    if (chanceValue == 1.0D)
      return true; 
    StringLib.warn("++++++ Condition: Chance, Value: " + chanceValue);
    double r = Math.random();
    StringLib.warn("+++++++ Random generated: " + r);
    this.result = (r <= chanceValue);
    StringLib.warn("++++++++ Result is: " + this.result);
    return this.result;
  }
}
