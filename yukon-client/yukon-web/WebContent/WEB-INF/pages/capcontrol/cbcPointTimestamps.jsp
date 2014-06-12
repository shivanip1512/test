<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import="com.cannontech.core.roleproperties.YukonRoleProperty"%>

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
                <c:forEach var="point" items="${pointMap.ANALOG}">
                    <tr>
                        <td><spring:escapeBody htmlEscape="true">${point.pointName}</spring:escapeBody></td>
                        <td>
                            <c:choose>
                                <c:when test="${point.pointName == 'Firmware Version'}">
                                   <cti:pointValue pointId="${point.pointID}" format="{rawValue|com.cannontech.cbc.util.CapControlUtils.convertToFirmwareVersion}"/>
                                </c:when>
                                <c:when test="${point.pointName == 'IP Address'}">
                                    <cti:pointValue pointId="${point.pointID}" format="{rawValue|com.cannontech.cbc.util.CapControlUtils.convertToOctalIp}"/>
                                </c:when>
                                <c:when test="${point.pointName == 'Neutral Current Sensor'}">
                                    <cti:pointValue pointId="${point.pointID}" format="{rawValue|com.cannontech.cbc.util.CapControlUtils.convertNeutralCurrent}"/>
                                </c:when>
                                <c:when test="${point.pointName == 'Serial Number'}">
                                    <cti:pointValue pointId="${point.pointID}" format="{rawValue|com.cannontech.cbc.util.CapControlUtils.convertSerialNumber}"/>
                                </c:when>
                                <c:when test="${point.pointName == 'UDP Port'}">
                                    <cti:pointValue pointId="${point.pointID}" format="{rawValue|com.cannontech.cbc.util.CapControlUtils.convertSerialNumber}"/>
                                </c:when>
                                <c:when test="${point.stateGroupID != 0}">
                                    <cti:pointStatus pointId="${point.pointID}" />
                                    <cti:pointValue pointId="${point.pointID}" format="{state}"/>
                                </c:when>
                                <c:otherwise>
                                    <cti:pointValue pointId="${point.pointID}" format="SHORT"/>
                                </c:otherwise>
                            </c:choose>
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
                        <td><spring:escapeBody htmlEscape="true">${point.pointName}</spring:escapeBody></td>
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
                        <td><spring:escapeBody htmlEscape="true">${point.pointName}</spring:escapeBody>
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
                        <td><spring:escapeBody htmlEscape="true">${point.pointName}</spring:escapeBody>
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
                            <td><spring:escapeBody htmlEscape="true">${point.pointName}</spring:escapeBody>
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