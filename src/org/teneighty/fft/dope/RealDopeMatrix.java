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
 * The base real matrix interface.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public interface RealDopeMatrix
	extends DopeMatrix
{


	/**
	 * Get the real part at the specified index. (The index here is really multi-dimensional.)
	 *
	 * @param offsets the offsets.
	 * @return double the value.
	 * @throws IllegalArgumentException If <code>offsets</code> are illegal.
	 * @throws NullPointerException If <code>offsets</code> are <code>null</code>.
	 */
	public double getReal( int... offsets )
		throws IllegalArgumentException, NullPointerException;

	
	/**
	 * Set the real part of the specified number.
	 *
	 * @param offsets the offsets.
	 * @param value the new value.
	 * @throws IllegalArgumentException If <code>offsets</code> are illegal.
	 * @throws NullPointerException If <code>offsets</code> are <code>null</code>.
	 */
	public void setReal( double value, int... offsets )
		throws IllegalArgumentException, NullPointerException;


}
