<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.common.device.bulk.verifyConfigResults.pageTitle"/>
<cti:msg var="successLabel" key="yukon.common.device.bulk.verifyConfigResults.successLabel"/>
<cti:msg var="failureLabel" key="yukon.common.device.bulk.verifyConfigResults.failureLabel"/>
<cti:msg var="unsupportedLabel" key="yukon.common.device.bulk.verifyConfigResults.unsupportedLabel"/>
<cti:msg var="successResult" key="yukon.common.device.bulk.verifyConfigResults.successResult"/>
<cti:msg var="failureResult" key="yukon.common.device.bulk.verifyConfigResults.failureResult"/>
<cti:msg var="deviceNameColumn" key="yukon.common.device.bulk.verifyConfigResults.deviceNameColumn"/>
<cti:msg var="configNameColumn" key="yukon.common.device.bulk.verifyConfigResults.configNameColumn"/>
<cti:msg var="deviceTypeColumn" key="yukon.common.device.bulk.verifyConfigResults.deviceTypeColumn"/>
<cti:msg var="verifyResultsColumn" key="yukon.common.device.bulk.verifyConfigResults.verifyResultsColumn"/>

<cti:standardPage title="${pageTitle}" module="amr">
    <cti:includeCss link="/WebConfig/yukon/styles/yukon.css"/>
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
        
        <tags:crumbLinkByMap url="/spring/bulk/config/verifyConfig" parameterMap="${deviceCollection.collectionParameters}" titleKey="yukon.common.device.bulk.verifyConfig.pageTitle" />
        
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
    
    <c:choose>
        <c:when test="${not empty exceptionReason}">
            <span class="errorRed"><b>Error Occured: ${exceptionReason}</b></span>
        </c:when>
        <c:otherwise>
            <span class="okGreen"><b>Verify Completed Successfully</b></span>
		    <%-- SUCCESS --%>
		    <br><br>
		    <div class="normalBoldLabel">${successLabel} <span class="okGreen">${successCollection.deviceCount}</span></div>
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
		    <div class="normalBoldLabel">${failureLabel} <span class="errorRed">${failureCollection.deviceCount}</span></div>
		    
		    <c:if test="${failureCollection.deviceCount > 0}">
			    <div id="errorActionsDiv" style="padding:10px;">
			    
			        <%-- device collection action --%>
			        <cti:link href="/spring/bulk/collectionActions" key="yukon.common.device.commander.collectionActionOnDevicesLabel.failureResults" class="small">
			            <cti:mapParam value="${failureCollection.collectionParameters}"/>
			        </cti:link>
			        <tags:selectedDevicesPopup deviceCollection="${failureCollection}" />
			        
			    </div>
		    </c:if>
		    
		    <%-- UNSUPPORTED --%>
		    <div class="normalBoldLabel">${unsupportedLabel} <span class="errorRed">${unsupportedCollection.deviceCount}</span></div>
		    
		    <c:if test="${unsupportedCollection.deviceCount > 0}">
		        <div id="errorActionsDiv" style="padding:10px;">
		        
		            <%-- device collection action --%>
		            <cti:link href="/spring/bulk/collectionActions" key="yukon.common.device.commander.collectionActionOnDevicesLabel.failureResults" class="small">
		                <cti:mapParam value="${unsupportedCollection.collectionParameters}"/>
		            </cti:link>
		            <tags:selectedDevicesPopup deviceCollection="${unsupportedCollection}" />
		            
		        </div>
		    </c:if>
            <br>
		    <table class="resultsTable activeResultsTable" align="center">
		        <tr>
		            <th nowrap="nowrap">${deviceNameColumn}</th>
		            <th nowrap="nowrap">${configNameColumn}</th>
		            <th nowrap="nowrap">${deviceTypeColumn}</th>
		            <th nowrap="nowrap">${verifyResultsColumn}</th>
		        </tr>
		        
		        <c:forEach var="device" items="${devices}">
		            <c:choose>
		                <c:when test="${resultsMap[device].synced}">
		                    <tr>
		                        <td onclick="forwardToMeterHome(${device.deviceId});" nowrap="nowrap">${resultsMap[device].meter.name}</td>
		                        <td nowrap="nowrap">${resultsMap[device].config.name}</td>
		                        <td nowrap="nowrap">${resultsMap[device].config.type}</td>
		                        <td>${successResult}</td>
		                    </tr>
		                </c:when>
		                <c:otherwise>
		                    <tr class="lightRedBackground">
		                        <td onclick="forwardToMeterHome(${device.deviceId});" nowrap="nowrap">${resultsMap[device].meter.name}</td>
		                        <td nowrap="nowrap">${resultsMap[device].config.name}</td>
		                        <td nowrap="nowrap">${resultsMap[device].config.type}</td>
		                        <td>${failureResult} ${resultsMap[device].discrepancies}</td>
		                    </tr>
		             </c:otherwise>
		            </c:choose>
		        </c:forEach>
		    </table>
        </c:otherwise>
    </c:choose>
</cti:standardPage>