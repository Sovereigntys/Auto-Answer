package com.autoanswer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoAnswerMod implements ClientModInitializer {
    public static final String MOD_ID = "autoanswer";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Auto Answer Mod initialized!");
        ConfigManager.loadConfig();
        
        // Register tick event to handle delayed answers
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ChatHandler.tick();
        });
    }
}
