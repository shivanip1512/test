/**
 * 
 */
package com.cannontech.datagenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.message.dispatch.message.PointData;

/**
 * @author nmeverden
 *
 */
public class CSV2PointData {
    private static Logger log = YukonLogManager.getLogger(CSV2PointData.class);
    private AsyncDynamicDataSource dataSource;
    private Timer timer;
    private int multiplier;
    private File file;
    
    public CSV2PointData() {
        
    }
    
    public CSV2PointData(AsyncDynamicDataSource dataSource, Timer timer) {
        this.dataSource = dataSource;
        this.timer = timer;
    }

    public void setDataSource(AsyncDynamicDataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public void setTimer(Timer timer) {
        this.timer = timer;
    }
    
    public void setMultiplier(int multiplier) {
        Validate.isTrue(multiplier > 0, "multiplier must be > 0: ", multiplier);
        this.multiplier = multiplier;
    }
    
    public void setFile(File file) {
        Validate.isTrue(file.exists() && file.isFile(), "csv file must exist: ", file);
        this.file = file;
    }

    private List<PointData> parseFile(File file) throws IOException {
        List<PointData> list = new ArrayList<PointData>();

        BufferedReader in = 
            new BufferedReader(new FileReader(file));
            
        String line;
        while ((line = in.readLine()) != null) {
            if (line.startsWith("#") || line.matches("^\\s*$")) continue;
                
            String[] split = line.split(",");
                
            if (split.length != 4) {
                log.error("invalid line: " + line);
                continue;
            }
            
            try {
                PointData pData = new PointData();
                pData.setId(Integer.parseInt(split[0].trim()));
                pData.setTime(Timestamp.valueOf(split[1].trim()));
                pData.setPointQuality(PointQuality.getPointQuality(Integer.parseInt(split[2].trim())));
                pData.setValue(Double.parseDouble(split[3].trim()));
                pData.setType(1);
                list.add(pData);
            } catch (Exception e) {
                log.error("invalid line: " + line, e);
                continue;
            }
        }
        
        return list;
    }

    public void writeOut() throws IOException {
        List<PointData> list = parseFile(file);
        
        long time = 0;
        long lastLoggedDate = 0;
        
        for (int x = 0; x < list.size(); x++) {
            final PointData pData = list.get(x);
            long loggedDate = pData.getPointDataTimeStamp().getTime();
            long diff = loggedDate - lastLoggedDate;
            lastLoggedDate = loggedDate;
            
            time += (x == 0) ? System.currentTimeMillis() : (diff / multiplier);

            final Date date = new Date(time);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    log.info("sending PointData id number: " + pData.getId());
                    pData.setTime(date);
                    dataSource.putValue(pData);
                }
            }, date);
            
        }
        
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.exit(0);
            }
        }, new Date(time + 5000));
    }
    
}
