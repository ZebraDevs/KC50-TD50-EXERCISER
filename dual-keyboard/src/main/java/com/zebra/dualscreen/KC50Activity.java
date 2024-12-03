package com.zebra.dualscreen;

import static android.provider.ContactsContract.Directory.PACKAGE_NAME;
import static android.view.Gravity.LEFT;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import java.util.List;
import java.util.Random;

/*
* ZEBRA WORKSTATION CONNECT EXERCISER -
* */
public class KC50Activity extends AppCompatActivity {

    private final static String TAG1 = "LIFECYCLE";
    String last_activity_state ="N/A";
    TextView tvOut;

    private WindowManager windowManagerOT;
    private View floatingKeyboardViewOT;
    EditText editText2;
    TextView editText3;

    private int lastX, lastY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      setContentView(R.layout.activity_kc50);

        tvOut = findViewById(R.id.txtOut);
        tvOut.setMovementMethod(new ScrollingMovementMethod());
        printAppendOnScreen( currentScreenInfo() );

        Log.i(TAG1, "onCreate");
        last_activity_state = "onCreate";

        printAppendOnScreen( getDisplayInfo() );

        registerReceiver(dualScreenReceiver, new IntentFilter("com.zebra.dualscreen.TD50_ACTION"), Context.RECEIVER_NOT_EXPORTED);

        windowManagerOT = (WindowManager) getSystemService(WINDOW_SERVICE);
        floatingKeyboardViewOT = LayoutInflater.from(this).inflate(R.layout.custom_keyboard, null);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        );

        floatingKeyboardViewOT.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        int x = (int) event.getRawX();
                        int y = (int) event.getRawY();

// Calculate the distance moved and update the window position
                        params.x = params.x + (x - lastX);
                        params.y = params.y + (y - lastY);

// Update the floating window position
                        windowManagerOT.updateViewLayout(floatingKeyboardViewOT, params);

// Save the new touch position
                        lastX = x;
                        lastY = y;
                        break;
                }
                return false;
            }
        });

        editText2 = findViewById(R.id.editTextText2);
        editText2.setOnTouchListener((v, event) -> {
//            Log.i("onCreate/setOnTouchListener","#NDZL/listener: Thread "+ Thread.currentThread().getName()+ "(" + Thread.currentThread().getId() + ")");

            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                try {windowManagerOT.addView(floatingKeyboardViewOT, params);} catch (Exception e) {}
                setupKeyboardButtons(editText2);
            }

            return true;
        });


        editText3 = findViewById(R.id.editTextText3);
        editText3.setOnTouchListener((v, event) -> {
//            Log.i("onCreate/setOnTouchListener","#NDZL/listener: Thread "+ Thread.currentThread().getName()+ "(" + Thread.currentThread().getId() + ")");

            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                try {windowManagerOT.addView(floatingKeyboardViewOT, params);} catch (Exception e) {}
                setupKeyboardButtons(editText3);
            }

            return true;
        });

    }

    private void setupKeyboardButtons(View etView) {
        Button keyA = floatingKeyboardViewOT.findViewById(R.id.keyA);
        Button keyB = floatingKeyboardViewOT.findViewById(R.id.keyB);
        Button keyC = floatingKeyboardViewOT.findViewById(R.id.keyC);
        Button key0 = floatingKeyboardViewOT.findViewById(R.id.key0);
        Button key1 = floatingKeyboardViewOT.findViewById(R.id.key1);
        Button keyEnter = floatingKeyboardViewOT.findViewById(R.id.keyEnter);
        Button keyDelete = floatingKeyboardViewOT.findViewById(R.id.keyDEL);

//        Log.i("FloatingKeyboardService/setupKeyboardButtons","#NDZL/body: Thread "+ Thread.currentThread().getName()+ "(" + Thread.currentThread().getId() + ")");


        EditText _et = (EditText) etView;
        keyA.setOnClickListener(v -> {
//            Log.i("FloatingKeyboardService/setOnClickListener","#NDZL/listener: Thread "+ Thread.currentThread().getName()+ "(" + Thread.currentThread().getId() + ")");
            _et.getText().append("A");
        });

        keyB.setOnClickListener(v -> {
            _et.getText().append("B");
        });

        keyEnter.setOnClickListener(v -> {
            //try {
            windowManagerOT.removeView(floatingKeyboardViewOT);
            //} catch (Exception e) {}
        });

        keyDelete.setOnClickListener(v -> {
            //try {
            _et.setText( _et.getText().delete(_et.getText().length()-1, _et.getText().length()) );
            //} catch (Exception e) {}
        });

        // Add more button setups for other keys
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG1, "onStart");
        last_activity_state = "onStart";
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG1, "onResume");
        last_activity_state = "onResume";

//        EditText editText1 = findViewById(R.id.editText1);
//        editText1.requestFocus();
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(editText1, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG1, "onPause");
        last_activity_state = "onPause";
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG1, "onStop");
        last_activity_state = "onStop";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG1, "onDestroy");
        last_activity_state = "onDestroy";
    }





    public void btClick_HDLauncher(View v) {

/*
        Intent intent2= new Intent(getBaseContext(), FooActivity.class);
        intent2.setFlags(  Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent2);
*/



        //Launch on 2nd display if available
        ActivityOptions ao = ActivityOptions.makeBasic();
        int other_display_id = 0;
        int cur_display_id = getDisplay().getDisplayId();
        if(cur_display_id>0){
            other_display_id = cur_display_id;
        } else {
            DisplayManager displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
            Display[] displays = displayManager.getDisplays();
            for (Display _d : displays) {
                if (_d.getDisplayId() >0 ) {
                    other_display_id = _d.getDisplayId();
                    break;
                }
            }
        }

        ao.setLaunchDisplayId(other_display_id);

        Bundle bao = ao.toBundle();
        Intent intent= new Intent(getBaseContext(), TD50Activity.class);
        intent.setFlags(  Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent, bao);
    }



    String currentScreenInfo(){
        StringBuilder _sb = new StringBuilder();

        DisplayManager displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = displayManager.getDisplays();
        int currentDisplayId = getDisplay().getDisplayId();
        for (Display _d:displays) {

            if( _d.getDisplayId() != currentDisplayId )
                continue;

            _sb.append( "DISPLAY ID="+_d.getDisplayId()+" " ) ;
            DisplayMetrics metrics = new DisplayMetrics();
            _d.getRealMetrics(metrics);

            String DISPMETRICS_DENSITY= ""+metrics.density;
            String DISPMETRICS_DENSITY_DPI= ""+metrics.densityDpi;
            String DISPMETRICS_SCALED_DENSITY= ""+metrics.scaledDensity;
            String DISPMETRICS_X_DPI= ""+metrics.xdpi;
            String DISPMETRICS_Y_DPI= ""+metrics.ydpi;
            String DISPMETRICS_H_PIXEL= ""+metrics.heightPixels;
            String DISPMETRICS_W_PIXEL= ""+metrics.widthPixels;

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    _sb.append( _d.getDeviceProductInfo().getProductId()+"\n" ) ;
                    String PROD_ID=_d.getDeviceProductInfo().getProductId();
                    String PROD_INFO=_d.getDeviceProductInfo().getName();
                    String PROD_MANUF_PNPID=_d.getDeviceProductInfo().getManufacturerPnpId();
                    String PROD_SINKTYPE=""+_d.getDeviceProductInfo().getConnectionToSinkType();
                    String PROD_YEARWEEK=""+_d.getDeviceProductInfo().getManufactureYear()+"-"+_d.getDeviceProductInfo().getManufactureWeek();
                    String PROD_MODELYEAR=""+_d.getDeviceProductInfo().getModelYear();
                    _sb.append( "NAME="+_d.getName()+" " ) ;
                    _sb.append( "PROD_ID="+ PROD_ID +" " ) ;
                    _sb.append( "PROD_INFO="+ PROD_INFO +" " ) ;
                    _sb.append( "PROD_MANUF_PNPID="+ PROD_MANUF_PNPID +" " ) ;
                    _sb.append( "PROD_SINKTYPE="+ PROD_SINKTYPE +" " ) ;
                    _sb.append( "PROD_YEARWEEK="+ PROD_YEARWEEK +" " ) ;
                    _sb.append( "PROD_MODELYEAR="+ PROD_MODELYEAR +" " ) ;
                }
            } catch (Exception e) {
                _sb.append( "NO PRODUCT INFO AVAILABLE.") ;
            }
            _sb.append("\n");

            /*
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            String DISPMETRICS_DENSITY= ""+metrics.density;
            String DISPMETRICS_DENSITY_DPI= ""+metrics.densityDpi;
            String DISPMETRICS_SCALED_DENSITY= ""+metrics.scaledDensity;
            String DISPMETRICS_X_DPI= ""+metrics.xdpi;
            String DISPMETRICS_Y_DPI= ""+metrics.ydpi;
            String DISPMETRICS_H_PIXEL= ""+metrics.heightPixels;
            String DISPMETRICS_W_PIXEL= ""+metrics.widthPixels;

             */


            _sb.append( "DISPMETRICS_DENSITY="+ DISPMETRICS_DENSITY +" " ) ;
            _sb.append( "DISPMETRICS_DENSITY_DPI="+ DISPMETRICS_DENSITY_DPI +" " ) ;
            _sb.append( "DISPMETRICS_SCALED_DENSITY="+ DISPMETRICS_SCALED_DENSITY +" " ) ;
            _sb.append( "DISPMETRICS_X_DPI="+ DISPMETRICS_X_DPI +" " ) ;
            _sb.append( "DISPMETRICS_Y_DPI="+ DISPMETRICS_Y_DPI +" " ) ;
            _sb.append( "DISPMETRICS_H_PIXEL="+ DISPMETRICS_H_PIXEL +" " ) ;
            _sb.append( "DISPMETRICS_W_PIXEL="+ DISPMETRICS_W_PIXEL +" " ) ;
            _sb.append("\n");
        }

        Display activityDisplay = getDisplay();
        _sb.append( "\nACTIVITY DISPLAY ID="+activityDisplay.getDisplayId()+"\n" ) ;

        _sb.append("\n");
        WindowMetrics maximumWindowMetrics = getWindowManager().getMaximumWindowMetrics();
        WindowMetrics windowMetrics = getWindowManager().getCurrentWindowMetrics();

        _sb.append( "METRICS MAX="+maximumWindowMetrics.getBounds().width()+"x"+maximumWindowMetrics.getBounds().height()+"\n" ) ;
        _sb.append( "METRICS CURRENT="+windowMetrics.getBounds().width()+"x"+windowMetrics.getBounds().height()+"\n" ) ;
        _sb.append("\n");


        return _sb.toString();
    }


    boolean _IS_TOP_RESUMED_ACTIVITY=false;
    @Override
    public void onTopResumedActivityChanged(boolean topResumed) {
        if (topResumed) {
            _IS_TOP_RESUMED_ACTIVITY = true;
        } else {
            _IS_TOP_RESUMED_ACTIVITY = false;
        }

        tvOut.setText( currentScreenInfo() );
        printAppendOnScreen( getDisplayInfo() );

    }

    private String getAppPackageName() {
        String appPackageName = "";
        PackageManager pm = this.getPackageManager();

        final Intent secondaryIntent = new Intent(Intent.ACTION_MAIN, null);
        secondaryIntent.addCategory(Intent.CATEGORY_SECONDARY_HOME);
        final List<ResolveInfo> appsList = pm.queryIntentActivities(secondaryIntent, 0);

        for (ResolveInfo resolveInfo : appsList) {
            if (resolveInfo.activityInfo.packageName.contains(PACKAGE_NAME)) {
                appPackageName = resolveInfo.activityInfo.packageName;
            }
        }
        return appPackageName;
    }

    void printAppendOnScreen(String txt ){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvOut.setText( tvOut.getText() +"\n"+ txt );
            }
        });
    }


    String getDisplayInfo(){
        Display activityDisplay = getDisplay();

        return "ACTIVITY RUNNING ON DISPLAY ID="+activityDisplay.getDisplayId()+"\n" ;
    }

    public void onClickbtn_RED(View v) {
        sendMessageToTD50("RED");
    }
    public void onClickbtn_GREEN(View v) {
        sendMessageToTD50("GREEN");
    }
    public void onClickbtn_BLUE(View v) {
        sendMessageToTD50("BLUE");
    }

    void sendMessageToTD50(String msg){
        Intent intent = new Intent();
        intent.setAction("com.zebra.dualscreen.KC50_ACTION");
        intent.putExtra("msg", msg);
        sendBroadcast(intent);
    }



    private BroadcastReceiver dualScreenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.zebra.dualscreen.TD50_ACTION")) {
                String message = intent.getStringExtra("msg");
                Log.i("KC50", "Received message from TD50: " + message);
                printAppendOnScreen( "Received message from TD50: " + message );

                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Random random = new Random();
                        for (int i = 0; i < 4; i++) {
                            int randomValue = 50 + random.nextInt(451); // 451 because upper bound is exclusive
                            //playPCMData(generateNote((double) randomValue, 0.3, 1000));
                            TD50Activity.playTone();
                        }
                    }
                });
            }
        }
    };


    public void playPCMData(byte[] data) {
        try {
            AudioTrack audioTrack = new AudioTrack(
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build(),
                    new AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_8BIT)
                            .setSampleRate(11025)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                            .build(),
                    data.length,
                    AudioTrack.MODE_STATIC,
                    AudioManager.AUDIO_SESSION_ID_GENERATE
            );

            audioTrack.write(data, 0, data.length);
            audioTrack.play();
        } catch (Exception e) {
            // Handle exception
        }
    }


    public  byte[] generateNote(double frequency, double durationInSeconds, int sampleRate) {
        int numSamples = (int) (durationInSeconds * sampleRate);
        byte[] samples = new byte[numSamples];

        for (int i = 0; i < numSamples; i++) {
            double time = i / 100.0;
            samples[i] = (byte) ((Math.sin(frequency * time) * 127.0) + 0);
        }

        return samples;
    }



}