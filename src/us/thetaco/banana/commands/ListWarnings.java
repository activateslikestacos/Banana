package us.thetaco.banana.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import info.dyndns.thetaco.uuid.api.Main;
import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.CommandType;
import us.thetaco.banana.utils.Lang;

public class ListWarnings implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			
			// run this if the sender isn't a player
			return new ListWarningCommandConsole().runListWarningCommand(sender, args);
			
		}
		
		Player player = (Player) sender;
		
		if (!player.hasPermission("banana.commands.listwarnings")) {
			player.sendMessage(Lang.NO_PERMISSIONS.toString());
			return true;
		}
		
		if (args.length < 1) {
			player.sendMessage(Lang.LIST_WARNINGS_WRONG_ARGS.toString());
			return true;
		}
		
		Player target = Bukkit.getPlayer(args[0]);
		
		String uuid = null;
		
		if (target == null) {
			
			Main main = new Main();
			uuid = main.getPlayer(args[0]).getUUID();
			
		} else {
			uuid = target.getUniqueId().toString();
		}
		
		if (uuid == null) {
			player.sendMessage(Lang.PLAYER_NEVER_ONLINE.parseObject(args[0]));
			return true;
		}
		
		Banana.getDatabaseManager().asyncListWarnings(player, uuid);
		
		Banana.getDatabaseManager().logCommand(CommandType.LIST_WARNINGS, player.getUniqueId(), args, false);
		
		return true;
	}

}
