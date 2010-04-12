package se.sandos.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

public class FFTView extends View {

    private Paint white;
    private Paint red;
    private Paint green;
    private double[] vals;
    private int correctBin;
    
    int w;
    int h;
    
    public FFTView(Context context) {
        super(context);
        
        white = new Paint();
        white.setAntiAlias(false);
        white.setColor(0xffffffff);

        red = new Paint();
        red.setAntiAlias(false);
        red.setColor(0xffff0000);

        green = new Paint();
        green.setAntiAlias(false);
        green.setColor(0xff00ff00);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        
        double max = -1;
        if(vals != null) {
            for (int i = 0; i < vals.length; i++) {
                max = Math.max(vals[i], max);
            }
            Log.v("MAJS", "MAX: " + max);
            max = Math.max(1.0, max);
            for (int i = 0; i < w && i < vals.length; i++) {
                double ratio = vals[i] / max;
                Paint p = white;
                if(i == correctBin) {
                    p = green;
                }
                if(i == 46) {
                    canvas.drawLine(i, h, i, 0, red);
                }
                canvas.drawLine(i, h, i, (float) (h - (ratio * h)), p);
            }
        }
    }
    
    public void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        this.w = w;
        this.h = h;
    }
    
    public void newFFT(double[] vals, int index)
    {
        this.vals = vals;
        correctBin = index;
    }
    
}
