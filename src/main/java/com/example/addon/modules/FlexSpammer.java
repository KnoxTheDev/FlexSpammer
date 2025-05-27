package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.text.Text; // Although Text is imported, it's not strictly needed for sending raw strings.
// import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents; // This import is no longer needed

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

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (System.currentTimeMillis() - lastMessageTime < delay.get()) return;

        if (mc.player != null) {
            String suffix = String.valueOf(asciiChars[index]);
            String fullMessage = baseMessage.get() + "       " + suffix; // Added more spaces for better visibility of suffix

            // Correct way to send a chat message from the client
            mc.player.networkHandler.sendPacket(new ChatMessageC2SPacket(fullMessage));

            index = (index + 1) % asciiChars.length;
            lastMessageTime = System.currentTimeMillis();
        }
    }
}
