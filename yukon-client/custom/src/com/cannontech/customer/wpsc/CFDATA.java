package com.cannontech.customer.wpsc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.FileInterface;
import com.cannontech.common.util.LogWriter;
import com.cannontech.message.porter.message.Request;
import com.cannontech.yukon.IServerConnection;
import com.cannontech.yukon.conns.ConnPool;

/**
 * Custom file interface that reads lines from a file (probably a .rcv extension) 
 * Input is of the format: Address,Division,District,R1,R2,R3,???
 * File is generated by some third party system, read-in by Yukon, translated into control strings and written to porter.
 * Basically, this is performing individual switch commands as submitted by some third party.
 */
public class CFDATA extends FileInterface {

    public CFDATA(String dirToWatch, String fileExt) {
        super(dirToWatch, fileExt);
    }

    /**
     * Line format as documented by WPS: Code,Switch,Utility
     *   Address,Division,District,R1,R2,R3,???
     * Example:
     *   04,502938,222,1,010,3,0,0,3
     * Code (aka Functions) as documented by WPS: 
     *   1 - program relays for the first time 
     *   2/3 - re-program relays on an existing switch 
     *   4 -turn switch on 
     *   5 - turn switch off
     * @return String - command string
     * @param line String - line from file
     */
    private String decodeLine(String line) {

        StringBuffer buf = new StringBuffer();

        try {

            StringTokenizer tok = new StringTokenizer(line, " ");

            // DLC Func
            int func = Integer.parseInt(tok.nextToken());

            String serialNumberStr = tok.nextToken();
            if (serialNumberStr.startsWith("4")) { // "4000 "series switches at WPS are all expressCom
                buf.append("putconfig xcom serial ");
            } else { // "5000" series switches, or assume everything else, is versacom
                buf.append("putconfig versacom serial ");
            }
            // SerialNumber
            buf.append(Integer.parseInt(serialNumberStr));

            if (func == 4) { // turn switch on
                buf.append(" service in");
                return buf.toString();
            } else if (func == 5) { // turn switch off
                buf.append(" service out");
                return buf.toString();
            }

            // Utility ID
            int utilityId = Integer.parseInt(tok.nextToken());

            if (func != 2) {
                buf.append(" utility ");
                buf.append(utilityId);
            }

            // Division Code
            int division = Integer.parseInt(tok.nextToken());
            buf.append(" aux ");
            buf.append(division);

            // Operating District - section
            int district = Integer.parseInt(tok.nextToken());
            buf.append(" section ");
            buf.append(district);

            int classID = 0;
            // Class #1
            int relay = Integer.parseInt(tok.nextToken());
            if (relay > 0)
                classID |= (1 << (relay - 1));

            // Class #2
            relay = Integer.parseInt(tok.nextToken());
            if (relay > 0)
                classID |= (1 << (relay - 1));

            // Class #3
            relay = Integer.parseInt(tok.nextToken());
            if (relay > 0)
                classID |= (1 << (relay - 1));

            if (classID > 65535) // Can only allow 16bits here
                return null;

            int temp = 0;
            for (int i = 0; i < 16; i++)
                temp |= (((classID >> i) & 0x0001) << (15 - i));

            classID = temp;

            buf.append(" class 0x" + Long.toHexString(classID));

            int divID = 0;
            // DLC Division
            int dlcDivision = Integer.parseInt(tok.nextToken());
            if (dlcDivision > 0)
                divID |= (1 << (dlcDivision - 1));

            if (divID > 65535)
                return null;

            temp = 0;
            for (int i = 0; i < 16; i++)
                temp |= (((divID >> i) & 0x0001) << (15 - i));

            divID = temp;

            buf.append(" division 0x" + Long.toHexString(divID));
        } catch (Exception e) {
            return null;
        }

        return buf.toString();

    }

    @Override
    protected void handleFile(InputStream in) {
        try {
            IServerConnection porterConn = ConnPool.getInstance().getDefPorterConn();
            int countSent = 0;
            BufferedReader rdr = new BufferedReader(new InputStreamReader(in));

            String str;
            CTILogger.info("Begin Processing CFDATA file...");
            WPSCMain.logMessage("Begin Processing CFDATA Files", LogWriter.INFO);
            while ((str = rdr.readLine()) != null) {
                String decoded = decodeLine(str);

                if (decoded != null) {
                    CTILogger.info("CFDATA: " + decoded);
                    WPSCMain.logMessage(" ** CFDATA:  " + decoded, LogWriter.DEBUG);
                    Request req = new Request(0, decoded, 1L);
                    porterConn.write(req);
                    countSent++;
                    Thread.sleep(500);
                }
            }
            CTILogger.info("...Done processing CFDATA file");
            WPSCMain.logMessage("Done Processing CFDATA Files  *  " + countSent + "  *  read and wrote to Porter", LogWriter.INFO);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        }
    }
}
