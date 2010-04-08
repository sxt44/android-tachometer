package se.sandos.android;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;


public class Recorder implements Runnable { 
     private int frequency; 
     private int channelConfiguration; 
     private volatile boolean isPaused; 
     private volatile boolean isRecording; 
     private final Object mutex = new Object(); 
     
     private AudioCallback callback;

     // Changing the sample resolution changes sample type. byte vs. short. 
     private static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT; 

     /** 
      * 
      */ 
     public Recorder(AudioCallback callback) { 
          super(); 
          this.setFrequency(44100); 
          this.setChannelConfiguration(AudioFormat.CHANNEL_CONFIGURATION_MONO); 
          this.setPaused(false);
          this.callback = callback;
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

          
          // We're important... 
//          android.os.Process 
//                    .setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO); 

          // Allocate Recorder and Start Recording... 
          int bufferRead = 0; 
          int bufferSize = AudioRecord.getMinBufferSize(this.getFrequency(), 
                    this.getChannelConfiguration(), this.getAudioEncoding()); 
          AudioRecord recordInstance = new AudioRecord( 
                    MediaRecorder.AudioSource.MIC, this.getFrequency(), this 
                              .getChannelConfiguration(), this.getAudioEncoding(), 
                    bufferSize); 
          short[] tempBuffer = new short[bufferSize]; 
          recordInstance.startRecording(); 
          while (this.isRecording) { 
               // Are we paused? 
               synchronized (mutex) { 
                    if (this.isPaused) { 
                         try { 
                              mutex.wait(250); 
                         } catch (InterruptedException e) { 
                              throw new IllegalStateException("Wait() interrupted!", 
                                        e); 
                         } 
                         continue; 
                    } 
               } 
                
               bufferRead = recordInstance.read(tempBuffer, 0, bufferSize);
               //Log.i("", "Got audio " + bufferSize);
               if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) { 
                    throw new IllegalStateException( 
                              "read() returned AudioRecord.ERROR_INVALID_OPERATION"); 
               } else if (bufferRead == AudioRecord.ERROR_BAD_VALUE) { 
                    throw new IllegalStateException( 
                              "read() returned AudioRecord.ERROR_BAD_VALUE"); 
               } else if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) { 
                    throw new IllegalStateException( 
                              "read() returned AudioRecord.ERROR_INVALID_OPERATION"); 
               }
               
               callback.receiveAudio(tempBuffer);
                
          } 

          // Close resources... 
          recordInstance.stop(); 
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