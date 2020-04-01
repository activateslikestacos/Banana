package us.thetaco.banana.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;

import info.dyndns.thetaco.uuid.api.Main;
import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.CommandType;
import us.thetaco.banana.utils.Lang;

public class IPBanListCommandConsole {

	public boolean runIPBanListCommand(CommandSender sender, String[] args) {
		
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
		
		sender.sendMessage(Lang.IP_BAN_LIST_HEADER.toString());
		sender.sendMessage(bannedCompiled);
		
		Banana.getDatabaseManager().logCommand(CommandType.IPBAN_LIST, null, args, true);
		
		return true;
		
	}
	
}
