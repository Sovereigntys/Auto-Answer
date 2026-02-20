package com.autoanswer;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.io.File;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        AutoAnswerMod.LOGGER.info("ModMenu requesting config screen factory!");
        return parent -> {
            AutoAnswerMod.LOGGER.info("Creating config screen with parent: " + parent);
            return new ConfigScreen(parent);
        };
    }

    private static class ConfigScreen extends Screen {
        private final Screen parent;
        private static final Text TITLE = Text.literal("Auto Answer Config");

        protected ConfigScreen(Screen parent) {
            super(TITLE);
            this.parent = parent;
        }

        @Override
        protected void init() {
            // Open config file button
            this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Open Config File"),
                button -> {
                    File configFile = ConfigManager.getConfigPath().toFile();
                    Util.getOperatingSystem().open(configFile.getParentFile());
                }
            ).dimensions(this.width / 2 - 100, this.height / 2 - 50, 200, 20).build());

            // Reload config button
            this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Reload Config"),
                button -> {
                    ConfigManager.loadConfig();
                    if (client != null && client.player != null) {
                        client.player.sendMessage(Text.literal("§aConfig reloaded!"), false);
                    }
                }
            ).dimensions(this.width / 2 - 100, this.height / 2 - 25, 200, 20).build());

            // How to Use button
            this.addDrawableChild(ButtonWidget.builder(
                Text.literal("How to Use"),
                button -> {
                    if (client != null) {
                        client.setScreen(new InfoScreen(this));
                    }
                }
            ).dimensions(this.width / 2 - 100, this.height / 2, 200, 20).build());

            // Answer Delay button
            this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Answer Delay"),
                button -> {
                    if (client != null) {
                        client.setScreen(new DelayConfigScreen(this));
                    }
                }
            ).dimensions(this.width / 2 - 100, this.height / 2 + 25, 200, 20).build());

            // Toggle Answering button (bottom right)
            this.addDrawableChild(ButtonWidget.builder(
                Text.literal(ConfigManager.isEnabled() ? "§aAnswering: ON" : "§cAnswering: OFF"),
                button -> {
                    ConfigManager.toggleEnabled();
                    boolean enabled = ConfigManager.isEnabled();
                    
                    // Update button text
                    button.setMessage(Text.literal(enabled ? "§aAnswering: ON" : "§cAnswering: OFF"));
                    
                    // ConfigManager.toggleEnabled() already sends the chat message
                }
            ).dimensions(this.width - 110, this.height - 30, 100, 20).build());

            // Done button
            this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Done"),
                button -> {
                    if (client != null) {
                        client.setScreen(parent);
                    }
                }
            ).dimensions(this.width / 2 - 100, this.height / 2 + 55, 200, 20).build());
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            super.render(context, mouseX, mouseY, delta);
            context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
            
            // Display current config info
            int loadedAnswers = ConfigManager.getTriviaAnswers().size();
            String info = "§7Loaded custom prompts: §f" + loadedAnswers;
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(info), this.width / 2, this.height / 2 - 70, 0xFFFFFF);
            
            // Instructions
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("§7Edit config/autoanswer.json to add prompts"), this.width / 2, this.height / 2 + 85, 0xAAAAAA);
        }

        @Override
        public void close() {
            if (client != null) {
                client.setScreen(parent);
            }
        }
    }

    private static class DelayConfigScreen extends Screen {
        private final Screen parent;
        private static final Text TITLE = Text.literal("Answer Delay Config");
        private int currentDelay;

        protected DelayConfigScreen(Screen parent) {
            super(TITLE);
            this.parent = parent;
            this.currentDelay = ConfigManager.getAnswerDelay();
        }

        @Override
        protected void init() {
            // Delay adjustment buttons
            int delayY = this.height / 2;
            
            // -1000ms button
            this.addDrawableChild(ButtonWidget.builder(
                Text.literal("-1s"),
                button -> {
                    currentDelay = Math.max(0, currentDelay - 1000);
                    ConfigManager.setAnswerDelay(currentDelay);
                }
            ).dimensions(this.width / 2 - 155, delayY, 50, 20).build());
            
            // -100ms button
            this.addDrawableChild(ButtonWidget.builder(
                Text.literal("-100ms"),
                button -> {
                    currentDelay = Math.max(0, currentDelay - 100);
                    ConfigManager.setAnswerDelay(currentDelay);
                }
            ).dimensions(this.width / 2 - 100, delayY, 60, 20).build());
            
            // +100ms button
            this.addDrawableChild(ButtonWidget.builder(
                Text.literal("+100ms"),
                button -> {
                    currentDelay = Math.min(10000, currentDelay + 100);
                    ConfigManager.setAnswerDelay(currentDelay);
                }
            ).dimensions(this.width / 2 + 40, delayY, 60, 20).build());
            
            // +1000ms button
            this.addDrawableChild(ButtonWidget.builder(
                Text.literal("+1s"),
                button -> {
                    currentDelay = Math.min(10000, currentDelay + 1000);
                    ConfigManager.setAnswerDelay(currentDelay);
                }
            ).dimensions(this.width / 2 + 105, delayY, 50, 20).build());

            // Done button
            this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Done"),
                button -> {
                    if (client != null) {
                        client.setScreen(parent);
                    }
                }
            ).dimensions(this.width / 2 - 100, this.height / 2 + 40, 200, 20).build());
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            super.render(context, mouseX, mouseY, delta);
            context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
            
            // Display delay settings label above the delay buttons
            currentDelay = ConfigManager.getAnswerDelay();
            String delayText = "§6Current Delay: §f" + currentDelay + "ms §7(" + String.format("%.1f", currentDelay / 1000.0) + "s)";
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(delayText), this.width / 2, this.height / 2 - 30, 0xFFFFFF);
            
            // Instructions
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("§7Adjust the delay between auto-answers"), this.width / 2, this.height / 2 + 70, 0xAAAAAA);
        }

        @Override
        public void close() {
            if (this.client != null) {
                this.client.setScreen(this.parent);
            }
        }
    }

    private static class InfoScreen extends Screen {
        private final Screen parent;
        private static final Text TITLE = Text.literal("How to Use Auto Answer");

        protected InfoScreen(Screen parent) {
            super(TITLE);
            this.parent = parent;
        }

        @Override
        protected void init() {
            this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Back"),
                button -> {
                    if (client != null) {
                        client.setScreen(parent);
                    }
                }
            ).dimensions(this.width / 2 - 100, this.height - 40, 200, 20).build());
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            super.render(context, mouseX, mouseY, delta);
            context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);

            int y = 50;
            int lineHeight = 12;

            // Instructions
            context.drawText(this.textRenderer, Text.literal("§6Math Equations:"), this.width / 2 - 150, y, 0xFFFFFF, false);
            y += lineHeight + 5;
            context.drawText(this.textRenderer, Text.literal("§7• Automatically solves math in chat"), this.width / 2 - 150, y, 0xFFFFFF, false);
            y += lineHeight;
            context.drawText(this.textRenderer, Text.literal("§7• Examples: 5+3, 12*4, (5+3)*2"), this.width / 2 - 150, y, 0xFFFFFF, false);
            y += lineHeight * 2;

            context.drawText(this.textRenderer, Text.literal("§6Custom Prompts:"), this.width / 2 - 150, y, 0xFFFFFF, false);
            y += lineHeight + 5;
            context.drawText(this.textRenderer, Text.literal("§7• Edit config/autoanswer.json"), this.width / 2 - 150, y, 0xFFFFFF, false);
            y += lineHeight;
            context.drawText(this.textRenderer, Text.literal("§7• Add your prompts like this:"), this.width / 2 - 150, y, 0xFFFFFF, false);
            y += lineHeight + 5;
            context.drawText(this.textRenderer, Text.literal("§e  \"your prompt\": \"your answer\""), this.width / 2 - 150, y, 0xFFFFFF, false);
            y += lineHeight * 2;

            context.drawText(this.textRenderer, Text.literal("§6Example Config:"), this.width / 2 - 150, y, 0xFFFFFF, false);
            y += lineHeight + 5;
            context.drawText(this.textRenderer, Text.literal("§e{"), this.width / 2 - 150, y, 0xFFFFFF, false);
            y += lineHeight;
            context.drawText(this.textRenderer, Text.literal("§e  \"red\": \"Sovereigntys\","), this.width / 2 - 150, y, 0xFFFFFF, false);
            y += lineHeight;
            context.drawText(this.textRenderer, Text.literal("§e  \"blue\": \"Skywars\""), this.width / 2 - 150, y, 0xFFFFFF, false);
            y += lineHeight;
            context.drawText(this.textRenderer, Text.literal("§e}"), this.width / 2 - 150, y, 0xFFFFFF, false);
            y += lineHeight * 2;

            context.drawText(this.textRenderer, Text.literal("§7The mod will auto-answer when it sees"), this.width / 2 - 150, y, 0xFFFFFF, false);
            y += lineHeight;
            context.drawText(this.textRenderer, Text.literal("§7your prompts in chat!"), this.width / 2 - 150, y, 0xFFFFFF, false);
        }

        @Override
        public void close() {
            if (client != null) {
                client.setScreen(parent);
            }
        }
    }
}
