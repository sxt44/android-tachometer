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

package org.teneighty.fft.dope;


/**
 * This class represents a dope vector, which encapsulates the length, stride,
 * etc. of an array.
 * <p>
 * This interface is mainly useful for doing array slicing and such, which is
 * difficult to do cheaply/naturally with Java's built in arrays.
 * <p>
 * ComplexDopeVector supports only double-precision backed complex vectors.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public interface ComplexDopeVector
	extends DopeVector, RealDopeVector
{


	/**
	 * Get the imaginary part of the specified index.
	 * 
	 * @param index the index.
	 * @return double the imaginary part.
	 * @throws ArrayIndexOutOfBoundsException If <code>index</code> is out of
	 *         bounds.
	 */
	public double getImaginary( int index )
		throws ArrayIndexOutOfBoundsException;


	/**
	 * Set the imaginary part of the specified number.
	 * 
	 * @param value the value.
	 * @param index the index.
	 * @throws ArrayIndexOutOfBoundsException If <code>index</code> is out of
	 *         bounds.
	 */
	public void setImaginary( double value, int index )
		throws ArrayIndexOutOfBoundsException;
	

}
