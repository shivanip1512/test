package com.cannontech.common.gui.util;

 import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

 public class SplashWindow extends Window 
 {
   private Image splashImage;   
   private int imgWidth, imgHeight;
  
   private FontMetrics fontMetrics;
   
   private String imgName;
   private int borderSize;
   private Color borderColor;
   Toolkit tk;
  
   private String displayText = " ";
   private Color textColor;

   public SplashWindow(Frame f, String imgName  ) 
   {
		super( (f == null ? new JFrame() : f) );
		initialize( f, imgName, "Loading...", new java.awt.Font("dialog", 0, 12), Color.black, Color.black, 1  );
   }
   
	public SplashWindow(Frame f, String imgName, String displayText, Font displayFont, Color textColor, Color borderColor, int borderSize ) 
	{
		super( (f == null ? new JFrame() : f) );
		initialize( f, imgName, displayText, displayFont, textColor, borderColor, borderSize ); 
	} 

   public SplashWindow(Frame f, String imgName, String displayText )
   {
		super( (f == null ? new JFrame() : f) );
		initialize( f, imgName, displayText, new java.awt.Font("dialog", 0, 12), Color.black, Color.black, 1  );			 
	 } 

	/**
	 * This method was created in VisualAge.
	 * @param f java.awt.Frame
	 * @param imgName java.lang.String
	 * @param borderColor java.awt.Color
	 * @param borderSize int
	 */
	private void initialize( Frame f, String imgName, String displayText, Font displayFont, Color textColor, Color borderColor, int borderSize) {
		this.imgName = imgName;
		this.textColor = textColor;
		this.borderColor = borderColor;
		this.borderSize = borderSize;
		
		tk = Toolkit.getDefaultToolkit();
		 splashImage = loadSplashImage();
	
		 setFont( displayFont );
		 fontMetrics = getFontMetrics(displayFont);
	
		 this.displayText = displayText;
		 
		 showSplashScreen();
	
		 if( f != null )
			 f.addWindowListener( new WindowAdapter()
		 	 {
			 	 public void windowActivated(WindowEvent we )
			 	 {
				 	 setVisible(false);
				 	 dispose();
			 	 }
			 });
	}

   public Image loadSplashImage() {
	 MediaTracker tracker = new MediaTracker(this);
	 Image result;
	 result = tk.getImage(imgName);
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
			
			com.cannontech.common.gui.util.SplashWindow splash = new com.cannontech.common.gui.util.SplashWindow(f, "ctismall.gif", "Hi, I'm loading...", new java.awt.Font("dialog", 0, 16), Color.black,  Color.black, 1  );
		
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
	 g.drawImage(splashImage, borderSize, borderSize,
	   imgWidth, imgHeight, this);

	 g.setColor(Color.white);
	 g.fillRect( borderSize, imgHeight+(borderSize),
		 imgWidth, getHeight() - ( 2 * borderSize + imgHeight));

	 g.setColor(textColor);
	 g.drawString(displayText, (getWidth() / 2) - (fontMetrics.stringWidth(displayText)/2),
		 				       imgHeight+(borderSize) + fontMetrics.getHeight() );
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
	 
	 int w = imgWidth + (borderSize * 2);
	 int h = imgHeight + (borderSize * 2) + (int) (stringHeight * 1.5 );
	 int x = (screenSize.width - w) /2;
	 int y = (screenSize.height - h) /2;
	 setBounds(x, y, w, h);
	 setVisible(true);
	 } 
}
