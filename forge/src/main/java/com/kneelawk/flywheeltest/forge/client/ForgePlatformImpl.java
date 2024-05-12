package com.kneelawk.flywheeltest.forge.client;

import java.nio.file.Path;

import net.minecraftforge.fml.loading.FMLLoader;

import com.kneelawk.flywheeltest.client.Platform;

public class ForgePlatformImpl implements Platform {
    @Override
    public Path getGamePath() {
        return FMLLoader.getGamePath();
    }
}
