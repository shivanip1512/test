<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.amr.meterPoints">
<table class="compact-results-table"> 
    <thead> 
        <tr> 
            <tags:sort column="${ATTRIBUTE}"></tags:sort>
            <tags:sort column="${POINTNAME}"></tags:sort>
            <th></th>
            <th><i:inline key="yukon.common.value"/></th> 
            <th><i:inline key="yukon.common.timestamp"/></th> 
            <th><i:inline key="yukon.common.quality"/></th>
            <tags:sort column="${POINTTYPE}"></tags:sort> 
            <tags:sort column="${POINTOFFSET}"></tags:sort> 
            <th></th>
        </tr> 
    </thead>
    <tfoot></tfoot>
    <tbody>
        <c:forEach var="pointResultRow" items="${points}">
            <tr>
                <td>
                    <c:choose>
                        <c:when test="${empty pointResultRow.attribute}"><i:inline key="yukon.common.na"/></c:when> 
                        <c:otherwise><i:inline key="${pointResultRow.attribute}"/></c:otherwise> 
                    </c:choose>
                </td>
                <td>${fn:escapeXml(pointResultRow.pointName)}</td>
                <td class="state-indicator">
                   <c:if test="${pointResultRow.paoPointIdentifier.pointIdentifier.pointType.status}">
                       <cti:pointStatus pointId="${pointResultRow.pointId}" />
                   </c:if>
                </td>
                <td><cti:pointValue pointId="${pointResultRow.pointId}" format="VALUE_UNIT"/></td> 
                <td><tags:historicalValue pao="${pao}" pointId="${pointResultRow.pointId}"/></td> 
                <td><cti:pointValue pointId="${pointResultRow.pointId}" format="{quality}"/></td> 
                <td><i:inline key="${pointResultRow.paoPointIdentifier.pointIdentifier.pointType}"/></td>
                <td>${fn:escapeXml(pointResultRow.paoPointIdentifier.pointIdentifier.offset)}</td> 
            </tr>
        </c:forEach>
    </tbody>
</table>
</cti:msgScope>