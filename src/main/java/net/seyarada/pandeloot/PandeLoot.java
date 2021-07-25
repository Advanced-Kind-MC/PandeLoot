package net.seyarada.pandeloot;

import java.util.ArrayList;
import java.util.List;
import net.milkbowl.vault.economy.Economy;
import net.seyarada.pandeloot.commands.AutoComplete;
import net.seyarada.pandeloot.commands.BoosterCommand;
import net.seyarada.pandeloot.commands.CommandManager;
import net.seyarada.pandeloot.commands.GiveCommand;
import net.seyarada.pandeloot.commands.ReloadCommand;
import net.seyarada.pandeloot.compatibility.mythicmobs.MythicMobsCompatibility;
import net.seyarada.pandeloot.damage.DamageTracker;
import net.seyarada.pandeloot.items.LootBalloon;
import net.seyarada.pandeloot.nms.PlayerListener;
import net.seyarada.pandeloot.options.RegisterOptions;
import net.seyarada.pandeloot.rewards.RewardsListener;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class PandeLoot extends JavaPlugin {
  private static PandeLoot instance;
  
  private static Economy econ = null;
  
  public static final List<ArmorStand> totalHolograms = new ArrayList<>();
  
  public void onEnable() {
    instance = this;
    new Config();
    if (getServer().getPluginManager().getPlugin("MythicMobs") != null) {
      MythicMobsCompatibility mmComp = new MythicMobsCompatibility();
      getServer().getPluginManager().registerEvents((Listener)mmComp, (Plugin)this);
    } 
    new RegisterOptions();
    getServer().getPluginManager().registerEvents((Listener)new DamageTracker(), (Plugin)this);
    getServer().getPluginManager().registerEvents((Listener)new RewardsListener(), (Plugin)this);
    getServer().getPluginManager().registerEvents((Listener)new PlayerListener(), (Plugin)this);
    getServer().getPluginManager().registerEvents((Listener)new LootBalloon(null, null), (Plugin)this);
    getCommand("pandeloot").setExecutor((CommandExecutor)new CommandManager());
    new BoosterCommand();
    new GiveCommand();
    new ReloadCommand();
    getCommand("pandeloot").setTabCompleter((TabCompleter)new AutoComplete());
    setupEconomy();
  }
  
  public void onDisable() {
    try {
      for (ArmorStand i : totalHolograms) {
        if (i != null && i.isValid())
          i.remove(); 
      } 
    } catch (Exception exception) {}
  }
  
  private void setupEconomy() {
    if (getServer().getPluginManager().getPlugin("Vault") == null)
      return; 
    RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
    if (rsp == null)
      return; 
    econ = (Economy)rsp.getProvider();
  }
  
  public static Economy getEconomy() {
    return econ;
  }
  
  public static PandeLoot getInstance() {
    return instance;
  }
}
