<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:if test="${empty definitionName}">
    <c:set var="definitionName" value="groupCommanderFailureResultDefinition"/>
</c:if>

<c:choose>
    <c:when test="${fn:length(result.resultHolder.failedDevices) > 0}">
    
        <div class="buffered">
            Tabular Data: 
            <cti:simpleReportLinkFromNameTag definitionName="${definitionName}" viewType="extView" resultKey="${result.key}" module="amr" showMenu="true">HTML</cti:simpleReportLinkFromNameTag>
            |
            <cti:simpleReportLinkFromNameTag definitionName="${definitionName}" viewType="csvView" resultKey="${result.key}">CSV</cti:simpleReportLinkFromNameTag>
            |
            <cti:simpleReportLinkFromNameTag definitionName="${definitionName}" viewType="pdfView" resultKey="${result.key}">PDF</cti:simpleReportLinkFromNameTag>
        </div>
        
        <table class="compact-results-table">
            <thead>
                <tr>
                    <th>Device Name</th>
                    <th>Error Description</th>
                    <th>Error Code</th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach items="${result.resultHolder.failedDevices}" var="device">
                
                    <c:set value="${result.resultHolder.errors[device]}" var="error"/>
                    <tr>
                        <td>
                            <div><cti:deviceName device="${device}"/></div>
                        </td>
                        <td>
                            ${error.description} ${error.porter}
                        </td>
                        <td>
                            (${error.errorCode})
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:when>
    
    <c:otherwise>None</c:otherwise>
    
</c:choose>