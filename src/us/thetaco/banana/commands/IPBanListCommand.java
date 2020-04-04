package us.thetaco.banana.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.CommandType;
import us.thetaco.banana.utils.Lang;

public class IPBanListCommand implements CommandExecutor  {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender instanceof Player) {
			
			if (!((Player)sender).hasPermission("banana.commands.ipbanlist")) {
				sender.sendMessage(Lang.NO_PERMISSIONS.toString());
				return true;
			}
			
			Banana.getDatabaseManager().logCommand(CommandType.IPBAN_LIST, ((Player)sender).getUniqueId(), args, false);
			
		} else {
			
			Banana.getDatabaseManager().logCommand(CommandType.IPBAN_LIST, null, args, true);
			
		}

		
		Set<String> currentyBanned = Banana.getBanCache().getIPBans();
		List<String> currentlyBannedNames = new ArrayList<String>();
		
		for (String s : currentyBanned) {
			
			String name = Banana.getPlayerCache().getLatestName(s);
			
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
		
		sender.sendMessage(Lang.IP_BAN_LIST_HEADER.toString());
		sender.sendMessage(bannedCompiled);
		
		return true;
	}

}
