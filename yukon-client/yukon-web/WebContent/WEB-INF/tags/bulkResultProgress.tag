<%@ attribute name="progressLabelTextKey" required="true" type="java.lang.String"%>
<%@ attribute name="inProgressTextKey" required="true" type="java.lang.String"%>
<%@ attribute name="completeTextKey" required="true" type="java.lang.String"%>
<%@ attribute name="canceledTextKey" required="false" type="java.lang.String"%>

<%@ attribute name="totalCount" required="true" type="java.lang.Integer"%>
<%@ attribute name="countKey" required="true" type="java.lang.String"%>

<%@ attribute name="completeKey" required="true" type="java.lang.String"%>
<%@ attribute name="canceledKey" required="false" type="java.lang.String"%>

<%@ attribute name="hasExceptionKey" required="false" type="java.lang.String"%>
<%@ attribute name="exceptionReasonKey" required="false" type="java.lang.String"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/bulkDataUpdaterCallbacks.js"/>
<cti:uniqueIdentifier var="pDescId" prefix="pgd_"/>

<cti:msg var="progressLabelText" key="${progressLabelTextKey}" />
<cti:msg var="inProgressText" key="${inProgressTextKey}" />
<cti:msg var="completeText" key="${completeTextKey}" />
<c:set var="canceledText" value=""/>
<c:if test="${not empty canceledTextKey}">
    <cti:msg var="canceledText" key="${canceledTextKey}" />
</c:if>

<span class="normalBoldLabel">${progressLabelText}: </span><span id="progressDescription_${pDescId}">${inProgressText}</span>

<div style="padding:10px;">

    <tags:updateableProgressBar totalCount="${totalCount}" countKey="${countKey}" canceledKey="${canceledKey}" hasExceptionKey="${hasExceptionKey}"/>

    <jsp:doBody />
   
</div>

<cti:dataUpdaterCallback function="updateProgressDescription('${pDescId}', '${completeText}')" initialize="true" isCompleteCondition="${completeKey}" />

<c:if test="${not empty hasExceptionKey && not empty exceptionReasonKey}">
	<cti:dataUpdaterCallback function="updateProgressDescription('${pDescId}', '')" initialize="true" hasExceptionCondition="${hasExceptionKey}" exceptionReasonText="${exceptionReasonKey}" />
</c:if>

<c:if test="${not empty canceledKey}">
    <cti:dataUpdaterCallback function="updateProgressDescription('${pDescId}', '${canceledText}')" initialize="true" isCompleteCondition="${canceledKey}" />
</c:if>