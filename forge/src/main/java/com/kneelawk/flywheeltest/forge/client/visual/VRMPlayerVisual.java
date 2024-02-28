package com.kneelawk.flywheeltest.forge.client.visual;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;
import com.jozufozu.flywheel.api.material.Material;
import com.jozufozu.flywheel.api.model.Model;
import com.jozufozu.flywheel.api.visual.VisualFrameContext;
import com.jozufozu.flywheel.api.visualization.VisualizationContext;
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
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import com.kneelawk.flywheeltest.FlywheelTestMod;
import com.kneelawk.flywheeltest.forge.client.FlywheelTestModForgeClient;
import com.kneelawk.flywheeltest.glow.glb.GLBLoader;
import com.kneelawk.flywheeltest.glow.glb.impl.GLBData;
import com.kneelawk.flywheeltest.glow.gltf.impl.GLTFData;

public class VRMPlayerVisual extends SimpleEntityVisual<Player> {
    private static final Pattern BLOCKED_CHARS = Pattern.compile("[^a-z0-9/._-]");

    private final Model model;
    private VRMInstance instance;
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
        instance = instancerProvider.instancer(FlywheelTestModForgeClient.VRM_INSTANCE_TYPE, model).createInstance();

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

    private static ImmutableList<Model.ConfiguredMesh> loadVRM(Player player) {
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

        Material missingMaterial =
            SimpleMaterial.builder().backfaceCulling(false).texture(new ResourceLocation("missing")).build();

        Int2ObjectMap<Material> materialMap = new Int2ObjectOpenHashMap<>();

        ImmutableList.Builder<Model.ConfiguredMesh> modelBuilder = ImmutableList.builder();
        for (GLTFData.GLTFMesh mesh : data.data.meshes) {
            for (GLTFData.GLTFPrimitive primitive : mesh.primitives) {
                int materialIndex = primitive.material;

                Material material = materialMap.computeIfAbsent(materialIndex, _materialIndex -> {
                    GLTFData.GLTFMaterial gltfMaterial = data.data.materials[primitive.material];
                    int colorIndex = gltfMaterial.pbrMetallicRoughness.baseColorTexture.index;
                    GLTFData.GLTFImage image = data.data.images[colorIndex];
                    GLTFData.GLTFBufferView view = data.data.bufferViews[image.bufferView];

                    ResourceLocation textureId;
                    // FIXME: this is mutating the texture manager off thread!
                    //  We're not doing it on the main thread because the main thread is waiting for this one
//                    var future = CompletableFuture.supplyAsync(() -> {
                    NativeImage nativeImage;
                    try {
                        nativeImage = read(data.binaryData, view.byteOffset, view.byteLength);
                    } catch (IOException e) {
                        FlywheelTestMod.LOG.error("Error loading vrm image: '" + image.name + "'");
                        return missingMaterial;
                    }

                    if (gltfMaterial.emissiveTexture != null && gltfMaterial.emissiveFactor != null &&
                        !almostZero(gltfMaterial.emissiveFactor)) {
                        GLTFData.GLTFImage emissiveImage = data.data.images[gltfMaterial.emissiveTexture.index];
                        GLTFData.GLTFBufferView emissiveView = data.data.bufferViews[emissiveImage.bufferView];
                        FlywheelTestMod.LOG.info(
                            "Adding emissive texture '" + emissiveImage.name + "' to '" + image.name + "'");

                        try (NativeImage emissiveNI = read(data.binaryData, emissiveView.byteOffset,
                            emissiveView.byteLength)) {
                            if (nativeImage.getWidth() == emissiveNI.getWidth() &&
                                nativeImage.getHeight() == emissiveNI.getHeight()) {
                                int width = emissiveNI.getWidth();
                                int height = emissiveNI.getHeight();
                                for (int y = 0; y < height; y++) {
                                    for (int x = 0; x < width; x++) {
                                        int color = nativeImage.getPixelRGBA(x, y);
                                        int emissiveColor = emissiveNI.getPixelRGBA(x, y);
                                        int r = (FastColor.ABGR32.red(color) +
                                            (int) ((float) FastColor.ABGR32.red(emissiveColor) *
                                                gltfMaterial.emissiveFactor[0])) & 0xFF;
                                        int g = (FastColor.ABGR32.green(color) +
                                            (int) ((float) FastColor.ABGR32.green(emissiveColor) *
                                                gltfMaterial.emissiveFactor[1])) & 0xFF;
                                        int b = (FastColor.ABGR32.blue(color) +
                                            (int) ((float) FastColor.ABGR32.blue(emissiveColor) *
                                                gltfMaterial.emissiveFactor[2])) & 0xFF;
                                        nativeImage.setPixelRGBA(x, y,
                                            FastColor.ABGR32.color(FastColor.ABGR32.alpha(color), b, g, r));
                                    }
                                }
                            } else {
                                FlywheelTestMod.LOG.warn("Attempted to add emissive texture '" + emissiveImage.name + "' to '" + image.name + "' but they have different sizes");
                            }
                        } catch (IOException e) {
                            FlywheelTestMod.LOG.error("Error loading emissive vrm image: '" + emissiveImage.name + "'");
                            return missingMaterial;
                        }
                    }
                    
                    writeInt(nativeImage, 0, 0, 0xDEADBEEF);

                    DynamicTexture imageTexture = new DynamicTexture(nativeImage);
                    // FIXME: this probably leaks memory like crazy!
                    //  These textures should be reference-counted and removed.
                    textureId = registerTexture(image.name, imageTexture);
//                    }, Minecraft.getInstance());
//                    try {
//                        return future.get();
//                    } catch (InterruptedException | ExecutionException e) {
//                        throw new RuntimeException(e);
//                    }

                    return SimpleMaterial.builder().cutout(CutoutShaders.HALF).mipmap(false).diffuse(false)
                        .backfaceCulling(false).texture(textureId)
                        .shaders(FlywheelTestModForgeClient.VRM_MATERIAL_SHADERS).build();
                });

                modelBuilder.add(new Model.ConfiguredMesh(material,
                    new VRMMesh(data.data, data.binaryData, primitive, boundingSphere)));
            }
        }

        return modelBuilder.build();
    }
    
    private static void writeInt(NativeImage image, int x, int y, int i) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            // Most machines this is on are little-endian, so this is the default
            image.setPixelRGBA(x, y, i);
        } else {
            // not so many machines are bit-endian, so we do the conversion here
            image.setPixelRGBA(x, y, ((i << 24) & 0xFF000000) | ((i << 8) & 0xFF0000) | ((i >> 8) & 0xFF00) | ((i >> 24) & 0xFF));
        }
    }

    private static boolean almostZero(float[] floats) {
        for (float aFloat : floats) {
            if (!almostEqual(aFloat, 0.0f)) return false;
        }
        return true;
    }

    private static boolean almostEqual(float a, float b) {
        return Math.abs(a - b) < 0.0001f;
    }

    private static NativeImage read(byte[] data, int offset, int length) throws IOException {
        ByteBuffer imageBuf = MemoryUtil.memAlignedAlloc(8, length);
        try {
            imageBuf.put(data, offset, length);
            imageBuf.rewind();
            return NativeImage.read(imageBuf);
        } finally {
            MemoryUtil.memAlignedFree(imageBuf);
        }
    }

    private static ResourceLocation registerTexture(String name, DynamicTexture texture) {
        String textureName = BLOCKED_CHARS.matcher(name).replaceAll("_");
        TextureManager manager = Minecraft.getInstance().getTextureManager();
        synchronized (manager) {
            return manager.register(textureName, texture);
        }
    }
}
