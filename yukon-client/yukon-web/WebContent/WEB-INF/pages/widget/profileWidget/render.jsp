<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:includeScript link="/JavaScript/calendarControl.js"/>
<cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>

<script>
	//reinit the datepickers
	jQuery(function(){
		Yukon.ui.dateTimePickers.init();
	});
</script>

<c:choose>
<c:when test="${not empty reportQueryString}">
    <script language="JavaScript" type="text/javascript">
        Yukon.ui.blockPage();
        parent.window.location = "${reportQueryString}";
    </script>
    <i:inline key=".retrievingReport"/>
</c:when>

<c:otherwise>
    <script type="text/javascript"> 
        var scanningUpdater = null;
        
        function toggleChanPopup(popupDivName) {
            
            $(popupDivName).toggle();
            
            // stop the ajax so it doesn't reload the popup as we're using it
            if ($(popupDivName).visible()) {
                scanningUpdater.stop();
            }
            else {
                scanningUpdater.start();
            }
        }
        
        function doToggleScanning(channelNum, newToggleVal) {
            $('channelNum').value = channelNum;
            $('newToggleVal').value = newToggleVal;
            ${widgetParameters.jsWidget}.doDirectActionRefresh("toggleProfiling");
        }
    </script>
    <%-- CHANNEL SCANNING --%>
    <c:if test="${not empty toggleErrorMsg}">
        <cti:msg2 var="errorToggleChannel" key=".errorToggleChannel"/>
        <tags:hideReveal title="${errorToggleChannel}" styleClass="errorMessage" escapeTitle="true" showInitially="true">
            <div class="errorMessage">${toggleErrorMsg}</div>
        </tags:hideReveal>
    </c:if>
    
    <c:set var="channelScanDiv" value="${widgetParameters.widgetId}_channelScanning"/>
    <div id="${channelScanDiv}"></div>
    <script>
        var refreshCmd = 'refreshChannelScanningInfo';
        var refreshParams = {'deviceId':${deviceId}};
        var refreshPeriod = 90;
        scanningUpdater = ${widgetParameters.jsWidget}.doPeriodicRefresh(refreshCmd,
                                                                         refreshParams, refreshPeriod,
                                                                         '${channelScanDiv}');
    </script>
    
    <%--PAST PROFILES, don't display if the device does not support --%>
    <cti:checkRole role="operator.MeteringRole.ROLEID">
    <cti:checkProperty property="operator.MeteringRole.PROFILE_COLLECTION">
        <br/>
    	<table class="compactResultsTable">
    		<tr>
    			<th colspan="3" align="left"><i:inline key=".requestPastProfile"/></th>
    		</tr>
    	
    		<tr>
    			<td class="label"><i:inline key=".channel"/></td>
    			<td colspan="2">
    				<select name="channel" style="height:20px;">
    					<c:forEach var="channel" items="${availableChannels}">
    		   				<option value="${channel.channelNumber}">${channel.channelDescription}</option>
    		   			</c:forEach>
    		   		</select>
    		   	</td>
    		</tr>
    		
    		<tr>
    			<td class="label"><i:inline key=".startDate"/></td>
    			<td colspan="2">
    				<dt:date name="startDateStr" value="${startDate}" />
    			</td>
    		</tr>
    		
    		<tr>
    			<td class="label"><i:inline key=".stopDate"/></td>
    			<td colspan="2">
    				<dt:date name="stopDateStr" value="${stopDate}" />
    			</td>
    		</tr>
    		
    		<tr>
    			<td class="label"><i:inline key=".email"/></td>
    			<td class="last">
    				<input id="email" name="email" type="text" value="${email}" size="25" style="height:16px;">
    			</td>
    	  	    <td class="last" align="right">
    	            <tags:widgetActionRefresh method="initiateLoadProfile" nameKey="start"/>
                </td>
    		</tr>
    	</table>
    
        <c:if test="${not empty errorMsgRequest}">
            <cti:msg2 var="errorPastProfile" key=".errorPastProfile"/>
            <tags:hideReveal title="${errorPastProfile}" styleClass="errorMessage" escapeTitle="true" showInitially="true">
                <c:forEach items="${errorMsgRequest}" var="errorMsg" varStatus="msgNum">
                    <div class="errorMessage">${errorMsg}</div>
                </c:forEach>
            </tags:hideReveal>
        </c:if>
        <br>
    </cti:checkProperty>
    </cti:checkRole>
    
    <%-- DAILY USAGE REPORT --%>
    <form id="reportForm" action="/widget/profileWidget/viewDailyUsageReport">
    
    <input type="hidden" name="deviceId" value="${deviceId}">
    
    <table class="compactResultsTable">
    	<tr>
    		<th colspan="5" align="left"><i:inline key=".dailyUsageReport"/></th>
    	</tr>
    
        <tr>
            <td class="label"><i:inline key=".startDate"/></td>
            <td>
                <dt:date name="dailyUsageStartDate" value="${dailyUsageStartDate}" />
            </td>
            <td class="label"><i:inline key=".stopDate"/></td>
            <td>
                <dt:date name="dailyUsageStopDate" value="${dailyUsageStopDate}" />
            </td>
            <td class="last" align="right">
                <tags:widgetActionRefresh method="viewDailyUsageReport" nameKey="viewReport"/>
            </td>
        </tr>
        <tr>
            <td colspan="5">
            </td>
        </tr>
    	
    </table>
    <c:if test="${not empty errorMsgDailyUsage}">
        <cti:msg2 var="errorDailyReport" key=".errorDailyReport"/>
        <tags:hideReveal title="${errorDailyReport}" styleClass="errorMessage" escapeTitle="true" showInitially="true">
            <c:forEach items="${errorMsgDailyUsage}" var="errorMsg" varStatus="msgNum">
                <div class="errorMessage">${errorMsg}</div>
            </c:forEach>
        </tags:hideReveal>
    </c:if>
    </form>
</c:otherwise>
</c:choose>
