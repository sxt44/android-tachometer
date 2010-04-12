package se.sandos.android;

import android.app.Activity;
import android.os.Bundle;
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

        final Recorder recorderInstance = new Recorder(receiver, getApplicationContext());
        this.recorder = recorderInstance;
        final Thread th = new Thread(recorderInstance);
        this.th = th;
        th.start();
        recorderInstance.setRecording(true);
    }
    
    @Override
    public void onPause()
    {
        super.onPause();
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                recorder.setPaused(true);
                try {
                    th.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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