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
import java.util.List;

import org.teneighty.fft.FastFourierTransform;
import org.teneighty.fft.FourierTransform;
import org.teneighty.fft.FourierTransformFactory;
import org.teneighty.fft.FourierTransformUtilities;
import org.teneighty.fft.TransformDirection;
import org.teneighty.fft.dope.AbstractReIndexedComplexDopeVector;
import org.teneighty.fft.dope.AbstractReIndexedRealDopeVector;
import org.teneighty.fft.dope.ComplexDopeVector;
import org.teneighty.fft.dope.RealDopeVector;
import org.teneighty.fft.factor.Factorization.Factor;
import org.teneighty.fft.factor.DefaultFactorization;


/**
 * This class implements the Cooley-Tukey fast fourier transform for arbitrary
 * radices. This class delegates back to the factory to get good implementations
 * for the two sizes.
 * <p>
 * You should not use this class for pure powers of two; use the specially
 * written class instead.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public strictfp class CooleyTukeyFastFourierTransform
	extends AbstractFastFourierTransform
	implements FourierTransform, FastFourierTransform, Serializable
{


	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 32847343L;


	/**
	 * N1.
	 */
	private int cap_n1;

	/**
	 * N2.
	 */
	private int cap_n2;

	/**
	 * N1 size tranform.
	 */
	private FourierTransform n1_transform;

	/**
	 * N2 size transform.
	 */
	private FourierTransform n2_transform;

	/**
	 * The arrays in which we will temporarily store everything, during the
	 * comp_inner transform.
	 */
	private transient double[][] re, im;

	/**
	 * Twiddle factors.
	 */
	private transient double[][] twiddle_re, twiddle_im_for, twiddle_im_bak;

	/**
	 * Column major dope wrapper.
	 */
	private transient ColumnMajorComplexDopeVector col;

	/**
	 * Row major dope wrapper.
	 */
	private transient RowMajorComplexDopeVector row;

	/**
	 * Complex comp_inner re-indexer.
	 */
	private transient CooleyTukeyReIndexedComplexDopeVector comp_inner;

	/**
	 * Complex comp_inner re-indexer.
	 */
	private transient CooleyTukeyReIndexedRealDopeVector real_inner;

	/**
	 * Outer re-indexer.
	 */
	private transient CooleyTukeyReIndexedComplexDopeVector outer;


	/**
	 * Constructor.
	 * 
	 * @param fset1 the first set of factors.
	 * @param fset2 the second set of factors.
	 * @param size the size.
	 * @throws NullPointerException If <code>fset1</code> or <code>fset2</code>
	 *         is <code>null</code>.
	 * @throws IllegalArgumentException If there's something wrong with the factor
	 *         sets.
	 */
	public CooleyTukeyFastFourierTransform( final List<Factor> fset1,
																					final List<Factor> fset2,
																					final int size )
		throws NullPointerException, IllegalArgumentException
	{
		super( size ); // me.

		if( fset1 == null || fset2 == null )
		{
			throw new NullPointerException();
		}

		if( fset1.size() == 0 || fset2.size() == 0 )
		{
			throw new IllegalArgumentException();
		}

		this.cap_n1 = 1;
		this.cap_n2 = 1;

		for( int index = 0; index < fset1.size(); index++ )
		{
			this.cap_n1 *= fset1.get( index ).getTotal();
		}

		for( int index = 0; index < fset2.size(); index++ )
		{
			this.cap_n2 *= fset2.get( index ).getTotal();
		}

		if( size != ( this.cap_n1 * this.cap_n2 ) )
		{
			throw new IllegalArgumentException();
		}

		// get some delegate transforms.
		this.n1_transform = FourierTransformFactory.getTransform( new DefaultFactorization( fset1, this.cap_n1 ) );
		this.n2_transform = FourierTransformFactory.getTransform( new DefaultFactorization( fset2, this.cap_n2 ) );

		// init transient helper fields.
		this.transientInit();
	}


	/**
	 * Initialize transient fields.
	 */
	private void transientInit()
	{
		// create happy matrices.
		this.re = new double[ this.cap_n1 ][ this.cap_n2 ];
		this.im = new double[ this.cap_n1 ][ this.cap_n2 ];

		// create dopes around matrices.
		this.col = new ColumnMajorComplexDopeVector( this.re, this.im );
		this.row = new RowMajorComplexDopeVector( this.re, this.im );

		// create re-indexers.
		this.comp_inner = new CooleyTukeyReIndexedComplexDopeVector( this.cap_n1, this.cap_n2 );
		this.real_inner = new CooleyTukeyReIndexedRealDopeVector( this.cap_n1, this.cap_n2 );
		this.outer = new CooleyTukeyReIndexedComplexDopeVector( this.cap_n2, this.cap_n1 );

		// create twiddle factors and such.
		this.createTwiddles();
	}


	/**
	 * Create the twiddle factors.
	 */
	private void createTwiddles()
	{
		// instantiate it!
		this.twiddle_re = new double[ this.cap_n1 ][ this.cap_n2 ];
		this.twiddle_im_for = new double[ this.cap_n1 ][ this.cap_n2 ];
		this.twiddle_im_bak = new double[ this.cap_n1 ][ this.cap_n2 ];

		// dumb temporary variable.
		final int length = this.getLength();

		// useful vars.
		int num = 0;
		int n1 = 0;

		// gcd down the numerator and denominator.
		int gcd_num = 0;
		int gcd_den = 0;
		int gcd = 0;

		for( int k2 = 0; k2 < this.cap_n2; k2++ )
		{
			for( n1 = 0; n1 < this.cap_n1; n1++ )
			{
				// compute numerator.
				num = 2 * n1 * k2;

				// gcd 'em down, for numerical stability (the theory being sin and cos
				// can be more accurately computed with smaller numbers.
				gcd = FourierTransformUtilities.euclid( num, length );
				gcd_num = num / gcd;
				gcd_den = length / gcd;

				// eval and store twiddle factors.
				this.twiddle_re[ n1 ][ k2 ] = Math.cos( Math.PI * gcd_num / gcd_den );
				this.twiddle_im_bak[ n1 ][ k2 ] = Math.sin( Math.PI * gcd_num / gcd_den );
				this.twiddle_im_for[ n1 ][ k2 ] = -this.twiddle_im_bak[ n1 ][ k2 ];
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

		this.comp_inner.setDelegate( input );
		this.outer.setDelegate( dest );

		for( int index = 0; index < this.cap_n1; index++ )
		{
			this.col.setRow( index );
			this.comp_inner.setK( index );
			this.n2_transform.transform( this.comp_inner, this.col, dir );
		}

		this.finishTransform( dir );
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
		
		this.real_inner.setDelegate( input );
		this.outer.setDelegate( dest );

		for( int index = 0; index < this.cap_n1; index++ )
		{
			this.col.setRow( index );
			this.comp_inner.setK( index );
			this.n2_transform.transform( this.comp_inner, this.col, dir );
		}

		this.finishTransform( dir );
	}


	/**
	 * Finish the transform.
	 * 
	 * @param direction the direction.
	 */
	private void finishTransform( final TransformDirection direction )
	{
		// multiply by twiddle factors and such.
		this.twiddle( direction );

			for( int index = 0; index < this.cap_n2; index++ )
			{
				this.row.setColumn( index );
				this.outer.setK( index );
				this.n1_transform.transform( this.row, this.outer, direction );
			}

		this.comp_inner.setDelegate( null );
		this.outer.setDelegate( null );
	}


	/**
	 * Multiply by the twiddle factors in the specified direction.
	 * 
	 * @param direction the direction.
	 */
	private void twiddle( final TransformDirection direction )
	{
		double a, b, c, d;

		double[][] real = this.twiddle_re;
		double[][] imag = ( direction == TransformDirection.FORWARD ? this.twiddle_im_for : this.twiddle_im_bak );

		for( int index = 0; index < this.cap_n1; index++ )
		{
			for( int jindex = 0; jindex < this.cap_n2; jindex++ )
			{
				a = this.re[ index ][ jindex ];
				b = this.im[ index ][ jindex ];

				c = real[ index ][ jindex ];
				d = imag[ index ][ jindex ];

				// point-wise multiply.
				this.re[ index ][ jindex ] = ( a * c ) - ( b * d );
				this.im[ index ][ jindex ] = ( a * d ) + ( b * c );
			}
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

		// init transient fields.
		this.transientInit();
	}


	/**
	 * Cooley-Tukey style reindexer.
	 * <p>
	 * Reindexes in the form N*index + column
	 * 
	 * @author Fran Lattanzio
	 * @version $Revision$ $Date$
	 */
	private static final class CooleyTukeyReIndexedComplexDopeVector
		extends AbstractReIndexedComplexDopeVector
		implements ComplexDopeVector
	{


		/**
		 * N
		 */
		private int n;

		/**
		 * column.
		 */
		private int k;

		/**
		 * The size.
		 */
		private int size;


		/**
		 * Constructor.
		 * 
		 * @param n good old N1.
		 * @param size the size.
		 */
		CooleyTukeyReIndexedComplexDopeVector( final int n, final int size )
		{
			super();

			// store stuff.
			this.n = n;
			this.size = size;
		}


		/**
		 * Get column.
		 * 
		 * @return int column.
		 */
		int getK()
		{
			return ( this.k );
		}


		/**
		 * Set column.
		 * 
		 * @param k the new column value.
		 */
		void setK( final int k )
		{
			this.k = k;
		}


		/**
		 * Compute the specified reindexing.
		 * <p>
		 * Ideally, we might want to compute this in advance since * and % might be
		 * a little expensive to do so often.
		 * <p>
		 * Another possibility is to inline this (the old fashioned way, by hand,
		 * since Java doesn't support re inlining).
		 * 
		 * @param index the index.
		 * @return int the CRT reindexed index.
		 */
		@Override
		protected int reIndex( final int index )
		{
			return ( ( this.n * index ) + this.k );
		}


		/**
		 * Get the length.
		 * 
		 * @return int the length.
		 */
		@Override
		public int getLength()
		{
			return ( this.size );
		}


	}


	/**
	 * Cooley-Tukey style reindexer.
	 * <p>
	 * Reindexes in the form N*index + column
	 * 
	 * @author Fran Lattanzio
	 * @version $Revision$ $Date$
	 */
	private static final class CooleyTukeyReIndexedRealDopeVector
		extends AbstractReIndexedRealDopeVector
		implements RealDopeVector
	{


		/**
		 * N
		 */
		private int n;

		/**
		 * column.
		 */
		private int k;

		/**
		 * The size.
		 */
		private int size;


		/**
		 * Constructor.
		 * 
		 * @param n good old N1.
		 * @param size the size.
		 */
		CooleyTukeyReIndexedRealDopeVector( final int n, final int size )
		{
			super();

			// store stuff.
			this.n = n;
			this.size = size;
		}


		/**
		 * Get column.
		 * 
		 * @return int column.
		 */
		int getK()
		{
			return ( this.k );
		}


		/**
		 * Set k.
		 * 
		 * @param k new k value.
		 */
		void setK( final int k )
		{
			this.k = k;
		}


		/**
		 * Compute the specified reindexing.
		 * <p>
		 * Ideally, we might want to compute this in advance since * and % might be
		 * a little expensive to do so often.
		 * <p>
		 * Another possibility is to inline this (the old fashioned way, by hand,
		 * since Java doesn't support re inlining).
		 * 
		 * @param index the index.
		 * @return int the CRT reindexed index.
		 */
		@Override
		protected int reIndex( final int index )
		{
			return ( ( this.n * index ) + this.k );
		}


		/**
		 * Get the length.
		 * 
		 * @return int the length.
		 */
		@Override
		public int getLength()
		{
			return ( this.size );
		}


	}


}
