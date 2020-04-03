package us.thetaco.banana.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import us.thetaco.banana.Banana;
import us.thetaco.banana.sql.DatabaseManager.BannerType;
import us.thetaco.banana.utils.Action;
import us.thetaco.banana.utils.CommandType;
import us.thetaco.banana.utils.Lang;
import us.thetaco.banana.utils.OfflineCallback;
import us.thetaco.banana.utils.Values;

public class BanCommandConsole implements OfflineCallback {

	private Banana plugin;
	public BanCommandConsole(Banana plugin) {
		this.plugin = plugin;
	}
	
	private String banMessage = Lang.DEFAULT_BAN_MESSAGE.toString();
	private String[] cmdArgs;
	
	public boolean runBanCommand(CommandSender sender, String[] args) {
		
		// check to see if they supplied enough arguments
		if (args.length < 1) {
			sender.sendMessage(Lang.NO_PLAYER_SPECIFIED.toString());
			return true;
		}
		
		cmdArgs = new String[args.length];
		
		// Assemble arguments for logging
		for (int i = 0; i < args.length; i++) {
			cmdArgs[i] = new String(args[i]);
		}
			
		Player target = Bukkit.getPlayer(args[0]);
		
		if (target != null) {
			
			if (target.hasPermission("banana.immune.ban")) {
				sender.sendMessage(Lang.CANNOT_BE_BANNED.parseName(target.getName()));
				return true;
			}
			
		}
		
		String uuid;
		
		// check if they want a ban message
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
		
		if (target == null) {
			
			uuid = Banana.getPlayerCache().getUUIDByLatestName(args[0].toLowerCase());
			
			if (uuid == null) {
				this.plugin.getOfflineUUIDHandler().addOfflinePlayer(args[0].toLowerCase(), this);
				return true;	
			}
			
		} else {
		
			uuid = target.getUniqueId().toString();
			
		}
		
		// kick the target if they are online
		if (this.banPlayer(uuid, args[0].toLowerCase()) && target != null) {
			
			target.kickPlayer(Lang.BAN_FORMAT.parseBanFormat(Lang.CONSOLE_NAME.toString(), banMessage));
								
		}
		
		return true;
		
	}

	private boolean banPlayer(String uuid, String playerName) {
		
		ConsoleCommandSender sender = this.plugin.getServer().getConsoleSender();
		
		// check if the player is already banned
		if (Banana.getBanCache().isUUIDBanned(uuid)) {

			if (Banana.getBanCache().isTempBanned(uuid)) {

				sender.sendMessage(Lang.ALREADY_TEMP_BANNED.parseName(playerName));

			} else {

				sender.sendMessage(Lang.ALREADY_BANNED.parseName(playerName));

			}

			return false;

		}
		
		// add the player as banned
		Banana.getBanCache().addBannedUUID(uuid, banMessage, BannerType.CONSOLE, null);
		Banana.getDatabaseManager().asyncAddBan(uuid, BannerType.CONSOLE, null, banMessage, false, null, null);
		
		sender.sendMessage(Lang.BAN_SUCCESS.parseName(playerName));
		
		Banana.getDatabaseManager().logCommand(CommandType.BAN, null, cmdArgs, true);
		
		// check if announcements are enabled for this command.. then release the annoucnement
		if (Values.ANNOUNCE_BAN) {
			
			Action.broadcastMessage(Action.BAN, Lang.BAN_BROADCAST.parseWarningBroadcast(BannerType.CONSOLE.toString(), playerName, banMessage));
			
		}
		
		return true;
		
	}
	
	@Override
	public synchronized void fetchedName(String uuid, String playerName) {
		
		if (uuid == null) {
			this.plugin.getServer().getConsoleSender().sendMessage(Lang.PLAYER_NEVER_ONLINE.parseObject(playerName));
			return;
		}
		
		// call to ban the player
		this.banPlayer(uuid, playerName);
		
	}
	
}
