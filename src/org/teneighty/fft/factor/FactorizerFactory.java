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

import java.security.AccessController;
import java.security.PrivilegedAction;


/**
 * A class capable of returning a <code>Factorizer</code> instance. You can
 * change the factory instance created by setting the
 * <code>org.teneighty.fft.factor.FactorizerFactory</code> system property.
 * <p>
 * This class uses the abstract factory and singleton patterns.
 * 
 * @see org.teneighty.fft.factor.Factorizer
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public abstract class FactorizerFactory
	extends Object
{


	/**
	 * The lone instance of this class.
	 */
	private static FactorizerFactory ff;

	/**
	 * Property name.
	 */
	private static final String IMPLEMENTATION_PROPERTY = "org.teneighty.fft.factor.FactorizerFactory";

	/**
	 * Default implementation.
	 */
	private static final String DEFAULT_IMPLEMENTATION = "org.teneighty.fft.factor.TrialFactorizerFactory";


	/**
	 * Class initializer.
	 */
	static
	{
		// create the factorizer.
		ff = AccessController.doPrivileged( new PrivilegedAction<FactorizerFactory>()
		{


			/**
			 * Create the factorizer.
			 * 
			 * @return Factorizer the factorizer.
			 */
			public FactorizerFactory run()
			{
				String clazz_name = System.getProperty( IMPLEMENTATION_PROPERTY );
				if( clazz_name == null )
				{
					clazz_name = DEFAULT_IMPLEMENTATION;
				}

				if(clazz_name.equals("org.teneighty.fft.factor.TrialFactorizerFactory")) {
				    return new org.teneighty.fft.factor.TrialFactorizerFactory();
				}
				
				try
				{
					Class clazz = Class.forName( clazz_name, true, Thread.currentThread().getContextClassLoader() );
					return ( (FactorizerFactory)clazz.newInstance() );
				}
				catch( final ClassNotFoundException cnfe )
				{
					throw (SecurityException)new SecurityException().initCause( cnfe );
				}
				catch( final IllegalAccessException iae )
				{
					throw (SecurityException)new SecurityException().initCause( iae );
				}
				catch( final InstantiationException ie )
				{
					throw (SecurityException)new SecurityException().initCause( ie );
				}
			}

		} );
	}
	
	
	/**
	 * Get the factorizer.
	 * 
	 * @return Factorizer the factorizer.
	 */
	public static Factorizer getFactorizer()
	{
		return( getInstance().getFactorizerImpl() );
	}

	
	/**
	 * Get the instance of this class.
	 * 
	 * @return FactorizerFactory the factory instance.
	 */
	private static FactorizerFactory getInstance()
	{
		return ( ff );
	}

	
	/**
	 * Constructor.
	 * <p>
	 * Should be considered <code>private protected</code>.
	 */
	protected FactorizerFactory()
	{
		super();
	}


	/**
	 * Get the factorizer.
	 * 
	 * @return Factorizer the factorizer.
	 */
	protected abstract Factorizer getFactorizerImpl();


}
