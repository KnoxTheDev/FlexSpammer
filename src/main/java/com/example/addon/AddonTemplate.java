package com.example.addon;

import com.example.addon.modules.FlexSpammer;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class AddonTemplate extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = Categories.Misc;

    @Override
    public void onInitialize() {
        LOG.info("Initializing FlexSpammer Addon");

        // Register modules
        Modules.get().add(new FlexSpammer());
    }

    @Override
    public String getPackage() {
        return "com.example.addon";
    }
}
