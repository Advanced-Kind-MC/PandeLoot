package net.seyarada.pandeloot;

import net.seyarada.pandeloot.rewards.RewardLine;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class StringLib {
  public static final String root = "PandeLoot";
  
  public static final String playOnPickup = "PandeLoot.playOnPickup";
  
  public static final String preventPickup = "PandeLoot.preventPickUp";
  
  public static final String preventStack = "PandeLoot.preventStack";
  
  public static final String onUse = "PandeLoot.onUse";
  
  public static final String bag = "PandeLoot.LootBag";
  
  public static final String skin = "PandeLoot.Skin";
  
  public static final String prefix = ChatColor.YELLOW + "[" + "PandeLoot" + "] " + ChatColor.RED;
  
  public static int depthBonus;
  
  public static void badItemStack(RewardLine reward) {
    System.err.println(prefix + " Couldn't create the item -" + reward.item + "- from origin " + reward.origin);
  }
  
  public static void badItem(String item) {
    System.err.println(prefix + "Couldn't find the item -" + item + "- , this probably means that are trying to drop an item from another plugin but you haven't specified the origin plugin? You can take a look at all the origins here https://github.com/Seyarada/PandeLoot/wiki/Compatibility");
  }
  
  public static void badLootTable(String lootTable) {
    if (lootTable == null)
      return; 
    System.err.println(prefix + "Couldn't find the LootTable -" + lootTable + "- !");
  }
  
  public static void badMIGen(String item) {
    if (item == null)
      return; 
    System.err.println(prefix + "Couldn't use the MI Generator for the item -" + item + "- !");
  }
  
  public static void badLootBag(String lootBag) {
    System.err.println(prefix + " Couldn't find the LootBag -" + lootBag + "- !");
  }
  
  public static void badOption(String option) {
    System.err.println(prefix + "Can't find the option -" + option + "- !");
  }
  
  public static void badPlayer(String player, CommandSender sender) {
    System.err.println(prefix + "Can't find the player -" + player + "- !");
    sender.sendMessage(prefix + "Can't find the player -" + player + "- !");
  }
  
  public static void warn(String text) {
    long depth = text.chars().filter(ch -> (ch == 43)).count() + depthBonus;
    if (depth <= Config.debug) {
      switch ((int)depth) {
        case 1:
          System.err.println(prefix + ChatColor.WHITE + text);
          return;
        case 2:
          System.err.println(prefix + ChatColor.DARK_PURPLE + text);
          return;
        case 3:
          System.err.println(prefix + ChatColor.LIGHT_PURPLE + text);
          return;
        case 4:
          System.err.println(prefix + ChatColor.BLUE + text);
          return;
        case 5:
          System.err.println(prefix + ChatColor.DARK_BLUE + text);
          return;
        case 6:
          System.err.println(prefix + ChatColor.DARK_AQUA + text);
          return;
        case 7:
          System.err.println(prefix + ChatColor.AQUA + text);
          return;
        case 8:
          System.err.println(prefix + ChatColor.GREEN + text);
          return;
        case 9:
          System.err.println(prefix + ChatColor.DARK_GREEN + text);
          return;
        case 10:
          System.err.println(prefix + ChatColor.YELLOW + text);
          return;
        case 11:
          System.err.println(prefix + ChatColor.GOLD + text);
          return;
        case 12:
          System.err.println(prefix + ChatColor.RED + text);
          return;
        case 13:
          System.err.println(prefix + ChatColor.DARK_RED + text);
          return;
      } 
      System.err.println(prefix + text);
    } 
  }
}
