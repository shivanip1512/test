package com.cannontech.dbeditor.editor.user;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.cannontech.clientutils.CTILogger;



public class ChangePasswordDialog extends JDialog {
    private String newPassword;
    private JPasswordField passwordField;
    private JPasswordField repeatPasswordField;
    private JLabel passwordLabel;
    private JLabel repeatPasswordLabel;
    private JButton okButton;
    private JButton cancelButton;
    private JLabel messageLabel;
    
    public static ChangePasswordDialog create(Component parent, String message) {
        ChangePasswordDialog dialog;
        Window window = JOptionPane.getFrameForComponent(parent);
        if (window instanceof Frame) {
            dialog = new ChangePasswordDialog((Frame)window, message);   
        } else {
            dialog = new ChangePasswordDialog((Dialog)window, message);
        }
        dialog.setLocationRelativeTo(parent);
        return dialog;
    }
    
    
    public ChangePasswordDialog(Frame window, String message) {
        super(window, "Enter new password", true);
        initialize(message);
        
    }
    
    public ChangePasswordDialog(Dialog window, String message) {
        super(window, "Enter new password", true);
        initialize(message);
        
    }

    private void initialize(String message) {
        setSize(300,175);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeAndCancel();
            }
        });
        
        setLayout(new GridBagLayout());
        
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        Insets spaceOnLeft = new Insets(0, 20, 0, 5);
        Insets spaceOnRight = new Insets(0, 5, 0, 20);
        
        messageLabel = new JLabel(message + ":");
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(5, 20, 5, 20);
        add(messageLabel,constraints);
        
        passwordLabel = new JLabel("New Password:");
        constraints.gridx = 0; 
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weighty = .2f;
        constraints.insets = spaceOnLeft;
        constraints.anchor = java.awt.GridBagConstraints.EAST;
        add(passwordLabel, constraints);

        passwordField = new JPasswordField();
        constraints.gridx = 1; 
        constraints.gridy = 1;
        constraints.weightx = 1f;
        constraints.insets = spaceOnRight;
        constraints.anchor = java.awt.GridBagConstraints.WEST;
        add(passwordField, constraints);
        
        repeatPasswordLabel = new JLabel("Retype Password:");
        constraints.gridx = 0; 
        constraints.gridy = 2;
        constraints.weightx = 0;
        constraints.weighty = .2f;
        constraints.insets = spaceOnLeft;
        constraints.anchor = java.awt.GridBagConstraints.EAST;
        add(repeatPasswordLabel, constraints);
        
        repeatPasswordField = new JPasswordField();
        constraints.gridx = 1; 
        constraints.gridy = 2;
        constraints.insets = spaceOnRight;
        constraints.anchor = java.awt.GridBagConstraints.WEST;
        add(repeatPasswordField, constraints);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints panelConsts = new GridBagConstraints();
        panelConsts.weightx = .5f;
        
        cancelButton = new JButton("Cancel");
        panelConsts.anchor = GridBagConstraints.EAST;
        panelConsts.insets = spaceOnRight;
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                closeAndCancel();
            }
        });
        buttonPanel.add(cancelButton, panelConsts);
        
        okButton = new JButton("OK");
        panelConsts.anchor = GridBagConstraints.WEST;
        panelConsts.insets = spaceOnLeft;
        final JDialog that = this;
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                char[] password = passwordField.getPassword();
                char[] password2 = repeatPasswordField.getPassword();
                
                boolean match = Arrays.equals(password, password2);
                if (match) {
                    closeAndSave();
                } else {
                    JOptionPane.showMessageDialog(that, "Passwords did not match!");
                    passwordField.setText("");
                    repeatPasswordField.setText("");
                }
            }
        });
        okButton.setDefaultCapable(true);
        getRootPane().setDefaultButton(okButton);
        buttonPanel.add(okButton, panelConsts);
        
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        constraints.weighty = .6;
        add(buttonPanel, constraints);
    }
    
    
    /**
     * @return the new password entered by user
     */
    public String getNewPassword() {
        return newPassword;
    }
    
    public void closeAndSave() {
        newPassword = new String(passwordField.getPassword());
        setVisible(false);
    }
    
    public void closeAndCancel() {
        newPassword = null;
        setVisible(false);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            CTILogger.error(e);
        } catch (InstantiationException e) {
            CTILogger.error(e);
        } catch (IllegalAccessException e) {
            CTILogger.error(e);
        } catch (UnsupportedLookAndFeelException e) {
            CTILogger.error(e);
        }
        ChangePasswordDialog dialog = ChangePasswordDialog.create(null, "Please enter a new password");
        dialog.setVisible(true);
        System.out.println(dialog.getNewPassword());
        System.exit(0);
    }
}
