<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- ERROR MSG --%>
<cti:checkRolesAndProperties value="METERING">
<cti:checkRolesAndProperties value="PROFILE_COLLECTION_SCANNING">
    <c:set var="hasScanningRoleProperty" value="true"/>
</cti:checkRolesAndProperties>
</cti:checkRolesAndProperties>

<div class="dn" id="confirm-popup" data-dialog data-target="#toggle-state"
    data-title= "<cti:msg2 key=".scanning.popupTitle"/>"
    data-width="400" data-event="yukon.toggle.click">
       <cti:msg2 key=".scanning.popupMessage"/>  
</div>

<div id="unknown-interval-help-popup" class="dn" data-title="<cti:msg2 key=".scanning.unknown.interval.title"/>">
    <i:inline key=".scanning.unknown.interval.helpText"/>
</div>

<%--CHANNELS PROFILING--%>
<input type="hidden" name="channelNum" id="channelNum" value="">
<input type="hidden" name="newToggleVal" id="newToggleVal" value="">
<table class="compact-results-table no-stripes">
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
     <%-- This is for PLC devices --%>
    <c:if test="${not isRfn}">
        <tr align="left" valign="top">
            <td>${c.channelDescription}</td>
            <td>${c.channelProfileRate}</td>
            <td>
                <table>
                    
                    <c:choose>
                    <c:when test="${empty c.jobInfos}">
                        <tr style="border:none">
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
        </c:if>
        
        <%-- This is for RFN devices --%>
        <c:if test="${isRfn}">
           <tr align="left" valign="top">
           <td>${c.channelDescription}</td>
                <c:if test="${c.channelProfilingStatus eq 'UNKNOWN'}">
                    <td><cti:icon icon="icon-loading-bars"/></td>
                    <td><cti:icon icon="icon-loading-bars"/></td>
                    <td><cti:icon icon="icon-loading-bars"/></td>
                 </c:if>
                 
                 <c:if test="${c.channelProfilingStatus eq 'ENABLED'}">
                    <td class="wsnw">${fn:escapeXml(c.channelProfileRate)}
                        <c:if test="${not c.channelProfileRateKnown}">
                           <cti:icon icon="icon-help" classes="cp fn vatb" data-popup="#unknown-interval-help-popup" data-popup-toggle=""/>
                        </c:if>
                     </td>
                    <td>
                    <span><strong class="success"><i:inline key="yukon.common.on"/></strong></span>
                    <span>
                        <cti:formatDate value="${c.channelStopDate}" type="FULL" var="formattedScheduleDate" />
                        <i:inline key=".scanning.stops" arguments="${formattedScheduleDate}"/>
                    </span>
                    </td>
                     <td><a href="#" id='toggle-state' class="js-toggle-profile-off"><cti:msg2 key="yukon.common.stop"/></a></td>
                 </c:if>
                  
                 <c:if test="${c.channelProfilingStatus eq 'DISABLED'}">
                     <td class="wsnw">${fn:escapeXml(c.channelProfileRate)}
                     <c:if test="${not c.channelProfileRateKnown}">
                            <cti:icon icon="icon-help" classes="cp fn vatb" data-popup="#unknown-interval-help-popup" data-popup-toggle=""/>
                     </c:if>
                     </td>
                     <td>
                          <span><strong class="error"><i:inline key="yukon.common.off"/></strong></span>
                     </td>
                     <td><a href="#" id='toggle-state' class="js-toggle-profile-on"><cti:msg2 key="yukon.common.start"/></a></td>
                 </c:if>
        </tr>
        </c:if>
    </c:forEach>
    </tbody>
</table>