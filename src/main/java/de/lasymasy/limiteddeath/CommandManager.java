package de.lasymasy.limiteddeath;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class CommandManager implements CommandExecutor {

    private final LimitedDeath plugin;

    public CommandManager(LimitedDeath plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("limitetdeath") || label.equalsIgnoreCase("ld")) {
            if (args.length >= 1) {
                String subCommand = args[0].toLowerCase();
                if (subCommand.equals("setcoins")) {
                    // Handle setcoins command
                    if (args.length != 3) {
                        sender.sendMessage("Usage: /" + label + " setcoins <player> <count>");
                        return true;
                    }
                    String player = util.getUUIDFromName(args[1]);
                    String credits_set = args[2];

                    sender.sendMessage("Player: " + util.getNameFromUUID(player) + " has currently " + plugin.getDeathCredits(UUID.fromString(player)) + " and would be set to " + credits_set);
                    plugin.setDeathCredits(UUID.fromString(player), Integer.parseInt(credits_set), plugin.getNextRegenTime(UUID.fromString(player)));

                } else if (subCommand.equals("show")) {
                    // Handle viewcoins command
                    if (args.length != 1) {
                        sender.sendMessage("Usage: /" + label + " view");
                        return true;
                    }
                    String player = util.getUUIDFromName(args[1]);
                    sender.sendMessage("Player: " + util.getNameFromUUID(player) + " has currently " + plugin.getDeathCredits(UUID.fromString(player)));
                    // Your viewcoins logic here

                } else if (subCommand.equals("showall")) {
                    // Handle showall command
                    // Check if the sender has permission
                    if (!sender.hasPermission("limiteddeath.showall")) {
                        sender.sendMessage("You don't have permission to use this command.");
                        return true;
                    }


                    for (UUID playerId : plugin.getAllPlayers()) {
                        sender.sendMessage(playerId.toString() + ": " + plugin.getDeathCredits(playerId));
                    }
                    return true;

                    }
                    return true;
                } else {
                    sender.sendMessage("Unknown subcommand. Available subcommands: setcoins, viewcoins, show, showall");
                    return true;
                }
            } else {
                sender.sendMessage("Usage: /" + label + " <subcommand>");
                return true;
            }
    }
}

