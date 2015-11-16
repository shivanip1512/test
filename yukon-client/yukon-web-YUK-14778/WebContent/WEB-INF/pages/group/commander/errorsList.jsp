<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="yukon.common.device.groupMeterRead.resultDetail">

<c:if test="${empty definitionName}">
    <c:set var="definitionName" value="groupCommanderFailureResultDefinition"/>
</c:if>

<c:choose>
    <c:when test="${fn:length(result.resultHolder.failedDevices) > 0}">
    
        <div class="buffered">
            <i:inline key=".tabularData"/>
            <cti:simpleReportLinkFromNameTag definitionName="${definitionName}" viewType="extView" resultKey="${result.key}">
                <i:inline key="yukon.common.html"/>
            </cti:simpleReportLinkFromNameTag>|
            <cti:simpleReportLinkFromNameTag definitionName="${definitionName}" viewType="csvView" resultKey="${result.key}">
                <i:inline key="yukon.common.csv"/>
            </cti:simpleReportLinkFromNameTag>|
            <cti:simpleReportLinkFromNameTag definitionName="${definitionName}" viewType="pdfView" resultKey="${result.key}">
                <i:inline key="yukon.common.pdf"/>
            </cti:simpleReportLinkFromNameTag>
        </div>
        
        <table class="compact-results-table">
            <thead>
                <tr>
                    <th class="wsnw"><i:inline key="yukon.common.device"/></th>
                    <th><i:inline key=".errorDescription"/></th>
                    <th class="wsnw"><i:inline key=".errorCode"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach items="${result.resultHolder.failedDevices}" var="device">
                    <c:set value="${result.resultHolder.errors[device]}" var="error"/>
                    <tr>
                        <td class="wsnw"><cti:deviceName device="${device}"/></td>
                        <td>${fn:escapeXml(error.description)} ${fn:escapeXml(error.porter)}</td>
                        <td class="wsnw tar">${error.errorCode}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:when>
    
    <c:otherwise><i:inline key="yukon.common.none.choice"/></c:otherwise>
    
</c:choose>

</cti:msgScope>