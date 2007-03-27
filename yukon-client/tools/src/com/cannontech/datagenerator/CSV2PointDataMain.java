/**
 * 
 */
package com.cannontech.datagenerator;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.cannontech.message.dispatch.message.PointData;
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
        
        if (args.length != 1) {
            System.out.println("args[0] Specifiy path to csv file");
            System.out.println("args[1] Specifiy time multiplier to speed time by X (OPTIONAL)");
            System.exit(0);
        }
        
        CSV2PointData cpd = YukonSpringHook.getBean("csv2PointData", CSV2PointData.class);
        
        try {
            if (args[1] != null) {
                int multi = Integer.parseInt(args[1]);
                cpd.setMultiplier(multi);
            }
            cpd.parseFile(new File(args[0]));
        } catch (IOException e) {
            System.out.println("Unable to parse csv file " + args[0]);
        } catch (NumberFormatException ex) {
            System.out.println("args[1] is not a valid number");
        }
    }

}
