package com.cannontech.common.util;

/**
 * This type was created in VisualAge.
 */
public final class ShellSort {
/**
 * This method was stolen from a function in some C book.
 * Hey it works.
 * @return java.lang.Object[]
 * @param obj java.lang.Object[]
 */
public final static Object[] lexShellSort(Object[] obj ) {

	int lb = 0;
	int ub = obj.length-1;
	
	int n, h, i, j;
	Object t;

	n = ub - lb + 1;
	h = 1;

	if( n < 14 )
		h = 1;
	else
	if( obj.length == 2 && n > 29524 )
		 h = 3280;
	else
	{
		while( h < n )
			h = 3 * h + 1;
		h /= 3;
		h /= 3;
	}

	while( h > 0 )
	{
		for (i = lb + h; i <= ub; i++) 
		{
			
			t = obj[i];

			//Java re-engineered
			for ( j = i-h; 
				  j >= lb && obj[j].toString().toLowerCase().compareTo( t.toString().toLowerCase() ) > 0; 
				  j -= h )
			{
				obj[j+h] = obj[j];
			}
			
			obj[j+h] = t;
		}

		/* compute next increment */
		h /= 3;
	}

	return obj;	
}
/**
 * This method was created in VisualAge.
 * @param args java.lang.String[]
 */
public static void main(String args[]) {

	int length = (new Integer( args[0] )).intValue();
	long a, b;
	Long s[] = new Long[length];
		
	java.util.Random r = new java.util.Random();

	for( int i = 0; i < length; i++ )
		s[i] =  new Long( r.nextLong() );

	
	System.out.println( (a = System.currentTimeMillis()) );
	lexShellSort(s);
	System.out.println( (b = System.currentTimeMillis()) );

	System.out.println( b - a );
	
}
}
