<%@ attribute name="deviceId" required="true" type="java.lang.Long"%>
<%@ attribute name="peakDate" required="false" type="java.lang.String"%>
<%@ attribute name="startDate" required="false" type="java.lang.String"%>
<%@ attribute name="stopDate" required="false" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="profileRequestOrigin" required="true" type="java.lang.String"%>
<%@ attribute name="isReadable" required="true" type="java.lang.Boolean"%>

<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<cti:uniqueIdentifier prefix="pdp_" var="id"/>
<cti:includeScript link="/JavaScript/longLoadProfile.js"/>
<cti:includeScript link="/JavaScript/peakDayProfile.js"/>

<%-- The link --%>
<c:if test="${isReadable}">
    <a style="position:relative;" class="${styleClass}" href="javascript:peakDayProfile_toggleDisplay('${id}')"><jsp:doBody/></a>
</c:if>

<div style="position:relative;z-index:2;">
<div id="${id}_holder" style="width:380px; display:none; position:absolute; left:-60px; bottom:5px; background-color:white; padding:.5em; border:1px #888 solid;">
    
    <div style="padding-bottom: 6px; text-align:right;">
        <a class="${styleClass}" href="javascript:peakDayProfile_toggleDisplay('${id}', '${profileRequestOrigin}');">Close</a>
    </div>
    
    <input type="hidden" id="${id}_deviceId" value="${deviceId}">
    <input type="hidden" id="${id}_peakDate" value="${peakDate}">
    <input type="hidden" id="${id}_startDate" value="${startDate}">
    <input type="hidden" id="${id}_stopDate" value="${stopDate}">
    
    <table class="miniResultsTable" style="width:100%;">
    
        <%--  HEADERS --%>
        <tr style="text-align:center; white-space:nowrap;">
            <th>Days Before Peak</th>
            <th>Peak Day</th>
            <th>Days After Peak</th>
        </tr>
        
        <%--  ADJUST +/- OFF PEAK DATE --%>
        <tr>
            <td style="text-align:center;">
                <select id="${id}_beforeDays">
                    <option value="0" selected>0</option>
                    <option value="1">1</option>
                    <option value="2">2</option>
                    <option value="3">3</option>
                    <option value="all">All</option>
                </select>
            </td>
            <td style="text-align:center;">
                ${peakDate}
            </td>
            <td style="text-align:center;">
                <select id="${id}_afterDays">
                    <option value="0" selected>0</option>
                    <option value="1">1</option>
                    <option value="2">2</option>
                    <option value="3">3</option>
                    <option value="all">All</option>
                </select>
            </td>
        </tr>
        
        <%-- EMAIL --%>
        <tr>
            <td colspan="3">
                <table class="noStyle">
                    <tr>
                        <td nowrap>
                            Email: <input id="${id}_email" type="text" size="32">
                        </td>
                        <td>
                            <button id="${id}_startButton" type="button" onclick="peakDayProfile_start('${id}', '${profileRequestOrigin}')">Start</button>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2">
                              <div id="${id}_errors" class="formErrorSummary"></div>
                              <div id="${id}_pendingHolder" style="display:none">
                              <tags:hideReveal title="Pending requests in progress" showInitially="true">
                                <ol id="${id}_pendingList" style="margin: 0 0 10px 0; padding: 0 0 0 40px" ></ol>
                              </tags:hideReveal>
                              </div>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        
    </table>
   
    
</div>
</div>










