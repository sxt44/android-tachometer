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

package org.teneighty.fft.dope;

import java.io.Serializable;


/**
 * A dope vector backed by an array of doubles.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public class DefaultRealDopeVector
	extends AbstractDopeVector
	implements RealDopeVector, Serializable, Cloneable
{


	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 155434L;


	/**
	 * The backing array.
	 */
	private double[] array;

	/**
	 * The size.
	 */
	private int size;


	/**
	 * Constructor.
	 * 
	 * @param size the size.
	 * @throws IllegalArgumentException If <code>size</code> is negative.
	 */
	public DefaultRealDopeVector( final int size )
		throws IllegalArgumentException
	{
		super();
		
		if( size < 0 )
		{
			throw new IllegalArgumentException();
		}		

		// store and create stuff!
		this.size = size;
		this.array = new double[ size ];
	}


	/**
	 * Get the length of this vector.
	 * 
	 * @return int the length.
	 */
	public int getLength()
	{
		return ( this.size );
	}


	/**
	 * Get the real part of the specified number.
	 * 
	 * @param index the index.
	 * @return double the real part.
	 * @throws ArrayIndexOutOfBoundsException If <code>index</code> is out of
	 *         bounds.
	 */
	public double getReal( final int index )
		throws ArrayIndexOutOfBoundsException
	{
		this.checkIndex( index );
		return ( this.array[ index ] );
	}


	/**
	 * Set the real part of the specified number.
	 * @param value the value.
	 * @param index the index.
	 * 
	 * @throws ArrayIndexOutOfBoundsException If <code>index</code> is out of
	 *         bounds.
	 */
	public void setReal( double value, int index )
		throws ArrayIndexOutOfBoundsException
	{
		this.checkIndex( index );
		this.array[ index ] = value;
	}
	
	
	/**
	 * Clone this vector.
	 * 
	 * @return Object a clone of this vector.
	 */
	@Override
	public Object clone()
	{
		try
		{
			DefaultRealDopeVector ddv = (DefaultRealDopeVector)super.clone();
			ddv.array = this.array.clone();
			return( ddv );
		}
		catch( final CloneNotSupportedException cnse )
		{
			throw (InternalError)new InternalError().initCause( cnse );
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
		return( DopeUtilities.toString( this ) );
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
		return( DopeUtilities.equals( this, other ) );
	}


}