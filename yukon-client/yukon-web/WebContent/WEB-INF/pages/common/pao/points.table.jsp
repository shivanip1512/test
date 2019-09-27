<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<cti:msgScope paths="modules.amr.meterPoints,yukon.common.point">

<table class="compact-results-table has-actions"> 
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
            <cti:checkRolesAndProperties value="MANAGE_POINT_DATA" level="UPDATE">
                <th class="action-column"><cti:icon icon="icon-cog" classes="M0" /></th>
            </cti:checkRolesAndProperties>
        </tr> 
    </thead>
    <tfoot></tfoot>
    <tbody>
        <c:set var="isPointLinkVisible" value= "false" />
        <cti:checkRolesAndProperties value="MANAGE_POINTS" level="RESTRICTED">
            <c:set var="isPointLinkVisible" value= "true" />
        </cti:checkRolesAndProperties>
        <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
            <c:set var="isPointLinkVisible" value= "true" />
        </cti:checkRolesAndProperties>
        <c:forEach var="pointResultRow" items="${points}">
            <tr>
                <td>
                    <c:choose>
                        <c:when test="${empty pointResultRow.attribute}"><i:inline key="yukon.common.na"/></c:when> 
                        <c:otherwise><i:inline key="${pointResultRow.attribute}"/></c:otherwise> 
                    </c:choose>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${!isPointLinkVisible}">${fn:escapeXml(pointResultRow.pointName)}</c:when>
                        <c:otherwise>
                            <cti:url var="pointEditor" value="/tools/points/${pointResultRow.pointId}"/>
                            <a href="${pointEditor}">${fn:escapeXml(pointResultRow.pointName)}</a>
                        </c:otherwise>
                    </c:choose>
                </td>
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
                <cti:checkRolesAndProperties value="MANAGE_POINT_DATA" level="UPDATE">
                <td>
                    <cm:dropdown icon="icon-cog">
                        <cti:msg2 key=".manualEntry.title" var="title"/>
                        <cti:list var="arguments">
                            <cti:item value="${title}"/>
                            <cti:item value="${deviceName}"/>
                            <cti:item value="${pointResultRow.pointName}"/>
                        </cti:list>
                        <cti:msg2 key=".popupTitle" arguments="${arguments}" var="popupTitle"/>
                        <cm:dropdownOption key=".manualEntry.title" icon="icon-pencil"
                                           data-point-id="${pointResultRow.pointId}" data-popup-title="${popupTitle}" 
                                           classes="js-manual-entry" id="manualEntry-${pointResultRow.pointId}"/>
                    </cm:dropdown>
                </td>
                </cti:checkRolesAndProperties>
            </tr>
        </c:forEach>
    </tbody>
</table>
</cti:msgScope>
 <cti:includeScript link="/resources/js/pages/yukon.points.js"/>