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

import java.util.List;


/**
 * Represents the factorization of an integer.
 *
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public interface Factorization
{
	
	
	/**
	 * Get the number which is factorized.
	 * 
	 * @return int the number.
	 */
	public int getNumber();

	
	/**
	 * Get the list of factor.
	 * <p>
	 * The returned list is not modifiable. 
	 * 
	 * @return List{@literal <Factor>} the list of factors.
	 */
	public List<Factor> getFactors();
	
	
	/**
	 * Represents one factor in a factorization.
	 * 
	 * @author Fran Lattanzio
	 * @version $Revision$ $Date$
	 */
	public static interface Factor
	{
		
		
		/**
		 * Get the base.
		 * 
		 * @return int the base.
		 */
		public int getBase();
		
		
		/**
		 * Get the power.
		 * 
		 * @return int the power.
		 */
		public int getPower();
		
		
		/**
		 * Get the total (i.e. base ** power).
		 * 
		 * @return int the total.
		 */
		public int getTotal();
		
		
	}
	

}
