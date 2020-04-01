package us.thetaco.banana.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import info.dyndns.thetaco.uuid.api.Main;
import us.thetaco.banana.Banana;
import us.thetaco.banana.sql.DatabaseManager.BannerType;

public class WarnCache {

	private Map<String, Integer> warnAmount = new HashMap<String, Integer>();
	private Map<String, List<String>> warnMessages = new HashMap<String, List<String>>();
	
	/** Get the current amounts of warnings a player has
	 * @param uuid
	 * @return
	 */
	public int getWarnAmounts(String uuid) {
		
		Integer warnAmount = this.warnAmount.get(uuid);
		 
		if (warnAmount == null) {
			return 0;
		} else {
			return warnAmount;
		}
		
	}
	
	/** Get the current amounts of warnings a player has
	 * @param uuid
	 * @return
	 */
	public int getWarnAmounts(UUID uuid) {
		
		Integer warnAmount = this.warnAmount.get(uuid.toString());
		 
		if (warnAmount == null) {
			return 0;
		} else {
			return warnAmount;
		}
		
	}
	
	/** Adds a warning to the specified uuid. Also increments their warning amount
	 * @param uuid
	 * @param message
	 */
	public void addWarning(String uuid, String message) {
		
		int currentWarnings = this.getWarnAmounts(uuid.toString());
		currentWarnings++;
		
		List<String> currentMessages = warnMessages.get(uuid.toString());
		
		if (currentMessages == null) {
			
			currentMessages = new ArrayList<String>();
			
		}
		
		currentMessages.add(message);
		
		warnAmount.put(uuid.toString(), currentWarnings);
		warnMessages.put(uuid.toString(), currentMessages);
		
	}
	
	/** Adds a warning to the specified uuid. Also increments their warning amount
	 * @param uuid
	 * @param message
	 */
	public void addWarning(UUID uuid, String message) {
		
		int currentWarnings = this.getWarnAmounts(uuid.toString());
		currentWarnings++;
		
		List<String> currentMessages = warnMessages.get(uuid.toString());
		
		if (currentMessages == null) {
			
			currentMessages = new ArrayList<String>();
			
		}
		
		currentMessages.add(message);
		
		warnAmount.put(uuid.toString(), currentWarnings);
		warnMessages.put(uuid.toString(), currentMessages);
		
	}
	
	/** Used to set player warning amoungs
	 * @param uuid The uuid to set the warning for
	 * @param amount The new amount
	 */
	public void setWarningAmount(String uuid, int amount) {
		
		if (uuid == null) return;
		
		this.warnAmount.put(uuid, amount);
		
	}
	
	/** Used to set player warning amoungs
	 * @param uuid The uuid to set the warning for
	 * @param amount The new amount
	 */
	public void setWarningAmount(UUID uuid, int amount) {
		
		if (uuid.toString() == null) return;
		
		this.warnAmount.put(uuid.toString(), amount);
		
	}
	
	/** Used to subtract a warning amount from a player
	 * @param uuid The uuid to subtract from
	 */
	public void subtractWarning(String uuid) {
		
		int currentWarnAmount = this.getWarnAmounts(uuid);
		
		if (currentWarnAmount < 1) return;
		
		currentWarnAmount--;
		
		this.setWarningAmount(uuid, currentWarnAmount);
		
	}
	
	/** Used to subtract a warning amount from a player
	 * @param uuid The uuid to subtract from
	 */
	public void subtractWarning(UUID uuid) {
		
		int currentWarnAmount = this.getWarnAmounts(uuid);
		
		if (currentWarnAmount < 1) return;
		
		currentWarnAmount--;
		
		this.setWarningAmount(uuid, currentWarnAmount);
		
	}
	
	/** Used to remove a warning message from a player's warning (it will only delete one, so if there
	 * are duplicates, only one will be removed)
	 * @param uuid The uuid to search and destroy for
	 * @param message the message to remove (case does not mattter)
	 */
	public void removeWarning(String uuid, String message) {
		
		System.out.println(uuid + " " + message);
		
		List<String> warningMessages = this.getWarningMessage(uuid);
		
		if (warningMessages == null) return;
		
		for (int i = 0; i < warningMessages.size(); i++) {
			
			if (warningMessages.get(i).equalsIgnoreCase(message)) {
				
				System.out.println("Warning removed");
				
				// if it matches, only remove one
				warningMessages.remove(i);
				
				// also decrement their warning amount
				this.subtractWarning(uuid);
				
				break;
				
			}
			
		}
		
		this.warnMessages.put(uuid, warningMessages);
		
	}
	
	/** Used to get the warning messages for the player
	 * @param uuid The uuid to get the messages from
	 * @return A list of messages
	 */
	public List<String> getWarningMessage(String uuid) {
		
		return this.warnMessages.get(uuid);
		
	}
	
	/** Used to delete all warnings for a particular player and resets their warning count
	 * @param uuid The uuid to purge the warnings for
	 */
	public void purgeWarnings(String uuid) {
		
		this.warnMessages.remove(uuid);
		this.setWarningAmount(uuid, 0);
		
	}
	
	/** Looks up the amount of warns on the provided UUID and applies the configured warning action (if applicable)
	 * This will work even if the config disallows warning actions, so check that first!
	 * @param uuid The uuid to check and apply warning actions for
	 */
	public void applyWarningAction(String uuid) {
		
		// get the player's current warning amount
		int currentWarnAmount = Banana.getWarnCache().getWarnAmounts(uuid);
		
		String suggestedAction = Values.WARNING_ACTIONS.get(currentWarnAmount);
		
		if (suggestedAction == null) {
			// stop here because there are no actions to take
			return;
		}
		
		try {
			
			String[] parsedAction = suggestedAction.split(" ");
	
			Enum<Action> actionEnum = Action.valueOf(parsedAction[0]);
			
			if (actionEnum == Action.BAN) {
				
				// run this in case of ban
				// compile warning messages:
				String warningMessage = "";
				
				List<String> warningMessages = this.getWarningMessage(uuid);
				
				if (warningMessages != null && warningMessages.size() > 0) {
					
					warningMessage = warningMessages.get(0);
					
					int i = 0;
					for (String s : warningMessages) {
						
						if (i > 0) {
							warningMessage += "\n" + s;
						}
						i++;
					}
					
				}
				
				String banMessage = Lang.WARNING_BAN_MESSAGE.parseAutoWarnActions(currentWarnAmount, warningMessage);
				
				Banana.getBanCache().addBannedUUID(uuid, banMessage, BannerType.AUTOBANNED, null);
				Banana.getDatabaseManager().asyncAddBan(uuid, BannerType.AUTOBANNED, null, banMessage, false, null, null);
				
				Player target = Bukkit.getPlayer(UUID.fromString(uuid));
				
				if (target != null) {
					
					target.kickPlayer(banMessage);
					
				}
				
				// Announce it
				if (Values.ANNOUNCE_KICK) {
					
					Action.broadcastMessage(Action.BAN, Lang.WARNING_AUTOBAN_BROADCAST.parseAutoWarnBroadcast((new Main()).getLatestName(uuid), currentWarnAmount));
					
				}
				
				return;
				
			} else if (actionEnum == Action.KICK) {
				
				// get the player to kick
				Player target = Bukkit.getPlayer(UUID.fromString(uuid));
				
				if (target == null) {
					// stop here because an offline player can't be kicked.. anyways
					return;
				}
				
				// compile warning messages:
				String warningMessage = "";
				
				List<String> warningMessages = this.getWarningMessage(uuid);
				
				if (warningMessages != null && warningMessages.size() > 0) {
					
					warningMessage = warningMessages.get(0);
					
					int i = 0;
					for (String s : warningMessages) {
						
						if (i > 0) {
							warningMessage += "\n" + s;
						}
						i++;
					}
					
				}
				
				// kick player with desired message
				target.kickPlayer(Lang.WARNING_KICK_MESSAGE.parseAutoWarnActions(currentWarnAmount, warningMessage));
				
				if (Values.ANNOUNCE_KICK) {
					
					Action.broadcastMessage(Action.KICK, Lang.WARNING_AUTOKICK_BROADCAST.parseAutoWarnBroadcast((new Main()).getLatestName(uuid), currentWarnAmount));
					
				}
				
			} else if (actionEnum == Action.MUTE) {
				
				// get the player to kick
				Player target = Bukkit.getPlayer(UUID.fromString(uuid));
				
				// just.. mute the specified uuid applied
				
				Banana.getMuteCache().addMutedPlayer(uuid);
				Banana.getDatabaseManager().asyncAddMute(uuid, Lang.WARNING_MUTE_LOG_MESSAGE.parseNumber(currentWarnAmount), false, null, null);
				
				if (Values.ANNOUNCE_MUTE) {
					
					Action.broadcastMessage(Action.MUTE, Lang.WARNING_AUTOMUTE_BROADCAST.parseAutoWarnBroadcast((new Main()).getLatestName(uuid), currentWarnAmount));
					
				}
				
				if (target != null) {
					
					if (Values.NOTIFY_MUTE) {
						target.sendMessage(Lang.WARNING_MUTE_MESSAGE.parseNumber(currentWarnAmount));
					}
					
				}
				
				return;
				
			} else if (actionEnum == Action.TEMPBAN) {
				
				if (parsedAction.length < 2) {
					throw new Exception();
				}
				
				String[] timeString = parsedAction[1].split(":");
				
				int seconds = 0;
				int minutes = 0;
				int hours = 0;
				int days = 0;
				
				try {
				
					if (timeString.length == 1) {
					
						// this will run if seconds are wanted
						seconds = Integer.parseInt(timeString[0]);
					
						
					} else if (timeString.length == 2) {
						
						// this will run if minutes and seconds are wanted
						minutes = Integer.parseInt(timeString[0]);
						seconds = Integer.parseInt(timeString[1]);
						
					} else if (timeString.length == 3) {
					
						// this will run if seconds, minutes, and hours are wanted
						hours = Integer.parseInt(timeString[0]);
						minutes = Integer.parseInt(timeString[1]);
						seconds = Integer.parseInt(timeString[2]);
					
					} else {
						
						// this will run if seconds, minutes, hours, and days are wanted
						days = Integer.parseInt(timeString[0]);
						hours = Integer.parseInt(timeString[1]);
						minutes = Integer.parseInt(timeString[2]);
						seconds = Integer.parseInt(timeString[3]);
						
					}
				
				} catch (Exception e) {
					throw new Exception();
				}
				
				Calendar calendar = Calendar.getInstance();
				
				calendar.setTimeInMillis(System.currentTimeMillis());
				
				calendar.add(Calendar.DAY_OF_MONTH, days);
				calendar.add(Calendar.HOUR_OF_DAY, hours);
				calendar.add(Calendar.MINUTE, minutes);
				calendar.add(Calendar.SECOND, seconds);
				
				Date unbanDate = calendar.getTime();
				
				// compile warning messages:
				String warningMessage = "";
				
				List<String> warningMessages = this.getWarningMessage(uuid);
				
				if (warningMessages != null && warningMessages.size() > 0) {
					
					warningMessage = warningMessages.get(0);
					
					int i = 0;
					for (String s : warningMessages) {
						
						if (i > 0) {
							warningMessage += "\n" + s;
						}
						i++;
					}
					
				}
				
				/*
				 * 
				 * OLD
				 * 
				String banKickMessage = Lang.WARNING_TEMPBAN_MESSAGE.parseTimeWarningActions(currentWarnAmount, warningMessage, days, hours, minutes, seconds);
				
				// start by tempBanning player
				Banana.getBanCache().addTempBannedUUID(uuid, banKickMessage, unbanDate, BannerType.AUTOBANNED, null, parsedAction[1]);
				Banana.getDatabaseManager().asyncAddBan(uuid, BannerType.AUTOBANNED, null, banKickMessage, true, unbanDate.getTime(), parsedAction[1]);
				
				*/
				
				String banKickMessage = Lang.WARNING_TEMPBAN_MESSAGE.parseTimeWarningActions(currentWarnAmount, warningMessage, days, hours, minutes, seconds);
				
				// start by tempBanning player
				Banana.getBanCache().addTempBannedUUID(uuid, banKickMessage, unbanDate, BannerType.AUTOBANNED, null, parsedAction[1]);
				Banana.getDatabaseManager().asyncAddBan(uuid, BannerType.AUTOBANNED, null, banKickMessage, true, unbanDate.getTime(), parsedAction[1]);
				
				// kick player with desired message
				
				// get the player to kick
				Player target = Bukkit.getPlayer(UUID.fromString(uuid));
				
				if (target != null) {
					target.kickPlayer(banKickMessage);
				}
				
				// Announce it
				if (Values.ANNOUNCE_TEMPBAN) {
					
					Action.broadcastMessage(Action.TEMPBAN, Lang.WARNING_TEMPBAN_MESSAGE.parseTimeWarningBroadcast(currentWarnAmount, warningMessage, (new Main()).getLatestName(uuid), days, hours, minutes, seconds));
					
				}
				
				return;
				
			} else if (actionEnum == Action.BANIP) {
				
				// get their address
				String address = Banana.getPlayerCache().getAddress(uuid);
				
				if (address == null) {
					SimpleLogger.logMessage("No IP stored for UUID: " + uuid);
					return;
				}
				
				// compile warning messages:
				String warningMessage = "";
				List<String> warningMessages = this.getWarningMessage(uuid);
				
				if (warningMessages != null && warningMessages.size() > 0) {
					
					warningMessage = warningMessages.get(0);
					
					int i = 0;
					for (String s : warningMessages) {
						
						if (i > 0) {
							warningMessage += "\n" + s;
						}
						i++;
					}
					
				}
				
				String banMessage = Lang.WARNING_IPBAN_MESSAGE.parseAutoWarnActions(currentWarnAmount, warningMessage);
				
				// lay dat ban down
				Banana.getBanCache().addBannedIP(address, banMessage, BannerType.AUTOBANNED, null);
				Banana.getDatabaseManager().asyncBanIP(address, banMessage, BannerType.AUTOBANNED, null, false, null, null);
				
				// kick the player if they are online
				Player target = Bukkit.getPlayer(UUID.fromString(uuid));
				
				if (target != null) {
					
					target.kickPlayer(banMessage);
					
				}
				
				// Announce it
				if (Values.ANNOUNCE_BANIP) {
					
					Action.broadcastMessage(Action.BANIP, Lang.WARNING_AUTO_IPBAN_BROADCAST.parseAutoWarnBroadcast((new Main()).getLatestName(uuid), currentWarnAmount));
					
				}
				
				return;
				
			} else if (actionEnum == Action.TEMPBANIP) {
				
				// get their address
				String address = Banana.getPlayerCache().getAddress(uuid);
				
				if (address == null) {
					SimpleLogger.logMessage(Lang.NO_IP_STORED.toString());
					return;
				}
				
				if (parsedAction.length < 2) {
					throw new Exception();
				}
				
				String[] timeString = parsedAction[1].split(":");
				
				int seconds = 0;
				int minutes = 0;
				int hours = 0;
				int days = 0;
				
				try {
				
					if (timeString.length == 1) {
					
						// this will run if seconds are wanted
						seconds = Integer.parseInt(timeString[0]);
					
						
					} else if (timeString.length == 2) {
						
						// this will run if minutes and seconds are wanted
						minutes = Integer.parseInt(timeString[0]);
						seconds = Integer.parseInt(timeString[1]);
						
					} else if (timeString.length == 3) {
					
						// this will run if seconds, minutes, and hours are wanted
						hours = Integer.parseInt(timeString[0]);
						minutes = Integer.parseInt(timeString[1]);
						seconds = Integer.parseInt(timeString[2]);
					
					} else {
						
						// this will run if seconds, minutes, hours, and days are wanted
						days = Integer.parseInt(timeString[0]);
						hours = Integer.parseInt(timeString[1]);
						minutes = Integer.parseInt(timeString[2]);
						seconds = Integer.parseInt(timeString[3]);
						
					}
				
				} catch (Exception e) {
					throw new Exception();
				}
				
				Calendar calendar = Calendar.getInstance();
				
				calendar.setTimeInMillis(System.currentTimeMillis());
				
				calendar.add(Calendar.DAY_OF_MONTH, days);
				calendar.add(Calendar.HOUR_OF_DAY, hours);
				calendar.add(Calendar.MINUTE, minutes);
				calendar.add(Calendar.SECOND, seconds);
				
				Date unbanDate = calendar.getTime();
				
				// compile warning messages:
				String warningMessage = "";
				
				List<String> warningMessages = this.getWarningMessage(uuid);
				
				if (warningMessages != null && warningMessages.size() > 0) {
					
					warningMessage = warningMessages.get(0);
					
					int i = 0;
					for (String s : warningMessages) {
						
						if (i > 0) {
							warningMessage += "\n" + s;
						}
						i++;
					}
					
				}
				
				String banKickMessage = "Your IP has been temp-banned for " + days + " day(s), " + hours + " hour(s), " + minutes + " minute(s), and " + seconds + " second(s) from the server for recieving " + currentWarnAmount + " warnings!\n"
						+ "Warning History:\n" + warningMessage;
				
				Banana.getBanCache().addTempBannedUUID(uuid, banKickMessage, unbanDate, BannerType.AUTOBANNED, null, parsedAction[1]);
				Banana.getDatabaseManager().asyncBanIP(address, banKickMessage, BannerType.AUTOBANNED, null, true, unbanDate.getTime(), parsedAction[1]);
				
				Player target = Bukkit.getPlayer(UUID.fromString(uuid));
				
				if (target != null) {
					
					target.kickPlayer(banKickMessage);
					
				}
				
				// Announce it
				if (Values.ANNOUNCE_TEMPBANIP) {
					
					Action.broadcastMessage(Action.TEMPBANIP, (new Main()).getLatestName(uuid) + " was automatically temp ip-banned for recieving " + currentWarnAmount + " warns");
					
				}
				
			} else if (actionEnum == Action.TEMPMUTE) {
				
				if (parsedAction.length < 2) {
					throw new Exception();
				}
				
				String[] timeString = parsedAction[1].split(":");
				
				int seconds = 0;
				int minutes = 0;
				int hours = 0;
				int days = 0;
				
				try {
				
					if (timeString.length == 1) {
					
						// this will run if seconds are wanted
						seconds = Integer.parseInt(timeString[0]);
					
						
					} else if (timeString.length == 2) {
						
						// this will run if minutes and seconds are wanted
						minutes = Integer.parseInt(timeString[0]);
						seconds = Integer.parseInt(timeString[1]);
						
					} else if (timeString.length == 3) {
					
						// this will run if seconds, minutes, and hours are wanted
						hours = Integer.parseInt(timeString[0]);
						minutes = Integer.parseInt(timeString[1]);
						seconds = Integer.parseInt(timeString[2]);
					
					} else {
						
						// this will run if seconds, minutes, hours, and days are wanted
						days = Integer.parseInt(timeString[0]);
						hours = Integer.parseInt(timeString[1]);
						minutes = Integer.parseInt(timeString[2]);
						seconds = Integer.parseInt(timeString[3]);
						
					}
				
				} catch (Exception e) {
					throw new Exception();
				}
				
				Calendar calendar = Calendar.getInstance();
				
				calendar.setTimeInMillis(System.currentTimeMillis());
				
				calendar.add(Calendar.DAY_OF_MONTH, days);
				calendar.add(Calendar.HOUR_OF_DAY, hours);
				calendar.add(Calendar.MINUTE, minutes);
				calendar.add(Calendar.SECOND, seconds);
				
				Date unmuteDate = calendar.getTime();
				
				// String banKickMessage = "You have been muted for " + days + " day(s), " + hours + " hour(s), " + minutes + " minute(s), and " + seconds + " second(s) for recieving " + currentWarnAmount + " warnings!";
				
				if (Values.ANNOUNCE_TEMPMUTE) {
					
					Action.broadcastMessage(Action.TEMPMUTE, (new Main()).getLatestName(uuid) + " was automatically temp ip-muted for recieving " + currentWarnAmount + " warns");
					
				}
				
				Banana.getMuteCache().addMutedPlayer(uuid);
				Banana.getDatabaseManager().asyncAddMute(uuid, "Recieved " + currentWarnAmount + " and was automatically muted", true, unmuteDate.getTime(), parsedAction[1]);
				
				// notify the player if that they have been muted if it is enabled
				if (Values.NOTIFY_TEMPMUTE) {
					
					Player target = Bukkit.getPlayer(UUID.fromString(uuid));
					
					if (target != null) target.sendMessage(ChatColor.RED + "You have been temp-muted for " + days + " day(s), " + hours + " hour(s), " + minutes + " minute(s), and " + seconds + " second(s)");
					
				}
				
			} else if (actionEnum == Action.COMMAND) {
				
				// check to make sure a command was specified
				if (parsedAction.length < 2) {
					// if not, then throw an exception to stop it here
					throw new Exception();
				}
				
				// compile the command to execute
				String preparedCommand = parsedAction[1];
				
				// parse arguments if they are any
				// we'll also parse {PLAYER} into it so they can run it for the player
				
				if (parsedAction.length > 2) {
					
					int i = 0;
					for (String s : parsedAction) {
						if (i > 1) {
						
							preparedCommand += " " + s;
								
						}
						i++;
					}
				}
					
				SimpleLogger.logMessage(preparedCommand);
				
				// attempting to parse in a player to parse the player in
				preparedCommand = preparedCommand.replaceAll("\\{PLAYER\\}", (new Main()).getLatestName(uuid));
				
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), preparedCommand);
				
			} else if (actionEnum == Action.NOTHING) {
			
				// do nothing
				
			} else {
				
				throw new Exception();
				
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
			SimpleLogger.logMessage("Warning actions have been correctly configured! Unknown action: " + suggestedAction);
			
		}
		
	}
	
}
