package com.opengl.vr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.Matrix;
import android.util.Log;

import com.opengl.vr.objects.Heightmap;
import com.opengl.vr.programs.HeightmapShaderProgram;
import com.opengl.vr.util.Geometry.Vector;
import com.opengl.vr.util.TextureHelper;


/**
 * Created by bozhao on 2017/12/6.
 */

public class HeightmapRenderer {
    private final Context mContext;

    private final float[] mModelMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mMVMatrix = new float[16];
    private final float[] mITMVMatrix = new float[16];
    private final float[] mMVPMatrix = new float[16];

    private float[] mSensorRotatoMatrix = new float[16];

    private final float[] mDayVectorToLight = { 0.61f, 0.64f, -0.47f, 0.0f };
    private final float[] mNightVectorToLight = { 0.31f, 0.36f, -0.88f, 0.0f };

    // 每个点光源放在粒子发射器上方一个单位处
    // 因为粒子发射器向y方向平移-1.5f,向z方向平移-5.0f，所以这里对点光源位置也做相应处理
    private final float[] mPointLightingPositions = {
        -1.0f, 1.0f - 1.5f, 0.0f - 5.0f, 1.0f,
         0.0f, 1.0f - 1.5f, 0.0f - 5.0f, 1.0f,
         1.0f, 1.0f - 1.5f, 0.0f - 5.0f, 1.0f
    };

    private final float[] mPointLightingColors = {
        1.00f, 0.20f, 0.02f,
        0.02f, 0.25f, 0.02f,
        0.02f, 0.20f, 1.00f
    };

    // private final float[] testVector = new float[4];
    private HeightmapShaderProgram    mHeightmapProgram;
    private Heightmap                 mHeightmap;

    private int mGrassTexture;
    private int mStoneTexture;

    public HeightmapRenderer(Context context) {
        mContext = context;
        /*testVector[0] = 0;
        testVector[1] = 0;
        testVector[2] = -1;
        testVector[3] = 0;*/
    }

    public void handleSensorRotate(float[] rotateMatrix) {
        float[] tempViewMatrix = new float[16];
        Matrix.setLookAtM(tempViewMatrix, 0, 0,0,0, 0,0,-1,0,-1,0);
        Matrix.rotateM(tempViewMatrix, 0, 90, 1,0,0);
        Matrix.multiplyMM(mViewMatrix, 0, rotateMatrix, 0, tempViewMatrix, 0);
    }

    public void handleTouchDrag(float deltaX, float deltaY) {
        Matrix.setLookAtM(mViewMatrix, 0, 0,0,0, 0,0,-1,0,1,0);
        Matrix.rotateM(mViewMatrix, 0, -deltaY, 1f, 0f, 0f);
        Matrix.rotateM(mViewMatrix, 0, -deltaX, 0f, 1f, 0f);

        /*float[] testMatrix = new float[16];
        Matrix.setIdentityM(testMatrix, 0);
        Matrix.rotateM(testMatrix, 0, deltaY, 1f, 0f, 0f);
        Matrix.rotateM(testMatrix, 0, deltaX, 0f, 1f, 0f);
        float[] resultVector = new float[4];
        Matrix.multiplyMV(resultVector, 0, testMatrix, 0, testVector, 0);
        Log.w("TEST","x: " + resultVector[0] + "y: " + resultVector[1] + "z: " + resultVector[2] + "w: " + resultVector[3]);*/
    }

    public void onSurfaceCreated() {
        mHeightmapProgram = new HeightmapShaderProgram(mContext);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.heightmap, options);
        mHeightmap = new Heightmap(bitmap);
        //mHeightmap = new Heightmap(((BitmapDrawable)mContext.getResources().getDrawable(R.drawable.heightmap)).getBitmap());

        mGrassTexture = TextureHelper.loadTexture(mContext, R.drawable.noisy_grass_public_domain);
        mStoneTexture = TextureHelper.loadTexture(mContext, R.drawable.stone_public_domain);
    }

    public void onSurfaceChanged(int width, int height) {
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.scaleM(mModelMatrix, 0, 100, 10, 100);
        Matrix.translateM(mModelMatrix, 0, 0f, -0.15f, -0.05f);

        Matrix.setLookAtM(mViewMatrix, 0, 0,0,0, 0,0,-1,0,1,0);
        Matrix.perspectiveM(mProjectionMatrix, 0,45, (float) width / (float) height, 1f, 100f);
    }

    public void onDrawFrame() {
        Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        float[] tempMatrix = new float[16];
        Matrix.invertM(tempMatrix, 0, mMVMatrix, 0);
        Matrix.transposeM(mITMVMatrix, 0, tempMatrix, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0);

        float[] vectorToLightInEyeSpace = new float[4];
        Matrix.multiplyMV(vectorToLightInEyeSpace, 0, mViewMatrix, 0, mNightVectorToLight, 0);
        float[] pointLightPositionsInEyeSpace = new float[12];
        Matrix.multiplyMV(pointLightPositionsInEyeSpace, 0, mViewMatrix, 0, mPointLightingPositions, 0);
        Matrix.multiplyMV(pointLightPositionsInEyeSpace, 4, mViewMatrix, 0, mPointLightingPositions, 4);
        Matrix.multiplyMV(pointLightPositionsInEyeSpace, 8, mViewMatrix, 0, mPointLightingPositions, 8);

        mHeightmapProgram.useProgram();
        mHeightmapProgram.setUniforms(
                mMVPMatrix, mMVMatrix, mITMVMatrix,
                vectorToLightInEyeSpace, pointLightPositionsInEyeSpace, mPointLightingColors,
                mGrassTexture);
        mHeightmap.bindData(mHeightmapProgram);
        mHeightmap.draw();
    }
}
