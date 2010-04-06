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
 * Default complex dope matrix.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public class DefaultComplexDopeMatrix
	extends DefaultRealDopeMatrix
	implements DopeMatrix, RealDopeMatrix, ComplexDopeMatrix, Serializable, Cloneable
{


	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 902824L;
	
	
	/**
	 * Im data.
	 */
	private double[] im_data;

	
	/**
	 * Constructor.
	 * 
	 * @param lens the lengths.
	 * @throws IllegalArgumentException If <code>lens</code> are illegal.
	 */
	public DefaultComplexDopeMatrix( final int... lens )
		throws IllegalArgumentException
	{
		super( lens );
		
		// alloc im data.
		this.im_data = new double[ this.total ];
	}

	
	/**
	 * Get the imaginary part at the specified index. (The index here is really multi-dimensional.)
	 *
	 * @param offsets the offsets.
	 * @return double the value.
	 * @throws IllegalArgumentException If <code>offsets</code> are illegal.
	 * @throws NullPointerException If <code>offsets</code> are <code>null</code>.
	 */
	public double getImaginary( final int... offsets )
		throws IllegalArgumentException, NullPointerException
	{
		return( this.im_data[ this.getIndex( offsets ) ] );
	}
		
	
	/**
	 * Set the imaginary part of the specified number.
	 *
	 * @param offsets the offsets.
	 * @param value the new value.
	 * @throws IllegalArgumentException If <code>offsets</code> are illegal.
	 * @throws NullPointerException If <code>offsets</code> are <code>null</code>.
	 */
	public void setImaginary( final double value, final int... offsets )
		throws IllegalArgumentException, NullPointerException
	{
		this.im_data[ this.getIndex( offsets ) ] = value;
	}
	
	
	/**
	 * Clone this object.
	 * 
	 * @return Object a clone.
	 */
	@Override
	public Object clone()
	{
		DefaultComplexDopeMatrix clone = (DefaultComplexDopeMatrix)super.clone();
		clone.im_data = this.im_data.clone();
		return( clone );
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
