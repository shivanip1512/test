<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="id" required="true" type="java.lang.String"%>
<%@ attribute name="title" required="true" type="java.lang.String"%>

<%@ attribute name="preResult" required="true" type="java.lang.Object"%>
<%@ attribute name="postResult" required="true" type="java.lang.Object"%>

<%@ attribute name="displayName" required="true" type="java.lang.String"%>
<%@ attribute name="prePeriodStartDate" required="true" type="java.util.Date"%>
<%@ attribute name="prePeriodStopDate" required="true" type="java.util.Date"%>
<%@ attribute name="prePeriodStartDateDisplay" required="true" type="java.lang.String"%>
<%@ attribute name="prePeriodStopDateDisplay" required="true" type="java.lang.String"%>
<%@ attribute name="prePeakValue" required="true" type="java.lang.String"%>

<%@ attribute name="postPeriodStartDate" required="true" type="java.util.Date"%>
<%@ attribute name="postPeriodStopDate" required="true" type="java.util.Date"%>
<%@ attribute name="postPeriodStartDateDisplay" required="true" type="java.lang.String"%>
<%@ attribute name="postPeriodStopDateDisplay" required="true" type="java.lang.String"%>
<%@ attribute name="postPeakValue" required="true" type="java.lang.String"%>
            
<div id="${id}">

    <c:if test="${! empty preResult || ! empty postResult}">
    <table>
        <tr>
            <td>
                <div style="font-size:11px;font-weight:bold;padding-top:10px;padding-bottom:5px;">${title}</div>
                
                <table class="miniResultsTable" width="100%">
                    <tr>
                        <th>
                            Period
                        </th>
                        <th>
                            Avg Daily / <br /> Total Usage
                        </th>
                        <th>
                            Peak ${displayName}
                        </th>
                        <th>
                            Peak Day Total Usage
                        </th>
                    </tr>
                        <c:if test="${! empty preResult}">
                            <c:choose>
                                <c:when test="${!preResult.noData && preResult.deviceError == ''}">
                                    <tr>
                                        <td>
                                            ${prePeriodStartDateDisplay} - ${prePeriodStopDateDisplay}
                                        </td>
                                        <td>
                                            ${preResult.averageDailyUsage} / ${preResult.totalUsage} kWH
                                        </td>
                                        <td>
                                            ${prePeakValue}
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
                                            ${postPeriodStartDateDisplay} - ${postPeriodStopDateDisplay}
                                        </td>
                                        <td nowrap>
                                            ${postResult.averageDailyUsage} / ${postResult.totalUsage} kWH
                                        </td>
                                        <td>
                                            ${postPeakValue}
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
                </table>
            </td>
        </tr>
    </table>
    </c:if>
    
</div>