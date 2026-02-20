package com.autoanswer.mixin;

import com.autoanswer.ToggleButton;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        // Only handle left click (button 0) on press (action 1)
        if (button == 0 && action == 1) {
            Mouse mouse = (Mouse) (Object) this;
            double mouseX = mouse.getX() * net.minecraft.client.MinecraftClient.getInstance().getWindow().getScaledWidth() / 
                           net.minecraft.client.MinecraftClient.getInstance().getWindow().getWidth();
            double mouseY = mouse.getY() * net.minecraft.client.MinecraftClient.getInstance().getWindow().getScaledHeight() / 
                           net.minecraft.client.MinecraftClient.getInstance().getWindow().getHeight();
            
            if (ToggleButton.handleClick(mouseX, mouseY)) {
                ci.cancel();
            }
        }
    }
}
