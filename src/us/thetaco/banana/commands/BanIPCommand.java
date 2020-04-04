package us.thetaco.banana.commands;

import java.util.List;
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

public class BanIPCommand implements CommandExecutor, OfflineCallback {

	private CommandSender sender;
	private String senderName;
	private UUID senderUUID;
	private boolean isConsole;
	private String[] cmdArgs;
	private String message;
	private Banana plugin;
	public BanIPCommand(Banana plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		isConsole = !(sender instanceof Player);
		
		if (!isConsole) {
			
			if (!((Player)sender).hasPermission("banana.commands.banip")) {
				sender.sendMessage(Lang.NO_PERMISSIONS.toString());
				return true;
			}
			
			senderName = ((Player)sender).getName();
			senderUUID = ((Player)sender).getUniqueId();
			
		} else {
			
			senderName = Lang.CONSOLE_NAME.toString();
			
		}
		
		
		if (args.length < 1) {
			sender.sendMessage(Lang.BAN_IP_INCORRECT_ARGS.toString());
			return true;
		}
		
		// copy over the sender
		this.sender = sender;
		
		// Assemble the arguments
		cmdArgs = new String[args.length];
		
		for (int i = 0; i < args.length; i++)
			cmdArgs[i] = new String(args[i]);
		
		// Assemble ban message
		message = Lang.DEFAULT_BAN_MESSAGE.toString();
		
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
		
		Player target = Bukkit.getPlayer(args[0]);
		
		if (target != null) {
			
			if (target.hasPermission("banana.immune.banip")) {
				sender.sendMessage(Lang.CANNOT_BE_IP_BANNED.parseName(target.getName()));
				return true;
			}
			
		}
		
		String uuid = null;
		
		if (target == null) {
			
			uuid = Banana.getPlayerCache().getUUIDByLatestName(args[0].toLowerCase());
			
			if (uuid == null) {
				
				this.plugin.getOfflineUUIDHandler().addOfflinePlayer(args[0].toLowerCase(), this);
				return true;
				
			}
			
		} else {
			
			uuid = target.getUniqueId().toString();
			
		}
		
		this.banIP(uuid, args[0].toLowerCase());
		
		if (target != null) {
			
			target.kickPlayer(Lang.BAN_IP_FORMAT.parseBanFormat(senderName, message));
			
		}
		
		return true;
	}

	
	private void banIP(String uuid, String playerName) {
		
		String address;
		boolean ipGiven = false;
		
		if (uuid == null) {
			
			sender.sendMessage(Lang.USING_IP.toString());
			
			address = cmdArgs[0];
			
			ipGiven = true;
			
		} else {
			
			String tempAddress = Banana.getPlayerCache().getAddress(uuid);
			
			if (tempAddress == null) {
				sender.sendMessage(Lang.NO_IP_STORED.toString());
				return;
			}
			
			address = tempAddress;
			
		}
		
		// If the IP is set to be given, then use that string
		String nameToUse;

		if (ipGiven) {
			nameToUse = address;
		} else {
			nameToUse = new String(playerName);
		}
		
		// check to see if the IP is already banned
		if (Banana.getBanCache().isIPBanned(address)) {

			if (Banana.getBanCache().isTempBanned(uuid)) {

				sender.sendMessage(Lang.ALREADY_TEMP_BANNED.parseName(nameToUse));

			} else {

				sender.sendMessage(Lang.ALREADY_BANNED.parseName(nameToUse));

			}

			return;
		}
		
		if (isConsole) {
			
			Banana.getBanCache().addBannedIP(address, message, BannerType.CONSOLE, null);
			Banana.getDatabaseManager().asyncBanIP(address, message, BannerType.CONSOLE, null, false, null, null);
			
			Banana.getDatabaseManager().logCommand(CommandType.BAN_IP, null, cmdArgs, true);
			
		} else {
		
			Banana.getBanCache().addBannedIP(address, message, BannerType.PLAYER, senderUUID.toString());
			Banana.getDatabaseManager().asyncBanIP(address, message, BannerType.PLAYER, senderUUID.toString(), false, null, null);
		
			Banana.getDatabaseManager().logCommand(CommandType.BAN_IP, senderUUID, cmdArgs, false);
			
		}
		if (!ipGiven) {
			sender.sendMessage(Lang.IP_BAN_SUCCESS.parseName(playerName));
		} else {
			sender.sendMessage(Lang.IP_BAN_SUCCESS.parseName(address));
		}
		
		// check if announcements are enabled for this command.. then release the
		// announcement
		if (Values.ANNOUNCE_BANIP) {

			if (!ipGiven) {
				Action.broadcastMessage(Action.BANIP, Lang.IP_BAN_BROADCAST.parseWarningBroadcast(senderName,
						playerName, message));
			} else {
				Action.broadcastMessage(Action.BANIP,
						Lang.IP_BAN_BROADCAST.parseWarningBroadcast(senderName, address, message));
			}
		}
		
		// checking to make sure that username's attatched to ips is also enabled
		if (Values.BAN_USERNAME_OF_IP) {

			// get all usernames attatched to the IP
			List<String> playersWithIP = Banana.getPlayerCache().getPlayersWithIP(address);

			if (playersWithIP == null)
				return;

			// loop through the list and ban all players who are attatched to that certain
			// IP
			for (String s : playersWithIP) {

				// making sure the player isn't already banned
				if (!Banana.getBanCache().isUUIDBanned(s)) {

					// going ahead and banning them with the appropriate message
					Banana.getBanCache().addBannedUUID(s, Lang.PLAYER_IP_AUTOBANNED.parseBanFormat(null, message),
							BannerType.AUTOBANNED, null);
					Banana.getDatabaseManager().asyncAddBan(s, BannerType.AUTOBANNED, null,
							Lang.PLAYER_IP_AUTOBANNED.parseBanFormat(null, message), false, null, null);

					// check if the player is online and kick them if they are
					Player duplicate = Bukkit.getPlayer(UUID.fromString(s));

					if (duplicate != null) {
						
						// Kick player synchronously.. Because it's a good thing to do
						Bukkit.getScheduler().runTask(plugin, new Runnable() {
							
							public void run() {
							
								duplicate.kickPlayer(Lang.PLAYER_IP_AUTOBANNED.parseBanFormat(null, message));
							
							}
							
						});
						
					}

					// check if announcements are want for this type of ban
					if (Values.ANNOUNCE_BAN_USERNAME_OF_IP) {

						// check if announcements are enabled for this command.. then release the
						// annoucnement
						if (Values.ANNOUNCE_BANIP) {

							Action.broadcastMessage(Action.BAN,
									Lang.AUTOBAN_BROADCAST.parseName(Banana.getPlayerCache().getLatestName(s)));

						}

					}

				}

			}

		}
		
	}


	@Override
	public synchronized void fetchedName(String uuid, String playerName) {
		
		this.banIP(uuid, playerName);
		
	}
	
}
