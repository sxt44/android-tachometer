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


/**
 * A class from which all re-indexed dope vectors can extend.
 *
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public abstract class AbstractReIndexedDopeVector
	extends AbstractDopeVector
	implements DopeVector
{
	
	
	/**
	 * Compute the specified reindexing.
	 * <p>
	 * Implement this method with your specific re-indexing scheme.
	 * 
	 * @param index the index.
	 * @return int the re-indexed index.
	 */
	protected abstract int reIndex( final int index );
	
	
	/**
	 * Re-abstraction of get length; you must implement this method, obviously.
	 * 
	 * @return int the length.
	 */
	public abstract int getLength();


}
