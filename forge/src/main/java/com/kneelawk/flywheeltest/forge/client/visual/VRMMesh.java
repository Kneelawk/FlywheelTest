package com.kneelawk.flywheeltest.forge.client.visual;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jozufozu.flywheel.api.model.IndexSequence;
import com.jozufozu.flywheel.api.model.Mesh;
import com.jozufozu.flywheel.api.vertex.MutableVertexList;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryUtil;

import org.joml.Vector4fc;

import com.kneelawk.flywheeltest.glow.gltf.impl.GLTFData;

public class VRMMesh implements Mesh {
    private final GLTFData data;
    private final byte[] binary;
    private final GLTFData.GLTFPrimitive primitive;
    private final Vector4fc boundingSphere;

    private final int indexCount;
    private final int indexOffset;
    private final int indexLen;

    private final int vertexCount;
    private final int positionOffset;
    private final int positionLen;
    private final int normalOffset;
    private final int normalLen;
    private final int texOffset;
    private final int texLen;

    public VRMMesh(GLTFData data, byte[] binary, GLTFData.GLTFPrimitive primitive, Vector4fc boundingSphere) {
        this.data = data;
        this.binary = binary;
        this.primitive = primitive;
        this.boundingSphere = boundingSphere;

        GLTFData.GLTFAccessor indexAccessor = data.accessors[primitive.indices];
        indexCount = indexAccessor.count;
        GLTFData.GLTFBufferView indexView = data.bufferViews[indexAccessor.bufferView];
        indexOffset = indexView.byteOffset;
        indexLen = indexView.byteLength;

        GLTFData.GLTFAccessor positionAccessor = data.accessors[primitive.attributes.POSITION];
        vertexCount = positionAccessor.count;
        GLTFData.GLTFBufferView positionView = data.bufferViews[positionAccessor.bufferView];
        positionOffset = positionView.byteOffset;
        positionLen = positionView.byteLength;
        GLTFData.GLTFBufferView normalView = data.bufferViews[data.accessors[primitive.attributes.NORMAL].bufferView];
        normalOffset = normalView.byteOffset;
        normalLen = normalView.byteLength;
        GLTFData.GLTFBufferView texView = data.bufferViews[data.accessors[primitive.attributes.TEXCOORD_0].bufferView];
        texOffset = texView.byteOffset;
        texLen = texView.byteLength;
    }

    @Override
    public int vertexCount() {
        return vertexCount;
    }

    @Override
    public void write(@NotNull MutableVertexList vertexList) {
        FloatBuffer positionBuffer =
            ByteBuffer.wrap(binary, positionOffset, positionLen).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
        FloatBuffer normalBuffer =
            ByteBuffer.wrap(binary, normalOffset, normalLen).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
        FloatBuffer texBuffer =
            ByteBuffer.wrap(binary, texOffset, texLen).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
        for (int i = 0; i < vertexCount; i++) {
            vertexList.x(i, positionBuffer.get(i * 3));
            vertexList.y(i, positionBuffer.get(i * 3 + 1));
            vertexList.z(i, positionBuffer.get(i * 3 + 2));
            vertexList.normalX(i, normalBuffer.get(i * 3));
            vertexList.normalY(i, normalBuffer.get(i * 3 + 1));
            vertexList.normalZ(i, normalBuffer.get(i * 3 + 2));
            vertexList.u(i, texBuffer.get(i * 2));
            vertexList.v(i, texBuffer.get(i * 2 + 1));
        }
    }

    @Override
    public @NotNull IndexSequence indexSequence() {
        return (ptr, count) -> {
            IntBuffer indexBuffer =
                ByteBuffer.wrap(binary, indexOffset, indexLen).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
            for (int i = 0; i < indexCount; i++) {
                MemoryUtil.memPutInt(ptr + i * 4L, indexBuffer.get(i));
            }
        };
    }

    @Override
    public int indexCount() {
        return indexCount;
    }

    @Override
    public @NotNull Vector4fc boundingSphere() {
        return boundingSphere;
    }

    @Override
    public void delete() {

    }
}
