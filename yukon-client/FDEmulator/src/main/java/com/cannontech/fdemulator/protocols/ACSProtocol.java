/*
 * Created on Jun 2, 2004
 *
 */
package com.cannontech.fdemulator.protocols;
import java.awt.Color;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Observer;
import java.util.Random;
import java.util.SimpleTimeZone;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import com.cannontech.fdemulator.common.FDEProtocol;
import com.cannontech.fdemulator.common.FDTestPanel;
import com.cannontech.fdemulator.common.FDTestPanelNotifier;
import com.cannontech.fdemulator.common.FdeLogger;
import com.cannontech.fdemulator.common.TraceLogPanel;
import com.cannontech.fdemulator.fileio.AcsFileIO;

/**
 * @author ASolberg
 */
public class ACSProtocol extends FDEProtocol implements Runnable
{
	// settings gui
	private ACSSettings settings = null;

	// loop escape
	private int quit = 0;

	//socket and streams

	private TraceLogPanel log;
	private FDTestPanel testPanel;

	// processes incoming data
	private volatile Thread readThread = null;

	// connection variables
	private String DEFAULT_YUKON_HOST = "127.0.0.1";
	private String yukon_host = null;
	private String YUKON_BACKUP1;
	private String YUKON_BACKUP2;
	private String YUKON_BACKUP3;
	private int DEFAULT_YUKON_PORT = 1668;
	private int yukon_port = 0;
	private String server;
	private int retryStart = 0;

	//message types
	private final int ACS_NULL = 0;
	private final int ACS_VALUE = 101;
	private final int ACS_STATUS = 102;
	private final int ACS_CONTROL = 201;
	private final int ACS_TIMESYNC = 401;

	// timers and timer variables
	private Timer heartbeat;
	private Timer interval;
	private Timer timesync;
	private int thirtySecond = 0;
	private int sixtySecond = 0;
	private int fiveMinute = 0;
	private int fifteenMinute = 0;
	private int sixtyMinute = 0;
	private int flipflop10 = 0;
	private int flipflop30 = 0;
	private int flipflop60 = 0;
	private int flipflop300 = 0;
	private int flipflop900 = 0;
	private int flipflop3600 = 0;
	private final static long T1_TIME = 10500;
	// 10.5 seconds, so we don't get a lot of timeouts
	private final static long T2_TIME = 10000; // 10 seconds
	private final static long T3_TIME = 300000; // 5 minutes
	private int retryHeartbeat = 0;
	private boolean hbeat = false;

	// timestamp variables
	private static final String formatDesc = "yyyyMMddHHmmss";
	private static final String formatDesc2 = "MMMMM dd, yyyy HH:mm:ss";
	private static final String formatDesc3 = "MM/dd/yyyy HH:mm:ss";
	private DateFormat df = new SimpleDateFormat(formatDesc);
	private DateFormat fdf = new SimpleDateFormat(formatDesc2);
	private DateFormat dbdf = new SimpleDateFormat(formatDesc3);

	// socket and file IO variables
	private Socket fdeSocket = null;
	private DataOutputStream out = null;
	private DataInputStream in = null;
	private final String DEFAULT_POINTFILE = "resource/acs_points.cfg";
	private final String DEFAULT_TRAFFICFILE = "resource/acs_traffic_log.txt";
	private RandomAccessFile pointList;
	private String pointFile = null;
	private String trafficFile = null;
	private AcsFileIO acsFileIO = null;

	// stat panel variables
	private JPanel statPanel = new JPanel();
	private JLabel serverLabel = new JLabel("Server:");
	private JLabel back1Label = new JLabel("Backup 1:");
	private JLabel back2Label = new JLabel("Backup 2:");
	private JLabel back3Label = new JLabel("Backup 3:");
	private JLabel portLabel = new JLabel("Port:");
	private JLabel pathLabel = new JLabel("Point File:");
	private JLabel trafficLabel = new JLabel("Traffic Log File:");
	private JLabel serverDataLabel = new JLabel();
	private JLabel back1DataLabel = new JLabel();
	private JLabel back2DataLabel = new JLabel();
	private JLabel back3DataLabel = new JLabel();
	private JLabel portDataLabel = new JLabel();
	private JLabel pathDataLabel = new JLabel();
	private JLabel trafficDataLabel = new JLabel();

	// protocol utility variables
	private Random gen = new Random();
	private FDTestPanelNotifier notifier;
	private NumberFormat nf;
	private Object[] pointarray;
	private SimpleTimeZone tz;
	private GregorianCalendar gc;
	private static final String LF = System.getProperty("line.separator");

	/**
	 * Main constructor: 
	 * Set TraceLogInstance to parent TestPanel's ouput log.
	 * Save reference to parents test panel. 
	 */
	public ACSProtocol(TraceLogPanel logPanel, FDTestPanel panel)
	{
		log = logPanel;
		testPanel = panel;

		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		nf.setMaximumIntegerDigits(5);
		nf.setMinimumFractionDigits(2);
		nf.setMinimumIntegerDigits(2);

		// Start settings frame
		settings = new ACSSettings("Settings", this);
		settings.listenForActions(testPanel.getWindow());
		settings.setVisible(true);
		statPanel.setLayout(null);
		serverLabel.setBounds(new Rectangle(5, 5, 48, 20));
		back1Label.setBounds(new Rectangle(5, 30, 48, 20));
		back2Label.setBounds(new Rectangle(5, 55, 48, 20));
		back3Label.setBounds(new Rectangle(5, 80, 48, 20));
		portLabel.setBounds(new Rectangle(5, 105, 48, 20));
		pathLabel.setBounds(new Rectangle(5, 130, 48, 20));
		trafficLabel.setBounds(new Rectangle(5, 170, 80, 20));

		serverDataLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		back1DataLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		back2DataLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		back3DataLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		portDataLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		pathDataLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		trafficDataLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));

		serverDataLabel.setBounds(new Rectangle(55, 5, 90, 20));
		back1DataLabel.setBounds(new Rectangle(55, 30, 90, 20));
		back2DataLabel.setBounds(new Rectangle(55, 55, 90, 20));
		back3DataLabel.setBounds(new Rectangle(55, 80, 90, 20));
		portDataLabel.setBounds(new Rectangle(55, 105, 90, 20));
		pathDataLabel.setBounds(new Rectangle(5, 150, 145, 20));
		trafficDataLabel.setBounds(new Rectangle(5, 195, 145, 20));

		statPanel.add(serverLabel);
		statPanel.add(back1Label);
		statPanel.add(back2Label);
		statPanel.add(back3Label);
		statPanel.add(portLabel);
		statPanel.add(pathLabel);
		statPanel.add(trafficLabel);
		statPanel.add(serverDataLabel);
		statPanel.add(back1DataLabel);
		statPanel.add(back2DataLabel);
		statPanel.add(back3DataLabel);
		statPanel.add(portDataLabel);
		statPanel.add(pathDataLabel);
		statPanel.add(trafficDataLabel);

		notifier = getTestPanel().getNotifier();
		

		tz = new SimpleTimeZone(-21600000, "America/Central", Calendar.APRIL, 1, -Calendar.SUNDAY, 7200000, Calendar.OCTOBER, -1, Calendar.SUNDAY, 7200000, 3600000);
		gc = new GregorianCalendar(tz);
		getTimeStamp();
	}
	
	public AcsFileIO getAcsFileIO()
	{
		if(acsFileIO == null)
		{
			acsFileIO = new AcsFileIO(settings.getPath());
			{
			};
		}
		return acsFileIO;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	private class heartbeatTask extends TimerTask
	{
		/**
		 * Run gets called when a timer expires.  Timers are
		 * set to ensure logon processing and message blocks
		 * arrive in timely fashion.  A timer going off
		 * indicates an error that will be processed
		 */
		public void run()
		{
            // this number is reset when we receive a heartbeat
			retryHeartbeat++;
			SwingUtilities.invokeLater(new FdeLogger(log, "Heartbeat timeout: " + getTimeStamp(), 4));
			//write message to traffic log file
			try
			{
				FileWriter traffic = new FileWriter("resource/acs_traffic_log.txt", true);
				traffic.write(getDebugTimeStamp() + "-------------------10 SECOND HEARTBEAT TIMED OUT-------------------" + "\n");
				traffic.close();
			} catch (Exception e)
			{
				System.out.println(getDebugTimeStamp() + "Error with traffic stream");
				e.printStackTrace(System.out);
			}

			if (retryHeartbeat == 6)
			{
                // we haven't received a heartbeat in the last 6 * 10 seconds
				SwingUtilities.invokeLater(new FdeLogger(log, "Closing Connection: too many timeouts", 3));
				closeConnection();

				//write message to traffic log file
				try
				{
					FileWriter traffic = new FileWriter("resource/acs_traffic_log.txt", true);
					traffic.write(getDebugTimeStamp() + "---------DISCONNECTING DUE TO 60 SECOND TIME OUT FROM YUKON--------" + "\n");
					traffic.close();
				} catch (Exception e)
				{
					System.out.println(getDebugTimeStamp() + "Error with traffic stream");
					e.printStackTrace(System.out);
				}
				notifier.setActionCode(FDTestPanelNotifier.ACTION_CONNECTION_LOST);
			}
			// send heartbeat
			try
			{
                if (hbeat) {
                    synchronized (out) {
        				out.writeShort(0);
        				out.writeBytes(getTimeStamp());
        				for (int i = 0; i < 12; i++)
        				{
        					out.writeByte(0);
        				}
                    }
    				FileWriter traffic = new FileWriter(trafficFile, true);
    				traffic.write(getDebugTimeStamp() + "SENT              " + "Heartbeat messsage due to heartbeat timer timeout------------------------" + "\n");
    				traffic.close();
                }
			} catch (Exception e)
			{
				SwingUtilities.invokeLater(new FdeLogger(log, "Error writing heartbeat", 3));
			}
		}
	}

	// Run method for point send interval timer
	private class intervalTask extends TimerTask
	{
		/**
		 * run gets called when the interval timer expires,
		 * sendPoints gets called to send all points whose intervals are up
		 */
		public void run()
		{
			thirtySecond++;
			sixtySecond++;
			fiveMinute++;
			fifteenMinute++;
			sixtyMinute++;
			if (thirtySecond == 4)
			{
				thirtySecond = 1;
			}
			if (sixtySecond == 7)
			{
				sixtySecond = 1;
			}
			if (fiveMinute == 31)
			{
				fiveMinute = 1;
			}
			if (fifteenMinute == 91)
			{
				fifteenMinute = 1;
			}
			if (sixtyMinute == 361)
			{
				sixtyMinute = 1;
			}
			if (flipflop10 == 0)
			{
				flipflop10 = 1;
			} else if (flipflop10 == 1)
			{
				flipflop10 = 0;
			}
			if (thirtySecond == 3)
			{
				if (flipflop30 == 0)
				{
					flipflop30 = 1;
				} else if (flipflop30 == 1)
				{
					flipflop30 = 0;
				}
			}
			if (sixtySecond == 6)
			{
				if (flipflop60 == 0)
				{
					flipflop60 = 1;
				} else if (flipflop60 == 1)
				{
					flipflop60 = 0;
				}
			}
			if (fiveMinute == 30)
			{
				if (flipflop300 == 0)
				{
					flipflop300 = 1;
				} else if (flipflop300 == 1)
				{
					flipflop300 = 0;
				}
			}
			if (fifteenMinute == 90)
			{
				if (flipflop900 == 0)
				{
					flipflop900 = 1;
				} else if (flipflop900 == 1)
				{
					flipflop900 = 0;
				}
			}
			if (sixtyMinute == 360)
			{
				if (flipflop3600 == 0)
				{
					flipflop3600 = 1;
				} else if (flipflop3600 == 1)
				{
					flipflop3600 = 0;
				}
			}
			System.out.println("60sec: "+ sixtySecond + " 5min: "+ fiveMinute + " 15min: " + fifteenMinute + " 60min: " + sixtyMinute);
			sendPoints();
		}
	}

	//	Run method for sending five minute time sync
	private class timesyncTask extends TimerTask
	{
		/**
		 * Run gets called when the timer expires, just sends a timesync to fdr who will
		 * probably ignore it.
		 */
		public void run()
		{
			try
			{
                synchronized (out) {
    				out.writeShort(ACS_TIMESYNC);
    				out.writeBytes(getTimeStamp());
    				for (int i = 0; i < 12; i++)
    				{
    					out.writeByte(0);
    				}
                }
			} catch (Exception e)
			{
				System.out.println(getDebugTimeStamp() + "Error sending timesync");
				e.printStackTrace(System.out);
			}
			SwingUtilities.invokeLater(new FdeLogger(log, "SENT TimeSync Message " + getTimeStamp(), 1));

			// Write message to traffic log file
			try
			{
				FileWriter traffic = new FileWriter(trafficFile, true);
				traffic.write(getDebugTimeStamp() + "SENT ON INTERVAL: TIME SYNC MESSAGE---------------------\n");
				traffic.close();
			} catch (IOException e)
			{
				System.out.println(getDebugTimeStamp() + "IO Error with traffic stream ");
				e.printStackTrace(System.out);
			}
		}
	}

	public void run()
	{
		try
		{

			//for (;;)
			Thread thisThread = Thread.currentThread();
			while(readThread == thisThread)
			{
				synchronized (this)
				{
					readBytes();
				}
			}
		} catch (Exception e)
		{
			System.out.println(getDebugTimeStamp() + "Error in read thread run method");
			e.printStackTrace(System.out);
		}
	}
	public void stop()
	{
		Thread killme = readThread;
		readThread = null;
		killme.interrupt();
	}

	// Method to make sure settings are done b4 starting
	public boolean settingDone()
	{
		return settings.isSet();
	}

	public JPanel getStatPanel()
	{
		return statPanel;
	}

	public FDTestPanel getTestPanel()
	{
		return testPanel;
	}

	public ACSSettings getSettings()
	{
		return settings;
	}

	public void updateStats()
	{
		serverDataLabel.setText(settings.getServer());
		back1DataLabel.setText(settings.getBack1());
		back2DataLabel.setText(settings.getBack2());
		back3DataLabel.setText(settings.getBack3());
		portDataLabel.setText(settings.getPort());
		pathDataLabel.setText(settings.getPath());
		trafficDataLabel.setText(settings.getTrafficPath());
	}

	public boolean startConnection()
	{
		retryStart++;
		if (retryStart == 5)
			retryStart = 1;
		// get settings
		if (settings.getServer().equals(""))
		{
			yukon_host = DEFAULT_YUKON_HOST;
		} else
		{
			yukon_host = settings.getServer();
		}
		YUKON_BACKUP1 = settings.getBack1();
		YUKON_BACKUP2 = settings.getBack2();
		YUKON_BACKUP3 = settings.getBack3();
		if (settings.getPath().equals(""))
		{
			pointFile = DEFAULT_POINTFILE;
		} else
		{
			pointFile = settings.getPath();
		}
		if (settings.getTrafficPath().equals(""))
		{
			trafficFile = DEFAULT_TRAFFICFILE;
		} else
		{
			trafficFile = settings.getTrafficPath();
		}
		if (settings.getPort().equals(""))
		{
			yukon_port = DEFAULT_YUKON_PORT;
		} else
		{
			yukon_port = Integer.parseInt(settings.getPort());
		}
		if (retryStart == 1)
		{
			server = yukon_host;
		} else if (retryStart == 2)
		{
			server = YUKON_BACKUP1;
		} else if (retryStart == 3)
		{
			server = YUKON_BACKUP2;
		} else
		{
			server = YUKON_BACKUP3;
		}
		// Create Socket
		try
		{
			fdeSocket = new Socket(server, yukon_port);
			SwingUtilities.invokeLater(new FdeLogger(log, "Connected to server socket", 0));
		} catch (Exception e)
		{
			if (e.toString().equalsIgnoreCase("java.net.UnknownHostException"))
			{
				System.out.println(getDebugTimeStamp() + yukon_host + " not visible on the network: ");
				SwingUtilities.invokeLater(new FdeLogger(log, yukon_host + " is not visible on the network", 3));
				return false;
			} else if (e.toString().equalsIgnoreCase("java.io.IOException"))
			{
				System.out.println(getDebugTimeStamp() + "IOException when creating socket");
				SwingUtilities.invokeLater(new FdeLogger(log, "IOException when creating socket", 3));
				return false;
			} else if (e.toString().equalsIgnoreCase("java.net.ConnectException: Connection refused: connect"))
			{
				System.out.println(getDebugTimeStamp() + "ConnectException: connection refused");
				SwingUtilities.invokeLater(new FdeLogger(log, "ConnectException when creating socket: connection refused", 3));
				return false;
			} else
			{
				e.printStackTrace(System.out);
				return false;
			}
		}

		SwingUtilities.invokeLater(new FdeLogger(log, "Connected on server " + retryStart, 0));
		if (retryStart == 1)
		{
			serverDataLabel.setBackground(Color.BLUE);
			back1DataLabel.setBackground(Color.BLACK);
			back2DataLabel.setBackground(Color.BLACK);
			back3DataLabel.setBackground(Color.BLACK);
		} else if (retryStart == 2)
		{
			back1DataLabel.setBackground(Color.BLUE);
			serverDataLabel.setBackground(Color.BLACK);
			back2DataLabel.setBackground(Color.BLACK);
			back3DataLabel.setBackground(Color.BLACK);
		} else if (retryStart == 3)
		{
			back2DataLabel.setBackground(Color.BLUE);
			serverDataLabel.setBackground(Color.BLACK);
			back1DataLabel.setBackground(Color.BLACK);
			back3DataLabel.setBackground(Color.BLACK);
		} else if (retryStart == 4)
		{
			back3DataLabel.setBackground(Color.BLUE);
			serverDataLabel.setBackground(Color.BLACK);
			back1DataLabel.setBackground(Color.BLACK);
			back2DataLabel.setBackground(Color.BLACK);
		}

		// Create data output stream
		try
		{
			out = new DataOutputStream(fdeSocket.getOutputStream());
			SwingUtilities.invokeLater(new FdeLogger(log, "Output stream created successfully", 0));
		} catch (IOException e)
		{
			System.out.println(getDebugTimeStamp() + "Could not extablish output stream to " + yukon_host + ": " + e);
			SwingUtilities.invokeLater(new FdeLogger(log, "Could not establish output stream to " + yukon_host, 3));
		}

		// Create buffered input stream
		try
		{
			in = new DataInputStream(fdeSocket.getInputStream());
			log.append("Input stream created successfully", 0);
		} catch (Exception e)
		{
			System.out.println(getDebugTimeStamp() + "Error creating input stream");
			e.printStackTrace(System.out);
			SwingUtilities.invokeLater(new FdeLogger(log, "Error creating input stream", 3));
		}

		// Create recieve log random access file
		try
		{
			pointList = new RandomAccessFile("resource/acs_yukon_points.txt", "rw");
		} catch (Exception e)
		{
			System.out.println(getDebugTimeStamp() + "Error creating recv log file");
			SwingUtilities.invokeLater(new FdeLogger(log, "Error creating recv log file", 3));
		}

		// Start thread for reading
		readThread = new Thread(this, "ACS READ THREAD");
		readThread.start();

		// Start timers
		heartbeat = new Timer();
		heartbeat.schedule(new heartbeatTask(), T1_TIME, T1_TIME);
		interval = new Timer();
		interval.schedule(new intervalTask(), T2_TIME, T2_TIME);
		timesync = new Timer();
		timesync.schedule(new timesyncTask(), T3_TIME, T3_TIME);

		// Write message to traffic log file
		try
		{

			FileWriter traffic = new FileWriter(trafficFile, true);
			traffic.write(getDebugTimeStamp() + "----------FDE ACS EMULATOR STARTED SUCESSFULLY---------\n");
			traffic.close();
		} catch (Exception e)
		{
			System.out.println(getDebugTimeStamp() + "Error with traffic stream " + e);
		}

		// fill point array with points from point file
		pointarray = getAcsFileIO().getAcsPointsFromFile();

		return true;
	}

	// Send random points based on interval from file as timers expire
	public void sendPoints()
	{

		//System.out.println(getDebugTimeStamp() + "ACS   Counters: 30 sec: " + thirtySecond + "        60 sec: " + sixtySecond + "        5 min: " + fiveMinute + "        15 min: " + fifteenMinute + "        60 min: " + sixtyMinute);
		int exit = 0;
		int i = 0;
		ACSPoint nextpoint;
		ACSPoint lastpoint;
		float value = 0.0f;
		while (exit != 1)
		{
			try
			{
				nextpoint = (ACSPoint) pointarray[i];
				lastpoint = (ACSPoint) pointarray[i + 1];
				try
				{
					if (lastpoint.getPointType().equalsIgnoreCase(""))
					{
						exit = 1;
					}
				} catch (Exception e)
				{
					if ("java.lang.NullPointerException".equalsIgnoreCase(e.toString()))
					{
						exit = 1;
					} else
					{
						e.printStackTrace(System.out);
						exit = 1;
					}
				}

				if (nextpoint.getPointInterval() == 10)
				{
				    value = sendPointWorker(nextpoint, value);
				} else if (nextpoint.getPointInterval() == 30 && thirtySecond == 3)
				{
				    value = sendPointWorker(nextpoint, value);
                } else if (nextpoint.getPointInterval() == 60 && sixtySecond == 6)
				{
				    value = sendPointWorker(nextpoint, value);
                } else if (nextpoint.getPointInterval() == 300 && fiveMinute == 30)
				{
				    value = sendPointWorker(nextpoint, value);
				} else if (nextpoint.getPointInterval() == 900 && fifteenMinute == 90)
				{
                    value = sendPointWorker(nextpoint, value);
				} else if (nextpoint.getPointInterval() == 3600 && sixtyMinute == 360)
				{
					value = sendPointWorker(nextpoint, value);
				}
				i++;
			} catch (Exception e)
			{
				e.printStackTrace(System.out);
				exit = 1;
			}
		}
	}

	private float sendPointWorker(ACSPoint nextpoint, float value) {
	    if ("Value".equalsIgnoreCase(nextpoint.getPointType()))
	    {
	        try
	        {
	            synchronized (out) {
	                out.writeShort(ACS_VALUE);
	                out.writeBytes(getTimeStamp());
	                out.writeShort(nextpoint.getPointRemote());
	                out.writeShort(nextpoint.getPointNumber());
	                out.writeBytes(nextpoint.getPointCategory());
	                out.writeByte(0);
	                if ("RANDOM".equalsIgnoreCase(nextpoint.getPointFunction()))
	                {
	                    Double maxdouble = new Double(nextpoint.getPointMax());
	                    int first = gen.nextInt(maxdouble.intValue());
	                    float last = gen.nextFloat();
	                    value = first + last;
	                    out.writeFloat(value);
	                    out.writeShort(1);
	                    
	                } else if ("PYRAMID".equalsIgnoreCase(nextpoint.getPointFunction()))
	                {
	                    double currentvalue = nextpoint.getPointCurrentValue();
	                    double incriment = nextpoint.getPointDelta();
	                    if ((currentvalue + incriment) >= nextpoint.getPointMax())
	                    {
	                        Double cvdouble = new Double(currentvalue);
	                        value = cvdouble.floatValue();
	                        out.writeFloat(value);
	                        out.writeShort(1);
	                        currentvalue = currentvalue + incriment;
	                        
	                        nextpoint.setPointCurrentValue(currentvalue);
	                        nextpoint.setPointDelta(nextpoint.getPointDelta() - (2 * nextpoint.getPointDelta()));
	                    } else if ((currentvalue + incriment) <= nextpoint.getPointMax())
	                    {
	                        Double cvdouble = new Double(currentvalue);
	                        value = cvdouble.floatValue();
	                        out.writeFloat(value);
	                        out.writeShort(1);
	                        currentvalue = currentvalue + incriment;
	                        
	                        nextpoint.setPointCurrentValue(currentvalue);
	                        nextpoint.setPointDelta(nextpoint.getPointDelta() + (-2 * nextpoint.getPointDelta()));
	                    } else
	                    {
	                        Double cvdouble = new Double(currentvalue);
	                        value = cvdouble.floatValue();
	                        out.writeFloat(value);
	                        out.writeShort(1);
	                        currentvalue = currentvalue + incriment;
	                        
	                        nextpoint.setPointCurrentValue(currentvalue);
	                    }
	                    
	                } else if ("DROPOFF".equalsIgnoreCase(nextpoint.getPointFunction()))
	                {
	                    double currentvalue = nextpoint.getPointCurrentValue();
	                    if (nextpoint.getPointMaxStart())
	                    {
	                        Double cvdouble = new Double(currentvalue);
	                        value = cvdouble.floatValue();
	                        out.writeFloat(value);
	                        out.writeShort(1);
	                        currentvalue = currentvalue - nextpoint.getPointDelta();
	                        
	                        if (currentvalue < nextpoint.getPointMin())
	                        {
	                            nextpoint.setPointCurrentValue(nextpoint.getPointMax());
	                        } else
	                            nextpoint.setPointCurrentValue(currentvalue);
	                    } else
	                    {
	                        Double cvdouble = new Double(currentvalue);
	                        value = cvdouble.floatValue();
	                        out.writeFloat(value);
	                        out.writeShort(1);
	                        currentvalue = currentvalue + nextpoint.getPointDelta();
	                        
	                        if (currentvalue > nextpoint.getPointMax())
	                        {
	                            nextpoint.setPointCurrentValue(nextpoint.getPointMin());
	                        } else
	                            nextpoint.setPointCurrentValue(currentvalue);
	                    }
	                    
	                }
	            }
	        } catch (Exception e)
	        {
	            System.out.println(getDebugTimeStamp() + "Error writing in 10 second interval");
	            e.printStackTrace(System.out);
	        }
	        
	        // Write message to traffic log file
	        try
	        {
	            
	            FileWriter traffic = new FileWriter(trafficFile, true);
	            traffic.write(getDebugTimeStamp() + "SENT ON INTERVAL: " + nextpoint.getPointType() + "   ");
	            
	            traffic.write(" R: " + nextpoint.getPointRemote() + " P: " + nextpoint.getPointNumber() + " C: " + nextpoint.getPointCategory() + " Point: " + value + "\n");
	            
	            traffic.close();
	        } catch (Exception e)
	        {
	            System.out.println(getDebugTimeStamp() + "Error with traffic stream");
	            e.printStackTrace(System.out);
	        }
	        SwingUtilities.invokeLater(new FdeLogger(log, "SENT ON INTERVAL: " + "Value" + " R: " + nextpoint.getPointRemote() + " P: " + nextpoint.getPointNumber() + " C: " + nextpoint.getPointCategory() + " Point: " + value + " " + getDebugTimeStamp() + "\n", 1));
	    } else if ("Status".equalsIgnoreCase(nextpoint.getPointType()))
	    {
	        String valueString = "";
	        if (flipflop3600 == 0)
	        {
	            valueString = "Open";
	        } else
	            valueString = "Close";
	        try
	        {
	            synchronized (out) {
	                out.writeShort(ACS_STATUS);
	                out.writeBytes(getTimeStamp());
	                out.writeShort(nextpoint.getPointRemote());
	                out.writeShort(nextpoint.getPointNumber());
	                out.writeBytes(nextpoint.getPointCategory());
	                out.writeByte(0);
	                out.writeShort(flipflop3600);
	                out.writeShort(1);
	                out.writeShort(0);
	            }
	        } catch (Exception e)
	        {
	            System.out.println(getDebugTimeStamp() + "Error writing in 10 second interval");
	            e.printStackTrace(System.out);
	        }
	        // Write message to traffic log file
	        try
	        {
	            FileWriter traffic = new FileWriter(trafficFile, true);
	            traffic.write(getDebugTimeStamp() + "SENT ON INTERVAL: " + nextpoint.getPointType() + "  ");
	            
	            traffic.write(" R: " + nextpoint.getPointRemote() + " P: " + nextpoint.getPointNumber() + " C: " + nextpoint.getPointCategory() + " Point: " + value + "\n");
	            
	            traffic.close();
	        } catch (Exception e)
	        {
	            System.out.println(getDebugTimeStamp() + "Error with traffic stream");
	            e.printStackTrace(System.out);
	        }
	        
	        SwingUtilities.invokeLater(new FdeLogger(log, "SENT ON INTERVAL: " + "Status" + " R: " + nextpoint.getPointRemote() + " P: " + nextpoint.getPointNumber() + " C: " + nextpoint.getPointCategory() + " Point: " + valueString + " " + getDebugTimeStamp() + "\n", 1));
	        
	    } else if ("Control".equalsIgnoreCase(nextpoint.getPointType()))
	    {
	        String valueString = "";
	        if (flipflop3600 == 0)
	        {
	            valueString = "Open";
	        } else
	            valueString = "Close";
	        try
	        {
	            synchronized (out) {
	                out.writeShort(ACS_CONTROL);
	                out.writeBytes(getTimeStamp());
	                out.writeShort(nextpoint.getPointRemote());
	                out.writeShort(nextpoint.getPointNumber());
	                out.writeBytes(nextpoint.getPointCategory());
	                out.writeByte(0);
	                
	                out.writeShort(flipflop3600);
	                out.writeInt(0);
	            }
	        } catch (Exception e)
	        {
	            System.out.println(getDebugTimeStamp() + "Error writing in 10 second interval");
	            e.printStackTrace(System.out);
	        }
	        // Write message to traffic log file
	        try
	        {
	            
	            FileWriter traffic = new FileWriter(trafficFile, true);
	            traffic.write(getDebugTimeStamp() + "SENT ON INTERVAL: " + nextpoint.getPointType() + " ");
	            traffic.write(" R: " + nextpoint.getPointRemote() + " P: " + nextpoint.getPointNumber() + " C: " + nextpoint.getPointCategory() + " Point: " + value + "\n");
	            
	            traffic.close();
	        } catch (Exception e)
	        {
	            System.out.println(getDebugTimeStamp() + "Error with traffic stream ");
	            e.printStackTrace(System.out);
	        }
	        
	        SwingUtilities.invokeLater(new FdeLogger(log, "SENT ON INTERVAL: " + "Control" + " R: " + nextpoint.getPointRemote() + " P: " + nextpoint.getPointNumber() + " C: " + nextpoint.getPointCategory() + " Point: " + valueString + " " + getDebugTimeStamp() + "\n", 1));
	        
	    }
	    return value;
	}

	// Send all points from array reguardless of interval
	public void sendAllPoints()
	{
	    
	    int exit = 0;
	    int i = 0;
	    ACSPoint nextpoint;
	    ACSPoint lastpoint;
	    float value = 0.0f;
	    while (exit != 1)
	    {
	        try
	        {
	            nextpoint = (ACSPoint) pointarray[i];
	            lastpoint = (ACSPoint) pointarray[i + 1];
	            
	            try
	            {
	                if (lastpoint.getPointType().equalsIgnoreCase(""))
	                {
	                    exit = 1;
	                }
	            } catch (Exception e)
	            {
	                if (e.toString().equalsIgnoreCase("java.lang.NullPointerException"))
	                {
	                    exit = 1;
	                } else
	                {
	                    e.printStackTrace(System.out);
	                    exit = 1;
	                }
	            }
	            value = sendPointWorker(nextpoint, value);
	            i++;
	        } catch (Exception e)
	        {
	            if (e.toString().equals("java.lang.NullPointerException"))
	            {
	                System.out.println(getDebugTimeStamp() + "Error writing from file while sending all points " + pointFile);
	            }
	            e.printStackTrace(System.out);
	            exit = 1;
	        }
	    }
	    
	    SwingUtilities.invokeLater(new FdeLogger(log, "Finished sending points: " + getFormalTimeStamp(), 0));
	}
	
	public void sendManual(String mtype, int mremote, int mpointNum, String mcategory, String mpoint, int mquality)
	{
	    
	    if ("Value".equalsIgnoreCase(mtype))
	    {
	        
	        try
	        {
	            Thread.sleep(50);
	            synchronized (out) {
	                out.writeShort(ACS_VALUE);
	                out.writeBytes(getTimeStamp());
	                out.writeShort(mremote);
	                out.writeShort(mpointNum);
	                out.writeBytes(mcategory);
	                out.writeByte(0);
	                // four byte float value
	                float mfloat = Float.parseFloat(mpoint);
	                out.writeFloat(mfloat);
	                out.writeShort(mquality);
	            }
	            SwingUtilities.invokeLater(new FdeLogger(log, "SENT ON MANUAL: Value: R: " + mremote + " P: " + mpointNum + " C: " + mcategory + " Point: " + mpoint, 1));
	            
	            // Write message to traffic log file
	            try
	            {
	                FileWriter traffic = new FileWriter(trafficFile, true);
	                traffic.write(getDebugTimeStamp() + "SENT ON MANUAL:   Value " + " R: " + mremote + " P: " + mpointNum + " C: " + mcategory + " Point: " + mpoint + "\n");
	                traffic.close();
	            } catch (Exception e)
	            {
	                System.out.println(getDebugTimeStamp() + "Error with traffic stream while sending manually");
	                e.printStackTrace(System.out);
	            }
	        } catch (Exception E)
	        {
	            System.out.println(getDebugTimeStamp() + "Error writing value manually");
	            E.printStackTrace(System.out);
	        }
	        
	    } else if ("Status".equalsIgnoreCase(mtype))
	    {
	        try
	        {
	            Thread.sleep(50);
	            synchronized (out) {
	                out.writeShort(ACS_STATUS);
	                out.writeBytes(getTimeStamp());
	                out.writeShort(mremote);
	                out.writeShort(mpointNum);
	                out.writeBytes(mcategory);
	                out.writeByte(0);
	                int mint;
	                try
	                {
	                    mint = Integer.parseInt(mpoint);
	                } catch (NumberFormatException e)
	                {
	                    mint = 0;
	                }
	                out.writeShort(mint);
	                out.writeShort(mquality);
	                out.writeShort(0);
	            }
	            String state = "";
	            if (mpoint.equals("1"))
	            {
	                state = "Close";
	            } else
	                state = "Open";
	            SwingUtilities.invokeLater(new FdeLogger(log, "SENT ON MANUAL: Status: R: " + mremote + " P: " + mpointNum + " C: " + mcategory + " Point: " + state, 1));
	            
	            // Write message to traffic log file
	            try
	            {
	                FileWriter traffic = new FileWriter(trafficFile, true);
	                traffic.write(getDebugTimeStamp() + "SENT ON MANUAL:   Status " + " R: " + mremote + " P: " + mpointNum + " C: " + mcategory + " Point: " + state + "\n");
	                traffic.close();
	            } catch (Exception e)
	            {
	                System.out.println(getDebugTimeStamp() + "Error with traffic stream while sending manually");
	                e.printStackTrace(System.out);
	            }
	        } catch (Exception E)
	        {
	            System.out.println(getDebugTimeStamp() + "Error writing status manually");
	            E.printStackTrace(System.out);
	        }
	    } else if ("Control".equalsIgnoreCase(mtype))
	    {
	        
	        try
	        {
	            Thread.sleep(50);
	            synchronized (out) {
	                out.writeShort(ACS_CONTROL);
	                out.writeBytes(getTimeStamp());
	                out.writeShort(mremote);
	                out.writeShort(mpointNum);
	                out.writeBytes(mcategory);
	                out.writeByte(0);
	                int mint;
	                try
	                {
	                    mint = Integer.parseInt(mpoint);
	                } catch (NumberFormatException e)
	                {
	                    mint = 0;
	                }
	                out.writeShort(mint);
	                out.writeInt(0);
	            }
	            String state = "";
	            if (mpoint.equals("1"))
	            {
	                state = "Close";
	            } else
	                state = "Open";
	            SwingUtilities.invokeLater(new FdeLogger(log, "SENT ON MANUAL: Control: R: " + mremote + " P: " + mpointNum + " C: " + mcategory + " Point: " + state, 1));
	            
	            // Write message to traffic log file
	            try
	            {
	                FileWriter traffic = new FileWriter(trafficFile, true);
	                traffic.write(getDebugTimeStamp() + "SENT ON MANUAL:   Control " + " R: " + mremote + " P: " + mpointNum + " C: " + mcategory + " Point: " + state + "\n");
	                traffic.close();
	            } catch (Exception e)
	            {
	                System.out.println(getDebugTimeStamp() + "Error with traffic stream while sending manually");
	                e.printStackTrace(System.out);
	            }
	        } catch (Exception E)
	        {
	            System.out.println(getDebugTimeStamp() + "Error writing control manually");
	            E.printStackTrace(System.out);
	        }
	    }
	}

	// Method for reading input from socket into a byte array
	public void readBytes()
	{
		int function = 0;
		int remoteNum = 0;
		int pointNum = 0;
		char category;
		byte firstchar;
		float rFloatValue;
		int rStatusValue;
		int quality;

		byte[] b = new byte[2048];
		byte[] time = new byte[2048];
		try
		{
			quit = 0;
			// Read from socket queue, If excecption occurs cancel the read thread and close connection and reconnects
			try
			{
				function = in.readShort();
			} catch (Exception e)
			{
				System.out.println(e.toString());
				if( readThread == null )
				{
					quit = 1;
				}else
				{
					SwingUtilities.invokeLater(new FdeLogger(log, "Read Failed from Yukon socket connection.", 3));
					SwingUtilities.invokeLater(new FdeLogger(log, "Stopping read thread and closing connection...", 3));
					notifier.setActionCode(FDTestPanelNotifier.ACTION_CONNECTION_LOST);
					closeConnection();
					quit = 1;
				}
			}

			while (quit != 1)
			{
				heartbeat.cancel();
				retryHeartbeat = 0;
				//process based on message type
				switch (function)
				{
					case ACS_NULL :

						// Recieved a heartbeat message
					    SwingUtilities.invokeLater(new FdeLogger(log, "RECV: heartbeat", 2));
						in.read(time, 0, 16);
						in.read(b, 0, 12);
						FileWriter traffic = new FileWriter(trafficFile, true);
						traffic.write(getDebugTimeStamp() + "RECV              " + "Heartbeat messsage" + "\n");
						traffic.close();
						// send heartbeat reply
                        if (hbeat) {
                            synchronized (out) {
        						out.writeShort(0);
        						out.writeBytes(getTimeStamp());
        						for (int i = 0; i < 12; i++)
        						{
        							out.writeByte(0);
        						}
                            }
                            SwingUtilities.invokeLater(new FdeLogger(log, "SENT: heartbeat", 1));
    						FileWriter traffic2 = new FileWriter(trafficFile, true);
    						traffic2.write(getDebugTimeStamp() + "SENT              " + "Heartbeat messsage" + "\n");
    						traffic2.close();
                        }
						quit = 1;
						break;

					case ACS_VALUE :

						// Received an analog point message
						in.read(time, 0, 16);

						remoteNum = in.readShort();
						pointNum = in.readShort();
						firstchar = in.readByte();
						category = (char) firstchar;
						in.readByte();
						rFloatValue = in.readFloat();
						quality = in.readShort();
						SwingUtilities.invokeLater(new FdeLogger(log, "RECV: Value R: " + remoteNum + " P: " + pointNum + " C: " + category + " Value: " + rFloatValue + " Quality: " + quality + " TStamp: " + new String(time, 0, 16), 2));
						System.out.println(getDebugTimeStamp() + "ACS RECV: Value " + rFloatValue);
						//sendAllPoints();
						writeToFile("Value", remoteNum, pointNum, Character.toString(category), nf.format(rFloatValue));
						quit = 1;
						break;

					case ACS_STATUS :

						// Recieved a status point message
						in.read(time, 0, 16);
						remoteNum = in.readShort();
						pointNum = in.readShort();
						firstchar = in.readByte();
						category = (char) firstchar;
						in.readByte();
						rStatusValue = in.readShort();
						String statusValueString = "";
						if (rStatusValue == 1)
						{
							statusValueString = "Close";
						} else if (rStatusValue == 0)
						{
							statusValueString = "Open";
						}
						quality = in.readShort();
						in.readShort();
						SwingUtilities.invokeLater(new FdeLogger(log, "RECV: Status R: " + remoteNum + " P: " + pointNum + " C: " + category + " State: " + statusValueString + " Quality: " + quality + " TStamp: " + new String(time, 0, 16), 2));
						System.out.println(getDebugTimeStamp() + "ACS RECV: Status " + statusValueString);
						writeToFile("Status", remoteNum, pointNum, Character.toString(category), nf.format(rStatusValue));
						quit = 1;
						break;

					case ACS_CONTROL :

						// Received a control point message
						in.read(time, 0, 16);
						remoteNum = in.readShort();
						pointNum = in.readShort();
						firstchar = in.readByte();
						category = (char) firstchar;
						in.readByte();
						rStatusValue = in.readShort();
						String controlValueString = "";
						if (rStatusValue == 1)
						{
							controlValueString = "Close";
						} else if (rStatusValue == 0)
						{
							controlValueString = "Open";
						}
						in.readInt();
						SwingUtilities.invokeLater(new FdeLogger(log, "RECV: Control R: " + remoteNum + " P: " + pointNum + " C: " + category + " State: " + controlValueString + " TStamp: " + new String(time, 0, 16), 2));
						System.out.println(getDebugTimeStamp() + "ACS RECV: Control " + controlValueString);
						writeToFile("Control", remoteNum, pointNum, Character.toString(category), nf.format(rStatusValue));
						quit = 1;
						break;

					default :

						// Received unknown message
						SwingUtilities.invokeLater(new FdeLogger(log, "Received unknown message type", 3));
						System.out.println(getDebugTimeStamp() + "Received unknown message type");
						quit = 1;
						break;
				}
				heartbeat = new Timer();
				heartbeat.schedule(new heartbeatTask(), T1_TIME, T1_TIME);
			}
		} catch (Exception e)
		{
			SwingUtilities.invokeLater(new FdeLogger(log, "Error reading from input stream", 3));
			if (e.toString().equals("java.lang.NullPointerException"))
			{
				SwingUtilities.invokeLater(new FdeLogger(log, "Input stream was null", 3));
			}
			e.printStackTrace(System.out);
		}

		return;
	}

	// Write recieved message to point list file and traffic log file
	public void writeToFile(String ftype, int fremoteNum, int fpointNum, String fcategory, String fpoint)
	{

		boolean newpoint = false;

		// make buffered reader
		BufferedReader fileBuffer = null;
		try
		{
			fileBuffer = new BufferedReader(new FileReader("resource/acs_yukon_points.txt"));
		} catch (Exception e)
		{
			if ("java.io.FileNotFoundException".equalsIgnoreCase(e.toString()))
			{
				System.out.println("File not found when trying to load points from file");
			} else
				e.printStackTrace(System.out);
		}

		String[] pointAsStrings = new String[500];
		//		fill points array with point from yukon file

		int i = 0;
		int k = 0;
		int l = 0;

		while (l != 1)
		{
			try
			{
				String pointline = fileBuffer.readLine();

				k++;
				if ("EOF".equalsIgnoreCase(pointline))
				{

					l = 1;
				} else
				{
					StringTokenizer st = new StringTokenizer(pointline, ";");
					String pointtype = st.nextToken();
					String pointremote = st.nextToken();
					String pointnumber = st.nextToken();
					String pointcategory = st.nextToken();
					String pointvalue = st.nextToken();
					String lineEntry = pointtype + ";" + pointremote + ";" + pointnumber + ";" + pointcategory + ";" + pointvalue;

					pointAsStrings[i] = lineEntry;

					i++;
				}

			} catch (Exception e)
			{
				System.out.println("Error getting points from file");
				e.printStackTrace(System.out);
				break;
			}
		}

		int out = 0;

		try
		{
			fileBuffer = new BufferedReader(new FileReader("resource/acs_yukon_points.txt"));
		} catch (Exception e)
		{
			if ("java.io.FileNotFoundException".equalsIgnoreCase(e.toString()))
			{
				System.out.println("File not found when trying to load points from file");
			} else
				e.printStackTrace(System.out);
		}

		String currentLine = "";
		// is point new or not?
		while (out != 1)
		{
			try
			{
				currentLine = fileBuffer.readLine();
				StringTokenizer ft = new StringTokenizer(currentLine, ";");

				if ("EOF".equals(currentLine))
				{
					// Point does not exist, set newpoint to true
					try
					{

						SwingUtilities.invokeLater(new FdeLogger(log, "Unexpected point from Yukon: ", 4));
						newpoint = true;
						out = 1;

					} catch (Exception e)
					{

						System.out.println(getDebugTimeStamp() + "Error writing new point to end of point file");
						e.printStackTrace(System.out);
						out = 1;
					}
				} else
				{

					String type = ft.nextToken();
					Integer remoteNumInt = new Integer(ft.nextToken());
					int remoteNum = remoteNumInt.intValue();
					Integer pointNumInt = new Integer(ft.nextToken());
					int pointNum = pointNumInt.intValue();
					String category = ft.nextToken();

					if (ftype.equalsIgnoreCase(type) && fremoteNum == remoteNum && fpointNum == pointNum && fcategory.equalsIgnoreCase(category))
					{

						newpoint = false;
						out = 1;
					}
				}
			} catch (Exception e)
			{
				if (e.toString().equals("java.lang.NullPointerException"))
				{

					System.out.println(getDebugTimeStamp() + "EOF ERROR while writing to file");
					SwingUtilities.invokeLater(new FdeLogger(log, "EOF ERROR while writing to file", 3));

				}
				e.printStackTrace(System.out);
				out = 1;
			}
		}

		if (newpoint)
		{
			try
			{
				FileWriter yukonFileWriter = new FileWriter(new File("resource/acs_yukon_points.txt"));

				for (int j = 0; j < pointAsStrings.length; j++)
				{
					try
					{
						if (pointAsStrings[j] == null)
						{
							// we are done writing our previous points out to new file
							break;
						} else
						{

							yukonFileWriter.write(pointAsStrings[j] + LF);

						}

					} catch (Exception e)
					{
						if (e.toString().equalsIgnoreCase("java.lang.NullPointerException"))
						{
							break;
						}
						e.printStackTrace(System.out);
					}

				}
				String formattedPoint = "";
				if ("Value".equalsIgnoreCase(ftype))
				{
					formattedPoint = fpoint;
				} else
				{
					if ("1.0".equals(fpoint))
					{
						formattedPoint = "Close";
					} else
					{
						formattedPoint = "Open  ";
					}
				}

				yukonFileWriter.write(ftype + ";" + fremoteNum + ";" + fpointNum + ";" + fcategory + ";" + formattedPoint + LF);
				yukonFileWriter.write("EOF" + LF);
				yukonFileWriter.write("#TO CLEAR THIS FILE, DELETE ALL LINES UP T0 'EOF' LEAVING EOF AT THE TOP" + LF);
				yukonFileWriter.write("#THIS IS LIST OF POINTS RECIEVED FROM YUKON, WHEN POINTS ARE RECIEVED OLD ONES ARE REWRITTEN WITH NEW VALUES");
				yukonFileWriter.write("#NEW ONES ARE APPENED TO THE BOTTOM OF THE LIST");
				yukonFileWriter.flush();

			} catch (Exception e)
			{
				e.printStackTrace();
			}
		} else
		{
			try
			{
				FileWriter yukonFileWriter = new FileWriter(new File("resource/acs_yukon_points.txt"));

				for (int j = 0; i < pointAsStrings.length; j++)
				{
					try
					{

						StringTokenizer st = new StringTokenizer(pointAsStrings[j], ";");
						String type = st.nextToken();
						Integer remoteNumInt = new Integer(st.nextToken());
						int remoteNum = remoteNumInt.intValue();
						Integer pointNumInt = new Integer(st.nextToken());
						int pointNum = pointNumInt.intValue();
						String category = st.nextToken();

						if (type.equalsIgnoreCase(ftype) && remoteNum == fremoteNum && pointNum == fpointNum && category.equalsIgnoreCase(fcategory))
						{
							System.out.println("got here");
							String formattedPoint = "";
							if (ftype.equalsIgnoreCase("Value"))
							{
								formattedPoint = fpoint;
							} else
							{
								if (fpoint.equals("1.0"))
								{
									formattedPoint = "Close";
								} else
								{
									formattedPoint = "Open  ";
								}
							}

							yukonFileWriter.write(ftype + ";" + fremoteNum + ";" + fpointNum + ";" + fcategory + ";" + formattedPoint + LF);

						} else
						{
							yukonFileWriter.write(pointAsStrings[j] + LF);
						}
					} catch (Exception e)
					{

						if ("java.lang.NullPointerException".equalsIgnoreCase(e.toString()))
						{
							break;
						}
						e.printStackTrace(System.out);
					}

				}

				yukonFileWriter.write("EOF" + LF);
				yukonFileWriter.write("#TO CLEAR THIS FILE, DELETE ALL LINES UP T0 'EOF' LEAVING EOF AT THE TOP" + LF);
				yukonFileWriter.write("#THIS IS LIST OF POINTS RECIEVED FROM YUKON, WHEN POINTS ARE RECIEVED OLD ONES ARE REWRITTEN WITH NEW VALUES");
				yukonFileWriter.write("#NEW ONES ARE APPENED TO THE BOTTOM OF THE LIST");
				yukonFileWriter.flush();

			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		// Write message to traffic log file
		try
		{
			String pointword = "";
			if ("Value".equalsIgnoreCase(ftype))
			{
				pointword = fpoint;
			} else
			{
				if ("01.00".equals(fpoint))
				{
					pointword = "Close";
				} else
				{
					pointword = "Open";
				}
			}
			FileWriter traffic = new FileWriter(trafficFile, true);
			traffic.write(getDebugTimeStamp() + "RECV             " + ftype + " R: " + fremoteNum + " P: " + fpointNum + " C: " + fcategory + " Point: " + pointword + "\n");
			traffic.close();
		} catch (Exception e)
		{
			System.out.println(getDebugTimeStamp() + "Error with traffic stream while writing to traffic file");
			e.printStackTrace(System.out);
		}
	}

	public void hbOn(boolean on)
	{
		hbeat = on;
	}

	// returns protocol name
	public String getName()
	{
		return new String("ACS");
	}

	//	Creates a new timestamp
	public String getTimeStamp()
	{
		if (tz.inDaylightTime(gc.getTime()))
		{
			return df.format(new Date()) + "D ";
		} else
		{
			String date = df.format(new Date()) + "S ";
			return date;
		}

	}

	public String getFormalTimeStamp()
	{
		return fdf.format(new Date());
	}

	public String getDebugTimeStamp()
	{
		return dbdf.format(new Date()) + " ";
	}

	public void closeConnection()
	{
		try
		{
			heartbeat.cancel();
			interval.cancel();
			timesync.cancel();
			stop();
			Thread.sleep(10);
			out.close();
			in.close();
			fdeSocket.close();
		} catch (Exception e)
		{
			System.out.println(getDebugTimeStamp() + "Error closing connection");
			if ("java.lang.NullPointerException".equalsIgnoreCase(e.toString()))
			{
				System.out.println("canceled heartbeat timer that didn't exist yet");
			} else
				e.printStackTrace(System.out);
		}
	}

	public void listenForActions(Observer o)
	{
		notifier.addObserver(o);
	}

	public void editSettings()
	{
		settings = new ACSSettings("Settings", this);
		settings.listenForActions(testPanel.getWindow());
		settings.setVisible(true);
	}

}