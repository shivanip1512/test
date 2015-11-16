<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<cti:msg var="successLabel" key="yukon.common.device.bulk.verifyConfigResults.successLabel"/>
<cti:msg var="failureLabel" key="yukon.common.device.bulk.verifyConfigResults.failureLabel"/>
<cti:msg var="unsupportedLabel" key="yukon.common.device.bulk.verifyConfigResults.unsupportedLabel"/>
<cti:msg var="successResult" key="yukon.common.device.bulk.verifyConfigResults.successResult"/>
<cti:msg var="failureResult" key="yukon.common.device.bulk.verifyConfigResults.failureResult"/>
<cti:msg var="deviceNameColumn" key="yukon.common.device.bulk.verifyConfigResults.deviceNameColumn"/>
<cti:msg var="configNameColumn" key="yukon.common.device.bulk.verifyConfigResults.configNameColumn"/>
<cti:msg var="deviceTypeColumn" key="yukon.common.device.bulk.verifyConfigResults.deviceTypeColumn"/>
<cti:msg var="verifyResultsColumn" key="yukon.common.device.bulk.verifyConfigResults.verifyResultsColumn"/>

<cti:standardPage module="tools" page="bulk.verifyConfigResults">

    <cti:url value="/meter/home" var="meterHomeUrl"/>
    
    <script type="text/javascript">
    function forwardToMeterHome(id) {
        window.location = "${meterHomeUrl}?deviceId=" + id;
    }
    </script>
    
    <c:choose>
        <c:when test="${not empty exceptionReason}">
            <span class="error"><b>Error Occured: ${exceptionReason}</b></span>
        </c:when>
        <c:otherwise>
            <span class="success"><b>Verify Completed Successfully</b></span>
		    <%-- SUCCESS --%>
		    <br><br>
		    <div class="fwb">${successLabel} <span class="success">${successCollection.deviceCount}</span></div>
		    <c:if test="${successCollection.deviceCount > 0}">
		        <div id="js-success-actions" style="padding:10px;">
		    
		        <%-- device collection action --%>
		        <cti:link href="/bulk/collectionActions" key="yukon.common.device.commander.collectionActionOnDevicesLabel.successResults" class="small">
		            <cti:mapParam value="${successCollection.collectionParameters}"/>
		        </cti:link>
		        <tags:selectedDevicesPopup deviceCollection="${successCollection}" />
		        
		    </div>
		    <br>
		    </c:if>
		    
		    <%-- FAILURE --%>
		    <div class="fwb">${failureLabel} <span class="error">${failureCollection.deviceCount}</span></div>
		    
		    <c:if test="${failureCollection.deviceCount > 0}">
			    <div id="js-error-actions" style="padding:10px;">
			    
			        <%-- device collection action --%>
			        <cti:link href="/bulk/collectionActions" key="yukon.common.device.commander.collectionActionOnDevicesLabel.failureResults" class="small">
			            <cti:mapParam value="${failureCollection.collectionParameters}"/>
			        </cti:link>
			        <tags:selectedDevicesPopup deviceCollection="${failureCollection}" />
			        
			    </div>
		    </c:if>
		    
		    <%-- UNSUPPORTED --%>
		    <div class="fwb">${unsupportedLabel} <span class="error">${unsupportedCollection.deviceCount}</span></div>
		    
		    <c:if test="${unsupportedCollection.deviceCount > 0}">
		        <div id="js-error-actions" style="padding:10px;">
		        
		            <%-- device collection action --%>
		            <cti:link href="/bulk/collectionActions" key="yukon.common.device.commander.collectionActionOnDevicesLabel.failureResults" class="small">
		                <cti:mapParam value="${unsupportedCollection.collectionParameters}"/>
		            </cti:link>
		            <tags:selectedDevicesPopup deviceCollection="${unsupportedCollection}" />
		            
		        </div>
		    </c:if>
            <br>
		    <table class="results-table" align="center">
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
		                        <td onclick="forwardToMeterHome(${device.deviceId});" nowrap="nowrap">${fn:escapeXml(resultsMap[device].device.name)}</td>
		                        <td nowrap="nowrap">${resultsMap[device].config.name}</td>
		                        <td nowrap="nowrap">${device.deviceType}</td>
		                        <td>${successResult}</td>
		                    </tr>
		                </c:when>
		                <c:otherwise>
		                    <tr class="error">
		                        <td onclick="forwardToMeterHome(${device.deviceId});" nowrap="nowrap">${fn:escapeXml(resultsMap[device].device.name)}</td>
		                        <td nowrap="nowrap">${resultsMap[device].config.name}</td>
		                        <td nowrap="nowrap">${device.deviceType}</td>
		                        <td>${failureResult} ${resultsMap[device].discrepancies}</td>
		                    </tr>
		             </c:otherwise>
		            </c:choose>
		        </c:forEach>
		    </table>
        </c:otherwise>
    </c:choose>
</cti:standardPage>