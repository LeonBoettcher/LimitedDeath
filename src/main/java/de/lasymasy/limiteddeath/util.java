package de.lasymasy.limiteddeath;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class util {
    public static long calculateUnixTimeInTwoHour() {
        // Get the current time in milliseconds since Unix epoch
        long currentTimeMillis = System.currentTimeMillis();

        // Add 1 hour worth of milliseconds (3600 seconds * 1000 milliseconds)
        long oneHourMillis = 3600 * 1000;

        long twoHoursMillis = oneHourMillis * 2;

        // Calculate the Unix time for 1 hour from the current time
        long unixTimeInOneHour = currentTimeMillis + twoHoursMillis;

        return unixTimeInOneHour;
    }

    public static String formatTime(long seconds) {
        long hours = seconds / 3600;
        seconds %= 3600;
        long minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    // Method to get UUID from player name
    public static String getUUIDFromName(String playerName) {
        try {
            String urlString = "https://api.mojang.com/users/profiles/minecraft/" + playerName;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader inputReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = inputReader.readLine()) != null) {
                response.append(inputLine);
            }
            inputReader.close();

            JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
            String rawUUID = jsonObject.get("id").getAsString();

            // Insert hyphens into the UUID string at appropriate positions
            String formattedUUID = String.format(
                    "%s-%s-%s-%s-%s",
                    rawUUID.substring(0, 8),
                    rawUUID.substring(8, 12),
                    rawUUID.substring(12, 16),
                    rawUUID.substring(16, 20),
                    rawUUID.substring(20)
            );

            return formattedUUID;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to get player name from UUID
    public static String getNameFromUUID(String uuid) {
        try {
            String urlString = "https://api.mojang.com/user/profile/" + uuid;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = inputReader.readLine()) != null) {
                    response.append(inputLine);
                }
                inputReader.close();

                JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
                return jsonObject.get("name").getAsString();
            } else {
                System.out.println("Error: HTTP status code " + responseCode);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}


