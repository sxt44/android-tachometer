/*
 * $Id$
 *
 * Copyright (c) 2006 Fran Lattanzio
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.teneighty.fft.algorithms;

import java.io.Serializable;

import org.teneighty.fft.dope.ComplexDopeVector;
import org.teneighty.fft.dope.RealDopeVector;


/**
 * Column-major complex dope vector.
 * <p>
 * This is a package-level class.
 *
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
class ColumnMajorComplexDopeVector
	extends ColumnMajorRealDopeVector
	implements RealDopeVector, ComplexDopeVector, Serializable
{

	
	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8347243L;
	
	
	/**
	 * Imaginary part.
	 */
	private double[][] im;


	/**
	 * Constructor.
	 * 
	 * @param re the real part.
	 * @param im the imaginary part.
	 */
	ColumnMajorComplexDopeVector( final double[][] re, final double[][] im )
	{
		super( re );

		// store stuff.
		this.im = im;
	}


	/**
	 * Get the imaginary part of the specified number.
	 * 
	 * @param index the index.
	 * @return double the imaginary part.
	 * @throws ArrayIndexOutOfBoundsException If <code>index</code> is col of
	 *         bounds.
	 */
	public double getImaginary( final int index )
		throws ArrayIndexOutOfBoundsException
	{
		return ( this.im[ this.row ][ index ] );
	}


	/**
	 * Set the imaginary part of the specified number.
	 * @param value the value.
	 * @param index the index.
	 * 
	 * @throws ArrayIndexOutOfBoundsException If <code>index</code> is col of
	 *         bounds.
	 */
	public void setImaginary( double value, int index )
		throws ArrayIndexOutOfBoundsException
	{
		this.im[ this.row ][ index ] = value;
	}
	

}
