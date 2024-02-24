/*
 * This file is part of Glow ( https://github.com/playsawdust/glow-base ), used under the Mozilla Public License.
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kneelawk.flywheeltest.glow.gltf.impl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.Nullable;

// Refer to https://registry.khronos.org/glTF/specs/2.0/glTF-2.0.html
// Refer to https://github.com/KhronosGroup/glTF/tree/main
public class GLTFData {
    //PrimitiveType
    public static final int GL_POINTS = 0;
    public static final int GL_LINES = 1;
    public static final int GL_LINE_LOOP = 2;
    public static final int GL_LINE_STRIP = 3;
    public static final int GL_TRIANGLES = 4;
    public static final int GL_TRIANGLE_STRIP = 5;
    public static final int GL_TRIANGLE_FAN = 6;
    //ComponentType
    public static final int GL_BYTE = 5120;
    public static final int GL_UBYTE = 5121;
    public static final int GL_SHORT = 5122;
    public static final int GL_USHORT = 5123;
    public static final int GL_UINT = 5125;
    public static final int GL_FLOAT = 5126;
    //Wrap
    public static final int GL_REPEAT = 10497;

    public String[] extensionsUsed = new String[0];
    public String[] extensionsRequired = new String[0];
    public GLTFAccessor[] accessors = new GLTFAccessor[0];
    public GLTFAnimation[] animations = new GLTFAnimation[0];
    public GLTFAsset asset = new GLTFAsset();
    public GLTFBuffer[] buffers = new GLTFBuffer[0];
    public GLTFBufferView[] bufferViews = new GLTFBufferView[0];
    public GLTFImage[] images = new GLTFImage[0];
    public GLTFMaterial[] materials = new GLTFMaterial[0];
    public GLTFMesh[] meshes = new GLTFMesh[0];
    public GLTFNode[] nodes = new GLTFNode[0];
    public GLTFSampler[] samplers = new GLTFSampler[0];
    public GLTFTexture[] textures = new GLTFTexture[0];
    public JsonObject extensions = null;
    public JsonElement extras = null;

    public GLTFData() {
    }

    public ByteBuffer getDataBuffer(int bufferView, byte[] binaryData) {
        if (bufferView < 0 || bufferView >= bufferViews.length)
            throw new IllegalArgumentException("buffer argument must be between 0 and " + (bufferViews.length - 1));
        GLTFBufferView view = bufferViews[bufferView];
        if (view.buffer < 0 || view.buffer >= buffers.length)
            throw new IllegalStateException("bufferView points to nonexistant buffer #" + view.buffer);
        GLTFBuffer buf = buffers[view.buffer];

        String uri = buf.uri;
        if (uri.startsWith("data:application/octet-stream;base64,")) {
            uri = uri.substring("data:application/octet-stream;base64,".length());

            byte[] data = Base64.getDecoder().decode(uri);
            return ByteBuffer.wrap(data, view.byteOffset, view.byteLength).order(ByteOrder.LITTLE_ENDIAN);
        }

        if (uri.startsWith("data:application/gltf-buffer;base64,")) {
            uri = uri.substring("data:application/gltf-buffer;base64,".length());

            byte[] data = Base64.getDecoder().decode(uri);
            return ByteBuffer.wrap(data, view.byteOffset, view.byteLength).order(ByteOrder.LITTLE_ENDIAN);
        }

        if (view.buffer == 0 && binaryData != null) {
            return ByteBuffer.wrap(binaryData, view.byteOffset, view.byteLength).order(ByteOrder.LITTLE_ENDIAN);
        }

        throw new IllegalArgumentException("Data buffer is not backed by a data URI or binary");
    }

    public static class GLTFAccessor {
        public int bufferView = 0;
        public int byteOffset = 0;
        public int componentType = 0;
        public boolean normalized = false;
        public int count = 0;
        public String type = "VOID";
        public float[] max = {0};
        public float[] min = {0};
        public GLTFAccessorSparse sparse = null;
        public String name = null;

        public GLTFAccessor() {
        }

        public @Nullable GLTFComponentType getComponentType() {
            return GLTFComponentType.fromGL(componentType);
        }

        public @Nullable GLTFElementType getType() {
            return GLTFElementType.fromGL(type);
        }
    }

    public enum GLTFComponentType {
        BYTE(1, true, true, GL_BYTE),
        UNSIGNED_BYTE(1, false, true, GL_UBYTE),
        SHORT(2, true, true, GL_SHORT),
        UNSIGNED_SHORT(2, false, true, GL_USHORT),
        UNSIGNED_INT(4, false, true, GL_UINT),
        FLOAT(4, true, false, GL_FLOAT);

        public final int byteWidth;
        public final boolean signed;
        public final boolean integer;
        public final int glType;

        GLTFComponentType(int byteWidth, boolean signed, boolean integer, int glType) {
            this.byteWidth = byteWidth;
            this.signed = signed;
            this.integer = integer;
            this.glType = glType;
        }

        public static @Nullable GLTFComponentType fromGL(int glType) {
            return switch (glType) {
                case GL_BYTE -> BYTE;
                case GL_UBYTE -> UNSIGNED_BYTE;
                case GL_SHORT -> SHORT;
                case GL_USHORT -> UNSIGNED_SHORT;
                case GL_UINT -> UNSIGNED_INT;
                case GL_FLOAT -> FLOAT;
                default -> null;
            };
        }
    }

    public enum GLTFElementType {
        SCALAR(1, true, false, false, "SCALAR"),
        VEC2(2, false, true, false, "VEC2"),
        VEC3(3, false, true, false, "VEC3"),
        VEC4(4, false, true, false, "VEC4"),
        MAT2(4, false, false, true, "MAT2"),
        MAT3(9, false, false, true, "MAT3"),
        MAT4(16, false, false, true, "MAT4");

        public final int componentCount;
        public final boolean scalar;
        public final boolean vector;
        public final boolean matrix;
        public final String glType;

        GLTFElementType(int componentCount, boolean scalar, boolean vector, boolean matrix, String glType) {
            this.componentCount = componentCount;
            this.scalar = scalar;
            this.vector = vector;
            this.matrix = matrix;
            this.glType = glType;
        }

        public static @Nullable GLTFElementType fromGL(String glType) {
            return switch (glType) {
                case "SCALAR" -> SCALAR;
                case "VEC2" -> VEC2;
                case "VEC3" -> VEC3;
                case "VEC4" -> VEC4;
                case "MAT2" -> MAT2;
                case "MAT3" -> MAT3;
                case "MAT4" -> MAT4;
                default -> null;
            };
        }
    }

    public static class GLTFAccessorSparse {
        public int count = 0;
        public GLTFAccessorSparseIndices indices = new GLTFAccessorSparseIndices();
        public GLTFAccessorSparseValues values = new GLTFAccessorSparseValues();
    }

    public static class GLTFAccessorSparseIndices {
        public int bufferView = 0;
        public int byteOffset = 0;
        public int componentType = 0;

        public GLTFAccessorSparseIndices() {
        }
    }

    public static class GLTFAccessorSparseValues {
        public int bufferView = 0;
        public int byteOffset = 0;

        public GLTFAccessorSparseValues() {
        }
    }

    public static class GLTFAnimation {
        public GLTFAnimationChannel[] channels = new GLTFAnimationChannel[0];
        public GLTFAnimationSampler[] samplers = new GLTFAnimationSampler[0];
        public String name = null;

        public GLTFAnimation() {
        }
    }

    public static class GLTFAnimationChannel {
        public int sampler = 0;
        public GLTFAnimationChannelTarget target = new GLTFAnimationChannelTarget();

        public GLTFAnimationChannel() {
        }
    }

    public static class GLTFAnimationChannelTarget {
        public int node = -1;
        public String path = null;
        public JsonObject extensions = null;

        public GLTFAnimationChannelTarget() {
        }
    }

    public static class GLTFAnimationSampler {
        /**
         * Accessor.
         */
        public int input = 0;
        public String interpolation = "LINEAR";
        /**
         * Accessor.
         */
        public int output = 0;

        public GLTFAnimationSampler() {
        }
    }

    public static class GLTFAsset {
        public String version = "untitiled";
        public String generator = "unknown";
        public String copyright = null;

        public GLTFAsset() {
        }
    }

    public static class GLTFBuffer {
        public int byteLength = 0;
        public String uri = "";

        public GLTFBuffer() {
        }
    }

    public static class GLTFBufferView {
        public int buffer = 0;
        public int byteOffset = 0;
        public int byteLength = 0;
        public int target = 0;
            //34962 for ARRAY_BUFFER (e.g. positions, normals); 34963 for ELEMENT_BUFFER (e.g. indices)
        public int byteStride = 0;

        public GLTFBufferView() {
        }
    }

    public static class GLTFMaterial {
        public String name = null;
        public JsonObject extensions = null;
        public JsonElement extras = null;
        public GLTFPBRMetallicRoughness pbrMetallicRoughness = new GLTFPBRMetallicRoughness();
        public GLTFMaterialNormalTextureInfo normalTexture = new GLTFMaterialNormalTextureInfo();
        public GLTFMaterialOcclusionTextureInfo occlusionTexture = new GLTFMaterialOcclusionTextureInfo();
        public GLTFTextureInfo emissiveTexture = new GLTFTextureInfo();
        public float[] emissiveFactor = {0, 0, 0};
        public String alphaMode = "OPAQUE";
        public float alphaCutoff = 0.5f;
        public boolean doubleSided = false;

        public GLTFMaterial() {
        }
    }

    public static class GLTFPBRMetallicRoughness {
        public float[] baseColorFactor = {1, 1, 1, 1};
        public GLTFTextureInfo baseColorTexture = new GLTFTextureInfo();
        public float metallicFactor = 1;
        public float roughnessFactor = 1;
        public GLTFTextureInfo metallicRoughnessTexture = new GLTFTextureInfo();

        public GLTFPBRMetallicRoughness() {
        }
    }

    public static class GLTFTextureInfo {
        public int index = 0;
        public int texCoord = 0;
        public JsonObject extensions = null;
        public JsonElement extras = null;

        public GLTFTextureInfo() {
        }
    }

    public static class GLTFMaterialNormalTextureInfo {
        public int index = 0;
        public int texCoord = 0;
        public float scale = 1;

        public GLTFMaterialNormalTextureInfo() {
        }
    }

    public static class GLTFMaterialOcclusionTextureInfo {
        public int index = 0;
        public int texCoord = 0;
        public float strength = 1;

        public GLTFMaterialOcclusionTextureInfo() {
        }
    }

    /**
     * Equivalent to glow Model
     */
    public static class GLTFMesh {
        public GLTFPrimitive[] primitives = new GLTFPrimitive[0];
        public int[] weights = new int[0];
        public String name = null;

        public GLTFMesh() {
        }
    }

    /**
     * Equivalent to glow Mesh
     */
    public static class GLTFPrimitive {
        public int mode = 0;
        public int material = 0;
        public int indices = 0; //index buffer
        public GLTFPrimitiveAttributes attributes = new GLTFPrimitiveAttributes();
        public GLTFPrimitiveAttributes[] targets = new GLTFPrimitiveAttributes[0];

        public GLTFPrimitive() {
        }
    }

    /**
     * sort of equivalent to Mesh.Vertex[]
     */
    public static class GLTFPrimitiveAttributes {
        public int POSITION = -1;
        public int NORMAL = -1;
        public int TANGENT = -1;
        public int TEXCOORD_0 = -1;
        public int TEXCOORD_1 = -1;
        public int COLOR_0 = -1;
        public int JOINTS_0 = -1;
        public int WEIGHTS_0 = -1;

        public GLTFPrimitiveAttributes() {
        }
    }

    public static class GLTFNode {
        public int[] children;
        public int skin;
        public float[] matrix = {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};
        public int mesh;
        public float[] rotation = {0, 0, 0, 1};
        public float[] scale = {1, 1, 1};
        public float[] translation = {0, 0, 0};
        public float[] weights = new float[0];
        public String name = null;
        public JsonObject extensions = null;

        public GLTFNode() {
        }
    }

    public static class GLTFSampler {
        public int magFilter = 0;
        public int minFilter = 0;
        public int wrapS = GL_REPEAT;
        public int wrapT = GL_REPEAT;
        public String name = null;

        public GLTFSampler() {
        }
    }

    public static class GLTFSkin {
        public int inverseBindMatrices = 0;
        public int skeleton = 0;
        public int[] joints = new int[0];
        public String name = null;

        public GLTFSkin() {
        }
    }

    public static class GLTFTexture {
        public int sampler = 0;
        public int source = 0;
        public String name = null;
        public JsonObject extensions = null;

        public GLTFTexture() {
        }
    }

    public static class GLTFImage {
        public String mimeType = "image/png";
        public String uri = "#all";
        public int bufferView = -1;
        public String name = null;

        public GLTFImage() {
        }
    }
}
