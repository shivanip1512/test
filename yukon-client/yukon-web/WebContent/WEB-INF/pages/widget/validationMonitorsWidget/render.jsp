<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<c:url var="enabledImg" value="/WebConfig/yukon/Icons/green_circle.gif"/>
<c:url var="disabledImg" value="/WebConfig/yukon/Icons/gray_circle.gif"/>

<cti:msg var="enableText" key="yukon.common.enable"/> 
<cti:msg var="disableText" key="yukon.common.disable"/> 

<%-- CREATE NEW VALIDATION MONITOR FORM --%>
<form id="createNewValidationMonitorForm_${widgetParameters.widgetId}" action="/spring/common/vee/monitor/edit" method="get">
</form>

<%-- ERROR --%>
<c:if test="${not empty validationMonitorsWidgetError}">
    <div class="errorRed">${validationMonitorsWidgetError}</div>
</c:if>
        
<%-- TABLE --%>
<c:choose>
<c:when test="${fn:length(monitors) > 0}">

<table class="compactResultsTable">
    
    <tr>
        <th><i:inline key=".tableHeader.name"/></th>
        <th style="text-align:right;"><i:inline key=".tableHeader.threshold"/> (<i:inline key=".thresholdUnits"/>)</th>
        <th style="text-align:right;"><i:inline key=".tableHeader.monitoring"/></th>
        <th style="text-align:right;width:80px;"><i:inline key=".tableHeader.enabled"/></th>
    </tr>

    <c:forEach var="monitor" items="${monitors}">
    
        <c:set var="monitorId" value="${monitor.validationMonitorId}"/>
        <c:set var="monitorName" value="${monitor.name}"/>

        <c:set var="tdClass" value=""/>
        <c:if test="${monitor.evaluatorStatus == 'DISABLED'}">
            <c:set var="tdClass" value="subtleGray"/>
        </c:if>
        
        <tr>
            
            <td>
            
                <%-- monitor name --%>
                <cti:url var="viewValidationMonitorEditorUrl" value="/spring/common/vee/monitor/edit">
                    <cti:param name="validationMonitorId" value="${monitorId}"/>
                </cti:url>
                <cti:msg2 var="editActionTitleText" key=".actionTitle.edit"/>
                <a href="${viewValidationMonitorEditorUrl}" title="${editActionTitleText}">
                    ${monitorName}
                </a>
                
            </td>
            
            <%-- threshold --%>
            <td class="${tdClass}" style="text-align:right;">
                ${monitor.reasonableMaxKwhPerDay}
            </td>
            
            <%-- monitoring count --%>
            <td class="${tdClass}" style="text-align:right;">
                <cti:dataUpdaterValue type="VALIDATION_PROCESSING" identifier="${monitorId}/MONITORING_COUNT"/>
            </td>
            
            <%-- enable/disable --%>
			<td class="${tdClass}" style="text-align:right;">
				<c:choose>
					<c:when test="${monitor.evaluatorStatus eq 'ENABLED'}">
						<tags:widgetActionRefreshImage2 method="toggleEnabled" imgSrc="${enabledImg}" imgSrcHover="${enabledImg}" validationMonitorId="${monitorId}" title=".disable" titleArgument="${monitorName}"/>
					</c:when>
					<c:when test="${monitor.evaluatorStatus eq 'DISABLED'}">
						<tags:widgetActionRefreshImage2 method="toggleEnabled" imgSrc="${disabledImg}" imgSrcHover="${disabledImg}" validationMonitorId="${monitorId}" title=".enable" titleArgument="${monitorName}" checked="false"/>
					</c:when>
				</c:choose>
			</td>
			
        </tr>
    
    </c:forEach>

</table>
</c:when>

<c:otherwise>
    <i:inline key=".noMonitorsSetup"/>
</c:otherwise>
</c:choose>

<div style="padding-top:5px;">
<table class="noStyle" cellpadding="0" cellspacing="0" style="width:100%;">
	<tr>
		<td align="left">
			<cti:url var="reviewUrl" value="/spring/common/veeReview/home"/>
    		<a href="${reviewUrl}"><i:inline key=".review"/></a>
		</td>
		<td align="right">
			<cti:msg2 var="createNewText" key=".createNew"/>
            <tags:slowInput myFormId="createNewValidationMonitorForm_${widgetParameters.widgetId}" labelBusy="${createNewText}" label="${createNewText}"/>
		</td>
	</tr>
</table>
</div>