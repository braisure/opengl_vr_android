package com.opengl.vr.programs;

import android.content.Context;

import com.opengl.vr.R;

import java.net.PortUnreachableException;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by bozhao on 2017/12/8.
 */

public class ModelObjectProgram extends ShaderProgram {

    private final int uMVPMatrixLocation;
    private final int uKaLocation;
    private final int uKdLocation;
    private final int uKsLocation;

    private final int uTextureSamplerLocation;

    private final int aPositionLocation;
    private final int aNormalLocation;
    private final int aTextureCoordinatesLocation;

    public ModelObjectProgram(Context context) {
        super(context, R.raw.modelobject_vertex_shader,
                R.raw.modelobject_fragment_shader);

        uMVPMatrixLocation = glGetUniformLocation(program, U_MVP_MATRIX);

        uKaLocation = glGetUniformLocation(program, U_KA);
        uKdLocation = glGetUniformLocation(program, U_KD);
        uKsLocation = glGetUniformLocation(program, U_KS);

        uTextureSamplerLocation = glGetUniformLocation(program, U_TEXTURE_SAMPLER);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aNormalLocation = glGetAttribLocation(program, A_NORMAL);
        aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    public void setUniforms(float[] mvpMatrix, float ka, float kd, float ks) {
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0);

        glUniform1f(uKaLocation, ka);
        glUniform1f(uKdLocation, kd);
        glUniform1f(uKsLocation, ks);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getNormalAttributeLocation() {
        return aNormalLocation;
    }

    public int getTextureCoordinatesAttributeLocation() {
        return aTextureCoordinatesLocation;
    }

    public int getTextureSamplerLocation() {
        return uTextureSamplerLocation;
    }
}
