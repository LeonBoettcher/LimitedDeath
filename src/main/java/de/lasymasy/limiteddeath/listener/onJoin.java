package de.lasymasy.limiteddeath.listener;

import de.lasymasy.limiteddeath.LimitedDeath;
import de.lasymasy.limiteddeath.util;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.UUID;

public class onJoin implements Listener {
    private LimitedDeath limitdeath;
    public onJoin(LimitedDeath limitdeath) {
        this.limitdeath = limitdeath;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();

        limitdeath.loadDeathCreditsFile();
        if(!limitdeath.hasDeathCredit(event.getPlayer().getUniqueId())){
            limitdeath.setDeathCredits(event.getPlayer().getUniqueId(), 3, 0);
        }
        limitdeath.saveDeathCreditsFile();
        event.getPlayer().sendMessage("[LimitedDeath] Du hast derzeit " + limitdeath.getDeathCredits(event.getPlayer().getUniqueId()) + "/" + limitdeath.getMax_deathcredits() + " Todescredits");
        }
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        // Get player UUID
        UUID playerId = event.getPlayer().getUniqueId();

        // Check if the player has death credits
        if (limitdeath.hasDeathCredit(playerId)) {
            // Get the number of death credits
            int deathCredits = limitdeath.getDeathCredits(playerId);

            // If the player has no death credits, cancel the login
            if (deathCredits <= 0) {
                // Get the time left until the next regeneration
                long nextRegenTime = limitdeath.getNextRegenTime(playerId) / 1000;
                long currentTime = System.currentTimeMillis() / 1000; // Current time in seconds
                long timeLeft = nextRegenTime - currentTime;

                // Cancel the player login event
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "You have no death credits remaining. You will receive a new death credit in " + util.formatTime(timeLeft));
            }
        }
    }

    }

