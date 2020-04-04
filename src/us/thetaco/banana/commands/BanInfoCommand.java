package us.thetaco.banana.commands;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.Lang;
import us.thetaco.banana.utils.OfflineCallback;

public class BanInfoCommand implements CommandExecutor, OfflineCallback {

	private CommandSender sender;
	
	private Banana plugin;
	public BanInfoCommand(Banana plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		boolean isConsole = !(sender instanceof Player);
		
		if (!isConsole) {
			
			// check if the player has permissions to run this command
			if (!((Player)sender).hasPermission("banana.commands.baninfo")) {
				sender.sendMessage(Lang.NO_PERMISSIONS.toString());
				return true;
			}

		}
		
		// check if a player was specified
		if (args.length < 1) {
			sender.sendMessage(Lang.BAN_INFO_WRONG_ARGS.toString());
			return true;
		}
		
		this.sender = sender;
		
		// attempt to retrieve information on the specified argument
		
		// start by seeing if they are online
		Player target = Bukkit.getPlayer(args[0]);
		
		String uuid;
		
		if (target != null) {
			
			// run this if the player is online, and even though they shouldn't be online if they are banned, we will check to see if they are
			uuid = target.getUniqueId().toString();
			
		} else {
			
			// Attempt to call UUID from cache
			uuid = Banana.getPlayerCache().getUUIDByLatestName(args[0].toLowerCase());
			
			if (uuid == null) {
			
				// If uuid is null, call Mojang for uuid information
				this.plugin.getOfflineUUIDHandler().addOfflinePlayer(args[0].toLowerCase(), this);
				return true;
			
			}
			
		}
		
		// Call to list out info, now
		this.listInfo(uuid, args[0].toLowerCase());
		
		return true;
	}

	/** Lists out all info on the player, if they exist
	 * 
	 * @param uuid The uuid to list the info out for
	 * @param playerName The playername attached to the UUID
	 */
	private void listInfo(String uuid, String playerName) {
		
		// if we get to this point, we at least have a UUID to work with, so we can
		// collect information
		boolean isBanned = Banana.getBanCache().isUUIDBanned(uuid);

		boolean isMuted = Banana.getMuteCache().isMuted(uuid);

		boolean isTempMuted = Banana.getMuteCache().isTempMuted(uuid);
		String tempMuteRemovalDate = "none";

		boolean isTempBanned = Banana.getBanCache().isTempBanned(uuid);
		String tempBanRemovalDate = "none";

		boolean isIPBanned = Banana.getBanCache().isIPBanned(Banana.getPlayerCache().getAddress(uuid));

		boolean isTempIPBanned = Banana.getBanCache().isIPTempBanned(Banana.getPlayerCache().getAddress(uuid));
		String tempIPBanRemovalDate = "none";

		String playerAddress = Banana.getPlayerCache().getAddress(uuid);
		
		if (playerAddress == null) {
			playerAddress = "none";
		}
		
		// a nice format for the date
		// TODO: make this a configurable option
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

		// checking if the player is temp banned, so that way we can set up the unban
		// date
		if (isTempBanned) {
			Date unbanDate = Banana.getBanCache().getTempBanEndDate(uuid);

			// avoiding possible errors
			if (unbanDate != null) {
				tempBanRemovalDate = sdf.format(unbanDate);
			}
		}

		// checking if the player is IP temp banned, so that way we can set the unban
		// date
		if (isTempIPBanned) {
			Date unbanDate = Banana.getBanCache().getIPTempBanEndDate(Banana.getPlayerCache().getAddress(uuid));

			// avoiding possible errors
			if (unbanDate != null) {
				tempIPBanRemovalDate = sdf.format(unbanDate);
			}
		}

		// checking if the player is temp muted, so that way we can set up the unmute
		// date
		if (isTempMuted) {
			Date unmuteDate = Banana.getMuteCache().getTempMuteRemovalDate(uuid);

			// avoiding possible errors
			if (unmuteDate != null) {
				tempMuteRemovalDate = sdf.format(unmuteDate);
			}
		}

		// now that we have the information collected, we can post it all to the player
		// in one big message!
		// that message will be made up of several messages, however

		sender.sendMessage(Lang.BAN_INFO_HEADER.toString());
		sender.sendMessage(Lang.BAN_INFO_LINE_BAN.parseObject(Boolean.toString(isBanned)));
		sender.sendMessage(Lang.BAN_INFO_LINE_MUTE.parseObject(Boolean.toString(isMuted)));
		sender.sendMessage(Lang.BAN_INFO_LINE_TEMP_MUTE.parseObject(Boolean.toString(isTempMuted)));
		sender.sendMessage(Lang.BAN_INFO_LINE_MUTE_REMOVAL.parseObject(tempMuteRemovalDate));
		sender.sendMessage(Lang.BAN_INFO_LINE_TEMP_BAN.parseObject(Boolean.toString(isTempBanned)));
		sender.sendMessage(Lang.BAN_INFO_LINE_BAN_REMOVAL.parseObject(tempBanRemovalDate));
		sender.sendMessage(Lang.BAN_INFO_LINE_IP_BAN.parseObject(Boolean.toString(isIPBanned)));
		sender.sendMessage(Lang.BAN_INFO_LINE_TEMP_IP_BAN.parseObject(Boolean.toString(isTempIPBanned)));
		sender.sendMessage(Lang.BAN_INFO_LINE_IP_BAN_REMOVAL.parseObject(tempIPBanRemovalDate));
		sender.sendMessage(Lang.BAN_INFO_LINE_PLAYER_IP.parseObject(playerAddress));

	}
	
	@Override
	public synchronized void fetchedName(String uuid, String playerName) {
		
		if (uuid == null) {
			
			sender.sendMessage(Lang.PLAYER_NOT_FOUND.parseObject(playerName));
			return;
			
		}
		
		this.listInfo(uuid, playerName);
		
	}

}
