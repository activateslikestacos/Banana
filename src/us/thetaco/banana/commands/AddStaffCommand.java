package us.thetaco.banana.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.Lang;
import us.thetaco.banana.utils.OfflineCallback;

public class AddStaffCommand implements CommandExecutor, OfflineCallback {

	// Used by the async thread to respond to sender
	private CommandSender sender;
	private Banana plugin;
	
	public AddStaffCommand(Banana plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		boolean isConsole = !(sender instanceof Player);
		
		// run this if the sender is a player
		
		if (!isConsole) {
			
			if (!((Player)sender).hasPermission("banana.commands.addstaff")) {
				sender.sendMessage(Lang.NO_PERMISSIONS.toString());
				return true;
			}

		}
		
		if (args.length < 1) {
			sender.sendMessage(Lang.ADD_STAFF_WRONG_ARGS.toString());
			return true;
		}
		
		this.sender = sender;
		
		Player target = Bukkit.getPlayer(args[0]);
		
		String uuid = null;
		
		if (target == null) {
			
			// If target is not online, check if in player cache
			
			uuid = Banana.getPlayerCache().getUUIDByLatestName(args[0]);
			
			if (uuid == null) {
				
				// If the UUID was null, we need to fetch it from mojang.. Do that here
				this.plugin.getOfflineUUIDHandler().addOfflinePlayer(args[0].toLowerCase(), this);
				
				return true;
				
			} 
			
		} else {
			
			uuid = target.getUniqueId().toString();
			
		}
		
		// If we get down here, we already have the info we need to add them as staff
		this.addStaff(uuid, args[0].toLowerCase());
		
		return true;
	}

	private void addStaff(String uuid, String playerName) {
		
		if (Banana.getPlayerCache().isStaff(uuid)) {
			sender.sendMessage(Lang.ALREADY_STAFF.parseName(playerName));
			return;
		}
		
		Banana.getPlayerCache().addStaff(uuid);
		Banana.getDatabaseManager().asyncAddStaffMember(uuid);
		
		sender.sendMessage(Lang.ADD_STAFF_SUCCESS.parseName(playerName));
		
	}
	
	/** Used for offline uuid fetching. The async thread will call this function once it has finished querying mojang
	 * 
	 * @param uuid The uuid that was requested
	 * @param playerName The name of the player attached to the UUID
	 */
	public synchronized void fetchedName(String uuid, String playerName) {

		if (uuid == null) {
			sender.sendMessage(Lang.PLAYER_NEVER_ONLINE.parseObject(playerName));
			return;
		}
		
		this.addStaff(uuid, playerName);
		
	}

}
