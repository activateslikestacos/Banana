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

		if (!(sender instanceof Player)) {
			
			// run this if the sender is not a player
			return new KickAllCommandConsole().runKickAllCommand(sender, args);
			
		}
		
		// run this if the sender is a player
		Player player = (Player) sender;
		
		if (!player.hasPermission("banana.commands.kickall")) {
			player.sendMessage(Lang.NO_PERMISSIONS.toString());
			return true;
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
			
			if (!p.getName().equalsIgnoreCase(player.getName())) {
				p.kickPlayer(Lang.KICK_FORMAT.parseBanFormat(player.getName(), message));
			}
			
		}
		
		player.sendMessage(Lang.KICK_ALL_SUCCESS.toString());
		
		// publish the kickall to the database for the other servers
		
		if (args.length < 1) {
		
			// run the database update function, but tell it there was no message
			Banana.getDatabaseManager().kickAllUpdatePublisher(player.getName(), "{NO_MESSAGE}");
			
		} else {
			
			// run the database update function and just pass the message to it
			Banana.getDatabaseManager().kickAllUpdatePublisher(player.getName(), message);
			
		}
		return true;
	}

	
	
}
