package se.sandos.android;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

public class Recorder implements Runnable {
    private static final int SAMPLERATE = 8000;
    //private static final int SAMPLERATE = 44100;
    private int frequency;
    private int channelConfiguration;
    private volatile boolean isPaused;
    private volatile boolean isRecording;
    private final Object mutex = new Object();
    private final static boolean writeFile = false;

    private AudioCallback callback;

    private Context context;

    // Changing the sample resolution changes sample type. byte vs. short.
    private static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

    /** 
      * 
      */
    public Recorder(AudioCallback callback, Context ctx) {
        super();
        this.setFrequency(SAMPLERATE);
        this.setChannelConfiguration(AudioFormat.CHANNEL_CONFIGURATION_MONO);
        this.setPaused(false);
        this.callback = callback;
        context = ctx;
    }

    public void run() {
        // Wait until we're recording...
        synchronized (mutex) {
            while (!this.isRecording) {
                try {
                    mutex.wait();
                } catch (InterruptedException e) {
                    throw new IllegalStateException("Wait() interrupted!", e);
                }
            }
        }

         android.os.Process
         .setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        // Allocate Recorder and Start Recording...
        int bufferRead = 0;
        int bufferSize = AudioRecord.getMinBufferSize(this.getFrequency(), this.getChannelConfiguration(),
                this.getAudioEncoding())*8;
        Log.v("MAJS", "Buffersize is " + bufferSize);
        AudioRecord recordInstance = new AudioRecord(MediaRecorder.AudioSource.MIC, this.getFrequency(), this
                .getChannelConfiguration(), this.getAudioEncoding(), bufferSize);
        short[] tempBuffer = new short[bufferSize];
        recordInstance.startRecording();
        try {
            while (this.isRecording) {
                // Are we paused?
                synchronized (mutex) {
                    if (this.isPaused) {
                        try {
                            mutex.wait(250);
                        } catch (InterruptedException e) {
                            throw new IllegalStateException("Wait() interrupted!", e);
                        }
                        continue;
                    }
                }

                bufferRead = recordInstance.read(tempBuffer, 0, bufferSize);
                // Log.i("", "Got audio " + bufferSize);
                if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
                    throw new IllegalStateException("read() returned AudioRecord.ERROR_INVALID_OPERATION");
                } else if (bufferRead == AudioRecord.ERROR_BAD_VALUE) {
                    throw new IllegalStateException("read() returned AudioRecord.ERROR_BAD_VALUE");
                } else if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
                    throw new IllegalStateException("read() returned AudioRecord.ERROR_INVALID_OPERATION");
                }

                if(writeFile) {
                    try {
                        FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + File.separator + "rawAudio", true);
                        byte[] data = new byte[bufferRead*2];
                        for(int i=0; i<bufferRead; i++) {
                            data[i*2] = (byte) (tempBuffer[i] & 0x00ff);
                            data[i*2+1] = (byte) ((tempBuffer[i] & 0xff00) >> 8);
                        }
                        fos.write(data);
                        Log.v("MAJS", "Wrote " + bufferRead + " bytes");
                    } catch (Exception e) {
                        Log.v("MAJS", "Error: " + e);
                    }
                }
                
                callback.receiveAudio(tempBuffer, bufferRead, this.getFrequency());

            }
        } finally {
            // Close resources...
            recordInstance.stop();
            recordInstance.release();
            Log.v("MAJS", "Cleaning up recorder etc");
        }
    }

    /**
     * @param isRecording
     *            the isRecording to set
     */
    public void setRecording(boolean isRecording) {
        synchronized (mutex) {
            this.isRecording = isRecording;
            if (this.isRecording) {
                mutex.notify();
            }
        }
    }

    /**
     * @return the isRecording
     */
    public boolean isRecording() {
        synchronized (mutex) {
            return isRecording;
        }
    }

    /**
     * @param frequency
     *            the frequency to set
     */
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    /**
     * @return the frequency
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * @param channelConfiguration
     *            the channelConfiguration to set
     */
    public void setChannelConfiguration(int channelConfiguration) {
        this.channelConfiguration = channelConfiguration;
    }

    /**
     * @return the channelConfiguration
     */
    public int getChannelConfiguration() {
        return channelConfiguration;
    }

    /**
     * @return the audioEncoding
     */
    public int getAudioEncoding() {
        return audioEncoding;
    }

    /**
     * @param isPaused
     *            the isPaused to set
     */
    public void setPaused(boolean isPaused) {
        synchronized (mutex) {
            this.isPaused = isPaused;
        }
    }

    /**
     * @return the isPaused
     */
    public boolean isPaused() {
        synchronized (mutex) {
            return isPaused;
        }
    }

}