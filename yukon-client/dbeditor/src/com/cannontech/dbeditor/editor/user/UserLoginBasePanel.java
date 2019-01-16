package com.cannontech.dbeditor.editor.user;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.common.i18n.DisplayableEnumCellRenderer;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.user.UserAuthenticationInfo;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.authentication.model.AuthenticationCategory;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.user.YukonUser;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.web.EnergyCompanyOperatorLoginList;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.user.UserUtils;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class UserLoginBasePanel extends DataInputPanel {
    
    private AuthenticationService authenticationService = YukonSpringHook.getBean(AuthenticationService.class);
    private UserGroupDao userGroupDao = YukonSpringHook.getBean(UserGroupDao.class);
    private YukonUserDao yukonUserDao = YukonSpringHook.getBean(YukonUserDao.class);
    private EnergyCompanyDao ecDao = YukonSpringHook.getBean(EnergyCompanyDao.class);

    private JLabel ivjJLabelUserName = null;
    private JPanel ivjJPanelLoginPanel = null;
    private JTextField ivjJTextFieldUserID = null;
    private JCheckBox ivjJCheckBoxEnableLogin = null;
    private JButton ivjJButtonChangePassword = null;
    private JComboBox<AuthenticationCategory> ivjJListAuthType = null;
    private JLabel ivjJLabelAuthType = null;
    private JComboBox ivjJListUserGroup = null;
    private JLabel ivjJLabelUserGroup = null;
    private JLabel ivjJLabelErrorMessage = null;
    private JPanel ivjJPanelError = null;
    private AuthenticationCategory initialAuthenticationCategory = null;
    private String newPasswordValue = null;
    private boolean passwordRequiresChanging = false;
    private long oldEnergyCompanyID = -1;
    private JCheckBox ivjJCheckBoxEnableEC = null;
    private JComboBox<EnergyCompany> ivjJComboBoxEnergyCompany = null;
    private JPanel ivjJPanelEC = null;
    private String initialUsername;

    private ActionListener enableEcActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            jCheckBoxEnableEC_ActionPerformed(e);
        }
    };

    private CaretListener inputUpdateCaretListener = new CaretListener() {
        @Override
        public void caretUpdate(CaretEvent e) {
            fireInputUpdate();
        }
    };

    private ActionListener inputUpdateActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            fireInputUpdate();
        }
    };

    public UserLoginBasePanel() {
        super();
        initialize();
    }

    private JCheckBox getJCheckBoxEnableEC() {
        if (ivjJCheckBoxEnableEC == null) {
            try {
                ivjJCheckBoxEnableEC = new JCheckBox();
                ivjJCheckBoxEnableEC.setName("JCheckBoxEnableEC");
                ivjJCheckBoxEnableEC.setPreferredSize(new Dimension(174, 22));
                ivjJCheckBoxEnableEC.setText("Link to Energy Company: ");
                ivjJCheckBoxEnableEC.setMaximumSize(new Dimension(174, 22));
                ivjJCheckBoxEnableEC.setMinimumSize(new Dimension(174, 22));
                ivjJCheckBoxEnableEC.setToolTipText("Only for use with STARS.  Verify that STARS and its operator " +
                		"groups have been properly configured.");
            } catch (Exception ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJCheckBoxEnableEC;
    }

    private JCheckBox getJCheckBoxEnableLogin() {
        if (ivjJCheckBoxEnableLogin == null) {
            try {
                ivjJCheckBoxEnableLogin = new JCheckBox();
                ivjJCheckBoxEnableLogin.setName("JCheckBoxEnableLogin");
                ivjJCheckBoxEnableLogin.setFont(new Font("Arial", 1, 14));
                ivjJCheckBoxEnableLogin.setText("Enabled");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJCheckBoxEnableLogin;
    }

    private JComboBox<EnergyCompany> getJComboBoxEnergyCompany() {
        if (ivjJComboBoxEnergyCompany == null) {
            try {
                ivjJComboBoxEnergyCompany = new JComboBox<>();
                ivjJComboBoxEnergyCompany.setName("JComboBoxEnergyCompany");
                ivjJComboBoxEnergyCompany.setPreferredSize(new Dimension(215, 23));
                ivjJComboBoxEnergyCompany.setMinimumSize(new Dimension(215, 23));
                ivjJComboBoxEnergyCompany.setEnabled(false);
                ivjJComboBoxEnergyCompany.setToolTipText("Only for use with STARS.  Verify that STARS and its " +
                		"operator groups have been properly configured.");
                ivjJComboBoxEnergyCompany.setRenderer(new DefaultListCellRenderer() {
                   @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                             boolean isSelected, boolean cellHasFocus) {
                         super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                         if (value instanceof EnergyCompany) {
                             EnergyCompany ec = (EnergyCompany) value;
                             setText(ec.getName());
                         }
                         return this;
                    }
                });
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        
        return ivjJComboBoxEnergyCompany;
    }

    private JButton getJButtonChangePassword() {
        if (ivjJButtonChangePassword == null) {
            try {
                ivjJButtonChangePassword = new JButton();
                ivjJButtonChangePassword.setName("JButtonChangePassword");
                ivjJButtonChangePassword.setText("Change Password");
                ivjJButtonChangePassword.setEnabled(true);
                ivjJButtonChangePassword.setSelected(false);
                final JPanel that = this;
                ivjJButtonChangePassword.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ChangePasswordDialog dialog =
                            ChangePasswordDialog.create(that, "Enter a new password for user");
                        dialog.setVisible(true);
                        if (dialog.getNewPassword() != null) {
                            fireInputUpdate();
                            newPasswordValue = dialog.getNewPassword();
                        }
                    }
                });
            } catch (Exception ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJButtonChangePassword;
    }

    private JLabel getJLabelAuthType() {
        if (ivjJLabelAuthType == null) {
            try {
                ivjJLabelUserName = new JLabel();
                ivjJLabelUserName.setName("JLabelAuthType");
                ivjJLabelUserName.setText("Authentication:");
                ivjJLabelUserName.setMaximumSize(new Dimension(122, 17));
                ivjJLabelUserName.setPreferredSize(new Dimension(122, 17));
                ivjJLabelUserName.setFont(new Font("dialog", 0, 14));
                ivjJLabelUserName.setMinimumSize(new Dimension(122, 17));
            } catch (Exception ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelUserName;
    }

    private JComboBox<AuthenticationCategory> getJListAuthType() {
        if (ivjJListAuthType == null) {
            ivjJListAuthType = new JComboBox<>(AuthenticationCategory.values());
            ivjJListAuthType.setRenderer(new DisplayableEnumCellRenderer());
            ivjJListAuthType.setName("JListAuthType");
            Dimension dimension = new Dimension(122, 17);
            ivjJListAuthType.setMaximumSize(dimension);
            ivjJListAuthType.setPreferredSize(dimension);
            ivjJListAuthType.setMinimumSize(dimension);
            ivjJListAuthType.setFont(new Font("dialog", 0, 12));
            ivjJListAuthType.setEnabled(true);
            ivjJListAuthType.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.DESELECTED) {
                        return;
                    }
                    AuthenticationCategory type = (AuthenticationCategory) ivjJListAuthType.getSelectedItem();
                    boolean shouldSetPass = authenticationService.supportsPasswordSet(type.getSupportingAuthType());
                    if (type != initialAuthenticationCategory) {
                        fireInputUpdate();
                    }
                    getJButtonChangePassword().setEnabled(shouldSetPass);
                }
            });
        }
        return ivjJListAuthType;
    }

    private JLabel getJLabelUserName() {
        if (ivjJLabelUserName == null) {
            try {
                ivjJLabelUserName = new JLabel();
                ivjJLabelUserName.setName("JLabelUserName");
                ivjJLabelUserName.setText("Username:");
                ivjJLabelUserName.setMaximumSize(new Dimension(122, 17));
                ivjJLabelUserName.setPreferredSize(new Dimension(122, 17));
                ivjJLabelUserName.setFont(new Font("dialog", 0, 14));
                ivjJLabelUserName.setEnabled(true);
                ivjJLabelUserName.setMinimumSize(new Dimension(122, 17));
                // user code begin {1}
                // user code end
            } catch (Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJLabelUserName;
    }

    private JLabel getJLabelUserGroup() {
        if (ivjJLabelUserGroup == null) {
            try {
                ivjJLabelUserGroup = new javax.swing.JLabel();
                ivjJLabelUserGroup.setName("JLabelUserGroup");
                ivjJLabelUserGroup.setText("User Group:");
                ivjJLabelUserGroup.setMaximumSize(new java.awt.Dimension(122, 17));
                ivjJLabelUserGroup.setPreferredSize(new java.awt.Dimension(122, 17));
                ivjJLabelUserGroup.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelUserGroup.setMinimumSize(new java.awt.Dimension(122, 17));
            } catch (Exception ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelUserGroup;
    }
    
    private JComboBox getJListUserGroup() {
        
        List<LiteUserGroup> allUserGroups = userGroupDao.getAllLiteUserGroups();
        
        List<String> userGroupNames = new ArrayList<>();
        userGroupNames.add(CtiUtilities.STRING_NONE);
        userGroupNames.addAll(
            Lists.transform(allUserGroups, new Function<LiteUserGroup, String>() {
                @Override
                public String apply(LiteUserGroup liteUserGroup) {
                    return liteUserGroup.getUserGroupName();
                }
            }));
        
        if (ivjJListUserGroup == null) {
            ivjJListUserGroup = new javax.swing.JComboBox(userGroupNames.toArray());
            ivjJListUserGroup.setName("JListUserGroup");
            java.awt.Dimension dimension = new java.awt.Dimension(400, 17);
            ivjJListUserGroup.setMaximumSize(dimension);
            ivjJListUserGroup.setPreferredSize(dimension);
            ivjJListUserGroup.setMinimumSize(dimension);
            ivjJListUserGroup.setFont(new java.awt.Font("dialog", 0, 12));
            ivjJListUserGroup.setEnabled(true);
            ivjJListUserGroup.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.DESELECTED) {
                        return;
                    }
                    fireInputUpdate();
                }
            });
        }
        return ivjJListUserGroup;
    }
    
    private JPanel getJPanelError() {
        if (ivjJPanelError == null) {
            try {
                ivjJPanelError = new JPanel();
                ivjJPanelError.setName("JPanelError");
                ivjJPanelError.setPreferredSize(new Dimension(342, 10));
                ivjJPanelError.setLayout(new FlowLayout());
                ivjJPanelError.setMaximumSize(new Dimension(342, 10));
                ivjJPanelError.setMinimumSize(new Dimension(342, 10));
                getJPanelError().add(getJLabelErrorMessage(), getJLabelErrorMessage().getName());

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJPanelError;
    }

    private JLabel getJLabelErrorMessage() {
        if (ivjJLabelErrorMessage == null) {
            try {
                ivjJLabelErrorMessage = new JLabel();
                ivjJLabelErrorMessage.setName("JLabelRange");
                ivjJLabelErrorMessage.setOpaque(false);
                ivjJLabelErrorMessage.setText("...RANGE TEXT...");
                ivjJLabelErrorMessage.setVisible(true);
                ivjJLabelErrorMessage.setHorizontalTextPosition(SwingConstants.CENTER);
                ivjJLabelErrorMessage.setFont(new Font("Arial", 1, 10));
                ivjJLabelErrorMessage.setHorizontalAlignment(SwingConstants.CENTER);
                ivjJLabelErrorMessage.setVisible(false);

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelErrorMessage;
    }

    private JPanel getJPanelEC() {
        if (ivjJPanelEC == null) {
            try {
                TitleBorder ivjLocalBorder1;
                ivjLocalBorder1 = new TitleBorder();
                ivjLocalBorder1.setTitleFont(new Font("Arial", 1, 14));
                ivjLocalBorder1.setTitle("Energy Company");
                ivjJPanelEC = new JPanel();
                ivjJPanelEC.setName("JPanelEC");
                ivjJPanelEC.setBorder(ivjLocalBorder1);
                ivjJPanelEC.setLayout(new GridBagLayout());

                GridBagConstraints constraintsJCheckBoxEnableEC = new GridBagConstraints();
                constraintsJCheckBoxEnableEC.gridx = 1;
                constraintsJCheckBoxEnableEC.gridy = 1;
                constraintsJCheckBoxEnableEC.anchor = GridBagConstraints.WEST;
                constraintsJCheckBoxEnableEC.ipadx = -4;
                constraintsJCheckBoxEnableEC.insets = new Insets(20, 11, 29, 6);
                getJPanelEC().add(getJCheckBoxEnableEC(), constraintsJCheckBoxEnableEC);

                GridBagConstraints constraintsJComboBoxEnergyCompany = new GridBagConstraints();
                constraintsJComboBoxEnergyCompany.gridx = 2;
                constraintsJComboBoxEnergyCompany.gridy = 1;
                constraintsJComboBoxEnergyCompany.anchor = GridBagConstraints.WEST;
                constraintsJComboBoxEnergyCompany.weightx = 1.0;
                constraintsJComboBoxEnergyCompany.ipadx = -20;
                constraintsJComboBoxEnergyCompany.insets = new Insets(20, 6, 28, 12);
                getJPanelEC().add(getJComboBoxEnergyCompany(), constraintsJComboBoxEnergyCompany);
                // user code begin {1}
                // user code end
            } catch (Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJPanelEC;
    }

    private JPanel getJPanelLoginPanel() {
        if (ivjJPanelLoginPanel == null) {
            try {
                TitleBorder ivjLocalBorder;
                ivjLocalBorder = new TitleBorder();
                ivjLocalBorder.setTitleFont(new Font("Arial", 1, 14));
                ivjLocalBorder.setTitle("User Information");
                ivjJPanelLoginPanel = new JPanel();
                ivjJPanelLoginPanel.setName("JPanelLoginPanel");
                ivjJPanelLoginPanel.setBorder(ivjLocalBorder);
                ivjJPanelLoginPanel.setLayout(new GridBagLayout());

                GridBagConstraints constraintsJLabelUserName = new GridBagConstraints();
                constraintsJLabelUserName.gridx = 1;
                constraintsJLabelUserName.gridy = 1;
                constraintsJLabelUserName.anchor = GridBagConstraints.WEST;
                constraintsJLabelUserName.insets = new Insets(2, 20, 2, 3);
                getJPanelLoginPanel().add(getJLabelUserName(), constraintsJLabelUserName);

                GridBagConstraints constraintsJTextFieldUserID = new GridBagConstraints();
                constraintsJTextFieldUserID.gridx = 2;
                constraintsJTextFieldUserID.gridy = 1;
                constraintsJTextFieldUserID.anchor = GridBagConstraints.WEST;
                constraintsJTextFieldUserID.weightx = 1.0;
                constraintsJTextFieldUserID.insets = new Insets(0, 4, 1, 22);
                getJPanelLoginPanel().add(getJTextFieldUserID(), constraintsJTextFieldUserID);

                GridBagConstraints constraintsJLabelAuthType = new GridBagConstraints();
                constraintsJLabelAuthType.gridx = 1;
                constraintsJLabelAuthType.gridy = 2;
                constraintsJLabelAuthType.anchor = GridBagConstraints.WEST;
                constraintsJLabelAuthType.insets = new Insets(2, 20, 2, 3);
                getJPanelLoginPanel().add(getJLabelAuthType(), constraintsJLabelAuthType);

                GridBagConstraints constraintsJListAuthType = new GridBagConstraints();
                constraintsJListAuthType.gridx = 2;
                constraintsJListAuthType.gridy = 2;
                constraintsJListAuthType.anchor = GridBagConstraints.WEST;
                constraintsJListAuthType.insets = new Insets(0, 4, 1, 22);
                getJPanelLoginPanel().add(getJListAuthType(), constraintsJListAuthType);

                GridBagConstraints constraintsJLabelUserGroup = new GridBagConstraints();
                constraintsJLabelUserGroup.gridx = 1; constraintsJLabelUserGroup.gridy = 3;
                constraintsJLabelUserGroup.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabelUserGroup.insets = new java.awt.Insets(2, 20, 2, 3);
                getJPanelLoginPanel().add(getJLabelUserGroup(), constraintsJLabelUserGroup);
                
                GridBagConstraints constraintsJListUserGroup = new GridBagConstraints();
                constraintsJListUserGroup.gridx = 2; constraintsJListUserGroup.gridy = 3;
                constraintsJListUserGroup.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJListUserGroup.insets = new java.awt.Insets(0, 4, 1, 22);
                getJPanelLoginPanel().add(getJListUserGroup(), constraintsJListUserGroup);

                GridBagConstraints constraintsJButtonChangePassword = new GridBagConstraints();
                constraintsJButtonChangePassword.gridx = 1;
                constraintsJButtonChangePassword.gridy = 4;
                constraintsJButtonChangePassword.gridwidth = 2;
                constraintsJButtonChangePassword.anchor = GridBagConstraints.CENTER;
                constraintsJButtonChangePassword.insets = new Insets(15, 15, 2, 15);
                getJPanelLoginPanel().add(getJButtonChangePassword(), constraintsJButtonChangePassword);

            } catch (Exception ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJPanelLoginPanel;
    }

    private JTextField getJTextFieldUserID() {
        if (ivjJTextFieldUserID == null) {
            try {
                ivjJTextFieldUserID = new JTextField();
                ivjJTextFieldUserID.setName("JTextFieldUserID");
                ivjJTextFieldUserID.setPreferredSize(new Dimension(229, 20));
                ivjJTextFieldUserID.setEnabled(true);
                ivjJTextFieldUserID.setMinimumSize(new Dimension(229, 20));
                ivjJTextFieldUserID.setDocument(new TextFieldDocument(64));
            } catch (Exception ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJTextFieldUserID;
    }

    @Override
    public Object getValue(Object o) {
        YukonUser login = null;

        if (o == null) {
            login = new YukonUser();
            /* addDefaultGroups( login ); */
        } else {
            login = (YukonUser) o;
        }

        if (getJTextFieldUserID().getText() != null && getJTextFieldUserID().getText().length() > 0) {
            login.getYukonUser().setUsername(getJTextFieldUserID().getText());
        }

        AuthenticationCategory authenticationCategory = (AuthenticationCategory) getJListAuthType().getSelectedItem();

        boolean shouldSetPass = authenticationService.supportsPasswordSet(authenticationCategory);
        if (authenticationCategory != initialAuthenticationCategory) {
            if (shouldSetPass) {
                passwordRequiresChanging = true;
            } else {
                passwordRequiresChanging = false;
                newPasswordValue = null;
            }
        }

        String userGroupName = String.valueOf(getJListUserGroup().getSelectedItem());
        if (userGroupName.equals(CtiUtilities.STRING_NONE)) {
            login.getYukonUser().setUserGroupId(null);
        } else {
            LiteUserGroup newUserGroup = userGroupDao.getLiteUserGroupByUserGroupName(userGroupName);
            login.getYukonUser().setUserGroupId(newUserGroup.getUserGroupId());
        }

        if (passwordRequiresChanging && newPasswordValue == null) {
            // prompt for new password

            ChangePasswordDialog dialog =
                ChangePasswordDialog.create(this, "A new password must be entered for this user");
            dialog.setVisible(true);
            if (dialog.getNewPassword() == null) {
                throw new RuntimeException("Must enter new password to change user.");
            }
            newPasswordValue = dialog.getNewPassword();
        }

        if (getJCheckBoxEnableLogin().isSelected()) {
            login.getYukonUser().setLoginStatus(LoginStatusEnum.ENABLED);
        } else {
            login.getYukonUser().setLoginStatus(LoginStatusEnum.DISABLED);
        }

        if (getJCheckBoxEnableEC().isSelected()) {
            EnergyCompany co = (EnergyCompany) getJComboBoxEnergyCompany().getSelectedItem();

            EnergyCompanyOperatorLoginList ecop =
                new EnergyCompanyOperatorLoginList(co.getId(), login.getUserID());

            // if this is the same energy company that it was before, don't bother updating
            if (co.getId() != oldEnergyCompanyID) {
                login.setEnergyCompany(ecop);
            }
        }

        // there was an existing energy company link, but we want it no longer
        else if ((!getJCheckBoxEnableEC().isSelected()) && oldEnergyCompanyID != -1) {
            EnergyCompanyOperatorLoginList ecop = new EnergyCompanyOperatorLoginList();
            ecop.setOperatorLoginID(login.getUserID());
            login.setEnergyCompany(ecop);
        }

        return login;
    }

    @Override
    public void postSave(DBPersistent o) {
        super.postSave(o);
        AuthenticationCategory authCategory = (AuthenticationCategory) getJListAuthType().getSelectedItem();
        LiteYukonUser user = (LiteYukonUser) LiteFactory.createLite(o);
        if (newPasswordValue != null) {
            // change password
            boolean supportsSetPassword = authenticationService.supportsPasswordSet(authCategory);
            if (supportsSetPassword) {
                authenticationService.setPassword(user, authCategory, newPasswordValue, user);
            }

            newPasswordValue = null;
            passwordRequiresChanging = false;
        } else if (authCategory != initialAuthenticationCategory
                && !authenticationService.supportsPasswordSet(authCategory)) {
            authenticationService.setAuthenticationCategory(user, authCategory);
        }
    }

    /**
     * Called whenever the part throws an exception.
     * @param exception Throwable
     */
    private void handleException(Throwable exception) {
        /* Uncomment the following lines to print uncaught exceptions to stdout */
        CTILogger.error("Uncaught exception in Spring code", exception);
    }

    /**
     * Initializes connections
     */
    private void initConnections() throws Exception {
        getJTextFieldUserID().addCaretListener(inputUpdateCaretListener);
        getJCheckBoxEnableLogin().addActionListener(inputUpdateActionListener);
        getJComboBoxEnergyCompany().addActionListener(inputUpdateActionListener);
        getJCheckBoxEnableEC().addActionListener(enableEcActionListener);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("CustomerContactLoginPanel");
            setToolTipText("");
            setLayout(new GridBagLayout());
            setSize(416, 348);

            GridBagConstraints constraintsJPanelLoginPanel = new GridBagConstraints();
            constraintsJPanelLoginPanel.gridx = 0;
            constraintsJPanelLoginPanel.gridy = 1;
            constraintsJPanelLoginPanel.fill = GridBagConstraints.BOTH;
            constraintsJPanelLoginPanel.weightx = 1.0;
            constraintsJPanelLoginPanel.weighty = 1.0;
            constraintsJPanelLoginPanel.ipadx = -10;
            constraintsJPanelLoginPanel.ipady = -26;
            constraintsJPanelLoginPanel.insets = new Insets(3, 8, 3, 8);
            add(getJPanelLoginPanel(), constraintsJPanelLoginPanel);

            GridBagConstraints constraintsJCheckBoxEnableLogin = new GridBagConstraints();
            constraintsJCheckBoxEnableLogin.gridx = 0;
            constraintsJCheckBoxEnableLogin.gridy = 0;
            constraintsJCheckBoxEnableLogin.ipadx = 32;
            constraintsJCheckBoxEnableLogin.ipady = -2;
            constraintsJCheckBoxEnableLogin.insets = new Insets(10, 8, 2, 251);
            add(getJCheckBoxEnableLogin(), constraintsJCheckBoxEnableLogin);

            GridBagConstraints constraintsJPanelEC = new GridBagConstraints();
            constraintsJPanelEC.gridx = 0;
            constraintsJPanelEC.gridy = 2;
            constraintsJPanelEC.fill = GridBagConstraints.BOTH;
            constraintsJPanelEC.weightx = 1.0;
            constraintsJPanelEC.weighty = 1.0;
            constraintsJPanelEC.ipadx = -10;
            constraintsJPanelEC.ipady = -26;
            constraintsJPanelEC.insets = new Insets(4, 8, 4, 8);
            add(getJPanelEC(), constraintsJPanelEC);

            GridBagConstraints constraintsJPanelError = new GridBagConstraints();
            constraintsJPanelError.gridx = 0;
            constraintsJPanelLoginPanel.gridy = 3;
            constraintsJPanelError.fill = GridBagConstraints.BOTH;
            constraintsJPanelError.weightx = 1.0;
            constraintsJPanelError.weighty = 1.0;
            constraintsJPanelError.ipadx = -20;
            constraintsJPanelError.ipady = -36;
            constraintsJPanelError.insets = new Insets(6, 8, 6, 8);
            add(getJPanelError(), constraintsJPanelError);

            initConnections();
        } catch (Exception ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public boolean isInputValid() {
        String userName = getJTextFieldUserID().getText();
        if (StringUtils.isBlank(userName)) {
            setErrorString("The Username text field must be filled in.");
            return false;
        }

        LiteYukonUser liteYukonUser = YukonSpringHook.getBean(YukonUserDao.class).findUserByUsername(userName);

        // if we are trying to enter a username to one that already exists
        if (!userName.equals(initialUsername) && liteYukonUser != null) {
            setErrorString("Invalid username selection, please try a different username");
            getJLabelErrorMessage().setText("(" + getErrorString() + ")");
            getJLabelErrorMessage().setToolTipText("(" + getErrorString() + ")");
            getJLabelErrorMessage().setVisible(true);
            return false;
        }

        initialUsername = getJTextFieldUserID().getText();
        getJLabelErrorMessage().setText("");
        getJLabelErrorMessage().setToolTipText("");
        getJLabelErrorMessage().setVisible(false);
        return true;
    }

    public void jCheckBoxEnableEC_ActionPerformed(ActionEvent actionEvent) {

        getJComboBoxEnergyCompany().setEnabled(getJCheckBoxEnableEC().isSelected());

        if (getJCheckBoxEnableEC().isSelected()) {
            IDatabaseCache cache = DefaultDatabaseCache.getInstance();

            List<EnergyCompany> companies = new ArrayList<>(ecDao.getAllEnergyCompanies());
            Collections.sort(companies, new Comparator<EnergyCompany>() {
                @Override
                public int compare(EnergyCompany energyCompanyA, EnergyCompany energyCompanyB) {
                    return energyCompanyA.getName().compareToIgnoreCase(energyCompanyB.getName());
                }
            });

            getJComboBoxEnergyCompany().removeAllItems();
            for (EnergyCompany energyCompany : companies) {
                if (energyCompany.getId() != EnergyCompanyDao.DEFAULT_ENERGY_COMPANY_ID) {
                    getJComboBoxEnergyCompany().addItem(energyCompany);
                }
            }

        }

        fireInputUpdate();
    }

    public void jCheckBoxEnableLogin_ActionPerformed(ActionEvent actionEvent) {
        getJTextFieldUserID().setEnabled(getJCheckBoxEnableLogin().isSelected());
        getJButtonChangePassword().setEnabled(getJCheckBoxEnableLogin().isSelected());
        getJLabelUserName().setEnabled(getJCheckBoxEnableLogin().isSelected());

        fireInputUpdate();
    }

    /**
     * main entry point - starts the part when it is run as an application
     */
    public static void main(String[] args) {
        try {
            JFrame frame = new JFrame();
            UserLoginBasePanel aUserLoginBasePanel;
            aUserLoginBasePanel = new UserLoginBasePanel();
            frame.setContentPane(aUserLoginBasePanel);
            frame.setSize(aUserLoginBasePanel.getSize());
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                };
            });
            frame.setVisible(true);
            Insets insets = frame.getInsets();
            frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
            frame.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of DataInputPanel");
            exception.printStackTrace(System.out);
        }
    }

    @Override
    public void setValue(Object o) {
        if (o == null) {
            initialAuthenticationCategory = authenticationService.getDefaultAuthenticationCategory();

            getJListAuthType().setSelectedItem(initialAuthenticationCategory);
            boolean supportsPasswordSet =
                    authenticationService.supportsPasswordSet(initialAuthenticationCategory.getSupportingAuthType());
            passwordRequiresChanging = supportsPasswordSet;

            return;
        }

        YukonUser login = (YukonUser) o;

        if (!login.getYukonUser().getLoginStatus().isDisabled()) {
            getJCheckBoxEnableLogin().doClick();
        }

        UserAuthenticationInfo userAuthenticationInfo = yukonUserDao.getUserAuthenticationInfo(login.getUserID());
        getJTextFieldUserID().setText(login.getYukonUser().getUsername());
        initialUsername = getJTextFieldUserID().getText();
        initialAuthenticationCategory = userAuthenticationInfo.getAuthenticationCategory();
        getJListAuthType().setSelectedItem(initialAuthenticationCategory);
        boolean authSupportsPasswordSet = authenticationService.supportsPasswordSet(initialAuthenticationCategory);
        getJButtonChangePassword().setEnabled(authSupportsPasswordSet);

        if (ClientSession.getInstance().getUser().getUserID() == login.getYukonUser().getUserID().intValue()) {
            getJCheckBoxEnableLogin().setEnabled(false);
            getJCheckBoxEnableLogin().setToolTipText("You cannot disable the logged in user.");
        }

        if (login.getYukonUser().getUserGroupId() != null) {
            LiteUserGroup userGroup = userGroupDao.getLiteUserGroup(login.getYukonUser().getUserGroupId());
            getJListUserGroup().setSelectedItem(userGroup.getUserGroupName());
        }
        

        if (login.getUserID().intValue() == UserUtils.USER_ADMIN_ID) {
            getJTextFieldUserID().setEnabled(false);
            getJCheckBoxEnableLogin().setEnabled(false);
            getJCheckBoxEnableLogin().setSelected(true);
            boolean loggedInAsAdmin = ClientSession.getInstance().getUser().getUserID() == UserUtils.USER_ADMIN_ID;
            getJButtonChangePassword().setEnabled(loggedInAsAdmin);
        }

        long company = EnergyCompanyOperatorLoginList.getEnergyCompanyID(login.getUserID().longValue());

        if (company != -1) {
            getJCheckBoxEnableEC().doClick();
            for (int d = 0; d < getJComboBoxEnergyCompany().getModel().getSize(); d++) {
                EnergyCompany energyCompany = getJComboBoxEnergyCompany().getModel().getElementAt(d);
                if (energyCompany.getId() == company) {
                    getJComboBoxEnergyCompany().setSelectedItem(energyCompany);
                    oldEnergyCompanyID = company;
                    break;
                }
            }
        }
    }
}