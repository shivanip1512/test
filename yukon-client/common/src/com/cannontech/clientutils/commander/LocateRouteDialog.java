package com.cannontech.clientutils.commander;

import java.awt.Frame;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.cache.DBChangeLiteListener;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.spring.YukonSpringHook;

/**
 * Insert the type's description here.
 * Creation date: (2/25/2002 3:46:52 PM)
 * @author: 
 */
public class LocateRouteDialog extends javax.swing.JDialog implements java.awt.event.ActionListener, DBChangeLiteListener
{
	private javax.swing.JButton ivjLocateButton = null;
	private javax.swing.JPanel ivjLocateRouteContentPane = null;
	private javax.swing.JLabel ivjResultsLabel = null;
	private javax.swing.JComboBox ivjRouteComboBox = null;
	private javax.swing.JLabel ivjSelectRouteLabel = null;
	private javax.swing.JButton ivjCancelButton = null;
	private javax.swing.JLabel ivjDeviceLabel = null;
	private javax.swing.JTextField ivjDeviceNameTextField = null;
	
	private LiteYukonPAObject[] allRoutes;
	/**
	 * ClearPrintButtonPanel constructor comment.
	 */
	public LocateRouteDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		initialize();
	}
	
	public void actionPerformed(java.awt.event.ActionEvent event) {
		if( event.getSource() == getCancelButton()) {
			exit();
		}
	}

	public void exit() {
		setVisible(false);
		dispose();
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
	public javax.swing.JButton getLocateButton() {
		if (ivjLocateButton == null) {
			try {
				ivjLocateButton = new javax.swing.JButton();
				ivjLocateButton.setName("LocateButton");
				ivjLocateButton.setText("Locate");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjLocateButton;
	}

	/**
	 * Return the LocateRouteContentPane property value.
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getLocateRouteContentPane() {
		if (ivjLocateRouteContentPane == null) {
			try {
				ivjLocateRouteContentPane = new javax.swing.JPanel();
				ivjLocateRouteContentPane.setName("LocateRouteContentPane");
				ivjLocateRouteContentPane.setLayout(new java.awt.GridBagLayout());
	
				java.awt.GridBagConstraints constraintsSelectRouteLabel = new java.awt.GridBagConstraints();
				constraintsSelectRouteLabel.gridx = 0; constraintsSelectRouteLabel.gridy = 2;
				constraintsSelectRouteLabel.anchor = java.awt.GridBagConstraints.WEST;
				constraintsSelectRouteLabel.insets = new java.awt.Insets(5, 5, 5, 5);
				getLocateRouteContentPane().add(getSelectRouteLabel(), constraintsSelectRouteLabel);
	
				java.awt.GridBagConstraints constraintsRouteComboBox = new java.awt.GridBagConstraints();
				constraintsRouteComboBox.gridx = 0; constraintsRouteComboBox.gridy = 3;
				constraintsRouteComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsRouteComboBox.weightx = 1.0;
				constraintsRouteComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
				getLocateRouteContentPane().add(getRouteComboBox(), constraintsRouteComboBox);
	
				java.awt.GridBagConstraints constraintsLocateButton = new java.awt.GridBagConstraints();
				constraintsLocateButton.gridx = 1; constraintsLocateButton.gridy = 3;
				constraintsLocateButton.insets = new java.awt.Insets(5, 5, 5, 5);
				getLocateRouteContentPane().add(getLocateButton(), constraintsLocateButton);
	
				java.awt.GridBagConstraints constraintsResultsLabel = new java.awt.GridBagConstraints();
				constraintsResultsLabel.gridx = 0; constraintsResultsLabel.gridy = 4;
				constraintsResultsLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsResultsLabel.weightx = 1.0;
				constraintsResultsLabel.insets = new java.awt.Insets(5, 5, 5, 5);
				getLocateRouteContentPane().add(getResultsLabel(), constraintsResultsLabel);
	
				java.awt.GridBagConstraints constraintsCancelButton = new java.awt.GridBagConstraints();
				constraintsCancelButton.gridx = 1; constraintsCancelButton.gridy = 4;
				constraintsCancelButton.insets = new java.awt.Insets(5, 5, 5, 5);
				getLocateRouteContentPane().add(getCancelButton(), constraintsCancelButton);
	
				java.awt.GridBagConstraints constraintsDeviceLabel = new java.awt.GridBagConstraints();
				constraintsDeviceLabel.gridx = 0; constraintsDeviceLabel.gridy = 0;
				constraintsDeviceLabel.anchor = java.awt.GridBagConstraints.WEST;
				constraintsDeviceLabel.insets = new java.awt.Insets(5, 5, 5, 5);
				getLocateRouteContentPane().add(getDeviceLabel(), constraintsDeviceLabel);
	
				java.awt.GridBagConstraints constraintsDeviceNameTextField = new java.awt.GridBagConstraints();
				constraintsDeviceNameTextField.gridx = 0; constraintsDeviceNameTextField.gridy = 1;
				constraintsDeviceNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsDeviceNameTextField.anchor = java.awt.GridBagConstraints.WEST;
				constraintsDeviceNameTextField.weightx = 1.0;
				constraintsDeviceNameTextField.insets = new java.awt.Insets(5, 5, 5, 5);
				getLocateRouteContentPane().add(getDeviceNameTextField(), constraintsDeviceNameTextField);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjLocateRouteContentPane;
	}

	/**
	 * Return the ResultsLabel property value.
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getResultsLabel() {
		if (ivjResultsLabel == null) {
			try {
				ivjResultsLabel = new javax.swing.JLabel();
				ivjResultsLabel.setName("ResultsLabel");
				ivjResultsLabel.setText("Results!");
				ivjResultsLabel.setVisible(true);
				ivjResultsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
				ivjResultsLabel.setRequestFocusEnabled(false);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjResultsLabel;
	}

	/**
	 * Return the RouteComboBox property value.
	 * @return javax.swing.JComboBox
	 */
	public javax.swing.JComboBox getRouteComboBox() {
		if (ivjRouteComboBox == null) {
			try {
				ivjRouteComboBox = new javax.swing.JComboBox();
				ivjRouteComboBox.setName("RouteComboBox");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjRouteComboBox;
	}

	/**
	 * Return the SelectRouteLabel property value.
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getSelectRouteLabel() {
		if (ivjSelectRouteLabel == null) {
			try {
				ivjSelectRouteLabel = new javax.swing.JLabel();
				ivjSelectRouteLabel.setName("SelectRouteLabel");
				ivjSelectRouteLabel.setText("Select Route to Locate.");
				ivjSelectRouteLabel.setRequestFocusEnabled(false);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjSelectRouteLabel;
	}

	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {
	    CTILogger.error(exception);
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
		try {
			setName("LocateRouteDialog");
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			setSize(362, 177);
			setModal(true);
			setTitle("Loop Locate Route");
			setContentPane(getLocateRouteContentPane());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}

        AsyncDynamicDataSource dataSource =  (AsyncDynamicDataSource) YukonSpringHook.getBean("asyncDynamicDataSource");
        dataSource.addDBChangeLiteListener(this);
		getAllRoutes();
	}

	public void processKeyEvent(java.awt.event.KeyEvent event) {	
		if( event.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
			exit();
		}
	}

	public void showLocateDialog() {
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				exit();
			};
		});

		javax.swing.KeyStroke ks = javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0, true);
		getRootPane().getInputMap().put(ks, "CloseAction");
		getRootPane().getActionMap().put("CloseAction", new javax.swing.AbstractAction() {
			public void actionPerformed(java.awt.event.ActionEvent ae) {
				dispose();
				setVisible(false);
			}
		});
		
		//set the app to start as close to the center as you can....
		//  only works with small gui interfaces.
		java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int)(d.width * .3),(int)(d.height * .2));
		this.show();
		//this.toFront();
	}

	/**
	 * Returns allRoutes.  If allRoutes is null, then allRoutes is loaded from paoDao and the RouteComboBox is populated.
	 * @return
	 */
	private LiteYukonPAObject[] getAllRoutes() {
        if( allRoutes == null) {
            allRoutes = DaoFactory.getPaoDao().getAllLiteRoutes();
            for (LiteYukonPAObject route : allRoutes) {
                getRouteComboBox().addItem(route);
            }
            if( getRouteComboBox().getItemCount() > 0 ) {
                getRouteComboBox().setSelectedIndex(0);
            }
        }
        return allRoutes;
    }

    @Override
    public void handleDBChangeMsg(DBChangeMsg msg, LiteBase liteBase) {
        
        if(msg.getCategory().equals(PAOGroups.STRING_CAT_ROUTE)){
            DbChangeType dbChangeType = msg.getDbChangeType();
            
            if (dbChangeType == DbChangeType.UPDATE) {
                for (int i = 0; i < getRouteComboBox().getItemCount(); i++) {
    
                    Object item = getRouteComboBox().getItemAt(i);
    
                    if (item instanceof LiteYukonPAObject) {
                        LiteYukonPAObject route = (LiteYukonPAObject) getRouteComboBox().getItemAt(i);
                        
                        if (liteBase.equals(route)) {
                            route.retrieve(CtiUtilities.getDatabaseAlias());
                            getRouteComboBox().update(getRouteComboBox().getGraphics());
                            break;
                        }
                    }
                }
            } else if (dbChangeType == DbChangeType.ADD) {
                getRouteComboBox().addItem(liteBase);
            } else if (dbChangeType == DbChangeType.DELETE) {
                getRouteComboBox().removeItem(liteBase);
            }
        }
    }
}
