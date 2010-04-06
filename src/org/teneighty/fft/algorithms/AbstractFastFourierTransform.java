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

import org.teneighty.fft.FastFourierTransform;
import org.teneighty.fft.FourierTransform;


/**
 * Abstract fast Fourier transform class. 
 * <p>
 * Does nothing for now. Might do something someday.
 *
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public abstract class AbstractFastFourierTransform
	extends AbstractFourierTransform
	implements FourierTransform, FastFourierTransform, Serializable
{
	
	
	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 32489237L;
	
	
	/**
	 * Constructor.
	 * <p>
	 * Should be considered <code>private protected</code>.
	 * 
	 * @param size the size.
 	 * @throws IllegalArgumentException If <code>size</code> is less than 1.
	 */
	protected AbstractFastFourierTransform( final int size )
		throws IllegalArgumentException
	{
		super( size );
	}
	

}
