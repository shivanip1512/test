package com.cannontech.datagenerator;

/**
 * @author rneuharth
 * Jul 31, 2002 at 1:41:21 PM
 * 
 * A undefined generated comment
 */
public class DataTools
{

	/**
	 * Constructor for DataTools.
	 */
	private DataTools()
	{
		super();
	}

   public static String createByteHexStream( java.io.File f ) throws java.io.IOException
   {
      long len = f.length();             
      java.io.InputStream in = new java.io.FileInputStream(f.getPath());
      StringBuffer buf = new StringBuffer( (int)len );

      byte[] b = new byte[4096];
      int n;
      while( (n = in.read(b, 0, b.length)) != -1) 
      {
         for( int j = 0; j < n; j++ )
         {
            String s = Integer.toHexString(b[j]);
            buf.append(
               (s.length() < 2 ? "0"+s :
                  (s.length() > 2 ? s.substring(s.length()-2,s.length()) : s)) );
         }
      }
      
//      System.out.println();
//      System.out.println("       Len = " + b.length );      
      
      return buf.toString();
   }
 
 
   public static void main(String args[])
   {
      
      if( args.length <= 0 )
      {
         System.out.println("Syntax: DataTools <filepath1> <filepath2>...<filepathN>");
      }      

      for( int i = 0; i < args.length; i++ )
      {               
         try
         {
            java.io.File f = new java.io.File(args[i]);
            System.out.println( "0x" + DataTools.createByteHexStream(f) );
         }
         catch( Exception e )
         {
            e.printStackTrace( System.out );
         }
      }
      
   }
   
}
