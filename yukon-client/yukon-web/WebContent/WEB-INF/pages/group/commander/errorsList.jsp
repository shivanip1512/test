<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:choose>

    <c:when test="${fn:length(result.resultHolder.failedDevices) > 0}">
    
        <br>
        <div style="font-size:11px;">
            Tabular Data: 
            <cti:simpleReportLinkFromNameTag definitionName="groupCommanderFailureResultDefinition" viewType="htmlView" resultKey="${result.key}">HTML</cti:simpleReportLinkFromNameTag>
            |
            <cti:simpleReportLinkFromNameTag definitionName="groupCommanderFailureResultDefinition" viewType="csvView" resultKey="${result.key}">CSV</cti:simpleReportLinkFromNameTag>
            |
            <cti:simpleReportLinkFromNameTag definitionName="groupCommanderFailureResultDefinition" viewType="pdfView" resultKey="${result.key}">PDF</cti:simpleReportLinkFromNameTag>
        </div>
        
        <br>
        <table class="compactResultsTable">
        
            <th>Device Name</th>
            <th>Error Description</th>
            <th>Error Code</th>
        
            <c:forEach items="${result.resultHolder.failedDevices}" var="device">
            
                <c:set value="${result.resultHolder.errors[device]}" var="error"/>
                
                <tr>
                    <td>
                        <div><cti:deviceName device="${device}"/></div>
                    </td>
                    <td>
                        ${error.description}
                    </td>
                    <td>
                        (${error.errorCode})
                    </td>
                </tr>
                
            </c:forEach>
            
        </table>
    
    </c:when>
    
    <c:otherwise>
        None
    </c:otherwise>
    
</c:choose>