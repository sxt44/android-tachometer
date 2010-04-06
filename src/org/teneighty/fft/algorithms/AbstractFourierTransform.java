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

package org.teneighty.fft.algorithms;

import java.io.Serializable;

import org.teneighty.fft.FourierTransform;
import org.teneighty.fft.TransformDirection;
import org.teneighty.fft.dope.ComplexDopeVector;
import org.teneighty.fft.dope.DopeVector;
import org.teneighty.fft.dope.RealDopeVector;


/**
 * Abstract class from which all Fourier Transform implementation can extend.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public abstract class AbstractFourierTransform
	extends Object
	implements FourierTransform, Serializable
{


	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 2823423L;


	/**
	 * Tranform size.
	 */
	private int trans_size;


	/**
	 * Constructor.
	 * <p>
	 * Should be considered <code>private protected</code>.
	 * 
	 * @param size the size.
	 * @throws IllegalArgumentException If <code>size</code> is less than 1.
	 */
	protected AbstractFourierTransform( final int size )
		throws IllegalArgumentException
	{
		super();

		if( size < 1 )
		{
			throw new IllegalArgumentException();
		}

		// Store size.
		this.trans_size = size;
	}


	/**
	 * Check the specified vectors, to make sure they're valid input and
	 * destination vectors.
	 * 
	 * @param input the input vector.
	 * @param dest the destination vector.
	 * @throws NullPointerException If <code>input</code> or <code>dest</code>
	 *         is <code>null</code>.
	 * @throws IllegalArgumentException If <code>input</code> does not have the
	 *         right length</code>, or if <code>input</code> == <code>dest</code>.
	 */
	protected final void checkVectors( final DopeVector input,
			final DopeVector dest )
		throws NullPointerException, IllegalArgumentException
	{
		if( input == null || dest == null )
		{
			throw new NullPointerException();
		}

		if( input == dest )
		{
			throw new IllegalArgumentException();
		}

		if( input.getLength() != this.getLength() || dest.getLength() != this.getLength() )
		{
			throw new IllegalArgumentException();
		}
	}
	
	
	/**
	 * Check direction.
	 * 
	 * @param td the direction to check.
	 * @throws NullPointerException If <code>td</code> if <code>null</code>.
	 * @throws InternalError If some unknown value finds its way here.
	 */
	protected final void checkDirection( final TransformDirection td )
		throws NullPointerException, InternalError
	{
		if( td == null )
		{
			throw new NullPointerException();
		}
		
		if( td != TransformDirection.FORWARD && td != TransformDirection.BACKWARD )
		{
			throw new InternalError();
		}		
	}


	/**
	 * Get the size of this transform.
	 * 
	 * @return int the size.
	 */
	public int getLength()
	{
		return ( this.trans_size );
	}
	
	
	/**
	 * Transform the specified vectors.
	 * 
	 * @param input the vector to transform.
	 * @param dest the destination.
	 * @throws NullPointerException If <code>input</code> or <code>dest</code>
	 *         is <code>null</code>.
	 * @throws IllegalArgumentException If <code>input</code> does not have the
	 *         right length</code>, or if <code>input</code> == <code>dest</code>.
	 */
	public void forward( final ComplexDopeVector input,
											final ComplexDopeVector dest )
		throws NullPointerException, IllegalArgumentException
	{
		this.transform( input, dest, TransformDirection.FORWARD );
	}


	/**
	 * Perform the backward Fourier transform, on the specified vector.
	 * 
	 * @param input the vector to transform.
 	 * @param dest the destination vector.
	 * @throws NullPointerException If <code>input</code> or <code>dest</code>
	 *         is <code>null</code>.
	 * @throws IllegalArgumentException If <code>input</code> does not have the
	 *         right length</code>, or if <code>input</code> == <code>dest</code>.
	 */
	public void backward( final ComplexDopeVector input,
												final ComplexDopeVector dest )
		throws NullPointerException, IllegalArgumentException
	{
		this.transform( input, dest, TransformDirection.BACKWARD );
	}
	
	
	/**
	 * Transform the specified vectors.
	 * 
	 * @param input the vector to transform.
	 * @param dest the destination.
	 * @throws NullPointerException If <code>input</code> or <code>dest</code>
	 *         is <code>null</code>.
	 * @throws IllegalArgumentException If <code>input</code> does not have the
	 *         right length</code>, or if <code>input</code> == <code>dest</code>.
	 */
	public void forward( final RealDopeVector input,
											final ComplexDopeVector dest )
		throws NullPointerException, IllegalArgumentException
	{
		this.transform( input, dest, TransformDirection.FORWARD );
	}


	/**
	 * Perform the backward Fourier transform, on the specified vector.
	 * 
	 * @param input the vector to transform.
 	 * @param dest the destination vector.
	 * @throws NullPointerException If <code>input</code> or <code>dest</code>
	 *         is <code>null</code>.
	 * @throws IllegalArgumentException If <code>input</code> does not have the
	 *         right length</code>, or if <code>input</code> == <code>dest</code>.
	 */
	public void backward( final RealDopeVector input,
												final ComplexDopeVector dest )
		throws NullPointerException, IllegalArgumentException
	{
		this.transform( input, dest, TransformDirection.BACKWARD );
	}


	/**
	 * A half-way decent implementation of equals.
	 * <p>
	 * Two transforms are considered equal if they are of the same class and
	 * handle the same size input.
	 * 
	 * @param other some other object.
	 * @return boolean <code>true</code> if equal.
	 */
	@Override
	public boolean equals( final Object other )
	{
		if( other == null )
		{
			return ( false );
		}

		if( other == this )
		{
			return ( true );
		}

		if( this.getClass().equals( other.getClass() ) == true )
		{
			FourierTransform that = (FourierTransform)other;
			return ( that.getLength() == this.getLength() );
		}

		return ( false );
	}


	/**
	 * Get the hashcode for this transform.
	 * 
	 * @return int the hashcode.
	 */
	@Override
	public int hashCode()
	{
		return ( this.trans_size );
	}


	/**
	 * Slightly lamer to string.
	 * 
	 * @return String a string.
	 */
	@Override
	public String toString()
	{
		return ( this.getClass().getName() );
	}


}
