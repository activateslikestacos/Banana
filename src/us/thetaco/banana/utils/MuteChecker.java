package us.thetaco.banana.utils;

import java.util.Calendar;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import us.thetaco.banana.Banana;

/** This Runnable is specifically for checking if players need to be unmuted or not. If they do not need to be unmuted,
 * do nothing with them
 * @author activates
 *
 */
public class MuteChecker implements Runnable {

	@Override
	public void run() {
		
		// gets all players on the server and checks if they are muted or not
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			
			String uuid = player.getUniqueId().toString();
			
			if (Banana.getMuteCache().isTempMuted(uuid)) {
				
				Calendar currentDate = Calendar.getInstance();
				
				currentDate.setTimeInMillis(System.currentTimeMillis());
				
				Date systemDate = currentDate.getTime();
				Date unMuteDate = Banana.getMuteCache().getTempMuteRemovalDate(uuid);
				
				if (systemDate.equals(unMuteDate) || systemDate.after(unMuteDate)) {
					
					// checking to see if this option is enabled in the config
					if (Values.NOTIFY_UNMUTE) {
					
						// if this runs, unmute the player
						// this will run if the player will be allowed to chat because their mute time is up
						Action.notifyPlayer(Action.UNMUTE, player, ChatColor.GREEN + "You mute time has expired, so you are free to chat again");
						
					}
					// remove the mutes in the database and cache
					Banana.getDatabaseManager().asyncRemoveMute(uuid);
					Banana.getMuteCache().unMutePlayer(uuid);
					
				}
				
			}
			
		}
		
		
		
	}

}
