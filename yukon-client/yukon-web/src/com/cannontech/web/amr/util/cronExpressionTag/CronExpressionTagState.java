package com.cannontech.web.amr.util.cronExpressionTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CronExpressionTagState {
	
	// custom
	boolean custom = false;
	String customExpression = "";
	
	// description
	String description = "";
	
	// time
	int second = 0;
	int minute = 0;
	int hour = 1;
	CronExprTagAmPmOptionEnum cronExpressionAmPm = CronExprTagAmPmOptionEnum.AM;
	
	// frequency
	CronTagStyleType cronTagStyleType = CronTagStyleType.DAILY;
	
	// daily
	CronExprTagDailyOptionEnum cronExpressionDailyOption = CronExprTagDailyOptionEnum.EVERYDAY;
	
	// weekly
	List<CronDay> allCronDays = Arrays.asList(CronDay.values());
	List<CronDay> selectedCronDays = new ArrayList<CronDay>(7);
	
	// monthly
	CronExprMonthlyOptionEnum cronExpressionMontlyOption = CronExprMonthlyOptionEnum.ON_DAY;
	int cronExpressionMontlyOptionOnDayX = 1;
	
	// one time
	Date date = new Date();
	
	public CronExpressionTagState() {
	}
	
	// setters/getters
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	public boolean isCustom() {
		return this.cronTagStyleType.equals(CronTagStyleType.CUSTOM);
	}
	public String getCustomExpression() {
		return customExpression;
	}
	public void setCustomExpression(String customExperession) {
		this.customExpression = customExperession;
	}
	public int getSecond() {
		return second;
	}
	public void setSecond(int second) {
		this.second = second;
	}
	public int getMinute() {
		return minute;
	}
	public void setMinute(int minute) {
		this.minute = minute;
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public CronExprTagAmPmOptionEnum getCronExpressionAmPm() {
		return cronExpressionAmPm;
	}
	public void setCronExpressionAmPm(CronExprTagAmPmOptionEnum cronExpressionAmPm) {
		this.cronExpressionAmPm = cronExpressionAmPm;
	}
	public CronTagStyleType getCronTagStyleType() {
		return cronTagStyleType;
	}
	public void setCronTagStyleType(CronTagStyleType cronTagStyleType) {
		this.cronTagStyleType = cronTagStyleType;
	}
	public CronExprTagDailyOptionEnum getCronExpressionDailyOption() {
		return cronExpressionDailyOption;
	}
	public void setCronExpressionDailyOption(
			CronExprTagDailyOptionEnum cronExpressionDailyOption) {
		this.cronExpressionDailyOption = cronExpressionDailyOption;
	}
	
	public void addSelectedCronDay(CronDay cronDay) {
	    selectedCronDays.add(cronDay);
	}
	public List<CronDay> getSelectedCronDays() {
	    return selectedCronDays;
	}
	public List<CronDay> getAllCronDays() {
	    return allCronDays;
	}

	public CronExprMonthlyOptionEnum getCronExpressionMontlyOption() {
		return cronExpressionMontlyOption;
	}
	public void setCronExpressionMontlyOption(
			CronExprMonthlyOptionEnum cronExpressionMontlyOption) {
		this.cronExpressionMontlyOption = cronExpressionMontlyOption;
	}
	public int getCronExpressionMontlyOptionOnDayX() {
		return cronExpressionMontlyOptionOnDayX;
	}
	public void setCronExpressionMontlyOptionOnDayX(
			int cronExpressionMontlyOptionOnDayX) {
		this.cronExpressionMontlyOptionOnDayX = cronExpressionMontlyOptionOnDayX;
	}
	public Date getDate() {
        return date;
    }
	public void setDate(Date date) {
        this.date = date;
    }
	
}
