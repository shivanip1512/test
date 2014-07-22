/**
 * 
 */
package com.cannontech.datagenerator;

import java.io.File;
import java.io.IOException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.spring.YukonSpringHook;

/**
 * @author nmeverden
 *
 */
public class CSV2PointDataMain {

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        if ((args.length == 0) || (args.length > 2)) {
            System.out.println("\n args[0] Specify path to csv file");
            System.out.println("\n args[1] Specify time multiplier to speed time by X (OPTIONAL)");
            System.exit(0);
        }
        String appName = "CSV2PointDataTool";
        System.setProperty("cti.app.name", appName);
        CTILogger.info(appName + " starting...");
        YukonSpringHook.setDefaultContext("com.cannontech.context.tools");
        CSV2PointData cpd = YukonSpringHook.getBean("csv2PointData", CSV2PointData.class);
        
        try {
            if (args.length == 2) {
                int multi = Integer.parseInt(args[1]);
                cpd.setMultiplier(multi);
            }
            cpd.setFile(new File(args[0]));
            cpd.writeOut();
        } catch (IOException e) {
            System.out.println("Unable to parse csv file " + args[0]);
        } catch (NumberFormatException ex) {
            System.out.println("args[1] is not a valid number");
        }
    }

}
