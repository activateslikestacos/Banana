package us.thetaco.banana.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.Lang;

public class KickAllCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		boolean isConsole = !(sender instanceof Player);
		String playerName;
		
		if (!isConsole) {
			
			if (!((Player)sender).hasPermission("banana.commands.kickall")) {
				sender.sendMessage(Lang.NO_PERMISSIONS.toString());
				return true;
			}
			
			playerName = ((Player)sender).getName();
			
		} else {
			
			playerName = Lang.CONSOLE_NAME.toString();
			
		}
		
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
			
			if (isConsole || !p.getName().equalsIgnoreCase(playerName)) {
				p.kickPlayer(Lang.KICK_FORMAT.parseBanFormat(playerName, message));
			}
			
		}
		
		sender.sendMessage(Lang.KICK_ALL_SUCCESS.toString());
		
		// publish the kickall to the database for the other servers
		
		if (args.length < 1) {
		
			// run the database update function, but tell it there was no message
			Banana.getDatabaseManager().kickAllUpdatePublisher(playerName, "{NO_MESSAGE}");
			
		} else {
			
			// run the database update function and just pass the message to it
			Banana.getDatabaseManager().kickAllUpdatePublisher(playerName, message);
			
		}
		return true;
	}

	
	
}
