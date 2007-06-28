<%@ attribute name="deviceId" required="true" type="java.lang.Long"%>
<%@ attribute name="startOffset" required="false" type="java.lang.Long"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>

<%@ attribute name="lpStartDate" required="false" type="java.lang.String"%>
<%@ attribute name="lpStopDate" required="false" type="java.lang.String"%>

<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<cti:uniqueIdentifier prefix="llp_" var="thisId"/>

<cti:includeScript link="/JavaScript/longLoadProfile.js"/>
<div style="position:relative">
<a id="${thisId}_link" class="${styleClass}" href="javascript:longLoadProfile_display('${thisId}')"><jsp:doBody/></a>
<span id="${thisId}_text" style="display:none; font-weight: bold"><jsp:doBody/></span>
<span id="${thisId}_indicator" style="visibility:hidden"><img src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>"></span>
<div id="${thisId}_holder" style="display:none;margin-left: 20px" class="longLoadProfileHolder">
<input id="${thisId}_startOffset" type="hidden" value="${startOffset}">
<input id="${thisId}_deviceId" type="hidden" value="${deviceId}">
  <table>
    <tr>
    	<td>
	      	<b>Start Date:</b>
	      	<c:choose>
	      		<c:when test="${lpStartDate != null}">
					<input id="${thisId}_start" type="hidden" value="${lpStartDate}">
	      			<span id="${thisId}_startDisplay"></span>
	      		</c:when>
	      		<c:otherwise>
					<input id="${thisId}_start" type="text">
	      		</c:otherwise>
	      	</c:choose>
	    </td>
	    <td>
	    	<b>Stop Date:</b>
	    	<c:choose>
	      		<c:when test="${lpStopDate != null}">
					<input id="${thisId}_stop" type="hidden" value="${lpStopDate}">
	      			<span id="${thisId}_stopDisplay"></span>
	      		</c:when>
	      		<c:otherwise>
					<input id="${thisId}_stop" type="text">
	      		</c:otherwise>
	      	</c:choose>
	    	
		</td>
	    <td>
	    	<b>Email:</b>
			<input id="${thisId}_email" type="text" size="40">
		</td>
	    <td>
			<button id="${thisId}_startButton" type="button" onclick="longLoadProfile_start('${thisId}')">Start</button>
		</td>
    </tr>
  </table>
  <div id="${thisId}_errors" class="formErrorSummary"></div>
  <div id="${thisId}_pendingHolder" style="display:none">
  <tags:hideReveal styleClass="smallText" title="Pending requests in progress" showInitially="true">
  <ol id="${thisId}_pendingList" style="margin: 0 0 10px 0; padding: 0 0 0 40px" class="smallText"></ol>
  </tags:hideReveal>
  </div>
</div>
</div>
