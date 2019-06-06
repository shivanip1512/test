<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.operator.gateways">

<c:if test="${not empty errorMsg}"><tags:alertBox>${errorMsg}</tags:alertBox></c:if>

<cti:url var="url" value="/stars/gateways/${id}/schedule"/>
<form:form id="gateway-schedule-form" action="${url}" method="post" modelAttribute="schedule">
    <cti:csrfToken/>
    
    <cti:uniqueIdentifier var="uid" prefix="schedule"/>
    <input type="hidden" name="uid" value="${uid}">
    
    <cti:msgScope paths="components.cronPicker">

        <input type="hidden" name="${uid}_CRONEXP_FREQ" value="CUSTOM"/>
        
        <c:if test="${hourlyRandomized}">
                <c:set var="hourlyRandomizedChecked" value="checked"/>
        </c:if>
        <input type="checkbox" id="${uid}_HOURLY_RANDOMIZED" name="${uid}_HOURLY_RANDOMIZED" ${hourlyRandomizedChecked} onclick="yukon.ui.util.cronGatewayHourlyRandomizedChange('${uid}', this.checked);"/><i:inline key=".hourlyRandomized"/>
        <br/><br/>
        
        <div id="${uid}-cron-exp-custom">
        
            <i:inline key=".cronExpression"/><br>
            <c:if test="${not empty invalidCronString}">
                <c:set var="errorClass" value="error"/>
            </c:if>
            <div>
                <input type="text" id="${uid}_CRONEXP_CUSTOM_EXPRESSION" name="${uid}_CRONEXP_CUSTOM_EXPRESSION" value="${state.customExpression}" class="${errorClass}"> 
                <cti:icon icon="icon-help" data-popup="#${uid}-cron-help" classes="fn vatb cp"/>
            </div>
            <c:if test="${invalidCronString}">
                <div class="error"><i:inline key="yukon.common.invalidCron"/></div>
            </c:if>
            
            <cti:msg2 var="cronHelpTitle" key=".cronHelpTitle"/>
            <div id="${uid}-cron-help" data-title="${cronHelpTitle}" class="dn">
                <i:inline key=".cronHelp"/>
            </div>
            
        </div>
    </cti:msgScope>
    
</form:form>

</cti:msgScope>