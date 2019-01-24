<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="modules.capcontrol.cbcPoints">

    <div>

        <table class="compact-results-table stacked">
            <thead>
                <tr>
                    <th><i:inline key=".analogPoints"/></th>
                    <th></th>
                    <th><i:inline key=".value"/></th>
                    <th><i:inline key=".timestamp"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="point" items="${pointMap[cbcPointGroup.ANALOG]}">
                    <c:set var="format" value="${formatForAnalogPoints.get(point)}"/>
                    <tr>
                        <td>${fn:escapeXml(point.pointName)}</td>
                        <td class="state-indicator">
                            <c:set var="rendered" value="${false}"/>
                            <c:choose>
                                <c:when test="${format == '{rawValue|lastControlReason}'}">
                                    <cti:pointStatus pointId="${point.pointID}" format="{rawValue|lastControlReasonColor}"/>
                                </c:when>
                                <c:when test="${format == '{rawValue|ignoredControlReason}'}">
                                    <cti:pointStatus pointId="${point.pointID}" format="{rawValue|ignoredControlReasonColor}"/>
                                </c:when>
                                <c:when test="${point.stateGroupID != 0}">
                                    <cti:pointStatus pointId="${point.pointID}" />
                                </c:when>
                            </c:choose>
                        </td>
                        <td>
                            <cti:pointValue pointId="${point.pointID}" format="${format}"/>
                        </td>
                        <td><cti:pointValue pointId="${point.pointID}" format="DATE"/></td>
                    </tr>
                </c:forEach>
            </tbody>            
        </table>
        
        <c:if test="${not empty pointMap[cbcPointGroup.ACCUMULATOR]}">
            <table class="compact-results-table three-column-table stacked">
                <thead>
                    <tr>
                        <th><i:inline key=".accumulatorPoints"/></th>
                        <th><i:inline key=".value"/></th>
                        <th><i:inline key=".timestamp"/></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="point" items="${pointMap[cbcPointGroup.ACCUMULATOR]}">
                        <tr>
                            <td>${fn:escapeXml(point.pointName)}</td>
                            <td><cti:pointValue pointId="${point.pointID}" format="SHORT"/></td>
                            <td><cti:pointValue pointId="${point.pointID}" format="DATE"/></td>
                        </tr>
                    </c:forEach>
                </tbody>    
            </table>
        </c:if>
        
        <c:if test="${not empty pointMap[cbcPointGroup.STATUS]}">
            <table class="compact-results-table stacked">
                <thead>
                    <tr>
                        <th><i:inline key=".statusPoints"/></th>
                        <th></th>
                        <th><i:inline key=".state"/></th>
                        <th><i:inline key=".timestamp"/></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="point" items="${pointMap[cbcPointGroup.STATUS]}">
                        <tr>
                            <td>${fn:escapeXml(point.pointName)}</td>
                            <td class="state-indicator">
                                <cti:pointStatus pointId="${point.pointID}" />
                            </td>
                            <td>
                                <cti:pointValue pointId="${point.pointID}" format="SHORT"/>
                            </td>
                            <td><cti:pointValue pointId="${point.pointID}" format="DATE"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>        
        
        <c:if test="${not empty pointMap[cbcPointGroup.CONFIGURABLE_PARAMETERS]}">
            <table class="compact-results-table stacked">
                <thead>
                    <tr>
                        <th><i:inline key=".configParams"/></th>
                        <th><i:inline key=".value"/></th>
                        <th><i:inline key=".timestamp"/></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="point" items="${pointMap[cbcPointGroup.CONFIGURABLE_PARAMETERS]}">
                        <c:set var="format" value="${formatForConfigurablePoints.get(point)}" />
                        <tr>
                            <td>${fn:escapeXml(point.pointName)}</td>
                            <td><cti:pointValue pointId="${point.pointID}" format="${format}"/></td>
                            <td><cti:pointValue pointId="${point.pointID}" format="DATE"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
        
        <c:if test="${not empty pointMap[cbcPointGroup.MISC]}">
            <table class="compact-results-table row-highlighting stacked">
                <thead>
                    <tr>
                        <th><i:inline key=".misc"/></th>
                        <th><i:inline key=".value"/></th>
                        <th><i:inline key=".timestamp"/></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="point" items="${pointMap[cbcPointGroup.MISC]}">
                        <tr>
                            <td>${fn:escapeXml(point.pointName)}</td>
                            <td><cti:pointValue pointId="${point.pointID}" format="SHORT"/></td>
                            <td><cti:pointValue pointId="${point.pointID}" format="DATE"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
        
    </div>
    <tags:dataUpdateEnabler/>
</cti:msgScope>