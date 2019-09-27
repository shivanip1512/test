<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.operator.rtuDetail">
        
    <cti:url var="action" value="/stars/rtu/${rtuId}/allPoints"/>
    <form:form id="rtuAllPoints" modelAttribute="filter" action="${action}" method="get">
        <i:inline key="yukon.common.filterBy"/>
        <cti:msg2 var="selectDevices" key=".selectDevices"/>
        <tags:selectWithItems items="${devices}" path="deviceIds" itemLabel="name" itemValue="id"  dataPlaceholder="${selectDevices}" inputClass="js-chosen"/>
        <cti:msg2 var="selectPoints" key=".selectPoints"/>
        <tags:selectWithItems items="${pointNames}" path="pointNames" dataPlaceholder="${selectPoints}" inputClass="js-chosen"/>
        <cti:msg2 var="selectTypes" key=".selectTypes"/>
        <tags:selectWithItems items="${pointTypes}" path="types" dataPlaceholder="${selectTypes}" inputClass="js-chosen"/>
        <cti:button nameKey="filter" classes="action primary js-filter fn vab"/>
    </form:form>
    
    <hr/>
    
    <c:choose>
        <c:when test="${empty details.resultList}">
            <i:inline key=".noPoints"/>
        </c:when>
        <c:otherwise>
            
            <table class="compact-results-table row-highlighting">
                <tr>
                    <tags:sort column="${pointName}" />
                    <th></th>
                    <th><i:inline key=".pointValue"/></th>
                    <th><i:inline key=".dateTime"/></th>
                    <tags:sort column="${offset}" />
                    <tags:sort column="${deviceName}" />
                    <tags:sort column="${pointType}" />
                </tr>
                <tbody>
                    <c:forEach var="point" items="${details.resultList}">
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
                                <c:when test="${!isPointLinkVisible}">${fn:escapeXml(point.pointName)}</c:when>
                                <c:otherwise>
                                    <cti:url var="pointUrl" value="/tools/points/${point.pointId}" />
                                    <a href="${pointUrl}">${fn:escapeXml(point.pointName)}</a>
                                </c:otherwise>
                            </c:choose>
                                
                            </td>
                            <td class="state-indicator">
                                <c:choose>
                                    <c:when test="${point.format == '{rawValue|lastControlReason}'}">
                                        <cti:pointStatus pointId="${point.pointId}"
                                             format="{rawValue|lastControlReasonColor}" />
                                    </c:when>
                                    <c:when test="${point.format == '{rawValue|ignoredControlReason}'}">
                                        <cti:pointStatus pointId="${point.pointId}"
                                            format="{rawValue|ignoredControlReasonColor}" />
                                    </c:when>
                                    <c:otherwise>
                                        <cti:pointStatus pointId="${point.pointId}"
                                            statusPointOnly="${true}" />
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="wsnw">
                                <cti:pointValue pointId="${point.pointId}" format="${point.format}" />
                            </td>
                            <td class="wsnw">
                                <tags:historicalValue pao="${rtu}" pointId="${point.pointId}" />
                            </td>
                            <td>
                                ${point.paoPointIdentifier.pointIdentifier.offset}
                            </td>
                            <td>
                                <cti:paoDetailUrl paoId="${point.paoPointIdentifier.paoIdentifier.paoId}">
                                    <c:if test="${!empty point.deviceName}">${fn:escapeXml(point.deviceName)}</c:if>
                                </cti:paoDetailUrl>
                            </td>
                            <td>
                                <i:inline key="${point.paoPointIdentifier.pointIdentifier.pointType}"/>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <tags:pagingResultsControls result="${details}" adjustPageCount="true" thousands="true"/>
        </c:otherwise>
    </c:choose>
    

</cti:msgScope>

<script>
    $(".js-chosen").chosen({width: "250px"});
</script>