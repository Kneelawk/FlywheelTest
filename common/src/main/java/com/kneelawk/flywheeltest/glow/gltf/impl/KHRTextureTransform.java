package com.kneelawk.flywheeltest.glow.gltf.impl;

import org.jetbrains.annotations.Nullable;

import com.kneelawk.flywheeltest.glow.gltf.GLTFLoader;

// Refer to https://github.com/KhronosGroup/glTF/tree/main/extensions/2.0/Khronos/KHR_texture_transform
public class KHRTextureTransform {
    public float[] offset = {0, 0};
    public float rotation = 0;
    public float[] scale = {1, 1};
    public int texCoord = -1;

    public KHRTextureTransform() {
    }

    public static @Nullable KHRTextureTransform get(GLTFData.GLTFTextureInfo textureInfo) {
        if (textureInfo.extensions == null) return null;
        return GLTFLoader.GSON.fromJson(textureInfo.extensions.get("KHR_texture_transform"), KHRTextureTransform.class);
    }
}
