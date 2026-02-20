package com.autoanswer;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class ToggleButton {
    private static final int BUTTON_WIDTH = 60;
    private static final int BUTTON_HEIGHT = 20;
    private static final int PADDING = 5;
    
    public static void register() {
        HudRenderCallback.EVENT.register(ToggleButton::render);
    }
    
    private static void render(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        if (client.options.hudHidden) {
            return;
        }
        
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        
        // Position in bottom right corner
        int buttonX = screenWidth - BUTTON_WIDTH - PADDING;
        int buttonY = screenHeight - BUTTON_HEIGHT - PADDING;
        
        // Check if mouse is hovering over button
        double mouseX = client.mouse.getX() * client.getWindow().getScaledWidth() / client.getWindow().getWidth();
        double mouseY = client.mouse.getY() * client.getWindow().getScaledHeight() / client.getWindow().getHeight();
        boolean isHovering = mouseX >= buttonX && mouseX <= buttonX + BUTTON_WIDTH && 
                             mouseY >= buttonY && mouseY <= buttonY + BUTTON_HEIGHT;
        
        // Determine colors based on enabled state
        boolean enabled = ConfigManager.isEnabled();
        int backgroundColor = enabled ? 0x8000FF00 : 0x80FF0000; // Green if enabled, red if disabled
        int hoverColor = enabled ? 0xA000FF00 : 0xA0FF0000; // Brighter when hovering
        int textColor = 0xFFFFFFFF;
        
        // Draw button background
        int bgColor = isHovering ? hoverColor : backgroundColor;
        drawContext.fill(buttonX, buttonY, buttonX + BUTTON_WIDTH, buttonY + BUTTON_HEIGHT, bgColor);
        
        // Draw button border
        drawContext.fill(buttonX, buttonY, buttonX + BUTTON_WIDTH, buttonY + 1, 0xFF000000); // Top
        drawContext.fill(buttonX, buttonY + BUTTON_HEIGHT - 1, buttonX + BUTTON_WIDTH, buttonY + BUTTON_HEIGHT, 0xFF000000); // Bottom
        drawContext.fill(buttonX, buttonY, buttonX + 1, buttonY + BUTTON_HEIGHT, 0xFF000000); // Left
        drawContext.fill(buttonX + BUTTON_WIDTH - 1, buttonY, buttonX + BUTTON_WIDTH, buttonY + BUTTON_HEIGHT, 0xFF000000); // Right
        
        // Draw text centered in button
        String text = enabled ? "AA: ON" : "AA: OFF";
        int textWidth = client.textRenderer.getWidth(text);
        int textX = buttonX + (BUTTON_WIDTH - textWidth) / 2;
        int textY = buttonY + (BUTTON_HEIGHT - client.textRenderer.fontHeight) / 2;
        
        drawContext.drawText(client.textRenderer, text, textX, textY, textColor, true);
    }
    
    public static boolean handleClick(double mouseX, double mouseY) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        
        int buttonX = screenWidth - BUTTON_WIDTH - PADDING;
        int buttonY = screenHeight - BUTTON_HEIGHT - PADDING;
        
        // Check if click is within button bounds
        if (mouseX >= buttonX && mouseX <= buttonX + BUTTON_WIDTH && 
            mouseY >= buttonY && mouseY <= buttonY + BUTTON_HEIGHT) {
            ConfigManager.toggleEnabled();
            return true;
        }
        
        return false;
    }
}
