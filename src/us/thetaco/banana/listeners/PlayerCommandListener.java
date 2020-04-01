package us.thetaco.banana.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.Lang;
import us.thetaco.banana.utils.Values;

public class PlayerCommandListener implements Listener {

	@EventHandler (priority = EventPriority.HIGH)
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		
		// stop running if the event is already cancelled
		if (e.isCancelled()) return;
		
		Player player = e.getPlayer();
		String uuid = player.getUniqueId().toString();
				
		// no need to go any further if the player is not muted
		if (!Banana.getMuteCache().isMuted(uuid)) return;
		
		String[] formattedCommand = e.getMessage().split(" ");
		
		// check to see if the command name matches any of the allowed ones, if it doesn't, cancel the even and message the player
		for (String s : Values.MUTING_COMMANDS) {
			
			if (formattedCommand[0].equalsIgnoreCase("/" + s)) {
				
				// Since the command matches.. we will stop them in their tracks!
				player.sendMessage(Lang.COMMAND_DISALLOWED_RUNNING.toString());
				return;
				
			}
			
		}
		
	}
	
}
