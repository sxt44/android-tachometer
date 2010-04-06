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

package org.teneighty.fft;

import java.util.Collections;
import java.util.List;

import org.teneighty.fft.algorithms.CooleyTukeyFastFourierTransform;
import org.teneighty.fft.algorithms.CooleyTukeyRadixTwoFastFourierTransform;
import org.teneighty.fft.algorithms.PrimeFactorFastFourierTransform;
import org.teneighty.fft.algorithms.DirectFourierTransform;
import org.teneighty.fft.factor.Factorization;
import org.teneighty.fft.factor.Factorization.Factor;
import org.teneighty.fft.factor.DefaultFactorization.DefaultFactor;


/**
 * The default fourier transform factory.
 *
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public class DefaultFourierTransformFactory
	extends FourierTransformFactory
{

	
	/**
	 * Constructor.
	 */
	public DefaultFourierTransformFactory()
	{
		super();
	}
	

	/**
	 * Create a good fourier transform for the specified factorization.
	 * 
	 * @param fact the factorization.
	 * @return FourierTranform a new transform.
	 * @throws NullPointerException If <code>fact</code> is <code>null</code>.
	 * @throws IllegalArgumentException If fact is malformed.
	 */
	@Override
	protected FourierTransform getTransformImpl( final Factorization fact )
		throws IllegalArgumentException, NullPointerException
	{
		List<Factor> facts = fact.getFactors();
		Factor first = facts.get( 0 );

		// get base.
		final int base = first.getBase();
		final int pow = first.getPower();

		if( facts.size() == 1 )
		{
			if( base == 1 )
			{
				return( new DirectFourierTransform( base ) );
			}			
			else if( base == 2 )
			{
				// use special radix two.
				return ( new CooleyTukeyRadixTwoFastFourierTransform( first.getTotal() ) );
			}
			else if( pow == 1 )
			{
				// it's prime - use prime selector!
				return( PrimeSelector.getFourierTransform( base ) );
			}
			else
			{
				// we have repeated factors - need to use mixed-radix CT.
				// create new factors...
				Factor one = new DefaultFactor( base, 1 );
				Factor two = new DefaultFactor( base, ( pow - 1 ) );

				// use mixed-radix cooley tukey.
				return ( new CooleyTukeyFastFourierTransform( Collections.singletonList( two ), Collections.singletonList( one ), first.getTotal() ) );				
			}
		}
		
		// use PFA.
		final int fsm1 = facts.size() - 1;
		return( new PrimeFactorFastFourierTransform( facts.subList( 0, fsm1 ), facts.subList( fsm1, facts.size() ), fact.getNumber() ) );
	}
	

}
