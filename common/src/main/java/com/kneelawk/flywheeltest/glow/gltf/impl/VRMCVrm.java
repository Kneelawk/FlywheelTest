package com.kneelawk.flywheeltest.glow.gltf.impl;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.kneelawk.flywheeltest.glow.gltf.GLTFLoader;

// Refer to https://github.com/vrm-c/vrm-specification/tree/master/specification/VRMC_vrm-1.0
public class VRMCVrm {
    public String specVersion;
    public VRMCHumanoid humanoid = new VRMCHumanoid();
    public VRMCMeta meta = new VRMCMeta();
    public VRMCFirstPerson firstPerson = new VRMCFirstPerson();
    public VRMCExpressions expressions = new VRMCExpressions();
    public VRMCLookAt lookAt = new VRMCLookAt();

    public VRMCVrm() {
    }

    public static @Nullable VRMCVrm get(GLTFData data) {
        if (data.extensions == null) return null;
        return GLTFLoader.GSON.fromJson(data.extensions.get("VRMC_vrm"), VRMCVrm.class);
    }

    // Refer to https://github.com/vrm-c/vrm-specification/blob/master/specification/VRMC_vrm-1.0/humanoid.md
    public static class VRMCHumanoid {
        public VRMCHumanBones humanBones = new VRMCHumanBones();

        public VRMCHumanoid() {
        }
    }

    public static class VRMCHumanBones {
        // Torso
        public VRMCHumanBone hips = new VRMCHumanBone();
        public VRMCHumanBone spine = new VRMCHumanBone();
        public VRMCHumanBone chest = null;
        public VRMCHumanBone upperChest = null;
        public VRMCHumanBone neck = null;

        // Head
        public VRMCHumanBone head = new VRMCHumanBone();
        public VRMCHumanBone leftEye = null;
        public VRMCHumanBone rightEye = null;
        public VRMCHumanBone jaw = null;

        // Leg
        public VRMCHumanBone leftUpperLeg = new VRMCHumanBone();
        public VRMCHumanBone leftLowerLeg = new VRMCHumanBone();
        public VRMCHumanBone leftFoot = new VRMCHumanBone();
        public VRMCHumanBone leftToes = null;
        public VRMCHumanBone rightUpperLeg = new VRMCHumanBone();
        public VRMCHumanBone rightLowerLeg = new VRMCHumanBone();
        public VRMCHumanBone rightFoot = new VRMCHumanBone();
        public VRMCHumanBone rightToes = null;

        // Arm
        public VRMCHumanBone leftShoulder = null;
        public VRMCHumanBone leftUpperArm = new VRMCHumanBone();
        public VRMCHumanBone leftLowerArm = new VRMCHumanBone();
        public VRMCHumanBone leftHand = new VRMCHumanBone();
        public VRMCHumanBone rightShoulder = null;
        public VRMCHumanBone rightUpperArm = new VRMCHumanBone();
        public VRMCHumanBone rightLowerArm = new VRMCHumanBone();
        public VRMCHumanBone rightHand = new VRMCHumanBone();

        // Finger
        public VRMCHumanBone leftThumbMetacarpal = null;
        public VRMCHumanBone leftThumbProximal = null;
        public VRMCHumanBone leftThumbDistal = null;
        public VRMCHumanBone leftIndexProximal = null;
        public VRMCHumanBone leftIndexIntermediate = null;
        public VRMCHumanBone leftIndexDistal = null;
        public VRMCHumanBone leftMiddleProximal = null;
        public VRMCHumanBone leftMiddleIntermediate = null;
        public VRMCHumanBone leftMiddleDistal = null;
        public VRMCHumanBone leftRingProximal = null;
        public VRMCHumanBone leftRingIntermediate = null;
        public VRMCHumanBone leftRingDistal = null;
        public VRMCHumanBone leftLittleProximal = null;
        public VRMCHumanBone leftLittleIntermediate = null;
        public VRMCHumanBone leftLittleDistal = null;
        public VRMCHumanBone rightThumbMetacarpal = null;
        public VRMCHumanBone rightThumbProximal = null;
        public VRMCHumanBone rightThumbDistal = null;
        public VRMCHumanBone rightIndexProximal = null;
        public VRMCHumanBone rightIndexIntermediate = null;
        public VRMCHumanBone rightIndexDistal = null;
        public VRMCHumanBone rightMiddleProximal = null;
        public VRMCHumanBone rightMiddleIntermediate = null;
        public VRMCHumanBone rightMiddleDistal = null;
        public VRMCHumanBone rightRingProximal = null;
        public VRMCHumanBone rightRingIntermediate = null;
        public VRMCHumanBone rightRingDistal = null;
        public VRMCHumanBone rightLittleProximal = null;
        public VRMCHumanBone rightLittleIntermediate = null;
        public VRMCHumanBone rightLittleDistal = null;

        public VRMCHumanBones() {
        }
    }

    public static class VRMCHumanBone {
        public int node = -1;

        public VRMCHumanBone() {
        }
    }

    // Refer to https://github.com/vrm-c/vrm-specification/blob/master/specification/VRMC_vrm-1.0/meta.md
    public static class VRMCMeta {
        public String name = "Missing name";
        public String version = null;
        public String[] authors = {"Unknown Author"};
        public String copyrightInformation = null;
        public String contactInformation = null;
        public String[] references = null;
        public String thirdPartyLicenses = null;
        public int thumbnailImage = -1;
        public String licenseUrl = "https://vrm.dev/en/licenses/1.0/index.html";
        public String avatarPermission = "onlyAuthor";
        public boolean allowExcessivelyViolentUsage = false;
        public boolean allowExcessivelySexualUsage = false;
        public String commercialUsage = "personalNonProfit";
        public boolean allowPoliticalOrReligiousUsage = false;
        public boolean allowAntisocialOrHateUsage = false;
        public String creditNotation = "required";
        public boolean allowRedistribution = false;
        public String modification = "prohibited";
        public String otherLicenseUrl = null;

        public VRMCMeta() {
        }
    }

    // Refer to https://github.com/vrm-c/vrm-specification/blob/master/specification/VRMC_vrm-1.0/firstPerson.md
    public static class VRMCFirstPerson {
        public VRMCMeshAnnotation[] meshAnnotations = new VRMCMeshAnnotation[0];

        public VRMCFirstPerson() {
        }
    }

    public static class VRMCMeshAnnotation {
        public int node = -1;
        public String firstPersonFlag = "auto";

        public VRMCMeshAnnotation() {
        }
    }

    // Refer to https://github.com/vrm-c/vrm-specification/blob/master/specification/VRMC_vrm-1.0/expressions.md
    public static class VRMCExpressions {
        public Map<String, VRMCExpression> preset = new HashMap<>();
        public Map<String, VRMCExpression> custom = new HashMap<>();

        public VRMCExpressions() {
        }
    }

    public static class VRMCExpression {
        public boolean isBinary = false;
        public VRMCMorphTargetBind[] morphTargetBinds = null;
        public VRMCMaterialColorBind[] materialColorBinds = null;
        public VRMCTextureTransformBind[] textureTransformBinds = null;
        public String overrideMouth = "none";
        public String overrideBlink = "none";
        public String overrideLookAt = "none";

        public VRMCExpression() {
        }
    }

    public static class VRMCMorphTargetBind {
        public int node = -1;
        public int index = -1;
        public float weight = 1;

        public VRMCMorphTargetBind() {
        }
    }

    public static class VRMCMaterialColorBind {
        public int material = -1;
        public String type = "color";
        public float[] targetValue = {0, 0, 0, 0};

        public VRMCMaterialColorBind() {
        }
    }

    public static class VRMCTextureTransformBind {
        public int material = -1;
        public float[] scale = {1, 1};
        public float[] offset = {0, 0};
    }

    // Refer to https://github.com/vrm-c/vrm-specification/blob/master/specification/VRMC_vrm-1.0/lookAt.md
    public static class VRMCLookAt {
        public String type = "bone";
        public float[] offsetFromHeadBone = {0, 0.06f, 0};
        public VRMCRangeMap rangeMapHorizontalInner = new VRMCRangeMap();
        public VRMCRangeMap rangeMapHorizontalOuter = new VRMCRangeMap();
        public VRMCRangeMap rangeMapVerticalDown = new VRMCRangeMap();
        public VRMCRangeMap rangeMapVerticalUp = new VRMCRangeMap();

        public VRMCLookAt() {
        }
    }

    public static class VRMCRangeMap {
        public float inputMaxValue = 90;
        public float outputScale = 10;

        public VRMCRangeMap() {
        }
    }
}
