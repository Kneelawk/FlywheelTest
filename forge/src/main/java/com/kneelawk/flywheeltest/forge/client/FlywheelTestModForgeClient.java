package com.kneelawk.flywheeltest.forge.client;

import com.jozufozu.flywheel.api.instance.InstanceType;
import com.jozufozu.flywheel.api.layout.FloatRepr;
import com.jozufozu.flywheel.api.layout.IntegerRepr;
import com.jozufozu.flywheel.api.layout.LayoutBuilder;
import com.jozufozu.flywheel.lib.instance.SimpleInstanceType;
import com.jozufozu.flywheel.lib.math.MatrixMath;
import com.jozufozu.flywheel.lib.visual.SimpleEntityVisualizer;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import org.lwjgl.system.MemoryUtil;

import net.minecraft.world.entity.EntityType;

import com.kneelawk.flywheeltest.forge.client.visual.VRMInstance;
import com.kneelawk.flywheeltest.forge.client.visual.VRMPlayerVisual;

import static com.kneelawk.flywheeltest.FlywheelTestMod.id;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class FlywheelTestModForgeClient {
    public static final InstanceType<VRMInstance> VRM_INSTANCE_TYPE = SimpleInstanceType.builder(VRMInstance::new)
        .layout(LayoutBuilder.create()
            .vector("color", FloatRepr.NORMALIZED_UNSIGNED_BYTE, 4)
            .vector("overlay", IntegerRepr.SHORT, 2)
            .vector("light", FloatRepr.UNSIGNED_SHORT, 2)
            .matrix("pose", FloatRepr.FLOAT, 4)
            .matrix("normal", FloatRepr.FLOAT, 3)
            .build())
        .writer((ptr, instance) -> {
            MemoryUtil.memPutByte(ptr, instance.r);
            MemoryUtil.memPutByte(ptr + 1, instance.g);
            MemoryUtil.memPutByte(ptr + 2, instance.b);
            MemoryUtil.memPutByte(ptr + 3, instance.a);
            MemoryUtil.memPutInt(ptr + 4, instance.overlay);
            MemoryUtil.memPutInt(ptr + 8, (int) instance.blockLight | (int) instance.skyLight << 16);
            MatrixMath.writeUnsafe(instance.model, ptr + 12);
            MatrixMath.writeUnsafe(instance.normal, ptr + 76);
        })
        .vertexShader(id("instance/vrm.vert"))
        .cullShader(id("instance/cull/vrm.glsl"))
        .register();

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        SimpleEntityVisualizer.builder(EntityType.PLAYER).factory(VRMPlayerVisual::new)
            .skipVanillaRender(VRMPlayerVisual::isVRMPlayer).apply();
    }
}
