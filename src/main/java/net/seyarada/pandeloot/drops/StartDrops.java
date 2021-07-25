package net.seyarada.pandeloot.drops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.seyarada.pandeloot.PandeLoot;
import net.seyarada.pandeloot.StringLib;
import net.seyarada.pandeloot.damage.DamageUtil;
import net.seyarada.pandeloot.items.ItemUtils;
import net.seyarada.pandeloot.options.Options;
import net.seyarada.pandeloot.rewards.Reward;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class StartDrops {
  public StartDrops(List<Player> players, List<String> rewardStrings, DamageUtil damageUtil, Location location) {
    StringLib.warn("+++ Started drop starter");
    if (players == null) {
      List<Reward> rewards = Reward.rewardsFromStringList(rewardStrings, null, damageUtil);
      perEntryDrop(rewards, location, rewardStrings);
      return;
    } 
    for (Player player : players) {
      StringLib.warn("++++ Doing drop for: " + player.getName());
      List<Reward> rewards = Reward.rewardsFromStringList(rewardStrings, player, damageUtil);
      StringLib.warn("++++ Pre-collected rewards:");
      for (Reward reward : rewards)
        StringLib.warn("+++++ " + reward.rewardLine.baseLine); 
      List<Reward> outputRewards = new ArrayList<>();
      ItemUtils.collectRewards(rewards, outputRewards, players.size(), true);
      StringLib.warn("++++ Post-collected rewards:");
      for (Reward reward : outputRewards)
        StringLib.warn("+++++ " + reward.rewardLine.baseLine); 
      if (location == null) {
        perEntryDrop(outputRewards, player.getLocation(), rewardStrings);
        continue;
      } 
      perEntryDrop(outputRewards, location, rewardStrings);
    } 
  }
  
  private void perEntryDrop(List<Reward> rewards, Location location, List<String> rewardSource) {
    Map<Double, Integer> radialCounter = new HashMap<>();
    int playerDelay = 0;
    int skip = 0;
    StringLib.warn("+++ Starting good drop loop");
    for (Reward reward : rewards) {
      playerDelay += Integer.parseInt((String)reward.options.get("delay"));
      StringLib.warn("+++ Doing drop for " + reward.rewardLine.baseLine);
      if (skip > 0) {
        skip--;
        StringLib.warn("+++ Reward skipped");
        continue;
      } 
      StringLib.warn("+++ Running reward drop");
      runRewardDrop(reward, radialCounter, location, playerDelay);
      if (Boolean.parseBoolean(reward.get("shared"))) {
        StringLib.warn("+++ Reward was shared, removing it from reward list...");
        rewardSource.remove(reward.rewardLine.baseLine);
      } 
      if (Boolean.parseBoolean(reward.get("stop"))) {
        StringLib.warn("+++ Reward had stop, stopping loop...");
        break;
      } 
      skip = reward.skip;
    } 
  }
  
  private void runRewardDrop(final Reward reward, Map<Double, Integer> radialCounter, final Location location, int playerDelay) {
    String explodeRadiusString = reward.get("exploderadius");
    if (explodeRadiusString != null) {
      StringLib.warn("++++ Assigning explode radius order");
      double explodeRadius = Double.parseDouble(explodeRadiusString);
      radialCounter.put(Double.valueOf(explodeRadius), Integer.valueOf(radialCounter.containsKey(Double.valueOf(explodeRadius)) ? ((
            (Integer)radialCounter.get(Double.valueOf(explodeRadius))).intValue() + 1) : 
            1));
      reward.radialDropInformation = radialCounter;
      reward.radialOrder = ((Integer)radialCounter.get(Double.valueOf(explodeRadius))).intValue() + 1;
    } 
    (new BukkitRunnable() {
        public void run() {
          StringLib.warn("++++ Spawning the reward and calling the options for " + reward.rewardLine.baseLine);
          new SpawnReward(reward, location);
          Options.callOptions(reward);
        }
      }).runTaskLater((Plugin)PandeLoot.getInstance(), playerDelay);
  }
}
