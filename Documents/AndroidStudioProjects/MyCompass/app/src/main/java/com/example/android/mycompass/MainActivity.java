package com.example.android.mycompass;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;

public class MainActivity extends AppCompatActivity {

    //TODO: adjust for geomagnetic north
    //TODO: only change direction values if device is moving (GYROSCOPE)
    //Adaap4rxbhrf

    private SensorManager mSensorManager;
    private SensorEventListener mEventListenerAccelerometer;
    private SensorEventListener mEventListenerMagneticField;
    private SensorEventListener mEventListenerGyroscope;

    private SensorManagerSimulator mSensorManagerSimulator;
    private org.openintents.sensorsimulator.hardware.SensorEventListener mSimulatedEventListenerAccelerometer;
    private org.openintents.sensorsimulator.hardware.SensorEventListener mSimulatedEventListenerMagneticField;
    private org.openintents.sensorsimulator.hardware.SensorEventListener mSimulatedEventListenerGyroscope;
    private org.openintents.sensorsimulator.hardware.SensorEventListener sensorOrientation;

    private float[] valuesAccelerometer;// = new float[3];
    private float[] valuesMagneticField;// = new float[3];
    private float[] valuesGyroscope;
    float[] valuesRotation;// = new float[9];
    float[] valuesOrientation;// = new float[3];
    private static final double MIN_GYROSCOPE = 0.001;

    boolean ownSensors = true, newAzimuth = false;
    public double azimuth, currentAzimuth = 0;
    CompassView mCompassView;
    TextView viewCount;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCompassView = (CompassView) findViewById(R.id.view_compass);
        viewCount = (TextView)findViewById(R.id.count_draws_textview);

    }

    @Override
    protected void onStart() {
        super.onStart();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if(mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null || mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null || mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) == null) {
            //Get values from simulated sensors
            ownSensors = false;

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            mSensorManagerSimulator = SensorManagerSimulator.getSystemService(this, SENSOR_SERVICE);
            mSensorManagerSimulator.connectSimulator();

        }
        initListeners();
    }

    private void initListeners(){
        valuesRotation = new float[9];
        valuesOrientation = new float[3];
        if(ownSensors) {
            mEventListenerAccelerometer = new SensorEventListener() {
                @Override
                public void onSensorChanged(android.hardware.SensorEvent event) {

                    valuesAccelerometer = event.values.clone();
                    /*if (valuesMagneticField != null) {
                        if (SensorManager.getRotationMatrix(valuesRotation, null, valuesAccelerometer, valuesMagneticField)) {
                            SensorManager.getOrientation(valuesRotation, valuesOrientation);
                            azimuth = Math.toDegrees(valuesOrientation[0]);
                            if (Math.abs(currentAzimuth - azimuth) > 5) {
                                newAzimuth = true;
                                currentAzimuth = azimuth;
                            }
                        }
                    }

                    if(newAzimuth) {
                        mCompassView.azimutValue = azimuth;
                        mCompassView.postInvalidate();
                        viewCount.setText(String.valueOf(mCompassView.getDrawCount()));
                        newAzimuth = false;
                    }*/
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };
            mEventListenerMagneticField = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {

                    valuesMagneticField = event.values.clone();
                    /*if (valuesAccelerometer != null) {
                        if (SensorManager.getRotationMatrix(valuesRotation, null, valuesAccelerometer, valuesMagneticField)) {
                            SensorManager.getOrientation(valuesRotation, valuesOrientation);
                            azimuth = Math.toDegrees(valuesOrientation[0]);
                            if (Math.abs(currentAzimuth - azimuth) > 5) {
                                newAzimuth = true;
                                currentAzimuth = azimuth;
                            }
                        }
                    }

                    if(newAzimuth) {
                        mCompassView.azimutValue = azimuth;
                        mCompassView.postInvalidate();
                        viewCount.setText(String.valueOf(mCompassView.getDrawCount()));
                        newAzimuth = false;
                    }*/
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };
            mEventListenerGyroscope = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {

                    valuesGyroscope = event.values.clone();
                    if (valuesAccelerometer != null && valuesMagneticField != null) {
                        if (SensorManager.getRotationMatrix(valuesRotation, null, valuesAccelerometer, valuesMagneticField)) {
                            SensorManager.getOrientation(valuesRotation, valuesOrientation);
                            azimuth = Math.toDegrees(valuesOrientation[0]);
                            if (valuesGyroscope[0] > MIN_GYROSCOPE && valuesGyroscope[1] > MIN_GYROSCOPE && valuesGyroscope[2] > MIN_GYROSCOPE) {
                                newAzimuth = true;
                                currentAzimuth = azimuth;
                                viewCount.setText(String.valueOf(valuesGyroscope[0] + "\n" + valuesGyroscope[1] + "\n" + valuesGyroscope[2]));

                            }
                        }
                    }

                    if (newAzimuth) {
                        mCompassView.azimutValue = azimuth;
                        mCompassView.postInvalidate();
                        newAzimuth = false;
                    }
                }
                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };
        } else {
            mSimulatedEventListenerAccelerometer = new org.openintents.sensorsimulator.hardware.SensorEventListener() {
                @Override
                public void onSensorChanged(org.openintents.sensorsimulator.hardware.SensorEvent sensorEvent) {

                    valuesAccelerometer = sensorEvent.values.clone();
                    /*if (valuesMagneticField != null) {
                        if (SensorManager.getRotationMatrix(valuesRotation, null, valuesAccelerometer, valuesMagneticField)) {
                            SensorManager.getOrientation(valuesRotation, valuesOrientation);
                            azimuth = Math.toDegrees(valuesOrientation[0]);
                            if (Math.abs(currentAzimuth - azimuth) > 5) {
                                newAzimuth = true;
                                currentAzimuth = azimuth;
                            }
                        }

                        if (newAzimuth) {
                            mCompassView.azimutValue = azimuth;
                            mCompassView.postInvalidate();
                            viewCount.setText(String.valueOf(mCompassView.getDrawCount()));
                            newAzimuth = false;
                        }
                    }*/
                }
                @Override
                public void onAccuracyChanged(org.openintents.sensorsimulator.hardware.Sensor sensor, int i) {

                }
            };
            mSimulatedEventListenerMagneticField = new org.openintents.sensorsimulator.hardware.SensorEventListener() {
                @Override
                public void onSensorChanged(org.openintents.sensorsimulator.hardware.SensorEvent sensorEvent) {
                    valuesMagneticField = sensorEvent.values.clone();



                   // if(valuesAccelerometer != null){

                    //}
                }

                /*@Override
                                public void onSensorChanged(org.openintents.sensorsimulator.hardware.SensorEvent sensorEvent) {

                                    valuesMagneticField = sensorEvent.values.clone();
                                    /*if (valuesAccelerometer != null) {
                                        if (SensorManager.getRotationMatrix(valuesRotation, null, valuesAccelerometer, valuesMagneticField)) {
                                            SensorManager.getOrientation(valuesRotation, valuesOrientation);
                                            azimuth = Math.toDegrees(valuesOrientation[0]);
                                            if (Math.abs(currentAzimuth - azimuth) > 5) {
                                                newAzimuth = true;
                                                currentAzimuth = azimuth;
                                            }
                                        }
                                    }

                                    if(newAzimuth) {
                                        mCompassView.azimutValue = currentAzimuth;
                                        mCompassView.postInvalidate();
                                        viewCount.setText(String.valueOf(mCompassView.getDrawCount()));
                                        newAzimuth = false;
                                    }
                                }*/
                @Override
                public void onAccuracyChanged(org.openintents.sensorsimulator.hardware.Sensor sensor, int i) {

                }
            };
            mSimulatedEventListenerGyroscope = new org.openintents.sensorsimulator.hardware.SensorEventListener(){
                @Override
                public void onSensorChanged(org.openintents.sensorsimulator.hardware.SensorEvent sensorEvent) {

                    valuesGyroscope = sensorEvent.values.clone();
                    if (valuesAccelerometer != null && valuesMagneticField != null) {
                        if (SensorManager.getRotationMatrix(valuesRotation, null, valuesAccelerometer, valuesMagneticField)) {
                            SensorManager.getOrientation(valuesRotation, valuesOrientation);
                            azimuth = Math.toDegrees(valuesOrientation[0]);
                            if (valuesGyroscope[0] > MIN_GYROSCOPE && valuesGyroscope[1] > MIN_GYROSCOPE && valuesGyroscope[2] > MIN_GYROSCOPE) {
                                newAzimuth = true;
                                currentAzimuth = azimuth;
                            }
                        }
                    }

                    if (newAzimuth) {
                        mCompassView.azimutValue = azimuth;
                        mCompassView.postInvalidate();
                        newAzimuth = false;
                    }
                }
                @Override
                public void onAccuracyChanged(org.openintents.sensorsimulator.hardware.Sensor sensor, int i) {

                }
            };
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ownSensors) {
            mSensorManager.registerListener(
                    mEventListenerAccelerometer,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(
                    mEventListenerMagneticField,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                    SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(
                    mEventListenerGyroscope,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            mSensorManagerSimulator.registerListener(
                    mSimulatedEventListenerAccelerometer,
                    mSensorManagerSimulator.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManagerSimulator.registerListener(
                    mSimulatedEventListenerMagneticField,
                    mSensorManagerSimulator.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                    SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManagerSimulator.registerListener(
                    mSimulatedEventListenerGyroscope,
                    mSensorManagerSimulator.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        if(ownSensors){
            mSensorManager.unregisterListener(mEventListenerAccelerometer);
            mSensorManager.unregisterListener(mEventListenerMagneticField);
            mSensorManager.unregisterListener(mEventListenerGyroscope);
        } else {
            mSensorManagerSimulator.unregisterListener(mSimulatedEventListenerAccelerometer);
            mSensorManagerSimulator.unregisterListener(mSimulatedEventListenerMagneticField);
            mSensorManagerSimulator.unregisterListener(mSimulatedEventListenerGyroscope);
            mSensorManagerSimulator.unregisterListener(sensorOrientation);
        }
        super.onStop();
    }

    private float[] smoothening(float[] values){
        float[] ret = new float[3];
        return ret;
    }
}

/*

        //mSensorManager.getSensorList(Sensor.TYPE_ALL).size();
        int p = mSensorManager.getSensorList(Sensor.TYPE_ALL).size();
        //sensorMagnetic = mSensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).get(0);
        if(mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() > 0)
            sensorGravity = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
        if(mSensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).size() > 0)
            sensorMagnetic = mSensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).get(0);

       // Sensor testSensor = mSensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).get(1);

        mSensorManager.registerListener(this, sensorMagnetic, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, sensorGravity, SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this, sensorGravity);
        mSensorManager.unregisterListener(this, sensorMagnetic);
    }

    protected float[] lowPass(float[] input, float[] output) {
        if (output == null)
            return input;
        for (int i = 0; i < input.length; i++)
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        return output;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            smoothed = lowPass(event.values.clone(), geomagnetic);
            geomagnetic[0] = smoothed[0];
            geomagnetic[1] = smoothed[1];
            geomagnetic[2] = smoothed[2];
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            smoothed = lowPass(event.values.clone(), gravity);
            gravity[0] = smoothed[0];
            gravity[1] = smoothed[1];
            gravity[2] = smoothed[2];
        }


        SensorManager.getRotationMatrix(rotation, null, gravity, geomagnetic);
        SensorManager.getOrientation(rotation, orientation);
        azimut = Math.toDegrees(orientation[0]);

        if(azimut != mCompassView.azimutValue) {
            mCompassView.azimutValue = azimut;
            mCompassView.postInvalidate();
        }



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}*/
