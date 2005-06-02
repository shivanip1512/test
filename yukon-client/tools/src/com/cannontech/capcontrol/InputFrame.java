/*
 * Created on May 24, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.capcontrol;
import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;
import java.io.*;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.capcontrol.ICapBankController;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.message.dispatch.message.DBChangeMsg;

/**
 * @author ASolberg
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class InputFrame extends JFrame implements ActionListener, Runnable, Observer
{

	private JPanel contentPane;
	private TitledBorder titledBorder;
	private JLabel serialRangeLabel = new JLabel("* Serial # Range:");
	private JLabel serialToLabel = new JLabel("to");
	private JTextField serialFromTF;
	private JTextField serialToTF;
	private JButton submitButton = new JButton("Submit");
	private JLabel routeLabel = new JLabel("* Route:");
	private JLabel bankSizeLabel = new JLabel("Bank Size:");
	private JLabel switchManLabel = new JLabel("Switch Manufacture:");
	private JLabel switchTypeLabel = new JLabel("Type of Switch:");
	private JLabel conTypeLabel = new JLabel("* CBC Type:");
	private JLabel requiredLabel = new JLabel("* indicates required fields");
	private JComboBox routeCB = new JComboBox();
	private JComboBox bankSizeCB = new JComboBox();
	private JComboBox switchManCB = new JComboBox();
	private JComboBox switchTypeCB = new JComboBox();
	private JComboBox conTypeCB = new JComboBox();
	private JProgressBar bar;
	private InputValidater val;
	private volatile Thread progressThread;
	private Timer timer;
	private double progress = 0;
	private final int ONE_SECOND = 1000;
	private boolean validating = false;
	private ActionNotifier notifier = new ActionNotifier(this);
	private static DecimalFormat SERIAL_FORM = new DecimalFormat("000000000");
	private static DecimalFormat DBL_FORM = new DecimalFormat("#.00");
	private BufferedReader input;
	


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
	
	public InputFrame(String command)
	{
		clInit();
	}
	
	private void clInit()
	{
		input = new BufferedReader(new InputStreamReader(System.in));
		try
		{
			System.out.println("Enter nine digit starting serial number.( series 7000 CBC's require a s/n starting with '7')");
			System.out.println("");
			System.out.print("Serial Range From: ");
			String from = input.readLine();
			System.out.print("Serial Range To: ");
			String to = input.readLine();
			System.out.print("Route: ");
			String route = input.readLine();
			System.out.println("");
			System.out.println("CBC Types: " +
				com.cannontech.database.data.pao.PAOGroups.STRING_CBC_FP_2800[0] + ", " +
				com.cannontech.database.data.pao.PAOGroups.STRING_CAP_BANK_CONTROLLER[0] + ", " +
				com.cannontech.database.data.pao.PAOGroups.STRING_DNP_CBC_6510[0] + ", " +
				com.cannontech.database.data.pao.PAOGroups.STRING_CBC_EXPRESSCOM[0] + ", " +
				com.cannontech.database.data.pao.PAOGroups.STRING_CBC_7010[0]);
				
			System.out.print("CBC Type: ");
			String capcntrlType = input.readLine();
			System.out.print("Bank Size: ");
			String banksize = input.readLine();
			System.out.println("");
			System.out.println("Manufacturers: " + com.cannontech.common.util.CtiUtilities.STRING_NONE + ", " +
				CapBank.SWITCHMAN_WESTING + ", " +
				CapBank.SWITCHMAN_ABB + ", " +
				CapBank.SWITCHMAN_COOPER + ", " +
				CapBank.SWITCHMAN_SIEMENS + ", " +
				CapBank.SWITCHMAN_TRINETICS);
				
				
			System.out.print("Switch Manufacturer: ");
			String switchMan = input.readLine();
			System.out.print("Type of Switch:  Vacuum or Oil: ");
			String switchType = input.readLine();
			val = new InputValidater(this, new Integer(from).intValue(), new Integer(to).intValue(), route, banksize, switchMan, switchType, capcntrlType);
			
		}catch(Exception e)
		{
			
		}
		
		
	}

	private void init()
	{
		contentPane = (JPanel) this.getContentPane();
		titledBorder = new TitledBorder("");
		contentPane.setLayout(null);
		this.setSize(new Dimension(290, 350));
		this.setTitle("CapControl Importer");
		
		serialFromTF = new JTextField();
		serialToTF = new JTextField();
		serialFromTF.setDocument(new com.cannontech.common.gui.unchanging.LongRangeDocument(-999999999, 999999999));
		serialToTF.setDocument(new com.cannontech.common.gui.unchanging.LongRangeDocument(-999999999, 999999999));
		serialRangeLabel.setBounds(new Rectangle(10, 10, 90, 20));
		serialFromTF.setBounds(new Rectangle(100, 10, 60, 20));
		
		serialToLabel.setBounds(new Rectangle(170, 10, 20, 20));
		serialToTF.setBounds(new Rectangle(190, 10, 60, 20));
		routeLabel.setBounds(new Rectangle(10, 40, 50, 20));
		routeCB.setBounds(new Rectangle(70, 40, 120, 20));
		bankSizeLabel.setBounds(new Rectangle(10, 160, 90, 20));
		bankSizeCB.setBounds(new Rectangle(110, 160, 120, 20));
		switchManLabel.setBounds(new Rectangle(10, 100, 110, 20));
		switchManCB.setBounds(new Rectangle(130, 100, 120, 20));
		switchTypeLabel.setBounds(new Rectangle(10, 130, 90, 20));
		switchTypeCB.setBounds(new Rectangle(110, 130, 120, 20));
		conTypeLabel.setBounds(new Rectangle(10, 70, 90, 20));
		conTypeCB.setBounds(new Rectangle(110, 70, 120, 20));
		submitButton.setBounds(new Rectangle(100, 190, 70, 25));
		requiredLabel.setBounds(new Rectangle(60, 255, 130, 20));
		bar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
		bar.setValue(0);
		bar.setStringPainted(true);
		bar.setBounds(new Rectangle(35, 225, 200, 20));
		bar.setVisible(false);
		submitButton.addActionListener(this);

		conTypeCB.addItem(com.cannontech.database.data.pao.PAOGroups.STRING_CBC_FP_2800[0]);
		conTypeCB.addItem(com.cannontech.database.data.pao.PAOGroups.STRING_CAP_BANK_CONTROLLER[0]);
		conTypeCB.addItem(com.cannontech.database.data.pao.PAOGroups.STRING_DNP_CBC_6510[0]);
		conTypeCB.addItem(com.cannontech.database.data.pao.PAOGroups.STRING_CBC_EXPRESSCOM[0]);
		conTypeCB.addItem(com.cannontech.database.data.pao.PAOGroups.STRING_CBC_7010[0]);

		switchManCB.addItem(com.cannontech.common.util.CtiUtilities.STRING_NONE);
		switchManCB.addItem(CapBank.SWITCHMAN_WESTING);
		switchManCB.addItem(CapBank.SWITCHMAN_ABB);
		switchManCB.addItem(CapBank.SWITCHMAN_COOPER);
		switchManCB.addItem(CapBank.SWITCHMAN_SIEMENS);
		switchManCB.addItem(CapBank.SWITCHMAN_TRINETICS);

		switchTypeCB.addItem(com.cannontech.common.util.CtiUtilities.STRING_NONE);
		switchTypeCB.addItem(CapBank.SWITCHTYPE_OIL);
		switchTypeCB.addItem(CapBank.SWITCHTYPE_VACUUM);

		bankSizeCB.addItem(new Integer(50));
		bankSizeCB.addItem(new Integer(100));
		bankSizeCB.addItem(new Integer(150));
		bankSizeCB.addItem(new Integer(275));
		bankSizeCB.addItem(new Integer(300));
		bankSizeCB.addItem(new Integer(450));
		bankSizeCB.addItem(new Integer(550));
		bankSizeCB.addItem(new Integer(600));
		bankSizeCB.addItem(new Integer(825));
		bankSizeCB.addItem(new Integer(900));
		bankSizeCB.addItem(new Integer(1100));
		bankSizeCB.addItem(new Integer(1200));

		routeCB.removeAllItems();
		com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
		synchronized(cache)
		{         

			java.util.List list = cache.getAllRoutes();

			for( int i = 0; i < list.size(); i++ )
				routeCB.addItem( list.get(i) );
		}

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
		contentPane.add(conTypeLabel);
		contentPane.add(conTypeCB);
		contentPane.add(submitButton);
		contentPane.add(requiredLabel);
		contentPane.add(bar);

		//Create a timer.
		timer = new Timer(ONE_SECOND, new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				Double newDouble = new Double(progress);
				int newInt = newDouble.intValue();
				bar.setValue(newInt);

				if (progress == 100)
				{
					Toolkit.getDefaultToolkit().beep();
					timer.stop();

					//bar.setValue(bar.getMinimum());
				}
			}
		});
		
		this.listenForActions(this);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		Object obj = e.getSource();
		if (obj == submitButton)
		{
			// check data before running
			try
			{
				
				if ("".equalsIgnoreCase(serialFromTF.getText()) || "".equalsIgnoreCase(serialToTF.getText()))
				{
					JOptionPane.showMessageDialog(this, "Please enter values into required fields.", "Finish settings first", JOptionPane.WARNING_MESSAGE);
				}else if ( (new Integer (serialFromTF.getText())).intValue() > (new Integer (serialToTF.getText())).intValue() )
				{
					JOptionPane.showMessageDialog(this, "Range must be accending: "+serialToTF.getText()+" to "+serialFromTF.getText(), "Finish settings first", JOptionPane.WARNING_MESSAGE);
				}
				else
				{
					validating = true;
					bar.setVisible(true);
					progress = 0;
					bar.setValue(bar.getMinimum());
					String fromS = serialFromTF.getText();
					Integer from = new Integer(fromS);
					String toS = serialToTF.getText();
					Integer to = new Integer(toS);
					String route = (String) routeCB.getSelectedItem().toString();
					String banksize = (String) bankSizeCB.getSelectedItem().toString();
					String manufacturer = (String) switchManCB.getSelectedItem();
					String type = (String) switchTypeCB.getSelectedItem();
					String conType = (String) conTypeCB.getSelectedItem();
					timer.start();
					val = new InputValidater(this, from.intValue(), to.intValue(), route, banksize, manufacturer, type, conType);
					progressThread = new Thread(this, "Progress Thread");
					progressThread.start();
				}
			}catch(NumberFormatException nfe)
			{
				JOptionPane.showMessageDialog(this, "Please enter a valid serial range.", "Finish settings first", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		
		try
		{
			while (true)
			{
				// this is all meaningless except for checkRange, maybe needed later.
				boolean rangeOK = val.checkRange();
				if (rangeOK)
				{
					progress += 17;
				} else
				{
					notifier.setActionCode(ActionNotifier.ACTION_VALIDATION_FAILURE);
					timer.stop();
					validating = false;
					break;
				}
				boolean routeOK = val.checkRoute();
				if (routeOK)
				{
					progress += 17;
				} else
				{
					notifier.setActionCode(ActionNotifier.ACTION_VALIDATION_FAILURE);
					timer.stop();
					validating = false;
					break;
				}
				boolean bankOK = val.checkBankSize();
				if (bankOK)
				{
					progress += 17;
				} else
				{
					notifier.setActionCode(ActionNotifier.ACTION_VALIDATION_FAILURE);
					timer.stop();
					validating = false;
					break;
				}
				boolean switchManOK = val.checkManufacturer();
				if (switchManOK)
				{
					progress += 17;
				} else
				{
					notifier.setActionCode(ActionNotifier.ACTION_VALIDATION_FAILURE);
					timer.stop();
					validating = false;
					break;
				}
				boolean switchTypeOK = val.checkSwitchType();
				if (switchTypeOK)
				{
					progress += 17;
				} else
				{
					notifier.setActionCode(ActionNotifier.ACTION_VALIDATION_FAILURE);
					timer.stop();
					validating = false;
					break;
				}
				boolean switchConTypeOK = val.checkConType();
				if (switchConTypeOK)
				{
					progress += 100;
					notifier.setActionCode(ActionNotifier.ACTION_VALIDATION_SUCCESSFUL);
					validating = false;
					break;
				} else
				{
					notifier.setActionCode(ActionNotifier.ACTION_VALIDATION_FAILURE);
					timer.stop();
					validating = false;
					break;
				}
				
			}
		} catch (Exception e)
		{
			CTILogger.info("error while validating");
			e.printStackTrace(System.out);
		}
		
	}
	
	private boolean writeToDB(InputValidater val)
	{
		try
		{
			timer.start();
			int[] ids = com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectIDs((val.getTo()-val.getFrom()+1)*2);
			
			int index = 0;
			
			float updateSize = 100.0f / val.getCBCCount();
			
			for(int i = val.getFrom(); i <= val.getTo();i++){
				// create devices
				CapBank capBank = (CapBank)DeviceFactory.createDevice(PAOGroups.CAPBANK);
				capBank.setPAOName( "CapBank " + SERIAL_FORM.format(i) );
				//String capbankMan = switchManCB.getSelectedItem().toString();
				String capbankMan = val.getSwitchMan();
				capBank.getCapBank().setSwitchManufacture(capbankMan);
				//String typeOfSwitch = switchTypeCB.getSelectedItem().toString();
				String typeOfSwitch = val.getSwitchType();
				capBank.getCapBank().setTypeOfSwitch(typeOfSwitch);
				capBank.getCapBank().setOperationalState( CapBank.UNINSTALLED_OPSTATE );
				//capBank.getCapBank().setBankSize(new Integer(bankSizeCB.getSelectedItem().toString()));
				capBank.getCapBank().setBankSize(new Integer(val.getBankSize()));
				
				SmartMultiDBPersistent smartMulti = new SmartMultiDBPersistent();
				smartMulti.setCreateNewPAOIDs(false);
				
				PointFactory.createBankStatusPt( smartMulti );
				PointFactory.createBankOpCntPoint( smartMulti );
				
				capBank.setDeviceID( new Integer(ids[index]) );
				capBank.getDevice().setDeviceID(new Integer(ids[index]));
				capBank.getCapBank().setDeviceID(new Integer(ids[index]));
				
				smartMulti.addDBPersistent( capBank );
				smartMulti.setOwnerDBPersistent( capBank );
				
				DeviceBase newCBC = null;
				if( val.getControllerType().equalsIgnoreCase(com.cannontech.database.data.pao.PAOGroups.STRING_CBC_FP_2800[0]) )
				{
					newCBC = DeviceFactory.createDevice(com.cannontech.database.data.pao.PAOGroups.CBC_FP_2800);
					capBank.getCapBank().setControllerType(com.cannontech.database.data.pao.PAOGroups.STRING_CBC_FP_2800[0]);
				}
				else if( val.getControllerType().equalsIgnoreCase(com.cannontech.database.data.pao.PAOGroups.STRING_CAP_BANK_CONTROLLER[0]) )
				{
					newCBC = DeviceFactory.createDevice(com.cannontech.database.data.pao.PAOGroups.CAPBANKCONTROLLER);
					capBank.getCapBank().setControllerType(com.cannontech.database.data.pao.PAOGroups.STRING_CAP_BANK_CONTROLLER[0]);
				}
				else if( val.getControllerType().equalsIgnoreCase(com.cannontech.database.data.pao.PAOGroups.STRING_DNP_CBC_6510[0]) )
				{
					newCBC = DeviceFactory.createDevice(com.cannontech.database.data.pao.PAOGroups.DNP_CBC_6510);
					capBank.getCapBank().setControllerType(com.cannontech.database.data.pao.PAOGroups.STRING_DNP_CBC_6510[0]);
				}
				else if( val.getControllerType().equalsIgnoreCase(com.cannontech.database.data.pao.PAOGroups.STRING_CBC_EXPRESSCOM[0]))
				{
					newCBC = DeviceFactory.createDevice(com.cannontech.database.data.pao.PAOGroups.CBC_EXPRESSCOM);
					capBank.getCapBank().setControllerType(com.cannontech.database.data.pao.PAOGroups.STRING_CBC_EXPRESSCOM[0]);
				}
				else if( val.getControllerType().equalsIgnoreCase(com.cannontech.database.data.pao.PAOGroups.STRING_CBC_7010[0]))
				{
					newCBC = DeviceFactory.createDevice(com.cannontech.database.data.pao.PAOGroups.CBC_7010);
					capBank.getCapBank().setControllerType(com.cannontech.database.data.pao.PAOGroups.STRING_CBC_7010[0]);
				}
				if( newCBC instanceof ICapBankController )
				{
					ICapBankController cntrler = (ICapBankController)newCBC;
					cntrler.assignAddress( new Integer(SERIAL_FORM.format(i)) );
					Integer comID = new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)
					routeCB.getSelectedItem()).getYukonID());
					cntrler.setCommID( comID );
				}
				else
					throw new IllegalStateException("CBC class of: " + newCBC.getClass().getName() + " not found");
				
				//just use the serial number in the name
				newCBC.setPAOName( "CBC " + SERIAL_FORM.format(i) );
				newCBC.setDeviceID( new Integer(ids[index+1]) );
				newCBC.getDevice().setDeviceID(new Integer(ids[index+1]));
								
				index +=2;
				//a status point is automatically added to all capbank controllers
				
				PointBase newPoint = 
					CapBankController.createStatusControlPoint( 
							newCBC.getDevice().getDeviceID().intValue() );
				
				smartMulti.insertDBPersistentAt( newCBC,0 );
				smartMulti.insertDBPersistentAt( newPoint,1 );
				for( int j = 0; j < smartMulti.size(); j++ )
				{
					if( smartMulti.getDBPersistent(j) instanceof PointBase )
					{
						
						capBank.getCapBank().setControlPointID(
								((PointBase)smartMulti.getDBPersistent(j)).getPoint().getPointID() );

						
						capBank.getCapBank().setControlDeviceID(
								((PointBase)smartMulti.getDBPersistent(j)).getPoint().getPaoID() );
								
						break;
					}
				}
				
				try
				{
					Transaction.createTransaction(Transaction.INSERT,smartMulti).execute();
					
					DBChangeMsg[] dbChange = 
							DefaultDatabaseCache.getInstance().createDBChangeMessages(
								(CTIDbChange)smartMulti, DBChangeMsg.CHANGE_TYPE_ADD );

					for( int j = 0; j < dbChange.length; j++ )
					{
						//handle the DBChangeMsg locally
						LiteBase lBase = DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange[j]);
					}
 	
				}
				catch( com.cannontech.database.TransactionException t )
				{
					CTILogger.error( t.getMessage(), t );
					
					return false;
				}
				
				
				// update progress bar
				progress += updateSize;
				if(progress > 100){
					progress = 100;
				}
				
			}
			return true;
		}catch (Exception e)
		{
			CTILogger.info("error writing to database");
			e.printStackTrace(System.out);
			return false;
		}
	}

	public void fileExit_actionPerformed(ActionEvent e)
	{
		this.setVisible(false);
		this.dispose();
	}

	protected void processWindowEvent(WindowEvent e)
	{
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING)
		{
			fileExit_actionPerformed(null);
		}
	}
	
	public void listenForActions(Observer o)
	{
		notifier.addObserver(o);
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg)
	{
		ActionNotifier os = (ActionNotifier) o;

		if (os.getAction() == com.cannontech.capcontrol.ActionNotifier.ACTION_VALIDATION_SUCCESSFUL)
		{
			progress = 0;
			bar.setValue(0);
			CTILogger.info("validation success");
			validating = false;
			boolean success = writeToDB(val);
			if(success){
				notifier.setActionCode(ActionNotifier.ACTION_DBWRITE_SUCCESSFUL);
			}else
			{
				
				notifier.setActionCode(ActionNotifier.ACTION_DBWRITE_FAILURE);
			}
		}else if(os.getAction() == com.cannontech.capcontrol.ActionNotifier.ACTION_VALIDATION_FAILURE)
		{
			progress = 0;
			validating = false;
			bar.setValue(0);
			CTILogger.info(" validation failure");
			
			
		}else if(os.getAction() == com.cannontech.capcontrol.ActionNotifier.ACTION_DBWRITE_SUCCESSFUL)
		{
			CTILogger.info("dbwrite success");
			progress = 100;
			bar.setValue(100);
			timer.stop();
			
		}else if(os.getAction() == com.cannontech.capcontrol.ActionNotifier.ACTION_DBWRITE_FAILURE)
		{
			CTILogger.info("dbwrite failure");
			timer.stop();
		}
	}

}