<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%-- MODULE, MENU --%>
<cti:standardPage title="${reportTitle}" module="amr">

    <cti:breadCrumbs>
        <cti:crumbLink url="/dashboard" title="Home" />
        <cti:crumbLink url="/meter/start" title="Metering" />
        <c:if test="${searchResults != null}">
            <cti:crumbLink url="${searchResults}" title="Search" />
        </c:if>
        
        <c:choose>
            <c:when test="${isWaterMeter}">
                <cti:crumbLink url="/meter/water/home?deviceId=${deviceId}">
                    <cti:deviceName deviceId="${deviceId}"></cti:deviceName>
                </cti:crumbLink>
            </c:when>
            <c:when test="${isGasMeter}">
                <cti:crumbLink url="/meter/gas/home?deviceId=${deviceId}">
                    <cti:deviceName deviceId="${deviceId}"></cti:deviceName>
                </cti:crumbLink>
            </c:when>
            <c:otherwise>
                <cti:crumbLink url="/meter/home?deviceId=${deviceId}">
                    <cti:deviceName deviceId="${deviceId}"></cti:deviceName>
                </cti:crumbLink>
            </c:otherwise>
        </c:choose>
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