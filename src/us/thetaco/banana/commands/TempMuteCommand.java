package us.thetaco.banana.commands;

import java.util.Calendar;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import info.dyndns.thetaco.uuid.api.Main;
import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.Action;
import us.thetaco.banana.utils.CommandType;
import us.thetaco.banana.utils.Lang;
import us.thetaco.banana.utils.Values;

public class TempMuteCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			
			// Run this if the sender is not a player
			return new TempMuteCommandConsole().runTempMuteCommand(sender, args);
			
		}
		
		// run this if the sender is a player
		Player player = (Player) sender;
		
		if (!player.hasPermission("banana.commands.tempmute")) {
			player.sendMessage(Lang.NO_PERMISSIONS.toString());
			return true;
		}
		
		if (args.length < 2) {
			player.sendMessage(Lang.TEMPMUTE_WRONG_ARGS.toString());
			return true;
		}
		
		Player target = Bukkit.getPlayer(args[0]);
		
		String uuid = null;
		
		if (target == null) {
			
			Main main = new Main();
			
			uuid = main.getPlayer(args[0]).getUUID();
			
		} else {
			
			if (target.hasPermission("banana.immune.tempmuted")) {
				player.sendMessage(Lang.CANNOT_BE_TEMP_MUTED.toString());
				return true;
			}
			
			uuid = target.getUniqueId().toString();
			
		}
		
		if (uuid == null) {
			player.sendMessage(Lang.PLAYER_NEVER_ONLINE.parseObject(args[0]));
			return true;
		}
		
		// check if the player is already muted!
		if (Banana.getMuteCache().isMuted(uuid)) {

			if (Banana.getMuteCache().isTempMuted(uuid)) {
				player.sendMessage(Lang.ALREADY_TEMP_BANNED.toString());
			} else {
				player.sendMessage(Lang.ALREADY_MUTED.toString());
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
			player.sendMessage(Lang.IMPROPER_TIME_GIVEN.toString());
			return true;
		}
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTimeInMillis(System.currentTimeMillis());
		
		calendar.add(Calendar.DAY_OF_MONTH, days);
		calendar.add(Calendar.HOUR_OF_DAY, hours);
		calendar.add(Calendar.MINUTE, minutes);
		calendar.add(Calendar.SECOND, seconds);
		
		Date unMuteDate = calendar.getTime();
		
		// check for message
		String message = Lang.DEFAULT_TEMPMUTE_MESSAGE.toString();
		
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
		
		Banana.getMuteCache().tempMute(uuid, unMuteDate);
		Banana.getDatabaseManager().asyncAddMute(uuid, message, true, unMuteDate.getTime(), args[1]);
		
		player.sendMessage(Lang.TEMPMUTE_SUCCESS.parseName((new Main()).getLatestName(uuid)));
		
		Banana.getDatabaseManager().logCommand(CommandType.TEMP_MUTE, player.getUniqueId(), args, false);
		
		// check if announcements are enabled for this command.. then release the annoucnement
		if (Values.ANNOUNCE_TEMPMUTE) {
												
			Action.broadcastMessage(Action.TEMPMUTE, Lang.TEMP_MUTE_BROADCAST.parseTimeBroadcast(days, hours, minutes, seconds, (new Main()).getLatestName(uuid), player.getName(), message));
			
		}
		
		// no need to go any further if the target is offline
		if (target == null) return true;
		
		// check if the player should be notified
		if (Values.NOTIFY_TEMPMUTE) {
						
			Action.notifyPlayer(Action.TEMPMUTE, target, Lang.TEMP_MUTE_NOTIFY.parseTimeBroadcast(days, hours, minutes, seconds, "", target.getName(), message));
		}
		
		return true;
	}

}
