package us.thetaco.banana.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import info.dyndns.thetaco.uuid.api.Main;
import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.Action;
import us.thetaco.banana.utils.CommandType;
import us.thetaco.banana.utils.Lang;
import us.thetaco.banana.utils.Values;

public class WarnCommandConsole {

	public boolean runWarnCommand(CommandSender sender, String[] args) {
		
		if (args.length < 1) {
			sender.sendMessage(Lang.WARN_WRONG_ARGS.toString());
			return true;
		}
		
		Main main = new Main();
		
		Player target = Bukkit.getPlayer(args[0]);
		
		String uuid = null;
		
		if (target == null) {
			
			uuid = main.getPlayer(args[0]).getUUID();
			
		} else {
			
			if (target.hasPermission("banana.immune.warn")) {
				sender.sendMessage(Lang.PLAYER_CANNOT_BE_WARNED.parseName(target.getName()));
				return true;
			}
			
			uuid = target.getUniqueId().toString();
			
		}
		
		if (uuid == null) {
			sender.sendMessage(Lang.PLAYER_NEVER_ONLINE.toString());
			return true;
		}
		
		// check if a warn message was supplied
		
		String message = null;
		
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
		
		
		if (message == null) message = Lang.DEFAULT_WARN_MESSAGE.toString();
		
		Banana.getWarnCache().addWarning(uuid, message);
		Banana.getDatabaseManager().asyncAddWarning(uuid, message, Lang.CONSOLE_NAME.toString());
		
		Banana.getDatabaseManager().logCommand(CommandType.WARN, null, args, true);
		
		// Take care of warning actions if they are enabled
		if (Values.TAKE_WARNING_ACTION) {
			
			Banana.getWarnCache().applyWarningAction(uuid);
			
		}
		
		// check if announcements are enabled for this command.. then release the annoucnement
		if (Values.ANNOUNCE_WARNING) {
					
			Action.broadcastMessage(Action.WARNING, Lang.WARN_BROADCAST.parseWarningBroadcast(Lang.CONSOLE_NAME.toString(), (new Main()).getLatestName(uuid), message));
					
		}
		
		// tell the sender that target has been warned
		sender.sendMessage(Lang.WARN_SUCCESS.parseName(main.getLatestName(uuid)));
		
		// no need to go any further if the player is offline
		if (target == null) return true;
		
		// check if the player should be notified
		if (Values.NOTIFY_WARNING) {
			
			Action.notifyPlayer(Action.WARNING, target, Lang.WARN_NOTIFY.parseBanFormat(Lang.CONSOLE_NAME.toString(), message));
			
		}
		
		return true;
		
	}
	
}
