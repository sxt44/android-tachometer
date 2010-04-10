package se.sandos.android;

import org.teneighty.fft.FourierTransform;
import org.teneighty.fft.FourierTransformFactory;
import org.teneighty.fft.dope.BackedRealDopeVector;
import org.teneighty.fft.dope.RealDopeVector;

import android.util.Log;

public class FFTReceiver implements AudioCallback {

    private static final int FFT_SIZE = 512;
    private HertzReceiver receiver;
    private FFTView fft;
    private double[] avg;
    
    
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
    public void receiveAudio(short[] audio, int numSamples, int sampleRate) {
        //Log.v("MAJS", "" + numSamples);
        int offset = 0;
        avg = new double[FFT_SIZE];
        while(offset + FFT_SIZE < numSamples) {
            doFFT(audio, offset, sampleRate);
            offset += 16;
        }
        
        FFTResult result = new FFTResult();
        findFreq(avg, result, sampleRate);
        
        Log.v("MAJS", "Freq: " + result.frequency);
        receiver.hertz((int) result.frequency);

    }

    public void setView(FFTView view)
    {
        fft = view;
    }
    
    private void doFFT(short[] audio, int offset, int sampleRate) {
        double in[] = new double[FFT_SIZE];
        long avg = 0;
        for(int i=0; i<FFT_SIZE; i++) {
            avg += audio[i+offset];
        }
        
        avg /= FFT_SIZE;
        
        for(int i=0; i<FFT_SIZE; i++) {
            in[i] = (audio[i+offset]-avg) / 65536.0;
        }
        
        testFFT(FFT_SIZE, in, sampleRate);
    }

    private void testFFT(int size, double[] data, int sampleRate) {
        FourierTransform transform = FourierTransformFactory.getTransform(size);
//        double in[] = new double[size];
//        Random r = new Random();
//        for (int i = 0; i < size; i++) {
//            in[i] = r.nextDouble();
//        }
        RealDopeVector in_dope = new BackedRealDopeVector(data);
        MyDope out_dope = new MyDope(size);

        //long time = System.currentTimeMillis();
        transform.forward(in_dope, out_dope);
        //Log.i("MAJS", "Size: " + size + " took " + (System.currentTimeMillis() - time));

        fft.newFFT(out_dope);
        
//        double max = -1;
//        double maxMinus = -1;
//        double maxPlus = -1;
//        int index = -1;
        for(int i=0; i<size; i++) {
            avg[i] = avg[i] + out_dope.magAt(i);
            
//            double amp = out_dope.magAt(i);
//            if(max < amp) {
//                max = amp;
//                
//                maxPlus = out_dope.magAt(i+1);
//                maxMinus = out_dope.magAt(i-1);
//                
//                index = i;
//            }
        }
        
//        double d = (maxPlus - maxMinus) / (maxMinus + max + maxPlus);
//        
//        double freq = (44100 ) * (index/(double)size);
//        double freq2 = (44100 ) * ((index + d)/(double)size);
//        //Log.i("MAJS", "Found highest frequency at index " + index + "[" + freq + "][" + freq2 + "] at value " + max);
//        receiver.hertz((int) freq2);
        
        //System.gc();
    }

    /**
     * Find the strongest frequency
     * @param fftMagnitude Array of magnitude of the FFT
     * @param result
     */
    private void findFreq(double[] fftMagnitude, FFTResult result, int sampleRate)
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
        
        double freq = (sampleRate / 2) * (index/(double)fftMagnitude.length);
        double freq2 = (sampleRate / 2) * ((index + d)/(double)fftMagnitude.length);

        Log.i("MAJS", "Found highest frequency at index " + index + "[" + freq + "][" + freq2 + "] at value " + max);
        //receiver.hertz((int) freq2);

        result.frequency = freq2;
        result.frequency_raw = freq;
    }


}
