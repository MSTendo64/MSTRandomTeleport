package com.mstendo.rtp;

import java.lang.reflect.Constructor;
import lombok.Generated;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import com.mstendo.rtp.configuration.Config;
import com.mstendo.rtp.utils.PluginMessage;
import com.mstendo.rtp.utils.Utils;
import com.mstendo.rtp.utils.VersionUtils;
import com.mstendo.rtp.utils.logging.BukkitLogger;
import com.mstendo.rtp.utils.logging.Logger;
import com.mstendo.rtp.utils.logging.PaperLogger;
import com.mstendo.rtp.utils.metrics.bukkit.Metrics;
import com.mstendo.rtp.utils.regions.WGUtils;

public final class MSTRandomTeleport extends JavaPlugin {
  private final Server server = getServer();
  
  private final Logger pluginLogger = (VersionUtils.SUB_VERSION >= 19) ? (Logger)new PaperLogger(this) : (Logger)new BukkitLogger(this);
  
  @Generated
  public Logger getPluginLogger() {
    return this.pluginLogger;
  }
  
  private final Config pluginConfig = new Config(this);
  
  @Generated
  public Config getPluginConfig() {
    return this.pluginConfig;
  }
  
  private final RtpManager rtpManager = new RtpManager(this);
  
  private Economy economy;
  
  private Permission perms;
  
  private PluginMessage pluginMessage;
  
  private RtpExpansion rtpExpansion;
  
  private Boolean hasWorldGuard;
  
  @Generated
  public RtpManager getRtpManager() {
    return this.rtpManager;
  }
  
  @Generated
  public Economy getEconomy() {
    return this.economy;
  }
  
  @Generated
  public Permission getPerms() {
    return this.perms;
  }
  
  @Generated
  public PluginMessage getPluginMessage() {
    return this.pluginMessage;
  }
  
  @Generated
  public RtpExpansion getRtpExpansion() {
    return this.rtpExpansion;
  }
  
  public void onLoad() {
    if (this.server.getPluginManager().isPluginEnabled("PlugManX") || this.server.getPluginManager().isPluginEnabled("PlugMan"))
      return; 
    if (hasWorldGuard()) {
      WGUtils.setupRtpFlag();
      this.pluginLogger.info("§5WorldGuard подключён!");
    } 
  }
  
  public boolean hasWorldGuard() {
    if (this.hasWorldGuard == null)
      try {
        Class.forName("com.sk89q.worldguard.protection.flags.registry.FlagConflictException");
        this.hasWorldGuard = Boolean.valueOf(true);
      } catch (ClassNotFoundException ex) {
        this.hasWorldGuard = Boolean.valueOf(false);
      }  
    return this.hasWorldGuard.booleanValue();
  }
  
  public void onEnable() {
    saveDefaultConfig();
    FileConfiguration config = getConfig();
    ConfigurationSection mainSettings = config.getConfigurationSection("main_settings");
    Utils.setupColorizer(mainSettings);
    this.pluginConfig.setupMessages(config);
    this.pluginConfig.setupTemplates();
    PluginManager pluginManager = this.server.getPluginManager();
    registerCommand(pluginManager, mainSettings);
    if (mainSettings.getBoolean("enable_metrics"))
      new Metrics((Plugin)this, 22021); 
    if (pluginManager.isPluginEnabled("Vault")) {
      ServicesManager servicesManager = this.server.getServicesManager();
      setupEconomy(servicesManager);
      setupPerms(servicesManager);
    } 
    setupPlaceholders(mainSettings, pluginManager);
    setupProxy(mainSettings);
    pluginManager.registerEvents(new RtpListener(this), (Plugin)this);
    checkForUpdates(mainSettings);
    this.server.getScheduler().runTaskAsynchronously((Plugin)this, () -> this.rtpManager.setupChannels(config, pluginManager));
  }
  
  public void checkForUpdates(ConfigurationSection mainSettings) {
    if (!mainSettings.getBoolean("update_checker", true))
      return; 
    Utils.checkUpdates(this, version -> {
          this.pluginLogger.info("§6========================================");
          if (getDescription().getVersion().equals(version)) {
            this.pluginLogger.info("§aВы используете последнюю версию плагина!");
          } else {
            this.pluginLogger.info("§aВы используете устаревшую плагина!");
            this.pluginLogger.info("§aВы можете скачать новую версию здесь:");
            this.pluginLogger.info("§bgithub.com/mstendo/MSTRandomTeleport/releases/");
            this.pluginLogger.info("");
            this.pluginLogger.info("§aИли обновите плагин при помощи §b/rtp admin update");
          } 
          this.pluginLogger.info("§6========================================");
        });
  }
  
  private void setupEconomy(ServicesManager servicesManager) {
    this.economy = getProvider(servicesManager, Economy.class);
    if (this.economy == null)
      return; 
    this.pluginLogger.info("§6Экономика подключена!");
  }
  
  private void setupPerms(ServicesManager servicesManager) {
    this.perms = getProvider(servicesManager, Permission.class);
    if (this.perms == null)
      return; 
    this.pluginLogger.info("§aМенеджер прав подключён!");
  }
  
  private <T> T getProvider(ServicesManager servicesManager, Class<T> clazz) {
    RegisteredServiceProvider<T> provider = servicesManager.getRegistration(clazz);
    return (provider != null) ? (T)provider.getProvider() : null;
  }
  
  private void setupPlaceholders(ConfigurationSection mainSettings, PluginManager pluginManager) {
    if (!mainSettings.getBoolean("papi_support", true) || !pluginManager.isPluginEnabled("PlaceholderAPI"))
      return; 
    Utils.USE_PAPI = true;
    this.rtpExpansion = new RtpExpansion(this);
    this.rtpExpansion.register();
    this.pluginLogger.info("§eПлейсхолдеры подключены!");
  }
  
  private void setupProxy(ConfigurationSection mainSettings) {
    ConfigurationSection proxy = mainSettings.getConfigurationSection("proxy");
    if (proxy.getBoolean("enabled", false)) {
      this.server.getMessenger().registerOutgoingPluginChannel((Plugin)this, "BungeeCord");
      String serverId = proxy.getString("server_id");
      this.pluginMessage = new PluginMessage(this, serverId);
      this.server.getMessenger().registerIncomingPluginChannel((Plugin)this, "BungeeCord", (PluginMessageListener)this.pluginMessage);
      this.rtpManager.initProxyCalls();
    } 
  }
  
  private void registerCommand(PluginManager pluginManager, ConfigurationSection mainSettings) {
    try {
      String commandName = mainSettings.getString("rtp_command", "rtp");
      this.pluginLogger.info("Registering RTP command: " + commandName);
      CommandMap commandMap = (CommandMap) this.server.getClass().getMethod("getCommandMap").invoke(this.server);
      Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(new Class[] { String.class, Plugin.class });
      constructor.setAccessible(true);
      PluginCommand command = constructor.newInstance(new Object[] { commandName, this });
      command.setAliases(mainSettings.getStringList("rtp_aliases"));
      RtpCommand rtpCommand = new RtpCommand(this);
      command.setExecutor((CommandExecutor)rtpCommand);
      commandMap.register(getDescription().getName(), (Command)command);
      this.pluginLogger.info("RTP command registered successfully: " + commandName);
    } catch (Exception ex) {
      this.pluginLogger.info("Unable to register RTP command!" + String.valueOf(ex));
      ex.printStackTrace();
      pluginManager.disablePlugin((Plugin)this);
    } 
  }
  
  public void onDisable() {
    this.rtpManager.cancelAllTasks();
    if (this.rtpExpansion != null)
      this.rtpExpansion.unregister(); 
    this.server.getScheduler().cancelTasks((Plugin)this);
  }
}
