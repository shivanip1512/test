package com.cannontech.common.wizard;

/**
 * This type was created in VisualAge.
 */
import java.util.Vector;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.DataInputPanelEvent;

public abstract class WizardPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.DataInputPanelListener, java.awt.event.ActionListener {
	//10 should be good enough...?
	private static final int MAX_PANELS = 10;
	private DataInputPanel[] inputPanels  = new DataInputPanel[MAX_PANELS];
	public int currentPanel = -1;
	private WizardButtonPanel buttonPanel;
	private javax.swing.JLabel headerLabel;
	private boolean cancelled = false;
	private Vector listeners = new Vector();
/**
 * WizardPanel constructor comment.
 */
public WizardPanel() {
	super();
	initialize();
}
/**
 * This method handles an ActionEvent from one of the wizard buttons and takes
 * the appropriate action.  In all cases WizardPanelListeners are notified.
 * @param newEvent java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent newEvent) {

	//Change the cursor in case this takes a while
	java.awt.Frame owner = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
	java.awt.Cursor savedCursor = owner.getCursor();
	try
	{
		owner.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
		
		if( newEvent.getSource() == getWizardButtonPanel().getBackButton() )
		{
			//this.currentPanel must == one at least to go backwards (zero is the first panel)
			if( this.currentPanel > 0 )
			{
				setCurrentInputPanel( this.inputPanels[--this.currentPanel]);
			}
			else
			{
				throw new Error("WizardPanel - no previous InputPanel available");
			}

			//Let the world know
			fireWizardPanelEvent( new WizardPanelEvent( this, WizardPanelEvent.BACK_SELECTION) );
		}
		else
		if( newEvent.getSource() == getWizardButtonPanel().getNextButton() )
		{
			//At this point there should already be at least one input panel
			if( this.currentPanel > -1 )
			{
				DataInputPanel nextPanel = getNextInputPanel( this.inputPanels[ this.currentPanel]);

				if( nextPanel != null )
				{
					inputPanels[++this.currentPanel] = nextPanel;
					setCurrentInputPanel( inputPanels[this.currentPanel]);	
				}
				else
				{
					throw new Error("WizardPanel - next panel not available");
				}
			}

			//Let the world know
			fireWizardPanelEvent( new WizardPanelEvent( this, WizardPanelEvent.NEXT_SELECTION) );
		}
		else
		if( newEvent.getSource() == getWizardButtonPanel().getFinishButton() )
		{
			//Whomever is listening will handle what to do next
			fireWizardPanelEvent( new WizardPanelEvent( this, WizardPanelEvent.FINISH_SELECTION ) );
		}
		else
		if( newEvent.getSource() == getWizardButtonPanel().getCancelButton() )
		{
			cancelled = true;

			fireWizardPanelEvent( new WizardPanelEvent( this, WizardPanelEvent.CANCEL_SELECTION ) );

			
		}
	}
	catch(Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		owner.setCursor( savedCursor);
	}
}
/**
 * This method was created in VisualAge.
 * @param listener com.cannontech.common.editor.WizardPanelListener
 */
public void addWizardPanelListener(WizardPanelListener listener) {

	if( !this.listeners.contains( listener ) )
	{
		this.listeners.addElement( listener );
	}	
}
/**
 * This method was created in VisualAge.
 * @param e com.cannontech.common.editor.WizardPanelEvent
 */
public void fireWizardPanelEvent(WizardPanelEvent e ) {

	for( int i = this.listeners.size()-1; i >= 0; i-- )
	{
		((WizardPanelListener) this.listeners.elementAt(i)).selectionPerformed( e );
	}
}
/**
 * This method was created in VisualAge.
 * @return Dimension
 */
/* This method sets the size of each class that
/* has WizardPanel as its super whenever its called  */
/*   ex:

		public java.awt.Dimension getActualSize() 
		{
			setPreferredSize( new java.awt.Dimension(430, 480) );

			return getPreferredSize();
		}

*/
public abstract java.awt.Dimension getActualSize();
/**
 * This method was created in VisualAge.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getHeaderLabel() {
	if( this.headerLabel == null )
	{
		this.headerLabel = new javax.swing.JLabel( getHeaderText() );
		this.headerLabel.setFont( new java.awt.Font( "dialog", 1, 24) );
		this.headerLabel.setForeground( java.awt.SystemColor.textHighlight );
	}

	return this.headerLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
protected abstract String getHeaderText();
/**
 * This method was created in VisualAge.
 * @return java.util.Stack
 */
protected DataInputPanel[] getInputPanels()
{
	return this.inputPanels;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.common.gui.util.InputPanel
 * @param currentInputPanel com.cannontech.common.gui.util.InputPanel
 */
protected abstract DataInputPanel getNextInputPanel(DataInputPanel currentInputPanel);
/**
 * getValue method comment.
 */
public Object getValue(Object o) {
	
	for( int i = 0; (i < this.inputPanels.length) && (i <= this.currentPanel); i++ )
	{
		DataInputPanel p = this.inputPanels[i];

		if( p != null )
		{
			o = p.getValue(o);
		}
		else
		{
			//we've reached the end get out of here
			break;
		}
	}
	
	return o;	
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.common.gui.util.WizardButtonPanel
 */
protected WizardButtonPanel getWizardButtonPanel() {
	if( this.buttonPanel == null )
	{
		this.buttonPanel = new WizardButtonPanel();
	}
	
	return this.buttonPanel;	
}
/**
 * This method was created in VisualAge.
 */
private void initialize() {

	setLayout( new java.awt.BorderLayout() );
	
	getWizardButtonPanel().getBackButton().addActionListener(this);
	getWizardButtonPanel().getNextButton().addActionListener(this);
	getWizardButtonPanel().getFinishButton().addActionListener(this);
	getWizardButtonPanel().getCancelButton().addActionListener(this);

	
	DataInputPanel nextPanel = getNextInputPanel( null);
		
	if( nextPanel != null )
	{
		inputPanels[++this.currentPanel] = nextPanel;
		setCurrentInputPanel( inputPanels[this.currentPanel]);	
	}
	else
	{
		throw new Error("WizardPanel - next panel not available");
	}
}
/**
 * This method was created in VisualAge.
 * @param event DataInputPanelEvent
 */
public void inputUpdate(DataInputPanelEvent event) {

	if( currentPanel == -1 )
		return;

	//Every panel has a cancel enabled
	setCancelEnabled(true);

	//Is this the last panel?
	if( isLastInputPanel( (DataInputPanel) event.getSource() ) )
	{
		setFinishedVisible(true);

		if( ((DataInputPanel) event.getSource()).isInputValid() )
		{
			setFinishedEnabled(true);
		}
		else
		{
			setFinishedEnabled(false);
		}
	}
	else
	{
		setNextVisible(true);

		if( ((DataInputPanel) event.getSource()).isInputValid() )
		{
			setNextEnabled(true);
		}
		else
		{
			setNextEnabled(false);
		}
	}
		
	//First Panel - no back button enabled
	if( currentPanel == 0 )
	{
		setBackEnabled(false);	
	}
	else
	{
		setBackEnabled(true);
	}

	revalidate();
	repaint();
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param currentPanel com.cannontech.common.gui.util.DataInputPanel
 */
protected abstract boolean isLastInputPanel(DataInputPanel currentPanel);
/**
 * This method was created in VisualAge.
 * @param listener com.cannontech.common.editor.WizardPanelListener
 */
public void removeWizardPanelListener(WizardPanelListener listener) {

	if( this.listeners.contains( listener ) )
	{
		this.listeners.removeElement( listener );
	}
}
/**
 * This method was created in VisualAge.
 * @param val boolean
 */
protected void setBackEnabled(boolean val) {
	getWizardButtonPanel().getBackButton().setEnabled(val);
}
/**
 * This method was created in VisualAge.
 * @param val boolean
 */
protected void setCancelEnabled(boolean val) {
	getWizardButtonPanel().getCancelButton().setEnabled(val);
}
/**
 * This method was created in VisualAge.
 * @param current com.cannontech.common.gui.util.InputPanel
 */
private void setCurrentInputPanel( DataInputPanel current) {

	current.addDataInputPanelListener(this);
	
	//Make sure all is set up before we add the panel
	current.fireInputUpdate();
	
	removeAll();

	add( getHeaderLabel(), "North" );
	add( current, "Center" ); 
	add( getWizardButtonPanel(), "South" );

	revalidate();
	repaint();
}
/**
 * This method was created in VisualAge.
 * @param val boolean
 */
protected void setFinishedEnabled(boolean val) {
	getWizardButtonPanel().getFinishButton().setEnabled(val);
}
/**
 * This method was created in VisualAge.
 * @param val boolean
 */
protected void setFinishedVisible(boolean val) {
	
	if( val )
		getWizardButtonPanel().showFinishButton();
	else
		getWizardButtonPanel().showNextButton();
}
/**
 * This method was created in VisualAge.
 * @param val boolean
 */
protected void setNextEnabled(boolean val) {
	getWizardButtonPanel().getNextButton().setEnabled(val);
}
/**
 * This method was created in VisualAge.
 * @param val boolean
 */
protected void setNextVisible(boolean val) {
	
	setFinishedVisible(!val);
}
/**
 * setValue method comment.
 */
public void setValue(Object val) {

	for( int i = 0; i < this.inputPanels.length; i++ )
	{
		DataInputPanel p = this.inputPanels[i];

		if( p != null )
		{
			p.setValue( val );
		}
		else
		{
			//We've hit the last InputPanel
			break;
		}
	}
	
}
}
