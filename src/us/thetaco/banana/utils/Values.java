package us.thetaco.banana.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** A class holding all values from the config file
 * @author activates
 *
 */
public class Values {

	/** Used to determine if MySQL should be used
	 * 
	 */
	public static boolean USE_MYSQL = false;
	
	/** The address of the remote MySQL server
	 * 
	 */
	public static String MYSQL_ADDRESS = "127.0.0.1";
	
	/** The port of the remote MySQL server
	 * 
	 */
	public static int MYSQL_PORT = 3307;
	
	/** The database name of the mysql database
	 * 
	 */
	public static String MYSQL_DATABASE_NAME = "banana";
	
	/** The database username
	 * 
	 */
	public static String MYSQL_DATABASE_USERNAME = "root";
	
	/** The database password
	 * 
	 */
	public static String MYSQL_DATABASE_PASSWORD = "tunafish";
	
	/** Used to distinguish this server from the other ones connected to the mysql server
	 * 
	 */
	public static String SERVER_NAME = "main";
	
	/** Used to determine if announcements are sent on ban
	 * 
	 */
	public static boolean ANNOUNCE_BAN = true;
	
	/** Used to determine if announcements are sent on IP ban
	 * 
	 */
	public static boolean ANNOUNCE_BANIP = true;
	
	/** Used to determine if announcements are sent on kick
	 * 
	 */
	public static boolean ANNOUNCE_KICK = true;
	
	/** Used to determine if announcements are sent on mute
	 * 
	 */
	public static boolean ANNOUNCE_MUTE = true;
	
	/** Used to determine if announcements are sent on tempban
	 * 
	 */
	public static boolean ANNOUNCE_TEMPBAN = true;
	
	/** Used to determine if announcements are sent on an IP tempban
	 * 
	 */
	public static boolean ANNOUNCE_TEMPBANIP = true;
	
	/** Used to determine if announcements are sent on a tempmute
	 * 
	 */
	public static boolean ANNOUNCE_TEMPMUTE = true;
	
	/** Used to determine if announcements are sent on unban
	 * 
	 */
	public static boolean ANNOUNCE_UNBAN = true;
	
	/** Used to determine if announcements are sent on unbanip
	 * 
	 */
	public static boolean ANNOUNCE_UNBANIP = true;
	
	/** Used to determine if announcements are sent on unmute
	 * 
	 */
	public static boolean ANNOUNCE_UNMUTE = true;
	
	/** Used to determine if announcements are sent on warning
	 * 
	 */
	public static boolean ANNOUNCE_WARNING = true;
	
	/** Used to determine if players are sent notification on mute
	 * 
	 */
	public static boolean NOTIFY_MUTE = true;
	
	/** Used to determine if players are sent notification on a tempmute
	 * 
	 */
	public static boolean NOTIFY_TEMPMUTE = true;
	
	/** Used to determine if players are sent notification on unmute
	 * 
	 */
	public static boolean NOTIFY_UNMUTE = true;
	
	/** Used to determine if players will be notified upon being warned
	 * 
	 */
	public static boolean NOTIFY_WARNING = true;
	
	/** Used to determine how many seconds are between mute check loops
	 * 
	 */
	public static int MUTE_CHECK_TIME = 5;
	
	/** Used to determine if warning actions should be enabled
	 * 
	 */
	public static boolean TAKE_WARNING_ACTION = true;
	
	/** All actions that will be taken against players at certain warns increment amounts
	 * 
	 */
	public static Map<Integer, String> WARNING_ACTIONS = new HashMap<Integer, String>();
	
	/** Used to determine if muting commands should be enabled or not
	 * 
	 */
	public static boolean MUTING_COMMAND_ENABLED = true;
	
	/** The commands that are blocked when a player is muted will be stored here from the config
	 * 
	 */
	public static List<String> MUTING_COMMANDS = new ArrayList<String>();
	
	/** Used to tell if a player should be notified that they are muted whenever they attempt to chat
	 * 
	 */
	public static boolean MUTE_NOTIFY_ON_CHAT = true;
	
	/** Used to determine if the name attached to an IP should be banned in an IP ban or not
	 * 
	 */
	public static boolean BAN_USERNAME_OF_IP = true;
	
	/** Used to determine if the ip of a banned-player trying to join should also be banned
	 * 
	 */
	public static boolean BAN_JOINING_IP = true;
	
	/** Used to determine if the player joining on a banned ip should also be banned
	 * 
	 */
	public static boolean BAN_JOINING_PLAYER = true;
	
	/** Used to determine if it should be announced that a player was banned because their IP was attached to a
	 * certain IP
	 * 
	 */
	public static boolean ANNOUNCE_BAN_USERNAME_OF_IP = true;
	
	/** Used to determine if kick messages should be formatted for bungeecord
	 * 
	 */
	public static boolean BUNGEE_CORD_FORMAT = false;
	
}
