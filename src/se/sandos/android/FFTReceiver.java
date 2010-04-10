package se.sandos.android;

import org.teneighty.fft.FourierTransform;
import org.teneighty.fft.FourierTransformFactory;
import org.teneighty.fft.dope.BackedRealDopeVector;
import org.teneighty.fft.dope.RealDopeVector;

import android.util.Log;

public class FFTReceiver implements AudioCallback {

    private static final int FFT_SIZE = 1024;
    private static final int HOP = 256;
    private HertzReceiver receiver;
    private FFTView fft;
    private double[] avg;
    
    public FFTReceiver(HertzReceiver recv) {
        receiver = recv;
    }

    @Override
    public void receiveAudio(short[] audio, int numSamples, int sampleRate) {
        //Log.v("MAJS", "" + numSamples);
        int offset = 0;
        avg = new double[FFT_SIZE/2];
        while(offset + FFT_SIZE < numSamples) {
            doFFT(audio, offset, sampleRate);
            offset += HOP;
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
        RealDopeVector in_dope = new BackedRealDopeVector(data);
        MyDope out_dope = new MyDope(size);

        //long time = System.currentTimeMillis();
        transform.forward(in_dope, out_dope);
        //Log.i("MAJS", "Size: " + size + " took " + (System.currentTimeMillis() - time));

        fft.newFFT(out_dope);
        
        for(int i=0; i<size/2 && i<avg.length; i++) {
            avg[i] = avg[i] + out_dope.magAt(i);
        }
    }

    /**
     * Find the strongest frequency. Uses some simple interpolation for now.
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

        result.frequency = freq2;
        result.frequency_raw = freq;
    }


}
