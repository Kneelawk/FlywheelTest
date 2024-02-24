package com.kneelawk.flywheeltest.forge.client.visual;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.collect.ImmutableMap;
import com.jozufozu.flywheel.api.material.Material;
import com.jozufozu.flywheel.api.model.Mesh;
import com.jozufozu.flywheel.api.model.Model;
import com.jozufozu.flywheel.api.visual.VisualFrameContext;
import com.jozufozu.flywheel.api.visualization.VisualizationContext;
import com.jozufozu.flywheel.lib.instance.InstanceTypes;
import com.jozufozu.flywheel.lib.instance.TransformedInstance;
import com.jozufozu.flywheel.lib.material.CutoutShaders;
import com.jozufozu.flywheel.lib.material.SimpleMaterial;
import com.jozufozu.flywheel.lib.model.SimpleModel;
import com.jozufozu.flywheel.lib.visual.SimpleEntityVisual;

import net.minecraftforge.fml.loading.FMLLoader;

import org.lwjgl.system.MemoryUtil;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import org.joml.Vector4f;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import com.kneelawk.flywheeltest.glow.glb.GLBLoader;
import com.kneelawk.flywheeltest.glow.glb.impl.GLBData;
import com.kneelawk.flywheeltest.glow.gltf.impl.GLTFData;

public class VRMPlayerVisual extends SimpleEntityVisual<Player> {
    private final Model model;
    private TransformedInstance instance;
    private final PoseStack stack = new PoseStack();

    public static boolean isVRMPlayer(Player player) {
        return true;
    }

    public VRMPlayerVisual(VisualizationContext ctx, Player entity) {
        super(ctx, entity);

        model = new SimpleModel(loadVRM(entity));
    }

    @Override
    public void init(float partialTick) {
        instance = instancerProvider.instancer(InstanceTypes.TRANSFORMED, model).createInstance();

        super.init(partialTick);
    }

    @Override
    public void beginFrame(VisualFrameContext ctx) {
        super.beginFrame(ctx);

        stack.setIdentity();
        Vec3 pos = entity.position();
        stack.translate(pos.x, pos.y, pos.z);

        instance.updateLight(Minecraft.getInstance().level, entity.blockPosition());
        instance.setTransform(stack).setChanged();
    }

    @Override
    protected void _delete() {
        super._delete();
        model.delete();
        instance.delete();
    }

    private static ImmutableMap<Material, Mesh> loadVRM(Player player) {
        Path vrm = FMLLoader.getGamePath().resolve("Data Cloud 1.vrm");
        GLBData data;
        try (InputStream is = Files.newInputStream(vrm)) {
            data = GLBLoader.loadRaw(new BufferedInputStream(is));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (data.binaryData == null) throw new RuntimeException("Error loading binary data");

        // really hacky bounding sphere
        Vector4f boundingSphere = new Vector4f(0f, 1f, 0f, 1f);

        Int2ObjectMap<ResourceLocation> image2RL = new Int2ObjectOpenHashMap<>();

        ImmutableMap.Builder<Material, Mesh> modelBuilder = ImmutableMap.builder();
        for (GLTFData.GLTFMesh mesh : data.data.meshes) {
            for (GLTFData.GLTFPrimitive primitive : mesh.primitives) {
                GLTFData.GLTFMaterial gltfMaterial = data.data.materials[primitive.material];
                int colorIndex = gltfMaterial.pbrMetallicRoughness.baseColorTexture.index;

                ResourceLocation textureId = image2RL.computeIfAbsent(colorIndex, _colorIndex -> {
                    GLTFData.GLTFImage image = data.data.images[colorIndex];
                    GLTFData.GLTFBufferView view = data.data.bufferViews[image.bufferView];

//                    var future = CompletableFuture.supplyAsync(() -> {
                    ByteBuffer imageBuf = MemoryUtil.memAlignedAlloc(8, view.byteLength);
                    try {
                        imageBuf.put(data.binaryData, view.byteOffset, view.byteLength);
                        imageBuf.rewind();
                        NativeImage nativeImage = NativeImage.read(imageBuf);
                        DynamicTexture imageTexture = new DynamicTexture(nativeImage);
                        // FIXME: this probably leaks memory like crazy
                        return Minecraft.getInstance().textureManager.register(image.name, imageTexture);
                    } catch (IOException e) {
                        System.err.println("Error loading " + image.name);
                        return new ResourceLocation("missing");
                    } finally {
                        MemoryUtil.memAlignedFree(imageBuf);
                    }
//                    }, Minecraft.getInstance());
//                    try {
//                        return future.get();
//                    } catch (InterruptedException | ExecutionException e) {
//                        throw new RuntimeException(e);
//                    }
                });

                Material material =
                    SimpleMaterial.builder().cutout(CutoutShaders.HALF).mipmap(false).diffuse(false).texture(textureId)
                        .build();

                modelBuilder.put(material, new VRMMesh(data.data, data.binaryData, primitive, boundingSphere));
            }
        }

        return modelBuilder.build();
    }
}
