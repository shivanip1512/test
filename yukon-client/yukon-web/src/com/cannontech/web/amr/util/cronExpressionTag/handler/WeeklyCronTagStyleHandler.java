package com.cannontech.web.amr.util.cronExpressionTag.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.amr.util.cronExpressionTag.CronTagStyleType;

public class WeeklyCronTagStyleHandler extends CronTagStyleHandlerBase {

	public static final String[] CRONEXP_DAYS = {"CRONEXP_WEEKLY_OPTION_EVERY_X_WEEKS_ON_SUN",
													"CRONEXP_WEEKLY_OPTION_EVERY_X_WEEKS_ON_MON",
													"CRONEXP_WEEKLY_OPTION_EVERY_X_WEEKS_ON_TUES",
													"CRONEXP_WEEKLY_OPTION_EVERY_X_WEEKS_ON_WED",
													"CRONEXP_WEEKLY_OPTION_EVERY_X_WEEKS_ON_THURS",
													"CRONEXP_WEEKLY_OPTION_EVERY_X_WEEKS_ON_FRI",
													"CRONEXP_WEEKLY_OPTION_EVERY_X_WEEKS_ON_SAT"};
	
	// BUILD
	@Override
	public String build(String id, HttpServletRequest request) throws ServletRequestBindingException {

		String[] parts = new String[]{"*", "*", "*", "*", "*", "*"};
		
		// time
		buildTime(id, request, parts);
		
		// weekly
		parts[3] = "?";
		parts[4] = "*";
		
		List<String> selectedDays = new ArrayList<String>();
		for (int i = 0; i < CRONEXP_DAYS.length; i++) {
			
			String dayName = CRONEXP_DAYS[i];
			String dayStr = ServletRequestUtils.getStringParameter(request, id + "_" + dayName, null);
			if (dayStr != null) {
				selectedDays.add(String.valueOf(dayStr));
			}
		}
		
		if (selectedDays.size() > 0) {
			parts[5] = StringUtils.join(selectedDays, ",");
		} else {
			throw new IllegalArgumentException("Weekly option selected, but no days checked.");
		}
		
		return StringUtils.join(parts, " ").trim();
	}

	
	// CAN PARSE
	@Override
	public boolean canParse(String[] parts) {

		if (usesCustomTime(parts)) {
			return false;
		}
		
		String dayOfMonthStr = parts[3];
		String dayOfWeek = parts[5];
		String[] dayStrsArray = {"1", "2", "3", "4", "5", "6", "7"};
		List<String> dayStrsList = Arrays.asList(dayStrsArray);
		if (dayOfMonthStr.equals("?")) {

			// dayOfWeek must be single day number or list of day numbers
			String[] dayStrs = dayOfWeek.split(",");
			for (String dayStr : dayStrs) {
				if (!dayStrsList.contains(dayStr)) {
					return false;
				}
			}
			return true;
		}
		
		return false;
	}


	// PARSE
	@Override
	public CronExpressionTagState parse(String[] parts) {

		CronExpressionTagState state = new CronExpressionTagState();
		parseTime(parts, state);
		state.setCronTagStyleType(CronTagStyleType.WEEKLY);
		
		String dayOfWeek = parts[5];
		String[] dayStrs = dayOfWeek.split(",");
		for (String dayStr : dayStrs) {
			int dayNum = Integer.valueOf(dayStr);
			if (dayNum == 1) {
				state.setCronExpWeeklyOptionSun(true);
			} else if (dayNum == 2) {
				state.setCronExpWeeklyOptionMon(true);
			} else if (dayNum == 3) {
				state.setCronExpWeeklyOptionTues(true);
			} else if (dayNum == 4) {
				state.setCronExpWeeklyOptionWed(true);
			} else if (dayNum == 5) {
				state.setCronExpWeeklyOptionThurs(true);
			} else if (dayNum == 6) {
				state.setCronExpWeeklyOptionFri(true);
			} else if (dayNum == 7) {
				state.setCronExpWeeklyOptionSat(true);
			}
		}
		
		return state;
	}
	
	// DESCRIPTION
	public String generateDescription(CronExpressionTagState state) {
		
		String desc = "";
		
		int days = 0;
		List<String> daysAbbrs = new ArrayList<String>();
		List<String> daysFulls = new ArrayList<String>();
		
		if (state.isCronExpWeeklyOptionSun()) {
			days++;
			daysAbbrs.add("Sun");
			daysFulls.add("Sundays");
		}
		if (state.isCronExpWeeklyOptionMon()) {
			days++;
			daysAbbrs.add("Mon");
			daysFulls.add("Mondays");
		}
		if (state.isCronExpWeeklyOptionTues()) {
			days++;
			daysAbbrs.add("Tues");
			daysFulls.add("Tuesdays");
		}
		if (state.isCronExpWeeklyOptionWed()) {
			days++;
			daysAbbrs.add("Wed");
			daysFulls.add("Wednesdays");
		}
		if (state.isCronExpWeeklyOptionThurs()) {
			days++;
			daysAbbrs.add("Thurs");
			daysFulls.add("Thursdays");
		}
		if (state.isCronExpWeeklyOptionFri()) {
			days++;
			daysAbbrs.add("Fri");
			daysFulls.add("Fridays");
		}
		if (state.isCronExpWeeklyOptionSat()) {
			days++;
			daysAbbrs.add("Sat");
			daysFulls.add("Saturdays");
		}
		
		if (days == 1) {
			desc += daysFulls.get(0) + ", at " + getTimeDescription(state);
		} else if (days == 2) {
			desc += daysFulls.get(0) + " and " + daysFulls.get(1) + ", at " + getTimeDescription(state);
		} else if (days > 2) {
			
			List<String> allButLast = daysAbbrs.subList(0, daysAbbrs.size() - 1);
			String last = daysAbbrs.get(daysAbbrs.size() - 1);
			
			desc += StringUtils.join(allButLast, ", ") + " and " + last + ", at " + getTimeDescription(state);
		}
		
		return desc;
	}
}
