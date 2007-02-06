package com.cannontech.common.gui.util;

 import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
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

import com.cannontech.common.util.CtiUtilities;

 public class SplashWindow extends Window 
 {
   private Image splashImage;   
   private int imgWidth, imgHeight;
   private int padding = 15;
  
   private FontMetrics fontMetrics;
   
   private URL imgURL;
   private int borderSize;
   private Color borderColor;
   Toolkit tk;
  
   private String displayText = " ";
   private Color textColor;

   public static SplashWindow createYukonSplash(Frame frame) {
       return new SplashWindow(frame,
                        CtiUtilities.CTISMALL_GIF,
                        "Loading " + CtiUtilities.getApplicationName() + "...",
                        new Font("dialog", Font.BOLD, 14 ), 
                        Color.black, 
                        Color.blue, 
                        2);
   }
   
	public SplashWindow(Frame f, String imgName, String displayText, Font displayFont, Color textColor, Color borderColor, int borderSize ) 
	{
		super( (f == null ? new JFrame() : f) );
		initialize( f, SplashWindow.class.getResource(imgName), displayText, displayFont,
                textColor, borderColor, borderSize ); 
	} 

    public SplashWindow(Frame f, URL imgURL_, String displayText, Font displayFont, Color textColor, Color borderColor, int borderSize ) 
    {
        super( (f == null ? new JFrame() : f) );
        initialize( f, imgURL_, displayText, displayFont, textColor, borderColor, borderSize ); 
    } 

	/**
	 * This method was created in VisualAge.
	 * @param f java.awt.Frame
	 * @param imgName java.lang.String
	 * @param borderColor java.awt.Color
	 * @param borderSize int
	 */
	private void initialize( Frame f, URL imgURL_, String displayText, Font displayFont, Color textColor, Color borderColor, int borderSize) {
		this.imgURL = imgURL_;
		this.textColor = textColor;
		this.borderColor = borderColor;
		this.borderSize = borderSize;
		
		tk = Toolkit.getDefaultToolkit();
		 splashImage = loadSplashImage();
	
		 setFont( displayFont );
		 fontMetrics = getFontMetrics(displayFont);
	
		 this.displayText = displayText;
		 
		 showSplashScreen();
	
		 // Close the splash window when the parent frame is opened
		 if( f != null )
			 f.addWindowListener( new WindowAdapter()
		 	 {
				public void windowOpened(WindowEvent we )
				{
					setVisible(false);
					dispose();
				}
			 });
	}

   public Image loadSplashImage() {
	 MediaTracker tracker = new MediaTracker(this);
	 Image result;
	 result = tk.getImage( imgURL );
	 tracker.addImage(result, 0);
	 try { 
	   tracker.waitForAll(); 
	   }
	 catch (Exception e) {
	   com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	   }
	 imgWidth = result.getWidth(this);
	 imgHeight = result.getHeight(this);
	 return (result);
	 } 

	/**
	 * Insert the method's description here.
	 * Creation date: (3/17/00 3:28:12 PM)
	 * @param args java.lang.String[]
	 */
	public static void main(String[] args) 
	{
		try
		{
			javax.swing.JFrame f = new javax.swing.JFrame();
			f.setSize(100,200);		
			
			com.cannontech.common.gui.util.SplashWindow splash = new com.cannontech.common.gui.util.SplashWindow(f, CtiUtilities.CTISMALL_GIF, "Hi, I'm loading...", new java.awt.Font("dialog", 0, 16), Color.black,  Color.black, 1  );
		
			Thread.sleep(3000);
			splash.setDisplayText("Hi, I'm still loading...");
	
			Thread.sleep(3000);
			splash.setDisplayText("Geez, I'm still loading...");
	
			Thread.sleep(4000);
			splash.setDisplayText("Geez, this must be a java application...");
	
			Thread.sleep(5000);
			splash.setDisplayText("Almost done.....");
	
			Thread.sleep(3000);		
			splash.setDisplayText("Just hang on.");
	
			Thread.sleep(500);
			splash.setDisplayText("Just hang on....");
	
			Thread.sleep(500);
			splash.setDisplayText("Just hang on......");
	
			Thread.sleep(500);
			splash.setDisplayText("Just hang on........");
	
			Thread.sleep(500);
			splash.setDisplayText("Just hang on..........");
	
			Thread.sleep(500);
			splash.setDisplayText("Just hang on............");
	
			Thread.sleep(500);
			splash.setDisplayText("Just hang on..............");
	
			Thread.sleep(500);
			splash.setDisplayText("Just hang on................");
			
			Thread.sleep(1000);
			
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
	    g.drawImage(splashImage, borderSize + padding, borderSize + padding,
	                imgWidth, imgHeight, this);

	    g.setColor(textColor);
	    g.drawString(displayText, (getWidth() / 2) - (fontMetrics.stringWidth(displayText)/2),
	                 imgHeight + borderSize + 2*padding + fontMetrics.getHeight() );
	}
 
	/**
	 * Insert the method's description here.
	 * Creation date: (3/17/00 3:27:22 PM)
	 * @param newDisplayText java.lang.String
	 */
	public void setDisplayText(java.lang.String newDisplayText) {
		displayText = newDisplayText;
		repaint();
	}

   public void showSplashScreen() {
	 Dimension screenSize = tk.getScreenSize();
	 setBackground(borderColor);

	 int stringHeight = fontMetrics.getHeight();
	 
	 int w = imgWidth + (borderSize * 2) + (padding * 2);
	 int h = imgHeight + (borderSize * 2) + (padding * 2) + (int) (stringHeight * 1.5 );
	 int x = (screenSize.width - w) /2;
	 int y = (screenSize.height - h) /2;
	 setBounds(x, y, w, h);
	 setVisible(true);
	 } 
}
