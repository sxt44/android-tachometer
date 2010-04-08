package se.sandos.android;

import java.io.File;
import java.util.Random;

import org.teneighty.fft.FourierTransform;
import org.teneighty.fft.dope.BackedRealDopeVector;
import org.teneighty.fft.dope.ComplexDopeVector;
import org.teneighty.fft.dope.DefaultComplexDopeVector;
import org.teneighty.fft.dope.RealDopeVector;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class Test extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Record 20 seconds of audio.
        Recorder recorderInstance = new Recorder(new FFTReceiver());
        Thread th = new Thread(recorderInstance);
        th.start();
        recorderInstance.setRecording(true);
        synchronized (this) {
            try {
                this.wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        recorderInstance.setRecording(false);
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}