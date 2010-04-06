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


/**
 * A class capable of assembling multi-dimension Fourier transforms.
 * <p>
 * This class uses the singleton and abstract factory design patterns.
 *
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public abstract class MultiDimensionFourierTransformFactory
	extends Object
{
	
	
	/**
	 * Property name.
	 */
	public static final String IMPLEMENTATION_PROPERTY = "org.teneighty.fft.MultiDimensionFourierTransformFactory";

	/**
	 * Default implementation.
	 */
	private static final String DEFAULT_IMPLEMENTATION = "org.teneighty.fft.DefaultMultiDimensionFourierTransformFactory";
	
	/**
	 * The lone instance of this class.
	 */
	private static MultiDimensionFourierTransformFactory fourier_factory;


	/**
	 * Constructor.
	 * <p>
	 * Should be considered <code>private protected</code>.
	 */
	protected MultiDimensionFourierTransformFactory()
	{
		super();
	}
	

	/**
	 * Get the instance of this class.
	 * 
	 * @return FourierTransformFactory the factory instance.
	 */
	private static MultiDimensionFourierTransformFactory getInstance()
	{
		synchronized( MultiDimensionFourierTransformFactory.class )
		{
			if( fourier_factory == null )
			{
				fourier_factory = ReflectiveInstantiator.newInstance( IMPLEMENTATION_PROPERTY, DEFAULT_IMPLEMENTATION );
			}
		}

		return ( fourier_factory );
	}
	
	
	/**
	 * Get an implementation that can handle the specified dimension and
	 * lengths.
	 * 
	 * @param lens the lengths.
	 * @return MultiDimensionFourierTransform a transform.
	 * @throws IllegalArgumentException If <code>dim</code> or <code>lens</code> are illegal.
	 */
	public static MultiDimensionFourierTransform getTransform( final int... lens )
		throws IllegalArgumentException
	{
		if( lens == null )
		{
			throw new NullPointerException();
		}
		
		final int dim = lens.length;
		
		if( dim < 1 )
		{
			throw new IllegalArgumentException();
		}
		
		if( dim != lens.length )
		{
			throw new IllegalArgumentException();
		}
		
		for( int index = 0; index < dim; index++ )
		{
			if( lens[ index ] < 1 )
			{
				throw new IllegalArgumentException();
			}
		}
		
		return( getInstance().getTransformImpl( lens ) );
	}
	
	
	/**
	 * Get transform impl.
	 * 
	 * @param dim the dimension.
	 * @param lens the length.
	 * @return MultiDimensionFourierTransform the transform.
	 */
	protected abstract MultiDimensionFourierTransform getTransformImpl( int[] lens );
	

}
