const vec3 vrm_lightPosition = vec3(0.0, 1.0, 0.0);
const float vrm_lightSharpness = 4.0;
const float vrm_lightFactor = 0.2;
const float vrm_lightOffset = 0.1;

void flw_materialFragment() {
//    float factor = 1.0 + ((clamp((dot(vrm_lightPosition, flw_vertexNormal) + vrm_lightOffset) * vrm_lightSharpness, -1.0, 1.0) + 1.0) / 2.0 - 1.0) * vrm_lightFactor;

//    flw_fragColor.xyz *= factor;
    
    vec2 size = vec2(textureSize(flw_diffuseTex, 0));
    ivec2 pos = ivec2(0, 0);
    vec2 texPos = vec2(pos) / size;
    vec4 pixel = texture(flw_diffuseTex, texPos);
    
    float b = pixel.z;
    int m1 = int(b * 255.0 + 0.5);
    float g = pixel.y;
    int m2 = int(g * 255.0 + 0.5);
    float r = pixel.x;
    int m3 = int(r * 255.0 + 0.5);
    
    if (m1 == 120) {
        flw_fragColor.z = 1.0;
    }
    if (m2 == 254) {
        flw_fragColor.y = 1.0;
    }
    if (m3 == 2) {
        flw_fragColor.x = 1.0;
    }
}
