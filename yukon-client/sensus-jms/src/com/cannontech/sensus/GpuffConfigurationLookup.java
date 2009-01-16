package com.cannontech.sensus;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.tools.csv.CSVReader;

public class GpuffConfigurationLookup implements ConfigLookup, InitializingBean {
    private Logger log = YukonLogManager.getLogger(GpuffConfigurationLookup.class);
    private Map<Integer, GpuffConfig> configMapping = null;
    private Resource csvFileResource;
    private long resetInterval = 3000000;
    protected boolean resetCSV;
    
    private void processInputCSV() {
        InputStream inputStream = null;
        Reader configReader = null;
        try {
            String row[] = null;
            inputStream = csvFileResource.getInputStream();
            configReader = new InputStreamReader(inputStream,"UTF-8");
            CSVReader csvReader = null;
            try {
                csvReader = new CSVReader(configReader);
            }catch(Exception e){
                System.out.print(e);
                log.info(e);
            }

            while (csvReader != null && (row = csvReader.readNext()) != null) {
                try {
                    if( row.length != 0) {
                        GpuffConfig gpuffConfig = new GpuffConfig();
                        int rowNum = 0;
                        String tempStr = row[rowNum++];
                        
                        if(tempStr != null && !tempStr.isEmpty()) { 
                            gpuffConfig.setSerial(Integer.parseInt(tempStr));
                            gpuffConfig.setUser(row[rowNum++]);
                            gpuffConfig.setPassword(row[rowNum++]);
                            gpuffConfig.setApn(row[rowNum++]);
                            gpuffConfig.setIp0(row[rowNum++]);
                            gpuffConfig.setPort0(row[rowNum++]);
                            gpuffConfig.setIp1(row[rowNum++]);
                            gpuffConfig.setPort1(row[rowNum++]);
                            gpuffConfig.setPeriodic(row[rowNum++].compareToIgnoreCase("Y") == 0);
                            
                            getConfigMapping().put(gpuffConfig.getSerial(), gpuffConfig);
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.print(e);
                    log.info(e);
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.error(e.toString());
            IOUtils.closeQuietly(configReader);
            IOUtils.closeQuietly(inputStream);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(configReader);
            IOUtils.closeQuietly(inputStream);
        }
    }
    
    public void setResource(Resource cfRes) {
        csvFileResource = cfRes;
        log.info("Resource " + cfRes.toString() + " assigned for configuration.");
    }
    
    Map<Integer, GpuffConfig> getConfigMapping() {
        if (configMapping == null) {
            configMapping = new HashMap<Integer, GpuffConfig>();
    
            resetCSV = false;
            processInputCSV();  // Consume the CSV File.          
        }

        return configMapping;
    }

    @Override
    public GpuffConfig getConfigForSerial(int serialNum) {
        return getConfigMapping().get(serialNum);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                log.info("Configuration CSV will reload on the next message reciept");
                resetCSV = true;
            }
        }, resetInterval, resetInterval);
        
    }

    @Override
    public void reset() {
        if(resetCSV == true) {
            resetCSV = false;
            configMapping = null;       // Drop the map to make it reload the CSV next time through!        
        }
    }

    public void setResetInterval(long resetInterval) {
        this.resetInterval = resetInterval;
    }
    
}



