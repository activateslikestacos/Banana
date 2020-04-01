package us.thetaco.banana.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public enum Action {

	KICK,
	BAN,
	MUTE,
	TEMPBAN,
	BANIP,
	TEMPBANIP,
	NOTHING,
	TEMPMUTE,
	UNBAN,
	UNMUTE,
	WARNING,
	COMMAND,
	UNBAN_IP;
	
	/** This method is simply to announce the action done by a warning increment. It does not add anything to the message,
	 * but it does make sure that players have the permission to receive that certain broadcast (varies on the type of action e.g. BAN KICK etc.)
	 * @param action
	 * @param message
	 */
	public static void broadcastMessage(Enum<Action> action, String message) {
		
		// loop through all players and make sure they have the required permission to see the particular broadcast
				
		List<String> notifiedPlayers = new ArrayList<String>();
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			
			boolean doesContain = false;
			
			for (String s : notifiedPlayers) {
				if (s.equalsIgnoreCase(p.getName())) {
					doesContain = true;
					break;
				}
			}
			
			if (p.hasPermission("banana.announcements.receive") && !doesContain) {
				
				if (action == Action.KICK) {
					
					if (p.hasPermission("banana.announcements.receive.kick")) {
						p.sendMessage(message);
					}
					
				} else if (action == Action.BAN) {
					
					if (p.hasPermission("banana.announcements.receive.ban")) {
						p.sendMessage(message);
					}
					
				} else if (action == Action.MUTE) {
					
					if (p.hasPermission("banana.announcements.receive.mute")) {
						p.sendMessage(message);
					}
					
				} else if (action == Action.TEMPBAN) {
					
					if (p.hasPermission("banana.announcements.receive.tempban")) {
						p.sendMessage(message);
					}
					
				} else if (action == Action.BANIP) {
					
					if (p.hasPermission("banana.announcements.receive.banip")) {
						p.sendMessage(message);
					}
					
				} else if (action == Action.TEMPBANIP) {
					
					if (p.hasPermission("banana.announcements.receive.tempbanip")) {
						p.sendMessage(message);
					}
					
				} else if (action == Action.TEMPMUTE) {
					
					if (p.hasPermission("banana.announcements.receive.tempmute")) {
						p.sendMessage(message);
					}
					
				} else if (action == Action.UNBAN) {
					
					if (p.hasPermission("banana.announcements.receive.unban")) {
						p.sendMessage(message);
					}
					
				} else if (action == Action.UNMUTE) {
					
					if (p.hasPermission("banana.announcements.receive.unmute")) {
						p.sendMessage(message);
					}
					
				} else if (action == Action.WARNING) {
					
					if (p.hasPermission("banana.announcements.receive.warning")) {
						p.sendMessage(message);
					}
					
				} else if (action == Action.UNBAN_IP) {
					
					if (p.hasPermission("banana.announcements.receive.unbanip")) {
						p.sendMessage(message);
					}
					
				}
				
			}
			
			notifiedPlayers.add(p.getName().toLowerCase());
			
		}
		
	}
	
	/** Used in the process of notifying players.. It's basic job is to check if the player has the required permissions to
	 * see the particular message and send it to them if they do
	 * @param action The action being taken against that player in the case that they need to be notified about it
	 * @param player The player to check for permissions and to send the message
	 * @param message The message to send if everything checks out
	 */
	public static void notifyPlayer(Enum<Action> action, Player player, String message) {
		
		if (action == Action.WARNING) {
			
			if (player.hasPermission("banana.notify.warning")) {
				
				player.sendMessage(message);
				
			}
			
		} else if (action == Action.MUTE) {
			
			if (player.hasPermission("banana.notify.mute")) {
				
				player.sendMessage(message);
				
			}
			
		} else if (action == Action.TEMPMUTE) {
			
			if (player.hasPermission("banana.notify.tempmute")) {
				
				player.sendMessage(message);
				
			}
			
		} else if (action == Action.UNMUTE) {
			
			if (player.hasPermission("banana.notify.unmute")) {
				
				player.sendMessage(message);
				
			}
			
		}
		
	}
	
}
