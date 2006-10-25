package com.cannontech.common.login;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.concurrent.CountDownLatch;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Frame which presents login information
 */
public class LoginFrame extends JFrame {

    private boolean login = false;

    public LoginFrame(Image image, final CountDownLatch latch, LoginPanel loginPanel) {

        this.setLayout(new GridBagLayout());
        this.setResizable(false);
        this.setLocationByPlatform(true);
        this.setTitle("Yukon Login");
        this.setIconImage(image);

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

        this.addWindowListener(new WindowListener() {

            public void windowOpened(WindowEvent e) {
            }

            public void windowClosing(WindowEvent e) {
                latch.countDown();
            }

            public void windowClosed(WindowEvent e) {
            }

            public void windowIconified(WindowEvent e) {
            }

            public void windowDeiconified(WindowEvent e) {
            }

            public void windowActivated(WindowEvent e) {
            }

            public void windowDeactivated(WindowEvent e) {
            }
        });

        this.pack();
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
     * @param image - Image which will be used as the frame's icon
     * @param loginPanel - Panel used for login input
     * @return True if the user attempts to login
     */
    public static boolean showLogin(Image image, LoginPanel loginPanel) {

        LoginFrame frame = null;
        CountDownLatch latch = new CountDownLatch(1);
        frame = new LoginFrame(image, latch, loginPanel);

        try {
            // Wait for the user to attempt to login or cancel/close frame
            latch.await();
        } catch (InterruptedException e) {
            return false;
        }
        return frame.getLogin();
    }

}
