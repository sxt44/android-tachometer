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

package org.teneighty.fft.dope;


/**
 * A package private class that contains some support methods for dope vector
 * implementations.
 * <p>
 * This class is stateless and cannot be instantiated.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
final class DopeUtilities
	extends Object
{


	/**
	 * A good to-string for complex dope vectors.
	 * 
	 * @param dope vector the vector.
	 * @return String a nice string.
	 * @throws NullPointerException If <code>dope</code> is <code>null</code>.
	 */
	static String toString( final ComplexDopeVector dope )
		throws NullPointerException
	{
		if( dope == null )
		{
			throw new NullPointerException();
		}

		StringBuilder sb = new StringBuilder();
		final int length = dope.getLength();
		sb.append( dope.getClass().getName() );
		sb.append( "(" + length + ") " );
		sb.append( "[ " );

		for( int index = 0; index < length; index++ )
		{
			sb.append( dope.getReal( index ) + "/" + dope.getImaginary( index ) + "(" + index + ")" );
			if( index != length )
			{
				sb.append( ", " );
			}
			else
			{
				sb.append( " " );
			}
		}

		sb.append( "]" );

		return ( sb.toString() );
	}


	/**
	 * A good to-string for real dope vectors.
	 * 
	 * @param dope vector the vector.
	 * @return String a nice string.
	 * @throws NullPointerException If <code>dope</code> is <code>null</code>.
	 */
	static String toString( final RealDopeVector dope )
		throws NullPointerException
	{
		if( dope == null )
		{
			throw new NullPointerException();
		}

		StringBuilder sb = new StringBuilder();
		final int length = dope.getLength();
		sb.append( dope.getClass().getName() );
		sb.append( "(" + length + ") " );
		sb.append( "[ " );

		for( int index = 0; index < length; index++ )
		{
			sb.append( dope.getReal( index ) + "(" + index + ")" );
			if( index != length )
			{
				sb.append( ", " );
			}
			else
			{
				sb.append( " " );
			}
		}

		sb.append( "]" );

		return ( sb.toString() );
	}


	/**
	 * A correct equals implementation for complex dope vectors.
	 * 
	 * @param dope vector the vector.
	 * @param other some other object.
	 * @return boolean <code>true</code> if equal.
	 * @throws NullPointerException If <code>dope</code> is <code>null</code>.
	 */
	static boolean equals( final ComplexDopeVector dope, final Object other )
		throws NullPointerException
	{
		if( dope == null )
		{
			throw new NullPointerException();
		}

		if( other == null )
		{
			return ( false );
		}

		if( other == dope )
		{
			return ( true );
		}

		if( ComplexDopeVector.class.isAssignableFrom( other.getClass() ) == true )
		{
			ComplexDopeVector that = (ComplexDopeVector)other;

			if( that.getLength() != dope.getLength() )
			{
				return ( false );
			}

			for( int index = 0; index < dope.getLength(); index++ )
			{
				if( ( that.getReal( index ) != dope.getReal( index ) ) || ( that.getImaginary( index ) != dope.getImaginary( index ) ) )
				{
					return ( false );
				}
			}

			return ( true );
		}

		// not an instance.
		return ( false );
	}


	/**
	 * A correct equals implementation for real dope vectors.
	 * 
	 * @param dope vector the vector.
	 * @param other some other object.
	 * @return boolean <code>true</code> if equal.
	 * @throws NullPointerException If <code>dope</code> is <code>null</code>.
	 */
	static boolean equals( final RealDopeVector dope, final Object other )
		throws NullPointerException
	{
		if( dope == null )
		{
			throw new NullPointerException();
		}

		if( other == null )
		{
			return ( false );
		}

		if( other == dope )
		{
			return ( true );
		}

		if( RealDopeVector.class.isAssignableFrom( other.getClass() ) == true )
		{
			RealDopeVector that = (RealDopeVector)other;

			if( that.getLength() != dope.getLength() )
			{
				return ( false );
			}

			for( int index = 0; index < dope.getLength(); index++ )
			{
				if( that.getReal( index ) != dope.getReal( index ) )
				{
					return ( false );
				}
			}

			return ( true );
		}

		// not an instance.
		return ( false );
	}


	/**
	 * Append the specified int[] to the specified builder.
	 * 
	 * @param array the array.
	 * @param sb the builder.
	 */
	private static void appendArray( final int[] array, final StringBuilder sb )
	{
		sb.append( "( " );
		for( int index = 0; index < array.length; index++ )
		{
			sb.append( array[ index ] );
			sb.append( ( index != ( array.length - 1 ) ) ? ", " : " " );
		}
		sb.append( ")" );
	}


	/**
	 * A good to-string for complex dope vectors.
	 * 
	 * @param dope vector the vector.
	 * @return String a nice string.
	 * @throws NullPointerException If <code>dope</code> is <code>null</code>.
	 */
	static String toString( final ComplexDopeMatrix dope )
		throws NullPointerException
	{
		if( dope == null )
		{
			throw new NullPointerException();
		}

		int[] lens = new int[ dope.getDimension() ];
		int[] coords = new int[ dope.getDimension() ];
		int total = 1;

		// turn it into a local array.
		for( int index = 0; index < lens.length; index++ )
		{
			lens[ index ] = dope.getLength( index );
			total *= lens[ index ];
		}

		StringBuilder sb = new StringBuilder();
		sb.append( dope.getClass().getName() );
		appendArray( lens, sb );

		sb.append( "[ " );

		int add_to = 0;
		for( int index = 0; index < total; index++ )
		{
			appendArray( coords, sb );
			sb.append( "->" );
			sb.append( dope.getReal( coords ) + "/" + dope.getImaginary( coords ) );

			if( index != ( total - 1 ) )
			{
				sb.append( ", " );

				// get next coord.
				add_to = ( lens.length  ) - 1;
				while( true )
				{
					coords[ add_to ] += 1;
					if( coords[ add_to ] == lens[ add_to ] )
					{
						coords[ add_to ] = 0;
						add_to -= 1;
					}
					else
					{
						break;
					}
				}
			}
			else
			{
				sb.append( " " );
			}

		}

		sb.append( "]" );

		return ( sb.toString() );
	}


	/**
	 * A good to-string for real dope vectors.
	 * 
	 * @param dope vector the vector.
	 * @return String a nice string.
	 * @throws NullPointerException If <code>dope</code> is <code>null</code>.
	 */
	static String toString( final RealDopeMatrix dope )
		throws NullPointerException
	{
		if( dope == null )
		{
			throw new NullPointerException();
		}

		int[] lens = new int[ dope.getDimension() ];
		int[] coords = new int[ dope.getDimension() ];
		int total = 1;

		// turn it into a local array.
		for( int index = 0; index < lens.length; index++ )
		{
			lens[ index ] = dope.getLength( index );
			total *= lens[ index ];
		}

		StringBuilder sb = new StringBuilder();
		sb.append( dope.getClass().getName() );
		appendArray( lens, sb );

		sb.append( "[ " );

		int add_to = 0;
		for( int index = 0; index < total; index++ )
		{
			appendArray( coords, sb );
			sb.append( "->" );
			sb.append( dope.getReal( coords ) );

			if( index != ( total - 1 ) )
			{
				sb.append( ", " );

				// get next coord.
				add_to = ( lens.length  ) - 1;
				while( true )
				{
					coords[ add_to ] += 1;
					if( coords[ add_to ] == lens[ add_to ] )
					{
						coords[ add_to ] = 0;
						add_to -= 1;
					}
					else
					{
						break;
					}
				}
			}
			else
			{
				sb.append( " " );
			}

		}

		sb.append( "]" );

		return ( sb.toString() );
	}


	/**
	 * A correct equals implementation for complex dope vectors.
	 * 
	 * @param dope vector the vector.
	 * @param other some other object.
	 * @return boolean <code>true</code> if equal.
	 * @throws NullPointerException If <code>dope</code> is <code>null</code>.
	 */
	static boolean equals( final ComplexDopeMatrix dope, final Object other )
		throws NullPointerException
	{
		if( dope == null )
		{
			throw new NullPointerException();
		}

		if( other == null )
		{
			return ( false );
		}

		if( other == dope )
		{
			return ( true );
		}

		if( ComplexDopeMatrix.class.isAssignableFrom( other.getClass() ) == true )
		{
			ComplexDopeMatrix that = (ComplexDopeMatrix)other;
			
			if( that.getDimension() != dope.getDimension() )
			{
				return( false );
			}
			
			int[] lens = new int[ dope.getDimension() ];
			int[] coords = new int[ dope.getDimension() ];
			int total = 1;

			// turn it into a local array.
			for( int index = 0; index < lens.length; index++ )
			{
				lens[ index ] = dope.getLength( index );
				total *= lens[ index ];
				
				if( dope.getLength( index ) != that.getLength( index ) )
				{
					return( false );
				}				
			}

			int add_to = 0;
			for( int index = 0; index < total; index++ )
			{
				if( dope.getReal( coords ) != that.getReal( coords ) ||
						dope.getImaginary( coords ) != that.getImaginary( coords ) )					
				{
					return( false );
				}
				
				if( index != ( total - 1 ) )
				{
					// get next coord.
					add_to = ( lens.length  ) - 1;
					while( true )
					{
						coords[ add_to ] += 1;
						if( coords[ add_to ] == lens[ add_to ] )
						{
							coords[ add_to ] = 0;
							add_to -= 1;
						}
						else
						{
							break;
						}
					}
				}
			}

			return ( true );
		}

		// not an instance.
		return ( false );
	}


	/**
	 * A correct equals implementation for real dope vectors.
	 * 
	 * @param dope vector the vector.
	 * @param other some other object.
	 * @return boolean <code>true</code> if equal.
	 * @throws NullPointerException If <code>dope</code> is <code>null</code>.
	 */
	static boolean equals( final RealDopeMatrix dope, final Object other )
		throws NullPointerException
	{
		if( dope == null )
		{
			throw new NullPointerException();
		}

		if( other == null )
		{
			return ( false );
		}

		if( other == dope )
		{
			return ( true );
		}

		if( RealDopeMatrix.class.isAssignableFrom( other.getClass() ) == true )
		{
			RealDopeMatrix that = (RealDopeMatrix)other;

			if( that.getDimension() != dope.getDimension() )
			{
				return( false );
			}

			int[] lens = new int[ dope.getDimension() ];
			int[] coords = new int[ dope.getDimension() ];
			int total = 1;

			// turn it into a local array.
			for( int index = 0; index < lens.length; index++ )
			{
				lens[ index ] = dope.getLength( index );
				total *= lens[ index ];
				
				if( dope.getLength( index ) != that.getLength( index ) )
				{
					return( false );
				}
			}

			int add_to = 0;
			for( int index = 0; index < total; index++ )
			{
				if( dope.getReal( coords ) != that.getReal( coords ) )
				{
					return( false );
				}
				
				if( index != ( total - 1 ) )
				{
					// get next coord.
					add_to = ( lens.length  ) - 1;
					while( true )
					{
						coords[ add_to ] += 1;
						if( coords[ add_to ] == lens[ add_to ] )
						{
							coords[ add_to ] = 0;
							add_to -= 1;
						}
						else
						{
							break;
						}
					}
				}
			}

			return ( true );
		}

		// not an instance.
		return ( false );
	}


	/**
	 * No instances.
	 * 
	 * @throws InternalError always.
	 */
	private DopeUtilities()
		throws InternalError
	{
		throw new InternalError();
	}


}
