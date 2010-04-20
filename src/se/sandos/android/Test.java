package se.sandos.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Test extends Activity implements HertzReceiver {

    private Thread th;
    private Recorder recorder;
    private FFTView fft;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
        fft = new FFTView(getApplicationContext());
        ll.addView(fft);
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }
    
    public void onResume()
    {
        super.onResume();
        
        FFTReceiver receiver = new FFTReceiver(this);
        receiver.setView(fft);

        if(th == null) {
            Log.v("MAJS", "RESUMING RECORDING!");
            final Recorder recorderInstance = new Recorder(receiver, getApplicationContext());
            this.recorder = recorderInstance;
            final Thread th = new Thread(recorderInstance);
            this.th = th;
            th.start();
            recorderInstance.setRecording(true);
        }
        
//        new Thread(new Runnable(){
//            @Override
//            public void run() {
//                short[] fftbuffer = new short[1<<16];
//                while(true) {
//                    int res = JniTest.fix_fft(fftbuffer, (short) 16, (short)0);
//                }
//            }
//            
//        }).start();
    }
    
    @Override
    public void onPause()
    {
        super.onPause();
        
        clearThread();
    }

    private void clearThread() {
        Log.v("MAJS", "Clearing thread");
        recorder.setRecording(false);
        try {
            th.interrupt();
            if(th.isAlive()) {
                th.join(500);
                if(!th.isAlive()) {
                    th = null;
                }
            } else {
                th = null;
            }
        } catch (InterruptedException e) {
            Log.v("MAJS", e.getMessage());
        }
    }
    
    @Override
    public void hertz(final int frequency) {
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                fft.invalidate();
                
                TextView v = (TextView) findViewById(R.id.Frequency);
                
                //CharSequence t = v.getText();
                
                //v.setText(v.getText() + " " + frequency);
                v.setText("" + frequency);
                v.invalidate();
            }
        });
    }

}