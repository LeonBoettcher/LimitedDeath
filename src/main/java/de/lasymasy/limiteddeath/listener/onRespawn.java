package de.lasymasy.limiteddeath.listener;

import de.lasymasy.limiteddeath.LimitedDeath;
import de.lasymasy.limiteddeath.util;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class onRespawn implements Listener {
    private LimitedDeath limitdeath;
    public onRespawn(LimitedDeath limitdeath) {
        this.limitdeath = limitdeath;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {

        if(limitdeath.getNextRegenTime(event.getPlayer().getUniqueId()) == 0){
            //set regen time to in 2h, player was full of death credits
            limitdeath.setDeathCredits(event.getPlayer().getUniqueId(), limitdeath.getDeathCredits(event.getPlayer().getUniqueId()) - 1, util.calculateUnixTimeInTwoHour());
        }else{
            limitdeath.setDeathCredits(event.getPlayer().getUniqueId(), limitdeath.getDeathCredits(event.getPlayer().getUniqueId()) - 1, limitdeath.getNextRegenTime(event.getPlayer().getUniqueId()));
        }
        event.getPlayer().sendMessage("[LimitedDeath] Du hast jetzt noch " + limitdeath.getDeathCredits(event.getPlayer().getUniqueId()) + "/" + limitdeath.getMax_deathcredits() + " Todescredits");



        if(limitdeath.getDeathCredits(event.getPlayer().getUniqueId()) == 0){

            //Handle 0 credits
            long nextRegenTime = limitdeath.getNextRegenTime(event.getPlayer().getUniqueId()) / 1000;
            long currentTime = System.currentTimeMillis() / 1000; // Current time in seconds
            long timeLeft = nextRegenTime - currentTime;

            // Kick the player with a message indicating the time left until the next regeneration
            event.getPlayer().kickPlayer("You have no death credits remaining. You will receive a new death credit in " + util.formatTime(timeLeft));
        }
    }
}
