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
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.capcontrol.CapBank;
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
			System.out.println();
			log("Argument Syntax:");
			System.out.println();
			log("Commandline arguments should be enclosed in quote marks.");
			log("For command line: \"from=000000001\" \"to=000000050\" \"route=route1\" \"contype=CBC FP-2800\" \"banksize=1200\" \"manufacturer=Westinghouse\" \"switchtype=oil\"");
			System.out.println();
			log("For gui: gui");
			System.out.println();
			log("Controller Types: " + com.cannontech.database.data.pao.PAOGroups.STRING_CBC_FP_2800[0] + ", " + 
				com.cannontech.database.data.pao.PAOGroups.STRING_CAP_BANK_CONTROLLER[0] + ", " +
				com.cannontech.database.data.pao.PAOGroups.STRING_DNP_CBC_6510[0] + ", " +
				com.cannontech.database.data.pao.PAOGroups.STRING_CBC_EXPRESSCOM[0] + ", " +
				com.cannontech.database.data.pao.PAOGroups.STRING_CBC_7010[0]);
			System.out.println();
			log("Manufacturers: " + CapBank.SWITCHMAN_WESTING + ", " +
				CapBank.SWITCHMAN_ABB + ", " +
				CapBank.SWITCHMAN_COOPER + ", " +
				CapBank.SWITCHMAN_SIEMENS + ", " +
				CapBank.SWITCHMAN_TRINETICS);
			System.out.println();
			log("Switch Types: " + CapBank.SWITCHTYPE_OIL + ", " +
				CapBank.SWITCHTYPE_VACUUM);
			System.out.println();
			
		}else 
		{
			boolean command = true;
			for(int i = 0; i < args.length; i++)
			{
				if(args[i].equalsIgnoreCase("gui"))
				{
					command = false;
				}
			}
			
			if(command)
			{
				new InputFrame(args);
			}else new CapControlImporter();
			
		}
	}
	private static void log(String msg)
	{
		System.out.println(msg);
	}
}