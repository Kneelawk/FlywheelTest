package com.kneelawk.flywheeltest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.resources.ResourceLocation;

public class FlywheelTestMod {
    public static final String MOD_ID = "flywheel_test";
    
    public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);
    
    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
