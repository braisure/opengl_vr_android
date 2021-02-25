package com.opengl.vr.objects;

import android.graphics.Color;

import com.opengl.vr.data.VertexArray;
import com.opengl.vr.programs.ParticleShaderProgram;
import com.opengl.vr.util.Geometry.Point;
import com.opengl.vr.util.Geometry.Vector;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDrawArrays;
import static com.opengl.vr.Constants.BYTES_PER_FLOAT;

public class ParticleSystem {
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int VECTOR_COMPONENT_COUNT = 3;
    private static final int PARTICLE_START_TIME_COMPONENT_COUNT = 1;

    private static final int TOTAL_COMPONENT_COUNT =
            POSITION_COMPONENT_COUNT
          + COLOR_COMPONENT_COUNT
          + VECTOR_COMPONENT_COUNT
          + PARTICLE_START_TIME_COMPONENT_COUNT;

    private static final int STRIDE = TOTAL_COMPONENT_COUNT * BYTES_PER_FLOAT;

    private final float[] mParticles;
    private final VertexArray mVertexArray;
    private final int mMaxParticleCount;

    private int m_CurrentParticleCount;
    private int m_NextParticle;

    public ParticleSystem(int maxParticleCount) {
        mParticles = new float[maxParticleCount * TOTAL_COMPONENT_COUNT];
        mVertexArray = new VertexArray(mParticles);
        mMaxParticleCount = maxParticleCount;
    }

    public void addParticle(Point position, int color, Vector direction, float particleStartTime) {
        final int particleOffset = m_NextParticle * TOTAL_COMPONENT_COUNT;

        int currentOffset = particleOffset;
        m_NextParticle++;

        if (m_CurrentParticleCount < mMaxParticleCount) {
            m_CurrentParticleCount++;
        }

        if (m_NextParticle == mMaxParticleCount) {
            m_NextParticle = 0;
        }

        mParticles[currentOffset++] = position.x;
        mParticles[currentOffset++] = position.y;
        mParticles[currentOffset++] = position.z;

        mParticles[currentOffset++] = Color.red(color) / 255f;
        mParticles[currentOffset++] = Color.green(color) / 255f;
        mParticles[currentOffset++] = Color.blue(color) / 255f;

        mParticles[currentOffset++] = direction.x;
        mParticles[currentOffset++] = direction.y;
        mParticles[currentOffset++] = direction.z;

        mParticles[currentOffset++] = particleStartTime;

        mVertexArray.updateBuffer(mParticles, particleOffset, TOTAL_COMPONENT_COUNT);
    }

    public void bindData(ParticleShaderProgram particleProgram) {
        int dataOffset = 0;
        mVertexArray.setVertexAttribPointer(dataOffset,
                particleProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, STRIDE);
        dataOffset += POSITION_COMPONENT_COUNT;

        mVertexArray.setVertexAttribPointer(dataOffset,
                particleProgram.getColorAttributeLocation(),
                COLOR_COMPONENT_COUNT, STRIDE);
        dataOffset += COLOR_COMPONENT_COUNT;

        mVertexArray.setVertexAttribPointer(dataOffset,
                particleProgram.getDirectionVectorAttributeLocation(),
                VECTOR_COMPONENT_COUNT, STRIDE);
        dataOffset += VECTOR_COMPONENT_COUNT;

        mVertexArray.setVertexAttribPointer(dataOffset,
                particleProgram.getParticleStartTimeAttributeLocation(),
                PARTICLE_START_TIME_COMPONENT_COUNT, STRIDE);
        dataOffset += PARTICLE_START_TIME_COMPONENT_COUNT;
    }

    public void draw() {
        glDrawArrays(GL_POINTS, 0, m_CurrentParticleCount);
    }

}