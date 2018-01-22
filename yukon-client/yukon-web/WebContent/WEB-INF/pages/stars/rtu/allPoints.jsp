<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.operator.rtuDetail">

    <cti:url var="action" value="/stars/rtu/${rtuId}/allPoints"/>
    <form:form id="rtuAllPoints" commandName="filter" action="${action}" method="get">
        <i:inline key="yukon.common.filterBy"/>
        <cti:msg2 var="selectDevices" key=".selectDevices"/>
        <tags:selectWithItems id="deviceNames" items="${devices}" path="deviceIds" itemLabel="name" itemValue="id"  dataPlaceholder="${selectDevices}"/>
        <cti:msg2 var="selectPoints" key=".selectPoints"/>
        <tags:selectWithItems id="pointNames" items="${pointNames}" path="pointNames" dataPlaceholder="${selectPoints}"/>
<%--         <select id="pointNames" multiple data-placeholder="${selectPoints}"><option>TEst</option></select> --%>
        <cti:msg2 var="selectTypes" key=".selectTypes"/>
        <tags:selectWithItems id="pointType" items="${pointTypes}" path="types" dataPlaceholder="${selectTypes}"/>
        <cti:button nameKey="filter" classes="action primary js-filter fn vab"/>
    </form:form>
    
    <hr/>
    
    <table class="compact-results-table row-highlighting">
        <tr>
            <tags:sort column="${pointName}" />
            <th></th>
            <tags:sort column="${pointValue}" />
            <tags:sort column="${dateTime}" />
            <tags:sort column="${offset}" />
            <tags:sort column="${deviceName}" />
            <tags:sort column="${pointType}" />
        </tr>
        <tbody>
            <c:forEach var="pointMap" items="${points}">
                <c:if test="${not empty pointMap.value}">
                    <c:forEach var="point" items="${pointMap.value}">
                        <tr>
                            <td>
                                <cti:url var="pointUrl" value="/tools/points/${point.pointId}" />
                                <a href="${pointUrl}">${fn:escapeXml(point.name)}</a>
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
                                ${point.pointIdentifier.offset}
                            </td>
                            <td>
                            </td>
                            <td>
                                <i:inline key="yukon.common.point.pointType.${point.type}"/>
                            </td>
                        </tr>
                    </c:forEach>
                </c:if>
            </c:forEach>
        </tbody>
    </table>
    <%-- <tags:pagingResultsControls result="${points}" adjustPageCount="true"/> --%>

</cti:msgScope>

<script>
    $("#deviceNames").chosen({width: "250px"});
    $("#pointNames").chosen({width: "250px"});
    $("#pointType").chosen({width: "250px"});
</script>