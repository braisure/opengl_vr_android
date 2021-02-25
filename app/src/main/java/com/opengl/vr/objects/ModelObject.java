package com.opengl.vr.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.opengl.vr.programs.ModelObjectProgram;
import com.opengl.vr.util.Object3D;
import com.opengl.vr.util.TextureHelper;

import java.io.IOException;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.opengl.vr.Constants.BYTES_PER_FLOAT;

/**
 * Created by bozhao on 2017/12/8.
 */

public class ModelObject {

    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int NORMAL_COMPONENT_COUNT = 3;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int TOTAL_COMPONENT_COUNT =
            POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT;

    private static final int STRIDE = TOTAL_COMPONENT_COUNT * BYTES_PER_FLOAT;

    private final Context mContext;
    private final Object3D mObject;

    private int mTextureId;

    public ModelObject(Context context, Object3D object) {
        mContext = context;
        mObject = object;

        try {
            mTextureId = createTexture(BitmapFactory.decodeStream(mContext.getResources().getAssets().open(mObject.mtl.map_Kd)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getTextureId() {
        return mTextureId;
    }

    public void bindData(ModelObjectProgram modelObjectProgram) {

        glEnableVertexAttribArray(modelObjectProgram.getPositionAttributeLocation());
        glVertexAttribPointer(modelObjectProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, GL_FLOAT, false,0, mObject.vert);

        glEnableVertexAttribArray(modelObjectProgram.getNormalAttributeLocation());
        glVertexAttribPointer(modelObjectProgram.getNormalAttributeLocation(),
                NORMAL_COMPONENT_COUNT, GL_FLOAT, false, 0, mObject.vertNorl);

        glEnableVertexAttribArray(modelObjectProgram.getTextureCoordinatesAttributeLocation());
        glVertexAttribPointer(modelObjectProgram.getTextureCoordinatesAttributeLocation(),
                TEXTURE_COORDINATES_COMPONENT_COUNT, GL_FLOAT, false, 0, mObject.vertTexture);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mTextureId);
        glUniform1i(modelObjectProgram.getTextureSamplerLocation(), 0);
    }

    public void draw() {
       glDrawArrays(GL_TRIANGLES,0, mObject.vertCount);
    }

    private int createTexture(Bitmap bitmap){
        int[] texture = new int[1];
        if (bitmap != null && !bitmap.isRecycled()) {

            GLES20.glGenTextures(1,texture,0);
            glBindTexture(GL_TEXTURE_2D, texture[0]);

            GLES20.glTexParameterf(GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
            return texture[0];
        }
        return 0;
    }

}
