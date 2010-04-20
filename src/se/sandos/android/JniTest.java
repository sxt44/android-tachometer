package se.sandos.android;

public class JniTest {

    static {
        System.loadLibrary("fft_fix");
    }

    /**
     * @param width the current view width
     * @param height the current view height
     */
     public static native int fix_fft(short fr[], short m, short inverse);
}
