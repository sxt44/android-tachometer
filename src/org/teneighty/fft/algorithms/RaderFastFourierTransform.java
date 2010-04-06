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

package org.teneighty.fft.algorithms;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.teneighty.fft.FastFourierTransform;
import org.teneighty.fft.FourierTransform;
import org.teneighty.fft.FourierTransformFactory;
import org.teneighty.fft.FourierTransformUtilities;
import org.teneighty.fft.TransformDirection;
import org.teneighty.fft.dope.BackedComplexDopeVector;
import org.teneighty.fft.dope.AbstractReIndexedComplexDopeVector;
import org.teneighty.fft.dope.RealDopeVector;
import org.teneighty.fft.dope.ComplexDopeVector;


/**
 * This class implements Rader's Fast Fourier Transform.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public strictfp class RaderFastFourierTransform
	extends AbstractFastFourierTransform
	implements FourierTransform, FastFourierTransform, Serializable
{


	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 23498347L;


	/**
	 * Size - 1 transform.
	 */
	private FourierTransform ft;

	/**
	 * The generator of the group.
	 */
	private transient int generator;

	/**
	 * Multiplicative inverse of the generator, mod length.
	 */
	private transient int generator_inv;

	/**
	 * Length - 1.
	 */
	private transient int lm1;

	/**
	 * Forward FFT'ed twiddle factors.
	 */
	private transient double[] fft_re_for, fft_im_for;

	/**
	 * Backward FFT'ed twiddle factors.
	 */
	private transient double[] fft_re_back, fft_im_back;

	/**
	 * Input permutation map.
	 */
	private transient int[] input_perm;

	/**
	 * Output permutation map.
	 */
	private transient int[] output_perm;

	/**
	 * Buffers for intermediate transforms.
	 */
	private transient BackedComplexDopeVector buffer;

	/**
	 * Happy buffers.
	 */
	private transient double[] buffer_re, buffer_im;

	/**
	 * Permutation wrapper.
	 */
	private transient PermutedComplexDopeVector perm_vec;

	/**
	 * Real permutation wrapper.
	 */
	private transient PermutedRealDopeVector perm_vec_real;

	/**
	 * Shifted complex dope.
	 */
	private transient ShiftedComplexDopeVector shifted_vec;


	/**
	 * Constructor.
	 * <p>
	 * We do not check if <code>size</code> is prime here. Passing a composite
	 * number will simply result in meaningless data.
	 * 
	 * @param size the size. Assumed to be prime.
	 * @throws IllegalArgumentException If <code>size</code> is illegal.
	 */
	public RaderFastFourierTransform( final int size )
		throws IllegalArgumentException
	{
		super( size );

		// get the generator.
		this.lm1 = size - 1;
		this.generator = FourierTransformUtilities.generator( size );

		// compute generator inverse.
		this.generator_inv = FourierTransformUtilities.inverse( this.generator, size );

		// get delegate transform.
		this.ft = FourierTransformFactory.getTransform( this.lm1 );

		// init the twiddles and whatnot.
		this.transientInit();
	}


	/**
	 * Create and initialize all the happy transient data.
	 */
	private void transientInit()
	{
		final int size = this.getLength();

		// create stuff.
		this.fft_re_for = new double[ this.lm1 ];
		this.fft_im_for = new double[ this.lm1 ];
		this.fft_re_back = new double[ this.lm1 ];
		this.fft_im_back = new double[ this.lm1 ];

		// fill in turds.
		double[] input_re = new double[ this.lm1 ];
		double[] input_im = new double[ this.lm1 ];

		int num, denom, gcd, power;
		for( int index = 0; index < this.lm1; index++ )
		{
			// compute multiplicate inverse of g**p.
			power = FourierTransformUtilities.powerMod( this.generator, index, size );
			num = FourierTransformUtilities.inverse( power, size );
			denom = size;

			// gcd 'em down.
			gcd = FourierTransformUtilities.euclid( num, denom );
			num /= gcd;
			denom /= gcd;

			// fill in bq.
			input_re[ index ] = Math.cos( 2 * Math.PI * num / denom );
			input_im[ index ] = -Math.sin( 2 * Math.PI * num / denom );
		}

		// run forward FFT over everything...
		BackedComplexDopeVector in = new BackedComplexDopeVector( input_re, input_im );
		this.ft.forward( in, new BackedComplexDopeVector( this.fft_re_for, this.fft_im_for ) );

		// now backward fft...
		for( int index = 0; index < this.lm1; index++ )
		{
			input_im[ index ] = -input_im[ index ];
		}
		this.ft.backward( in, new BackedComplexDopeVector( this.fft_re_back, this.fft_im_back ) );

		// create permutation tables.
		this.input_perm = new int[ this.lm1 ];
		this.output_perm = new int[ this.lm1 ];

		// compute the permutation tables.
		for( int index = 0; index < this.lm1; index++ )
		{
			power = FourierTransformUtilities.powerMod( this.generator, index, size );
			this.input_perm[ index ] = power;

			power = FourierTransformUtilities.powerMod( this.generator_inv, index, size );
			this.output_perm[ index ] = ( power - 1 );
		}

		// create buffers.
		this.buffer_re = new double[ this.lm1 ];
		this.buffer_im = new double[ this.lm1 ];
		this.buffer = new BackedComplexDopeVector( this.buffer_re, this.buffer_im );

		// create perm vector.
		this.perm_vec = new PermutedComplexDopeVector( this.input_perm );
		this.perm_vec_real = new PermutedRealDopeVector( this.input_perm );

		// create shitfted vector.
		this.shifted_vec = new ShiftedComplexDopeVector( this.lm1 );
	}


	/**
	 * Permuate the specified dopes, according to the specified permutation map.
	 * 
	 * @param input the input dope.
	 */
	private void permute( final ComplexDopeVector input )
	{
		for( int index = 0; index < this.lm1; index++ )
		{
			this.buffer_re[ this.output_perm[ index ] ] = input.getReal( index );
			this.buffer_im[ this.output_perm[ index ] ] = input.getImaginary( index );
		}

		for( int index = 0; index < this.lm1; index++ )
		{
			input.setReal( this.buffer_re[ index ], index );
			input.setImaginary( this.buffer_im[ index ], index );
		}
	}


	/**
	 * Normalize the specified dope.
	 * 
	 * @param input the input dope.
	 */
	private void normalize( final ComplexDopeVector input )
	{
		for( int index = 0; index < this.lm1; index++ )
		{
			this.buffer_re[ index ] = input.getReal( index ) / this.lm1;
			this.buffer_im[ index ] = input.getImaginary( index ) / this.lm1;
		}

		for( int index = 0; index < this.lm1; index++ )
		{
			input.setReal( this.buffer_re[ index ], index );
			input.setImaginary( this.buffer_im[ index ], index );
		}
	}


	/**
	 * Permuate the specified dopes, according to the specified permutation map.
	 * <p>
	 * Obviously, we're assuming the most recent backwards transform did an
	 * unnormalized transform... otherwise, this will create problems.
	 * 
	 * @param input the input dope.
	 */
	private void permuteAndNormalize( final ComplexDopeVector input )
	{
		for( int index = 0; index < this.lm1; index++ )
		{
			this.buffer_re[ this.output_perm[ index ] ] = input.getReal( index ) / this.lm1;
			this.buffer_im[ this.output_perm[ index ] ] = input.getImaginary( index ) / this.lm1;
		}

		for( int index = 0; index < this.lm1; index++ )
		{
			input.setReal( this.buffer_re[ index ], index );
			input.setImaginary( this.buffer_im[ index ], index );
		}
	}


	/**
	 * Transform the specified vectors.
	 * 
	 * @param input the vector to transform.
	 * @param dest the destination.
	 * @throws NullPointerException If <code>input</code> or <code>dest</code>
	 *         is <code>null</code>.
	 * @throws IllegalArgumentException If <code>input</code> does not have the
	 *         right length</code>, or if <code>input</code> == <code>dest</code>.
	 */
	@Override
	public void forward( final ComplexDopeVector input,
			final ComplexDopeVector dest )
		throws NullPointerException, IllegalArgumentException
	{
		this.checkVectors( input, dest );

		this.perm_vec.setDelegate( input );

		// run the forward transformation.
		this.ft.forward( this.perm_vec, this.buffer );

		// multiply aq and bq (which was pre-computed, above) point-wise.
		double a, b, c, d;
		for( int index = 0; index < this.lm1; index++ )
		{
			a = this.buffer_re[ index ];
			b = this.buffer_im[ index ];
			c = this.fft_re_for[ index ];
			d = this.fft_im_for[ index ];

			this.buffer_re[ index ] = ( a * c ) - ( b * d );
			this.buffer_im[ index ] = ( a * d ) + ( b * c );
		}

		this.shifted_vec.setDelegate( dest );

		// perform inverse transform, as per the convolution theorem.
		this.ft.backward( this.buffer, this.shifted_vec );

		// normalize the data (we assume that the backwards/inverse transform was
		// unnormalized)
		// and permute the data back into the right order.
		this.permuteAndNormalize( this.shifted_vec );

		double x0_real = input.getReal( 0 );
		double x0_imag = input.getImaginary( 0 );

		// add x0 to all Xi.
		for( int index = 0; index < this.lm1; index++ )
		{
			this.shifted_vec.setReal( this.shifted_vec.getReal( index ) + x0_real, index );
			this.shifted_vec.setImaginary( this.shifted_vec.getImaginary( index ) + x0_imag, index );
		}

		// compute x0.
		x0_real = 0;
		x0_imag = 0;
		for( int index = 0; index < this.getLength(); index++ )
		{
			x0_real += input.getReal( index );
			x0_imag += input.getImaginary( index );
		}

		// set x0.
		dest.setReal( x0_real, 0 );
		dest.setImaginary( x0_imag, 0 );
	}


	/**
	 * Perform the backward Fourier transform, on the specified vector.
	 * 
	 * @param input the vector to transform.
	 * @param dest the destination vector.
	 * @throws NullPointerException If <code>input</code> or <code>dest</code>
	 *         is <code>null</code>.
	 * @throws IllegalArgumentException If <code>input</code> does not have the
	 *         right length</code>, or if <code>input</code> == <code>dest</code>.
	 */
	@Override
	public void backward( final ComplexDopeVector input,
			final ComplexDopeVector dest )
		throws NullPointerException, IllegalArgumentException
	{
		this.checkVectors( input, dest );

		this.perm_vec.setDelegate( input );

		// run the backward transformation.
		this.ft.backward( this.perm_vec, this.buffer );

		// normalize the results.
		this.normalize( this.buffer );

		// multiply aq and bq (which was pre-computed, above) point-wise.
		double a, b, c, d;
		for( int index = 0; index < this.lm1; index++ )
		{
			a = this.buffer_re[ index ];
			b = this.buffer_im[ index ];
			c = this.fft_re_back[ index ];
			d = this.fft_im_back[ index ];

			this.buffer_re[ index ] = ( a * c ) - ( b * d );
			this.buffer_im[ index ] = ( a * d ) + ( b * c );
		}

		this.shifted_vec.setDelegate( dest );

		// perform inverse transform, as per the convolution theorem.
		this.ft.forward( this.buffer, this.shifted_vec );

		// normalize the data (we assume that the backwards/inverse transform was
		// unnormalized)
		// and permute the data back into the right order.
		this.permute( this.shifted_vec );

		double x0_real = input.getReal( 0 );
		double x0_imag = input.getImaginary( 0 );

		// add x0 to all Xi.
		for( int index = 0; index < this.lm1; index++ )
		{
			this.shifted_vec.setReal( this.shifted_vec.getReal( index ) + x0_real, index );
			this.shifted_vec.setImaginary( this.shifted_vec.getImaginary( index ) + x0_imag, index );
		}

		// compute x0.
		x0_real = 0;
		x0_imag = 0;
		for( int index = 0; index < this.getLength(); index++ )
		{
			x0_real += input.getReal( index );
			x0_imag += input.getImaginary( index );
		}

		// set x0.
		dest.setReal( x0_real, 0 );
		dest.setImaginary( x0_imag, 0 );
	}


	/**
	 * Transform the specified vectors.
	 * 
	 * @param input the vector to transform.
	 * @param dest the destination.
	 * @throws NullPointerException If <code>input</code> or <code>dest</code>
	 *         is <code>null</code>.
	 * @throws IllegalArgumentException If <code>input</code> does not have the
	 *         right length</code>, or if <code>input</code> == <code>dest</code>.
	 */
	@Override
	public void forward( final RealDopeVector input, final ComplexDopeVector dest )
		throws NullPointerException, IllegalArgumentException
	{
		this.checkVectors( input, dest );

		this.perm_vec_real.setDelegate( input );

		// run the forward transformation.
		this.ft.forward( this.perm_vec, this.buffer );

		// multiply aq and bq (which was pre-computed, above) point-wise.
		double a, b, c, d;
		for( int index = 0; index < this.lm1; index++ )
		{
			a = this.buffer_re[ index ];
			b = this.buffer_im[ index ];
			c = this.fft_re_for[ index ];
			d = this.fft_im_for[ index ];

			this.buffer_re[ index ] = ( a * c ) - ( b * d );
			this.buffer_im[ index ] = ( a * d ) + ( b * c );
		}

		this.shifted_vec.setDelegate( dest );

		// perform inverse transform, as per the convolution theorem.
		this.ft.backward( this.buffer, this.shifted_vec );

		// normalize the data (we assume that the backwards/inverse transform was
		// unnormalized)
		// and permute the data back into the right order.
		this.permuteAndNormalize( this.shifted_vec );

		double x0_real = input.getReal( 0 );

		// add x0 to all Xi.
		for( int index = 0; index < this.lm1; index++ )
		{
			this.shifted_vec.setReal( this.shifted_vec.getReal( index ) + x0_real, index );
		}

		// compute x0.
		x0_real = 0;
		for( int index = 0; index < this.getLength(); index++ )
		{
			x0_real += input.getReal( index );
		}

		// set x0.
		dest.setReal( x0_real, 0 );
		dest.setImaginary( 0, 0 );
	}


	/**
	 * Perform the backward Fourier transform, on the specified vector.
	 * 
	 * @param input the vector to transform.
	 * @param dest the destination vector.
	 * @throws NullPointerException If <code>input</code> or <code>dest</code>
	 *         is <code>null</code>.
	 * @throws IllegalArgumentException If <code>input</code> does not have the
	 *         right length</code>, or if <code>input</code> == <code>dest</code>.
	 */
	@Override
	public void backward( final RealDopeVector input, final ComplexDopeVector dest )
		throws NullPointerException, IllegalArgumentException
	{
		this.checkVectors( input, dest );

		this.perm_vec_real.setDelegate( input );

		// run the backward transformation.
		this.ft.backward( this.perm_vec, this.buffer );

		// normalize the results.
		this.normalize( this.buffer );

		// multiply aq and bq (which was pre-computed, above) point-wise.
		double a, b, c, d;
		for( int index = 0; index < this.lm1; index++ )
		{
			a = this.buffer_re[ index ];
			b = this.buffer_im[ index ];
			c = this.fft_re_back[ index ];
			d = this.fft_im_back[ index ];

			this.buffer_re[ index ] = ( a * c ) - ( b * d );
			this.buffer_im[ index ] = ( a * d ) + ( b * c );
		}

		this.shifted_vec.setDelegate( dest );

		// perform inverse transform, as per the convolution theorem.
		this.ft.forward( this.buffer, this.shifted_vec );

		// normalize the data (we assume that the backwards/inverse transform was
		// unnormalized)
		// and permute the data back into the right order.
		this.permute( this.shifted_vec );

		double x0_real = input.getReal( 0 );

		// add x0 to all Xi.
		for( int index = 0; index < this.lm1; index++ )
		{
			this.shifted_vec.setReal( this.shifted_vec.getReal( index ) + x0_real, index );
		}

		// compute x0.
		x0_real = 0;
		for( int index = 0; index < this.getLength(); index++ )
		{
			x0_real += input.getReal( index );
		}

		// set x0.
		dest.setReal( x0_real, 0 );
		dest.setImaginary( 0, 0 );
	}


	/**
	 * Transform complex data in the specified direction.
	 * 
	 * @param input the vector to transform.
	 * @param dest the destination.
	 * @param direction the direction.
	 * @throws NullPointerException If <code>input</code> or <code>dest</code>
	 *         is <code>null</code>.
	 * @throws IllegalArgumentException If <code>input</code> does not have the
	 *         right length</code>, or if <code>input</code> == <code>dest</code>.
	 */
	public void transform( final ComplexDopeVector input,
			final ComplexDopeVector dest, final TransformDirection direction )
		throws NullPointerException, IllegalArgumentException
	{
		this.checkDirection( direction );

		if( direction == TransformDirection.FORWARD )
		{
			this.forward( input, dest );
		}
		else
		{
			this.backward( input, dest );
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
	 * @throws IllegalArgumentException If <code>input</code> does not have the
	 *         right length</code>, or if <code>input</code> == <code>dest</code>.
	 */
	public void transform( final RealDopeVector input,
			final ComplexDopeVector dest, final TransformDirection direction )
		throws NullPointerException, IllegalArgumentException
	{
		this.checkDirection( direction );

		if( direction == TransformDirection.FORWARD )
		{
			this.forward( input, dest );
		}
		else
		{
			this.backward( input, dest );
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

		// compute various happy but very important numbers.
		this.lm1 = ( this.getLength() - 1 );
		this.generator = FourierTransformUtilities.generator( this.getLength() );
		this.generator_inv = FourierTransformUtilities.inverse( this.generator, this.getLength() );

		// init transient fields.
		this.transientInit();
	}


	/**
	 * Creates a +1 shifted view around the backed dope vector.
	 * 
	 * @author Fran Lattanzio
	 * @version $Revision$ $Date$
	 */
	private static final class ShiftedComplexDopeVector
		extends AbstractReIndexedComplexDopeVector
		implements ComplexDopeVector
	{


		/**
		 * Length.
		 */
		private int length;


		/**
		 * Constructor.
		 * 
		 * @param length the length of this vector.
		 */
		private ShiftedComplexDopeVector( final int length )
		{
			super();

			this.length = length;
		}


		/**
		 * Re-index.
		 * 
		 * @param index the index to re-index.
		 * @return int the re-indexed index.
		 */
		@Override
		protected int reIndex( final int index )
		{
			return ( index + 1 );
		}


		/**
		 * Get length.
		 * 
		 * @return int the length.
		 */
		@Override
		public int getLength()
		{
			return ( this.length );
		}


	}


}
