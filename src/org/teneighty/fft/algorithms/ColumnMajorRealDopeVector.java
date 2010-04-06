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

import org.teneighty.fft.dope.AbstractDopeVector;
import org.teneighty.fft.dope.RealDopeVector;



/**
 * Column-major dope vector for real data.
 *
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
class ColumnMajorRealDopeVector
	extends AbstractDopeVector
	implements RealDopeVector, Serializable
{
	
	
	/**
	 * Serial verison UID.
	 */
	private static final long serialVersionUID = 92348324L;
	

	/**
	 * Real part.
	 */
	private double[][] re;

	/**
	 * Offset (row).
	 */
	protected int row;


	/**
	 * Constructor.
	 * 
	 * @param re the real part.
	 */
	ColumnMajorRealDopeVector( final double[][] re )
	{
		super();

		// store stuff.
		this.re = re;
	}


	/**
	 * Set row.
	 * 
	 * @param row the new row.
	 */
	final void setRow( final int row )
	{
		this.row = row;
	}


	/**
	 * Get the length of this vector.
	 * 
	 * @return int the length.
	 */
	public int getLength()
	{
		return ( this.re[ 0 ].length );
	}


	/**
	 * Get the real part of the specified number.
	 * 
	 * @param index the index.
	 * @return double the real part.
	 * @throws ArrayIndexOutOfBoundsException If <code>index</code> is col of
	 *         bounds.
	 */
	public double getReal( final int index )
		throws ArrayIndexOutOfBoundsException
	{
		return ( this.re[ this.row ][ index ] );
	}


	/**
	 * Set the real part of the specified number.
	 * @param value the value.
	 * @param index the index.
	 * 
	 * @throws ArrayIndexOutOfBoundsException If <code>index</code> is col of
	 *         bounds.
	 */
	public void setReal( final double value, final int index )
		throws ArrayIndexOutOfBoundsException
	{
		this.re[ this.row ][ index ] = value;
	}

	
}
