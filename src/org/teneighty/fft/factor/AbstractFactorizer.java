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

package org.teneighty.fft.factor;


/**
 * A class from which other factorizer implementations can extend.
 * <p>
 * Basically does nothing for now (but someday it might do something).
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public abstract class AbstractFactorizer
	extends Object
	implements Factorizer
{


	/**
	 * Constructor.
	 * <p>
	 * Should be considered <code>private protected</code>.
	 */
	protected AbstractFactorizer()
	{
		super();
	}
	
	
	/**
	 * A lame to string.
	 * 
	 * @return String a string.
	 */
	@Override
	public String toString()
	{
		return( this.getClass().getCanonicalName() );
	}
	
	
	/**
	 * Equals for this object.
	 * <p>
	 * Two factorizers are considered equal if they are of the same class.
	 * 
	 * @param other some other object.
	 * @return boolean <code>true</code> if equal.
	 */
	@Override
	public boolean equals( final Object other )
	{
		if( other == this )
		{
			return( true );
		}
		
		if( other == null )
		{
			return( false );
		}
		
		return( other.getClass().equals( this.getClass() ) == true );
	}	
		

	/**
	 * A hashcode function inline with equals.
	 * 
	 * @return int the hashcode.
	 */
	@Override
	public int hashCode()
	{
		return( this.getClass().hashCode() );
	}
	

}
