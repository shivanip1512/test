<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%-- MODULE, MENU --%>
<cti:standardPage title="${reportTitle}" module="amr">

    <cti:standardMenu menuSelection="meters" />

    <cti:breadCrumbs>
		<cti:crumbLink url="/operator/Operations.jsp" title="Home" />
		<cti:crumbLink url="/spring/meter/start" title="Metering" />
		<c:if test="${searchResults != null}">
			<cti:crumbLink url="${searchResults}" title="Search" />
		</c:if>
		<c:if test="${isWaterMeter}">
	        <cti:crumbLink url="/spring/meter/water/home?deviceId=${deviceId}">
	            <cti:deviceName deviceId="${deviceId}"></cti:deviceName>
	        </cti:crumbLink>
        </c:if>
        <c:if test="${!isWaterMeter}">
	        <cti:crumbLink url="/spring/meter/home?deviceId=${deviceId}">
	            <cti:deviceName deviceId="${deviceId}"></cti:deviceName>
	        </cti:crumbLink>
        </c:if>
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