package us.thetaco.banana.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import info.dyndns.thetaco.uuid.api.Main;
import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.Action;
import us.thetaco.banana.utils.CommandType;
import us.thetaco.banana.utils.Lang;
import us.thetaco.banana.utils.Values;

public class UnMuteCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
		
			// run this if the sender is not a player
			return new UnMuteCommandConsole().runUnMuteCommand(sender, args);
			
		}
		
		// run this if the sender is not a player
		Player player = (Player) sender;
		
		if (!player.hasPermission("banana.commands.unmute")) {
			player.sendMessage(Lang.NO_PERMISSIONS.toString());
			return true;
		}
		
		if (args.length < 1) {
			player.sendMessage(Lang.UNMUTE_WRONG_ARGS.toString());
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
			player.sendMessage(Lang.PLAYER_NEVER_ONLINE.parseObject(args[0]));
			return true;
		}
		
		// check if the player is already muted!
		if (!Banana.getMuteCache().isMuted(uuid)) {
			player.sendMessage(Lang.PLAYER_NOT_MUTED.toString());
			return true;
		}
		
		Banana.getMuteCache().unMutePlayer(uuid);
		Banana.getDatabaseManager().asyncRemoveMute(uuid);
		
		player.sendMessage(ChatColor.GREEN + Lang.UNMUTE_SUCCESSFUL.parseName((new Main()).getLatestName(uuid)));
		
		Banana.getDatabaseManager().logCommand(CommandType.UNMUTE, player.getUniqueId(), args, false);
		
		// check if announcements are enabled for this command.. then release the annoucnement
		if (Values.ANNOUNCE_UNMUTE) {
					
			Action.broadcastMessage(Action.UNMUTE, Lang.UNMUTE_BROADCAST.parseBroadcast(player.getName(), (new Main()).getLatestName(uuid)));
					
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
