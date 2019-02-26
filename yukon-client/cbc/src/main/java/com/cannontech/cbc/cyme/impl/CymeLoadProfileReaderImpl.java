package com.cannontech.cbc.cyme.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateMidnight;
import org.joda.time.Days;
import org.joda.time.Instant;

import com.cannontech.cbc.cyme.CymeLoadProfileReader;
import com.cannontech.cbc.cyme.model.SimulationLoadFactor;
import com.cannontech.cbc.cyme.profile.CymeLoadProfile;
import com.cannontech.cbc.cyme.profile.CymeType.GlobalUnit;
import com.cannontech.cbc.cyme.profile.CymeType.IntervalFormat;
import com.cannontech.cbc.cyme.profile.CymeType.ProfileType;
import com.cannontech.cbc.cyme.profile.CymeType.Season;
import com.cannontech.cbc.cyme.profile.CymeType.TimeInterval;
import com.cannontech.cbc.cyme.profile.CymeType.Unit;
import com.cannontech.clientutils.YukonLogManager;
import com.opencsv.CSVReader;

public class CymeLoadProfileReaderImpl implements CymeLoadProfileReader {
    
    private static final Logger log = YukonLogManager.getLogger(CymeLoadProfileReaderImpl.class);
    
    public CymeLoadProfile readFromFile(String fileName) {
        CymeLoadProfile loadProfile = null;
        try {
            CSVReader reader  = new CSVReader(new BufferedReader(new FileReader(fileName)));
            
            String[] row = reader.readNext();//cyme  empty line
            row = reader.readNext();//headers
            row = reader.readNext();
            
            loadProfile = processRow(row);
            
        } catch (FileNotFoundException e) {
            log.warn("caught exception in readFromFile2", e);
        } catch (IOException e) {
            log.warn("caught exception in readFromFile2", e);
        }
        
        return loadProfile;
    }
   
   
    private CymeLoadProfile processRow(String[] row) {
        CymeLoadProfile loadProfile = new CymeLoadProfile();
        String value;

        /////////////////////
        //0 is ID. not important yet.

        /////////////////////
        //1 is Profile Type
        value = row[1].trim();
        ProfileType profileType = ProfileType.getFromCymeValue(value);
        loadProfile.setProfileType(profileType);

        /////////////////////
        //2 is Interval Format
        value = row[2].trim();
        IntervalFormat intervalFormat = IntervalFormat.getFromCymeValue(value);
        loadProfile.setIntervalFormat(intervalFormat);
        
        /////////////////////
        //3 is Time Interval
        value = row[3].trim();
        TimeInterval timeInterval = TimeInterval.getFromCymeValue(value);
        loadProfile.setTimeInterval(timeInterval);
        
        /////////////////////
        //4 is Global Unit
        value = row[4].trim();
        GlobalUnit globalUnit = GlobalUnit.getFromCymeValue(value);
        loadProfile.setGlobalUnit(globalUnit);
        
        /////////////////////
        //5 is Network Id
        value = row[5].trim();
        loadProfile.setNetworkId(value);
        
        /////////////////////
        //6 Year
        value = row[6].trim();
        loadProfile.setYear(Integer.parseInt(value));
        
        /////////////////////
        //7 Season
        value = row[7].trim();
        Season season = Season.getFromCymeValue(value);
        loadProfile.setSeason(season);

        /////////////////////
        //8 Day Type
        value = row[8].trim();
        
        /////////////////////
        //9 UNIT
        value = row[9].trim();
        Unit unit = Unit.getFromCymeValue(value);
        loadProfile.setUnit(unit);
        
        /////////////////////
        //10 Phase
        value = row[10].trim();
        loadProfile.setPhase(value);
        
        /////////////////////
        //11 Load Values.
        int num = 1440 / timeInterval.getMinutes();//1440 minutes in a day
        int intervalMillis = timeInterval.getMinutes()*60*1000;
        
        
        Float load = Float.parseFloat(row[11].trim());
        
        //Get the Instant at midnight today
        Instant executionTime = new DateMidnight().toInstant();        
        List<SimulationLoadFactor> loadValues = new ArrayList<>(num);
        int i = 0;

        do {
            load = Float.parseFloat(row[11+i].trim());
            
            Instant loadFactorTime = executionTime;
            if (executionTime.isBeforeNow()) {
                loadFactorTime = executionTime.plus(Days.ONE.toStandardDuration());    
            }
            loadValues.add(new SimulationLoadFactor(load, loadFactorTime));
            
            executionTime = executionTime.plus(intervalMillis);
        } while (++i <= num);
        
        //Order these by execution time.
        Collections.sort(loadValues);
        
        loadProfile.setValues(loadValues);
        
        return loadProfile;
    }
}
