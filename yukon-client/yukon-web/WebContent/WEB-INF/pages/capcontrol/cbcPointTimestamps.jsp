<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:msgScope paths="modules.capcontrol.cbcPoints">

    <div style="overflow-y:auto;overflow-x:hidden;height:500px;">

        <table class="compactResultsTable rowHighlighting cbcPointTable" style="width:100%">
            <tr>
                <th><i:inline key=".analogPoints"/></th>
                <th><i:inline key=".value"/></th>
                <th><i:inline key=".timestamp"/></th>
            </tr>
            
            <c:forEach var="point" items="${pointMap.ANALOG}">
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <td><spring:escapeBody htmlEscape="true">${point.pointName}</spring:escapeBody></td>
                    <td>
                        <c:choose>
                            <c:when test="${point.pointName == 'IP Address'}">
                                <cti:pointValue pointId="${point.pointID}" format="{rawValue|com.cannontech.core.dao.impl.CapControlDaoImpl.convertToOctalIp}"/>
                            </c:when>
                            <c:when test="${point.pointName == 'Neutral Current Sensor'}">
                                <cti:pointValue pointId="${point.pointID}" format="{rawValue|com.cannontech.core.dao.impl.CapControlDaoImpl.convertNeutralCurrent}"/>
                            </c:when>
                            <c:otherwise>
                                <cti:pointValue pointId="${point.pointID}" format="VALUE"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td><cti:pointValue pointId="${point.pointID}" format="DATE"/></td>
                </tr>
            </c:forEach>
        </table>
        
        <br>
        
        <table class="compactResultsTable rowHighlighting cbcPointTable">
            <tr>
                <th><i:inline key=".accumulatorPoints"/></th>
                <th><i:inline key=".value"/></th>
                <th><i:inline key=".timestamp"/></th>
            </tr>
                
            <c:forEach var="point" items="${pointMap.ACCUMULATOR}">
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <td><spring:escapeBody htmlEscape="true">${point.pointName}</spring:escapeBody></td>
                    <td><cti:pointValue pointId="${point.pointID}" format="VALUE"/></td>
                    <td><cti:pointValue pointId="${point.pointID}" format="DATE"/></td>
                </tr>
            </c:forEach>
        </table>
        
        <br>
        
        <table class="compactResultsTable rowHighlighting cbcPointTable">
            
            <tr>
                <th><i:inline key=".statusPoints"/></th>
                <th><i:inline key=".state"/></th>
                <th><i:inline key=".timestamp"/></th>
            </tr>
            
            <c:forEach var="point" items="${pointMap.STATUS}">
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <td><spring:escapeBody htmlEscape="true">${point.pointName}</spring:escapeBody>
                    <td><cti:pointValue pointId="${point.pointID}" format="VALUE"/></td>
                    <td><cti:pointValue pointId="${point.pointID}" format="DATE"/></td>
                </tr>
            </c:forEach>
        </table>
        
        <br>
        
        <table class="compactResultsTable rowHighlighting cbcPointTable">
            
            <tr>
                <th><i:inline key=".configParams"/></th>
                <th><i:inline key=".value"/></th>
                <th><i:inline key=".timestamp"/></th>
            </tr>
            
            <c:forEach var="point" items="${pointMap.CONFIGURABLE_PARAMETERS}">
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <td><spring:escapeBody htmlEscape="true">${point.pointName}</spring:escapeBody>
                    <td><cti:pointValue pointId="${point.pointID}" format="VALUE"/></td>
                    <td><cti:pointValue pointId="${point.pointID}" format="DATE"/></td>
                </tr>
            </c:forEach>
        </table>
        
        <c:if test="${fn:length(pointMap.MISC) > 0 }">
            <br>
        
            <table class="compactResultsTable rowHighlighting cbcPointTable">
                
                <tr>
                    <th><i:inline key=".misc"/></th>
                    <th><i:inline key=".value"/></th>
                    <th><i:inline key=".timestamp"/></th>
                </tr>
                
                <c:forEach var="point" items="${pointMap.MISC}">
                    <tr class="<tags:alternateRow odd="" even="altRow"/>">
                        <td><spring:escapeBody htmlEscape="true">${point.pointName}</spring:escapeBody>
                        <td><cti:pointValue pointId="${point.pointID}" format="VALUE"/></td>
                        <td><cti:pointValue pointId="${point.pointID}" format="DATE"/></td>
                    </tr>
                </c:forEach>
            </table>
        </c:if>
    </div>
    
<tags:dataUpdateEnabler/>
</cti:msgScope>