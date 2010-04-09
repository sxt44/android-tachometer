package se.sandos.android;

import org.teneighty.fft.FourierTransform;
import org.teneighty.fft.FourierTransformFactory;
import org.teneighty.fft.dope.BackedRealDopeVector;
import org.teneighty.fft.dope.ComplexDopeVector;
import org.teneighty.fft.dope.DefaultComplexDopeVector;
import org.teneighty.fft.dope.RealDopeVector;

import android.util.Log;

public class FFTReceiver implements AudioCallback {

    private static final int FFT_SIZE = 512;
    private HertzReceiver receiver;
    
    public FFTReceiver(HertzReceiver recv) {
        receiver = recv;
        
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
    public void receiveAudio(short[] audio, int numSamples) {
        int offset = 0;
        while(offset + FFT_SIZE < numSamples) {
            doFFT(audio, offset);
            offset += 32;
        }
    }

    private void doFFT(short[] audio, int offset) {
        double in[] = new double[FFT_SIZE];
        long avg = 0;
        for(int i=0; i<FFT_SIZE; i++) {
            avg += audio[i+offset];
        }
        
        avg /= FFT_SIZE;
        
        for(int i=0; i<FFT_SIZE; i++) {
            in[i] = (audio[i+offset]-avg) / 65536.0;
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

        //long time = System.currentTimeMillis();
        transform.forward(in_dope, out_dope);
        //Log.i("MAJS", "Size: " + size + " took " + (System.currentTimeMillis() - time));

        double max = -1;
        double maxMinus = -1;
        double maxPlus = -1;
        int index = -1;
        for(int i=0; i<size; i++) {
            
            double amp = magAt(out_dope, i);
            if(max < amp) {
                max = amp;
                
                maxPlus = magAt(out_dope, i+1);
                maxMinus = magAt(out_dope, i-1);
                
                index = i;
            }
        }
        
        double d = (maxPlus - maxMinus) / (maxMinus + max + maxPlus);
        
        double freq = (44100 / 2.0) * (index/(double)size);
        double freq2 = (44100 / 2.0) * ((index + d)/(double)size);
        Log.i("MAJS", "Found highest frequency at index " + index + "[" + freq + "][" + freq2 + "] at value " + max);
        receiver.hertz((int) freq2);
        
        //System.gc();
    }

    private double magAt(ComplexDopeVector out_dope, int i) {
        if(i < 0 || i >= out_dope.getLength()) {
            return 0;
        }
        double imag = out_dope.getImaginary(i);
        double real = out_dope.getReal(i);
        double amp = Math.sqrt(imag*imag + real*real);
        return amp;
    }

}
