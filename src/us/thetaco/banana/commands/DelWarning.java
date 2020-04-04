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
		
		if (sender instanceof Player) {

			if (!((Player)sender).hasPermission("banana.commands.delwarning")) {
				sender.sendMessage(Lang.NO_PERMISSIONS.toString());
				return true;
			}

			Banana.getDatabaseManager().logCommand(CommandType.DEL_WARNING, ((Player)sender).getUniqueId(), args, false);
			
		} else {
			
			Banana.getDatabaseManager().logCommand(CommandType.DEL_WARNING, null, args, true);
			
		}
		
		
		if (args.length < 1) {
			sender.sendMessage(Lang.DEL_WARNING_WRONG_ARGS.toString());
			return true;
		}
		
		int warnID = 0;
		
		try {
			
			warnID = Integer.parseInt(args[0]);
			
		} catch (Exception e) {
			
			sender.sendMessage(Lang.NUMBER_EXPECTED.parseObject(args[0]));
			return true;
			
		}
		
		Banana.getDatabaseManager().decrementWarning(warnID);
		Banana.getDatabaseManager().asyncDeleteWarning(warnID);
		
		sender.sendMessage(Lang.WARNING_DELETE_SUCCESSFUL.toString());
		
		return true;
	}

	
	
}
