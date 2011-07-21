<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<%-- NOT completely safe to use multiple tags on single page yet! --%>
<%@ attribute name="id" required="true" type="java.lang.String" description="Used to give the variable unique names so that multiple tags can be used on a single page without conflicting"%>
<%@ attribute name="state" required="true" type="com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState" description="Used to configure the controls to a certain state. Pass a new CronExpressionTagState to configure to default state."%>

<c:url var="help" value="/WebConfig/yukon/Icons/help.gif"/>
<c:url var="helpOver" value="/WebConfig/yukon/Icons/help_over.gif"/>

<script type="text/javascript">

    Event.observe(window, 'load', function() {
		${id}_cronExpFreqChange($('${id}_cronExpFreq'));
	});

	function ${id}_cronExpFreqChange(sel) {

		var selectedFreqVal = sel.options[sel.selectedIndex].value;

		if (selectedFreqVal == 'DAILY') {
			$('${id}_cronExpTimeDiv').show();
			$('${id}_cronExpDailyDiv').show();
			$('${id}_cronExpWeeklyDiv').hide();
			$('${id}_cronExpMonthlyDiv').hide();
			$('${id}_cronExpOneTimeDiv').hide();
			$('${id}_cronExpCustomDiv').hide();
		}
		else if (selectedFreqVal == 'WEEKLY') {
			$('${id}_cronExpTimeDiv').show();
			$('${id}_cronExpDailyDiv').hide();
			$('${id}_cronExpWeeklyDiv').show();
			$('${id}_cronExpMonthlyDiv').hide();
			$('${id}_cronExpOneTimeDiv').hide();
			$('${id}_cronExpCustomDiv').hide();
		}
		else if (selectedFreqVal == 'MONTHLY') {
			$('${id}_cronExpTimeDiv').show();
			$('${id}_cronExpDailyDiv').hide();
			$('${id}_cronExpWeeklyDiv').hide();
			$('${id}_cronExpMonthlyDiv').show();
			$('${id}_cronExpOneTimeDiv').hide();
			$('${id}_cronExpCustomDiv').hide();
		}
		else if (selectedFreqVal == 'ONETIME') {
			$('${id}_cronExpTimeDiv').show();
			$('${id}_cronExpDailyDiv').hide();
			$('${id}_cronExpWeeklyDiv').hide();
			$('${id}_cronExpMonthlyDiv').hide();
			$('${id}_cronExpOneTimeDiv').show();
			$('${id}_cronExpCustomDiv').hide();
		}
		else if (selectedFreqVal == 'CUSTOM') {
			$('${id}_cronExpTimeDiv').hide();
			$('${id}_cronExpDailyDiv').hide();
			$('${id}_cronExpWeeklyDiv').hide();
			$('${id}_cronExpMonthlyDiv').hide();
			$('${id}_cronExpOneTimeDiv').hide();
			$('${id}_cronExpCustomDiv').show();
		}
	}

</script>

<div style="height:105px;">

<%-- FREQUENCY --%>
<div style="padding-bottom:6px;">
<select id="${id}_cronExpFreq" name="${id}_CRONEXP_FREQ" onchange="${id}_cronExpFreqChange(this);">
	<option value="DAILY" <c:if test="${state.cronTagStyleType == 'DAILY'}">selected</c:if>>Daily</option>
	<option value="WEEKLY" <c:if test="${state.cronTagStyleType == 'WEEKLY'}">selected</c:if>>Weekly</option>
	<option value="MONTHLY" <c:if test="${state.cronTagStyleType == 'MONTHLY'}">selected</c:if>>Monthly</option>
	<option value="ONETIME" <c:if test="${state.cronTagStyleType == 'ONETIME'}">selected</c:if>>One-Time</option>
	<option value="CUSTOM" <c:if test="${state.cronTagStyleType == 'CUSTOM'}">selected</c:if>>Custom</option>
</select>
</div>

<%-- TIME --%>
<div id="${id}_cronExpTimeDiv">
<select name="${id}_CRONEXP_HOUR">
	<c:forEach var="hour" begin="1" end="12" step="1">
		<option value="${hour}" <c:if test="${state.hour == hour}">selected</c:if>>${hour}</option>
	</c:forEach>
</select> :

<select name="${id}_CRONEXP_MINUTE">
	<c:forEach var="minute" begin="0" end="59" step="5">
		<option value="${minute}" <c:if test="${state.minute == minute}">selected</c:if>>
			<c:if test="${minute < 10}">
				<c:set var="minute" value="0${minute}"/>
			</c:if>
			${minute}
		</option>
	</c:forEach>
</select>

<select name="${id}_CRONEXP_AMPM">
	<option value="AM" <c:if test="${state.cronExpressionAmPm == 'AM'}">selected</c:if>>AM</option>
	<option value="PM" <c:if test="${state.cronExpressionAmPm == 'PM'}">selected</c:if>>PM</option>
</select>
</div>

<%-- DAILY --%>
<div id="${id}_cronExpDailyDiv" style="padding-top:6px;">

	<input type="radio" name="${id}_CRONEXP_DAILY_OPTION" value="EVERYDAY" <c:if test="${state.cronExpressionDailyOption == 'EVERYDAY'}">checked</c:if> > Every Day
	<br>
	<input type="radio" name="${id}_CRONEXP_DAILY_OPTION" value="WEEKDAYS" <c:if test="${state.cronExpressionDailyOption == 'WEEKDAYS'}">checked</c:if> > Every Weekday
	
</div>

<%-- WEEKLY --%>
<div id="${id}_cronExpWeeklyDiv" style="padding-top:6px;display:none;">

	<c:forEach var="cronDay" items="${state.allCronDays}">
	
		<c:set var="cronDayChecked" value=""/>
		<c:set var="break" value="false"/>
		<c:forEach var="selectedCronDay" items="${state.selectedCronDays}">
			<c:if test="${!break && selectedCronDay == cronDay}">
				<c:set var="cronDayChecked" value="checked"/>
				<c:set var="break" value="true"/>
			</c:if>
		</c:forEach>
	
		<label><input type="checkbox" name="${id}_${cronDay.requestName}" value="${cronDay.number}" ${cronDayChecked}> ${cronDay.abbreviatedName}</label>&nbsp;
	
	</c:forEach>
	
</div>

<%-- MONTHLY --%>
<div id="${id}_cronExpMonthlyDiv" style="padding-top:6px;display:none;">

	<c:choose>
		<c:when test="${empty state.cronExpressionMontlyOptionOnDayX}">
			<c:set var="cronExpressionMontlyOptionOnDayX" value="1"/>
		</c:when>
		<c:otherwise>
			<c:set var="cronExpressionMontlyOptionOnDayX" value="${state.cronExpressionMontlyOptionOnDayX}"/>
		</c:otherwise>
	</c:choose>

	<input type="radio" name="${id}_CRONEXP_MONTHLY_OPTION" value="ON_DAY" <c:if test="${state.cronExpressionMontlyOption == 'ON_DAY'}">checked</c:if>> On day <input type="text" name="${id}_CRONEXP_MONTHLY_OPTION_ON_DAY_X" size="3" maxlength="3" style="text-align:right;" value="${cronExpressionMontlyOptionOnDayX}"> of month.
	<br>
	<input type="radio" name="${id}_CRONEXP_MONTHLY_OPTION" value="LAST_DAY" <c:if test="${state.cronExpressionMontlyOption == 'LAST_DAY'}">checked</c:if>> On last day of month.
	
</div>

<%-- ONE-TIME --%>
<div id="${id}_cronExpOneTimeDiv" style="padding-top:6px;display:none;">

<cti:formatDate var="todayStr" type="DATE" value="${state.date}"/>
<tags:dateInputCalendar fieldName="${id}_CRONEXP_ONETIME_DATE" fieldValue="${todayStr}"/>

</div>

<%-- CUSTOM --%>
<div id="${id}_cronExpCustomDiv" style="display:none;">

	Cron Expression<br>
	<input type="text" name="${id}_CRONEXP_CUSTOM_EXPRESSION" value="${state.customExpression}"> 
	<img onclick="$('${id}_customCronExpressInfoPopup').toggle();" src="${help}" onmouseover="javascript:this.src='${helpOver}'" onmouseout="javascript:this.src='${help}'">
	
	<tags:simplePopup id="${id}_customCronExpressInfoPopup" title="Custom Cron Expression">
	     <br>
	     So you've decided to use a custom Cron Expression, <a href="http://www.quartz-scheduler.org/docs/1.x/tutorials/crontrigger.html" target="_blank">now what</a>?
	     <br><br>
	</tags:simplePopup>
	
</div>

</div>