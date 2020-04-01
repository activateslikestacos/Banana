package us.thetaco.banana.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import info.dyndns.thetaco.uuid.api.Main;
import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.Action;
import us.thetaco.banana.utils.CommandType;
import us.thetaco.banana.utils.Lang;
import us.thetaco.banana.utils.Values;

public class UnMuteCommandConsole {

	public boolean runUnMuteCommand(CommandSender sender, String[] args) {
		
		if (args.length < 1) {
			sender.sendMessage(Lang.UNMUTE_WRONG_ARGS.toString());
			return true;
		}
		
		Player target = Bukkit.getPlayer(args[0]);
		
		String uuid = null;
		
		if (target == null) {
			
			uuid = new Main().getPlayer(uuid).getLatestName();
			
		} else {
			
			uuid = target.getUniqueId().toString();
			
		}
		
		if (uuid == null) {
			sender.sendMessage(Lang.PLAYER_NEVER_ONLINE.parseObject(args[0]));
			return true;
		}
		
		// check if the player is already muted!
		if (!Banana.getMuteCache().isMuted(uuid)) {
			sender.sendMessage(Lang.PLAYER_NOT_MUTED.toString());
			return true;
		}
		
		Banana.getMuteCache().unMutePlayer(uuid);
		Banana.getDatabaseManager().asyncRemoveMute(uuid);
		
		sender.sendMessage(ChatColor.GREEN + Lang.UNMUTE_SUCCESSFUL.parseName((new Main()).getLatestName(uuid)));
		
		Banana.getDatabaseManager().logCommand(CommandType.UNMUTE, null, args, true);
		
		// check if announcements are enabled for this command.. then release the annoucnement
		if (Values.ANNOUNCE_UNMUTE) {
					
			Action.broadcastMessage(Action.UNMUTE, Lang.UNMUTE_BROADCAST.parseBroadcast(Lang.CONSOLE_NAME.toString(), (new Main()).getLatestName(uuid)));
					
		}
		
		// no need to go any further if the player is offline
		if (target == null) return true;
		
		// check if the player should be notified
		if (Values.NOTIFY_UNMUTE) {
			
			Action.notifyPlayer(Action.UNMUTE, target, Lang.UNMUTE_NOTIFY.toString());
			
		}
		
		return true;
		
	}
	
}
