package com.cannontech.tdc.windows;

/**
 * @author rneuharth
 * Oct 21, 2002 at 10:54:58 AM
 * 
 * A undefined generated comment
 */
public class ExecuteServiceCmd extends java.util.Observable implements Runnable
{

	/**
	 * Constructor for ExecuteServiceCmd.
	 */
	public ExecuteServiceCmd()
	{
		super();
	}

   public void run()
   {
      
      
      
      //tell our observers we changed
      setChanged();
      notifyObservers( this );
   }


}
