package com.kneelawk.flywheeltest.forge.client;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import com.kneelawk.flywheeltest.client.FlywheelTestModClient;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class FlywheelTestModForgeClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        FlywheelTestModClient.registerVisuals();
    }
}
