package com.opengl.vr;

import android.content.Context;
import android.opengl.Matrix;

import com.opengl.vr.objects.ModelObject;
import com.opengl.vr.objects.Skybox;
import com.opengl.vr.programs.ModelObjectProgram;
import com.opengl.vr.util.Object3D;
import com.opengl.vr.util.ObjectReader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bozhao on 2017/12/8.
 */

public class PikachuRenderer {

    private final Context mContext;

    private final float[] mModelMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mMVPMatrix = new float[16];

    private List<ModelObjectProgram> mPikachuProgram;
    private List<ModelObject> mPikachu;

    public PikachuRenderer(Context context) {
        mContext = context;
    }

    public void onSurfaceCreated() {

        List<Object3D> objectList = ObjectReader.readMultiObj(mContext,"assets/pikachu.obj");
        mPikachuProgram = new ArrayList<>();
        mPikachu = new ArrayList<>();
        for (int i = 0; i < objectList.size(); ++i) {
            ModelObjectProgram program = new ModelObjectProgram(mContext);
            mPikachuProgram.add(program);

            ModelObject object = new ModelObject(mContext, objectList.get(i));
            mPikachu.add(object);
        }
    }

    public void onSurfaceChanged(int width, int height) {
        Matrix.setIdentityM(mModelMatrix,0);
        Matrix.scaleM(mModelMatrix,0,0.02f,0.02f,0.02f);
        Matrix.setLookAtM(mViewMatrix, 0, 0,0,0, 0,0,-1,0,1,0);
        Matrix.perspectiveM(mProjectionMatrix, 0,45, (float) width / (float) height, 25f, 300f);
    }

    public void onDrawFrame() {
        float[] modelViewMatrix = new float[16];
        Matrix.multiplyMM(modelViewMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, modelViewMatrix, 0);

        int size = mPikachuProgram.size();
        for (int i = 0; i < size; ++i) {
            mPikachuProgram.get(i).useProgram();
            mPikachuProgram.get(i).setUniforms(mMVPMatrix, 0, 0, 0);
            mPikachu.get(i).bindData(mPikachuProgram.get(i));
            mPikachu.get(i).draw();
        }
    }
}
