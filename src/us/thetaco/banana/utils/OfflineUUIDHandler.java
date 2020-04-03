package us.thetaco.banana.utils;

import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.nio.charset.Charset;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import us.thetaco.banana.Banana;

/** Used to get offline player UUIDs. Runs in a seperate thread
 * @author activates
 *
 */
public class OfflineUUIDHandler {
	
	// Since the thread is alive during the entire plugin execution, this is a queue of names that
	// need checking
	private Map<String, OfflineCallback> callbackList;
	private Banana plugin;
	
	// Pass over the main plugin file for logging and Bukkit API access
	public OfflineUUIDHandler(Banana plugin) {
		
		this.callbackList = new HashMap<String, OfflineCallback>();
		this.plugin = plugin;
		
	}
	
	/** Used to start the offline player thread
	 * 
	 * @return The thread that was started
	 */
	public Thread createThread() {
		
		Thread uuidThread = new Thread(new OfflineUUIDThread());
		
		return uuidThread;
		
	}
	
	private class OfflineUUIDThread implements Runnable {
		
		@Override
		/**
		 * Used for running the offline player check thread This thread will be kept
		 * alive until it is no longer needed
		 */
		public void run() {
			
			// Keep the thread alive, till it is interrupted, of course
			while (!Thread.interrupted()) {

				// Check to see if there is anything in the queue
				if (callbackList.size() < 1) {
					// sleep for ~1/2 a second, then check again
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						plugin.getLogger().info("UUID thread interrupted");
						return;
					}
				} else {

					for (String s : callbackList.keySet()) {

						// Connect to Mojang and attempt to get username
						String url = "https://api.mojang.com/users/profiles/minecraft/" + s;

						String UUIDJson = new String();

						try {
							UUIDJson = IOUtils.toString(new URL(url), Charset.forName("UTF-8"));
						} catch (MalformedURLException e) {

							plugin.getLogger().info("UUID URL Malformed. Message: " + e.getMessage());
							callbackList.get(s).fetchedName(null, s);
							continue;

						} catch (IOException e) {

							plugin.getLogger().info("I/O Exception fetching UUID from Mojang Services. Message: " + e.getMessage());
							callbackList.get(s).fetchedName(null, s);
							continue;

						}

						// Check to see if we got a valid reponse back
						if (UUIDJson.isEmpty()) {

							// If failed to get a proper response, so the name doesn't exist.. or Mojang is
							// A N G R Y
							callbackList.get(s).fetchedName(null, s);
							continue;

						}

						// IF we get down here, then the fetch was a success, and we can give the data
						// back to the callback function
						JSONObject UUIDObject = null;
						try {
							UUIDObject = (JSONObject) JSONValue.parseWithException(UUIDJson);
						} catch (ParseException e) {
							e.printStackTrace();
							callbackList.get(s).fetchedName(null, s);
							continue;
						}
						
						String noDashUUID = UUIDObject.get("id").toString();
						
						// Prep the UUID by re-adding dashes
						UUID uuid = new UUID(
						        new BigInteger(noDashUUID.substring(0, 16), 16).longValue(),
						        new BigInteger(noDashUUID.substring(16), 16).longValue());
						
						// Post the update to the other servers
						Banana.getDatabaseManager().asyncUpdatePlayer(uuid.toString(), "none", UUIDObject.get("name").toString());
						
						callbackList.get(s).fetchedName(uuid.toString(),
								UUIDObject.get("name").toString());

					}

					// Once it has ran through the list, clear it out
					clearList();

				}

			}

		}

	}
	private synchronized void clearList() {
		
		this.callbackList.clear();
		
	}
	
	public synchronized void addOfflinePlayer(String playerName, OfflineCallback callback) {
		
		this.callbackList.put(playerName, callback);
		
	}
	
}
