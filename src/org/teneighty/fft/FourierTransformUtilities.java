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

import java.util.List;
import java.util.Random;

import org.teneighty.fft.factor.Factorization;
import org.teneighty.fft.factor.Factorization.Factor;
import org.teneighty.fft.factor.Factorizer;
import org.teneighty.fft.factor.FactorizerFactory;


/**
 * A class of happy static methods generally useful in doing Fourier transforms.
 * <p>
 * This class is stateless and cannot be instantiated.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public final class FourierTransformUtilities
	extends Object
{


	/**
	 * A happy little random number generator.
	 */
	private static final Random random;


	/**
	 * Initialize some lame class data.
	 */
	static
	{
		random = new Random();
	}


	/**
	 * Find a generator element of the group defined by <b>Z</b><sub><i>p</i></sub></b>
	 * <p>
	 * This method is SLOW, because of the modpow implementation used...
	 * 
	 * @param p a number.
	 * @return the generator element of the group defined by <b>Z</b><sub><i>p</i></sub></b>
	 * @throws IllegalArgumentException If <code>p</code> is illegal (e.g.
	 *         negative, not prime).
	 */
	public static int generator( final int p )
		throws IllegalArgumentException
	{
		if( p < 2 )
		{
			throw new IllegalArgumentException();
		}

		if( isPrime( p ) == false )
		{
			throw new IllegalArgumentException();
		}

		// special case...
		if( p == 2 )
		{
			return ( 1 );
		}

		// p minus 1
		final int pm1 = ( p - 1 );

		// factor p - 1.
		Factorizer facter = FactorizerFactory.getFactorizer();
		Factorization fact = facter.factorize( pm1 );

		List<Factor> factors = fact.getFactors();

		int gen = 2, temp = 0, tt;
		for( int index = 0; index < factors.size(); index++ )
		{
			temp = pm1 / factors.get( index ).getBase();
			tt = powerMod( gen, temp, p );
			if( tt == 1 )
			{
				index = -1;
				gen += 1;
			}
		}

		return ( gen );
	}


	/**
	 * Prime method hidder. You should use this method.
	 * 
	 * @param num the number to check.
	 * @return boolean true if prime.
	 */
	public static boolean isPrime( final int num )
	{
		return ( trialIsPrime( num ) );
	}


	/**
	 * Test the specified number for primality, using Miller-Rabin (random).
	 * 
	 * @param num the number.
	 * @return boolean true if most likely prime.
	 */
	public static boolean mrtIsPrime( final int num )
	{
		// re-seed random.
		random.setSeed( System.currentTimeMillis() );

		int a = 0;
		for( int index = 0; index < 10; index++ )
		{
			a = random.nextInt( num - 1 ) + 1;
			if( witness( a, num ) == true )
			{
				// definitely composite.
				return ( false );
			}

		}

		// probably prime... probably.
		return ( true );
	}


	/**
	 * Miller-Rabin witness.
	 * 
	 * @param a a potential witness.
	 * @param n number to check.
	 * @return boolean true if a was a witness was found.
	 */
	private static boolean witness( final int a, final int n )
	{
		int d = 1;
		int x = 0;
		for( int i = 31; i >= 0; i-- )
		{
			x = d;
			d = ( d * d ) % n;

			if( d == 1 && x != 1 && x != ( n - 1 ) )
			{
				return ( true );
			}

			if( ( n & ( 1 << i ) ) == 1 )
			{
				d = ( d * a ) % n;
			}
		}

		return ( ( d != 1 ) ? true : false );
	}


	/**
	 * Test the specified number for primality (deterministic).
	 * <p>
	 * Slow method... I suppose I could implement AKS or something, but I'm too
	 * lazy.
	 * 
	 * @param num the number to check.
	 * @return boolean true if prime.
	 * @throws IllegalArgumentException If <code>num</code> is negative.
	 */
	public static boolean trialIsPrime( final int num )
		throws IllegalArgumentException
	{
		if( num < 0 )
		{
			throw new IllegalArgumentException();
		}

		if( num == 0 || num == 1 )
		{
			return ( false );
		}

		if( num == 2 )
		{
			return ( true );
		}

		if( ( num % 2 ) == 0 )
		{
			return ( false );
		}

		final int sqrt = 1 + ( (int)Math.sqrt( num ) );
		for( int index = 3; index < sqrt; index += 2 )
		{
			if( ( num % index ) == 0 )
			{
				return ( false );
			}

		}

		// it's prime and stuff.
		return ( true );
	}


	/**
	 * Dumb method to compute (base ** pow) mod n.
	 * <p>
	 * This is not fast. Or slow, depending on your point of view...
	 * 
	 * @param base the base.
	 * @param pow the power.
	 * @param n the mod.
	 * @return int (base ** pow ) mod n
	 * @throws IllegalArgumentException If n is less than 2.
	 */
	public static int powerMod( final int base, final int pow, final int n )
		throws IllegalArgumentException
	{
		if( n < 2 )
		{
			throw new IllegalArgumentException();
		}

		if( base == 0 )
		{
			return ( 0 );
		}

		if( pow == 0 )
		{
			return ( 1 );
		}

		int total = 1;
		for( int index = 0; index < pow; index++ )
		{
			total = multiplyMod( total, base, n );
		}

		return ( total );
	}


	/**
	 * Primitive method to compute (a * b) mod n
	 * 
	 * @param a first number.
	 * @param b second number.
	 * @param n number by which to mod.
	 * @return int (a * b) mod n.
	 * @throws IllegalArgumentException If <code>n</code> is less than 2.
	 */
	public static final int multiplyMod( final int a, final int b, final int n )
		throws IllegalArgumentException
	{
		if( n < 2 )
		{
			throw new IllegalArgumentException();
		}

		if( a == 0 || b == 0 )
		{
			return ( 0 );
		}

		if( a < ( Integer.MAX_VALUE / b ) )
		{
			// hooray.
			return ( ( a * b ) % n );
		}

		// time to recurse and stuff.
		int new_b1 = ( b / 2 );
		int new_b2 = b - new_b1;
		return ( multiplyMod( a, new_b1, n ) + multiplyMod( a, new_b2, n ) );
	}


	/**
	 * Find the gcd of the specifed numbers, using Euclid's algorithm.
	 * 
	 * @param a the first number.
	 * @param b the second number.
	 * @return int the gcd of a and b.
	 */
	public static int euclid( int a, int b )
	{
		int t = 0;
		while( b != 0 )
		{
			t = b;
			b = a % b;
			a = t;
		}

		return ( Math.abs( a ) );
	}


	/**
	 * Extended Euclidean GCD algorithm.
	 * 
	 * @param a a integer
	 * @param b another integer
	 * @return int[] of size three s.t.
	 *         <code>a*int[ 0 ] + b*int[ 1 ] = int[ 2 ] = gcd(a,b)</code>.
	 */
	public static int[] extendedEuclid( int a, int b )
	{
		int x = 0;
		int lastx = 1;
		int y = 1;
		int lasty = 0;
		int temp = 0;
		int quotient = 0;

		while( b != 0 )
		{
			temp = b;
			quotient = a / b;
			b = a % b;
			a = temp;

			temp = x;
			x = lastx - ( quotient * x );
			lastx = temp;

			temp = y;
			y = lasty - ( quotient * y );
			lasty = temp;
		}

		return ( new int[]{ lastx, lasty, a } );
	}


	/**
	 * Compute the inverse of a mod b (assuming a and b are relatively prime, of
	 * course), i.e., <code>c</code> such that <code>( c * a ) % b == 0</code>.
	 * <p>
	 * Returns only <i>positive</i> values.
	 * 
	 * @param a an integer.
	 * @param b some other integer
	 * @return int the multiplicative inverse of a mod b.
	 * @throws IllegalArgumentException If <code>a</code> or <code>b</code>
	 *         are negative or not relatively prime.
	 */
	public static int inverse( int a, int b )
		throws IllegalArgumentException
	{
		if( a < 1 || b < 1 )
		{
			throw new IllegalArgumentException();
		}

		if( euclid( a, b ) != 1 )
		{
			throw new IllegalArgumentException();
		}

		final int init = b;
		int x = 0;
		int lastx = 1;
		int y = 1;
		int lasty = 0;
		int temp = 0;
		int quotient = 0;

		while( b != 0 )
		{
			temp = b;
			quotient = a / b;
			b = a % b;
			a = temp;

			temp = x;
			x = lastx - ( quotient * x );
			lastx = temp;

			temp = y;
			y = lasty - ( quotient * y );
			lasty = temp;
		}

		while( lastx < 0 )
		{
			// turn it into a positive value.
			lastx += init;
		}

		return ( lastx );
	}


	/**
	 * No instances. Even reflectively. Go ahead and try. I dare you!
	 * 
	 * @throws InternalError always.
	 */
	private FourierTransformUtilities()
		throws InternalError
	{
		throw new InternalError();
	}


}
