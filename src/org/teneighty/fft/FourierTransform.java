/*
 * $Id$
 * 
 * Copyright (c) 2005, 2006 Fran Lattanzio
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

package org.teneighty.fft;

import org.teneighty.fft.dope.ComplexDopeVector;
import org.teneighty.fft.dope.RealDopeVector;


/**
 * Instances of this class are capable of performing the Discrete Fourier
 * Transform (DFT). Strictly speaking, I suppose I should call this class
 * "FourierTransformer", since it does not represent a Fourier transform,
 * <i>per se</i>.
 * <p>
 * Note that, in general, instances of this class are not safe for concurrent
 * use by multiple threads.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public interface FourierTransform
{


	/**
	 * Get the size this transform is capable of handling.
	 * 
	 * @return int the size.
	 */
	public int getLength();


	/**
	 * Perform the forward Fourier transform on the specified vector.
	 * 
	 * @param input the vector to transform.
	 * @param dest the destination.
	 * @throws NullPointerException If <code>input</code> or <code>dest</code>
	 *         is <code>null</code>.
	 * @throws IllegalArgumentException If <code>input</code> does not have the
	 *         right length</code>, or if <code>input</code> == <code>dest</code>.
	 */
	public void forward( ComplexDopeVector input, ComplexDopeVector dest )
		throws NullPointerException, IllegalArgumentException;


	/**
	 * Perform the backward Fourier transform on the specified vector.
	 * 
	 * @param input the vector to transform.
	 * @param dest the destination.
	 * @throws NullPointerException If <code>input</code> or <code>dest</code>
	 *         is <code>null</code>.
	 * @throws IllegalArgumentException If <code>input</code> does not have the
	 *         right length</code>, or if <code>input</code> == <code>dest</code>.
	 */
	public void backward( ComplexDopeVector input, ComplexDopeVector dest )
		throws NullPointerException, IllegalArgumentException;
	
	
	/**
	 * Transform complex data in the specified direction.
	 * 
	 * @param input the vector to transform.
	 * @param dest the destination.
	 * @param direction the direction.
	 * @throws NullPointerException If <code>input</code> or <code>dest</code>
	 *         is <code>null</code>.
	 * @throws IllegalArgumentException If <code>input</code> does not have the
	 *         right length</code>, or if <code>input</code> == <code>dest</code>.
	 */
	public void transform( ComplexDopeVector input, ComplexDopeVector dest, TransformDirection direction )
		throws NullPointerException, IllegalArgumentException;

	
	/**
	 * Perform the forward Fourier transform on the specified vector.
	 * 
	 * @param input the vector to transform.
	 * @param dest the destination.
	 * @throws NullPointerException If <code>input</code> or <code>dest</code>
	 *         is <code>null</code>.
	 * @throws IllegalArgumentException If <code>input</code> does not have the
	 *         right length</code>, or if <code>input</code> == <code>dest</code>.
	 */
	public void forward( RealDopeVector input, ComplexDopeVector dest )
		throws NullPointerException, IllegalArgumentException;


	/**
	 * Perform the backward Fourier transform on the specified vector.
	 * 
	 * @param input the vector to transform.
	 * @param dest the destination.
	 * @throws NullPointerException If <code>input</code> or <code>dest</code>
	 *         is <code>null</code>.
	 * @throws IllegalArgumentException If <code>input</code> does not have the
	 *         right length</code>, or if <code>input</code> == <code>dest</code>.
	 */
	public void backward( RealDopeVector input, ComplexDopeVector dest )
		throws NullPointerException, IllegalArgumentException;
	
	
	/**
	 * Transform real data in the specified direction.
	 * 
	 * @param input the vector to transform.
	 * @param dest the destination.
	 * @param direction the direction.
	 * @throws NullPointerException If <code>input</code> or <code>dest</code>
	 *         is <code>null</code>.
	 * @throws IllegalArgumentException If <code>input</code> does not have the
	 *         right length</code>, or if <code>input</code> == <code>dest</code>.
	 */
	public void transform( RealDopeVector input, ComplexDopeVector dest, TransformDirection direction )
		throws NullPointerException, IllegalArgumentException;

	
	/**
	 * A reminder to override equals.
	 * 
	 * @param other some other happy object.
	 * @return boolean <code>true</code> if equal.
	 */
	public boolean equals( Object other );


	/**
	 * Get a hashcode inline with equals.
	 * 
	 * @return int hashcode.
	 */
	public int hashCode();


}
