package com.cannontech.web.amr.util.cronExpressionTag;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.cannontech.user.YukonUserContext;

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
	boolean cronExpWeeklyOptionSun = false;
	boolean cronExpWeeklyOptionMon = false;
	boolean cronExpWeeklyOptionTues = false;
	boolean cronExpWeeklyOptionWed = false;
	boolean cronExpWeeklyOptionThurs = false;
	boolean cronExpWeeklyOptionFri = false;
	boolean cronExpWeeklyOptionSat = false;
	
	// monthly
	CronExprMonthlyOptionEnum cronExpressionMontlyOption = CronExprMonthlyOptionEnum.ON_DAY;
	int cronExpressionMontlyOptionOnDayX = 1;
	
	// one time
	String date = "";
	
	public CronExpressionTagState(YukonUserContext userContext) {
		
		Date today = new Date();
		SimpleDateFormat dateFormatter;
		TimeZone systemTimeZone = userContext.getTimeZone();
		
		dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
		dateFormatter.setTimeZone(systemTimeZone);
		date = dateFormatter.format(today);
		
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
	public boolean isCronExpWeeklyOptionSun() {
		return cronExpWeeklyOptionSun;
	}
	public void setCronExpWeeklyOptionSun(boolean cronExpWeeklyOptionSun) {
		this.cronExpWeeklyOptionSun = cronExpWeeklyOptionSun;
	}
	public boolean isCronExpWeeklyOptionMon() {
		return cronExpWeeklyOptionMon;
	}
	public void setCronExpWeeklyOptionMon(boolean cronExpWeeklyOptionMon) {
		this.cronExpWeeklyOptionMon = cronExpWeeklyOptionMon;
	}
	public boolean isCronExpWeeklyOptionTues() {
		return cronExpWeeklyOptionTues;
	}
	public void setCronExpWeeklyOptionTues(boolean cronExpWeeklyOptionTues) {
		this.cronExpWeeklyOptionTues = cronExpWeeklyOptionTues;
	}
	public boolean isCronExpWeeklyOptionWed() {
		return cronExpWeeklyOptionWed;
	}
	public void setCronExpWeeklyOptionWed(boolean cronExpWeeklyOptionWed) {
		this.cronExpWeeklyOptionWed = cronExpWeeklyOptionWed;
	}
	public boolean isCronExpWeeklyOptionThurs() {
		return cronExpWeeklyOptionThurs;
	}
	public void setCronExpWeeklyOptionThurs(boolean cronExpWeeklyOptionThurs) {
		this.cronExpWeeklyOptionThurs = cronExpWeeklyOptionThurs;
	}
	public boolean isCronExpWeeklyOptionFri() {
		return cronExpWeeklyOptionFri;
	}
	public void setCronExpWeeklyOptionFri(boolean cronExpWeeklyOptionFri) {
		this.cronExpWeeklyOptionFri = cronExpWeeklyOptionFri;
	}
	public boolean isCronExpWeeklyOptionSat() {
		return cronExpWeeklyOptionSat;
	}
	public void setCronExpWeeklyOptionSat(boolean cronExpWeeklyOptionSat) {
		this.cronExpWeeklyOptionSat = cronExpWeeklyOptionSat;
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
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
}
