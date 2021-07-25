package net.seyarada.pandeloot.options.mechanics;

import java.util.Map;
import net.seyarada.pandeloot.StringLib;
import net.seyarada.pandeloot.options.MechanicEvent;
import net.seyarada.pandeloot.rewards.Reward;
import net.seyarada.pandeloot.utils.MathUtil;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

public class ExplodeMechanic implements MechanicEvent {
  public void onCall(Reward reward, String value) {
    if (value != null && !value.isEmpty() && !reward.isAbandoned) {
      StringLib.warn("++++++ Applying explode effect with value " + value);
      boolean shouldExplode = Boolean.parseBoolean(value);
      if (shouldExplode) {
        Map<String, String> options = reward.options;
        String type = ((String)options.get("explodetype")).toLowerCase();
        StringLib.warn("++++++ Explode effect is type " + type);
        switch (type) {
          default:
            doSpreadDrop(reward);
            return;
          case "radial":
            break;
        } 
        doRadialDrop(reward);
      } 
    } 
  }
  
  private void doSpreadDrop(Reward reward) {
    Map<String, String> options = reward.options;
    double offset = Double.parseDouble(options.get("expoffset"));
    double height = Double.parseDouble(options.get("expheight"));
    Vector velocity = MathUtil.getVelocity(offset, height);
    reward.item.setVelocity(velocity);
  }
  
  private void doRadialDrop(Reward reward) {
    StringLib.warn("++++++ Executing radial drop to " + reward.item);
    Map<String, String> options = reward.options;
    double radius = Double.parseDouble(options.get("exploderadius"));
    Item item = reward.item;
    int numberOfTotalDrops = ((Integer)reward.radialDropInformation.get(Double.valueOf(radius))).intValue();
    int numberOfThisDrop = reward.radialOrder;
    StringLib.warn("++++++ Total radial drops: " + numberOfTotalDrops);
    StringLib.warn("++++++ Order of this drop: " + numberOfThisDrop);
    if (numberOfTotalDrops == 1) {
      doSpreadDrop(reward);
      return;
    } 
    double angle = 6.283185307179586D / numberOfTotalDrops;
    double cos = Math.cos(angle * numberOfThisDrop);
    double sin = Math.sin(angle * numberOfThisDrop);
    double iX = item.getLocation().getX() + radius * cos;
    double iZ = item.getLocation().getZ() + radius * sin;
    Location loc = item.getLocation().clone();
    loc.setX(iX);
    loc.setZ(iZ);
    item.setVelocity(
        MathUtil.calculateVelocity(item.getLocation().toVector(), loc.toVector(), 0.115D, 3.0D));
  }
}
