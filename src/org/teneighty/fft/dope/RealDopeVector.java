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
 * A real dope vector.
 * <p>
 * Real dopes support only double precision.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public interface RealDopeVector
	extends DopeVector
{
	
	
	/**
	 * Get the index<sup>th</sup> element.
	 * 
	 * @param index the index to get.
	 * @return double the index<sup>th</sup> element.
	 * @throws ArrayIndexOutOfBoundsException If <code>index</code> is out of bounds.
	 */
	public double getReal( int index )
		throws ArrayIndexOutOfBoundsException;
	
	
	/**
	 * Set the index<sup>th</sup> element.
	 * 
	 * @param value the value.
	 * @param index the index.
	 * @throws ArrayIndexOutOfBoundsException If <code>index</code> is out of
	 *         bounds.
	 */
	public void setReal( double value, int index )
		throws ArrayIndexOutOfBoundsException;
	

}
