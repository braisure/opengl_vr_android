package com.opengl.vr;

import android.content.Context;
import android.graphics.Color;
import android.opengl.Matrix;

import com.opengl.vr.objects.ParticleShooter;
import com.opengl.vr.objects.ParticleSystem;
import com.opengl.vr.programs.ParticleShaderProgram;
import com.opengl.vr.util.Geometry.Point;
import com.opengl.vr.util.Geometry.Vector;
import com.opengl.vr.util.TextureHelper;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;

public class ParticlesRenderer {
    private final Context context;

    private final float[] mModelMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mMVPMatrix = new float[16];

    private float[] mSensorRotatoMatrix = new float[16];

    private ParticleShaderProgram mParticleProgram;
    private ParticleSystem mParticleSystem;
    private ParticleShooter mRedParticleShooter;
    private ParticleShooter mGreenParticleShooter;
    private ParticleShooter mBlueParticleShooter;

    private long mGlobalStartTime;

    private int  mTexture;

    public ParticlesRenderer(Context context) {
        this.context = context;
    }

    public void handleSensorRotate(float[] rotateMatrix) {
       // mSensorRotatoMatrix = rotateMatrix;

        float[] tempViewMatrix = new float[16];
        Matrix.setLookAtM(tempViewMatrix, 0, 0,0,0, 0,0,-1,0,-1,0);
        Matrix.rotateM(tempViewMatrix, 0, 90, 1,0,0);
        Matrix.multiplyMM(mViewMatrix, 0, rotateMatrix, 0, tempViewMatrix, 0);
    }

    public void handleTouchDrag(float deltaX, float deltaY) {
        Matrix.setLookAtM(mViewMatrix, 0, 0,0,0, 0,0,-1,0,1,0);
        Matrix.rotateM(mViewMatrix, 0, -deltaY, 1f, 0f, 0f);
        Matrix.rotateM(mViewMatrix, 0, -deltaX, 0f, 1f, 0f);
    }

    public void onSurfaceCreated() {

        mParticleProgram = new ParticleShaderProgram(context);
        mParticleSystem = new ParticleSystem(10000);
        mGlobalStartTime = System.nanoTime();
        
        final Vector particleDirection = new Vector(0f, 0.5f, 0f);
        
        final float angleVarianceInDegrees = 5f; 
        final float speedVariance = 1f;

        mRedParticleShooter = new ParticleShooter(
            new Point(-1f, 0f, 0f),
            particleDirection,                
            Color.rgb(255, 50, 5),
            angleVarianceInDegrees,
            speedVariance);

        mGreenParticleShooter = new ParticleShooter(
            new Point(0f, 0f, 0f), 
            particleDirection,
            Color.rgb(25, 255, 25),
            angleVarianceInDegrees,
            speedVariance);

        mBlueParticleShooter = new ParticleShooter(
            new Point(1f, 0f, 0f), 
            particleDirection,
            Color.rgb(5, 50, 255),
            angleVarianceInDegrees,
            speedVariance);

        mTexture = TextureHelper.loadTexture(context, R.drawable.particle_texture);
    }

    public void onSurfaceChanged(int width, int height) {
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0f, -1.5f, -5f);
        Matrix.setLookAtM(mViewMatrix, 0, 0,0,0, 0,0,-1,0,1,0);
        Matrix.perspectiveM(mProjectionMatrix, 0,45, (float) width / (float) height, 1f, 100f);
    }

    public void onDrawFrame() {
        float[] modelViewMatrix = new float[16];
        Matrix.multiplyMM(modelViewMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, modelViewMatrix, 0);

        float currentTime = (System.nanoTime() - mGlobalStartTime) / 1000000000f;

        mRedParticleShooter.addParticles(mParticleSystem, currentTime, 5);
        mGreenParticleShooter.addParticles(mParticleSystem, currentTime, 5);
        mBlueParticleShooter.addParticles(mParticleSystem, currentTime, 5);

        // Enable additive blending
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);

        mParticleProgram.useProgram();
        mParticleProgram.setUniforms(mMVPMatrix, currentTime, mTexture);
        mParticleSystem.bindData(mParticleProgram);
        mParticleSystem.draw();

        glDisable(GL_BLEND);
    }
}