package com.cannontech.dbeditor.wizard.device.lmgroup;

import javax.swing.JToggleButton;
/**
 * Insert the type's description here.
 * Creation date: (10/18/2004 10:19:46 AM)
 * @author: 
 */
public class SpecialDoubleOrderBitPanel extends javax.swing.JPanel {
	private javax.swing.JLabel ivjJLabel10 = null;
	private javax.swing.JLabel ivjJLabel11 = null;
	private javax.swing.JLabel ivjJLabel12 = null;
	private javax.swing.JLabel ivjJLabel13 = null;
	private javax.swing.JLabel ivjJLabel14 = null;
	private javax.swing.JLabel ivjJLabel15 = null;
	private javax.swing.JLabel ivjJLabel16 = null;
	private javax.swing.JLabel ivjJLabel17 = null;
	private javax.swing.JLabel ivjJLabel18 = null;
	private javax.swing.JLabel ivjJLabel19 = null;
	private javax.swing.JLabel ivjJLabel20 = null;
	private javax.swing.JLabel ivjJLabel21 = null;
	private javax.swing.JLabel ivjJLabel22 = null;
	private javax.swing.JLabel ivjJLabel23 = null;
	private javax.swing.JLabel ivjJLabel24 = null;
	private javax.swing.JLabel ivjJLabel9 = null;
	private javax.swing.JToggleButton ivjJToggle10A = null;
	private javax.swing.JToggleButton ivjJToggle10B = null;
	private javax.swing.JToggleButton ivjJToggle11A = null;
	private javax.swing.JToggleButton ivjJToggle11B = null;
	private javax.swing.JToggleButton ivjJToggle12A = null;
	private javax.swing.JToggleButton ivjJToggle12B = null;
	private javax.swing.JToggleButton ivjJToggle13A = null;
	private javax.swing.JToggleButton ivjJToggle13B = null;
	private javax.swing.JToggleButton ivjJToggle14A = null;
	private javax.swing.JToggleButton ivjJToggle14B = null;
	private javax.swing.JToggleButton ivjJToggle15A = null;
	private javax.swing.JToggleButton ivjJToggle15B = null;
	private javax.swing.JToggleButton ivjJToggle16A = null;
	private javax.swing.JToggleButton ivjJToggle16B = null;
	private javax.swing.JToggleButton ivjJToggle17A = null;
	private javax.swing.JToggleButton ivjJToggle17B = null;
	private javax.swing.JToggleButton ivjJToggle18A = null;
	private javax.swing.JToggleButton ivjJToggle18B = null;
	private javax.swing.JToggleButton ivjJToggle19A = null;
	private javax.swing.JToggleButton ivjJToggle19B = null;
	private javax.swing.JToggleButton ivjJToggle20A = null;
	private javax.swing.JToggleButton ivjJToggle20B = null;
	private javax.swing.JToggleButton ivjJToggle21A = null;
	private javax.swing.JToggleButton ivjJToggle21B = null;
	private javax.swing.JToggleButton ivjJToggle22A = null;
	private javax.swing.JToggleButton ivjJToggle22B = null;
	private javax.swing.JToggleButton ivjJToggle23A = null;
	private javax.swing.JToggleButton ivjJToggle23B = null;
	private javax.swing.JToggleButton ivjJToggle24A = null;
	private javax.swing.JToggleButton ivjJToggle24B = null;
	private javax.swing.JToggleButton ivjJToggle9A = null;
	private javax.swing.JToggleButton ivjJToggle9B = null;
	private JToggleButton[] toggleButtons;
	private javax.swing.JButton ivjActionPasser = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JLabel ivjJLabel25 = null;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle9A()) 
				connEtoC1(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle9B()) 
				connEtoC2(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle24A()) 
				connEtoC3(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle24B()) 
				connEtoC4(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle23A()) 
				connEtoC5(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle23B()) 
				connEtoC6(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle22A()) 
				connEtoC7(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle22B()) 
				connEtoC8(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle21A()) 
				connEtoC9(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle21B()) 
				connEtoC10(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle20A()) 
				connEtoC11(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle20B()) 
				connEtoC12(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle19A()) 
				connEtoC13(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle19B()) 
				connEtoC14(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle18A()) 
				connEtoC15(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle18B()) 
				connEtoC16(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle17A()) 
				connEtoC17(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle17B()) 
				connEtoC18(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle16A()) 
				connEtoC19(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle16B()) 
				connEtoC20(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle15A()) 
				connEtoC21(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle15B()) 
				connEtoC22(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle14A()) 
				connEtoC23(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle14B()) 
				connEtoC24(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle13A()) 
				connEtoC25(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle13B()) 
				connEtoC26(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle12A()) 
				connEtoC27(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle12B()) 
				connEtoC28(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle11A()) 
				connEtoC29(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle11B()) 
				connEtoC30(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle10A()) 
				connEtoC31(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle10B()) 
				connEtoC32(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle25A()) 
				connEtoC33(e);
			if (e.getSource() == SpecialDoubleOrderBitPanel.this.getJToggle25B()) 
				connEtoC34(e);
		};
	};
	private javax.swing.JToggleButton ivjJToggle25A = null;
	private javax.swing.JToggleButton ivjJToggle25B = null;
/**
 * SpecialDoubleOrderBitPanel constructor comment.
 */
public SpecialDoubleOrderBitPanel() {
	super();
	initialize();
}
/**
 * SpecialDoubleOrderBitPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public SpecialDoubleOrderBitPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * SpecialDoubleOrderBitPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public SpecialDoubleOrderBitPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * SpecialDoubleOrderBitPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public SpecialDoubleOrderBitPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * connEtoC1:  (JToggle9A.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC10:  (JToggle21B.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC11:  (JToggle20A.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC12:  (JToggle20B.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC13:  (JToggle19A.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC14:  (JToggle19B.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC14(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC15:  (JToggle18A.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC15(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC16:  (JToggle18B.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC16(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC17:  (JToggle17A.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC17(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC18:  (JToggle17B.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC18(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC19:  (JToggle16A.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC19(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JToggle9B.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC20:  (JToggle16B.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC20(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC21:  (JToggle15A.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC21(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC22:  (JToggle15B.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC22(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC23:  (JToggle14A.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC23(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC24:  (JToggle14B.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC24(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC25:  (JToggle13A.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC25(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC26:  (JToggle13B.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC26(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC27:  (JToggle12A.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC27(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC28:  (JToggle12B.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC28(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC29:  (JToggle11A.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC29(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JToggle24A.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC30:  (JToggle11B.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC30(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC31:  (JToggle10A.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC31(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC32:  (JToggle10B.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC32(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC33:  (JToggle25A1.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC33(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC34:  (JToggle25B1.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC34(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JToggle24B.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (JToggle23A.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (JToggle23B.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC7:  (JToggle22A.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC8:  (JToggle22B.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC9:  (JToggle21A.action.actionPerformed(java.awt.event.ActionEvent) --> SpecialDoubleOrderBitPanel.toggle_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.toggle_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the ActionPasser property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JButton getActionPasser() {
	if (ivjActionPasser == null) {
		try {
			ivjActionPasser = new javax.swing.JButton();
			ivjActionPasser.setName("ActionPasser");
			ivjActionPasser.setText("");
			ivjActionPasser.setVisible(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjActionPasser;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G310BE8B1GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFD8FD895D57A3FED58DAD1CBAD955B3A451A5B52CAAD9595EDD4543A08AD7BDDABAB5AF50B5D285C0D4A0ADAEA591C0B1828281826D8E4282018C0177F0814002828E824E8D4ECF905ABDC3DF0EFF0C5B4545FFB4E1C734E4C1DB3F7007D6E776177BC5B536B4C791C776FB9E7661CBB731EC1DA7EC170DCCB6AF2C9324C1558FF1596C94A3AD3124E1C3CF3BC47EACFC4DBA455FFEB0016CA9F9FCD03
	E3B3540DA91CE24914CAAB53215C8165108DD3ACAB61FC1974E2D955ADF0E2E0A7936A0A47DEF52FEFBB17231DF9347C59768CB8AE866A824C9FA88EA8D3F2EC4FE2F898149733F3C216D4C91A58CFF8128EE429706CFE19EFG0E7EC00DA7890F5F76ECD61F889861C983BC6B244CEB0563FC6A5D586AAA4657F6D90814744025290B08AD15FECE81CCB61C9DF324952924A166C81E377741312679F867A76D2E6E22F6DBCF0F2B47E3EF6FAA6AB8E26FF0F8DC3DC5A79D27ED3D9DC59D76FEC73B3D48595D596B
	BA612E2EBE603637BBEC4E865709A3CE7B273D9D765EFA07275956E3F7CAD26EG09635D16F4EA8362G3BE1943B3C0534DD971C0F818A9A6031BC382806799AF8BCC40A3F6EBC43D81286649832CEA93184B51F1D9BF30A683F11768D11EB05F2G106C4B2B5B39AF992739AFCFEEA13C8DD0EE8372A0FEFD8D417D21BC91A872B47771638D5C47566F6E10120F5C1C3A4440476927E55FD2CEABBEE64DFDE44EAABAFED6F711B1D0759D5885F28385820583CD87FA0F0E5B470FFE88473AA31EED2E6EEED7CF1D
	5BDD6F6C685D6B6A30BB613C3A9AEA98365F5E6E6A6D10A4F9ECFE50D2A07007D362F75AF1C9C80E6DF3090F25507DB7C99E4F4F6978700EAD7C1100D4E9D05631FCD009ADE610345B1707C83B1541B9115B8EA463E3A91EF1C605533E8A22E301140321AE769CEF6756455C17263489D2E6712D2973843E845D9F121AFC1E6CC3667794DF64B6CEF610B62E813C95488DB48EA884A866FB3C0D57F5161A3571AE3896FAFBEC4E83A70E38ED3D1E7A938EA78CF4565637EE775556A212656E9B361C5CAEA1256ABE
	1AF16BAD42F61991F2D5EA82EB0F94D53B48365FB0341DFD3EFEECAC46BEBCC02F896B79F29F4EC17CDF942FD56334EF1F265796A9B7C3DDB8104C5F738941337C3C47480FA89E6A67BDC6C2EB7898299D9BEDF1FEBF5AA2776FCF0F13FBE45005209BA896A881A86D22E8CB8ED059C53C0FFBBABF9A62F5542C6ADFD9F6C3578AB85A87BC3D36DDBDFD9EDBCF3B7D102D57E1037BDD1F44F82CDD24DD0FE8644A69755D8EB6F65AFB6C3DB60F4355835AED5DF698CCFDB24FA7DDCB89CF2F1D5636B83A6DE0205B
	ADF13DBB3A164151E96B739CF4F758BCF605C32E5F57C546C43F6D90E88485F06EF791E92F8AFA4FED01F377C53C2D7E47C93098A847E33BCC6DFA9B0EFBE0F05B1D55558D0EF662062D77949F23078F0D03E3927087DD9CED297187D9A03FCB22AD7917C2FB8339FF90EDD99E90ED09GEA3B8C664D4B61FE89547843E8CBA2D0C0A0744395B0B7828DDE897DF2D534E5B2D0D7D034A563C7F02F85EA39BA5A128CB47E4751165A715007C063FE92EDA938A65AB28D48F3AD4CFB417C3E70C2AB9F7F8D0BEE148A
	9E39D9B87E89C5864BE3F9FA3077783F18461582F876CFA1AE20B620C12071C011C0B300D20032A7C0BDD09B50A050F820C82099C0A9C05957C1BDD09B50A050F820C82099C0A9C05993219E288DE890E8BCD0A4508C2094206C1F717544B5ADB92962EB1234BFE9B752CE243DC9DB137ED07310B6267763G85A76D692D0B7499E95B615CF73C09749F69BBF29EF705DE9769AFFECE468499AFE6BA4958B22AA3E3A56CC74675E4AC12F1C9469E9FF384A763F0A8711031CA4619999F993FC351C746AF994FD941
	7FDEBB93AAE0FE116B029C653132712051D8A1775A0DC02514CF125E811A6B45330E9D172930CA01AE322E79785F74FF9350C76CBCCFD03FCDF5CE566A8B0720F31E0F3AC220E29F75E4BDC96EA3641E7801A42F6D897E5E9063F99F28EE88FC9B0628EF8BD039A44FED7F6ED8594A0E1B59D13E6F5774103933866EB32DC083BF63777EB4C70EC46744FADB1F1D4F02F485EF6FD0E6821957F20C0C85FB3F3D4753E0F75BFBBA60240F4DC16947C8FB32797128CB62F357E20A137552B618407A90DFFEEC290C03
	8E75EC4B6B33E93D4D6369F59CB961319F30FBBC0E1ECEC5BE177A5227D90A7531D8BFF111D883A0469020880C7546E3E42C73F92E4965BAFE425DC2E6F0B6DFAE5DC16646D860CF407B7197B56477DE7A753C5CCE4BCA7DF3DBDA0751B7C44FF07AD23611D6B39C9BF306B9DEE49B1ECF48DC4857EDF399CE7A0EDC0B64FAD9A2497792520664FE32120DAD523664FA605791392F10EB1C0CFDF28FA257BE399E4975CF2E49D2B656AB5998A5E37FE3B6EE49B5358DFD3A167AE405B6EB859A839482B48DA899A8
	6767303EGF2838581C5814D844A8C0936D4819D869A8794869487A45DG6B9520E6208120E0209820D9C0D93F00B587D097D0GD084D0BCD07A24E80B9528F5926F6FDBEA48BDF73434533FFB2F78AF7DE7C89EAB9FFFCC464AE898A77F9B734AFF69BFC3725CB46938715CFA4BF1715C9AF1DCBC375EF35C775C99FDDCB4F74AF53FBBCE462A6BE40FEAAEFE68B879FD586E6A69315385810AC872F34397DAC89C0E9E0767F3C7C7275DF340F15AAEC9A113481C3A177E869D8367011378EF37136B78EF375C53
	B7C8D15F788B1FAF857DECD2EA62A4F93E14C2155FEE32CF8FBB499CEFF75A3B21213639CE74F85833B27A7B1661FDF3A856F4688AA21B3F3B4555596934EF2EAB3051B80A64C3333D77282B375B5E11FF4C56EFAB321D749451FEA82A23555B4979461C4242CFF20C9FF396F8286AEF6A3CB4B6992B5C327150C62BA45F9F16C9D9CB34BE560F9A9F0B51470D472ED47BD8D2B6FA9A32047B386F9DAF9FC7CFC316F29F1FCB577AD8BAFA5A310C7B187835170F23279D4B390FA5D9DA9FCBC6CFBBD6F09FF3F23C
	FC9CBD6DD849FD7C54297531F87434E39577713AFEAF9FC7CFBBEE66BE6EAA38CA6323F57434E331157938305F4B4751530E45454C475D799A9F0BC75104DDDC42FCFC3C434B47D1540E254CC77F55DA9F2BC6D1BB16B19FEF5C686563A8EA47F266E335D36BE365A8EA478A666335AB3CFC9CC56DD84957BDDFE9FD2C98C56DD845FCFC23454B47D1540E1B190F75FDDA9F4BC7CFBB1670F9660B6DDEBE0E1EF6AC61734C4623DA9FC7536F99BE4F7CE319170F23289D79BC93780E5647D174FB26044FB35DEB3C
	FC9CC56D4867197B6DDA9FC7516F1992BE4FBC3E524B47D1540EFC1E5963B5DF0F225FB3A5FC1EF97010170F23289D79BC73F5275647D174FB26144FB34D8EAF9FC7CFBB1672F9A635AEC8731B2B3C2EF874B8D9A27B7864E9AF9F6BC7110F2572FB5DEF5D6419EFCEE834258C28A9143F57BD58CD6A79FBDD7672FA1B4B69149FD7C3DDD8A8F9967D27C34BD54F17BBC265FC5D45D8BF7FF35F7565A675B6137ACBCC6A6FB429FF5A24FEE92B6F7A0FCD6A9DA675D718545FE7D27F3CC97D2AAFFC57EFB7297F56
	247EA7A675D3CD6ADFB529DF73256F7A3DA67527CC6AFFE6D27F20C97DBFCD6AB7FE653B7E20C97DF9A675B71A54BFEED2BF374DF7FD31C97D3FCC6AAFB42C176FB375BD19F06C33FFFB425E53EEA709A3FB6C27D88E672E1E373D6A8E591CA7145C504FFA48FBDF27AA67C4469BDC6277D83BDDA44F64B86A5F67FACBA2B91E32CE793DDB180D68F477BA3AED3D27C8928D5BD7103FC33D2FA09962A0FB9032EF0C36D801E45965E75E05232F3C7D341B082E17BE617B8F9AC176B050189BE5FDD1B74AEDA3735D
	361573B5C3396BA6A3BEC7AD679B8765E0C33E776BB8DF9414638C799EC43EE4D27E25915F58FA4EE7851EC6C33E4D5BB8DF9761B164FB9E790201A742106F278D1CAF8EF8E699729D44FD9E19E151167CB0A33E79BB30BF006730A15F5D48B786F8028C793E5D4979A200E71AA1DFDEA3671B85BC69B79B71BD0EFC7940D3EB48F749AE4EF798F8868C796AF6F33EA06089B364FB957926814FCCC33E9B76F03E74DB22ADB93798717D2B09735582CF2BA15F523D1CEFGF8828C79FE03FCE14093E348F7EA9F67
	1B89BC522DC6FC051FF23E9C6029B2647BB37235820F5B106F0AE64E97GBCA106FC3BBE63FCB14093EF4877CF64134223ADD961C6FCB76F67FCD54053EC48E7BF40795C40B34E10EFD58B678B811EA8C33E5FA1DFBC70A49B721DF71073E55D8677CF20665BC4FC3568DFG54078878E2EEA3FC9D388F6AA3443E23589F67C95205E9FCFF475ECC75FE2E0E3E09429C89C29937335C88FE44FDAF57FFCB7456825EFCBBDFAB473854B910F5FDFDAE18G68AAD92EBF69D667CF485820FB819C0FBAFA7BBC3BFABA
	6C83FCCE7B1E1B78CA76A5B0184DCB13B6117CA36FFD965B698EB21EE23A6DA4DD5E37383639BA6C0DB0ADEE437412C38E7BC9093743199D6A3DE011CBFE143A50209DBAD87CFE2AF6181176489C5927D7B652BDC0E4EF9B7931B1D054F7525153D9DD3D1B4EF6723E304BE97EA751BF79F63E7FEB5ECEB2BF27C0F9799D511682A0F9AF4A3BDBB3D83C8D008D8249EB0841F36AB5C4789DF2AEE9BC9C655FD967EDAD016370FFD971363D1066FA0D68F794366904E5354C3F581BA6C8B567CE8B772E0DDB62171A
	F4075C165991CA1BDA33A74FE9G125B6189EC07AE60890E606D306B0C289D62A2F87BDDDE4B6592814B3D134B255708645A6E64F2BB6A381C9B3090143B3AC0586EA877864AA581D670ABAEF74B9611DC472F385CCD75DCEE004820DC28505ECC14BB320D4B25GD6F69717DBD4AC12F35E4565D6B4F039B100C5205CCDEBC4F233D06E8114CB0B0C3654C4F239E02BC84E9349654EEE67F263G0BC23988213DE414AB594165B26E0E36B45C4D651EA891498D5E4D651EC139G025D6DBBBE699EAEF775CEAE37
	9C3026FB385C8F047179215C3EC6AE978458B4147B3950DE7A3DDCAEF597174B862C65DEAEF72AC2A4670FF23723DCB0E0F1A837F103C8AEB30A4B9D5B4D65F281EB0B62F239C2FB01A837FA8F178B81AC9E652A0576326E63F28F21DC81E09D77F13989C2FB63D16E02A6AE978658CC142B935A4B391F4BD56F65F2E500B96F67F26D25A23909A877824AC5GB66BFE5F63B37F57DCEE42BEAED783186757DC6EE1213DD014BB74A9170B82AC99656A73C4F25649DCEEC1B34E87008DCE66F2634BC4F261A8F78F
	4A45GA67D867BCFE82F6AB7DC2E77B3AE5784189F4AFDD9A9120BC439757B395CB440522339DC3850DEEDB4177B834A35G660FF2F7150B64A6235C258738DC9CE019D338DC69FA11DC6394AED75F42655AG8BC43924FA11DCAC4A3D06F27100E545F03917360A641AE3385CAF8EF2398E4046235CB9617CB0BD064F671683A31D4FE53B8EEC1F1920B3E7AA377B06F0FEE91D4AF3E8BFAFD12F8F2362038C57074E29725CBDE62A777A7022BE325E99CFECCE656B43FF3AC96EB0DF9F6E0795192D87169A9266
	A9FFCB62BEEEBF454A3F7C1658254FA5A43E57E80A0778BE99F44683A583E5BEG574A835CC6C52F69DE6B6D83BE76DADF35036FDBEEFEC00E296381E5DF4DG1C7B83852BB0F9DF71A3E7C9DB07BFA06FABF6A07E76B93ADF6837AA1C764DA2BA671032B39666625F73BDA0B9DB3D77CD27BD2C5D571363D516650AF4F22B3D4A8DDEF2721A6F7A2F49DEG415AD5FED6DFFF426301753B3C063D75DCBA3FEE9E66FBFE5F6FA73FCD82219C86948314G14BE9D56DDC00D53F93F0C6D952D5B357DE234E8D776EF
	4931A60C300DDEB6ED232E69DAB9B96EB1E7F85CE32673FE8A2A9D198F576B643CFD08907A3035067BB08DFDE82A9B198FDF6A643CFDC8FFC4644303855C071CC7388FD27D48FCD82C13737621D56843DF37F09F5C68C37888FD385F540790218FCFE1BBC4218FDD5BC666C33FCE4E5B07E4218F8D455C074CC7398F598DA37361C31D1C378F0D0F0AFCF8FA8D7761B07AB0ED04BEBCE16AC3105007472C5C07B07441CF776D092179F039CE4E5B0719C29FDE44F6109E63BED46D98198FBBF4F25EBED4BDA67261
	1DD26EC3B37A10B8C29F5EB075E11C5087E5BC84238F41BBC76643CDBAB9EF9F6204BEC463F81805BE34B40E4C07A3BAB9EF9F7263C4BEBC016DD09B47FDC85FB5B29FD668643CFD98907A90D641FD88C09FA2C768438326BE4488FDB83C017B900FBE38F70F4C0733BAB9EF9F329E97797095360375F16EC36E1E1179D02213532D9F04BEFC0B6DB086FD089B218F4F187A90A17461F1EC07E96803FF5348FC38DAA727DBBF7CC16443G36C34E9F388F35FBC76643BE1D1CEE7DA07461EA3CCF3A510724917A10
	3A4FF47DA074C119AF225007901D2E2179F03B298F49C29F9614617A610F5C0736CFC76643B11D1CEE7D70C7118F2772F07D00BEE4B60F4C0755BAB95D7AC1684343588EE1684364917A7010298FB305BEFC0B6DA04D60BE8CFCB6B29FAE504969568FB3C4BEDCD6096B877421E07F48FC28564969568FC29F1E43F688C69F62C768438B26BE4489FDF8399C578F68C360011179B0C1A727DBBF7CC96443317538FE78937721D1773D3D2179F0C8A727DBBF88FD58348D578F68C34A88FDD8F050F47DA074A13F96
	578F68C318CE5750FC384754072CA7C4BE5C0E3FB72DCFF09FDCA39C8FE7CC47C317500762C2DCBFBC411F3B7C6BA441A2209C7384FF467217DE726D877EFC2D59656871C87C595A8F7AC80E0F2B5751696861D8A0455ADD3DF4C33E0C7D5EE37C2D00C7BD62EF854C70083F9570970F6F6F707C4DE374AD81791BB9B751F726F190DF92100C5D77A141521F0C36E4BF4931EB686F71DAA837A2F6AFED33C1A887A2360E5EDFC2209C75A46F4BBF1E1D864769D0CE84CA0B0F36E48195GB5G3581F9007C010201
	4201E2019200D200167FB95A128FD4835482648472839A8F9486B499E886D092D046DF22AD39C0D5C0CDC09DC003C001C021C0D1C053019201521E0278004A009A015A00BCC07EC09301A2012681A5G25GADCFG1F019A005A92F8ECD967C8ECA42F0A64CC39818FC810739612D9BE8449F58883ECDA021CEFC1F2A95CACC7E396E019CF4BF9A2A487A4006516D881EBFEDA4EEFA139ABE1ACA7468DD84053F2DE8E49391946F2F942G1B76341CCFC4F205C81E9249C11A85D866B3F29E9449F14AE439D3D640
	1A1F117337C8EE167B99B9670164C784BC43E32B9B2C1DA3DFA3CE56566A6B069369A7916ECD24FFC9FB7B62C9E175241F3C6B0259B871A55FE260A3A1B2DE52FC58A7E3A241C7FD1340A7B5B528FC0BD46949B0101B61656BF446C746AA3FCE06522E04BCDEFE17F9154938A557D01BCAA7994F64C846FAAC43EB18CF0DDE7264FA4BB50803DC23EDDE7CED895AB239E648B59FAA50614FE48B8C62CDB2685349867820011FD5A67DE7C664BE54E120A35C4BF7F25FA077BBF2AEDFCB4B4F70EB09FC8728504046
	D03FDFC7F200761A705858D1746D20F7A4795BC53E64C55F7963C43EE13447C77DAE20EFFC54EFB1313DC8F57E156A3C5C005F7BDB01BB5911FCBF09FF3BE9287DFC502B7C29D7193CA7A35FF5DA22425A59718BC9FE07C64E797799BF7312A75F0092FD27119079EE5481AF4C61D56E1464BCA0517743BEE7C7236FAF169A609F9960DB8D704D8678D0093C8FB37AA665CA2F723FA4651BF86F537329ABC85B3BE199E46F23AF349AEC9E9B2BFB56C346366EDBC77BEC5DF609FF6761732D73994FF60FEBDB3144
	2F5108F3226FA907BDA3DF47131F51DF7B6C5B427D466B3922FE71FA2E2C1F0C71BE57095EF63B771AEDCBFF9656A9EF68F6F96C5DA48C16F7CE65BD365ECE3BEE4D37074A4BF5C672AD7DC66BC2390D5E2BD537D189365158B322B60AE3ED13A8E8A3D95F6FB57ACAD1DFC30DC81F742C2CE77933C67A4E2FD36BABC3FD0F96087465B3BDB5067AAA6B557A4AD15F8BDBC47A1A191E8EC3FD4FE974D5203E19C27F86189EFFC3FD57EED36B2BC4FD3B0AC57A02191EF0C3FD879A547A2AD05F5F56087445B0BDB3
	8C754D5B2E563799754D300A744DE2FA52E69A693BEB7B82153EE2AB5717A474AF0B69A9B054675A2151D74C753DDBAA52D74B7434986ADB37D3232F44F77BF5B1BD03067A9E536AAB657AFEABEC3FG26E722213E0B9BB57A4A383E27047EC5B0BD31067A366E5268AB677AEE2B90690BE7FA128C753D2255D7417559B70874257FD556137DD7A3FDBF5F2D51D7095FBE977AE7E5FA9A8C75FD35C7232F8A5FA58B7DEBE5FA1C067A16B4E974ED46F732C27F46B0BD01067AFE2D51D70257479921FFA1CCCF2421
	3E13FBB57A707A38D6F8FDCCE3FA928C75ED5A2751075747E921FF49CCCF465F0C74BD295507574712B2113E9C26274CD05F8FBF5568436B63EC1EC8DFA35353E628EFE733C69FDE9FF1C27F5CCC0F1F213EFFE875617551AF74AF086989B554774B4FB47A707A3832D224AF0A6919EE282FFD3FC69FDE9F33047E4DE4FAD28C752DBC20510757475F4BC57AB21F137564BEE7246F379AFD25F8FD38560B74D5B1BDCD067A4E35E8746175E155651690BA4C74F88C75159E5468436B63D1DD6E9221F1CC4FF8C3FD
	4FEA756175F117F07D924674CCB654F73D367D707A28A8942E7F181EC401BEF93DF964A4494D9B4072D1DABE0576FE33032CE75D3D7623765EDEFB077C89D229AA113E6B274FA8A5B84FC864F91BF19F12672ACE5BA957898F0CAF4A37F7935FB7B98865C020C820E909F26E251C0BF773C9725B68E82F2D9B163BE47B24B479F9A2FBDB811735C239812883488FE862735C662305C4365BB66068BE51CDBD147D1BD8C47C4BF8A15A127582672D4E233C0E9EAF5E7D7479E88370B5A36FC395E46D04DFB745B619
	502C466B91DFDBCE7AE2F7136D085D493F75D0D21F0ED831D546E29C8B9479E2EB9D175F63566075B887342B71E2640FBA2541117F6ECE154DE2992BB92C425837893E48D54B17205E5CB59A9C7546262A71D264FFE03386C77EB75BD5B6CBE52C620C8AABE358B7EA79B254FB558E8D0EFA8FD52971F2645FD8204111BFF12E4AE6390CEDA9D7E195B2D638DEADDF01FA2DC59A9C75DE2D692FCA64FF532E4111FF62B6154DCA999B10D4D8953B67562A652BD06F577BB4B86A75577463E6647FFC2F86C77E1655
	AA1B1BE56CD5D59B1530F1FAB6CBA5DF220C531DA59A9C751ED67BD1220C536BF6E9F064774BD659E463F453B7AA0C0D53319AFB4AB8FDF51586C73DDF162BF1E51CBED72C41117F476F2AEC32F1DA3BC50531F17A5281353CB2CEC30FE9F0546B7010BCE736190CBC9569157110B9EAFEE51CBE35DA03237E6D25AADF4A65BC768BB4F96C03AF4877526097F0FFD22D28BE9C6BF32AC475B1D81F1EAF2A1F01754925227AD9D87FE91E28BE6DC5DE7FFA3928BE8B6B3F5EA02AAF407AA7057235D87F29D03E856B
	3B2C227AAE2CFFE40D28FE906B7B057281D87F2CD0FEA256F7EF9155C7A871897BB7966B2F5048473F4876D94D62757B04725933787B405F53FCF6AB14EB91AB13783B44A64446537B75F8A807A2E6B94B79A2907B0A66E8273F84FD789247A2EBB8DFAEE24F5377159ED2C6ECFFA1678BC0ECAE150D0772AC440E213E9444E6D3FDCDAFC7DB5ADE66D8876A6BC26CB04D990E00F28CE27795F33E6908D95692AC7715E8CB59AB9CBB3A167355A2768A4D358C00F2B0E22D28AF943105D4B6854A99C99CF3233EAC
	440A69B55E85658144AEAF65FCFEC9DA7F26C3B9A1C96F5FAC447EC27D2BFD9556B32FF2ECA77A5706589BB49FAB944A1108FD037AE2900B25B9F4D92FC5DB72DF63D8E1A567ABC3EC10663979C1B99031BFEF60FC41081D247AE6C1D97ABB47E6D4F03E8C44AE2132EDD0F6A276B26A9BC06CD5AA9B8365B8448E21FF890885D25932E4D8CBA6F36CF554570C586D346D03219C0658FBE51CAF92314B28EC466BF0ED3D4E3157D0DFBEE26B296CG1447A176FD5497085895F49D1AG65A4445E4863FC529B9CFB
	114AB6C379B0E2AD651C4F0958A5D4B6924A31083D0C7A62903B1136D57EBF22ADD57F605862AA4E570058BC1A1F9388650908B957F33EB0C5167607148267A99C6B437E4DC66C82AA6B0472A0E24F213EF1086D24EB27B8A84FC4ACE49B67CBC26CF91AB75370CF38577F936FAFF51C6FB0E28F5179BD8C4AD108D5627DA596313D74DE127DA64CBDEFF26C963C3FD481B66ECD7277ECF71CA5614A73756D9F1277A8BD7613B938D12D4151ED6FA1FFC4B03F343C34A0274A3A116D998FBE295EEFD635609A437D
	E631EF4A3F61625951FF72BA361F6A44268CA613B59B4670EC7E1B2A78B0591F047B1B08A35555BBFB9D9D75364EA67A8BCD4E3139FBD3BA139FB71B6F4FFF0E6A0C02F29C502C59FCFF5C40FA7E5D00506BA7C8CDE3EE917E8D5978169F2526CBD364E73F6CC87647954CFDE40EEC730F855CE6975487CB64EF8B4FAFD44FD924ADFF8AD4C4DA0956EF51541377A49BB5755D1AFA626F0B296A7AEBB575F3D572F2EC3F4D6331257CB6CCF25E96EC7877F1E73218B2ACAC369F2E13029E1ABCC79E8BC7A5E5ACE4
	658DE5AC48F297DA94393F6D980A9C7B8643D103EFB05095A36BE35A5EA44E8670196D9F296558A0E21FD7F1AC9C312D799CCBC46C23B20E6526F26C02BC0E35A1E62D60D8A0E2D7EC60D8ACE2EB10AFED8E6AC33E9A442E35F24C03D846DA0E05A2F6A372A5A0369A793253B8B6F18B47DA90DBD148B1FF4064B6DEF2E015380D59AEC11E2F96CB751167G13D15FD8EC7B9440647EFD669C798E1547BE60117B7E4ED45277ADF349B85DA3B588461CFA5C4EE36304D0994890FA070D55C6B8FFF2766C598BF1AC
	5DFAC6EBEB469C45D656D9B3DB73FD589A2F33352BD0EBEBF61AE26B619ADF365619449522331566E5EB6DDB0AADC72119ADDFF1056BECE557E8EDFDB9CF31F51E4F385618449525333542ABAE3A84E7363E7399579A1338E2F536BEDB2B35F57BFC45D6DC31AFDB9F18449527331565E56B71850A2DB3EB4DEC790AABC1E7EBC13156D64AC245164BE7DC6F1B44B5CBE7EB2DD7DC9FACD2ECFD5FE7DC6F1B4415223335554BD67DE245568D253EEC3DE792D77ADBE67DE5DFB2547EFA4FA42E65BADB2FDB353616
	AED5ECFD6D333FD619441523333554AB2E323795DB7DBE63DAE592D7014E561D9B3436BEDF2658EA2874E5EB25C9DCD5BADBBF7432F5FA39E2ABF90319ADDFF1B568ECDDDA21357513F794DB2FD47832750EC9DC4DBADB933D620ADA21581A1FEFE64BD7DCEDBADBB1DE361EDA29586A7659DF4BCC626A52591A64E5EB69AA45D6264F381619446551597ADD295718FFD731D5D2664B56D213384668EC05F845F57EFB0A2DC5BE63DAEA92173F7E7A4A535A3ABEDB31F50D4F38161844952433956AE5EB6A7B0A2D
	256B4DEC790AEB224E56DB65DADB492B95DB474BFD59DAE492D7187E7A720A6B3D9C4556FC1FF1713F77ECE4ABD2E72BB7DFEB6B13B50A2D3CAADF36961A4495233335FA3D5616ED2DE26BFC1FF1ADB409EB3A4ED61E577DF06ABA4556C5BE6F0773CD620A57597A09D7DC09390A2DD41F777979A6F1A56AECAD28535ADA32DE31F56FB6DF36661944152C331566E52BE403E24BD1E7E64BD7DCE9733CED7DF50B565641FC4556BE1F6B79BC1338B2F536BA3C5673839B95DBD3FD2E6773CC624AD659121FE17C7E
	AB7573244C2B4725DAA471B30C7CF972330B32F94A7399F9FF55B13A4F288BF0BFA0B9AF606233A4377C106D187E2B13GB6773A5C76DE4FA99925DFE8E6F9895FED9265A578A2D96E42914A5DBFECB979DBC55F941067DD93A1DE5EDEB25EC571B0B5CE1F393D044F5CA221AEA7FD8A5B1B76E31A6B608765E0208820E9C03300B26673361CFE167CFDD21F5F83EAF1391B6C7DF627EA6B985B93AA4CD375C5723838EF9EFF36957E2BDFC891979CB67C5EE74EFCF9BC5432E344E56BA8AE47D7D4C862F3C31DBF
	D098D08C508C20A40CAF1B66EB132F9A41F02873F8EC6DDD742B27BCB872A1D285A6499EF2EC79C5429C4385329F596CE805E383BBE7392C425C1CD64663E447B1F08C544835D589F37298CFA4BBCE03E302C62EA1DF18AB47F8B296326B890EE58B55F2E5421C33C64653460EEEB87AE964AE966716B11ED0F60C0263F40DDC39B007F1A663C9E1474C0CA9165C8C355C5542DC40AA4653440E07616851482D975A9B47F84633E3989CA7EB64AE965A0BE3BC096CA8AD1AE2D93EC8ADB7D118031B4FF8EA583199
	0E9D9A3925EB05395E0C471F9D0361982E113BC5E8AF06714CE047D9F0CCDB2C16DBA7341745F88A5831960EAD9A399B04B9E2DD0CE7109D8360B8D1A3771EB097AC02714432E3BC9C13B4F28F8A6D25AFE1579F9C2D6C1C13ACF711701E5440F8DB59518947B16C1CBC4FA13FE16803AD761FBCB7DEC5363C30FB561FB378BDABFB52F5D243E307D34922CAF44F8AE4FA9316A8F3D9540133F3E51D16E2C5FFEE09F23E33D4B9BFD6261CDFD22E1C07D5A8678FD7AA672FD529F4D62BF4EED14EDD9FAB6717FF22
	1C5F36D5B90F2BD54E7FD1271C2F5E261C9FECD04E8736AB672F6FD04E339B15732D3B1473A33B157331FB147310A6653CF91FF25E7729F2FEE533F2FE67E74A790C7D4A796C834A795A032AFEBC271C7F0A7626BCFFF452F79C72BC9139112CEF04EB8A18A5F65B7AED1F9EB9E6EF773458BAD14675A169DF5C9FA3AFB5D8DD7F0764018779FB698E1B53F11AFEFE7BGB076F4329CF6B2CC59186DD11D7FFDD32BE0CC7DEF917B36C741FF52069C7F44CD467B1DA6ED926FF73AF913F87F7A9D1BFC7F2D7BFBB7
	997F2D7B68CD6A3FF5AF773BA71574CDF66694CB95D09B109B48AF132F99DA8A480FB368F641FAE1FBC7A759EF55673111CD7DB2FFE521F12C1F942ED0E2A5D26DB0CEFA9D642F03605F8D59D9C89EBE9FF1F9BC2EEE221F63FB8BC9FFF9DCEE8A4A587EC252C63D0E4EAE0F4C4A70D67A1E48E9BFCAE1795D7056A21ED793CC6223D8F0916605A07674D61A9782651C259C2B266F634A205C0258AB357C3DF797E22D78BE5B0358E61AE7BA994AB3913B14E2A9D04EFE1BE3FDD57C3D3C953169781E3F86313D74
	5D1BBF1443901BC03149D0CEC06C2BFC1EB7100CD8D4BE5717360CE301747DDE8B1487907BAB457C219C0A589E4CEB08C2AC9473A4E295FD39743D7C72A9165A65280FE2ADD076A0B6A18F73AE901B0779850108E551FC0F84A8273F4331EE0AE5C3398631268A1E97520C580D1857500658F554C6A81427A116CC3184A8272D60584D9BF85ECA96E2AB307DF291FB1666F6F8209C04D88D359B8A65D844BE463C1AF8442EC17F92918B243E542C1CE2B93C925B0FE29EA8872E5476B9497B89C1EC9E7A970E58BD
	543F342CA9167CAC0EAD23F6EB205C0658DD181764C6AC9B73128691FB1766A545C2F996E2AE0A252D02756AAA0E652F65F9D3D508DD0279CB8D082DAB21F9ABD00EC0EC00E231D0CEC4ECAA66F5C96FF26CC374AF8331826ACB9B1447A0F69E4582219C0ED8B57505661DA1F6AD7AB79D317D54EE6EFBD3AC0D6FF16C6794EB0372A0E2D1747DB2490B8BC0EC8D662D0CC7AC173EF3CE04F2E6B647CED1AC974A8D081550FB98495BEBC56CAA4CEF69C0ECAF359B8E65B844A6D0AC914A996FF3ECA645F220ECC5
	ECAF7AD703187C5D92FFA887A37653821E13930ED8AA7A9205D8AE3511399AFEDF2D4658A85688652E557C3778C3F34842E9904AC1C011C05301E682C99F40EF8920FC209A2096A0A7509820C02090208820D820788F789CB563A3726046689988CB2C2193D52FCD7D7D1941D463396A7CD473394ABF1544E607B92665141B6D933EAC15FE53666851BEBB4E09D726E6A2362B2F595567A0ABA0F98DF6937D44167C4C690EA233F77EF24E037819D3923447CAF6AEBF5FCA59CAE268F275BACE3BFABCB6E78B0443
	2D733DA2ABEBF93E0F1443F3A0BE2BDD26112BF3BABAFB484C4FE40E230C95E464B89ED43D63F719BE93749547E150B9DF93C7F33DF99C476B39CFCE0C63E7753E633897E546E39CC92A7C0167FCBE771EEF92C72CAE0E0E8D66F11C5B40FD0A43B86E5C60BB0E07D1A6FD8D0FE325AADF60B11F6FF6961A44D13546BB0EC99566F1C4D7F01F9A5670B824CA5FF13CDB49E5BC98C735AA9761FFFC3EB7DAE49247C4FD9C39C308A3177B94264411E792C79E170947B80E28F28FD61B3E137295C766DA6FB8BEAAB5
	0FE3F7A977A9FBAD0F432F54F79C5720CC53DA9EC740E2A50E49BE5F1FACB509E3D097472F561B477148FA4C0944B81618DC9F9B717A0840B8EED565CC941A3E53759547CCDD9CEF161B47318A2F0FA40C63C0056FB83AD1A6F79D0F6351CCA50E5BCC5F6F7B0A23E51D6E7A98C2FFC4E3FF9CDE075707C9FF3C0B7D9100F13C2E4AABD96D33BFD61A4491250B6311A174472CF26ED3AC467121C9FFB4E0FF24E09C592ABC12D2537C98DFF19464FA47D13C46BC0E26B55C272ADC9E4785EBFC47910CB2DD39BC0E
	2D2A9C150926B9C53E62885245D1DDE29E4741926E53C40C6387A53E633881E526E39CC7D479AFF52679E53E62C84B730EE35B907A63EBEC5B4CBC9EC710C9FF5C02B235F9BC0E312A5C1A5BCDF372FC4561564561301A4761E765BE8DE29C77D8FD479107B22198C708AAEF274AB4BF53D79CB3F4F1ECA9B00F639066374F44B8828AFC47718B1449DA4F63B8244A893A49B42755D79C0D6BF56B44ADC3D8A7EE61BE352C47F5629613F5A24A78214CEC2F776D27D479C79DBE7B077F9E194D7E730EAB9CEC5C85
	78F3C0322D5B8A48623A5F5167A0FF8FD876A9965BB68A5BF63CAAAFE91569FBF8DFEDBBDD5736DB06503607301D62311D82CC5A7697A81339017FAEFDE60E716F4B6767187F3EFCED0E78D9E84A9C7D33D0CB1A78D9683CB47533D0994BC8A3ED56893F0BDB11EFA9E2DFA036026AA458670E8ECF976259E98B985EE8A786B93EB60D78F512E22D7CDBDFAA6C8B069524119F0B90B2B4A717ADCEA36370284369144B6514475183BF1931BD2A290F8E37CD6577D644085F72783917661A497DD6D4CB6EA32A2D81
	C587483F7C31F5DF51366D7BDA366FC77E6F8D19DF3675177795DB5F2B9F1E2DC7074CAF5B1A7B01E26B4A8D43337537A1734B3672F394DBFF2A981E2D1A4A61595A33C6E5ABF7F8360A7306E72BE72DE24BD6BABCDB378D19DF36F559BA45D6627A61592A9DE6FF0567AA362C436CAF3FA1734B369E4BD37557B063AA9EE6DCF3B7A836668DB32E2F0699D7FE3EE22BFD4D70EC5DB1E4FEB666B7AA361CA5433395B5E4FEB666BFD4EC1D98E6DCD3079957E51B94DBBF368E4F560BC366E7E33EC03155DDB0BCDB
	778D191F0D79C2553DF74BB06F3DC36617ED3DD12458FA2A004CEFC7ED4EBEBB563FEFD545BDCCDF6E9BB27FCA4AFFE17BD4F0A074F9E9EEDF2F7436FE4FE35D250FBF967ACD4372D4A9F51774D6971D67F7C9F3CF128543009BD4B4432A4856772DC477610ACA30FC78BF789F3CDF025E6F224B0A787E526AB9CFDDB7A7D5CA835765E556AEA90D2C15A002DEB912B9304C22F110FD00E459C316F829B48212F11BA1C4D39969F97568FFF97F4B7BFF493BD048CBD10FE4596C11D20B2D641FE272CF897927147C
	D3C67EA9A77FD410FFAA49BFD5649FA2D1C2A4CA08C48911A8A192A5E57442CBADA9C7E344A1FE31AC512029239665B57FBF201A7FF43C242B79FE97C95AF8CE1D0F1EBC761C61778DA6974A39FBF1055E7968E574FBE64B0B26D82CC57CB72F5FD1B2D19A650E9D6836B91D706B79F8DDCF8749A32BBF6531AB39CB1BBB097EBE4E43AA653A5ACE6277B1154C7F83D0CB8788F82323C6A5ACGGC039GGD0CB818294G94G88G88G310BE8B1F82323C6A5ACGGC039GG8CGGGGGGGGGGGG
	GGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGDFACGGGG
**end of data**/
}
public String getDoubleOrderBitString()
{
	StringBuffer doubleOrders = new StringBuffer();
	
	//this is a little nasty; originally all toggle buttons were in an array, which was
	//cleaner.  Unfortunately, Visual Age didn't like losing direct sight of each component
	//for visual composition...
	
	//9
	if(getJToggle9A().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	if(getJToggle9B().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	//10
	if(getJToggle10A().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	if(getJToggle10B().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	//11
	if(getJToggle11A().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	if(getJToggle11B().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	//12
	if(getJToggle12A().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	if(getJToggle12B().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	//13
	if(getJToggle13A().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	if(getJToggle13B().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	//14
	if(getJToggle14A().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	if(getJToggle14B().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	//15
	if(getJToggle15A().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	if(getJToggle15B().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	//16
	if(getJToggle16A().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	if(getJToggle16B().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	//17
	if(getJToggle17A().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	if(getJToggle17B().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	//18
	if(getJToggle18A().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	if(getJToggle18B().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	//19
	if(getJToggle19A().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	if(getJToggle19B().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	//20
	if(getJToggle20A().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	if(getJToggle20B().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	//21
	if(getJToggle21A().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	if(getJToggle21B().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	//22
	if(getJToggle22A().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	if(getJToggle22B().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	//23
	if(getJToggle23A().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	if(getJToggle23B().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	//24
	if(getJToggle24A().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	if(getJToggle24B().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	//25
	if(getJToggle25A().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
	if(getJToggle25B().isSelected())
		doubleOrders.append("1");
	else
		doubleOrders.append("0");
		
	return doubleOrders.toString();
	
}
/**
 * Return the JLabel10 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel10() {
	if (ivjJLabel10 == null) {
		try {
			ivjJLabel10 = new javax.swing.JLabel();
			ivjJLabel10.setName("JLabel10");
			ivjJLabel10.setText("10");
			ivjJLabel10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel10;
}
/**
 * Return the JLabel11 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel11() {
	if (ivjJLabel11 == null) {
		try {
			ivjJLabel11 = new javax.swing.JLabel();
			ivjJLabel11.setName("JLabel11");
			ivjJLabel11.setText("11");
			ivjJLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel11;
}
/**
 * Return the JLabel12 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel12() {
	if (ivjJLabel12 == null) {
		try {
			ivjJLabel12 = new javax.swing.JLabel();
			ivjJLabel12.setName("JLabel12");
			ivjJLabel12.setText("12");
			ivjJLabel12.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel12;
}
/**
 * Return the JLabel13 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel13() {
	if (ivjJLabel13 == null) {
		try {
			ivjJLabel13 = new javax.swing.JLabel();
			ivjJLabel13.setName("JLabel13");
			ivjJLabel13.setText("13");
			ivjJLabel13.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel13;
}
/**
 * Return the JLabel14 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel14() {
	if (ivjJLabel14 == null) {
		try {
			ivjJLabel14 = new javax.swing.JLabel();
			ivjJLabel14.setName("JLabel14");
			ivjJLabel14.setText("14");
			ivjJLabel14.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel14;
}
/**
 * Return the JLabel15 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel15() {
	if (ivjJLabel15 == null) {
		try {
			ivjJLabel15 = new javax.swing.JLabel();
			ivjJLabel15.setName("JLabel15");
			ivjJLabel15.setText("15");
			ivjJLabel15.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel15;
}
/**
 * Return the JLabel16 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel16() {
	if (ivjJLabel16 == null) {
		try {
			ivjJLabel16 = new javax.swing.JLabel();
			ivjJLabel16.setName("JLabel16");
			ivjJLabel16.setText("16");
			ivjJLabel16.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel16;
}
/**
 * Return the JLabel17 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel17() {
	if (ivjJLabel17 == null) {
		try {
			ivjJLabel17 = new javax.swing.JLabel();
			ivjJLabel17.setName("JLabel17");
			ivjJLabel17.setText("17");
			ivjJLabel17.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel17;
}
/**
 * Return the JLabel18 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel18() {
	if (ivjJLabel18 == null) {
		try {
			ivjJLabel18 = new javax.swing.JLabel();
			ivjJLabel18.setName("JLabel18");
			ivjJLabel18.setText("18");
			ivjJLabel18.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel18;
}
/**
 * Return the JLabel19 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel19() {
	if (ivjJLabel19 == null) {
		try {
			ivjJLabel19 = new javax.swing.JLabel();
			ivjJLabel19.setName("JLabel19");
			ivjJLabel19.setText("19");
			ivjJLabel19.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel19;
}
/**
 * Return the JLabel20 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel20() {
	if (ivjJLabel20 == null) {
		try {
			ivjJLabel20 = new javax.swing.JLabel();
			ivjJLabel20.setName("JLabel20");
			ivjJLabel20.setText("20");
			ivjJLabel20.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel20;
}
/**
 * Return the JLabel21 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel21() {
	if (ivjJLabel21 == null) {
		try {
			ivjJLabel21 = new javax.swing.JLabel();
			ivjJLabel21.setName("JLabel21");
			ivjJLabel21.setText("21");
			ivjJLabel21.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel21;
}
/**
 * Return the JLabel22 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel22() {
	if (ivjJLabel22 == null) {
		try {
			ivjJLabel22 = new javax.swing.JLabel();
			ivjJLabel22.setName("JLabel22");
			ivjJLabel22.setText("22");
			ivjJLabel22.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel22;
}
/**
 * Return the JLabel23 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel23() {
	if (ivjJLabel23 == null) {
		try {
			ivjJLabel23 = new javax.swing.JLabel();
			ivjJLabel23.setName("JLabel23");
			ivjJLabel23.setText("23");
			ivjJLabel23.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel23;
}
/**
 * Return the JLabel24 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel24() {
	if (ivjJLabel24 == null) {
		try {
			ivjJLabel24 = new javax.swing.JLabel();
			ivjJLabel24.setName("JLabel24");
			ivjJLabel24.setText("24");
			ivjJLabel24.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel24;
}
/**
 * Return the JLabel25 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel25() {
	if (ivjJLabel25 == null) {
		try {
			ivjJLabel25 = new javax.swing.JLabel();
			ivjJLabel25.setName("JLabel25");
			ivjJLabel25.setText("25");
			ivjJLabel25.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel25;
}
/**
 * Return the JLabel9 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel9() {
	if (ivjJLabel9 == null) {
		try {
			ivjJLabel9 = new javax.swing.JLabel();
			ivjJLabel9.setName("JLabel9");
			ivjJLabel9.setText("9");
			ivjJLabel9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel9;
}
/**
 * Return the JToggle10A property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle10A() {
	if (ivjJToggle10A == null) {
		try {
			ivjJToggle10A = new javax.swing.JToggleButton();
			ivjJToggle10A.setName("JToggle10A");
			ivjJToggle10A.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle10A;
}
/**
 * Return the JToggle10B property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle10B() {
	if (ivjJToggle10B == null) {
		try {
			ivjJToggle10B = new javax.swing.JToggleButton();
			ivjJToggle10B.setName("JToggle10B");
			ivjJToggle10B.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle10B;
}
/**
 * Return the JToggle11A property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle11A() {
	if (ivjJToggle11A == null) {
		try {
			ivjJToggle11A = new javax.swing.JToggleButton();
			ivjJToggle11A.setName("JToggle11A");
			ivjJToggle11A.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle11A;
}
/**
 * Return the JToggle11B property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle11B() {
	if (ivjJToggle11B == null) {
		try {
			ivjJToggle11B = new javax.swing.JToggleButton();
			ivjJToggle11B.setName("JToggle11B");
			ivjJToggle11B.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle11B;
}
/**
 * Return the JToggle12A property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle12A() {
	if (ivjJToggle12A == null) {
		try {
			ivjJToggle12A = new javax.swing.JToggleButton();
			ivjJToggle12A.setName("JToggle12A");
			ivjJToggle12A.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle12A;
}
/**
 * Return the JToggle12B property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle12B() {
	if (ivjJToggle12B == null) {
		try {
			ivjJToggle12B = new javax.swing.JToggleButton();
			ivjJToggle12B.setName("JToggle12B");
			ivjJToggle12B.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle12B;
}
/**
 * Return the JToggle13A property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle13A() {
	if (ivjJToggle13A == null) {
		try {
			ivjJToggle13A = new javax.swing.JToggleButton();
			ivjJToggle13A.setName("JToggle13A");
			ivjJToggle13A.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle13A;
}
/**
 * Return the JToggle13B property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle13B() {
	if (ivjJToggle13B == null) {
		try {
			ivjJToggle13B = new javax.swing.JToggleButton();
			ivjJToggle13B.setName("JToggle13B");
			ivjJToggle13B.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle13B;
}
/**
 * Return the JToggle14A property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle14A() {
	if (ivjJToggle14A == null) {
		try {
			ivjJToggle14A = new javax.swing.JToggleButton();
			ivjJToggle14A.setName("JToggle14A");
			ivjJToggle14A.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle14A;
}
/**
 * Return the JToggle14B property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle14B() {
	if (ivjJToggle14B == null) {
		try {
			ivjJToggle14B = new javax.swing.JToggleButton();
			ivjJToggle14B.setName("JToggle14B");
			ivjJToggle14B.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle14B;
}
/**
 * Return the JToggle15A property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle15A() {
	if (ivjJToggle15A == null) {
		try {
			ivjJToggle15A = new javax.swing.JToggleButton();
			ivjJToggle15A.setName("JToggle15A");
			ivjJToggle15A.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle15A;
}
/**
 * Return the JToggle15B property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle15B() {
	if (ivjJToggle15B == null) {
		try {
			ivjJToggle15B = new javax.swing.JToggleButton();
			ivjJToggle15B.setName("JToggle15B");
			ivjJToggle15B.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle15B;
}
/**
 * Return the JToggle16A property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle16A() {
	if (ivjJToggle16A == null) {
		try {
			ivjJToggle16A = new javax.swing.JToggleButton();
			ivjJToggle16A.setName("JToggle16A");
			ivjJToggle16A.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle16A;
}
/**
 * Return the JToggle16B property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle16B() {
	if (ivjJToggle16B == null) {
		try {
			ivjJToggle16B = new javax.swing.JToggleButton();
			ivjJToggle16B.setName("JToggle16B");
			ivjJToggle16B.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle16B;
}
/**
 * Return the JToggle17A property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle17A() {
	if (ivjJToggle17A == null) {
		try {
			ivjJToggle17A = new javax.swing.JToggleButton();
			ivjJToggle17A.setName("JToggle17A");
			ivjJToggle17A.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle17A;
}
/**
 * Return the JToggle17B property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle17B() {
	if (ivjJToggle17B == null) {
		try {
			ivjJToggle17B = new javax.swing.JToggleButton();
			ivjJToggle17B.setName("JToggle17B");
			ivjJToggle17B.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle17B;
}
/**
 * Return the JToggle18A property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle18A() {
	if (ivjJToggle18A == null) {
		try {
			ivjJToggle18A = new javax.swing.JToggleButton();
			ivjJToggle18A.setName("JToggle18A");
			ivjJToggle18A.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle18A;
}
/**
 * Return the JToggle18B property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle18B() {
	if (ivjJToggle18B == null) {
		try {
			ivjJToggle18B = new javax.swing.JToggleButton();
			ivjJToggle18B.setName("JToggle18B");
			ivjJToggle18B.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle18B;
}
/**
 * Return the JToggle19A property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle19A() {
	if (ivjJToggle19A == null) {
		try {
			ivjJToggle19A = new javax.swing.JToggleButton();
			ivjJToggle19A.setName("JToggle19A");
			ivjJToggle19A.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle19A;
}
/**
 * Return the JToggle19B property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle19B() {
	if (ivjJToggle19B == null) {
		try {
			ivjJToggle19B = new javax.swing.JToggleButton();
			ivjJToggle19B.setName("JToggle19B");
			ivjJToggle19B.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle19B;
}
/**
 * Return the JToggle20A property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle20A() {
	if (ivjJToggle20A == null) {
		try {
			ivjJToggle20A = new javax.swing.JToggleButton();
			ivjJToggle20A.setName("JToggle20A");
			ivjJToggle20A.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle20A;
}
/**
 * Return the JToggle20B property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle20B() {
	if (ivjJToggle20B == null) {
		try {
			ivjJToggle20B = new javax.swing.JToggleButton();
			ivjJToggle20B.setName("JToggle20B");
			ivjJToggle20B.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle20B;
}
/**
 * Return the JToggle21A property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle21A() {
	if (ivjJToggle21A == null) {
		try {
			ivjJToggle21A = new javax.swing.JToggleButton();
			ivjJToggle21A.setName("JToggle21A");
			ivjJToggle21A.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle21A;
}
/**
 * Return the JToggle21B property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle21B() {
	if (ivjJToggle21B == null) {
		try {
			ivjJToggle21B = new javax.swing.JToggleButton();
			ivjJToggle21B.setName("JToggle21B");
			ivjJToggle21B.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle21B;
}
/**
 * Return the JToggle22A property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle22A() {
	if (ivjJToggle22A == null) {
		try {
			ivjJToggle22A = new javax.swing.JToggleButton();
			ivjJToggle22A.setName("JToggle22A");
			ivjJToggle22A.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle22A;
}
/**
 * Return the JToggle22B property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle22B() {
	if (ivjJToggle22B == null) {
		try {
			ivjJToggle22B = new javax.swing.JToggleButton();
			ivjJToggle22B.setName("JToggle22B");
			ivjJToggle22B.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle22B;
}
/**
 * Return the JToggle23A property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle23A() {
	if (ivjJToggle23A == null) {
		try {
			ivjJToggle23A = new javax.swing.JToggleButton();
			ivjJToggle23A.setName("JToggle23A");
			ivjJToggle23A.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle23A;
}
/**
 * Return the JToggle23B property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle23B() {
	if (ivjJToggle23B == null) {
		try {
			ivjJToggle23B = new javax.swing.JToggleButton();
			ivjJToggle23B.setName("JToggle23B");
			ivjJToggle23B.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle23B;
}
/**
 * Return the JToggle24A property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle24A() {
	if (ivjJToggle24A == null) {
		try {
			ivjJToggle24A = new javax.swing.JToggleButton();
			ivjJToggle24A.setName("JToggle24A");
			ivjJToggle24A.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle24A;
}
/**
 * Return the JToggle24B property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle24B() {
	if (ivjJToggle24B == null) {
		try {
			ivjJToggle24B = new javax.swing.JToggleButton();
			ivjJToggle24B.setName("JToggle24B");
			ivjJToggle24B.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle24B;
}
/**
 * Return the JToggle25A1 property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle25A() {
	if (ivjJToggle25A == null) {
		try {
			ivjJToggle25A = new javax.swing.JToggleButton();
			ivjJToggle25A.setName("JToggle25A");
			ivjJToggle25A.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle25A;
}
/**
 * Return the JToggle25B1 property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle25B() {
	if (ivjJToggle25B == null) {
		try {
			ivjJToggle25B = new javax.swing.JToggleButton();
			ivjJToggle25B.setName("JToggle25B");
			ivjJToggle25B.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle25B;
}
/**
 * Return the JToggle9A property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle9A() {
	if (ivjJToggle9A == null) {
		try {
			ivjJToggle9A = new javax.swing.JToggleButton();
			ivjJToggle9A.setName("JToggle9A");
			ivjJToggle9A.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle9A;
}
/**
 * Return the JToggle9B property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getJToggle9B() {
	if (ivjJToggle9B == null) {
		try {
			ivjJToggle9B = new javax.swing.JToggleButton();
			ivjJToggle9B.setName("JToggle9B");
			ivjJToggle9B.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToggle9B;
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
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJToggle9A().addActionListener(ivjEventHandler);
	getJToggle9B().addActionListener(ivjEventHandler);
	getJToggle24A().addActionListener(ivjEventHandler);
	getJToggle24B().addActionListener(ivjEventHandler);
	getJToggle23A().addActionListener(ivjEventHandler);
	getJToggle23B().addActionListener(ivjEventHandler);
	getJToggle22A().addActionListener(ivjEventHandler);
	getJToggle22B().addActionListener(ivjEventHandler);
	getJToggle21A().addActionListener(ivjEventHandler);
	getJToggle21B().addActionListener(ivjEventHandler);
	getJToggle20A().addActionListener(ivjEventHandler);
	getJToggle20B().addActionListener(ivjEventHandler);
	getJToggle19A().addActionListener(ivjEventHandler);
	getJToggle19B().addActionListener(ivjEventHandler);
	getJToggle18A().addActionListener(ivjEventHandler);
	getJToggle18B().addActionListener(ivjEventHandler);
	getJToggle17A().addActionListener(ivjEventHandler);
	getJToggle17B().addActionListener(ivjEventHandler);
	getJToggle16A().addActionListener(ivjEventHandler);
	getJToggle16B().addActionListener(ivjEventHandler);
	getJToggle15A().addActionListener(ivjEventHandler);
	getJToggle15B().addActionListener(ivjEventHandler);
	getJToggle14A().addActionListener(ivjEventHandler);
	getJToggle14B().addActionListener(ivjEventHandler);
	getJToggle13A().addActionListener(ivjEventHandler);
	getJToggle13B().addActionListener(ivjEventHandler);
	getJToggle12A().addActionListener(ivjEventHandler);
	getJToggle12B().addActionListener(ivjEventHandler);
	getJToggle11A().addActionListener(ivjEventHandler);
	getJToggle11B().addActionListener(ivjEventHandler);
	getJToggle10A().addActionListener(ivjEventHandler);
	getJToggle10B().addActionListener(ivjEventHandler);
	getJToggle25A().addActionListener(ivjEventHandler);
	getJToggle25B().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("SpecialDoubleOrderBitPanel");
		setPreferredSize(new java.awt.Dimension(353, 80));
		setLayout(new java.awt.GridBagLayout());
		setSize(375, 80);
		setMaximumSize(new java.awt.Dimension(353, 80));
		setMinimumSize(new java.awt.Dimension(353, 80));

		java.awt.GridBagConstraints constraintsJToggle9A = new java.awt.GridBagConstraints();
		constraintsJToggle9A.gridx = 2; constraintsJToggle9A.gridy = 3;
		constraintsJToggle9A.ipadx = -17;
		constraintsJToggle9A.ipady = 5;
		constraintsJToggle9A.insets = new java.awt.Insets(2, 10, 2, 1);
		add(getJToggle9A(), constraintsJToggle9A);

		java.awt.GridBagConstraints constraintsJToggle9B = new java.awt.GridBagConstraints();
		constraintsJToggle9B.gridx = 2; constraintsJToggle9B.gridy = 4;
		constraintsJToggle9B.ipadx = -17;
		constraintsJToggle9B.ipady = 5;
		constraintsJToggle9B.insets = new java.awt.Insets(2, 10, 12, 1);
		add(getJToggle9B(), constraintsJToggle9B);

		java.awt.GridBagConstraints constraintsJLabel9 = new java.awt.GridBagConstraints();
		constraintsJLabel9.gridx = 2; constraintsJLabel9.gridy = 2;
		constraintsJLabel9.ipadx = 11;
		constraintsJLabel9.insets = new java.awt.Insets(15, 10, 1, 1);
		add(getJLabel9(), constraintsJLabel9);

		java.awt.GridBagConstraints constraintsJLabel10 = new java.awt.GridBagConstraints();
		constraintsJLabel10.gridx = 3; constraintsJLabel10.gridy = 2;
		constraintsJLabel10.ipadx = 4;
		constraintsJLabel10.insets = new java.awt.Insets(15, 2, 1, 1);
		add(getJLabel10(), constraintsJLabel10);

		java.awt.GridBagConstraints constraintsJToggle10A = new java.awt.GridBagConstraints();
		constraintsJToggle10A.gridx = 3; constraintsJToggle10A.gridy = 3;
		constraintsJToggle10A.ipadx = -17;
		constraintsJToggle10A.ipady = 5;
		constraintsJToggle10A.insets = new java.awt.Insets(2, 2, 2, 1);
		add(getJToggle10A(), constraintsJToggle10A);

		java.awt.GridBagConstraints constraintsJToggle10B = new java.awt.GridBagConstraints();
		constraintsJToggle10B.gridx = 3; constraintsJToggle10B.gridy = 4;
		constraintsJToggle10B.ipadx = -17;
		constraintsJToggle10B.ipady = 5;
		constraintsJToggle10B.insets = new java.awt.Insets(2, 2, 12, 1);
		add(getJToggle10B(), constraintsJToggle10B);

		java.awt.GridBagConstraints constraintsJToggle11A = new java.awt.GridBagConstraints();
		constraintsJToggle11A.gridx = 4; constraintsJToggle11A.gridy = 3;
		constraintsJToggle11A.ipadx = -17;
		constraintsJToggle11A.ipady = 5;
		constraintsJToggle11A.insets = new java.awt.Insets(2, 2, 2, 1);
		add(getJToggle11A(), constraintsJToggle11A);

		java.awt.GridBagConstraints constraintsJToggle11B = new java.awt.GridBagConstraints();
		constraintsJToggle11B.gridx = 4; constraintsJToggle11B.gridy = 4;
		constraintsJToggle11B.ipadx = -17;
		constraintsJToggle11B.ipady = 5;
		constraintsJToggle11B.insets = new java.awt.Insets(2, 2, 12, 1);
		add(getJToggle11B(), constraintsJToggle11B);

		java.awt.GridBagConstraints constraintsJLabel11 = new java.awt.GridBagConstraints();
		constraintsJLabel11.gridx = 4; constraintsJLabel11.gridy = 2;
		constraintsJLabel11.ipadx = 4;
		constraintsJLabel11.insets = new java.awt.Insets(15, 2, 1, 1);
		add(getJLabel11(), constraintsJLabel11);

		java.awt.GridBagConstraints constraintsJLabel12 = new java.awt.GridBagConstraints();
		constraintsJLabel12.gridx = 5; constraintsJLabel12.gridy = 2;
		constraintsJLabel12.ipadx = 4;
		constraintsJLabel12.insets = new java.awt.Insets(15, 2, 1, 1);
		add(getJLabel12(), constraintsJLabel12);

		java.awt.GridBagConstraints constraintsJToggle12A = new java.awt.GridBagConstraints();
		constraintsJToggle12A.gridx = 5; constraintsJToggle12A.gridy = 3;
		constraintsJToggle12A.ipadx = -17;
		constraintsJToggle12A.ipady = 5;
		constraintsJToggle12A.insets = new java.awt.Insets(2, 2, 2, 1);
		add(getJToggle12A(), constraintsJToggle12A);

		java.awt.GridBagConstraints constraintsJToggle12B = new java.awt.GridBagConstraints();
		constraintsJToggle12B.gridx = 5; constraintsJToggle12B.gridy = 4;
		constraintsJToggle12B.ipadx = -17;
		constraintsJToggle12B.ipady = 5;
		constraintsJToggle12B.insets = new java.awt.Insets(2, 2, 12, 1);
		add(getJToggle12B(), constraintsJToggle12B);

		java.awt.GridBagConstraints constraintsJToggle13A = new java.awt.GridBagConstraints();
		constraintsJToggle13A.gridx = 6; constraintsJToggle13A.gridy = 3;
		constraintsJToggle13A.ipadx = -17;
		constraintsJToggle13A.ipady = 5;
		constraintsJToggle13A.insets = new java.awt.Insets(2, 2, 2, 1);
		add(getJToggle13A(), constraintsJToggle13A);

		java.awt.GridBagConstraints constraintsJToggle13B = new java.awt.GridBagConstraints();
		constraintsJToggle13B.gridx = 6; constraintsJToggle13B.gridy = 4;
		constraintsJToggle13B.ipadx = -17;
		constraintsJToggle13B.ipady = 5;
		constraintsJToggle13B.insets = new java.awt.Insets(2, 2, 12, 1);
		add(getJToggle13B(), constraintsJToggle13B);

		java.awt.GridBagConstraints constraintsJLabel13 = new java.awt.GridBagConstraints();
		constraintsJLabel13.gridx = 6; constraintsJLabel13.gridy = 2;
		constraintsJLabel13.ipadx = 4;
		constraintsJLabel13.insets = new java.awt.Insets(15, 2, 1, 1);
		add(getJLabel13(), constraintsJLabel13);

		java.awt.GridBagConstraints constraintsJLabel14 = new java.awt.GridBagConstraints();
		constraintsJLabel14.gridx = 7; constraintsJLabel14.gridy = 2;
		constraintsJLabel14.ipadx = 4;
		constraintsJLabel14.insets = new java.awt.Insets(15, 2, 1, 1);
		add(getJLabel14(), constraintsJLabel14);

		java.awt.GridBagConstraints constraintsJToggle14A = new java.awt.GridBagConstraints();
		constraintsJToggle14A.gridx = 7; constraintsJToggle14A.gridy = 3;
		constraintsJToggle14A.ipadx = -17;
		constraintsJToggle14A.ipady = 5;
		constraintsJToggle14A.insets = new java.awt.Insets(2, 2, 2, 1);
		add(getJToggle14A(), constraintsJToggle14A);

		java.awt.GridBagConstraints constraintsJToggle14B = new java.awt.GridBagConstraints();
		constraintsJToggle14B.gridx = 7; constraintsJToggle14B.gridy = 4;
		constraintsJToggle14B.ipadx = -17;
		constraintsJToggle14B.ipady = 5;
		constraintsJToggle14B.insets = new java.awt.Insets(2, 2, 12, 1);
		add(getJToggle14B(), constraintsJToggle14B);

		java.awt.GridBagConstraints constraintsJToggle15A = new java.awt.GridBagConstraints();
		constraintsJToggle15A.gridx = 8; constraintsJToggle15A.gridy = 3;
		constraintsJToggle15A.ipadx = -17;
		constraintsJToggle15A.ipady = 5;
		constraintsJToggle15A.insets = new java.awt.Insets(2, 2, 2, 1);
		add(getJToggle15A(), constraintsJToggle15A);

		java.awt.GridBagConstraints constraintsJToggle15B = new java.awt.GridBagConstraints();
		constraintsJToggle15B.gridx = 8; constraintsJToggle15B.gridy = 4;
		constraintsJToggle15B.ipadx = -17;
		constraintsJToggle15B.ipady = 5;
		constraintsJToggle15B.insets = new java.awt.Insets(2, 2, 12, 1);
		add(getJToggle15B(), constraintsJToggle15B);

		java.awt.GridBagConstraints constraintsJLabel15 = new java.awt.GridBagConstraints();
		constraintsJLabel15.gridx = 8; constraintsJLabel15.gridy = 2;
		constraintsJLabel15.ipadx = 4;
		constraintsJLabel15.insets = new java.awt.Insets(15, 2, 1, 1);
		add(getJLabel15(), constraintsJLabel15);

		java.awt.GridBagConstraints constraintsJLabel16 = new java.awt.GridBagConstraints();
		constraintsJLabel16.gridx = 9; constraintsJLabel16.gridy = 2;
		constraintsJLabel16.ipadx = 4;
		constraintsJLabel16.insets = new java.awt.Insets(15, 2, 1, 1);
		add(getJLabel16(), constraintsJLabel16);

		java.awt.GridBagConstraints constraintsJToggle16A = new java.awt.GridBagConstraints();
		constraintsJToggle16A.gridx = 9; constraintsJToggle16A.gridy = 3;
		constraintsJToggle16A.ipadx = -17;
		constraintsJToggle16A.ipady = 5;
		constraintsJToggle16A.insets = new java.awt.Insets(2, 2, 2, 1);
		add(getJToggle16A(), constraintsJToggle16A);

		java.awt.GridBagConstraints constraintsJToggle16B = new java.awt.GridBagConstraints();
		constraintsJToggle16B.gridx = 9; constraintsJToggle16B.gridy = 4;
		constraintsJToggle16B.ipadx = -17;
		constraintsJToggle16B.ipady = 5;
		constraintsJToggle16B.insets = new java.awt.Insets(2, 2, 12, 1);
		add(getJToggle16B(), constraintsJToggle16B);

		java.awt.GridBagConstraints constraintsJToggle17A = new java.awt.GridBagConstraints();
		constraintsJToggle17A.gridx = 10; constraintsJToggle17A.gridy = 3;
		constraintsJToggle17A.ipadx = -17;
		constraintsJToggle17A.ipady = 5;
		constraintsJToggle17A.insets = new java.awt.Insets(2, 2, 2, 1);
		add(getJToggle17A(), constraintsJToggle17A);

		java.awt.GridBagConstraints constraintsJToggle17B = new java.awt.GridBagConstraints();
		constraintsJToggle17B.gridx = 10; constraintsJToggle17B.gridy = 4;
		constraintsJToggle17B.ipadx = -17;
		constraintsJToggle17B.ipady = 5;
		constraintsJToggle17B.insets = new java.awt.Insets(2, 2, 12, 1);
		add(getJToggle17B(), constraintsJToggle17B);

		java.awt.GridBagConstraints constraintsJLabel17 = new java.awt.GridBagConstraints();
		constraintsJLabel17.gridx = 10; constraintsJLabel17.gridy = 2;
		constraintsJLabel17.ipadx = 4;
		constraintsJLabel17.insets = new java.awt.Insets(15, 1, 1, 2);
		add(getJLabel17(), constraintsJLabel17);

		java.awt.GridBagConstraints constraintsJLabel18 = new java.awt.GridBagConstraints();
		constraintsJLabel18.gridx = 11; constraintsJLabel18.gridy = 2;
		constraintsJLabel18.ipadx = 4;
		constraintsJLabel18.insets = new java.awt.Insets(15, 1, 1, 2);
		add(getJLabel18(), constraintsJLabel18);

		java.awt.GridBagConstraints constraintsJToggle18A = new java.awt.GridBagConstraints();
		constraintsJToggle18A.gridx = 11; constraintsJToggle18A.gridy = 3;
		constraintsJToggle18A.ipadx = -17;
		constraintsJToggle18A.ipady = 5;
		constraintsJToggle18A.insets = new java.awt.Insets(2, 2, 2, 1);
		add(getJToggle18A(), constraintsJToggle18A);

		java.awt.GridBagConstraints constraintsJToggle18B = new java.awt.GridBagConstraints();
		constraintsJToggle18B.gridx = 11; constraintsJToggle18B.gridy = 4;
		constraintsJToggle18B.ipadx = -17;
		constraintsJToggle18B.ipady = 5;
		constraintsJToggle18B.insets = new java.awt.Insets(2, 2, 12, 1);
		add(getJToggle18B(), constraintsJToggle18B);

		java.awt.GridBagConstraints constraintsJToggle19A = new java.awt.GridBagConstraints();
		constraintsJToggle19A.gridx = 12; constraintsJToggle19A.gridy = 3;
		constraintsJToggle19A.ipadx = -17;
		constraintsJToggle19A.ipady = 5;
		constraintsJToggle19A.insets = new java.awt.Insets(2, 2, 2, 1);
		add(getJToggle19A(), constraintsJToggle19A);

		java.awt.GridBagConstraints constraintsJToggle19B = new java.awt.GridBagConstraints();
		constraintsJToggle19B.gridx = 12; constraintsJToggle19B.gridy = 4;
		constraintsJToggle19B.ipadx = -17;
		constraintsJToggle19B.ipady = 5;
		constraintsJToggle19B.insets = new java.awt.Insets(2, 2, 12, 1);
		add(getJToggle19B(), constraintsJToggle19B);

		java.awt.GridBagConstraints constraintsJLabel19 = new java.awt.GridBagConstraints();
		constraintsJLabel19.gridx = 12; constraintsJLabel19.gridy = 2;
		constraintsJLabel19.ipadx = 4;
		constraintsJLabel19.insets = new java.awt.Insets(15, 1, 1, 2);
		add(getJLabel19(), constraintsJLabel19);

		java.awt.GridBagConstraints constraintsJLabel20 = new java.awt.GridBagConstraints();
		constraintsJLabel20.gridx = 13; constraintsJLabel20.gridy = 2;
		constraintsJLabel20.ipadx = 4;
		constraintsJLabel20.insets = new java.awt.Insets(15, 1, 1, 2);
		add(getJLabel20(), constraintsJLabel20);

		java.awt.GridBagConstraints constraintsJToggle20A = new java.awt.GridBagConstraints();
		constraintsJToggle20A.gridx = 13; constraintsJToggle20A.gridy = 3;
		constraintsJToggle20A.ipadx = -17;
		constraintsJToggle20A.ipady = 5;
		constraintsJToggle20A.insets = new java.awt.Insets(2, 2, 2, 1);
		add(getJToggle20A(), constraintsJToggle20A);

		java.awt.GridBagConstraints constraintsJToggle20B = new java.awt.GridBagConstraints();
		constraintsJToggle20B.gridx = 13; constraintsJToggle20B.gridy = 4;
		constraintsJToggle20B.ipadx = -17;
		constraintsJToggle20B.ipady = 5;
		constraintsJToggle20B.insets = new java.awt.Insets(2, 2, 12, 1);
		add(getJToggle20B(), constraintsJToggle20B);

		java.awt.GridBagConstraints constraintsJToggle21A = new java.awt.GridBagConstraints();
		constraintsJToggle21A.gridx = 14; constraintsJToggle21A.gridy = 3;
		constraintsJToggle21A.ipadx = -17;
		constraintsJToggle21A.ipady = 5;
		constraintsJToggle21A.insets = new java.awt.Insets(2, 2, 2, 1);
		add(getJToggle21A(), constraintsJToggle21A);

		java.awt.GridBagConstraints constraintsJToggle21B = new java.awt.GridBagConstraints();
		constraintsJToggle21B.gridx = 14; constraintsJToggle21B.gridy = 4;
		constraintsJToggle21B.ipadx = -17;
		constraintsJToggle21B.ipady = 5;
		constraintsJToggle21B.insets = new java.awt.Insets(2, 2, 12, 1);
		add(getJToggle21B(), constraintsJToggle21B);

		java.awt.GridBagConstraints constraintsJLabel21 = new java.awt.GridBagConstraints();
		constraintsJLabel21.gridx = 14; constraintsJLabel21.gridy = 2;
		constraintsJLabel21.ipadx = 4;
		constraintsJLabel21.insets = new java.awt.Insets(15, 1, 1, 2);
		add(getJLabel21(), constraintsJLabel21);

		java.awt.GridBagConstraints constraintsJLabel22 = new java.awt.GridBagConstraints();
		constraintsJLabel22.gridx = 15; constraintsJLabel22.gridy = 2;
		constraintsJLabel22.ipadx = 4;
		constraintsJLabel22.insets = new java.awt.Insets(15, 1, 1, 2);
		add(getJLabel22(), constraintsJLabel22);

		java.awt.GridBagConstraints constraintsJToggle22A = new java.awt.GridBagConstraints();
		constraintsJToggle22A.gridx = 15; constraintsJToggle22A.gridy = 3;
		constraintsJToggle22A.ipadx = -17;
		constraintsJToggle22A.ipady = 5;
		constraintsJToggle22A.insets = new java.awt.Insets(2, 2, 2, 1);
		add(getJToggle22A(), constraintsJToggle22A);

		java.awt.GridBagConstraints constraintsJToggle22B = new java.awt.GridBagConstraints();
		constraintsJToggle22B.gridx = 15; constraintsJToggle22B.gridy = 4;
		constraintsJToggle22B.ipadx = -17;
		constraintsJToggle22B.ipady = 5;
		constraintsJToggle22B.insets = new java.awt.Insets(2, 2, 12, 1);
		add(getJToggle22B(), constraintsJToggle22B);

		java.awt.GridBagConstraints constraintsJToggle23A = new java.awt.GridBagConstraints();
		constraintsJToggle23A.gridx = 16; constraintsJToggle23A.gridy = 3;
		constraintsJToggle23A.ipadx = -17;
		constraintsJToggle23A.ipady = 5;
		constraintsJToggle23A.insets = new java.awt.Insets(2, 2, 2, 1);
		add(getJToggle23A(), constraintsJToggle23A);

		java.awt.GridBagConstraints constraintsJToggle23B = new java.awt.GridBagConstraints();
		constraintsJToggle23B.gridx = 16; constraintsJToggle23B.gridy = 4;
		constraintsJToggle23B.ipadx = -17;
		constraintsJToggle23B.ipady = 5;
		constraintsJToggle23B.insets = new java.awt.Insets(2, 2, 12, 1);
		add(getJToggle23B(), constraintsJToggle23B);

		java.awt.GridBagConstraints constraintsJLabel23 = new java.awt.GridBagConstraints();
		constraintsJLabel23.gridx = 16; constraintsJLabel23.gridy = 2;
		constraintsJLabel23.ipadx = 4;
		constraintsJLabel23.insets = new java.awt.Insets(15, 1, 1, 2);
		add(getJLabel23(), constraintsJLabel23);

		java.awt.GridBagConstraints constraintsJLabel24 = new java.awt.GridBagConstraints();
		constraintsJLabel24.gridx = 17; constraintsJLabel24.gridy = 2;
		constraintsJLabel24.ipadx = 4;
		constraintsJLabel24.insets = new java.awt.Insets(15, 1, 1, 2);
		add(getJLabel24(), constraintsJLabel24);

		java.awt.GridBagConstraints constraintsJToggle24A = new java.awt.GridBagConstraints();
		constraintsJToggle24A.gridx = 17; constraintsJToggle24A.gridy = 3;
		constraintsJToggle24A.ipadx = -17;
		constraintsJToggle24A.ipady = 5;
		constraintsJToggle24A.insets = new java.awt.Insets(2, 2, 2, 1);
		add(getJToggle24A(), constraintsJToggle24A);

		java.awt.GridBagConstraints constraintsJToggle24B = new java.awt.GridBagConstraints();
		constraintsJToggle24B.gridx = 17; constraintsJToggle24B.gridy = 4;
		constraintsJToggle24B.ipadx = -17;
		constraintsJToggle24B.ipady = 5;
		constraintsJToggle24B.insets = new java.awt.Insets(2, 2, 12, 1);
		add(getJToggle24B(), constraintsJToggle24B);

		java.awt.GridBagConstraints constraintsActionPasser = new java.awt.GridBagConstraints();
		constraintsActionPasser.gridx = 2; constraintsActionPasser.gridy = 2;
		constraintsActionPasser.gridwidth = -1;
constraintsActionPasser.gridheight = -1;
		constraintsActionPasser.ipadx = -35;
		constraintsActionPasser.ipady = -11;
		add(getActionPasser(), constraintsActionPasser);

		java.awt.GridBagConstraints constraintsJToggle25B = new java.awt.GridBagConstraints();
		constraintsJToggle25B.gridx = 18; constraintsJToggle25B.gridy = 4;
		constraintsJToggle25B.ipadx = -17;
		constraintsJToggle25B.ipady = 5;
		constraintsJToggle25B.insets = new java.awt.Insets(2, 2, 12, 11);
		add(getJToggle25B(), constraintsJToggle25B);

		java.awt.GridBagConstraints constraintsJToggle25A = new java.awt.GridBagConstraints();
		constraintsJToggle25A.gridx = 18; constraintsJToggle25A.gridy = 3;
		constraintsJToggle25A.ipadx = -17;
		constraintsJToggle25A.ipady = 5;
		constraintsJToggle25A.insets = new java.awt.Insets(2, 2, 2, 11);
		add(getJToggle25A(), constraintsJToggle25A);

		java.awt.GridBagConstraints constraintsJLabel25 = new java.awt.GridBagConstraints();
		constraintsJLabel25.gridx = 18; constraintsJLabel25.gridy = 2;
		constraintsJLabel25.ipadx = 4;
		constraintsJLabel25.insets = new java.awt.Insets(15, 1, 1, 12);
		add(getJLabel25(), constraintsJLabel25);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		SpecialDoubleOrderBitPanel aSpecialDoubleOrderBitPanel;
		aSpecialDoubleOrderBitPanel = new SpecialDoubleOrderBitPanel();
		frame.setContentPane(aSpecialDoubleOrderBitPanel);
		frame.setSize(aSpecialDoubleOrderBitPanel.getSize());
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
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
public void setDoubleOrderBitString(String theBits)
{
	getJToggle9A().setSelected(theBits.charAt(0) == '1');
	getJToggle9B().setSelected(theBits.charAt(1) == '1');
	getJToggle10A().setSelected(theBits.charAt(2) == '1');
	getJToggle10B().setSelected(theBits.charAt(3) == '1');
	getJToggle11A().setSelected(theBits.charAt(4) == '1');
	getJToggle11B().setSelected(theBits.charAt(5) == '1');
	getJToggle12A().setSelected(theBits.charAt(6) == '1');
	getJToggle12B().setSelected(theBits.charAt(7) == '1');
	getJToggle13A().setSelected(theBits.charAt(8) == '1');
	getJToggle13B().setSelected(theBits.charAt(9) == '1');
	getJToggle14A().setSelected(theBits.charAt(10) == '1');
	getJToggle14B().setSelected(theBits.charAt(11) == '1');
	getJToggle15A().setSelected(theBits.charAt(12) == '1');
	getJToggle15B().setSelected(theBits.charAt(13) == '1');
	getJToggle16A().setSelected(theBits.charAt(14) == '1');
	getJToggle16B().setSelected(theBits.charAt(15) == '1');
	getJToggle17A().setSelected(theBits.charAt(16) == '1');
	getJToggle17B().setSelected(theBits.charAt(17) == '1');
	getJToggle18A().setSelected(theBits.charAt(18) == '1');
	getJToggle18B().setSelected(theBits.charAt(19) == '1');
	getJToggle19A().setSelected(theBits.charAt(20) == '1');
	getJToggle19B().setSelected(theBits.charAt(21) == '1');
	getJToggle20A().setSelected(theBits.charAt(22) == '1');
	getJToggle20B().setSelected(theBits.charAt(23) == '1');
	getJToggle21A().setSelected(theBits.charAt(24) == '1');
	getJToggle21B().setSelected(theBits.charAt(25) == '1');
	getJToggle22A().setSelected(theBits.charAt(26) == '1');
	getJToggle22B().setSelected(theBits.charAt(27) == '1');
	getJToggle23A().setSelected(theBits.charAt(28) == '1');
	getJToggle23B().setSelected(theBits.charAt(29) == '1');
	getJToggle24A().setSelected(theBits.charAt(30) == '1');
	getJToggle24B().setSelected(theBits.charAt(31) == '1');
	getJToggle25A().setSelected(theBits.charAt(32) == '1');
	getJToggle25B().setSelected(theBits.charAt(33) == '1');

}
/**
 * Comment
 */
public void toggle_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	getActionPasser().doClick();
	return;
}
}
