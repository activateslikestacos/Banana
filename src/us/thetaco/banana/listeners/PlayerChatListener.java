package us.thetaco.banana.listeners;

import java.util.Calendar;
import java.util.Date;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.Action;
import us.thetaco.banana.utils.Lang;
import us.thetaco.banana.utils.Values;

public class PlayerChatListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		
		// stop running if the event is already cancelled
		if (e.isCancelled()) return;
		
		Player player = e.getPlayer();
		String uuid = player.getUniqueId().toString();
		
		// stop the method here if the player is muted
		if (!Banana.getMuteCache().isMuted(uuid)) return;
		
		if (!Banana.getMuteCache().isTempMuted(uuid)) {
			
			// if the player is muted, stop the event and don't run any checks
			e.setCancelled(true);
			
			// checking to make sure the config allows the notification to muted players
			if (Values.MUTE_NOTIFY_ON_CHAT) {
				// tell the player that they are muted
				player.sendMessage(Lang.NO_SPEAK_MUTED.toString());
			}
			return;
			
		}
		
		// this will continue to run if the player is temp muted
		
		Calendar currentDate = Calendar.getInstance();
		
		currentDate.setTimeInMillis(System.currentTimeMillis());
		
		Date systemDate = currentDate.getTime();
		Date unMuteDate = Banana.getMuteCache().getTempMuteRemovalDate(uuid);
		
		if (systemDate.equals(unMuteDate) || systemDate.after(unMuteDate)) {
			
			// check to see if this option is enabled in the config
			
			if (Values.NOTIFY_UNMUTE) {
				// this will run if the player will be allowed to chat because their mute time is up
				Action.notifyPlayer(Action.UNMUTE, player, Lang.MUTE_TIME_EXPIRED.toString());
			}
			
			// remove the mutes in the database and cache
			Banana.getDatabaseManager().asyncRemoveMute(uuid);
			Banana.getMuteCache().unMutePlayer(uuid);
			
		} else {
			
			// cancel the event because the player is still muted!
			e.setCancelled(true);
			
			// check to see if this option is enabled
			if (Values.MUTE_NOTIFY_ON_CHAT) {
			
				// tell the player that they are muted
				player.sendMessage(Lang.NO_SPEAK_MUTED.toString());
			
			}
		}
		
	}
	
}
