package net.seyarada.pandeloot.options.mechanics;

import java.util.Map;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.seyarada.pandeloot.StringLib;
import net.seyarada.pandeloot.options.MechanicEvent;
import net.seyarada.pandeloot.rewards.Reward;
import net.seyarada.pandeloot.utils.ColorUtil;

public class GlowMechanic implements MechanicEvent {
  public void onCall(Reward reward, String value) {
    if (value != null && !value.isEmpty()) {
      StringLib.warn("++++++ Applying glow effect with value " + value);
      boolean shouldGlow = Boolean.parseBoolean(value);
      if (shouldGlow) {
        String color;
        Map<String, String> options = reward.options;
        if (reward.specialOptions.containsKey("matchcolor")) {
          String tier = MMOItems.plugin.getMMOItem(Type.get(options.get("type")), reward.rewardLine.item).getData(ItemStats.TIER).toString();
          StringLib.warn("+ tier: " + tier);
          StringLib.warn("+ pre: " + MMOItems.plugin.getTiers().getOrThrow(tier).getUnidentificationInfo().getPrefix() + "ayaya");
          color = ColorUtil.colorSwitch(MMOItems.plugin.getTiers().getOrThrow(tier).getUnidentificationInfo().getPrefix().substring(1));
          StringLib.warn("+ tier color: " + color);
        } else {
          color = options.get("color");
        } 
        double beam = Double.parseDouble(options.get("beam"));
        reward.item.setGlowing(true);
        ColorUtil.setColorEffects(reward.item, color, reward.player, beam);
      } 
    } 
  }
}
