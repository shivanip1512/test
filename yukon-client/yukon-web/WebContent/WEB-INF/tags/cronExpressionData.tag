<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<%@ attribute name="id" required="true" rtexprvalue="true" description="Used to give the variable unique names so that multiple tags can be used on a single page without conflicting"%>
<%@ attribute name="state" required="true" rtexprvalue="true" type="com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState" description="Used to configure the controls to a certain state. Pass a new CronExpressionTagState to configure to default state."%>
<%@ attribute name="allowTypeChange" rtexprvalue="true" type="java.lang.Boolean" description="Allow changing between Repeating and One-Time job options."%>

<cti:includeScript link="/JavaScript/cronExpressionData.js"/>

<c:url var="help" value="/WebConfig/yukon/Icons/help.gif"/>
<c:url var="helpOver" value="/WebConfig/yukon/Icons/help_over.gif"/>

<c:set var="allowChange" value="true"/>
<c:if test="${!empty pageScope.allowTypeChange}">
    <c:set var="allowChange" value="${pageScope.allowTypeChange}"/>
</c:if>

<div style="height:105px;">
<%-- FREQUENCY --%>
<div style="padding-bottom: 6px;">
    <select id="${id}_cronExpFreq" name="${id}_CRONEXP_FREQ" onchange="cronExpFreqChange('${id}', this);">
        <c:choose>
            <c:when test="${!allowChange}">
                <c:choose>
                    <c:when test="${state.cronTagStyleType == 'ONETIME'}">
                        <option value="ONETIME"
                            <c:if test="${state.cronTagStyleType == 'ONETIME'}">selected</c:if>>
                            <i:inline key="yukon.web.components.cronPicker.oneTime" />
                        </option>
                    </c:when>
                    <c:otherwise>
                        <option value="DAILY"
                            <c:if test="${state.cronTagStyleType == 'DAILY'}">selected</c:if>>
                            <i:inline key="yukon.web.components.cronPicker.daily" />
                        </option>
                        <option value="WEEKLY"
                            <c:if test="${state.cronTagStyleType == 'WEEKLY'}">selected</c:if>>
                            <i:inline key="yukon.web.components.cronPicker.weekly" />
                        </option>
                        <option value="MONTHLY"
                            <c:if test="${state.cronTagStyleType == 'MONTHLY'}">selected</c:if>>
                            <i:inline key="yukon.web.components.cronPicker.monthly" />
                        </option>
                        <option value="CUSTOM"
                            <c:if test="${state.cronTagStyleType == 'CUSTOM'}">selected</c:if>>
                            <i:inline key="yukon.web.components.cronPicker.custom" />
                        </option>
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <option value="DAILY"
                    <c:if test="${state.cronTagStyleType == 'DAILY'}">selected</c:if>>
                    <i:inline key="yukon.web.components.cronPicker.daily" />
                </option>
                <option value="WEEKLY"
                    <c:if test="${state.cronTagStyleType == 'WEEKLY'}">selected</c:if>>
                    <i:inline key="yukon.web.components.cronPicker.weekly" />
                </option>
                <option value="MONTHLY"
                    <c:if test="${state.cronTagStyleType == 'MONTHLY'}">selected</c:if>>
                    <i:inline key="yukon.web.components.cronPicker.monthly" />
                </option>
                <option value="ONETIME"
                    <c:if test="${state.cronTagStyleType == 'ONETIME'}">selected</c:if>>
                    <i:inline key="yukon.web.components.cronPicker.oneTime" />
                </option>
                <option value="CUSTOM"
                    <c:if test="${state.cronTagStyleType == 'CUSTOM'}">selected</c:if>>
                    <i:inline key="yukon.web.components.cronPicker.custom" />
                </option>
            </c:otherwise>
        </c:choose>
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
	<option value="AM" <c:if test="${state.cronExpressionAmPm == 'AM'}">selected</c:if>>
        <i:inline key="yukon.web.components.cronPicker.am"/>
    </option>
	<option value="PM" <c:if test="${state.cronExpressionAmPm == 'PM'}">selected</c:if>>
        <i:inline key="yukon.web.components.cronPicker.pm"/>
    </option>
</select>
</div>

<%-- DAILY --%>
<div id="${id}_cronExpDailyDiv" style="padding-top:6px;">

	<input type="radio" name="${id}_CRONEXP_DAILY_OPTION" value="EVERYDAY" 
        <c:if test="${state.cronExpressionDailyOption == 'EVERYDAY'}">checked</c:if>>
    <i:inline key="yukon.web.components.cronPicker.everyDay"/>
	<br>
	<input type="radio" name="${id}_CRONEXP_DAILY_OPTION" value="WEEKDAYS" 
        <c:if test="${state.cronExpressionDailyOption == 'WEEKDAYS'}">checked</c:if>>
    <i:inline key="yukon.web.components.cronPicker.everyWeekday"/>
	
</div>

<%-- WEEKLY --%>
<div id="${id}_cronExpWeeklyDiv" style="padding-top:6px;display:none;">

	<c:forEach var="cronDay" items="${state.allCronDays}">
	
		<c:set var="cronDayChecked" value=""/>
		<c:set var="found" value="false"/>
		<c:forEach var="selectedCronDay" items="${state.selectedCronDays}">
			<c:if test="${!found && selectedCronDay == cronDay}">
				<c:set var="cronDayChecked" value="checked"/>
				<c:set var="found" value="true"/>
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

	<input type="radio" name="${id}_CRONEXP_MONTHLY_OPTION" value="ON_DAY" 
        <c:if test="${state.cronExpressionMontlyOption == 'ON_DAY'}">checked</c:if>>
    <i:inline key="yukon.web.components.cronPicker.onDay"/>
    <input type="text" name="${id}_CRONEXP_MONTHLY_OPTION_ON_DAY_X" size="3" maxlength="3" 
        style="text-align:right;" value="${cronExpressionMontlyOptionOnDayX}">
    <i:inline key="yukon.web.components.cronPicker.ofMonth"/>
	<br>
	<input type="radio" name="${id}_CRONEXP_MONTHLY_OPTION" value="LAST_DAY" 
        <c:if test="${state.cronExpressionMontlyOption == 'LAST_DAY'}">checked</c:if>>
    <i:inline key="yukon.web.components.cronPicker.onLastDayOfMonth"/>
	
</div>

<%-- ONE-TIME --%>
<div id="${id}_cronExpOneTimeDiv" style="padding-top:6px;display:none;">
	<dt:date name="${id}_CRONEXP_ONETIME_DATE" value="${state.date}"/>
</div>

<%-- CUSTOM --%>
<div id="${id}_cronExpCustomDiv" style="display:none;">

	<i:inline key="yukon.web.components.cronPicker.cronExpression"/><br>
	<input type="text" name="${id}_CRONEXP_CUSTOM_EXPRESSION" value="${state.customExpression}"> 
	<img onclick="$('${id}_customCronExpressInfoPopup').toggle();" src="${help}" onmouseover="javascript:this.src='${helpOver}'" onmouseout="javascript:this.src='${help}'">
	
    <cti:msg2 var="cronTitle" key="yukon.web.components.cronPicker.cronHelpTitle"/>
	<tags:simplePopup id="${id}_customCronExpressInfoPopup" title="${cronTitle}">
        <br>
        <i:inline key="yukon.web.components.cronPicker.cronHelpStart" />
        <a href="http://www.quartz-scheduler.org/documentation/quartz-1.x/tutorials/TutorialLesson06"
            target="_blank"><i:inline key="yukon.web.components.cronPicker.cronHelpLink"/></a><i:inline key="yukon.web.components.cronPicker.cronHelpEnd" />
        <br><br>
    </tags:simplePopup>
	
</div>

</div>

<script type="text/javascript">
callAfterMainWindowLoad(function () {
    cronExpFreqChange('${id}', $('${id}_cronExpFreq'));
});
</script>