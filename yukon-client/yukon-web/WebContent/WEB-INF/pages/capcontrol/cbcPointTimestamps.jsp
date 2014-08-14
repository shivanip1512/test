<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="modules.capcontrol.cbcPoints">

    <div style="overflow-y:auto;overflow-x:hidden;">

        <table class="compact-results-table row-highlighting cbcPointTable stacked">
            <thead>
                <tr>
                    <th><i:inline key=".analogPoints"/></th>
                    <th><i:inline key=".value"/></th>
                    <th><i:inline key=".timestamp"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="formatForAnalogPoint" items="${formatForAnalogPoints}">
                    <c:set var="point" value="${formatForAnalogPoint.key}" />
                    <c:set var="format" value="${formatForAnalogPoint.value}" />
                    <tr>
                        <td>${fn:escapeXml(point.pointName)}</td>
                        <td>
                            <c:if test="${point.stateGroupID != 0}">
                              <cti:pointStatus pointId="${point.pointID}" />
                            </c:if>
                            <cti:pointValue pointId="${point.pointID}" format="${format}"/>
                        </td>
                        <td><cti:pointValue pointId="${point.pointID}" format="DATE"/></td>
                    </tr>
                </c:forEach>
            </tbody>            
        </table>
        
        <table class="compact-results-table row-highlighting cbcPointTable stacked">
            <thead>
                <tr>
                    <th><i:inline key=".accumulatorPoints"/></th>
                    <th><i:inline key=".value"/></th>
                    <th><i:inline key=".timestamp"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="point" items="${pointMap.ACCUMULATOR}">
                    <tr>
                        <td>${fn:escapeXml(point.pointName)}</td>
                        <td><cti:pointValue pointId="${point.pointID}" format="SHORT"/></td>
                        <td><cti:pointValue pointId="${point.pointID}" format="DATE"/></td>
                    </tr>
                </c:forEach>
            </tbody>    
        </table>
        
        <table class="compact-results-table row-highlighting cbcPointTable stacked">
            <thead>
                <tr>
                    <th><i:inline key=".statusPoints"/></th>
                    <th><i:inline key=".state"/></th>
                    <th><i:inline key=".timestamp"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="point" items="${pointMap.STATUS}">
                    <tr>
                        <td>${point.pointName}</td>
                        <td class="b">
                            <cti:pointStatus pointId="${point.pointID}" />
                            <cti:pointValue pointId="${point.pointID}" format="VALUE"/>
                        </td>
                        <td><cti:pointValue pointId="${point.pointID}" format="DATE"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        
        <table class="compact-results-table row-highlighting cbcPointTable stacked">
            <thead>
                <tr>
                    <th><i:inline key=".configParams"/></th>
                    <th><i:inline key=".value"/></th>
                    <th><i:inline key=".timestamp"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="point" items="${pointMap.CONFIGURABLE_PARAMETERS}">
                    <tr>
                        <td>${fn:escapeXml(point.pointName)}</td>
                        <td><cti:pointValue pointId="${point.pointID}" format="SHORT"/></td>
                        <td><cti:pointValue pointId="${point.pointID}" format="DATE"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        
        <c:if test="${fn:length(pointMap.MISC) > 0 }">
            <table class="compact-results-table row-highlighting cbcPointTable stacked">
                <thead>
                    <tr>
                        <th><i:inline key=".misc"/></th>
                        <th><i:inline key=".value"/></th>
                        <th><i:inline key=".timestamp"/></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="point" items="${pointMap.MISC}">
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