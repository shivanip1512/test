<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%-- ERROR MSG --%>
<cti:checkRole role="operator.MeteringRole.ROLEID">
<cti:checkProperty property="operator.MeteringRole.PROFILE_COLLECTION_SCANNING">
    <c:set var="hasScanningRoleProperty" value="true"/>
</cti:checkProperty>
</cti:checkRole>


<%--CHANNELS PROFILING--%>
<input type="hidden" name="channelNum" id="channelNum" value="">
<input type="hidden" name="newToggleVal" id="newToggleVal" value="">
<table class="compactResultsTable">
    <tr align="left">
      <th align="left"><i:inline key=".channel"/></th>
      <th><i:inline key=".interval"/></th>
      <th><i:inline key=".collectionState"/></th>
      <c:if test="${hasScanningRoleProperty}">
        <th><i:inline key=".action"/></th>
      </c:if>
    </tr>
    
    <c:forEach var="c" items="${availableChannels}">
        <tr align="left" valign="top">
            <td>${c.channelDescription}</td>
            <td>${c.channelProfileRate}</td>
            <td>
                <table cellspacing="0" cellpadding="0">
                    
                    <c:choose>
                    <c:when test="${empty c.jobInfos}">
                        <tr>
                            <c:if test="${c.channelProfilingOn}">
                                <td><div class="channelOn"><i:inline key="yukon.web.defaults.on"/></div></td>
                                <td><i:inline key=".neverStops"/></td>
                            </c:if>
                            <c:if test="${not c.channelProfilingOn}">
                                <td><div class="channelOff"><i:inline key="yukon.web.defaults.off"/></div></td>
                                <td><i:inline key=".neverStarts"/></td>
                            </c:if>                        
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="jobInfo" items="${c.jobInfos}" varStatus="status">
                            <c:choose>
                                <c:when test="${status.count == 1}">
                                    <tr>
                                        <c:if test="${c.channelProfilingOn}">
                                            <td><div class="channelOn"><i:inline key="yukon.web.defaults.on"/></div></td>
                                        </c:if>
                                        <c:if test="${not c.channelProfilingOn}">
                                            <td><div class="channelOff"><i:inline key="yukon.web.defaults.off"/></div></td>
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
                                        <i:inline key=".starts" arguments="${formattedScheduleDate}"/>
                                    </td>
                                </c:when>
                                <c:otherwise>
                                    <td>
                                        <cti:formatDate value="${jobInfo.startTime}" type="FULL" var="formattedScheduleDate" />
                                        <i:inline key=".stops" arguments="${formattedScheduleDate}"/>
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
</table>