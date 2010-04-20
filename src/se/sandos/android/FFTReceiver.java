package se.sandos.android;

import org.teneighty.fft.FourierTransform;
import org.teneighty.fft.dope.BackedRealDopeVector;
import org.teneighty.fft.dope.RealDopeVector;

import android.util.Log;

public class FFTReceiver implements AudioCallback {

    private static final int FFT_SIZE_BITS = 11;
    private static final int FFT_SIZE = 1 << FFT_SIZE_BITS;
    
    private static final int HOP = FFT_SIZE;
    private HertzReceiver receiver;
    private FFTView fft;
    //private double[] avg;
    private long[] avg = new long[FFT_SIZE/2];
    private FourierTransform transform;
    
    public FFTReceiver(HertzReceiver recv) {
        //transform = FourierTransformFactory.getTransform(FFT_SIZE);
        receiver = recv;
    }

    @Override
    public void receiveAudio(short[] audio, int numSamples, int sampleRate) {
        Log.v("MAJS", "" + numSamples);
        int offset = 0;
        //avg = new double[FFT_SIZE/2];
        //avg = new long[FFT_SIZE/2];
        for(int i=0; i<avg.length; i++) {
            avg[i] = 0;
        }
        long s = System.currentTimeMillis();
        int num = 0;
        
        while(offset + FFT_SIZE <= numSamples) {
            doFFT(audio, offset, sampleRate);
            offset += HOP;
            num++;
        }
            
        Log.v("MAJS", "" + num + " FFTs took " + (System.currentTimeMillis() - s));
        
        long max = Long.MIN_VALUE;
        int index = -1;
        for(int i=1; i<FFT_SIZE/2; i++) {
            if(max < avg[i]) {
                max = avg[i];
                index = i;
            }
        }
        Log.v("MAJS", "max at " + index + "[" + binFreq(index, sampleRate) + "] is " + max);
        
        FFTResult result = new FFTResult();
        result.frequency = binFreq(index, sampleRate);
        findFreq(avg, result, sampleRate);
//
        fft.newFFT(avg, result.bin, sampleRate, FFT_SIZE);
//
//        //Log.v("MAJS", "Freq: " + result.frequency);
        receiver.hertz((int) result.frequency);

    }

    private int binFreq(int index, int sampleRate) {
        return sampleRate*index/FFT_SIZE;
        
    }

    public void setView(FFTView view)
    {
        fft = view;
    }
    
    private void doFFT(short[] audio, int offset, int sampleRate) {
        short[] dummy = new short[FFT_SIZE];
//        for(int i=FFT_SIZE/2; i<FFT_SIZE; i++) {
//            dummy[i] = 0;
//        }
        for(int i=0; i<FFT_SIZE; i++) {
            dummy[i] = audio[i+offset];
        }
        
//        Log.v("MAJS", "Firt values java: " + dummy[0]);
//        Log.v("MAJS", "Next-Last value java: " + dummy[FFT_SIZE/2-1]);
//        Log.v("MAJS", "Last value java: " + dummy[FFT_SIZE/2]);

        JniTest.fix_fft(dummy, (short) FFT_SIZE_BITS, (short)0);
         
        for(int i=0; i<FFT_SIZE/2; i++) {
            long mag = (long) Math.sqrt((long)dummy[i] * dummy[i] + (long)dummy[i*2] * dummy[i*2]);
            //avg[i] += dummy[i];
            avg[i] += mag;
        }
        
//        double in[] = new double[FFT_SIZE];
//        long avg = 0;
//        for(int i=0; i<FFT_SIZE; i++) {
//            avg += audio[i+offset];
//        }
//        
//        avg /= FFT_SIZE;
//        
//        for(int i=0; i<FFT_SIZE; i++) {
//            double where = i/((double)FFT_SIZE-1);
//            double window = where;
//            if(window > 0.5) {
//                window = 1.0 - window;
//            }
//            in[i] = ((audio[i+offset]-avg) / 65536.0) * window * 2;
//        }
//        
//        testFFT(FFT_SIZE, in, sampleRate);
    }

    private void testFFT(int size, double[] data, int sampleRate) {
        RealDopeVector in_dope = new BackedRealDopeVector(data);
        MyDope out_dope = new MyDope(size);

        long time = System.currentTimeMillis();
        transform.forward(in_dope, out_dope);
        Log.i("MAJS", "Size: " + size + " took " + (System.currentTimeMillis() - time));

        for(int i=0; i<size/2 && i<avg.length; i++) {
            avg[i] += out_dope.magAt(i);
        }
    }

    /**
     * Find the strongest frequency. Uses some simple interpolation for now.
     * @param fftMagnitude Array of magnitude of the FFT
     * @param result
     */
    private void findFreq(long[] fftMagnitude, FFTResult result, int sampleRate)
    {
        double max = -1;
        double maxMinus = -1;
        double maxPlus = -1;
        int index = -1;
        
        for(int i=0; i<fftMagnitude.length; i++) {
            double amp = fftMagnitude[i];
            if(max < amp) {
                max = amp;
                
                maxPlus = 0;
                if(i+1 < fftMagnitude.length) {
                    maxPlus = fftMagnitude[i+1];
                }
                
                maxMinus = 0;
                if(i-1 > 0) {
                    maxMinus = fftMagnitude[i-1];
                }
                
                index = i;
            }
        }
        
        double d = (maxPlus - maxMinus) / (maxMinus + max + maxPlus);
        
        double freq = sampleRate * index / FFT_SIZE;
        double freq2 = sampleRate * (index + d) / FFT_SIZE;

        Log.i("MAJS", "Found highest frequency at index " + index + "[" + freq + "][" + freq2 + "] at value " + max);

        result.frequency = freq2;
        result.frequency_raw = freq;
        result.bin = index;
    }
}
