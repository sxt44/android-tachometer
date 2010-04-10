package se.sandos.android;

import org.teneighty.fft.dope.DefaultComplexDopeVector;

public class MyDope extends DefaultComplexDopeVector {
    private static final long serialVersionUID = 1L;
    
    public MyDope(int size) throws IllegalArgumentException {
        super(size);
    }

    /**
     * Return the magnitude at index i
     * @param out_dope
     * @param i
     * @return
     */
    public double magAt(int i) {
        if(i < 0 || i >= getLength()) {
            return 0;
        }
        double imag = getImaginary(i);
        double real = getReal(i);
        double amp = Math.sqrt(imag*imag + real*real);
        return amp;
    }
}
