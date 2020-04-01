package us.thetaco.banana.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MuteCache {

	private List<String> mutedPlayers = new ArrayList<String>();
	private Map<String, Long> tempMutes = new HashMap<String, Long>();
	
	/** Used to add a muted player
	 * @param uuid The uuid of the player that is to be muted
	 * @return Will return true if the player was not already muted
	 */
	public boolean addMutedPlayer(String uuid) {
		
		if (this.isMuted(uuid)) return false;
		
		this.mutedPlayers.add(uuid);
		return true;
		
	}
	
	/** Used to add a muted player
	 * @param uuid The uuid of the player that is to be muted
	 * @return Will return true if the player was not already muted
	 */
	public boolean addMutedPlayer(UUID uuid) {
		
		if (this.isMuted(uuid)) return false;
		
		this.mutedPlayers.add(uuid.toString());
		return true;
		
	}
	
	/** Used to tell if a player is muted or not
	 * @param uuid The uuid of the muted player
	 * @return Will return true if the player is muted
	 */
	public boolean isMuted(String uuid) {
		
		for (String s : mutedPlayers) {
			if (s.equals(uuid)) return true;
		}
		
		return false;
		
	}
	
	/** Used to tell if a player is muted or not
	 * @param uuid The uuid of the muted player
	 * @return Will return true if the player is muted
	 */
	public boolean isMuted(UUID uuid) {
		
		for (String s : mutedPlayers) {
			if (s.equals(uuid.toString())) return true;
		}
		
		return false;
		
	}
	
	/** Used to fetch all of the muted playres
	 * @return A String list of all the muted players
	 */
	public List<String> getMutedPlayers() {
		
		return this.mutedPlayers;
		
	}
	
	/** Used to temporarily mute a player
	 * @param uuid The uuid of the player to temporarily mute
	 * @param unMuteDate The date that the player will be unmuted on
	 */
	public void tempMute(String uuid, Date unMuteDate) {
		
		if (unMuteDate == null) return;
		
		this.addMutedPlayer(uuid);
		
		this.tempMutes.put(uuid, unMuteDate.getTime());
		
	}
	
	/** Used to temporarily mute a player
	 * @param uuid The uuid of the player to temporarily mute
	 * @param unMuteDate The date that the player will be unmuted on
	 */
	public void tempMute(UUID uuid, Date unMuteDate) {
		
		if (unMuteDate == null) return;
		
		this.addMutedPlayer(uuid);
		
		this.tempMutes.put(uuid.toString(), unMuteDate.getTime());
		
	}
	
	/** Used to determine if a particular UUID is temp muted
	 * @param uuid
	 * @return
	 */
	public boolean isTempMuted(String uuid) {
		
		for (String s : this.tempMutes.keySet()) {
			if (s.equals(uuid)) return true;
		}
		
		return false;
		
	}
	
	/** Used to determine if a particular UUID is temp muted
	 * @param uuid
	 * @return
	 */
	public boolean isTempMuted(UUID uuid) {
		
		for (String s : this.tempMutes.keySet()) {
			if (s.equals(uuid.toString())) return true;
		}
		
		return false;
		
	}
	
	/** Used to get the date at which the temporary mute will be removed
	 * @param uuid The to check for
	 * @return Will return the date that the mute will be lifted. If not date is stored, it will return null
	 */
	public Date getTempMuteRemovalDate(String uuid) {
		
		if (!this.isTempMuted(uuid)) return null;
		
		Calendar currentDate = Calendar.getInstance();
		
		currentDate.setTimeInMillis(this.tempMutes.get(uuid));
		
		return currentDate.getTime();
		
	}
	
	/** Used to get the date at which the temporary mute will be removed
	 * @param uuid The to check for
	 * @return Will return the date that the mute will be lifted. If not date is stored, it will return null
	 */
	public Date getTempMuteRemovalDate(UUID uuid) {
		
		if (!this.isTempMuted(uuid)) return null;
		
		Calendar currentDate = Calendar.getInstance();
		
		currentDate.setTimeInMillis(this.tempMutes.get(uuid.toString()));
		
		return currentDate.getTime();
		
	}
	
	/** Unmutes a player
	 * @param uuid The uuid of the player to unmute
	 */
	public void unMutePlayer(String uuid) {
		
		this.mutedPlayers.remove(uuid);
		this.tempMutes.remove(uuid);
		
	}
	
	/** Unmutes a player
	 * @param uuid The uuid of the player to unmute
	 */
	public void unMutePlayer(UUID uuid) {
		
		this.mutedPlayers.remove(uuid.toString());
		this.tempMutes.remove(uuid.toString());
		
	}
	
}
