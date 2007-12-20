<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/calendarControl.js"/>
<cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>

<script type="text/javascript"> 
	
	function toggleWhatsThis(){
		$('whatsThisText').toggle();
	}
	
	var progressUpdaters = new Array();
    
    function toggleChanPopup(chanId) {
        $(chanId).toggle();
    }
</script>

<%-- ERROR MSG --%>
<c:if test="${not empty errorMsg}">
    <div style="font-weight:bold;color:#BB0000">${errorMsg}</div>
    <br>
</c:if>

<%--CHANNElS PROFILING--%>
<table class="compactResultsTable">
	<tr align="left">
	  <th align="left">Channel (Type)</th>
	  <th>Interval</th>
	  <th>Scan</th>
      <th>Scheduled</th>
	  <th>Action</th>
	</tr>
    
    <c:forEach var="c" items="${availableChannels}">
    
        <c:if test="${c.channelProfilingOn}">
            <c:set var="actionDesc" value="Stop"/>
            <c:set var="scanTd" value='<td style="font-weight:bold;color:#339900">On</td>'></c:set>
        </c:if>
        <c:if test="${not c.channelProfilingOn}">
            <c:set var="actionDesc" value="Start"/>
            <c:set var="scanTd" value='<td style="font-weight:bold;color:#BB0000">Off</td>'></c:set>
        </c:if>
    
        <tr align="left">
            <td>${c.channelDescription}</td>
            <td>${c.channelProfileRate}</td>
            ${scanTd}
            <td>
                <c:if test="${empty c.jobInfo}">
                    No
                </c:if>
                <c:if test="${not empty c.jobInfo}">
                    <cti:formatDate value="${c.jobInfo.startTime}" type="DATEHM" var="formattedScheduleDate" />
                    ${actionDesc} ${formattedScheduleDate}
                </c:if>
            </td>
            
            <td><tags:toggleProfilingPopup channelNum="${c.channelNumber}" newToggleVal="${not c.channelProfilingOn}"/></td>
        </tr>
    </c:forEach>
</table>

<br/>
	
<%--PAST PROFILES--%>
<table class="compactResultsTable">
	<tr>
		<th colspan="3" align="left">Request Past Profile:</th>
	</tr>

	<tr>
		<td class="label">Channel:</td>
		<td colspan="2">
			<select name="channel" style="height:20px;">
				<c:forEach var="channel" items="${availableChannels}">
	   				<option value="${channel.channelNumber}">${channel.channelDescription}</option>
	   			</c:forEach>
	   		</select>
	   	</td>
	</tr>
	
	<tr>
		<td class="label">Start Date:</td>
		<td colspan="2">
			<tags:dateInputCalendar fieldName="startDateStr" fieldValue="${startDateStr}"></tags:dateInputCalendar>
		</td>
	</tr>
	
	<tr>
		<td class="label">Stop Date:</td>
		<td colspan="2">
			<tags:dateInputCalendar fieldName="stopDateStr" fieldValue="${stopDateStr}"></tags:dateInputCalendar>
		</td>
	</tr>
	
	<tr>
		<td class="label">Email:</td>
		<td class="last">
			<input id="email" name="email" type="text" value="${email}" size="25" style="height:16px;">
		</td>
		<td class="last" align="right">
			<tags:widgetActionUpdate method="initiateLoadProfile" label="Start" labelBusy="Start" container="${widgetParameters.widgetId}_results"/>
		</td>
	</tr>
	
</table>
<br/>

<%--RESULTS--%>
<div id="${widgetParameters.widgetId}_results">
    <c:url var="ongoingProfilesUrl" value="/WEB-INF/pages/widget/profileWidget/ongoingProfiles.jsp" />
    <jsp:include page="${ongoingProfilesUrl}" />
</div>
