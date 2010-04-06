package se.sandos.android;

import java.util.Random;

import org.teneighty.fft.FourierTransform;
import org.teneighty.fft.dope.BackedRealDopeVector;
import org.teneighty.fft.dope.ComplexDopeVector;
import org.teneighty.fft.dope.DefaultComplexDopeVector;
import org.teneighty.fft.dope.RealDopeVector;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class Test extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        org.teneighty.fft.DefaultFourierTransformFactory fact = new org.teneighty.fft.DefaultFourierTransformFactory();

        testFFT(fact, 256);
        testFFT(fact, 512);
        testFFT(fact, 1024);
        testFFT(fact, 2048);
        testFFT(fact, 4096);
        testFFT(fact, 8192);
        testFFT(fact, 16384);
        testFFT(fact, 32768);
        testFFT(fact, 65536);
    }

    private void testFFT(org.teneighty.fft.DefaultFourierTransformFactory fact, int size) {
        FourierTransform transform = fact.getTransform(size);
        double in[] = new double[size];
        Random r = new Random();
        for(int i=0; i<size; i++) {
            in[i] = r.nextDouble();
        }
        RealDopeVector in_dope = new BackedRealDopeVector( in );
        ComplexDopeVector out_dope = new DefaultComplexDopeVector( size );
        
        long time = System.currentTimeMillis();
        transform.forward(in_dope, out_dope);
        Log.i("MAJS", "Size: " + size + " took " + (System.currentTimeMillis()-time));
        
        System.gc();
    }
}