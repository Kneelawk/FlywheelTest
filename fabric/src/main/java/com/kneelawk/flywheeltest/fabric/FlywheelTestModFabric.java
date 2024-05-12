package com.kneelawk.flywheeltest.fabric;

import net.fabricmc.api.ModInitializer;

import com.kneelawk.flywheeltest.client.FlywheelTestModClient;

public class FlywheelTestModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FlywheelTestModClient.registerVisuals();
    }
}
