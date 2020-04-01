package us.thetaco.banana.commands;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import info.dyndns.thetaco.uuid.api.Main;
import us.thetaco.banana.Banana;
import us.thetaco.banana.sql.DatabaseManager.BannerType;
import us.thetaco.banana.utils.Action;
import us.thetaco.banana.utils.CommandType;
import us.thetaco.banana.utils.Lang;
import us.thetaco.banana.utils.Values;

public class TempBanIPConsole {

	public boolean runTempBanIPCommand(CommandSender sender, String[] args) {
		
		if (args.length < 2) {
			sender.sendMessage(Lang.TEMPBAN_IP_INCORRECT_ARGS.toString());
			return true;
		}
		
		Player target = Bukkit.getPlayer(args[0]);
		
		String uuid = null;
		
		if (target == null) {
			
			Main main = new Main();
			
			uuid = main.getPlayer(args[0]).getUUID();
			
		} else {
			
			if (target.hasPermission("banana.immune.tempbanip")) {
				sender.sendMessage(Lang.CANNOT_BE_TEMP_IP_BANNED.toString());
				return true;
			}
			
			uuid = target.getUniqueId().toString();
			
		}
		
		String address = null;
		boolean ipGiven = false;
		
		if (uuid == null) {
			sender.sendMessage(Lang.USING_IP.toString());
			
			address = args[0];
			
			ipGiven = true;
			
		} else {
			
			String tempAddress = Banana.getPlayerCache().getAddress(uuid);
			
			if (tempAddress == null) {
				sender.sendMessage(Lang.NO_IP_STORED.toString());
				return true;
			}
			
			address = tempAddress;
			
		}
		
		// check to see if the IP is already banned
		if (Banana.getBanCache().isIPBanned(address)) {

			if (Banana.getBanCache().isTempBanned(uuid)) {
				
				sender.sendMessage(Lang.ALREADY_TEMP_BANNED.parseName((new Main()).getLatestName(uuid)));
				
			} else {
				
				sender.sendMessage(Lang.ALREADY_BANNED.parseName((new Main()).getLatestName(uuid)));
				
			}
			

			return true;
		}
		
		String[] timeString = args[1].split(":");
		
		int seconds = 0;
		int minutes = 0;
		int hours = 0;
		int days = 0;
		
		try {
		
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
		
		} catch (Exception e) {
			sender.sendMessage(Lang.IMPROPER_TIME_GIVEN.toString());
			return true;
		}
		
		Calendar calendar = Calendar.getInstance();
				
		calendar.setTimeInMillis(System.currentTimeMillis());
		
		calendar.add(Calendar.DAY_OF_MONTH, days);
		calendar.add(Calendar.HOUR_OF_DAY, hours);
		calendar.add(Calendar.MINUTE, minutes);
		calendar.add(Calendar.SECOND, seconds);
		
		String message = Lang.IP_TEMPBAN_DEFAULT_MESSAGE.toString();
		
		if (args.length > 2) {
			
				
			message = args[2];
				
			int i = 0;
			for (String s : args) {
				
				if (i > 2) {
					
					message += " " + s;
					
				}
				i++;
			}
			
		}
		
		String banMessage = Lang.IP_TEMPBAN_FORMAT.parseTime(days, hours, minutes, seconds, Lang.CONSOLE_NAME.toString(), message);
		
		/*
		 * JUST A NOTE TO MYSELF: I changed the two lines below from banMessage to message. I did this because it was printing the message
		 * twice in an awkward way. If you keep having trouble in that area, look around here.. Good luck!
		 */
		
		// add the bans
		Banana.getBanCache().tempBanIP(address, message, calendar.getTime(), BannerType.CONSOLE, null, args[1]);
		Banana.getDatabaseManager().asyncBanIP(address, message, BannerType.CONSOLE, null, true, calendar.getTimeInMillis(), args[1]);
		
		sender.sendMessage(Lang.TEMP_BAN_IP_SUCCESS.toString());

		Banana.getDatabaseManager().logCommand(CommandType.TEMP_BAN_IP, null, args, true);

		// check if announcements are enabled for this command.. then release the annoucnement
		if (Values.ANNOUNCE_TEMPBANIP) {
							
			if (!ipGiven) {
				Action.broadcastMessage(Action.TEMPBANIP, Lang.IP_TEMPBAN_BROADCAST.parseTimeBroadcast(days, hours, minutes, seconds, (new Main()).getLatestName(uuid), Lang.CONSOLE_NAME.toString(), message));
			} else {
				Action.broadcastMessage(Action.TEMPBANIP, Lang.IP_TEMPBAN_BROADCAST.parseTimeBroadcast(days, hours, minutes, seconds, address, Lang.CONSOLE_NAME.toString(), message));
			}
		}
		
		// checking to make sure that username's attatched to ips is also
		// enabled
		if (Values.BAN_USERNAME_OF_IP) {

			// get all usernames attatched to the IP
			List<String> playersWithIP = Banana.getPlayerCache().getPlayersWithIP(address);

			if (playersWithIP == null)
				return true;

			// loop through the list and ban all players who are attatched to
			// that certain IP
			for (String s : playersWithIP) {

				// making sure the player isn't already banned
				if (!Banana.getBanCache().isUUIDBanned(s)) {

					// going ahead and banning them with the appropriate message
					Banana.getBanCache().tempBanIP(address, Lang.PLAYER_IP_AUTO_TEMPBANNED.parseTime(days, hours, minutes, seconds, Lang.CONSOLE_NAME.toString(), message), calendar.getTime(), BannerType.AUTOBANNED, null, args[1]);
					Banana.getDatabaseManager().asyncAddBan(uuid, BannerType.AUTOBANNED, null, Lang.PLAYER_IP_AUTO_TEMPBANNED.parseTime(days, hours, minutes, seconds, Lang.CONSOLE_NAME.toString(), message), true, calendar.getTimeInMillis(), args[1]);
					
					// check if the player is online and kick them if they are
					Player duplicate = Bukkit.getPlayer(UUID.fromString(s));

					if (duplicate != null) {
						duplicate.kickPlayer(Lang.PLAYER_IP_AUTO_TEMPBANNED.parseTime(days, hours, minutes, seconds, Lang.CONSOLE_NAME.toString(), banMessage));
					}

					
					// check if announcements are want for this type of ban
					if (Values.ANNOUNCE_BAN_USERNAME_OF_IP) {
						
						// check if announcements are enabled for this command.. then release the annoucnement
						if (Values.ANNOUNCE_BANIP) {
									
							Action.broadcastMessage(Action.TEMPBANIP, Lang.AUTO_TEMPBAN_BROADCAST.parseName((new Main()).getLatestName(uuid)));
							
						}
						
					}
					
				}

			}

		}
		
		return true;
		
	}
	
}
