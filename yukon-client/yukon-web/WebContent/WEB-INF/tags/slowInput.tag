<%@ attribute name="myFormId" required="true" type="java.lang.String" %>
<%@ attribute name="label" required="true" type="java.lang.String"%>
<%@ attribute name="labelBusy" required="true" type="java.lang.String"%>
<%@ attribute name="description" required="false" type="java.lang.String"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<cti:includeScript link="/JavaScript/slowInput.js"/>

<cti:uniqueIdentifier var="uniqueId" prefix="slowinput_"/>

<span id="slowInputSpan${uniqueId}"> 
    <input id="slowInputButton${uniqueId}" type="button" value="${label}" onclick="updateButton('slowInputSpan${uniqueId}', '${labelBusy}...', '${myFormId}','slowInputProcessImg${uniqueId}');" >
    <span id="slowInputWaitingSpan${uniqueId}" class="slowInput_waiting" style="display:none"> 
        <img id="slowInputProcessImg${uniqueId}" src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" alt="waiting"> 
        <br />
        <span class="internalSectionHeader${uniqueId}">${description}</span>
    </span> 
</span>
