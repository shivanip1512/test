/*
 * Created on May 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.fdemulator.common;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import com.cannontech.fdemulator.protocols.ACSManual;
import com.cannontech.fdemulator.protocols.ACSProtocol;
import com.cannontech.fdemulator.protocols.RdexManual;
import com.cannontech.fdemulator.protocols.RdexProtocol;
import com.cannontech.fdemulator.protocols.ValmetManual;
import com.cannontech.fdemulator.protocols.ValmetProtocol;

/**
 * @author ASolberg
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FDTestPanel extends JPanel implements Runnable, Observer, ActionListener
{

	//UI and components
	private JButton manual = new JButton("Manual");
	private JButton clearTest = new JButton();
	private JButton startTest = new JButton();
	private JButton closeTest = new JButton();
	private JButton editButton = new JButton();
	ImageIcon hearton = new ImageIcon("src/main/resources/hearton.png");
	ImageIcon heartoff = new ImageIcon("src/main/resources/heartoff.png");
	ImageIcon cannon = new ImageIcon("src/main/resources/eaton_logo.png");
	private JCheckBox hbCheckBox = new JCheckBox(heartoff);
	private JLabel logo = new JLabel(cannon);
	private JLabel testLabel = new JLabel();
	private TraceLogPanel logPanel = new TraceLogPanel();

	// Notifier to basically tell gui to get rid of a testpanel instance or clear output debug pane
	private FDTestPanelNotifier notifier = new FDTestPanelNotifier(this);
	private FDEFrameWindow parentFrame;
	private FDEProtocol proto;
	private JPanel statPanel;
	// Closing variables
	private boolean connectStatus = false;
	private boolean closeable = false;

	private volatile Thread startThread = null;
	// Protocol types
	private final int RDEX = 1;
	private final int ACS = 2;
	private final int VALMET = 3;
	private int protoType = 0;

	private static final String formatDesc = "MM/dd/yyyy HH:mm:ss";
	private DateFormat dbdf = new SimpleDateFormat(formatDesc);

	FDTestPanel(int protocolType, FDEFrameWindow window)
	{
		super();
		parentFrame = window;
		protoType = protocolType;
		if (protoType == RDEX)
		{
			try
			{
				initRDEX();
			} catch (Exception e)
			{
				System.out.println(getDebugTimeStamp() + "Error initializing rdex test panel " + e);
			}
		} else if (protoType == ACS)
		{
			try
			{
				initACS();
			} catch (Exception e)
			{
				System.out.println(getDebugTimeStamp() + "Error initializing acs test panel " + e);
			}
		} else if (protoType == VALMET)
		{
			try
			{
				initVALMET();
			} catch (Exception e)
			{
				System.out.println(getDebugTimeStamp() + "Error initializing valmet test panel " + e);
			}
		}

	}

	public TraceLogPanel getLogPanel()
	{
		return logPanel;
	}

	public FDEProtocol getProtocol()
	{
		return proto;
	}

	public FDEFrameWindow getWindow()
	{
		return parentFrame;
	}

	public FDTestPanelNotifier getNotifier()
	{
		return notifier;
	}

	public String getDebugTimeStamp()
	{
		return dbdf.format(new Date()) + " ";
	}

	public void initRDEX()
	{
		// Initialize RDEX Emulator
		hbCheckBox.addActionListener(this);
		manual.addActionListener(this);
		startTest.addActionListener(this);
		clearTest.addActionListener(this);
		closeTest.addActionListener(this);
		editButton.addActionListener(this);

		this.setLayout(null);

		manual.setBounds(new Rectangle(10, 25, 70, 27));
		startTest.setBounds(new Rectangle(195, 25, 65, 27));
		startTest.setText("Start");
		clearTest.setBounds(new Rectangle(90, 25, 95, 27));
		clearTest.setText("Clear Output");
		closeTest.setText("Close");
		closeTest.setBounds(new Rectangle(265, 25, 65, 27));
		hbCheckBox.setBounds(new Rectangle(340, 25, 20, 20));
		logPanel.setBounds(new Rectangle(10, 53, 500, 210));
		logo.setBounds(550, 5, 125, 50);
		logo.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		editButton.setText("Edit");
		editButton.setBounds(new Rectangle(445, 270, 65, 20));
		testLabel.setBounds(10, 270, 160, 20);
		testLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		testLabel.setText(" Foreign Data Emulator: RDEX");

		this.add(manual);
		this.add(startTest);
		this.add(clearTest);
		this.add(closeTest);
		this.add(logPanel);
		this.add(hbCheckBox);
		this.add(logo);
		this.add(testLabel);
		this.add(editButton);

		proto = new RdexProtocol(logPanel, this);
		proto.listenForActions(this);
		statPanel = proto.getStatPanel();
		statPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		statPanel.setBounds(new Rectangle(520, 70, 155, 220));
		this.add(statPanel);

	}

	public void initACS()
	{
		// Initialize ACS Emulator
		hbCheckBox.addActionListener(this);
		manual.addActionListener(this);
		startTest.addActionListener(this);
		clearTest.addActionListener(this);
		clearTest.addActionListener(this);
		closeTest.addActionListener(this);
		editButton.addActionListener(this);

		this.setLayout(null);

		manual.setBounds(new Rectangle(10, 25, 70, 27));
		startTest.setBounds(new Rectangle(195, 25, 65, 27));
		startTest.setText("Start");
		clearTest.setBounds(new Rectangle(90, 25, 95, 27));
		clearTest.setText("Clear Output");
		closeTest.setText("Close");
		closeTest.setBounds(new Rectangle(265, 25, 65, 27));
		hbCheckBox.setBounds(new Rectangle(340, 25, 20, 20));
		logPanel.setBounds(new Rectangle(10, 53, 500, 210));
		logo.setBounds(550, 5, 125, 50);
		logo.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		testLabel.setBounds(10, 270, 160, 20);
		testLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		testLabel.setText(" Foreign Data Emulator: ACS");
		editButton.setText("Edit");
		editButton.setBounds(new Rectangle(445, 270, 65, 20));

		this.add(manual);
		this.add(startTest);
		this.add(clearTest);
		this.add(closeTest);
		this.add(logPanel);
		this.add(hbCheckBox);
		this.add(logo);
		this.add(testLabel);
		this.add(editButton);

		proto = new ACSProtocol(logPanel, this);
		proto.listenForActions(this);
		statPanel = proto.getStatPanel();
		statPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		statPanel.setBounds(new Rectangle(520, 70, 155, 220));
		this.add(statPanel);
	}

	public void initVALMET()
	{
		// Initialize VALMET Emulator
		hbCheckBox.addActionListener(this);
		manual.addActionListener(this);
		startTest.addActionListener(this);
		clearTest.addActionListener(this);
		closeTest.addActionListener(this);
		editButton.addActionListener(this);

		this.setLayout(null);

		manual.setBounds(new Rectangle(10, 25, 70, 27));
		startTest.setBounds(new Rectangle(195, 25, 65, 27));
		startTest.setText("Start");
		clearTest.setBounds(new Rectangle(90, 25, 95, 27));
		clearTest.setText("Clear Output");
		closeTest.setText("Close");
		closeTest.setBounds(new Rectangle(265, 25, 65, 27));
		hbCheckBox.setBounds(new Rectangle(340, 25, 20, 20));
		logPanel.setBounds(new Rectangle(10, 53, 500, 210));
		logo.setBounds(550, 5, 125, 50);
		logo.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		testLabel.setBounds(10, 300, 160, 20);
		testLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		testLabel.setText(" Foreign Data Emulator: VALMET");
		editButton.setText("Edit");
		editButton.setBounds(new Rectangle(445, 270, 65, 20));

		this.add(manual);
		this.add(startTest);
		this.add(clearTest);
		this.add(closeTest);
		this.add(logPanel);
		this.add(hbCheckBox);
		this.add(logo);
		this.add(testLabel);
		this.add(editButton);

		proto = new ValmetProtocol(logPanel, this);
		proto.listenForActions(this);
		statPanel = proto.getStatPanel();
		statPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		statPanel.setBounds(new Rectangle(520, 70, 175, 350));
		this.add(statPanel);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == hbCheckBox)
		{
			hbCheckBox_actionPerformed();
		} else if (e.getSource() == manual)
		{
			manual_actionPerformed();
		} else if (e.getSource() == startTest)
		{
			startTest_actionPerformed();
		} else if (e.getSource() == clearTest)
		{
			clearTest_actionPerformed();
		} else if (e.getSource() == closeTest)
		{
			closeTest_actionPerformed();
		} else if (e.getSource() == editButton)
		{
			editButton_actionPerformed();
		}
	}

	public void editButton_actionPerformed()
	{
		proto.editSettings();
	}

	public void hbCheckBox_actionPerformed()
	{
		if (hbCheckBox.isSelected() == true)
		{
			proto.hbOn(true);
			hbCheckBox.setIcon(hearton);
		} else
		{
			proto.hbOn(false);
			hbCheckBox.setIcon(heartoff);
		}

	}

	public void manual_actionPerformed()
	{

		if (connectStatus == true)
		{

			JFrame entryFrame = null;
			if (protoType == 1)
			{
				entryFrame = new RdexManual((RdexProtocol) proto);
			} else if (protoType == 2)
			{
				entryFrame = new ACSManual((ACSProtocol) proto);
			} else if (protoType == 3)
			{
				entryFrame = new ValmetManual((ValmetProtocol) proto);
			}

			// Center the window
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension frameSize = entryFrame.getSize();
			if (frameSize.height > screenSize.height)
			{
				frameSize.height = screenSize.height;
			}
			if (frameSize.width > screenSize.width)
			{
				frameSize.width = screenSize.width;
			}
			entryFrame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
			entryFrame.setVisible(true);
		}

		repaint();
	}

	public void clearTest_actionPerformed()
	{
		// set action flag and nofity frame
		notifier.setActionCode(FDTestPanelNotifier.ACTION_CLEAR_STATS);

	}

	public void startTest_actionPerformed()
	{
		if (connectStatus == true)
		{ // hitting stop after we have made a successful connection
			proto.closeConnection();
			connectStatus = false;
			closeable = false;
			startTest.setText("Start");
			editButton.setEnabled(true);
		} else if (closeable == true)
		{ // hitting stop when we are trying to connect in our start connection loop
			stop();
			startTest.setText("Start");
			editButton.setEnabled(true);
			closeable = false;
		} else
		{ // hitting start
			// Start thread for reading if settings are done
			if (proto.settingDone() == true)
			{
				proto.updateStats();
				startThread = new Thread(this, "START CONNECTION THREAD");
				startThread.start();
			} else
			{
				JOptionPane.showMessageDialog(null, "Finish settings first!", "Settings not done yet.", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	public void closeTest_actionPerformed()
	{

		if (closeable == true)
		{
			stop();
			notifier.setActionCode(FDTestPanelNotifier.ACTION_STOP_TEST);
		} else
		{
			proto.closeConnection();
			notifier.setActionCode(FDTestPanelNotifier.ACTION_STOP_TEST);
		}
	}

	// Run method for startThread, tries to connect until successful
	public void run()
	{
		try
		{
			startTest.setText("Stop");
			closeable = true;
			editButton.setEnabled(false);
			Thread thisThread = Thread.currentThread();
			while (startThread == thisThread)
			{
				connectStatus = proto.startConnection();
				if (connectStatus == true)
				{
					break;
				}
				SwingUtilities.invokeLater(new FdeLogger(logPanel, "Retrying in 5 seconds", 0));
				Thread.sleep(5000);
			}

		} catch (Exception e)
		{
			System.out.println(getDebugTimeStamp() + "Error with startthread");
			e.printStackTrace(System.out);
		}
	}
	
	public void stop(){
		Thread killme = startThread;
		startThread = null;
		killme.interrupt();
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
		FDTestPanelNotifier os = (FDTestPanelNotifier) o;

		if (os.getAction() == com.cannontech.fdemulator.common.FDTestPanelNotifier.ACTION_CONNECTION_LOST)
		{
			System.out.println(getDebugTimeStamp() + "Connection to FDR lost");
			System.out.println(getDebugTimeStamp() + "Starting reconnect thread");
			connectStatus = false;
			closeable = false;
			startThread = new Thread(this, "RECONNECT THREAD");
			startThread.start();
		}
	}
}