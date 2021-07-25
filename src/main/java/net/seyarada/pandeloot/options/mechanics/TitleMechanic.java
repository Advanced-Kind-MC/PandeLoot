package net.seyarada.pandeloot.options.mechanics;

import java.util.Map;
import net.seyarada.pandeloot.StringLib;
import net.seyarada.pandeloot.options.MechanicEvent;
import net.seyarada.pandeloot.rewards.Reward;

public class TitleMechanic implements MechanicEvent {
  public void onCall(Reward reward, String value) {
    Map<String, String> options = reward.options;
    String title = options.get("title");
    String subtitle = options.get("subtitle");
    int titleFade = Integer.parseInt(options.get("titlefade"));
    int titleDuration = Integer.parseInt(options.get("titleduration"));
    if (titleDuration == 0)
      titleDuration = 20; 
    if ((title != null && !title.isEmpty()) || (subtitle != null && !subtitle.isEmpty())) {
      StringLib.warn("++++++ Applying title effect with value " + value);
      reward.player.sendTitle(title, subtitle, titleFade, titleDuration, titleFade);
    } 
  }
}
