package com.kneelawk.flywheeltest.fabric.client;

import java.nio.file.Path;

import net.fabricmc.loader.api.FabricLoader;

import com.kneelawk.flywheeltest.client.Platform;

public class FabricPlatformImpl implements Platform {
    @Override
    public Path getGamePath() {
        return FabricLoader.getInstance().getGameDir();
    }
}
