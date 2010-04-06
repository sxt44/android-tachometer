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

import java.security.AccessController;
import java.security.PrivilegedAction;


/**
 * A stupid util class used by the factories.
 * <p>
 * This is a package-level class. This class is stateless and cannot be
 * instantiated.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
final class ReflectiveInstantiator
	extends Object
{


	/**
	 * Create the instance.
	 * 
	 * @param <T> the the type.
	 * @param impl_property the system property grab.
	 * @param def the default class name if <code>impl_property</code> is
	 *        <code>null</code>.
	 * @return T a new instance.
	 * @throws InternalError if stuff cannot be created (it is assumed that this
	 *         is a fatal condition).
	 */
	static <T> T newInstance( final String impl_property, final String def )
		throws InternalError
	{
		// create the factory.
		T instance = AccessController.doPrivileged( new PrivilegedAction<T>()
		{


			/**
			 * Create the factorizer.
			 * 
			 * @return Factorizer the factorizer.
			 */
			@SuppressWarnings( "unchecked" )
			public T run()
			{
				String clazz_name = System.getProperty( impl_property );
				if( clazz_name == null )
				{
					clazz_name = def;
				}

				try
				{
					// load class. assume no-arg constructor.
					Class clazz = Class.forName( clazz_name, true, Thread.currentThread().getContextClassLoader() );
					
					// erased cast.
					return ( (T)clazz.newInstance() );
				}
				catch( final ClassNotFoundException cnfe )
				{
					throw (InternalError)new InternalError().initCause( cnfe );
				}
				catch( final IllegalAccessException iae )
				{
					throw (InternalError)new InternalError().initCause( iae );
				}
				catch( final InstantiationException ie )
				{
					throw (InternalError)new InternalError().initCause( ie );
				}
			}

		} );

		return ( instance );
	}


	/**
	 * No instances.
	 * 
	 * @throws InternalError always.
	 */
	private ReflectiveInstantiator()
	{
		throw new InternalError();
	}


}
