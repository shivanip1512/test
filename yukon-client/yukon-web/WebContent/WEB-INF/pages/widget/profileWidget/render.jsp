<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<script>
    // Reinit the datepickers. Required after an action is take on the Meter Profile
    // page and the contents, including the date pickers, are ajaxed in.
    $(function () {
        if (typeof yukon.ui.initDateTimePickers !== 'undefined') {
            yukon.ui.initDateTimePickers();
        }
    });
</script>

<c:choose>
<c:when test="${not empty reportQueryString}">
    <script language="JavaScript" type="text/javascript">
        yukon.ui.blockPage();
        parent.window.location = "${reportQueryString}";
    </script>
    <i:inline key=".retrievingReport"/>
</c:when>

<c:otherwise>
    <script type="text/javascript"> 
        var scanningUpdater = null;
        
        function toggleChanPopup(popupDivName) {
            
            $('#' + popupDivName).toggle(200);
            
            // stop the ajax so it doesn't reload the popup as we're using it
            if ($('#' + popupDivName).is(':visible')) {
                // init these datepickers each time the popup is made visible
                yukon.ui.initDateTimePickers();
                scanningUpdater.stop();
            }
            else {
                scanningUpdater.start();
            }
        }
        
        function doToggleScanning(channelNum, newToggleVal) {
            $('#' + 'channelNum').val(channelNum);
            $('#' + 'newToggleVal').val(newToggleVal);
            ${widgetParameters.jsWidget}.doDirectActionRefresh("toggleProfiling");
        }
    </script>
     <c:if test="${not isRfn}">
    <%-- CHANNEL SCANNING --%>
    <c:if test="${not empty toggleErrorMsg}">
        <cti:msg2 var="errorToggleChannel" key=".errorToggleChannel"/>
        <tags:hideReveal title="${errorToggleChannel}" styleClass="error" escapeTitle="true" showInitially="true">
            <div class="error">${toggleErrorMsg}</div>
        </tags:hideReveal>
    </c:if>
    
    <c:set var="channelScanDiv" value="${widgetParameters.widgetId}_channelScanning"/>
    <div id="${channelScanDiv}" class="stacked"></div>
    
    <script>
        var refreshCmd = 'refreshChannelScanningInfo',
            refreshParams = {'deviceId':${deviceId}},
            refreshPeriod = 90;
        scanningUpdater = ${widgetParameters.jsWidget}.doPeriodicRefresh(refreshCmd,
                                                                         refreshParams, refreshPeriod,
                                                                         '${channelScanDiv}');
    </script>
    </c:if>
    <%--PAST PROFILES, don't display if the device does not support --%>
    <cti:checkRolesAndProperties value="METERING">
    <cti:checkRolesAndProperties value="PROFILE_COLLECTION">
        <tags:sectionContainer2 nameKey="requestPastProfile">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".channel">
                    <select name="channel">
                        <c:forEach var="channel" items="${availableChannels}">
                            <option value="${channel.channelNumber}">${channel.channelDescription}</option>
                        </c:forEach>
                    </select>
                </tags:nameValue2>
                <c:if test="${isRfn}">
                <tags:nameValue2 nameKey=".startDate">
                    <dt:date name="startDateStr" value="${startDate}" minDate="${minDate}" maxDate="${maxDate}"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".stopDate">
                    <dt:date name="stopDateStr" value="${stopDate}" minDate="${minDate}" maxDate="${maxDate}"/>
                </tags:nameValue2>
                </c:if>
                <c:if test="${not isRfn}">
                <tags:nameValue2 nameKey=".startDate">
                    <dt:date name="startDateStr" value="${startDate}"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".stopDate">
                    <dt:date name="stopDateStr" value="${stopDate}"/>
                </tags:nameValue2>
                </c:if>
                <tags:nameValue2 nameKey=".email">
                    <input id="email" name="email" type="text" value="${email}">
                </tags:nameValue2>
            
            </tags:nameValueContainer2>
            <div class="action-area">
                <tags:widgetActionRefresh method="initiateLoadProfile" nameKey="start"/>
            </div>
        </tags:sectionContainer2>

        <c:if test="${not empty errorMsgRequest}">
            <cti:msg2 var="errorPastProfile" key=".errorPastProfile"/>
            <tags:hideReveal title="${errorPastProfile}" styleClass="error" escapeTitle="true" showInitially="true">
                <c:forEach items="${errorMsgRequest}" var="errorMsg" varStatus="msgNum">
                    <div class="error">${errorMsg}</div>
                </c:forEach>
            </tags:hideReveal>
        </c:if>
    </cti:checkRolesAndProperties>
    </cti:checkRolesAndProperties>
    
    <%-- DAILY USAGE REPORT --%>
    <c:if test="${not isRfn}">
    <form id="reportForm" action="/widget/profileWidget/viewDailyUsageReport">
    
    <input type="hidden" name="deviceId" value="${deviceId}">
    
    <tags:sectionContainer2 nameKey="dailyUsageReport">
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".startDate">
                <dt:date name="dailyUsageStartDate" value="${dailyUsageStartDate}" />
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".stopDate">
                <dt:date name="dailyUsageStopDate" value="${dailyUsageStopDate}" />
            </tags:nameValue2>
        </tags:nameValueContainer2>
        <div class="action-area">
            <tags:widgetActionRefresh method="viewDailyUsageReport" nameKey="viewReport"/>
        </div>
    </tags:sectionContainer2>
    
    <c:if test="${not empty errorMsgDailyUsage}">
        <cti:msg2 var="errorDailyReport" key=".errorDailyReport"/>
        <tags:hideReveal title="${errorDailyReport}" styleClass="error" escapeTitle="true" showInitially="true">
            <c:forEach items="${errorMsgDailyUsage}" var="errorMsg" varStatus="msgNum">
                <div class="error">${errorMsg}</div>
            </c:forEach>
        </tags:hideReveal>
    </c:if>
    </form>
    </c:if>
</c:otherwise>
</c:choose>
