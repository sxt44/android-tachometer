package se.sandos.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

public class FFTView extends View {

    private Paint black;
    private MyDope vals;
    
    int w;
    int h;
    
    public FFTView(Context context) {
        super(context);
        
        black = new Paint();
        black.setAntiAlias(false);
        black.setColor(0xffffffff);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        
        if(vals != null) {
            double[] mag = new double[vals.getLength()/2];
            double max = -1;
            for(int i=0; i<mag.length; i++) {
                mag[i] = vals.magAt(i);
                max = Math.max(mag[i], max);
            }
            Log.v("MAJS", "MAX: " + max);
            max = Math.max(1.0, max);
            for(int i=0; i<w && i <mag.length; i++) {
                double ratio = mag[i] / max;
                canvas.drawLine(i, h, i, (float) (h-(ratio*h)), black);
            }
        }
    }
    
    public void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        this.w = w;
        this.h = h;
    }
    
    public void newFFT(MyDope vals)
    {
        this.vals = vals;
    }
    
}
