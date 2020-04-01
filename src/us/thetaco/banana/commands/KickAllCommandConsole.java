package us.thetaco.banana.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.Lang;

public class KickAllCommandConsole {

	public boolean runKickAllCommand(CommandSender sender, String[] args) {
		
		String message = Lang.DEFAULT_KICK_MESSAGE.toString();
		
		if (args.length > 0) {
			
			message = args[0];
			
			int i = 0;
			for (String s : args) {
				
				if (i > 0) {
					
					message += " " + s;
					
				}
				i++;
			}
			
		}
		
		// loop through and kick all the players except the kicker
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			
			p.kickPlayer(Lang.KICK_FORMAT.parseBanFormat(Lang.CONSOLE_NAME.toString(), message));
			
		}
		
		sender.sendMessage(Lang.KICK_ALL_SUCCESS.toString());
		
		if (args.length < 1) {
			
			// run the database update function, but tell it there was no message
			Banana.getDatabaseManager().kickAllUpdatePublisher("{CONSOLE}", "{NO_MESSAGE}");
			
		} else {
			
			// run the database update function and just pass the message to it
			Banana.getDatabaseManager().kickAllUpdatePublisher("{CONSOLE}", message);
			
		}
		
		return true;
		
	}
	
}
