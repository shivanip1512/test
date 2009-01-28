<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%-- MODULE, MENU --%>
<cti:standardPage title="${reportTitle}" module="amr">

    <cti:standardMenu menuSelection="meters" />

    <cti:breadCrumbs>
    	<cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
    	<cti:crumbLink url="/spring/meter/search" title="Meters" />
        <cti:crumbLink url="/spring/meter/home?deviceId=${deviceId}">
            <cti:deviceName deviceId="${deviceId}"></cti:deviceName>
        </cti:crumbLink>
        
        <cti:url var="hbcUrl" value="/spring/meter/highBill/view">
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
                                    viewType="htmlView"
                                    viewJsp="BODY"
                                    definitionName="${definitionName}"
                                    pointId="${pointId}"
                                    startDate="${startDate}"
                                    stopDate="${stopDate}" />
                                    
    <jsp:include page="${bodyUrl}" />

</cti:standardPage>