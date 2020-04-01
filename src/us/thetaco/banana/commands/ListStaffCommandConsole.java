package us.thetaco.banana.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import info.dyndns.thetaco.uuid.api.Main;
import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.Lang;

public class ListStaffCommandConsole {

	public boolean runListStaffCommand(CommandSender sender, String[] args) {
		
		List<String> staffMembers = Banana.getPlayerCache().getStaff();
		
		String listMessage = "";
		
		if (staffMembers.size() > 0) {
			
			listMessage = (new Main()).getLatestName(staffMembers.get(0));
			
			if (staffMembers.size() > 1) {
				
				int i = 0;
				for (String s : staffMembers) {
					if (i > 0) {
						listMessage += " " + (new Main()).getLatestName(s);
					}
					i++;
				}
				
			}
			
		}
		
		sender.sendMessage(Lang.LIST_STAFF_HEADER.toString());
		
		sender.sendMessage(listMessage);
		
		return true;
		
	}
	
}
