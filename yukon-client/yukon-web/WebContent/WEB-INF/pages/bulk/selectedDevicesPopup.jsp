<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<c:if test="${not empty resultsLimitedTo}">
    <div class="errorRed" style="width:95%;text-align:right;">
        <cti:msg key="yukon.common.device.bulk.selectedDevicesResultsLimited" arguments="${resultsLimitedTo}" />
    </div>
    <br>
</c:if>

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
                <td>${info.value}</td>
            </c:forEach>
        </tr>
        
    </c:forEach>
</table>