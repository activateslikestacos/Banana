package us.thetaco.banana.commands;

import org.bukkit.command.CommandSender;

import info.dyndns.thetaco.uuid.api.Main;
import info.dyndns.thetaco.uuid.api.UUIDPlayer;
import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.Action;
import us.thetaco.banana.utils.CommandType;
import us.thetaco.banana.utils.Lang;
import us.thetaco.banana.utils.Values;

public class UnBanCommandConsole {

	public boolean runUnBanCommand(CommandSender sender, String[] args) {
		
		if (args.length < 1) {
			sender.sendMessage(Lang.NO_PLAYER_SPECIFIED.toString());
			return true;
		}
		
		// assuming the player is offline since they are banned
		Main main = new Main();
		
		UUIDPlayer uuid = main.getPlayer(args[0]);
		
		if (uuid.getUUID() == null || uuid.getLatestName() == null) {
			sender.sendMessage(Lang.PLAYER_NOT_FOUND.parseObject(args[0]));
			return true;
		}
		
		if (!Banana.getBanCache().isUUIDBanned(uuid.getUUID())) {
			sender.sendMessage(Lang.PLAYER_NOT_BANNED.parseName(uuid.getLatestName()));
			return true;
		}
		
		Banana.getBanCache().removeBan(uuid.getUUID());
		Banana.getDatabaseManager().asyncRemoveBan(uuid.getUUID());
		
		sender.sendMessage(Lang.UNBAN_SUCCESS.parseName(uuid.getLatestName()));
		
		Banana.getDatabaseManager().logCommand(CommandType.UN_BAN, null, args, true);
		
		// check if announcements are enabled for this command.. then release the annoucnement
		if (Values.ANNOUNCE_UNBAN) {
									
			Action.broadcastMessage(Action.UNBAN, Lang.UNBAN_BROADCAST.parseBroadcast(Lang.CONSOLE_NAME.toString(), uuid.getLatestName()));
							
		}
		
		return true;
		
	}
	
}
