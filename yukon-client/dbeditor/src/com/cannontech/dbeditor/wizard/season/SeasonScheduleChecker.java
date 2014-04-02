package com.cannontech.dbeditor.wizard.season;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.season.DateOfSeason;

public class SeasonScheduleChecker {

    /* A matrix of months X days that represents every day of the year */
    String[][] fullYearOfDays = new String[12][31];
    
    public SeasonScheduleChecker(){
        /* Fill in the days that don't exist in a calender. */
        /* Febuary */
        fullYearOfDays[1][28] = CtiUtilities.STRING_NONE;
        fullYearOfDays[1][29] = CtiUtilities.STRING_NONE;
        fullYearOfDays[1][30] = CtiUtilities.STRING_NONE;
        
        /* April */
        fullYearOfDays[3][30] = CtiUtilities.STRING_NONE;
        
        /* June */
        fullYearOfDays[5][30] = CtiUtilities.STRING_NONE;
        
        /* September */
        fullYearOfDays[8][30] = CtiUtilities.STRING_NONE;

        /* November */
        fullYearOfDays[10][30] = CtiUtilities.STRING_NONE;
        
    }
    
    public void addSeason(DateOfSeason season){
        boolean wrapsEndOfYear = doesSeasonWrapEOY(season);
        
        int startingMonth = season.getSeasonStartMonth() - 1;
        int endingMonth = season.getSeasonEndMonth() - 1;
        int startingDay = season.getSeasonStartDay() - 1;
        int endingDay = season.getSeasonEndDay() - 1;
        
        if(wrapsEndOfYear) {
            /* Fill in this year */
            for(int i = startingMonth; i < 12; i++){
                for(int j = 0; j < 31; j++){
                    if(i == startingMonth){
                        if(j < startingDay){
                            continue;
                        }else {
                            reserveDay(i,j, season);
                        }
                    }else {
                        reserveDay(i,j, season);
                    }
                }
            }
            /* Fill in next year */
            for(int i = 0; i <= endingMonth; i++){
                for(int j = 0; j < 31; j++){
                    if(i == endingMonth){
                        if(j > endingDay) { 
                            break; /* All done */
                        }else {
                            reserveDay(i,j, season);
                        }
                    } else {
                        reserveDay(i,j, season);
                    }
                }
            }
        } else if(startingMonth == endingMonth) { /* Season contained in one month. */
            for(int i = startingDay; i <=endingDay; i ++){
                reserveDay(startingMonth, i, season);
            }
        } else { /* Seasons extends past end of starting month but does not wrap end of the year. */
            for(int i = startingMonth; i <= endingMonth; i++){
                if(i == startingMonth){
                    for(int j = startingDay; j <=30; j++){ /* Fill in first month */
                        reserveDay(i, j, season);
                    }
                } else if(i == endingMonth) {
                    for(int j = 0; j <= endingDay; j++){ /* Fill in last month */
                        reserveDay(i, j, season);
                    }
                } else {
                    for(int j = 0; j < 31; j ++){ /* Fill in middle months */
                        reserveDay(i, j, season);
                    }
                }
            }
        }
    }

    private void reserveDay(int i, int j, DateOfSeason season) {
        String value = fullYearOfDays[i][j];
        if(StringUtils.isBlank(value)){
            fullYearOfDays[i][j] = season.getSeasonName();
        }else if (value.equalsIgnoreCase(CtiUtilities.STRING_NONE)){
            return; /* These days don't really exist. */
        }else {
            throw new OverlappingSeasonException("Season: " + season.getSeasonName() + " overlaps with Season:" + value);
        }
    }

    private boolean doesSeasonWrapEOY(DateOfSeason season) {
        int startingMonth = season.getSeasonStartMonth();
        int endingMonth = season.getSeasonEndMonth();
        int startingDay = season.getSeasonStartDay();
        int endingDay = season.getSeasonEndDay();
        if(startingMonth > endingMonth ){
            return true;
        } else { 
            if( startingMonth == endingMonth){
                if(endingDay < startingDay){
                    return true;
                }else {
                    return false;
                }
            }
        }
        
        return false;
    }
    
}