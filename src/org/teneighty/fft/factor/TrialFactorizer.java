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

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;


/**
 * Trial division factorizer: Factors integers by trial division!
 *
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public class TrialFactorizer
	extends AbstractFactorizer
	implements Factorizer, Serializable
{
	
	
	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 234234L;
	
	
	/**
	 * Constructor.
	 * <p>
	 * Does nothing.
	 */
	public TrialFactorizer()
	{
		super();
	}
	
	
	/**
	 * Factorize the specified integer.
	 * 
	 * @param n the integer to factor.
	 * @return Factorization the factorization.
	 * @throws IllegalArgumentException If <code>n</code> is less than 1.
	 */
	public Factorization factorize( int n )
		throws IllegalArgumentException
	{
		if( n < 1 )
		{
			throw new IllegalArgumentException( String.valueOf( n ) );
		}

		final int orig = n;
		final List<Factorization.Factor> facts = new ArrayList<Factorization.Factor>();

		if( n == 1 )
		{
			DefaultFactorization.DefaultFactor one = new DefaultFactorization.DefaultFactor( 1, 1 );
			facts.add( one );
			return( new DefaultFactorization( facts, orig ) );
		}
				
		int two = 0;
		while( ( n & 1 ) != 1 )
		{
			n = n >> 1;
			two += 1;			
		}
		
		if( two != 0 )
		{
			// create and add new factor.
			facts.add( new DefaultFactorization.DefaultFactor( 2, two ) );
		}
		
		for( int times = 0, index = 3; n != 1; index += 2 )
		{
			times = 0;
			while( ( n % index ) == 0 )
			{
				// how many times is it in there???
				times += 1;
				n /= index;
			}

			if( times != 0 )
			{
				// create and add new factor.
				facts.add( new DefaultFactorization.DefaultFactor( index, times ) );
			}			
		}
				
		// return it...
		return( new DefaultFactorization( facts, orig ) );
	}
	
	
}
