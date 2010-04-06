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

import org.teneighty.fft.dope.AbstractReIndexedRealDopeVector;
import org.teneighty.fft.dope.RealDopeVector;


/**
 * This class provides the ability to view the underlying dope vector
 * through an arbitrary permutation.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
class PermutedRealDopeVector
	extends AbstractReIndexedRealDopeVector
	implements RealDopeVector
{

	
	/**
	 * Permutation map.
	 */
	private int[] map;


	/**
	 * Constructor.
	 * 
	 * @param map the permutation map.
	 */
	public PermutedRealDopeVector( final int[] map )
	{
		super();

		// store the map!
		this.map = map;
	}


	/**
	 * Re-index.
	 * 
	 * @param index the index to re-index.
	 * @return int the re-indexed index.
	 */
	@Override
	protected int reIndex( final int index )
	{
		return ( this.map[ index ] );
	}


	/**
	 * Get length.
	 * 
	 * @return int the length.
	 */
	@Override
	public int getLength()
	{
		return ( this.map.length );
	}


}


