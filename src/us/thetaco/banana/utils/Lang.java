package us.thetaco.banana.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public enum Lang {

	NO_PERMISSIONS("no-permissions", "&aYou do not have the required permissions to run this command"),
	NO_PLAYER_SPECIFIED("no-player-specified", "&cYou did not specify a player!"),
	CANNOT_BE_BANNED("cannot-be-banned", "&e{PLAYER} &acannot be banned!"),
	ALREADY_BANNED("already-banned", "&e{PLAYER} &ais already banned!"),
	ALREADY_TEMP_BANNED("already-temp-banned", "&e{PLAYER} &ais already temp-banned"),
	BAN_SUCCESS("ban-success", "&e{PLAYER} &bhas been successfully banned"),
	BAN_BROADCAST("ban-broadcast", "&e{PLAYER} &3has been banned by &e{EXECUTOR}&3. &fReason: &e\"{MESSAGE}\""),
	DEFAULT_BAN_MESSAGE("default-ban-message", "&eIgnoring the rules."),
	DEFAULT_IP_BAN_MESSAGE("default-ip-ban-message", "&eIgnoring the rules."),
	BAN_FORMAT("ban-format", "&aYou have been banned by &e{EXECUTOR}&a. {NEWLINE}&fReason: &e{MESSAGE}{NEWLINE}&bIf you find this ban unfair, appeal at (server website)"),
	BAN_IP_INCORRECT_ARGS("ban-ip-incorrect-args", "&bPlayername not entered, assuming IP was given"),
	CANNOT_BE_IP_BANNED("cannot-be-ip-banned", "&e{PLAYER} &acannot be IP banned!"),
	USING_IP("using-ip", "&bPlayername not recognized, assuming IP was given"),
	NO_IP_STORED("no-ip-stored", "&aThere is no IP address stored for this UUID"),
	BAN_IP_FORMAT("ban-ip-format", "&aYou have been IP banned by &e{EXECUTOR}&a. {NEWLINE}&fReason: &e{MESSAGE}{NEWLINE}&bIf you find this ban unfair, appeal at (server website)"),
	IP_BAN_SUCCESS("ip-ban-success", "&bThe IP of &e{PLAYER} &bhas been sucessfully banned"),
	IP_BAN_BROADCAST("ip-ban-broadcast", "&e{PLAYER} &3has been IP banned by &e{EXECUTOR}&3. &fReason: &e\"{MESSAGE}\""),
	PLAYER_IP_AUTOBANNED("player-ip-autobanned", "{NEWLINE}&aYou have been autobanned for association with a banned IP. {NEWLINE}&fReason: &e{MESSAGE}"),
	AUTOBAN_BROADCAST("autoban-broadcast", "&&e{PLAYER} &3has been autobanned for being association with a banned IP!"),
	BAN_LIST_HEADER("ban-list-header", "&eBanned Users:"),
	DEL_WARNING_WRONG_ARGS("del-warning-wrong-args", "&cYou must specify the ID # of a warning to delete it!{NEWLINE}&cUsage: /listwarnings <playername>"),
	NUMBER_EXPECTED("number-expected", "&c{OBJECT} was given, but a number was expected!"),
	WARNING_DELETE_SUCCESSFUL("warning-delete-successful", "&bWarn history deleted!"),
	IP_BAN_LIST_HEADER("ip-ban-list-header", "&eBanned IPs:"),
	KICK_WRONG_ARGS("kick-wrong-args", "&cYou must specify a player to be kicked {NEWLINE}&cUsage: /kick <player> [message]"),
	PLAYER_NOT_FOUND("player-not-found", "&aPlayer '&e{OBJECT}' not found!"),
	PLAYER_CANNOT_BE_KICKED("player-cannot-be-kicked", "&e{PLAYER} &acannot be kicked!"),
	DEFAULT_KICK_MESSAGE("default-kick-message", "&eIgnoring the rules"),
	KICK_FORMAT("kick-format", "&aYou have been kicked by &e{EXECUTOR}&a. {NEWLINE}&fReason: &e{MESSAGE}"),
	KICK_SUCCESS("kick-success", "&e{PLAYER} &bhas been kicked"),
	KICK_BROADCAST("kick-broadcast", "&e{PLAYER} &3has been kicked by &e{EXECUTOR}&a. &fReason: &e{MESSAGE}"),
	LIST_WARNINGS_WRONG_ARGS("list-warnings-wrong-args", "&cYou must specify a player to view their warns!"),
	PLAYER_NEVER_ONLINE("player-never-online", "&e{OBJECT} &ahas never been on the server before"),
	MUTE_WRONG_ARGS("mute-wrong-args", "&cYou must specify a player to mute!{NEWLINE}&cUsage: /mute <player> [reason]"),
	CANNOT_BE_MUTED("cannot-be-muted", "&e{PLAYER} &acannot be muted!"),
	ALREADY_MUTED("already-muted", "&e{PLAYER} &ais already muted"),
	ALREADY_TEMP_MUTED("already-temp-muted", "&e{PLAYER} &ais already temp-muted"),
	MUTE_SUCCESS("mute-success", "&e{PLAYER} &bhas been successfully muted"),
	MUTE_BROADCAST("mute-broadcast", "&e{PLAYER} &3been muted by &e{EXECUTOR}&3. &fReason: &e{MESSAGE}"),
	MUTE_NOTIFY("mute-notify", "&aYou have been muted by &e{EXECUTOR}&a! &fReason: &e\"{Message}\""),
	TEMPBAN_INCORRECT_ARGS("tempban-inccorect-args", "&cInvalid Arguments {NEWLINE}&cUsage: /tempban <playername> <days:hours:minutes:seconds> [message]"),
	CANNOT_BE_TEMPBANNED("cannot-be-tempbanned", "&e{PLAYER} &acannot be temp banned!"),
	IMPROPER_TIME_GIVEN("improper-time-given", "&cImproper time given!"),
	DEFAULT_TEMPBAN_MESSAGE("default-tempban-message", "&eIgnoring the rules"),
	TEMPBAN_FORMAT("tempban-format", "&aYou have been tempbanned for &e{DAYS} day(s)&a, &e{HOURS} hour(s)&a, &e{MINUTES} minute(s)&a, &e{SECONDS} seconds &aby &e{EXECUTOR}&a. {NEWLINE}&fReason: &e{MESSAGE}"),
	TEMPBAN_BROADCAST("tempban-broadcast", "&e{PLAYER} &3has been tempbanned from the server for &e{DAYS} day(s)&3, &e{HOURS} hour(s)&3, &e{MINUTES} minute(s)&3, &e{SECONDS} seconds &3by &e{EXECUTOR}&3. &fReason: &e\"{MESSAGE}\""),
	TEMPBAN_IP_INCORRECT_ARGS("tempban-ip-incorrect-args", "&cInvalid Arguments{NEWLINE}&cUsage: /tempbanip <playername/IP> <days:hours:minutes:seconds> [message]"),
	CANNOT_BE_TEMP_IP_BANNED("cannot-be-temp-ip-banned", "&e{PLAYER} &acannot be tempbanned!"),
	TEMP_BAN_IP_SUCCESS("temp-ban-ip-success", "&bIP has been sucessfully banned!"),
	IP_TEMPBAN_FORMAT("ip-tempban-format", "{NEWLINE}&aYou have been temp ip-banned for &e{DAYS} day(s)&a, &e{HOURS} hour(s)&a, &e{MINUTES} minute(s)&a, &e{SECONDS} seconds &aby &e{EXECUTOR}&a. {NEWLINE}&fReason: &e{MESSAGE}"),
	IP_TEMPBAN_BROADCAST("ip-tempban-broadcast", "&e{PLAYER} &3has been temp ip-banned for &e{DAYS} day(s)&3, &e{HOURS} hour(s)&3, &e{MINUTES} minute(s)&3, &e{SECONDS} seconds &3by &e{EXECUTOR}&3. &fReason: &e\"{MESSAGE}\""),
	IP_TEMPBAN_DEFAULT_MESSAGE("default-temp-ban-ip-message", "&eIgnoring the rules"),
	PLAYER_IP_AUTO_TEMPBANNED("player-ip-auto-tempbanned", "&aYou have been auto temp-banned for {NEWLINE}&e{DAYS} day(s)&a, &e{HOURS} hour(s)&a, &e{MINUTES} minutes&a, {SECONDS} seconds &aby &e{EXECUTOR} &afor association with a banned IP. {NEWLINE}&fReason: &e{MESSAGE}"),
	AUTO_TEMPBAN_BROADCAST("auto-tempban-broadcast", "&c{PLAYER} has been auto temp-banned for having a temp-banned IP!"),
	TEMPMUTE_WRONG_ARGS("tempmute-wrong-args", "&cInvalid Arguments{NEWLINE}&cUsage: /tempmute <playername> <days:hours:minutes:seconds> [reason]"),
	CANNOT_BE_TEMP_MUTED("cannot-be-temp-muted", "&e{PLAYER} &acannot be temp muted!"),
	TEMPMUTE_SUCCESS("tempmute-success", "&e{PLAYER} &bhas been successfully temp-muted!"),
	DEFAULT_TEMPMUTE_MESSAGE("default-mute-message", "&eIgnoring the rules"),
	TEMP_MUTE_BROADCAST("temp-mute-broadcast", "&e{PLAYER} &3has been tempmuted for &e{DAYS} day(s)&3, &e{HOURS} hour(s)&3, &e{MINUTES} minute(s)&3, &e{SECONDS} seconds &3by &e{EXECUTOR}&3. &fReason: &e\"{MESSAGE}\""),
	TEMP_MUTE_NOTIFY("temp-mute-notify", "&aYou been tempmuted for &e{DAYS} day(s)&a, &e{HOURS} hour(s)&a, &e{MINUTES} minute(s)&a, &e{SECONDS} seconds &aby &e{EXECUTOR}&a. &fReason: &e\"{MESSAGE}\""),
	UNBAN_SUCCESS("unban-success", "&e{PLAYER} &bhas been successfully unbanned"),
	UNBAN_BROADCAST("unban-broadcast", "&e{PLAYER} &3has been unbanned by &e{EXECUTOR}"),
	UNMUTE_WRONG_ARGS("unmute-wrong-args", "&cYou must specify a player to unmute!{NEWLINE}Usage: /unmute <player>"),
	PLAYER_NOT_MUTED("player-not-muted", "&e{PLAYER} &ais not muted!"),
	UNMUTE_SUCCESSFUL("mute-successful", "&e{PLAYER} &ahas been sucessfully unmuted"),
	UNMUTE_BROADCAST("unmute-broadcast", "&e{PLAYER} &3has been unmuted by &e{EXECUTOR}"),
	UNMUTE_NOTIFY("unmute-notify", "&bYou have been unmuted"),
	WARN_WRONG_ARGS("warn-wrong-args", "&cYou must specify a player to add a warning to!{NEWLINE}Usage: /warn <player> [message]"),
	PLAYER_CANNOT_BE_WARNED("player-cannot-be-warned", "&e{PLAYER} &acannot be warned!"),
	DEFAULT_WARN_MESSAGE("default-warn-message", "&eIgnoring the rules"),
	WARNING_FORMAT("warning-format", "&aYou have been warned by &e{EXECUTOR}&a! &fReason: &e{MESSAGE}"),
	WARN_SUCCESS("warn-success", "&e{PLAYER} &bhas been successfully warned"),
	WARN_BROADCAST("warn-broadcast", "&e{PLAYER} &3has been warned by &e{EXECUTOR}&3. &fReason: &e\"{MESSAGE}\""),
	WARN_NOTIFY("warn-notify", "&aYou have been warned by &e{EXECUTOR}&a. &fReason: &e\"{MESSAGE}\""),
	NO_SPEAK_MUTED("no-speak-muted", "&aYou cannot speak while muted"),
	MUTE_TIME_EXPIRED("mute-time-expired", "&bYou mute has been lifted"),
	COMMAND_DISALLOWED_RUNNING("command-disallowed-running", "&aYou are not able to run this command while muted!"),
	ADD_STAFF_WRONG_ARGS("add-staff-wrong-args", "&cYou must specify a player to add as staff!{NEWLINE}&cUsage: /addstaff <player>"),
	ADD_STAFF_SUCCESS("add-staff-success", "&bYou have successfully added &e{PLAYER} &bto the staff list!"),
	RM_STAFF_WRONG_ARGS("rm-staff-wrong-args", "&cYou must specify a player to remove from staff!{NEWLINE}&cUsage: /rmstaff <player>"),
	ALREADY_STAFF("already-staff", "&e{PLAYER} &ais already in the staff list! Check /liststaff for a list of all staff members"),
	NOT_STAFF("not-staff", "&e{PLAYER} &ais not staff"),
	RM_STAFF_SUCCESS("rm-staff-success", "&e{PLAYER} &bhas been removed from the staff list"),
	LIST_STAFF_HEADER("list-staff-header", "&eStaff Members:"),
	WARNING_LIST_HEADER("warning-list-header", "&e{OBJECT} &bWarn History:"),
	WARNING_LIST_FORMAT("warning-list-format", "&fID: &e{ID} &fMessage: &e{MESSAGE}"),
	WARNING_BAN_MESSAGE("warnings-ban-message", "&aYou have been banned for receiving &e{NUMBER} warnings&a!"),
	WARNING_AUTOBAN_BROADCAST("warning-autoban-broadcast", "&e{PLAYER} &3was banned for recieving &e{NUMBER} warnings"),
	WARNING_KICK_MESSAGE("warnings-kick-message", "&aYou have been kicked for receiving &e{NUMBER} warnings&3!{NEWLINE}&fWarn History:{NEWLINE}&e{WARNINGS}"),
	WARNING_AUTOKICK_BROADCAST("warning-autokick-broadcast", "&e{PLAYER} &3was kicked for recieving &e{NUMBER} warnings"),
	WARNING_MUTE_LOG_MESSAGE("warning-mute-log-message", "Recieved {NUMBER} and was automatically muted"),
	WARNING_MUTE_MESSAGE("warning-mute-message", "&aYou have been muted for recieving &e{NUMBER} warnings&a!"),
	WARNING_AUTOMUTE_BROADCAST("warning-automute-broadcast", "e{PLAYER} &3was muted for recieving &e{NUMBER} warnings"),
	WARNING_TEMPBAN_MESSAGE("warning-tempban-message", "&aYou have been temp-banned for {NEWLINE}&e{DAYS} day(s)&a, &e{HOURS} hour(s)&a, &e{MINUTES} minute(s)&a, and &e{SECONDS} second(s) &afor recieving &e{NUMBER} warnings&a!{NEWLINE}&fWarning History:{NEWLINE}&e{WARNINGS}"),
	WARNING_AUTO_TEMPMUTE_BROADCAST("warning-auto-tempmute-broadcast", "&e{PLAYER} &3was temp-muted for &e{DAYS} day(s)&3, &e{HOURS} hour(s)&3, &e{MINUTES} minute(s)&3, and &e{SECONDS} second(s) &3for recieving &e{NUMBER} warnings&3!"),
	WARNING_IPBAN_MESSAGE("warning-ipban-message", "&aYou have been IP banned for receiving &e{NUMBER} warnings&a!{NEWLINE}&fWarn History:{NEWLINE}&e{WARNINGS}"),
	WARNING_AUTO_IPBAN_BROADCAST("warning-auto-ipban-broadcast", "&e{PLAYER} &3was ip-banned for recieving &e{NUMBER} warnings"),
	CONSOLE_NAME("console-name", "CONSOLE"),
	NOT_RAN_CONSOLE("not-ran-console", "This command cannot be ran from the console!"),
	KICK_ALL_SUCCESS("kick-all-success", "&bYou have successfully kicked all players from the server"),
	STAFF_MODE_KICK_MESSAGE("staff-mode-kick-message", "&aThis server is currently in staff mode!{NEWLINE}&fPlease try to reconnect at another time {NEWLINE}&bSorry for the inconvenience!"),
	STAFF_MODE_ENABLED_BROADCAST("staff-mode-enabled-broadcast", "&bStaff mode has been enabled!"),
	STAFF_MODE_DISABLED_BROADCAST("staff-mode-disabled-broadcast", "&bStaff mode has been disabled!"),
	PURGE_WARNINGS_INCORRECT_ARGS("purge-warnings-incorrect-args", "&cYou must specify a player to purge the warnings for!{NEWLINE}Usage: /purgewarnings <player>"),
	PURGE_WARNINGS_SUCCESS("purge-warnings-success", "&bYou have successfully purged the warnings for &e{PLAYER}"),
	UNBAN_IP_WRONG_ARGS("unban-ip-wrong-args", "&cYou must specify an address or player to un ip-ban!"),
	PLAYER_NOT_BANNED("player-not-banned", "&e{PLAYER} &bis not banned"),
	IP_NOT_BANNED("ip-not-banned", "&e{OBJECT} &bis not ip-banned"),
	UNBAN_IP_SUCCESS("unban-ip-success", "&b{OBJECT} has been successfully unbanned"),
	UNBAN_IP_BROADCAST("unban-ip-broadcast", "&e{PLAYER} &3has been un ip-banned by &e{EXECUTOR}"),
	AUTO_BAN_TEMP_IP_MESSAGE("auto-ban-temp-ip-message", "Your IP has been auto temp-banned for being associated with an already banned player {OBJECT}"),
	AUTO_BAN_IP_MESSAGE("auto-ban-ip-message", "Your IP has been auto-banned for being associated with an already banned player {OBJECT}"),
	AUTO_BAN_PLAYER_MESSAGE("auto-ban-player-message", "You have been auto-banned for being associated with an already banned IP {OBJECT}"),
	AUTO_TEMP_BAN_PLAYER_MESSAGE("auto-temp-ban-player-message", "You have been auto temp-banned for being associated with an already banned IP {OBJECT}"),
	BAN_INFO_WRONG_ARGS("ban-info-wrong-args", "&cYou must specify a player!{NEWLINE}&cUsage: /baninfo <player>"),
	BAN_INFO_HEADER("ban-info-header", "&4---&6Player Information&4---"),
	BAN_INFO_LINE_BAN("ban-info-line-ban", "&cIs banned: &e{OBJECT}"),
	BAN_INFO_LINE_MUTE("ban-info-line-mute", "&cIs muted: &e{OBJECT}"),
	BAN_INFO_LINE_TEMP_MUTE("ban-info-line-temp-mute", "&cIs temp-muted: &e{OBJECT}"),
	BAN_INFO_LINE_MUTE_REMOVAL("ban-info-line-mute-removal", "&cTemp-mute removal date: &e{OBJECT}"),
	BAN_INFO_LINE_TEMP_BAN("ban-info-line-temp-ban", "&cIs temp-banned: &e{OBJECT}"),
	BAN_INFO_LINE_BAN_REMOVAL("ban-info-line-ban-removal", "&cTemp-ban-removal date: &e{OBJECT}"),
	BAN_INFO_LINE_IP_BAN("ban-info-line-ip-ban", "&cIs IP-banned: &e{OBJECT}"),
	BAN_INFO_LINE_TEMP_IP_BAN("ban-info-line-temp-ip-ban", "&cIs temp ip-banned: &e{OBJECT}"),
	BAN_INFO_LINE_IP_BAN_REMOVAL("ban-info-line-ip-ban-removal", "&cIP temp-ban removal date: &e{OBJECT}"),
	BAN_INFO_LINE_PLAYER_IP("ban-info-line-player-ip", "&cIP address: &e{OBJECT}");
	
	private String path;
    private String def;
    private static YamlConfiguration LANG;
	
    Lang(String path, String start) {
        this.path = path;
        this.def = start;
    }
	
    /** Used to set the location of the language file
     * @param config
     */
    public static void setFile(YamlConfiguration config) {
        LANG = config;
    }
    
    /** Used to get the default stored in here
     * @return The default value of the message
     */
    public String getDefault() {
        return this.def;
    }
    
    /** Used to get the YAML path
     * @return A string version of the YAML path
     */
    public String getPath() {
        return this.path;
    }
	
    @Override
    public String toString() {
    	
    	String toEdit = LANG.getString(this.path, def);
    	
    	toEdit = ChatColor.translateAlternateColorCodes('&', toEdit);
    	toEdit = toEdit.replace("{NEWLINE}", "\n");
    	
        return toEdit;
    	
    }
    
    /** Used to parse time in warning actions
     * @return Returns the parsed message! :D (I'm so tired pls send help)
     */
    public String parseTimeWarningActions(int warningAmount, String warnings, int days, int hours, int minutes, int seconds) {
    	
    	String toEdit = LANG.getString(this.path, def);
    	
    	toEdit = ChatColor.translateAlternateColorCodes('&', toEdit);
    	toEdit = toEdit.replace("{NEWLINE}", "\n");
    	toEdit = toEdit.replace("{NUMBER}", Integer.toString(warningAmount));
    	toEdit = toEdit.replace("{WARNINGS}", warnings);
    	toEdit = toEdit.replace("{DAYS}", Integer.toString(days));
    	toEdit = toEdit.replace("{HOURS}", Integer.toString(hours));
    	toEdit = toEdit.replace("{MINUTES}", Integer.toString(minutes));
    	toEdit = toEdit.replace("{SECONDS}", Integer.toString(seconds));
    	
    	return toEdit;
    	
    }
    
    /** Used to parse time in warning actions
     * @return Returns the parsed message! :D (I'm so tired pls send help)
     */
    public String parseTimeWarningBroadcast(int warningAmount, String warnings, String player, int days, int hours, int minutes, int seconds) {
    	
    	String toEdit = LANG.getString(this.path, def);
    	
    	toEdit = ChatColor.translateAlternateColorCodes('&', toEdit);
    	toEdit = toEdit.replace("{NEWLINE}", "\n");
    	toEdit = toEdit.replace("{NUMBER}", Integer.toString(warningAmount));
    	toEdit = toEdit.replace("{WARNINGS}", warnings);
    	toEdit = toEdit.replace("{DAYS}", Integer.toString(days));
    	toEdit = toEdit.replace("{HOURS}", Integer.toString(hours));
    	toEdit = toEdit.replace("{MINUTES}", Integer.toString(minutes));
    	toEdit = toEdit.replace("{SECONDS}", Integer.toString(seconds));
    	toEdit = toEdit.replace("{PLAYER}", player);
    	
    	return toEdit;
    	
    }
    
    /** Used to simply parse one number into a message
     * @param number The number to parsed
     * @return The parsed message
     */
    public String parseNumber(int number) {
    	
    	String toEdit = LANG.getString(this.path, def);
    	
    	toEdit = ChatColor.translateAlternateColorCodes('&', toEdit);
    	toEdit = toEdit.replace("{NEWLINE}", "\n");
    	toEdit = toEdit.replace("{NUMBER}", Integer.toString(number));
    	
    	return toEdit;
    	
    }
    
    /** Used to parse broadcasts from the auto warn action system
     * @param player The player involved
     * @param warningAmount The amount of warnings the player had to receive this warning
     * @return The parsed message
     */
    public String parseAutoWarnBroadcast(String player, int warningAmount) {
    	
    	String toEdit = LANG.getString(this.path, def);
    	
    	toEdit = ChatColor.translateAlternateColorCodes('&', toEdit);
    	toEdit = toEdit.replace("{NEWLINE}", "\n");
    	toEdit = toEdit.replace("{NUMBER}", Integer.toString(warningAmount));
    	toEdit = toEdit.replace("{PLAYER}", player);
    	
    	return toEdit;
    	
    }
    
    /** Used to parse warning action messages
     * @param warningAmount The player's current amount of warnings
     * @param warnings The compiled warning strings
     * @return The parsed warnings
     */
    public String parseAutoWarnActions(int warningAmount, String warnings) {
    	
    	String toEdit = LANG.getString(this.path, def);
    	
    	toEdit = ChatColor.translateAlternateColorCodes('&', toEdit);
    	toEdit = toEdit.replace("{NEWLINE}", "\n");
    	toEdit = toEdit.replace("{NUMBER}", Integer.toString(warningAmount));
    	toEdit = toEdit.replace("{WARNINGS}", warnings);
    	
    	return toEdit;
    	
    }
    
    /** Used to parse the warning list (used in the /mywarns command)
     * @param id The database id of the warning
     * @param message The message tied along with the particular warning
     * @return The parsed warning list
     */
    public String parseWarningList(int id, String message) {
    	
    	String toEdit = LANG.getString(this.path, def);
    	
    	toEdit = ChatColor.translateAlternateColorCodes('&', toEdit);
    	toEdit = toEdit.replace("{NEWLINE}", "\n");
    	toEdit = toEdit.replace("{ID}", Integer.toString(id));
    	toEdit = toEdit.replace("{MESSAGE}", message);
    	
    	return toEdit;
    	
    }
    
    /** Used to parse a player's name into a language set
     * @param playerName The name to parse in
     * @return The parsed string
     */
    public String parseName(String playerName) {
    	
    	String toEdit = LANG.getString(this.path, def);
    	
    	toEdit = ChatColor.translateAlternateColorCodes('&', toEdit);
    	toEdit = toEdit.replace("{PLAYER}", playerName);
    	toEdit = toEdit.replace("{NEWLINE}", "\n");
    	
    	return toEdit;
    	
    }
    
    /** Used to parse a basic ban
     * @param executor The person who layed down dat ban!
     * @param message The message to go along with the ban
     * @return The parsed information
     */
    public String parseBanFormat(String executor, String message) {
    	
    	String toEdit = LANG.getString(this.path, def);
    	
    	toEdit = ChatColor.translateAlternateColorCodes('&', toEdit);
    	
    	if (executor != null) {
    		toEdit = toEdit.replace("{EXECUTOR}", executor);
    	}
    	
    	if (message != null) {
    		toEdit = toEdit.replace("{MESSAGE}", message);
    	}
    	
    	toEdit = toEdit.replace("{NEWLINE}", "\n");
    	
    	return toEdit;
    	
    }
    
    /** Used to parse most broadcast messages
     * @param executor The person who is layin down da law
     * @param player The person who action is being taken against
     * @return The parsed string
     */
    public String parseBroadcast(String executor, String player) {
    	
    	String toEdit = LANG.getString(this.path, def);
    	
    	toEdit = ChatColor.translateAlternateColorCodes('&', toEdit);
    	toEdit = toEdit.replace("{EXECUTOR}", executor);
    	toEdit = toEdit.replace("{PLAYER}", player);
    	toEdit = toEdit.replace("{NEWLINE}", "\n");
    	
    	return toEdit;
    	
    }
    
    /** Used to parse broadcasts specifically for warnings
     * @param executor The person sending the warning
     * @param player The receiver of a warning
     * @param message The message reason to go along with the warning
     * @return The parsed warning message
     */
    public String parseWarningBroadcast(String executor, String player, String message) {
    	
    	String toEdit = LANG.getString(this.path, def);
    	
    	toEdit = ChatColor.translateAlternateColorCodes('&', toEdit);
    	toEdit = toEdit.replace("{MESSAGE}", message);
    	toEdit = toEdit.replace("{EXECUTOR}", executor);
    	toEdit = toEdit.replace("{PLAYER}", player);
    	toEdit = toEdit.replace("{NEWLINE}", "\n");
    	
    	return toEdit;
    	
    }
    
    /** Used to parse in a random object
     * @param object The object to parse in
     * @return The parsed String
     */
    public String parseObject(Object object) {
    	
    	String toEdit = LANG.getString(this.path, def);
    	
    	toEdit = ChatColor.translateAlternateColorCodes('&', toEdit);
    	toEdit = toEdit.replace("{OBJECT}", (String) object);
    	toEdit = toEdit.replace("{NEWLINE}", "\n");
    	
    	return toEdit;
    	
    }
    
    /** Used to parse a message that deals with dates (anything with temp in it)
     * @param days Amount of days
     * @param hours Amount of hours
     * @param minutes Amount of minutes
     * @param seconds Amount of seconds
     * @param executor The player name of the player running the command
     * @param message The message to go along with said action
     * @return The parsed message
     */
    public String parseTime(int days, int hours, int minutes, int seconds, String executor, String message) {
    	
    	String toEdit = LANG.getString(this.path, def);
    	
    	toEdit = ChatColor.translateAlternateColorCodes('&', toEdit);
    	toEdit = toEdit.replace("{DAYS}", Integer.toString(days));
    	toEdit = toEdit.replace("{HOURS}", Integer.toString(hours));
    	toEdit = toEdit.replace("{MINUTES}", Integer.toString(minutes));
    	toEdit = toEdit.replace("{SECONDS}", Integer.toString(seconds));
    	toEdit = toEdit.replace("{EXECUTOR}", executor);
    	toEdit = toEdit.replace("{MESSAGE}", message);
    	toEdit = toEdit.replace("{NEWLINE}", "\n");
    	
    	return toEdit;
    	
    }
    
    /** Used to parse time for a broadcast type message
     * @param days Amount of days
     * @param hours Amount of hours
     * @param minutes Amount of minutes
     * @param seconds Amount of seconds
     * @param player The player who is having the action done against them
     * @param executor The player name of the player running the command
     * @param message The message to go along with said action
     * @return The parsed message
     * @return
     */
    public String parseTimeBroadcast(int days, int hours, int minutes, int seconds, String player, String executor, String message) {
    	
    	String toEdit = LANG.getString(this.path, def);
    	
    	toEdit = ChatColor.translateAlternateColorCodes('&', toEdit);
    	toEdit = toEdit.replace("{PLAYER}", player);
    	toEdit = toEdit.replace("{DAYS}", Integer.toString(days));
    	toEdit = toEdit.replace("{HOURS}", Integer.toString(hours));
    	toEdit = toEdit.replace("{MINUTES}", Integer.toString(minutes));
    	toEdit = toEdit.replace("{SECONDS}", Integer.toString(seconds));
    	toEdit = toEdit.replace("{EXECUTOR}", executor);
    	toEdit = toEdit.replace("{MESSAGE}", message);
    	toEdit = toEdit.replace("{NEWLINE}", "\n");
    	
    	return toEdit;
    	
    }
    
    /** Used specifically in the process of parsing the temp ban messages
     * @param days Amount of days
     * @param hours Amount of hours
     * @param minutes Amount of minutes
     * @param seconds mount of seconds
     * @param player The player who is temp banned
     * @param executor The entity that layed down dat ban
     * @param message The message of the ban
     * @return The parsed string
     */
    public String parseTimeTempBan(int days, int hours, int minutes, int seconds, String player, String executor, String message) {
    	
    	String toEdit = LANG.getString(this.path, def);
    	
    	toEdit = ChatColor.translateAlternateColorCodes('&', toEdit);
    	toEdit = toEdit.replace("{DAYS}", Integer.toString(days));
    	toEdit = toEdit.replace("{HOURS}", Integer.toString(hours));
    	toEdit = toEdit.replace("{MINUTES}", Integer.toString(minutes));
    	toEdit = toEdit.replace("{SECONDS}", Integer.toString(seconds));
    	toEdit = toEdit.replace("{EXECUTOR}", executor);
    	toEdit = toEdit.replace("{MESSAGE}", message);
    	toEdit = toEdit.replace("{NEWLINE}", "\n");
    	
    	return toEdit;
    	
    }
    
}
