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
		};
	};
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
	D0CB838494G88G88G33F2D3B1GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFDFFDC9D4555B77541961F7276451656D8D3250AADD52CD45122222226652A282858E20B155628E830C54596DB2AFB8990FEC342A51004A4A4A19192027CCCC8CC1490E2C882918DA6D8E3428D778257DCFE88B789C97171BD33BBF3F6776E6CDD201F3E8FFF540FA73B731D73F3E6F6E7766E19C5302C8D48B027D984411CA1507F361B85417BC6C178D6425F6FE6D8EBD024D9D07C378EE829603FAC
	9D0E5DD017F854E4AE92EE6F4F02328D4A368FCD66D2B8AF966E6ECF8E02939DBBD1D017D3E47375EC2766843333C4AC5F3EB98F0E85C0C3007982458295890FEDAE23F888148B68B9A1F31AA0847E04706C2FADD36065BF1178A661688B54F19D61F9E9F3B92DCF840C702483DEF61D44DB8747EC513B0BF736D03E63178689498FFCABAD1F58D268E764C7E5C36931C2D81EC69AE23160DAF6980E3B3ACF8F3E5BBFBADC5BEF9D99999DF1597B07EAED276CB607EBF43C761C6302F55CD6EB331FF5745BEB1D43
	036323E746F66EBCB2E66FF7D81D6D23E7CEB96D9F0C5B6C63ED8ED737F5446E94044A6BC99C4335D9228D08836C0608583335246D06605C8BE85E75AC16C773F7D1DF7DCF87897157FCCDB716646B25984A0265D8048F5F48B085937D4B3764D139DEA87B81C93E1C5842FC493B0179F20F057036C379B8108BF11F92027BC2B990A87C8666636E37180F3D7F7C3910F26AE7E905BABE465CA07916FA036CE3CD469D69AB447193F6120C01161F02DD20B1A0FF20D0209820D562381DB831990EFBCE39760E8E8F
	0F0E6C999BEBF35A463BC6EDF6A71C6F5C89B5947B485EBFBAEE9384E9EC0E5756F07CE11478D37538A4A445F6CCE0E3A978231F8AAE570F4571619EDB6893FEE9420D128E4B0DF2EC91B712F6EBEBA76D568467C4EE8F10041F9671749099177AEA11B896C8598F6A665F445A393700795219FE35D0D4FFF35A920EAF736E8DCA8B8A15FC088815FD115A785BD652460900E75D92E92EGEA816A851A3C0535711DA79A0D5A78GDC8B63A3D66711B327462C632E36B38EA78CF45A5665DBBC35B50FA4394DB316
	135AA528D159C7F1B75F44ED1738204B527CEF115AA3789639DDA45B7D3A3623EE510E0D826C43E6711A083FC56A4345083FA762C98ADC6A5BC8715AA2E561D6389F83C97CAD2584B75D6ABEC62CA25E73F377B1722D92B6C66221AE7136C833543FF57D649E19F2C72459929EE92E81EA873281F981790733BE4EBA7976B42F23EEC57FCA32771CDC8EC77B24EB5CFAE0E442E59D69379F350EBB2CF03F1B90A84F83A7C93B1ED249BDA2DE5F7DE0E33FFD44BEEEF5B9C6C7C03BF5588E03E9C26209BD3914700C
	5B455A9E47309D8C8C0F89CCEF7C49E2B8BA2D932E0F47ECD617DD66106A93CE12B1F156FA94B40282B8DF98CE5A6B92711E9B8A6751612C2DBE33912C6A4EC8F36B1D2CDD7A7B164171908CEE3BF3674EF6C7BFF143BAFE1E0D510EF6AF529F401F82B47FAE103DBB523C9048A7825A7B9E508F643A9766257B609E895437G6629DFC41A0300BAFF99E9CE82720F046B4E94E90E83723A9F6E458FC41ACDC04E874157A838678175BE94E9CE858AF8987A7011C8F38210EFF424396ED1F6BF78E3AF9B776D79B7
	88B5CF7C0CBB6E457B6E235298CEFC54FD5CFFA146E3F9AC525C845483648272878A850A81CA8632BC8E75C0BDC0AEA0FF20D0209820E4A0CB8C548375G39007C01C201E2001201ACCFC0BDD08F108B489FA894A886A8798936FE3832CDC8137CA5ED4462AF206D6D69BEC05A569DCBF113A16D68C9876993F2E4FDD8C97B0355333EA67DB013FB54F4090C03594811B1C64605A79EB2364838A263CF092753F6E36317A7CB4629324C46F6BB279D62CCFC9D649A28FB54330FE44C1323B496566DE5E3012CB51B
	0136D33E45866D311A03E53815F7986858C00F0522AF02308568EDC5BD7111DC033B7F8D6300D075AC65485A7EBD831E2DC075C0EB5D70D574188B142FA33B512DCC6E45A5C06FF0F85F5251C16E3DFB8CFCECD470AE959E3413394E72AB98C3C05D3FE2776AAB7BAA84F18EEB33CE58592CA52E386DB6794EAD71DEE1A5FDE9BFEB9FF1355B476CA3B6B81920F34655D65217F4BE9B989558DC93A462E4FD3397A61C8944FFEADDAA6120C3B9BB327ADB45FA2B4BB56EB8F546E5BFE2F7399CA303327CDD22AF93
	2A2553840D75218171B9GE274827243D8771FA2ED4D66254E515153E746FA480CCB67378B62BABE907843FF456E276F14106733F8AC6F934BF27DC3165E990CAD22E7A6E3D132715C0CEC98DD5BFC9B6F16FA89523CC764B384797AE4E319DD47EC3C165231C55A165CAF4835C06EB39B6898A7E317DC03643AA7578B39766B6938E477A2F28FA1631E5C835EC3DF86C5DF32FE8DE3952897488BA888A89AA88528A2961EE70146006681C5GA5G95BD89EB8220BEA09F209020D8A061B71166AA20EE20C920G
	A093D092D059EFE1CE829A8272838A830A7FAD6BDF7F92F28F7D5FEE17FF477D71BFB453EB24F4B9998B7F1B6340681E7F9F7A7713B4176C6C571F8B77747367427D7D7C39302B5F73DC77F1BFEF2E136A0E771331B8FAEEC4B1F7DA7B4973D77F684808DD1C40D1C410FE177BE38F0943B162F09DF358866D2EA30E8BF6C1280BA3F3E000780C17856765F16C99695C86760CD4F961A7C244C9DF6E6FB7732E3DBE4D95A74DEFBEF172B3126453A99B19136DCE7BB0B4545E51B3A3AE899F921F9FA9BE31D844BA
	0FFE1748D69D6C999D9CF45AF76C29310AF154CA07EE7B78406878305DD67539752C3556FA4ED5AB76C36D9E31FA9FB92F2E583A755D8A7D1F91EBDC226A13FB5CB4F66A2BFC277AE8F51DA0DD7F45C2D9215A4736B963E3BD7AD87D797714BEB6B44D1D06ECE0BE9EAEF173F16EB4E4A373F1D1165A474639530ECD4C474493EEBE4E1DF6ECE6BEB61429FDEC18BB6D380D79D8D16166635CE9476D4C478F1CEA9F6B67CEBB36B09F2FB96B66635CE9479D4C4783B51729FC2C1BBB6DD8DFC7FD4CBD6B66635CE9
	477AFA6A6341AA150F75F3E8422EEF20BEBEE9F373F18E35E3A37551F70D5A4716B9540ECD54479B2A5DFC1CC36D58CCFD5C69D47B38FD8E3563B66A63D54B5DFC1CC36D381D2DFBBED57B38ED8E35E38B75712FBDEEBE4E21F65CC1FDEC1BD07B58BCF75A31014DB31F6CF373F16E34E3831BE72A8754BE4E256799B64F7C2D584D47B9540EEC1E71AFD17BB8071EE79A58BCB33C4E4D47B9540EEC1E39572E76F18EBD4FB430F96649D2B79F67D0BB32F966105BFCBD071EE79A58BC7368D1B79F67D0BB32F966
	44205A47B974BC53486619EE071B0FF3279D9B6B697BC3E1087CD6999497E98E870A09E36F8F975AC8BDFBFFC8DF126E9DF5BA251FD9212E65A9B1772F5D2278DDB4B1CE4A63AC407A178C6AD76C73DC3F4F207E8B037A9F98545FEFD07F1AC17D3A77BD57F799541FB7283F56207ED1037A3F9B54D76F77DC7F31C17D578C6AEFB028FF5220BE2343F3FD3DC17DE786751798545FE2D07FFB037A7C831E6BF798545B8D6AAFB5283F53207E85037A1203FA7552FD62D1FB919CA76CDF1C310F745BC90241A17BF9
	161BE7DF66D6F7546ABCA3677C455B497BC427A2B7C1429F9F603FBFF9F20064A31CC67DCF8DE48AA4F7CF52A93D6F8931921DE3630EE16B78F912ECC17345AF5D224C97770258C8EEF9A09C4300A4D94B17AB6168A99F5B77F7C4574FCBD9DEF9B448A6GE57DCE525771BB29EDA43EFF16B13ED8A8A77DCE0F6F6D950C2F6869C8F35553FAFC4FA0DF8770746972DD361271F9814FBCDD3EC32B98DF9870C46B723D09FCC904A7DE0F6F66F246D785BC2D3AFC032B99DF9F70CC6A722DDA43786681CF08AE5FA3
	48978DBC893AFC7F351671E57DBE52DC71FBBD3E6D950C2F95F8FAF57996A15FA470786972DD350E7105G0FC9176F48FA4617GBC429F747816ECE0FC954053224BF7877275824F18AE5FE8A56373831EA0DD3E8D9B991F89F862F579FE0DFC42B3116632E774783E39097135GCF37AE5FFBEFB13EB160715165FB95790200A7C2176F47D50CAF9EF8D2F479BE2DE6FCE58BA34DF58B75788AF11FC9B7708C69725D07FCBE4093204BF7EE8B630BG1ED8DD3EADB50CAF85F80A1E5563FB9A796A0027C3176FBB
	DB995F9070F8F178821E956FFF389F65ED449E96315FAE9104CB52D91EFDD711F2DF0DEDA2107BAE1D5042E7A57DA94F3A6FBFE8F6903DE54F41FD71B936B6BDB6284CED5BB3B1B18AB7ECF1D5AA55E78EA95F33CBD86ED08E9C879C63932E83A3B67BA41B03168E91DFC9FEB80569BCF27D961207621E6F3ECF5C4943D287771E935753BD23FBC7ED768E184676E29A42D1077D1C405A614B7715FBF2428BAFCF4B55E907DD4FC9719FFFCEEE072474BB52A51FDE2DA6F3183847082C6EA7EBA74EB9C686F76EBC
	A84ECE527E1C3F09F9FDC47F7C6758BE1CCD627E1098A8A78225BF8F77A0F1CFC03C38D70068AE872C87C81A73237B14F33E6F73D20EE0989C25671A7DE58DF01C79238CEB5BCB441C1FD9BD38E01BDEDD5CCA7D0B7A6955422E2FAEF077907994FA27C5BDAF35E57273F21B3617DF175E8BA435C3E099EB075684180F92D8BBBCB44CEB07108456FE3DAB185C8240D2D14E3C0CA7577482134BDB4964BAG73FA0149DDE46136BB4AC520DC94E069AFB2392FB2F9F23BDEE4F22ED5CC2E9BB09F14BB4B359701F2
	D565CCAE86303CC5CCEEA157DE7BA2A69707F23D0079215C3733F9F2A61473DB4D6462G33A4B239A92E3D4EC4A6573E06497581B68F657E4935970DF2A9A83790307217185C1F3876FADEE2F241EB195C90E081A877DD2E3DD8146B2BE0F20900D57E11490DF06D9D7FA313332CE3F2E30085211C8F57DEBC4A19D0AE99301ABFB13941BC1E1C6DCFCCEEEABD131B84AC8465F28AF9F289A8D73701492582561444648A38761CC9CCEEA14AF9819606F219DCFBC9A8B72F1249253F8C637DE54F7139DEE6F21D9B
	191C8FE091AFFB0EAF856504CDCCAE6F95986F2FB039435CFE18FA054905221C9F41D04E0BABA73C4A64065EE2F29640BADFE5F27DB9BCB9EF14AB2FE2F273G0BC6394B386D12154C6422D12E9C301EE4A677A5571EAF4AF9D7B339G40E2D16EECBECF2E68B5A657321949D582F67CB5A6574275539F6592D1AE8830F8147B9A57DE591F19DC4096A6D783186D4FCC2EE9BDCFEEBE4A7554B039904092D02E3E00A7D77197A617351549B581667C8BDE6FA5BC39C0148BC739B04012D06E816E3CD21542F2970F
	B5A857DB917173F457DB3BD2243930BB45FD3D3562B4D9BFB88177CAE16BAD5FA112334956DB9F418A47BA82D3370079218E32AE0067F2DAFE57C16C0A4F65825B13F154C9FC77871DA1C0A620F8A061F5E663978E43BD247BA6BD6CA195EA58FE4C0A5725181ADE17779FF44079F120B185A66D17FC3B0F3435776B52FE49FE449DA25E73D799177A26C6DCCB11F2655F6099778D16BB7FDC297BFE5063E613EA7F4324DBD912CB56482DF1AB4FF7131356D0BFBAC1F22CB9EBC169376636B3AE972C0725B56192
	EB96158B85DDD2FF848F1335FEA2143352CC668A20DDC0FDC0D3C081C0D23FD8BE672D03557D223788167739C831DED7B63BB68A53483937D1C41ADACE0ABBFD04459D8B75122E93ABE66703D3A3676EC351E21E8FAD4B188FD50B198F4BD64E4E078A0D1C3B8FFDDC9FEE33B09FA65107DF4E520798C39FC238BE04AFE5BE1850078B2BE667030FC64E5D87A11D67438D588EE5694C075A72597930CBA3676EC3B75707D5CB188FC368439FE669C312218F81DC9F6E4FE6BE04218F5FDBBDBB9F82B5F26EBEA4F1
	FD38B91379101541FCB838E6F6BE74EA645CFDE84D6079F09336C3AF7A7046ACFD48DBEB64039F5707CDB89E0250071BB43A2667C304218F71DC9F22309DD25007010A5979605248397BD017497361DA9C8F9D194C07156BE667C315C64E5D87AF2E8FF7E3BB4CC39F9E1E258FF106BEC4F3FD38B20F791000BEDC34FEF6BE78E9645CFD28D84273E1CFA17321E58973E15B065979502E11F377E10C6B43BEEC879F746145D97A10E268C38457075D588E31684315153373A1D8A327D9BFE4F99E8FD5D94C070FB6
	4E4E07BE0D1CE67D4075C19E8F1368C366265979E05148E9568FDC9FAE46EB5304BE5CBECB9FCC06BE8859BC9FFE0EBE14E5B39FC65E1A1D8FD39AB94D7A016B4377F3F07D00BE2C2F1A1D8FF59AB94D7A016B4343B8A6435007DF4D52070506BEA4F1FD8845F6484AE1BE7CF7756CFC182711532C9FF2386B49FCDCBF208F6FEE1E1D8F1D9AB94D7A016B43A876C5907A704AACFD90B45FB4522C9F38BE04E1BB24208FBF52681A1E8F2106BE546572FCB83D9E578F394C07FF544C4E07A10D1CE67D4075E128G
	578F68C341565979D02E11532C9F38BEDCDB026B8774615ED97A90ED68C3C59E4F87F3B12E9F72586F8D0962337898143D7258EF8BDFFCCE7612335F153AC79DA3AE017D263466B4496D989DF78CBAC69836C1447AC747458D4092F642293FB7596E646FCDBE6D646FCD7E426979BB9D671DFAFB1725EFEA145610987DA03EE0A00A89840B00F28CE2734C84CB01F2D1BE43EECBA3D89314BB917B76850275C1F9B21F75E5F35F82B87AC2B990A89CA81A70832582D98ACC669A20F620DEA0A710B750FC20D020A8
	20F820E4203CC2133992E897D08F5090509410BFD0B0508220B820A42074A213399C288928934886B489648B9488948E94831488A4AC85BF00EA005A010E833900FC0066830582C5ADE5710C0A71109C9A129F33907074E5A6719DB5F97F9CCB5FEBD782568EB4C45F1B13F77664FD3C8DB0EFA09F7A3E3F0C6691848236GA80866A934507C07050025971B449C8C12DF51CB73B6AA81EB87A239A3A4AFE412661B58G73864A2279AC73E81ECCA0E08B002AE89E4E42E296CFC3ED51E275B562CE248DF8B869CF
	525E6EB869D3F6CE7AC0D997C57BD23C06E97BFB32CD7A59D741C346C2924766F4090CBBE539177A922C23130C2F1A82EDDD1342A7B2AEE3B8F1D8E83B0552F1B5D3DFAB5DFCF2AA7C18CF4FD37379FA4935C046F0201BDF7EEEBAFD29FCA747FFB27E49355A4E09FF3AE4D368A557AD397E85FABDF9AB74065273A105FF64FACC277247E751FE33A5F2EF102E0D2F1FB03AB6CE6A60E4AE594E418B95676E5F56A23F0F336FF26D837A4040CF72ED127D0AB279FE0951F73FBCD12EDB39049E9BF5785F9624EF2A
	38634AEF6210EF29F4F1F80E5023D5A0DFA01D392F6E5F5EAAD51C336FFC69FD630B64F2FC8AF4408D2FF7AB33EFB5713E6B7219A065F5947F8B6D3DD7F1CE3EB5C3465C0702742EE605220EFD6FED2B827BD8103FC976493F6043CC699075E731F07FF2B21647E0B9E21F90DF2834DBDDD6DA773C0B0C9D4DB7CE8EDB076D825B5F3D3AAC1B726CF30D6E2D9770F7C9AB6FBB256996697AAF33E86F83741BE4437A6B2ABB07796B2A7B0649981998BDB35EEFF7DFBBBDB0DC06F5729B2283AE7BB08903667D0A72
	AE6B7820DD33767A0DA8AF556949BFB32C37BE135A68772AB6EA40B64A9E6135D18DED1BD6CE9BC97A3E3BC2292F91756DDE465357C3758C696ABB30D2292F89755DE561691B22FA7CF4757DDD252F99755D331427AF106A8953557733D5CAFD5BD05FCDDC7F22281EF8DDFD76F2253E6D282FE289CFDFB25513D52227EF45EA253E965477F0B6CFDFB955D3272B6FA1153E9D28AFAC13272F1D6A6955557735B5B98AFD75F5CC5F4F397EB9299EAFDDFD4DEBD57A6A193EAD5C767327FA02F4753D2056574074BD
	4A75AF1C6A09565577038A153EC6FC8F4CED3F05D4CF0A2E3E8F572974B5B1FD8B387E65AD1774D4AC57531731DE252F19693BA60F272F096A695055F71BDA5FB6266F7DC21E3E63D44F182E3E618DAAFD5B193E03DC7F3C291EF93A7A56D52A7435B0FDEDDC7F02291E88DDFDCF28756D705CFEB1D4CF022E3ECBB6AA75B554F9EE3FD42A2728D4CF5F6ECDAAFDF8FD7CB7F73CD4D2BDAD3A7ADED66B436BA31C2B2F136A695355F76DDBAAFDF8FDDC1543536722FAFCF475FDD22552075747635C7E18CF750468
	6A4B2FD669436B63B62EFF8B281ED8DDFD7728756175F1D3BECFDFA255A3146969BB33D9258F2F8F97B7DE8B55D3252B2FFA0BCA9FDE9FF7F27D5BC575F46B6AFBCA2D8F2F0FB14DFBB9C2B62AE7D2D75F25B5CAFD0DF8FD8C97707479D2BD813A7A76EDD569436B633A921E3ED02A47242B6FF535BE3CBEB20BF97A62281EA40EBEE9BD777288493D1A4472EBE279BC5A2B2DA16B4531F17B00FDFC5CEE13BE6DA76CDAC17E0649FE713738F4F28EC47985727B2153FAFE740C8B6EC7A25FB502EADF98E0C1C061
	ABA41F24DC2B1F1DA34FF2836356E1D8CE126DE1C248CAA2FB23194916C3398E289BE88C48EFA53399AD667A8FDBA79D43E706C58FA57F92445F7BD200AFFD95637D49851157B16246FB0F78BBE09D706DC25E4736113589FEB5905B646AEEA55E06787AE6529787BB2D276CCE36073A21AD8B317ABA89B3B9F2E4797A3ABDCC7E50188AEFE37839FEA5DE0F7C9167D5B8725FB92830D9AFE13B7A94D8833D17D5AA659BD0EF65BA950EFA235214F8A372BF30C305A37F9B7D8A1B0D92366DCB8556C43113CA79A6
	54FB597BAA9C759EEDD1624D48DFDD234211BFB1C3E133D9425EE9D6E05BA4EC6BA6257CB654DBD72B42D16F77D57D359D795F302BF0648F5C2B3039DD42A6858556A2E1252DCA799654FB62308AC73D3E2AFE5C017C473AD4B872772CD1585CA1E12FA95A28010E537FA9D348B74863F4FF038AC73D9714FEB44863741A83AA9C793D4B95B669B85DF2D20151F1FA314A1EBCCEDFDB2142D16FBF1A1538BCCE97552BF0643FE22542A69D272D6FA8B0BACEDFBE22141747E97067AA9C75BADCA40F15EE3EA13FBA
	0CCBB9464B1C4A9C63A1385F08395EF0146A87B2F87581D81F1F4B2B8F457AADC53CFA9356BF1D4D2B0F437A7AFCDEFD92565F41351FDE4E6A1B8BF975E5651E65EB30FEB7D73ED5561F492B6F417AEFF0758FE17D47DC79A92C7F8537FD7CE479253C7AC02C3F4C422B8F437A2FD472D165D2FDF2B9FBA735C14CA54EDAEDB2172DE6180F153D4F2AC42CC2FC5F670232B7E2970FB0BEBF44FA447C58F8A8A7A1F64BB246170A5893E26EE6679A1379789A066DACE1FCC308450AF926E1D0B6A1369B7545A0F63F
	282FF22D495C3416E135282F95316DE2FE1E9F1483901B37047185A3762C189B128A653C8A069D4CE1FCE508BDA266398DC1F99231A654670D5843223E98D2C62C9175A5A156A64A362E033547BA06DD037A0EA316A06A8B06F2B8E2DD284F0458BD22FFE56BCD662A758C5B047A1A907B2D284F9B4A7E0835223EG44EE9773931220ACECE0582AFC46170758E7E20E51F1A8BB913B371071CDA2E6957519209C0B58AF73985FC244BE91E51BAACD6676CA065D057A3AAB557E85C0B924D26BDFF8255A3F3C0DA6
	F379C62DFFD5087DD0EC17C9A87BA016096D620F5863A25FC2A8A7EF547AA7ECE2582D22ECB7147B90AB4DE5FCCE44DE94638807F294E29FE09C3108C50B7A2A5E026B7DAD06B5233EF6446E97E57D219C08D8ED81638BC16C1B22DD218A5629D58CCB447EA8C7ACC65467047294E2BBD01F8FE25F9073B8E2211CA06BDB4F7812917396E55B2BCD661EEA063D007A7A903BC73C26C3209C01587A650CAF8A3155E24EC279E61339E6B343AEAAE6FCAD0079G411D6A8C89D71AC37344F7CCA376F39538E7355DB1
	EC9FA1FFC82A2A3139312622252E1A6ED78DB8275C1B5312F3256E5E1C284D74F7D3FA2C49A8234FA175FDE44F866E79A086F76E5CBF6E3035D987BB45279A1A5FAD2E5B086E32ADA63A8778AF82196F3B21BC846485A479B539096DCD8E7E5155C22757CD5C3F97985FF3F95A3CAD12BF6174C8768C35E65C11AE59FC424CEC26C0FDDD8DF9EED91C261CCBC81B7D902896C82AEFD655135F6E2BD575032AFA626FFDE94A7A4BD575998AF9A93687B73258D29F8C911C3784687EAD44499AA92679749871F82990
	7C58F569D21F0B0F20344F3FF1E1BAFDAE49EDB8AF4BE557CEC70E6E739E505967AD2E782476BE604C22F146004F9276038C0665EDE5D8CE81435A91BBD54430A9441667B0AC943121FC06A5A276CAAE43AAEB9966ACE45871DAAD1FBFE2AE640B12E5B399167EB6436EC259DD0879A21F8B3154EC06854BFCCB9936903107AD8CAB2FB351EF3D949ED941EFE33AF30A65B25DAA6AA34F4EBDF56C59FB2715FC0746E51FF4C97DFC6482696760BAB2A62F3236F34617F20CAE21E30210AF4890AA2163B2G4E1FFE
	734DB7F3F15CEC9FD15BBA7BCF59567C91A3DB599EECA5EBEC19CB5436AE1F12EDC5AD73E4EB03C1DCC23D3B2DC40B5AD67817322D8FCB0CECF90AABCFE36BCFCB5536127FC7365563B12EF586F115E9EC453B45C55E24B3DB7BBC46354EA02ECA0D2D37F354363A4D322D9B17F8323556A02EBA0D2D54EC352D09B45916B54748162738F6E9ECBD172936753D45322DC39E63DAED90D707465633EEF15D16AE5BBA68B12E5586F175E8EC3D1C2B369517A15B3A5DE3DC2B8C626A5358FA53AD2EB7B3E5DBB60FF1
	2DB2084B2931956B56DF37E64936F6FB0CEB05C1DC139ADB2F384575E436EC6B184738D69844652D317563C2352D549C59D6C33EA7DB2586F179E9EC395CEC091F1820361EACB43265A92E799ADB13F9EADBED79322D2772BC59AAB108ABC8E3EB4CADAEFB01EC6B890FF1159844952A31F5151BAD1F426976D731C1DC91067DF5FD51F47B2B58A02EA80D2DB037B17F70D25956EA0FE3FE29C1DC319ADBAF3A59DADDBC5D7EDAEA9057C20D2DAB725536DAAD322D5ADCCF368A8D62CA5258B23B596AAF11ED8DF8
	9C9B0586F125EAECBD6FB6AFFFFB39EC2B53E3DC7986F1E5B5E8569BEEF15DD2AA5B5AD7E0E44BD3DC960D2D6F9629ED4516493696FBEC43DC0338AAB436963945753795322DEE0FF1659A44D52331F565FA373131CA915786CF36328D62EA51583A22C0ED6B6B6532AD41637515ED90D73B46D6F4065A56CFD64B36F6AC77E4EB09C1DC5D9ADBCD45EADBD1EBE4DB17979B59729457F105AD69397E11CF153F2594FD5FA74DAC701F6B079A2467F9D7037C1B05349F652E35E4119284780286369FE563D91283FC
	547A3976EBEFGF60F0F0E5947DD67A5D47C12A94BAF93F86F37BD11740D1153E6B249AF847B4CFF89F709F81282173EBD72F771B79C711E83F5FD0DECEF4D1FAA480538G4A71C0A9C0C5CDA6F39DD0E7930BED64AC79FBEE9E3F6351B3BA56E9BFEBF7AA363EC8717D46BC537824FE3AE7897B7DA574766B04302F7769FE772E2F09663A51E36FE325A2AE45F751FA92DF8854C581A581E5B51B4C65C0CD4DAC3EDB4F56C871116E5963F2D97B07442F7F3160488779E4183C4417E2BB6C64455651CC736469F1
	8C0E5E74DC123BBA03A7B70F728453E3849CE3D4F2055CDC0D844A13CA0FC55BCC664AEDCA3941A21EDC8B65692447BEB83AD4F22B3839BABE14E7BEBD0640F101CAEE181B7392CBF99269D158EEB2DB36AB657E124B132B22BC3B68319B0EB6155C975C9C2FC94A63CB0F81F08C1D06BD9365092347A4B82637A8654EF36D15D11E9AFAEC05E30FCA6E8BEE6E6290651922C7BFB8862A646E637A99C6F92268B19E0E49AA39CB397632F6C8BC6574D88747769DCAB9B3375FFBA90F139E3D60682F123B141B9B9C
	C4F94269B19A0E8BD5F20FF3F300D3A8CF5ECEFA7F05E353CE255C9DDCFB9D1467B89C47F62A7303486F89E4D1AABEE4527F24B9A720121CCB771E5F6731FBCF797557886D3F6ECBA33F9B716EBD5ED4FF8C739166082A1EB7B2A41D87B7497A075F124F7D2A64731B2B6573DFED164FDF5FA21F17EF154F5F2B154FCF3DAD1FDFDCA71F87554B67510D72792BCD727972ED72790E6D72797196797C2B9D727955BB6573A75F114F176C164FFD5E154F2FFFCFBEFF38D5BEFFF90FFCEE59AB1FEFEF174F1F5EA71F
	276D174FB7F648679D87647353874939F43FAFA35BA2E84ED751DA326705BBA743DD7D20752C7503D31F5B7BDDBD56C11439EFAD69C75C87A0CD55EC9F42DA7210C17E9E2C436AF4DC90BF9BFB8498C7866975AD7E06A61DDFD01CAFB64F646F124F14681E5FFF2B8DA97E148A7DFD9DEFD67077F5E42C636F174DDD6779EF79962F537FDB3EAB57A97F162F546F9BBBC95F643DE3B2D781F5827581395EE1F3FC503A7502B847F36677FD36C1322FE442E5A51B08A57EEF2E570F755B6B174B319229FE98A7638E
	7295FA7CBE7D95E2BE7729D117EBF41868E7788F57137EF20D0E0920047DE8BDE923F1476010CBE225F830786E52E99F90E1693D225D49F2A87C5EE16F9AEBB0B7E2BEE2572793AC914AC53B98A6086FF8AA215C0E58ED996C9DE7AFE2EBB219BE9BE2B70B6FC1C2219C0BD81678FEB4914AF93B71FDFF81FB87DB0158B7705DEA8DE2E3627B1CA9A887A0768B918B05F28CE27FD3445E91A7A076CC913E53C56CCB713DCF7B3BA673503B8CFBCC442620BC9F3186FC079D0258F7709DFBB8E2E7457750F96F41DA
	78BD06BDA8E26DD036A1661D4F5E31CFA276823EBB77C6ACC84A4900F28AE2FF97313CD6E817D6069564319C00D644C6711DF8A7E29744B61D8F6588449E913198A8A7A366D348F2943276B06CA54C9530A096AC36158D4ABE0819C53B73219C3EC76D9F4921085E2375AF8E310BC4DFEA5ACC666EB6067DDA44ECD076C64C2F1065F84CC36CB574AF30CD6DDFB2144B762A7D2B01F2A7E2BE18035207588BB81E1D08C509FD9E8E65F84456C9B620ECE9E758EFF2D90ECC95E26FE13CCD08B509B63C219C0458A5
	A2968E65B84402B007A78931EC4C11C9C56C4DAC02F56E03B54CBE4CF71272E6209C08D819988749B18AC36CCB9C4F8B902B966DDA5EB719DB5E47EBCB44BA216CC4AC88F3203C904B469CA8DF4462447C06B8A88B7B99D6AF5EA3ACD0EEC26CC591A3B9DA9D08FD0039D2BD083DAF5A8D04328931ABC4AC8E4A2908BDA6E2C59DF0FF6940BCAB742F86B13BE8F78A4AFE9D6CD9773F3A48A2A3904A91048FA8952868G74AD50AE20EEA09B509410BFD090D0B8D0B4D0BCD0B2D056C1B627BCDBC996EFFA4F7CB4
	39C21CD8462D4A6FD3EC6D541FDB1ABA0D67165D1DA4B6BB4C89BD6747687E4536CE711B97838393F61C4376F796A1F6E022FBF442C1D6AC521A69A45952CCFFE359EB78AED8FA6F4D7F0D259C5A23141ECB3F675C1BCEE2989A9DF7DC989DF1D91DBD908E334EF26C1FCAE7B99FD587593B71520CE2155C9E27E3F0044C54D426BE0349749EE4B9B597F54BF1B4E79A7DD664A98E1F03D2DE0B9C47839663B81E35B01F7CB10E35A51E6358D142E496E09C57FEA847F125475FA8338D62C85644710DC263B82EAE
	E4BE8907D89CF1051E6378B34A549DE2F118BE126378EB1151EF2D1E6268BB649E07FF2EF19CB766B21F1C98478B391E63D81C47E466E39C0B0E48F1DC6771BDC93EC19CD11AB87ECF1EF19CC168D3AC467107BC4FF12422CCD6A70B2360E3B98E416338AAB408232553BD0E3B330D6378CDB673293D1345E149719CC7ED8E13F1E19C75C765B83E6E71BD63D203B882B5F1DCDAE09CC7F08173A98463F82E40F39C82DE9F71984749E3F29C699E2F0FE203B80A3A5C6338EF9A57476F707AA86FE2F114990C2B86
	9CD71DDDF85F6D1DEEFF149844B125096372E974C7A876078F4671C7037E48427E8843B82E7DC40EA34FE3FF149A441170AF76C772AC7A2372B05EF77F21384E8D73A6BC4551F358BD0EE7F20C63F8832F593E43AC0E96037B6EC7983B9F463168D3B90E239E4755AA03B8A2B4F19C4AB40EE3A013799405F184E4FA0E63F614C945B88ABE136328B04CBB7294C74D876EF1AC1F46FD379E6F3BAD9F30B8CEE7FB0E63E214997AG6F3BA764B87C8DF335BC45B1CF93C76BB47A63D3EC5BC00C633B867DF1A34A44
	E09CBDA7957DE1183767A90E74EE77B8DEADB60E23G5709C55DAC0E8E0B67B8CE21CCEBB70BE36A149C4700E12E2327B846B4F1FC3654B80E49254C27A90C6326251E6308C219E014F941AD2F722AFEB92E439E7B07BD0F3CC97FF30FAB8EEC5C8178A2A0495614052C7D4FBAA69C646FC4CABEFD875BB6211BBD4FDD56257FDCF6E51771F3590F3B783F795D50257D4D6F26AE7EEFFEE1DD4A5F7C68B7F33AC83C0370BC590BFC77A27689E21122CE029DF358DCC308BFA47AC5708EBBB1487047C53F4E09D8AF
	45E29558A794FB2A0BBCDBC2485045CC768FDD247F869CCE27D4FECE64F10C4023A63647A251C74718D561F792E244EF296F6F3589B8861353497527C82B2EBDC27EE5BF4FCE546E5DF738E75FC7485F11B1DDFE495633F659D62AE5E6363ACBE6E66BBB03322D5B8AE7E6AB3FE8E636EE9D12ED7DB2F7E636D6664D4C56138E59565D531615780B73E7E6EB49E959564B59B33335AFE7E636B6BBE5DB77954C4CD669347BD73255B5AC5BFAED067DF5E0067DB5B6A25BFAF006F12D1DE1DC5F997D7F97572DE332
	2D12995AF2CC1B1F0E79AFE4DBDFE64E4C5623536627394263322D0E990E79795366273942AE5956788C637A658C63EAD36CFF5BB843FB6F79E97353DC6133322D7F3BF4E6361E19B6BFFD6FFDCE36F51D054CEF83D6678419E8CAC5AC39F08A406017040C09F1E119F68F571EEFBD796B60136D16B4A16D0010B9A44E3D87040CF3E4921FA4E33C9BD6AA5609AF84F16F892F841D78EF7D9F7CAEC43F8F0813FC7C59461D0B1F39E6F11A108ECECB0B1E83C2BAD939006F63D28C0BE151A3C6C0F6FC10C588D9F0
	25093E13FC0BE5DCB4CD412F6655220CD72BD70B7E0777BF3CB364F589669DAEA12D3E0E7CD3CF7EE9A07FB412FF1A48BF4D641FED641F6D641F96728F11E8A0928DC42201C8B4900906A6F2A5B1BBE64558AFD121E9F396E5B5F3934DD5212A7F243C2E6A2F14393FA997FF253B0F3A6E18144F55FE4CBD77771B62FBCFAF40679FE34F0D3FDDC6923474720C0E8CDB1DCEF8AABE3DE744C6F20E5A4E3B6CF21E0BDFB151BF41F8E8A5FD9EADA6FEFF2E1079FFD0CB878896198EFF72A9GG2C2FGGD0CB8182
	94G94G88G88G33F2D3B196198EFF72A9GG2C2FGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGACAAGGGG
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
		setSize(353, 80);
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
		constraintsJToggle9B.insets = new java.awt.Insets(2, 10, 18, 1);
		add(getJToggle9B(), constraintsJToggle9B);

		java.awt.GridBagConstraints constraintsJLabel9 = new java.awt.GridBagConstraints();
		constraintsJLabel9.gridx = 2; constraintsJLabel9.gridy = 2;
		constraintsJLabel9.ipadx = 11;
		constraintsJLabel9.insets = new java.awt.Insets(21, 10, 1, 1);
		add(getJLabel9(), constraintsJLabel9);

		java.awt.GridBagConstraints constraintsJLabel10 = new java.awt.GridBagConstraints();
		constraintsJLabel10.gridx = 3; constraintsJLabel10.gridy = 2;
		constraintsJLabel10.ipadx = 4;
		constraintsJLabel10.insets = new java.awt.Insets(21, 2, 1, 1);
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
		constraintsJToggle10B.insets = new java.awt.Insets(2, 2, 18, 1);
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
		constraintsJToggle11B.insets = new java.awt.Insets(2, 2, 18, 1);
		add(getJToggle11B(), constraintsJToggle11B);

		java.awt.GridBagConstraints constraintsJLabel11 = new java.awt.GridBagConstraints();
		constraintsJLabel11.gridx = 4; constraintsJLabel11.gridy = 2;
		constraintsJLabel11.ipadx = 4;
		constraintsJLabel11.insets = new java.awt.Insets(21, 2, 1, 1);
		add(getJLabel11(), constraintsJLabel11);

		java.awt.GridBagConstraints constraintsJLabel12 = new java.awt.GridBagConstraints();
		constraintsJLabel12.gridx = 5; constraintsJLabel12.gridy = 2;
		constraintsJLabel12.ipadx = 4;
		constraintsJLabel12.insets = new java.awt.Insets(21, 2, 1, 1);
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
		constraintsJToggle12B.insets = new java.awt.Insets(2, 2, 18, 1);
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
		constraintsJToggle13B.insets = new java.awt.Insets(2, 2, 18, 1);
		add(getJToggle13B(), constraintsJToggle13B);

		java.awt.GridBagConstraints constraintsJLabel13 = new java.awt.GridBagConstraints();
		constraintsJLabel13.gridx = 6; constraintsJLabel13.gridy = 2;
		constraintsJLabel13.ipadx = 4;
		constraintsJLabel13.insets = new java.awt.Insets(21, 2, 1, 1);
		add(getJLabel13(), constraintsJLabel13);

		java.awt.GridBagConstraints constraintsJLabel14 = new java.awt.GridBagConstraints();
		constraintsJLabel14.gridx = 7; constraintsJLabel14.gridy = 2;
		constraintsJLabel14.ipadx = 4;
		constraintsJLabel14.insets = new java.awt.Insets(21, 2, 1, 1);
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
		constraintsJToggle14B.insets = new java.awt.Insets(2, 2, 18, 1);
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
		constraintsJToggle15B.insets = new java.awt.Insets(2, 2, 18, 1);
		add(getJToggle15B(), constraintsJToggle15B);

		java.awt.GridBagConstraints constraintsJLabel15 = new java.awt.GridBagConstraints();
		constraintsJLabel15.gridx = 8; constraintsJLabel15.gridy = 2;
		constraintsJLabel15.ipadx = 4;
		constraintsJLabel15.insets = new java.awt.Insets(21, 2, 1, 1);
		add(getJLabel15(), constraintsJLabel15);

		java.awt.GridBagConstraints constraintsJLabel16 = new java.awt.GridBagConstraints();
		constraintsJLabel16.gridx = 9; constraintsJLabel16.gridy = 2;
		constraintsJLabel16.ipadx = 4;
		constraintsJLabel16.insets = new java.awt.Insets(21, 2, 1, 1);
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
		constraintsJToggle16B.insets = new java.awt.Insets(2, 2, 18, 1);
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
		constraintsJToggle17B.insets = new java.awt.Insets(2, 2, 18, 1);
		add(getJToggle17B(), constraintsJToggle17B);

		java.awt.GridBagConstraints constraintsJLabel17 = new java.awt.GridBagConstraints();
		constraintsJLabel17.gridx = 10; constraintsJLabel17.gridy = 2;
		constraintsJLabel17.ipadx = 4;
		constraintsJLabel17.insets = new java.awt.Insets(21, 1, 1, 2);
		add(getJLabel17(), constraintsJLabel17);

		java.awt.GridBagConstraints constraintsJLabel18 = new java.awt.GridBagConstraints();
		constraintsJLabel18.gridx = 11; constraintsJLabel18.gridy = 2;
		constraintsJLabel18.ipadx = 4;
		constraintsJLabel18.insets = new java.awt.Insets(21, 1, 1, 2);
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
		constraintsJToggle18B.insets = new java.awt.Insets(2, 2, 18, 1);
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
		constraintsJToggle19B.insets = new java.awt.Insets(2, 2, 18, 1);
		add(getJToggle19B(), constraintsJToggle19B);

		java.awt.GridBagConstraints constraintsJLabel19 = new java.awt.GridBagConstraints();
		constraintsJLabel19.gridx = 12; constraintsJLabel19.gridy = 2;
		constraintsJLabel19.ipadx = 4;
		constraintsJLabel19.insets = new java.awt.Insets(21, 1, 1, 2);
		add(getJLabel19(), constraintsJLabel19);

		java.awt.GridBagConstraints constraintsJLabel20 = new java.awt.GridBagConstraints();
		constraintsJLabel20.gridx = 13; constraintsJLabel20.gridy = 2;
		constraintsJLabel20.ipadx = 4;
		constraintsJLabel20.insets = new java.awt.Insets(21, 1, 1, 2);
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
		constraintsJToggle20B.insets = new java.awt.Insets(2, 2, 18, 1);
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
		constraintsJToggle21B.insets = new java.awt.Insets(2, 2, 18, 1);
		add(getJToggle21B(), constraintsJToggle21B);

		java.awt.GridBagConstraints constraintsJLabel21 = new java.awt.GridBagConstraints();
		constraintsJLabel21.gridx = 14; constraintsJLabel21.gridy = 2;
		constraintsJLabel21.ipadx = 4;
		constraintsJLabel21.insets = new java.awt.Insets(21, 1, 1, 2);
		add(getJLabel21(), constraintsJLabel21);

		java.awt.GridBagConstraints constraintsJLabel22 = new java.awt.GridBagConstraints();
		constraintsJLabel22.gridx = 15; constraintsJLabel22.gridy = 2;
		constraintsJLabel22.ipadx = 4;
		constraintsJLabel22.insets = new java.awt.Insets(21, 1, 1, 2);
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
		constraintsJToggle22B.insets = new java.awt.Insets(2, 2, 18, 1);
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
		constraintsJToggle23B.insets = new java.awt.Insets(2, 2, 18, 1);
		add(getJToggle23B(), constraintsJToggle23B);

		java.awt.GridBagConstraints constraintsJLabel23 = new java.awt.GridBagConstraints();
		constraintsJLabel23.gridx = 16; constraintsJLabel23.gridy = 2;
		constraintsJLabel23.ipadx = 4;
		constraintsJLabel23.insets = new java.awt.Insets(21, 1, 1, 2);
		add(getJLabel23(), constraintsJLabel23);

		java.awt.GridBagConstraints constraintsJLabel24 = new java.awt.GridBagConstraints();
		constraintsJLabel24.gridx = 17; constraintsJLabel24.gridy = 2;
		constraintsJLabel24.ipadx = 4;
		constraintsJLabel24.insets = new java.awt.Insets(21, 1, 1, 11);
		add(getJLabel24(), constraintsJLabel24);

		java.awt.GridBagConstraints constraintsJToggle24A = new java.awt.GridBagConstraints();
		constraintsJToggle24A.gridx = 17; constraintsJToggle24A.gridy = 3;
		constraintsJToggle24A.ipadx = -17;
		constraintsJToggle24A.ipady = 5;
		constraintsJToggle24A.insets = new java.awt.Insets(2, 2, 2, 10);
		add(getJToggle24A(), constraintsJToggle24A);

		java.awt.GridBagConstraints constraintsJToggle24B = new java.awt.GridBagConstraints();
		constraintsJToggle24B.gridx = 17; constraintsJToggle24B.gridy = 4;
		constraintsJToggle24B.ipadx = -17;
		constraintsJToggle24B.ipady = 5;
		constraintsJToggle24B.insets = new java.awt.Insets(2, 2, 18, 10);
		add(getJToggle24B(), constraintsJToggle24B);

		java.awt.GridBagConstraints constraintsActionPasser = new java.awt.GridBagConstraints();
		constraintsActionPasser.gridx = 2; constraintsActionPasser.gridy = 2;
		constraintsActionPasser.gridwidth = -1;
constraintsActionPasser.gridheight = -1;
		constraintsActionPasser.ipadx = -35;
		constraintsActionPasser.ipady = -11;
		add(getActionPasser(), constraintsActionPasser);
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

}
/**
 * Comment
 */
public void toggle_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	getActionPasser().doClick();
	return;
}
}
