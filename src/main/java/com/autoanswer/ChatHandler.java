package com.autoanswer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class ChatHandler {
    private static long lastAnswerTime = 0;
    private static String pendingAnswer = null;
    private static long answerScheduledTime = 0;

    public static void handleChatMessage(String message) {
        // Check if mod is enabled
        if (!ConfigManager.isEnabled()) {
            return;
        }

        String answer = null;

        // First, try to solve as math equation
        answer = MathSolver.solveComplexExpression(message);
        
        if (answer != null) {
            AutoAnswerMod.LOGGER.info("Detected math equation. Answer: {}", answer);
        } else {
            // If not math, check for trivia question
            answer = ConfigManager.getTriviaAnswer(message);
            if (answer != null) {
                AutoAnswerMod.LOGGER.info("Detected trivia question. Answer: {}", answer);
            }
        }

        // Schedule answer with delay if found
        if (answer != null) {
            scheduleAnswer(answer);
        }
    }

    private static void scheduleAnswer(String answer) {
        long currentTime = System.currentTimeMillis();
        long answerDelay = ConfigManager.getAnswerDelay();
        
        // Store the answer and schedule time
        pendingAnswer = answer;
        answerScheduledTime = currentTime + answerDelay;
        
        AutoAnswerMod.LOGGER.info("Scheduled answer '{}' to be sent in {}ms", answer, answerDelay);
    }

    public static void tick() {
        // Check if mod is enabled
        if (!ConfigManager.isEnabled()) {
            pendingAnswer = null;
            return;
        }

        // Check if we have a pending answer to send
        if (pendingAnswer != null) {
            long currentTime = System.currentTimeMillis();
            
            if (currentTime >= answerScheduledTime) {
                sendChatMessage(pendingAnswer);
                pendingAnswer = null;
                lastAnswerTime = currentTime;
            }
        }
    }

    private static void sendChatMessage(String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        
        if (player != null && client.getNetworkHandler() != null) {
            player.networkHandler.sendChatMessage(message);
            AutoAnswerMod.LOGGER.info("Sent answer: {}", message);
        }
    }
}
