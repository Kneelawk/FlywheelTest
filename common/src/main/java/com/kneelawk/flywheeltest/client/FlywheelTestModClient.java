package com.kneelawk.flywheeltest.client;

import com.jozufozu.flywheel.api.instance.InstanceType;
import com.jozufozu.flywheel.api.layout.FloatRepr;
import com.jozufozu.flywheel.api.layout.IntegerRepr;
import com.jozufozu.flywheel.api.layout.LayoutBuilder;
import com.jozufozu.flywheel.api.material.MaterialShaders;
import com.jozufozu.flywheel.lib.instance.SimpleInstanceType;
import com.jozufozu.flywheel.lib.material.SimpleMaterialShaders;
import com.jozufozu.flywheel.lib.math.MatrixMath;
import com.jozufozu.flywheel.lib.visual.SimpleEntityVisualizer;

import org.lwjgl.system.MemoryUtil;

import net.minecraft.world.entity.EntityType;

import com.kneelawk.flywheeltest.client.visual.VRMInstance;
import com.kneelawk.flywheeltest.client.visual.VRMPlayerVisual;

import static com.kneelawk.flywheeltest.FlywheelTestMod.id;

public class FlywheelTestModClient {
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
            MemoryUtil.memPutShort(ptr + 4, (short) (instance.overlay & 0xFFFF));
            MemoryUtil.memPutShort(ptr + 6, (short) (instance.overlay >> 16 & 0xFFFF));
            MemoryUtil.memPutShort(ptr + 8, (short) (instance.packedLight & 0xFFFF));
            MemoryUtil.memPutShort(ptr + 10, (short) (instance.packedLight >> 16 & 0xFFFF));
            MatrixMath.writeUnsafe(ptr + 12, instance.model);
            MatrixMath.writeUnsafe(ptr + 76, instance.normal);
        })
        .vertexShader(id("instance/vrm.vert"))
        .cullShader(id("instance/cull/vrm.glsl"))
        .register();

    public static final MaterialShaders VRM_MATERIAL_SHADERS = MaterialShaders.REGISTRY.registerAndGet(
        new SimpleMaterialShaders(id("material/vrm.vert"), id("material/vrm.frag")));
    
    public static void registerVisuals() {
        SimpleEntityVisualizer.builder(EntityType.PLAYER).factory(VRMPlayerVisual::new)
            .skipVanillaRender(VRMPlayerVisual::isVRMPlayer).apply();
    }
}
