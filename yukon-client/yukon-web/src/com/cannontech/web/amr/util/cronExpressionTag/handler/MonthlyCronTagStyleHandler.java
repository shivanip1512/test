package com.cannontech.web.amr.util.cronExpressionTag.handler;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.web.amr.util.cronExpressionTag.CronExprMonthlyOptionEnum;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.amr.util.cronExpressionTag.CronTagStyleType;

public class MonthlyCronTagStyleHandler extends CronTagStyleHandlerBase {

	public static final String CRONEXP_MONTHLY_OPTION = "CRONEXP_MONTHLY_OPTION";
	public static final String CRONEXP_MONTHLY_OPTION_ON_DAY_X = "CRONEXP_MONTHLY_OPTION_ON_DAY_X";
	
	// BUILD
	@Override
	public String build(String id, HttpServletRequest request) throws ServletRequestBindingException {

		String[] parts = new String[]{"*", "*", "*", "*", "*", "*"};
		
		// time
		buildTime(id, request, parts);
		
		// monthly
		String monthlyOptionStr = ServletRequestUtils.getRequiredStringParameter(request, id + "_" + CRONEXP_MONTHLY_OPTION);
		CronExprMonthlyOptionEnum monthlyOption = CronExprMonthlyOptionEnum.valueOf(monthlyOptionStr);
		
		if (monthlyOption.equals(CronExprMonthlyOptionEnum.ON_DAY)) {
			
			int monthlyOnDay = ServletRequestUtils.getRequiredIntParameter(request, id + "_" + CRONEXP_MONTHLY_OPTION_ON_DAY_X);
			parts[3] = String.valueOf(monthlyOnDay);
			
		} else if (monthlyOption.equals(CronExprMonthlyOptionEnum.LAST_DAY)) {
			parts[3] = "L";
		} else {
			throw new IllegalArgumentException(CRONEXP_MONTHLY_OPTION + " of " + monthlyOption.name() + " is not supported.");
		}
		
		parts[4] = "*";
		parts[5] = "?";
		
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
		String year = parts[6];
		if (dayOfWeek.equals("?") && year.equals("*") && (dayOfMonthStr.equals("L") || NumberUtils.isDigits(dayOfMonthStr))) {
			return true;
		}
		
		return false;
	}

	
	// PARSE
	@Override
	public CronExpressionTagState parse(String[] parts) {

		CronExpressionTagState state = new CronExpressionTagState();
		parseTime(parts, state);
		state.setCronTagStyleType(CronTagStyleType.MONTHLY);
		
		String dayOfMonthStr = parts[3];
		
		// last day
		if (dayOfMonthStr.equals("L")) {
			
			state.setCronExpressionMontlyOption(CronExprMonthlyOptionEnum.LAST_DAY);
		
		// on day X
		} else {
			
			state.setCronExpressionMontlyOption(CronExprMonthlyOptionEnum.ON_DAY);
			
			int dayX = Integer.valueOf(dayOfMonthStr);
			state.setCronExpressionMontlyOptionOnDayX(dayX);
		}
		
		return state;
	}
	
	// DESCRIPTION
	public String generateDescription(CronExpressionTagState state) {
		
		String desc = "";
		
		CronExprMonthlyOptionEnum montlyOption = state.getCronExpressionMontlyOption();
		if (montlyOption.equals(CronExprMonthlyOptionEnum.LAST_DAY)) {
			desc += "Last day of month";
		} else if (montlyOption.equals(CronExprMonthlyOptionEnum.ON_DAY)) {
			int dayX = state.getCronExpressionMontlyOptionOnDayX();
			desc += "Monthly, on the " + dayX + CtiUtilities.getOrdinalFor(dayX);
		}
		
		desc += ", at " + getTimeDescription(state);
		
		return desc;
	}
}
