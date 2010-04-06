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
import org.teneighty.fft.factor.DefaultFactorization;
import org.teneighty.fft.factor.Factorization.Factor;


/**
 * This class implements the Prime-Factor FFT.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public strictfp class PrimeFactorFastFourierTransform
	extends AbstractFastFourierTransform
	implements Serializable, FourierTransform, FastFourierTransform
{


	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 2874572345L;


	/**
	 * N1 in the PFA formula.
	 */
	private int cap_n1;

	/**
	 * N2 in PFA.
	 */
	private int cap_n2;

	/**
	 * N1<sup>-1</sup>
	 */
	private int cap_n1_inv;

	/**
	 * N2<sup>-1</sup>.
	 */
	private int cap_n2_inv;

	/**
	 * Size N1 transform.
	 */
	private FourierTransform n1_transform;

	/**
	 * Size N2 transform.
	 */
	private FourierTransform n2_transform;

	/**
	 * The arrays in which we will temporarily store everything, during the
	 * inner_comp transform.
	 */
	private transient double[][] re, im;

	/**
	 * Column major dope wrapper.
	 */
	private transient ColumnMajorComplexDopeVector col;

	/**
	 * Row major dope wrapper.
	 */
	private transient RowMajorComplexDopeVector row;

	/**
	 * Inner CRT re-indexer
	 */
	private transient InnerCrtComplexDopeVector inner_comp;

	/**
	 * Inner CRT re-indexer
	 */
	private transient InnerCrtRealDopeVector inner_real;

	/**
	 * Outer CRT re-indexer.
	 */
	private transient OuterCrtComplexDopeVector outer;


	/**
	 * Constructor.
	 * 
	 * @param fac1 the first set of factors.
	 * @param fac2 the second set of factors.
	 * @param size the size.
	 * @throws IllegalArgumentException If <code>size</code> is less than 1, or
	 *         fac1 and fac2 are illegal in some way (e.g. they're not relatively
	 *         prime).
	 * @throws NullPointerException If <code>fac1</code> or <code>fac2</code>
	 *         are <code>null</code>.
	 */
	public PrimeFactorFastFourierTransform( final List<Factor> fac1,
																					final List<Factor> fac2,
																					final int size )
		throws IllegalArgumentException, NullPointerException
	{
		super( size );

		if( fac1 == null || fac2 == null )
		{
			throw new NullPointerException();
		}

		if( fac1.isEmpty() == true || fac2.isEmpty() == true )
		{
			throw new IllegalArgumentException();
		}

		// init ns.
		this.cap_n1 = 1;
		this.cap_n2 = 1;

		for( int index = 0; index < fac1.size(); index++ )
		{
			this.cap_n1 *= fac1.get( index ).getTotal();
		}

		for( int index = 0; index < fac2.size(); index++ )
		{
			this.cap_n2 *= fac2.get( index ).getTotal();
		}

		if( size != ( this.cap_n1 * this.cap_n2 ) )
		{
			// check size.
			throw new IllegalArgumentException();
		}

		if( FourierTransformUtilities.euclid( this.cap_n1, this.cap_n2 ) != 1 )
		{
			throw new IllegalArgumentException();
		}

		// compute mulitpicative inverses!
		this.cap_n1_inv = FourierTransformUtilities.inverse( this.cap_n1, this.cap_n2 );
		this.cap_n2_inv = FourierTransformUtilities.inverse( this.cap_n2, this.cap_n1 );

		// create sub transforms.
		this.n1_transform = FourierTransformFactory.getTransform( new DefaultFactorization( fac1, this.cap_n1 ) );
		this.n2_transform = FourierTransformFactory.getTransform( new DefaultFactorization( fac2, this.cap_n2 ) );

		// initialize dumb transient stuff.
		this.transientInit();
	}


	/**
	 * Create the transient stuff.
	 */
	private void transientInit()
	{
		// create happy matrices.
		this.re = new double[ this.cap_n1 ][ this.cap_n2 ];
		this.im = new double[ this.cap_n1 ][ this.cap_n2 ];

		// create dopes around matrices.
		this.col = new ColumnMajorComplexDopeVector( this.re, this.im );
		this.row = new RowMajorComplexDopeVector( this.re, this.im );

		// create inner_comp and outer vectors.
		this.inner_comp = new InnerCrtComplexDopeVector( this.cap_n1, this.cap_n2 );
		this.inner_real = new InnerCrtRealDopeVector( this.cap_n1, this.cap_n2 );
		this.outer = new OuterCrtComplexDopeVector( this.cap_n1, this.cap_n2, this.cap_n1_inv, this.cap_n2_inv );
	}


	/**
	 * Transform the specified vector, in place.
	 * 
	 * @param input the vector to transform.
	 * @param dest the destination.
	 * @param dir the direction.
	 * @throws NullPointerException If <code>input</code> or <code>dest</code>
	 *         is <code>null</code>.
	 * @throws IllegalArgumentException If <code>input</code> does not have the
	 *         right length</code>, or if <code>input</code> == <code>dest</code>.
	 */
	public void transform( final ComplexDopeVector input,
			final ComplexDopeVector dest, final TransformDirection dir )
		throws NullPointerException, IllegalArgumentException
	{
		this.checkVectors( input, dest );
		this.checkDirection( dir );

		this.inner_comp.setDelegate( input );
		this.outer.setDelegate( dest );

		for( int index = 0; index < this.cap_n1; index++ )
		{
			this.col.setRow( index );
			this.inner_comp.setN1( index );
			this.n2_transform.transform( this.inner_comp, this.col, dir );
		}

		for( int index = 0; index < this.cap_n2; index++ )
		{
			this.row.setColumn( index );
			this.outer.setK2( index );
			this.n1_transform.transform( this.row, this.outer, dir );
		}

		this.inner_comp.setDelegate( null );
		this.outer.setDelegate( null );
	}


	/**
	 * Transform the specified real vector.
	 * 
	 * @param input the vector to transform.
	 * @param dest the destination.
	 * @param dir the direction.
	 * @throws NullPointerException If <code>input</code> or <code>dest</code>
	 *         is <code>null</code>.
	 * @throws IllegalArgumentException If <code>input</code> does not have the
	 *         right length</code>, or if <code>input</code> == <code>dest</code>.
	 */
	public void transform( final RealDopeVector input, final ComplexDopeVector dest, final TransformDirection dir )
		throws NullPointerException, IllegalArgumentException
	{
		this.checkVectors( input, dest );
		this.checkDirection( dir );
		
		this.inner_real.setDelegate( input );
		this.outer.setDelegate( dest );

		for( int index = 0; index < this.cap_n1; index++ )
		{
			this.col.setRow( index );
			this.inner_real.setN1( index );
			this.n2_transform.forward( this.inner_real, this.col );
		}

		for( int index = 0; index < this.cap_n2; index++ )
		{
			this.row.setColumn( index );
			this.outer.setK2( index );
			this.n1_transform.forward( this.row, this.outer );
		}

		this.inner_comp.setDelegate( null );
		this.outer.setDelegate( null );
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
	 * Inner CRT re-indexer.
	 * 
	 * @author Fran Lattanzio
	 * @version $Revision$ $Date$
	 */
	private static final class InnerCrtComplexDopeVector
		extends AbstractReIndexedComplexDopeVector
		implements ComplexDopeVector
	{


		/**
		 * N = n1 * n2
		 */
		private int n;

		/**
		 * Cap n1.
		 */
		private int cap_n1;

		/**
		 * N2.
		 */
		private int cap_n2;

		/**
		 * Little n1.
		 */
		private int low_n1;

		/**
		 * Permutation map.
		 */
		private int[][] perm_map;
		

		/**
		 * Contructor.
		 * 
		 * @param cap_n1 N1 in the PFA definition.
		 * @param cap_n2 N2 in the PFA definition.
		 */
		InnerCrtComplexDopeVector( final int cap_n1, final int cap_n2 )
		{
			super();

			// store it.
			this.cap_n1 = cap_n1;
			this.cap_n2 = cap_n2;

			// find n.
			this.n = cap_n1 * cap_n2;
			
			this.perm_map = new int[ this.cap_n1 ][ this.cap_n2 ];
			for( int index = 0; index < this.cap_n1; index++ )
			{
				for( int jindex = 0; jindex < this.cap_n2; jindex++ )
				{
					this.perm_map[ index ][ jindex ] = ( ( index * this.cap_n2 ) + ( jindex * this.cap_n1 ) ) % this.n;
				}
			}
		}


		/**
		 * Get n1.
		 * 
		 * @return int n1.
		 */
		int getN1()
		{
			return ( this.low_n1 );
		}


		/**
		 * Set little n1.
		 * 
		 * @param n1 little n1.
		 */
		void setN1( final int n1 )
		{
			this.low_n1 = n1;
		}


		/**
		 * Re-index according to CRT.
		 * 
		 * @param index the index.
		 * @return int the new re-indexed.
		 */
		@Override
		protected int reIndex( final int index )
		{
			return( this.perm_map[ this.low_n1 ][ index ] );
		}


		/**
		 * Get the length.
		 * 
		 * @return int the length.
		 */
		@Override
		public int getLength()
		{
			return ( this.cap_n2 );
		}


	}


	/**
	 * Real inner CRT re-indexer.
	 * 
	 * @author Fran Lattanzio
	 * @version $Revision$ $Date$
	 */
	private static final class InnerCrtRealDopeVector
		extends AbstractReIndexedRealDopeVector
		implements RealDopeVector
	{


		/**
		 * N = n1 * n2
		 */
		private int n;

		/**
		 * Cap n1.
		 */
		private int cap_n1;

		/**
		 * N2.
		 */
		private int cap_n2;

		/**
		 * Little n1.
		 */
		private int low_n1;

		/**
		 * Permutation map.
		 */
		private int[][] perm_map;
		

		/**
		 * Contructor.
		 * 
		 * @param cap_n1 N1 in the PFA definition.
		 * @param cap_n2 N2 in the PFA definition.
		 */
		InnerCrtRealDopeVector( final int cap_n1, final int cap_n2 )
		{
			super();

			// store it.
			this.cap_n1 = cap_n1;
			this.cap_n2 = cap_n2;

			// find n.
			this.n = cap_n1 * cap_n2;
			
			this.perm_map = new int[ this.cap_n1 ][ this.cap_n2 ];
			for( int index = 0; index < this.cap_n1; index++ )
			{
				for( int jindex = 0; jindex < this.cap_n2; jindex++ )
				{
					this.perm_map[ index ][ jindex ] = ( ( index * this.cap_n2 ) + ( jindex * this.cap_n1 ) ) % this.n;
				}
			}
		}


		/**
		 * Get n1.
		 * 
		 * @return int n1.
		 */
		int getN1()
		{
			return ( this.low_n1 );
		}


		/**
		 * Set little n1.
		 * 
		 * @param n1 little n1.
		 */
		void setN1( final int n1 )
		{
			this.low_n1 = n1;
		}


		/**
		 * Re-index according to CRT.
		 * 
		 * @param index the index.
		 * @return int the new re-indexed.
		 */
		@Override
		protected int reIndex( final int index )
		{
			return( this.perm_map[ this.low_n1 ][ index ] );
		}


		/**
		 * Get the length.
		 * 
		 * @return int the length.
		 */
		@Override
		public int getLength()
		{
			return ( this.cap_n2 );
		}


	}


	/**
	 * Outer CRT re-indexer.
	 * 
	 * @author Fran Lattanzio
	 * @version $Revision$ $Date$
	 */
	private static final class OuterCrtComplexDopeVector
		extends AbstractReIndexedComplexDopeVector
		implements ComplexDopeVector
	{


		/**
		 * N = n1 * n2
		 */
		private int n;

		/**
		 * Cap n1.
		 */
		private int cap_n1;

		/**
		 * Cap n2.
		 */
		private int cap_n2;

		/**
		 * Cap n1.
		 */
		private int cap_n1_inv;

		/**
		 * Cap n2.
		 */
		private int cap_n2_inv;

		/**
		 * n1 * N2.
		 */
		private int k2;

		/**
		 * Permutation map.
		 */
		private int[][] perm_map;


		/**
		 * Contructor.
		 * 
		 * @param cap_n1 N1 in the PFA defintion.
		 * @param cap_n2 N2 in the PFA definition.
		 * @param cap_n1_inv the multiplicative inverse of N1.
		 * @param cap_n2_inv the multiplicative inverse of N2.
		 */
		OuterCrtComplexDopeVector( final int cap_n1, final int cap_n2,
															final int cap_n1_inv, final int cap_n2_inv )
		{
			super();

			// store it.
			this.cap_n1 = cap_n1;
			this.cap_n2 = cap_n2;
			this.cap_n1_inv = cap_n1_inv;
			this.cap_n2_inv = cap_n2_inv;

			// find n.
			this.n = cap_n1 * cap_n2;

			// create permuation map.
			this.perm_map = new int[ this.cap_n2 ][ this.cap_n1 ];

			for( int jindex = 0; jindex < this.cap_n2; jindex++ )
			{
				for( int index = 0; index < this.cap_n1; index++ )
				{
					this.perm_map[ jindex ][ index ] = ( ( index * this.cap_n2 * this.cap_n2_inv ) + ( jindex * this.cap_n1 * this.cap_n1_inv ) ) % this.n;
				}
			}
		}


		/**
		 * Get k2.
		 * 
		 * @return int k2.
		 */
		int getK2()
		{
			return ( this.k2 );
		}


		/**
		 * Set k2.
		 * 
		 * @param k2 new k2 value.
		 */
		void setK2( int k2 )
		{
			this.k2 = k2;
		}


		/**
		 * Re-index according to CRT.
		 * 
		 * @param index the index.
		 * @return int the new re-indexed.
		 */
		@Override
		protected int reIndex( final int index )
		{
			return ( this.perm_map[ this.k2 ][ index ] );
		}


		/**
		 * Get the length.
		 * 
		 * @return int the length.
		 */
		@Override
		public int getLength()
		{
			return ( this.cap_n1 );
		}


	}


}
