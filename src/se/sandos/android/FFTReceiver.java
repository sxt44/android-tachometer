package se.sandos.android;

import org.teneighty.fft.FourierTransform;
import org.teneighty.fft.FourierTransformFactory;
import org.teneighty.fft.dope.BackedRealDopeVector;
import org.teneighty.fft.dope.ComplexDopeVector;
import org.teneighty.fft.dope.DefaultComplexDopeVector;
import org.teneighty.fft.dope.RealDopeVector;

import android.util.Log;

public class FFTReceiver implements AudioCallback {

    private static final int FFT_SIZE = 128;

    public FFTReceiver() {

        //org.teneighty.fft.DefaultFourierTransformFactory fact = new org.teneighty.fft.DefaultFourierTransformFactory();

//        testFFT(256);
//        testFFT(512);
//        testFFT(1024);
//        testFFT(2048);
//        testFFT(4096);
//        testFFT(8192);
//        testFFT(16384);
//        testFFT(32768);
//        testFFT(65536);

    }

    @Override
    public void receiveAudio(short[] audio) {
        double in[] = new double[FFT_SIZE];
        for(int i=0; i<FFT_SIZE; i++) {
            in[i] = audio[i] / 65536.0;
        }
        
        testFFT(FFT_SIZE, in);
    }

    private void testFFT(int size, double[] data) {
        FourierTransform transform = FourierTransformFactory.getTransform(size);
//        double in[] = new double[size];
//        Random r = new Random();
//        for (int i = 0; i < size; i++) {
//            in[i] = r.nextDouble();
//        }
        RealDopeVector in_dope = new BackedRealDopeVector(data);
        ComplexDopeVector out_dope = new DefaultComplexDopeVector(size);

        long time = System.currentTimeMillis();
        transform.forward(in_dope, out_dope);
        Log.i("MAJS", "Size: " + size + " took " + (System.currentTimeMillis() - time));

        double max = -1;
        int index = -1;
        for(int i=0; i<size; i++) {
            
            double imag = out_dope.getImaginary(i);
            double real = out_dope.getReal(i);
            double amp = Math.sqrt(imag*imag + real*real);
            if(max < amp) {
                max = amp;
                index = i;
            }
        }
        
        Log.i("", "Found highest frequency at index " + index + " at value " + max);
        //System.gc();
    }

}
