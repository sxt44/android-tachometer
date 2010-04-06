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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;


/**
 * Simple implementation of sieve of Eratosthenes factorizer. This is very
 * similiar to the trial factorizer, except that it records known prime factors
 * for future re-use.
 * <p>
 * It is generally faster than the trial division factorizer, though only 3-5
 * times faster (i.e. not an order of magnitude), but at the expense of
 * increased memory usage. This factorizer also allows you some control over its
 * behaviour. You can, for example, tell it to expand the sieve to any radix, or
 * clear the sieve (the cache of primes).
 * <p>
 * Not safe for use by multiple threads.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public class SieveOfEratosthenesFactorizer
	extends AbstractFactorizer
	implements Factorizer, Serializable
{


	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 328348L;


	/**
	 * List of known prime factors.
	 */
	private transient int[] primes;

	/**
	 * Largest known prime.
	 */
	private transient int largest;

	/**
	 * Next index to use.
	 */
	private transient int nextIndex;


	/**
	 * Constructor.
	 */
	public SieveOfEratosthenesFactorizer()
	{
		super();

		// initialize stuff.
		this.initialize();
	}


	/**
	 * Initialize this object.
	 */
	private void initialize()
	{
		// create prime factor list.
		this.primes = new int[ 16 ];
		this.primes[ 0 ] = 3;
		this.largest = 3;
		this.nextIndex = 1;
	}


	/**
	 * Factorize the specified integer.
	 * 
	 * @param n the integer to factor.
	 * @return Factorization the factorization.
	 * @throws IllegalArgumentException If <code>n</code> is less than 2.
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

		if( n >= this.largest )
		{
			this.sievePast( n );
		}

		int f = 0, c = 0, index = 0;

		while( n != 1 )
		{
			f = this.primes[ index ];
			c = 0;

			while( ( n % f ) == 0 )
			{
				n /= f;
				c += 1;
			}

			if( c != 0 )
			{
				facts.add( new DefaultFactorization.DefaultFactor( f, c ) );
			}

			index += 1;
		}

		if( n != 1 )
		{
			facts.add( new DefaultFactorization.DefaultFactor( n, 1 ) );
		}

		return ( new DefaultFactorization( facts, orig ) );
	}


	/**
	 * Sieve past the specified number.
	 * <p>
	 * This stores all prime numbers less than <code>past</code>, making for
	 * very fast factorization of numbers smaller than <code>past</code>.
	 * 
	 * @param past the number to sieve past (i.e. record the smallest prime that
	 *        is larger than <code>past</code> but no more).
	 * @throws IllegalArgumentException If <code>past</code> is less than 1.
	 */
	public void sievePast( final int past )
		throws IllegalArgumentException
	{
		if( past < 1 )
		{
			throw new IllegalArgumentException();
		}

		while( this.largest <= past )
		{
			this.sieveNext();
		}
	}


	/**
	 * Clear the sieve (i.e. the cached primes).
	 */
	public void clearSieve()
	{
		this.initialize();
	}


	/**
	 * Sieve to the next number.
	 */
	private void sieveNext()
	{
		int next = this.largest;

		do
		{
			next += 2;
		}
		while( isPrime( next ) == false );

		if( this.nextIndex == this.primes.length )
		{
			// use stupid array doubling trick.
			int p[] = new int[ this.primes.length * 2 ];
			System.arraycopy( this.primes, 0, p, 0, this.primes.length );
			this.primes = p;
		}

		// actually store it.
		this.largest = next;
		this.primes[ this.nextIndex++ ] = next;
	}


	/**
	 * Is the specified number prime?
	 * 
	 * @param num the number to check.
	 * @return true if prime.
	 */
	private boolean isPrime( final int num )
	{
		int sqrt = (int)Math.ceil( Math.sqrt( num ) );
		int last = 0, index = 0;
		while( last < sqrt )
		{
			last = this.primes[ index ];

			if( ( num % last ) == 0 )
			{
				return ( false );
			}

			index += 1;
		}

		return ( true );
	}


	/**
	 * Serialization nonsense.
	 * 
	 * @param out the stream to which to write.
	 * @throws IOException If serialization fails.
	 */
	private void writeObject( final ObjectOutputStream out )
		throws IOException
	{
		out.defaultWriteObject();
	}


	/**
	 * Read and restore this object from the specified stream.
	 * 
	 * @param in the stream from which to read.
	 * @throws IOException If deserialization fails.
	 * @throws ClassNotFoundException If deserialization attempts to classload a
	 *         non-existant class.
	 */
	private void readObject( final ObjectInputStream in )
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		// init stuff.
		this.initialize();
	}


}
