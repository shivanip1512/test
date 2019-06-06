<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="id" required="true" 
        description="Used to create unique names so multiple tags can be used on a single page without conflicting." %>
                                                     
<%@ attribute name="state" required="true" type="com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState" 
        description="Used to configure the controls to a certain state. Pass a new CronExpressionTagState 
                     to configure to default state." %>

<%@ attribute name="allowManual" type="java.lang.Boolean"
    description="Displays manual option. Default: false."%>
<cti:default var="allowManual" value="${false}" />

<cti:msgScope paths="components.cronPicker">
<%-- FREQUENCY --%>
<div class="stacked">
    <select id="${id}-cron-exp-freq" name="${id}_CRONEXP_FREQ" onchange="yukon.ui.util.cronExpFreqChange('${id}', this);">
        <option value="DAILY"
            <c:if test="${state.cronTagStyleType == 'DAILY'}">selected</c:if>>
            <i:inline key=".daily" />
        </option>
        <option value="WEEKLY"
            <c:if test="${state.cronTagStyleType == 'WEEKLY'}">selected</c:if>>
            <i:inline key=".weekly" />
        </option>
        <option value="MONTHLY"
            <c:if test="${state.cronTagStyleType == 'MONTHLY'}">selected</c:if>>
            <i:inline key=".monthly" />
        </option>
        <c:if test="${allowManual}">
            <option value="ONETIME"
                <c:if test="${state.cronTagStyleType == 'ONETIME'}">selected</c:if>>
                <i:inline key=".oneTime" />
            </option>
        </c:if>
        <option value="CUSTOM"
            <c:if test="${state.cronTagStyleType == 'CUSTOM'}">selected</c:if>>
            <i:inline key=".custom" />
        </option>
    </select>
</div>

<%-- TIME --%>
<div id="${id}-cron-exp-time" class="stacked">
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
            <i:inline key=".am"/>
        </option>
        <option value="PM" <c:if test="${state.cronExpressionAmPm == 'PM'}">selected</c:if>>
            <i:inline key=".pm"/>
        </option>
    </select>
</div>

<%-- DAILY --%>
<div id="${id}-cron-exp-daily" class="stacked">
    <label class="db">
        <c:set var="checked" value="${state.cronExpressionDailyOption == 'EVERYDAY' ? 'checked' : ''}"/>
        <input type="radio" name="${id}_CRONEXP_DAILY_OPTION" value="EVERYDAY" ${checked}>
        <i:inline key=".everyDay"/>
    </label>
    <label class="db">
        <c:set var="checked" value="${state.cronExpressionDailyOption == 'WEEKDAYS' ? 'checked' : ''}"/>
        <input type="radio" name="${id}_CRONEXP_DAILY_OPTION" value="WEEKDAYS" ${checked}> 
        <i:inline key=".everyWeekday"/>
    </label>
</div>

<%-- WEEKLY --%>
<div id="${id}-cron-exp-weekly" class="stacked dn button-group">
    <c:forEach var="cronDay" items="${state.allCronDays}">
        <c:set var="cronDayChecked" value=""/>
        <c:set var="found" value="false"/>
        <c:forEach var="selectedCronDay" items="${state.selectedCronDays}">
            <c:if test="${!found && selectedCronDay == cronDay}">
                <c:set var="cronDayChecked" value="true"/>
                <c:set var="found" value="true"/>
            </c:if>
        </c:forEach>
        <tags:check name="${id}_${cronDay.requestName}" value="${cronDay.number}" label="${cronDay.abbreviatedName}" checked="${cronDayChecked}" classes="M0"/>
    </c:forEach>
</div>

<%-- MONTHLY --%>
<div id="${id}-cron-exp-monthly" class="stacked dn">

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
    <i:inline key=".onDay"/>
    <input type="text" name="${id}_CRONEXP_MONTHLY_OPTION_ON_DAY_X" size="3" maxlength="3" 
        style="text-align:right;" value="${cronExpressionMontlyOptionOnDayX}">
    <i:inline key=".ofMonth"/>
    <br>
    <input type="radio" name="${id}_CRONEXP_MONTHLY_OPTION" value="LAST_DAY" 
        <c:if test="${state.cronExpressionMontlyOption == 'LAST_DAY'}">checked</c:if>>
    <i:inline key=".onLastDayOfMonth"/>
    
</div>

<%-- ONE-TIME --%>
<div id="${id}-cron-exp-one-time" class="stacked dn">
    <dt:date name="${id}_CRONEXP_ONETIME_DATE" value="${state.date}"/>    
</div>

<%-- CUSTOM --%>
<div id="${id}-cron-exp-custom" class="dn">

    <i:inline key=".cronExpression"/><br>
    <c:if test="${not empty invalidCronString}">
        <c:set var="errorClass" value="error"/>
    </c:if>
    <div>
        <input type="text" name="${id}_CRONEXP_CUSTOM_EXPRESSION" value="${fn:escapeXml(state.customExpression)}" class="${errorClass}"> 
        <cti:icon icon="icon-help" data-popup="#${id}-cron-help" classes="fn vatb cp"/>
    </div>
    <c:if test="${invalidCronString}">
        <div class="error"><i:inline key="yukon.common.invalidCron"/></div>
    </c:if>
    
    <cti:msg2 var="cronHelpTitle" key=".cronHelpTitle"/>
    <div id="${id}-cron-help" data-title="${cronHelpTitle}" class="dn">
        <i:inline key=".cronHelp"/>
    </div>
    
</div>
</cti:msgScope>
<script>$(function () { yukon.ui.util.cronExpFreqChange('${id}', $('#${id}-cron-exp-freq')[0]); });</script>