int vrm_readInt(ivec2 pos) {
    vec4 pixel = texelFetch(flw_diffuseTex, pos, 0);
    ivec4 bits = ivec4(pixel * 255.0 + vec4(0.5, 0.5, 0.5, 0.5));
    // we make sure data is always stored in little-endian
    return ((bits.w & 0xFF) << 24) | ((bits.z & 0xFF) << 16) | ((bits.y & 0xFF) << 8) | (bits.x & 0xFF);
}

const vec3 vrm_lightPosition = vec3(0.0, 1.0, 0.0);
const float vrm_lightSharpness = 4.0;
const float vrm_lightFactor = 0.2;
const float vrm_lightOffset = 0.1;

void flw_materialFragment() {
    //    float factor = 1.0 + ((clamp((dot(vrm_lightPosition, flw_vertexNormal) + vrm_lightOffset) * vrm_lightSharpness, -1.0, 1.0) + 1.0) / 2.0 - 1.0) * vrm_lightFactor;

    //    flw_fragColor.xyz *= factor;

    //    if (vrm_readInt(ivec2(0, 0)) == 0xDEADBEEF) {
    //        flw_fragColor = vec4(1.0);
    //    }
}
