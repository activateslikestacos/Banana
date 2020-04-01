package us.thetaco.banana.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.Lang;

public class ToggleStaffModeCommandConsole {

	public boolean runToggleStaffModeCommand(CommandSender sender, String[] args) {
		
		// check if the server is in staff mode.. if it isn't, kick all players
		// who aren't in staff mode, and prevent them
		// from joining

		if (!Banana.isStaffMode()) {

			// first start by setting the server to staff mode
			Banana.setStaffMode(true);

			// kick all non-staff players
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {

				if (!Banana.getPlayerCache().isStaff(p.getUniqueId().toString())) {
					p.kickPlayer(Lang.STAFF_MODE_KICK_MESSAGE.toString());
				}

			}

			// message that staff mode has been enabled to all staff members!
			Bukkit.broadcastMessage(Lang.STAFF_MODE_ENABLED_BROADCAST.toString());

			return true;
		} else {

			// disable staff mode and notify staff members
			Banana.setStaffMode(false);

			// announce it's disabling
			Bukkit.broadcastMessage(Lang.STAFF_MODE_DISABLED_BROADCAST.toString());

			return true;
		}
		
	}
	
}
