<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:choose>
    <c:when test="${readable}">
        
        <%--REQUEST--%>
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".channel">
                <select name="channel">
                    <c:forEach var="channelInfo" items="${availableChannels}">
                        <option value="${channelInfo.channelNumber}" ${channelInfo.selected}>${channelInfo.channelDescription}</option>
                    </c:forEach>
                </select>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".reportType">
                <select name="peakType">
                    <c:forEach var="peakTypeInfo" items="${availablePeakTypes}">
                        <option value="${peakTypeInfo.peakType}" ${peakTypeInfo.selected}>${peakTypeInfo.peakTypeDisplayName}</option>
                    </c:forEach>
                </select>
            </tags:nameValue2>
            <tags:nameValue2 nameKey="yukon.web.defaults.dateRange">
                <dt:dateRange startName="startDateStr" 
                              startValue="${startDate}"
                              endName="stopDateStr"
                              endValue="${stopDate}"/>
            </tags:nameValue2 >
        </tags:nameValueContainer2>
        <div class="action-area">
            <tags:widgetActionUpdate method="requestReport" nameKey="getReport" container="${widgetParameters.widgetId}_results"/>  
        </div>
        
        <%--RESULTS--%>
        <div id="${widgetParameters.widgetId}_results">
            <cti:url var="peakSummaryReportResultUrl" value="/WEB-INF/pages/widget/peakReportWidget/peakSummaryReportResult.jsp"/>
            <jsp:include page="${peakSummaryReportResultUrl}" />
        </div>
    </c:when>
    <c:otherwise>
        <i:inline key=".notAuthorized"/>
    </c:otherwise>
</c:choose>
