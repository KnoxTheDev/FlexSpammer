package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket; // Keep this
import net.minecraft.text.Text; // May need this if direct String constructor is problematic
import net.minecraft.client.network.ClientPlayerEntity; // Explicitly import ClientPlayerEntity for clarity

public class FlexSpammer extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> baseMessage = sgGeneral.add(new StringSetting.Builder()
        .name("base-message")
        .description("The message to send with random suffix.")
        .defaultValue("@everyone Knoxius is the king and Ryan is the world's best developer.")
        .build()
    );

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay-ms")
        .description("Delay between messages in milliseconds.")
        .defaultValue(3050)
        .min(1000)
        .sliderMax(10000)
        .build()
    );

    private final char[] asciiChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    private int index = 0;
    private long lastMessageTime = 0;

    public FlexSpammer() {
        super(AddonTemplate.CATEGORY, "flex-spammer", "Sends a message with anti-spam variation to bypass debounce.");
    }

    // Removed @Override from onTick as it's not overriding a superclass method
    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (System.currentTimeMillis() - lastMessageTime < delay.get()) return;

        if (mc.player != null) {
            String suffix = String.valueOf(asciiChars[index]);
            String fullMessage = baseMessage.get() + "       " + suffix;

            // This is the most common and robust way to send a chat message packet
            // for versions where .sendChatMessage(String) isn't directly available
            // but the String constructor for ChatMessageC2SPacket exists.
            // If this still fails, your version of ChatMessageC2SPacket might
            // require a Text object, or be an even older/different constructor.
            mc.player.networkHandler.sendPacket(new ChatMessageC2SPacket(fullMessage));

            index = (index + 1) % asciiChars.length;
            lastMessageTime = System.currentTimeMillis();
        }
    }

    @Override
    public void onActivate() {
        super.onActivate();
        // Reset state when module is activated
        index = 0;
        lastMessageTime = 0;
    }

    @Override
    public void onDeactivate() {
        super.onDeactivate();
        // Any cleanup if necessary
    }
}
