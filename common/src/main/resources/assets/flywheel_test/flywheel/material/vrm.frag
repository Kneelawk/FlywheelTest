const vec3 vrm_lightPosition = vec3(0.0, 1.0, 0.0);
const float vrm_lightSharpness = 4.0;
const float vrm_lightFactor = 0.2;
const float vrm_lightOffset = 0.1;

void flw_materialFragment() {
//    float factor = 1.0 + ((clamp((dot(vrm_lightPosition, flw_vertexNormal) + vrm_lightOffset) * vrm_lightSharpness, -1.0, 1.0) + 1.0) / 2.0 - 1.0) * vrm_lightFactor;

//    flw_fragColor.xyz *= factor;
}
