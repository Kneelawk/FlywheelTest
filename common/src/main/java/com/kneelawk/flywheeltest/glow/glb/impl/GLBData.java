package com.kneelawk.flywheeltest.glow.glb.impl;

import org.jetbrains.annotations.Nullable;

import com.kneelawk.flywheeltest.glow.gltf.impl.GLTFData;

public class GLBData {
    public GLTFData data;
    public byte @Nullable [] binaryData;

    public GLBData(GLTFData data, byte @Nullable [] binaryData) {
        this.data = data;
        this.binaryData = binaryData;
    }
}
