package com.cannontech.common.wizard;

import java.util.Vector;

import com.cannontech.common.editor.EditorInputValidationException;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.database.db.DBPersistent;

public abstract class WizardPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.DataInputPanelListener, java.awt.event.ActionListener {
	private static final int MAX_PANELS = 20;
	private DataInputPanel[] inputPanels  = new DataInputPanel[MAX_PANELS];
	public int currentPanel = -1;
	private WizardButtonPanel buttonPanel;
	private javax.swing.JLabel headerLabel;
    protected int currentDatabase=0;
	private Vector<WizardPanelListener> listeners = new Vector<WizardPanelListener>();

	public WizardPanel() {
    	super();
    	initialize();
    }
    
    public WizardPanel(int flagDB) {
        super();
        currentDatabase = flagDB;
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
    
    public void addWizardPanelListener(WizardPanelListener listener) {
    
    	if( !this.listeners.contains( listener ) )
    	{
    		this.listeners.addElement( listener );
    	}	
    }
    
    public void fireWizardPanelEvent(WizardPanelEvent e ) {
    
    	for( int i = this.listeners.size()-1; i >= 0; i-- )
    	{
    		((WizardPanelListener) this.listeners.elementAt(i)).selectionPerformed( e );
    	}
    }
    
    public abstract java.awt.Dimension getActualSize();
    
    private javax.swing.JLabel getHeaderLabel() {
    	if( this.headerLabel == null )
    	{
    		this.headerLabel = new javax.swing.JLabel( getHeaderText() );
    		this.headerLabel.setFont( new java.awt.Font( "dialog", 1, 24) );
    		this.headerLabel.setForeground( java.awt.SystemColor.textHighlight );
    	}
    
    	return this.headerLabel;
    }
    
    protected abstract String getHeaderText();
    
    protected DataInputPanel[] getInputPanels() {
    	return this.inputPanels;
    }
    
    /**
     * Retrieves the next input panel of this wizard.
     * @param currentInputPanel The current panel, null if retrieving the first panel.
     * @return DataInputPanel The next input panel.
     */
    protected abstract DataInputPanel getNextInputPanel(DataInputPanel currentInputPanel);
    
    public Object getValue(Object o) throws CancelInsertException{
    	
    	for( int i = 0; (i < this.inputPanels.length) && (i <= this.currentPanel); i++ )
    	{
    		DataInputPanel p = this.inputPanels[i];
    
    		if( p != null )
    		{
    			try {
                    o = p.getValue(o);
                } catch (EditorInputValidationException e) {
                    if (e.getMessage() != "")
                        javax.swing.JOptionPane.showMessageDialog( p, 
                                                                   e.getMessage(), 
                                                                   "Input Error", 
                                                                   javax.swing.JOptionPane.WARNING_MESSAGE );
             
                    throw new CancelInsertException();
                }
    		}
    		else
    		{
    			//we've reached the end get out of here
    			break;
    		}
    	}
    	
    	return o;	
    }
    
    @Override
    public void postSave(DBPersistent o) {
        for (DataInputPanel p : inputPanels) {
            if (p != null) {
                p.postSave(o);
            }
        }
    }
    
    protected WizardButtonPanel getWizardButtonPanel() {
    	if( this.buttonPanel == null )
    	{
    		this.buttonPanel = new WizardButtonPanel();
    	}
    	
    	return this.buttonPanel;	
    }
    
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
    
    public void inputUpdate(PropertyPanelEvent event) {
    
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
    	if( isBackButtonSupported((DataInputPanel) event.getSource()) )
    	{
    		setBackEnabled(false);
    	}
    	else
    	{
    		setBackEnabled(true);
    	}
    
    
    	revalidate();
    	repaint();
    	
    	fireWizardPanelEvent(
    			new WizardPanelEvent( 
    					event.getSource(), 
    					event.getID(), 
    					event.getDataChanged() )  );
    }

    // Override to control when back button is enabled.
    // By default it is disabled for the first panel only
    protected boolean isBackButtonSupported(DataInputPanel panel) {
        return currentPanel == 0;
    }
    
    protected abstract boolean isLastInputPanel(DataInputPanel currentPanel);
    
    public void removeWizardPanelListener(WizardPanelListener listener) {
    
    	if( this.listeners.contains( listener ) )
    	{
    		this.listeners.removeElement( listener );
    	}
    }
    
    protected void setBackEnabled(boolean val) {
    	getWizardButtonPanel().getBackButton().setEnabled(val);
    }
    
    protected void setBackVisible(boolean val) {
    	getWizardButtonPanel().getBackButton().setVisible(val);
    }
    
    protected void setCancelEnabled(boolean val) {
    	getWizardButtonPanel().getCancelButton().setEnabled(val);
    }
    
    protected void setCancelVisible(boolean val) {
    	getWizardButtonPanel().getCancelButton().setVisible(val);
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
    
    protected void setFinishedEnabled(boolean val) {
    	getWizardButtonPanel().getFinishButton().setEnabled(val);
    }
    
    protected void setFinishedVisible(boolean val) {
    	
    	if( val )
    		getWizardButtonPanel().showFinishButton();
    	else
    		getWizardButtonPanel().showNextButton();
    }
    
    protected void setNextAndFinishedVisible(boolean val) {
    	getWizardButtonPanel().getFinishButton().setVisible(val);
    	getWizardButtonPanel().getNextButton().setVisible(val);
    }
    
    protected void setNextEnabled(boolean val) {
    	getWizardButtonPanel().getNextButton().setEnabled(val);
    }
    
    protected void setNextVisible(boolean val) {
    	
    	setFinishedVisible(!val);
    }
    
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
    
    public Vector<WizardPanelListener> getListeners() {
    	return listeners;
    }
    
    public void setInputPanels(DataInputPanel[] inputPanels) {
    	this.inputPanels = inputPanels;
    }
}