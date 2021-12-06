package com.wuest.prefab.structures.render;

import com.wuest.prefab.Prefab;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.math.MathHelper;

public class TranslucentBufferBuilderWrapper implements VertexConsumer {

    private final BufferBuilder inner;
    private final int tintAlpha;
    private final FloatList primitivePositions;

    private VertexFormat.DrawMode drawMode;
    private float[] primitiveVertexPositions; // vertex positions in the current primitive
    private int currentPosIdx;
    private int currentVert; // TODO: need better name for this
    private boolean firstPrimFinished;

    public TranslucentBufferBuilderWrapper(BufferBuilder inner, int alpha, int initialSize) {
        this.inner = inner;

        // Alpha value should be between 0 and 255
        this.tintAlpha = MathHelper.clamp(alpha, 0, 0xFF);
        this.primitivePositions = new FloatArrayList(initialSize);
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        // Keep track of the vertex positions with an on-CPU buffer
        // Only store them as single-precision floats, that's plenty for transparency sorting

        primitiveVertexPositions[currentPosIdx++] = (float) x;
        primitiveVertexPositions[currentPosIdx++] = (float) y;
        primitiveVertexPositions[currentPosIdx++] = (float) z;

        // for modes like triangle strip and line strip, the primitive ends after the vertex count
        // fills, and from then it's every time the size fills. NOTE: this doesn't account for
        // primitive restarts, but minecraft doesn't use them anyway.
        boolean primFinished = false;
        if (firstPrimFinished) {
            currentVert++;
            if (currentVert >= drawMode.size) {
                currentVert = 0;
                primFinished = true;
            }
        }

        if (currentPosIdx >= primitiveVertexPositions.length) {
            currentPosIdx = 0;
            if (!firstPrimFinished) {
                firstPrimFinished = true;
                primFinished = true;
            }
        }

        if (primFinished) {
            // average vertex positions in primitive
            float totalX = 0.0f;
            float totalY = 0.0f;
            float totalZ = 0.0f;

            for (int vert = 0; vert < drawMode.vertexCount; vert++) {
                int startingPos = vert * 3;
                totalX += primitiveVertexPositions[startingPos];
                totalY += primitiveVertexPositions[startingPos + 1];
                totalZ += primitiveVertexPositions[startingPos + 2];
            }

            primitivePositions.add(totalX / drawMode.vertexCount);
            primitivePositions.add(totalY / drawMode.vertexCount);
            primitivePositions.add(totalZ / drawMode.vertexCount);
        }

        return inner.vertex(x, y, z);
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        return inner.color(red, green, blue, alpha * tintAlpha / 0xFF);
    }

    @Override
    public VertexConsumer texture(float u, float v) {
        return inner.texture(u, v);
    }

    @Override
    public VertexConsumer overlay(int u, int v) {
        return inner.overlay(u, v);
    }

    @Override
    public VertexConsumer light(int u, int v) {
        return inner.light(u, v);
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        return inner.normal(x, y, z);
    }

    @Override
    public void next() {
        inner.next();
    }

    @Override
    public void fixedColor(int red, int green, int blue, int alpha) {
        inner.fixedColor(red, green, blue, alpha * tintAlpha / 0xFF);
    }

    @Override
    public void unfixColor() {
        inner.unfixColor();
    }

    public void begin(VertexFormat.DrawMode drawMode, VertexFormat vertexFormat) {
        inner.begin(drawMode, vertexFormat);
        this.drawMode = drawMode;
        primitiveVertexPositions = new float[drawMode.vertexCount * 3];
    }

    public void end() {
        inner.end();
        if (currentPosIdx != 0) {
            Prefab.logger.warn("Primitive not finished! Pos idx at: " + currentPosIdx);
            currentPosIdx = 0;
        }
        primitiveVertexPositions = null;
        currentVert = 0;
        firstPrimFinished = false;
    }

    public float[] getPrimitivePositions() {
        return primitivePositions.toFloatArray();
    }

    public void clear() {
        inner.clear();
        primitivePositions.clear();
    }

    public BufferBuilder getInner() {
        return inner;
    }

}
