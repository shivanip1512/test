package com.cannontech.tdc.observe;

/**
 * Insert the type's description here.
 * Creation date: (1/9/2002 10:48:06 AM)
 * @author: 
 */
public abstract class ObservableJPopupMenu extends javax.swing.JPopupMenu 
{
	private ObservableJPopUp observable = null;


	//the observable obj for this menu
	public class ObservableJPopUp extends java.util.Observable {
		private java.util.Observable src = null;
		private Object data = null;

		public ObservableJPopUp( java.util.Observable src_ ) {
			super();
			src = src_;
		}

		public java.lang.Object getData()  {
			return data;
		}

		public void setChanged() {
			super.setChanged();
		}

		public void addObserver( java.util.Observer o ) {
			super.addObserver( o );
		}

		public void setData(java.lang.Object newData) {
			data = newData;
		}
	}


	/**
	 * ObservableJPopupMenu constructor comment.
	 */
	public ObservableJPopupMenu() {
		super();
	}

	public ObservableJPopUp getObservable() {
		return observable;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/9/2002 10:54:50 AM)
	 * @param newObservable java.util.Observable
	 */
	public void setObservable(java.util.Observable newObservable) {
		observable = new ObservableJPopUp( newObservable );
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (1/9/2002 10:54:50 AM)
	 * @param newObservable java.util.Observable
	 */
	public void addObserver(java.util.Observer newObserver) {
		if( observable != null )
			observable.addObserver( newObserver );
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/9/2002 10:21:56 AM)
	 */
	protected void fireObservedRowChanged( Object value ) 
	{
		if( getObservable() != null
			 && getObservable().countObservers() > 0 )
		{		
			getObservable().setChanged();
			getObservable().notifyObservers( value );
		}
	
	}
}
