package com.cannontech.clientutils.commander;

import java.util.List;

import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteTOUSchedule;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Insert the type's description here.
 * Creation date: (4/11/2002 3:51:40 PM)
 * @author: 
 */
public class DownloadTOUSchedulePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener 
{
	private javax.swing.JDialog dialog = null;
	
	private javax.swing.JButton ivjDownloadButton = null;
	private javax.swing.JPanel ivjDownloadTOUContentPane = null;
	private javax.swing.JComboBox ivjTOUScheduleComboBox = null;
	private javax.swing.JLabel ivjSelectTOUScheduleLabel = null;
	private javax.swing.JButton ivjCancelButton = null;
	private javax.swing.JLabel ivjDeviceLabel = null;
	private javax.swing.JTextField ivjDeviceNameTextField = null;
	
	private int CANCEL = 0;
	private int DOWNLOAD = 1;
	private int buttonPushed = CANCEL;
	
	/**
	 * DownloadTOUSchedulePanel constructor comment.
	 */
	public DownloadTOUSchedulePanel() {
		super();
		initialize();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (5/17/2002 11:49:08 AM)
	 * @param source java.awt.event.ActionEvent
	 */
	public void actionPerformed(java.awt.event.ActionEvent event)
	{
		if( event.getSource() == getDownloadButton())
		{
			setButtonPushed(DOWNLOAD);
			exit();
		}				
		else if( event.getSource() == getCancelButton())
		{
			setButtonPushed(CANCEL);
			exit();
		}
	
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/3/2002 4:00:35 PM)
	 */
	public void exit() 
	{
		removeAll();
		setVisible(false);
		dialog.dispose();
	}
	/**
	 * Return the Panel1 property value.
	 * @return java.awt.Panel
	 */
	private javax.swing.JPanel getDownloadTOUContentPane() {
		if (ivjDownloadTOUContentPane == null) {
			try {
				ivjDownloadTOUContentPane = new javax.swing.JPanel();
				ivjDownloadTOUContentPane.setName("DownloadTOUContentPane");
				ivjDownloadTOUContentPane.setLayout(new java.awt.GridBagLayout());
	
				java.awt.GridBagConstraints constraintsSelectTOUScheduleLabel = new java.awt.GridBagConstraints();
				constraintsSelectTOUScheduleLabel.gridx = 0; constraintsSelectTOUScheduleLabel.gridy = 2;
				constraintsSelectTOUScheduleLabel.anchor = java.awt.GridBagConstraints.WEST;
				constraintsSelectTOUScheduleLabel.insets = new java.awt.Insets(5, 5, 5, 5);
				getDownloadTOUContentPane().add(getSelectTOUScheduleLabel(), constraintsSelectTOUScheduleLabel);
	
				java.awt.GridBagConstraints constraintsRouteComboBox = new java.awt.GridBagConstraints();
				constraintsRouteComboBox.gridx = 0; constraintsRouteComboBox.gridy = 3;
				constraintsRouteComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsRouteComboBox.weightx = 1.0;
				constraintsRouteComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
				getDownloadTOUContentPane().add(getTOUScheduleComboBox(), constraintsRouteComboBox);
	
				java.awt.GridBagConstraints constraintsLocateButton = new java.awt.GridBagConstraints();
				constraintsLocateButton.gridx = 1; constraintsLocateButton.gridy = 3;
				constraintsLocateButton.insets = new java.awt.Insets(5, 5, 5, 5);
				getDownloadTOUContentPane().add(getDownloadButton(), constraintsLocateButton);
	
				java.awt.GridBagConstraints constraintsCancelButton = new java.awt.GridBagConstraints();
				constraintsCancelButton.gridx = 1; constraintsCancelButton.gridy = 4;
				constraintsCancelButton.insets = new java.awt.Insets(5, 5, 5, 5);
				getDownloadTOUContentPane().add(getCancelButton(), constraintsCancelButton);
	
				java.awt.GridBagConstraints constraintsDeviceLabel = new java.awt.GridBagConstraints();
				constraintsDeviceLabel.gridx = 0; constraintsDeviceLabel.gridy = 0;
				constraintsDeviceLabel.anchor = java.awt.GridBagConstraints.WEST;
				constraintsDeviceLabel.insets = new java.awt.Insets(5, 5, 5, 5);
				getDownloadTOUContentPane().add(getDeviceLabel(), constraintsDeviceLabel);
	
				java.awt.GridBagConstraints constraintsDeviceNameTextField = new java.awt.GridBagConstraints();
				constraintsDeviceNameTextField.gridx = 0; constraintsDeviceNameTextField.gridy = 1;
				constraintsDeviceNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsDeviceNameTextField.anchor = java.awt.GridBagConstraints.WEST;
				constraintsDeviceNameTextField.weightx = 1.0;
				constraintsDeviceNameTextField.insets = new java.awt.Insets(5, 5, 5, 5);
				getDownloadTOUContentPane().add(getDeviceNameTextField(), constraintsDeviceNameTextField);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjDownloadTOUContentPane;
	}
	/**
	 * Return the TOUScheduleComboBox property value.
	 * @return javax.swing.JComboBox
	 */
	public javax.swing.JComboBox getTOUScheduleComboBox() {
		if (ivjTOUScheduleComboBox == null) {
			try {
				ivjTOUScheduleComboBox  = new javax.swing.JComboBox();
				ivjTOUScheduleComboBox.setName("TOUScheduleComboBox");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjTOUScheduleComboBox;
	}
	/**
	 * Return the SelectRouteLabel property value.
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getSelectTOUScheduleLabel() {
		if (ivjSelectTOUScheduleLabel == null) {
			try {
			    ivjSelectTOUScheduleLabel = new javax.swing.JLabel();
			    ivjSelectTOUScheduleLabel.setName("SelectTOUScheduleLabel");
			    ivjSelectTOUScheduleLabel.setText("Select TOU Schedule to Download.");
			    ivjSelectTOUScheduleLabel.setRequestFocusEnabled(false);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjSelectTOUScheduleLabel;
	}
	/**
	 * Return the CancelButton property value.
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getCancelButton() {
		if (ivjCancelButton == null) {
			try {
				ivjCancelButton = new javax.swing.JButton();
				ivjCancelButton.setName("CancelButton");
				ivjCancelButton.setText("Cancel");

				ivjCancelButton.addActionListener(this);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjCancelButton;
	}
	/**
	 * Return the DeviceLabel property value.
	 * @return javax.swing.JLabel
	 */
	public javax.swing.JLabel getDeviceLabel() {
		if (ivjDeviceLabel == null) {
			try {
				ivjDeviceLabel = new javax.swing.JLabel();
				ivjDeviceLabel.setName("DeviceLabel");
				ivjDeviceLabel.setText("Selected Device:");

			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjDeviceLabel;
	}
	/**
	 * Return the DeviceNameTextField property value.
	 * @return javax.swing.JTextField
	 */
	public javax.swing.JTextField getDeviceNameTextField() {
		if (ivjDeviceNameTextField == null) {
			try {
				ivjDeviceNameTextField = new javax.swing.JTextField();
				ivjDeviceNameTextField.setName("DeviceNameTextField");
				ivjDeviceNameTextField.setEditable(false);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjDeviceNameTextField;
	}
	/**
	 * Return the LocateButton property value.
	 * @return javax.swing.JButton
	 */
	public javax.swing.JButton getDownloadButton() {
		if (ivjDownloadButton== null) {
			try {
				ivjDownloadButton = new javax.swing.JButton();
				ivjDownloadButton.setName("DownloadButton");
				ivjDownloadButton.setText("Download");
				
				ivjDownloadButton.addActionListener(this);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjDownloadButton;
	}

	
	
	
	
	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {
	
		/* Uncomment the following lines to print uncaught exceptions to stdout */
		 com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
		 exception.printStackTrace(System.out);
	}
	/**
	 * Initialize the class.
	 */
	private void initialize() {
		try {
			//set the app to start as close to the center as you can....
			//  only works with small gui interfaces.
			java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation((int)(d.width * .3),(int)(d.height * .2));
			setName("DownloadTOUScheduleFrame");
			setSize(379, 283);
			setVisible(true);
		}
		catch (java.lang.Throwable ivjExc)
		{
			handleException(ivjExc);
		}
	}
	/**
	 * main entrypoint - starts the part when it is run as an application
	 * @param args java.lang.String[]
	 */
	public static void main(java.lang.String[] args)
	{
		try
		{
			DownloadTOUSchedulePanel aAdvancedOptionsPanel;
			aAdvancedOptionsPanel = new DownloadTOUSchedulePanel();
			aAdvancedOptionsPanel.showDownloadOptions(new javax.swing.JFrame());
		}
		catch (Throwable exception)
		{
			System.err.println("Exception occurred in main() of javax.swing.JDialog");
			exception.printStackTrace(System.out);
		}
	}
	/**
	 * @see com.cannontech.common.gui.util.DataInputPanel#getValue(Object)
	 */
	public Object getValue(Object o)
	{
		return getTOUScheduleComboBox().getSelectedItem();
	}

	/**
	 * @see com.cannontech.common.gui.util.DataInputPanel#setValue(Object)
	 */
	public void setValue(Object o)
	{
		if ( o == null || !(o instanceof LiteTOUSchedule))
			return;
		//Really, there is not much to do here, I suppose we could allow for setting of the selected schedule?
	}
	/**
	 * Show AdvancedOptionsPanel with a JDialog to control the closing time.
	 * @param parent javax.swing.JFrame
	 */
	public Object showDownloadOptions(javax.swing.JFrame parent)
	{
		dialog = new javax.swing.JDialog(parent);
		dialog.setTitle("Download TOU Schedule");
		dialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setModal(true);
		dialog.setContentPane(getDownloadTOUContentPane());
		dialog.getContentPane().add(this);
		dialog.setSize(379, 283);
		
		// Add a keyListener to the Escape key.
		javax.swing.KeyStroke ks = javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0, true);
		dialog.getRootPane().getInputMap().put(ks, "CloseAction");
		dialog.getRootPane().getActionMap().put("CloseAction", new javax.swing.AbstractAction()
		{
			public void actionPerformed(java.awt.event.ActionEvent ae)
			{
				setButtonPushed(CANCEL);
				exit();
			}
		});
		
		// Add a window closeing event, even though I think it's already handled by setDefaultCloseOperation(..)
		dialog.addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent e)
			{
				setButtonPushed(CANCEL);
				exit();
			};
		});
//		set the app to start as close to the center as you can....
		//  only works with small gui interfaces.
		java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int)(d.width * .3),(int)(d.height * .2));
			
		dialog.show();
		if( getButtonPushed() == this.DOWNLOAD)
			return getValue(null);
		else
			return null;
	}
	/**
	 * Returns the buttonPushed.
	 * @return int
	 */
	private int getButtonPushed()
	{
		return buttonPushed;
	}

	/**
	 * Sets the buttonPushed.
	 * @param buttonPushed The buttonPushed to set
	 */
	private void setButtonPushed(int buttonPushed)
	{
		this.buttonPushed = buttonPushed;
	}
	public void addItems()
	{
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        List scheds = cache.getAllTOUSchedules();
        getTOUScheduleComboBox().removeAllItems();
        if( scheds != null)
        {
			for ( int i = 0; i < scheds.size(); i++)
			{
				getTOUScheduleComboBox().addItem(scheds.get(i));
			}
			if( getTOUScheduleComboBox().getItemCount() > 0 )
				getTOUScheduleComboBox().setSelectedIndex(0);
		}
	}
}
