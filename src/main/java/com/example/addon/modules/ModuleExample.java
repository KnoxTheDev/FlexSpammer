package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

public class ModuleExample extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // Settings
    private final Setting<String> baseMessage = sgGeneral.add(new StringSetting.Builder()
        .name("base-message")
        .description("Base message to spam.")
        .defaultValue("Knoxius is the king and Ryan is world's best dev.")
        .build()
    );

    private final Setting<Integer> interval = sgGeneral.add(new IntSetting.Builder()
        .name("interval")
        .description("Delay between messages in milliseconds.")
        .defaultValue(3050)
        .min(500)
        .sliderMax(10000)
        .build()
    );

    private final Setting<String> suffixChars = sgGeneral.add(new StringSetting.Builder()
        .name("suffix-chars")
        .description("Characters used for suffix variation.")
        .defaultValue("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")
        .build()
    );

    private int index = 0;
    private long lastMessageTime = 0L;

    public ModuleExample() {
        super(AddonTemplate.CATEGORY, "flex-spammer", "Spams chat with a flexible message and suffix variation.");
    }

    @Override
    public void onActivate() {
        index = 0;
        lastMessageTime = 0L;
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMessageTime < interval.get()) return;

        String suffix = "";
        String chars = suffixChars.get();
        if (!chars.isEmpty()) {
            suffix = "     " + chars.charAt(index); // 5 spaces + suffix char
            index = (index + 1) % chars.length();
        }

        String msg = baseMessage.get() + suffix;

        mc.player.networkHandler.sendPacket(new ChatMessageC2SPacket(msg));
        info("Sent: " + msg.replace(" ", "·"));

        lastMessageTime = currentTime;
    }
}
