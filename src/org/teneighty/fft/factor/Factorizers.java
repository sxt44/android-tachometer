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
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


/**
 * Provides static wrapper functionality around a <code>Factorizer</code>
 * instance.
 * <p>
 * This class cannot be instantiated.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public final class Factorizers
	extends Object
{


	/**
	 * Create a threadsafe view around the specified factorizer.
	 * 
	 * @param fact the factorizer to wrap.
	 * @return Factorizer a threadsafe view.
	 * @throws NullPointerException If <code>fact</code> is <code>null</code>.
	 */
	public static Factorizer synchronizedFactorizer( final Factorizer fact )
		throws NullPointerException
	{
		if( fact == null )
		{
			throw new NullPointerException();
		}

		return ( new SynchronizedFactorizer( fact ) );
	}


	/**
	 * A threadsafe Fourier transform wrapper.
	 * 
	 * @author Fran Lattanzio
	 * @version $Revision$ $Date$
	 */
	private static class SynchronizedFactorizer
		extends AbstractFactorizer
		implements Factorizer, Serializable
	{


		/**
		 * Serial version UID.
		 */
		private static final long serialVersionUID = 23842347L;


		/**
		 * The backing transform.
		 */
		private Factorizer ft;

		/**
		 * Mutex
		 */
		private transient Object mutex;


		/**
		 * Constructor.
		 * 
		 * @param ft the factorizer.
		 */
		private SynchronizedFactorizer( final Factorizer ft )
		{
			super();

			// store it.
			this.ft = ft;

			// use this as mutex.
			this.mutex = this;
		}


		/**
		 * Factorize the specified integer.
		 * 
		 * @param fact the integer to factor.
		 * @return Factorization the factorization.
		 * @throws IllegalArgumentException If <code>fact</code> is illegal.
		 */
		public Factorization factorize( final int fact )
			throws IllegalArgumentException
		{
			synchronized ( this.mutex )
			{
				return ( this.ft.factorize( fact ) );
			}

		}


		/**
		 * Check this object for equality.
		 * 
		 * @param other some other object.
		 * @return boolean <code>true</code> if equal.
		 */
		@Override
		public boolean equals( final Object other )
		{
			synchronized ( this.mutex )
			{

				if( other == null )
				{
					return ( false );
				}

				if( other == this )
				{
					return ( true );
				}

				return ( this.ft.equals( other ) );
			}
		}


		/**
		 * Hashcode.
		 * 
		 * @return int hashcode.
		 */
		@Override
		public int hashCode()
		{
			synchronized ( this.mutex )
			{
				return ( this.ft.hashCode() );
			}
		}


		/**
		 * To string override.
		 * 
		 * @return String a nice string.
		 */
		@Override
		public String toString()
		{
			synchronized ( this.mutex )
			{
				return ( this.ft.toString() );
			}
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

			if( this.ft == null )
			{
				throw new InvalidObjectException( "No backing factorizer." );
			}

			// Mutex is this.
			this.mutex = this;
		}


	}


	/**
	 * No instances.
	 * 
	 * @throws InternalError always
	 */
	private Factorizers()
		throws InternalError
	{
		throw new InternalError();
	}


}
