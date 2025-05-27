package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class FlexSpammer extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> baseMessage = sgGeneral.add(new StringSetting.Builder()
        .name("base-message")
        .description("The message to send with random suffix.")
        .defaultValue("@everyone Knoxius is the king.")
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
            String fullMessage = baseMessage.get() + "     " + suffix;

            mc.player.sendChatMessage(fullMessage); // Sends the chat message

            index = (index + 1) % asciiChars.length;
            lastMessageTime = System.currentTimeMillis();
        }
    }
}
