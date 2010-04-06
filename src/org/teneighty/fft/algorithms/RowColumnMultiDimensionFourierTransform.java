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
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.teneighty.fft.MultiDimensionFourierTransform;
import org.teneighty.fft.FourierTransform;
import org.teneighty.fft.FourierTransformFactory;
import org.teneighty.fft.TransformDirection;
import org.teneighty.fft.dope.ComplexDopeMatrix;
import org.teneighty.fft.dope.ComplexDopeVector;
import org.teneighty.fft.dope.RealDopeMatrix;


/**
 * This class uses the row-column algorithm to perform multi-dimension Fourier
 * transform. (Despite the name, this class can compute the Fourier transform on
 * data of any dimension).
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public strictfp class RowColumnMultiDimensionFourierTransform
	extends AbstractMultiDimensionFourierTransform
	implements MultiDimensionFourierTransform, Serializable
{


	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 327834L;


	/**
	 * Map of transforms.
	 */
	private transient Map<Integer, FourierTransform> ffts;

	/**
	 * Total size.
	 */
	private transient int total;
	
	/**
	 * A buffer.
	 */
	private transient ComplexMatrixToVector a_buffer;

	/**
	 * B buffer.
	 */
	private transient ComplexMatrixToVector b_buffer;

	/**
	 * Stupid coordinate buffer.
	 */
	private transient int[] coords;


	/**
	 * Constructor.
	 * 
	 * @param lens the length.
	 * @throws IllegalArgumentException If stuff is wrong.
	 */
	public RowColumnMultiDimensionFourierTransform( final int... lens )
		throws IllegalArgumentException
	{
		super( lens );

		// initialize stuff.
		this.initialize();

		// create one-dimensional transforms, for each size neeed.
		int sz = 0;
		for( int index = 0; index < this.lengths.length; index++ )
		{
			// update total.
			this.total *= this.lengths[ index ];

			if( this.ffts.containsKey( this.lengths[ index ] ) == true )
			{
				// we already have a transform of the specified size.
				continue;
			}

			// create and add.
			sz = this.lengths[ index ];
			this.ffts.put( sz, FourierTransformFactory.getTransform( sz ) );
		}
	}


	/**
	 * Initialize the transient stuff.
	 */
	private void initialize()
	{
		// create stupid buffers.
		this.a_buffer = new ComplexMatrixToVector( this.lengths );
		this.b_buffer = new ComplexMatrixToVector( this.lengths );
		this.coords = new int[ this.lengths.length ];

		// create map.
		this.ffts = new HashMap<Integer, FourierTransform>();
		this.total = 1;
	}


	/**
	 * Zero the coordinate buffer.
	 */
	private void zeroCoordinates()
	{
		for( int index = 0; index < this.coords.length; index++ )
		{
			// piece of cake.
			this.coords[ index ] = 0;
		}
	}


	/**
	 * Compute the next coordinate.
	 */
	private void advanceCoordinates()
	{
		int add_to = this.dimension - 1;
		while( true )
		{
			this.coords[ add_to ] += 1;
			if( this.coords[ add_to ] == this.lengths[ add_to ] )
			{
				this.coords[ add_to ] = 0;
				add_to -= 1;
			}
			else
			{
				break;
			}
		}
	}


	/**
	 * Copy from the specified buffer to the specified array.
	 * 
	 * @param buf the buffer.
	 * @param dest the destination.
	 */
	private void copy( final ComplexMatrixToVector buf,
			final ComplexDopeMatrix dest )
	{
		this.zeroCoordinates();

		for( int index = 0; index < this.total; index++ )
		{
			// actually copy to a buffer.
			dest.setReal( buf.re_data[ index ], this.coords );
			dest.setImaginary( buf.im_data[ index ], this.coords );

			if( index != ( this.total - 1 ) )
			{
				// get next coord.
				this.advanceCoordinates();
			}
		}
	}


	/**
	 * Copy from the specified array to the specified buffer.
	 * 
	 * @param src the source matrix.
	 * @param dest the destination.
	 */
	private void copy( final ComplexDopeMatrix src,
			final ComplexMatrixToVector dest )
	{
		this.zeroCoordinates();

		for( int index = 0; index < this.total; index++ )
		{
			// actually copy to a buffer.
			dest.re_data[ index ] = src.getReal( this.coords );
			dest.im_data[ index ] = src.getImaginary( this.coords );

			// compute next coord.
			if( index != ( this.total - 1 ) )
			{
				this.advanceCoordinates();
			}
		}
	}


	/**
	 * Copy from the specified array to the specified buffer.
	 * 
	 * @param src the source matrix.
	 * @param dest the destination.
	 */
	private void copy( final RealDopeMatrix src, final ComplexMatrixToVector dest )
	{
		this.zeroCoordinates();

		for( int index = 0; index < this.total; index++ )
		{
			// actually copy to a buffer.
			dest.re_data[ index ] = src.getReal( this.coords );

			// cheap, lame, and lazy, but it works...
			dest.im_data[ index ] = 0;

			if( index != ( this.total - 1 ) )
			{
				this.advanceCoordinates();
			}
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
	 * @throws IllegalArgumentException If <code>input</code> does not have the
	 *         right length</code>, or if <code>input</code> == <code>dest</code>.
	 */
	public void transform( final ComplexDopeMatrix input,
			final ComplexDopeMatrix dest, final TransformDirection direction )
		throws NullPointerException, IllegalArgumentException
	{
		this.checkMatrices( input, dest );
		this.checkDirection( direction );

		// this is now pretty easy... copy to a buffer.
		this.copy( input, this.a_buffer );

		// this will bounce the results from a to b, then back to b from a, etc. as
		// transforms are computed along each dimension.
		this.sliceAll( direction );

		// now, just copy back the results from the correct buffer.
		this.copy( ( ( this.dimension % 2 == 0 ) ? this.a_buffer : this.b_buffer ), dest );
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
	public void transform( final RealDopeMatrix input,
			final ComplexDopeMatrix dest, final TransformDirection direction )
		throws NullPointerException, IllegalArgumentException
	{
		this.checkMatrices( input, dest );
		this.checkDirection( direction );

		// same as above.
		this.copy( input, this.a_buffer );
		this.sliceAll( direction );
		this.copy( ( ( this.dimension % 2 == 0 ) ? this.a_buffer : this.b_buffer ), dest );
	}


	/**
	 * Run across all dimensions.
	 * 
	 * @param direction the transform direction.
	 */
	private void sliceAll( final TransformDirection direction )
	{
		ComplexMatrixToVector a = this.a_buffer;
		ComplexMatrixToVector b = this.b_buffer;
		ComplexMatrixToVector t = null;

		for( int index = 0; index < this.lengths.length; index++ )
		{
			// slice along the specified dimension.
			this.sliceDimension( a, b, index, direction );

			// swap buffers.
			t = a;
			a = b;
			b = t;
		}
	}


	/**
	 * Run the transform along the specified dimension, from the source to the
	 * destination buffer.
	 * 
	 * @param src the source buffer.
	 * @param dest the destination buffer.
	 * @param dim the dimension.
	 * @param direction the direction.
	 */
	private void sliceDimension( final ComplexMatrixToVector src,
			final ComplexMatrixToVector dest, final int dim,
			final TransformDirection direction )
	{
		src.setFixedIndex( dim );
		dest.setFixedIndex( dim );

		int perm_count = src.getPermutationCount();
		FourierTransform ft = this.ffts.get( this.lengths[ dim ] );

		for( int index = 0; index < perm_count; index++ )
		{
			ft.transform( src, dest, direction );
			src.nextPermutation();
			dest.nextPermutation();

			for( int jindex = 0; jindex < src.getLength(); jindex++ )
			{
				src.getReal( jindex );
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

		// write out delegate FTs.
		out.writeInt( this.ffts.size() );

		Iterator<Map.Entry<Integer, FourierTransform>> it = this.ffts.entrySet().iterator();
		Map.Entry<Integer, FourierTransform> entry = null;
		while( it.hasNext() == true )
		{
			entry = it.next();
			out.writeObject( entry.getValue() );
		}
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
		this.initialize();

		// restore the hashmap of death!
		final int count = in.readInt();
		FourierTransform rest = null;

		for( int index = 0; index < count; index++ )
		{
			// read and hash.
			rest = (FourierTransform)in.readObject();
			this.ffts.put( rest.getLength(), rest );
		}
	}


	/**
	 * Matrix to vector view.
	 * 
	 * @author Fran Lattanzio
	 * @version $Revision$ $Date$
	 */
	private final static class ComplexMatrixToVector
		extends Object
		implements ComplexDopeVector
	{


		/**
		 * Real data.
		 */
		double[] re_data;

		/**
		 * Imaginary data.
		 */
		double[] im_data;

		/**
		 * Fixed index.
		 */
		private int fixed_index;

		/**
		 * Lengths.
		 */
		private int[] lengths;

		/**
		 * Current permutation.
		 */
		private int[] perm;

		/**
		 * Permutation count.
		 */
		private int perm_count;

		/**
		 * Dimension.
		 */
		private int dim;


		/**
		 * Constructor.
		 * 
		 * @param lengths the lengths.
		 */
		ComplexMatrixToVector( final int... lengths )
		{
			super();

			// store and create stuff.
			this.lengths = lengths;
			this.dim = this.lengths.length;
			this.perm = new int[ this.dim ];

			// find the total.
			int total = 1;
			for( int index = 0; index < this.dim; index++ )
			{
				total *= lengths[ index ];
			}

			// create data.
			this.im_data = new double[ total ];
			this.re_data = new double[ total ];
		}


		/**
		 * Get the length of this vector.
		 * 
		 * @return int the length.
		 */
		public int getLength()
		{
			return ( this.lengths[ this.fixed_index ] );
		}


		/**
		 * Get the index<sup>th</sup> element.
		 * 
		 * @param index the index to get.
		 * @return double the index<sup>th</sup> element.
		 * @throws ArrayIndexOutOfBoundsException If <code>index</code> is out of
		 *         bounds.
		 */
		public double getReal( final int index )
			throws ArrayIndexOutOfBoundsException
		{
			return ( this.re_data[ this.getIndex( index ) ] );
		}


		/**
		 * Set the index<sup>th</sup> element.
		 * 
		 * @param value the value.
		 * @param index the index.
		 * @throws ArrayIndexOutOfBoundsException If <code>index</code> is out of
		 *         bounds.
		 */
		public void setReal( final double value, final int index )
			throws ArrayIndexOutOfBoundsException
		{			
			this.re_data[ this.getIndex( index ) ] = value;
		}


		/**
		 * Get the imaginary part of the specified index.
		 * 
		 * @param index the index.
		 * @return double the imaginary part.
		 * @throws ArrayIndexOutOfBoundsException If <code>index</code> is out of
		 *         bounds.
		 */
		public double getImaginary( final int index )
			throws ArrayIndexOutOfBoundsException
		{
			return ( this.im_data[ this.getIndex( index ) ] );
		}


		/**
		 * Set the imaginary part of the specified number.
		 * 
		 * @param value the value.
		 * @param index the index.
		 * @throws ArrayIndexOutOfBoundsException If <code>index</code> is out of
		 *         bounds.
		 */
		public void setImaginary( final double value, final int index )
			throws ArrayIndexOutOfBoundsException
		{
			this.im_data[ this.getIndex( index ) ] = value;
		}


		/**
		 * Get the row-major index of the specified index.
		 * 
		 * @param i the index.
		 * @return int the index into the array.
		 * @throws IllegalArgumentException If <code>lens</code> is illegal.
		 * @throws NullPointerException If <code>lens</code> is <code>null</code>.
		 */
		private int getIndex( final int i )
			throws NullPointerException, IllegalArgumentException
		{
			int array_index = 0;

			if( this.fixed_index == 0 )
			{
				array_index = i;
			}
			else
			{
				array_index = this.perm[ 0 ];
			}

			for( int index = 1; index < this.perm.length; index++ )
			{
				if( index == this.fixed_index )
				{
					// use i, instead.
					array_index *= this.lengths[ index ];
					array_index += i;
				}
				else
				{
					// normal way of computing row major offsets and whatnot.
					array_index *= this.lengths[ index ];
					array_index += this.perm[ index ];
				}
			}

			return ( array_index );
		}


		/**
		 * Permutation count.
		 * 
		 * @return int perm count.
		 */
		int getPermutationCount()
		{
			return ( this.perm_count );
		}


		/**
		 * Compute the next permutation.
		 */
		void nextPermutation()
		{
			int add_index = 0;

			while( add_index < this.dim )
			{
				if( add_index == this.fixed_index )
				{
					// skip the fixed index when computing permutations!
					add_index += 1;
					continue;
				}

				this.perm[ add_index ] += 1;

				if( this.perm[ add_index ] == this.lengths[ add_index ] )
				{
					// carry to the next index.
					// if I cared, I could use a redundant multi-n-ary counter and save
					// some time. yet I don't, so I'm not. HA!
					this.perm[ add_index ] = 0;
					add_index += 1;
				}
				else
				{
					break;
				}
			}
		}


		/**
		 * Get fixed_index.
		 * 
		 * @return int the fixed_index.
		 */
		int getFixedIndex()
		{
			return ( this.fixed_index );
		}


		/**
		 * Set fixed_index.
		 * 
		 * @param fixed_index the new value for fixed_index.
		 */
		void setFixedIndex( final int fixed_index )
		{
			this.perm_count = 1;
			for( int index = 0; index < this.dim; index++ )
			{
				if( index == fixed_index )
				{
					continue;
				}

				// compute new permutation count.
				this.perm_count *= this.lengths[ index ];
			}

			this.fixed_index = fixed_index;

			// zero the permutation vector.
			for( int index = 0; index < this.dim; index++ )
			{
				this.perm[ index ] = 0;
			}
		}


	}


}
