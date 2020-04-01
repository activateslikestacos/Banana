package us.thetaco.banana.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.Action;
import us.thetaco.banana.utils.CommandType;
import us.thetaco.banana.utils.Lang;
import us.thetaco.banana.utils.Values;

public class KickCommandConsole {

	public boolean runKickCommand(CommandSender sender, String[] args) {
		
		if (args.length < 1) {
			sender.sendMessage(Lang.KICK_WRONG_ARGS.toString());
			return true;
		}
		
		Player target = Bukkit.getPlayer(args[0]);
		
		if (target == null) {
			sender.sendMessage(Lang.PLAYER_NOT_FOUND.parseObject(args[0]));
			return true;
		} else {
			
			if (target.hasPermission("banana.immune.kick")) {
				sender.sendMessage(Lang.PLAYER_CANNOT_BE_KICKED.parseName(target.getName()));
				
				return true;
			}

		}
		
		String message = Lang.DEFAULT_KICK_MESSAGE.toString();
		
		if (args.length > 1) {
			
			message = args[1];
			
			int i = 0;
			for (String s : args) {
				
				if (i > 1) {
					
					message += " " + s;
					
				}
				i++;
			}
			
		}
		
		// kick the player with the supplied message
		target.kickPlayer(Lang.KICK_FORMAT.parseBanFormat(Lang.CONSOLE_NAME.toString(), message));
		
		Banana.getDatabaseManager().logCommand(CommandType.KICK, null, args, true);
		
		// check if announcements are enabled for this command.. then release the annoucnement
		if (Values.ANNOUNCE_KICK) {
									
			Action.broadcastMessage(Action.KICK, Lang.KICK_BROADCAST.parseWarningBroadcast(Lang.CONSOLE_NAME.toString(), target.getName(), message));
							
		}
			
		sender.sendMessage(Lang.KICK_SUCCESS.parseName(target.getName()));
		
		return true;
		
	}
	
}
