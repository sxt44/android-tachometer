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

import org.teneighty.fft.factor.Factorization;
import org.teneighty.fft.factor.Factorizer;
import org.teneighty.fft.factor.FactorizerFactory;


/**
 * A class capable of assembling a good fast fourier transform by intelligently
 * compositing other FFTs together. You can change the instance created by
 * setting the <code>org.teneighty.fft.FourierTransformFactory</code> system
 * property <i>before</i> this class is classloaded.
 * <p>
 * This class uses the abstract factory and singleton patterns.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public abstract class FourierTransformFactory
	extends Object
{


	/**
	 * Property name.
	 */
	public static final String IMPLEMENTATION_PROPERTY = "org.teneighty.fft.FourierTransformFactory";

	/**
	 * Default implementation.
	 */
	private static final String DEFAULT_IMPLEMENTATION = "org.teneighty.fft.DefaultFourierTransformFactory";

	/**
	 * The lone instance of this class.
	 */
	private static FourierTransformFactory fourier_factory;


	/**
	 * Class initializer.
	 */
	static
	{
		// factory used to be initialized here, but not anymore
		// (although I might switch it back).
	}


	/**
	 * Get the instance of this class.
	 * 
	 * @return FourierTransformFactory the factory instance.
	 */
	private static FourierTransformFactory getInstance()
	{
		synchronized( FourierTransformFactory.class )
		{
			if( fourier_factory == null )
			{
                //fourier_factory = ReflectiveInstantiator.newInstance( IMPLEMENTATION_PROPERTY, DEFAULT_IMPLEMENTATION );
                fourier_factory = new org.teneighty.fft.DefaultFourierTransformFactory();
			}
		}

		return ( fourier_factory );
	}


	/**
	 * Create a good fourier transform for the specified factorization.
	 * 
	 * @param size the size.
	 * @return FourierTransform a suitable transform.
	 * @throws NullPointerException If stuff dies.
	 * @throws IllegalArgumentException If fact
	 */
	public static FourierTransform getTransform( final int size )
		throws IllegalArgumentException
	{
		return ( getInstance().getTransformImpl( size ) );
	}


	/**
	 * Create a good fourier transform for the specified factorization.
	 * 
	 * @param fact the factorization.
	 * @return FourierTransform a suitable transform.
	 * @throws NullPointerException If stuff dies.
	 * @throws IllegalArgumentException If fact
	 */
	public static FourierTransform getTransform( final Factorization fact )
		throws IllegalArgumentException
	{
		return ( getInstance().getTransformImpl( fact ) );
	}


	/**
	 * Create a good Fourier Transform for the given size.
	 * 
	 * @param size the size.
	 * @return FourierTransform a good transform.
	 * @throws IllegalArgumentException If size is illegal.
	 */
	private FourierTransform getTransformImpl( final int size )
		throws IllegalArgumentException
	{
		if( size < 1 )
		{
			throw new IllegalArgumentException();
		}

		// get a factorizer.
		Factorizer facter = FactorizerFactory.getFactorizer();

		// factor the number.
		Factorization fact = facter.factorize( size );

		// delegate it.
		return ( this.getTransformImpl( fact ) );
	}


	/**
	 * Create a good fourier transform for the specified factorization.
	 * 
	 * @param fact the factorization.
	 * @return FourierTransform a good transform.
	 * @throws NullPointerException If stuff dies.
	 * @throws IllegalArgumentException If fact is malformed.
	 */
	protected abstract FourierTransform getTransformImpl( final Factorization fact )
		throws IllegalArgumentException;


	/**
	 * Constructor.
	 * <p>
	 * Should be considered <code>private protected</code>.
	 */
	protected FourierTransformFactory()
	{
		super();
	}


}
