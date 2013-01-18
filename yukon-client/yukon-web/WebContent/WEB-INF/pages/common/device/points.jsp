<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="amr" page="meterPoints">
    
    <c:set var="actionsMenu">
        <cti:url var="download" value="download">
            <cti:param name="deviceId" value="${deviceId}"/>
            <cti:param name="orderBy" value="${orderBy}"/>
            <cti:param name="descending" value="${descending}"/>
        </cti:url>
        <a id="exportCsv" href="${download}" class="labeled_icon csv title-bar-tool"><i:inline key="yukon.web.components.button.csv.label"/></a>
    </c:set>
     
     <tags:boxContainer2 nameKey="currentPointValues" arguments="${deviceName}" titleLinkHtml="${actionsMenu}" hideEnabled="true" showInitially="true">
         <table class="compactResultsTable rowHighlighting"> 
             <thead> 
                 <tr> 
                     <th><tags:sortLink nameKey="columnHeader.attribute" baseUrl="${baseUrl}" fieldName="ATTRIBUTE" sortParam="orderBy" /></th> 
                     <th><tags:sortLink nameKey="columnHeader.pointName" baseUrl="${baseUrl}" fieldName="POINTNAME" sortParam="orderBy" /></th> 
                     <th class="state-indicator tar"></th>
                     <th><i:inline key=".columnHeader.value"/></th> 
                     <th><i:inline key=".columnHeader.timestamp"/></th> 
                     <th><i:inline key=".columnHeader.quality"/></th> 
                     <th><tags:sortLink nameKey="columnHeader.pointType" baseUrl="${baseUrl}" fieldName="POINTTYPE" sortParam="orderBy" /></th> 
                     <th><tags:sortLink nameKey="columnHeader.pointOffset" baseUrl="${baseUrl}" fieldName="POINTOFFSET" sortParam="orderBy"/></th> 
                     <th></th>
                 </tr> 
             </thead>
             <tfoot></tfoot>
             <tbody>
                 <c:forEach var="pointResultRow" items="${points}">
                     <tr class="<tags:alternateRow odd="" even="altRow"/>">
                         <td>
                             <c:choose>
                                 <c:when test="${empty pointResultRow.attribute}"><i:inline key="yukon.web.defaults.na"/></c:when> 
                                 <c:otherwise><i:inline key="${pointResultRow.attribute}"/></c:otherwise> 
                             </c:choose>
                         </td>
                         <td>${fn:escapeXml(pointResultRow.pointName)}</td>
                         <td class="state-indicator tar">
                            <c:if test="${pointResultRow.paoPointIdentifier.pointIdentifier.pointType.status}">
                                <cti:pointStatusColor pointId="${pointResultRow.pointId}" styleClass="box stateBox" background="true">&nbsp;</cti:pointStatusColor>
                            </c:if>
                         </td>
                         <td><cti:pointValue pointId="${pointResultRow.pointId}" format="VALUE_UNIT"/></td> 
                         <td><tags:historicalValue device="${device}" pointId="${pointResultRow.pointId}"/></td> 
                         <td><cti:pointValue pointId="${pointResultRow.pointId}" format="{quality}"/></td> 
                         <td>${fn:escapeXml(pointResultRow.paoPointIdentifier.pointIdentifier.pointType.pointTypeString)}</td> 
                         <td>${fn:escapeXml(pointResultRow.paoPointIdentifier.pointIdentifier.offset)}</td> 
                     </tr>
                 </c:forEach>
             </tbody>
         </table>
     </tags:boxContainer2>
    
</cti:standardPage>