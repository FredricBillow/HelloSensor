package com.dynamicdusk.hellosensor;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


// source
//https://examples.javacodegeeks.com/android/core/hardware/sensor/android-accelerometer-example/
//https://www.javacodegeeks.com/2013/09/android-compass-code-example.html

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView mTextMessage;
    private LinearLayout mAccLinear;
    private TextView mHeading;
    // record the compass picture angle turned
    private float currentDegree = 0f;
    // device sensor manager
    private SensorManager mSensorManager;
    private TextView tvHeading;
    private ImageView mCompassImage;
    private boolean showCompass = false;
    private boolean onMain = true;
    private ConstraintLayout mainView;



    // ---- accelerometer
    private TextView currentX, currentY, currentZ;
    public Vibrator v;
    private float vibrateThreshold = 9f; //0;
    private Sensor accelerometer;
    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;
    private float lastX, lastY, lastZ;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    onMain = true;
                    mCompassImage.clearAnimation();
                    showCompass = false;
                    mTextMessage.setText("Welcome  to Hello Sensor, which demonstrates simple sensor possibilites!");
                    mTextMessage.setVisibility(View.VISIBLE);
                    mAccLinear.setVisibility(View.INVISIBLE);
                    mCompassImage.setVisibility(View.GONE);
                    tvHeading.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_dashboard:
                    onMain = false;
                    mCompassImage.clearAnimation();
                    mTextMessage.setVisibility(View.INVISIBLE);
                    mAccLinear.setVisibility(View.VISIBLE);
                    mCompassImage.setVisibility(View.GONE);
                    tvHeading.setVisibility(View.GONE);
                    showCompass = false;
                    return true;
                case R.id.navigation_notifications:
                    onMain = false;
                    mTextMessage.setVisibility(View.INVISIBLE);
                    mAccLinear.setVisibility(View.INVISIBLE);
                    showCompass = true;

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mAccLinear = (LinearLayout) findViewById(R.id.acclinear);
        mCompassImage = (ImageView) findViewById(R.id.main_iv);
        mHeading = (TextView) findViewById(R.id.tvHeading);
        mCompassImage.setVisibility(View.INVISIBLE);
        mCompassImage = (ImageView) findViewById(R.id.main_iv);
        tvHeading = (TextView) findViewById(R.id.tvHeading);
        mHeading.setVisibility(View.INVISIBLE);

        mTextMessage.setText("Welcome  to Hello Sensor. A simple app which demonstrates the some simple possibilites of your smart phone.");
        mTextMessage.setVisibility(View.VISIBLE);
        mAccLinear.setVisibility(View.INVISIBLE);
        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mainView = (ConstraintLayout) findViewById(R.id.container);


        //------------accelerometer
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            //vibrateThreshold = accelerometer.getMaximumRange() / 2;
            System.out.println("-----------------------------------" + vibrateThreshold);
        } else {
            // fai! we dont have an accelerometer!
        }

        //initialize vibration
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);





    }

    @Override
    protected void onResume() {
        super.onResume();

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }


    private boolean playedOnNorth = false;
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(!onMain && showCompass && event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            // get the angle around the z-axis rotated
            float degree = Math.round(event.values[0]);

            tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");

            // create a rotation animation (reverse turn degree degrees)
            RotateAnimation ra = new RotateAnimation(
                    currentDegree,
                    -degree,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            // how long the animation will take place
            ra.setDuration(210);

            // set the animation after the end of the reservation status
            ra.setFillAfter(true);

            // Start the animation
            mCompassImage.startAnimation(ra);
            currentDegree = -degree;

             if(degree > 345 || degree < 15) {
                 if(!playedOnNorth) {
                     System.out.println("-----------------------play north: "+degree + " playedOnNorth: " + playedOnNorth);
                     v.vibrate(100);
                     mainView.setBackgroundColor(Color.CYAN);
                     playedOnNorth = true;
                 }
             } else {
                 playedOnNorth = false;
                 mainView.setBackgroundColor(Color.WHITE);
             }


        } else if(!onMain && !showCompass && event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            //------------accelerometer
            displayCleanValues();
            displayCurrentValues();
            // get the change of the x,y,z values of the accelerometer
            deltaX = Math.abs(lastX - event.values[0]);
            deltaY = Math.abs(lastY - event.values[1]);
            deltaZ = Math.abs(lastZ - event.values[2]);

            // if the change is below 2, it is just plain noise
            if (deltaX < 2)
                deltaX = 0;
            if (deltaY < 2)
                deltaY = 0;
            if ((deltaX > vibrateThreshold) || (deltaY > vibrateThreshold)) {
                v.vibrate(50);
            }
        }
    }

    public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        currentX.setText(Float.toString((float) Math.round(deltaX * 100) / 100));
        currentY.setText(Float.toString((float) Math.round(deltaY * 100) / 100));
        currentZ.setText(Float.toString((float) Math.round(deltaZ * 100) / 100));
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }
}

