package se.sandos.android;

public interface AudioCallback {
    public void receiveAudio(short[] audio, int numSamples);
}
