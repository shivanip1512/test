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
        
        <tags:crumbLinkByMap url="/spring/bulk/config/pushConfig" parameterMap="${deviceCollection.collectionParameters}" titleKey="yukon.common.device.bulk.pushConfig.pageTitle" />
        
        <%-- verify config results --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    <cti:url value="/spring/meter/home" var="meterHomeUrl"/>
    
    <script type="text/javascript">
    function forwardToMeterHome(id) {
        window.location = "${meterHomeUrl}?deviceId=" + id;
    }
    </script>
    
    <h2>${pageTitle}</h2>
    <br>
    
    <%-- SUCCESS --%>
    <br>
    <div class="normalBoldLabel">Verified Devices: <span class="okGreen">${successCollection.deviceCount}</span></div>
    <c:if test="${successCollection.deviceCount > 0}">
        <div id="successActionsDiv" style="padding:10px;">
    
        <%-- device collection action --%>
        <cti:link href="/spring/bulk/collectionActions" key="yukon.common.device.commander.collectionActionOnDevicesLabel.successResults" class="small">
            <cti:mapParam value="${successCollection.collectionParameters}"/>
        </cti:link>
        <tags:selectedDevicesPopup deviceCollection="${successCollection}" />
        
    </div>
    <br>
    </c:if>
    
    <%-- FAILURE --%>
    <div class="normalBoldLabel">Devices with configuration descrepancies: <span class="errorRed">${failureCollection.deviceCount}</span></div>
    
    <c:if test="${failureCollection.deviceCount > 0}">
	    <div id="errorActionsDiv" style="padding:10px;">
	    
	        <%-- device collection action --%>
	        <cti:link href="/spring/bulk/collectionActions" key="yukon.common.device.commander.collectionActionOnDevicesLabel.failureResults" class="small">
	            <cti:mapParam value="${failureCollection.collectionParameters}"/>
	        </cti:link>
	        <tags:selectedDevicesPopup deviceCollection="${failureCollection}" />
	        
	    </div>
    </c:if>
     
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
                    <tr>
                        <td onclick="forwardToMeterHome(${device.deviceId});">${resultsMap[device].meter.name}</td>
                        <td>${resultsMap[device].config.name}</td>
                        <td>${resultsMap[device].config.type}</td>
                        <td>In Sync</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <tr class="lightRedBackground">
                        <td onclick="forwardToMeterHome(${device.deviceId});">${resultsMap[device].meter.name}</td>
                        <td>${resultsMap[device].config.name}</td>
                        <td>${resultsMap[device].config.type}</td>
                        <td>Some attributes did not match: ${resultsMap[device].discrepancies}</td>
                    </tr>
             </c:otherwise>
            </c:choose>
        </c:forEach>
    </table>
</cti:standardPage>