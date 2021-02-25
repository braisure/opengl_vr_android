package com.opengl.vr.programs;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE1;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniform3fv;
import static android.opengl.GLES20.glUniform4fv;
import static android.opengl.GLES20.glUniformMatrix4fv;
import android.content.Context;

import com.opengl.vr.R;
import com.opengl.vr.util.Geometry.Vector;

public class HeightmapShaderProgram extends ShaderProgram {
    private final int uMVMatrixLocation;
    private final int uITMVMatrixLocation;
    private final int uMVPMatrixLocation;
    private final int uVectorToLightLocation;
    private final int uPointLightPositionLocation;
    private final int uPointLightColorLocation;

    private final int uTextureSamplerLocation;
    private final int uTextureSampler2Location;

    private final int aPositionLocation;
    private final int aNormalLocation;
    private final int aTextureCoordinatesLocation;

    public HeightmapShaderProgram(Context context) {
        super(context, R.raw.heightmap_vertex_shader,
            R.raw.heightmap_fragment_shader);

        uMVMatrixLocation = glGetUniformLocation(program, U_MV_MATRIX);
        uITMVMatrixLocation = glGetUniformLocation(program, U_IT_MV_MATRIX);
        uMVPMatrixLocation = glGetUniformLocation(program, U_MVP_MATRIX);
        uVectorToLightLocation = glGetUniformLocation(program, U_VECTOR_TO_LIGHT);

        uPointLightPositionLocation = glGetUniformLocation(program, U_POINT_LIGHT_POSITION);
        uPointLightColorLocation = glGetUniformLocation(program, U_POINT_LIGHT_COLOR);

        uTextureSamplerLocation = glGetUniformLocation(program, U_TEXTURE_SAMPLER);
        uTextureSampler2Location = glGetUniformLocation(program, U_TEXTURE_SAMPLER2);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aNormalLocation = glGetAttribLocation(program, A_NORMAL);
        aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    public void setUniforms(float[] mvpMatrix, float[] mvMatrix, float[] itmvMatrix,
                            float[] vectorToDirectionalLight,
                            float[] pointLightPositions,
                            float[] pointLightColors,
                            int textureId) {
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0);
        glUniformMatrix4fv(uMVMatrixLocation, 1, false, mvMatrix, 0);
        glUniformMatrix4fv(uITMVMatrixLocation, 1, false, itmvMatrix, 0);

        glUniform3fv(uVectorToLightLocation, 1, vectorToDirectionalLight, 0);
        glUniform4fv(uPointLightPositionLocation, 3, pointLightPositions, 0);
        glUniform3fv(uPointLightColorLocation, 3, pointLightColors, 0);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId);
        glUniform1i(uTextureSamplerLocation, 0);
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
}
