package com.cannontech.dbeditor.editor.user;

import java.awt.Cursor;
import java.util.concurrent.Executor;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;
import javax.swing.event.CaretListener;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.user.UserGroup;
import com.cannontech.spring.YukonSpringHook;

public class UserGroupBasePanel extends DataInputPanel implements CaretListener {

    private YukonUserDao userDao = YukonSpringHook.getBean("yukonUserDao", YukonUserDao.class);
    
    private JPanel ivjJPanelUserGroupPanel = null;
	private JLabel ivjJLabelUserGroupName = null;
	private JTextField ivjJTextFieldUserGroupName = null;

	private JLabel ivjUserGroupIDJLabel = null;
	private JLabel ivjUserGroupIDValueField = null;

	private JLabel ivjJLabelNormalDesc = null;
	private JEditorPane ivjJEditorPaneDesc = null;
	private JScrollPane ivjJScrollPaneDesc = null;

	private JScrollPane ivjJScrollPaneMembers = null;
	private JList ivjJListMembers = null;
	
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();

	class IvjEventHandler implements CaretListener {
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == UserGroupBasePanel.this.getJTextFieldUserGroupName()) 
				connEtoC1(e);
			if (e.getSource() == UserGroupBasePanel.this.getJEditorPaneDesc()) 
				connEtoC2(e);
		};
	};

    public UserGroupBasePanel() {
    	super();
    	initialize();
    }
    
    public UserGroupBasePanel(boolean showMembers_) {
    	super();
    	getJScrollPaneMembers().setVisible( showMembers_ );
    	initialize();
    }

    /**
     * Method to handle events for the CaretListener interface.
     */
    public void caretUpdate(javax.swing.event.CaretEvent e) {
    	if (e.getSource() == getJTextFieldUserGroupName()) 
    		connEtoC1(e);
    	if (e.getSource() == getJEditorPaneDesc()) 
    		connEtoC2(e);
    }

    /**
     * connEtoC1:  (JTextFieldFirstName.caret.caretUpdate(javax.swing.event.CaretEvent) --> CustomerContactBasePanel.fireInputUpdate()V)
     */
    private void connEtoC1(javax.swing.event.CaretEvent arg1) {
    	try {
    		this.fireInputUpdate();
    	} catch (java.lang.Throwable ivjExc) {
    		handleException(ivjExc);
    	}
    }

    /**
     * connEtoC2:  (JEditorPaneDesc.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupRoleBasePanel.fireInputUpdate()V)
     */
    private void connEtoC2(javax.swing.event.CaretEvent arg1) {
    	try {
    		this.fireInputUpdate();
    	} catch (java.lang.Throwable ivjExc) {
    		handleException(ivjExc);
    	}
    }

    private javax.swing.JLabel getUserGroupIdJLabel() {
    	if (ivjUserGroupIDJLabel == null) {
			ivjUserGroupIDJLabel = new javax.swing.JLabel();
			ivjUserGroupIDJLabel.setName("UserGroupIDJLabel");
			ivjUserGroupIDJLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjUserGroupIDJLabel.setText("User Group ID: ");
    	}
    	
    	return ivjUserGroupIDJLabel;
    }

    private javax.swing.JLabel getUserGroupIdValueField() {
    	if (ivjUserGroupIDValueField == null) {
			ivjUserGroupIDValueField = new javax.swing.JLabel();
			ivjUserGroupIDValueField.setName("UserGroupIDValueField");
			ivjUserGroupIDValueField.setFont(new java.awt.Font("Arial", 1, 14));
			ivjUserGroupIDValueField.setText("new");
			ivjUserGroupIDValueField.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    	}

    	return ivjUserGroupIDValueField;
    }

    private javax.swing.JEditorPane getJEditorPaneDesc() {
    	if (ivjJEditorPaneDesc == null) {
			ivjJEditorPaneDesc = new javax.swing.JEditorPane();
			ivjJEditorPaneDesc.setName("JEditorPaneDesc");
			ivjJEditorPaneDesc.setPreferredSize(new java.awt.Dimension(137, 56));
			ivjJEditorPaneDesc.setBounds(0, 0, 137, 56);
			ivjJEditorPaneDesc.setMinimumSize(new java.awt.Dimension(137, 56));
			ivjJEditorPaneDesc.setDocument( new TextFieldDocument(200));
			ivjJEditorPaneDesc.setText("A user created user group");
    	}
    	
    	return ivjJEditorPaneDesc;
    }

    private javax.swing.JLabel getJLabelUserGroupName() {
    	if (ivjJLabelUserGroupName == null) {
			ivjJLabelUserGroupName = new javax.swing.JLabel();
			ivjJLabelUserGroupName.setName("JLabelUserGroupName");
			ivjJLabelUserGroupName.setText("User Group Name:");
			ivjJLabelUserGroupName.setMaximumSize(new java.awt.Dimension(133, 17));
			ivjJLabelUserGroupName.setPreferredSize(new java.awt.Dimension(133, 17));
			ivjJLabelUserGroupName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelUserGroupName.setEnabled(true);
			ivjJLabelUserGroupName.setMinimumSize(new java.awt.Dimension(133, 17));
    	}
    	
    	return ivjJLabelUserGroupName;
    }

    private javax.swing.JLabel getJLabelNormalDesc() {
    	if (ivjJLabelNormalDesc == null) {
			ivjJLabelNormalDesc = new javax.swing.JLabel();
			ivjJLabelNormalDesc.setName("JLabelNormalDesc");
			ivjJLabelNormalDesc.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelNormalDesc.setText("Description");
			ivjJLabelNormalDesc.setEnabled(true);
    	}

    	return ivjJLabelNormalDesc;
    }

    private JList getJListMembers() {
    	if (ivjJListMembers == null) {
			ivjJListMembers = new JList();
			ivjJListMembers.setName("JListMembers");
			ivjJListMembers.setBounds(0, 0, 390, 729);
			ivjJListMembers.setBorder( BorderFactory.createBevelBorder(BevelBorder.LOWERED) );
    	}
    	return ivjJListMembers;
    }

    /**
     * Return the JPanelGroupPanel property value.
     */
    private JPanel getJPanelUserGroupPanel() {
    	if (ivjJPanelUserGroupPanel == null) {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("User Group Information");
			ivjJPanelUserGroupPanel = new JPanel();
			ivjJPanelUserGroupPanel.setName("JPanelUserGroupPanel");
			ivjJPanelUserGroupPanel.setBorder(ivjLocalBorder);
			ivjJPanelUserGroupPanel.setLayout(new java.awt.GridBagLayout());
			ivjJPanelUserGroupPanel.setMaximumSize(new java.awt.Dimension(400, 159));
			ivjJPanelUserGroupPanel.setPreferredSize(new java.awt.Dimension(400, 159));
			ivjJPanelUserGroupPanel.setMinimumSize(new java.awt.Dimension(400, 159));

			java.awt.GridBagConstraints constraintsJLabelUserGroupName = new java.awt.GridBagConstraints();
			constraintsJLabelUserGroupName.gridx = 1; constraintsJLabelUserGroupName.gridy = 1;
			constraintsJLabelUserGroupName.insets = new java.awt.Insets(21, 14, 6, 2);
			getJPanelUserGroupPanel().add(getJLabelUserGroupName(), constraintsJLabelUserGroupName);

            java.awt.GridBagConstraints constraintsJTextFieldUserGroupName = new java.awt.GridBagConstraints();
            constraintsJTextFieldUserGroupName.gridx = 2; constraintsJTextFieldUserGroupName.gridy = 1;
            constraintsJTextFieldUserGroupName.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJTextFieldUserGroupName.weightx = 1.0;
            constraintsJTextFieldUserGroupName.ipadx = -20;
            constraintsJTextFieldUserGroupName.insets = new java.awt.Insets(21, 3, 3, 52);
            getJPanelUserGroupPanel().add(getJTextFieldUserGroupName(), constraintsJTextFieldUserGroupName);

            java.awt.GridBagConstraints constraintsUserGroupIdJLabel = new java.awt.GridBagConstraints();
            constraintsUserGroupIdJLabel.gridx = 1; constraintsUserGroupIdJLabel.gridy = 2;
            constraintsUserGroupIdJLabel.ipadx = 24;
            constraintsUserGroupIdJLabel.ipady = -2;
            constraintsUserGroupIdJLabel.insets = new java.awt.Insets(3, 14, 3, 46);
            getJPanelUserGroupPanel().add(getUserGroupIdJLabel(), constraintsUserGroupIdJLabel);

            java.awt.GridBagConstraints constraintsUserGroupIdValueField = new java.awt.GridBagConstraints();
            constraintsUserGroupIdValueField.gridx = 2; constraintsUserGroupIdValueField.gridy = 2;
            constraintsUserGroupIdValueField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsUserGroupIdValueField.ipadx = 147;
            constraintsUserGroupIdValueField.ipady = 1;
            constraintsUserGroupIdValueField.insets = new java.awt.Insets(3, 3, 3, 53);
            getJPanelUserGroupPanel().add(getUserGroupIdValueField(), constraintsUserGroupIdValueField);

            java.awt.GridBagConstraints constraintsJLabelNormalDesc = new java.awt.GridBagConstraints();
			constraintsJLabelNormalDesc.gridx = 1; constraintsJLabelNormalDesc.gridy = 3;
			constraintsJLabelNormalDesc.ipadx = 62;
			constraintsJLabelNormalDesc.ipady = -2;
			constraintsJLabelNormalDesc.insets = new java.awt.Insets(3, 14, 1, 2);
			getJPanelUserGroupPanel().add(getJLabelNormalDesc(), constraintsJLabelNormalDesc);

			java.awt.GridBagConstraints constraintsJScrollPaneDesc = new java.awt.GridBagConstraints();
			constraintsJScrollPaneDesc.gridx = 1; constraintsJScrollPaneDesc.gridy = 4;
			constraintsJScrollPaneDesc.gridwidth = 2;
			constraintsJScrollPaneDesc.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPaneDesc.weightx = 1.0;
			constraintsJScrollPaneDesc.weighty = 1.0;
			constraintsJScrollPaneDesc.ipadx = -9;
			constraintsJScrollPaneDesc.ipady = 24;
			constraintsJScrollPaneDesc.insets = new java.awt.Insets(1, 14, 14, 29);
			getJPanelUserGroupPanel().add(getJScrollPaneDesc(), constraintsJScrollPaneDesc);

    	}
    	return ivjJPanelUserGroupPanel;
    }

    private javax.swing.JScrollPane getJScrollPaneDesc() {
    	if (ivjJScrollPaneDesc == null) {
			ivjJScrollPaneDesc = new javax.swing.JScrollPane();
			ivjJScrollPaneDesc.setName("JScrollPaneDesc");
			ivjJScrollPaneDesc.setPreferredSize(new java.awt.Dimension(366, 58));
			ivjJScrollPaneDesc.setMinimumSize(new java.awt.Dimension(366, 58));
			getJScrollPaneDesc().setViewportView(getJEditorPaneDesc());
    	}
    	return ivjJScrollPaneDesc;
    }

    private javax.swing.JScrollPane getJScrollPaneMembers() {
    	if (ivjJScrollPaneMembers == null) {
		    TitleBorder memberScrollPaneTitleBorder = createTitledBorder("Members of this User Group (loading...)");
			ivjJScrollPaneMembers = new javax.swing.JScrollPane();
			ivjJScrollPaneMembers.setName("JScrollPaneMembers");
			ivjJScrollPaneMembers.setAutoscrolls(true);
			ivjJScrollPaneMembers.setBorder(memberScrollPaneTitleBorder);
			ivjJScrollPaneMembers.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			ivjJScrollPaneMembers.setPreferredSize(new java.awt.Dimension(400, 165));
			ivjJScrollPaneMembers.setMinimumSize(new java.awt.Dimension(400, 165));
			getJScrollPaneMembers().setViewportView(getJListMembers());
    	}
    	return ivjJScrollPaneMembers;
    }

    private TitleBorder createTitledBorder(String text) {
        TitleBorder memberScrollPaneTitleBorder = new TitleBorder();
        memberScrollPaneTitleBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
        memberScrollPaneTitleBorder.setTitle(text);
        return memberScrollPaneTitleBorder;
    }

    private javax.swing.JTextField getJTextFieldUserGroupName() {
    	if (ivjJTextFieldUserGroupName == null) {
			ivjJTextFieldUserGroupName = new javax.swing.JTextField();
			ivjJTextFieldUserGroupName.setName("JTextFieldUserGroupName");
			ivjJTextFieldUserGroupName.setPreferredSize(new java.awt.Dimension(216, 20));
			ivjJTextFieldUserGroupName.setEnabled(true);
			ivjJTextFieldUserGroupName.setMinimumSize(new java.awt.Dimension(216, 20));
			ivjJTextFieldUserGroupName.setDocument(new TextFieldDocument(120, TextFieldDocument.INVALID_CHARS_PAO));
    	}
    	return ivjJTextFieldUserGroupName;
    }

    public Object getValue(Object o)  {
    	UserGroup userGroup = null;
    	
    	if( o == null ) {
    		userGroup = new UserGroup();
    	} else {
    		userGroup = (UserGroup)o;
    	}
    
    	if( getJTextFieldUserGroupName().getText() != null && getJTextFieldUserGroupName().getText().length() > 0 )
    		userGroup.getLiteUserGroup().setUserGroupName( getJTextFieldUserGroupName().getText() );
    
    	if( getJEditorPaneDesc().getText() != null && getJEditorPaneDesc().getText().length() > 0 )
    		userGroup.getLiteUserGroup().setUserGroupDescription( getJEditorPaneDesc().getText() );
    
    	return userGroup;
    }

    /**
     * Called whenever the part throws an exception.
     */
    private void handleException(Throwable exception) {
    
    	/* Uncomment the following lines to print uncaught exceptions to stdout */
    	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
    	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
    }

    /**
     * Initializes connections
     */
    private void initConnections() {
    	getJTextFieldUserGroupName().addCaretListener(ivjEventHandler);
    	getJEditorPaneDesc().addCaretListener(ivjEventHandler);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
    		setName("UserGroupPanel");
    		setToolTipText("");
    		setLayout(new java.awt.GridBagLayout());
    		setSize(416, 348);
    
    		java.awt.GridBagConstraints constraintsJPanelUserGroupPanel = new java.awt.GridBagConstraints();
    		constraintsJPanelUserGroupPanel.gridx = 1; constraintsJPanelUserGroupPanel.gridy = 1;
    		constraintsJPanelUserGroupPanel.fill = java.awt.GridBagConstraints.BOTH;
    		constraintsJPanelUserGroupPanel.weightx = 1.0;
    		constraintsJPanelUserGroupPanel.weighty = 1.0;
    		constraintsJPanelUserGroupPanel.ipady = 26;
    		constraintsJPanelUserGroupPanel.insets = new java.awt.Insets(9, 4, 2, 12);
    		add(getJPanelUserGroupPanel(), constraintsJPanelUserGroupPanel);
    
    		java.awt.GridBagConstraints constraintsJScrollPaneMembers = new java.awt.GridBagConstraints();
    		constraintsJScrollPaneMembers.gridx = 1; constraintsJScrollPaneMembers.gridy = 2;
    		constraintsJScrollPaneMembers.fill = java.awt.GridBagConstraints.BOTH;
    		constraintsJScrollPaneMembers.weightx = 1.0;
    		constraintsJScrollPaneMembers.weighty = 1.0;
    		constraintsJScrollPaneMembers.ipady = -23;
    		constraintsJScrollPaneMembers.insets = new java.awt.Insets(3, 4, 7, 12);
    		add(getJScrollPaneMembers(), constraintsJScrollPaneMembers);
    		initConnections();
    }

    /**
     * 
     */
    public boolean isInputValid() {
    	if( getJTextFieldUserGroupName().getText() == null || getJTextFieldUserGroupName().getText().length() <= 0 ) {
    		setErrorString("The User Group Name text field must be filled in");
    		return false;
    	}
    
    	if( getJEditorPaneDesc().getText() == null || getJEditorPaneDesc().getText().length() <= 0 ) {
    		setErrorString("The User Group Description text field must be filled in");
    		return false;
    	}
    
    	return true;
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     */
    public static void main(java.lang.String[] args) {
		JFrame frame = new JFrame();
		UserGroupBasePanel userGroupBasePanel;
		userGroupBasePanel = new UserGroupBasePanel();
		frame.setContentPane(userGroupBasePanel);
		frame.setSize(userGroupBasePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
    }
    
    /**
     * setValue method comment.
     */
    public void setValue(Object o) {
        if( o == null )
            return;
        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    
        final LiteUserGroup userGroup = ((com.cannontech.database.data.user.UserGroup)o).getLiteUserGroup();
    
        getJTextFieldUserGroupName().setText( userGroup.getUserGroupName() );
        getJEditorPaneDesc().setText( userGroup.getUserGroupDescription() );
    
        getUserGroupIdValueField().setText(String.valueOf(userGroup.getUserGroupId()));
    
        final DefaultListModel listModel = new DefaultListModel();
        getJListMembers().setModel(listModel);
        SwingWorker<Object, LiteYukonUser> worker = new SwingWorker<Object, LiteYukonUser>() {
            
            @Override
            protected Object doInBackground() {
                userDao.callbackWithYukonUsersInUserGroup(userGroup, new SimpleCallback<LiteYukonUser>() {
                    public void handle(LiteYukonUser item) {
                        publish(item);
                    }
                });
                return null;
            };
            
            @Override
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
    
    public void setFirstFocus() {
        // Make sure that when its time to display this panel, the focus starts in the top component
        javax.swing.SwingUtilities.invokeLater( new Runnable() { 
            public void run() { 
                getJTextFieldUserGroupName().requestFocus(); 
            } 
        });    
    }
}