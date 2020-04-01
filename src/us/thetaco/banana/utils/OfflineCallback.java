package us.thetaco.banana.utils;

/**
 * Used for calling back the requester of a player's offline name
 * @author activates
 *
 */
public interface OfflineCallback {
	
	public void fetchedName(String uuid, String playerName);
	
}