package de.lasymasy.limiteddeath;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class Commands implements CommandExecutor {

    private final LimitedDeath plugin;

    public Commands(LimitedDeath limitedDeath) {
        this.plugin = limitedDeath;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (s.equalsIgnoreCase("limitetdeath") || s.equalsIgnoreCase("ld")) {
            if (args.length >= 1) {
                String subCommand = args[0].toLowerCase();
                if (subCommand.equals("setcoins")) {
                    // Handle setcoins command
                    if (args.length != 3) {
                        commandSender.sendMessage("Usage: /" + s + " setcoins <player> <count>");
                        return true;
                    }
                    String player = util.getUUIDFromName(args[1]);
                    if (player == null) {
                        commandSender.sendMessage("Invalid player name.");
                        return true;
                    }
                    plugin.loadDeathCreditsFile();
                    String credits_set = args[2];

                    commandSender.sendMessage("Player: " + util.getNameFromUUID(player) + " has currently " + plugin.getDeathCredits(UUID.fromString(player)) + " and would be set to " + credits_set);
                    plugin.setDeathCredits(UUID.fromString(player), Integer.parseInt(credits_set), plugin.getNextRegenTime(UUID.fromString(player)));
                    plugin.saveDeathCreditsFile();
                } else if (subCommand.equals("show")) {
                    // Handle viewcoins command
                    if (args.length != 2) {
                        commandSender.sendMessage("Usage: /" + s + " show <player>");
                        return true;
                    }
                    String player = util.getUUIDFromName(args[1]);
                    if (player == null) {
                        commandSender.sendMessage("Invalid player name.");
                        return true;
                    }
                    commandSender.sendMessage("Player: " + util.getNameFromUUID(player) + " has currently " + plugin.getDeathCredits(UUID.fromString(player)));
                    // Your viewcoins logic here

                } else if (subCommand.equals("showall")) {
                    // Handle showall command
                    if (!commandSender.hasPermission("limiteddeath.showall")) {
                        commandSender.sendMessage("You don't have permission to use this command.");
                        return true;
                    }

                    for (UUID playerId : plugin.getAllPlayers()) {
                        String playerName = util.getNameFromUUID(playerId.toString());
                        commandSender.sendMessage(playerName + ": " + plugin.getDeathCredits(playerId) + "- Next Regen: " + plugin.getNextRegenTime(playerId));
                    }
                    return true;

                } else {
                    commandSender.sendMessage("Unknown subcommand. Available subcommands: setcoins, show, showall");
                    return true;
                }
            } else {
                commandSender.sendMessage("Usage: /" + s + " <subcommand>");
                return true;
            }
        } else {
            commandSender.sendMessage("Usage: /" + s + " <subcommand>");
            return true;
        }

        return false;
    }
}
