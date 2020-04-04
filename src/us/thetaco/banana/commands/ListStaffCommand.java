package us.thetaco.banana.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.Lang;

public class ListStaffCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender instanceof Player) {
			
			if (!((Player)sender).hasPermission("banana.commands.liststaff")) {
				sender.sendMessage(Lang.NO_PERMISSIONS.toString());
				return true;
			}
			
		}
		
		List<String> staffMembers = Banana.getPlayerCache().getStaff();
		
		String listMessage = "";
		
		if (staffMembers.size() > 0) {
			
			sender.sendMessage(Lang.LIST_STAFF_HEADER.toString());
			
			listMessage = Banana.getPlayerCache().getLatestName(staffMembers.get(0));
			
			if (staffMembers.size() > 1) {
				
				int i = 0;
				for (String s : staffMembers) {
					if (i > 0) {
						listMessage += " " + Banana.getPlayerCache().getLatestName(s);
					}
					i++;
				}
				
			}
			
			sender.sendMessage(listMessage);
			
		} else {
			
			sender.sendMessage(Lang.NO_STAFF.toString());
			
		}
		
		
		
		
		
		return true;
	}

}
