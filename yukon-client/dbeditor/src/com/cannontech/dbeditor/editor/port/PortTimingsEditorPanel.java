package com.cannontech.dbeditor.editor.port;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.database.data.port.LocalSharedPortBase;
import com.cannontech.database.data.port.TcpPort;
import com.cannontech.database.data.port.TerminalServerSharedPortBase;
import com.cannontech.database.db.port.PortTiming;
 
public class PortTimingsEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener
{
	private javax.swing.JLabel ivjExtraTimeOutLabel = null;
	private javax.swing.JLabel ivjPreTxWaitLabel = null;
	private javax.swing.JLabel ivjReceiveDataWaitLabel = null;
	private javax.swing.JLabel ivjRTSToTxWaitLabel = null;
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JLabel ivjJLabel2 = null;
	private javax.swing.JLabel ivjJLabel3 = null;
	private javax.swing.JLabel ivjJLabel4 = null;
	private javax.swing.JLabel ivjJLabel5 = null;
	private javax.swing.JLabel ivjPostTxWaitLabel = null;
	private com.klg.jclass.field.JCSpinField ivjExtraTimeOutSpinner = null;
	private com.klg.jclass.field.JCSpinField ivjPostTxWaitSpinner = null;
	private com.klg.jclass.field.JCSpinField ivjPreTxWaitSpinner = null;
	private com.klg.jclass.field.JCSpinField ivjReceiveDataWaitSpinner = null;
	private com.klg.jclass.field.JCSpinField ivjRTSToTxWaitSpinner = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PortTimingsEditorPanel() {
	super();
	initialize();
}
/**
 * Return the ExtraTimeOutLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getExtraTimeOutLabel() {
	if (ivjExtraTimeOutLabel == null) {
		try {
			ivjExtraTimeOutLabel = new javax.swing.JLabel();
			ivjExtraTimeOutLabel.setName("ExtraTimeOutLabel");
			ivjExtraTimeOutLabel.setText("Additional Time Out:");
			ivjExtraTimeOutLabel.setMaximumSize(new java.awt.Dimension(98, 16));
			ivjExtraTimeOutLabel.setPreferredSize(new java.awt.Dimension(150, 16));
			ivjExtraTimeOutLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjExtraTimeOutLabel.setMinimumSize(new java.awt.Dimension(98, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjExtraTimeOutLabel;
}
/**
 * Return the ExtraTimeOutSpin property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getExtraTimeOutSpinner() {
	if (ivjExtraTimeOutSpinner == null) {
		try {
			ivjExtraTimeOutSpinner = new com.klg.jclass.field.JCSpinField();
			ivjExtraTimeOutSpinner.setName("ExtraTimeOutSpinner");
			ivjExtraTimeOutSpinner.setPreferredSize(new java.awt.Dimension(55, 22));
			ivjExtraTimeOutSpinner.setBackground(java.awt.Color.white);
			ivjExtraTimeOutSpinner.setMinimumSize(new java.awt.Dimension(55, 22));
			// user code begin {1}
			ivjExtraTimeOutSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(60), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjExtraTimeOutSpinner;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabel1.setText("msec.");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel2() {
	if (ivjJLabel2 == null) {
		try {
			ivjJLabel2 = new javax.swing.JLabel();
			ivjJLabel2.setName("JLabel2");
			ivjJLabel2.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabel2.setText("msec.");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel2;
}
/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel3() {
	if (ivjJLabel3 == null) {
		try {
			ivjJLabel3 = new javax.swing.JLabel();
			ivjJLabel3.setName("JLabel3");
			ivjJLabel3.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabel3.setText("msec.");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel3;
}
/**
 * Return the JLabel4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel4() {
	if (ivjJLabel4 == null) {
		try {
			ivjJLabel4 = new javax.swing.JLabel();
			ivjJLabel4.setName("JLabel4");
			ivjJLabel4.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabel4.setText("msec.");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel4;
}
/**
 * Return the JLabel5 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel5() {
	if (ivjJLabel5 == null) {
		try {
			ivjJLabel5 = new javax.swing.JLabel();
			ivjJLabel5.setName("JLabel5");
			ivjJLabel5.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabel5.setText("sec.");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel5;
}
/**
 * Return the PortTxWaitLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPostTxWaitLabel() {
	if (ivjPostTxWaitLabel == null) {
		try {
			ivjPostTxWaitLabel = new javax.swing.JLabel();
			ivjPostTxWaitLabel.setName("PostTxWaitLabel");
			ivjPostTxWaitLabel.setText("Post Tx Wait:");
			ivjPostTxWaitLabel.setMaximumSize(new java.awt.Dimension(83, 16));
			ivjPostTxWaitLabel.setPreferredSize(new java.awt.Dimension(83, 16));
			ivjPostTxWaitLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPostTxWaitLabel.setMinimumSize(new java.awt.Dimension(83, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPostTxWaitLabel;
}
/**
 * Return the PostTxWaitSpin property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getPostTxWaitSpinner() {
	if (ivjPostTxWaitSpinner == null) {
		try {
			ivjPostTxWaitSpinner = new com.klg.jclass.field.JCSpinField();
			ivjPostTxWaitSpinner.setName("PostTxWaitSpinner");
			ivjPostTxWaitSpinner.setPreferredSize(new java.awt.Dimension(55, 22));
			ivjPostTxWaitSpinner.setBackground(java.awt.Color.white);
			ivjPostTxWaitSpinner.setMinimumSize(new java.awt.Dimension(55, 22));
			// user code begin {1}
			ivjPostTxWaitSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(10000000), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPostTxWaitSpinner;
}
/**
 * Return the PreTxWaitLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPreTxWaitLabel() {
	if (ivjPreTxWaitLabel == null) {
		try {
			ivjPreTxWaitLabel = new javax.swing.JLabel();
			ivjPreTxWaitLabel.setName("PreTxWaitLabel");
			ivjPreTxWaitLabel.setText("Pre Tx Wait:");
			ivjPreTxWaitLabel.setMaximumSize(new java.awt.Dimension(77, 16));
			ivjPreTxWaitLabel.setPreferredSize(new java.awt.Dimension(77, 16));
			ivjPreTxWaitLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPreTxWaitLabel.setMinimumSize(new java.awt.Dimension(77, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPreTxWaitLabel;
}
/**
 * Return the PreTxWaitSpin property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getPreTxWaitSpinner() {
	if (ivjPreTxWaitSpinner == null) {
		try {
			ivjPreTxWaitSpinner = new com.klg.jclass.field.JCSpinField();
			ivjPreTxWaitSpinner.setName("PreTxWaitSpinner");
			ivjPreTxWaitSpinner.setPreferredSize(new java.awt.Dimension(55, 22));
			ivjPreTxWaitSpinner.setBackground(java.awt.Color.white);
			ivjPreTxWaitSpinner.setMinimumSize(new java.awt.Dimension(55, 22));
			// user code begin {1}
			ivjPreTxWaitSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(10000000), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPreTxWaitSpinner;
}
/**
 * Return the ReceiveDataWaitLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getReceiveDataWaitLabel() {
	if (ivjReceiveDataWaitLabel == null) {
		try {
			ivjReceiveDataWaitLabel = new javax.swing.JLabel();
			ivjReceiveDataWaitLabel.setName("ReceiveDataWaitLabel");
			ivjReceiveDataWaitLabel.setText("Receive Data Wait:");
			ivjReceiveDataWaitLabel.setMaximumSize(new java.awt.Dimension(121, 16));
			ivjReceiveDataWaitLabel.setPreferredSize(new java.awt.Dimension(121, 16));
			ivjReceiveDataWaitLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjReceiveDataWaitLabel.setMinimumSize(new java.awt.Dimension(121, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjReceiveDataWaitLabel;
}
/**
 * Return the ReceiveDataWaitSpin property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getReceiveDataWaitSpinner() {
	if (ivjReceiveDataWaitSpinner == null) {
		try {
			ivjReceiveDataWaitSpinner = new com.klg.jclass.field.JCSpinField();
			ivjReceiveDataWaitSpinner.setName("ReceiveDataWaitSpinner");
			ivjReceiveDataWaitSpinner.setPreferredSize(new java.awt.Dimension(55, 22));
			ivjReceiveDataWaitSpinner.setBackground(java.awt.Color.white);
			ivjReceiveDataWaitSpinner.setMinimumSize(new java.awt.Dimension(55, 22));
			// user code begin {1}
			ivjReceiveDataWaitSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(1000), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjReceiveDataWaitSpinner;
}
/**
 * Return the RTSToTxWaitLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRTSToTxWaitLabel() {
	if (ivjRTSToTxWaitLabel == null) {
		try {
			ivjRTSToTxWaitLabel = new javax.swing.JLabel();
			ivjRTSToTxWaitLabel.setName("RTSToTxWaitLabel");
			ivjRTSToTxWaitLabel.setText("RTS To Tx Wait:");
			ivjRTSToTxWaitLabel.setMaximumSize(new java.awt.Dimension(104, 16));
			ivjRTSToTxWaitLabel.setPreferredSize(new java.awt.Dimension(104, 16));
			ivjRTSToTxWaitLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjRTSToTxWaitLabel.setMinimumSize(new java.awt.Dimension(104, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRTSToTxWaitLabel;
}
/**
 * Return the RTSToTxWaitSpin property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getRTSToTxWaitSpinner() {
	if (ivjRTSToTxWaitSpinner == null) {
		try {
			ivjRTSToTxWaitSpinner = new com.klg.jclass.field.JCSpinField();
			ivjRTSToTxWaitSpinner.setName("RTSToTxWaitSpinner");
			ivjRTSToTxWaitSpinner.setPreferredSize(new java.awt.Dimension(55, 22));
			ivjRTSToTxWaitSpinner.setBackground(java.awt.Color.white);
			ivjRTSToTxWaitSpinner.setMinimumSize(new java.awt.Dimension(55, 22));
			// user code begin {1}
			ivjRTSToTxWaitSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(10000000), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRTSToTxWaitSpinner;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) {
    PortTiming pt;
    if (val instanceof TcpPort) {
        pt = ((TcpPort) val).getPortTiming();
    } else {
        try {
            pt = ((LocalSharedPortBase) val).getPortTiming();
        } catch (ClassCastException cce) {
            // don't try/catch this one
            pt = ((TerminalServerSharedPortBase) val).getPortTiming();
        }
    }

	Integer preTxWait = null;
	Object preTxWaitSpinVal = getPreTxWaitSpinner().getValue();
	if( preTxWaitSpinVal instanceof Long )
		preTxWait = new Integer( ((Long)preTxWaitSpinVal).intValue() );
	else if( preTxWaitSpinVal instanceof Integer )
		preTxWait = new Integer( ((Integer)preTxWaitSpinVal).intValue() );

	Integer rtsToTxWait = null;
	Object rtsToTxWaitSpinVal = getRTSToTxWaitSpinner().getValue();
	if( rtsToTxWaitSpinVal instanceof Long )
		rtsToTxWait = new Integer( ((Long)rtsToTxWaitSpinVal).intValue() );
	else if( rtsToTxWaitSpinVal instanceof Integer )
		rtsToTxWait = new Integer( ((Integer)rtsToTxWaitSpinVal).intValue() );

	Integer postTxWait = null;
	Object postTxWaitSpinVal = getPostTxWaitSpinner().getValue();
	if( postTxWaitSpinVal instanceof Long )
		postTxWait = new Integer( ((Long)postTxWaitSpinVal).intValue() );
	else if( postTxWaitSpinVal instanceof Integer )
		postTxWait = new Integer( ((Integer)postTxWaitSpinVal).intValue() );

	Integer receiveDataWait = null;
	Object receiveDataWaitSpinVal = getReceiveDataWaitSpinner().getValue();
	if( receiveDataWaitSpinVal instanceof Long )
		receiveDataWait = new Integer( ((Long)receiveDataWaitSpinVal).intValue() );
	else if( receiveDataWaitSpinVal instanceof Integer )
		receiveDataWait = new Integer( ((Integer)receiveDataWaitSpinVal).intValue() );

	Integer extraTimeOut = null;
	Object extraTimeOutSpinVal = getExtraTimeOutSpinner().getValue();
	if( extraTimeOutSpinVal instanceof Long )
		extraTimeOut = new Integer( ((Long)extraTimeOutSpinVal).intValue() );
	else if( extraTimeOutSpinVal instanceof Integer )
		extraTimeOut = new Integer( ((Integer)extraTimeOutSpinVal).intValue() );

	pt.setPreTxWait( preTxWait );
	pt.setRtsToTxWait( rtsToTxWait );
	pt.setPostTxWait( postTxWait );
	pt.setReceiveDataWait( receiveDataWait );
	pt.setExtraTimeOut( extraTimeOut );

	return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}

	getPreTxWaitSpinner().addValueListener(this);
	getRTSToTxWaitSpinner().addValueListener(this);
	getPostTxWaitSpinner().addValueListener(this);
	getReceiveDataWaitSpinner().addValueListener(this);
	getExtraTimeOutSpinner().addValueListener(this);
	
	// user code end
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PortTimingsEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(425, 279);

		java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
		constraintsJLabel1.gridx = 2; constraintsJLabel1.gridy = 0;
		constraintsJLabel1.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabel1.insets = new java.awt.Insets(0, 5, 0, 0);
		add(getJLabel1(), constraintsJLabel1);

		java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
		constraintsJLabel2.gridx = 2; constraintsJLabel2.gridy = 1;
		constraintsJLabel2.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabel2.insets = new java.awt.Insets(0, 5, 0, 0);
		add(getJLabel2(), constraintsJLabel2);

		java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
		constraintsJLabel3.gridx = 2; constraintsJLabel3.gridy = 2;
		constraintsJLabel3.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabel3.insets = new java.awt.Insets(0, 5, 0, 0);
		add(getJLabel3(), constraintsJLabel3);

		java.awt.GridBagConstraints constraintsJLabel4 = new java.awt.GridBagConstraints();
		constraintsJLabel4.gridx = 2; constraintsJLabel4.gridy = 3;
		constraintsJLabel4.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabel4.insets = new java.awt.Insets(0, 5, 0, 0);
		add(getJLabel4(), constraintsJLabel4);

		java.awt.GridBagConstraints constraintsJLabel5 = new java.awt.GridBagConstraints();
		constraintsJLabel5.gridx = 2; constraintsJLabel5.gridy = 4;
		constraintsJLabel5.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabel5.insets = new java.awt.Insets(0, 5, 0, 0);
		add(getJLabel5(), constraintsJLabel5);

		java.awt.GridBagConstraints constraintsExtraTimeOutLabel = new java.awt.GridBagConstraints();
		constraintsExtraTimeOutLabel.gridx = 0; constraintsExtraTimeOutLabel.gridy = 4;
		constraintsExtraTimeOutLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsExtraTimeOutLabel.anchor = java.awt.GridBagConstraints.WEST;
		add(getExtraTimeOutLabel(), constraintsExtraTimeOutLabel);

		java.awt.GridBagConstraints constraintsReceiveDataWaitLabel = new java.awt.GridBagConstraints();
		constraintsReceiveDataWaitLabel.gridx = 0; constraintsReceiveDataWaitLabel.gridy = 3;
		constraintsReceiveDataWaitLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsReceiveDataWaitLabel.anchor = java.awt.GridBagConstraints.WEST;
		add(getReceiveDataWaitLabel(), constraintsReceiveDataWaitLabel);

		java.awt.GridBagConstraints constraintsPostTxWaitLabel = new java.awt.GridBagConstraints();
		constraintsPostTxWaitLabel.gridx = 0; constraintsPostTxWaitLabel.gridy = 2;
		constraintsPostTxWaitLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsPostTxWaitLabel.anchor = java.awt.GridBagConstraints.WEST;
		add(getPostTxWaitLabel(), constraintsPostTxWaitLabel);

		java.awt.GridBagConstraints constraintsRTSToTxWaitLabel = new java.awt.GridBagConstraints();
		constraintsRTSToTxWaitLabel.gridx = 0; constraintsRTSToTxWaitLabel.gridy = 1;
		constraintsRTSToTxWaitLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsRTSToTxWaitLabel.anchor = java.awt.GridBagConstraints.WEST;
		add(getRTSToTxWaitLabel(), constraintsRTSToTxWaitLabel);

		java.awt.GridBagConstraints constraintsPreTxWaitLabel = new java.awt.GridBagConstraints();
		constraintsPreTxWaitLabel.gridx = 0; constraintsPreTxWaitLabel.gridy = 0;
		constraintsPreTxWaitLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsPreTxWaitLabel.anchor = java.awt.GridBagConstraints.WEST;
		add(getPreTxWaitLabel(), constraintsPreTxWaitLabel);

		java.awt.GridBagConstraints constraintsPreTxWaitSpinner = new java.awt.GridBagConstraints();
		constraintsPreTxWaitSpinner.gridx = 1; constraintsPreTxWaitSpinner.gridy = 0;
		constraintsPreTxWaitSpinner.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getPreTxWaitSpinner(), constraintsPreTxWaitSpinner);

		java.awt.GridBagConstraints constraintsRTSToTxWaitSpinner = new java.awt.GridBagConstraints();
		constraintsRTSToTxWaitSpinner.gridx = 1; constraintsRTSToTxWaitSpinner.gridy = 1;
		constraintsRTSToTxWaitSpinner.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getRTSToTxWaitSpinner(), constraintsRTSToTxWaitSpinner);

		java.awt.GridBagConstraints constraintsPostTxWaitSpinner = new java.awt.GridBagConstraints();
		constraintsPostTxWaitSpinner.gridx = 1; constraintsPostTxWaitSpinner.gridy = 2;
		constraintsPostTxWaitSpinner.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getPostTxWaitSpinner(), constraintsPostTxWaitSpinner);

		java.awt.GridBagConstraints constraintsReceiveDataWaitSpinner = new java.awt.GridBagConstraints();
		constraintsReceiveDataWaitSpinner.gridx = 1; constraintsReceiveDataWaitSpinner.gridy = 3;
		constraintsReceiveDataWaitSpinner.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getReceiveDataWaitSpinner(), constraintsReceiveDataWaitSpinner);

		java.awt.GridBagConstraints constraintsExtraTimeOutSpinner = new java.awt.GridBagConstraints();
		constraintsExtraTimeOutSpinner.gridx = 1; constraintsExtraTimeOutSpinner.gridy = 4;
		constraintsExtraTimeOutSpinner.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getExtraTimeOutSpinner(), constraintsExtraTimeOutSpinner);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * isDataComplete method comment.
 */
public boolean isDataComplete() {
	return false;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame;
		try {
			Class aFrameClass = Class.forName("com.ibm.uvm.abt.edit.TestFrame");
			frame = (java.awt.Frame)aFrameClass.newInstance();
		} catch (java.lang.Throwable ivjExc) {
			frame = new java.awt.Frame();
		}
		PortTimingsEditorPanel aPortTimingsEditorPanel;
		aPortTimingsEditorPanel = new PortTimingsEditorPanel();
		frame.add("Center", aPortTimingsEditorPanel);
		frame.setSize(aPortTimingsEditorPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) {

	PortTiming pt = null;

	if( val instanceof LocalSharedPortBase )
	{
		pt = ((LocalSharedPortBase) val).getPortTiming();
	}
	else if( val instanceof TerminalServerSharedPortBase )
	{
		pt = ((TerminalServerSharedPortBase) val).getPortTiming();
	}else if ( val instanceof TcpPort ) {
	    pt = ((TcpPort) val).getPortTiming();
	}

	

	Integer preTxWait = pt.getPreTxWait();
	Integer rtsToTxWait = pt.getRtsToTxWait();
	Integer postTxWait = pt.getPostTxWait();
	Integer receiveDataWait = pt.getReceiveDataWait();
	Integer extraTimeOut = pt.getExtraTimeOut();

	getPreTxWaitSpinner().setValue( preTxWait );
	getRTSToTxWaitSpinner().setValue( rtsToTxWait );
	getPostTxWaitSpinner().setValue( postTxWait );
	getReceiveDataWaitSpinner().setValue( receiveDataWait );
	getExtraTimeOutSpinner().setValue( extraTimeOut );
	
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	if (arg1.getSource() == getPreTxWaitSpinner()) 
		fireInputUpdate();
	if (arg1.getSource() == getRTSToTxWaitSpinner()) 
		fireInputUpdate();
	if (arg1.getSource() == getPostTxWaitSpinner()) 
		fireInputUpdate();
	if (arg1.getSource() == getReceiveDataWaitSpinner()) 
		fireInputUpdate();
	if (arg1.getSource() == getExtraTimeOutSpinner()) 
		fireInputUpdate();
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) {
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GCEF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E165DC8DD494573D57C48D31B6C10B89C653E8020A11A4A6212DA7B52F5412F7B0B12DF94F34A661343465353ED753524657C792DFB3AC5F72A1D1C4B09A099F8493C4C0C0C2B095C1A23A29C4B68A11AA83BB406A32334E4E320BE2FC7FFB6F5CBB333B336B12131E131E47B9FFE66F6F7E3F6F775D812EF0575C7494C3A1472524F34A4F61940EB3F6F1DC5D7EE71E255884E1F9862779F98768CDAEB1A3
	8D1E21D037FE59720C82EED1F39614A320DC3CE4F946B6783C05FB38F968A4FC70E367A85455DF99098DECE77EB9EAA793174F966642B39FA892B074DCAFA07B3642E285DF296014D28C9C37E6G715CDE38C3419A205C81948694B9C078E360190DADCFEEECD078FA264F6556BF7D2EA18F69516824D4BAC8E4DB15E7A977C84A3AC1E46BB16CA7129903F2B4908ABE150B6BBE8C4F260E4B039F750B43157D3C4DA65AE421FF28D2B0DBE4D12A340B125C5858813FCD16E10BED50F19C639D3CCD30F2C3C3C877
	EB05D9D89747D5C1F9BD467EDBC2762260F3ACD0BC4317B9905EE001F80106ACB446A36FB6A9B1F4DDFB040B7E19ADF5131F98A7D8C8EC1196B546B067EB19A538BD123AD05E9321AE6B52720C9DC0C701FA01A6G3D057B413AF36F413339CFEE910707C5DB335DFE4CEA16BE914D0295BEB7B6C20D021D917AC5494CF1242D1796D668789308085C3F0EDB0E64A47290ED7728B38B387847CF99B2F5F2929FD9E5883FC4F23164121A93E27B8D3F36D7DD726C171E364FF1DA5B91D34454F49D5B514F6FB7A4AA
	FA12B5364BAF2336366036AE064FA9CA7FA0388363D68DCE7A686B384F20729854A5DC26FD23AB1F7AE2CC3B07AB28B66A66A1FCF9332155CAFC6831FA7BD2A5A35DA1D6E2B30779729146A3B5B871E5B976911563202EE2F8F98669DF2FFE0E46ECA8142300E2016201B6G955B004FC67BD8DD5741A07BD887AF49471C962BD910B8A27BEAD791BC8537AC716DB6074C5B7A05333CE4617B2C0203D3F88C5DA887FD9EF2195D286F77030D0F851BA07132C53401F6FED8108549C1F88A3A5FC4BC1200EBE1C88B
	E0E0584ED13D455DDBD05B708E79B33B19178515035417F623B699614F02C6D0G1FC9DE4AFAD03E0C903F99C8E6F9180D751D92A418AE9A9B0D22F859E9B721C838D89B4AF351D6B4F704406739B61A635DFFC7D81638BC23D424FA7E2DFB33C6CF2B259F394FCB23F40C098EB4AF570A344F7C3E0B7619563C79DC4573A7F57B8C1EEFC452D76C2277589945BA11DF241C62689A67B876C735226F58A3B636FFE5B6D2F5B0125F508B282D424107E82055AC47F91F1712BEFB0CF78834171E16200389E6B56344
	4FFA39D049A732132E781F4D62E845756BC0F721FDF9C6931099E81A1D66B28E4729A77FD544CEEC7C925B70561F739559A0B9CBBD0F56A9E1C430492D02DD30196103C3295BF89E59D346710048E48AB0EE07F15F82834D41702273E8EC7232ACD97A1C3250A948B2DAF0D97D5B58164359E7D7278CEAEB5FF994236802F14F785FBB0F46CC3FE8338938711808D2DF01472CE0950641699651E91389DE53037A7DE5E1D419B36AFB50C6E80437BA517CC3768DCBF814C73BE49906AE0126B02E788A9A4F873696
	EBF2948D6D0D76BF6B6049DD21E35335070E4D3DD767F1B197779974561670792D065EABE41C48D75431C9FCFA776F7A3E9F40F30702BB082F4FEEC673291FAD0CB2A6D6DCC47A4240CEA4D04C95BAA6967D9D5553F9C7998CAD2255CAD24AF2FD57058254B6429527G53B51A60CE317C5DFBE133D75DD9A51724FE7E8514CB2BE6EEF4CBA81723B25D9F2E81FF12D0FEA5124FAE096403707D55C979E2219CAF7963FB0571ED00F221439F5F42914AD78BBCC63FFC67DD144F8ABC13F478C87EB3AF22BD085E9E
	F350E929F44A96B42373B28C923BD3462DC27615F76335816908F0507D6354E254CE71288C14AB53BE75DC9E5DBF06DD1E4BA5BC305D6F9EF92FCCFCBBA12BFD2A74D63AFF5C426375886A42E55A87CAAFEA5730E607C36C376065D3E93F1FDC5476B9022D3E38911E839649A13743246026ED7D62C564AFDADF95D859EF5C0F77A1A856A8301B88E4F6A27CBDBC0FD25FF7B849BA59814FD42CAF5B59C847E6A57E5DB2445B8685F3B94A74E56BE06525FEB44760AC93064A13AD387B9DC7F356FE5AC6317BB793
	FF855C8AE471AF5FFF166E876F5FF2D471E1654377F0CDB73698747667A11BF69A221CA42F2B1DEAFE479E4BC8DDF76175CC926B101346DABA82FDE504463AF3D4AF56D028A7F2BBC628DC84E071CC2ECED72E50C565D6B139F240FADDD4EE06DBCFAE1C49CDF231B681AC1149F18EBD399DEE3A364D596A3D672EF22B6BFD2B5BF76DA7FBD1DB89325765A6FB51DD8C1F0271399A1C78970A4782AA2F053A44D13AFFFA288F4D4189D7E1DF8AD4F88D7625C0C6A06BB55A277BFA8FF1B7591F36439815EC3C3553
	E341217BC49E75BFD8A314728EBE1D76C30E2E3F1132F79EC22EF9469E61D5A6F25F75112B74AA47F94911BE7CC5FD19F61E51747E810BE0B5B7B61EEC6934DBECED2840A9E7D7DEC6E397757104EBF4AE395E261DCB16ED2AB1647819CB0A47089F8DE3EADF2FE0E751F3E6B4BF3A21AE8CA88AA88EE89DD056F5E8136B34AD9670C7EE5696F8301A449640CE40BA51623D26335C591CDFAE67E3BEF25EB9B7DE7714A379B30D527C192F537E3FFB644B79D065A3676D439CDD9F2C4C07E86643CF3F248F09B775
	E11D2E8FAB5C540734AF288FD3DDDF4E07F09FB9EF9F1A3E50736130037AE002FAF23EFA9C2F215320BC17E12F96A1AC964A714CD7398475F96E864C97C065C0CDC09DC0E620C9C091C0B1C071C04908EF42720CDAA09310FB8255B38D6B890772D220B5C049C0459317E7B4GF5813901420116CEA4FB1AF8E5AFE2D47690C44FDD45C9CAEC650ACDED3CE6451FE445DF0A475C701DDB7D9172DBDBF6FB15BB149CF84B21DCF0C15AA9D4F456CE880E1F447EBC0B9D1537FA7164E19E0EC3F71A699AFCCB10F1A3
	5A69D55E65D54E61744FE2E84D57BBD76AD10972FCABC8FEB297FE374F7FF96A49BE7D73540F7A744FD3AB7A5EA4B868505E32507A677A821F37DE6853BBEF6D44B9D8D134896BF6B884871E1C5186D6297B2D0C5A4C675C75B7FED8D07237057B051C2D709C174516EA567683EC6DDBB351334F2C03B2F78B7477DBBCF132567CDCC0FB12F228EB856A81120142EE21EB0C58FF53757E38BB40FA4F1B7D374B10D93FDDECE654D79D22D36A973CF36FB49733BAF5B5EB1705E114A64273851617F9E9D070E93B49
	D81E5479133F436C2FFDC99B4CF7EA5B200635C177A8ED03E82FDC47C1B99EA8498BA77A3AC7347A8EB1FDD7183E34DBBD650A21DC81F474D6BDFDF9AE2D3EDA266F47EE2A4F64A5B78465B120D0DDFDBF7450D74774B5B9283E88AF3925D0DE8914202B6F692D68DE404A0F0ACE99768713505A72CBF67EDF8FB239131479EF9259A313FD5A23AEB43F8CC8BCBA0CF75A05FE2EFCB212AD58C6E54300BF92A88EE8ADD056E4FA5E19E8C4BE1FB472FD0235DA71635889952BD1303B7AD56C1002395ED631DA857B
	74300A55A99819DF0F237E0C2EBFCFBBE5DC4D6206F99D680C50AFD8C684B46E4F719642C26A4F94223152A1BAE4131BD5B1D9F4CFF04654E992B515348E2FBB122095D3645EBCD2202DC37BD81BA0113A8317B7FB6AD4EA29EC277C2607BF2CDA294F29A9720D47CB47E7BB36F86504B210B351B3A55AB311FDB2E96F3049744C14F1C32FBE1255BFFDCA2FFE992BAF2A572BDF456A6FED562BCFE475FFB926D71F4C6A3739746AF327507ACCDD7B252CBEE9C42F3E1655FF33C62F7E44943AA73B0E4FA1BDD0F6
	B3ECCA8D5E3BC1B902E1FFF4513D5BD206BD58006F4BEF83FFEE23737DEF847F73754B027EFC7D0A203F0E3EAA84DEC7D3867C4D3344BF7BA80D2D76B66A736C43B436938CFBE20C465643305843B436A80605B0FDB18C7BC9BD55371AE167183E358CEBE17A9AC2A8769FEE2A4F48304EEA2A2F17E18BC728BE19E10FD6D3FD312A2C036ADB4330B226EF9D4396B0FD5C6D941B4874F55CCE6FA1320B68F737E640665CCE67516ADEF43E44675DCB7C88DF493B6446460FA50B7998BFE84433B239B3EB43722B11
	BE203429741C6BAE23675C288577F0468927F46F4C92CCB5069DD349BCFEF42AFA4E0D04F3AEB9F704ED4BD2644620BEFAAA7E7EEC1B363F23B96420011CE3C87DFA0FFA54FF5077E5EAFDD931369E795B192A2D6F7228C779582F4813581E59C7E35B70CCA4E77D4E87FE6F83B750183EC11E9DE777F0FB273E1EC9F27C4AC031B6476ECA078B7AF6E3A3D9B1C87C066DA8FE340EF4018E029D39C131D0062598A9964730CF6AA946CD23586AE60AB5B02C2E85E1A886BB438A2FD2ECB543BE2A27D843B7A9762E
	0BE2F39896DBC3B16E8E3A360649D83FA48C8812A418BBADD751FEF54E9D087FA1BCBF1961331D715FDB036EB87B787E4B0312683419A96FF7306E84783C8E68681D884BF237AA794E3D5377AC144A295F85A1AA859EC4DB953C76CEF277C5F9E0AF530076903001308CBB07312744768BF8AE8C817EB0A0024D4273C58C14D7B20C4CAB49D0CE8B25BEBEB7C6FD5C9B2A6FE3EAG9F9BC2D59F090D17310DD640BBC2295DC731AFE3D08EE1D8B2EE33E8A847B0DFEC4C1784BF3E248770E53D0FAFD66C8BB79D56
	346954EEBE7625814A2D8C9B43730F9D4AE35329AF7FB2C2FD19B3DD5F174CG3EAC196E6D4B0B5846B24063D43BF84EDC8FE5EE8645CEE33E8AA8B74C203EBC49FC691A216FCBF6GDF2CB33CFDF9905BF083BEE9863DFB58D474655E7358B9EEB9F23F7B841E1F17E8FCA3780FB03ECC03137540485603B8282B78963D770D9DC4F3D5A8142300E2016201B6G950751FD415D83D55CCD4E01A651EE040D03D533541378E6ED7BF26F317CB0134E4551CF5C4FAD1D72EA0A3F7B540AB05226C665395634C7F346
	BDE7C1710DC1DDB850B22055C0EB013819B43EDF8CD4107850F7FC4D324C778F61EFE5E8F0E7AD02CB05516103441639DD9BDB61CCE23FDCF9B64D7477FD7D319BDA398E054F2CBC5DFE65B20CDA39E98A5FDC65996DD76ECC3DD6AECE610BD71EC9FE65DEE8564A255DC5780A15E745DD7E649ADB745EC7B82A701BF464908D81BE66D5978A6588AF0C58A8392AE7E329423B520F0D844057FB55E55D3DBCE3475DFAB6DA6A75ECD4A93C2DF76B5B68825C6AD5B7814AE13AB6AADCFAB6A2955EE5FEEC2C82BC51
	2BAE994A3961FAB6D6546859A8D5F8EB4375ED1CG3C472B4E8E65C9DED8F8B8FA67D1C42F83A96B6C351A3761E993DC25F40BD9598A67BF9BFA79273C2E2E223426E6BF9D67F3DD5A77949BB69E567DDE9F77D745EE1272DCF65B9E45766B12EA7BBF71190C59ED912D22D4699A32488259437C60BA7DFE38FC56720CDD9CB9C363F5CE79A1BA8F4968B371711739F4AE5A31F8B6577A33D7D376787131F71672CE5DACF54F3B765206CC2273F1276AA7B79ACC0E085CCFC7D4395043414BB539D5394746826745
	40723296FCA77297C755FC5CF2D555D5BC8E5F67DFD365E26B0317BBFDDD15BBFD935F6F9B233E175EC3FD173ED0FD1FF9C35565F4876F43D2CE15BBD19D3CDCC52AAAF75FC8E05F7FB0C2FD6FE23E1FCED3FD1752D5DD3F160277E1E606AA7756B8FCFFBDD3157B76CDFC7FBD73BD1479FEB2DB75FDF8232AEBCDD03E1379AA86DDAD5139CB7379CC51D7751E1B9E919B177F21B648FE6359C17F77AC2B87756FD9DE9C543FE7C9988C7CFD445AC17F6FFF7DE1D07B7E9759AF9D49C1ED9E876D198814B59B56C6
	202A59F42FF4FDE8B70777CABA7B246366C1F4AF0F5EBE15297DDE0B7FD887ADB7FFE755EE4167C5D11645E1241F62A39654DE32E847A0412ED9D00EA44B6010CCD895FC228575D32BB000E1F2E772B6BEFF9BA12E1E59749E65777B70B9864A939856D9C66FDB26B12C7B86150DE7588D094AA6B1ACC122329B983653C8E50DF3A876679BD43617E167B8AAEBE518251E4AAEE3582CBCAA3B0AE12F65316FEA991654CCE5AB6E255887C5D476A8436EE15F739A9956036F9166C0B90AE1BF4458D2A82FE2D8884E
	E9A21439EFD36C29BA0495C2F9AF4332CB280D86067D8E7305C0F9AE43BE3CCA6DC6B3EC4ED5EAB706E1649E8F59DD4D3027DD546EDA067D659CEB8F06D53929DD63FD94936A295DDE067D391E5A15993647C56D06B06CB40BF78E43920B295DA58C1B44626566D26CEFAEEA3710E189AEEA371CE193EA285DA606654A54EE8743AE1928DDB343664954EEB443FED7C36D46B1EC6C90359B4F30D2195ACDE258359AEAB7ED9E45F630BED44C30E7EA285D21F974AE71A76735FB3F02FB5EB150B32B773CB9B60F6C
	27C266297BAAB2DF5D170B6F85GDFB30F4ED151767761F9163F241CD4492B1BF8168230C3926D02A40F92943F56264C7D315735671DE45017FBFF606F60098FAF65217B05D260ED826A85F283CD830AGDA8A3492A881A899286081988BC08DC0C6209EA0BB502487287FEE740A1DDF7F154BDABC51CA3C85CD350A7F199E7E07033E2887827BCF64A24634F2B1A033BAA839759E6F3D2C85198DC1493D62A1D79081790888ACC772F2BC477FDAE24C3979DA72D98EFE679256G5328DD793E77739C34D1928786
	9C82DB33AE6694B02C5D51A1BAAC68FBE2320F295B0EB6E78322CDA6F7C127701C57G7E37C650BB4E74B244A303B3E49F74BD64B6C76E22AE15513BA8D9A7E6FA7621F7D1A14003081EB766C4F85FC55D02570EC84023A3689E6CB7852A3D7F11283DC4BF76D28358CB76315706572F347970F9BE35173DD935E763283D2373756D258730E71A6FEDEF861EF7FB8137B2FB95DBD4FBEF6633FBC6BF76B28358DB62E32F64981ECFG0FE376CE972A769EAD2276127D584B8EE0AFF701771D3423452B2F20EBC73A
	E7EE4177553C5B6B3EBA892F3D4520EB6F825A6732AE7A3F07FF873FEB5C813CBD0CBF3E4E339F7F092CC7D09F36005E1B161650D8E39778461A62956B9A60C134C74943BA1FD88D757A776F5BF19BA783FF5AC21A7397713E1F54BF05577CAA28EBDA68F97FBD84E519E12F6035F2AE142396529836F6519862976A471099A00624055E7D239B6F97B6G1E95C96D5E01FD290572D1066D42F767BDD09EE218917B978665398CF3633C2F04721AC86A736B4554672CC57ABE278770F947A2EF1F9F42B64A812FDD
	C46D3A30AF3DD036B2AC9B47B68D4A618CDB04576DD8A82FE2D89D5E87EC00F25603546709A66AF343037ABE278670597820374FCFE39BDD001B9F24F68B711EA4941599B61F6CD3209C4B30F6AC1B84658D8C0B40F114AF069CAC26BE9FE2E3593CD85F6714GBE3B97FB7B3C9E5B1884F868E2FA3FFE9BFA65CED9D77E18330F73332E436969A41C0BCF77DD927AE593BF48E4A2F211AF6CFCC736ABCA5D62DCB4B141F012AD3C55F2957FF9C1A7B05A86491E6BA5FC2733CC71D359CFE2EC3502A96740BC7F6B
	6A1F73EE3E2E3E1127FFC6CB4F77BD23ED4C57BF239564EB4FE8845B1A0F72BC887B1DAE46371DE15D8C5B05F5A26C1C45AC8FB1FCDF7EC685BFA1A00394FF9F7B6542D81702D5E930EE852B4BC7FB8388F9C81428ECE3BE1EB7ADD6ABA9FF04F9ACB658AA30FC34E09FADF6DE63778906F53336F8DB27FD76F8348F413EBF664B97B3664B17637265ABF07972ED54615B6441C766607B517528B2F77FEA9FBA77FB5C5F344162D13E5B8CFDCF9C5CDDD1DAD5D1DADD3B1F3ED7BC3B0C5E8DA5AD8EF44F3A2AD47D
	BCF50FEA4FA10D4FDE39035D6BC651B524E61F2AFB563B2A6E7F3AB1BE5D69862A3B1769DED62E6AAEFEDF557D4D3C7169BE16CFF507B35D9FD6283AE3EAD45D870A46277BD7C5D477AA267B5707D45D99F52A6E6B5B835C4FFD50GFA6B599D617EFAD56E093ADD7E652A6B2A08A011AB78C0157B5F62714572E9890DA56B210077760D6A67339F2A76DE9B89BE3E51A6D5AE36BED0FCB5555A78669CD165FE608ADEAE792F2A5C1F4E0DAFAFEDDDB4AFC79F22ED7CD6331A030F0EE97AFD4036727409EFD1657E
	D00F56075D68E540D2133B943D695738DB611B542A72953B8264386110C70E979ED765DA6B0217FB7704AA37E61CFD280275A1B74B5549F6B5D743A7D55DF1017A0B17CF2127B4BE3950FD6C6EB3264ED21328499665FD5C2872BAEB8235C7D52D36BDFE7A09AA17A287AFF724C3153BE09ADF3E9EB351FCADE1796A7DD44D572DE7D45D76F144325824494121FC5C37A4C11BAC4277634FD43E2DF20076B8FC5823BDB61C557A953C1CE8D6658A46B9571A59DC3B1665EA0620666ABB832A6E3C20E2A1FBFC3E8C
	3D37216C7170760660BB5CF9AACE37B74A7D4A5CB26D7DCA69433067FDB818FB19838E2D5C89106989CAEE23C1ABE78719C9C149FD1C6FF1DF85B2D1C1497D56631D199810D99D145C59920F7BAA105990145C29AE0F7B2AC7605C7DC8B0F2559E769AC04698145C77CCDA39DE1011031233F864A584E4665CC40E6C7D3ED7C677039FB2ECD9995D8F3E4330D607AFDF3B43176F9D03AFDF1941176FC23EAFDFFF3EAF5FDF0AFC79D23779728D17D03E2798A615787275F5D13ED798B658654BF70269DB4830CFF4
	747D58C4791EE3583F1BFC79A630B8E6B3ECF211160F7C4FA95170AC8C7A281739F40744ED761DA31A6FF861E7D197DB8B8D1C211D4B9842033F1DCBF72159410D5E43C0C73643A01B6F795DED4A7CED37965CF948E960325A380C815EEA9F62C18E7F812E114B694327B2C182DE19ABB0A25903A0FB904BFEE8648C7D8E19FBCB457B91CEEABB6DFC3FGAC59E6DE3A0CB5DC613E60D24FF3598352E09FB6A0F30673ED6741ACAE216AF4A337D19EC25F98226BAF195B262A6EC0775ACA4507B8A46E8D7831639F
	88EC938B2C9D1F3C92C68EB5267E7A01D483178699A1A751F6AE8D9DA7A1C009A4A895CE22D8977B97DE6833833942312479168CDE569116757FCD0B4C258FBB047ECA7F4FB472A8ECB61B71F5B9EFADC5FF74D1FA5AA9B74A5CE665AFC5CA511F0A10E59E1AB14FE3EDFDEADE5DEFD67E7C2CA3154BE867CA5055E427A0611B8D410C4414FF9F5446E55BC5689E6882915A58019F698EF9548A1D38F0608C7A17998ED9B0B753BB936835DBC957C27F506060895EB1A4237B8B682FE9B6740F0E384D245A4A5B86
	8F128B945269D21AFBF18A50D489A7D2EE8B76977FDB8426E54B77B76DFB67E46252D44E5046ED340A3C390D6F17C5291D4B1607A441B1A4DA4DEE7BDF0FE2C5D978140DEEC1D3C15DDBACFC94EC2589BABBF37A327B5EE9F9EB735A8C689FE7F087B1A79A8D150C435CB244CBE0CA105AE5740F1BC4E9AF2B1B7836DC766C475BFA8CDC0A92A4EA4D3DEE3B0C467D3045BA8A9F48CE8637F3398C5ECA3C4DB1A0CA4360D92917E7340A99287C560A2A190BDE7B9C9BD0C5E5EE17BA088785F178E073400045A6B0
	8510D7D03F1329C7BC15DEBC1B5D53371EFC7E0DBE68872DDC6AF0D5957EDD0DFF57205F55982946C8F58DA4B7098B7863DD6F26B1634E432693547C15ED5002720BAF2F387F252624F590DD1BB2BFC2A39428DCFFF38E778952142819BBF342923E015A480E3A0A9B692DF0338145E98795C6F77B74AA041679F68F843F479A89158EE0560378F74D38C3C6D235D8361EF95A74CF63E94BBF0D27A77FE9BC7DB410276887FD0B5867D13A6B6B9745671ED1301FGD10C6A143ED6B119030989E670BC0F9B990F5A
	CE1D5257AAC6B11898BBE5EE1357CD0AC77D10CE69EB956555E0220413FB2E767E432372931D5257AA4449410408AE2F48BBBF7BEFE85F79C91EDC6C77FD6F06A7153F6BF8527B1D1F17B271F75500C7BCC95F19F9BF73G677F3B354EE15EEA15F96B65E61B99FD4FF6ECD496B46FD0E6A27D8E4A23D4123A5FE6A23FAFE9E47E8FD0CB8788CB820E68309BGGF4D8GGD0CB818294G94G88G88GCEF954ACCB820E68309BGGF4D8GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0
	E4E1F4E1D0CB8586GGGG81G81GBAGGG6A9BGGGG
**end of data**/
}
}
