package com.kneelawk.flywheeltest.glow.gltf.impl;

// Refer to https://github.com/KhronosGroup/glTF/tree/main/extensions/2.0/Khronos/KHR_materials_unlit
public class KHRMaterialsUnlit {
	public static boolean isPresent(GLTFData.GLTFMaterial material) {
		if (material.extensions == null) return false;
		return material.extensions.get("KHR_materials_unlit") != null;
	}
}
