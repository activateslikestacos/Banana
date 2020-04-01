package us.thetaco.banana.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import info.dyndns.thetaco.uuid.api.Main;
import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.Lang;

public class PurgeWarningsCommandConsole {

	public boolean runPurgeWarningsCommand(CommandSender sender, String[] args) {
		
		if (args.length < 1) {
			sender.sendMessage(Lang.PURGE_WARNINGS_INCORRECT_ARGS.toString());
			return true;
		}
		
		Player target = Bukkit.getPlayer(args[0]);
		
		String uuid = null;
		
		if (target == null) {
			
			uuid = (new Main()).getPlayer(args[0]).getUUID();
			
		} else {
			
			uuid = target.getUniqueId().toString();
			
		}
		
		if (uuid == null) {
			sender.sendMessage(Lang.PLAYER_NEVER_ONLINE.toString());
			return true;
		}
		 
		// removing all of their warning stats
		Banana.getWarnCache().purgeWarnings(uuid);
		Banana.getDatabaseManager().asyncPurgeWarnings(uuid);
		
		sender.sendMessage(Lang.PURGE_WARNINGS_SUCCESS.parseName((new Main()).getLatestName(uuid)));
		
		return true;
		
	}
	
}
