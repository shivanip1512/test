<%@ attribute name="totalCount" required="true" type="java.lang.Integer"%>
<%@ attribute name="countKey" required="true" type="java.lang.String"%>
<%@ attribute name="progressLabelTextKey" required="true" type="java.lang.String"%>
<%@ attribute name="statusTextKey" required="true" type="java.lang.String"%>
<%@ attribute name="statusClassKey" required="false" type="java.lang.String"%>
<%@ attribute name="isAbortedKey" required="false" type="java.lang.String"%>
<%@ attribute name="hideCount" required="false" type="java.lang.String"%>
<%@ attribute name="completionCallback" required="false" type="java.lang.String" description="Name of a javascript function to call when progress reaches 100%. The function will be called each iteration of the data updater, so the function must manage being called multiple times if needed."%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/bulkDataUpdaterCallbacks.js"/>
<cti:uniqueIdentifier var="pDescId" prefix="pgd_"/>

<span class="normalBoldLabel"><cti:msg key="${progressLabelTextKey}" />: </span>

<c:choose>
	<c:when test="${not empty pageScope.statusClassKey}">
		<cti:classUpdater key="${pageScope.statusClassKey}">
			<span cannonClassUpdater="${pageScope.statusClassKey}" ><span id="progressStatus_${pDescId}"></span></span>
		</cti:classUpdater>
	</c:when>
	<c:otherwise>
		<span id="progressStatus_${pDescId}"></span>
	</c:otherwise>
</c:choose>

<div style="padding:10px;">

    <tags:updateableProgressBar totalCount="${totalCount}" countKey="${countKey}" isAbortedKey="${pageScope.isAbortedKey}" hideCount="${pageScope.hideCount}" completionCallback="${pageScope.completionCallback}" />

    <jsp:doBody />
   
</div>

<cti:dataUpdaterCallback function="updateProgressStatus('${pDescId}')" initialize="true" statusText="${statusTextKey}" />