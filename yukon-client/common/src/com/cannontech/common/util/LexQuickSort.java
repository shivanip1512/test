package com.cannontech.common.util;

/**
 * Warning:  This thing can blow the stack quite easily
 */
public final class LexQuickSort {
	static Object[] obj;
/**
 * This method was created in VisualAge.
 * @return java.lang.Object[]
 * @param obj java.lang.Object[]
 * @param p int
 * @param r int
 */
private final static int lexPartition(int p, int r) {

	Object x = LexQuickSort.obj[p];
	int i = p - 1;
	int j = r + 1;

	while( true )
	{
		do
		{
			j--;
		} while( LexQuickSort.obj[j].toString().compareTo(x.toString().toLowerCase() ) > 0 );

		do
		{
			i++;
		} while( LexQuickSort.obj[i].toString().compareTo(x.toString().toLowerCase() ) < 0 );

		if( i < j )
		{
			Object temp = LexQuickSort.obj[i];
			LexQuickSort.obj[i] = LexQuickSort.obj[j];
			LexQuickSort.obj[j] = temp;
		}
		else
			return j;
	}
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object[]
 * @param obj java.lang.Object[]
 * @param p int
 * @param r int
 */
private final static void lexQuickSort(int p, int r) {

	if( p < r )
	{
		int q = lexPartition( p, r );
		lexQuickSort( p, q );
		lexQuickSort( q+1, r );
	}	
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object[]
 * @param obj java.lang.Object[]
 */
public final static Object[] lexSort(Object[] obj) {

	LexQuickSort.obj = obj;
	
	lexQuickSort(0, obj.length-1 );

	return LexQuickSort.obj;
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
	lexSort(s);
	System.out.println( (b = System.currentTimeMillis()) );

	System.out.println( b - a );
}
}
