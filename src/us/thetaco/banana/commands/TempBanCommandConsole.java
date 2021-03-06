package us.thetaco.banana.commands;

import java.util.Calendar;
import java.util.Date;

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

public class TempBanCommandConsole {

	public boolean runTempBanCommand(CommandSender sender, String[] args) {
		
		if (args.length < 2) {
			sender.sendMessage(Lang.TEMPBAN_INCORRECT_ARGS.toString());
			return true;
		}
		
		Player target = Bukkit.getPlayer(args[0]);
		String uuid;
		
		if (target == null) {
			
			Main main = new Main();
			
			uuid = main.getPlayer(args[0]).getUUID();
			
		} else {
			
			if (target.hasPermission("banana.immune.tempban")) {
				sender.sendMessage(Lang.CANNOT_BE_TEMPBANNED.parseName(target.getName()));
				return true;
			}
			
			uuid = target.getUniqueId().toString();
			
		}
		
		if (uuid == null) {
			sender.sendMessage(Lang.PLAYER_NEVER_ONLINE.toString());
			return true;
		}
		
		// check if the player is already banned
		if (Banana.getBanCache().isUUIDBanned(uuid)) {

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
		
		Date unbanDate = calendar.getTime();
		
		// check for message
		String message = Lang.DEFAULT_TEMPBAN_MESSAGE.toString();
		
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
		
		String banMessage = Lang.TEMPBAN_FORMAT.parseTime(days, hours, minutes, seconds, Lang.CONSOLE_NAME.toString(), message);
		
		// Finalize the ban and kick the player
				
		Banana.getBanCache().addTempBannedUUID(uuid, message, unbanDate, BannerType.CONSOLE, null, args[1]);
		Banana.getDatabaseManager().asyncAddBan(uuid, BannerType.CONSOLE, null, message, true, unbanDate.getTime(), args[1]);
		
		if (target != null) {
		
			// kick the player!
			target.kickPlayer(banMessage);
		
		}
		
		Banana.getDatabaseManager().logCommand(CommandType.TEMP_BAN, null, args, true);
		
		// check if announcements are enabled for this command.. then release the annoucnement
		if (Values.ANNOUNCE_TEMPBAN) {
							
			Action.broadcastMessage(Action.TEMPBAN, Lang.TEMPBAN_BROADCAST.parseTimeBroadcast(days, hours, minutes, seconds, (new Main()).getLatestName(uuid), Lang.CONSOLE_NAME.toString(), message));
					
		}
		
		return true;
		
	}
	
}
