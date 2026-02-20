package com.autoanswer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Paths.get("config", "autoanswer.json");
    private static final Path SETTINGS_PATH = Paths.get("config", "autoanswer_settings.json");
    private static Map<String, String> triviaAnswers = new HashMap<>();
    private static int answerDelay = 1000; // Default 1 second delay in milliseconds
    private static boolean enabled = true; // Default enabled

    public static void loadConfig() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());

            if (!Files.exists(CONFIG_PATH)) {
                createDefaultConfig();
            }

            Reader reader = Files.newBufferedReader(CONFIG_PATH);
            triviaAnswers = GSON.fromJson(reader, new TypeToken<Map<String, String>>(){}.getType());
            reader.close();

            if (triviaAnswers == null) {
                triviaAnswers = new HashMap<>();
            }

            AutoAnswerMod.LOGGER.info("Loaded {} trivia answers from config", triviaAnswers.size());
        } catch (Exception e) {
            AutoAnswerMod.LOGGER.error("Failed to load config", e);
            triviaAnswers = new HashMap<>();
        }
        
        // Load settings
        loadSettings();
    }
    
    private static void loadSettings() {
        try {
            if (!Files.exists(SETTINGS_PATH)) {
                saveSettings();
                return;
            }

            Reader reader = Files.newBufferedReader(SETTINGS_PATH);
            Map<String, Object> settings = GSON.fromJson(reader, new TypeToken<Map<String, Object>>(){}.getType());
            reader.close();

            if (settings != null) {
                if (settings.containsKey("answerDelay")) {
                    Object delay = settings.get("answerDelay");
                    if (delay instanceof Number) {
                        answerDelay = ((Number) delay).intValue();
                    }
                }
                
                if (settings.containsKey("enabled")) {
                    Object enabledObj = settings.get("enabled");
                    if (enabledObj instanceof Boolean) {
                        enabled = (Boolean) enabledObj;
                    }
                }
            }

            AutoAnswerMod.LOGGER.info("Loaded settings. Answer delay: {}ms, Enabled: {}", answerDelay, enabled);
        } catch (Exception e) {
            AutoAnswerMod.LOGGER.error("Failed to load settings", e);
        }
    }
    
    public static void saveSettings() {
        try {
            Files.createDirectories(SETTINGS_PATH.getParent());
            
            Map<String, Object> settings = new HashMap<>();
            settings.put("answerDelay", answerDelay);
            settings.put("enabled", enabled);
            
            Writer writer = Files.newBufferedWriter(SETTINGS_PATH);
            GSON.toJson(settings, writer);
            writer.close();
            
            AutoAnswerMod.LOGGER.info("Saved settings");
        } catch (Exception e) {
            AutoAnswerMod.LOGGER.error("Failed to save settings", e);
        }
    }

    private static void createDefaultConfig() throws IOException {
        Map<String, String> defaultAnswers = new HashMap<>();
        // Empty config - add your own custom prompts and answers here
        // Example: "your prompt": "your answer"
        
        Writer writer = Files.newBufferedWriter(CONFIG_PATH);
        GSON.toJson(defaultAnswers, writer);
        writer.close();
        
        AutoAnswerMod.LOGGER.info("Created empty config file at {}", CONFIG_PATH);
    }

    public static String getTriviaAnswer(String question) {
        // Try exact match first
        for (Map.Entry<String, String> entry : triviaAnswers.entrySet()) {
            if (question.toLowerCase().contains(entry.getKey().toLowerCase())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static Map<String, String> getTriviaAnswers() {
        return new HashMap<>(triviaAnswers);
    }

    public static Path getConfigPath() {
        return CONFIG_PATH;
    }
    
    public static int getAnswerDelay() {
        return answerDelay;
    }
    
    public static void setAnswerDelay(int delay) {
        answerDelay = Math.max(0, Math.min(10000, delay)); // Clamp between 0-10 seconds
        saveSettings();
    }
    
    public static boolean isEnabled() {
        return enabled;
    }
    
    public static void setEnabled(boolean value) {
        enabled = value;
        saveSettings();
        AutoAnswerMod.LOGGER.info("Auto Answer Mod {}", enabled ? "enabled" : "disabled");
        
        // Send chat message notification
        sendToggleMessage();
    }
    
    private static void sendToggleMessage() {
        net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
        if (client.player != null) {
            net.minecraft.text.Text message;
            if (enabled) {
                // Green message for enabled
                message = net.minecraft.text.Text.literal("Mod Enabled!").styled(style -> 
                    style.withColor(net.minecraft.util.Formatting.GREEN));
            } else {
                // Red message for disabled
                message = net.minecraft.text.Text.literal("Mod Disabled!").styled(style -> 
                    style.withColor(net.minecraft.util.Formatting.RED));
            }
            client.player.sendMessage(message, false);
        }
    }
    
    public static void toggleEnabled() {
        setEnabled(!enabled);
    }
}
