package us.thetaco.banana.listeners;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import info.dyndns.thetaco.uuid.api.Main;
import us.thetaco.banana.Banana;
import us.thetaco.banana.sql.DatabaseManager.BannerType;
import us.thetaco.banana.utils.Lang;
import us.thetaco.banana.utils.Values;

public class PlayerLoginListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerLogin(PlayerLoginEvent e) {
		
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		String address = e.getAddress().getHostAddress();
		
		// no matter what, update their info
		// This will also update the player cache
		Banana.getDatabaseManager().asyncUpdatePlayer(uuid.toString(), address, player.getName());
		
		// check to see if the server is in staff mode
		if (Banana.isStaffMode()) {
			
			// check to see if the member is in staff, if they aren't, kick them
			if (!Banana.getPlayerCache().isStaff(uuid.toString())) {
				
				if (Values.BUNGEE_CORD_FORMAT) {
					e.disallow(Result.KICK_OTHER, "\n" + Lang.STAFF_MODE_KICK_MESSAGE.toString());
				} else {
					e.disallow(Result.KICK_OTHER, Lang.STAFF_MODE_KICK_MESSAGE.toString());
				}
			}
			
		}
		
		// checking if the player is IP banned
		
		if (Banana.getBanCache().isIPBanned(address)) {
			
			if (Banana.getBanCache().isIPTempBanned(address)) {
				
				// check if player should be banned
				if (player.hasPermission("banana.immune.tempbanip")) {
					
					// Unban the player since they shouldn't be banned
					Banana.getBanCache().unbanIP(address);
					Banana.getDatabaseManager().asyncRemoveIPBan(address);
					
					// just stop here..
					
					return;
				}
				
				Calendar currentDate = Calendar.getInstance();
				
				currentDate.setTimeInMillis(System.currentTimeMillis());
				
				Date systemDate = currentDate.getTime();
				Date unbanDate = Banana.getBanCache().getIPTempBanEndDate(address);
				
				
				// this will run if the IP is temp banned
				if (systemDate.equals(unbanDate) || systemDate.after(unbanDate)) {
					
					// allow the player to join and unban them
					// actually.. just don't disallow them to join
					
					// also be sure to remove the ban!
					Banana.getBanCache().unbanIP(address);
					Banana.getDatabaseManager().asyncRemoveIPBan(address);
					
					return;
					
				} else {
					
					if (player.hasPermission("banana.immune.banip")) {
						
						// Unban the player since they shouldn't be banned
						Banana.getBanCache().unbanIP(address);
						Banana.getDatabaseManager().asyncRemoveIPBan(address);
						
						// just stop here..
						
						return;
					}
					
					// prevent them from joining.. tell them how much time they have left until their ban is lifted
					
					String message = Banana.getBanCache().getIPBanMessage(address);
					
					String bannerName = "";
					Enum<BannerType> type = Banana.getBanCache().getBannerTypeIP(address);
					
					if (type == BannerType.PLAYER) {
						
						bannerName = Banana.getPlayerCache().getLatestName(Banana.getBanCache().getBannerIP(address));
						
						if (bannerName == null) {
							bannerName = (new Main()).getLatestName(Banana.getBanCache().getBannerIP(address));
						}
						
					} else {
						
						bannerName = type.toString();
						
					}
					
					// getting the stored date
					String pureDate = Banana.getBanCache().getFriendlyDate(uuid.toString());
					
					if (pureDate == null) {
					
						String kickMessage = new String();
						
						if (Banana.getBanCache().getBannerTypeIP(address) == BannerType.AUTOBANNED) {
							kickMessage = message;
						} else {
							kickMessage = Lang.BAN_IP_FORMAT.parseBanFormat(bannerName, message);
						}
						
						if (Values.BUNGEE_CORD_FORMAT) {
							e.disallow(Result.KICK_BANNED, "\n" + kickMessage);
						} else {
							e.disallow(Result.KICK_BANNED, kickMessage);
						}
					} else {
						
						String[] timeSplit = pureDate.split(":");
						
						int days = 0;
						int hours = 0;
						int minutes = 0;
						int seconds = 0;
						
						if (timeSplit.length == 1) {
							
							seconds = Integer.parseInt(timeSplit[0]);
							
						} else if (timeSplit.length == 2) {
							
							minutes = Integer.parseInt(timeSplit[0]);
							seconds = Integer.parseInt(timeSplit[1]);
							
						} else if (timeSplit.length == 3) {
							
							hours = Integer.parseInt(timeSplit[0]);
							minutes = Integer.parseInt(timeSplit[1]);
							seconds = Integer.parseInt(timeSplit[2]);
							
						} else if (timeSplit.length == 4) {
							
							days = Integer.parseInt(timeSplit[0]);
							hours = Integer.parseInt(timeSplit[1]);
							minutes = Integer.parseInt(timeSplit[2]);
							seconds = Integer.parseInt(timeSplit[3]);
							
						}
						
						String kickMessage = new String();
						
						if (Banana.getBanCache().getBannerTypeIP(address) == BannerType.AUTOBANNED) {
							kickMessage = message;
						} else {
							kickMessage = Lang.IP_TEMPBAN_FORMAT.parseTimeTempBan(days, hours, minutes, seconds, player.getName(), bannerName, message);
						}
						
						if (Values.BUNGEE_CORD_FORMAT) {
							e.disallow(Result.KICK_BANNED, "\n" + kickMessage);
						} else {
							e.disallow(Result.KICK_BANNED, kickMessage);
						}
						
						// if ban-joining-player is enabled, we will ban the ip address of the player (temporarily)
						// if it is not already banned
						if (Values.BAN_JOINING_PLAYER && !Banana.getBanCache().isUUIDBanned(uuid)) {
														
							Banana.getBanCache().addTempBannedUUID(uuid.toString(), Lang.AUTO_TEMP_BAN_PLAYER_MESSAGE.parseObject(address), unbanDate, BannerType.AUTOBANNED, null, pureDate);
							Banana.getDatabaseManager().asyncAddBan(uuid.toString(), BannerType.AUTOBANNED, null, Lang.AUTO_TEMP_BAN_PLAYER_MESSAGE.parseObject(address), true, unbanDate.getTime(), pureDate);
							
						}
						
					}
					
					return;
				}
				
			} else {
				
				String message = Banana.getBanCache().getIPBanMessage(address);
				
				String bannerName = "";
				Enum<BannerType> type = Banana.getBanCache().getBannerTypeIP(address);
								
				if (type == BannerType.PLAYER) {
					
					bannerName = Banana.getPlayerCache().getLatestName(Banana.getBanCache().getBannerIP(address));
					
					if (bannerName == null) {
						bannerName = (new Main()).getLatestName(Banana.getBanCache().getBannerIP(address));
					}
					
				} else {
					
					bannerName = type.toString();
					
				}
				
				String kickMessage = new String();
				
				if (Banana.getBanCache().getBannerTypeIP(address) == BannerType.AUTOBANNED) {
					// parse in the executor here
					kickMessage = message;
				} else {
					kickMessage = Lang.BAN_IP_FORMAT.parseBanFormat(bannerName, message);
				}
				
				if (Values.BUNGEE_CORD_FORMAT) {
					e.disallow(Result.KICK_BANNED, "\n" + kickMessage);
				} else {
					// this will run if the IP isn't temp banned
					e.disallow(Result.KICK_BANNED, kickMessage);
				}
				
				// if ban-joining-player is enabled, we will ban the ip address of the player (permanently)
				// if it is not already banned
				if (Values.BAN_JOINING_PLAYER && !Banana.getBanCache().isUUIDBanned(uuid.toString())) {
					
					Banana.getBanCache().addBannedUUID(uuid.toString(), Lang.AUTO_BAN_PLAYER_MESSAGE.parseObject(address), BannerType.AUTOBANNED, null);
					Banana.getDatabaseManager().asyncAddBan(uuid.toString(), BannerType.AUTOBANNED, null, Lang.AUTO_BAN_PLAYER_MESSAGE.parseObject(address), false, null, null);
										
				}
				
			}
			
			return;
		}
		
		// check if the player is banned in the cache
		if (Banana.getBanCache().isUUIDBanned(player.getUniqueId())) {
			
			// if the player is banned, check if they are tempbanned
			if (Banana.getBanCache().isTempBanned(uuid)) {
				
				if (player.hasPermission("banana.immune.tempban")) {
					
					// allow the player to join them and unban them since they are immune
					Banana.getBanCache().removeBan(player.getUniqueId());
					Banana.getDatabaseManager().asyncRemoveBan(player.getUniqueId().toString());
					
					return;
				}
				
				Date currentDate = new Date();
				
				Date unbanDate = Banana.getBanCache().getTempBanEndDate(uuid);
				
				if (currentDate.equals(unbanDate) || currentDate.after(unbanDate)) {
					
					// allow the player to join and unban them
					// actually.. just don't disallow them to join
					
					// also be sure to remove the ban!
					Banana.getBanCache().removeBan(player.getUniqueId());
					Banana.getDatabaseManager().asyncRemoveBan(player.getUniqueId().toString());
					
					return;
					
				} else {
					
					// prevent them from joining.. tell them how much time they have left until their ban is lifted
					
					String message = Banana.getBanCache().getBanMessage(uuid.toString());
					
					String bannerName = "";
					Enum<BannerType> type = Banana.getBanCache().getBannerType(uuid.toString());
					
					if (type == BannerType.PLAYER) {
						
						bannerName = Banana.getPlayerCache().getLatestName(Banana.getBanCache().getBanner(uuid.toString()));
						
						if (bannerName == null) {
							bannerName = (new Main()).getLatestName(Banana.getBanCache().getBanner(uuid.toString()));
						}
						
					} else {
						
						bannerName = type.toString();
						
					}
						
					// getting the stored date
					String pureDate = Banana.getBanCache().getFriendlyDate(uuid.toString());
					
					if (pureDate == null) {
					
						String kickMessage = new String();
						
						if (Banana.getBanCache().getBannerType(uuid.toString()) == BannerType.AUTOBANNED) {
							kickMessage = message;
						} else {
							kickMessage = Lang.BAN_FORMAT.parseBanFormat(bannerName, message);
						}
						
						if (Values.BUNGEE_CORD_FORMAT) {
							e.disallow(Result.KICK_BANNED, "\n" + kickMessage);
						} else {
							e.disallow(Result.KICK_BANNED, kickMessage);
						}
					} else {
						
						String[] timeSplit = pureDate.split(":");
						
						int days = 0;
						int hours = 0;
						int minutes = 0;
						int seconds = 0;
						
						if (timeSplit.length == 1) {
							
							seconds = Integer.parseInt(timeSplit[0]);
							
						} else if (timeSplit.length == 2) {
							
							minutes = Integer.parseInt(timeSplit[0]);
							seconds = Integer.parseInt(timeSplit[1]);
							
						} else if (timeSplit.length == 3) {
							
							hours = Integer.parseInt(timeSplit[0]);
							minutes = Integer.parseInt(timeSplit[1]);
							seconds = Integer.parseInt(timeSplit[2]);
							
						} else if (timeSplit.length == 4) {
							
							days = Integer.parseInt(timeSplit[0]);
							hours = Integer.parseInt(timeSplit[1]);
							minutes = Integer.parseInt(timeSplit[2]);
							seconds = Integer.parseInt(timeSplit[3]);
							
						}
						
						String kickMessage = new String();
						
						if (Banana.getBanCache().getBannerType(uuid.toString()) == BannerType.AUTOBANNED) {
							kickMessage = message;
						} else {
							kickMessage = Lang.TEMPBAN_FORMAT.parseTimeTempBan(days, hours, minutes, seconds, player.getName(), bannerName, message);
						}
						
						if (Values.BUNGEE_CORD_FORMAT) {
							e.disallow(Result.KICK_BANNED, "\n" + kickMessage);
						} else {
							e.disallow(Result.KICK_BANNED, kickMessage);
						}
						
						// if ban-joining-ip is enabled, we will ban the ip address of the player (temporarily)
						// if it is not already banned
						if (Values.BAN_JOINING_IP && !Banana.getBanCache().isIPBanned(address)) {
							
							Banana.getBanCache().tempBanIP(address, Lang.AUTO_BAN_TEMP_IP_MESSAGE.parseObject(player.getName()), unbanDate, BannerType.AUTOBANNED, null, pureDate);
							Banana.getDatabaseManager().asyncBanIP(address, Lang.AUTO_BAN_TEMP_IP_MESSAGE.parseObject(player.getName()), BannerType.AUTOBANNED, null, true, unbanDate.getTime(), pureDate);
							
						}
						
					}
					
				}
				
				return;
			}
			
			if (player.hasPermission("banana.immune.ban")) {
				
				// allow the player to join them and unban them since they are immune
				Banana.getBanCache().removeBan(player.getUniqueId());
				Banana.getDatabaseManager().asyncRemoveBan(player.getUniqueId().toString());
				
				return;
			}
			
			// if we get to this point, we know they are just banned... so prevent them from joining
			
			String message = Banana.getBanCache().getBanMessage(uuid.toString());
			
			String bannerName = "";
			Enum<BannerType> type = Banana.getBanCache().getBannerType(uuid.toString());
			
			if (type == BannerType.PLAYER) {
				
				bannerName = Banana.getPlayerCache().getLatestName(Banana.getBanCache().getBanner(uuid.toString()));
				
				if (bannerName == null) {
					bannerName = (new Main()).getLatestName(Banana.getBanCache().getBanner(uuid.toString()));
				}
			} else {
				
				bannerName = type.toString();
				
			}
			
			if (message != null) {
				
				String kickMessage = new String();
				
				if (Banana.getBanCache().getBannerType(uuid.toString()) == BannerType.AUTOBANNED) {
					kickMessage = message;
				} else {
					kickMessage = Lang.BAN_FORMAT.parseBanFormat(bannerName, message);
				}
				
				// kick them with the set message
				if (Values.BUNGEE_CORD_FORMAT) {
					e.disallow(Result.KICK_BANNED, "\n" + kickMessage);
				} else {
					e.disallow(Result.KICK_BANNED, kickMessage);
				}
			} else {
			
				String kickMessage = new String();
				
				if (Banana.getBanCache().getBannerType(uuid.toString()) == BannerType.AUTOBANNED) {
					kickMessage = message;
				} else {
					kickMessage = Lang.BAN_FORMAT.parseBanFormat(bannerName, Lang.DEFAULT_BAN_MESSAGE.toString());
				}
				
				if (Values.BUNGEE_CORD_FORMAT) {
					e.disallow(Result.KICK_BANNED, "\n" + kickMessage);
				} else {
					// kick them with the set message
					e.disallow(Result.KICK_BANNED, kickMessage);
				}
				
			}
			
			// if ban-joining-ip is enabled, we will ban the ip address of the player (permanently)
			// if it is not already banned
			if (Values.BAN_JOINING_IP && !Banana.getBanCache().isIPBanned(address)) {
				
				Banana.getBanCache().addBannedIP(address, Lang.AUTO_BAN_IP_MESSAGE.parseObject(player.getName()), BannerType.AUTOBANNED, null);
				Banana.getDatabaseManager().asyncBanIP(address, Lang.AUTO_BAN_IP_MESSAGE.parseObject(player.getName()), BannerType.AUTOBANNED, null, false, null, null);
									
			}
			
		}
		
	}
	
}
