package se.sandos.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class FFTView extends View {

    private Paint white, red, green, lightgrey, darkgrey, blackhole, green2;
    private long[] vals;
    private int correctBin, correctBin2;
    private int samplerate;
    
    int w;
    int h;
    private int bins;
    
    public FFTView(Context context) {
        super(context);
        
        white = new Paint();
        white.setAntiAlias(false);
        white.setColor(0xffffffff);

        lightgrey = new Paint();
        lightgrey.setAntiAlias(false);
        lightgrey.setColor(0x77ffffff);

        darkgrey = new Paint();
        darkgrey.setAntiAlias(false);
        darkgrey.setColor(0x33ffffff);

        blackhole = new Paint();
        blackhole.setAntiAlias(false);
        blackhole.setColor(0x11ffffff);

        
        red = new Paint();
        red.setAntiAlias(false);
        red.setColor(0xffff0000);

        green = new Paint();
        green.setAntiAlias(false);
        green.setColor(0xff00ff00);

        green2 = new Paint();
        green2.setAntiAlias(false);
        green2.setColor(0xff55ff55);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        
        double max = -1;
        if(vals != null) {
            boolean marker = false;
            double oldThousand = -1;
            double fivehundred = -1;
            double hundred = -1;
            for (int i = 0; i < vals.length; i++) {
                max = Math.max(vals[i], max);
            }
            //Log.v("MAJS", "MAX: " + max);
            max = Math.max(100000, max);
            for (int i = 0; i < w && i < vals.length; i++) {
                int binNumber = i/2;
                double ratio = vals[binNumber] / max;
                double ratio2 = vals[binNumber+1] / max;
                ratio = Math.max(ratio, ratio2);
                Paint p = white;
                if(binNumber == correctBin || binNumber + 1 == correctBin) {
                    p = green;
                }

                if(binNumber == correctBin2 || binNumber + 1 == correctBin2) {
                    p = green2;
                }
                
                int hh =  (int)((binNumber*samplerate/(double)bins)/100.0);
                if(hundred != -1) {
                    if(hundred != hh) {
                        canvas.drawLine(i, h, i, 0, blackhole);
                    }
                    
                }
                hundred = hh;
                
                int fh =  (int)((binNumber*samplerate/(double)bins)/500.0);
                if(fivehundred != -1) {
                    if(fivehundred != fh) {
                        canvas.drawLine(i, h, i, 0, darkgrey);
                    }
                    
                }
                fivehundred = fh;

                int thousand = (int)((binNumber*samplerate/(double)bins)/1000.0);
                if(oldThousand != -1) {
                    if(oldThousand != thousand) {
                        canvas.drawLine(i, h, i, 0, lightgrey);
                    }
                }
                oldThousand = thousand;
                
                canvas.drawLine(i, h, i, (float) (h - (ratio * h)), p);
            }
        }
    }
    
    public void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        this.w = w;
        this.h = h;
    }
    
    public void newFFT(long[] vals, FFTResult result, int samplerate, int bins)
    {
        this.bins = bins;
        this.samplerate = samplerate;
        this.vals = vals;
        correctBin = result.bin;
        correctBin2 = result.bin2;
    }
    
}
