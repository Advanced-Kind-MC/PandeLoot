package net.seyarada.pandeloot.options.conditions;

import java.util.Map;
import net.seyarada.pandeloot.StringLib;
import net.seyarada.pandeloot.damage.DamageUtil;
import net.seyarada.pandeloot.options.ConditionEvent;
import net.seyarada.pandeloot.rewards.Reward;
import org.bukkit.entity.Player;

public class TopCondition implements ConditionEvent {
  boolean result;
  
  public boolean onCall(Reward reward, String value) {
    if (reward.damageUtil == null)
      return true; 
    if (value == null)
      return true; 
    StringLib.warn("++++++ Condition: Top, Value: " + value);
    DamageUtil damageUtil = reward.damageUtil;
    Player player = reward.player;
    StringLib.warn("+++++++ The player to check has a top of: " + damageUtil.getPlayerRank(player));
    if (value.contains("to")) {
      StringLib.warn("+++++++ Top is ranged");
      String[] values = value.split("to");
      this
        .result = (Integer.parseInt(values[0]) <= damageUtil.getPlayerRank(player) && Integer.parseInt(values[1]) >= damageUtil.getPlayerRank(player));
      StringLib.warn("++++++++ Result is: " + this.result);
      return this.result;
    } 
    int intTop = Integer.parseInt(value) - 1;
    if (intTop >= 0) {
      if (damageUtil.getRankedPlayers().size() > intTop) {
        StringLib.warn("++++++++ Player at internal position " + intTop + " is " + ((Map.Entry)damageUtil.getRankedPlayers().get(intTop)).getKey());
        return (((Map.Entry)damageUtil.getRankedPlayers().get(intTop)).getKey() == player.getUniqueId());
      } 
      StringLib.warn("++++++++ Player at internal position " + intTop + " is not registered");
      return false;
    } 
    StringLib.warn("++++++++ Internal position " + intTop + " is <0, ignoring condition");
    return true;
  }
}
