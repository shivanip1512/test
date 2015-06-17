<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%-- ERROR MSG --%>
<cti:checkRolesAndProperties value="METERING">
<cti:checkRolesAndProperties value="PROFILE_COLLECTION_SCANNING">
    <c:set var="hasScanningRoleProperty" value="true"/>
</cti:checkRolesAndProperties>
</cti:checkRolesAndProperties>


<%--CHANNELS PROFILING--%>
<input type="hidden" name="channelNum" id="channelNum" value="">
<input type="hidden" name="newToggleVal" id="newToggleVal" value="">
<table class="compact-results-table">
    <thead>
    <tr align="left">
      <th align="left"><i:inline key=".scanning.channel"/></th>
      <th><i:inline key=".scanning.interval"/></th>
      <th><i:inline key=".scanning.collectionState"/></th>
      <c:if test="${hasScanningRoleProperty}">
        <th><i:inline key=".scanning.action"/></th>
      </c:if>
    </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
    
    <c:forEach var="c" items="${availableChannels}">
        <tr align="left" valign="top">
            <td>${c.channelDescription}</td>
            <td>${c.channelProfileRate}</td>
            <td>
                <table>
                    
                    <c:choose>
                    <c:when test="${empty c.jobInfos}">
                        <tr>
                            <c:if test="${c.channelProfilingOn}">
                                <td><strong class="success"><i:inline key="yukon.common.on"/></strong></td>
                                <td><i:inline key=".scanning.neverStops"/></td>
                            </c:if>
                            <c:if test="${not c.channelProfilingOn}">
                                <td><strong class="error"><i:inline key="yukon.common.off"/></strong></td>
                                <td><i:inline key=".scanning.neverStarts"/></td>
                            </c:if>                        
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="jobInfo" items="${c.jobInfos}" varStatus="status">
                            <c:choose>
                                <c:when test="${status.count == 1}">
                                    <tr>
                                        <c:if test="${c.channelProfilingOn}">
                                            <td><strong class="success"><i:inline key="yukon.common.on"/></strong></td>
                                        </c:if>
                                        <c:if test="${not c.channelProfilingOn}">
                                            <td><strong class="error"><i:inline key="yukon.common.off"/></strong></td>
                                        </c:if>        
                                </c:when>
                                <c:otherwise>
                                    <tr><td>&nbsp;</td>
                                </c:otherwise>
                            </c:choose>
                        
                            <c:choose>
                                <c:when test="${jobInfo.newToggleVal}">
                                    <td>
                                        <cti:formatDate value="${jobInfo.startTime}" type="FULL" var="formattedScheduleDate" />
                                        <i:inline key=".scanning.starts" arguments="${formattedScheduleDate}"/>
                                    </td>
                                </c:when>
                                <c:otherwise>
                                    <td>
                                        <cti:formatDate value="${jobInfo.startTime}" type="FULL" var="formattedScheduleDate" />
                                        <i:inline key=".scanning.stops" arguments="${formattedScheduleDate}"/>
                                    </td>
                                </c:otherwise>
                            </c:choose>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                    </c:choose>
                </table>
            </td>
            
            <c:if test="${hasScanningRoleProperty}">
                <td><tags:toggleProfilingPopup channelNum="${c.channelNumber}" newToggleVal="${not c.channelProfilingOn}"/></td>
            </c:if>
        </tr>
    </c:forEach>
    </tbody>
</table>