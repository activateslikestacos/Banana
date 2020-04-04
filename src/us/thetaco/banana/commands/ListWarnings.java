package us.thetaco.banana.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.CommandType;
import us.thetaco.banana.utils.Lang;

public class ListWarnings implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		boolean isConsole = !(sender instanceof Player);
		UUID senderUUID;
		
		if (!isConsole) {
			
			if (!((Player)sender).hasPermission("banana.commands.listwarnings")) {
				sender.sendMessage(Lang.NO_PERMISSIONS.toString());
				return true;
			}
			
			senderUUID = ((Player)sender).getUniqueId();
			
			Banana.getDatabaseManager().logCommand(CommandType.LIST_WARNINGS, senderUUID, args, false);
			
		} else {
			
			senderUUID = null;
			
			Banana.getDatabaseManager().logCommand(CommandType.LIST_WARNINGS, senderUUID, args, true);
			
		}
		
		if (args.length < 1) {
			sender.sendMessage(Lang.LIST_WARNINGS_WRONG_ARGS.toString());
			return true;
		}
		
		Player target = Bukkit.getPlayer(args[0]);
		
		String uuid = null;
		
		if (target == null) {
			
			uuid = Banana.getPlayerCache().getLatestName(args[0].toLowerCase());
			
		} else {
			
			uuid = target.getUniqueId().toString();
		
		}
		
		if (uuid == null) {
			sender.sendMessage(Lang.PLAYER_NEVER_ONLINE.parseObject(args[0].toLowerCase()));
			return true;
		}
		
		Banana.getDatabaseManager().asyncListWarnings(sender, uuid);
		
		return true;
	}

}
