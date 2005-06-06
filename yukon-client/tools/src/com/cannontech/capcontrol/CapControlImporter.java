/*
 * Created on May 24, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.capcontrol;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.UIManager;
/**
 * @author ASolberg
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CapControlImporter
{

	private boolean packFrame = false;
	
	public CapControlImporter()
	{
		InputFrame frame = new InputFrame();
		
		if (packFrame)
		{
			frame.pack();
		} else
		{
			frame.validate();
		}
		
		// Center the window
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = frame.getSize();

		if (frameSize.height > screenSize.height)
		{
			frameSize.height = screenSize.height;
		}

		if (frameSize.width > screenSize.width)
		{
			frameSize.width = screenSize.width;
		}

		frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		//frame.setVisible(true);
		frame.show();
	}
	
	public static void main(String[] args)
	{
		
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		if (args.length == 0)
		{
			new CapControlImporter();
		}else
		{
			new InputFrame(args);
		}
		
	}
}