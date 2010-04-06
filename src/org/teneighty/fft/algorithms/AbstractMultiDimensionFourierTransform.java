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

import org.teneighty.fft.MultiDimensionFourierTransform;
import org.teneighty.fft.TransformDirection;
import org.teneighty.fft.dope.ComplexDopeMatrix;
import org.teneighty.fft.dope.DopeMatrix;
import org.teneighty.fft.dope.RealDopeMatrix;


/**
 * Abstract multi-dimenson transform.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public abstract class AbstractMultiDimensionFourierTransform
	extends Object
	implements MultiDimensionFourierTransform, Serializable
{


	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 238243L;


	/**
	 * Lengths.
	 */
	protected final int[] lengths;

	/**
	 * Dimension.
	 */
	protected final int dimension;


	/**
	 * Constructor.
	 * <p>
	 * Should be considered <code>private protected</code>.
	 * 
	 * @param lens the lengths
	 * @throws IllegalArgumentException If <code>dim</code> or <code>lens</code>
	 *         are goofy.
	 */
	protected AbstractMultiDimensionFourierTransform( final int... lens )
		throws IllegalArgumentException
	{
		super();

		if( lens.length < 1 )
		{
			throw new IllegalArgumentException();
		}

		// store stuff.
		this.dimension = lens.length;
		this.lengths = new int[ lens.length ];
		for( int index = 0; index < lens.length; index++ )
		{
			if( lens[ index ] < 1 )
			{
				throw new IllegalArgumentException();
			}

			// store lengths...
			this.lengths[ index ] = lens[ index ];
		}
	}


	/**
	 * Check the specified list of matrices.
	 * <p>
	 * Should be considered <code>private protected</code>.
	 * 
	 * @param in input matrix.
	 * @param out output matrix.
	 * @throws IllegalArgumentException If any of <code>matrices</code> is
	 *         illegal.
	 * @throws NullPointerException If any of <code>matrices</code> is
	 *         <code>null</code>.
	 */
	protected final void checkMatrices( final DopeMatrix in, final DopeMatrix out )
		throws IllegalArgumentException, NullPointerException
	{
		if( in == null || out == null )
		{
			throw new NullPointerException();
		}

		this.checkMatrix( in );
		this.checkMatrix( out );
	}


	/**
	 * Check the specified matrix.
	 * <p>
	 * Should be considered <code>private protected</code>.
	 * 
	 * @param matrix the matrix to check.
	 * @throws IllegalArgumentException If <code>matrix</code> is illegal.
	 * @throws NullPointerException If <code>matrix</code> is <code>null</code>.
	 */
	private final void checkMatrix( final DopeMatrix matrix )
		throws IllegalArgumentException, NullPointerException
	{
		if( matrix == null )
		{
			throw new NullPointerException();
		}

		if( matrix.getDimension() != this.getDimension() )
		{
			throw new IllegalArgumentException();
		}

		for( int index = 0; index < this.getDimension(); index++ )
		{
			if( this.getLength( index ) != matrix.getLength( index ) )
			{
				throw new IllegalArgumentException();
			}
		}

		// it's ok... do nothing.
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
	public void forward( final ComplexDopeMatrix input,
			final ComplexDopeMatrix dest )
		throws NullPointerException, IllegalArgumentException
	{
		this.transform( input, dest, TransformDirection.FORWARD );
	}


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
	public void backward( final ComplexDopeMatrix input,
			final ComplexDopeMatrix dest )
		throws NullPointerException, IllegalArgumentException
	{
		this.transform( input, dest, TransformDirection.BACKWARD );
	}


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
	public void forward( final RealDopeMatrix input, final ComplexDopeMatrix dest )
		throws NullPointerException, IllegalArgumentException
	{
		this.transform( input, dest, TransformDirection.FORWARD );
	}


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
	public void backward( final RealDopeMatrix input, final ComplexDopeMatrix dest )
		throws NullPointerException, IllegalArgumentException
	{
		this.transform( input, dest, TransformDirection.BACKWARD );
	}


	/**
	 * Get the dimension.
	 * 
	 * @return int the dimension.
	 */
	public int getDimension()
	{
		return ( this.dimension );
	}


	/**
	 * Get the length along the specified dimension.
	 * 
	 * @param dim the dimension.
	 * @return int the length along the specified dimension.
	 * @throws IllegalArgumentException If <code>dim</code> is negative or
	 *         greater than the dimension.
	 */
	public int getLength( final int dim )
		throws IllegalArgumentException
	{
		if( dim < 0 || dim > this.dimension )
		{
			throw new IllegalArgumentException();
		}

		return ( this.lengths[ dim ] );
	}


	/**
	 * A better implementation of equals.
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
			MultiDimensionFourierTransform that = (MultiDimensionFourierTransform)other;
			if( that.getDimension() != this.getDimension() )
			{
				return ( false );
			}

			for( int index = 0; index < this.getDimension(); index++ )
			{
				if( that.getLength( index ) != this.getLength( index ) )
				{
					return ( false );
				}
			}

			return ( true );
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
		return ( this.dimension );
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
