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

/**
 * @author nmeverden
 *
 */
public class CSV2PointData {
    private DynamicDataSource dataSource;
    private Timer timer;
    
    public CSV2PointData() {
        
    }
    
    public CSV2PointData(DynamicDataSource dataSource, Timer timer) {
        this.dataSource = dataSource;
        this.timer = timer;
    }

    public void setDataSource(DynamicDataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public void setTimer(Timer timer) {
        this.timer = timer;
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

    public void writeOut(List<PointData> list, int multi) {
        long time = 0;
        long lastLoggedDate = 0;
        
        for (final PointData pData : list) {
            long loggedDate = pData.getPointDataTimeStamp().getTime();
            long diff = loggedDate - lastLoggedDate;
            lastLoggedDate = loggedDate;
            
            if (list.indexOf(pData) == 0) {
                time = System.currentTimeMillis();
            } else {
                time += (multi !=  0) ? (diff / multi) : diff;
            }

            final long scheduledTime = time;
            timer.schedule(new TimerTask() {
                public void run() {
                    System.out.println("sending PointData id number: " + pData.getId()); //DEBUG INFO
                    pData.setTime(new Timestamp(scheduledTime));
                    dataSource.putValue(pData);
                }
            }, 5);
        }
    }
    
}
