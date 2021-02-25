package com.opengl.vr;

import android.content.Context;
import android.opengl.Matrix;

import com.opengl.vr.objects.Skybox;
import com.opengl.vr.programs.SkyboxShaderProgram;
import com.opengl.vr.util.TextureHelper;

/**
 * Created by bozhao on 2017/12/5.
 */

public class SkyboxRenderer {

    private final Context mContext;

    private final float[] mModelMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mMVPMatrix = new float[16];

    private float[] mSensorRotatoMatrix = new float[16];

    private SkyboxShaderProgram mSkyboxProgram;
    private Skybox              mSkybox;

    private int mSkyboxTexture;

    public SkyboxRenderer(Context context) {
        mContext = context;
    }

    public void handleTouchDrag(float deltaX, float deltaY) {
        Matrix.setLookAtM(mViewMatrix, 0, 0,0,0, 0,0,-1,0,1,0);
        Matrix.rotateM(mViewMatrix, 0, -deltaY, 1f, 0f, 0f);
        Matrix.rotateM(mViewMatrix, 0, -deltaX, 0f, 1f, 0f);
    }

    public void handleSensorRotate(float[] rotateMatrix) {
        float[] tempViewMatrix = new float[16];
        Matrix.setLookAtM(tempViewMatrix, 0, 0,0,0, 0,0,-1,0,-1,0);
        Matrix.rotateM(tempViewMatrix, 0, 90, 1,0,0);
        Matrix.multiplyMM(mViewMatrix, 0, rotateMatrix, 0, tempViewMatrix, 0);
    }

    public void onSurfaceCreated() {
        mSkyboxProgram = new SkyboxShaderProgram(mContext);
        mSkybox = new Skybox();
        /*mSkyboxTexture = TextureHelper.loadCubeMap(mContext,
                new int[] { R.drawable.left, R.drawable.right,
                             R.drawable.bottom, R.drawable.top,
                             R.drawable.front, R.drawable.back });*/

        mSkyboxTexture = TextureHelper.loadCubeMap(mContext,
                new int[] { R.drawable.night_left, R.drawable.night_right,
                             R.drawable.night_bottom, R.drawable.night_top,
                             R.drawable.night_front, R.drawable.night_back});
    }

    public void onSurfaceChanged(int width, int height) {
        Matrix.setIdentityM(mModelMatrix,0);
        Matrix.setLookAtM(mViewMatrix, 0, 0,0,0, 0,0,-1,0,1,0);
        Matrix.perspectiveM(mProjectionMatrix, 0,45, (float) width / (float) height, 1f, 100f);
    }

    public void onDrawFrame() {
        float[] modelViewMatrix = new float[16];
        Matrix.multiplyMM(modelViewMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, modelViewMatrix, 0);

        mSkyboxProgram.useProgram();
        mSkyboxProgram.setUniforms(mMVPMatrix, mSkyboxTexture);
        mSkybox.bindData(mSkyboxProgram);
        mSkybox.draw();
    }

}
