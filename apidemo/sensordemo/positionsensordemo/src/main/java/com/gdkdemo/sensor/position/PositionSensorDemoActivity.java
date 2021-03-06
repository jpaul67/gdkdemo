package com.gdkdemo.sensor.position;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;

import com.gdkdemo.sensor.position.service.PositionSensorDemoLocalService;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;


// The "main" activity...
public class PositionSensorDemoActivity extends Activity
{
    // For tap event
    private GestureDetector mGestureDetector;

    // Service to handle liveCard publishing, etc...
    private boolean mIsBound = false;
    private PositionSensorDemoLocalService positionSensorDemoLocalService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d("onServiceConnected() called.");
            positionSensorDemoLocalService = ((PositionSensorDemoLocalService.LocalBinder)service).getService();
        }
        public void onServiceDisconnected(ComponentName className) {
            Log.d("onServiceDisconnected() called.");
            positionSensorDemoLocalService = null;
        }
    };
    private void doBindService()
    {
        bindService(new Intent(this, PositionSensorDemoLocalService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }
    private void doUnbindService() {
        if (mIsBound) {
            unbindService(serviceConnection);
            mIsBound = false;
        }
    }
    private void doStartService()
    {
        startService(new Intent(this, PositionSensorDemoLocalService.class));
    }
    private void doStopService()
    {
        stopService(new Intent(this, PositionSensorDemoLocalService.class));
    }


    @Override
    protected void onDestroy()
    {
        doUnbindService();
        // doStopService();   // TBD: When do we call Stop service???
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d("onCreate() called.");

        setContentView(R.layout.activity_positionsensordemo);

        // For gesture handling.
        mGestureDetector = createGestureDetector(this);

        // bind does not work. We need to call start() explilicitly...
        // doBindService();
        doStartService();
        // TBD: We need to call doStopService() when user "closes" the app....
        // ...

    }


    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d("onResume() called.");

    }



    // TBD:
    // Just use context menu instead of gesture ???
    // ...

    @Override
    public boolean onGenericMotionEvent(MotionEvent event)
    {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }

    private GestureDetector createGestureDetector(Context context)
    {
        GestureDetector gestureDetector = new GestureDetector(context);
        //Create a base listener for generic gestures
        gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if(Log.D) Log.d("gesture = " + gesture);
                if (gesture == Gesture.TAP) {
                    handleGestureTap();
                    return true;
                } else if (gesture == Gesture.TWO_TAP) {
                    handleGestureTwoTap();
                    return true;
                }
                return false;
            }
        });
        return gestureDetector;
    }

    private void handleGestureTap()
    {
        Log.d("handleGestureTap() called.");
        doStopService();
        finish();
    }

    private void handleGestureTwoTap()
    {
        Log.d("handleGestureTwoTap() called.");
    }


}
