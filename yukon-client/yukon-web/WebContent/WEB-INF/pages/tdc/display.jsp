<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="tools" page="tdc.display.${mode}">

<style type="text/css">
.display-table tr:hover .f-dropdown_outer_container {visibility: visible;}
</style>

<table class="compactResultsTable rowHighlighting display-table"> 
     <thead> 
         <tr> 
<%--              <th><tags:sortLink nameKey="columnHeader.pointName" baseUrl="${baseUrl}" fieldName="POINTNAME" sortParam="orderBy" /></th>  --%>
             <th>Point</th> 
             <th class="state-indicator tar"></th>
             <th><i:inline key="yukon.common.value"/></th> 
             <th><i:inline key="yukon.common.quality"/></th> 
             <th><i:inline key="yukon.common.timestamp"/></th> 
             <th>Alarming</th>
         </tr> 
     </thead>
     <tfoot></tfoot>
     <tbody>
         <c:forEach var="row" items="${points}">
             <tr>
                 <td>${fn:escapeXml(row.pointName)}</td>
                 <td class="state-indicator tar">
                    <c:if test="${row.paoPointIdentifier.pointIdentifier.pointType.status}">
                        <cti:pointStatusColor pointId="${row.pointId}" styleClass="box stateBox" background="true">&nbsp;</cti:pointStatusColor>
                    </c:if>
                 </td>
                 <td><cti:pointValue pointId="${row.pointId}" format="VALUE_UNIT"/></td>
                 <td><cti:pointValue pointId="${row.pointId}" format="{quality}"/></td>
                 <td><tags:historicalValue device="${device}" pointId="${row.pointId}"/></td>
                 <td>
                     <span>Normal</span>
                     <cm:dropdown containerCssClass="fr vh">
                        <li><a class="clearfix"><cti:icon icon="icon-tick"/><span>Acknowledge</span></a></li>
                        <li class="divider"></li>
                        <li><a class="clearfix"><cti:icon icon="icon-pencil"/><span>Manual Entry</span></a></li>
                        <li><a class="clearfix"><cti:icon icon="icon-wrench"/><span>Manual Control</span></a></li>
                        <li class="divider"></li>
                        <li><a class="clearfix"><cti:icon icon="icon-tag-blue"/><span>Tags...</span></a></li>
                        <li><a class="clearfix"><cti:icon icon="icon-accept"/><span>Enable/Disable...</span></a></li>
                        <li><a class="clearfix"><cti:icon icon="icon-chart-line"/><span>Trend</span></a></li>
                        <li><a class="clearfix"><cti:icon icon="icon-transmit-blue"/><span>Force Alt Scan</span></a></li>
                    </cm:dropdown>
                 </td> 
             </tr>
         </c:forEach>
     </tbody>
 </table>
 
</cti:standardPage>