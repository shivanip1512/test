/*
 * Created on May 19, 2004
 *
 */
package com.cannontech.fdemulator.protocols;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.net.Socket;
import java.text.*;
import java.util.*;
import java.io.*;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import com.cannontech.fdemulator.common.*;
import com.cannontech.fdemulator.fileio.*;

/**
 * @author ASolberg
 */
public class RdexProtocol extends FDEProtocol implements Runnable
{
	// settings gui
	private RdexSettings settings;

	// loop escape
	private int quit = 0;

	//socket and streams

	private TraceLogPanel log;
	private FDTestPanel testPanel;

	// processes incoming data
	private volatile Thread readThread = null;

	// connection variables
	private String RDEX_REG_NAME = "SIM";
	private String DEFAULT_YUKON_HOST = "127.0.0.1";
	private String yukon_host = null;
	private String YUKON_BACKUP1;
	private String YUKON_BACKUP2;
	private String YUKON_BACKUP3;
	private int DEFAULT_YUKON_PORT = 1670;
	private int yukon_port = 0;
	private String server;
	private int retryStart = 0;

	//message types
	private final int RDEX_NULL = 0;
	private final int RDEX_REG = 1;
	private final int RDEX_ACK = 2;
	private final int RDEX_VALUE = 101;
	private final int RDEX_STATUS = 102;
	private final int RDEX_CONTROL = 201;

	// timer variables
	private Timer heartbeat;
	private Timer interval;
	private int tenSecond = 0;
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
	private final static long T3_TIME = 1000; // one second timer
	private final static long T1_TIME = 10500; // 10.5 seconds, so we don't get a lot of timeouts
	private final static long T2_TIME = 10000;
	private int retryHeartbeat = 0;
	private boolean hbeat = false;

	// socket and file IO variables
	private Socket fdeSocket = null;
	private DataOutputStream out = null;
	private DataInputStream in = null;
	private final String DEFAULT_POINTFILE = "resource/rdex_points.cfg";
	private final String DEFAULT_TRAFFICFILE = "resource/rdex_traffic_log.txt";
	private String pointFile = null;
	private String trafficFile = null;
	private RdexFileIO rdexFileIO = null;

	private BufferedReader file = null;
	private BufferedReader file2 = null;
	private RandomAccessFile pointList;

	// message variables
	private String pointName;
	private String pointName2;
	private String pointType;
	private String pointType2;
	private String pointInterval;
	private int qual;

	// timestamp variables
	private static final String formatDesc = "yyyyMMddHHmmss";
	private static final String formatDesc2 = "MMMMM dd, yyyy HH:mm:ss";
	private static final String formatDesc3 = "MM/dd/yyyy HH:mm:ss";
	private DateFormat df = new SimpleDateFormat(formatDesc);
	private DateFormat fdf = new SimpleDateFormat(formatDesc2);
	private DateFormat dbdf = new SimpleDateFormat(formatDesc3);

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
	 * Main constructor. 
	 * Set TraceLogInstance to parent TestPanel's ouput log 
	 */
	public RdexProtocol(TraceLogPanel logPanel, FDTestPanel panel)
	{
		log = logPanel;
		testPanel = panel;

		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		nf.setMaximumIntegerDigits(5);
		nf.setMinimumFractionDigits(2);
		nf.setMinimumIntegerDigits(1);

		// Start settings frame
		settings = new RdexSettings("Settings", this);
		settings.listenForActions(testPanel.getWindow());
		// Center the window
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = settings.getSize();
		if (frameSize.height > screenSize.height)
		{
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width)
		{
			frameSize.width = screenSize.width;
		}
		settings.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		settings.setVisible(true);
		settings.show();
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

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	private class heartbeatTask extends TimerTask
	{
		/**
		 * run gets called when a timer expires.  Timers are
		 * set to ensure logon processing and message blocks
		 * arrive in timely fashion.  A timer going off
		 * indicates an error that will be processed
		 */
		public void run()
		{
			retryHeartbeat++;
			SwingUtilities.invokeLater(new Logger(log, "Heartbeat timeout: " + getTimeStamp(), 4));
			//write message to traffic log file
			try
			{
				FileWriter traffic = new FileWriter("resource/rdex_traffic_log.txt", true);
				traffic.write(getDebugTimeStamp() + "-------------------10 SECOND HEARTBEAT TIMED OUT-------------------" + "\n");
				traffic.close();
			} catch (Exception e)
			{
				System.out.println(getDebugTimeStamp() + "Error with traffic stream");
				e.printStackTrace(System.out);
			}

			if (retryHeartbeat == 6)
			{
				SwingUtilities.invokeLater(new Logger(log, "Closing Connection: too many timeouts", 3));
				closeConnection();

				//write message to traffic log file
				try
				{
					FileWriter traffic = new FileWriter("resource/rdex_traffic_log.txt", true);
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
				out.writeInt(0);
				out.writeBytes(getTimeStamp());
				for (int i = 0; i < 80; i++)
				{
					out.writeByte(0);
				}
				out.writeInt(0);
				out.writeInt(0);
				FileWriter traffic2 = new FileWriter(trafficFile, true);
				traffic2.write(getDebugTimeStamp() + "SENT              " + "Heartbeat messsage due to heartbeat timer timeout------------------------" + "\n");
				traffic2.close();
			} catch (Exception e)
			{
				System.out.println("Error sending heartbeat from hb timeout");
				e.printStackTrace(System.out);
			}
		}
	}

	// Run method for point send interval timer
	private class intervalTask extends TimerTask
	{
		/**
		 * Run gets called when the interval timer expires,
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
			System.out.println("60sec: " + sixtySecond + " 5min: " + fiveMinute + " 15min: " + fifteenMinute + " 60min: " + sixtyMinute);
			sendPoints();
		}
	}

	public void run()
	{
		try
		{

			//for (;;)
			Thread thisThread = Thread.currentThread();
			while (readThread == thisThread)
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

	public RdexSettings getSettings()
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
		// get the settings
		RDEX_REG_NAME = settings.getRegName();
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
			SwingUtilities.invokeLater(new Logger(log, "Connected to server socket", 0));
		} catch (Exception e)
		{
			if ("java.net.UnknownHostException".equalsIgnoreCase(e.toString()))
			{
				System.out.println(getDebugTimeStamp() + yukon_host + " not visible on the network: ");
				SwingUtilities.invokeLater(new Logger(log, yukon_host + " is not visible on the network", 3));
				return false;
			} else if ("java.io.IOException".equalsIgnoreCase(e.toString()))
			{
				System.out.println(getDebugTimeStamp() + "IOException when creating socket");
				SwingUtilities.invokeLater(new Logger(log, "IOException when creating socket", 3));
				return false;
			} else if ("java.net.ConnectException: Connection refused: connect".equalsIgnoreCase(e.toString()))
			{
				System.out.println(getDebugTimeStamp() + "ConnectException: connection refused");
				SwingUtilities.invokeLater(new Logger(log, "ConnectException when creating socket: connection refused", 3));
				SwingUtilities.invokeLater(new Logger(log, "No process is listening on the remote address/port.", 3));
				return false;
			} else if ("java.net.ConnectException: Connection timed out: connect".equalsIgnoreCase(e.toString()))
			{
				System.out.println(getDebugTimeStamp() + "ConnectException: connection timed out when making socket");
				SwingUtilities.invokeLater(new Logger(log, "ConnectException when creating socket: connection timed out", 3));
				return false;
			} else
			{
				SwingUtilities.invokeLater(new Logger(log, e.toString(), 3));
				e.printStackTrace(System.out);
				return false;
			}
		}

		SwingUtilities.invokeLater(new Logger(log, "Connected on server " + retryStart, 0));
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
			SwingUtilities.invokeLater(new Logger(log, "Output stream created successfully", 0));
		} catch (IOException e)
		{
			System.out.println(getDebugTimeStamp() + "Could not extablish output stream to " + yukon_host + ": " + e);
			SwingUtilities.invokeLater(new Logger(log, "Could not establish output stream to " + yukon_host, 3));
		}

		// Create buffered input stream
		try
		{
			in = new DataInputStream(fdeSocket.getInputStream());
			SwingUtilities.invokeLater(new Logger(log, "Input stream created successfully", 0));
		} catch (Exception e)
		{
			System.out.println(getDebugTimeStamp() + "Error creating input stream");
			e.printStackTrace(System.out);
			SwingUtilities.invokeLater(new Logger(log, "Error creating input stream", 3));
		}

		// Create recieve log random access file
		try
		{
			pointList = new RandomAccessFile("resource/rdex_yukon_points.txt", "rw");

		} catch (Exception e)
		{
			System.out.println(getDebugTimeStamp() + "Error creating recv log file");
			SwingUtilities.invokeLater(new Logger(log, "Error creating recv log file", 3));
			e.printStackTrace(System.out);
		}

		// Send Registration Message
		try
		{
			// Send header,timestamp, and translation
			out.writeInt(RDEX_REG);
			out.writeBytes(getTimeStamp());
			out.writeBytes(RDEX_REG_NAME);
			// pack on zeros to fill out translation and spare
			for (int i = 0; i < (80 - RDEX_REG_NAME.length()); i++)
			{
				out.writeByte(0);
			}
			// send quality and value
			out.writeInt(0);
			out.writeInt(0);
			out.flush();
			SwingUtilities.invokeLater(new Logger(log, "Sent registration message: " + getFormalTimeStamp() + " --waiting for acknowledge--", 0));

		} catch (Exception e)
		{
			SwingUtilities.invokeLater(new Logger(log, "Error writing to output stream", 3));
			e.printStackTrace(System.out);
		}

		// Write message to traffic log file
		try
		{

			FileWriter traffic = new FileWriter(trafficFile, true);
			traffic.write(getDebugTimeStamp() + "----------FDE RDEX EMULATOR STARTED SUCESSFULLY---------\n");
			traffic.write(getDebugTimeStamp() + "SENT REGISTRATION MESSAGE\n");
			traffic.close();
		} catch (Exception e)
		{
			System.out.println(getDebugTimeStamp() + "Error with traffic stream");
			e.printStackTrace(System.out);
		}

		// Start thread for reading
		readThread = new Thread(this, "RDEX READ THREAD");
		readThread.start();

		// Start timers
		heartbeat = new Timer();
		heartbeat.schedule(new heartbeatTask(), T1_TIME, T1_TIME);
		interval = new Timer();
		interval.schedule(new intervalTask(), T2_TIME, T2_TIME);

		// fill point array with points from point file
		pointarray = getRdexFileIO().getRdexPointsFromFile();

		return true;
	}

	public RdexFileIO getRdexFileIO()
	{
		if (rdexFileIO == null)
		{
			rdexFileIO = new RdexFileIO(settings.getPath())
			{
			};
		}
		return rdexFileIO;
	}

	// Send points based on interval and type from file as timers expire
	public void sendPoints()
	{
		int exit = 0;
		int i = 0;
		RdexPoint nextpoint;
		RdexPoint lastpoint;
		float value = 0.0f;
		while (exit != 1)
		{
			try
			{
				nextpoint = (RdexPoint) pointarray[i];
				lastpoint = (RdexPoint) pointarray[i + 1];
				try
				{
					if (lastpoint.getPointName().equalsIgnoreCase(""))
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

					if ("Value".equals(nextpoint.getPointType()))
					{
						out.writeInt(RDEX_VALUE);
						out.writeBytes(getTimeStamp());
						out.writeBytes(nextpoint.getPointName());
						for (int j = 0; j < (80 - nextpoint.getPointName().length()); j++)
						{
							out.writeByte(0);
						}
						out.writeInt(1);
						if ("RANDOM".equalsIgnoreCase(nextpoint.getPointFunction()))
						{
							Double maxdouble = new Double(nextpoint.getPointMax());
							int first = gen.nextInt(maxdouble.intValue());
							float last = gen.nextFloat();
							value = first + last;
							out.writeFloat(value);
						} else if ("PYRAMID".equalsIgnoreCase(nextpoint.getPointFunction()))
						{
							double currentvalue = nextpoint.getPointCurrentValue();
							double incriment = nextpoint.getPointDelta();

							if ((currentvalue + incriment) >= nextpoint.getPointMax())
							{
								Double cvdouble = new Double(currentvalue);
								value = cvdouble.floatValue();
								out.writeFloat(value);
								currentvalue = currentvalue + incriment;
								nextpoint.setPointCurrentValue(currentvalue);
								nextpoint.setPointDelta(nextpoint.getPointDelta() - (2 * nextpoint.getPointDelta()));
							} else if ((currentvalue + incriment) <= nextpoint.getPointMin())
							{
								Double cvdouble = new Double(currentvalue);
								value = cvdouble.floatValue();
								out.writeFloat(value);
								currentvalue = currentvalue + incriment;
								nextpoint.setPointCurrentValue(currentvalue);
								nextpoint.setPointDelta(nextpoint.getPointDelta() + (-2 * nextpoint.getPointDelta()));
							} else
							{
								Double cvdouble = new Double(currentvalue);
								value = cvdouble.floatValue();
								out.writeFloat(value);
								currentvalue = currentvalue + incriment;
								nextpoint.setPointCurrentValue(currentvalue);
							}
						} else if ("DROPOFF".equalsIgnoreCase(nextpoint.getPointFunction()))
						{
							double currentvalue = nextpoint.getPointCurrentValue();
							double incriment = nextpoint.getPointDelta();
							if (nextpoint.getPointMaxStart())
							{
								Double cvdouble = new Double(currentvalue);
								value = cvdouble.floatValue();
								out.writeFloat(value);
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
								currentvalue = currentvalue + nextpoint.getPointDelta();
								if (currentvalue > nextpoint.getPointMax())
								{
									nextpoint.setPointCurrentValue(nextpoint.getPointMin());
								} else
									nextpoint.setPointCurrentValue(currentvalue);
							}
						}
						out.flush();
						SwingUtilities.invokeLater(new Logger(log, "SENT ON INTERVAL: Value " + nextpoint.getPointName() + " Point: " + nf.format(value), 1));
						//Write message to traffic log file
						try
						{
							FileWriter traffic = new FileWriter(trafficFile, true);
							traffic.write(getDebugTimeStamp() + "SENT ON INTERVAL: " + nextpoint.getPointType() + " Translation: " + nextpoint.getPointName() + " Quality: 1" + " Point: " + nf.format(value) + "\n");
							traffic.close();
						} catch (Exception e)
						{
							System.out.println(getDebugTimeStamp() + "Error with traffic stream");
							e.printStackTrace(System.out);
						}
					} else if ("Status".equals(nextpoint.getPointType()))
					{
						out.writeInt(RDEX_STATUS);
						out.writeBytes(getTimeStamp());
						out.writeBytes(nextpoint.getPointName());
						for (int j = 0; j < (80 - nextpoint.getPointName().length()); j++)
						{
							out.writeByte(0);
						}
						out.writeInt(1);
						out.writeInt(flipflop10);
						out.flush();
						//Write message to traffic log file
						String valueString = "";
						if (flipflop10 == 0)
						{
							valueString = "Open";
						} else
							valueString = "Close";
						SwingUtilities.invokeLater(new Logger(log, "SENT ON INTERVAL: Status " + nextpoint.getPointName() + " Point: " + valueString, 1));
						try
						{
							FileWriter traffic = new FileWriter(trafficFile, true);
							traffic.write(getDebugTimeStamp() + "SENT ON INTERVAL: " + nextpoint.getPointType() + " Translation: " + nextpoint.getPointName() + " Quality: 1" + " Point: " + valueString + "\n");
							traffic.close();
						} catch (Exception e)
						{
							System.out.println(getDebugTimeStamp() + "Error with traffic stream");
							e.printStackTrace(System.out);
						}
					} else if ("Control".equals(nextpoint.getPointType()))
					{
						out.writeInt(RDEX_CONTROL);
						out.writeBytes(getTimeStamp());
						out.writeBytes(nextpoint.getPointName());
						for (int j = 0; j < (80 - nextpoint.getPointName().length()); j++)
						{
							out.writeByte(0);
						}
						out.writeInt(1);
						out.writeInt(flipflop10);
						out.flush();
						//Write message to traffic log file
						String valueString = "";
						if (flipflop10 == 0)
						{
							valueString = "Open";
						} else
							valueString = "Close";
						SwingUtilities.invokeLater(new Logger(log, "SENT ON INTERVAL: Control " + nextpoint.getPointName() + " Point: " + valueString, 1));
						try
						{
							FileWriter traffic = new FileWriter(trafficFile, true);
							traffic.write(getDebugTimeStamp() + "SENT ON INTERVAL: " + nextpoint.getPointType() + " Translation: " + nextpoint.getPointName() + " Quality: 1" + " Point: " + valueString + "\n");
							traffic.close();
						} catch (Exception e)
						{
							System.out.println(getDebugTimeStamp() + "Error with traffic stream");
							e.printStackTrace(System.out);
						}
					}
				} else if (nextpoint.getPointInterval() == 30 && thirtySecond == 3)
				{
					if ("Value".equals(nextpoint.getPointType()))
					{
						out.writeInt(RDEX_VALUE);
						out.writeBytes(getTimeStamp());
						out.writeBytes(nextpoint.getPointName());
						for (int j = 0; j < (80 - nextpoint.getPointName().length()); j++)
						{
							out.writeByte(0);
						}
						out.writeInt(1);
						if ("RANDOM".equalsIgnoreCase(nextpoint.getPointFunction()))
						{
							Double maxdouble = new Double(nextpoint.getPointMax());
							int first = gen.nextInt(maxdouble.intValue());
							float last = gen.nextFloat();
							value = first + last;
							out.writeFloat(value);
						} else if ("PYRAMID".equalsIgnoreCase(nextpoint.getPointFunction()))
						{
							double currentvalue = nextpoint.getPointCurrentValue();
							double incriment = nextpoint.getPointDelta();

							if ((currentvalue + incriment) >= nextpoint.getPointMax())
							{
								Double cvdouble = new Double(currentvalue);
								value = cvdouble.floatValue();
								out.writeFloat(value);
								currentvalue = currentvalue + incriment;
								nextpoint.setPointCurrentValue(currentvalue);
								nextpoint.setPointDelta(nextpoint.getPointDelta() - (2 * nextpoint.getPointDelta()));
							} else if ((currentvalue + incriment) <= nextpoint.getPointMin())
							{
								Double cvdouble = new Double(currentvalue);
								value = cvdouble.floatValue();
								out.writeFloat(value);
								currentvalue = currentvalue + incriment;
								nextpoint.setPointCurrentValue(currentvalue);
								nextpoint.setPointDelta(nextpoint.getPointDelta() + (-2 * nextpoint.getPointDelta()));
							} else
							{
								Double cvdouble = new Double(currentvalue);
								value = cvdouble.floatValue();
								out.writeFloat(value);
								currentvalue = currentvalue + incriment;
								nextpoint.setPointCurrentValue(currentvalue);
							}
						} else if ("DROPOFF".equalsIgnoreCase(nextpoint.getPointFunction()))
						{
							double currentvalue = nextpoint.getPointCurrentValue();
							double incriment = nextpoint.getPointDelta();
							if (nextpoint.getPointMaxStart())
							{
								Double cvdouble = new Double(currentvalue);
								value = cvdouble.floatValue();
								out.writeFloat(value);
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
								currentvalue = currentvalue + nextpoint.getPointDelta();
								if (currentvalue > nextpoint.getPointMax())
								{
									nextpoint.setPointCurrentValue(nextpoint.getPointMin());
								} else
									nextpoint.setPointCurrentValue(currentvalue);
							}
						}
						out.flush();
						SwingUtilities.invokeLater(new Logger(log, "SENT ON INTERVAL: Value " + nextpoint.getPointName() + " Point: " + nf.format(value), 1));
						//Write message to traffic log file
						try
						{
							FileWriter traffic = new FileWriter(trafficFile, true);
							traffic.write(getDebugTimeStamp() + "SENT ON INTERVAL: " + nextpoint.getPointType() + " Translation: " + nextpoint.getPointName() + " Quality: 1" + " Point: " + nf.format(value) + "\n");
							traffic.close();
						} catch (Exception e)
						{
							System.out.println(getDebugTimeStamp() + "Error with traffic stream");
							e.printStackTrace(System.out);
						}
					} else if ("Status".equalsIgnoreCase(nextpoint.getPointType()))
					{
						out.writeInt(RDEX_STATUS);
						out.writeBytes(getTimeStamp());
						out.writeBytes(nextpoint.getPointName());
						for (int j = 0; j < (80 - nextpoint.getPointName().length()); j++)
						{
							out.writeByte(0);
						}
						out.writeInt(1);
						out.writeInt(flipflop30);
						out.flush();
						//Write message to traffic log file
						String valueString = "";
						if (flipflop30 == 0)
						{
							valueString = "Open";
						} else
							valueString = "Close";
						SwingUtilities.invokeLater(new Logger(log, "SENT ON INTERVAL: Status " + nextpoint.getPointName() + " Point: " + valueString, 1));
						try
						{
							FileWriter traffic = new FileWriter(trafficFile, true);
							traffic.write(getDebugTimeStamp() + "SENT ON INTERVAL: " + nextpoint.getPointType() + " Translation: " + nextpoint.getPointName() + " Quality: 1" + " Point: " + valueString + "\n");
							traffic.close();
						} catch (Exception e)
						{
							System.out.println(getDebugTimeStamp() + "Error with traffic stream");
							e.printStackTrace(System.out);
						}
					} else if ("Control".equals(nextpoint.getPointType()))
					{
						out.writeInt(RDEX_CONTROL);
						out.writeBytes(getTimeStamp());
						out.writeBytes(nextpoint.getPointName());
						for (int j = 0; j < (80 - nextpoint.getPointName().length()); j++)
						{
							out.writeByte(0);
						}
						out.writeInt(1);
						out.writeInt(flipflop30);
						out.flush();
						//Write message to traffic log file
						String valueString = "";
						if (flipflop30 == 0)
						{
							valueString = "Open";
						} else
							valueString = "Close";
						SwingUtilities.invokeLater(new Logger(log, "SENT ON INTERVAL: Control " + nextpoint.getPointName() + " Point: " + valueString, 1));
						try
						{
							FileWriter traffic = new FileWriter(trafficFile, true);
							traffic.write(getDebugTimeStamp() + "SENT ON INTERVAL: " + nextpoint.getPointType() + " Translation: " + nextpoint.getPointName() + " Quality: 1" + " Point: " + valueString + "\n");
							traffic.close();
						} catch (Exception e)
						{
							System.out.println(getDebugTimeStamp() + "Error with traffic stream");
							e.printStackTrace(System.out);
						}
					}
				} else if (nextpoint.getPointInterval() == 60 && sixtySecond == 6)
				{

					if ("Value".equals(nextpoint.getPointType()))
					{
						out.writeInt(RDEX_VALUE);
						out.writeBytes(getTimeStamp());
						out.writeBytes(nextpoint.getPointName());
						for (int j = 0; j < (80 - nextpoint.getPointName().length()); j++)
						{
							out.writeByte(0);
						}
						out.writeInt(1);
						if ("RANDOM".equalsIgnoreCase(nextpoint.getPointFunction()))
						{
							Double maxdouble = new Double(nextpoint.getPointMax());
							int first = gen.nextInt(maxdouble.intValue());
							float last = gen.nextFloat();
							value = first + last;
							out.writeFloat(value);
						} else if ("PYRAMID".equalsIgnoreCase(nextpoint.getPointFunction()))
						{
							double currentvalue = nextpoint.getPointCurrentValue();
							double incriment = nextpoint.getPointDelta();
							if ((currentvalue + incriment) >= nextpoint.getPointMax())
							{
								Double cvdouble = new Double(currentvalue);
								value = cvdouble.floatValue();
								out.writeFloat(value);
								currentvalue = currentvalue + incriment;
								nextpoint.setPointCurrentValue(currentvalue);
								nextpoint.setPointDelta(nextpoint.getPointDelta() - (2 * nextpoint.getPointDelta()));
							} else if ((currentvalue + incriment) <= nextpoint.getPointMin())
							{
								Double cvdouble = new Double(currentvalue);
								value = cvdouble.floatValue();
								out.writeFloat(value);
								currentvalue = currentvalue + incriment;
								nextpoint.setPointCurrentValue(currentvalue);
								nextpoint.setPointDelta(nextpoint.getPointDelta() + (-2 * nextpoint.getPointDelta()));
							} else
							{
								Double cvdouble = new Double(currentvalue);
								value = cvdouble.floatValue();
								out.writeFloat(value);
								currentvalue = currentvalue + incriment;
								nextpoint.setPointCurrentValue(currentvalue);
							}
						} else if ("DROPOFF".equalsIgnoreCase(nextpoint.getPointFunction()))
						{
							double currentvalue = nextpoint.getPointCurrentValue();
							double incriment = nextpoint.getPointDelta();
							if (nextpoint.getPointMaxStart())
							{

								Double cvdouble = new Double(currentvalue);
								value = cvdouble.floatValue();
								out.writeFloat(value);
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

								currentvalue = currentvalue + nextpoint.getPointDelta();

								if (currentvalue > nextpoint.getPointMax())
								{
									nextpoint.setPointCurrentValue(nextpoint.getPointMin());
								} else
									nextpoint.setPointCurrentValue(currentvalue);
							}
						}
						out.flush();
						SwingUtilities.invokeLater(new Logger(log, "SENT ON INTERVAL: Value " + nextpoint.getPointName() + " Point: " + nf.format(value), 1));
						//Write message to traffic log file
						try
						{
							FileWriter traffic = new FileWriter(trafficFile, true);
							traffic.write(getDebugTimeStamp() + "SENT ON INTERVAL: " + nextpoint.getPointType() + " Translation: " + nextpoint.getPointName() + " Quality: 1" + " Point: " + nf.format(value) + "\n");
							traffic.close();
						} catch (Exception e)
						{
							System.out.println(getDebugTimeStamp() + "Error with traffic stream");
							e.printStackTrace(System.out);
						}
					} else if ("Status".equalsIgnoreCase(nextpoint.getPointType()))
					{
						out.writeInt(RDEX_STATUS);
						out.writeBytes(getTimeStamp());
						out.writeBytes(nextpoint.getPointName());
						for (int j = 0; j < (80 - nextpoint.getPointName().length()); j++)
						{
							out.writeByte(0);
						}
						out.writeInt(1);
						out.writeInt(flipflop60);
						out.flush();
						//Write message to traffic log file
						String valueString = "";
						if (flipflop60 == 0)
						{
							valueString = "Open";
						} else
							valueString = "Close";
						SwingUtilities.invokeLater(new Logger(log, "SENT ON INTERVAL: Status " + nextpoint.getPointName() + " Point: " + valueString, 1));
						try
						{
							FileWriter traffic = new FileWriter(trafficFile, true);
							traffic.write(getDebugTimeStamp() + "SENT ON INTERVAL: " + nextpoint.getPointType() + " Translation: " + nextpoint.getPointName() + " Quality: 1" + " Point: " + valueString + "\n");
							traffic.close();
						} catch (Exception e)
						{
							System.out.println(getDebugTimeStamp() + "Error with traffic stream");
							e.printStackTrace(System.out);
						}
					} else if ("Control".equalsIgnoreCase(nextpoint.getPointType()))
					{
						out.writeInt(RDEX_CONTROL);
						out.writeBytes(getTimeStamp());
						out.writeBytes(nextpoint.getPointName());
						for (int j = 0; j < (80 - nextpoint.getPointName().length()); j++)
						{
							out.writeByte(0);
						}
						out.writeInt(1);
						out.writeInt(flipflop60);
						out.flush();
						//Write message to traffic log file
						String valueString = "";
						if (flipflop60 == 0)
						{
							valueString = "Open";
						} else
							valueString = "Close";
						SwingUtilities.invokeLater(new Logger(log, "SENT ON INTERVAL: Control " + nextpoint.getPointName() + " Point: " + valueString, 1));
						try
						{
							FileWriter traffic = new FileWriter(trafficFile, true);
							traffic.write(getDebugTimeStamp() + "SENT ON INTERVAL: " + nextpoint.getPointType() + " Translation: " + nextpoint.getPointName() + " Quality: 1" + " Point: " + valueString + "\n");
							traffic.close();
						} catch (Exception e)
						{
							System.out.println(getDebugTimeStamp() + "Error with traffic stream");
							e.printStackTrace(System.out);
						}
					}
				} else if (nextpoint.getPointInterval() == 300 && fiveMinute == 30)
				{
					if ("Value".equals(nextpoint.getPointType()))
					{
						out.writeInt(RDEX_VALUE);
						out.writeBytes(getTimeStamp());
						out.writeBytes(nextpoint.getPointName());
						for (int j = 0; j < (80 - nextpoint.getPointName().length()); j++)
						{
							out.writeByte(0);
						}
						out.writeInt(1);
						if ("RANDOM".equalsIgnoreCase(nextpoint.getPointFunction()))
						{
							Double maxdouble = new Double(nextpoint.getPointMax());
							int first = gen.nextInt(maxdouble.intValue());
							float last = gen.nextFloat();
							value = first + last;
							out.writeFloat(value);
						} else if ("PYRAMID".equalsIgnoreCase(nextpoint.getPointFunction()))
						{
							double currentvalue = nextpoint.getPointCurrentValue();
							double incriment = nextpoint.getPointDelta();
							if ((currentvalue + incriment) >= nextpoint.getPointMax())
							{
								Double cvdouble = new Double(currentvalue);
								value = cvdouble.floatValue();
								out.writeFloat(value);
								currentvalue = currentvalue + incriment;
								nextpoint.setPointCurrentValue(currentvalue);
								nextpoint.setPointDelta(nextpoint.getPointDelta() - (2 * nextpoint.getPointDelta()));
							} else if ((currentvalue + incriment) <= nextpoint.getPointMin())
							{
								Double cvdouble = new Double(currentvalue);
								value = cvdouble.floatValue();
								out.writeFloat(value);
								currentvalue = currentvalue + incriment;
								nextpoint.setPointCurrentValue(currentvalue);
								nextpoint.setPointDelta(nextpoint.getPointDelta() + (-2 * nextpoint.getPointDelta()));
							} else
							{
								Double cvdouble = new Double(currentvalue);
								value = cvdouble.floatValue();
								out.writeFloat(value);
								currentvalue = currentvalue + incriment;
								nextpoint.setPointCurrentValue(currentvalue);
							}
						} else if ("DROPOFF".equalsIgnoreCase(nextpoint.getPointFunction()))
						{
							double currentvalue = nextpoint.getPointCurrentValue();
							double incriment = nextpoint.getPointDelta();
							if (nextpoint.getPointMaxStart())
							{
								Double cvdouble = new Double(currentvalue);
								value = cvdouble.floatValue();
								out.writeFloat(value);
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
								currentvalue = currentvalue + nextpoint.getPointDelta();
								if (currentvalue > nextpoint.getPointMax())
								{
									nextpoint.setPointCurrentValue(nextpoint.getPointMin());
								} else
									nextpoint.setPointCurrentValue(currentvalue);
							}
						}
						out.flush();
						SwingUtilities.invokeLater(new Logger(log, "SENT ON INTERVAL: Value " + nextpoint.getPointName() + " Point: " + nf.format(value), 1));
						//Write message to traffic log file
						try
						{
							FileWriter traffic = new FileWriter(trafficFile, true);
							traffic.write(getDebugTimeStamp() + "SENT ON INTERVAL: " + nextpoint.getPointType() + " Translation: " + nextpoint.getPointName() + " Quality: 1" + " Point: " + nf.format(value) + "\n");
							traffic.close();
						} catch (Exception e)
						{
							System.out.println(getDebugTimeStamp() + "Error with traffic stream");
							e.printStackTrace(System.out);
						}
					} else if ("Status".equals(nextpoint.getPointType()))
					{
						out.writeInt(RDEX_STATUS);
						out.writeBytes(getTimeStamp());
						out.writeBytes(nextpoint.getPointName());
						for (int j = 0; j < (80 - nextpoint.getPointName().length()); j++)
						{
							out.writeByte(0);
						}
						out.writeInt(1);
						out.writeInt(flipflop300);
						out.flush();
						//Write message to traffic log file
						String valueString = "";
						if (flipflop300 == 0)
						{
							valueString = "Open";
						} else
							valueString = "Close";
						SwingUtilities.invokeLater(new Logger(log, "SENT ON INTERVAL: Status " + nextpoint.getPointName() + " Point: " + valueString, 1));
						try
						{
							FileWriter traffic = new FileWriter(trafficFile, true);
							traffic.write(getDebugTimeStamp() + "SENT ON INTERVAL: " + nextpoint.getPointType() + " Translation: " + nextpoint.getPointName() + " Quality: 1" + " Point: " + valueString + "\n");
							traffic.close();
						} catch (Exception e)
						{
							System.out.println(getDebugTimeStamp() + "Error with traffic stream");
							e.printStackTrace(System.out);
						}
					} else if ("Control".equalsIgnoreCase(nextpoint.getPointType()))
					{
						out.writeInt(RDEX_CONTROL);
						out.writeBytes(getTimeStamp());
						out.writeBytes(nextpoint.getPointName());
						for (int j = 0; j < (80 - nextpoint.getPointName().length()); j++)
						{
							out.writeByte(0);
						}
						out.writeInt(1);
						out.writeInt(flipflop300);
						out.flush();
						//Write message to traffic log file
						String valueString = "";
						if (flipflop300 == 0)
						{
							valueString = "Open";
						} else
							valueString = "Close";
						SwingUtilities.invokeLater(new Logger(log, "SENT ON INTERVAL: Control " + nextpoint.getPointName() + " Point: " + valueString, 1));
						try
						{
							FileWriter traffic = new FileWriter(trafficFile, true);
							traffic.write(getDebugTimeStamp() + "SENT ON INTERVAL: " + nextpoint.getPointType() + " Translation: " + nextpoint.getPointName() + " Quality: 1" + " Point: " + valueString + "\n");
							traffic.close();
						} catch (Exception e)
						{
							System.out.println(getDebugTimeStamp() + "Error with traffic stream");
							e.printStackTrace(System.out);
						}
					}
				} else if (nextpoint.getPointInterval() == 900 && fifteenMinute == 90)
				{
					if ("Value".equalsIgnoreCase(nextpoint.getPointType()))
					{
						out.writeInt(RDEX_VALUE);
						out.writeBytes(getTimeStamp());
						out.writeBytes(nextpoint.getPointName());
						for (int j = 0; j < (80 - nextpoint.getPointName().length()); j++)
						{
							out.writeByte(0);
						}
						out.writeInt(1);
						if ("RANDOM".equalsIgnoreCase(nextpoint.getPointFunction()))
						{
							Double maxdouble = new Double(nextpoint.getPointMax());
							int first = gen.nextInt(maxdouble.intValue());
							float last = gen.nextFloat();
							value = first + last;
							out.writeFloat(value);
						} else if ("PYRAMID".equalsIgnoreCase(nextpoint.getPointFunction()))
						{
							double currentvalue = nextpoint.getPointCurrentValue();
							double incriment = nextpoint.getPointDelta();
							if ((currentvalue + incriment) >= nextpoint.getPointMax())
							{
								Double cvdouble = new Double(currentvalue);
								value = cvdouble.floatValue();
								out.writeFloat(value);
								currentvalue = currentvalue + incriment;
								nextpoint.setPointCurrentValue(currentvalue);
								nextpoint.setPointDelta(nextpoint.getPointDelta() - (2 * nextpoint.getPointDelta()));
							} else if ((currentvalue + incriment) <= nextpoint.getPointMin())
							{
								Double cvdouble = new Double(currentvalue);
								value = cvdouble.floatValue();
								out.writeFloat(value);
								currentvalue = currentvalue + incriment;
								nextpoint.setPointCurrentValue(currentvalue);
								nextpoint.setPointDelta(nextpoint.getPointDelta() + (-2 * nextpoint.getPointDelta()));
							} else
							{
								Double cvdouble = new Double(currentvalue);
								value = cvdouble.floatValue();
								out.writeFloat(value);
								currentvalue = currentvalue + incriment;
								nextpoint.setPointCurrentValue(currentvalue);
							}
						} else if ("DROPOFF".equalsIgnoreCase(nextpoint.getPointFunction()))
						{
							double currentvalue = nextpoint.getPointCurrentValue();
							double incriment = nextpoint.getPointDelta();
							if (nextpoint.getPointMaxStart())
							{
								Double cvdouble = new Double(currentvalue);
								value = cvdouble.floatValue();
								out.writeFloat(value);
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
								currentvalue = currentvalue + nextpoint.getPointDelta();
								if (currentvalue > nextpoint.getPointMax())
								{
									nextpoint.setPointCurrentValue(nextpoint.getPointMin());
								} else
									nextpoint.setPointCurrentValue(currentvalue);
							}
						}
						out.flush();
						SwingUtilities.invokeLater(new Logger(log, "SENT ON INTERVAL: Value " + nextpoint.getPointName() + " Point: " + nf.format(value), 1));
						//Write message to traffic log file
						try
						{
							FileWriter traffic = new FileWriter(trafficFile, true);
							traffic.write(getDebugTimeStamp() + "SENT ON INTERVAL: " + nextpoint.getPointType() + " Translation: " + nextpoint.getPointName() + " Quality: 1" + " Point: " + nf.format(value) + "\n");
							traffic.close();
						} catch (Exception e)
						{
							System.out.println(getDebugTimeStamp() + "Error with traffic stream");
							e.printStackTrace(System.out);
						}
					} else if ("Status".equals(nextpoint.getPointType()))
					{
						out.writeInt(RDEX_STATUS);
						out.writeBytes(getTimeStamp());
						out.writeBytes(nextpoint.getPointName());
						for (int j = 0; j < (80 - nextpoint.getPointName().length()); j++)
						{
							out.writeByte(0);
						}
						out.writeInt(1);
						out.writeInt(flipflop900);
						out.flush();
						//Write message to traffic log file
						String valueString = "";
						if (flipflop900 == 0)
						{
							valueString = "Open";
						} else
							valueString = "Close";
						SwingUtilities.invokeLater(new Logger(log, "SENT ON INTERVAL: Status " + nextpoint.getPointName() + " Point: " + valueString, 1));
						try
						{
							FileWriter traffic = new FileWriter(trafficFile, true);
							traffic.write(getDebugTimeStamp() + "SENT ON INTERVAL: " + nextpoint.getPointType() + " Translation: " + nextpoint.getPointName() + " Quality: 1" + " Point: " + valueString + "\n");
							traffic.close();
						} catch (Exception e)
						{
							System.out.println(getDebugTimeStamp() + "Error with traffic stream");
							e.printStackTrace(System.out);
						}
					} else if ("Control".equalsIgnoreCase(nextpoint.getPointType()))
					{
						out.writeInt(RDEX_CONTROL);
						out.writeBytes(getTimeStamp());
						out.writeBytes(nextpoint.getPointName());
						for (int j = 0; j < (80 - nextpoint.getPointName().length()); j++)
						{
							out.writeByte(0);
						}
						out.writeInt(1);
						out.writeInt(flipflop900);
						out.flush();
						//Write message to traffic log file
						String valueString = "";
						if (flipflop900 == 0)
						{
							valueString = "Open";
						} else
							valueString = "Close";
						SwingUtilities.invokeLater(new Logger(log, "SENT ON INTERVAL: Control " + nextpoint.getPointName() + " Point: " + valueString, 1));
						try
						{
							FileWriter traffic = new FileWriter(trafficFile, true);
							traffic.write(getDebugTimeStamp() + "SENT ON INTERVAL: " + nextpoint.getPointType() + " Translation: " + nextpoint.getPointName() + " Quality: 1" + " Point: " + valueString + "\n");
							traffic.close();
						} catch (Exception e)
						{
							System.out.println(getDebugTimeStamp() + "Error with traffic stream");
							e.printStackTrace(System.out);
						}
					}
				} else if (nextpoint.getPointInterval() == 3600 && sixtySecond == 360)
				{
					if ("Value".equals(nextpoint.getPointType()))
					{
						out.writeInt(RDEX_VALUE);
						out.writeBytes(getTimeStamp());
						out.writeBytes(nextpoint.getPointName());
						for (int j = 0; j < (80 - nextpoint.getPointName().length()); j++)
						{
							out.writeByte(0);
						}
						out.writeInt(1);
						if ("RANDOM".equalsIgnoreCase(nextpoint.getPointFunction()))
						{
							Double maxdouble = new Double(nextpoint.getPointMax());
							int first = gen.nextInt(maxdouble.intValue());
							float last = gen.nextFloat();
							value = first + last;
							out.writeFloat(value);
						} else if ("PYRAMID".equalsIgnoreCase(nextpoint.getPointFunction()))
						{
							double currentvalue = nextpoint.getPointCurrentValue();
							double incriment = nextpoint.getPointDelta();
							System.out.println("currentval: " + currentvalue + " incriment: " + incriment);
							if ((currentvalue + incriment) >= nextpoint.getPointMax())
							{
								Double cvdouble = new Double(currentvalue);
								value = cvdouble.floatValue();
								out.writeFloat(value);
								currentvalue = currentvalue + incriment;
								nextpoint.setPointCurrentValue(currentvalue);
								nextpoint.setPointDelta(nextpoint.getPointDelta() - (2 * nextpoint.getPointDelta()));
							} else if ((currentvalue + incriment) <= nextpoint.getPointMin())
							{
								Double cvdouble = new Double(currentvalue);
								value = cvdouble.floatValue();
								out.writeFloat(value);
								currentvalue = currentvalue + incriment;
								nextpoint.setPointCurrentValue(currentvalue);
								nextpoint.setPointDelta(nextpoint.getPointDelta() + (-2 * nextpoint.getPointDelta()));
							} else
							{
								Double cvdouble = new Double(currentvalue);
								value = cvdouble.floatValue();
								out.writeFloat(value);
								currentvalue = currentvalue + incriment;
								nextpoint.setPointCurrentValue(currentvalue);
							}
						} else if ("DROPOFF".equalsIgnoreCase(nextpoint.getPointFunction()))
						{
							double currentvalue = nextpoint.getPointCurrentValue();
							double incriment = nextpoint.getPointDelta();
							if (nextpoint.getPointMaxStart())
							{
								Double cvdouble = new Double(currentvalue);
								value = cvdouble.floatValue();
								out.writeFloat(value);

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

								currentvalue = currentvalue + nextpoint.getPointDelta();

								if (currentvalue > nextpoint.getPointMax())
								{
									nextpoint.setPointCurrentValue(nextpoint.getPointMin());
								} else
									nextpoint.setPointCurrentValue(currentvalue);
							}
						}
						out.flush();
						SwingUtilities.invokeLater(new Logger(log, "SENT ON INTERVAL: Value " + nextpoint.getPointName() + " Point: " + nf.format(value), 1));
						//Write message to traffic log file
						try
						{
							FileWriter traffic = new FileWriter(trafficFile, true);
							traffic.write(getDebugTimeStamp() + "SENT ON INTERVAL: " + nextpoint.getPointType() + " Translation: " + nextpoint.getPointName() + " Quality: 1" + " Point: " + nf.format(value) + "\n");
							traffic.close();
						} catch (Exception e)
						{
							System.out.println(getDebugTimeStamp() + "Error with traffic stream");
							e.printStackTrace(System.out);
						}
					} else if ("Status".equals(nextpoint.getPointType()))
					{
						out.writeInt(RDEX_STATUS);
						out.writeBytes(getTimeStamp());
						out.writeBytes(nextpoint.getPointName());
						for (int j = 0; j < (80 - nextpoint.getPointName().length()); j++)
						{
							out.writeByte(0);
						}
						out.writeInt(1);
						out.writeInt(flipflop3600);
						out.flush();
						//Write message to traffic log file
						String valueString = "";
						if (flipflop3600 == 0)
						{
							valueString = "Open";
						} else
							valueString = "Close";
						SwingUtilities.invokeLater(new Logger(log, "SENT ON INTERVAL: Status " + nextpoint.getPointName() + " Point: " + valueString, 1));
						try
						{
							FileWriter traffic = new FileWriter(trafficFile, true);
							traffic.write(getDebugTimeStamp() + "SENT ON INTERVAL: " + nextpoint.getPointType() + " Translation: " + nextpoint.getPointName() + " Quality: 1" + " Point: " + valueString + "\n");
							traffic.close();
						} catch (Exception e)
						{
							System.out.println(getDebugTimeStamp() + "Error with traffic stream");
							e.printStackTrace(System.out);
						}
					} else if ("Control".equals(nextpoint.getPointType()))
					{
						out.writeInt(RDEX_CONTROL);
						out.writeBytes(getTimeStamp());
						out.writeBytes(nextpoint.getPointName());
						for (int j = 0; j < (80 - nextpoint.getPointName().length()); j++)
						{
							out.writeByte(0);
						}
						out.writeInt(1);
						out.writeInt(flipflop3600);
						out.flush();
						//Write message to traffic log file
						String valueString = "";
						if (flipflop3600 == 0)
						{
							valueString = "Open";
						} else
							valueString = "Close";
						SwingUtilities.invokeLater(new Logger(log, "SENT ON INTERVAL: Control " + nextpoint.getPointName() + " Point: " + valueString, 1));
						try
						{
							FileWriter traffic = new FileWriter(trafficFile, true);
							traffic.write(getDebugTimeStamp() + "SENT ON INTERVAL: " + nextpoint.getPointType() + " Translation: " + nextpoint.getPointName() + " Quality: 1" + " Point: " + valueString + "\n");
							traffic.close();
						} catch (Exception e)
						{
							System.out.println(getDebugTimeStamp() + "Error with traffic stream");
							e.printStackTrace(System.out);
						}
					}

				}
				i++;
			} catch (Exception e)
			{
				e.printStackTrace(System.out);
				exit = 1;
			}
		}
	}

	// Send all points from file reguardless of interval
	public void sendAllPoints()
	{

		int exit = 0;
		int i = 0;
		RdexPoint nextpoint;
		RdexPoint lastpoint;
		float value = 0.0f;
		while (exit != 1)
		{
			try
			{
				nextpoint = (RdexPoint) pointarray[i];
				lastpoint = (RdexPoint) pointarray[i + 1];

				try
				{
					if (lastpoint.getPointName().equalsIgnoreCase(""))
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

				if ("Value".equals(nextpoint.getPointType()))
				{

					out.writeInt(RDEX_VALUE);
					out.writeBytes(getTimeStamp());
					out.writeBytes(nextpoint.getPointName());
					for (int j = 0; j < (80 - nextpoint.getPointName().length()); j++)
					{
						out.writeByte(0);
					}
					out.writeInt(1);
					if ("RANDOM".equalsIgnoreCase(nextpoint.getPointFunction()))
					{
						int first = gen.nextInt(100);
						float last = gen.nextFloat();
						value = first + last;
						out.writeFloat(value);
					} else if ("PYRAMID".equalsIgnoreCase(nextpoint.getPointFunction()))
					{
						double currentvalue = nextpoint.getPointCurrentValue();
						double incriment = nextpoint.getPointDelta();
						if ((currentvalue + incriment) > nextpoint.getPointMax())
						{
							Double cvdouble = new Double(currentvalue);
							value = cvdouble.floatValue();
							out.writeFloat(value);
							currentvalue = currentvalue + incriment;
							nextpoint.setPointCurrentValue(currentvalue);
							nextpoint.setPointDelta(nextpoint.getPointDelta() - (2 * nextpoint.getPointDelta()));
						} else if ((currentvalue + incriment) < nextpoint.getPointMin())
						{

							Double cvdouble = new Double(currentvalue);
							value = cvdouble.floatValue();
							out.writeFloat(value);
							currentvalue = currentvalue + incriment;
							nextpoint.setPointCurrentValue(currentvalue);
							nextpoint.setPointDelta(nextpoint.getPointDelta() + (-2 * nextpoint.getPointDelta()));
						} else
						{
							Double cvdouble = new Double(currentvalue);
							value = cvdouble.floatValue();
							out.writeFloat(value);
							currentvalue = currentvalue + incriment;
							nextpoint.setPointCurrentValue(currentvalue);
						}
					} else if ("DROPOFF".equalsIgnoreCase(nextpoint.getPointFunction()))
					{
						double currentvalue = nextpoint.getPointCurrentValue();
						double incriment = nextpoint.getPointDelta();
						if (nextpoint.getPointMaxStart())
						{

							Double cvdouble = new Double(currentvalue);
							value = cvdouble.floatValue();
							out.writeFloat(value);

							currentvalue = currentvalue - nextpoint.getPointDelta();

							if (currentvalue < nextpoint.getPointMin())
							{
								nextpoint.setPointDelta(nextpoint.getPointMax());
							}
							nextpoint.setPointCurrentValue(currentvalue);
						} else
						{

							Double cvdouble = new Double(currentvalue);
							value = cvdouble.floatValue();
							out.writeFloat(value);

							currentvalue = currentvalue + nextpoint.getPointDelta();

							if (currentvalue > nextpoint.getPointMax())
							{
								nextpoint.setPointDelta(nextpoint.getPointMin());
							}
							nextpoint.setPointCurrentValue(currentvalue);
						}
					}
					out.flush();
					SwingUtilities.invokeLater(new Logger(log, "SENT ON STARTUP: Value " + nextpoint.getPointName() + " Point: " + nf.format(value), 1));
					//Write message to traffic log file
					try
					{
						FileWriter traffic = new FileWriter(trafficFile, true);
						traffic.write(getDebugTimeStamp() + "SENT ON STARTUP: " + nextpoint.getPointType() + " Translation: " + nextpoint.getPointName() + " Quality: 1" + " Point: " + nf.format(value) + "\n");
						traffic.close();
					} catch (Exception e)
					{
						System.out.println(getDebugTimeStamp() + "Error with traffic stream");
						e.printStackTrace(System.out);
					}
				} else if ("Status".equals(nextpoint.getPointType()))
				{
					out.writeInt(RDEX_STATUS);
					out.writeBytes(getTimeStamp());
					out.writeBytes(nextpoint.getPointName());
					for (int j = 0; j < (80 - nextpoint.getPointName().length()); j++)
					{
						out.writeByte(0);
					}
					out.writeInt(1);
					out.writeInt(flipflop10);
					out.flush();
					//Write message to traffic log file
					String valueString = "";
					if (flipflop10 == 0)
					{
						valueString = "Open";
					} else
						valueString = "Close";
					SwingUtilities.invokeLater(new Logger(log, "SENT ON STARTUP: Status " + nextpoint.getPointName() + " Point: " + valueString, 1));
					try
					{
						FileWriter traffic = new FileWriter(trafficFile, true);
						traffic.write(getDebugTimeStamp() + "SENT ON STARTUP: " + nextpoint.getPointType() + " Translation: " + nextpoint.getPointName() + " Quality: 1" + " Point: " + valueString + "\n");
						traffic.close();
					} catch (Exception e)
					{
						System.out.println(getDebugTimeStamp() + "Error with traffic stream");
						e.printStackTrace(System.out);
					}
				} else if ("Control".equalsIgnoreCase(nextpoint.getPointType()))
				{
					out.writeInt(RDEX_CONTROL);
					out.writeBytes(getTimeStamp());
					out.writeBytes(nextpoint.getPointName());
					for (int j = 0; j < (80 - nextpoint.getPointName().length()); j++)
					{
						out.writeByte(0);
					}
					out.writeInt(1);
					out.writeInt(flipflop10);
					out.flush();
					//Write message to traffic log file
					String valueString = "";
					if (flipflop10 == 0)
					{
						valueString = "Open";
					} else
						valueString = "Close";
					SwingUtilities.invokeLater(new Logger(log, "SENT ON STARTUP: Control " + nextpoint.getPointName() + " Point: " + valueString, 1));
					try
					{
						FileWriter traffic = new FileWriter(trafficFile, true);
						traffic.write(getDebugTimeStamp() + "SENT ON STARTUP: " + nextpoint.getPointType() + " Translation: " + nextpoint.getPointName() + " Quality: 1" + " Point: " + valueString + "\n");
						traffic.close();
					} catch (Exception e)
					{
						System.out.println(getDebugTimeStamp() + "Error with traffic stream");
						e.printStackTrace(System.out);
					}
				}

				i++;
			} catch (Exception e)
			{
				if ("java.lang.NullPointerException".equalsIgnoreCase(e.toString()))
				{
					System.out.println(getDebugTimeStamp() + "Error writing from file while sending all points ");
				}
				e.printStackTrace(System.out);
				exit = 1;
			}
		}
		SwingUtilities.invokeLater(new Logger(log, "Finished sending points: " + getFormalTimeStamp(), 0));
	}

	public void sendManual(String mtype, String mname, String mvalue, String mquality, int mcount)
	{
		if ("Value".equalsIgnoreCase(mtype))
		{

			try
			{
				Thread.sleep(50);
				out.writeInt(RDEX_VALUE);
				out.writeBytes(getTimeStamp());
				out.writeBytes(mname);
				// pack on zeros to fill out 80 char string
				for (int j = 0; j < (80 - mname.length()); j++)
				{
					out.writeByte(0);
				}
				// four byte quality value 
				out.writeInt(Integer.parseInt(mquality));
				// four byte float value
				float mfloat = Float.parseFloat(mvalue);
				out.writeFloat(mfloat);
				SwingUtilities.invokeLater(new Logger(log, "SENT ON MANUAL: Value Name: " + mname + " Point: " + mvalue, 1));
				// Write message to traffic log file
				try
				{
					FileWriter traffic = new FileWriter(trafficFile, true);
					traffic.write(getDebugTimeStamp() + "SENT ON MANUAL: Value" + " Translation: " + mname + " Quality: " + mquality + " Value: " + mvalue + "\n");
					traffic.close();
				} catch (Exception e)
				{
					System.out.println(getDebugTimeStamp() + "Error with traffic stream");
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
				out.writeInt(RDEX_STATUS);
				out.writeBytes(getTimeStamp());
				out.writeBytes(mname);
				// pack on zeros to fill out 80 char string
				for (int j = 0; j < (80 - mname.length()); j++)
				{
					out.writeByte(0);
				}
				// four byte quality value 
				out.writeInt(Integer.parseInt(mquality));
				// four byte value
				int mint;
				try
				{
					mint = Integer.parseInt(mvalue);
				} catch (NumberFormatException e)
				{
					mint = 0;
				}
				out.writeInt(mint);
				String state = "";
				if ("1".equals(mvalue))
				{
					state = "Close";
				} else
					state = "Open";
				SwingUtilities.invokeLater(new Logger(log, "SENT ON MANUAL: Status Name: " + mname + " Point: " + state, 1));
				// Write message to traffic log file
				try
				{
					FileWriter traffic = new FileWriter(trafficFile, true);
					traffic.write(getDebugTimeStamp() + "SENT ON MANUAL: Status" + " Translation: " + mname + " Quality: " + mquality + " Value: " + state + "\n");
					traffic.close();
				} catch (Exception e)
				{
					System.out.println(getDebugTimeStamp() + "Error with traffic stream");
					e.printStackTrace(System.out);
				}
			} catch (Exception E)
			{
				System.out.println(getDebugTimeStamp() + "Error writing status manually ");
				E.printStackTrace(System.out);
			}

		} else if ("Control".equalsIgnoreCase(mtype))
		{

			try
			{
				Thread.sleep(50);
				out.writeInt(RDEX_CONTROL);
				out.writeBytes(getTimeStamp());
				out.writeBytes(mname);
				// pack on zeros to fill out 80 char string
				for (int j = 0; j < (80 - mname.length()); j++)
				{
					out.writeByte(0);
				}
				// four byte quality value 
				out.writeInt(Integer.parseInt(mquality));
				// four byte float value
				int mint;
				try
				{
					mint = Integer.parseInt(mvalue);
				} catch (NumberFormatException e)
				{
					mint = 0;
				}

				out.writeInt(mint);
				String state = "";
				if ("1".equals(mvalue))
				{
					state = "Close";
				} else
					state = "Open";
				SwingUtilities.invokeLater(new Logger(log, "SENT ON MANUAL: Control Name: " + mname + " Point: " + state, 1));
				// Write message to traffic log file
				try
				{
					FileWriter traffic = new FileWriter(trafficFile, true);
					traffic.write(getDebugTimeStamp() + "SENT ON MANUAL: Control" + " Translation: " + mname + " Quality: " + mquality + " Value: " + state + "\n");
					traffic.close();
				} catch (Exception e)
				{
					System.out.println(getDebugTimeStamp() + "Error with traffic stream");
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
		int num = 0;
		int function = 0;
		float rFloatValue;
		int rStatusValue;
		int quality;
		int index;
		String pointString = "";
		byte[] pointName = new byte[2048];
		byte[] time = new byte[2048];
		try
		{
			quit = 0;
			// Read from socket queue, If excecption occurs cancel the read thread and close connection and reconnect
			try
			{
				function = in.readInt();
			} catch (Exception e)
			{
				System.out.println(e.toString());
				if (readThread == null)
				{
					quit = 1;
				} else
				{
					SwingUtilities.invokeLater(new Logger(log, "Read Failed from Yukon socket connection.", 3));
					SwingUtilities.invokeLater(new Logger(log, "Stopping read thread and closing connection...", 3));
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
					case RDEX_NULL :

						// Recieved a heartbeat message
						if (hbeat == true)
						{
							SwingUtilities.invokeLater(new Logger(log, "RECV: Heartbeat", 2));
							SwingUtilities.invokeLater(new Logger(log, "SENT: Heartbeat", 1));
						}
						num = in.read(time, 0, 16);
						in.read(pointName, 0, 88);
						FileWriter traffic = new FileWriter(trafficFile, true);
						traffic.write(getDebugTimeStamp() + "RECV              " + "Heartbeat messsage" + "\n");
						traffic.close();
						// send heartbeat reply
						out.writeInt(0);
						out.writeBytes(getTimeStamp());
						for (int i = 0; i < 80; i++)
						{
							out.writeByte(0);
						}
						out.writeInt(0);
						out.writeInt(0);
						FileWriter traffic2 = new FileWriter(trafficFile, true);
						traffic2.write(getDebugTimeStamp() + "SENT              " + "Heartbeat messsage" + "\n");
						traffic2.close();
						quit = 1;
						break;

					case RDEX_REG :

						// Should never get this
						quit = 1;
						break;

					case RDEX_ACK :

						// Recieved ACK
						SwingUtilities.invokeLater(new Logger(log, "RECV: Acknowledge", 2));
						//System.out.println(getDebugTimeStamp() + "RDEX RECV: Acknowledge");
						// Write message to traffic log file
						num = in.read(time, 0, 16);
						in.read(pointName, 0, 88);
						index = 0;
						for (int i = 0; i < 80; i++)
						{
							if (pointName[i] == 0)
							{
								index = i;
								break;
							}
						}
						pointString = new String(pointName, 0, index);
						try
						{
							FileWriter traffic3 = new FileWriter(trafficFile, true);
							traffic3.write(getDebugTimeStamp() + "RECV " + "Type: Acknowledge From: " + pointString + "\n");
							traffic3.close();
						} catch (Exception e)
						{
							e.printStackTrace(System.out);
						}
						// send all points initailly so fdr knows what we have
						sendAllPoints();
						quit = 1;
						break;

					case RDEX_VALUE :

						// Received a point value
						num = in.read(time, 0, 16);
						in.read(pointName, 0, 80);
						index = 0;
						for (int i = 0; i < 80; i++)
						{
							if (pointName[i] == 0)
							{
								index = i;
								break;
							}
						}
						pointString = new String(pointName, 0, index);
						quality = in.readInt();
						rFloatValue = in.readFloat();
						System.out.println(getDebugTimeStamp() + "RDEX RECV: Value " + rFloatValue);
						SwingUtilities.invokeLater(new Logger(log, "RECV: Value " + "Name: " + pointString + " Value: " + nf.format(rFloatValue) + " Quality: " + quality + " TStamp: " + new String(time, 0, 16), 2));
						writeToFile("Value", pointString, (double) rFloatValue);
						quit = 1;
						break;

					case RDEX_STATUS :

						// Received a satus message
						num = in.read(time, 0, 16);
						in.read(pointName, 0, 80);
						index = 0;
						for (int i = 0; i < 80; i++)
						{
							if (pointName[i] == 0)
							{
								index = i;
								break;
							}
						}
						pointString = new String(pointName, 0, index);
						quality = in.readInt();
						rStatusValue = in.readInt();
						String statusValueString = "";
						if (rStatusValue == 1)
						{
							statusValueString = "Close";
						} else if (rStatusValue == 0)
						{
							statusValueString = "Open";
						}
						System.out.println(getDebugTimeStamp() + "RDEX RECV: Status " + statusValueString);
						SwingUtilities.invokeLater(new Logger(log, "RECV: Status " + "Name: " + pointString + " State: " + statusValueString + " Quality: " + quality + " TStamp: " + new String(time, 0, 16), 2));
						writeToFile("Status", pointString, (double) rStatusValue);
						quit = 1;
						break;

					case RDEX_CONTROL :

						// Received a control message
						num = in.read(time, 0, 16);
						in.read(pointName, 0, 80);
						index = 0;
						for (int i = 0; i < 80; i++)
						{
							if (pointName[i] == 0)
							{
								index = i;
								break;
							}
						}
						pointString = new String(pointName, 0, index);
						quality = in.readInt();
						rStatusValue = in.readInt();
						String controlValueString = "";
						if (rStatusValue == 1)
						{
							controlValueString = "Close";
						} else if (rStatusValue == 0)
						{
							controlValueString = "Open";
						}
						SwingUtilities.invokeLater(new Logger(log, "RECV: Control " + "Name: " + pointString + " State: " + controlValueString + " TStamp: " + new String(time, 0, 16), 2));
						System.out.println(getDebugTimeStamp() + "RDEX RECV: Control " + controlValueString);
						writeToFile("Control", pointString, (double) rStatusValue);
						quit = 1;
						break;

					default :

						// Received unknown message
						SwingUtilities.invokeLater(new Logger(log, "Received unknown message type", 3));
						System.out.println(getDebugTimeStamp() + "Received unknown message type");
						quit = 1;
						break;
				}
				heartbeat = new Timer();
				heartbeat.schedule(new heartbeatTask(), T1_TIME, T1_TIME);
			}
		} catch (IOException e)
		{
			SwingUtilities.invokeLater(new Logger(log, "Error reading from input stream", 3));
			if ("java.lang.NullPointerException".equalsIgnoreCase(e.toString()))
			{
				SwingUtilities.invokeLater(new Logger(log, "Input stream was null", 3));
			}
			e.printStackTrace(System.out);
		}
		return;
	}

	// Write recieved message to point list file and traffic log file
	public void writeToFile(String ftype, String fname, double fpoint)
	{

		boolean newpoint = false;

		// make buffered reader
		BufferedReader fileBuffer = null;
		try
		{
			fileBuffer = new BufferedReader(new FileReader("resource/rdex_yukon_points.txt"));
		} catch (Exception e)
		{
			if (e.toString().equalsIgnoreCase("java.io.FileNotFoundException"))
			{
				System.out.println("File not found when trying to load points from file");
			} else
				e.printStackTrace(System.out);
		}

		String[] pointAsStrings = new String[500];

		// fill points array with point from yukon file

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
					String pointname = st.nextToken();
					String interval = st.nextToken();
					String lineEntry = pointtype + ";" + pointname + ";" + interval;

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
			fileBuffer = new BufferedReader(new FileReader("resource/rdex_yukon_points.txt"));
		} catch (Exception e)
		{
			if (e.toString().equalsIgnoreCase("java.io.FileNotFoundException"))
			{
				System.out.println("File not found when trying to load points from file");
			} else
				e.printStackTrace(System.out);
		}

		String currentLine = "";
		// Main writing loop
		while (out != 1)
		{
			try
			{
				currentLine = fileBuffer.readLine();
				StringTokenizer ft = new StringTokenizer(currentLine, ";");

				if ("EOF".equalsIgnoreCase(currentLine))
				{ // Point does not exist, set newpoint to true
					try
					{

						SwingUtilities.invokeLater(new Logger(log, "Unexpected point from Yukon: ", 4));
						newpoint = true;
						out = 1;

					} catch (Exception e)
					{

						System.out.println(getDebugTimeStamp() + "Error writing new point to end of point file");
						e.printStackTrace(System.out);
						out = 1;
					}
				} else if (ft.nextToken().equalsIgnoreCase(ftype))
				{
					if (ft.nextToken().equalsIgnoreCase(fname))
					{

						newpoint = false;
						out = 1;
					}
				}
			} catch (Exception e)
			{
				if ("java.lang.NullPointerException".equalsIgnoreCase(e.toString()))
				{

					System.out.println(getDebugTimeStamp() + "EOF ERROR while writing to file");
					SwingUtilities.invokeLater(new Logger(log, "EOF ERROR while writing to file", 3));

				}
				e.printStackTrace(System.out);
				out = 1;
			}
		}

		if (newpoint)
		{
			try
			{
				FileWriter yukonFileWriter = new FileWriter(new File("resource/rdex_yukon_points.txt"));

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
					}

				}
				String formattedPoint = "";
				if (ftype.equalsIgnoreCase("Value"))
				{
					formattedPoint = nf.format(fpoint);
				} else
				{
					if (fpoint == 1.0)
					{
						formattedPoint = "Close";
					} else
					{
						formattedPoint = "Open  ";
					}
				}

				yukonFileWriter.write(ftype + ";" + fname + ";" + formattedPoint + LF);
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
				FileWriter yukonFileWriter = new FileWriter(new File("resource/rdex_yukon_points.txt"));

				for (int j = 0; i < pointAsStrings.length; j++)
				{
					try
					{

						StringTokenizer st = new StringTokenizer(pointAsStrings[j], ";");
						String type = st.nextToken();
						String name = st.nextToken();

						if (type.equalsIgnoreCase(ftype) && name.equalsIgnoreCase(fname))
						{

							String formattedPoint = "";
							if (ftype.equalsIgnoreCase("Value"))
							{
								formattedPoint = nf.format(fpoint);
							} else
							{
								if (fpoint == 1.0)
								{
									formattedPoint = "Close";
								} else
								{
									formattedPoint = "Open  ";
								}
							}

							yukonFileWriter.write(ftype + ";" + fname + ";" + formattedPoint + LF);

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
				pointword = nf.format(fpoint);
			} else
			{
				if (fpoint == 1.0)
				{
					pointword = "Close";
				} else
				{
					pointword = "Open";
				}
			}
			FileWriter traffic = new FileWriter(trafficFile, true);
			traffic.write(getDebugTimeStamp() + "RECV              " + ftype + " Point: " + pointword + "\n");
			traffic.close();
		} catch (Exception e)
		{
			System.out.println(getDebugTimeStamp() + "Error with traffic stream");
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
		return new String("RDEX");
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
			//return df.format(new Date()) + "S ";
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
			stop();
			Thread.sleep(10);
			out.close();
			in.close();
			fdeSocket.close();
		} catch (Exception e)
		{
			System.out.println(getDebugTimeStamp() + "Error closing connection");
			if (e.toString().equalsIgnoreCase("java.lang.NullPointerException"))
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
		settings = new RdexSettings("Settings", this);
		settings.listenForActions(testPanel.getWindow());
		//		Center the window
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = settings.getSize();
		if (frameSize.height > screenSize.height)
		{
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width)
		{
			frameSize.width = screenSize.width;
		}
		settings.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		settings.setVisible(true);
		settings.show();
	}
}