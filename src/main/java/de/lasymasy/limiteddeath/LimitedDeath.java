package de.lasymasy.limiteddeath;

import de.lasymasy.limiteddeath.listener.onDeath;
import de.lasymasy.limiteddeath.listener.onJoin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public final class LimitedDeath extends JavaPlugin {

    private File deathCreditsFile;
    private FileConfiguration deathCreditsConfig;
    private HashMap<UUID, DeathCreditData> deathCredits = new HashMap<>();

    //Config Values
    String max_deathcredits;
    String max_deathcredits_daylimit;
    String deathcredits_regeneration_minutes;

    Integer max_deathcredits_int;
    Integer max_deathcredits_daylimit_int;
    Integer deathcredits_regeneration_minutes_int;

    boolean Debug = true;


    @Override
    public void onEnable() {
        saveDefaultConfig();
        readConfig();
        getServer().getPluginManager().registerEvents(new onJoin(this), this);
        getServer().getPluginManager().registerEvents(new onDeath(this), this);
        createDeathCreditsSaveFile();
        loadDeathCreditsFile();
        startDeathCreditsRegen();

        // Register commands
        getCommand("limitetdeath").setExecutor(new Commands(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void readConfig() {
        max_deathcredits = getConfig().getString("death_credit_limit");
        max_deathcredits_daylimit = getConfig().getString("death_credit_regen_per_day");
        deathcredits_regeneration_minutes = getConfig().getString("death_credit_regen_time");
        Debug = getConfig().getBoolean("debug");

        max_deathcredits_int = getConfig().getInt("death_credit_limit");
        max_deathcredits_daylimit_int = getConfig().getInt("death_credit_regen_per_day");
        deathcredits_regeneration_minutes_int = getConfig().getInt("death_credit_regen_time");
    }

    public void checkRegeneration() {
        getLogger().info("Checking regeneration of credits");
        loadDeathCreditsFile();
        long currentTime = System.currentTimeMillis() / 1000; // Current time in Unix timestamp
        for (UUID playerId : deathCredits.keySet()) {
            DeathCreditData data = deathCredits.get(playerId);
            getLogger().info("Checking Player " + playerId + " --- Data: Time_next_Gen_File: " + data.getNextRegenTime() + " Current Time: " + currentTime);
            if (data.getNextRegenTime() / 1000 <= currentTime) {
                getLogger().info("Player has passed Regentime");
                int currentCredits = data.getCredits();
                int currentNextRegenTime = (int) data.getNextRegenTime();
                int maxCredits = max_deathcredits_int;
                if (currentCredits < maxCredits) {
                    getLogger().info("Player hasnt reached maxCredits");
                    // Increment death credits and update next regeneration time
                    int regeneratedCredits = Math.min(currentCredits + 1, maxCredits);

                    //If Credits filled up to maxCredits set Regentime 0
                    if(regeneratedCredits == maxCredits) {
                        setDeathCredits(playerId, regeneratedCredits, 0);
                    }else{
                        setDeathCredits(playerId, regeneratedCredits, util.calculateUnixTimeInTwoHour());
                    }

                    sendMessagetoChat("DeathCredits sind Regeneriert worden auf " + regeneratedCredits, playerId);
                    getLogger().info("Player has regenerated");
                } else {
                    //check if this is the time were deathcredits get Full
                    if(currentNextRegenTime != 0){
                        //Send Notification That Credits are now Full
                        getLogger().info("Player has regenerated fully");
                        sendMessagetoChat("Deathcredits voll (Platzhalter Später überarbeiten", playerId);
                    }else{
                        getLogger().info("Player is already full");
                    }
                    setDeathCredits(playerId, maxCredits, 0);
                }
            }
        }
        saveDeathCreditsFile();
    }

    public void sendMessagetoChat(String message, UUID playerId){
        try {
            getServer().getPlayer(playerId).sendMessage(message);
            return;
        }catch(NullPointerException e){
            //This gets Thrown when Player is not online
            return;
        }
    }
    public void startDeathCreditsRegen() {
        int updateInterval = 60; //Updateinterval in Seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                checkRegeneration();
            }
        }.runTaskTimer(this, 0, updateInterval * 20);
    }

    public void createDeathCreditsSaveFile() {
        // Create or load death credits file
        deathCreditsFile = new File(getDataFolder(), "death_credits.yml");
        if (!deathCreditsFile.exists()) {
            deathCreditsFile.getParentFile().mkdirs();
            saveResource("death_credits.yml", false);
        }
        deathCreditsConfig = YamlConfiguration.loadConfiguration(deathCreditsFile);
    }
    public void saveDeathCreditsFile() {
        try {
            sendDebugMessage("Saving Deathcredits file");
            deathCreditsConfig.save(deathCreditsFile);
        } catch (IOException e) {
            getLogger().warning("Could not save death credits file: " + e.getMessage());
        }
    }

    public void loadDeathCreditsFile() {
        sendDebugMessage("Loading deathcredits file");
        for (String key : deathCreditsConfig.getKeys(false)) {
            UUID playerId = UUID.fromString(key);
            int credits = deathCreditsConfig.getInt(key + ".credits");
            long nextRegenTime = deathCreditsConfig.getLong(key + ".nextRegenTime");
            deathCredits.put(playerId, new DeathCreditData(credits, nextRegenTime));
        }
    }

    public void setDeathCredits(UUID playerId, int credits, long nextRegenTime) {
        sendDebugMessage("set Deathcredits from " + playerId + " to " + credits + " Next Regen " + nextRegenTime);
        deathCredits.put(playerId, new DeathCreditData(credits, nextRegenTime));
        deathCreditsConfig.set(playerId.toString() + ".credits", credits);
        deathCreditsConfig.set(playerId.toString() + ".nextRegenTime", nextRegenTime);
        saveDeathCreditsFile();
    }

    public int getDeathCredits(UUID playerId) {
        return deathCredits.getOrDefault(playerId, new DeathCreditData(0, 0)).getCredits();
    }

    public long getNextRegenTime(UUID playerId) {
        return deathCredits.getOrDefault(playerId, new DeathCreditData(0, 0)).getNextRegenTime();
    }
    public boolean hasDeathCredit(UUID playerId) {
        return deathCredits.containsKey(playerId);
    }

    public String getMax_deathcredits(){
        return max_deathcredits;
    }

    public Collection<UUID> getAllPlayers() {
        return deathCredits.keySet();
    }

    public void sendDebugMessage(String message){
        if(Debug){
            getLogger().info("[DEBUG]" + message);
        }
    }
    private static class DeathCreditData {
        private final int credits;
        private final long nextRegenTime;

        public DeathCreditData(int credits, long nextRegenTime) {
            this.credits = credits;
            this.nextRegenTime = nextRegenTime;
        }

        public int getCredits() {
            return credits;
        }

        public long getNextRegenTime() {
            return nextRegenTime;
        };

    }
}
