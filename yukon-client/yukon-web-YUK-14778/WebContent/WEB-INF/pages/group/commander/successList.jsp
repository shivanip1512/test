<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="yukon.common.device.groupMeterRead.resultDetail">

<c:if test="${empty definitionName}">
    <c:set var="definitionName" value="groupCommanderSuccessResultDefinition"/>
</c:if>

<c:choose>

    <c:when test="${fn:length(result.resultHolder.successfulDevices) > 0}">
    
        <div class="buffered">
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
                <th class="wsnw"><i:inline key="yukon.common.device"/></th>
                <th class="wsnw"><i:inline key=".firstPoint"/></th>
                <th><i:inline key=".lastResult"/></th>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach items="${result.resultHolder.successfulDevices}" var="device">
                    <tr>
                        <td class="wsnw"><cti:deviceName device="${device}"/></td>
                        <td class="wsnw tar"><cti:pointValueFormatter format="FULL" value="${result.resultHolder.values[device][0]}"/></td>
                        <td>${fn:escapeXml(result.resultHolder.resultStrings[device])}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    
    </c:when>
    
    <c:otherwise><i:inline key="yukon.common.none.choice"/></c:otherwise>
    
</c:choose>

</cti:msgScope>