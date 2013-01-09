package com.cannontech.dbeditor.editor.user;

/**
 * This type was created in VisualAge.
 */
import java.awt.Cursor;
import java.util.concurrent.Executor;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;

import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.user.YukonGroup;
import com.cannontech.spring.YukonSpringHook;

public class GroupRoleBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.CaretListener {
	private javax.swing.JEditorPane ivjJEditorPaneDesc = null;
	private javax.swing.JLabel ivjJLabelGroupName = null;
	private javax.swing.JLabel ivjJLabelNormalDesc = null;
	private javax.swing.JPanel ivjJPanelGroupPanel = null;
	private javax.swing.JScrollPane ivjJScrollPaneDesc = null;
	private javax.swing.JTextField ivjJTextFieldGroupName = null;
	private javax.swing.JList ivjJListMembers = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JLabel ivjGroupIDJLabel = null;
	private javax.swing.JLabel ivjGroupIDValueField = null;
	private javax.swing.JScrollPane ivjJScrollPaneMembers = null;

class IvjEventHandler implements javax.swing.event.CaretListener {
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == GroupRoleBasePanel.this.getJTextFieldGroupName()) 
				connEtoC1(e);
			if (e.getSource() == GroupRoleBasePanel.this.getJEditorPaneDesc()) 
				connEtoC2(e);
		};
	};
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public GroupRoleBasePanel() {
	super();
	initialize();
}
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public GroupRoleBasePanel(boolean showMembers_) {
	super();
	getJScrollPaneMembers().setVisible( showMembers_ );
	initialize();
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJTextFieldGroupName()) 
		connEtoC1(e);
	if (e.getSource() == getJEditorPaneDesc()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JTextFieldFirstName.caret.caretUpdate(javax.swing.event.CaretEvent) --> CustomerContactBasePanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JEditorPaneDesc.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupRoleBasePanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * Return the GroupIDJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getGroupIDJLabel() {
	if (ivjGroupIDJLabel == null) {
		try {
			ivjGroupIDJLabel = new javax.swing.JLabel();
			ivjGroupIDJLabel.setName("GroupIDJLabel");
			ivjGroupIDJLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjGroupIDJLabel.setText("Group ID: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGroupIDJLabel;
}
/**
 * Return the GroupIDValueField property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getGroupIDValueField() {
	if (ivjGroupIDValueField == null) {
		try {
			ivjGroupIDValueField = new javax.swing.JLabel();
			ivjGroupIDValueField.setName("GroupIDValueField");
			ivjGroupIDValueField.setFont(new java.awt.Font("Arial", 1, 14));
			ivjGroupIDValueField.setText("new");
			ivjGroupIDValueField.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGroupIDValueField;
}
/**
 * Return the JEditorPaneDesc property value.
 * @return javax.swing.JEditorPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JEditorPane getJEditorPaneDesc() {
	if (ivjJEditorPaneDesc == null) {
		try {
			ivjJEditorPaneDesc = new javax.swing.JEditorPane();
			ivjJEditorPaneDesc.setName("JEditorPaneDesc");
			ivjJEditorPaneDesc.setPreferredSize(new java.awt.Dimension(137, 56));
			ivjJEditorPaneDesc.setBounds(0, 0, 137, 56);
			ivjJEditorPaneDesc.setMinimumSize(new java.awt.Dimension(137, 56));
			ivjJEditorPaneDesc.setDocument( new TextFieldDocument(200));
			// user code begin {1}
			
			ivjJEditorPaneDesc.setText("A user created role group");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJEditorPaneDesc;
}
/**
 * Return the JLabelGroupName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelGroupName() {
	if (ivjJLabelGroupName == null) {
		try {
			ivjJLabelGroupName = new javax.swing.JLabel();
			ivjJLabelGroupName.setName("JLabelGroupName");
			ivjJLabelGroupName.setText("Group Name:");
			ivjJLabelGroupName.setMaximumSize(new java.awt.Dimension(133, 17));
			ivjJLabelGroupName.setPreferredSize(new java.awt.Dimension(133, 17));
			ivjJLabelGroupName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelGroupName.setEnabled(true);
			ivjJLabelGroupName.setMinimumSize(new java.awt.Dimension(133, 17));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelGroupName;
}
/**
 * Return the JLabelNormalDesc property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelNormalDesc() {
	if (ivjJLabelNormalDesc == null) {
		try {
			ivjJLabelNormalDesc = new javax.swing.JLabel();
			ivjJLabelNormalDesc.setName("JLabelNormalDesc");
			ivjJLabelNormalDesc.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelNormalDesc.setText("Description");
			ivjJLabelNormalDesc.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelNormalDesc;
}
/**
 * Return the JList1 property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getJListMembers() {
	if (ivjJListMembers == null) {
		try {
			ivjJListMembers = new javax.swing.JList();
			ivjJListMembers.setName("JListMembers");
			ivjJListMembers.setBounds(0, 0, 390, 729);
			// user code begin {1}
			
			ivjJListMembers.setBorder( BorderFactory.createBevelBorder(BevelBorder.LOWERED) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJListMembers;
}
/**
 * Return the JPanelGroupPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelGroupPanel() {
	if (ivjJPanelGroupPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Group Information");
			ivjJPanelGroupPanel = new javax.swing.JPanel();
			ivjJPanelGroupPanel.setName("JPanelGroupPanel");
			ivjJPanelGroupPanel.setBorder(ivjLocalBorder);
			ivjJPanelGroupPanel.setLayout(new java.awt.GridBagLayout());
			ivjJPanelGroupPanel.setMaximumSize(new java.awt.Dimension(400, 159));
			ivjJPanelGroupPanel.setPreferredSize(new java.awt.Dimension(400, 159));
			ivjJPanelGroupPanel.setMinimumSize(new java.awt.Dimension(400, 159));

			java.awt.GridBagConstraints constraintsJLabelGroupName = new java.awt.GridBagConstraints();
			constraintsJLabelGroupName.gridx = 1; constraintsJLabelGroupName.gridy = 1;
			constraintsJLabelGroupName.insets = new java.awt.Insets(21, 14, 6, 2);
			getJPanelGroupPanel().add(getJLabelGroupName(), constraintsJLabelGroupName);

			java.awt.GridBagConstraints constraintsJLabelNormalDesc = new java.awt.GridBagConstraints();
			constraintsJLabelNormalDesc.gridx = 1; constraintsJLabelNormalDesc.gridy = 3;
			constraintsJLabelNormalDesc.ipadx = 62;
			constraintsJLabelNormalDesc.ipady = -2;
			constraintsJLabelNormalDesc.insets = new java.awt.Insets(3, 14, 1, 2);
			getJPanelGroupPanel().add(getJLabelNormalDesc(), constraintsJLabelNormalDesc);

			java.awt.GridBagConstraints constraintsJTextFieldGroupName = new java.awt.GridBagConstraints();
			constraintsJTextFieldGroupName.gridx = 2; constraintsJTextFieldGroupName.gridy = 1;
			constraintsJTextFieldGroupName.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldGroupName.weightx = 1.0;
			constraintsJTextFieldGroupName.ipadx = -20;
			constraintsJTextFieldGroupName.insets = new java.awt.Insets(21, 3, 3, 52);
			getJPanelGroupPanel().add(getJTextFieldGroupName(), constraintsJTextFieldGroupName);

			java.awt.GridBagConstraints constraintsJScrollPaneDesc = new java.awt.GridBagConstraints();
			constraintsJScrollPaneDesc.gridx = 1; constraintsJScrollPaneDesc.gridy = 4;
			constraintsJScrollPaneDesc.gridwidth = 2;
			constraintsJScrollPaneDesc.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPaneDesc.weightx = 1.0;
			constraintsJScrollPaneDesc.weighty = 1.0;
			constraintsJScrollPaneDesc.ipadx = -9;
			constraintsJScrollPaneDesc.ipady = 24;
			constraintsJScrollPaneDesc.insets = new java.awt.Insets(1, 14, 14, 29);
			getJPanelGroupPanel().add(getJScrollPaneDesc(), constraintsJScrollPaneDesc);

			java.awt.GridBagConstraints constraintsGroupIDJLabel = new java.awt.GridBagConstraints();
			constraintsGroupIDJLabel.gridx = 1; constraintsGroupIDJLabel.gridy = 2;
			constraintsGroupIDJLabel.ipadx = 24;
			constraintsGroupIDJLabel.ipady = -2;
			constraintsGroupIDJLabel.insets = new java.awt.Insets(3, 14, 3, 46);
			getJPanelGroupPanel().add(getGroupIDJLabel(), constraintsGroupIDJLabel);

			java.awt.GridBagConstraints constraintsGroupIDValueField = new java.awt.GridBagConstraints();
			constraintsGroupIDValueField.gridx = 2; constraintsGroupIDValueField.gridy = 2;
			constraintsGroupIDValueField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGroupIDValueField.ipadx = 147;
			constraintsGroupIDValueField.ipady = 1;
			constraintsGroupIDValueField.insets = new java.awt.Insets(3, 3, 3, 53);
			getJPanelGroupPanel().add(getGroupIDValueField(), constraintsGroupIDValueField);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelGroupPanel;
}
/**
 * Return the JScrollPaneDesc property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneDesc() {
	if (ivjJScrollPaneDesc == null) {
		try {
			ivjJScrollPaneDesc = new javax.swing.JScrollPane();
			ivjJScrollPaneDesc.setName("JScrollPaneDesc");
			ivjJScrollPaneDesc.setPreferredSize(new java.awt.Dimension(366, 58));
			ivjJScrollPaneDesc.setMinimumSize(new java.awt.Dimension(366, 58));
			getJScrollPaneDesc().setViewportView(getJEditorPaneDesc());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneDesc;
}
/**
 * Return the JScrollPaneMembers property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneMembers() {
	if (ivjJScrollPaneMembers == null) {
		try {
		    TitleBorder memberScrollPaneTitleBorder = createTitledBorder("Members of this Group (loading...)");
			ivjJScrollPaneMembers = new javax.swing.JScrollPane();
			ivjJScrollPaneMembers.setName("JScrollPaneMembers");
			ivjJScrollPaneMembers.setAutoscrolls(true);
			ivjJScrollPaneMembers.setBorder(memberScrollPaneTitleBorder);
			ivjJScrollPaneMembers.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			ivjJScrollPaneMembers.setPreferredSize(new java.awt.Dimension(400, 165));
			ivjJScrollPaneMembers.setMinimumSize(new java.awt.Dimension(400, 165));
			getJScrollPaneMembers().setViewportView(getJListMembers());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneMembers;
}
private TitleBorder createTitledBorder(String text) {
    TitleBorder memberScrollPaneTitleBorder = new TitleBorder();
    memberScrollPaneTitleBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
    memberScrollPaneTitleBorder.setTitle(text);
    return memberScrollPaneTitleBorder;
}
/**
 * Return the JTextFieldGroupName property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldGroupName() {
	if (ivjJTextFieldGroupName == null) {
		try {
			ivjJTextFieldGroupName = new javax.swing.JTextField();
			ivjJTextFieldGroupName.setName("JTextFieldGroupName");
			ivjJTextFieldGroupName.setPreferredSize(new java.awt.Dimension(216, 20));
			ivjJTextFieldGroupName.setEnabled(true);
			ivjJTextFieldGroupName.setMinimumSize(new java.awt.Dimension(216, 20));
			ivjJTextFieldGroupName.setDocument(new TextFieldDocument(120, PaoUtils.ILLEGAL_NAME_CHARS) );
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldGroupName;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	YukonGroup group = null;
	
	if( o == null )
		group = new YukonGroup();
	else
		group = (YukonGroup)o;

	if( getJTextFieldGroupName().getText() != null && getJTextFieldGroupName().getText().length() > 0 )
		group.getYukonGroup().setGroupName( getJTextFieldGroupName().getText() );

	if( getJEditorPaneDesc().getText() != null && getJEditorPaneDesc().getText().length() > 0 )
		group.getYukonGroup().setGroupDescription( getJEditorPaneDesc().getText() );

	return group;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}

	// user code end
	getJTextFieldGroupName().addCaretListener(ivjEventHandler);
	getJEditorPaneDesc().addCaretListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CustomerContactLoginPanel");
		setToolTipText("");
		setLayout(new java.awt.GridBagLayout());
		setSize(416, 348);

		java.awt.GridBagConstraints constraintsJPanelGroupPanel = new java.awt.GridBagConstraints();
		constraintsJPanelGroupPanel.gridx = 1; constraintsJPanelGroupPanel.gridy = 1;
		constraintsJPanelGroupPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelGroupPanel.weightx = 1.0;
		constraintsJPanelGroupPanel.weighty = 1.0;
		constraintsJPanelGroupPanel.ipady = 26;
		constraintsJPanelGroupPanel.insets = new java.awt.Insets(9, 4, 2, 12);
		add(getJPanelGroupPanel(), constraintsJPanelGroupPanel);

		java.awt.GridBagConstraints constraintsJScrollPaneMembers = new java.awt.GridBagConstraints();
		constraintsJScrollPaneMembers.gridx = 1; constraintsJScrollPaneMembers.gridy = 2;
		constraintsJScrollPaneMembers.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneMembers.weightx = 1.0;
		constraintsJScrollPaneMembers.weighty = 1.0;
		constraintsJScrollPaneMembers.ipady = -23;
		constraintsJScrollPaneMembers.insets = new java.awt.Insets(3, 4, 7, 12);
		add(getJScrollPaneMembers(), constraintsJScrollPaneMembers);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	    // Disable editing login group descriptions:
	    // getJEditorPaneDesc().setFocusable(false);
	// user code end
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getJTextFieldGroupName().getText() == null || getJTextFieldGroupName().getText().length() <= 0 )
	{
		setErrorString("The Group Name text field must be filled in");
		return false;
	}

	if( getJEditorPaneDesc().getText() == null || getJEditorPaneDesc().getText().length() <= 0 )
	{
		setErrorString("The Group Description text field must be filled in");
		return false;
	}

	return true;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		UserLoginBasePanel aUserLoginBasePanel;
		aUserLoginBasePanel = new UserLoginBasePanel();
		frame.setContentPane(aUserLoginBasePanel);
		frame.setSize(aUserLoginBasePanel.getSize());
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
 * setValue method comment.
 */
public void setValue(Object o) {
    if( o == null )
        return;
    
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    YukonGroup group = (YukonGroup)o;

    getJTextFieldGroupName().setText( group.getYukonGroup().getGroupName() );
    getJEditorPaneDesc().setText( group.getYukonGroup().getGroupDescription() );

    getGroupIDValueField().setText(group.getGroupID().toString());

    final YukonUserDao yukonUserDao = DaoFactory.getYukonUserDao();
    final DefaultListModel listModel = new DefaultListModel();
    getJListMembers().setModel(listModel);
    final LiteYukonGroup liteGroup = (LiteYukonGroup)LiteFactory.createLite(group.getYukonGroup());

    SwingWorker<Object, LiteYukonUser> worker = new SwingWorker<Object, LiteYukonUser>() {
        protected Object doInBackground() throws Exception {
            yukonUserDao.callbackWithYukonUsersInGroup(liteGroup, new SimpleCallback<LiteYukonUser>() {
                public void handle(LiteYukonUser item) {
                    publish(item);
                }
            });
            return null;
        };
        protected void process(java.util.List<LiteYukonUser> chunks) {
            listModel.ensureCapacity(listModel.size() + chunks.size());
            for (LiteYukonUser user : chunks) {
                listModel.addElement( user.getUsername() );
            }
        };
        @Override
        protected void done() {
            setCursor(null);
            getJScrollPaneMembers().setBorder(createTitledBorder("Members of this Group"));
        }
    };

    Executor executor = YukonSpringHook.getBean("globalScheduledExecutor", Executor.class);
    executor.execute(worker);
}

@Override
public void disposeValue() {
    getJListMembers().setModel(new DefaultListModel());
}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getJTextFieldGroupName().requestFocus(); 
        } 
    });    
}

}
