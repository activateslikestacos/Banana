package us.thetaco.banana.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import info.dyndns.thetaco.uuid.api.Main;
import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.CommandType;
import us.thetaco.banana.utils.Lang;

public class ListWarningCommandConsole {

	public boolean runListWarningCommand(CommandSender sender, String[] args) {
		
		if (args.length < 1) {
			sender.sendMessage(Lang.LIST_WARNINGS_WRONG_ARGS.toString());
			return true;
		}
		
		Player target = Bukkit.getPlayer(args[0]);
		
		String uuid = null;
		
		if (target == null) {
			
			Main main = new Main();
			uuid = main.getPlayer(args[0]).getUUID();
			
		} else {
			uuid = target.getUniqueId().toString();
		}
		
		if (uuid == null) {
			sender.sendMessage(Lang.PLAYER_NEVER_ONLINE.parseObject(args[0]));
			return true;
		}
		
		Banana.getDatabaseManager().asyncListWarnings(sender, uuid);
		
		Banana.getDatabaseManager().logCommand(CommandType.LIST_WARNINGS, null, args, true);
		
		return true;
		
	}
	
}
