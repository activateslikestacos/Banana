package us.thetaco.banana.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.CommandType;
import us.thetaco.banana.utils.Lang;

public class MyWarnsCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			
			// run this if the sender is not a player
			sender.sendMessage(Lang.NOT_RAN_CONSOLE.toString());
			return true;
			
		}
		
		// run this if the sender is a player
		Player player = (Player) sender;
		
		if (player.hasPermission("banana.commands.mywarns")) {
			player.sendMessage(Lang.NO_PERMISSIONS.toString());
			return true;
		}
		
		Banana.getDatabaseManager().asyncListWarnings(player, player.getUniqueId().toString());
		
		// log the command usage:
		Banana.getDatabaseManager().logCommand(CommandType.LIST_WARNINGS, player.getUniqueId(), args, false);
		return true;
	}

	
	
}
