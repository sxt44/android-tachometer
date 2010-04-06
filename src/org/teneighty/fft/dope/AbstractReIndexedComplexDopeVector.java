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
 * Abstract class that aids in constructing re-indexing wrapper dope vectors
 * (i.e. that delegate to an underlying dope). This class is kind of dumb, since
 * it probably adds to the time it takes to actually extract/set a value from a
 * dope... but whatever.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public abstract class AbstractReIndexedComplexDopeVector
	extends AbstractReIndexedDopeVector
	implements ComplexDopeVector
{


	/**
	 * Delegate dope vector.
	 */
	private ComplexDopeVector delegate;


	/**
	 * Constructor.
	 */
	protected AbstractReIndexedComplexDopeVector()
	{
		super();

		// store delegate.
		this.setDelegate( null );
	}


	/**
	 * Get the delegate vector.
	 * 
	 * @return ComplexDopeVector the delegate.
	 */
	public final ComplexDopeVector getDelegate()
	{
		return ( this.delegate );
	}


	/**
	 * Set the delegate vector.
	 * 
	 * @param del the new delegate.
	 */
	public final void setDelegate( final ComplexDopeVector del )
	{
		this.delegate = del;
	}


	/**
	 * Get the imaginary component of the specified index.
	 * 
	 * @param index the index to get.
	 * @return double the value.
	 * @throws ArrayIndexOutOfBoundsException If <code>index</code> is out of
	 *         bounds.
	 */
	public double getImaginary( final int index )
		throws ArrayIndexOutOfBoundsException
	{
		return ( this.delegate.getImaginary( this.reIndex( index ) ) );
	}


	/**
	 * Get the re component of the specifed index.
	 * 
	 * @param index the index.
	 * @return double the re component.
	 * @throws ArrayIndexOutOfBoundsException If <code>index</code> is out of
	 *         bounds.
	 */
	public double getReal( final int index )
		throws ArrayIndexOutOfBoundsException
	{
		return ( this.delegate.getReal( this.reIndex( index ) ) );
	}


	/**
	 * Set the imaginary component of the specified index.
	 * 
	 * @param value the new value.
	 * @param index the index to set.
	 * @throws ArrayIndexOutOfBoundsException If <code>index</code> is out of
	 *         bounds.
	 */
	public void setImaginary( final double value, final int index )
		throws ArrayIndexOutOfBoundsException
	{
		this.delegate.setImaginary( value, this.reIndex( index ) );
	}


	/**
	 * Set the re component of the specified index.
	 * 
	 * @param value the new value.
	 * @param index the index to set.
	 * @throws ArrayIndexOutOfBoundsException If <code>index</code> is out of
	 *         bounds.
	 */
	public void setReal( final double value, final int index )
		throws ArrayIndexOutOfBoundsException
	{
		this.delegate.setReal( value, this.reIndex( index ) );
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
