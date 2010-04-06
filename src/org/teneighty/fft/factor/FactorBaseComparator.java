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

import java.io.Serializable;
import java.util.Comparator;


/**
 * A comparator that compares <code>Factorization.Factor</code> object based on base
 * (not total).
 *
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public class FactorBaseComparator
	extends Object
	implements Comparator<Factorization.Factor>, Serializable
{
	
	
	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 2392354723L;
	
	
	/**
	 * Constructor.
	 * <p>
	 * Does nothing! Or at least nothing interesting.
	 */
	public FactorBaseComparator()
	{
		super();
	}
	
	
	/**
	 * Compare two factor object, based on base.
	 * 
	 * @param f1 first factor.
	 * @param f2 second factor.
	 * @return int in the usual/expected way.
	 */
	public int compare( final Factorization.Factor f1, final Factorization.Factor f2 )
	{
		return( f1.getBase() - f2.getBase() );		
	}
	
	
	/**
	 * Hashcode.
	 * 
	 * @return int 1.
	 */
	@Override
	public int hashCode()
	{
		return( 1 );
	}
	
	
	/**
	 * Compare the specified object for equality.
	 * <p>
	 * Two instance of this class are always considered equal...
	 * 
	 * @param other the object to which to compare.
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
		
		return( other.getClass().equals( this.getClass() ) );
	}
	

}