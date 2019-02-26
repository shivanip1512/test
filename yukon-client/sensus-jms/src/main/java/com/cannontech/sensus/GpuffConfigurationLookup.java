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
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import com.cannontech.clientutils.YukonLogManager;
import com.opencsv.CSVReader;

public class GpuffConfigurationLookup
implements ConfigLookup, InitializingBean
{
private Logger log = YukonLogManager.getLogger(GpuffConfigurationLookup.class);
private Map<Integer, GpuffConfig> configMapping = null;
private Resource csvFileResource;
private Resource csvGCVTFileResource;
private Resource csvGVARFileResource;
private long resetInterval = 900000L;
protected boolean resetCSV;

private void processInputCSV()
{
  InputStream inputStream = null;
  Reader configReader = null;
  try {
    String[] row = null;
    inputStream = this.csvFileResource.getInputStream();
    configReader = new InputStreamReader(inputStream, "UTF-8");
    CSVReader csvReader = null;
    try {
      csvReader = new CSVReader(configReader);
    } catch (Exception e) {
      System.out.print(e);
      this.log.info(e);
    }

    while ((csvReader != null) && ((row = csvReader.readNext()) != null)) {
      if (row.length != 0) {
        GpuffConfig gpuffConfig = new GpuffConfig();
        String tempStr = row[0];

        if ((tempStr != null) && (!tempStr.isEmpty()) && (tempStr.charAt(0) != '#') && (tempStr.charAt(0) != ';')) {
          try {
            gpuffConfig.decodeRow(row);
          } catch (NumberFormatException e) {
            System.out.print(e);
            this.log.info("Skipping CSV row \"" + row.toString() + "\" " + e);
            continue;
          } catch (ArrayIndexOutOfBoundsException e) {
            System.out.print(e);
            this.log.info("Skipping CSV row \"" + row.toString() + "\" " + e);
            continue;
          }
          getConfigMapping().put(Integer.valueOf(gpuffConfig.getSerial()), gpuffConfig);
        }
      }
    }

    inputStream = this.csvGCVTFileResource.getInputStream();
    configReader = new InputStreamReader(inputStream, "UTF-8");
    csvReader = null;
    try {
      csvReader = new CSVReader(configReader);
    } catch (Exception e) {
      System.out.print(e);
      this.log.info(e);
    }

    while ((csvReader != null) && ((row = csvReader.readNext()) != null)) {
      if (row.length != 0) {
        GpuffConfig gpuffConfig = new GpuffConfigGCVT();
        String tempStr = row[0];

        if ((tempStr != null) && (!tempStr.isEmpty()) && (tempStr.charAt(0) != '#') && (tempStr.charAt(0) != ';')) {
          try {
            gpuffConfig.decodeRow(row);
          } catch (NumberFormatException e) {
            System.out.print(e);
            this.log.info("Skipping GCVT CSV row \"" + row.toString() + "\" " + e);
            continue;
          }

          getConfigMapping().put(Integer.valueOf(gpuffConfig.getSerial()), gpuffConfig);
        }
      }
    }

    inputStream = this.csvGVARFileResource.getInputStream();
    configReader = new InputStreamReader(inputStream, "UTF-8");
    csvReader = null;
    try {
      csvReader = new CSVReader(configReader);
    } catch (Exception e) {
      System.out.print(e);
      this.log.info(e);
    }

    while ((csvReader != null) && ((row = csvReader.readNext()) != null)) {
        if (row.length != 0) {
            GpuffConfig gpuffConfig = new GpuffConfigGVAR();
            String tempStr = row[0];

            if ((tempStr != null) && (!tempStr.isEmpty()) && (tempStr.charAt(0) != '#') && (tempStr.charAt(0) != ';')) {
                try {
                    gpuffConfig.decodeRow(row);
                } catch (NumberFormatException e) {
                    System.out.print(e);
                    this.log.info("Skipping GVAR CSV row \"" + row.toString() + "\" " + e);
                    continue;            
                }

                getConfigMapping().put(Integer.valueOf(gpuffConfig.getSerial()), gpuffConfig);
            }
        }
    }
  }
  catch (UnsupportedEncodingException e) {
    this.log.error(e.toString());
    IOUtils.closeQuietly(configReader);
    IOUtils.closeQuietly(inputStream);
  }
  catch (IOException e) {
    e.printStackTrace();
  } finally {
    IOUtils.closeQuietly(configReader);
    IOUtils.closeQuietly(inputStream);
  }
}

public void setResource(Resource cfRes) {
  this.csvFileResource = cfRes;
  this.log.info("Resource " + cfRes.toString() + " assigned for configuration.");
}

public void setGcvtResource(Resource cfRes) {
  this.csvGCVTFileResource = cfRes;
  this.log.info("Resource (GCVT) " + cfRes.toString() + " assigned for configuration.");
}

public void setGvarResource(Resource cfRes) {
  this.csvGVARFileResource = cfRes;
  this.log.info("Resource (GVAR) " + cfRes.toString() + " assigned for configuration.");
}

Map<Integer, GpuffConfig> getConfigMapping() {
  if (this.resetCSV) {
    reset();
  }
  if (this.configMapping == null) {
    this.configMapping = new HashMap<Integer, GpuffConfig>();

    processInputCSV();
  }

  return this.configMapping;
}

public GpuffConfig getConfigForSerial(int serialNum)
{
  return (GpuffConfig)getConfigMapping().get(Integer.valueOf(serialNum));
}

public void afterPropertiesSet() throws Exception
{
  Timer timer = new Timer(true);
  timer.schedule(new TimerTask()
  {
    public void run() {
      if (!GpuffConfigurationLookup.this.resetCSV) {
        GpuffConfigurationLookup.this.log.info("Configuration CSV will reload on the next config lookup");
        GpuffConfigurationLookup.this.resetCSV = true;
      }
    }
  }
  , this.resetInterval, this.resetInterval);
}

public void reset()
{
  this.resetCSV = false;
  this.configMapping = null;
}

public void setResetInterval(long resetInterval) {
  this.resetInterval = resetInterval;
}
}