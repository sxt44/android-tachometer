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

import org.teneighty.fft.dope.ComplexDopeMatrix;
import org.teneighty.fft.dope.RealDopeMatrix;


/**
 * This class contains static wrapper methods for multidimension Fourier
 * transforms.
 * <p>
 * This is a stateless class that cannot be instantiated.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public final class MultiDimensionFourierTransforms
	extends Object
{


	/**
	 * Create a threadsafe view around the specified multi-dimensional transform
	 * object.
	 * 
	 * @param trans the transform object.
	 * @return MultiDimensionFourierTransform a threadsafe view around the
	 *         specified transform.
	 * @throws NullPointerException If <code>trans</code> is <code>null</code>.
	 */
	public static MultiDimensionFourierTransform synchronizedMultiDimensionFourierTransform(
			final MultiDimensionFourierTransform trans )
		throws NullPointerException
	{
		if( trans == null )
		{
			throw new NullPointerException();
		}

		return ( new SynchronizedMultiDimensionFourierTransform( trans ) );
	}


	/**
	 * Create a threadsafe view around the specified multi-dimensional FFT object.
	 * 
	 * @param trans the transform object.
	 * @return MultiDimensionFourierTransform a threadsafe view around the
	 *         specified transform.
	 * @throws NullPointerException If <code>trans</code> is <code>null</code>.
	 */
	public static MultiDimensionFastFourierTransform synchronizedMultiDimensionFourierTransform(
			final MultiDimensionFastFourierTransform trans )
		throws NullPointerException
	{
		if( trans == null )
		{
			throw new NullPointerException();
		}

		return ( new SynchronizedMultiDimensionFastFourierTransform( trans ) );
	}


	/**
	 * Synchronized wrapper.
	 * 
	 * @author Fran Lattanzio
	 * @version $Revision$ $Date$
	 */
	private static class SynchronizedMultiDimensionFourierTransform
		extends Object
		implements MultiDimensionFourierTransform, Serializable
	{


		/**
		 * Serial version UID.
		 */
		private static final long serialVersionUID = 82374234L;

		/**
		 * Delegate.
		 */
		private MultiDimensionFourierTransform delegate;

		/**
		 * Mutex.
		 */
		private transient Object mutex;


		/**
		 * Constructor.
		 * 
		 * @param del the delegate.
		 */
		SynchronizedMultiDimensionFourierTransform(
																								final MultiDimensionFourierTransform del )
		{
			super();

			this.delegate = del;
			this.mutex = this;
		}


		/**
		 * Backwards transform.
		 * 
		 * @param input input vector.
		 * @param dest destination vector.
		 * @throws NullPointerException If <code>input</code> or <code>dest</code>
		 *         are <code>null</code>.
		 * @throws IllegalArgumentException If <code>input</code> or
		 *         <code>dest</code> are of the wrong size.
		 */
		public void backward( final ComplexDopeMatrix input,
				final ComplexDopeMatrix dest )
			throws NullPointerException, IllegalArgumentException
		{
			synchronized( this.mutex )
			{
				this.delegate.backward( input, dest );
			}
		}


		/**
		 * Backwards transform.
		 * 
		 * @param input
		 * @param dest
		 * @throws NullPointerException If <code>input</code> or <code>dest</code>
		 *         are <code>null</code>.
		 * @throws IllegalArgumentException If <code>input</code> or
		 *         <code>dest</code> are of the wrong size.
		 */
		public void backward( final RealDopeMatrix input,
				final ComplexDopeMatrix dest )
			throws NullPointerException, IllegalArgumentException
		{
			synchronized( this.mutex )
			{
				this.delegate.backward( input, dest );
			}
		}


		/**
		 * Forward transform.
		 * 
		 * @param input input matrix.
		 * @param dest destination.
		 * @throws NullPointerException If <code>input</code> or <code>dest</code>
		 *         are <code>null</code>.
		 * @throws IllegalArgumentException If <code>input</code> or
		 *         <code>dest</code> are of the wrong size.
		 */
		public void forward( final ComplexDopeMatrix input,
				final ComplexDopeMatrix dest )
			throws NullPointerException, IllegalArgumentException
		{
			synchronized( this.mutex )
			{
				this.delegate.forward( input, dest );
			}
		}


		/**
		 * Forward transform.
		 * 
		 * @param input input matrix.
		 * @param dest output matrix.
		 * @throws NullPointerException If <code>input</code> or <code>dest</code>
		 *         are <code>null</code>.
		 * @throws IllegalArgumentException If <code>input</code> or
		 *         <code>dest</code> are of the wrong size.
		 */
		public void forward( final RealDopeMatrix input,
				final ComplexDopeMatrix dest )
			throws NullPointerException, IllegalArgumentException
		{
			synchronized( this.mutex )
			{
				this.delegate.forward( input, dest );
			}
		}


		/**
		 * Transform in the specified direction.
		 * 
		 * @param input input matrix.
		 * @param dest output matrix.
		 * @param direction direction.
		 * @throws NullPointerException If <code>input</code> or <code>dest</code>
		 *         are <code>null</code>.
		 * @throws IllegalArgumentException If <code>input</code> or
		 *         <code>dest</code> are of the wrong size.
		 */
		public void transform( final ComplexDopeMatrix input,
				final ComplexDopeMatrix dest, final TransformDirection direction )
			throws NullPointerException, IllegalArgumentException
		{
			synchronized( this.mutex )
			{
				this.delegate.transform( input, dest, direction );
			}
		}


		/**
		 * Transform in the specified direction.
		 * 
		 * @param input input matrix.
		 * @param dest output matrix.
		 * @param direction direction.
		 * @throws NullPointerException If <code>input</code> or <code>dest</code>
		 *         are <code>null</code>.
		 * @throws IllegalArgumentException If <code>input</code> or
		 *         <code>dest</code> are of the wrong size.
		 */
		public void transform( final RealDopeMatrix input,
				final ComplexDopeMatrix dest, final TransformDirection direction )
			throws NullPointerException, IllegalArgumentException
		{
			synchronized( this.mutex )
			{
				this.delegate.transform( input, dest, direction );
			}
		}


		/**
		 * Get the dimension.
		 * 
		 * @return int the dimension.
		 */
		public int getDimension()
		{
			synchronized( this.mutex )
			{
				return ( this.delegate.getDimension() );
			}
		}


		/**
		 * Get the length of the specified dimension.
		 * 
		 * @param dim the dimension.
		 * @return int the length.
		 * @throws IllegalArgumentException If <code>dim</code> is illegal.
		 */
		public int getLength( final int dim )
			throws IllegalArgumentException
		{
			synchronized( this.mutex )
			{
				return ( this.delegate.getLength( dim ) );
			}
		}


		/**
		 * Delegate equals.
		 * 
		 * @param other the object to which to compare.
		 * @return boolean true if equal.
		 */
		@Override
		public boolean equals( Object other )
		{
			synchronized( this.mutex )
			{
				if( other == this )
				{
					return ( true );
				}

				if( other == null )
				{
					return ( false );
				}

				if( other == this.delegate )
				{
					return ( true );
				}

				return ( this.delegate.equals( other ) );
			}
		}


		/**
		 * Hashcode inline with equals.
		 * 
		 * @return int hashcode.
		 */
		@Override
		public int hashCode()
		{
			synchronized( this.mutex )
			{
				return ( this.delegate.hashCode() );
			}
		}


		/**
		 * Better to string.
		 * 
		 * @return String to string.
		 */
		@Override
		public String toString()
		{
			synchronized( this.mutex )
			{
				return ( this.toString() );
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

			if( this.delegate == null )
			{
				throw new InvalidObjectException( "No backing transform." );
			}

			// Mutex is this.
			this.mutex = this;
		}


	}


	/**
	 * Synchronized wrapper for MD FFT.
	 * 
	 * @author Fran Lattanzio
	 * @version $Revision$ $Date$
	 */
	private static class SynchronizedMultiDimensionFastFourierTransform
		extends SynchronizedMultiDimensionFourierTransform
		implements MultiDimensionFourierTransform,
		MultiDimensionFastFourierTransform, Serializable
	{


		/**
		 * Serial version UID.
		 */
		private static final long serialVersionUID = 2398234L;


		/**
		 * Constructor.
		 * 
		 * @param del the delegate.
		 */
		SynchronizedMultiDimensionFastFourierTransform(
																										final MultiDimensionFastFourierTransform del )
		{
			super( del );
		}


	}


	/**
	 * No instances.
	 * 
	 * @throws InternalError always.
	 */
	private MultiDimensionFourierTransforms()
		throws InternalError
	{
		throw new InternalError();
	}


}
