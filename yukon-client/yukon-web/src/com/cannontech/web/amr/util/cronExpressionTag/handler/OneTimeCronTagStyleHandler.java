package com.cannontech.web.amr.util.cronExpressionTag.handler;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.amr.util.cronExpressionTag.CronTagStyleType;

public class OneTimeCronTagStyleHandler extends CronTagStyleHandlerBase {

	public static final String CRONEXP_ONETIME_DATE = "CRONEXP_ONETIME_DATE";
	
	// BUILD
	@Override
	public String build(String id, HttpServletRequest request) throws ServletRequestBindingException {

		String[] parts = new String[]{"*", "*", "*", "*", "*", "*", "*"};
		
		// time
		buildTime(id, request, parts);
		
		String dateStr = ServletRequestUtils.getRequiredStringParameter(request, id + "_" + CRONEXP_ONETIME_DATE);
		
		String[] dateParts = StringUtils.split(dateStr, "/");
		int dayOfMonth = Integer.valueOf(dateParts[1]);
		int month = Integer.valueOf(dateParts[0]);
		int year = Integer.valueOf(dateParts[2]);
		
		// one time
		parts[3] = String.valueOf(dayOfMonth);
		parts[4] = String.valueOf(month);
		parts[5] = "?";
		parts[6] = String.valueOf(year);
		
		return StringUtils.join(parts, " ").trim();
	}

	
	// CAN PARSE
	@Override
	public boolean canParse(String[] parts) {

		if (usesCustomTime(parts)) {
			return false;
		}
		
		if (parts.length == 7 && NumberUtils.isDigits(parts[6])) {
			return true;
		}
		
		return false;
	}

	
	// PARSE
	@Override
	public CronExpressionTagState parse(String[] parts) {

		CronExpressionTagState state = new CronExpressionTagState();
		parseTime(parts, state);
		state.setCronTagStyleType(CronTagStyleType.ONETIME);
		
		String dayOfMonth = parts[3];
		String month = parts[4];
		String year = parts[6];
		
		String[] dateParts = {month, dayOfMonth, year};
		
		if (NumberUtils.isDigits(dayOfMonth) && NumberUtils.isDigits(month) && NumberUtils.isDigits(year)) {
			state.setDate(StringUtils.join(dateParts, "/"));
		}
		
		return state;
	}
	
	// DESCRIPTION
	public String generateDescription(CronExpressionTagState state) {
		
		String desc = "One-time, " +state.getDate() + ", at " + getTimeDescription(state);
		return desc;
	}
}
