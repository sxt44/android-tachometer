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
 * A class from which all dope matrix implementations can extend.
 *
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public abstract class AbstractDopeMatrix
	extends Object
	implements DopeMatrix
{
	
	
	/**
	 * Constructor.
	 * <p>
	 * Should be considered <code>private protected</code>.
	 */
	protected AbstractDopeMatrix()
	{
		super();
	}
	
	
	/**
	 * A hashcode based on dimension and dimensionality.
	 * 
	 * @return int hashcode.
	 */
	@Override
	public int hashCode()
	{
		int hash = this.getDimension();
		for( int index = 0; index < this.getDimension(); index++ )
		{
			hash ^= this.getLength( index );
		}		
		return( hash );
	}
	

}
