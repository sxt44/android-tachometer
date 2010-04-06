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

import java.io.Serializable;


/**
 * A dope vector in which the backing array are specified by the user and
 * accesible.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public class BackedComplexDopeVector
	extends BackedRealDopeVector
	implements RealDopeVector, ComplexDopeVector, Serializable, Cloneable
{


	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 13123;


	/**
	 * The imaginary part.
	 */
	private double[] im;


	/**
	 * Constructor.
	 * 
	 * @param re the real array.
	 * @param im the imaginary array.
	 */
	public BackedComplexDopeVector( final double[] re, final double[] im )
	{
		super( re );

		if( re.length != im.length )
		{
			throw new IllegalArgumentException();
		}

		this.im = im;
	}


	/**
	 * Get the imaginary part of the specified number.
	 * 
	 * @param index the index.
	 * @return double the imaginary part.
	 * @throws ArrayIndexOutOfBoundsException If <code>index</code> is out of
	 *         bounds.
	 */
	public double getImaginary( final int index )
		throws ArrayIndexOutOfBoundsException
	{
		this.checkIndex( index );
		return ( this.im[ index ] );
	}


	/**
	 * Set the imaginary part of the specified number.
	 * @param value the value.
	 * @param index the index.
	 * 
	 * @throws ArrayIndexOutOfBoundsException If <code>index</code> is out of
	 *         bounds.
	 */
	public void setImaginary( final double value, final int index )
		throws ArrayIndexOutOfBoundsException
	{
		this.checkIndex( index );
		this.im[ index ] = value;
	}


	/**
	 * Clone this vector.
	 * 
	 * @return Object a clone of this vector.
	 */
	@Override
	public Object clone()
	{
		BackedComplexDopeVector bdv = (BackedComplexDopeVector)super.clone();
		bdv.im = this.im.clone();
		return ( bdv );
	}


	/**
	 * Get the imaginary array.
	 * 
	 * @return double[] the imaginary part.
	 */
	public double[] getImaginaryArray()
	{
		return ( this.im );
	}


	/**
	 * Set the imaginary array.
	 * 
	 * @param im the imaginary part.
	 * @throws IllegalArgumentException If <code>im</code> is of improper
	 *         length.
	 * @throws NullPointerException If <code>im</code> is <code>null</code>.
	 */
	public void setImaginaryArray( final double[] im )
		throws NullPointerException, IllegalArgumentException
	{
		if( im == null )
		{
			throw new NullPointerException();
		}

		if( im.length != this.getLength() )
		{
			throw new IllegalArgumentException();
		}

		this.im = im;
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
