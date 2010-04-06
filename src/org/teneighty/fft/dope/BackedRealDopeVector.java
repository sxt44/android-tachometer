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
 * A dope vector in which the backing array are specified by the user and
 * accesible.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public class BackedRealDopeVector
	extends AbstractDopeVector
	implements RealDopeVector, Serializable, Cloneable
{


	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 13123;


	/**
	 * The real array.
	 */
	private double[] real;


	/**
	 * Constructor.
	 * 
	 * @param re the real array.
	 */
	public BackedRealDopeVector( final double[] re )
	{
		super();

		this.real = re;
	}


	/**
	 * Get the length of this vector.
	 * 
	 * @return int the length.
	 */
	public int getLength()
	{
		return ( this.real.length );
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
		return ( this.real[ index ] );
	}


	/**
	 * Set the real part of the specified number.
	 * @param value the value.
	 * @param index the index.
	 * 
	 * @throws ArrayIndexOutOfBoundsException If <code>index</code> is out of
	 *         bounds.
	 */
	public void setReal( final double value, final int index )
		throws ArrayIndexOutOfBoundsException
	{
		this.checkIndex( index );
		this.real[ index ] = value;
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
			BackedRealDopeVector bdv = (BackedRealDopeVector)super.clone();
			bdv.real = this.real.clone();
			return ( bdv );
		}
		catch( final CloneNotSupportedException cnse )
		{
			throw (InternalError)new InternalError().initCause( cnse );
		}
	}


	/**
	 * Get the real part.
	 * 
	 * @return double[] the real part.
	 */
	public double[] getRealArray()
	{
		return ( this.real );
	}


	/**
	 * Set the real part of this array.
	 * 
	 * @param real The real to set.
	 * @throws IllegalArgumentException If <code>im</code> is of improper
	 *         length.
	 * @throws NullPointerException If <code>im</code> is <code>null</code>.
	 */
	public void setRealArray( double[] real )
		throws NullPointerException, IllegalArgumentException
	{
		if( real == null )
		{
			throw new NullPointerException();
		}

		if( real.length != this.getLength() )
		{
			throw new IllegalArgumentException();
		}

		this.real = real;
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
