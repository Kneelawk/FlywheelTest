/*
 * This file is part of Glow ( https://github.com/playsawdust/glow-base ), used under the Mozilla Public License.
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kneelawk.flywheeltest.glow.gltf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.kneelawk.flywheeltest.glow.gltf.impl.GLTFData;

public class GLTFLoader {
    public static final Gson GSON = new GsonBuilder()
        //.registerTypeAdapter(ModelTransformation.class, foo)
        .create();

    public static GLTFData loadRaw(String json) {
        return GSON.fromJson(json, GLTFData.class);
        //return Jankson.builder().build().fromJson(json, GLTFData.class);
    }
}
