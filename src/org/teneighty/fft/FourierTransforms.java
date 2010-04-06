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

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.teneighty.fft.dope.ComplexDopeVector;
import org.teneighty.fft.dope.RealDopeVector;


/**
 * Provides static wrapper methods around Fourier transform objects.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public final class FourierTransforms
	extends Object
{


	/**
	 * Create a threadsafe view around the specified transform.
	 * 
	 * @param ft the transform.
	 * @return FourierTransform a threadsafe transform.
	 * @throws NullPointerException If <code>ft</code> is <code>null</code>.
	 */
	public static FourierTransform synchronizedFourierTransform(
			final FourierTransform ft )
		throws NullPointerException
	{
		if( ft == null )
		{
			throw new NullPointerException();
		}

		return ( new SynchronizedFourierTransform( ft ) );
	}


	/**
	 * Create a threadsafe view around the specified transform.
	 * 
	 * @param fft the transform.
	 * @return FastFourierTransform a threadsafe transform.
	 * @throws NullPointerException If <code>ft</code> is <code>null</code>.
	 */
	public static FastFourierTransform synchronizedFourierTransform(
			final FastFourierTransform fft )
		throws NullPointerException
	{
		if( fft == null )
		{
			throw new NullPointerException();
		}

		return ( new SynchronizedFastFourierTransform( fft ) );
	}


	/**
	 * Wrap the specified transform so that the forward and reverse transforms are
	 * normalized by 1/sqrt(n) (where n is the length of the transform).
	 * 
	 * @param ft the tranform to normalize
	 * @return FourierTransform a transform which normalizes by 1/sqrt(n).
	 * @throws NullPointerException If <code>ft</code> is <code>null</code>.
	 */
	public static FourierTransform normalizedFourierTransform(
			final FourierTransform ft )
		throws NullPointerException
	{
		if( ft == null )
		{
			throw new NullPointerException();
		}

		return ( ft );
	}


	/**
	 * A threadsafe Fourier transform wrapper.
	 * 
	 * @author Fran Lattanzio
	 * @version $Revision$ $Date$
	 */
	private static strictfp class SynchronizedFourierTransform
		extends Object
		implements FourierTransform, Serializable
	{


		/**
		 * Serial version UID.
		 */
		private static final long serialVersionUID = 23842347L;


		/**
		 * The backing transform.
		 */
		private FourierTransform ft;

		/**
		 * Mutex.
		 */
		private transient Object mutex;


		/**
		 * Constructor.
		 * 
		 * @param ft the transform.
		 */
		SynchronizedFourierTransform( final FourierTransform ft )
		{
			super();

			// store it.
			this.ft = ft;

			// use this as mutex.
			this.mutex = this;
		}


		/**
		 * Forward transform.
		 * 
		 * @param input the input vector.
		 * @param dest the destination vector.
		 * @throws NullPointerException If <code>input</code> or <code>dest</code>
		 *         is <code>null</code>.
		 * @throws IllegalArgumentException If <code>input</code> does not have
		 *         the right length</code>, or if <code>input</code> == <code>dest</code>.
		 */
		public void forward( final ComplexDopeVector input,
				final ComplexDopeVector dest )
			throws NullPointerException, IllegalArgumentException
		{
			synchronized( this.mutex )
			{
				this.ft.forward( input, dest );
			}
		}


		/**
		 * Backward FT.
		 * 
		 * @param input the input vector.
		 * @param dest the output vector.
		 * @throws NullPointerException If <code>input</code> or <code>dest</code>
		 *         is <code>null</code>.
		 * @throws IllegalArgumentException If <code>input</code> does not have
		 *         the right length</code>, or if <code>input</code> == <code>dest</code>.
		 */
		public void backward( final ComplexDopeVector input,
				final ComplexDopeVector dest )
			throws NullPointerException, IllegalArgumentException
		{
			synchronized( this.mutex )
			{
				this.ft.backward( input, dest );
			}
		}


		/**
		 * Transform complex data in the specified direction.
		 * 
		 * @param input the vector to transform.
		 * @param dest the destination.
		 * @param direction the direction.
		 * @throws NullPointerException If <code>input</code> or <code>dest</code>
		 *         is <code>null</code>.
		 * @throws IllegalArgumentException If <code>input</code> does not have
		 *         the right length</code>, or if <code>input</code> == <code>dest</code>.
		 */
		public void transform( final ComplexDopeVector input,
				final ComplexDopeVector dest, final TransformDirection direction )
			throws NullPointerException, IllegalArgumentException
		{
			synchronized( this.mutex )
			{
				this.ft.transform( input, dest, direction );
			}
		}


		/**
		 * Forward transform.
		 * 
		 * @param input the input vector.
		 * @param dest the destination vector.
		 * @throws NullPointerException If <code>input</code> or <code>dest</code>
		 *         is <code>null</code>.
		 * @throws IllegalArgumentException If <code>input</code> does not have
		 *         the right length</code>, or if <code>input</code> == <code>dest</code>.
		 */
		public void forward( final RealDopeVector input,
				final ComplexDopeVector dest )
			throws NullPointerException, IllegalArgumentException
		{
			synchronized( this.mutex )
			{
				this.ft.forward( input, dest );
			}
		}


		/**
		 * Backward FT.
		 * 
		 * @param input the input vector.
		 * @param dest the output vector.
		 * @throws NullPointerException If <code>input</code> or <code>dest</code>
		 *         is <code>null</code>.
		 * @throws IllegalArgumentException If <code>input</code> does not have
		 *         the right length</code>, or if <code>input</code> == <code>dest</code>.
		 * 
		 */
		public void backward( final RealDopeVector input,
				final ComplexDopeVector dest )
			throws NullPointerException, IllegalArgumentException
		{
			synchronized( this.mutex )
			{
				this.ft.backward( input, dest );
			}
		}


		/**
		 * Transform real data in the specified direction.
		 * 
		 * @param input the vector to transform.
		 * @param dest the destination.
		 * @param direction the direction.
		 * @throws NullPointerException If <code>input</code> or <code>dest</code>
		 *         is <code>null</code>.
		 * @throws IllegalArgumentException If <code>input</code> does not have
		 *         the right length</code>, or if <code>input</code> == <code>dest</code>.
		 */
		public void transform( final RealDopeVector input,
				final ComplexDopeVector dest, final TransformDirection direction )
			throws NullPointerException, IllegalArgumentException
		{
			synchronized( this.mutex )
			{
				this.ft.transform( input, dest, direction );
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
			synchronized( this.mutex )
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
		 * Get the length of this thing.
		 * 
		 * @return int the length.
		 */
		public int getLength()
		{
			synchronized( this.mutex )
			{
				return ( this.ft.getLength() );
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
			synchronized( this.mutex )
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
			synchronized( this.mutex )
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
				throw new InvalidObjectException( "No backing transform." );
			}

			// Mutex is this.
			this.mutex = this;
		}


	}


	/**
	 * Synchronized fast fourier transform!
	 * 
	 * @author Fran Lattanzio
	 * @version $Revision$ $Date$
	 */
	private static final strictfp class SynchronizedFastFourierTransform
		extends SynchronizedFourierTransform
		implements FourierTransform, FastFourierTransform, Serializable
	{


		/**
		 * Serial version UID.
		 */
		private static final long serialVersionUID = 323472347L;


		/**
		 * Constructor.
		 * 
		 * @param fft the transform.
		 */
		SynchronizedFastFourierTransform( final FastFourierTransform fft )
		{
			super( fft );
		}


	}


}
