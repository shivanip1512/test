/**
 * 
 */
package com.cannontech.datagenerator;

import java.util.List;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;

import java.sql.Timestamp;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.message.dispatch.message.PointData;

/**
 * @author nmeverden
 *
 */
public class CSV2PointData {
    private static Logger log = YukonLogManager.getLogger(CSV2PointData.class);
    private DynamicDataSource dataSource;
    private Timer timer;
    private int multiplier;
    
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
    
    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    public void parseFile(File file) throws IOException {
        List<PointData> list = new ArrayList<PointData>();

        BufferedReader in = 
            new BufferedReader(new FileReader(file));
            
        String line;
        while ((line = in.readLine()) != null) {
            if (line.startsWith("#")) continue;
                
            String[] split = line.split(",");
                
            if (split.length != 4) {
                log.error("invalid line: " + line);
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
                log.error("invalid line: " + line);
                continue;
            }
        }
        
        writeOut(list);
    }

    private void writeOut(List<PointData> list) {
        long time = 0;
        long lastLoggedDate = 0;
        long delay = 0;
        
        for (int x = 0; x < list.size(); x++, delay += 2) {
            final PointData pData = list.get(x);
            long loggedDate = pData.getPointDataTimeStamp().getTime();
            long diff = loggedDate - lastLoggedDate;
            lastLoggedDate = loggedDate;
            
            time = (x == 0) ? System.currentTimeMillis() : (multiplier != 0) ? (diff / multiplier) : diff;

            final long scheduledTime = time;
            timer.schedule(new TimerTask() {
                public void run() {
                    log.info("sending PointData id number: " + pData.getId());
                    pData.setTime(new Date(scheduledTime));
                    dataSource.putValue(pData);
                }
            }, delay);
            
        }
    }
    
}
