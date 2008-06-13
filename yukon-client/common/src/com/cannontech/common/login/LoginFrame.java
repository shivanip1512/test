package com.cannontech.common.login;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CountDownLatch;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Frame which presents login information
 */
public class LoginFrame extends JFrame {

    private boolean login = false;

    public LoginFrame(Frame parent, final CountDownLatch latch, LoginPanel loginPanel) {

        this.setLayout(new GridBagLayout());
        this.setResizable(false);
        this.setLocationByPlatform(true);
        this.setTitle("Yukon Login");
        if (parent != null) {
            this.setIconImage(parent.getIconImage());
        }

        this.add(loginPanel, new GridBagConstraints(1,
                                                    1,
                                                    1,
                                                    1,
                                                    1.0,
                                                    1.0,
                                                    GridBagConstraints.CENTER,
                                                    GridBagConstraints.NONE,
                                                    new Insets(10, 10, 10, 10),
                                                    0,
                                                    0));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton loginButton = new JButton("Login");
        buttonPanel.add(loginButton);

        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(cancelButton);

        this.add(buttonPanel, new GridBagConstraints(1,
                                                     2,
                                                     1,
                                                     1,
                                                     1.0,
                                                     0.0,
                                                     GridBagConstraints.CENTER,
                                                     GridBagConstraints.NONE,
                                                     new Insets(0, 10, 10, 10),
                                                     0,
                                                     0));

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setLogin(true);
                dispose();
                latch.countDown();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setLogin(false);
                dispose();
                latch.countDown();
            }
        });

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                latch.countDown();
            }
        });

        this.getRootPane().setDefaultButton(loginButton);
        this.pack();

        // Center login frame on screen
        Dimension dimension = getToolkit().getScreenSize();
        Rectangle bounds = getBounds();
        setLocation((dimension.width - bounds.width) / 2, (dimension.height - bounds.height) / 2);

        this.setVisible(true);

    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public boolean getLogin() {
        return this.login;
    }

    /**
     * Method to display a Yukon Login frame. This method will not return until
     * the user to attempts to login, closes the frame, or cancels
     * @param parent - parent frame for the login frame
     * @param loginPanel - Panel used for login input
     * @return True if the user attempts to login
     */
    public static boolean showLogin(Frame parent, LoginPanel loginPanel) {
        TextSelector.install();
        LoginFrame frame = null;
        CountDownLatch latch = new CountDownLatch(1);
        frame = new LoginFrame(parent, latch, loginPanel);

        try {
            // Wait for the user to attempt to login or cancel/close frame
            latch.await();
        } catch (InterruptedException e) {
            return false;
        }
        boolean result = frame.getLogin();
        TextSelector.uninstall();
        return result;
    }

}
