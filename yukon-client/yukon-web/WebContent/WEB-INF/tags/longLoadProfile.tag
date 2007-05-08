<%@ attribute name="deviceId" required="true" type="java.lang.Long"%>
<%@ attribute name="startOffset" required="false" type="java.lang.Long"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<cti:uniqueIdentifier prefix="llp_" var="thisId"/>

<cti:includeScript link="/JavaScript/longLoadProfile.js"/>
<div style="position:relative">
<a class="${styleClass}" href="javascript:longLoadProfile_display('${thisId}')"><jsp:doBody/></a>
<span id="${thisId}_indicator" style="visibility:hidden"><img src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>"></span>
<div id="${thisId}_holder" style="display:none; position:absolute; background-color: white; padding: .5em; border: 1px #888 solid;z-index:2" class="longLoadProfileHolder">
<input id="${thisId}_startOffset" type="hidden" value="${startOffset}">
<input id="${thisId}_deviceId" type="hidden" value="${deviceId}">
  <table>
    <tr>
      <td>Start Date:</td>
      <td><input id="${thisId}_start" type="text"></td>
    </tr>
    <tr>
      <td>Stop Date:</td>
      <td><input id="${thisId}_stop" type="text"></td>
    </tr>
    <tr>
      <td>Email:</td>
      <td><input id="${thisId}_email" type="text"></td>
    </tr>
  </table>
  <div id="${thisId}_errors" class="formErrorSummary"></div>
  <div id="${thisId}_pendingHolder" style="display:none">
  <tags:hideReveal styleClass="smallText" title="Pending requests in progress">
  <ol id="${thisId}_pendingList" style="margin: 0 0 10px 0; padding: 0 0 0 40px" class="smallText"></ol>
  </tags:hideReveal>
  </div>
  <button id="${thisId}_startButton" type="button" onclick="longLoadProfile_start('${thisId}')">Start</button>
</div>
</div>
