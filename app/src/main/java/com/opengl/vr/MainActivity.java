package com.opengl.vr;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.opengl.vr.objects.Heightmap;

import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES10.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES10.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES10.GL_DEPTH_TEST;
import static android.opengl.GLES10.GL_LESS;
import static android.opengl.GLES10.glEnable;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_GREATER;
import static android.opengl.GLES20.GL_LEQUAL;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glDepthMask;
import static android.opengl.GLES20.glViewport;


public class MainActivity extends AppCompatActivity implements GLSurfaceView.Renderer, SensorEventListener {

    private GLSurfaceView mVRSurfaceView;
    private SensorManager mSensorManager;
    private Sensor        mRotateSensor;

    private ParticlesRenderer mParticlesRenderer;
    private SkyboxRenderer    mSkyboxRenderer;
    private HeightmapRenderer mHeightmapRenderer;
    private PikachuRenderer   mPikachuRenderer;

    private float mXRotation, mYRotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!supportsOpenGLES2(this)) {
            throw new IllegalStateException("OpenGL ES 2.0 is not supported on this phone.");
        }
        mParticlesRenderer = new ParticlesRenderer(this);
        mSkyboxRenderer = new SkyboxRenderer(this);
        mHeightmapRenderer = new HeightmapRenderer(this);
       // mPikachuRenderer = new PikachuRenderer(this);

        mVRSurfaceView=(GLSurfaceView) findViewById(R.id.vr_surface_view);
        mVRSurfaceView.setEGLContextClientVersion(2);
        mVRSurfaceView.setRenderer(this);
        mVRSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        mVRSurfaceView.setOnTouchListener(new OnTouchListener() {
            float previousX, previousY;
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event != null) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        previousX = event.getX();
                        previousY = event.getY();
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        final float deltaX = event.getX() - previousX;
                        final float deltaY = event.getY() - previousY;

                        previousX = event.getX();
                        previousY = event.getY();

                        mVRSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                               handleTouchDrag(deltaX, deltaY);
                            }
                        });
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

        mSensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        // 获取旋转传感器
        mRotateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    public void handleTouchDrag(float deltaX, float deltaY) {
        mXRotation += deltaX / 16f;
        mYRotation += deltaY / 16f;

        if (mYRotation < -90) {
            mYRotation = -90;
        } else if (mYRotation > 90) {
            mYRotation = 90;
        }

        mSkyboxRenderer.handleTouchDrag(mXRotation, mYRotation);
        mHeightmapRenderer.handleTouchDrag(mXRotation, mYRotation);
        mParticlesRenderer.handleTouchDrag(mXRotation, mYRotation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mRotateSensor, SensorManager.SENSOR_DELAY_GAME);
        mVRSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        mVRSurfaceView.onPause();
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        mSkyboxRenderer.onSurfaceCreated();
        mHeightmapRenderer.onSurfaceCreated();
        mParticlesRenderer.onSurfaceCreated();
       // mPikachuRenderer.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        glViewport(0, 0, i, i1);
        mSkyboxRenderer.onSurfaceChanged(i, i1);
        mHeightmapRenderer.onSurfaceChanged(i, i1);
        mParticlesRenderer.onSurfaceChanged(i, i1);
       // mPikachuRenderer.onSurfaceChanged(i, i1);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        mHeightmapRenderer.onDrawFrame();

        glDepthFunc(GL_LEQUAL);
        mSkyboxRenderer.onDrawFrame();
        glDepthFunc(GL_LESS);

        glDepthMask(false);
        mParticlesRenderer.onDrawFrame();
        glDepthMask(true);

      //  mPikachuRenderer.onDrawFrame();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] matrix = new float[16];
        SensorManager.getRotationMatrixFromVector(matrix, sensorEvent.values);
        /*mSkyboxRenderer.handleSensorRotate(matrix);
        mParticlesRenderer.handleSensorRotate(matrix);
        mHeightmapRenderer.handleSensorRotate(matrix);*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private boolean supportsOpenGLES2(final Context context) {
        final ActivityManager activityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();
        return configurationInfo.reqGlEsVersion >= 0x20000;
    }
}
