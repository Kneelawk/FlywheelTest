package com.kneelawk.flywheeltest.glow.glb;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.kneelawk.flywheeltest.glow.glb.impl.GLBData;
import com.kneelawk.flywheeltest.glow.gltf.GLTFLoader;
import com.kneelawk.flywheeltest.glow.gltf.impl.GLTFData;

public class GLBLoader {
    public static void main(String[] args) {
        try (FileInputStream fis = new FileInputStream("Data Cloud 1.vrm");
             FileWriter fos = new FileWriter("Data Cloud 1.vrm.json")) {
            System.out.println("Loading...");
            GLBData data = loadRaw(fis);
            System.out.println("Loaded.");

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            System.out.println("Meshes:");
            for (GLTFData.GLTFMesh mesh : data.data.meshes) {
                System.out.println("  " + mesh.name + ":");
                GLTFData.GLTFPrimitive[] primitives = mesh.primitives;
                for (int i = 0; i < primitives.length; i++) {
                    GLTFData.GLTFPrimitive primitive = primitives[i];
                    System.out.println("    Primitive " + i + ":");

                    GLTFData.GLTFAccessor indexAccessor = data.data.accessors[primitive.indices];
                    System.out.println("      Index type: " + indexAccessor.getType());
                    System.out.println("      Index component type: " + indexAccessor.getComponentType());
                }
            }

            System.out.println("Writing...");
            gson.toJson(data.data, fos);
            System.out.println("Written.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static GLBData loadRaw(InputStream is) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        DataInputStream dis = new DataInputStream(bis);
        // readInt is big-endian
        int magic = dis.readInt();
        if (magic != 0x676C5446) throw new IOException("Not GLB data");
        int _version = swap(dis.readInt());
        int fileLength = swap(dis.readInt());

        int jsonChunkLength = swap(dis.readInt());
        int jsonChunkType = dis.readInt();
        if (jsonChunkType != 0x4A534F4E) throw new IOException("First chunk is not json");

        byte[] jsonBytes = new byte[jsonChunkLength];
        dis.readFully(jsonBytes);
        String json = new String(jsonBytes, StandardCharsets.UTF_8);
        GLTFData data = GLTFLoader.loadRaw(json);

        if (fileLength > jsonChunkLength + 12) {
            // binary blob is included too
            int binChunkLength = swap(dis.readInt());
            int binChunkType = dis.readInt();
            if (binChunkType != 0x42494E00) throw new IOException("Second chunk is not binary");

            byte[] bin = new byte[binChunkLength];
            dis.readFully(bin);

            return new GLBData(data, bin);
        } else {
            return new GLBData(data, null);
        }
    }

    private static int swap(int in) {
        return ((in >> 24) & 0x000000FF) | ((in >> 8) & 0x0000FF00) | ((in << 8) & 0x00FF0000) |
            ((in << 24) & 0xFF000000);
    }
}
