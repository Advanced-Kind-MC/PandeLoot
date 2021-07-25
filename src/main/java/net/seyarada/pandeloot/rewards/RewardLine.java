package net.seyarada.pandeloot.rewards;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.seyarada.pandeloot.StringLib;
import net.seyarada.pandeloot.damage.DamageUtil;
import net.seyarada.pandeloot.utils.PlaceholderUtil;
import org.bukkit.entity.Player;

public class RewardLine {
  final int optionsStartIndex;
  
  int optionsEndIndex;
  
  final int originIndicatorIndex;
  
  public final String baseLine;
  
  public final String origin;
  
  public final Map<String, String> specialOptions = new HashMap<>();
  
  public final Map<String, String> insideOptions = new HashMap<>();
  
  public String item;
  
  public String chance = "1";
  
  public String damage;
  
  public int amount = 1;
  
  public boolean skipConditions;
  
  public RewardLine(String baseLine) {
    this.baseLine = baseLine;
    this.optionsStartIndex = baseLine.indexOf("{");
    this.optionsEndIndex = baseLine.lastIndexOf("}");
    this.originIndicatorIndex = baseLine.indexOf(":");
    this.origin = setOrigin();
    this.item = setItem();
  }
  
  public void build(Player player, DamageUtil util) {
    String parsedBaseLine = PlaceholderUtil.parse(this.baseLine, util, player, false);
    generateOutsideOptions(parsedBaseLine);
    generateInsideOptions(parsedBaseLine);
  }
  
  public double getChance(DamageUtil u, Player p) {
    this.chance = PlaceholderUtil.parse(this.chance, u, p, true);
    double newChance = PlaceholderUtil.parseMath(this.chance);
    if (this.specialOptions.containsKey("multiplier")) {
      double multiplier = Double.parseDouble(this.specialOptions.get("multiplier"));
      double percent = u.getPercentageDamage(p);
      newChance += percent * multiplier;
    } 
    return newChance;
  }
  
  private String setOrigin() {
    if (this.originIndicatorIndex == -1)
      return "minecraft"; 
    if (this.optionsStartIndex == -1 || this.originIndicatorIndex < this.optionsStartIndex)
      return this.baseLine.substring(0, this.originIndicatorIndex); 
    return "minecraft";
  }
  
  private String setItem() {
    if (this.originIndicatorIndex == -1) {
      if (this.optionsStartIndex == -1) {
        if (this.baseLine.contains(" "))
          return this.baseLine.substring(0, this.baseLine.indexOf(" ")); 
        return this.baseLine;
      } 
      return this.baseLine.substring(0, this.optionsStartIndex);
    } 
    if (this.optionsStartIndex == -1) {
      if (this.baseLine.contains(" "))
        return this.baseLine.substring(this.originIndicatorIndex + 1, this.baseLine.indexOf(" ")); 
      return this.baseLine.substring(this.originIndicatorIndex + 1);
    } 
    if (this.originIndicatorIndex > this.optionsStartIndex)
      return this.baseLine.substring(0, this.optionsStartIndex); 
    return this.baseLine.substring(this.originIndicatorIndex + 1, this.optionsStartIndex);
  }
  
  public void generateInsideOptions(String baseLine) {
    int aStart = baseLine.indexOf("{");
    int aEnd = baseLine.lastIndexOf("}");
    if (aStart == -1 || aEnd == -1)
      return; 
    StringLib.warn("+++ BaseLine " + baseLine);
    String[] options = baseLine.substring(aStart + 1, aEnd).split(";");
    StringLib.warn("++++ Options for generateInside are " + Arrays.toString((Object[])options));
    for (String option : options) {
      StringLib.warn("+++++ Option is " + option);
      String[] l = option.split("=");
      String optionKey = l[0].toLowerCase();
      String optionValue = l[1];
      this.insideOptions.putIfAbsent(optionKey, optionValue);
    } 
  }
  
  private void generateOutsideOptions(String baseLine) {
    this.optionsEndIndex = baseLine.lastIndexOf("}");
    if (this.optionsEndIndex + 1 >= baseLine.length())
      return; 
    if (!baseLine.contains(" "))
      return; 
    if (this.optionsEndIndex > -1) {
      baseLine = baseLine.substring(this.optionsEndIndex + 2);
    } else {
      baseLine = baseLine.substring(baseLine.indexOf(" ") + 1);
    } 
    String[] options = baseLine.split(" ");
    for (String option : options) {
      if (!option.trim().isEmpty())
        if (option.startsWith("0.")) {
          this.chance = parseOutsideOption(option);
        } else if (option.contains("=") || option.contains(">") || option.contains("<")) {
          this.damage = option;
        } else if (option.startsWith("-")) {
          String[] specialOption = option.substring(1).split(":");
          if (specialOption.length == 1) {
            this.specialOptions.putIfAbsent(specialOption[0], "true");
          } else {
            String optionKey = specialOption[0];
            String optionValue = specialOption[1];
            this.specialOptions.putIfAbsent(optionKey, optionValue);
          } 
        } else {
          String thingLeft = parseOutsideOption(option);
          if (!thingLeft.equals("1"))
            this.amount = (int)Double.parseDouble(thingLeft); 
        }  
    } 
  }
  
  private String parseOutsideOption(String i) {
    if (i.contains("to")) {
      String[] n = i.split("to");
      Random r = new Random();
      if (n[0].isEmpty() || n[1].isEmpty())
        return i; 
      double rangeMin = Double.parseDouble(n[0]);
      double rangeMax = Double.parseDouble(n[1]);
      return String.valueOf(rangeMin + (rangeMax - rangeMin) * r.nextDouble());
    } 
    return i;
  }
}
