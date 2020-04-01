package us.thetaco.banana;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import us.thetaco.banana.commands.AddStaffCommand;
import us.thetaco.banana.commands.BanCommand;
import us.thetaco.banana.commands.BanIPCommand;
import us.thetaco.banana.commands.BanInfoCommand;
import us.thetaco.banana.commands.BanListCommand;
import us.thetaco.banana.commands.BananaCommand;
import us.thetaco.banana.commands.DelWarning;
import us.thetaco.banana.commands.IPBanListCommand;
import us.thetaco.banana.commands.KickAllCommand;
import us.thetaco.banana.commands.KickCommand;
import us.thetaco.banana.commands.ListStaffCommand;
import us.thetaco.banana.commands.ListWarnings;
import us.thetaco.banana.commands.MuteCommand;
import us.thetaco.banana.commands.MyWarnsCommand;
import us.thetaco.banana.commands.PurgeWarnings;
import us.thetaco.banana.commands.RmStaffCommand;
import us.thetaco.banana.commands.TempBanCommand;
import us.thetaco.banana.commands.TempBanIPCommand;
import us.thetaco.banana.commands.TempMuteCommand;
import us.thetaco.banana.commands.ToggleStaffModeCommand;
import us.thetaco.banana.commands.UnBanCommand;
import us.thetaco.banana.commands.UnBanIPCommand;
import us.thetaco.banana.commands.UnMuteCommand;
import us.thetaco.banana.commands.WarnCommand;
import us.thetaco.banana.listeners.PlayerChatListener;
import us.thetaco.banana.listeners.PlayerCommandListener;
import us.thetaco.banana.listeners.PlayerLoginListener;
import us.thetaco.banana.sql.DatabaseManager;
import us.thetaco.banana.utils.BanCache;
import us.thetaco.banana.utils.ConfigManager;
import us.thetaco.banana.utils.Lang;
import us.thetaco.banana.utils.MuteCache;
import us.thetaco.banana.utils.MuteChecker;
import us.thetaco.banana.utils.OfflineUUIDHandler;
import us.thetaco.banana.utils.PlayerCache;
import us.thetaco.banana.utils.SimpleLogger;
import us.thetaco.banana.utils.WarnCache;

public class Banana extends JavaPlugin {

	private static Integer MUTE_CHECKER_ID;
	private static BanCache BAN_CACHE;
	private static DatabaseManager DATABASE;
	private static WarnCache WARN_CACHE;
	private static PlayerCache PLAYER_CACHE;
	private static MuteCache MUTE_CACHE;
	private PlayerLoginListener pLoginListener;
	private PlayerChatListener pChatListener;
	private PlayerCommandListener pCommandListener;
	private BanCommand banCommand;
	private TempBanCommand tempBanCommand;
	private WarnCommand warnCommand;
	private KickCommand kickCommand;
	private ListWarnings listCommand;
	private MyWarnsCommand myWarnsCommand;
	private UnBanCommand unbanCommand;
	private BanListCommand banListCommand;
	private IPBanListCommand ipBanListCommand;
	private BanIPCommand banIPCommand;
	private TempBanIPCommand tempBanIPCommand;
	private UnBanIPCommand unBanIPCommand;
	private DelWarning delWarningCommand;
	private MuteCommand muteCommand;
	private UnMuteCommand unMuteCommand;
	private TempMuteCommand tempMuteCommand;
	private KickAllCommand kickAllCommand;
	private AddStaffCommand addStaffCommand;
	private RmStaffCommand rmStaffCommand;
	private ListStaffCommand listStaffCommand;
	private ToggleStaffModeCommand toggleStaffModeCommand;
	private PurgeWarnings purgeWarningsCommand;
	private ConfigManager configManager;
	private BananaCommand bananaCommand;
	private BanInfoCommand banInfoCommand;
	private OfflineUUIDHandler offlineUUIDHandler;
	private Thread oUUIDThread;
	private static YamlConfiguration LANG;
    private static File LANG_FILE;
    private static boolean STAFF_MODE;
	
	public void onEnable() {
		
		// loading up the language values
		this.loadLang();
		
		// loading all values
		Banana.BAN_CACHE = new BanCache();
		Banana.DATABASE = new DatabaseManager(this);
		Banana.WARN_CACHE = new WarnCache();
		Banana.PLAYER_CACHE = new PlayerCache();
		Banana.MUTE_CACHE = new MuteCache();
		pLoginListener = new PlayerLoginListener();
		banCommand = new BanCommand();
		tempBanCommand = new TempBanCommand();
		warnCommand = new WarnCommand();
		kickCommand = new KickCommand();
		listCommand = new ListWarnings();
		myWarnsCommand = new MyWarnsCommand();
		unbanCommand = new UnBanCommand();
		banListCommand = new BanListCommand();
		ipBanListCommand = new IPBanListCommand();
		banIPCommand = new BanIPCommand();
		tempBanIPCommand = new TempBanIPCommand();
		unBanIPCommand = new UnBanIPCommand();
		delWarningCommand = new DelWarning();
		muteCommand = new MuteCommand();
		tempMuteCommand = new TempMuteCommand();
		pChatListener = new PlayerChatListener();
		pCommandListener = new PlayerCommandListener();
		kickAllCommand = new KickAllCommand();
		addStaffCommand = new AddStaffCommand(this);
		rmStaffCommand = new RmStaffCommand();
		listStaffCommand = new ListStaffCommand();
		toggleStaffModeCommand = new ToggleStaffModeCommand();
		purgeWarningsCommand = new PurgeWarnings();
		unMuteCommand = new UnMuteCommand();
		banInfoCommand = new BanInfoCommand();
		configManager = new ConfigManager(this);
		offlineUUIDHandler = new OfflineUUIDHandler(this);
		
		// Load offline UUID Handler
		oUUIDThread = offlineUUIDHandler.createThread();
		oUUIDThread.start();
		
		// loading up the config
		configManager.initializeConfig();
		
		// populating the database &&/|| loading values
		if (Banana.DATABASE.populateDatabase() == false || Banana.DATABASE.syncLoadValues() == false) {
					
			this.getPluginLoader().disablePlugin(this);
			this.onDisable();
			return;
		}
		
		// special for reload command
		bananaCommand = new BananaCommand(this, configManager);
		
		// register listeners and commands
		this.registerListeners();
		this.registerCommands();
		
		// starting the database loop
		Banana.DATABASE.startLoop();
		
		// enabling the mute checker
		Banana.MUTE_CHECKER_ID = this.enableMuteChecker(5);
		
		// load up the staff mode value
		Banana.STAFF_MODE = false;
		
	}
	
	public void onDisable() {
		
		// clearing all values
		Banana.BAN_CACHE = null;
		Banana.DATABASE = null;
		
		// disabling the mute checker
		this.disableMuteChecker();
		
		// Disable UUID Thread
		oUUIDThread.interrupt();
		
	}
	
	/** Used to get the BanCache
	 * @return The ban cache
	 */
	public static BanCache getBanCache() {
		
		return Banana.BAN_CACHE;
		
	}
	
	/** Used to get the database for updating... stuff
	 * @return The database manager 
	 */
	public static DatabaseManager getDatabaseManager() {
		
		return Banana.DATABASE;
		
	}
	
	/** Used to get the warncache
	 * @return The warn cache
	 */
	public static WarnCache getWarnCache() {
		
		return Banana.WARN_CACHE;
		
	}
	
	/** Used to get the player cache
	 * @return The player cache
	 */
	public static PlayerCache getPlayerCache() {
		
		return Banana.PLAYER_CACHE;
		
	}
	
	/** Used to get the mute cache
	 * @return The mute cache
	 */
	public static MuteCache getMuteCache() {
		
		return Banana.MUTE_CACHE;
		
	}
	
	/** Used to register all of the plugins listeners! Nice and simple
	 * 
	 */
	private void registerListeners() {
		
		PluginManager pm = this.getServer().getPluginManager();
		
		pm.registerEvents(pLoginListener , this);
		pm.registerEvents(pChatListener, this);
		pm.registerEvents(pCommandListener, this);
		
	}
	
	/** Registers commands, too!
	 * 
	 */
	private void registerCommands() {
		
		this.getCommand("ban").setExecutor(banCommand);
		this.getCommand("tempban").setExecutor(tempBanCommand);
		this.getCommand("warn").setExecutor(warnCommand);
		this.getCommand("kick").setExecutor(kickCommand);
		this.getCommand("listwarnings").setExecutor(listCommand);
		this.getCommand("mywarns").setExecutor(myWarnsCommand);
		this.getCommand("unban").setExecutor(unbanCommand);
		this.getCommand("banlist").setExecutor(banListCommand);
		this.getCommand("ipbanlist").setExecutor(ipBanListCommand);
		this.getCommand("banip").setExecutor(banIPCommand);
		this.getCommand("tempbanip").setExecutor(tempBanIPCommand);
		this.getCommand("unbanip").setExecutor(unBanIPCommand);
		this.getCommand("delwarning").setExecutor(delWarningCommand);
		this.getCommand("mute").setExecutor(muteCommand);
		this.getCommand("unmute").setExecutor(unMuteCommand);
		this.getCommand("tempmute").setExecutor(tempMuteCommand);
		this.getCommand("kickall").setExecutor(kickAllCommand);
		this.getCommand("addstaff").setExecutor(addStaffCommand);
		this.getCommand("rmstaff").setExecutor(rmStaffCommand);
		this.getCommand("liststaff").setExecutor(listStaffCommand);
		this.getCommand("togglestaffmode").setExecutor(toggleStaffModeCommand);
		this.getCommand("purgewarnings").setExecutor(purgeWarningsCommand);
		this.getCommand("banana").setExecutor(bananaCommand);
		this.getCommand("baninfo").setExecutor(banInfoCommand);
		
	}
	
	/** Used to start the mute checker.. 
	 * @return The ID of the bukkit scheduler
	 */
	private int enableMuteChecker(int loopSeconds) {
		
		long loopTime = loopSeconds * 20L;
		
		return Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new MuteChecker(), 0L, loopTime);
		
	}
	
	/** Disables the mute checker
	 * 
	 */
	private void disableMuteChecker() {
		
		if (Banana.MUTE_CHECKER_ID == null) return;
		
		Bukkit.getScheduler().cancelTask(Banana.MUTE_CHECKER_ID);
		
	}
	
	/** Used to load all the language values from the language.yml file. If none exists or some values are missing, this will
	 * automatically set them to whatever the default is
	 * 
	 */
	public void loadLang() {
	    File lang = new File(this.getDataFolder().getPath() + "/language.yml");
	    if (!lang.exists()) {
	        try {
	            getDataFolder().mkdir();
	            lang.createNewFile();
	            File defConfigStream = new File(this.getDataFolder().getName() + "/language.yml");
	            if (defConfigStream != null) {
					YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	                defConfig.save(lang);
	                Lang.setFile(defConfig);
	                return;
	            }
	        } catch(IOException e) {
	            e.printStackTrace(); // So they notice
	            SimpleLogger.logMessage("Couldn't create language file");
	            SimpleLogger.logMessage("Without the language file this plugin doesn't know what to say!");
	            this.setEnabled(false);
	        }
	    }
	    YamlConfiguration conf = YamlConfiguration.loadConfiguration(lang);
	    for(Lang item : Lang.values()) {
	        if (conf.getString(item.getPath()) == null) {
	            conf.set(item.getPath(), item.getDefault());
	        }
	    }
	    Lang.setFile(conf);
	    Banana.LANG = conf;
	    Banana.LANG_FILE = lang;
	    try {
	        conf.save(getLangFile());
	    } catch(IOException e) {
	        SimpleLogger.logMessage("Not able to create and save a new language file. Reason: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	
	/**
	* Get the language.yml file.
	* @return The language.yml file.
	*/
	public File getLangFile() {
	    return LANG_FILE;
	}
	
	/** Used to get the parsed YAML from the language file
	 * @return The YamlConfiguration of the language file
	 */
	public YamlConfiguration getLang() {
	    return LANG;
	}
	
	/** Used to get the Offline UUID Handler
	 * 
	 * @return The current OfflineUUIDHandler
	 */
	public OfflineUUIDHandler getOfflineUUIDHandler() {
		
		return this.offlineUUIDHandler;
		
	}
	
	/** Used to determine if the server should be put into staff mode
	 * @return Will return true if the server is in staff mode
	 */
	public static boolean isStaffMode() {
		
		return Banana.STAFF_MODE;
		
	}
	
	/** Used to set the server's staff mode status
	 * @param b The new status of staff mode
	 */
	public static void setStaffMode(boolean b) {
		
		Banana.STAFF_MODE = b;
		
	}
	
}
