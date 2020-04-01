package us.thetaco.banana.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import info.dyndns.thetaco.uuid.api.Main;
import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.Lang;

public class RmStaffCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			
			// This will run if the sender is not player
			return new RmStaffCommandConsole().runRmStaffCommand(sender, args);
			
		}
		
		// run this if the sender is a player
		Player player = (Player) sender;

		if (!player.hasPermission("banana.commands.rmstaff")) {
			player.sendMessage(Lang.NO_PERMISSIONS.toString());
			return true;
		}

		if (args.length < 1) {
			player.sendMessage(Lang.RM_STAFF_WRONG_ARGS.toString());
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
			player.sendMessage(Lang.NOT_STAFF.toString());
			return true;
		}
		
		Banana.getPlayerCache().removeStaff(uuid);
		Banana.getDatabaseManager().asyncRemoveStaffMember(uuid);

		player.sendMessage(Lang.RM_STAFF_SUCCESS.parseName((new Main()).getLatestName(uuid)));

		// check to see if the player was online when de-staff-moded and the server is in staff mode
		if (target != null && Banana.isStaffMode()) {
			
			target.kickPlayer(Lang.STAFF_MODE_KICK_MESSAGE.toString());
			
		}
		
		return true;

	}

}
