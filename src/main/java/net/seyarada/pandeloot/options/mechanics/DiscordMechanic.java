package net.seyarada.pandeloot.options.mechanics;

import java.util.Map;
import net.seyarada.pandeloot.StringLib;
import net.seyarada.pandeloot.compatibility.DiscordSRVCompatibility;
import net.seyarada.pandeloot.options.MechanicEvent;
import net.seyarada.pandeloot.rewards.Reward;

public class DiscordMechanic implements MechanicEvent {
  public void onCall(Reward reward, String value) {
    StringLib.warn("++++++ Applying discord effect with value " + value);
    if (value != null && !value.isEmpty()) {
      Map<String, String> options = reward.options;
      String title = options.get("dtitle");
      String message = options.get("dmessage");
      String color = options.get("dcolor");
      String link = options.get("dlink");
      boolean avatar = Boolean.parseBoolean(options.get("davatar"));
      DiscordSRVCompatibility.embed(reward.player, title, message, value, color, link, avatar);
    } 
  }
}
