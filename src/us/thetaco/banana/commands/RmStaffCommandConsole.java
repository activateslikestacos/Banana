package us.thetaco.banana.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import info.dyndns.thetaco.uuid.api.Main;
import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.Lang;

public class RmStaffCommandConsole {

	public boolean runRmStaffCommand(CommandSender sender, String[] args) {
		
		if (args.length < 1) {
			sender.sendMessage(Lang.RM_STAFF_WRONG_ARGS.toString());
			return true;
		}

		Player target = Bukkit.getPlayer(args[0]);

		String uuid = null;

		if (target == null) {

			uuid = (new Main()).getPlayer(args[0]).getUUID();

		} else {

			uuid = target.getUniqueId().toString();

		}

		if (uuid == null) {
			Lang.PLAYER_NEVER_ONLINE.parseObject(args[0]);
			return true;
		}

		if (!Banana.getPlayerCache().isStaff(uuid)) {
			sender.sendMessage(Lang.NOT_STAFF.toString());
			return true;
		}
		
		Banana.getPlayerCache().removeStaff(uuid);
		Banana.getDatabaseManager().asyncRemoveStaffMember(uuid);

		sender.sendMessage(Lang.RM_STAFF_SUCCESS.parseName((new Main()).getLatestName(uuid)));

		// check to see if the player was online when de-staff-moded and the server is in staff mode
		if (target != null && Banana.isStaffMode()) {
					
			target.kickPlayer(Lang.STAFF_MODE_KICK_MESSAGE.toString());
					
		}
		
		return true;
		
	}
	
}
