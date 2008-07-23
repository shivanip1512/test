<%@ attribute name="labelKey" required="true" type="java.lang.String"%>
<%@ attribute name="inProgressKey" required="true" type="java.lang.String"%>
<%@ attribute name="completeKey" required="true" type="java.lang.String"%>

<%@ attribute name="updateKey" required="true" type="java.lang.String"%>
<%@ attribute name="totalCount" required="true" type="java.lang.Integer"%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/bulkDataUpdaterCallbacks.js"/>
<cti:uniqueIdentifier var="pDescId" prefix="pgd_"/>

<cti:msg var="bulkResultProgressLabel" key="${labelKey}" />
<cti:msg var="bulkResultInProgress" key="${inProgressKey}" />
<cti:msg var="bulkResultComplete" key="${completeKey}" />

<span class="normalBoldLabel">${bulkResultProgressLabel}: </span><span id="progressDescription_${pDescId}">${bulkResultInProgress}</span>

<div style="padding:10px;">

    <tags:updateableProgressBar totalCount="${totalCount}" updateKey="${updateKey}" />

    <jsp:doBody />
   
</div>

<cti:dataUpdaterCallback function="updateProgressBar('${pDescId}', ${totalCount}, '${bulkResultComplete}')" initialize="true" completedCount="${updateKey}" />