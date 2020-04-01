package us.thetaco.banana.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import info.dyndns.thetaco.uuid.api.Main;
import info.dyndns.thetaco.uuid.api.UUIDPlayer;
import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.Action;
import us.thetaco.banana.utils.CommandType;
import us.thetaco.banana.utils.Lang;
import us.thetaco.banana.utils.Values;

public class UnBanCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			
			// run this if the sender is not a player
			return new UnBanCommandConsole().runUnBanCommand(sender, args);
			
		}
		
		// run this if the sender is a player
		Player player = (Player) sender;
		
		if (!player.hasPermission("banana.commands.unban")) {
			player.sendMessage(Lang.NO_PERMISSIONS.toString());
			return true;
		}
		
		if (args.length < 1) {
			player.sendMessage(Lang.NO_PLAYER_SPECIFIED.toString());
			return true;
		}
		
		// assuming the player is offline since they are banned
		Main main = new Main();
		
		UUIDPlayer uuid = main.getPlayer(args[0]);
		
		if (uuid.getUUID() == null || uuid.getLatestName() == null) {
			player.sendMessage(Lang.PLAYER_NOT_FOUND.parseObject(args[0]));
			return true;
		}
		
		if (!Banana.getBanCache().isUUIDBanned(uuid.getUUID())) {
			player.sendMessage(Lang.PLAYER_NOT_BANNED.parseName(uuid.getLatestName()));
			return true;
		}
		
		Banana.getBanCache().removeBan(uuid.getUUID());
		Banana.getDatabaseManager().asyncRemoveBan(uuid.getUUID());
		
		player.sendMessage(Lang.UNBAN_SUCCESS.parseName(uuid.getLatestName()));
		
		Banana.getDatabaseManager().logCommand(CommandType.UN_BAN, player.getUniqueId(), args, false);
		
		// check if announcements are enabled for this command.. then release the annoucnement
		if (Values.ANNOUNCE_UNBAN) {
									
			Action.broadcastMessage(Action.UNBAN, Lang.UNBAN_BROADCAST.parseBroadcast(player.getName(), uuid.getLatestName()));
							
		}
		
		return true;
	}

	
	
}
