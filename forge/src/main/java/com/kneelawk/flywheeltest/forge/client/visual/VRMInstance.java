package com.kneelawk.flywheeltest.forge.client.visual;

import javax.annotation.ParametersAreNonnullByDefault;

import com.jozufozu.flywheel.api.instance.InstanceHandle;
import com.jozufozu.flywheel.api.instance.InstanceType;
import com.jozufozu.flywheel.lib.instance.ColoredLitInstance;
import com.jozufozu.flywheel.lib.transform.Transform;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.Mth;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VRMInstance extends ColoredLitInstance implements Transform<VRMInstance> {

    public final Matrix4f model = new Matrix4f();
    public final Matrix3f normal = new Matrix3f();

    public VRMInstance(InstanceType<? extends VRMInstance> type, InstanceHandle handle) {
        super(type, handle);
    }

    // Transform stuff copied from Flywheel's TransformedInstance

    @Override
    public VRMInstance mulPose(Matrix4f pose) {
        this.model.mul(pose);
        return this;
    }

    @Override
    public VRMInstance mulNormal(Matrix3f normal) {
        this.normal.mul(normal);
        return this;
    }

    @Override
    public VRMInstance rotateAround(Quaternionf quaternion, float x, float y, float z) {
        this.model.rotateAround(quaternion, x, y, z);
        this.normal.rotate(quaternion);
        return this;
    }

    @Override
    public VRMInstance scale(float x, float y, float z) {
        model.scale(x, y, z);

        if (x == y && y == z) {
            if (x < 0.0f) {
                normal.scale(-1.0f);
            }

            return this;
        }

        float invX = 1.0f / x;
        float invY = 1.0f / y;
        float invZ = 1.0f / z;
        float f = Mth.fastInvCubeRoot(Math.abs(invX * invY * invZ));
        normal.scale(f * invX, f * invY, f * invZ);
        return this;
    }

    @Override
    public VRMInstance rotate(Quaternionf quaternion) {
        model.rotate(quaternion);
        normal.rotate(quaternion);
        return this;
    }

    @Override
    public VRMInstance translate(double x, double y, double z) {
        model.translate((float) x, (float) y, (float) z);
        return this;
    }

    public VRMInstance setTransform(PoseStack stack) {
        return setTransform(stack.last());
    }

    public VRMInstance setTransform(PoseStack.Pose pose) {
        this.model.set(pose.pose());
        this.normal.set(pose.normal());
        return this;
    }

    /**
     * Sets the transform matrices to be all zeros.
     *
     * <p>
     * This will allow the GPU to quickly discard all geometry for this instance, effectively "turning it off".
     * </p>
     */
    public VRMInstance setEmptyTransform() {
        model.zero();
        normal.zero();
        return this;
    }

    public VRMInstance loadIdentity() {
        model.identity();
        normal.identity();
        return this;
    }
}
