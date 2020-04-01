package us.thetaco.banana.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import info.dyndns.thetaco.uuid.api.Main;
import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.Lang;

public class PurgeWarnings implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			
			return new PurgeWarningsCommandConsole().runPurgeWarningsCommand(sender, args);
			
		}
		
		// run this if the sender is a player
		Player player = (Player) sender;
		
		if (!player.hasPermission("banana.commands.purgewarnings")) {
			player.sendMessage(Lang.NO_PERMISSIONS.toString());
			return true;
		}
		
		if (args.length < 1) {
			player.sendMessage(Lang.PURGE_WARNINGS_INCORRECT_ARGS.toString());
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
			player.sendMessage(Lang.PLAYER_NEVER_ONLINE.toString());
			return true;
		}
		 
		// removing all of their warning stats
		Banana.getWarnCache().purgeWarnings(uuid);
		Banana.getDatabaseManager().asyncPurgeWarnings(uuid);
		
		player.sendMessage(Lang.PURGE_WARNINGS_SUCCESS.parseName((new Main()).getLatestName(uuid)));
		
		return true;
	}

}
