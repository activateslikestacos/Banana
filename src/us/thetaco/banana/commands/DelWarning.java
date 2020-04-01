package us.thetaco.banana.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.CommandType;
import us.thetaco.banana.utils.Lang;

public class DelWarning implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			
			// run this if the sender is not a player
			return new DelWarningCommandConsole().runDelWarningCommand(sender, args);
			
		}
		
		// This will run if the sender is a player
		Player player = (Player) sender;
		
		if (!player.hasPermission("banana.commands.delwarning")) {
			player.sendMessage(Lang.NO_PERMISSIONS.toString());
			return true;
		}
		
		if (args.length < 1) {
			player.sendMessage(Lang.DEL_WARNING_WRONG_ARGS.toString());
			return true;
		}
		
		int warnID = 0;
		
		try {
			
			warnID = Integer.parseInt(args[0]);
			
		} catch (Exception e) {
			player.sendMessage(Lang.NUMBER_EXPECTED.parseObject(args[0]));
			return true;
			
		}
		
		Banana.getDatabaseManager().decrementWarning(warnID);
		Banana.getDatabaseManager().asyncDeleteWarning(warnID);
		
		player.sendMessage(Lang.WARNING_DELETE_SUCCESSFUL.toString());
		
		Banana.getDatabaseManager().logCommand(CommandType.DEL_WARNING, player.getUniqueId(), args, false);
		
		return true;
	}

	
	
}
