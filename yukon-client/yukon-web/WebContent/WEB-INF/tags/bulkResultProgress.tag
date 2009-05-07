<%@ attribute name="totalCount" required="true" type="java.lang.Integer"%>
<%@ attribute name="countKey" required="true" type="java.lang.String"%>
<%@ attribute name="progressLabelTextKey" required="true" type="java.lang.String"%>
<%@ attribute name="statusTextKey" required="true" type="java.lang.String"%>
<%@ attribute name="statusClassKey" required="false" type="java.lang.String"%>
<%@ attribute name="isAbortedKey" required="false" type="java.lang.String"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/bulkDataUpdaterCallbacks.js"/>
<cti:uniqueIdentifier var="pDescId" prefix="pgd_"/>

<span class="normalBoldLabel"><cti:msg key="${progressLabelTextKey}" />: </span>

<c:choose>
	<c:when test="${not empty statusClassKey}">
		<cti:classUpdater key="${statusClassKey}" type="COMMANDER">
			<span cannonClassUpdater="${statusClassKey}" ><span id="progressStatus_${pDescId}"></span></span>
		</cti:classUpdater>
	</c:when>
	<c:otherwise>
		<span id="progressStatus_${pDescId}"></span>
	</c:otherwise>
</c:choose>

<div style="padding:10px;">

    <tags:updateableProgressBar totalCount="${totalCount}" countKey="${countKey}" isAbortedKey="${isAbortedKey}" />

    <jsp:doBody />
   
</div>

<cti:dataUpdaterCallback function="updateProgressStatus('${pDescId}')" initialize="true" statusText="${statusTextKey}" />