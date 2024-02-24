package com.kneelawk.flywheeltest;

import net.minecraft.resources.ResourceLocation;

public class FlywheelTestMod {
    public static final String MOD_ID = "flywheel_test";
    
    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
