<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<%-- ERROR --%>
<c:if test="${not empty validationMonitorsWidgetError}">
    <div class="errorMessage">${validationMonitorsWidgetError}</div>
</c:if>
        
<%-- TABLE --%>
<cti:url var="submitUrl" value="/common/vee/monitor/edit"/>
<form action="${submitUrl}" method="get">
<c:choose>
<c:when test="${fn:length(monitors) > 0}">

<table class="compactResultsTable">
    
    <tr>
        <th style="width:20px;">&nbsp;</th>
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
        <cti:url var="viewValidationMonitorEditorUrl" value="/common/vee/monitor/edit">
            <cti:param name="validationMonitorId" value="${monitorId}"/>
        </cti:url>
        
        <tr>
            <%-- edit button --%>
            <td>
                <cti:button nameKey="edit" renderMode="image" href="${viewValidationMonitorEditorUrl}" arguments="${monitorName}"/>
            </td>
            
            <%-- monitor name --%>
            <td>            
                <a href="${viewValidationMonitorEditorUrl}" title="<cti:msg2 key=".edit.hoverText" arguments="${monitorName}"/>" >
                    ${fn:escapeXml(monitorName)}
                </a>
            </td>
            
            <%-- threshold --%>
            <td class="${tdClass}" style="text-align:right;">
                <cti:msg2 key="yukon.common.float.tenths" argument="${monitor.reasonableMaxKwhPerDay}" />
            </td>
            
            <%-- monitoring count --%>
            <td class="${tdClass}" style="text-align:right;">
                <cti:dataUpdaterValue type="VALIDATION_PROCESSING" identifier="${monitorId}/MONITORING_COUNT"/>
            </td>
            
            <%-- enable/disable --%>
			<td class="${tdClass}" style="text-align:right;">
				<c:choose>
					<c:when test="${monitor.evaluatorStatus eq 'ENABLED'}">
						<tags:widgetActionRefreshImage  method="toggleEnabled" validationMonitorId="${monitorId}"
                                                        nameKey="disable" arguments="${monitorName}"/>
					</c:when>
					<c:when test="${monitor.evaluatorStatus eq 'DISABLED'}">
						<tags:widgetActionRefreshImage method="toggleEnabled" validationMonitorId="${monitorId}"
                                                       nameKey="enable" arguments="${monitorName}"/>
					</c:when>
				</c:choose>
			</td>
			
        </tr>
    
    </c:forEach>

</table>
</c:when>

<c:otherwise>
    <i:inline key=".noMonitors"/>
</c:otherwise>
</c:choose>

<div style="padding-top:5px;">
<table class="noStyle" cellpadding="0" cellspacing="0" style="width:100%;">
	<tr>
		<td align="left">
			<cti:url var="reviewUrl" value="/common/veeReview/home"/>
    		<a href="${reviewUrl}"><i:inline key=".review"/></a>
		</td>
		<td align="right">
            <cti:button nameKey="create" type="submit" styleClass="f_blocker"/>
		</td>
	</tr>
</table>
</div>
</form>
