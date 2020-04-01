package us.thetaco.banana.commands;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import info.dyndns.thetaco.uuid.api.Main;
import info.dyndns.thetaco.uuid.api.UUIDPlayer;
import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.Lang;

public class BanInfoCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			
			// Run this if the sender is console
			
			return true;
		}
		
		Player player = (Player) sender;
		
		// check if the player has permissions to run this command
		if (!player.hasPermission("banana.commands.baninfo")) {
			player.sendMessage(Lang.NO_PERMISSIONS.toString());
			return true;
		}
		
		// check if a player was specified
		if (args.length < 1) {
			player.sendMessage(Lang.BAN_INFO_WRONG_ARGS.toString());
			return true;
		}
		
		// attempt to retrieve information on the specified argument
		
		// start by seeing if they are online
		Player target = Bukkit.getPlayer(args[0]);
		
		String specifiedPlayer = null;
		
		if (target != null) {
			
			// run this if the player is online, and even though they shouldn't be online if they are banned, we will check to see if they are
			specifiedPlayer = target.getName();
			
		} else {
			
			specifiedPlayer = args[0];
			
		}
		
		Main main = new Main();
		
		UUIDPlayer uuidP = main.getPlayer(specifiedPlayer);
		
		String obtainedUUID = uuidP.getUUID();
		
		// check to find out if the specified player exists
		if (obtainedUUID == null) {
			player.sendMessage(Lang.PLAYER_NOT_FOUND.parseObject(specifiedPlayer));
			return true;
		}
		
		// if we get to this point, we at least have a UUID to work with, so we can collect information
		boolean isBanned = Banana.getBanCache().isUUIDBanned(obtainedUUID);
		
		boolean isMuted = Banana.getMuteCache().isMuted(obtainedUUID);
		
		boolean isTempMuted = Banana.getMuteCache().isTempMuted(obtainedUUID);
		String tempMuteRemovalDate = "none";
		
		boolean isTempBanned = Banana.getBanCache().isTempBanned(obtainedUUID);
		String tempBanRemovalDate = "none";
		
		boolean isIPBanned = Banana.getBanCache().isIPBanned(Banana.getPlayerCache().getAddress(obtainedUUID));
		
		boolean isTempIPBanned = Banana.getBanCache().isIPTempBanned(Banana.getPlayerCache().getAddress(obtainedUUID));
		String tempIPBanRemovalDate = "none";
		
		// a nice format for the date
		// TODO: make this a configurable option
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		
		// checking if the player is temp banned, so that way we can set up the unban date
		if (isTempBanned) {
			Date unbanDate = Banana.getBanCache().getTempBanEndDate(obtainedUUID);
			
			// avoiding possible errors
			if (unbanDate != null) {
				tempBanRemovalDate = sdf.format(unbanDate);
			}
		}
		
		// checking if the player is IP temp banned, so that way we can set the unban date
		if (isTempIPBanned) {
			Date unbanDate = Banana.getBanCache().getIPTempBanEndDate(Banana.getPlayerCache().getAddress(obtainedUUID));
			
			// avoiding possible errors
			if (unbanDate != null) {
				tempIPBanRemovalDate = sdf.format(unbanDate);
			}
		}
		
		// checking if the player is temp muted, so that way we can set up the unmute date
		if (isTempMuted) {
			Date unmuteDate = Banana.getMuteCache().getTempMuteRemovalDate(obtainedUUID);
			
			// avoiding possible errors
			if (unmuteDate != null) {
				tempMuteRemovalDate = sdf.format(unmuteDate);
			}
		}
		
		// now that we have the information collected, we can post it all to the player in one big message!
		// that message will be made up of several messages, however
		
		player.sendMessage(Lang.BAN_INFO_HEADER.toString());
		player.sendMessage(Lang.BAN_INFO_LINE_BAN.parseObject(Boolean.toString(isBanned)));
		player.sendMessage(Lang.BAN_INFO_LINE_MUTE.parseObject(Boolean.toString(isMuted)));
		player.sendMessage(Lang.BAN_INFO_LINE_TEMP_MUTE.parseObject(Boolean.toString(isTempMuted)));
		player.sendMessage(Lang.BAN_INFO_LINE_MUTE_REMOVAL.parseObject(tempMuteRemovalDate));
		player.sendMessage(Lang.BAN_INFO_LINE_TEMP_BAN.parseObject(Boolean.toString(isTempBanned)));
		player.sendMessage(Lang.BAN_INFO_LINE_BAN_REMOVAL.parseObject(tempBanRemovalDate));
		player.sendMessage(Lang.BAN_INFO_LINE_IP_BAN.parseObject(Boolean.toString(isIPBanned)));
		player.sendMessage(Lang.BAN_INFO_LINE_TEMP_IP_BAN.parseObject(Boolean.toString(isTempIPBanned)));
		player.sendMessage(Lang.BAN_INFO_LINE_IP_BAN_REMOVAL.parseObject(tempIPBanRemovalDate));
		player.sendMessage(Lang.BAN_INFO_LINE_PLAYER_IP.parseObject(Banana.getPlayerCache().getAddress(obtainedUUID)));
		
		return true;
	}

}
