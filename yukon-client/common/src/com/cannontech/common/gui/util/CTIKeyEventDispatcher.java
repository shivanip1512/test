package com.cannontech.common.gui.util;

import java.awt.KeyEventDispatcher;
import java.awt.TextComponent;
import java.awt.event.KeyEvent;

import javax.swing.text.JTextComponent;

/**
 * @author rneuharth
 *
 * Use this class to intercept key events for accelerators. This class takes care
 * of some of the functionality that all KeyEventDispatchers would need.
 * 
 */
public abstract class CTIKeyEventDispatcher implements KeyEventDispatcher
{
	/**
	 * 
	 */
	public CTIKeyEventDispatcher()
	{
		super();
	}

	/**
	 * This method will be executed during a key event.
	 * @param e
	 */
	public abstract boolean handleKeyEvent( KeyEvent e );


	/* (non-Javadoc)
	 * @see java.awt.KeyEventDispatcher#dispatchKeyEvent(java.awt.event.KeyEvent)
	 */
	public boolean dispatchKeyEvent(KeyEvent e)
	{
		//do the checks of the KeyEvent here
		if( e.getID() == KeyEvent.KEY_PRESSED && isValid(e) )
		{
			return handleKeyEvent( e );
		}
				
		//its this the last handling of the KeyEvent in this KeyboardFocusManager?
		return false;
	}	
	
	private boolean isValid( KeyEvent e )
	{
		//ignore all keys when the originating component is one of the following
		return !(e.getComponent() instanceof JTextComponent
				    || e.getComponent() instanceof TextComponent);
	}

}
