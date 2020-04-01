package us.thetaco.banana.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import info.dyndns.thetaco.uuid.api.Main;
import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.Lang;

public class ListStaffCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			
			// this will run if the sender is not a player
			return new ListStaffCommandConsole().runListStaffCommand(sender, args);
			
		}
		
		// this will run if the sender is a player
		Player player = (Player) sender;
		
		if (!player.hasPermission("banana.commands.liststaff")) {
			player.sendMessage(Lang.NO_PERMISSIONS.toString());
			return true;
		}
		
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
		
		player.sendMessage(Lang.LIST_STAFF_HEADER.toString());
		
		player.sendMessage(listMessage);
		
		return true;
	}

}
