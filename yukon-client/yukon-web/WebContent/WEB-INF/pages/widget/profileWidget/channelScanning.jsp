<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%-- ERROR MSG --%>
<c:if test="${not empty errorMsg}">
    <div style="font-weight:bold;color:#BB0000">${errorMsg}</div>
    <br>
</c:if>

<%--CHANNElS PROFILING--%>
<input type="hidden" name="channelNum" id="channelNum" value="">
<input type="hidden" name="newToggleVal" id="newToggleVal" value="">
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