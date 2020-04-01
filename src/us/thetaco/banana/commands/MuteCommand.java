package us.thetaco.banana.commands;

import org.bukkit.Bukkit;
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

public class MuteCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			
			// this will run if the sender is not a player
			return new MuteCommandConsole().runMuteCommand(sender, args);
			
		}
		
		// this will run if the sender is a player
		Player player = (Player) sender;
		
		if (!player.hasPermission("banana.commands.mute")) {
			player.sendMessage(Lang.NO_PERMISSIONS.toString());
			return true;
		}
		
		if (args.length < 1) {
			player.sendMessage(Lang.MUTE_WRONG_ARGS.toString());
			return true;
		}
		
		Player target = Bukkit.getPlayer(args[0]);
		
		String uuid = null;
		Main main = new Main();
		
		if (target == null) {

			uuid = main.getPlayer(args[0]).getUUID();
			
		} else {
				
			if (target.hasPermission("banana.immune.mute")) {
				player.sendMessage(Lang.CANNOT_BE_MUTED.parseName(target.getName()));
				return true;
			}
				
			uuid = target.getUniqueId().toString();
			
		}
		
		if (uuid == null) {
			player.sendMessage(Lang.PLAYER_NEVER_ONLINE.parseObject(args[0]));
			return true;
		}
		
		// check if the player is already muted!
		if (Banana.getMuteCache().isMuted(uuid)) {

			if (Banana.getMuteCache().isTempMuted(uuid)) {
				player.sendMessage(Lang.ALREADY_TEMP_BANNED.parseName((new Main()).getLatestName(uuid)));
			} else {
				player.sendMessage(Lang.ALREADY_MUTED.parseName((new Main()).getLatestName(uuid)));
			}

			return true;
		}
		
		// check if they want a mute message

		String message = null;

		if (args.length > 1) {

			message = args[1];

			int i = 0;
			for (String s : args) {

				if (i > 1) {

					message += " " + s;

				}
				i++;
			}

		}
		
		Banana.getMuteCache().addMutedPlayer(uuid);
		Banana.getDatabaseManager().asyncAddMute(uuid, message, false, null, null);
		
		player.sendMessage(Lang.MUTE_SUCCESS.parseName( (new Main()).getLatestName(uuid)));
		
		Banana.getDatabaseManager().logCommand(CommandType.MUTE, player.getUniqueId(), args, false);
		
		// check if announcements are enabled for this command.. then release the annoucnement
		if (Values.ANNOUNCE_MUTE) {
							
			Action.broadcastMessage(Action.MUTE, Lang.MUTE_BROADCAST.parseBroadcast(player.getName(), (new Main()).getLatestName(uuid)));
			
		}
		
		// no need to go any further if the player is offline
		if (target == null) return true;
		
		// check if the player should be notified
		if (Values.NOTIFY_MUTE) {
			
			Action.notifyPlayer(Action.MUTE, target, Lang.MUTE_NOTIFY.parseBanFormat(player.getName(), message));
			
		}
		
		return true;
	}

	
	
}
