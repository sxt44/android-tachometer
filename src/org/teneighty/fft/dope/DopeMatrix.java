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
 * The base interface from which all dope arrays shall extend.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public interface DopeMatrix
{


	/**
	 * Get the dimension of this matrix.
	 * 
	 * @return int the dimension.
	 */
	public int getDimension();


	/**
	 * Get the length along the specified dimension.
	 * 
	 * @param dim the dimension.
	 * @return int the length along the specified dimension.
	 * @throws IllegalArgumentException If <code>dim</code> is negative or
	 *         greater than the dimension of the matrix.
	 */
	public int getLength( int dim )
		throws IllegalArgumentException;


}
