/*
 * Created on Jun 2, 2004
 */
package com.cannontech.fdemulator.protocols;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
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
import com.cannontech.fdemulator.common.Logger;
import com.cannontech.fdemulator.common.TraceLogPanel;
import com.cannontech.fdemulator.fileio.ValmetFileIO;

/**
 * @author ASolberg
 */
public class ValmetProtocol extends FDEProtocol implements Runnable {
    // settings gui
    private ValmetSettings settings = null;

    // loop escape
    private int quit = 0;

    // socket and streams

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
    private int DEFAULT_YUKON_PORT = 1666;
    private int yukon_port = 0;
    private int yukon_in_port = 0;

    private String server;
    private int retryStart = 0;

    // message types
    private final int VALMET_NULL = 0;
    private final int VALMET_VALUE = 101;
    private final int VALMET_STATUS = 102;
    // private final int VALMET_ANALOGOUTPUT = 103;
    private final int VALMET_CONTROL = 201;
    // private final int VALMET_TIMESYNC = 401;

    // timers and timer variables
    private Timer heartbeat;
    private Timer interval;
    private Timer timesync;
    private int thirtySecond = 0;
    private int sixtySecond = 0;
    private int fiveMinute = 0;
    private int fifteenMinute = 0;
    private int sixtyMinute = 0;
    private int flipflop10 = 1;
    private int flipflop30 = 1;
    private int flipflop60 = 1;
    private int flipflop300 = 1;
    private int flipflop900 = 1;
    private int flipflop3600 = 1;
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
    private final String DEFAULT_POINTFILE = "resource/valmet_points.cfg";
    private final String DEFAULT_TRAFFICFILE = "resource/valmet_traffic_log.txt";
    private RandomAccessFile pointList;
    private String pointFile = null;
    private String trafficFile = null;
    private ValmetFileIO valmetFileIO = null;

    // stat panel variables
    private JPanel statPanel = new JPanel();
    private JLabel serverLabel = new JLabel("Server:");
    private JLabel back1Label = new JLabel("Backup 1:");
    private JLabel back2Label = new JLabel("Backup 2:");
    private JLabel back3Label = new JLabel("Backup 3:");
    private JLabel portLabel = new JLabel("Port:");
    private JLabel inPortLabel = new JLabel("Listen Port:");
    private JLabel pathLabel = new JLabel("Point File:");
    private JLabel trafficLabel = new JLabel("Traffic Log File:");
    private JLabel extendedNameLabel = new JLabel("Use Extended Name:");
    private JLabel serverDataLabel = new JLabel();
    private JLabel back1DataLabel = new JLabel();
    private JLabel back2DataLabel = new JLabel();
    private JLabel back3DataLabel = new JLabel();
    private JLabel portDataLabel = new JLabel();
    private JLabel inPortDataLabel = new JLabel();
    private JLabel pathDataLabel = new JLabel();
    private JLabel trafficDataLabel = new JLabel();
    private JLabel extendedNameDataLabel = new JLabel();

    // protocol utility variables
    private NumberFormat nf;
    private Random gen = new Random();
    private FDTestPanelNotifier notifier;
    private Object[] pointarray;
    private SimpleTimeZone tz;
    private GregorianCalendar gc;
    private static final String LF = System.getProperty("line.separator");

    /**
     * Main constructor:
     * Set TraceLogInstance to parent TestPanel's ouput log.
     * Save reference to parents test panel.
     */
    public ValmetProtocol(TraceLogPanel logPanel, FDTestPanel panel) {
        log = logPanel;
        testPanel = panel;

        nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMaximumIntegerDigits(5);
        nf.setMinimumFractionDigits(2);
        nf.setMinimumIntegerDigits(2);

        // Start settings frame
        settings = new ValmetSettings("Settings", this);
        settings.listenForActions(testPanel.getWindow());
        // Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = settings.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        settings.setLocation((screenSize.width - frameSize.width) / 2,
                             (screenSize.height - frameSize.height) / 2);
        settings.setVisible(true);
        statPanel.setLayout(null);
        serverLabel.setBounds(new Rectangle(5, 5, 48, 30));
        back1Label.setBounds(new Rectangle(5, 30, 48, 30));
        back2Label.setBounds(new Rectangle(5, 55, 48, 30));
        back3Label.setBounds(new Rectangle(5, 80, 48, 30));
        portLabel.setBounds(new Rectangle(5, 105, 48, 30));
        inPortLabel.setBounds(new Rectangle(5, 130, 48, 30));
        pathLabel.setBounds(new Rectangle(5, 155, 48, 30));
        trafficLabel.setBounds(new Rectangle(5, 215, 80, 20));
        extendedNameLabel.setBounds(new Rectangle(5, 270, 80, 20));

        serverDataLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        back1DataLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        back2DataLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        back3DataLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        portDataLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        inPortDataLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        pathDataLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        trafficDataLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        extendedNameDataLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));

        serverDataLabel.setBounds(new Rectangle(55, 5, 90, 30));
        back1DataLabel.setBounds(new Rectangle(55, 30, 90, 30));
        back2DataLabel.setBounds(new Rectangle(55, 55, 90, 30));
        back3DataLabel.setBounds(new Rectangle(55, 80, 90, 30));
        portDataLabel.setBounds(new Rectangle(55, 105, 90, 30));
        inPortDataLabel.setBounds(new Rectangle(55, 130, 90, 30));
        pathDataLabel.setBounds(new Rectangle(5, 185, 145, 30));
        trafficDataLabel.setBounds(new Rectangle(5, 235, 145, 30));
        extendedNameDataLabel.setBounds(new Rectangle(5, 290, 90, 30));

        statPanel.add(serverLabel);
        statPanel.add(back1Label);
        statPanel.add(back2Label);
        statPanel.add(back3Label);
        statPanel.add(portLabel);
        statPanel.add(inPortLabel);
        statPanel.add(pathLabel);
        statPanel.add(trafficLabel);
        statPanel.add(extendedNameLabel);
        statPanel.add(serverDataLabel);
        statPanel.add(back1DataLabel);
        statPanel.add(back2DataLabel);
        statPanel.add(back3DataLabel);
        statPanel.add(portDataLabel);
        statPanel.add(inPortDataLabel);
        statPanel.add(pathDataLabel);
        statPanel.add(trafficDataLabel);
        statPanel.add(extendedNameDataLabel);

        notifier = getTestPanel().getNotifier();

        tz =
            new SimpleTimeZone(-21600000,
                               "America/Central",
                               Calendar.APRIL,
                               1,
                               -Calendar.SUNDAY,
                               7200000,
                               Calendar.OCTOBER,
                               -1,
                               Calendar.SUNDAY,
                               7200000,
                               3600000);
        gc = new GregorianCalendar(tz);
        getTimeStamp();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    private class heartbeatTask extends TimerTask {
        /**
         * run gets called when a timer expires. Timers are
         * set to ensure logon processing and message blocks
         * arrive in timely fashion. A timer going off
         * indicates an error that will be processed
         */
        public void run() {
            retryHeartbeat++;
            SwingUtilities.invokeLater(new Logger(log, "Heartbeat timeout: " + getTimeStamp(), 4));
            // write message to traffic log file
            try {
                FileWriter traffic = new FileWriter("resource/valmet_traffic_log.txt", true);
                traffic
                    .write(getDebugTimeStamp()
                           + "-------------------10 SECOND HEARTBEAT TIMED OUT-------------------"
                           + "\n");
                traffic.close();
            } catch (Exception e) {
                System.out.println(getDebugTimeStamp() + "Error with traffic stream");
                e.printStackTrace(System.out);
            }

            if (retryHeartbeat == 6) {
                SwingUtilities
                    .invokeLater(new Logger(log,
                                            "Closing Connection: too many heartbeat timeouts",
                                            3));
                closeConnection();

                // write message to traffic log file
                try {
                    FileWriter traffic = new FileWriter("resource/valmet_traffic_log.txt", true);
                    traffic
                        .write(getDebugTimeStamp()
                               + "---------DISCONNECTING DUE TO 60 SECOND TIME OUT FROM YUKON--------"
                               + "\n");
                    traffic.close();
                } catch (Exception e) {
                    System.out.println(getDebugTimeStamp() + "Error with traffic stream");
                    e.printStackTrace(System.out);
                }
                notifier.setActionCode(FDTestPanelNotifier.ACTION_CONNECTION_LOST);
            }
            // send heartbeat
            try {
                out.writeShort(VALMET_NULL);
                out.writeBytes(getTimeStamp());
                int nameLength = settings.isExtendedName() ? 32 : 16;
                for (int i = 0; i < nameLength + 8; i++) {
                    out.writeByte(0);
                }
                FileWriter traffic = new FileWriter(trafficFile, true);
                traffic
                    .write(getDebugTimeStamp()
                           + "SENT              "
                           + "Heartbeat messsage due to heartbeat timer timeout------------------------"
                           + "\n");
                traffic.close();
            } catch (Exception e) {
                SwingUtilities.invokeLater(new Logger(log,
                                                      "Error sending heartbeat from hb timeout",
                                                      3));
                e.printStackTrace(System.out);
            }
        }
    }

    // Run method for point send interval timer
    private class intervalTask extends TimerTask {
        /**
         * Run gets called when the interval timer expires,
         * sendPoints gets called to send all points whose intervals are up.
         */
        public void run() {
            thirtySecond++;
            sixtySecond++;
            fiveMinute++;
            fifteenMinute++;
            sixtyMinute++;
            if (thirtySecond == 4) {
                thirtySecond = 1;
            }
            if (sixtySecond == 7) {
                sixtySecond = 1;
            }
            if (fiveMinute == 31) {
                fiveMinute = 1;
            }
            if (fifteenMinute == 91) {
                fifteenMinute = 1;
            }
            if (sixtyMinute == 361) {
                sixtyMinute = 1;
            }
            if (flipflop10 == 1) {
                flipflop10 = 2;
            } else if (flipflop10 == 2) {
                flipflop10 = 1;
            }
            if (thirtySecond == 3) {
                if (flipflop30 == 1) {
                    flipflop30 = 2;
                } else if (flipflop30 == 2) {
                    flipflop30 = 1;
                }
            }
            if (sixtySecond == 6) {
                if (flipflop60 == 1) {
                    flipflop60 = 2;
                } else if (flipflop60 == 2) {
                    flipflop60 = 1;
                }
            }
            if (fiveMinute == 30) {
                if (flipflop300 == 1) {
                    flipflop300 = 2;
                } else if (flipflop300 == 2) {
                    flipflop300 = 1;
                }
            }
            if (fifteenMinute == 90) {
                if (flipflop900 == 1) {
                    flipflop900 = 2;
                } else if (flipflop900 == 2) {
                    flipflop900 = 1;
                }
            }
            if (sixtyMinute == 360) {
                if (flipflop3600 == 1) {
                    flipflop3600 = 2;
                } else if (flipflop3600 == 2) {
                    flipflop3600 = 1;
                }
            }
            sendPoints();
        }
    }

    // Run method for sending five minute time sync
    private class timesyncTask extends TimerTask {
        /**
         * Run gets called when the timer expires, just sends a timesync to fdr who will
         * probably ignore it
         */
        public void run() {
            try {
                out.writeShort(401);
                out.writeBytes(getTimeStamp());
                int nameLength = settings.isExtendedName() ? 32 : 16;
                for (int i = 0; i < nameLength + 8; i++) {
                    out.writeByte(0);
                }
            } catch (Exception e) {
                System.out.println(getDebugTimeStamp() + "Error sending timesync");
                e.printStackTrace(System.out);
            }
            SwingUtilities
                .invokeLater(new Logger(log, "SENT TimeSync Message " + getTimeStamp(), 1));

            // Write message to traffic log file
            try {
                FileWriter traffic = new FileWriter(trafficFile, true);
                traffic.write(getDebugTimeStamp()
                              + "SENT ON INTERVAL: TIME SYNC MESSAGE---------------------\n");
                traffic.close();
            } catch (IOException e) {
                System.out.println(getDebugTimeStamp() + "IO Error with traffic stream ");
                e.printStackTrace(System.out);
            }
        }
    }

    public void run() {
        try {

            // for (;;)
            Thread thisThread = Thread.currentThread();
            while (readThread == thisThread) {
                synchronized (this) {
                    readBytes();
                }
            }
        } catch (Exception e) {
            System.out.println(getDebugTimeStamp() + "Error in read thread run method");
        }
    }

    public void stop() {
        Thread killme = readThread;
        readThread = null;
        killme.interrupt();
    }

    // Method to make sure settings are done b4 starting
    public boolean settingDone() {
        return settings.isSet();
    }

    public JPanel getStatPanel() {
        return statPanel;
    }

    public FDTestPanel getTestPanel() {
        return testPanel;
    }

    public ValmetSettings getSettings() {
        return settings;
    }

    public void updateStats() {
        serverDataLabel.setText(settings.getServer());
        back1DataLabel.setText(settings.getBack1());
        back2DataLabel.setText(settings.getBack2());
        back3DataLabel.setText(settings.getBack3());
        portDataLabel.setText(settings.getPort());
        inPortDataLabel.setText(settings.getInPort());
        pathDataLabel.setText(settings.getPath());
        trafficDataLabel.setText(settings.getTrafficPath());
        extendedNameDataLabel.setText(settings.isExtendedName() ? "true" : "false");
    }

    public boolean startConnection() {
        retryStart++;
        if (retryStart == 5)
            retryStart = 1;
        // get settings
        if (settings.getServer().equals("")) {
            yukon_host = DEFAULT_YUKON_HOST;
        } else {
            yukon_host = settings.getServer();
        }
        YUKON_BACKUP1 = settings.getBack1();
        YUKON_BACKUP2 = settings.getBack2();
        YUKON_BACKUP3 = settings.getBack3();
        if (settings.getPath().equals("")) {
            pointFile = DEFAULT_POINTFILE;
        } else {
            pointFile = settings.getPath();
        }
        if (settings.getTrafficPath().equals("")) {
            trafficFile = DEFAULT_TRAFFICFILE;
        } else {
            trafficFile = settings.getTrafficPath();
        }
        if (settings.getPort().equals("")) {
            yukon_port = DEFAULT_YUKON_PORT;
        } else {
            yukon_port = Integer.parseInt(settings.getPort());
        }
        if (settings.getInPort().equals("")) {
            yukon_in_port = DEFAULT_YUKON_PORT;
        } else {
            yukon_in_port = Integer.parseInt(settings.getInPort());
        }

        if (retryStart == 1) {
            server = yukon_host;
        } else if (retryStart == 2) {
            server = YUKON_BACKUP1;
        } else if (retryStart == 3) {
            server = YUKON_BACKUP2;
        } else {
            server = YUKON_BACKUP3;
        }
        // Create Socket
        try {
            if (yukon_port == yukon_in_port) {
                fdeSocket = new Socket(server, yukon_port);
            } else {
                InetAddress fdr = InetAddress.getByName(server);
                InetAddress emulator = InetAddress.getLocalHost();

                System.out.println(getDebugTimeStamp() + "FDR:" + fdr.getHostAddress() + "("
                                   + fdr.getHostName() + ")");
                System.out.println(getDebugTimeStamp() + "EMULATOR:" + emulator.getHostAddress()
                                   + "(" + emulator.getHostName() + ")");
                SwingUtilities.invokeLater(new Logger(log, "FDR:" + fdr.getHostAddress() + "("
                                                           + fdr.getHostName() + ")", 3));
                SwingUtilities
                    .invokeLater(new Logger(log, "EMULATOR:" + emulator.getHostAddress() + "("
                                                 + emulator.getHostName() + ")", 3));
                fdeSocket =
                    new Socket(server.equalsIgnoreCase("127.0.0.1") ? InetAddress.getLocalHost()
                            : fdr, yukon_port, InetAddress.getLocalHost(), yukon_in_port);
            }

            SwingUtilities.invokeLater(new Logger(log, "Connected to server socket", 0));
        } catch (Exception e) {
            if ("java.net.UnknownHostException".equalsIgnoreCase(e.toString())) {
                System.out.println(getDebugTimeStamp() + yukon_host
                                   + " not visible on the network: ");
                SwingUtilities.invokeLater(new Logger(log, yukon_host
                                                           + " is not visible on the network", 3));
                return false;
            } else if ("java.io.IOException".equalsIgnoreCase(e.toString())) {
                System.out.println(getDebugTimeStamp() + "IOException when creating socket");
                SwingUtilities.invokeLater(new Logger(log, "IOException when creating socket", 3));
                return false;
            } else if ("java.net.ConnectException: Connection refused: connect".equalsIgnoreCase(e
                .toString())) {
                System.out.println(getDebugTimeStamp() + "ConnectException: connection refused");
                SwingUtilities
                    .invokeLater(new Logger(log,
                                            "ConnectException when creating socket: connection refused",
                                            3));
                return false;
            } else {
                e.printStackTrace(System.out);
                return false;
            }
        }

        SwingUtilities.invokeLater(new Logger(log, "Connected on server " + retryStart, 0));
        if (retryStart == 1) {
            serverDataLabel.setBackground(Color.BLUE);
            back1DataLabel.setBackground(Color.BLACK);
            back2DataLabel.setBackground(Color.BLACK);
            back3DataLabel.setBackground(Color.BLACK);
        } else if (retryStart == 2) {
            back1DataLabel.setBackground(Color.BLUE);
            serverDataLabel.setBackground(Color.BLACK);
            back2DataLabel.setBackground(Color.BLACK);
            back3DataLabel.setBackground(Color.BLACK);
        } else if (retryStart == 3) {
            back2DataLabel.setBackground(Color.BLUE);
            serverDataLabel.setBackground(Color.BLACK);
            back1DataLabel.setBackground(Color.BLACK);
            back3DataLabel.setBackground(Color.BLACK);
        } else if (retryStart == 4) {
            back3DataLabel.setBackground(Color.BLUE);
            serverDataLabel.setBackground(Color.BLACK);
            back1DataLabel.setBackground(Color.BLACK);
            back2DataLabel.setBackground(Color.BLACK);
        }

        // Create data output stream
        try {
            out = new DataOutputStream(fdeSocket.getOutputStream());
            SwingUtilities.invokeLater(new Logger(log, "Output stream created successfully", 0));
        } catch (IOException e) {
            System.out.println(getDebugTimeStamp() + "Could not extablish output stream to "
                               + yukon_host + ": " + e);
            SwingUtilities.invokeLater(new Logger(log, "Could not establish output stream to "
                                                       + yukon_host, 3));
        }

        // Create buffered input stream
        try {
            in = new DataInputStream(fdeSocket.getInputStream());
            SwingUtilities.invokeLater(new Logger(log, "Input stream created successfully", 0));
        } catch (Exception e) {
            System.out.println(getDebugTimeStamp() + "Error creating input stream");
            e.printStackTrace(System.out);
            SwingUtilities.invokeLater(new Logger(log, "Error creating input stream", 3));
        }

        // Create recieve log random access file
        try {
            pointList = new RandomAccessFile("resource/valmet_yukon_points.txt", "rw");
        } catch (Exception e) {
            System.out.println(getDebugTimeStamp() + "Error creating recv log file");
            SwingUtilities.invokeLater(new Logger(log, "Error creating recv log file", 3));
        }

        // Start thread for reading
        readThread = new Thread(this, "VALMET READ THREAD");
        readThread.start();

        // Start timers
        heartbeat = new Timer();
        heartbeat.schedule(new heartbeatTask(), T1_TIME, T1_TIME);
        interval = new Timer();
        interval.schedule(new intervalTask(), T2_TIME, T2_TIME);
        timesync = new Timer();
        timesync.schedule(new timesyncTask(), T3_TIME, T3_TIME);

        // Write message to traffic log file
        try {
            FileWriter traffic = new FileWriter(trafficFile, true);
            traffic.write(getDebugTimeStamp()
                          + "----------FDE VALMET EMULATOR STARTED SUCESSFULLY---------\n");
            traffic.close();
        } catch (IOException e) {
            System.out.println(getDebugTimeStamp() + "IO Error with traffic stream ");
            e.printStackTrace(System.out);
        }

        // fill point array with points from point file
        pointarray = getValmetFileIO().getValmetPointsFromFileForPort(yukon_in_port);

        return true;
    }

    public ValmetFileIO getValmetFileIO() {
        if (valmetFileIO == null) {
            valmetFileIO = new ValmetFileIO(settings.getPath());
        }
        return valmetFileIO;
    }

    // Send random points based on interval from file as timers expire
    public void sendPoints() {
        // System.out.println(getDebugTimeStamp() + "VALMET   Counters: 30 sec: " + thirtySecond +
        // "        60 sec: " + sixtySecond + "        5 min: " + fiveMinute + "        15 min: " +
        // fifteenMinute + "        60 min: " + sixtyMinute);
        int exit = 0;
        int i = 0;
        ValmetPoint nextpoint;
        ValmetPoint lastpoint;

        while (exit != 1) {
            Random x = new Random();
            Float value = x.nextFloat();
            try {
                nextpoint = (ValmetPoint) pointarray[i];
                lastpoint = (ValmetPoint) pointarray[i + 1];
                try {
                    if (lastpoint.getPointName().equalsIgnoreCase("")) {
                        exit = 1;
                    }
                } catch (Exception e) {
                    if ("java.lang.NullPointerException".equalsIgnoreCase(e.toString())) {
                        exit = 1;
                    } else {
                        e.printStackTrace(System.out);
                        exit = 1;
                    }
                }
                // if (nextpoint.getPort() == yukon_in_port)
                // {
                if (nextpoint.getPointInterval() == 10) {
                    if ("Value".equalsIgnoreCase(nextpoint.getPointType())) {
                        createValuePoint(out, nextpoint, value);
                    } else if ("Status".equalsIgnoreCase(nextpoint.getPointType())) {
                        createStatusPoint(out, nextpoint, value, flipflop10);
                    } else if ("Control".equalsIgnoreCase(nextpoint.getPointType())
                               || "Analog Output".equalsIgnoreCase(nextpoint.getPointType())) {
                        createControlPoint(out, nextpoint, value, flipflop10);
                    }
                } else if (nextpoint.getPointInterval() == 30 && thirtySecond == 3) {
                    if ("Value".equals(nextpoint.getPointType())) {
                        createValuePoint(out, nextpoint, value);
                    } else if ("Status".equalsIgnoreCase(nextpoint.getPointType())) {
                        createStatusPoint(out, nextpoint, value, flipflop30);
                    } else if ("Control".equalsIgnoreCase(nextpoint.getPointType())
                               || "Analog Output".equalsIgnoreCase(nextpoint.getPointType())) {
                        createControlPoint(out, nextpoint, value, flipflop30);
                    }
                } else if (nextpoint.getPointInterval() == 60 && sixtySecond == 6) {
                    if ("Value".equals(nextpoint.getPointType())) {
                        createValuePoint(out, nextpoint, value);
                    } else if ("Status".equalsIgnoreCase(nextpoint.getPointType())) {
                        createStatusPoint(out, nextpoint, value, flipflop60);
                    } else if ("Control".equalsIgnoreCase(nextpoint.getPointType())
                               || "Analog Output".equalsIgnoreCase(nextpoint.getPointType())) {
                        createControlPoint(out, nextpoint, value, flipflop60);
                    }
                } else if (nextpoint.getPointInterval() == 300 && fiveMinute == 30) {
                    if ("Value".equals(nextpoint.getPointType())) {
                        createValuePoint(out, nextpoint, value);
                    } else if ("Status".equalsIgnoreCase(nextpoint.getPointType())) {
                        createStatusPoint(out, nextpoint, value, flipflop300);
                    } else if ("Control".equalsIgnoreCase(nextpoint.getPointType())
                               || "Analog Output".equalsIgnoreCase(nextpoint.getPointType())) {
                        createControlPoint(out, nextpoint, value, flipflop300);
                    }
                } else if (nextpoint.getPointInterval() == 900 && fifteenMinute == 90) {
                    if ("Value".equals(nextpoint.getPointType())) {
                        createValuePoint(out, nextpoint, value);
                    } else if ("Status".equalsIgnoreCase(nextpoint.getPointType())) {
                        createStatusPoint(out, nextpoint, value, flipflop900);
                    } else if ("Control".equalsIgnoreCase(nextpoint.getPointType())
                               || "Analog Output".equalsIgnoreCase(nextpoint.getPointType())) {
                        createControlPoint(out, nextpoint, value, flipflop900);
                    }
                } else if (nextpoint.getPointInterval() == 3600 && fiveMinute == 360) {
                    if ("Value".equals(nextpoint.getPointType())) {
                        createValuePoint(out, nextpoint, value);
                    } else if ("Status".equalsIgnoreCase(nextpoint.getPointType())) {
                        createStatusPoint(out, nextpoint, value, flipflop3600);
                    } else if ("Control".equalsIgnoreCase(nextpoint.getPointType())
                               || "Analog Output".equalsIgnoreCase(nextpoint.getPointType())) {
                        createControlPoint(out, nextpoint, value, flipflop3600);
                    }
                }
                // }

                i++;
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }
    }

    // Send all points from file reguardless of interval
    public void sendAllPoints() {
        int exit = 0;
        int i = 0;
        ValmetPoint nextpoint;
        ValmetPoint lastpoint;
        float value = 0.0f;
        while (exit != 1) {
            try {
                nextpoint = (ValmetPoint) pointarray[i];

                lastpoint = (ValmetPoint) pointarray[i + 1];

                try {
                    if (lastpoint.getPointName().equalsIgnoreCase("")) {
                        exit = 1;
                    }
                } catch (Exception e) {
                    if ("java.lang.NullPointerException".equalsIgnoreCase(e.toString())) {
                        exit = 1;
                    } else {
                        e.printStackTrace(System.out);
                        exit = 1;
                    }
                }

                if ("Value".equalsIgnoreCase(nextpoint.getPointType())) {
                    createValuePoint(out, nextpoint, value);
                } else if ("Status".equalsIgnoreCase(nextpoint.getPointType())) {
                    createStatusPoint(out, nextpoint, value, flipflop10);
                } else if ("Control".equalsIgnoreCase(nextpoint.getPointType())
                           || "Analog Output".equalsIgnoreCase(nextpoint.getPointType())) {
                    createControlPoint(out, nextpoint, value, flipflop10);
                }
                i++;
            } catch (Exception e) {
                if (e.toString().equals("java.lang.NullPointerException")) {
                    System.out.println(getDebugTimeStamp()
                                       + "Error writing from file while sending all points ");
                }
                e.printStackTrace(System.out);
                exit = 1;
            }
        }
        SwingUtilities.invokeLater(new Logger(log, "Finished sending points: "
                                                   + getFormalTimeStamp(), 0));
    }

    public void sendManual(String mtype, String mname, String mpoint, int mquality) {
        int nameLength = settings.isExtendedName() ? 32 : 16;
        if ("Value".equalsIgnoreCase(mtype)) {

            try {
                Thread.sleep(50);
                out.writeShort(VALMET_VALUE);
                out.writeBytes(getTimeStamp());
                out.writeBytes(mname);
                for (int i = 0; i < nameLength - mname.length(); i++) {
                    out.writeByte(0);
                }
                // four byte float value
                float mfloat = Float.parseFloat(mpoint);
                out.writeFloat(mfloat);
                out.writeShort(mquality);
                out.writeShort(0);
                SwingUtilities.invokeLater(new Logger(log, "SENT ON MANUAL: Value Name: " + mname
                                                           + " Point: " + mpoint + " Quality: "
                                                           + mquality, 1));

                // Write message to traffic log file
                try {
                    FileWriter traffic = new FileWriter(trafficFile, true);
                    traffic.write(getDebugTimeStamp() + "SENT ON MANUAL: Value " + " Name: "
                                  + mname + " Point: " + mpoint + " Quality: " + mquality + "\n");
                    traffic.close();
                } catch (Exception e) {
                    System.out.println(getDebugTimeStamp() + "Error with traffic stream");
                    e.printStackTrace(System.out);
                }
            } catch (Exception E) {
                System.out.println(getDebugTimeStamp() + "Error writing value manually");
                E.printStackTrace(System.out);
            }

        } else if ("Status".equalsIgnoreCase(mtype)) {
            try {
                Thread.sleep(50);
                out.writeShort(VALMET_STATUS);
                out.writeBytes(getTimeStamp());
                out.writeBytes(mname);
                for (int i = 0; i < nameLength - mname.length(); i++) {
                    out.writeByte(0);
                }
                int mfloat;
                try {
                    mfloat = Integer.parseInt(mpoint);
                } catch (NumberFormatException e) {
                    mfloat = 1;
                }
                out.writeShort(mfloat);
                out.writeShort(mquality);
                out.writeShort(0);
                out.writeShort(0);
                String state = "";
                if ("1".equals(mpoint)) {
                    state = "Close";
                } else
                    state = "Open";
                SwingUtilities.invokeLater(new Logger(log, "SENT ON MANUAL: Status Name: " + mname
                                                           + " Point: " + state + " Quality: "
                                                           + mquality, 1));

                // Write message to traffic log file
                try {
                    FileWriter traffic = new FileWriter(trafficFile, true);
                    traffic.write(getDebugTimeStamp() + "SENT ON MANUAL: Status " + " Name: "
                                  + mname + " Point: " + state + " Quality: " + mquality + "\n");
                    traffic.close();
                } catch (Exception e) {
                    System.out.println(getDebugTimeStamp() + "Error with traffic stream");
                    e.printStackTrace(System.out);
                }
            } catch (Exception E) {
                System.out.println(getDebugTimeStamp() + "Error writing status manually");
                E.printStackTrace(System.out);
            }

        } else if ("Control".equalsIgnoreCase(mtype)) {

            try {
                Thread.sleep(50);
                out.writeShort(VALMET_CONTROL);
                out.writeBytes(getTimeStamp());
                out.writeBytes(mname);
                for (int i = 0; i < nameLength - mname.length(); i++) {
                    out.writeByte(0);
                }
                int mfloat;
                try {
                    mfloat = Integer.parseInt(mpoint);
                } catch (NumberFormatException e) {
                    mfloat = 1;
                }

                out.writeShort(mfloat);
                out.writeInt(0);
                out.writeShort(0);
                String state = "";
                if ("1".equals(mpoint)) {
                    state = "Close";
                } else
                    state = "Open";
                SwingUtilities.invokeLater(new Logger(log, "SENT ON MANUAL: Control Name: " + mname
                                                           + " Point: " + state, 1));

                // Write message to traffic log file
                try {
                    FileWriter traffic = new FileWriter(trafficFile, true);
                    traffic.write(getDebugTimeStamp() + "SENT ON MANUAL: Control " + " Name: "
                                  + mname + " Point: " + state + "\n");
                    traffic.close();
                } catch (Exception e) {
                    System.out.println(getDebugTimeStamp() + "Error with traffic stream");
                    e.printStackTrace(System.out);
                }
            } catch (Exception E) {
                System.out.println(getDebugTimeStamp() + "Error writing control manually");
                E.printStackTrace(System.out);
            }

        }
    }

    // Method for reading input from socket into a byte array
    public void readBytes() {
        int function = 0;
        float rFloatValue;
        int rStatusValue;
        int quality;
        int index;
        int hbyte = 0, lbyte = 0;
        String pointString = "";
        byte[] pointName = new byte[2048];
        byte[] time = new byte[2048];
        try {
            quit = 0;
            // Read from Socket queue, close connection if excepiton occurs and reconnects
            try {
                hbyte = in.readUnsignedByte();
                lbyte = in.readUnsignedByte();
                function = hbyte * 256 + lbyte;
            } catch (Exception e) {
                System.out.println(e.toString());
                if (readThread == null) {
                    quit = 1;
                } else {
                    SwingUtilities
                        .invokeLater(new Logger(log, "Read Failed from Yukon socket connection.", 3));
                    SwingUtilities
                        .invokeLater(new Logger(log,
                                                "Stopping read thread and closing connection...",
                                                3));
                    notifier.setActionCode(FDTestPanelNotifier.ACTION_CONNECTION_LOST);
                    closeConnection();
                    quit = 1;
                }
            }

            while (quit != 1) {
                heartbeat.cancel();
                retryHeartbeat = 0;
                int nameLength = settings.isExtendedName() ? 32 : 16;
                // process based on message type
                switch (function) {
                case VALMET_NULL:

                    // Receive a heartbeat message
                    if (hbeat == true) {
                        SwingUtilities.invokeLater(new Logger(log, "RECV: heartbeat", 2));
                        SwingUtilities.invokeLater(new Logger(log, "SENT: heartbeat", 1));
                    }
                    in.read(time, 0, 18);
                    in.read(pointName, 0, nameLength + 8);
                    FileWriter traffic = new FileWriter(trafficFile, true);
                    traffic.write(getDebugTimeStamp() + "RECV              " + "Heartbeat messsage"
                                  + "\n");
                    traffic.close();
                    // send heartbeat reply
                    out.writeShort(VALMET_NULL);
                    out.writeBytes(getTimeStamp());

                    for (int i = 0; i < nameLength + 8; i++) {
                        out.writeByte(0);
                    }
                    FileWriter traffic2 = new FileWriter(trafficFile, true);
                    traffic2.write(getDebugTimeStamp() + "SENT              "
                                   + "Heartbeat messsage" + "\n");
                    traffic2.close();
                    quit = 1;
                    break;

                case VALMET_VALUE:

                    // Received an analog point message
                    in.read(time, 0, 18);
                    in.read(pointName, 0, nameLength);
                    index = 0;

                    for (int i = 0; i < nameLength; i++) {
                        if (pointName[i] == 0) {
                            index = i;
                            break;
                        }
                    }
                    pointString = new String(pointName, 0, index);
                    rFloatValue = in.readFloat();
                    quality = in.readShort();
                    in.readShort();
                    SwingUtilities.invokeLater(new Logger(log, "RECV: Value " + "Name: "
                                                               + pointString + " Value: "
                                                               + nf.format(rFloatValue)
                                                               + " Quality: " + quality
                                                               + " TStamp: "
                                                               + new String(time, 0, 18), 2));
                    System.out.println(getDebugTimeStamp() + "VALMET RECV: Value " + rFloatValue);
                    writeToFile("Value", pointString, rFloatValue);
                    // sendAllPoints();
                    quit = 1;
                    break;

                case VALMET_STATUS:

                    // Received a status point message
                    in.read(time, 0, 18);
                    in.read(pointName, 0, nameLength);
                    index = 0;

                    for (int i = 0; i < nameLength; i++) {
                        if (pointName[i] == 0) {
                            index = i;
                            break;
                        }
                    }
                    pointString = new String(pointName, 0, index);
                    rStatusValue = in.readShort();
                    String statusValueString = "";
                    if (rStatusValue == 2) {
                        statusValueString = "Close";
                    } else if (rStatusValue == 1) {
                        statusValueString = "Open";
                    }
                    in.readShort();
                    quality = in.readShort();
                    in.readShort();
                    SwingUtilities.invokeLater(new Logger(log, "RECV: Status " + "Name: "
                                                               + pointString + " State: "
                                                               + statusValueString + " Quality: "
                                                               + quality + " TStamp: "
                                                               + new String(time, 0, 18), 2));
                    System.out.println(getDebugTimeStamp() + "VALMET RECV: Status "
                                       + statusValueString);
                    writeToFile("Status", pointString, (double) rStatusValue);
                    quit = 1;
                    break;

                case VALMET_CONTROL:

                    // Received a control point message
                    in.read(time, 0, 18);
                    in.read(pointName, 0, nameLength);
                    index = 0;

                    for (int i = 0; i < nameLength; i++) {
                        if (pointName[i] == 0) {
                            index = i;
                            break;
                        }
                    }
                    pointString = new String(pointName, 0, index);
                    rStatusValue = in.readShort();
                    String controlValueString = "";
                    if (rStatusValue == 2) {
                        controlValueString = "Close";
                    } else if (rStatusValue == 1) {
                        controlValueString = "Open";
                    }
                    in.read(pointName, 0, 6);
                    SwingUtilities.invokeLater(new Logger(log, "RECV: Control " + "Name: "
                                                               + pointString + " State: "
                                                               + controlValueString + " TStamp: "
                                                               + new String(time, 0, 18), 2));
                    System.out.println(getDebugTimeStamp() + "VALMET RECV: Control "
                                       + controlValueString);
                    writeToFile("Control", pointString, (double) rStatusValue);
                    quit = 1;
                    break;

                default:

                    // Received unknown message
                    SwingUtilities.invokeLater(new Logger(log, "Received unknown function type: "
                                                               + function + " bytes: " + hbyte
                                                               + " " + lbyte, 3));
                    System.out.println(getDebugTimeStamp() + "Received unknown function type:"
                                       + function);
                    quit = 1;
                    break;
                }
                heartbeat = new Timer();
                heartbeat.schedule(new heartbeatTask(), T1_TIME, T1_TIME);
            }
        } catch (Exception e) {
            SwingUtilities.invokeLater(new Logger(log, "Error reading from input stream", 3));
            if (e.toString().equals("java.lang.NullPointerException")) {
                SwingUtilities.invokeLater(new Logger(log, "Input stream was null", 3));
            }
            e.printStackTrace(System.out);
        }

        return;
    }

    // Write recieved message to point list file and traffic log file
    public void writeToFile(String ftype, String fname, double fpoint) {

        boolean newpoint = false;

        // make buffered reader
        BufferedReader fileBuffer = null;
        try {
            fileBuffer = new BufferedReader(new FileReader("resource/valmet_yukon_points.txt"));
        } catch (Exception e) {
            if (e.toString().equalsIgnoreCase("java.io.FileNotFoundException")) {
                System.out.println("File not found when trying to load points from file");
            } else
                e.printStackTrace(System.out);
        }

        String[] pointAsStrings = new String[500];

        // fill points array with point from yukon file

        int i = 0, k = 0, l = 0;

        while (l != 1) {
            try {
                String pointline = fileBuffer.readLine();

                k++;
                if ("EOF".equalsIgnoreCase(pointline)) {
                    l = 1;
                } else {
                    StringTokenizer st = new StringTokenizer(pointline, ";");
                    String pointtype = st.nextToken();
                    String pointname = st.nextToken();
                    String interval = st.nextToken();
                    String lineEntry = pointtype + ";" + pointname + ";" + interval;

                    pointAsStrings[i] = lineEntry;

                    i++;
                }

            } catch (Exception e) {
                System.out.println("Error getting points from file");
                e.printStackTrace(System.out);
                break;
            }
        }

        int out = 0;

        try {
            fileBuffer = new BufferedReader(new FileReader("resource/valmet_yukon_points.txt"));
        } catch (Exception e) {
            if (e.toString().equalsIgnoreCase("java.io.FileNotFoundException")) {
                System.out.println("File not found when trying to load points from file");
            } else
                e.printStackTrace(System.out);
        }

        String currentLine = "";
        // Main writing loop
        while (out != 1) {
            try {
                currentLine = fileBuffer.readLine();
                StringTokenizer ft = new StringTokenizer(currentLine, ";");

                if ("EOF".equals(currentLine)) { // Point does not exist, set newpoint to true
                    try {

                        SwingUtilities.invokeLater(new Logger(log,
                                                              "Unexpected point from Yukon: ",
                                                              4));
                        newpoint = true;
                        out = 1;

                    } catch (Exception e) {

                        System.out.println(getDebugTimeStamp()
                                           + "Error writing new point to end of point file");
                        e.printStackTrace(System.out);
                        out = 1;
                    }
                } else if (ft.nextToken().equalsIgnoreCase(ftype)) {
                    if (ft.nextToken().equalsIgnoreCase(fname)) {

                        newpoint = false;
                        out = 1;
                    }
                }
            } catch (Exception e) {
                if ("java.lang.NullPointerException".equals(e.toString())) {

                    System.out.println(getDebugTimeStamp() + "EOF ERROR while writing to file");
                    SwingUtilities
                        .invokeLater(new Logger(log, "EOF ERROR while writing to file", 3));

                }
                e.printStackTrace(System.out);
                out = 1;
            }
        }

        if (newpoint) {
            try {
                FileWriter yukonFileWriter =
                    new FileWriter(new File("resource/valmet_yukon_points.txt"));

                for (int j = 0; j < pointAsStrings.length; j++) {
                    try {
                        if (pointAsStrings[j] == null) {
                            // we are done writing our previous points out to new file
                            break;
                        } else {

                            yukonFileWriter.write(pointAsStrings[j] + LF);

                        }

                    } catch (Exception e) {
                        if (e.toString().equalsIgnoreCase("java.lang.NullPointerException")) {
                            break;
                        }
                    }

                }
                String formattedPoint = "";
                if ("Value".equalsIgnoreCase(ftype)) {
                    formattedPoint = nf.format(fpoint);
                } else {
                    if (fpoint == 1.0) {
                        formattedPoint = "Close";
                    } else {
                        formattedPoint = "Open  ";
                    }
                }

                yukonFileWriter.write(ftype + ";" + fname + ";" + formattedPoint + LF);
                yukonFileWriter.write("EOF" + LF);
                yukonFileWriter
                    .write("#TO CLEAR THIS FILE, DELETE ALL LINES UP T0 'EOF' LEAVING EOF AT THE TOP"
                           + LF);
                yukonFileWriter
                    .write("#THIS IS LIST OF POINTS RECIEVED FROM YUKON, WHEN POINTS ARE RECIEVED OLD ONES ARE REWRITTEN WITH NEW VALUES");
                yukonFileWriter.write("#NEW ONES ARE APPENED TO THE BOTTOM OF THE LIST");
                yukonFileWriter.flush();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileWriter yukonFileWriter =
                    new FileWriter(new File("resource/valmet_yukon_points.txt"));

                for (int j = 0; i < pointAsStrings.length; j++) {
                    try {

                        StringTokenizer st = new StringTokenizer(pointAsStrings[j], ";");
                        String type = st.nextToken();
                        String name = st.nextToken();

                        if (type.equalsIgnoreCase(ftype) && name.equalsIgnoreCase(fname)) {

                            String formattedPoint = "";
                            if (ftype.equalsIgnoreCase("Value")) {
                                formattedPoint = nf.format(fpoint);
                            } else {
                                if (fpoint == 1.0) {
                                    formattedPoint = "Close";
                                } else {
                                    formattedPoint = "Open  ";
                                }
                            }

                            yukonFileWriter.write(ftype + ";" + fname + ";" + formattedPoint + LF);

                        } else {

                            yukonFileWriter.write(pointAsStrings[j] + LF);
                        }
                    } catch (Exception e) {
                        if (e.toString().equalsIgnoreCase("java.lang.NullPointerException")) {

                            break;
                        }
                    }

                }

                yukonFileWriter.write("EOF" + LF);
                yukonFileWriter
                    .write("#TO CLEAR THIS FILE, DELETE ALL LINES UP T0 'EOF' LEAVING EOF AT THE TOP"
                           + LF);
                yukonFileWriter
                    .write("#THIS IS LIST OF POINTS RECIEVED FROM YUKON, WHEN POINTS ARE RECIEVED OLD ONES ARE REWRITTEN WITH NEW VALUES");
                yukonFileWriter.write("#NEW ONES ARE APPENED TO THE BOTTOM OF THE LIST");
                yukonFileWriter.flush();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Write message to traffic log file
        try {
            String pointword = "";
            if (ftype.equalsIgnoreCase("Value")) {
                pointword = nf.format(fpoint);
            } else {
                if (fpoint == 2.0) {
                    pointword = "Close";
                } else {
                    pointword = "Open";
                }
            }
            FileWriter traffic = new FileWriter(trafficFile, true);
            traffic.write(getDebugTimeStamp() + "RECV              " + ftype + " Name: " + fname
                          + " Point: " + pointword + "\n");
            traffic.close();
        } catch (Exception e) {
            System.out.println(getDebugTimeStamp() + "Error with traffic stream");
            e.printStackTrace(System.out);
        }
    }

    public void hbOn(boolean on) {
        hbeat = on;
    }

    // returns protocol name
    public String getName() {
        return new String("VALMET");
    }

    // Creates a new timestamp
    public String getTimeStamp() {
        if (tz.inDaylightTime(gc.getTime())) {
            return df.format(new Date()) + "dCST";
        } else {
            String date = df.format(new Date()) + "sCST";
            return date;
        }

    }

    public String getFormalTimeStamp() {
        return fdf.format(new Date());
    }

    public String getDebugTimeStamp() {
        return dbdf.format(new Date()) + " ";
    }

    public void closeConnection() {
        try {
            heartbeat.cancel();
            interval.cancel();
            timesync.cancel();
            stop();
            Thread.sleep(10);
            out.close();
            in.close();
            fdeSocket.shutdownInput();
            fdeSocket.shutdownOutput();
            fdeSocket.close();
        } catch (Exception e) {
            System.out.println(getDebugTimeStamp() + "Error closing connection");
            if (e.toString().equalsIgnoreCase("java.lang.NullPointerException")) {
                System.out.println("canceled heartbeat timer that didn't exist yet");
            } else
                e.printStackTrace(System.out);
        }
    }

    public void listenForActions(Observer o) {
        notifier.addObserver(o);
    }

    public void editSettings() {
        settings = new ValmetSettings("Settings", this);
        settings.listenForActions(testPanel.getWindow());
        // Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = settings.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        settings.setLocation((screenSize.width - frameSize.width) / 2,
                             (screenSize.height - frameSize.height) / 2);
        settings.setVisible(true);
    }

    public void createControlPoint(DataOutputStream out, ValmetPoint nextpoint, Float value,
                                   int flipFlopValue) {
        String valueString = "";

        if (nextpoint.getPointType().equalsIgnoreCase("Analog Output")) {
            valueString = value.toString();
        } else if (flipFlopValue == 1) {
            valueString = "Open";
        } else
            valueString = "Close";
        try {
            out.writeShort(VALMET_CONTROL);
            out.writeBytes(getTimeStamp());
            writeName(out, nextpoint);

            if (nextpoint.getPointType().equalsIgnoreCase("Analog Output")) {
                out.writeFloat(value);
                out.writeShort(0);
            } else {
                out.writeShort(flipFlopValue);
                out.writeInt(0);
            }
            out.writeShort(0);
            SwingUtilities.invokeLater(new Logger(log, "SENT ON INTERVAL: Control Name: "
                                                       + nextpoint.getPointName() + " Point: "
                                                       + valueString, 1));
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.out.println(getDebugTimeStamp() + "Error writing in 10 second interval");
        }
        // Write message to traffic log file
        try {
            FileWriter traffic = new FileWriter(trafficFile, true);
            traffic.write(getDebugTimeStamp() + "SENT ON INTERVAL: " + nextpoint.getPointInterval()
                          + " ");
            traffic.write(" Name: " + nextpoint.getPointName() + " Point: " + value + "\n");
            traffic.close();
        } catch (Exception e) {
            System.out.println(getDebugTimeStamp() + "Error with traffic stream ");
            e.printStackTrace(System.out);
        }
    }

    public void createValuePoint(DataOutputStream out, ValmetPoint nextpoint, Float value) {
        try {

            out.writeShort(VALMET_VALUE);
            out.writeBytes(getTimeStamp());
            writeName(out, nextpoint);

            if ("RANDOM".equalsIgnoreCase(nextpoint.getPointFunction())) {
                // We want to normalize the scale to 0, then add the point minimum back at the end.
                Double randomMax = new Double(nextpoint.getPointMax() - nextpoint.getPointMin());
                int first = gen.nextInt(randomMax.intValue());
                float last = gen.nextFloat();
                value = first + last + (float) nextpoint.getPointMin();
                out.writeFloat(value);
                out.writeShort(0);
                out.writeShort(0);

            } else if ("PYRAMID".equalsIgnoreCase(nextpoint.getPointFunction())) {
                double currentvalue = nextpoint.getPointCurrentValue();
                double incriment = nextpoint.getPointDelta();
                System.out.println("cv: " + currentvalue + " inc: " + incriment);
                if ((currentvalue + incriment) >= nextpoint.getPointMax()) {

                    Double cvdouble = new Double(currentvalue);
                    value = cvdouble.floatValue();
                    out.writeFloat(value);
                    out.writeShort(0);
                    out.writeShort(0);

                    currentvalue = currentvalue + incriment;

                    nextpoint.setPointCurrentValue(currentvalue);
                    nextpoint.setPointDelta(nextpoint.getPointDelta()
                                            - (2 * nextpoint.getPointDelta()));
                } else if ((currentvalue + incriment) <= nextpoint.getPointMin()) {

                    Double cvdouble = new Double(currentvalue);
                    value = cvdouble.floatValue();
                    out.writeFloat(value);
                    out.writeShort(0);
                    out.writeShort(0);
                    currentvalue = currentvalue + incriment;

                    nextpoint.setPointCurrentValue(currentvalue);
                    nextpoint.setPointDelta(nextpoint.getPointDelta()
                                            + (-2 * nextpoint.getPointDelta()));
                } else {
                    Double cvdouble = new Double(currentvalue);
                    value = cvdouble.floatValue();
                    out.writeFloat(value);
                    out.writeShort(0);
                    out.writeShort(0);
                    currentvalue = currentvalue + incriment;
                    nextpoint.setPointCurrentValue(currentvalue);
                }
            } else if ("DROPOFF".equalsIgnoreCase(nextpoint.getPointFunction())) {

                double currentvalue = nextpoint.getPointCurrentValue();
                double incriment = nextpoint.getPointDelta();
                if (nextpoint.getPointMaxStart()) {

                    Double cvdouble = new Double(currentvalue);
                    value = cvdouble.floatValue();
                    out.writeFloat(value);
                    out.writeShort(0);
                    out.writeShort(0);

                    currentvalue = currentvalue - incriment;

                    if (currentvalue < nextpoint.getPointMin()) {
                        nextpoint.setPointCurrentValue(nextpoint.getPointMax());
                    } else
                        nextpoint.setPointCurrentValue(currentvalue);
                } else {

                    Double cvdouble = new Double(currentvalue);
                    value = cvdouble.floatValue();
                    out.writeFloat(value);
                    out.writeShort(0);
                    out.writeShort(0);
                    currentvalue = currentvalue + nextpoint.getPointDelta();

                    if (currentvalue > nextpoint.getPointMax()) {
                        nextpoint.setPointCurrentValue(nextpoint.getPointMin());
                    } else
                        nextpoint.setPointCurrentValue(currentvalue);
                }
            }
            SwingUtilities.invokeLater(new Logger(log, "SENT ON INTERVAL: " + "Value " + "Name: "
                                                       + nextpoint.getPointName() + " Point: "
                                                       + value, 1));

            // Write message to traffic log file
            try {
                FileWriter traffic = new FileWriter(trafficFile, true);
                traffic.write(getDebugTimeStamp() + "SENT ON INTERVAL: " + nextpoint.getPointType()
                              + "   ");
                traffic.write(" Name: " + nextpoint.getPointName() + " Point: " + value + "\n");
                traffic.close();
            } catch (Exception e) {
                System.out.println(getDebugTimeStamp() + "Error with traffic stream");
                e.printStackTrace(System.out);
            }
        } catch (Exception e) {
            System.out.println(getDebugTimeStamp() + "Error with traffic stream");
            e.printStackTrace(System.out);
        }
    }

    public void createStatusPoint(DataOutputStream out, ValmetPoint nextpoint, Float value,
                                  int flipFlopValue) {

        String valueString = "";
        if (flipFlopValue == 1) {
            valueString = "Open";
        } else
            valueString = "Close";
        try {
            out.writeShort(VALMET_STATUS);
            out.writeBytes(getTimeStamp());
            writeName(out, nextpoint);
            out.writeShort(flipFlopValue);
            out.writeShort(0);
            out.writeShort(0);
            out.writeShort(0);
            SwingUtilities.invokeLater(new Logger(log, "SENT ON INTERVAL: " + "Status " + "Name: "
                                                       + nextpoint.getPointName() + " Point: "
                                                       + valueString, 1));
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.out.println(getDebugTimeStamp() + "Error writing in 10 second interval" + e);
        }
        // Write message to traffic log file
        try {
            FileWriter traffic = new FileWriter(trafficFile, true);
            traffic.write(getDebugTimeStamp() + "SENT ON INTERVAL: " + nextpoint.getPointInterval()
                          + "  ");
            traffic.write(" Name: " + nextpoint.getPointName() + " Point: " + value + "\n");
            traffic.close();
        } catch (Exception e) {
            System.out.println(getDebugTimeStamp() + "Error with traffic stream ");
        }
    }

    public void writeName(DataOutputStream out, ValmetPoint nextpoint) {
        try {
            int nameLength = settings.isExtendedName() ? 32 : 16;

            if (nextpoint.getPointName().length() > nameLength) {
                out.writeBytes(nextpoint.getPointName().substring(0, nameLength - 1));
            } else {
                out.writeBytes(nextpoint.getPointName());

                for (int j = 0; j < (nameLength - nextpoint.getPointName().length()); j++) {
                    out.writeByte(0);
                }
            }
        } catch (Exception e) {
            System.out.println(getDebugTimeStamp() + "Error writing PointName to traffic stream ");
        }

    }

}