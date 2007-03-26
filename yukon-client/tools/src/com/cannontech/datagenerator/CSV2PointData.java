/**
 * 
 */
package com.cannontech.datagenerator;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import java.sql.Timestamp;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.spring.YukonSpringHook;

/**
 * @author nmeverden
 *
 */
public class CSV2PointData {
    private DynamicDataSource dataSource;
    
    public CSV2PointData() {
        
    }
    
    public CSV2PointData(DynamicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setDataSource(DynamicDataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("args[0] Specifiy path to csv file");
            System.out.println("args[1] Specifiy time deplay, options are minutes | seconds default is minutes");
            System.exit(0);
        }
        
        DynamicDataSource dds = YukonSpringHook.getBean("dynamicDataSource", DynamicDataSource.class);
        CSV2PointData cpd = new CSV2PointData(dds);
        
        try {
            boolean inSeconds = false;
            if ("seconds".equalsIgnoreCase(args[1])) inSeconds = true;
            
            List<PointData> list = cpd.parseFile(new File(args[0]));
            cpd.writeOut(list, inSeconds);
        } catch (IOException e) {
            System.out.println("Unable to parse csv file " + args[0]);
        }
    }

    public List<PointData> parseFile(File file) throws IOException {
        List<PointData> list = new ArrayList<PointData>();

        BufferedReader in = 
            new BufferedReader(new FileReader(file));
            
        String line;
        while ((line = in.readLine()) != null) {
            if (line.startsWith("#")) continue;
                
            String[] split = line.split(",");
                
            if (split.length != 4) {
                System.out.println("invalid line: " + line);
                continue;
            }
            
            try {
                PointData pData = new PointData();
                pData.setId(Integer.parseInt(split[0]));
                pData.setTime(Timestamp.valueOf(split[1]));
                pData.setQuality(Long.parseLong(split[2]));
                pData.setValue(Double.parseDouble(split[3]));
                list.add(pData);
            } catch (Exception e) {
                System.out.println("invalid line: " + line);
                continue;
            }
        }
        
        return list;
    }

    public void writeOut(List<PointData> list, boolean inSeconds) {
        long lastTime = 0;
        long delay = 0;
        
        for (final PointData pData : list) {
            Date date = pData.getPointDataTimeStamp();
            long time = date.getTime();

            if (lastTime == 0) {
                lastTime = time;
            } else {
                delay += (inSeconds) ? ((time - lastTime)/100) : (time - lastTime);
                lastTime = time;
            }
            
            System.out.println("sending next PointData in " + delay + " milliseconds"); //DEBUG INFO

            new Timer().schedule(new TimerTask() {
                public void run() {
                    dataSource.putValue(pData);
                }
            }, delay);
        }
    }
    
}
