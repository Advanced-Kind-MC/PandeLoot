package net.seyarada.pandeloot;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;

public class Config {
  public static int debug;
  
  public static final HashMap<String, String> defaultOptions = new HashMap<>();
  
  public static final FileConfiguration config = (FileConfiguration)new YamlConfiguration();
  
  public static final FileConfiguration rewardContainers = (FileConfiguration)new YamlConfiguration();
  
  private static final FileConfiguration mobConfig = (FileConfiguration)new YamlConfiguration();
  
  private static File dataFolder;
  
  private static File configFile;
  
  public Config() {
    dataFolder = PandeLoot.getInstance().getDataFolder();
    reload();
  }
  
  public static void reload() {
    configFile = generateFile("Config", "Config");
    loadFile(configFile, config);
    File mobFile = generateFile("Mobs", "Mobs");
    loadFile(mobFile, mobConfig);
    generateFolderAndLoadRewardContainers();
    updateConfig();
    ConfigurationSection defaultValues = config.getConfigurationSection("DefaultValues");
    if (defaultValues == null)
      return; 
    Set<String> options = defaultValues.getKeys(false);
    for (String option : options)
      defaultOptions.put(option.toLowerCase(), defaultValues.getString(option)); 
    debug = config.getInt("Settings.Debug");
  }
  
  public static void generateFolderAndLoadRewardContainers() {
    File rewardContainerFolder = new File(dataFolder, "RewardContainers");
    rewardContainerFolder.mkdirs();
    generateFile("RewardContainers/LootBags", "RewardContainers/LootBags");
    generateFile("RewardContainers/LootTables", "RewardContainers/LootTables");
    StringBuilder combinedYaml = new StringBuilder();
    for (File file : rewardContainerFolder.listFiles()) {
      YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
      combinedYaml.append(yamlConfiguration.saveToString());
      StringLib.warn("+ Loading file " + file.getName());
    } 
    try {
      rewardContainers.loadFromString(combinedYaml.toString());
    } catch (InvalidConfigurationException e) {
      e.printStackTrace();
    } 
  }
  
  public static File generateFile(String internalPath, String showName) {
    showName = showName + ".yml";
    internalPath = internalPath + ".yml";
    File file = new File(dataFolder, showName);
    if (!file.exists())
      try {
        InputStreamReader iSR = new InputStreamReader(PandeLoot.getInstance().getResource(internalPath));
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(iSR);
        yamlConfiguration.options().copyDefaults(true);
        yamlConfiguration.save(file);
      } catch (IOException e) {
        e.printStackTrace();
      }  
    return file;
  }
  
  public static void loadFile(File file, FileConfiguration fileConfig) {
    try {
      fileConfig.load(file);
    } catch (IOException|InvalidConfigurationException e) {
      e.printStackTrace();
    } 
  }
  
  public static void updateConfig() {
    InputStreamReader iSR = new InputStreamReader(PandeLoot.getInstance().getResource("Config.yml"));
    YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(iSR);
    for (String i : yamlConfiguration.getKeys(true)) {
      if (!config.contains(i) && config.getStringList(i).size() == 0)
        config.set(i, yamlConfiguration.get(i)); 
    } 
    try {
      config.save(configFile);
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  public static ConfigurationSection getMob(Entity entity) {
    String entityType = entity.getType().toString();
    String display = entity.getCustomName();
    String world = entity.getWorld().getName();
    for (String i : mobConfig.getKeys(false)) {
      ConfigurationSection subConfig = mobConfig.getConfigurationSection(i);
      String subType = subConfig.getString("Type");
      if (subType != null && !subType.equalsIgnoreCase(entityType))
        continue; 
      String subDisplay = subConfig.getString("Display");
      if (subDisplay != null && !subDisplay.equalsIgnoreCase(display))
        continue; 
      String subWorld = subConfig.getString("World");
      if (subWorld != null && !subWorld.equalsIgnoreCase(world))
        continue; 
      return subConfig;
    } 
    return null;
  }
  
  public static ConfigurationSection getRewardContainer(String internalName) {
    return rewardContainers.getConfigurationSection(internalName);
  }
  
  public static List<String> getScoreHologram() {
    return config.getStringList("Announcements.ScoreHologram");
  }
  
  public static List<String> getScoreMessage() {
    return config.getStringList("Announcements.ScoreMessage");
  }
  
  public static int getRainbowFrequency() {
    return config.getInt("Settings.RainbowFrequency");
  }
  
  public static boolean getPlayArm() {
    return config.getBoolean("Settings.PlayArmWhenOpeningLootBag");
  }
  
  public static boolean getPlayArmEmpty() {
    return config.getBoolean("Settings.OnlyPlayArmIfBothEmpty");
  }
  
  public static String getAbandonText() {
    return config.getString("Settings.AbandonText");
  }
  
  public static String getSecondsText() {
    return config.getString("Settings.TimeFormatSeconds");
  }
  
  public static String getMinutesText() {
    return config.getString("Settings.TimeFormatMinutes");
  }
  
  public static String getHoursText() {
    return config.getString("Settings.TimeFormatHours");
  }
}
