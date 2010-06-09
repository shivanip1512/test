<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<cti:standardPage title="LTC Points and TS" module="capcontrol_internal">
<cti:includeCss link="/capcontrol/css/base.css"/>     
    <input id="ltcName" type="hidden" value="${ltcName}"/>
    <c:if test="${oneline}">
        <center><span style="font-weight: bold; font-size: 16;">${ltcName}</span></center>
    </c:if>
    
    <div style="overflow: auto;">
        <table class="tierTable">
            <tr style="text-align: left;">
                <th>Attribute</th>
                <th>Device</th>
                <th>Point</th>
                <th>Value</th>
                <th>Timestamp</th>
            </tr>
            
            <c:forEach var="point" items="${mappings}">
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <td>
                        ${point.attribute.description}
                    </td>
                    <td>
                        ${point.paoName}
                    </td>
                    <td>
                        ${point.pointName}
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${point.pointId > 0}">
                                <cti:pointValue pointId="${point.pointId}" format="VALUE"/>
                            </c:when>
                            <c:otherwise>
                                ---
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${point.pointId > 0}">
                                <cti:pointValue pointId="${point.pointId}" format="DATE"/>
                            </c:when>
                            <c:otherwise>
                                ---
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
            
        </table>
    </div>  
<tags:dataUpdateEnabler/>
</cti:standardPage>