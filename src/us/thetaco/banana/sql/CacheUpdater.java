package us.thetaco.banana.sql;

import java.util.Date;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import info.dyndns.thetaco.uuid.api.Main;
import us.thetaco.banana.Banana;
import us.thetaco.banana.sql.DatabaseManager.BannerType;
import us.thetaco.banana.sql.DatabaseManager.UpdateType;
import us.thetaco.banana.utils.Action;
import us.thetaco.banana.utils.Lang;
import us.thetaco.banana.utils.SimpleLogger;
import us.thetaco.banana.utils.Values;

/** A class that is only for handling updates from the update table in the database.
 * Methods in here will be ran from an async thread.
 * @author activates
 *
 */
public class CacheUpdater {

	// TODO: make these messages configurable in the lang (like the update received notifications)
	
	/** Used to handle the update information from the database
	 * @param type The type of update sent from the database
	 * @param data The extra data provided by the server that published the update
	 */
	public synchronized void handleUpdate(Enum<UpdateType> type, String data, String updateSender) {
		
		// gather all of the information and split up the data etc
		String[] updateData = data.split("\\{-S-P-L-I-T-\\}");
		
		try {
		
			// start checking for all the conditions
			if (type == UpdateType.WARN) {
				
				// handle the update type being warn
				
				String warnedUUID = updateData[0];
				String warnerUUID = updateData[1];
				
				// un-fixing the ' in the database
				String warnMessage = updateData[2].replace("&#00", "'");
				
				Banana.getWarnCache().addWarning(warnedUUID, warnMessage);
				
				// log the message that the server has been updated
				SimpleLogger.logMessage("Warning update received from server " + updateSender + ". Update has been applied to local cache.");
				
				// update the cache and push the announcement
				// Take care of warning actions if they are enabled
				if (Values.TAKE_WARNING_ACTION) {
					
					this.asyncTakeWarningAction(warnedUUID);
					
				}
				
				// check if announcements are enabled for this command.. then release the annoucnement
				if (Values.ANNOUNCE_WARNING) {
							
					this.asyncWarnBroadcastMessage(warnerUUID, warnedUUID, warnMessage);
					
				}				
				
				// check if the player should be notified
				if (Values.NOTIFY_WARNING) {
					
					this.asyncWarnMessagePlayer(warnedUUID, warnerUUID, warnMessage);
					
				}
				
			} else if (type == UpdateType.DECREMENT_WARN) {
				
				// handle the update type being del warn
				
				String uuid = updateData[0];
				String warningMessage = updateData[1].replace("&#00", "'");
				
				Banana.getWarnCache().removeWarning(uuid, warningMessage);
				
				// log the message that the server has been updated
				SimpleLogger.logMessage("Warning decrement update received from server " + updateSender + ". Update has been applied to local cache.");
				
			} else if (type == UpdateType.BAN) {
				
				// handle the update type being ban
				
				String uuid = updateData[0];
				String bannerUUID = updateData[2];

				BannerType bannerType = BannerType.valueOf(updateData[1]);
				
				String banMessage = updateData[3].replace("&#00", "'");
				
				boolean tempBanned = Boolean.valueOf(updateData[4]);
				String tempBanDate = updateData[5];
				
				String tempBanPureDate = updateData[6];
				String[] timeString = tempBanPureDate.split(":");
								
				// start layin down dat ban!
				
				// prepare the banner name here
				String bannerName = Banana.getPlayerCache().getLatestName(bannerUUID);
				
				if (bannerName == null) bannerName = Lang.CONSOLE_NAME.toString();
				
				if (tempBanned == false) {
					
					// this is for a permanent ban
					Banana.getBanCache().addBannedUUID(uuid, banMessage.replace("&#00", "'"), bannerType, bannerUUID);
					
					this.asyncKickPlayer(uuid, Lang.BAN_FORMAT.parseBanFormat(bannerName, banMessage), false);
					
					// log the message that the server has been updated
					SimpleLogger.logMessage("Ban update received from server " + updateSender + ". Update has been applied to local cache.");
					
					if (Values.ANNOUNCE_BAN) {
						this.asyncBanBroadcastMessage(bannerUUID, uuid, banMessage);
					}
					
				} else {
					
					int seconds = 0;
					int minutes = 0;
					int hours = 0;
					int days = 0;
					
					// PREPARE FOR SUCK!
					if (timeString.length == 1) {
						
						// this will run if seconds are wanted
						seconds = Integer.parseInt(timeString[0]);
					
						
					} else if (timeString.length == 2) {
						
						// this will run if minutes and seconds are wanted
						minutes = Integer.parseInt(timeString[0]);
						seconds = Integer.parseInt(timeString[1]);
						
					} else if (timeString.length == 3) {
					
						// this will run if seconds, minutes, and hours are wanted
						hours = Integer.parseInt(timeString[0]);
						minutes = Integer.parseInt(timeString[1]);
						seconds = Integer.parseInt(timeString[2]);
					
					} else {
						
						// this will run if seconds, minutes, hours, and days are wanted
						days = Integer.parseInt(timeString[0]);
						hours = Integer.parseInt(timeString[1]);
						minutes = Integer.parseInt(timeString[2]);
						seconds = Integer.parseInt(timeString[3]);
						
					}
					
					
					// this is for a temporary ban
					Banana.getBanCache().addTempBannedUUID(uuid, banMessage, new Date(Long.parseLong(tempBanDate)), bannerType, bannerUUID, tempBanPureDate);
					
					this.asyncKickPlayer(uuid, Lang.TEMPBAN_FORMAT.parseTime(days, hours, minutes, seconds, bannerName, banMessage), false);
					
					// checking if the server wants us to announce temp bans accross the server
					if (Values.ANNOUNCE_TEMPBAN) {
						
						this.asyncTempBanBroadcastMessage(bannerUUID, uuid, banMessage, days, hours, minutes, seconds);
						
					}
					
					// log the message that the server has been updated
					SimpleLogger.logMessage("Temp-ban update received from server " + updateSender + ". Update has been applied to local cache.");
					
				}
				
			} else if (type == UpdateType.MUTE) {
				
				// handle the update type being mute
				
				String uuid = updateData[0];
				String message = updateData[1].replace("&#00", "'");
				
				boolean tempMuted = Boolean.valueOf(updateData[2]);
				
				// we will just have to refer to the muter as the console for now,
				// since we don't keep track of the muter
				String muter = Lang.CONSOLE_NAME.toString();
				
				if (!tempMuted) {
					
					// if the player is not temp muted, then just mute them
					
					// just start by adding the mute to the cache
					Banana.getMuteCache().addMutedPlayer(uuid);
					
					// then we will check to see if mute notifications are enabled
					if (Values.NOTIFY_MUTE) {
					
						this.asyncMuteMessagePlayer(uuid, muter, message);
						
					}
					
					// now we will check to see if broadcasts are enabled for mutes
					if (Values.ANNOUNCE_MUTE) {
						
						this.asyncMuteBroadcastMessage(uuid, muter, message);
						
					}
					
					// log the message that the server has been updated
					SimpleLogger.logMessage("Mute update received from server " + updateSender + ". Update has been applied to local cache.");
					
				} else {
					
					// if the player is temp muted, then temp mute them for the specified time
					Date unMuteDate = new Date(Long.parseLong(updateData[3])); 
					
					// getting the date in an easy to understand way!
					String[] timeString = updateData[4].split(":");
					
					int seconds = 0;
					int minutes = 0;
					int hours = 0;
					int days = 0;
					
					// PREPARE FOR SUCK!
					if (timeString.length == 1) {
						
						// this will run if seconds are wanted
						seconds = Integer.parseInt(timeString[0]);
					
						
					} else if (timeString.length == 2) {
						
						// this will run if minutes and seconds are wanted
						minutes = Integer.parseInt(timeString[0]);
						seconds = Integer.parseInt(timeString[1]);
						
					} else if (timeString.length == 3) {
					
						// this will run if seconds, minutes, and hours are wanted
						hours = Integer.parseInt(timeString[0]);
						minutes = Integer.parseInt(timeString[1]);
						seconds = Integer.parseInt(timeString[2]);
					
					} else {
						
						// this will run if seconds, minutes, hours, and days are wanted
						days = Integer.parseInt(timeString[0]);
						hours = Integer.parseInt(timeString[1]);
						minutes = Integer.parseInt(timeString[2]);
						seconds = Integer.parseInt(timeString[3]);
						
					}
					
					// first we'll just mute them in the cache
					Banana.getMuteCache().tempMute(uuid, unMuteDate);
					
					// now we'll check if announcements are enabled, then announce if they are
					if (Values.ANNOUNCE_TEMPMUTE) {
						
						this.asyncTempMuteBroadcastMessage(days, hours, minutes, seconds, uuid, muter, message);
						
					}
					
					// now we'll check if we need to notify the player
					if (Values.NOTIFY_MUTE) {
						
						this.asyncTempMuteMessagePlayer(days, hours, minutes, seconds, uuid, message);
						
					}
					
					
					// log the message that the server has been updated
					SimpleLogger.logMessage("Temp-mute update received from server " + updateSender + ". Update has been applied to local cache.");
					
				}
				
			} else if (type == UpdateType.BAN_IP) {
				
				// handle the update type being ban ip
				
				String address = updateData[0];
				
				Enum<BannerType> bannerType = BannerType.valueOf(updateData[1]);
				
				String bannerUUID = updateData[2];
				
				String banMessage = updateData[3].replace("&#00", "'");
				
				boolean tempBanned = Boolean.valueOf(updateData[4]);
			
				// check if a temp ban is being done
				
				if (!tempBanned) {
					
					// if this runs, we'll just permanently ban the player with the information above
					Banana.getBanCache().addBannedIP(address, banMessage, bannerType, bannerUUID);
					
					// now we'll check and see if the player is online and kick them with a specified message
					this.asyncKickPlayer(address, Lang.BAN_IP_FORMAT.parseBanFormat((new Main()).getLatestName(bannerUUID), banMessage), true);
					
					// now it's time for the broadcast funtime!
					if (Values.ANNOUNCE_BANIP) {
						
						this.asyncBanIPBroadcastMessage(bannerUUID, address, banMessage);
						
					}
					
					// no need to notify the player... because they should be kicked
					
					// log the message that the server has been updated
					SimpleLogger.logMessage("IP-ban update received from server " + updateSender + ". Update has been applied to local cache.");
				
				} else {
					
					Date tempBanDate = new Date(Long.parseLong(updateData[5]));
					
					String unparsedDate = updateData[6];
					
					// we'll run this if the player needs to be ip tempbanned
					Banana.getBanCache().tempBanIP(address, banMessage, tempBanDate, bannerType, bannerUUID, unparsedDate);
					
					int seconds = 0;
					int minutes = 0;
					int hours = 0;
					int days = 0;
					
					String[] timeString = unparsedDate.split(":");
					
					// PREPARE FOR MORE SUCK!
					if (timeString.length == 1) {
						
						// this will run if seconds are wanted
						seconds = Integer.parseInt(timeString[0]);
					
						
					} else if (timeString.length == 2) {
						
						// this will run if minutes and seconds are wanted
						minutes = Integer.parseInt(timeString[0]);
						seconds = Integer.parseInt(timeString[1]);
						
					} else if (timeString.length == 3) {
					
						// this will run if seconds, minutes, and hours are wanted
						hours = Integer.parseInt(timeString[0]);
						minutes = Integer.parseInt(timeString[1]);
						seconds = Integer.parseInt(timeString[2]);
					
					} else {
						
						// this will run if seconds, minutes, hours, and days are wanted
						days = Integer.parseInt(timeString[0]);
						hours = Integer.parseInt(timeString[1]);
						minutes = Integer.parseInt(timeString[2]);
						seconds = Integer.parseInt(timeString[3]);
						
					}
					
					String bannerName = new Main().getLatestName(bannerUUID);
					
					if (bannerName == null) bannerName = Lang.CONSOLE_NAME.toString();
					
					// now we'll check and see if the player is online and kick them with a specified message
					this.asyncKickPlayer(address, Lang.IP_TEMPBAN_FORMAT.parseTime(days, hours, minutes, seconds, bannerName, banMessage), true);
					
					// It's announcement time!
					if (Values.ANNOUNCE_TEMPBANIP) {
						
						this.asyncTempBanIPBroadcastMessage(days, hours, minutes, seconds, address, bannerUUID, banMessage);
						
					}
					
					// log the message that the server has been updated
					SimpleLogger.logMessage("Temp IP-ban update received from server " + updateSender + ". Update has been applied to local cache.");
					
				}
				
			} else if (type == UpdateType.UN_MUTE) {
				
				// handle the update type being un mute
				
				String uuid = updateData[0];
				
				Banana.getMuteCache().unMutePlayer(uuid);
				
				// check if we need to announce the player getting unmuted
				if (Values.ANNOUNCE_UNMUTE) {
					
					this.asyncUnMuteBroadcastMessage(uuid);
				
				}
				
				// now it's time to notify the player (if it's enabled)
				if (Values.NOTIFY_UNMUTE) {
					
					this.asyncUnMuteMessagePlayer(uuid);
					
				}
				
				// log the message that the server has been updated
				SimpleLogger.logMessage("Un-mute update received from server " + updateSender + ". Update has been applied to local cache.");
				
			} else if (type == UpdateType.UN_BAN) {
				
				// handle the update type being un ban
				
				String uuid = updateData[0];
				
				// just simply remove the ban from the cache
				Banana.getBanCache().removeBan(uuid);
				
				// check if announcements are enabled for this, then release the command
				if (Values.ANNOUNCE_UNBAN) {
					
					this.asyncUnBanBroadcastMessage(uuid);
					
				}
				
				// log the message that the server has been updated
				SimpleLogger.logMessage("Un-ban update received from server " + updateSender + ". Update has been applied to local cache.");
				
			} else if (type == UpdateType.UN_BAN_IP) {
				
				// handle the update type being un ban ip
				
				// gather the sent over information
				String address = updateData[0];
				
				// remove the ipban from the cache
				Banana.getBanCache().unbanIP(address);
				
				// check if announcements are enabled for this particular event
				if (Values.ANNOUNCE_UNBANIP) {
					
					this.asyncUnBanIPBroadcastMessage(address);
					
				}
				
				// log the message that the server has been updated
				SimpleLogger.logMessage("Un-ban IP update received from server " + updateSender + ". Update has been applied to local cache.");
				
			} else if (type == UpdateType.PURGE_WARNINGS) {
				
				// handle the update type being purge warnings
				
				// get the uuid from the database
				String uuid = updateData[0];
				
				// follow through to purging the warnings
				Banana.getWarnCache().purgeWarnings(uuid);
				
				// log the message that the server has been updated
				SimpleLogger.logMessage("Warn purge update received from server " + updateSender + ". Update has been applied to local cache.");
				
			} else if (type == UpdateType.ADD_STAFF) {
				
				// handle the update type being add staff
				
				// get the uuid from the database
				String uuid = updateData[0];
				
				Banana.getPlayerCache().addStaff(uuid);
				
				// log the message that the server has been updated
				SimpleLogger.logMessage("Add staff update received from server " + updateSender + ". Update has been applied to local cache.");
				
			} else if (type == UpdateType.RM_STAFF) {
				
				// handle the update type being rm staff
				
				// get the uuid fromthe database
				String uuid = updateData[0];
				
				Banana.getPlayerCache().removeStaff(uuid);
				
				// log the message that the server has been updated
				SimpleLogger.logMessage("Remove staff update received from server " + updateSender + ". Update has been applied to local cache.");
				
			} else if (type == UpdateType.KICK_ALL) {
				
				// handle the update type being kick all

				// the name of the kicker! (No UUID because it's being sent straight from the other server)
				String kickerName = updateData[0];
				
				// check if there was a player running this command, or it is was console
				if (kickerName.equals("{CONSOLE}")) {
					
					kickerName = Lang.CONSOLE_NAME.toString();
					
				}
				
				// get the message from the database
				String message = updateData[1];
				
				// check if there was a set message, if not then use this server's default
				if (message.equals("{NO_MESSAGE}")) {
					
					message = Lang.DEFAULT_KICK_MESSAGE.toString();
					
				}

				// kick all the players with the specified message
				this.asyncKickAllPlayers(Lang.KICK_FORMAT.parseBanFormat(kickerName, message));
				
				// log the message that the server has been updated
				SimpleLogger.logMessage("Kick all update received from server " + updateSender + ". Update has been applied to local cache.");
				
			} else if (type == UpdateType.PLAYER) {
				
				// Handle the update type being a player
				
				String playerUUID = updateData[0];
				String playerAddress = updateData[1];
				String playerName = updateData[2];
				
				// Apply the data to the cache
				Banana.getPlayerCache().addUUID(playerUUID, playerName);
				Banana.getPlayerCache().addAddress(playerUUID, playerAddress);
				
				// log the message that the server has been updated
				SimpleLogger.logMessage("Player update received from server " + updateSender + ". Update has been applied to local cache.");
				
			}
		
		} catch (Exception e) {
			
			// some sort of updater error happens here
			SimpleLogger.logMessage("Something didn't go as planned when parsing cache update information. Reason: " + e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
	/** Used to message players from an async thread.. Just to make things safe. It will
	 * automatically check if the player is offline
	 * @param uuid The uuid of the player to send the message to
	 * @param message The message to send
	 */
	private synchronized void asyncWarnMessagePlayer(final String uuid, final String warnerUUID, final String message) {
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("BANana"), new Runnable() {
			
			@Override
			public void run() {
				
				Player target = Bukkit.getPlayer(UUID.fromString(uuid));
            	
            	if (target != null) {
            		
            		String warnerName = (new Main()).getLatestName(warnerUUID);
                	
                	if (warnerName == null) {
                		warnerName = Lang.CONSOLE_NAME.toString();
                	}
            		
            		Action.notifyPlayer(Action.WARNING, target, Lang.WARN_NOTIFY.parseBanFormat(warnerName, message));
            	}
				
			}
			
		});
		
	}
	
	/** Used to notify a player that is being muted
	 * @param uuid
	 * @param muterName
	 * @param message
	 */
	private synchronized void asyncMuteMessagePlayer(final String uuid, final String muterName, final String message) {
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("BANana"), new Runnable() {
			
			@Override
			public void run() {
				
				Player target = Bukkit.getPlayer(UUID.fromString(uuid));
            	
            	if (target != null) {
            		
            		Action.notifyPlayer(Action.MUTE, target, Lang.MUTE_NOTIFY.parseBanFormat(muterName, message));
            		
            	}
            	
				
			}
			
		});
		
	}
	
	/** Used to message a player to tell them that they have been temp muted
	 * @param days The days of the temp mute
	 * @param hours The hours of the temp mute
	 * @param minutes The minutes of the temp mute
	 * @param seconds The seconds of the temp mute
	 * @param uuid The uuid to send the message to
	 * @param message The mute reason
	 */
	private synchronized void asyncTempMuteMessagePlayer(final int days, final int hours, final int minutes, final int seconds, final String uuid, final String message) {
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("BANana"), new Runnable() {
			
			@Override
			public void run() {
				
				Player target = Bukkit.getPlayer(UUID.fromString(uuid));
            	
            	if (target != null) {
            		
            		Action.notifyPlayer(Action.TEMPMUTE, target, Lang.TEMP_MUTE_NOTIFY.parseTimeBroadcast(days, hours, minutes, seconds, "", target.getName(), message));
            		
            	}
				
			}
			
		});
		
	}
	
	/** Sends out the unmuted broadcast to all players in a sync thread
	 * @param uuid The uuid of the player that has been unmuted
	 */
	private synchronized void asyncUnMuteMessagePlayer(final String uuid) {
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("BANana"), new Runnable() {
			
			@Override
			public void run() {
				
				Player target = Bukkit.getPlayer(UUID.fromString(uuid));
            	
            	if (target != null) {
            		
            		Action.notifyPlayer(Action.UNMUTE, target, Lang.UNMUTE_NOTIFY.toString());
            		
            	}
				
			}
			
		});
		
	}
	
	/** Used to broadcast a warn message accross the server in an async way
	 * automatically check if the player is offline
	 * @param message The message to broadcast
	 */
	private synchronized void asyncWarnBroadcastMessage(final String warnerUUID, final String warnedUUID, final String warnMessage) {
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("BANana"), new Runnable() {
			
			@Override
			public void run() {
				
				String warnerName = (new Main()).getLatestName(warnerUUID);
            	
            	if (warnerName == null) {
            		warnerName = Lang.CONSOLE_NAME.toString();
            	}
            	
            	Action.broadcastMessage(Action.WARNING, Lang.WARN_BROADCAST.parseWarningBroadcast(warnerName, (new Main()).getLatestName(warnedUUID), warnMessage));
				
			}
			
		});
		
	}
	
	
	/** Used to broadcast that player has been banned from an async thread
	 * @param bannerUUID The uuid of the ban executor
	 * @param bannedUUID The uuid being banned
	 * @param banMessage The message tied to the ban
	 */
	private synchronized void asyncBanBroadcastMessage(final String bannerUUID, final String bannedUUID, final String banMessage) {
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("BANana"), new Runnable() {
			
			@Override
			public void run() {
				
				String bannerName = Banana.getPlayerCache().getLatestName(bannerUUID);
            	
            	if (bannerName == null) {
            		bannerName = Lang.CONSOLE_NAME.toString();
            	}
            	
            	Action.broadcastMessage(Action.BAN, Lang.BAN_BROADCAST.parseWarningBroadcast(bannerName, Banana.getPlayerCache().getLatestName(bannedUUID), banMessage));
				
			}
			
		});
		
	}
	
	
	/** Used to broadcast that a player has been tempbanned in an async wawy
	 * @param bannerUUID The banner of the UUID
	 * @param bannedUUID The uuid being banned
	 * @param banMessage The message tied to the ban
	 * @param days How many days the tempban will last
	 * @param hours How many hours the tempban will last
	 * @param minutes How many minutes will the tempban last
	 * @param seconds How many seconds will the tempban last
	 */
	private synchronized void asyncTempBanBroadcastMessage(final String bannerUUID, final String bannedUUID, final String banMessage, final int days, final int hours, final int minutes, final int seconds) {
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("BANana"), new Runnable() {
			
			@Override
			public void run() {
				
				String bannerName = (new Main()).getLatestName(bannerUUID);
            	
            	if (bannerName == null) {
            		bannerName = Lang.CONSOLE_NAME.toString();
            	}
            	
            	Action.broadcastMessage(Action.TEMPBAN, Lang.TEMPBAN_BROADCAST.parseTimeBroadcast(days, hours, minutes, seconds, (new Main()).getLatestName(bannedUUID), bannerName, banMessage));
				
			}
			
		});
		
	}
	
	/** Used to broadcast that player has been muted from an async thread
	 * @param uuid The uuid of the player getting muted
	 * @param muterName The name of the entity that muted the player
	 * @param muteMessage The message that is going along with the mute
	 */
	private synchronized void asyncMuteBroadcastMessage(final String uuid, final String muterName, final String muteMessage) {
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("BANana"), new Runnable() {
			
			@Override
			public void run() {
				
				Action.broadcastMessage(Action.MUTE, Lang.MUTE_BROADCAST.parseBroadcast(muterName, (new Main()).getLatestName(uuid)));
				
			}
			
		});
		
	}
	
	/** Used to broadcast that a player has been temp muted from an async thread
	 * @param days The amount of days the mute will last
	 * @param hours The amount of hours the mute will last
	 * @param minutes The amount of minutes the mute will last
	 * @param seconds The amount of seconds the mute will last
	 * @param uuid The uuid that has been temp muted
	 * @param muterName The name of the entity that executed the temp mute
	 * @param message The message to go along with the temp mute
	 */
	private synchronized void asyncTempMuteBroadcastMessage(final int days, final int hours, final int minutes, final int seconds, final String uuid, final String muterName, final String message) {
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("BANana"), new Runnable() {
			
			@Override
			public void run() {
				
				Action.broadcastMessage(Action.TEMPMUTE, Lang.TEMP_MUTE_BROADCAST.parseTimeBroadcast(days, hours, minutes, seconds, (new Main()).getLatestName(uuid), muterName, message));
				
			}
			
		});
		
	}
	
	/** Used to broadcast a messaeg when a player is IP banned
	 * @param bannerUUID The uuid of the ban executor
	 * @param address The address that has been banned
	 * @param message The message that goes along with the ban
	 */
	private synchronized void asyncBanIPBroadcastMessage(final String bannerUUID, final String address, final String message) {
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("BANana"), new Runnable() {
			
			@Override
			public void run() {
				
				String bannerName = new Main().getLatestName(bannerUUID);
            	
            	if (bannerName == null) {
            		bannerName = Lang.CONSOLE_NAME.toString();
            	}
            	
            	Action.broadcastMessage(Action.BANIP, Lang.IP_BAN_BROADCAST.parseWarningBroadcast(bannerName, address, message));
				
			}
			
		});
		
	}
	
	/** Used to announce a temp banned ip from an async thread
	 * @param days The amount of days the ip ban will last
	 * @param hours The amount of hours the ip ban will last
	 * @param minutes The amount of minutes the ip ban will last
	 * @param seconds The amount of seconds the ip ban will last
	 * @param uuid The uuid that was banned
	 * @param bannerUUID The executor of the ban
	 * @param message The message to go along with the ban
	 */
	private synchronized void asyncTempBanIPBroadcastMessage(final int days, final int hours, final int minutes, final int seconds, final String address, final String bannerUUID, final String message) {
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("BANana"), new Runnable() {
			
			@Override
			public void run() {
				
				String bannerName = new Main().getLatestName(bannerUUID);
            	
            	if (bannerName == null) {
            		bannerName = Lang.CONSOLE_NAME.toString();
            	}
            	
            	Action.broadcastMessage(Action.TEMPBANIP, Lang.IP_TEMPBAN_BROADCAST.parseTimeBroadcast(days, hours, minutes, seconds, address, bannerName, message));
				
			}
			
		});
		
	}
	
	/** Used to broadcast when a player has been unmuted. It will broadcast the server name that the update came from
	 * in place of the player who ran the command
	 * @param uuid The UUID being unmuted
	 */
	private synchronized void asyncUnMuteBroadcastMessage(final String uuid) {
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("BANana"), new Runnable() {
			
			@Override
			public void run() {
				
				String unMuter = "Server: " + Lang.CONSOLE_NAME.toString();
            	
            	Action.broadcastMessage(Action.UNMUTE, Lang.UNMUTE_BROADCAST.parseBroadcast(unMuter, (new Main()).getLatestName(uuid)));
				
			}
			
		});
		
	}
	
	/** Used to broadcast a message when a player is unbanned
	 * @param uuid The uuid being unbanned
	 */
	private synchronized void asyncUnBanBroadcastMessage(final String uuid) {
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("BANana"), new Runnable() {
			
			@Override
			public void run() {
				
				String unBanner = "Server: " + Lang.CONSOLE_NAME.toString();
            	
            	Action.broadcastMessage(Action.UNBAN, Lang.UNBAN_BROADCAST.parseBroadcast(unBanner, (new Main()).getLatestName(uuid)));
				
			}
			
		});
		
	}
	
	/** Used to broadcast a message when a player is un ip-banned
	 * @param address The address that was un ip-banned
	 */
	private synchronized void asyncUnBanIPBroadcastMessage(final String address) {
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("BANana"), new Runnable() {
			
			@Override
			public void run() {
				
				String unBanner = "Server: " + Lang.CONSOLE_NAME.toString();
            	
            	Action.broadcastMessage(Action.BANIP, Lang.UNBAN_IP_BROADCAST.parseBroadcast(unBanner, address));
				
			}
			
		});
		
	}
	
	/** Used to safely kick a player from an async thread. It checks if players are online, then kicks them if they are
	 * with the specified message.
	 * @param uuid The uuid of the player to kick
	 * @param kickMessage The message that goes along with the kick
	 * @param isIP Used to tell if the IP should be sought out, or if just to kick the online player
	 */
	private synchronized void asyncKickPlayer(final String uuid, final String kickMessage, final boolean isIP) {
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("BANana"), new Runnable() {
			
			@Override
			public void run() {
				if (!isIP) {
            		
            		Player target = Bukkit.getPlayer(UUID.fromString(uuid));
            		
            		if (target != null) {
            			
            			target.kickPlayer(kickMessage);
            			
            		}
            		
            	} else {
            		
            		// now uuid is an IP address
            		String address = uuid;
            		
            		for (Player p : Bukkit.getOnlinePlayers()) {
            			
            			if (p.getAddress().getHostName().equalsIgnoreCase(address)) {
            				
            				// if this goes, we kick the player specified with the given message
            				p.kickPlayer(kickMessage);

            				// this method is nice because it will kick all players with this IP.. SO TAKE THAT!!!!
            				
            			}
            			
            		}
            		
            	}
			}
			
		});
		
		
	}
	
	/** Kicks all players on the server with the specified message. You will have to format the message
	 * because it just spits it right out to the player
	 * @param kickMessage The message that accompanies the player kick
	 */
	private synchronized void asyncKickAllPlayers(final String kickMessage) {

		Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("BANana"), new Runnable() {
			
			@Override
			public void run() {
				
				for (Player p : Bukkit.getOnlinePlayers()) {

					p.kickPlayer(kickMessage);

				}
				
			}
			
		});

	}
	
	/** Since almost anything can happen with warns.. we need to make sure this is ran in a sync thread
	 * @param uuid The uuid that is being warned
	 */
	private synchronized void asyncTakeWarningAction(final String uuid) {
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("BANana"), new Runnable() {
			
			@Override
			public void run() {
				
				Banana.getWarnCache().applyWarningAction(uuid);
				
			}
			
		});
		
		
	}
	
}
