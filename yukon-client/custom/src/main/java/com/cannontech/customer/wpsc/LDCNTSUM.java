package com.cannontech.customer.wpsc;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.LogWriter;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.message.dispatch.DispatchClientConnection;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.message.util.Message;

/**
 * Custom utility that - registers for events or alarms - if message is a
 * "signal", then we decode it more - tranlates the signal into some custom
 * string (undocumented unfortunately) - writes this string to a file
 * (presumably for third party consumption). - also logs a bunch of counts and
 * stuff.
 */
public class LDCNTSUM implements Runnable {
    private DispatchClientConnection dispatchConn = null;

    private long writeDelay = 30000;
    private String fileName = "LDCNTSUM.SND";

    private int SIGNAL_SHED_COUNTER = 0;
    private int SIGNAL_RESTORE_COUNTER = 0;
    private int SIGNAL_TERMINATE_COUNTER = 0;
    private int SIGNAL_CYCLE_COUNTER = 0;

    public LDCNTSUM(DispatchClientConnection newDispatchConn, String outputFile) {
        this.dispatchConn = newDispatchConn;
        this.fileName = outputFile;
    }

    /**
     * Decode message finds all the signals in msg and puts them in a list
     */
    private void decodeMessge(Message message, List<Signal> signalList) {

        if (message instanceof Multi) {
            Vector<Message> v = ((Multi)message).getVector();
            for (Message m : v) {
                if (m instanceof Signal) {
                    signalList.add((Signal)m);
                }
            }
        } else if (message instanceof Signal) {
            signalList.add((Signal)message);
        }

        return;
    }

    /**
     * Decode Signal message and translate into customized string. (exact format undocumented) 
     */
    private String decodeSignal(Signal signal) {

        String retVal = null;

        try {
            // First determine whether we need to do any work
            if (signal.getPointID() != PointTypes.SYS_PID_LOADMANAGEMENT) {
                return null;
            }

            // make sure the action is one we care about
            if (signal.getAction().indexOf("SHED") == -1 && 
                signal.getAction().indexOf("RESTORE") == -1 && 
                signal.getAction().indexOf("TERMINATE") == -1 && 
                signal.getAction().indexOf("CYCLE") == -1) {
                return null;
            }

            DecimalFormat decimalFormat = new DecimalFormat();
            decimalFormat.setMaximumFractionDigits(0);

            // Parse the action to see if it is one we want
            // Will be a group or a serial #
            String device;

            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yy HH:mm:ss");
            String timeStamp = dateFormat.format(signal.getTimeStamp());

            // The action string for each relay
            String relay[] = new String[3];
            relay[0] = "...      ";
            relay[1] = "...      ";
            relay[2] = "...      ";

            // Start parsing this thing
            String rawGroup = signal.getDescription().trim();

            int index;
            if ((index = rawGroup.lastIndexOf("Group:")) != -1) {
                device = getGroupAddress(rawGroup.substring(index + "Group:".length(), rawGroup.indexOf("Relay:")).trim());

            } else if ((index = rawGroup.lastIndexOf("Serial:")) != -1) {
                String serial = rawGroup.substring(index + "Serial:".length(), rawGroup.indexOf("Relay:")).trim();
                
                // Strip off the leading zeros from the serial string
                // 8/7/00 WPSC IS dept sais they can't handle leading zeroes
                device = "SERIAL #" + Integer.parseInt(serial) + " ";

                while (device.length() < 26) {
                    device = device.concat(" ");
                }
            } else {
                return null;
            }

            // Determine the relays, relayStr is in the form "r1 r2 r3"
            String relayStr = rawGroup.substring(rawGroup.indexOf("Relay:") + "Relay:".length())
                                      .trim();
            relayStr = relayStr.replace('r', ' ');
            java.util.StringTokenizer tok = new java.util.StringTokenizer(relayStr);

            while (tok.hasMoreTokens()) {
                String temp = tok.nextToken();
                int r = Integer.parseInt(temp);

                if ((r >= 1) && (r <= 3)) {
                    String action = signal.getAction().trim().toUpperCase();

                    if (action.startsWith("SHED")) {
                        SIGNAL_SHED_COUNTER++;
                        String time = action.substring(action.indexOf("SHED") + "SHED".length()).trim().toUpperCase();
                        relay[r - 1] = "CTRL=" + time;
                    } else if (action.equals("RESTORE SHED")) {
                        SIGNAL_RESTORE_COUNTER++;
                        relay[r - 1] = "RESTORE";
                    } else if (action.equals("TERMINATE CYCLE")) {
                        SIGNAL_TERMINATE_COUNTER++;
                        relay[r - 1] = "CYC=TERM";
                    } else if (action.startsWith("CYCLE")) {
                        SIGNAL_CYCLE_COUNTER++;
                        String percent = action.substring(action.indexOf("CYCLE") + "CYCLE".length()).trim().toUpperCase();
                        relay[r - 1] = "CYC=" + percent;
                    }

                    while (relay[r - 1].length() < 9) {
                        relay[r - 1] = relay[r - 1].concat(" ");
                    }
                }
            }
            // build up the final string
            retVal = timeStamp + " LC " + relay[0] + relay[1] + relay[2] + device + "SEQ 1 ";
        } catch (Exception e) {
            WPSCMain.logMessage("decodeSignal() - encountered badly formed signal", LogWriter.ERROR);
            CTILogger.info("decodeSignal() - encountered badly formed signal");
        }

        return retVal;
    }

    /**
     * Returns a customized group addressing string for paoName.
     */
    private String getGroupAddress(String name) {
        String sql = "SELECT UtilityAddress,ClassAddress,DivisionAddress FROM " + 
                " LMGroupVersacom,YukonPaobject WHERE LMGroupVersacom.DeviceID = " + 
                " YukonPaobject.PAObjectID AND YukonPaobject.PAOName = ?";

        String retVal = null;

        java.sql.Connection conn = null;
        java.sql.PreparedStatement stmt = null;
        java.sql.ResultSet rset = null;

        try {
            conn = PoolManager.getInstance().getConnection("yukon");
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            rset = stmt.executeQuery();

            if (rset.next()) {
                int u = rset.getInt(1);
                int c = rset.getInt(2);
                int d = rset.getInt(3);

                int temp = 0;
                for (int i = 0; i < 16; i++) {
                    temp |= (((c >> i) & 0x0001) << (15 - i));
                }

                c = temp;
                temp = 0;

                for (int i = 0; i < 16; i++) {
                    temp |= (((d >> i) & 0x0001) << (15 - i));
                }

                d = temp;

                String uStr = "U=" + u;
                while (uStr.length() < 12) {
                    uStr = uStr + " ";
                }

                String cStr = Long.toHexString(c).toUpperCase();
                while (cStr.length() < 4) {
                    cStr = "0" + cStr;
                }

                cStr = "C=" + cStr;
                while (cStr.length() < 7) {
                    cStr = cStr + " ";
                }

                String dStr = Long.toHexString(d).toUpperCase();
                while (dStr.length() < 4) {
                    dStr = "0" + dStr;
                }

                dStr = "D=" + dStr;
                while (dStr.length() < 7) {
                    dStr = dStr + " ";
                }

                retVal = uStr + cStr + dStr;
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        } finally {
            SqlUtils.close(rset, stmt, conn);
        }

        return retVal;

    }

    private void resetCounters() {
        SIGNAL_SHED_COUNTER = 0;
        SIGNAL_RESTORE_COUNTER = 0;
        SIGNAL_TERMINATE_COUNTER = 0;
        SIGNAL_CYCLE_COUNTER = 0;
    }

    @Override
    public void run() {
        try {
            while (WPSCMain.isService) {
                Object in;

                if ((in = dispatchConn.read(0L)) != null && in instanceof Message) {
                    Message message = (Message)in;
                    
                    long writeAt = System.currentTimeMillis() + getWriteDelay();

                    LinkedList<Signal> writeList = new LinkedList<>();
                    decodeMessge(message, writeList);

                    if (writeList.isEmpty()) {
                        continue;
                    }

                    while (writeAt > System.currentTimeMillis()) {
                        if ((in = dispatchConn.read(0L)) != null) {
                            decodeMessge(message, writeList);
                        } else {
                            Thread.sleep(200);
                        }
                    }

                    java.io.PrintWriter writer = null;
                    try {
                        writer = new PrintWriter(new BufferedOutputStream(new FileOutputStream(getFileName(), true)));
                        Signal sig;
                        Iterator<Signal> iter = writeList.iterator();

                        while (iter.hasNext()) {
                            sig = iter.next();

                            if (WPSCMain.DEBUG) {
                                CTILogger.info("\n[" + new java.util.Date() + "]   Signal received:\n" + sig);
                                // Because of the logger not understanding an
                                // end of line character,
                                // Each element is instead specified this way.
                                // Hack..whatever...
                                WPSCMain.logMessage("   Signal received:", LogWriter.DEBUG);
                                WPSCMain.logMessage("                Id: " + sig.getPointID(), LogWriter.DEBUG);
                                WPSCMain.logMessage("          Log Type: " + sig.getLogType(), LogWriter.DEBUG);
                                WPSCMain.logMessage("  Logging Priority: " + sig.getCategoryID(), LogWriter.DEBUG);
                                WPSCMain.logMessage("       Description: " + sig.getDescription(), LogWriter.DEBUG);
                                WPSCMain.logMessage("            Action: " + sig.getAction(), LogWriter.DEBUG);
                                WPSCMain.logMessage("              Tags: " + sig.getTags(), LogWriter.DEBUG);
                                WPSCMain.logMessage("       Description: " + sig.getDescription(), LogWriter.DEBUG);
                                WPSCMain.logMessage("", LogWriter.NONE);
                            }

                            String toWrite = decodeSignal(sig);
                            if (toWrite != null) {
                                CTILogger.info("LDCNTSUM:  " + toWrite);
                                WPSCMain.logMessage("LDCNTSUM: " + toWrite, LogWriter.DEBUG);
                                writer.write(toWrite + "\r\n");
                            } else {
                                CTILogger.info("Ignoring signal");
                                WPSCMain.logMessage("Ignoring signal, decodeSignal(sig) returned null.: ", LogWriter.DEBUG);
                            }
                        }

                        WPSCMain.logMessage("LDCNTSUM ** " + SIGNAL_SHED_COUNTER + " SHEDs ** signals.", LogWriter.INFO);
                        WPSCMain.logMessage("LDCNTSUM ** " + SIGNAL_RESTORE_COUNTER + " RESTORE SHEDs ** signals.", LogWriter.INFO);
                        WPSCMain.logMessage("LDCNTSUM ** " + SIGNAL_TERMINATE_COUNTER + " TERMINATE CYCLEs ** signals.", LogWriter.INFO);
                        WPSCMain.logMessage("LDCNTSUM ** " + SIGNAL_CYCLE_COUNTER + " CYCLEs ** signals. ", LogWriter.INFO);
                        resetCounters();

                        writer.flush();
                        writeList.clear();
                    } catch (FileNotFoundException fne) {
                        fne.printStackTrace();
                    } finally {
                        if (writer != null) {
                            writer.close();
                        }
                    }
                }

                Thread.sleep(1000L);
            }
        } catch (InterruptedException ie) {
            CTILogger.info(getClass() + " - interrupted");
            WPSCMain.logMessage("InterruptedException LDCNTSUM.run()." + getClass() + " - interrupted.", LogWriter.ERROR);
        }
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String newFileName) {
        fileName = newFileName;
    }

    public long getWriteDelay() {
        return writeDelay;
    }

    public void setWriteDelay(long newWriteDelay) {
        writeDelay = newWriteDelay;
    }
}
