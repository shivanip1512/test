package com.cannontech.common.editor;

/**
 * This type was created in VisualAge.
 */

import java.awt.Component;
import java.util.Vector;

import javax.swing.JTabbedPane;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.database.db.DBPersistent;

public abstract class PropertyPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.DataInputPanelListener, java.awt.event.ActionListener 
{
	private JTabbedPane tabbedPane;
	private PropertyButtonPanel buttonPanel;
	private boolean cancelled = false;
	private boolean hasChanged = false;
	private Vector listeners = new Vector();
	private Object originalObjectToEdit;

	//a value that is set to let us know if more than 1
	//type of data needs to be updated after the pressing
	//of the Ok/Apply button.
	private boolean hasMultipleUpdates = false;

	/**
	 * PropertyPanel constructor comment.
	 */
	public PropertyPanel() {
		super();
		initialize();
	} 
	/**
	 * This method was created in VisualAge.
	 * @param newEvent java.awt.event.ActionEvent
	 */
	public void actionPerformed(java.awt.event.ActionEvent newEvent) {
	
		//Change the cursor in case this takes a while
		java.awt.Frame owner = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
		java.awt.Cursor savedCursor = null;
		if( owner != null )
			savedCursor = owner.getCursor();
			
		try
		{
			if( owner != null ) {
				owner.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
			}
			 
			if( newEvent.getSource() == getPropertyButtonPanel().getOkJButton() ) {
				firePropertyPanelEvent( new PropertyPanelEvent(this, PropertyPanelEvent.OK_SELECTION ) );

			} else if( newEvent.getSource() == getPropertyButtonPanel().getCancelJButton() ) {
				this.cancelled = true;
				firePropertyPanelEvent( new PropertyPanelEvent( this, PropertyPanelEvent.CANCEL_SELECTION ) );

			} else if( newEvent.getSource() == getPropertyButtonPanel().getApplyJButton() ) {
				firePropertyPanelEvent( new PropertyPanelEvent( this, PropertyPanelEvent.APPLY_SELECTION ) );
			}
		} catch(Exception e) {
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		} finally {
			if( owner != null ) {
				owner.setCursor( savedCursor );
			}
		}
	}
	
	/**
	 * This method was created in VisualAge.
	 * @param listener com.cannontech.common.editor.PropertyPanelListener
	 */
	public void addPropertyPanelListener(PropertyPanelListener listener) {
	
		if( !this.listeners.contains( listener ) )
		{
			this.listeners.addElement( listener );
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/15/2001 10:01:29 AM)
	 */
	public void fireCancelButtonPressed() 
	{
		javax.swing.SwingUtilities.invokeLater( new Runnable()
		{
			public void run()
			{
				java.awt.event.ActionEvent newEvent = new java.awt.event.ActionEvent( getPropertyButtonPanel().getCancelJButton(),
										java.awt.event.ActionEvent.ACTION_PERFORMED,
										"cancelButtonPressed");
				actionPerformed( newEvent );
			}	
			
		});
		
	}
	
	public void fireApplyButtonPressed() 
	{
		javax.swing.SwingUtilities.invokeLater( new Runnable()
		{
			public void run()
			{
				java.awt.event.ActionEvent newEvent = new java.awt.event.ActionEvent( getPropertyButtonPanel().getApplyJButton(),
										java.awt.event.ActionEvent.ACTION_PERFORMED,
										"applyButtonPressed");
				actionPerformed( newEvent );
			}	
			
		});
		
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (3/15/2001 10:01:29 AM)
	 */
	public void fireOkButtonPressed() 
	{
		javax.swing.SwingUtilities.invokeLater( new Runnable()
		{
			public void run()
			{
				java.awt.event.ActionEvent newEvent = new java.awt.event.ActionEvent( getPropertyButtonPanel().getOkJButton(),
										java.awt.event.ActionEvent.ACTION_PERFORMED,
										"okButtonPressed");
				actionPerformed( newEvent );
			}	
			
		});
		
	}
	/**
	 * This method was created in VisualAge.
	 * @param e com.cannontech.common.editor.PropertyPanelEvent
	 */
	public void firePropertyPanelEvent(PropertyPanelEvent e) {
	
		for( int i = this.listeners.size()-1; i >= 0; i-- )
		{
			((PropertyPanelListener) this.listeners.elementAt(i)).selectionPerformed( e );
		}
	}
	/**
	 * This method must be implemented in subclasses.
	 * @return com.cannontech.common.gui.util.InputPanel[]
	 */
	protected abstract DataInputPanel[] getInputPanels();
	/**
	 * Insert the method's description here.
	 * Creation date: (6/8/2001 4:25:20 PM)
	 * @return java.lang.Object
	 */
	public java.lang.Object getOriginalObjectToEdit() {
		return originalObjectToEdit;
	}
	/**
	 * This method was created in VisualAge.
	 * @return com.cannontech.common.gui.util.PropertyButtonPanel
	 */
	public PropertyButtonPanel getPropertyButtonPanel() 
	{
		
		if( this.buttonPanel == null )
			this.buttonPanel = new PropertyButtonPanel();
			 
		return this.buttonPanel;
	}
	/**
	 * This method was created in VisualAge.
	 * @return javax.swing.JTabbedPane
	 */
	protected JTabbedPane getTabbedPane()
	{
		if( this.tabbedPane == null )
			this.tabbedPane = new JTabbedPane();
	
		return this.tabbedPane;
	}
	/**
	 * This method must be implementd by subclasses to provide the strings
	 * to represent each tab.
	 * @return java.lang.String
	 */
	protected abstract String[] getTabNames();
	
	public void disposeValue() {
        for( int i = 0; i < getTabbedPane().getTabCount(); i++ ) {
            DataInputPanel tab = (DataInputPanel) getTabbedPane().getComponentAt(i);
            if (tab != null) {
                tab.disposeValue();
            }
        }
	}
	
	/**
	 * getValue method comment.
	 * @throws EditorInputValidationException 
	 */
	public Object getValue(Object o) throws EditorInputValidationException
	{
		//If cancel has been pressed don't giv'em a thing
	 	if( this.cancelled )
			return null;
	
		try
		{
			o = com.cannontech.common.util.CtiUtilities.copyObject( originalObjectToEdit );
			int savedIndex = getTabbedPane().getSelectedIndex();
			
			for( int i = 0; i < getTabbedPane().getTabCount(); i++ )
			{
				getTabbedPane().setSelectedIndex(i);
			 	o = ((DataInputPanel) getTabbedPane().getSelectedComponent()).getValue( o );
			}
	
			getTabbedPane().setSelectedIndex( savedIndex );
		}
		catch( java.io.IOException e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		
		return o;
	}
    
    @Override
    public void postSave(DBPersistent o) {
        for (DataInputPanel panel : getInputPanels()) {
            panel.postSave(o);
        }
    }
    
	/**
	 * This method was created in VisualAge.
	 * @return boolean
	 */
	public boolean hasChanged() {
		return this.hasChanged;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/29/2001 3:33:47 PM)
	 * @return boolean
	 */
	public boolean hasMultipleUpdates() {
		return hasMultipleUpdates;
	}
	/**
	 * This method was created in VisualAge.
	 */
	private void initialize() {
		
		getTabbedPane().setFont( new java.awt.Font("dialog", 0, 14) );
		
		getPropertyButtonPanel().getOkJButton().addActionListener(this);
		getPropertyButtonPanel().getCancelJButton().addActionListener(this);
		getPropertyButtonPanel().getApplyJButton().addActionListener(this);
	
	}
	
	/**
	 * This method was created in VisualAge.
	 * @param event DataInputPanelEvent
	 */
	public void inputUpdate( PropertyPanelEvent event) 
	{
		setChanged(true);
		
		//special case events handled below
		if( event.getID() == PropertyPanelEvent.EVENT_FORCE_APPLY)
		{
			fireApplyButtonPressed();
		}	
		else if( event.getID() == PropertyPanelEvent.EVENT_FORCE_CANCEL)
		{
			fireCancelButtonPressed();
		}	
		else if( event.getID() == PropertyPanelEvent.EVENT_FORCE_OK )
		{
			fireOkButtonPressed();
		}	
		else
		{
			//just forward the event on as is
			java.awt.Frame owner = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
			java.awt.Cursor savedCursor = null;
			if( owner != null )
				savedCursor = owner.getCursor();
				
			try
			{
				if( owner != null )
					owner.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );

				firePropertyPanelEvent( event );
			}
			catch(Exception e)
			{
				com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			}
			finally
			{
				if( owner != null )
					owner.setCursor( savedCursor );
			}
			
		}
		
		
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/12/2001 2:49:24 PM)
	 * @return boolean
	 *
	 * Checks all the dataInputPanels isInputValid() routines to see if any
	 * are false.
	 */
	public boolean isInputValid() 
	{
		if( getInputPanels() != null )
		{
			for( int i = 0; i < getInputPanels().length; i++ )
			{
				// check all the input panels for the validity of the input data
				if( getInputPanels()[i].isInputValid() )			
					continue;
				else
				{
					//only show errors that have text!
					if( getInputPanels()[i].getErrorString() == null )
						setErrorString( null );
					else
						setErrorString( "The '" + getTabNames()[i] + "' panel had the following error(s): \n   -> " + getInputPanels()[i].getErrorString() );
					
					return false;
				}
			}
		}
		else
			throw new RuntimeException("(null) was found for the inputPanel array in " + getClass().getName() );
	
		return true;
	}
	/**
	 * This method was created in VisualAge.
	 * @param listener com.cannontech.common.editor.PropertyPanelListener
	 */
	public void removePropertyPanelListener(PropertyPanelListener listener) {
	
		if( this.listeners.contains( listener ) )
		{
			this.listeners.addElement( listener );
		}
	}
	/**
	 * This method was created in VisualAge.
	 * @param val boolean
	 */
	public void setChanged(boolean val) {
	
		getPropertyButtonPanel().getApplyJButton().setEnabled(val);
		this.hasChanged = val;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/29/2001 3:33:47 PM)
	 * @param newHasMultipleUpdates boolean
	 */
	protected void setHasMultipleUpdates(boolean newHasMultipleUpdates) {
		hasMultipleUpdates = newHasMultipleUpdates;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/8/2001 4:25:20 PM)
	 * @param newOriginalObjectToEdit java.lang.Object
	 */
	protected void setOriginalObjectToEdit(java.lang.Object newOriginalObjectToEdit) {
		originalObjectToEdit = newOriginalObjectToEdit;
	}
	/**
	 * setValue.
	 */
	public void setValue(Object val) 
	{
		setOriginalObjectToEdit( val );
	
		
		String[] tabNames = getTabNames();
		DataInputPanel[] inputPanels = getInputPanels();
		
		if( tabNames.length != inputPanels.length )
			throw new Error("Property Panel:  Difference in the number of tab identifiers and tabs - implement getTabNames() and getInputPanels() to return the same number of items");
	
		//only add the tabs if we have 0
		if( getTabbedPane().getTabRunCount() == 0 )
		{
			for( int i = 0; i < tabNames.length; i++ )
			{
				
		 		getTabbedPane().addTab( tabNames[i], inputPanels[i] );
			
				inputPanels[i].addDataInputPanelListener( this );
			}
		}
	
		
		for( int i = 0; i < getTabbedPane().getTabCount(); i++ )
		{
			getTabbedPane().setSelectedIndex(i);
			
			((DataInputPanel) getTabbedPane().getSelectedComponent()).setValue(val);
		}
	
		getTabbedPane().setSelectedIndex(0);
		this.removeAll();
		this.setLayout(new java.awt.BorderLayout() );
		add( getTabbedPane(), "Center") ;
		add( getPropertyButtonPanel(), "South");
		
		setChanged(false);
	
		return;
	}
}
