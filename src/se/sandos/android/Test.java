package se.sandos.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Test extends Activity implements HertzReceiver {

    private Thread th;
    private Recorder recorder;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

    }

    @Override
    public void onStart()
    {
        super.onStart();
        
        final Recorder recorderInstance = new Recorder(new FFTReceiver(this));
        this.recorder = recorderInstance;
        final Thread th = new Thread(recorderInstance);
        this.th = th;
        th.start();
        recorderInstance.setRecording(true);
    }
    
    @Override
    public void onPause()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    try {
                        this.wait(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
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
                TextView v = (TextView) findViewById(R.id.Frequency);
                
                //CharSequence t = v.getText();
                
                //v.setText(v.getText() + " " + frequency);
                v.setText("" + frequency);
                v.invalidate();
            }
        });
    }

}