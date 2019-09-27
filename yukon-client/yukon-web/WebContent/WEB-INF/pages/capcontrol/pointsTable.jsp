<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:forEach var="pointMap" items="${points}">
    <c:if test="${not empty pointMap.value}">
        <h4><i:inline key="yukon.common.point.pointType.${pointMap.key}"/></h4>
        <table class="compact-results-table row-highlighting">
            <thead></thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="point" items="${pointMap.value}">
                    <tr>
                        <td>
                            <c:set var="isPointLinkVisible" value= "false" />
                            <cti:checkRolesAndProperties value="MANAGE_POINTS" level="RESTRICTED">
                                <c:set var="isPointLinkVisible" value= "true" />
                            </cti:checkRolesAndProperties>
                            <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                                <c:set var="isPointLinkVisible" value= "true" />
                            </cti:checkRolesAndProperties>
                            <c:choose>
                                <c:when test="${!isPointLinkVisible}">${fn:escapeXml(point.name)}</c:when>
                                <c:otherwise>
                                    <cti:url var="pointUrl" value="/tools/points/${point.pointId}" /> 
                                    <a href="${pointUrl}">${fn:escapeXml(point.name)}</a>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="state-indicator">
                            <c:choose>
                                <c:when test="${point.format == '{rawValue|lastControlReason}'}">
                                    <cti:pointStatus pointId="${point.pointId}" format="{rawValue|lastControlReasonColor}"/>
                                </c:when>
                                <c:when test="${point.format == '{rawValue|ignoredControlReason}'}">
                                    <cti:pointStatus pointId="${point.pointId}" format="{rawValue|ignoredControlReasonColor}"/>
                                </c:when>
                                <c:otherwise>
                                    <cti:pointStatus pointId="${point.pointId}" statusPointOnly="${true}"/>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="wsnw">
                            <cti:pointValue pointId="${point.pointId}" format="${point.format}" />
                       </td>
                       <td class="wsnw">
                            <tags:historicalValue pao="${area}" pointId="${point.pointId}" />
                       </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
</c:forEach>