package com.opengl.vr.objects;
import android.opengl.Matrix;

import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.setRotateEulerM;

import java.util.Random;

import com.opengl.vr.util.Geometry.Point;
import com.opengl.vr.util.Geometry.Vector;

public class ParticleShooter {
    private final Point mPosition;
    private final Vector mDirection;
    private final int mColor;

    private float mAngleVariance;
    private float mSpeedVariance;

    private final Random mRandom = new Random();

    private float[] mRotateMatrix = new float[16];
    private float[] mDirectionVector = new float[4];
    private float[] mResultVector = new float[4];

    public ParticleShooter(Point position, Vector direction, int color, float angleVarianceInDegrees, float speedVariance) {
        mPosition = position;
        mDirection = direction;
        mColor = color;

        mAngleVariance = angleVarianceInDegrees;
        mSpeedVariance = speedVariance;

        mDirectionVector[0] = direction.x;
        mDirectionVector[1] = direction.y;
        mDirectionVector[2] = direction.z;
    }
    
    public void addParticles(ParticleSystem particleSystem, float currentTime, int count) {
        for (int i = 0; i < count; i++) {
            Matrix.setRotateEulerM(mRotateMatrix, 0,
                    (mRandom.nextFloat() - 0.5f) * mAngleVariance,
                    (mRandom.nextFloat() - 0.5f) * mAngleVariance,
                    (mRandom.nextFloat() - 0.5f) * mAngleVariance);

            Matrix.multiplyMV(mResultVector, 0, mRotateMatrix, 0, mDirectionVector, 0);

            float speedAdjustment = 1.0f + mRandom.nextFloat() * mSpeedVariance;

            Vector thisDirection = new Vector(
                    mResultVector[0] * speedAdjustment,
                    mResultVector[1] * speedAdjustment,
                    mResultVector[2] * speedAdjustment);

            particleSystem.addParticle(mPosition, mColor, thisDirection, currentTime);
        }       
    }
}
