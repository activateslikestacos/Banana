package us.thetaco.banana.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import us.thetaco.banana.sql.DatabaseManager.BannerType;

/** A cache of bans that will be updated along with the database. A much quicker method than waiting for the database
 * @author activates
 *
 */
public class BanCache {

	private Map<String, String> bannedUUIDs = new HashMap<String, String>();
	private Map<String, Long> tempBanned = new HashMap<String, Long>();
	private Map<String, String> bannedIPs = new HashMap<String, String>();
	private Map<String, Long> tempBannedIPs = new HashMap<String, Long>();
	private Map<String, String> tempBanFriendlyDate = new HashMap<String, String>();
	private Map<String, Enum<BannerType>> bannedBy = new HashMap<String, Enum<BannerType>>();
	private Map<String, Enum<BannerType>> bannedByIPs = new HashMap<String, Enum<BannerType>>();
	private Map<String, String> bannedByPlayer = new HashMap<String, String>();
	private Map<String, String> bannedByPlayerIP = new HashMap<String, String>();
	
	/** Adds a banned UUID to the cache. If the UUID is already banned, the process will be cancelled
	 * @param uuid The uuid to ban (in string form)
	 * @param message The message to set with the ban (set to null if default should be used)
	 * @param type The banner type (ie console player etc)
	 * @return Will return true if the UUID was banned. If it already banned, the method will return false
	 */
	public boolean addBannedUUID(String uuid, String message, Enum<BannerType> type, String bannerUUID) {
		
		if (uuid == null) return false;
		
		if (this.isUUIDBanned(uuid)) return false;
		
		if (message == null) {
		
			bannedUUIDs.put(uuid, Lang.DEFAULT_BAN_MESSAGE.toString());
		
		} else {
			
			bannedUUIDs.put(uuid, message);
		}
		
		// add the banner type
		this.bannedBy.put(uuid, type);
		
		// add the banneruuid if the type is a player
		if (type == BannerType.PLAYER) {
			this.bannedByPlayer.put(uuid, bannerUUID);
		}
		
		return true;
		
	}
	
	/** Adds a banned UUID to the cache. If the UUID is already banned, the process will be cancelled
	 * @param uuid The uuid to ban
	 * @param message The message to set with the ban (set to null if default should be used)
	 * @return Will return true if the UUID was banned. If it already banned, the method will return false
	 */
	public boolean addBannedUUID(UUID uuid, String message, Enum<BannerType> type, UUID bannerUUID) {
		
		if (uuid == null) return false;
		
		if (this.isUUIDBanned(uuid)) return false;
		
		if (message == null) {
		
			bannedUUIDs.put(uuid.toString(), Lang.DEFAULT_BAN_MESSAGE.toString());
		
		} else {
			
			bannedUUIDs.put(uuid.toString(), message);
		}
		
		// add the banner type
		this.bannedBy.put(uuid.toString(), type);
		
		// add the banneruuid if the type is a player
		if (type == BannerType.PLAYER) {
			this.bannedByPlayer.put(uuid.toString(), bannerUUID.toString());
		}
		
		return true;
		
	}
	
	/** Used to check if a particular UUID is banned
	 * @param uuid The UUID to check if banned (in string form)
	 * @return Will return true if the specified UUID is banned
	 */
	public boolean isUUIDBanned(String uuid) {
				
		if (uuid == null) return false;
		
		for (String s : bannedUUIDs.keySet()) {
			
			if (s.equals(uuid)) return true;
			
		}
		
		return false;
		
	}
	
	/** Used to check if a particular UUID is banned
	 * @param uuid The UUID to check if banned
	 * @return Will return true if the specified UUID is banned
	 */
	public boolean isUUIDBanned(UUID uuid) {
		
		for (String s : bannedUUIDs.keySet()) {
			
			if (s.equals(uuid.toString())) return true;
			
		}
		
		return false;
		
	}
	
	/** Used to get the particular ban message from a UUID
	 * @param uuid The UUID of the player to get the ban message for (in String form)
	 * @return The ban message stored. Will return null if there is no message stored (Would be a good idea to use the default value in that case)
	 */
	public String getBanMessage(String uuid) {
		
		String banMessage = this.bannedUUIDs.get(uuid);
		
		if (banMessage == null) {
			return Lang.DEFAULT_BAN_MESSAGE.toString();
		} else {
			return banMessage;
		}
		
	}
	
	/** Used to get the particular ban message from a UUID
	 * @param uuid The UUID of the player to get the ban message for
	 * @return The ban message stored. Will return null if there is no message stored (Would be a good idea to use the default value in that case)
	 */
	public String getBanMessage(UUID uuid) {
		
		String banMessage = this.bannedUUIDs.get(uuid.toString());
		
		if (banMessage == null) {
			return Lang.DEFAULT_BAN_MESSAGE.toString();
		} else {
			return banMessage;
		}
		
	}
	
	/** Used to check if a player is tempbanned
	 * @param uuid The uuid of the player to check (in String form)
	 * @return Will return true if the player is tempBanned
	 */
	public boolean isTempBanned(String uuid) {
		
		for (String s : tempBanned.keySet()) {
			
			if (s.equals(uuid)) return true;
			
		}
		
		return false;
		
	}
	
	/** Used to check if a player is tempbanned
	 * @param uuid The uuid of the player to check (in String form)
	 * @return Will return true if the player is tempBanned
	 */
	public boolean isTempBanned(UUID uuid) {
		
		for (String s : tempBanned.keySet()) {
			
			if (s.equals(uuid.toString())) return true;
			
		}
		
		return false;
		
	}
	
	/** Used to get the date the tempban should be lifted
	 * @param uuid The uuid of the player to get the tempban date
	 * @return Will return the date the tempban will be lifted.
	 */
	public Date getTempBanEndDate(String uuid) {
		
		if (!this.isTempBanned(uuid)) return null;
		
		Calendar currentDate = Calendar.getInstance();
		
		currentDate.setTimeInMillis(this.tempBanned.get(uuid));
		
		return currentDate.getTime();
		
	}
	
	/** Used to get the date the tempban should be lifted
	 * @param uuid The uuid of the player to get the tempban date
	 * @return Will return the date the tempban will be lifted.
	 */
	public Date getTempBanEndDate(UUID uuid) {
		
		if (!this.isTempBanned(uuid)) return null;
		
		Calendar currentDate = Calendar.getInstance();
		
		currentDate.setTimeInMillis(this.tempBanned.get(uuid.toString()));
		
		return currentDate.getTime();
		
	}
	
	/** Adds a player to the tempbanned list
	 * @param uuid The uuid to ban (in string form)
	 * @param message The message to tie with it (set to null to use default)
	 * @param unbanDate The date to unban the player at
	 */
	public void addTempBannedUUID(String uuid, String message, Date unbanDate, Enum<BannerType> type, String bannerUUID, String unparsedDate) {
		
		this.addBannedUUID(uuid, message, type, bannerUUID);
		
		this.tempBanned.put(uuid, unbanDate.getTime());
		
		this.addFriendlyDate(uuid, unparsedDate);
		
	}
	
	/** Adds a player to the tempbanned list
	 * @param uuid The uuid to ban
	 * @param message The message to tie with it (set to null to use default)
	 * @param unbanDate The date to unban the player at
	 */
	public void addTempBannedUUID(UUID uuid, String message, Date unbanDate, Enum<BannerType> type, UUID bannerUUID, String unparsedDate) {
		
		this.addBannedUUID(uuid.toString(), message, type, bannerUUID.toString());
		
		this.tempBanned.put(uuid.toString(), unbanDate.getTime());
		
		this.addFriendlyDate(uuid.toString(), unparsedDate);
		
	}
	
	/** Used to delete both normal bans and temp bans
	 * @param uuid The uuid to unban (In string form)
	 * @return Will return true if the player was sucessfully unbanned
	 */
	public boolean removeBan(String uuid) {
		
		if (!this.isUUIDBanned(uuid) && !this.isTempBanned(uuid)) return false;
		
		this.bannedUUIDs.remove(uuid);
		this.tempBanned.remove(uuid);
		return true;
		
	}
	
	/** Used to delete both normal bans and temp bans
	 * @param uuid The uuid to unban (In string form)
	 * @return Will return true if the player was sucessfully unbanned
	 */
	public boolean removeBan(UUID uuid) {
		
		if (!this.isUUIDBanned(uuid) && !this.isTempBanned(uuid)) return false;
		
		this.bannedUUIDs.remove(uuid.toString());
		this.tempBanned.remove(uuid.toString());
		return true;
		
	}
	
	/** Used to fetch a bulk list of all banned players
	 * @return A set/collection of all the banned player's uuid
	 */
	public Set<String> getBannedPlayers() {
		
		return this.bannedUUIDs.keySet();
		
	}
	
	/** Adds a banned IP to the cache
	 * @param address The IP address to be banned
	 * @param message The message to be tied with the ban (optional)
	 */
	public void addBannedIP(String address, String message, Enum<BannerType> type, String bannerUUID) {
		
		this.bannedIPs.put(address, message);
		
		this.bannedByIPs.put(address, type);
		
		// add the banneruuid if the type is a player
		if (type == BannerType.PLAYER) {
			this.bannedByPlayerIP.put(address, bannerUUID);
		}
		
	}
	
	/** Used to determine if an IP is already banned
	 * @param address The address to check for
	 * @return Will return true if the IP is already banned
	 */
	public boolean isIPBanned(String address) {
		
		for (String s : this.bannedIPs.keySet()) {
			
			if (s.equalsIgnoreCase(address)) return true;
			
		}
		
		return false;
	}
	
	/** Returns if an IP is temp banned
	 * @param address The ip in question
	 * @return Will return true if the IP is temp banned
	 */
	public boolean isIPTempBanned(String address) {

		for (String s : this.tempBannedIPs.keySet()) {
			
			if (s.equals(address)) return true;
			
		}
		
		return false;
		
	}

	/** Used to temp ban and IP address
	 * @param address The address to temp ban
	 * @param message The message to go with the ban (optional)
	 * @param unbanDate The unban date of the player
	 */
	public void tempBanIP(String address, String message, Date unbanDate, Enum<BannerType> type, String bannerUUID, String unparsedDate) {
		
		if (unbanDate == null) return;
		
		this.addBannedIP(address, message, type, bannerUUID);
		
		this.tempBannedIPs.put(address, unbanDate.getTime());
		
		this.addFriendlyDate(address, unparsedDate);
		
	}
	
	/** Used to unban an IP
	 * @param address The IP to unban
	 * @return Will return true if the IP was banned before getting removed
	 */
	public boolean unbanIP(String address) {
		
		if (!this.isIPBanned(address)) return false;
		
		this.bannedIPs.remove(address);
		this.tempBannedIPs.remove(address);
		
		return true;
		
	}
	
	/** Used to get the particular IP ban message from a UUID
	 * @param uuid The UUID of the player to get the ban message for (in String form)
	 * @return The ban message stored. Will return null if there is no message stored (Would be a good idea to use the default value in that case)
	 */
	public String getIPBanMessage(String address) {
		
		String banMessage = this.bannedIPs.get(address);
		
		if (banMessage == null) {
			return Lang.DEFAULT_IP_BAN_MESSAGE.toString();
		} else {
			return banMessage;
		}
		
	}
	
	/** Used to get the date the tempban should be lifted
	 * @param uuid The uuid of the player to get the tempban date
	 * @return Will return the date the tempban will be lifted.
	 */
	public Date getIPTempBanEndDate(String address) {
		
		if (!this.isIPTempBanned(address)) return null;
		
		Calendar currentDate = Calendar.getInstance();
		
		currentDate.setTimeInMillis(this.tempBannedIPs.get(address));
		
		return currentDate.getTime();
		
	}
	
	/** Returns a list of all the banned IPs
	 * @return
	 */
	public Set<String> getIPBans() {
		
		return this.bannedIPs.keySet();
		
	}
	
	/** Used to find out what entity banned the player in question
	 * @param uuid The uuid of the player to check for
	 * @return Will return the BannerType enum for the player's ban type. Will return null if the player is not banned
	 */
	public Enum<BannerType> getBannerType(String uuid) {
		
		if (!this.isUUIDBanned(uuid)) return null;
		
		return this.bannedBy.get(uuid);
		
	}
	
	/** Used to get the entity that banned the address in question
	 * @param address The address of the player to check for
	 * @return Will return the BannerType enum of the address' ban type. Will return null if none was found
	 */
	public Enum<BannerType> getBannerTypeIP(String address) {
		
		if (!this.isIPBanned(address)) return null;
		
		return this.bannedByIPs.get(address);
		
	}
	
	/** Gets the banner of supplied UUID. Will return null if the banner wasn't a player or the player wans't banned at all
	 * @param uuid The uuid of banned player
	 * @return Will return the uuid of the banner if it exists and the banner was a player
	 */
	public String getBanner(String uuid) {
		
		if (!this.isUUIDBanned(uuid)) return null;
		
		return this.bannedByPlayer.get(uuid);
		
	}
	
	/** Gets the banner of supplied IP. Will return null if the banner wasn't a player or the player wans't banned at all
	 * @param address The banned address
	 * @return Will return the uuid of the banner if it exists and the banner was a player
	 */
	public String getBannerIP(String address) {
		
		if (!this.isIPBanned(address)) return null;
		
		return this.bannedByPlayerIP.get(address);
		
	}
	
	/** Used to add the pure form of the date to the ban cache for the purpose of storing it in the database. This will help for making
	 * a more dynamic message to kick someone upon trying to join
	 * @param s A UUID or ip address.. just make sure you remember which one you put it under!
	 * @param unparsedDate
	 */
	public void addFriendlyDate(String s, String unparsedDate) {
		
		this.tempBanFriendlyDate.put(s, unparsedDate);
		
	}
	
	/** Used to fetch the pure form of the stored date.
	 * @param s Either the UUID or ip address you used to store this dtate
	 * @return The unparsed date eg: 0:0:1:0 <-- 1 minute
	 */
	public String getFriendlyDate(String s) {
		
		return this.tempBanFriendlyDate.get(s);
		
	}
	
}
