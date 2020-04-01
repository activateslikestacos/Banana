package us.thetaco.banana.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.Lang;
import us.thetaco.banana.utils.OfflineCallback;

public class AddStaffCommandConsole implements OfflineCallback {

	private Banana plugin;
	public AddStaffCommandConsole(Banana plugin) {
		this.plugin = plugin;
	}
	
	public boolean runAddStaffCommand(CommandSender sender, String[] args) {
		
		if (args.length < 1) {
			sender.sendMessage(Lang.ADD_STAFF_WRONG_ARGS.toString());
			return true;
		}
		
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
		
		this.addStaff(uuid, args[0].toLowerCase());
		
		return true;
		
	}

	/** Used to add staff
	 * 
	 * @param uuid The uuid of the player being added as staff
	 * @param playerName The username attached to the UUID
	 */
	private void addStaff(String uuid, String playerName) {
		
		ConsoleCommandSender sender = this.plugin.getServer().getConsoleSender();
		
		if (Banana.getPlayerCache().isStaff(uuid)) {
			sender.sendMessage(Lang.ALREADY_STAFF.parseName(playerName));
			return;
		}
		
		Banana.getPlayerCache().addStaff(uuid);
		Banana.getDatabaseManager().asyncAddStaffMember(uuid);
		
		sender.sendMessage(Lang.ADD_STAFF_SUCCESS.parseName(playerName));
		
	}
	
	@Override
	/** Callback from UUID Handler thread
	 * 
	 * @param uuid The fetched uuid (null if mojang didn't report back)
	 * @param playerName The name attached to the uuid (with case intact)
	 */
	public synchronized void fetchedName(String uuid, String playerName) {
		
		if (uuid == null) {
			
			this.plugin.getServer().getConsoleSender().sendMessage(Lang.PLAYER_NEVER_ONLINE.parseObject(playerName));
			return;
			
		}
		
		// If the UUID is not null, we need to add them to the database then add them to the staff
		Banana.getDatabaseManager().asyncUpdatePlayer(uuid, "none", playerName);
		this.addStaff(uuid, playerName);
		
	}
	
}
