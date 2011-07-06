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


<%--CHANNElS PROFILING--%>
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
    
        <c:if test="${c.channelProfilingOn}">
            <cti:msg2 var="actionDesc" key=".stop"/>
            <cti:msg2 var="on" key=".on"/>
            <c:set var="scanning" value='<div style="font-weight:bold;color:#339900;display:inline;">${on}</div>'></c:set>
        </c:if>
        <c:if test="${not c.channelProfilingOn}">
            <cti:msg2 var="actionDesc" key=".start"/>
            <cti:msg2 var="off" key=".off"/>
            <c:set var="scanning" value='<div style="font-weight:bold;color:#BB0000;display:inline;">${off}</div>'></c:set>
        </c:if>
    
        <tr align="left" valign="top">
            <td>${c.channelDescription}</td>
            <td>${c.channelProfileRate}</td>
            <td>
                <table cellspacing="0" cellpadding="0">
                    
                    <c:choose>
                    <c:when test="${empty c.jobInfos}">
                        <tr>
                            <td>${scanning}</td>
                            <td><i:inline key=".never" arguments="${actionDesc}"/></td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="jobInfo" items="${c.jobInfos}" varStatus="status">
                            <c:choose>
                                <c:when test="${status.count == 1}">
                                    <tr>
                                        <td>${scanning}</td>
                                </c:when>
                                <c:otherwise>
                                    <tr><td>&nbsp;</td>
                                </c:otherwise>
                            </c:choose>
                        
                            <c:choose>
                                <c:when test="${jobInfo.newToggleVal}">
                                    <td>
                                        <cti:formatDate value="${jobInfo.startTime}" type="DATE" var="formattedScheduleDate" />
                                        <i:inline key=".startsDate" arguments="${formattedScheduleDate}"/>
                                        <cti:formatDate value="${jobInfo.startTime}" type="TIME" var="formattedScheduleDate" />
                                        <i:inline key=".startsTime" arguments="${formattedScheduleDate}"/>
                                    
                                    </td>
                                </c:when>
                                <c:otherwise>
                                    <td>
                                        <cti:formatDate value="${jobInfo.startTime}" type="DATE" var="formattedScheduleDate" />
                                        <i:inline key=".stopsDate" arguments="${formattedScheduleDate}"/>
                                        <cti:formatDate value="${jobInfo.startTime}" type="TIME" var="formattedScheduleDate" />
                                        <i:inline key=".stopsTime" arguments="${formattedScheduleDate}"/>
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