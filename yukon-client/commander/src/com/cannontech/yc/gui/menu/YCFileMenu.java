package com.cannontech.yc.gui.menu;

/**
 * This type was created in VisualAge.
 */
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import javax.swing.JSeparator;

import com.cannontech.common.gui.util.CTIKeyEventDispatcher;
import com.cannontech.common.gui.util.CommandableMenuItem;

public class YCFileMenu extends javax.swing.JMenu {
	private java.awt.Font f = new java.awt.Font("dialog", 0, 14 );

    public JSeparator separator1;
	public JSeparator separator2;

	public CommandableMenuItem saveMenuItem;
	public CommandableMenuItem printMenuItem;
	public CommandableMenuItem exitMenuItem;
	public CommandableMenuItem recentItem0;
	public CommandableMenuItem recentItem1;
	public CommandableMenuItem recentItem2;
	public CommandableMenuItem recentItem3;
	public CommandableMenuItem recentItem4;
	
	public CommandableMenuItem commandSpecificControl;	//CHEATER MODE
	private Object[] recentItems = new Object[]{null, null, null, null, null};
/**
 * YukonCommanderFileMenu constructor comment.
 */
public YCFileMenu() {
	super();
	initialize();
}
/**
 * This method was created in VisualAge.
 */
private void initialize() {
	separator1 = new JSeparator();
	separator2 = new JSeparator();

	saveMenuItem = new CommandableMenuItem();
	saveMenuItem.setFont(f);
	saveMenuItem.setText("Save Output...");
	saveMenuItem.setMnemonic('s');
	saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
								java.awt.event.KeyEvent.VK_S,
								java.awt.Event.CTRL_MASK));

	printMenuItem = new CommandableMenuItem();
	printMenuItem.setFont(f);
	printMenuItem.setText("Print...");
	printMenuItem.setMnemonic('p');
	printMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
	                             java.awt.event.KeyEvent.VK_P,
	                             java.awt.Event.CTRL_MASK));
	
	exitMenuItem = new CommandableMenuItem();
	exitMenuItem.setFont( f );
	exitMenuItem.setText("Exit");
	exitMenuItem.setMnemonic('x');
	exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
	                            java.awt.event.KeyEvent.VK_X,
	                            java.awt.Event.CTRL_MASK));

	commandSpecificControl = new CommandableMenuItem();
	commandSpecificControl.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
	                                      java.awt.event.KeyEvent.VK_F5,
	                                      java.awt.Event.CTRL_MASK));
	commandSpecificControl.setVisible(false);
		
	setFont( f );
	setText("File");
	setMnemonic('f');

	add( saveMenuItem );
	add( printMenuItem );
	add( separator1 );
	add( getRecentItem0());
	add( getRecentItem1());
	add( getRecentItem2());
	add( getRecentItem3());
	add( getRecentItem4());
	add( separator2 );
	add( exitMenuItem );
	add( commandSpecificControl );	//THIS WILL NEVER BE VISIBLE
	
	/* 
	 * This way to handle accelerators was changed to work with JRE 1.4. The accelerator
	 * event would always get consumed by the component focus was in. This ensures that
	 * accelerator fires the correct event ONLY (that is why true is returned on after
	 * each click). We keep the above accelerators set for display purposes.
	 */
	KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(
		new CTIKeyEventDispatcher()
		{
			public boolean handleKeyEvent(KeyEvent e)
			{
				//do the checks of the keystrokes here
				if( e.getKeyCode() == KeyEvent.VK_S && e.isControlDown() )
				{
					saveMenuItem.doClick();
					return true;
				}
				else if( e.getKeyCode() == KeyEvent.VK_P && e.isControlDown() )
				{
					printMenuItem.doClick();
					return true;
				}
				else if( e.getKeyCode() == KeyEvent.VK_X && e.isControlDown() )
				{
					exitMenuItem.doClick();
					return true;
				}
				else if( e.getKeyCode() == KeyEvent.VK_F5 && e.isControlDown() )
				{
					commandSpecificControl.doClick();
					return true;
				}
				//its this the last handling of the KeyEvent in this KeyboardFocusManager?
				return false;
			}
		});	
	}
	public void updateRecentList(Object newItem)
	{
        int foundIndex = itemExists(newItem);
        if( foundIndex == 4 || foundIndex < 0)
        {
            recentItem4.setText(getRecentItem3().getText());
            recentItems[4] = recentItems[3];
            foundIndex = -1;
        }
        if( foundIndex == 3 || foundIndex < 0 )
        {
		    recentItem3.setText(getRecentItem2().getText());
		    recentItems[3] = recentItems[2];
		    foundIndex = -1;
        }
        if( foundIndex == 2 || foundIndex < 0)
        {
            recentItem2.setText(getRecentItem1().getText());
            recentItems[2] = recentItems[1];
            foundIndex = -1;
        }
        if( foundIndex == 1 || foundIndex < 0)
        {
            recentItem1.setText(getRecentItem0().getText());
            recentItems[1] = recentItems[0];
            foundIndex = -1;
        }        
        recentItem0.setText(newItem.toString());
        recentItems[0] = newItem;
        
        getRecentItem0().setVisible(getRecentItem0().getText().length() > 0);
	    getRecentItem1().setVisible(getRecentItem1().getText().length() > 0);
	    getRecentItem2().setVisible(getRecentItem2().getText().length() > 0);
	    getRecentItem3().setVisible(getRecentItem3().getText().length() > 0);
	    getRecentItem4().setVisible(getRecentItem4().getText().length() > 0);
	}
	private int itemExists(Object item)
	{
	    for (int i = 0; i < recentItems.length; i++)
	    {
	        if( recentItems[i] != null && recentItems[i].equals(item))
	            return i;
	    }
	    return -1;
	}
    /**
     * @return Returns the recentItem1.
     */
    public CommandableMenuItem getRecentItem0()
    {
        if( recentItem0 == null)
        {
            recentItem0 = new CommandableMenuItem();
            recentItem0.setFont(f);
            recentItem0.setText("");
            recentItem0.setVisible(false);
        }
        return recentItem0;
    }
	
    /**
     * @return Returns the recentItem1.
     */
    public CommandableMenuItem getRecentItem1()
    {
        if( recentItem1 == null)
        {
            recentItem1 = new CommandableMenuItem();
            recentItem1.setFont(f);
            recentItem1.setText("");
            recentItem1.setVisible(false);
        }
        return recentItem1;
    }
    /**
     * @return Returns the recentItem2.
     */
    public CommandableMenuItem getRecentItem2()
    {
        if( recentItem2 == null)
        {
            recentItem2 = new CommandableMenuItem();
            recentItem2.setFont(f);
            recentItem2.setText("");
            recentItem2.setVisible(false);
        }
        return recentItem2;
    }
    /**
     * @return Returns the recentItem3.
     */
    public CommandableMenuItem getRecentItem3()
    {
        if( recentItem3 == null)
        {
            recentItem3 = new CommandableMenuItem();
            recentItem3.setFont(f);
            recentItem3.setText("");
            recentItem3.setVisible(false);
        }
        return recentItem3;

    }
    /**
     * @return Returns the recentItem4.
     */
    public CommandableMenuItem getRecentItem4()
    {
        if( recentItem4 == null)
        {
            recentItem4 = new CommandableMenuItem();
            recentItem4.setFont(f);
            recentItem4.setText("");
            recentItem4.setVisible(false);
        }
        return recentItem4;
    }
    /**
     * @return Returns the recentItems.
     */
    public Object[] getRecentItems()
    {
        return recentItems;
    }
}
