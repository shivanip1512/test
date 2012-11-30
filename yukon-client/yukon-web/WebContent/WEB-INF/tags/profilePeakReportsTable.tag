<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="id" required="true" type="java.lang.String"%>
<%@ attribute name="title" required="true" type="java.lang.String"%>

<%@ attribute name="preResult" required="true" type="java.lang.Object"%>
<%@ attribute name="postResult" required="true" type="java.lang.Object"%>
            
<div id="${id}">

    <c:if test="${! empty preResult || ! empty postResult}">
    <table>
        <tr>
            <td>
                <div style="font-size:11px;font-weight:bold;padding-top:10px;padding-bottom:5px;">${title}</div>
                
                <table class="resultsTable">
                        <thead>
                            <tr>
                                <th>Period</th>
                                <th>Avg Daily / <br /> Total Usage</th>
                                <th>Peak ${preResult.peakType.displayName}</th>
                                <th>Peak Day Total Usage</th>
                            </tr>
                        </thead>
                        <tfoot></tfoot>
                    <tbody>
                        <c:if test="${! empty preResult}">
                            <c:choose>
                                <c:when test="${!preResult.noData && preResult.deviceError == ''}">
                                    <tr>
                                        <td>
                                            <cti:formatDate value="${preResult.rangeStartDate}" type="DATE" /> - 
                                            <cti:formatDate value="${preResult.rangeStopDate}" type="DATE_MIDNIGHT_PREV" />  
                                        </td>
                                        <td>
                                            ${preResult.averageDailyUsage} / ${preResult.totalUsage} kWH
                                        </td>
                                        <td>
                                            ${preResult.peakValue}
                                        </td>
                                        <td>
                                            ${preResult.usage} kWH
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="4">
                                        There was an error reading the meter<br>
                                        <c:forEach items="${preResult.errors}" var="error">
                                            <tags:hideReveal title="${error.description} (${error.errorCode})" showInitially="false">
                                            ${error.porter}<br>
                                            ${error.troubleshooting}<br>
                                            </tags:hideReveal><br>
                                        </c:forEach>
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </c:if>
        
                        <c:if test="${!empty postResult}">
                            <c:choose>
                                <c:when test="${!postResult.noData && postResult.deviceError == ''}">
                                    <tr>
                                        <td>
                                            <cti:formatDate value="${postResult.rangeStartDate}" type="DATE" /> - 
                                            <cti:formatDate value="${postResult.rangeStopDate}" type="DATE_MIDNIGHT_PREV" />
                                        </td>
                                        <td nowrap>
                                            ${postResult.averageDailyUsage} / ${postResult.totalUsage} kWH
                                        </td>
                                        <td>
                                            ${postResult.peakValue}
                                        </td>
                                        <td>
                                            ${postResult.usage} kWH
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="4">
                                        There was an error reading the meter<br>
                                        <c:forEach items="${postResult.errors}" var="error">
                                            <tags:hideReveal title="${error.description} (${error.errorCode})" showInitially="false">
                                            ${error.porter}<br>
                                            ${error.troubleshooting}<br>
                                            </tags:hideReveal><br>
                                        </c:forEach>
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                    </tbody>
                </table>
            </td>
        </tr>
    </table>
    </c:if>
    
</div>