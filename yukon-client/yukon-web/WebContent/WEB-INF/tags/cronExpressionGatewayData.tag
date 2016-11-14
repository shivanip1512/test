<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="id" required="true" 
        description="Used to create unique names so multiple tags can be used on a single page without conflicting." %>
                                                     
<%@ attribute name="state" required="true" type="com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState" 
        description="Used to configure the controls to a certain state. Pass a new CronExpressionTagState 
                     to configure to default state." %>

<cti:msgScope paths="components.cronPicker">

<input type="hidden" name="${id}_CRONEXP_FREQ" value="CUSTOM"/>

<input type="checkbox" name="${id}_HOURLY_RANDOMIZED" onclick="yukon.ui.util.cronGatewayHourlyRandomizedChange('${id}', this.checked);"/><i:inline key=".hourlyRandomized"/>
<br/><br/>

<%-- CUSTOM --%>
<div id="${id}-cron-exp-custom">

    <i:inline key=".cronExpression"/><br>
    <c:if test="${not empty invalidCronString}">
        <c:set var="errorClass" value="error"/>
    </c:if>
    <div>
        <input type="text" name="${id}_CRONEXP_CUSTOM_EXPRESSION" value="${state.customExpression}" class="${errorClass}"> 
        <cti:icon icon="icon-help" data-popup="#${id}-cron-help" classes="fn vatb cp"/>
    </div>
    <c:if test="${invalidCronString}">
        <div class="error"><i:inline key="yukon.common.invalidCron"/></div>
    </c:if>
    
    <cti:msg2 var="cronHelpTitle" key=".cronHelpTitle"/>
    <div id="${id}-cron-help" data-title="${cronHelpTitle}" class="dn">
        <i:inline key=".cronHelpStart"/>
        <a href="http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/tutorial-lesson-06.html" target="_blank">
            <i:inline key=".cronHelpLink"/>
        </a>
        <i:inline key=".cronHelpEnd"/>
    </div>
    
</div>
</cti:msgScope>
