struct VrmAtlasInfo {
    bool bigEndian;
    vec2 size;
};

VrmAtlasInfo vrm_atlasInfo;

int vrm_initAtlas() {
    vec2 size = vec2(textureSize(flw_diffuseTex, 0));
    ivec2 magicPos = ivec2(0, 0);
    vec2 magicTexPos = vec2(magicPos) / size;
    vec4 pixel = texture(flw_diffuseTex, magicTexPos);
    
    ivec4 bits = ivec4(pixel * 255.0 + vec4(0.5, 0.5, 0.5, 0.5));

    bool bigEndian;
    if (bits.x == 0xDE && bits.y == 0xAD && bits.z == 0xBE && bits.w == 0xEF) {
        bigEndian = true;
    } else if (bits.w == 0xDE && bits.z == 0xAD && bits.y == 0xBE && bits.x == 0xEF) {
        bigEndian = false;
    } else {
        // Unsupported endianness detected
        return -1;
    }
    
    vrm_atlasInfo.size = size;
    vrm_atlasInfo.bigEndian = bigEndian;

    return 0;
}

vec4 vrm_readColor(ivec2 pos) {
    vec2 texPos = vec2(pos) / vrm_atlasInfo.size;
    return texture(flw_diffuseTex, texPos);
}

int vrm_readInt(ivec2 pos) {
    vec4 pixel = vrm_readColor(pos);
    ivec4 bits = ivec4(pixel * 255.0 + vec4(0.5, 0.5, 0.5, 0.5));
    if (vrm_atlasInfo.bigEndian) {
        return ((bits.x & 0xFF) << 24) | ((bits.y & 0xFF) << 16) | ((bits.z & 0xFF) << 8) | (bits.w & 0xFF);
    } else {
        return ((bits.w & 0xFF) << 24) | ((bits.z & 0xFF) << 16) | ((bits.y & 0xFF) << 8) | (bits.x & 0xFF);
    }
}

const vec3 vrm_lightPosition = vec3(0.0, 1.0, 0.0);
const float vrm_lightSharpness = 4.0;
const float vrm_lightFactor = 0.2;
const float vrm_lightOffset = 0.1;

void flw_materialFragment() {
    //    float factor = 1.0 + ((clamp((dot(vrm_lightPosition, flw_vertexNormal) + vrm_lightOffset) * vrm_lightSharpness, -1.0, 1.0) + 1.0) / 2.0 - 1.0) * vrm_lightFactor;

    //    flw_fragColor.xyz *= factor;
}
