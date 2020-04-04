package us.thetaco.banana.sql;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.CommandType;
import us.thetaco.banana.utils.Lang;
import us.thetaco.banana.utils.Values;

/** A class for handling all to do with the SQL-lite database. MySQL is a possibility, but for now
 * SQL-lite is the best option
 * @author activates
 *
 */
public class DatabaseManager {

	/** The different types of banners for storing/retrieving in the database
	 * @author activates
	 *
	 */
	public enum BannerType {
		CONSOLE,
		AUTOBANNED,
		PLAYER;
	}
	
	/** The different types of updates for storing/retrieving in the database
	 * @author activates
	 *
	 */
	public enum UpdateType {
		WARN,
		DECREMENT_WARN,
		BAN,
		MUTE,
		BAN_IP,
		UN_MUTE,
		UN_BAN,
		UN_BAN_IP,
		PURGE_WARNINGS,
		ADD_STAFF,
		RM_STAFF,
		KICK_ALL,
		PLAYER;
	}
	
	private CacheUpdater cacheUpdater;
	private Banana plugin;
	public DatabaseManager(Banana plugin) {
		this.plugin = plugin;
		this.cacheUpdater = new CacheUpdater();
	}
	
	private Map<Integer, String> queuedEntries = new HashMap<Integer, String>();
	private static boolean UPDATE = true;
	
	/** Used to set if the database loop runs. If set to false, the loop will remain stopped until manually started again
	 * @param isUpdating
	 */
	public static void setUpdating(boolean isUpdating) {
		
		DatabaseManager.UPDATE = isUpdating;
		
	}
	
	/** Used to tell if the database is set to loop (this doesn't necessarily mean it's looping, though)
	 * @return
	 */
	public static boolean isUpating() {
		
		return DatabaseManager.UPDATE;
		
	}
	
	/** Used to start the update loop...
	 * 
	 */
	public Thread startLoop() {
		
		DatabaseManager.setUpdating(true);
		
		Thread workerThread = new Thread(new Worker(plugin.getDataFolder()));
		
		workerThread.start();
		
		return workerThread;
		
	}
	
	/** Used to start the player updater thread!
	 * 
	 */
	public Thread startPlayerThread() {
		
		Thread workerThread = new Thread(new PlayerUpdater());
		
		workerThread.start();
		
		return workerThread;
		
	}
	
	/** Adds an entry to be updating upon the next loop
	 * @param sql The string sql to be updated
	 */
	public void queueEntry(String sql) {
		
		Integer entryNumber = this.queuedEntries.size();
		
		while (this.queuedEntries.containsKey(entryNumber)) entryNumber++;
		
		this.queuedEntries.put(entryNumber, sql);
		
	}
	
	/** A class for executing database updates
	 * @author activates
	 *
	 */
	private class Worker implements Runnable {

		private File dataFolder;
		public Worker(File dataFolder) {
			this.dataFolder = dataFolder;
		}
		
		@Override
		public void run() {
			
			Connection connection;
			List<Integer> ranItems;
			
			while (DatabaseManager.UPDATE && !Thread.interrupted()) {
			
				// update database entries here
				if (queuedEntries.size() > 0) {
				
					try {
						
						Class.forName("org.sqlite.JDBC");
						
						// open a connection
						
						ranItems = new ArrayList<Integer>();
						
						boolean noException = false;
						
						while (noException == false) {
							
							// try recreating the connection
							
							// Check if MySQL is requested and use it if it's enabled
							if (Values.USE_MYSQL) {
								
								// connect to remote database here				
								
								connection = DriverManager.getConnection("jdbc:mysql://" + Values.MYSQL_ADDRESS + ":" + Values.MYSQL_PORT + "/" + Values.MYSQL_DATABASE_NAME, Values.MYSQL_DATABASE_USERNAME, Values.MYSQL_DATABASE_PASSWORD);
								
							} else {
								
								// just create a the sqlite connection here
								
								// open a connection
								connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder.getAbsolutePath() + "/Banana.db");
								
							}
						
							// turn the auto-commit off
							connection.setAutoCommit(false);
							
							Statement statement = connection.createStatement();
							
							try {
						
								for (Integer i : queuedEntries.keySet()) {
								
									statement.executeUpdate(queuedEntries.get(i));
									ranItems.add(i);
								
								}
								
								// change the no exception to true so the loop stops
								noException = true;
								
								// commit the changes and close the connection
								connection.commit();
								connection.close();
								
							} catch (ConcurrentModificationException e) {
								// sleep the thread for half a second, then try again
								// TODO: log messsage saying the queue was busy, trying again
								noException = false;
								try {
									Thread.sleep(500L);
									connection.close();
								} catch (InterruptedException e1) {}
								
							}
						
						}
						
						// clearing the list of ran items
						for (Integer i : ranItems) {
							
							queuedEntries.remove(i);
							
						}
						
					} catch (SQLException | ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
				
				try {
					// sleep the thread for 2 seconds, as it will be looping
					Thread.sleep(2000L);
				} catch (InterruptedException e) {}
				
				// Check if MySQL is being used
				if (Values.USE_MYSQL) {
					// if mysql is being used, then we check for updates
					this.checkDatabaseUpdates();
				}
				
			}
			
		}
		
		private class UpdateShell {
			
			private int updateID;
			private String updateType;
			private String updateData;
			private String updateSender;
			private String currentNames;
			
			public UpdateShell(int updateID, String updateType, String updateData, String updateSender, String currentNames) {
				this.updateID = updateID;
				this.updateType = updateType;
				this.updateData = updateData;
				this.updateSender = updateSender;
				this.currentNames = currentNames;
			}
			
			public int getUpdateID() {
				return updateID;
			}
			
			public UpdateType getUpdateType() {
				return UpdateType.valueOf(updateType);
			}
			
			public String getUpdateData() {
				return updateData;
			}
			
			public String getUpdateSender() {
				return updateSender;
			}
			
			public String getCurrentNames() {
				return currentNames;
			}
			
		}
		
		/** Checks if there are any updates that needs to be made for the cache
		 * 
		 */
		private void checkDatabaseUpdates() {
			
			// since this will only run if use mysql is enabled, we will only attempt to connect
			// to a remote mysql server
			
			try {
				
				Connection connection = DriverManager.getConnection("jdbc:mysql://" + Values.MYSQL_ADDRESS + ":" + Values.MYSQL_PORT + "/" + Values.MYSQL_DATABASE_NAME, Values.MYSQL_DATABASE_USERNAME, Values.MYSQL_DATABASE_PASSWORD);
				Statement statement = connection.createStatement();
				
				// turning off autocommit :D
				connection.setAutoCommit(false);
				
				String sql = "SELECT * FROM updates";
				
				ResultSet rs = statement.executeQuery(sql);
				
				// make a map to store what IDs are going to be updated, and what to set the names as
				Map<Integer, String> ranUpdates = new HashMap<Integer, String>();
				
				// A list for holding collected updates
				List<UpdateShell> updates = new ArrayList<UpdateShell>();
				
				// loop through and start checking!
				while (rs.next()) {
					
					// start by getting the current names
					String currentNames = rs.getString("executed");
					String[] names = currentNames.split(" ");
					
					// check to see if this server has run this command yet.
					boolean contains = false;
					
					for (String s : names) {
						if (s.equalsIgnoreCase(Values.SERVER_NAME)) {
							contains = true;
							break;
						}
					}
					
					if (contains == false) {
						
						// Collect update data for later use
						updates.add(new UpdateShell(rs.getInt("ID"), rs.getString("type"), rs.getString("data"), rs.getString("published"), currentNames));

					}
										
				}
				
				// close the previous statement
				statement.close();
				
				// Check if this contains any player updates
				List<UpdateShell> shellsToRemove = new ArrayList<UpdateShell>();
				for (UpdateShell u : updates) {
					
					if (u.getUpdateType() == UpdateType.PLAYER)
						shellsToRemove.add(u);
					
				}
				
				// Check to see if there's any player updates, and run them first (ensuring to update the ranUpdates map)
				if (shellsToRemove.size() > 0) {
					
					for (UpdateShell u : shellsToRemove) {
						
						cacheUpdater.handleUpdate(u.getUpdateType(), u.getUpdateData(), u.getUpdateSender());
						ranUpdates.put(u.getUpdateID(), u.getCurrentNames() + " " + Values.SERVER_NAME);
						
						// Remove the update that has been applied
						updates.remove(u);
						
					}
					
				}
				
				// Clear out the update shells that were player priority
				shellsToRemove.clear();
				
				// Now loop through and finish the rest (if there are any)
				
				for (UpdateShell u : updates) {
					
					cacheUpdater.handleUpdate(u.getUpdateType(), u.getUpdateData(), u.getUpdateSender());
					ranUpdates.put(u.getUpdateID(), u.getCurrentNames() + " " + Values.SERVER_NAME);
					
				}
				
				// handle updating the records in the database here
				// start by looping through the list we made above and removing anything necessary
				for (int id : ranUpdates.keySet()) {
					
					String updateSQL = "UPDATE updates set executed='" + ranUpdates.get(id) + "' WHERE ID=" + id;
					
					// creating a fresh statement
					statement = connection.createStatement();
					
					statement.executeUpdate(updateSQL);
					
					statement.close();
					
				}
				
				// commit everything before closing
				connection.commit();
				
				// now it's time for cleanup! We'll add 1 minute to the date, then check if the current date is still on or after that, then remove it
				statement = connection.createStatement();
				
				sql = "SELECT * FROM updates";
				
				rs = statement.executeQuery(sql);
				
				List<Integer> idsToDelete = new ArrayList<Integer>();
				
				while (rs.next()) {
														
					long dateLong = Long.parseLong(rs.getString("date"));
					
					Calendar cal = Calendar.getInstance();
					
					cal.setTime(new Date(dateLong));
					
					cal.add(Calendar.MINUTE, 1);
					
					Calendar currentDate = Calendar.getInstance();
					
					if (cal.equals(currentDate) || cal.before(currentDate)) {
												
						// add this to the list to prune off
						idsToDelete.add(rs.getInt("ID"));
						
					}
					
				}
				
				// close the statement
				statement.close();

				// now we go through and delete all the ids listed above
				for (int i : idsToDelete) {
										
					sql = "DELETE FROM updates WHERE ID=" + i;
					
					statement = connection.createStatement();
					
					statement.executeUpdate(sql);
					
					statement.close();
					
				}
				
				// commit the changes
				connection.commit();
				
				// once we've cleaned it up, we can close the connection
				connection.close();
				
			} catch (SQLException e) {
				// just print the stack trace if it can't update
				e.printStackTrace();
			}
			
		}
		
	}
	
	/** Used to get the database ready for work!
	 * @return Will return true if the database was sucessfully populated!
	 */
	public boolean populateDatabase() {
		
		try {
			
			// work around for the awkward implementation
			Class.forName("org.sqlite.JDBC");
			
			Connection connection;
			
			// Check if MySQL is requested and use it if it's enabled
			if (Values.USE_MYSQL) {
				
				// connect to remote database here				
				
				connection = DriverManager.getConnection("jdbc:mysql://" + Values.MYSQL_ADDRESS + ":" + Values.MYSQL_PORT + "/" + Values.MYSQL_DATABASE_NAME, Values.MYSQL_DATABASE_USERNAME, Values.MYSQL_DATABASE_PASSWORD);
				
			} else {
				
				// just create a the sqlite connection here
				
				// open a connection
				connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().getPath() + "/Banana.db");
				
			}
		
			// turn the auto-commit off
			connection.setAutoCommit(false);
			
			/*
			 * UNLEASE ALL THE INFORMATION!
			 */
			
			
			if (!Values.USE_MYSQL) {
			
				String sql = "CREATE TABLE IF NOT EXISTS bans"
						+ "("
						+ "ID Integer PRIMARY KEY,"
						+ "UUID varchar(200),"
						+ "tempBanned bit,"
						+ "banner varchar(200),"
						+ "type varchar(20),"
						+ "message varchar(1000),"
						+ "tempBanDate varchar(50),"
						+ "pureDate varchar(100)"
						+ ")";
					
				Statement statement = connection.createStatement();
					
				statement.executeUpdate(sql);
				statement.close();
				
				sql = "CREATE TABLE IF NOT EXISTS warns"
						+ "("
						+ "ID Integer PRIMARY KEY,"
						+ "UUID varchar(200),"
						+ "warner varchar(200),"
						+ "message varchar(1000),"
						+ "date varchar(50)"
						+ ")";
					
				statement = connection.createStatement();
				
				statement.executeUpdate(sql);
				statement.close();
				
				sql = "CREATE TABLE IF NOT EXISTS players"
						+ "("
						+ "ID Integer PRIMARY KEY,"
						+ "UUID varchar(200),"
						+ "address varchar(100),"
						+ "warnings int,"
						+ "playername varchar(50)"
						+ ")";
					
				statement = connection.createStatement();
				
				statement.executeUpdate(sql);
				statement.close();
				
				sql = "CREATE TABLE IF NOT EXISTS ipbans"
						+ "("
						+ "ID Integer PRIMARY KEY,"
						+ "ip varchar(100),"
						+ "banner varchar(200),"
						+ "type varchar(20),"
						+ "tempBanned bit,"
						+ "message varchar(1000),"
						+ "tempBanDate varchar(50),"
						+ "pureDate varchar(100)"
						+ ")";
					
				statement = connection.createStatement();
				
				statement.executeUpdate(sql);
				statement.close();
				
				sql = "CREATE TABLE IF NOT EXISTS logs"
						+ "("
						+ "ID Integer PRIMARY KEY,"
						+ "command varchar(50),"
						+ "sender varchar(1000),"
						+ "isConsole bit,"
						+ "args varchar(1000),"
						+ "date varchar(50)"
						+ ")";
				
				statement = connection.createStatement();
				
				statement.executeUpdate(sql);
				statement.close();
				
				sql = "CREATE TABLE IF NOT EXISTS mutes"
						+ "("
						+ "ID Integer PRIMARY KEY,"
						+ "UUID varchar(50),"
						+ "tempMuted bit,"
						+ "tempMuteDate varchar(50),"
						+ "reason varchar(1000),"
						+ "date varchar(50)"
						+ ")";
					
				statement = connection.createStatement();
				
				statement.executeUpdate(sql);
				statement.close();
				
				sql = "CREATE TABLE IF NOT EXISTS staff"
						+ "("
						+ "ID Integer PRIMARY KEY,"
						+ "UUID varchar(50)"
						+ ")";
					
				statement = connection.createStatement();
				
				statement.executeUpdate(sql);
				statement.close();
				
			} else {
				
				String sql = "CREATE TABLE IF NOT EXISTS bans"
						+ "("
						+ "ID Integer NOT NULL AUTO_INCREMENT,"
						+ "UUID varchar(200),"
						+ "tempBanned bit,"
						+ "banner varchar(200),"
						+ "type varchar(20),"
						+ "message varchar(1000),"
						+ "tempBanDate varchar(50),"
						+ "pureDate varchar(100),"
						+ "PRIMARY KEY (id))";
					
				Statement statement = connection.createStatement();
					
				statement.executeUpdate(sql);
				statement.close();
				
				sql = "CREATE TABLE IF NOT EXISTS warns"
						+ "("
						+ "ID Integer NOT NULL AUTO_INCREMENT,"
						+ "UUID varchar(200),"
						+ "warner varchar(200),"
						+ "message varchar(1000),"
						+ "date varchar(50),"
						+ "PRIMARY KEY (id))";
					
				statement = connection.createStatement();
				
				statement.executeUpdate(sql);
				statement.close();
				
				sql = "CREATE TABLE IF NOT EXISTS players"
						+ "("
						+ "ID Integer NOT NULL AUTO_INCREMENT,"
						+ "UUID varchar(200),"
						+ "address varchar(100),"
						+ "warnings int,"
						+ "playername varchar(50),"
						+ "PRIMARY KEY (id))";
					
				statement = connection.createStatement();
				
				statement.executeUpdate(sql);
				statement.close();
				
				sql = "CREATE TABLE IF NOT EXISTS ipbans"
						+ "("
						+ "ID Integer NOT NULL AUTO_INCREMENT,"
						+ "ip varchar(100),"
						+ "banner varchar(200),"
						+ "type varchar(20),"
						+ "tempBanned bit,"
						+ "message varchar(1000),"
						+ "tempBanDate varchar(50),"
						+ "pureDate varchar(100),"
						+ "PRIMARY KEY (id))";
					
				statement = connection.createStatement();
				
				statement.executeUpdate(sql);
				statement.close();
				
				sql = "CREATE TABLE IF NOT EXISTS logs"
						+ "("
						+ "ID Integer NOT NULL AUTO_INCREMENT,"
						+ "command varchar(50),"
						+ "sender varchar(1000),"
						+ "isConsole bit,"
						+ "args varchar(1000),"
						+ "date varchar(50),"
						+ "PRIMARY KEY (id))";
				
				statement = connection.createStatement();
				
				statement.executeUpdate(sql);
				statement.close();
				
				sql = "CREATE TABLE IF NOT EXISTS mutes"
						+ "("
						+ "ID Integer NOT NULL AUTO_INCREMENT,"
						+ "UUID varchar(50),"
						+ "tempMuted bit,"
						+ "tempMuteDate varchar(50),"
						+ "reason varchar(1000),"
						+ "date varchar(50),"
						+ "PRIMARY KEY (id))";
					
				statement = connection.createStatement();
				
				statement.executeUpdate(sql);
				statement.close();
				
				sql = "CREATE TABLE IF NOT EXISTS staff"
						+ "("
						+ "ID Integer NOT NULL AUTO_INCREMENT,"
						+ "UUID varchar(50),"
						+ "PRIMARY KEY (id))";
					
				statement = connection.createStatement();
				
				statement.executeUpdate(sql);
				statement.close();
				
				sql = "CREATE TABLE IF NOT EXISTS updates"
						+ "("
						+ "ID Integer NOT NULL AUTO_INCREMENT,"
						+ "type varchar(20),"
						+ "data varchar(1000),"
						+ "date varchar(50),"
						+ "executed varchar(200),"
						+ "published varchar(50),"
						+ "PRIMARY KEY (ID))";
					
				statement = connection.createStatement();
				
				statement.executeUpdate(sql);
				statement.close();
				
			}
			
			connection.commit();
			connection.close();
			
			return true;
			
		} catch (SQLException | ClassNotFoundException e) {
			
			// there was an error of some sort.. so return false
			e.printStackTrace();
			return false;
		}
	}
	
	/** Used to load all of the stored information into the active cache (should only be used once!)
	 * Done in a sync thread.
	 * @return Will return true if the operation was sucessful
	 */
	public boolean syncLoadValues() {
		
		try {
			
			Class.forName("org.sqlite.JDBC");
			
			// OH YEH CONNECTION TEIM
			Connection connection;
			
			// Check if MySQL is requested and use it if it's enabled
			if (Values.USE_MYSQL) {
				
				// connect to remote database here				
				
				connection = DriverManager.getConnection("jdbc:mysql://" + Values.MYSQL_ADDRESS + ":" + Values.MYSQL_PORT + "/" + Values.MYSQL_DATABASE_NAME, Values.MYSQL_DATABASE_USERNAME, Values.MYSQL_DATABASE_PASSWORD);
				
			} else {
				
				// just create a the sqlite connection here
				
				// open a connection
				connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + "/Banana.db");
				
			}
		
			// turn the auto-commit off
			connection.setAutoCommit(false);
			
			String sql = "SELECT * FROM bans";
			Statement statement = connection.createStatement();
			
			ResultSet rs = statement.executeQuery(sql);
			
			while (rs.next()) {
				
				byte isTempBanned = rs.getByte("tempBanned");
				Enum<BannerType> type = BannerType.valueOf(rs.getString("type"));
				
				String bannerUUID = null;
				
				if (type == BannerType.PLAYER) {
					
					bannerUUID = rs.getString("banner");
					
				}
				
				if (isTempBanned == 0) {
					
					Banana.getBanCache().addBannedUUID(rs.getString("UUID"), rs.getString("message").replaceAll("&#00", "'"), type, bannerUUID);
					
				} else {
					
					Calendar unbanDate = Calendar.getInstance();
					unbanDate.setTimeInMillis(Long.parseLong(rs.getString("tempBanDate")));
										
					String unparsedDate = rs.getString("pureDate");
					
					Banana.getBanCache().addTempBannedUUID(rs.getString("UUID"), rs.getString("message").replaceAll("&#00", "'"), unbanDate.getTime(), type, bannerUUID, unparsedDate);
					
				}
				
			}
			
			rs.close();
			statement.close();
			
			statement = connection.createStatement();
			
			sql = "SELECT * FROM players";
			
			rs = statement.executeQuery(sql);
			
			while (rs.next()) {
				
				Banana.getPlayerCache().addAddress(rs.getString("UUID"), rs.getString("address"));
				Banana.getPlayerCache().addLatestName(rs.getString("UUID"), rs.getString("playername"));
			}
			
			rs.close();
			statement.close();
			
			statement = connection.createStatement();
			
			sql = "SELECT * FROM warns";
			
			rs = statement.executeQuery(sql);
			
			while (rs.next()) {
				
				Banana.getWarnCache().addWarning(rs.getString("UUID"), rs.getString("message").replaceAll("&#00", "'"));
				
			}
			
			rs.close();
			statement.close();
			
			statement = connection.createStatement();
			
			sql = "SELECT * FROM ipbans";
			
			rs = statement.executeQuery(sql);
			
			while (rs.next()) {
				
				byte isTempBanned = rs.getByte("tempBanned");
				Enum<BannerType> type = BannerType.valueOf(rs.getString("type"));
				
				String bannerUUID = null;
				
				if (type == BannerType.PLAYER) {
					
					bannerUUID = rs.getString("banner");
					
				}
				
				
				if (isTempBanned == 0) {
					
					Banana.getBanCache().addBannedIP(rs.getString("ip"), rs.getString("message").replaceAll("&#00", "'"), type, bannerUUID);
					
				} else {
					
					Calendar unbanDate = Calendar.getInstance();
					unbanDate.setTimeInMillis(Long.parseLong(rs.getString("tempBanDate")));
							
					String unparsedDate = rs.getString("pureDate");
					
					Banana.getBanCache().tempBanIP(rs.getString("ip"), rs.getString("message").replaceAll("&#00", "'"), unbanDate.getTime(), type, bannerUUID, unparsedDate);
					
				}
								
			}
			
			rs.close();
			statement.close();
			
			statement = connection.createStatement();
			
			sql = "SELECT * FROM mutes";
			
			rs = statement.executeQuery(sql);
			
			while (rs.next()) {
				
				byte isTempBanned = rs.getByte("tempMuted");
				
				if (isTempBanned == 0) {
					
					Banana.getMuteCache().addMutedPlayer(rs.getString("UUID"));
					
				} else {
					
					Calendar unbanDate = Calendar.getInstance();
					unbanDate.setTimeInMillis(Long.parseLong(rs.getString("tempMuteDate")));
										
					Banana.getMuteCache().tempMute(rs.getString("UUID"), unbanDate.getTime());
					
				}
				
			}
			
			rs.close();
			statement.close();
			
			statement = connection.createStatement();
			
			sql = "SELECT * FROM staff";
			
			rs = statement.executeQuery(sql);
			
			while (rs.next()) {
					
				Banana.getPlayerCache().addStaff(rs.getString("UUID"));
				
			}
			
			rs.close();
			statement.close();
			
			connection.close();
			
			return true;
			
		} catch (SQLException | ClassNotFoundException e) {
			
			e.printStackTrace();
			return false;
			
		}
		
	}
	
	/** Used to add a player who has been banned. This will be added to a queue which will be ran once it's ready
	 * @param uuid The uuid to ban
	 * @param message The message to ban! Cannot be null, must be some value
	 * @param tempBanned If the player is being tempbanned or not
	 * @param tempBanDate If the player is being tempBanned, a long version of the date (in milliseconds) must be provided
	 */
	public void asyncAddBan(String uuid, Enum<BannerType> type, String bannerUUID, String message, boolean tempBanned, Long tempBanDate, String unparsedDate) {
		
		if (uuid == null) return;
		
		byte isTempBanned = 0;
		
		if (tempBanned) {
			isTempBanned = 1;
		}
		
		Long newTempBanDate = tempBanDate;
		
		if (tempBanDate == null) {
			newTempBanDate = 0L;
		}
		
		String newMessage = null;
		
		if (message == null) {
			newMessage = Lang.DEFAULT_BAN_MESSAGE.toString();
			
		} else {
			
			newMessage = message;
			
		}
		
		newMessage = newMessage.replaceAll("'", "&#00");
		
		String preparingStatement = "INSERT INTO bans (UUID, tempBanned, message, tempBanDate, type, banner, pureDate) VALUES ('" + uuid + "', " + isTempBanned + ", '" + newMessage + "', '" + newTempBanDate + "', '" + type.toString() + "', '" + bannerUUID + "', '" + unparsedDate + "')";
				
		this.queueEntry(preparingStatement);
		
		// check if mysql is being used
		if (Values.USE_MYSQL) {
			// if mysql is being used, make sure to post to the update row, so that way the other servers
			// know something has been changed, too. Put the server's name in, too, since it's already updated
						
			preparingStatement = "INSERT INTO updates (type, data, date, executed, published) VALUES ('" + UpdateType.BAN + "', '" + uuid + "{-S-P-L-I-T-}" + type + "{-S-P-L-I-T-}" + bannerUUID + "{-S-P-L-I-T-}" + newMessage + "{-S-P-L-I-T-}" + tempBanned + "{-S-P-L-I-T-}" + tempBanDate + "{-S-P-L-I-T-}" + unparsedDate + "', '" + System.currentTimeMillis() + "', '" + Values.SERVER_NAME + "', '" + Values.SERVER_NAME + "')";
					
			// upload that to the cache, too
			this.queueEntry(preparingStatement);
				
		}
		
	}

	/** Used to delete a ban in the database. It will be added to a queue which will be ran once it's ready
	 * @param uuid The uuid to unban
	 */
	public void asyncRemoveBan(String uuid) {
		
		String preparingStatement = "DELETE FROM bans WHERE UUID='" + uuid + "'";
		
		this.queueEntry(preparingStatement);
		
		// check if mysql is being used
		if (Values.USE_MYSQL) {
			// if mysql is being used, make sure to post to the update row, so that way the other servers
			// know something has been changed, too. Put the server's name in, too, since it's already updated
			preparingStatement = "INSERT INTO updates (type, data, date, executed, published) VALUES ('" + UpdateType.UN_BAN + "', '" + uuid + "', '" + System.currentTimeMillis() + "', '" + Values.SERVER_NAME + "', '" + Values.SERVER_NAME + "')";
			
			// upload that to the cache, too
			this.queueEntry(preparingStatement);
		
		}
		
	}
	
	/** Used to add a warning to the database, it will be 
	 * @param uuid The uuid to add to the string
	 * @param warnMessage The message that went along with the warning!
	 */
	public void asyncAddWarning(String uuid, String warnMessage, String warner) {
		
		if (uuid == null) return;
		
		String newWarnMessage = warnMessage;
		
		if (newWarnMessage != null) {
			newWarnMessage = warnMessage.replaceAll("'", "&#00");
		}
		
		String newWarner = warner;
		
		if (newWarner != null) {
			newWarner = warner.replaceAll("'", "&#00");
		}
		
		String preparingStatement = "INSERT INTO warns (UUID, warner, message, date) VALUES ('" + uuid + "', '" + newWarner + "', '" + newWarnMessage + "', '" + (new Date()).getTime() + "')";
		
		this.queueEntry(preparingStatement);
		
		// check if mysql is being used
		if (Values.USE_MYSQL) {
			// if mysql is being used, make sure to post to the update row, so that way the other servers
			// know something has been changed, too. Put the server's name in, too, since it's already updated
			preparingStatement = "INSERT INTO updates (type, data, date, executed, published) VALUES ('" + UpdateType.WARN + "', '" + uuid + "{-S-P-L-I-T-}" + newWarner + "{-S-P-L-I-T-}" + newWarnMessage + "', '" + System.currentTimeMillis() + "', '" + Values.SERVER_NAME + "', '" + Values.SERVER_NAME + "')";
			
			// upload that to the cache, too
			this.queueEntry(preparingStatement);
			
		}
		
	}
	
	/** Used to delete a warning in the database
	 * @param warningID The row ID to delete
	 */
	public void asyncDeleteWarning(int warningID) {
		
		String preparingStatement = "DELETE FROM warns WHERE ID=" + warningID;
		
		this.queueEntry(preparingStatement);
		
		// no need to send an update here since this is purely for the database. The decrement warning method is the one we need
		// to use
		
	}
	
	/** List the warnings to the player who is specified
	 * @param sender The entity to send the messages to
	 * @param uuid The uuid to fetch the warnings from
	 */
	public void asyncListWarnings(CommandSender sender, String uuid) {
		
		sender.sendMessage(ChatColor.AQUA + "Searching database.. Please Wait");
		
		// passing them to the new thread
		// TODO: put this in thread with many others!
		Thread worker = new Thread(new DatabaseReader(plugin, sender, uuid));
		
		worker.start();
		
	}
	
	
	/** Caches all of the information to be later printed out to the player
	 * @author activates
	 *
	 */
	private class DatabaseReader implements Runnable {

		private CommandSender sender;
		private String uuid;
		public DatabaseReader(Banana plugin, CommandSender sender, String uuid) {
			
			this.sender = sender;
			this.uuid = uuid;
			
		}
		
		@Override
		public void run() {

			try {
				
				Class.forName("org.sqlite.JDBC");
				
				// creating the connection for later use
				Connection connection;
				
				// Check if MySQL is requested and use it if it's enabled
				if (Values.USE_MYSQL) {
					
					// connect to remote database here				
					
					connection = DriverManager.getConnection("jdbc:mysql://" + Values.MYSQL_ADDRESS + ":" + Values.MYSQL_PORT + "/" + Values.MYSQL_DATABASE_NAME, Values.MYSQL_DATABASE_USERNAME, Values.MYSQL_DATABASE_PASSWORD);
					
				} else {
					
					// just create a the sqlite connection here
					
					// open a connection
					connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + "/Banana.db");
					
				}
				
				// turning off autocommit
				connection.setAutoCommit(false);
				
				String sql = "SELECT * FROM warns WHERE UUID='" + uuid + "'";
				
				Statement statement = connection.createStatement();
				
				ResultSet rs = statement.executeQuery(sql);
				
				Map<Integer, String> storedWarnings = new HashMap<Integer, String>();
				
				while (rs.next()) {
					
					storedWarnings.put(rs.getInt("ID"), rs.getString("message"));
					
				}
				
				// close up the database and send over the info to the sync bukkit scheduler
				statement.close();
				connection.close();
				
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new SyncRunner(storedWarnings, sender, uuid));
				
			} catch (SQLException | ClassNotFoundException e) {
				
				e.printStackTrace();
				
			}
			
		}
		
		
		
	}
	
	/** Used to safely print out the info from the database
	 * @author activates
	 *
	 */
	private class SyncRunner implements Runnable {

		private Map<Integer, String> warnings;
		private CommandSender sender;
		private String uuid;
		
		public SyncRunner(Map<Integer, String> warnings, CommandSender sender, String uuid) {
			
			this.warnings = warnings;
			this.sender = sender;
			this.uuid = uuid;
			
		}
		
		@Override
		public void run() {
			
			sender.sendMessage(Lang.WARNING_LIST_HEADER.parseObject(Banana.getPlayerCache().getLatestName(uuid)));
			
			for (Integer i : warnings.keySet()) {
				
				sender.sendMessage(Lang.WARNING_LIST_FORMAT.parseWarningList(i, warnings.get(i)));
				
			}
			
		}
		
		
		
	}
	
	/** Used to log a command a player ran! Only really useful for commands inside of BANana
	 * @param commandName The name of the command to log
	 * @param senderUUID The sender of the command
	 * @param args The arguments of the command.. Once again for logging purposes
	 */
	public void logCommand(Enum<CommandType> commandType, UUID senderUUID, String[] args, boolean isConsole) {
		
		String parsedArguments = "";
		
		if (args.length > 0) {
			
			parsedArguments = args[0];
			
			int i = 0;
			for (String s : args) {
				if (i > 0) {
					parsedArguments += " " + s;
				}
				i++;
			}
			
		}
		
		byte console = 0;
		
		if (isConsole) console = 1;
		
		parsedArguments = parsedArguments.replace("'", "&#00");
		
		String preparingStatement = "INSERT INTO logs (command, sender, args, date, isConsole) VALUES ('" + commandType.toString() + "', '" + senderUUID + "', '" + parsedArguments + "','" + (new Date()).getTime() + "', " + console + ")";
		
		this.queueEntry(preparingStatement);
		
	}
	
	/** Used to add async add an IP ban to the database 
	 * @param address The address to ban (wildcards might be supported D:)
	 * @param message The message to go with the ban (optional)
	 * @param tempBanned Set to true if this is a tempban
	 * @param tempBanDate If tempBanned is set to true, you must give the date of the unban
	 */
	public void asyncBanIP(String address, String message, Enum<BannerType> type, String bannerUUID, boolean tempBanned, Long tempBanDate, String unparsedDate) {
		
		if (address == null) return;
		
		String newMessage = message;
		
		if (message != null) {
			
			newMessage = newMessage.replaceAll("'", "&#00");
			
		}
		
		byte isTempBanned = 0;
		
		if (tempBanned) {
			isTempBanned = 1;
			
			if (tempBanDate == null) {
				return;
			}
			
		}
		
		String preparedString = "INSERT INTO ipbans (ip, message, tempBanned, tempBanDate, type, banner, pureDate) VALUES ('" + address + "', '" + newMessage + "', " + isTempBanned + ", '" + tempBanDate + "', '" + type.toString() + "', '"  + bannerUUID + "', '" + unparsedDate + "')";
		
		this.queueEntry(preparedString);
		
		// check if mysql is being used
		if (Values.USE_MYSQL) {
			// if mysql is being used, make sure to post to the update row, so that way the other servers
			// know something has been changed, too. Put the server's name in, too, since it's already updated
						
			preparedString = "INSERT INTO updates (type, data, date, executed, published) VALUES ('" + UpdateType.BAN_IP + "', '" + address + "{-S-P-L-I-T-}" + type + "{-S-P-L-I-T-}" + bannerUUID + "{-S-P-L-I-T-}" + newMessage + "{-S-P-L-I-T-}" + tempBanned + "{-S-P-L-I-T-}" + tempBanDate + "{-S-P-L-I-T-}" + unparsedDate + "', '" + System.currentTimeMillis() + "', '" + Values.SERVER_NAME + "', '" + Values.SERVER_NAME + "')";
							
			// upload that to the cache, too
			this.queueEntry(preparedString);
						
		}
		
	}
	
	// Used for holding player update data that is used by the seperate thread
	private class PlayerShell {
		
		// A sort of struct (like in C++)
		
		public String uuid, address, playerName;
		
		public PlayerShell(String uuid, String address, String playerName) {
			this.uuid = uuid;
			this.address = address;
			this.playerName = playerName;
		}
		
	}
	
	private List<PlayerShell> playerShells = new ArrayList<PlayerShell>();
	
	/** Used to async update the player's information in the database. This method will also update the local cache of the
	 * player's username. Set the address to null if there isn't one
	 * @param uuid The uuid of the player
	 * @param address The player's ip address
	 * @param playerName the player's latest username
	 */
	public synchronized void asyncUpdatePlayer(String uuid, String address, String playerName) {
		
		// update the cache information, too
		Banana.getPlayerCache().addLatestName(uuid, playerName);
		Banana.getPlayerCache().addAddress(uuid.toString(), address);
		
		// Add players to the queue of things that need to be updated
		playerShells.add(new PlayerShell(uuid, address, playerName));
		
		// Update the other servers with this information if mysql is being used
		
		if (Values.USE_MYSQL) {
			
			String preparedString = "INSERT INTO updates (type, data, date, executed, published) VALUES ('" + UpdateType.PLAYER + "', '" + uuid + "{-S-P-L-I-T-}" + address + "{-S-P-L-I-T-}" + playerName + "', '" + System.currentTimeMillis() + "', '" + Values.SERVER_NAME + "', '" + Values.SERVER_NAME + "')";
		
			// Send it to the database
			queueEntry(preparedString);
		
		}
		
	}
	
	private class PlayerUpdater implements Runnable {
		
		@Override
		public void run() {

			Connection connection = null;
			
			// A list to keep track of what player's have been removed
			List<PlayerShell> ranUpdates = new ArrayList<PlayerShell>();
			
			while (!Thread.interrupted()) {
				
				for (int i = 0; i < playerShells.size(); i++) {

					PlayerShell p = playerShells.get(i);
					
					try {
						
						Class.forName("org.sqlite.JDBC");

						// Check if MySQL is requested and use it if it's enabled
						if (Values.USE_MYSQL) {

							// connect to remote database here

							connection = DriverManager.getConnection(
									"jdbc:mysql://" + Values.MYSQL_ADDRESS + ":" + Values.MYSQL_PORT + "/"
											+ Values.MYSQL_DATABASE_NAME,
									Values.MYSQL_DATABASE_USERNAME, Values.MYSQL_DATABASE_PASSWORD);

						} else {

							// just create a the sqlite connection here

							// open a connection
							connection = DriverManager.getConnection(
									"jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + "/Banana.db");

						}

						// turning off autocommit
						connection.setAutoCommit(false);

						Thread.sleep(1000);

						Statement statement = connection.createStatement();

						String sql = "SELECT * FROM players WHERE UUID='" + p.uuid + "'";

						ResultSet rs = statement.executeQuery(sql);

						boolean doesContain = rs.next();

						if (doesContain) {

							// if this runs, we know to update the already existing
							// row

							sql = "UPDATE players SET address='" + p.address + "', playername='" + p.playerName
									+ "' WHERE UUID='" + p.uuid + "'";

						} else {

							// if this runs, we need to insert the new information

							sql = "INSERT INTO players (UUID, address, playername, warnings) VALUES ('" + p.uuid + "', '"
									+ p.address + "', '" + p.playerName + "', 0)";

						}

						statement.close();
						connection.close();

						// update what is needed to be updated
						queueEntry(sql);

						// Add the updated player's at the end
						ranUpdates.add(p);
						
					} catch (Exception e) {

						if (connection != null)
							try {
								connection.close();
							} catch (SQLException e1) {
							}

					}

				}

				// Remove updated players from list
				for (PlayerShell p : ranUpdates) {
					
					playerShells.remove(p);
					
				}
				
				// Clear out update table
				ranUpdates.clear();
				
				// Sleep on it for a while
				try {
					// Sleep for ~1/2 a second
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// We expect the thread to get interrupted at some point
				}
				
			}

		}
		
		
	}
	
	/** Used to async unban an ip
	 * @param address The IP to unban
	 */
	public void asyncRemoveIPBan(String address) {
		
		String preparingStatement = "DELETE FROM ipbans WHERE ip='" + address + "'";
		
		this.queueEntry(preparingStatement);
		
		// check if mysql is being used
		if (Values.USE_MYSQL) {
			// if mysql is being used, make sure to post to the update row, so that way the other servers
			// know something has been changed, too. Put the server's name in, too, since it's already updated
			preparingStatement = "INSERT INTO updates (type, data, date, executed, published) VALUES ('" + UpdateType.UN_BAN_IP + "', '" + address + "', '" + System.currentTimeMillis() + "', '" + Values.SERVER_NAME + "', '" + Values.SERVER_NAME + "')";
			
			// upload that to the cache, too
			this.queueEntry(preparingStatement);
				
		}
		
	}
	
	
	/** Used to add a mute to the database (both temp and perm)
	 * @param uuid The uuid to mute
	 * @param reason The reason used alongside the mute
	 * @param tempMuted If the player is temp muted or not
	 * @param unmuteDate The date that the player will be unmuted
	 * @param unparsedMuteDate The pure form of the date (in the form 0:0:0:0 or similar). This iw
	 * only for pushing the updates to the servers outside the network
	 */
	public void asyncAddMute(String uuid, String reason, boolean tempMuted, Long unmuteDate, String unparsedMuteDate) {
		
		if (uuid == null) return;
		
		String newReason = reason;
		
		if (newReason != null) {
			newReason = newReason.replaceAll("'", "&#00");
		}
		
		long currentDate = System.currentTimeMillis();
		byte tempMutedByte = 0;
		
		if (tempMuted) {
			
			if (unmuteDate == null) return;
			tempMutedByte = 1;
			
		}
		
		String preparingStatement = "INSERT INTO mutes (UUID, reason, date, tempMuted, tempMuteDate) VALUES ('" + uuid + "', '" + newReason + "', '" + currentDate + "', " + tempMutedByte + ", '" + unmuteDate + "')";
		
		this.queueEntry(preparingStatement);
		
		// check if mysql is being used
		if (Values.USE_MYSQL) {
			// if mysql is being used, make sure to post to the update row, so that way the other servers
			// know something has been changed, too. Put the server's name in, too, since it's already updated
						
			preparingStatement = "INSERT INTO updates (type, data, date, executed, published) VALUES ('" + UpdateType.MUTE + "', '" + uuid + "{-S-P-L-I-T-}" + newReason + "{-S-P-L-I-T-}" + tempMuted + "{-S-P-L-I-T-}" + unmuteDate + "{-S-P-L-I-T-}" + unparsedMuteDate + "', '" + System.currentTimeMillis() + "', '" + Values.SERVER_NAME + "', '" + Values.SERVER_NAME + "')";
				
			// upload that to the cache, too
			this.queueEntry(preparingStatement);
						
		}
		
	}
	
	/** Used to async remove mutes from the database
	 * @param uuid The uuid to remove (will remove all mutes on that specific UUID
	 */
	public void asyncRemoveMute(String uuid) {
		
		String preparingStatement = "DELETE FROM mutes WHERE UUID='" + uuid + "'";
		
		this.queueEntry(preparingStatement);
		
		// check if mysql is being used
		if (Values.USE_MYSQL) {
			// if mysql is being used, make sure to post to the update row, so that way the other servers
			// know something has been changed, too. Put the server's name in, too, since it's already updated
			preparingStatement = "INSERT INTO updates (type, data, date, executed, published) VALUES ('" + UpdateType.UN_MUTE + "', '" + uuid + "', '" + System.currentTimeMillis() + "', '" + Values.SERVER_NAME + "', '" + Values.SERVER_NAME + "')";
			
			// upload that to the cache, too
			this.queueEntry(preparingStatement);
				
		}
		
	}
	
	/** Async adds a staff member's uuid to the database
	 * @param uuid The uuid to add
	 */
	public void asyncAddStaffMember(String uuid) {
		
		String preparingStatement = "INSERT INTO staff (UUID) VALUES ('" + uuid + "')";
		
		this.queueEntry(preparingStatement);
		
		// check if mysql is being used
		if (Values.USE_MYSQL) {
			// if mysql is being used, make sure to post to the update row, so that way the other servers
			// know something has been changed, too. Put the server's name in, too, since it's already updated
			preparingStatement = "INSERT INTO updates (type, data, date, executed, published) VALUES ('" + UpdateType.ADD_STAFF + "', '" + uuid + "', '" + System.currentTimeMillis() + "', '" + Values.SERVER_NAME + "', '" + Values.SERVER_NAME + "')";
			
			// upload that to the cache, too
			this.queueEntry(preparingStatement);
		
		}
		
	}
	
	/** Async removes a member's uuid from the database
	 * @param uuid
	 */
	public void asyncRemoveStaffMember(String uuid) {
		
		String preparingStatement = "DELETE FROM staff WHERE UUID='" + uuid + "'";
		
		this.queueEntry(preparingStatement);
		
		// check if mysql is being used
		if (Values.USE_MYSQL) {
			// if mysql is being used, make sure to post to the update row, so that way the other servers
			// know something has been changed, too. Put the server's name in, too, since it's already updated
						
			preparingStatement = "INSERT INTO updates (type, data, date, executed, published) VALUES ('" + UpdateType.RM_STAFF + "', '" + uuid + "', '" + System.currentTimeMillis() + "', '" + Values.SERVER_NAME + "', '" + Values.SERVER_NAME + "')";
			
			// upload that to the cache, too
			this.queueEntry(preparingStatement);
				
		}
		
	}
	
	/** Decrements the warning number of a player by its ID (and removes it from the list)
	 * @param id the id to check for
	 */
	public void decrementWarning(int id) {
		
		// pass it over in a new thread
		Thread worker = new Thread(new DecrementWarning(id));
		worker.start();
		
	}
	
	private class DecrementWarning implements Runnable {

		int id;
		public DecrementWarning (int id) {
			this.id = id;
		}
		
		@Override
		public void run() {
			
			boolean accessAble = false;

			Connection connection = null;
			
			while (!accessAble) {

				try {

					Class.forName("org.sqlite.JDBC");

					// Check if MySQL is requested and use it if it's enabled
					if (Values.USE_MYSQL) {
						
						// connect to remote database here				
						
						connection = DriverManager.getConnection("jdbc:mysql://" + Values.MYSQL_ADDRESS + ":" + Values.MYSQL_PORT + "/" + Values.MYSQL_DATABASE_NAME, Values.MYSQL_DATABASE_USERNAME, Values.MYSQL_DATABASE_PASSWORD);
						
					} else {
						
						// just create a the sqlite connection here
						
						// open a connection
						connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + "/Banana.db");
						
					}

					// turning off autocommit
					connection.setAutoCommit(false);

					Thread.sleep(1000);

					Statement statement = connection.createStatement();

					String sql = "SELECT * FROM warns WHERE ID=" + id + "";

					ResultSet rs = statement.executeQuery(sql);

					// a list to hold all of the updates that will need to be pushed to the database
					// even if we don't use mysql, we will still leave this out here..
					List<String> updateStatements = new ArrayList<String>();
					
					while (rs.next()) {
						
						String uuid = rs.getString("UUID");
						String warningMessage = rs.getString("message");
						
						// decrement these warnings.. and remove the message from the player's warning history
						Banana.getWarnCache().removeWarning(uuid, warningMessage.replace("&#00", "'"));
						
						// check if MySQL is used
						if (Values.USE_MYSQL) {
							// if mysql is being used, add each update to the list
							updateStatements.add("INSERT INTO updates (type, data, date, executed, published) VALUES ('" + UpdateType.DECREMENT_WARN + "', '" + uuid + "{-S-P-L-I-T-}" + warningMessage.replace("&#00", "'") + "','" + System.currentTimeMillis() + "', '" + Values.SERVER_NAME + "', '" + Values.SERVER_NAME + "'");
						}
						
					}

					statement.close();
					
					// check if mysql is being used
					if (Values.USE_MYSQL) {
						
						// loop through the update list and run all the updates here since we are already in a seperate thread
						
						for (String s : updateStatements) {
							statement = connection.createStatement();
							
							statement.executeUpdate(s);
							
							statement.close();
						}
						
					}
					
					connection.close();

					accessAble = true;
					break;

				} catch (Exception e) {
					
					if (connection != null)
						try {
							connection.close();
						} catch (SQLException e1) {}
					
				}

			}
			
		}
		
		
	}
	
	/** Purges warnings for the particular player
	 * @param uuid The uuid to remove all warnings for
	 */
	public void asyncPurgeWarnings(String uuid) {
		
		String preparingStatement = "DELETE FROM warns WHERE UUID='" + uuid + "'";
		
		this.queueEntry(preparingStatement);
		
		// check if mysql is being used
		if (Values.USE_MYSQL) {
			// if mysql is being used, make sure to post to the update row, so that way the other servers
			// know something has been changed, too. Put the server's name in, too, since it's already updated
				
			preparingStatement = "INSERT INTO updates (type, data, date, executed, published) VALUES ('" + UpdateType.PURGE_WARNINGS + "', '" + uuid + "', '" + System.currentTimeMillis() + "', '" + Values.SERVER_NAME + "', '" + Values.SERVER_NAME + "')";
							
			// upload that to the cache, too
			this.queueEntry(preparingStatement);
				
		}
		
	}
	
	/** Sends the update to kick all players to the database. If the other servers read this,
	 * they will kick all the players on that server, too.
	 * @param kickerName The name of the player who is kicking all the other players. (Not a UUID!)
	 * @param message The message to kick the players with! Simple, right? No..? screw you!
	 */
	public void kickAllUpdatePublisher(String kickerName, String message) {
		
		// check if mysql is being used
		if (Values.USE_MYSQL) {
			// if mysql is being used, make sure to post to the update row, so that way the other servers
			// know something has been changed, too. Put the server's name in, too, since it's already updated
						
			String preparedStatement = "INSERT INTO updates (type, data, date, executed, published) VALUES ('" + UpdateType.KICK_ALL + "', '" + kickerName + "{-S-P-L-I-T-}" + message.replace("'", "&#00") + "', '" + System.currentTimeMillis() + "', '" + Values.SERVER_NAME + "', '" + Values.SERVER_NAME + "')";
					
			// upload that to the cache, too
			this.queueEntry(preparedStatement);
				
		}
		
	}
	
}
