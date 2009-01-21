package com.cannontech.common.gui.util;

 import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.JFrame;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;

 public class SplashWindow extends Window 
 {
	private Image splashImage;   
	private int imgWidth = 0, imgHeight = 0;
	private URL imgURL;
	Toolkit tk;
	private int borderSize = 2;

	public static SplashWindow createYukonSplash(Frame frame, URL imgURL) {
		return new SplashWindow(frame, imgURL);
	}
	
	public static SplashWindow createYukonSplash(Frame frame) {
		return createYukonSplash(frame, CtiUtilities.GENERIC_APPLICATION_SPLASH);
	}
   
    public SplashWindow(Frame f, URL imgURL_) {
        super( (f == null ? new JFrame() : f) );
        initialize( f, imgURL_); 
    } 
    
	/**
	 * This method was created in VisualAge.
	 * @param f java.awt.Frame
	 * @param imgName java.lang.String
	 * @param borderColor java.awt.Color
	 * @param borderSize int
	 */
	private void initialize( Frame f, URL imgURL_) {
		this.imgURL = imgURL_;
		
		tk = Toolkit.getDefaultToolkit();
		splashImage = loadSplashImage();

        if (!javax.swing.SwingUtilities.isEventDispatchThread()) {
        	javax.swing.SwingUtilities.invokeLater(new Runnable() {
        		public void run() {
        			showSplashScreen();
        		}
        	});
        } else {
        	showSplashScreen();
        }
	
		// Close the splash window when the parent frame is opened
        if( f != null ) {
        	f.addWindowListener( new WindowAdapter() {
        		public void windowOpened(WindowEvent we ){
        			setVisible(false);
        			dispose();
				}
        	});
        }
	}

	public Image loadSplashImage() {
		MediaTracker tracker = new MediaTracker(this);
		Image result;
		result = tk.getImage( imgURL);
		tracker.addImage(result, 0);
		try {
			tracker.waitForAll();
			
		} catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
		imgWidth = result.getWidth(this);
		imgHeight = result.getHeight(this);
		return (result);
	} 

	public static void main(String[] args) 
	{
		try {
			javax.swing.JFrame f = new javax.swing.JFrame();
			f.setSize(100,200);		
			
			SplashWindow splash = new SplashWindow(f, CtiUtilities.GENERIC_APPLICATION_SPLASH);
		

			f.setVisible(true);
		}
		catch(Exception e )
		{
		}	
	}

	public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	    g.setColor(Color.white);
	    g.fillRect(borderSize, borderSize, getWidth() - (2 * borderSize), getHeight() - ( 2 * borderSize));
	    g.drawImage(splashImage, borderSize, borderSize, imgWidth, imgHeight, this);
	}
 
	public void showSplashScreen() {
	   Dimension screenSize = tk.getScreenSize();
	   setBackground(Color.BLACK);
	   
	   int w = imgWidth + (borderSize * 2);
	   int h = imgHeight + (borderSize * 2);
	   int x = (screenSize.width - w) /2;
	   int y = (screenSize.height - h) /2;
	   setBounds(x, y, w, h);
	   setVisible(true);
	} 
}
