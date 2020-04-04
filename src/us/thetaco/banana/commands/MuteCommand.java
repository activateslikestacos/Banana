package us.thetaco.banana.commands;

import java.util.UUID;

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
import us.thetaco.banana.utils.OfflineCallback;
import us.thetaco.banana.utils.Values;

public class MuteCommand implements CommandExecutor, OfflineCallback {
	
	private Banana plugin;
	public MuteCommand(Banana plugin) {
		this.plugin = plugin;
	}
	
	private CommandSender sender;
	private String[] cmdArgs;
	private String message;
	private String senderName;
	private UUID senderUUID;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		boolean isConsole = !(sender instanceof Player);
		
		if (!isConsole) {
			
			if (!((Player)sender).hasPermission("banana.commands.mute")) {
				sender.sendMessage(Lang.NO_PERMISSIONS.toString());
				return true;
			}
			
			senderName = ((Player)sender).getName();
			senderUUID = ((Player)sender).getUniqueId();
			
		} else {
			
			senderName = Lang.CONSOLE_NAME.toString();
			senderUUID = null;
			
		}
		
		Banana.getDatabaseManager().logCommand(CommandType.MUTE, senderUUID, args, isConsole);
		
		if (args.length < 1) {
			sender.sendMessage(Lang.MUTE_WRONG_ARGS.toString());
			return true;
		}
		
		// Copy over command sender
		this.sender = sender;
		
		// Copy over arguments
		cmdArgs = new String[args.length];
		
		for (int i = 0; i < args.length; i++)
			cmdArgs[i] = new String(args[i]);
		
		// check if they want a mute message
		message = null;

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
		
		String uuid = null;
		
		if (target == null) {

			// Attempt to get player info from cache
			uuid = Banana.getPlayerCache().getUUIDByLatestName(args[0]);
			
			// if uuid is null, call to mojang for info
			if (uuid == null) {
				
				this.plugin.getOfflineUUIDHandler().addOfflinePlayer(args[0].toLowerCase(), this);
				return true;
				
			}
			
		} else {
				
			if (target.hasPermission("banana.immune.mute")) {
				sender.sendMessage(Lang.CANNOT_BE_MUTED.parseName(target.getName()));
				return true;
			}
				
			uuid = target.getUniqueId().toString();
			
		}
		
		this.mutePlayer(uuid, args[0]);
		
		// no need to go any further if the player is offline
		if (target == null) return true;
		
		// check if the player should be notified
		if (Values.NOTIFY_MUTE) {
			
			Action.notifyPlayer(Action.MUTE, target, Lang.MUTE_NOTIFY.parseBanFormat(senderName, message));
			
		}
		
		return true;
	}

	private void mutePlayer(String uuid, String playerName) {
		
		// check if the player is already muted!
		if (Banana.getMuteCache().isMuted(uuid)) {

			if (Banana.getMuteCache().isTempMuted(uuid)) {
				sender.sendMessage(Lang.ALREADY_TEMP_BANNED.parseName(playerName));
			} else {
				sender.sendMessage(Lang.ALREADY_MUTED.parseName(playerName));
			}

			return;
		}
		
		Banana.getMuteCache().addMutedPlayer(uuid);
		Banana.getDatabaseManager().asyncAddMute(uuid, message, false, null, null);
		
		sender.sendMessage(Lang.MUTE_SUCCESS.parseName( (new Main()).getLatestName(uuid)));
		
		// check if announcements are enabled for this command.. then release the
		// Announcement
		if (Values.ANNOUNCE_MUTE) {

			Action.broadcastMessage(Action.MUTE, Lang.MUTE_BROADCAST.parseWarningBroadcast(senderName, playerName, message));

		}
		
	}
	
	@Override
	public synchronized void fetchedName(String uuid, String playerName) {
		
		if (uuid == null) {
			sender.sendMessage(Lang.PLAYER_NEVER_ONLINE.parseObject(playerName));
			return;
		}
		
		this.mutePlayer(uuid, playerName);
		
	}

	
	
}
