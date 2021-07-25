package net.seyarada.pandeloot.options.mechanics;

import net.seyarada.pandeloot.StringLib;
import net.seyarada.pandeloot.options.MechanicEvent;
import net.seyarada.pandeloot.rewards.Reward;

public class ExperienceMechanic implements MechanicEvent {
  public void onCall(Reward reward, String value) {
    if (value != null && !value.isEmpty()) {
      int experience = Integer.parseInt(value);
      if (experience > 0) {
        StringLib.warn("++++++ Applying experience effect with value " + value);
        reward.player.giveExp(experience);
      } 
    } 
  }
}
