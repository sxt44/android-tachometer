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
import org.teneighty.fft.TransformDirection;
import org.teneighty.fft.dope.ComplexDopeVector;
import org.teneighty.fft.dope.RealDopeVector;


/**
 * A special, optimized Cooley-Tukey for powers of two ONLY!
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public strictfp class CooleyTukeyRadixTwoFastFourierTransform
	extends AbstractFastFourierTransform
	implements FourierTransform, FastFourierTransform, Serializable
{


	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 38473723L;


	/**
	 * Real (cos) wavetable.
	 */
	private transient double[][] real_wavetable;

	/**
	 * Forward imaginary wavetable.
	 */
	private transient double[][] for_im_wavetable;

	/**
	 * Backward imaginary wavetable.
	 */
	private transient double[][] bak_im_wavetable;


	/**
	 * Constructor.
	 * 
	 * @param size the size.
	 * @throws IllegalArgumentException If <code>size</code> is not a power of
	 *         two.
	 */
	public CooleyTukeyRadixTwoFastFourierTransform( final int size )
		throws IllegalArgumentException
	{
		super( size );

		if( ( size & ( size - 1 ) ) != 0 )
		{
			throw new IllegalArgumentException();
		}

		this.createWaveTables();
	}


	/**
	 * Create the wave tables.
	 */
	private void createWaveTables()
	{
		final int size = this.getLength();

		int table_size = (int) ( Math.log( size ) / Math.log( 2 ) );

		// create lame wavetables.
		this.real_wavetable = new double[ table_size ][ size ];
		this.for_im_wavetable = new double[ table_size ][ size ];
		this.bak_im_wavetable = new double[ table_size ][ size ];

		// loop vars
		int mmax, istep, m;
		int log;
		
		// some temp doubles.
		double w, wr, wi, delta;

		for( log = 0, mmax = 1, istep = 2 * mmax; mmax < size; mmax = istep, istep = 2 * mmax, log += 1 )
		{
			delta = Math.PI / mmax;
			for( m = 0; m < mmax; ++m )
			{
				w = m * delta;
				wr = Math.cos( w );
				wi = Math.sin( w );

				// store stuff in wavetable...
				this.real_wavetable[ log ][ m ] = wr;
				this.for_im_wavetable[ log ][ m ] = -wi;
				this.bak_im_wavetable[ log ][ m ] = wi;
			}
		}
	}

	
	/**
	 * Actually compute the CT-FFT, radix 2, for complex data.
	 * 
	 * @param input the input vector.
	 * @param dest the output vector.
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
		this.checkVectors( input, dest );
		this.checkDirection( direction );

		// happy local vars.
		final int n = this.getLength();
		int i, j, m;

		for( i = j = 0; i < n; ++i )
		{
			if( j >= i )
			{
				// bit reversal into the output array.
				dest.setReal( input.getReal( i ), j );
				dest.setImaginary( input.getImaginary( i ), j );

				dest.setReal( input.getReal( j ), i );
				dest.setImaginary( input.getImaginary( j ), i );
			}

			m = n / 2;

			while ( ( m >= 1 ) && ( j >= m ) )
			{
				j -= m;
				m /= 2;
			}
			j += m;
		}

		// loop vars
		int mmax, istep, log;

		// temporary real and imaginary turds.
		double re_t, im_t;

		// wave table values.
		double re_w, im_w;

		// happy vars to hit the dope with fewer method calls.
		double re_i, im_i, re_j, im_j;

		// get wavetables.
		double[][] real_wave = this.real_wavetable;
		double[][] im_wave = ( direction == TransformDirection.FORWARD ? this.for_im_wavetable : this.bak_im_wavetable );
				
		for( log = 0, mmax = 1, istep = 2 * mmax; mmax < n; mmax = istep, istep = 2 * mmax, log += 1 )
		{
			for( m = 0; m < mmax; m++ )
			{
				// pull up stuff from wave table.
				re_w = real_wave[ log ][ m ];
				im_w = im_wave[ log ][ m ];

				for( i = m; i < n; i += istep )
				{					
					j = i + mmax;
          
					// get j stuff.
					re_j = dest.getReal( j );
					im_j = dest.getImaginary( j );

					// get i stuff.
					re_i = dest.getReal( i );
					im_i = dest.getImaginary( i );

					// compute temporary real and imaginary parts.
					re_t = ( re_w * re_j ) - ( im_w * im_j );
					im_t = ( re_w * im_j ) + ( im_w * re_j );

					// compute new j values.
					re_j = re_i - re_t;
					im_j = im_i - im_t;

					// compute new i values.
					re_i += re_t;
					im_i += im_t;

					// store everything back in the dope.
					dest.setReal( re_j, j );
					dest.setImaginary( im_j, j );
					dest.setReal( re_i, i );
					dest.setImaginary( im_i, i );
				}
			}
		}	
	}
	

	/**
	 * Actually compute the CT-FFT, radix 2, for real data.
	 * 
	 * @param input the input vector.
	 * @param dest the output vector.
	 * @param direction the direction.
	 * @throws NullPointerException If <code>input</code> or <code>dest</code>
	 *         is <code>null</code>.
	 * @throws IllegalArgumentException If <code>input</code> does not have the
	 *         right length</code>, or if <code>input</code> == <code>dest</code>.
	 */
	public void transform( final RealDopeVector input,
													final ComplexDopeVector dest,
													final TransformDirection direction )
		throws NullPointerException, IllegalArgumentException
	{
		this.checkVectors( input, dest );

		// happy local vars.
		final int n = this.getLength();
		int i, j, m;

		for( i = j = 0; i < n; ++i )
		{
			if( j >= i )
			{
				// bit reversal into the output array.
				dest.setReal( input.getReal( i ), j );
				dest.setReal( input.getReal( j ), i );
			}

			m = n / 2;

			while ( ( m >= 1 ) && ( j >= m ) )
			{
				j -= m;
				m /= 2;
			}
			j += m;
		}

		// loop vars
		int mmax, istep, log;

		// temporary real and imaginary turds.
		double re_t, im_t;

		// wave table values.
		double re_w, im_w;

		// happy vars to hit the dope with fewer method calls.
		double re_i, im_i, re_j, im_j;

		// get wavetables.
		double[][] real_wave = this.real_wavetable;
		double[][] im_wave = ( direction == TransformDirection.FORWARD ? this.for_im_wavetable : this.bak_im_wavetable );

		for( log = 0, mmax = 1, istep = 2 * mmax; mmax < n; mmax = istep, istep = 2 * mmax, log += 1 )
		{
			for( m = 0; m < mmax; ++m )
			{

				// pull up stuff from wave table.
				re_w = real_wave[ log ][ m ];
				im_w = im_wave[ log ][ m ];

				for( i = m; i < n; i += istep )
				{
					j = i + mmax;

					// get j stuff.
					re_j = dest.getReal( j );
					im_j = 0;

					// get i stuff.
					re_i = dest.getReal( i );
					im_i = 0;

					// compute temporary real and imaginary parts.
					re_t = ( re_w * re_j );
					im_t = ( im_w * re_j );

					// compute new j values.
					re_j = re_i - re_t;
					im_j = im_t;

					// compute new i values.
					re_i += re_t;
					im_i += im_t;

					// store everything back in the dope.
					dest.setReal( re_j, j );
					dest.setImaginary( im_j, j );
					dest.setReal( re_i, i );
					dest.setImaginary( im_i, i );
				}
			}
		}
		
	}

	
	/**
	 * Serialize this object to the specified stream.
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

		// create wave tables.
		this.createWaveTables();
	}


}
