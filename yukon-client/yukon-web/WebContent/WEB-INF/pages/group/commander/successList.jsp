<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:choose>

    <c:when test="${fn:length(result.resultHolder.successfulDevices) > 0}">
    
        <br>
        <div style="font-size:11px;">
            Tabular Data: 
            <cti:simpleReportLinkFromNameTag definitionName="groupCommanderSuccessResultDefinition" viewType="htmlView" resultKey="${result.key}">HTML</cti:simpleReportLinkFromNameTag>
            |
            <cti:simpleReportLinkFromNameTag definitionName="groupCommanderSuccessResultDefinition" viewType="csvView" resultKey="${result.key}">CSV</cti:simpleReportLinkFromNameTag>
            |
            <cti:simpleReportLinkFromNameTag definitionName="groupCommanderSuccessResultDefinition" viewType="pdfView" resultKey="${result.key}">PDF</cti:simpleReportLinkFromNameTag>
        </div>
        
        <br>
        <table class="compactResultsTable">
        
            <th>Device Name</th>
            <th>First Point</th>
            <th>Last Result</th>
            
            <c:forEach items="${result.resultHolder.successfulDevices}" var="device">
            
                <tr>
                    <td>
                        <div><cti:deviceName device="${device}"/></div>
                    </td>
                    <td>
                        <cti:pointValueFormatter format="FULL" value="${result.resultHolder.values[device][0]}"/>
                    </td>
                    <td>
                        ${result.resultHolder.resultStrings[device]}
                    </td>
                </tr>
                
            </c:forEach>
            
        </table>
    
    </c:when>
    
    <c:otherwise>
        None
    </c:otherwise>
    
</c:choose>