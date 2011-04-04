<%@ attribute name="myFormId" required="true" type="java.lang.String" %>
<%@ attribute name="label" required="true" type="java.lang.String"%>
<%@ attribute name="labelBusy" required="false" type="java.lang.String"%>
<%@ attribute name="description" required="false" type="java.lang.String"%>
<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ attribute name="disableOtherButtons" required="false" type="java.lang.Boolean"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ attribute name="styleClass" required="false" type="java.lang.String"%>

<cti:includeScript link="/JavaScript/slowInput.js"/>

<cti:uniqueIdentifier var="uniqueId" prefix="slowinput_"/>
<c:if test="${not empty pageScope.id}">
	<c:set var="uniqueId" value="${pageScope.id}"/>
</c:if>

<c:if test="${empty pageScope.labelBusy}">
	<c:set var="labelBusy" value="${label}"/>
</c:if>

<c:if test="${empty pageScope.disableOtherButtons}">
	<c:set var="disableOtherButtons" value="false"/>
</c:if>

<span id="slowInputSpan${uniqueId}"> 

    <input id="${uniqueId}" 
    	   type="button" 
    	   value="${label}" 
           class="formSubmit ${styleClass}"
    	   onclick="updateButton('slowInputSpan${uniqueId}', '${labelBusy}...', '${myFormId}','slowInputProcessImg${uniqueId}', ${disableOtherButtons});">
    	   
    <span id="slowInputWaitingSpan${uniqueId}" class="slowInput_waiting" style="display:none;"> 
        <img id="slowInputProcessImg${uniqueId}" src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" alt="waiting"> 
        <c:if test="${not empty pageScope.description}">
	        <br>
	        <span class="internalSectionHeader${uniqueId}">${pageScope.description}</span>
        </c:if>
    </span> 
</span>
