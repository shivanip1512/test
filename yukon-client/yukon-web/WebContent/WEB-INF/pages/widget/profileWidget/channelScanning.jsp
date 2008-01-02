<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%-- ERROR MSG --%>


<%--CHANNElS PROFILING--%>
<input type="hidden" name="channelNum" id="channelNum" value="">
<input type="hidden" name="newToggleVal" id="newToggleVal" value="">
<table class="compactResultsTable">
    <tr align="left">
      <th align="left">Channel</th>
      <th>Interval</th>
      <th>Collection State</th>
      <th>Action</th>
    </tr>
    
    <c:forEach var="c" items="${availableChannels}">
    
        <c:if test="${c.channelProfilingOn}">
            <c:set var="actionDesc" value="Stop"/>
            <c:set var="scanning" value='<div style="font-weight:bold;color:#339900;display:inline;">On</div>'></c:set>
        </c:if>
        <c:if test="${not c.channelProfilingOn}">
            <c:set var="actionDesc" value="Start"/>
            <c:set var="scanning" value='<div style="font-weight:bold;color:#BB0000;display:inline;">Off</div>'></c:set>
        </c:if>
    
        <tr align="left" valign="top">
            <td>${c.channelDescription}</td>
            <td>${c.channelProfileRate}</td>
            <td>
                <table cellspacing="0" cellpadding="0">
                    
                    <c:choose>
                    <c:when test="${empty c.jobInfos}">
                       <tr><td>${scanning}</td><td>(Never ${actionDesc}s)</td></tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="jobInfo" items="${c.jobInfos}" varStatus="status">
                        
                            <c:choose>
                                <c:when test="${status.count == 1}">
                                    <tr><td>${scanning}</td>
                                </c:when>
                                <c:otherwise>
                                    <tr><td>&nbsp;</td>
                                </c:otherwise>
                            </c:choose>
                        
                        
                            <cti:formatDate value="${jobInfo.startTime}" type="DATEH_AP" var="formattedScheduleDate" />
                            <c:choose>
                                <c:when test="${jobInfo.newToggleVal}">
                                    <td>(Starts ${formattedScheduleDate})</td>
                                </c:when>
                                <c:otherwise>
                                    <td>(Stops ${formattedScheduleDate})</td>
                                </c:otherwise>
                            </c:choose>
                            
                            </tr>
                            
                        </c:forEach>
                    </c:otherwise>
                    </c:choose>
                </table>
            </td>
            
            <td><tags:toggleProfilingPopup channelNum="${c.channelNumber}" newToggleVal="${not c.channelProfilingOn}"/></td>
        </tr>
    </c:forEach>
</table>