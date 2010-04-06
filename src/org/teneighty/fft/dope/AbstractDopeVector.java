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
 * A class from which all dope vectors can extend.
 *
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public abstract class AbstractDopeVector
	extends Object
	implements DopeVector
{
	
	
	/**
	 * Constructor.
	 * <p>
	 * Should be considered <code>private protected</code>.
	 */
	protected AbstractDopeVector()
	{
		super();
	}
	
	
	/**
	 * Check the specified index for bounds constraints.
	 * 
	 * @param index the index to check.
	 * @throws ArrayIndexOutOfBoundsException If <code>index</code> is out of bounds.
	 */
	protected final void checkIndex( final int index )
		throws ArrayIndexOutOfBoundsException
	{
		if( index < 0 || index >= this.getLength() )
		{
			throw new ArrayIndexOutOfBoundsException();
		}
	}
	
	
	/**
	 * A hashcode based on length.
	 * 
	 * @return int hashcode.
	 */
	@Override
	public int hashCode()
	{
		return( this.getLength() );
	}
	
	
}
