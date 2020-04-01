package us.thetaco.banana.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.thetaco.banana.Banana;
import us.thetaco.banana.utils.ConfigManager;
import us.thetaco.banana.utils.Lang;

public class BananaCommand implements CommandExecutor {

	private ConfigManager config;
	private Banana plugin;
	public BananaCommand(Banana plugin, ConfigManager config){
		this.config = config;
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			
			// run this if the sender is not a player
			return true;
			
		}
		
		// run this if the sender is a player
		Player player = (Player) sender;
		
		// send them the info if there aren't any arguments
		
		if (args.length == 0) {
			player.sendMessage(ChatColor.GREEN + "BANana plugin made by activates (NightFury)");
			player.sendMessage(ChatColor.YELLOW + "/banana reload" + ChatColor.YELLOW + "-" + ChatColor.GREEN + "Reloads the config and language file");
			return true;
		}
		
		if (args.length > 0) {
			
			if (args[0].equalsIgnoreCase("reload")) {
				
				if (!player.hasPermission("banana.commands.reload")) {
					player.sendMessage(Lang.NO_PERMISSIONS.toString());
					return true;
				}
				
				config.reloadValues();
				plugin.loadLang();
				
				player.sendMessage(ChatColor.GREEN + "Config reloaded!");
				
			} else {
				
				player.sendMessage(ChatColor.RED + "Unknown argument");
				player.sendMessage(ChatColor.GREEN + "BANana plugin made by activates (NightFury)");
				player.sendMessage(ChatColor.YELLOW + "/banana reload" + ChatColor.YELLOW + " - " + ChatColor.GREEN + "Reloads the config and language file");
				
				return true;
			}
			
		}
		
		return true;
	}

}
