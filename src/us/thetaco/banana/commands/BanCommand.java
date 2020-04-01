package us.thetaco.banana.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import info.dyndns.thetaco.uuid.api.Main;
import us.thetaco.banana.Banana;
import us.thetaco.banana.sql.DatabaseManager.BannerType;
import us.thetaco.banana.utils.Action;
import us.thetaco.banana.utils.CommandType;
import us.thetaco.banana.utils.Lang;
import us.thetaco.banana.utils.Values;

public class BanCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			
			// this will run if the sender is console..
			return new BanCommandConsole().runBanCommand(sender, args);
			
		}
		
		// if we get to this point, the sender is a player
		Player player = (Player) sender;
		
		if (!player.hasPermission("banana.commands.ban")) {
			player.sendMessage(Lang.NO_PERMISSIONS.toString());
			return true;
		}
		
		// check to see if they supplied enough arguments
		if (args.length < 1) {
			player.sendMessage(Lang.NO_PLAYER_SPECIFIED.toString());
			return true;
		}
		
		// start by trying to get the player's UUID through uuidAPI or checking if they are online
		
		Player target = Bukkit.getPlayer(args[0]);
		
		String uuid;
		
		if (target != null) {
			
			uuid = target.getUniqueId().toString();
			
		} else {
			
			Main main = new Main();
		
			uuid = main.getPlayer(args[0]).getUUID();
			
		}
		
		if (target != null) {
			
			if (target.hasPermission("banana.immune.ban")) {
				player.sendMessage(Lang.CANNOT_BE_BANNED.parseName(target.getName()));
				return true;
			}
			
		}
		
		// check if there is a uuid stored
		if (uuid == null) {
			player.sendMessage(Lang.PLAYER_NEVER_ONLINE.parseObject(args[0]));
			return true;
		}
		
		// check if the player is already banned
		if (Banana.getBanCache().isUUIDBanned(uuid)) {
			
			if (Banana.getBanCache().isTempBanned(uuid)) {
				
				player.sendMessage(Lang.ALREADY_TEMP_BANNED.parseName((new Main()).getLatestName(uuid)));
				
			} else {
				
				player.sendMessage(Lang.ALREADY_BANNED.parseName((new Main()).getLatestName(uuid)));
				
			}
			
			return true;
			
		}
		
		// check if they want a ban message
		
		String banMessage = Lang.DEFAULT_BAN_MESSAGE.toString();
		
		if (args.length > 1) {
			
			banMessage = args[1];
			
			int i = 0;
			for (String s : args) {
				
				if (i > 1) {
					
					banMessage += " " + s;
					
				}
				i++;
			}
			
		}
		
		// add the player as banned
		Banana.getBanCache().addBannedUUID(uuid, banMessage, BannerType.PLAYER, player.getUniqueId().toString());
		Banana.getDatabaseManager().asyncAddBan(uuid, BannerType.PLAYER, player.getUniqueId().toString(), banMessage, false, null, null);
		// kick the target if they are online
		if (target != null) {
			
			target.kickPlayer(Lang.BAN_FORMAT.parseBanFormat(player.getName(), banMessage));
						
		}
		
		player.sendMessage(Lang.BAN_SUCCESS.parseName((new Main()).getLatestName(uuid)));
		
		Banana.getDatabaseManager().logCommand(CommandType.BAN, player.getUniqueId(), args, false);
		
		// check if announcements are enabled for this command.. then release the annoucnement
		if (Values.ANNOUNCE_BAN) {
			
			Action.broadcastMessage(Action.BAN, Lang.BAN_BROADCAST.parseWarningBroadcast(player.getName(), (new Main()).getLatestName(uuid), banMessage));
			
		}
		
		return true;
		
	}
	
}
