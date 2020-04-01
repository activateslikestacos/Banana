package us.thetaco.banana.commands;

import org.bukkit.command.CommandSender;

import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.CommandType;
import us.thetaco.banana.utils.Lang;

public class DelWarningCommandConsole {

	public boolean runDelWarningCommand(CommandSender sender, String[] args) {
		
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
		
		Banana.getDatabaseManager().logCommand(CommandType.DEL_WARNING, null, args, true);
		
		return true;
		
	}
	
}
