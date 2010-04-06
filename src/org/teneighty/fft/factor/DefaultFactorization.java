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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Collections;


/**
 * A simple default factorization class.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public final class DefaultFactorization
	extends Object
	implements Factorization, Serializable
{


	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 2342342L;


	/**
	 * Factors.
	 */
	private List<Factor> factors;

	/**
	 * Number.
	 */
	private int number;


	/**
	 * Constructor.
	 * 
	 * @param factors the factors.
	 * @param number the factored number.
	 */
	public DefaultFactorization( final List<Factor> factors, final int number )
	{
		super();

		// store stuff.
		this.factors = Collections.unmodifiableList( factors );
		this.number = number;
	}


	/**
	 * Get the number which is factorized.
	 * 
	 * @return int the number.
	 */
	public int getNumber()
	{
		return ( this.number );
	}


	/**
	 * Get the list of factor.
	 * <p>
	 * The returned list is not modifiable.
	 * 
	 * @return List{@literal <Factor>} the list of factors.
	 */
	public List<Factor> getFactors()
	{
		return ( this.factors );
	}
	
	
	/**
	 * A better to string!
	 * 
	 * @return String a better to string.
	 */
	@Override
	public String toString()
	{
		StringBuilder sw = new StringBuilder();
		
		for( int index = 0; index < this.factors.size(); index++ )
		{
			sw.append( this.factors.get( index ).toString() );
		}
				
		return( sw.toString() );
	}


	/**
	 * A simple default factor class.
	 * 
	 * @author Fran Lattanzio
	 * @version $Revision$ $Date$
	 */
	public static final class DefaultFactor
		extends Object
		implements Factorization.Factor, Serializable
	{


		/**
		 * Serial verison UID.
		 */
		private static final long serialVersionUID = 239482384L;

		
		/**
		 * Base.
		 */
		private int base;

		/**
		 * Exponent.
		 */
		private int exp;

		/**
		 * Total.
		 */
		private transient int total;


		/**
		 * Constructor.
		 * 
		 * @param base the base.
		 * @param exp the exponent.
		 */
		public DefaultFactor( int base, int exp )
		{
			super();

			// Store stuff.
			this.base = base;
			this.exp = exp;
			this.total = (int)Math.pow( this.base, this.exp );
		}


		/**
		 * Get the base.
		 * 
		 * @return int the base.
		 */
		public int getBase()
		{
			return ( this.base );
		}


		/**
		 * Get the exponent.
		 * 
		 * @return int the exponent.
		 */
		public int getPower()
		{
			return ( this.exp );
		}


		/**
		 * Get the total.
		 * 
		 * @return int base ** total.
		 */
		public int getTotal()
		{
			return ( this.total );
		}
		
		
		/**
		 * Compare this object for equality with the specified object.
		 * 
		 * @param other some other object
		 * @return boolean <code>true</code> if equal.
		 */
		@Override
		public boolean equals( final Object other )
		{
			if( other == null )
			{
				return( false );
			}
			
			if( other == this )
			{
				return( true );
			}
			
			if( Factor.class.isAssignableFrom( other.getClass() ) == true )
			{
				Factor fact = (Factor)other;
				return( this.getTotal() == fact.getTotal() && this.getBase() == fact.getBase() && this.getPower() == fact.getPower() );
			}
			
			return( false );
		}
		
		
		/**
		 * Get the hashcode for this object.
		 * 
		 * @return int hashcode inline with equals.
		 */
		@Override
		public int hashCode()
		{
			return( this.total ^ this.base ^ this.exp );
		}
		
		
		/**
		 * A better to string.
		 * 
		 * @return String a string.
		 */
		@Override
		public String toString()
		{
			return( this.total + "(" + this.base + "**" + this.exp + ")" );
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
		 * @throws IOException If this object cannot properly read from the
		 *         specified stream.
		 * @throws ClassNotFoundException If deserialization tries to classload an
		 *         undefined class.
		 */
		private void readObject( final ObjectInputStream in )
			throws IOException, ClassNotFoundException
		{
			// Read non-transient fields.
			in.defaultReadObject();

			this.total = (int)Math.pow( this.base, this.exp );
		}


	}
	

}
