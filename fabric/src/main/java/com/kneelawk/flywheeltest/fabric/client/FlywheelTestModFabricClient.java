package com.kneelawk.flywheeltest.fabric.client;

import net.fabricmc.api.ClientModInitializer;

import com.kneelawk.flywheeltest.client.FlywheelTestModClient;

public class FlywheelTestModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FlywheelTestModClient.registerVisuals();
    }
}
