<%@ attribute name="myFormId" required="true" type="java.lang.String" %>
<%@ attribute name="label" required="true" type="java.lang.String"%>
<%@ attribute name="labelBusy" required="true" type="java.lang.String"%>
<%@ attribute name="description" required="false" type="java.lang.String"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<cti:includeScript link="/JavaScript/slowInput.js"/>

<span id="slowInput"> 
    <input type="button" value="${label}" onclick="updateButton('slowInput', '${labelBusy}...', '${myFormId}');" >
    <span class="slowInput_waiting" style="display:none"> 
        <img id="processImg" src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" alt="waiting"> 
        <br />
        <span class="internalSectionHeader">${description}</span>
    </span> 
</span>
