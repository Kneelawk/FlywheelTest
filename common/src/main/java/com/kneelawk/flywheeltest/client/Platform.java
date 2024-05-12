package com.kneelawk.flywheeltest.client;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

public interface Platform {
    Platform INSTANCE = loadPlatform();

    private static Platform loadPlatform() {
        try {
            return (Platform) Class.forName("com.kneelawk.flywheeltest.fabric.client.FabricPlatformImpl")
                .getConstructor().newInstance();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException ignored) {
        }

        try {
            return (Platform) Class.forName("com.kneelawk.flywheeltest.forge.client.ForgePlatformImpl").getConstructor()
                .newInstance();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    
    Path getGamePath();
}
