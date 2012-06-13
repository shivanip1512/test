package com.cannontech.web.amr.util.cronExpressionTag.handler;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronExprTagDailyOptionEnum;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.amr.util.cronExpressionTag.CronTagStyleType;

public class DailyCronTagStyleHandler extends CronTagStyleHandlerBase {

	public static final String CRONEXP_DAILY_OPTION = "CRONEXP_DAILY_OPTION";
	@Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
	
	@Override
	public CronTagStyleType getType() {
	    return CronTagStyleType.DAILY;
	}
	
	// BUILD
	@Override
	public String build(String id, HttpServletRequest request, YukonUserContext userContext) throws ServletRequestBindingException {

		String[] parts = new String[]{"*", "*", "*", "*", "*", "*"};
		
		// time
		buildTime(id, request, parts);
		
		// daily
		parts[3] = "?";
		parts[4] = "*";
		
		String dailyOptionStr = ServletRequestUtils.getRequiredStringParameter(request, id + "_" + CRONEXP_DAILY_OPTION);
		CronExprTagDailyOptionEnum dailyOption = CronExprTagDailyOptionEnum.valueOf(dailyOptionStr);
		
		if (dailyOption.equals(CronExprTagDailyOptionEnum.EVERYDAY)) {
			parts[5] = "*";
		} else if (dailyOption.equals(CronExprTagDailyOptionEnum.WEEKDAYS)) {
			parts[5] = "2-6";
		} else {
			throw new IllegalArgumentException(CRONEXP_DAILY_OPTION + " of " + dailyOption.name() + " is not supported.");
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
		String year = parts[6];
		if (dayOfMonthStr.equals("?") && year.equals("*") && (dayOfWeek.equals("*") || dayOfWeek.equals("2-6"))) {
			return true;
		}
		
		return false;
	}

	
	// PARSE
	@Override
	public CronExpressionTagState parse(String[] parts, YukonUserContext userContext) {

		CronExpressionTagState state = new CronExpressionTagState();
		parseTime(parts, state);
		state.setCronTagStyleType(CronTagStyleType.DAILY);
		
		String dayOfWeek = parts[5];
		
		// everyday
		if (dayOfWeek.equals("*")) {
			state.setCronExpressionDailyOption(CronExprTagDailyOptionEnum.EVERYDAY);
			
		// weekdays
		} else if (dayOfWeek.equals("2-6")) {
			state.setCronExpressionDailyOption(CronExprTagDailyOptionEnum.WEEKDAYS);
		}
		
		return state;
	}
	
	// DESCRIPTION
	public String generateDescription(CronExpressionTagState state, YukonUserContext userContext) {
		String cronEnum = messageSourceResolver.getMessageSourceAccessor(userContext).getMessage(state.getCronExpressionDailyOption().getFormatKey());
		String desc = cronEnum + ", at " + getTimeDescription(state, userContext);
		return desc;
	}
}
