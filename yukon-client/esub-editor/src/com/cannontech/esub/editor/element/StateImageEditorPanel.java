package com.cannontech.esub.editor.element;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.esub.element.StateImage;

/**
 * Creation date: (1/14/2002 3:37:58 PM)
 * @author: alauinger
 */
public class StateImageEditorPanel extends DataInputPanel implements TreeSelectionListener {
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private PointSelectionPanel ivjPointSelectionPanel = null;
	private StateImage stateImage;
	private LinkToPanel ivjLinkToPanel = null;
/**
 * StateImageEditorPanel constructor comment.
 */
public StateImageEditorPanel() {
	super();
	initialize();
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsPointSelectionPanel = new java.awt.GridBagConstraints();
			constraintsPointSelectionPanel.gridx = 0; constraintsPointSelectionPanel.gridy = 1;
			constraintsPointSelectionPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsPointSelectionPanel.weightx = 1.0;
			constraintsPointSelectionPanel.weighty = 1.0;
			constraintsPointSelectionPanel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getPointSelectionPanel(), constraintsPointSelectionPanel);

			java.awt.GridBagConstraints constraintsLinkToPanel = new java.awt.GridBagConstraints();
			constraintsLinkToPanel.gridx = 0; constraintsLinkToPanel.gridy = 0;
			constraintsLinkToPanel.gridwidth = 2;
			constraintsLinkToPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsLinkToPanel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getLinkToPanel(), constraintsLinkToPanel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}
/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setLayout(new java.awt.GridBagLayout());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}
/**
 * Return the LinkToPanel property value.
 * @return com.cannontech.esub.editor.element.LinkToPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private LinkToPanel getLinkToPanel() {
	if (ivjLinkToPanel == null) {
		try {
			ivjLinkToPanel = new com.cannontech.esub.editor.element.LinkToPanel();
			ivjLinkToPanel.setName("LinkToPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLinkToPanel;
}
/**
 * Return the PointSelectionPanel property value.
 * @return com.cannontech.esub.editor.element.PointSelectionPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private PointSelectionPanel getPointSelectionPanel() {
	if (ivjPointSelectionPanel == null) {
		try {
			//ivjPointSelectionPanel = new com.cannontech.esub.editor.element.PointSelectionPanel();
			ivjPointSelectionPanel = com.cannontech.esub.editor.Util.getPointSelectionPanel();		
			ivjPointSelectionPanel.setName("PointSelectionPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPointSelectionPanel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
	String link = getLinkToPanel().getLinkTo();
	if(link.length() > 0 ) {
		stateImage.setLinkTo(link);
	}
	stateImage.setPoint(getPointSelectionPanel().getSelectedPoint());
	
	return stateImage;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("StateImageEditorPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(444, 407);
		add(getJPanel2(), "North");
		add(getJPanel1(), "Center");
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	getPointSelectionPanel().getIvjDevicePointTree().addTreeSelectionListener(this);
	
	// user code end
}
/**
 * Creation date: (1/23/2002 11:12:06 AM)
 * @return boolean
 */
public boolean isInputValid() {
	LitePoint pt = getPointSelectionPanel().getSelectedPoint();
	return (pt != null && pt.getPointType() == PointTypes.STATUS_POINT);
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		StateImageEditorPanel aStateImageEditorPanel;
		aStateImageEditorPanel = new StateImageEditorPanel();
		frame.setContentPane(aStateImageEditorPanel);
		frame.setSize(aStateImageEditorPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) {
	stateImage = (StateImage) o;

	// Set link
	getLinkToPanel().setLinkTo(stateImage.getLinkTo());

	getPointSelectionPanel().refresh();
	
	// Set selected point
	LitePoint lp = stateImage.getPoint();
	if( lp != null ) {
		getPointSelectionPanel().selectPoint(lp);
	}
}
/**
 * Creation date: (12/18/2001 4:16:51 PM)
 * @param evt javax.swing.event.TreeSelectionEvent
 */
public void valueChanged(TreeSelectionEvent evt) {
	com.cannontech.database.data.lite.LitePoint p = getPointSelectionPanel().getSelectedPoint();	
	fireInputUpdate();
}
}
