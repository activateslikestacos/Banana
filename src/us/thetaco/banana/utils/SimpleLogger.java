package us.thetaco.banana.utils;

import org.bukkit.Bukkit;

public class SimpleLogger {

	public static void logMessage(String message) {
		
		Bukkit.getLogger().info("[Banana] " + message);
		
	}
	
}
