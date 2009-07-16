<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.common.device.bulk.verifyConfigResults.pageTitle"/>

<cti:standardPage title="${pageTitle}" module="amr">
    <cti:includeCss link="/WebConfig/yukon/styles/YukonGeneralStyles.css"/>
    <cti:standardMenu menuSelection="" />

    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
        
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- bulk home --%>
        <cti:msg var="bulkOperationsPageTitle" key="yukon.common.device.bulk.bulkHome.pageTitle"/>
        <cti:crumbLink url="/spring/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        
        <%-- device selection --%>
        <cti:msg var="deviceSelectionPageTitle" key="yukon.common.device.bulk.deviceSelection.pageTitle"/>
        <cti:crumbLink url="/spring/bulk/deviceSelection" title="${deviceSelectionPageTitle}"/>
        
        <%-- collection actions --%>
        <tags:collectionActionsCrumbLink deviceCollection="${deviceCollection}" />
        
        <cti:url var="pushConfigUrl" value="/spring/bulk/config/pushConfig">
            <c:forEach var="deviceCollectionParam" items="${deviceCollection.collectionParameters}">
                <cti:param name="${deviceCollectionParam.key}" value="${deviceCollectionParam.value}"/>
            </c:forEach>
        </cti:url>
        <cti:msg var="pushConfigPageTitle" key="yukon.common.device.bulk.pushConfig.pageTitle"/>
        <cti:crumbLink url="${pushConfigUrl}" title="${pushConfigPageTitle}" />
        
        <%-- verify config results --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    <cti:url value="/spring/meter/home" var="meterHomeUrl"/>
    
    <script language="JavaScript">
    function forwardToMeterHome(id) {
        window.location = "${meterHomeUrl}?deviceId=" + id;
    }
    </script>
    
    <h2>${pageTitle}</h2>
    <br>
    <table class="resultsTable activeResultsTable" align="center">
        <tr>
            <th>Device Name</th>
            <th>Config Name</th>
            <th>Config Type</th>
            <th>Verify Results</th>
        </tr>
        
        <c:forEach var="device" items="${devices}">
            <c:choose>
                <c:when test="${resultsMap[device].synced}">
              <tr onclick="forwardToMeterHome(${device.deviceId});">
                  <td>${resultsMap[device].meter.name}</td>
                  <td>${resultsMap[device].config.name}</td>
                  <td>${resultsMap[device].config.type}</td>
                  <td>In Sync</td>
              </tr>
                </c:when>
             <c:otherwise>
                    <tr style="background: #FFDDDD;" onclick="forwardToMeterHome(${device.deviceId});">
                        <td>${resultsMap[device].meter.name}</td>
                        <td>${resultsMap[device].config.name}</td>
                        <td>${resultsMap[device].config.type}</td>
                        <td>Some attributes did not match: ${resultsMap[device].discrepancies}</td>
                    </tr>
             </c:otherwise>
            </c:choose>
        </c:forEach>
    </table>
</cti:standardPage>