package com.zebra.dualscreen;

import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.hardware.display.DisplayManager;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;
//https://developer.android.com/reference/android/hardware/display/DisplayManager#registerDisplayListener(android.hardware.display.DisplayManager.DisplayListener,%20android.os.Handler)

import android.view.ViewGroup;

//HAD TO SET org.gradle.jvmargs=-Xmx8G TO BUILD....
public class TD50Activity extends AppCompatActivity  {

    Intent starterIntent;
    TextView tvOut;
    private View customKeyboardView;
    Button b;


    private WindowManager windowManagerOT;
    private View floatingKeyboardViewOT;
    EditText editText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        starterIntent = getIntent();

        setContentView(R.layout.activity_td50);

        windowManagerOT = (WindowManager) getSystemService(WINDOW_SERVICE);
        floatingKeyboardViewOT = LayoutInflater.from(this).inflate(R.layout.custom_keyboard, null);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        );

        editText2 = findViewById(R.id.editTextText2);
        editText2.setOnTouchListener((v, event) -> {
//            Log.i("onCreate/setOnTouchListener","#NDZL/listener: Thread "+ Thread.currentThread().getName()+ "(" + Thread.currentThread().getId() + ")");

            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                windowManagerOT.addView(floatingKeyboardViewOT, params);
                setupKeyboardButtons();
            }

            setupKeyboardButtons();
            return true;
        });


        DisplayManager displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        displayManager.registerDisplayListener(new DisplayManager.DisplayListener() {
            @Override
            public void onDisplayAdded(int displayId) {  //As a multi-screen-aware app, detect a 2nd screen when available
                //move this launcher to the 2nd screen when available
                finish();
                ActivityOptions ao =ActivityOptions.makeBasic();
                ao.setLaunchDisplayId(displayId);
                Bundle bao = ao.toBundle();
                starterIntent.setFlags(  Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(starterIntent, bao);
            }

            @Override
            public void onDisplayRemoved(int displayId) {
                finish();
                ActivityOptions ao =ActivityOptions.makeBasic();
                ao.setLaunchDisplayId(0);
                Bundle bao = ao.toBundle();
                starterIntent.setFlags(  Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(starterIntent, bao);
            }

            @Override
            public void onDisplayChanged(int displayId) {
                recreate();
            }
        }, null);

        tvOut = findViewById(R.id.textView);

        printAppendOnScreen( currentScreenInfo() );

        registerReceiver(dualScreenReceiver, new IntentFilter("com.zebra.dualscreen.KC50_ACTION"), Context.RECEIVER_NOT_EXPORTED);


        //requestPermissions(new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, 1);
    }

    private void setupKeyboardButtons() {
        Button keyA = floatingKeyboardViewOT.findViewById(R.id.keyA);
        Button keyB = floatingKeyboardViewOT.findViewById(R.id.keyB);
        Button keyC = floatingKeyboardViewOT.findViewById(R.id.keyC);
        Button key0 = floatingKeyboardViewOT.findViewById(R.id.key0);
        Button key1 = floatingKeyboardViewOT.findViewById(R.id.key1);
        Button keyEnter = floatingKeyboardViewOT.findViewById(R.id.keyEnter);

//        Log.i("FloatingKeyboardService/setupKeyboardButtons","#NDZL/body: Thread "+ Thread.currentThread().getName()+ "(" + Thread.currentThread().getId() + ")");

        keyA.setOnClickListener(v -> {
//            Log.i("FloatingKeyboardService/setOnClickListener","#NDZL/listener: Thread "+ Thread.currentThread().getName()+ "(" + Thread.currentThread().getId() + ")");
            editText2.getText().append("A");
        });

        keyB.setOnClickListener(v -> {
            editText2.getText().append("B");
        });

        keyEnter.setOnClickListener(v -> {
            //try {
                windowManagerOT.removeView(floatingKeyboardViewOT);
            //} catch (Exception e) {}
        });



        // Add more button setups for other keys
    }



    /*    public void onKeyClick(View view) {
        Button key = (Button) view;
        EditText editText2 = findViewById(R.id.editTextText2);
        int start = editText2.getSelectionStart();
        int end = editText2.getSelectionEnd();
        editText2.getText().replace(Math.min(start, end), Math.max(start, end), key.getText(), 0, key.getText().length());
    }*/

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("TC50", "onStop");

        try {windowManagerOT.removeViewImmediate(floatingKeyboardViewOT);} catch (Exception e) {}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void onClickbtn_CYAN(View v) {
        sendMessageToKC50("CYAN");
    }
    public void onClickbtn_MAGENTA(View v) {
        sendMessageToKC50("MAGENTA");
    }
    public void onClickbtn_YELLOW(View v) {
        sendMessageToKC50("YELLOW");
    }

    void sendMessageToKC50(String msg){
        Intent intent = new Intent();
        intent.setAction("com.zebra.dualscreen.TD50_ACTION");
        intent.putExtra("msg", msg);
        sendBroadcast(intent);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }



    public String loadJSONFromAsset(String jsonAssetFileName) {
        String json = null;
        try {
            InputStream is = this.getAssets().open(jsonAssetFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    private void saveStringToLocalFile(String srcFile, String jsonAsset) {
        try {
            File f = new File(srcFile);
            if (f.exists()) {
                f.delete();
            }

            f.createNewFile();
            Process _p = Runtime.getRuntime().exec("chmod 666 " + srcFile); //chmod needed for /enterprise
            _p.waitFor();
            Log.i("TD50", "chmod 666 result="+_p.exitValue());

            FileOutputStream fos = new FileOutputStream(f);
            fos.write( jsonAsset.getBytes(StandardCharsets.UTF_8) );
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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


    private BroadcastReceiver dualScreenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.zebra.dualscreen.KC50_ACTION")) {
                String message = intent.getStringExtra("msg");
                Log.i("TD50", "Received message from KC50: " + message);
                printAppendOnScreen( "Received message from KC50: " + message );
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Random random = new Random();
                        for (int i = 0; i < 4; i++) {
                            int randomValue = 50 + random.nextInt(451); // 451 because upper bound is exclusive
                            //playPCMData(generateNote((double) randomValue, 0.3, 1000));
                            playTone();
                            //generateHighPitchBeep();
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

    static ToneGenerator tGen = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
    public static void playTone(){

        tGen.startTone(ToneGenerator.	TONE_CDMA_HIGH_SS, 15);
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

    public void generateHighPitchBeep() {
        int frequency = 8000; // Set the desired frequency above 2000Hz
        int duration = 1000; // Duration in milliseconds
        int sampleRate = 32000; // Sample rate in Hz

        int numSamples = (duration * sampleRate) / 1000;
        double[] sample = new double[numSamples];

        for (int i = 0; i < numSamples; i++) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / frequency));
        }

        int bufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
        );

        AudioTrack audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM
        );

        audioTrack.play();
        // Convert double[] to short[] and write to AudioTrack
        short[] shortSample = new short[numSamples];
        for (int i = 0; i < numSamples; i++) {
            shortSample[i] = (short) (sample[i] * Short.MAX_VALUE);
        }
        audioTrack.write(shortSample, 0, numSamples);

        audioTrack.stop();
        audioTrack.release();
    }


}