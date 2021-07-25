package net.seyarada.pandeloot.options.mechanics;

import net.seyarada.pandeloot.StringLib;
import net.seyarada.pandeloot.options.MechanicEvent;
import net.seyarada.pandeloot.rewards.Reward;

public class MessageMechanic implements MechanicEvent {
  public void onCall(Reward reward, String value) {
    if (value != null && !value.isEmpty()) {
      StringLib.warn("++++++ Applying message effect with value " + value);
      reward.player.sendMessage(value);
    } 
  }
}
