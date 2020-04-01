package us.thetaco.banana.commands;

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

public class UnBanIPCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			
			// run this if the sender is not a player
			return new UnBanIPCommandConsole().runUnBanIPCommand(sender, args);
			
		}
		
		// run this if the sender is a player
		Player player = (Player) sender;
		
		if (!player.hasPermission("banana.commands.unbanip")) {
			player.sendMessage(Lang.NO_PERMISSIONS.toString());
			return true;
		}
		
		if (args.length < 1) {
			player.sendMessage(Lang.UNBAN_IP_WRONG_ARGS.toString());
			return true;
		}
		
		String uuid = null;
		String address = null;
		boolean ipGiven = false;
		Main main = new Main();
		
		if (args.length >= 2 && args[1].equalsIgnoreCase("-f")) {
			
			// run this if the address should just be forced on
			address = args[0];
			ipGiven = true;
			
		} else {
				
			// run this in a normal conditions
		
			uuid = main.getPlayer(args[0]).getUUID();
			
			if (uuid == null) {
				
				player.sendMessage(Lang.USING_IP.toString());
				
				address = args[0];
				
				ipGiven = true;
				
			} else {
			
				String tempAddress = Banana.getPlayerCache().getAddress(uuid);
				
				if (tempAddress == null) {
					player.sendMessage(Lang.NO_IP_STORED.toString());
					return true;
				}
				
				address = tempAddress;
				
			}
		}
		
		if (!Banana.getBanCache().isIPBanned(address)) {
			player.sendMessage(Lang.IP_NOT_BANNED.parseObject(address));
			return true;
		}
		
		Banana.getDatabaseManager().asyncRemoveIPBan(address);
		Banana.getBanCache().unbanIP(address);
		
		player.sendMessage(Lang.UNBAN_IP_SUCCESS.parseObject(address));
		
		Banana.getDatabaseManager().logCommand(CommandType.UN_BAN_IP, player.getUniqueId(), args, false);
		
		// check if announcements are enabled for this command.. then release the annoucnement
		if (Values.ANNOUNCE_UNBANIP) {
									
			if (!ipGiven) {
				Action.broadcastMessage(Action.BANIP, Lang.UNBAN_IP_BROADCAST.parseBroadcast(player.getName(), main.getPlayer(args[0]).getLatestName()));
			} else {
				Action.broadcastMessage(Action.BANIP, Lang.UNBAN_IP_BROADCAST.parseBroadcast(player.getName(), address));
			}
			
		}
		
		return true;
	}

}
