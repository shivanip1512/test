package com.cannontech.common.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Observable;
import java.util.Observer;

/**
 * @author rneuharth
 * Created on May 28, 2003
 *
 * Allows for an Observer to be weakly referenced for GC purposes. 
 */
public class WeakObserver extends WeakReference implements Observer
{

	/**
	 * @param referent
	 */
	public WeakObserver(Observer referent)
	{
		super(referent);

		if( referent == null )
			 throw new NullPointerException();		
	}

	/**
	 * @param referent
	 * @param q
	 */
	public WeakObserver(Observer referent, ReferenceQueue q)
	{
		super(referent, q);

		if( referent == null )
			 throw new NullPointerException();		
	}

	/**
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg)
	{
		//this should be a serious mistake if get() is null!!
		((Observer)get()).update( o, arg );
	}

}
