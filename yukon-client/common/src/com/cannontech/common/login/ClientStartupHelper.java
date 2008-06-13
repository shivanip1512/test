package com.cannontech.common.login;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.SplashWindow;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.spring.YukonSpringHook;

public class ClientStartupHelper {
    private String appName;
    private JFrame parentFrame = null; // can be null initially
    private int requiredRole;
    private SplashWindow splash;

    public void doStartup() throws Exception {
        CTILogger.debug("starting doStartup");
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                CTILogger.error("Uncaught thread exception", e);
                JOptionPane.showMessageDialog(parentFrame, "An unexpected error has occurred: \n"
                                              + CtiUtilities.getRootCause(e),
                                              "Uncaught Exception", JOptionPane.ERROR_MESSAGE);               
            }
            
        });
        
        splash = SplashWindow.createYukonSplash(parentFrame);
        ClientSession session = ClientSession.getInstance(); 
        boolean loggingIn = true;
        while(loggingIn){
            if(!session.establishSession(parentFrame)) {
                System.exit(-1);          
            }
            CTILogger.debug("successfully established session");
            if(!session.checkRole(requiredRole)) {
                JOptionPane.showMessageDialog(parentFrame, 
                                              "User: '" + session.getUser().getUsername() + "' is not authorized to use this application. Please log in as a different user.", 
                                              "Access Denied", 
                                              JOptionPane.WARNING_MESSAGE);               
                CTILogger.debug("user failed role check");
            } else {
                loggingIn = false;
            }
        }
        CTILogger.debug("doStartup complete for " + appName);
        
    }

    private void restoreFrameSize() {
        // setup size
        Preferences prefs = Preferences.userNodeForPackage(parentFrame.getClass());

        // calc default size
        float percent = .85f;
        Rectangle defaultSize = getSimpleWindowBounds(percent);

        int lastX = prefs.getInt("LAST_X", defaultSize.x);
        int lastY = prefs.getInt("LAST_Y", defaultSize.y);
        int lastWidth = prefs.getInt("LAST_WIDTH", defaultSize.width);
        int lastHeight = prefs.getInt("LAST_HEIGHT", defaultSize.height);

        parentFrame.setBounds(lastX,
                              lastY,
                              lastWidth,
                              lastHeight);
    }

    private Rectangle getSimpleWindowBounds(float percent) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle defaultSize = new Rectangle();
        defaultSize.setSize( (int) (screenSize.width * percent), (int)( screenSize.height * percent) );

        //set the location of the frame to the center of the screen
        defaultSize.setLocation( (screenSize.width - defaultSize.getSize().width) / 2,
                       (screenSize.height - defaultSize.getSize().height) / 2);
        return defaultSize;
    }
    
    private static void saveFrameSize(JFrame frame) {
        Preferences prefs = Preferences.userNodeForPackage(frame.getClass());
        int x = frame.getX();
        prefs.putInt("LAST_X", x);
        prefs.putInt("LAST_Y", frame.getY());
        prefs.putInt("LAST_WIDTH", frame.getWidth());
        prefs.putInt("LAST_HEIGHT", frame.getHeight());
        
    }
    
    public void hideSplash() {
        if (splash != null) {
            splash.setVisible( false );
            splash.dispose();
        }
    }

    public void setAppName(String appName) {
        this.appName = appName;
        System.setProperty("cti.app.name", appName);
        CTILogger.info(appName + " starting...");
    }

    public void setParentFrame(final JFrame parentFrame) {
        this.parentFrame = parentFrame;
        if (parentFrame != null) {
            restoreFrameSize();
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                public void run() {
                    saveFrameSize(parentFrame);
                }
            }));

            hideSplash();
        }

    }

    public void setRequiredRole(int requiredRole) {
        this.requiredRole = requiredRole;
    }

    public void setContext(String context) {
        YukonSpringHook.setDefaultContext(context);
    }
}
