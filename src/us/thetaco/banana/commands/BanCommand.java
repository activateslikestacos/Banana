package us.thetaco.banana.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.thetaco.banana.Banana;
import us.thetaco.banana.sql.DatabaseManager.BannerType;
import us.thetaco.banana.utils.Action;
import us.thetaco.banana.utils.CommandType;
import us.thetaco.banana.utils.Lang;
import us.thetaco.banana.utils.OfflineCallback;
import us.thetaco.banana.utils.Values;

public class BanCommand implements CommandExecutor, OfflineCallback {

	private CommandSender sender;
	private String senderName;
	private UUID senderUUID;
	private boolean isConsole;
	
	private Banana plugin;
	public BanCommand(Banana plugin) {
		this.plugin = plugin;
	}
	
	private String banMessage;
	private String[] cmdArgs;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		isConsole = !(sender instanceof Player);
		
		if (!isConsole) {
			
			Player player = (Player) sender;
			
			if (!player.hasPermission("banana.commands.ban")) {
				sender.sendMessage(Lang.NO_PERMISSIONS.toString());
				return true;
			}
			
			this.senderName = player.getName();
			this.senderUUID = player.getUniqueId();
			
		} else {
			
			this.senderName = Lang.CONSOLE_NAME.toString();
			
		}
		
		
		// check to see if they supplied enough arguments
		if (args.length < 1) {
			sender.sendMessage(Lang.NO_PLAYER_SPECIFIED.toString());
			return true;
		}
		
		// Copy over sender
		this.sender = sender;
		
		// Copy command arguments
		cmdArgs = new String[args.length];
				
		for (int i = 0; i < args.length; i++) {
			cmdArgs[i] = new String(args[i]);
		}
		
		// check if they want a ban message
		banMessage = Lang.DEFAULT_BAN_MESSAGE.toString();

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
		
		// start by trying to get the player's UUID through uuidAPI or checking if they are online
		
		Player target = Bukkit.getPlayer(args[0]);
		
		String uuid;
		
		if (target == null) {
			
			// Check to see if the player is in the cache
			uuid = Banana.getPlayerCache().getUUIDByLatestName(args[0]);
			
			if (uuid == null) {
				
				// Request uuid from Mojang
				this.plugin.getOfflineUUIDHandler().addOfflinePlayer(args[0].toLowerCase(), this);
				return true;
				
			}
			
		} else {
			
			if (target.hasPermission("banana.immune.ban")) {
				sender.sendMessage(Lang.CANNOT_BE_BANNED.parseName(target.getName()));
				return true;
			}
			
			uuid = target.getUniqueId().toString();
			
		}
		
		// kick the target if they are online
		if (this.banPlayer(uuid, args[0].toLowerCase()) && target != null) {

			target.kickPlayer(Lang.BAN_FORMAT.parseBanFormat(senderName, banMessage));

		}
		
		return true;
		
	}

	/** Used to ban the player. Performs basic checks to ensure they are not already banned etc.
	 * 
	 * @param uuid The UUID of the player to ban (in string form)
	 * @param playerName The name of the player to ban
	 * @return Returns true if the player was successfully banned, false if not
	 */
	private boolean banPlayer(String uuid, String playerName) {
		
		// check if the player is already banned
		if (Banana.getBanCache().isUUIDBanned(uuid)) {

			if (Banana.getBanCache().isTempBanned(uuid)) {

				sender.sendMessage(Lang.ALREADY_TEMP_BANNED.parseName(playerName));

			} else {

				sender.sendMessage(Lang.ALREADY_BANNED.parseName(playerName));

			}

			return false;

		}
		
		if (isConsole) {
			
			// add the player as banned
			Banana.getBanCache().addBannedUUID(uuid, banMessage, BannerType.CONSOLE, null);
			Banana.getDatabaseManager().asyncAddBan(uuid, BannerType.CONSOLE, null, banMessage, false, null, null);
			
			Banana.getDatabaseManager().logCommand(CommandType.BAN, null, cmdArgs, true);
			
		} else {
		
			// add the player as banned
			Banana.getBanCache().addBannedUUID(uuid, banMessage, BannerType.PLAYER, senderUUID.toString());
			Banana.getDatabaseManager().asyncAddBan(uuid, BannerType.PLAYER, senderUUID.toString(), banMessage, false, null, null);
		
			Banana.getDatabaseManager().logCommand(CommandType.BAN, senderUUID, cmdArgs, false);
			
		}

		sender.sendMessage(Lang.BAN_SUCCESS.parseName(playerName));

		// check if announcements are enabled for this command.. then release the
		// annoucnement
		if (Values.ANNOUNCE_BAN) {

			Action.broadcastMessage(Action.BAN, Lang.BAN_BROADCAST.parseWarningBroadcast(senderName, playerName, banMessage));

		}
		
		return true;
		
	}
	
	@Override
	public synchronized void fetchedName(String uuid, String playerName) {
		
		if (uuid == null) {
			
			sender.sendMessage(Lang.PLAYER_NEVER_ONLINE.parseObject(playerName));
			return;
			
		}
		
		// call to ban the player
		this.banPlayer(uuid, playerName);	
		
	}
	
}
