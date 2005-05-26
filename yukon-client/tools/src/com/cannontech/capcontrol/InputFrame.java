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
import com.cannontech.database.db.DBPersistent;
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
	private JTextField serialFromTF = new JTextField("");
	private JTextField serialToTF = new JTextField("");
	//private JButton checkButton = new JButton("Check");
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
	private volatile Thread valThread;
	private Timer timer;
	private int progress = 0;
	private final int ONE_SECOND = 1000;
	private boolean valid = false;
	private ActionNotifier notifier = new ActionNotifier(this);
	private static DecimalFormat SERIAL_FORM = new DecimalFormat("000000000");
	private static DecimalFormat DBL_FORM = new DecimalFormat("#.00");


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

	private void init()
	{
		contentPane = (JPanel) this.getContentPane();
		titledBorder = new TitledBorder("");
		contentPane.setLayout(null);
		this.setSize(new Dimension(290, 350));
		this.setTitle("CapControl Importer");

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
		//checkButton.setBounds(new Rectangle(60,190,70,25));
		submitButton.setBounds(new Rectangle(100, 190, 70, 25));
		requiredLabel.setBounds(new Rectangle(60, 255, 130, 20));
		bar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
		bar.setValue(0);
		bar.setStringPainted(true);
		bar.setBounds(new Rectangle(35, 225, 200, 20));
		bar.setVisible(false);
		//checkButton.addActionListener(this);
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
//			java.util.List list = 
//			(cbcType == DeviceTypes.DNP_CBC_6510)
//			? cache.getAllPorts()
//			: cache.getAllRoutes();
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
		//contentPane.add(checkButton);
		contentPane.add(submitButton);
		contentPane.add(requiredLabel);
		contentPane.add(bar);

		//Create a timer.
		timer = new Timer(ONE_SECOND, new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				bar.setValue(progress);

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
			// check data with our input validater
			if ("".equalsIgnoreCase(serialFromTF.getText()) || "".equalsIgnoreCase(serialToTF.getText()))
			{
				JOptionPane.showMessageDialog(this, "Please enter values into required fields.", "Finish settings first", JOptionPane.WARNING_MESSAGE);
			} else
			{
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
				val = new InputValidater(from.intValue(), to.intValue(), route, banksize, manufacturer, type, conType);
				valThread = new Thread(this, "Validation Thread");
				valThread.start();
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
				boolean rangeOK = val.checkRange();
				if (rangeOK)
				{
					progress += 17;
				} else
				{
					notifier.setActionCode(ActionNotifier.ACTION_VALIDATION_FAILURE);
					timer.stop();
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
					break;
				}
				boolean switchConTypeOK = val.checkConType();
				if (switchConTypeOK)
				{
					progress += 100;
					notifier.setActionCode(ActionNotifier.ACTION_VALIDATION_SUCCESSFUL);
					break;
				} else
				{
					notifier.setActionCode(ActionNotifier.ACTION_VALIDATION_FAILURE);
					timer.stop();
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
			int[] ids = com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectIDs((val.getTo()-val.getFrom()+1)*2);
			int index = 0;
			for(int i = val.getFrom(); i <= val.getTo();i++){
				// create devices
				CapBank capBank = (CapBank)DeviceFactory.createDevice(PAOGroups.CAPBANK);
				capBank.setPAOName( "CapBank " + i );
				//capBank.setLocation( "" );
				String capbankMan = switchManCB.getSelectedItem().toString();
				capBank.getCapBank().setSwitchManufacture(capbankMan);
				String typeOfSwitch = switchTypeCB.getSelectedItem().toString();
				capBank.getCapBank().setTypeOfSwitch(typeOfSwitch);
				capBank.getCapBank().setOperationalState( CapBank.UNINSTALLED_OPSTATE );
				capBank.getCapBank().setBankSize(new Integer(bankSizeCB.getSelectedItem().toString()));
				SmartMultiDBPersistent smartMulti = new SmartMultiDBPersistent();
				PointFactory.createBankStatusPt( smartMulti );
				PointFactory.createBankOpCntPoint( smartMulti );
				//Integer id = com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID();
				//System.out.println("id: "+id);
				capBank.setDeviceID( new Integer(ids[index]) );
				smartMulti.addDBPersistent( capBank );
				smartMulti.setOwnerDBPersistent( capBank );
				
				DeviceBase newCBC = null;
				if( conTypeCB.getSelectedItem().toString().equalsIgnoreCase(com.cannontech.database.data.pao.PAOGroups.STRING_CBC_FP_2800[0]) )
					newCBC = DeviceFactory.createDevice(com.cannontech.database.data.pao.PAOGroups.CBC_FP_2800);
				else if( conTypeCB.getSelectedItem().toString().equalsIgnoreCase(com.cannontech.database.data.pao.PAOGroups.STRING_CAP_BANK_CONTROLLER[0]) )
					newCBC = DeviceFactory.createDevice(com.cannontech.database.data.pao.PAOGroups.CAPBANKCONTROLLER);
				else if( conTypeCB.getSelectedItem().toString().equalsIgnoreCase(com.cannontech.database.data.pao.PAOGroups.STRING_DNP_CBC_6510[0]) )
					newCBC = DeviceFactory.createDevice(com.cannontech.database.data.pao.PAOGroups.DNP_CBC_6510);
				else if( conTypeCB.getSelectedItem().toString().equalsIgnoreCase(com.cannontech.database.data.pao.PAOGroups.STRING_CBC_EXPRESSCOM[0]))
					newCBC = DeviceFactory.createDevice(com.cannontech.database.data.pao.PAOGroups.CBC_EXPRESSCOM);
				else if( conTypeCB.getSelectedItem().toString().equalsIgnoreCase(com.cannontech.database.data.pao.PAOGroups.STRING_CBC_7010[0]))
					newCBC = DeviceFactory.createDevice(com.cannontech.database.data.pao.PAOGroups.CBC_7010);
				
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
				
				//set the paoID
				//Integer cbcID = com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID();
				//System.out.println("id2: "+cbcID);
				newCBC.setDeviceID( new Integer(ids[index+1]) );
				index +=2;
				//a status point is automatically added to all capbank controllers
				PointBase newPoint = 
					CapBankController.createStatusControlPoint( 
							newCBC.getDevice().getDeviceID().intValue() );
				
				smartMulti.insertDBPersistentAt( newCBC, 0 );
				smartMulti.insertDBPersistentAt( newPoint, 1 );
				for( int j = 0; j < smartMulti.size(); j++ )
				{
					if( smartMulti.getDBPersistent(i) instanceof PointBase )
					{
						capBank.getCapBank().setControlPointID(
								((PointBase)smartMulti.getDBPersistent(i)).getPoint().getPointID() );

						capBank.getCapBank().setControlDeviceID(
								((PointBase)smartMulti.getDBPersistent(i)).getPoint().getPaoID() );
						break;
					}
				}
				
				try
				{
					DBPersistent db = (DBPersistent) smartMulti;
					db = (DBPersistent)Transaction.createTransaction(
							  Transaction.INSERT, 
					db).execute();
 	
					DBChangeMsg[] dbChange = 
							DefaultDatabaseCache.getInstance().createDBChangeMessages(
								(CTIDbChange)db, DBChangeMsg.CHANGE_TYPE_ADD );

					for( int j = 0; j < dbChange.length; j++ )
					{
						//handle the DBChangeMsg locally
						LiteBase lBase = DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange[i]);
					}
 	
				}
				catch( com.cannontech.database.TransactionException t )
				{
					CTILogger.error( t.getMessage(), t );
					return false;
				}
				// update progress bar
				
			}
			return true;
		}catch (Exception e)
		{
			// do something
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
			CTILogger.info("validation success");
			boolean success = writeToDB(val);
			if(success){
				CTILogger.info("dbwrite success");
				notifier.setActionCode(ActionNotifier.ACTION_DBWRITE_SUCCESSFUL);
			}else
			{
				
				notifier.setActionCode(ActionNotifier.ACTION_DBWRITE_FAILURE);
			}
		}else if(os.getAction() == com.cannontech.capcontrol.ActionNotifier.ACTION_VALIDATION_FAILURE)
		{
			CTILogger.info(" validation failure");
			JOptionPane.showMessageDialog(this, "Some of the numbers in the specified serial range are used.", "Serial Range Conflict", JOptionPane.WARNING_MESSAGE);
		}else if(os.getAction() == com.cannontech.capcontrol.ActionNotifier.ACTION_DBWRITE_SUCCESSFUL)
		{
			// fire a db change
			
		}else if(os.getAction() == com.cannontech.capcontrol.ActionNotifier.ACTION_DBWRITE_FAILURE)
		{
			CTILogger.info("dbwrite failure");
		}
	}
}