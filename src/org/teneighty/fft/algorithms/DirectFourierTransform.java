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

import org.teneighty.fft.FourierTransform;
import org.teneighty.fft.FourierTransformUtilities;
import org.teneighty.fft.TransformDirection;
import org.teneighty.fft.dope.ComplexDopeVector;
import org.teneighty.fft.dope.RealDopeVector;


/**
 * A class that computes the discrete Fourier transform directly, for any given size.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public strictfp class DirectFourierTransform
	extends AbstractFourierTransform
	implements FourierTransform, Serializable
{


	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 3282348L;

	
	/**
	 * Real wavetable.
	 */
	private transient double[] re_wavetable;
	
	/**
	 * Forward imaginary wavetable.
	 */
	private transient double[] im_wavetable_for;
	
	/**
	 * Backwards imaginary wavetable.
	 */
	private transient double[] im_wavetable_bak;


	/**
	 * Constructor.
	 * 
	 * @param size the size.
	 * @throws IllegalArgumentException If <code>size</code> is illegal.
	 */
	public DirectFourierTransform( final int size )
		throws IllegalArgumentException
	{
		super( size );

		// Create auxiliary array.
		this.createWaveTable();
	}


	/**
	 * Create the wave table.
	 * <p>
	 * Calling this method multiple times will have no real effect (other than
	 * to recreate the wavetable multiple times).
	 */
	private void createWaveTable()
	{
		// helper var.
		final int length = this.getLength();
		
		this.re_wavetable = new double[ ( length * 2 ) ];
		this.im_wavetable_for = new double[ ( length * 2 ) ];
		this.im_wavetable_bak = new double[ ( length * 2 ) ];

		// useful vars.
		int num_help = 0;
		int num = 0;
		int n = 0;
		
		// gcd down the numerator and denominator.
		int gcd_num = 0;
		int gcd_den = 0;
		int gcd = 0;

		for( int k = 0; k < length; k++)
		{

			// compute new num help.
			num_help = 2 * k;

			for( n = 0; n < length; n++)
			{
				// compute numerator.
				num = num_help * n;

				// mod fraction (num/denom) down to between [0, 2).
				num %= ( 2 * length );
				
				// gcd'em down, for numerical stability (the theory being sin and cos
				// can be more accurately computed with smaller numbers.
				gcd = FourierTransformUtilities.euclid( num, length );
				gcd_num = num / gcd;
				gcd_den = length / gcd;

				// store sin and cos values in wave table.
				this.re_wavetable[ num ] = Math.cos( Math.PI * gcd_num / gcd_den );
				this.im_wavetable_bak[ num ] = Math.sin( Math.PI * gcd_num / gcd_den );
				this.im_wavetable_for[ num ] = -this.im_wavetable_bak[ num ];
			}
		}

	}

	
	/**
	 * This method transforms the input dope in the specified direction.
	 * 
	 * @param input the input.
	 * @param dest the destination.
	 * @param dir the direction.
	 * @throws NullPointerException If <code>input</code> or <code>dest</code>
	 *         is <code>null</code>.
	 * @throws IllegalArgumentException If <code>input</code> does not have the
	 *         right length</code>, or if <code>input</code> == <code>dest</code>.
	 */
	public void transform( final ComplexDopeVector input,
														final ComplexDopeVector dest,
														final TransformDirection dir )
	{
		this.checkVectors( input, dest );
		this.checkDirection( dir );

		// the numbers xk = a + bi
		double a = 0;
		double b = 0;

		// Xk = re + imi
		double re = 0;
		double im = 0;

		// n of inner summa.
		int n = 0;
		int num = 0;
		int num_help = 0;

		// sin and cos helpers.
		double sin = 0;
		double cos = 0;

		// length of the transform/N.
		final int length = this.getLength();
		
		// choose appropiate imaginary wavetable.
		double[] im_wavetable = ( dir == TransformDirection.FORWARD ? this.im_wavetable_for : this.im_wavetable_bak );

		for( int k = 0; k < length; k++)
		{
			// clear re and im.
			re = 0;
			im = 0;

			// compute new num help.
			num_help = 2 * k;

			for( n = 0; n < length; n++)
			{
				a = input.getReal( n );
				b = input.getImaginary( n );

				if( n == 0 || k == 0 )
				{
					// easy case!
					re += a;
					im += b;
					continue;
				}

				// compute numerator.
				num = num_help * n;

				// mod fraction (num/length) down to between [0, 2 * denom).
				num %= ( 2 * length );

				// easy case recheck.
				if( num == 0 )
				{
					re += a;
					im += b;
					continue;
				}

				// pull from wavetables.
				cos = this.re_wavetable[ num ];
				sin = im_wavetable[ num ];

				// update real and imaginary pieces.
				re += ( a * cos ) - ( b * sin );
				im += ( a * sin ) + ( b * cos );
			}

			// Store in aux dope.
			dest.setReal( re, k );
			dest.setImaginary( im, k );
		}

	}
	
	
	/**
	 * This method transforms the input dope in the specified direction.
	 * 
	 * @param input the input.
	 * @param dest the destination.
	 * @param dir the direction.
	 * @throws NullPointerException If <code>input</code> or <code>dest</code>
	 *         is <code>null</code>.
	 * @throws IllegalArgumentException If <code>input</code> does not have the
	 *         right length</code>, or if <code>input</code> == <code>dest</code>.
	 */
	public void transform( final RealDopeVector input,
														final ComplexDopeVector dest,
														final TransformDirection dir )
	{
		this.checkVectors( input, dest );
		this.checkDirection( dir );

		// the numbers xk = a
		double a = 0;

		// Xk = re + imi
		double re = 0;
		double im = 0;

		// n of inner summa.
		int n = 0;
		int num = 0;
		int num_help = 0;

		// sin and cos helpers.
		double sin = 0;
		double cos = 0;

		// length of the transform/N.
		final int length = this.getLength();
		
		// imaginary wavetable.
		double[] im_wavetable = ( dir == TransformDirection.FORWARD ? this.im_wavetable_for : this.im_wavetable_bak );

		for( int k = 0; k < length; k++)
		{
			// clear re and im.
			re = 0;
			im = 0;

			// compute new num help.
			num_help = 2 * k;

			for( n = 0; n < length; n++)
			{
				a = input.getReal( n );

				if( n == 0 || k == 0 )
				{
					// easy case!
					re += a;
					continue;
				}

				// compute numerator.
				num = num_help * n;

				// mod fraction (num/denom) down to between [0, 2).
				num %= ( 2 * length );

				// easy case recheck.
				if( num == 0 )
				{
					re += a;
					continue;
				}

				// pull from wavetable.
				cos = this.re_wavetable[ num ];
				sin = im_wavetable[ num ];

				// update real and imaginary pieces.
				re += ( a * cos );
				im += ( a * sin );
			}

			// Store in aux dope.
			dest.setReal( re, k );
			dest.setImaginary( im, k );
		}

	}


	/**
	 * Serialize the object to the specified output stream.
	 * 
	 * @param out the stream to which to serialize this object.
	 * @throws IOException If this object cannot be serialized.
	 */
	private void writeObject( final ObjectOutputStream out )
		throws IOException
	{
		// Write non-transient fields.
		out.defaultWriteObject();
	}


	/**
	 * Deserialize and restore this object from the specified stream.
	 * 
	 * @param in the stream from which to read data.
	 * @throws IOException If this object cannot properly read from the specified
	 *         stream.
	 * @throws ClassNotFoundException If deserialization tries to classload an
	 *         undefined class.
	 */
	private void readObject( final ObjectInputStream in )
		throws IOException, ClassNotFoundException
	{
		// Read non-transient fields.
		in.defaultReadObject();
		
		// create wave table and such...
		this.createWaveTable();
	}


}
