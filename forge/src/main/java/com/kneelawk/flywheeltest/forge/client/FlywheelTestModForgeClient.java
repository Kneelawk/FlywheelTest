package com.kneelawk.flywheeltest.forge.client;

import com.jozufozu.flywheel.lib.visual.SimpleEntityVisualizer;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import net.minecraft.world.entity.EntityType;

import com.kneelawk.flywheeltest.forge.client.visual.VRMPlayerVisual;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class FlywheelTestModForgeClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        SimpleEntityVisualizer.builder(EntityType.PLAYER).factory(VRMPlayerVisual::new)
            .skipVanillaRender(VRMPlayerVisual::isVRMPlayer).apply();
    }
}
