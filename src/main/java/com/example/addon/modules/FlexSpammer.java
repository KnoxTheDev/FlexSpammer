package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class FlexSpammer extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final IntSetting delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay-ms")
        .description("Base delay between messages in milliseconds (adaptive jitter applied).")
        .defaultValue(3050)
        .min(1000)
        .sliderMax(10000)
        .build()
    );

    private final String jsonMessagesFile = "flex_msgs.json";
    private final String fallbackMessage = "Meteor on crack!";

    private List<String> rawMessages = new ArrayList<>();
    private int msgPointer = 0;
    private String lastRawMsg = "";

    private final char[] asciiChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    private int asciiIndex = 0;
    private long lastMessageTime = 0;

    private static final Map<Character, char[]> homoglyphsMap = new HashMap<>();
    static {
        // Lowercase letters
        homoglyphsMap.put('a', new char[]{'а', 'ɑ', 'α'});     // Cyrillic a, Latin alpha
        homoglyphsMap.put('b', new char[]{'Ь', 'Ƅ'});          // Cyrillic soft sign, Latin letter tone six
        homoglyphsMap.put('c', new char[]{'с', 'ϲ'});          // Cyrillic es, Greek lunate sigma
        homoglyphsMap.put('d', new char[]{'ԁ', 'ɗ'});          // Cyrillic d, Latin d with hook
        homoglyphsMap.put('e', new char[]{'е', 'ҽ', 'ɛ'});     // Cyrillic e, Cyrillic e variant, Latin open e
        homoglyphsMap.put('f', new char[]{'ғ', 'ƒ'});          // Cyrillic f, Latin f with hook
        homoglyphsMap.put('g', new char[]{'ɡ'});                // Latin script g
        homoglyphsMap.put('h', new char[]{'һ', 'н'});          // Cyrillic h and n
        homoglyphsMap.put('i', new char[]{'і', 'ɩ', 'í'});     // Cyrillic i, Latin iota, Latin i acute
        homoglyphsMap.put('j', new char[]{'ј', 'ʝ'});          // Cyrillic j, Latin j with hook
        homoglyphsMap.put('k', new char[]{'κ', 'к'});          // Greek kappa, Cyrillic k
        homoglyphsMap.put('l', new char[]{'ⅼ', 'ӏ', 'ι'});     // Roman numeral 50, Cyrillic palochka, Greek iota
        homoglyphsMap.put('m', new char[]{'ｍ', 'м'});          // Fullwidth m, Cyrillic m
        homoglyphsMap.put('n', new char[]{'ո', 'п'});          // Armenian n, Cyrillic p
        homoglyphsMap.put('o', new char[]{'о', 'օ', 'σ'});     // Cyrillic o, Armenian o, Greek sigma
        homoglyphsMap.put('p', new char[]{'р', 'ƿ'});          // Cyrillic r, Latin wynn
        homoglyphsMap.put('q', new char[]{'զ'});                // Armenian z
        homoglyphsMap.put('r', new char[]{'г', 'ɾ'});          // Cyrillic g, Latin r with hook
        homoglyphsMap.put('s', new char[]{'ѕ', 'ʂ'});          // Cyrillic s, Latin s with hook
        homoglyphsMap.put('t', new char[]{'т', 'ƚ'});          // Cyrillic t, Latin t with stroke
        homoglyphsMap.put('u', new char[]{'υ', 'ս'});          // Greek upsilon, Armenian s
        homoglyphsMap.put('v', new char[]{'ѵ', 'ν'});          // Cyrillic v, Greek nu
        homoglyphsMap.put('w', new char[]{'ѡ', 'ա'});          // Cyrillic omega, Armenian a
        homoglyphsMap.put('x', new char[]{'х', '×'});          // Cyrillic x, multiplication sign
        homoglyphsMap.put('y', new char[]{'у', 'ƴ'});          // Cyrillic u, Latin y with hook
        homoglyphsMap.put('z', new char[]{'ᴢ'});                // Latin small capital z

        // Uppercase letters
        homoglyphsMap.put('A', new char[]{'Α', 'А'});           // Greek Alpha, Cyrillic A
        homoglyphsMap.put('B', new char[]{'Β', 'В'});           // Greek Beta, Cyrillic Ve
        homoglyphsMap.put('C', new char[]{'Ϲ', 'С'});           // Greek lunate sigma, Cyrillic Es
        homoglyphsMap.put('D', new char[]{'Ԁ'});                // Cyrillic D
        homoglyphsMap.put('E', new char[]{'Ε', 'Е'});           // Greek Epsilon, Cyrillic E
        homoglyphsMap.put('F', new char[]{'Ғ'});                // Cyrillic F
        homoglyphsMap.put('G', new char[]{'Ԍ'});                // Cyrillic G
        homoglyphsMap.put('H', new char[]{'Η', 'Н'});           // Greek Eta, Cyrillic En
        homoglyphsMap.put('I', new char[]{'Ι', 'І', 'Ӏ'});      // Greek Iota, Cyrillic I, Cyrillic Palochka
        homoglyphsMap.put('J', new char[]{'Ј'});                // Cyrillic Je
        homoglyphsMap.put('K', new char[]{'Κ', 'К'});           // Greek Kappa, Cyrillic Ka
        homoglyphsMap.put('L', new char[]{'ʟ'});                // Latin small capital L
        homoglyphsMap.put('M', new char[]{'Μ', 'М'});           // Greek Mu, Cyrillic Em
        homoglyphsMap.put('N', new char[]{'Ν', 'Н'});           // Greek Nu, Cyrillic En
        homoglyphsMap.put('O', new char[]{'Ο', 'О'});           // Greek Omicron, Cyrillic O
        homoglyphsMap.put('P', new char[]{'Ρ', 'Р'});           // Greek Rho, Cyrillic Er
        homoglyphsMap.put('Q', new char[]{'Ⴓ'});                // Georgian Q
        homoglyphsMap.put('R', new char[]{'Ʀ', 'Р'});           // Latin capital R with tail, Cyrillic Er
        homoglyphsMap.put('S', new char[]{'Ѕ'});                // Cyrillic Dze
        homoglyphsMap.put('T', new char[]{'Τ', 'Т'});           // Greek Tau, Cyrillic Te
        homoglyphsMap.put('U', new char[]{'Ս'});                // Armenian Se
        homoglyphsMap.put('V', new char[]{'Ѵ', 'Ⅴ'});           // Cyrillic Izhitsa, Roman numeral five
        homoglyphsMap.put('W', new char[]{'Ԝ'});                // Cyrillic We
        homoglyphsMap.put('X', new char[]{'Χ', 'Х'});           // Greek Chi, Cyrillic Ha
        homoglyphsMap.put('Y', new char[]{'Υ', 'Ү'});           // Greek Upsilon, Cyrillic Ue
        homoglyphsMap.put('Z', new char[]{'Ζ'});                // Greek Zeta

        // Digits
        homoglyphsMap.put('0', new char[]{'О', 'Ο', 'Օ'});      // Cyrillic O, Greek Omicron, Armenian O
        homoglyphsMap.put('1', new char[]{'Ι', 'І', 'Ӏ'});      // Greek Iota, Cyrillic I, Cyrillic Palochka
        homoglyphsMap.put('2', new char[]{'２'});               // Fullwidth 2
        homoglyphsMap.put('3', new char[]{'З'});                // Cyrillic Ze
        homoglyphsMap.put('4', new char[]{'４'});               // Fullwidth 4
        homoglyphsMap.put('5', new char[]{'５'});               // Fullwidth 5
        homoglyphsMap.put('6', new char[]{'б'});                // Cyrillic be
        homoglyphsMap.put('7', new char[]{'７'});               // Fullwidth 7
        homoglyphsMap.put('8', new char[]{'Ȣ'});                // Latin letter OU
        homoglyphsMap.put('9', new char[]{'９'});               // Fullwidth 9
    }

    public FlexSpammer() {
        super(AddonTemplate.CATEGORY, "flex-spammer", "Sends unique messages with anti-spam variations to bypass debounce.");
    }

    @Override
    public void onActivate() {
        loadMessagesFromFile();
        msgPointer = 0;
        lastRawMsg = "";
        asciiIndex = 0;
        lastMessageTime = 0;
        info("FlexSpammer activated with " + rawMessages.size() + " messages.");
    }

    private void loadMessagesFromFile() {
        File file = new File(mc.runDirectory, jsonMessagesFile);
        if (!file.exists()) {
            info("Message JSON file not found, using fallback message.");
            rawMessages.clear();
            rawMessages.add(fallbackMessage);
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            List<String> loaded = gson.fromJson(reader, new TypeToken<List<String>>(){}.getType());

            rawMessages.clear();
            for (String msg : loaded) {
                // Replace special dots with spaces (same as before)
                rawMessages.add(msg.replace('·', ' ').replace('•', ' ').trim());
            }
            // Sort alphabetically for sequential sending
            Collections.sort(rawMessages, String.CASE_INSENSITIVE_ORDER);
        } catch (IOException e) {
            error("Failed to load messages JSON: " + e.getMessage());
            rawMessages.clear();
            rawMessages.add(fallbackMessage);
        }
    }

    private String unicodeMutate(String text, double intensity) {
        Random random = new Random();
        double homChance = Math.min(random.nextDouble() * intensity + 0.05, 0.3);
        double capChance = Math.min(random.nextDouble() * intensity + 0.1, 0.4);
        double zwspChance = 0.01 * intensity;

        StringBuilder sb = new StringBuilder();

        for (char ch : text.toCharArray()) {
            if (ch == ' ') {
                sb.append(' ');
                continue;
            }

            // Caps randomization
            if (Character.isLetter(ch) && random.nextDouble() < capChance) {
                ch = random.nextBoolean() ? Character.toUpperCase(ch) : Character.toLowerCase(ch);
            }

            // Homoglyph substitution
            char baseChar = Character.toLowerCase(ch);
            if (homoglyphsMap.containsKey(baseChar) && random.nextDouble() < homChance) {
                char[] variants = homoglyphsMap.get(baseChar);
                ch = variants[random.nextInt(variants.length)];
            }

            sb.append(ch);

            // Zero width space insertion
            if (random.nextDouble() < zwspChance) {
                sb.append('\u200B');
            }
        }

        return sb.toString();
    }

    private String generateNextMessage() {
        if (msgPointer >= rawMessages.size()) return null;

        String rawMsg = rawMessages.get(msgPointer++);

        // Avoid repeating the exact same raw message (case-insensitive)
        if (lastRawMsg.equalsIgnoreCase(rawMsg)) {
            if (msgPointer < rawMessages.size()) {
                rawMsg = rawMessages.get(msgPointer++);
            }
        }

        lastRawMsg = rawMsg;

        // Add suffix ASCII char cycling
        char suffix = asciiChars[asciiIndex++ % asciiChars.length];

        // Light unicode mutation to avoid spam filters, intensity low (0.1 - 0.3)
        double intensity = 0.1 + new Random().nextDouble() * 0.2;
        String mutatedMsg = unicodeMutate(rawMsg, intensity);

        return mutatedMsg + "     " + suffix;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null) return;

        long now = System.currentTimeMillis();

        // Adaptive delay with +/-30% jitter
        int baseDelay = delay.get();
        long adaptiveDelay = (long) (baseDelay * (0.7 + new Random().nextDouble() * 0.6));

        if (now - lastMessageTime < adaptiveDelay) return;

        String nextMsg = generateNextMessage();

        if (nextMsg == null) {
            // No more messages, disable module and notify
            ChatUtils.sendPlayerMsg("§6[FlexSpammer] §aAll messages have been sent.");
            this.toggle();
            return;
        }

        ChatUtils.sendPlayerMsg(nextMsg);
        lastMessageTime = now;
    }
}
