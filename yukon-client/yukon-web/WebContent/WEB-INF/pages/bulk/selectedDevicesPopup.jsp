<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${not empty resultsLimitedTo}">
    <div class="error" style="width:95%;text-align:right;">
        <cti:msg key="yukon.common.device.bulk.selectedDevicesResultsLimited" arguments="${resultsLimitedTo}" />
    </div>
    <br>
</c:if>

<c:choose>
	<c:when test="${fn:length(deviceInfoList) == 0}">
	    <div class="errorMessage">
	        <i:inline key="yukon.common.device.bulk.selectedDevicesEmpty"/>
	    </div>
	</c:when>
<c:otherwise>
<table class="compactResultsTable">
    
    <c:forEach var="deviceInfoMap" items="${deviceInfoList}" varStatus="status">
    
        <c:if test="${status.count == 1}">
        
            <tr>
                <c:forEach var="info" items="${deviceInfoMap}">
                    <th>
                        ${info.key}
                    </th>
                </c:forEach>
            </tr>
        
        </c:if>
    
        <tr>
            <c:forEach var="info" items="${deviceInfoMap}">
                <td style="text-align:left;">${info.value}</td>
            </c:forEach>
        </tr>
        
    </c:forEach>
</table>
</c:otherwise>
</c:choose>