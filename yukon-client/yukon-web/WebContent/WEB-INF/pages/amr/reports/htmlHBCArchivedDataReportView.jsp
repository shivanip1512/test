<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%-- MODULE, MENU --%>
<cti:standardPage title="${reportTitle}" module="amr">

    <cti:standardMenu menuSelection="meters" />

    <cti:breadCrumbs>
		<cti:crumbLink url="/dashboard" title="Home" />
		<cti:crumbLink url="/meter/start" title="Metering" />
		<c:if test="${searchResults != null}">
			<cti:crumbLink url="${searchResults}" title="Search" />
		</c:if>
        <cti:crumbLink url="/meter/home?deviceId=${deviceId}">
            <cti:deviceName deviceId="${deviceId}"></cti:deviceName>
        </cti:crumbLink>
        
        <cti:url var="hbcUrl" value="/meter/highBill/view">
            <cti:param name="deviceId" value="${deviceId}"/>
            <cti:param name="analyze" value="${analyze}"/>
            <cti:param name="getReportStartDate" value="${getReportStartDate}"/>
            <cti:param name="getReportStopDate" value="${getReportStopDate}"/>
            <cti:param name="chartRange" value="${chartRange}"/>
        </cti:url>
        
        <cti:crumbLink url="${hbcUrl}">High Bill Complaint</cti:crumbLink>
        <cti:crumbLink>${reportTitle}</cti:crumbLink>
    </cti:breadCrumbs>

    <cti:simpleReportUrlFromNameTag var="bodyUrl"
                                    htmlOutput="false"
                                    viewType="extView"
                                    viewJsp="BODY"
                                    definitionName="${definitionName}"
                                    pointId="${pointId}"
                                    startDate="${startDate}"
                                    stopDate="${stopDate}" />
                                    
    <jsp:include page="${bodyUrl}" />

</cti:standardPage>