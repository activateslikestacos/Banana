package us.thetaco.banana.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import info.dyndns.thetaco.uuid.api.Main;
import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.CommandType;
import us.thetaco.banana.utils.Lang;

public class IPBanListCommand implements CommandExecutor  {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			
			// run this if the sender is not a player
			return new IPBanListCommandConsole().runIPBanListCommand(sender, args);
			
		}
		
		// run this if the sender is a player
		Player player = (Player) sender;
		
		if (!player.hasPermission("banana.commands.ipbanlist")) {
			player.sendMessage(Lang.NO_PERMISSIONS.toString());
			return true;
		}
		
		Set<String> currentyBanned = Banana.getBanCache().getIPBans();
		List<String> currentlyBannedNames = new ArrayList<String>();
		
		Main main = new Main();
		
		for (String s : currentyBanned) {
			
			String name = main.getLatestName(s);
			
			if (name == null) {
				currentlyBannedNames.add(s);
			} else {
				currentlyBannedNames.add(name);
			}
			
		}
		
		String bannedCompiled = new String();
		
		if (currentlyBannedNames.size() == 1) {
			
			bannedCompiled = currentlyBannedNames.get(0);
			
		}
		
		if (currentlyBannedNames.size() > 1) {
			
			bannedCompiled = currentlyBannedNames.get(0);
			
			int i = 0;
			for (String s : currentlyBannedNames) {
				
				if (i > 1) {
					bannedCompiled += ", " + s;
				}
				i++;
				
			}
			
		}
		
		player.sendMessage(Lang.IP_BAN_LIST_HEADER.toString());
		player.sendMessage(bannedCompiled);
		
		Banana.getDatabaseManager().logCommand(CommandType.IPBAN_LIST, player.getUniqueId(), args, false);
		
		return true;
	}

}
