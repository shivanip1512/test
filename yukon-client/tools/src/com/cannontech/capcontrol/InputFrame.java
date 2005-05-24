/*
 * Created on May 24, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.capcontrol;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.ScrollPane;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;


import javax.swing.border.EtchedBorder;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.cannontech.database.data.capcontrol.CapBank;
/**
 * @author ASolberg
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class InputFrame extends JFrame implements ActionListener, Runnable
{
	
	private JPanel contentPane;
	private TitledBorder titledBorder;
	private JLabel serialRangeLabel = new JLabel("* Serial # Range:");
	private JLabel serialToLabel = new JLabel( "to" );
	private JTextField serialFromTF = new JTextField("");
	private JTextField serialToTF = new JTextField("");
	//private JButton checkButton = new JButton("Check");
	private JButton submitButton = new JButton("Submit");
	private JLabel routeLabel = new JLabel("* Route:");
	private JLabel bankSizeLabel = new JLabel("Bank Size:");
	private JLabel switchManLabel = new JLabel("Switch Manufacture:");
	private JLabel switchTypeLabel = new JLabel("Type of Switch:");
	private JLabel switchConTypeLabel = new JLabel("Controller Type:");
	private JLabel requiredLabel = new JLabel("* indicates required fields");
	private JComboBox routeCB = new JComboBox();
	private JComboBox bankSizeCB = new JComboBox();
	private JComboBox switchManCB = new JComboBox();
	private JComboBox switchTypeCB = new JComboBox();
	private JComboBox switchConTypeCB = new JComboBox();
	private JProgressBar bar;
	private InputValidater val;
	private volatile Thread valThread;
	private Timer timer;
	private int progress = 0;
	private final int ONE_SECOND = 1000;
	public InputFrame()
	{
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);

		try
		{
			init();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void init(){
		contentPane = (JPanel) this.getContentPane();
		titledBorder = new TitledBorder("");
		contentPane.setLayout(null);
		this.setSize(new Dimension(290, 350));
		this.setTitle("CapControl Importer");
		
		serialRangeLabel.setBounds(new Rectangle(10, 10, 90, 20));
		serialFromTF.setBounds(new Rectangle(100,10,60,20));
		serialToLabel.setBounds(new Rectangle(170,10,20,20));
		serialToTF.setBounds(new Rectangle(190,10,60,20));
		routeLabel.setBounds(new Rectangle(10,40,50,20));
		routeCB.setBounds(new Rectangle(70,40,120,20));
		bankSizeLabel.setBounds(new Rectangle(10,70,60,20));
		bankSizeCB.setBounds( new Rectangle(80,70,120,20));
		switchManLabel.setBounds(new Rectangle(10,100,110,20));
		switchManCB.setBounds(new Rectangle(130,100,120,20));
		switchTypeLabel.setBounds(new Rectangle(10,130,90,20));
		switchTypeCB.setBounds(new Rectangle(110,130,120,20));
		switchConTypeLabel.setBounds(new Rectangle(10,160,90,20));
		switchConTypeCB.setBounds(new Rectangle(110,160,120,20));
		//checkButton.setBounds(new Rectangle(60,190,70,25));
		submitButton.setBounds(new Rectangle(100,190,70,25));
		requiredLabel.setBounds(new Rectangle(60,255,130,20));
		bar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
		bar.setValue(0);
		bar.setStringPainted(true);
		bar.setBounds(new Rectangle(35,225,200,20));
		bar.setVisible(false);
		//checkButton.addActionListener(this);
		submitButton.addActionListener(this);
		
		switchConTypeCB.addItem( com.cannontech.common.util.CtiUtilities.STRING_NONE );
		switchConTypeCB.addItem( CapBank.CONTROL_TYPE_DLC );
		switchConTypeCB.addItem( CapBank.CONTROL_TYPE_PAGING );
		switchConTypeCB.addItem( CapBank.CONTROL_TYPE_FM );
		switchConTypeCB.addItem( CapBank.CONTROL_TYPE_FP_PAGING );
		
		switchManCB.addItem( com.cannontech.common.util.CtiUtilities.STRING_NONE );
		switchManCB.addItem( CapBank.SWITCHMAN_WESTING );
		switchManCB.addItem( CapBank.SWITCHMAN_ABB );
		switchManCB.addItem( CapBank.SWITCHMAN_COOPER );
		switchManCB.addItem( CapBank.SWITCHMAN_SIEMENS );
		switchManCB.addItem( CapBank.SWITCHMAN_TRINETICS );
		
		switchTypeCB.addItem( com.cannontech.common.util.CtiUtilities.STRING_NONE );
		switchTypeCB.addItem( CapBank.SWITCHTYPE_OIL );
		switchTypeCB.addItem( CapBank.SWITCHTYPE_VACUUM );
		
		bankSizeCB.addItem( new Integer(50) );
		bankSizeCB.addItem( new Integer(100) );
		bankSizeCB.addItem( new Integer(150) );
		bankSizeCB.addItem( new Integer(275) );
		bankSizeCB.addItem( new Integer(300) );
		bankSizeCB.addItem( new Integer(450) );
		bankSizeCB.addItem( new Integer(550) );         
		bankSizeCB.addItem( new Integer(600) );
		bankSizeCB.addItem( new Integer(825) );         
		bankSizeCB.addItem( new Integer(900) );
		bankSizeCB.addItem( new Integer(1100) );         
		bankSizeCB.addItem( new Integer(1200) );
		
//		routeCB.removeAllItems();
//		com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
//		synchronized(cache)
//		{         
////			java.util.List list = 
////			(cbcType == DeviceTypes.DNP_CBC_6510)
////			? cache.getAllPorts()
////			: cache.getAllRoutes();
//			java.util.List list = cache.getAllRoutes();
//
//		for( int i = 0; i < list.size(); i++ )
//		routeCB.addItem( list.get(i) );
//		}
		
//		serialRangeLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
//		serialFromTF.setBorder(new EtchedBorder(EtchedBorder.RAISED));
//		serialToLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
//		serialToTF.setBorder(new EtchedBorder(EtchedBorder.RAISED));
//		routeLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
//		routeCB.setBorder(new EtchedBorder(EtchedBorder.RAISED));
//		bankSizeLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
//		bankSizeCB.setBorder(new EtchedBorder(EtchedBorder.RAISED));
//		switchManLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
//		switchManCB.setBorder(new EtchedBorder(EtchedBorder.RAISED));
//		switchTypeLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
//		switchTypeCB.setBorder(new EtchedBorder(EtchedBorder.RAISED));
//		switchConTypeLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
//		switchConTypeCB.setBorder(new EtchedBorder(EtchedBorder.RAISED));
//		bar.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		
		contentPane.add(serialRangeLabel);
		contentPane.add(serialFromTF);
		contentPane.add(serialToLabel);
		contentPane.add(serialToTF);
		contentPane.add(routeLabel);
		contentPane.add(routeCB);
		contentPane.add(bankSizeLabel);
		contentPane.add(bankSizeCB);
		contentPane.add(switchManLabel);
		contentPane.add(switchManCB);
		contentPane.add(switchTypeLabel);
		contentPane.add(switchTypeCB);
		contentPane.add(switchConTypeLabel);
		contentPane.add(switchConTypeCB);
		//contentPane.add(checkButton);
		contentPane.add(submitButton);
		contentPane.add(requiredLabel);
		contentPane.add(bar);
		//Create a timer.
		timer = new Timer(ONE_SECOND, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				bar.setValue(progress);
				  
				if (progress == 100) {
					Toolkit.getDefaultToolkit().beep();
					timer.stop();
					//bar.setValue(bar.getMinimum());
				}
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		Object obj = e.getSource();
		if( obj == submitButton )
		{
			// check data with our imput validater
			bar.setVisible(true);
			progress = 0;
			bar.setValue(bar.getMinimum());
			String fromS = serialFromTF.getText();
			Integer from = new Integer(fromS);
			String toS = serialToTF.getText();
			Integer to = new Integer(toS);
			String route = (String) routeCB.getSelectedItem();
			String banksize = (String) bankSizeCB.getSelectedItem().toString();
			String manufacturer = (String) switchManCB.getSelectedItem();
			String type = (String) switchTypeCB.getSelectedItem();
			String conType = (String) switchConTypeCB.getSelectedItem();
			timer.start();
			val = new InputValidater(from.intValue(), to.intValue(), route, banksize, manufacturer, type, conType);
			valThread = new Thread(this,"Validation Thread");
			valThread.start();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		try
		{
			boolean rangeOK = val.checkRange();
			Thread.sleep(2000);
			progress += 17;
			boolean routeOK = val.checkRoute();
			Thread.sleep(2000);
			progress += 17;
			boolean bankOK = val.checkBankSize();
			Thread.sleep(2000);
			progress += 17;
			boolean switchManOK = val.checkManufacturer();
			Thread.sleep(2000);
			progress += 17;
			boolean switchTypeOK = val.checkSwitchType();
			Thread.sleep(2000);
			progress += 17;
			boolean switchConTypeOK = val.checkConType();
			Thread.sleep(2000);
			progress = 100;
			
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
	}
}