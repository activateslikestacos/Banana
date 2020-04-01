package us.thetaco.banana.commands;

import org.bukkit.command.CommandSender;

import info.dyndns.thetaco.uuid.api.Main;
import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.Action;
import us.thetaco.banana.utils.CommandType;
import us.thetaco.banana.utils.Lang;
import us.thetaco.banana.utils.Values;

public class UnBanIPCommandConsole {

	public boolean runUnBanIPCommand(CommandSender sender, String[] args) {
		
		if (args.length < 1) {
			sender.sendMessage(Lang.UNBAN_IP_WRONG_ARGS.toString());
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
			
			uuid = main.getPlayer(args[0]).getUUID();
				
			if (uuid == null) {
			
				sender.sendMessage(Lang.USING_IP.toString());
				
				address = args[0];
				
				ipGiven = true;
			
			} else {
			
				String tempAddress = Banana.getPlayerCache().getAddress(uuid);
				
				if (tempAddress == null) {
					sender.sendMessage(Lang.NO_IP_STORED.toString());
					return true;
				}
				
				address = tempAddress;
				
			}
			
		}
		
		if (!Banana.getBanCache().isIPBanned(address)) {
			sender.sendMessage(Lang.IP_NOT_BANNED.parseObject(address));
			return true;
		}
		
		Banana.getDatabaseManager().asyncRemoveIPBan(address);
		Banana.getBanCache().unbanIP(address);
		
		sender.sendMessage(Lang.UNBAN_IP_SUCCESS.parseObject(address));
		
		Banana.getDatabaseManager().logCommand(CommandType.UN_BAN_IP, null, args, true);
		
		// check if announcements are enabled for this command.. then release the annoucnement
		if (Values.ANNOUNCE_UNBANIP) {
									
			if (!ipGiven) {
				Action.broadcastMessage(Action.BANIP, Lang.UNBAN_IP_BROADCAST.parseBroadcast(Lang.CONSOLE_NAME.toString(), main.getPlayer(args[0]).getLatestName()));
			} else {
				Action.broadcastMessage(Action.BANIP, Lang.UNBAN_IP_BROADCAST.parseBroadcast(Lang.CONSOLE_NAME.toString(), address));
			}
			
		}
		
		return true;
		
	}
	
}
