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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


/**
 * Default real dope matrix.
 *
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public class DefaultRealDopeMatrix
	extends AbstractDopeMatrix
	implements DopeMatrix, RealDopeMatrix, Serializable, Cloneable
{
	
	
	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 28237L;

	
	/**
	 * The total length of the backing array.
	 */
	protected transient int total;

	/**
	 * The dimension.
	 */
	private transient int dim;
	
	/**
	 * Lengths.
	 */
	private int[] lengths;

	/**
	 * The backing array.
	 */
	private double[] re_data;
	

	/**
	 * Constructor.
	 * 
	 * @param lens the lengths along each dimension.
	 * @throws IllegalArgumentException If <code>dim</code> or any
	 *         <code>lens</code> are negative.
	 */
	public DefaultRealDopeMatrix( final int... lens )
		throws IllegalArgumentException
	{
		super();

		if( lens.length < 1 )
		{
			throw new IllegalArgumentException();
		}

		// store stuff.
		this.dim = lens.length;
		this.lengths = new int[ lens.length ];
		this.total = 1;
		for( int index = 0; index < lens.length; index++ )
		{
			if( lens[ index ] < 1 )
			{
				throw new IllegalArgumentException();
			}

			this.lengths[ index ] = lens[ index ];
			this.total *= lens[ index ];
		}

		// alloc backing array. stored in row-major order.
		this.re_data = new double[ this.total ];
	}


	/**
	 * Get the dimension of this matrix.
	 * 
	 * @return int the dimension.
	 */
	public int getDimension()
	{
		return ( this.dim );
	}


	/**
	 * Get the length along the specified dimension.
	 * 
	 * @param d the dimension.
	 * @return int the length along the specified dimension.
	 * @throws IllegalArgumentException If <code>dim</code> is negative or
	 *         greater than the dimension of the matrix.
	 */
	public int getLength( final int d )
		throws IllegalArgumentException
	{
		if( d < 0 || d > this.dim )
		{
			throw new IllegalArgumentException();
		}
		
		return( this.lengths[ d ] );
	}
	
	
	/**
	 * Get the real part at the specified index. (The index here is really multi-dimensional.)
	 *
	 * @param offsets the offsets.
	 * @return double the value.
	 * @throws IllegalArgumentException If <code>offsets</code> are illegal.
	 * @throws NullPointerException If <code>offsets</code> are <code>null</code>.
	 */
	public double getReal( final int... offsets )
		throws IllegalArgumentException, NullPointerException
	{
		return( this.re_data[ this.getIndex( offsets ) ] );
	}
		
	
	/**
	 * Set the real part of the specified number.
	 *
	 * @param offsets the offsets.
	 * @param value the new value.
	 * @throws IllegalArgumentException If <code>offsets</code> are illegal.
	 * @throws NullPointerException If <code>offsets</code> are <code>null</code>.
	 */
	public void setReal( final double value, final int... offsets )
		throws IllegalArgumentException, NullPointerException
	{
		this.re_data[ this.getIndex( offsets ) ] = value;
	}
		
	
	/**
	 * Get the row-major index of the specified offsets.
	 * 
	 * @param offsets the stuff to check.
	 * @return int the index into the array.
	 * @throws IllegalArgumentException If <code>lens</code> is illegal.
	 * @throws NullPointerException If <code>lens</code> is <code>null</code>.
	 */
	protected int getIndex( final int[] offsets )
		throws NullPointerException, IllegalArgumentException
	{
		if( offsets == null )
		{
			throw new NullPointerException();
		}
		
		if( offsets.length != this.dim )
		{
			throw new IllegalArgumentException();
		}
		
		int array_index = offsets[ 0 ];
		for( int index = 1; index < offsets.length; index++ )
		{
			array_index *= this.lengths[ index ];
			array_index += offsets[ index ];
		}
				
		return( array_index );
	}
	
	
	/**
	 * Clone this object.
	 * 
	 * @return Object a clone.
	 */
	@Override
	public Object clone()
	{
		try
		{
			DefaultRealDopeMatrix clone = (DefaultRealDopeMatrix)super.clone();
			clone.total = this.total;
			clone.dim = this.dim;
			clone.lengths = this.lengths.clone();
			clone.re_data = this.re_data.clone();

			return( clone );
		}
		catch( final CloneNotSupportedException cnse )
		{
			throw (InternalError)new InternalError().initCause( cnse );
		}			
	}
			
	
	/**
	 * Serialize this object to the specified stream.
	 * 
	 * @param out the stream to which to write.
	 * @throws IOException If serialization fails.
	 */
	private void writeObject( final ObjectOutputStream out )
		throws IOException
	{
		out.defaultWriteObject();
	}


	/**
	 * Read and restore this object from the specified stream.
	 * 
	 * @param in the stream from which to read.
	 * @throws IOException If deserialization fails.
	 * @throws ClassNotFoundException If deserialization attempts to classload a
	 *         non-existant class.
	 */
	private void readObject( final ObjectInputStream in )
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		this.dim = this.lengths.length;
		this.total = 1;
		
		for( int index = 0; index < this.lengths.length; index++ )
		{
			this.total *= this.lengths[ index ];
		}
	}
	
	
	/**
	 * A good tostring for this object.
	 * 
	 * @return String a String.
	 */
	@Override
	public String toString()
	{
		return ( DopeUtilities.toString( this ) );
	}


	/**
	 * Check this specified object with the object to see if they're equal.
	 * 
	 * @param other some other object.
	 * @return boolean <code>true</code> if equal.
	 */
	@Override
	public boolean equals( final Object other )
	{
		return ( DopeUtilities.equals( this, other ) );
	}
	

}
