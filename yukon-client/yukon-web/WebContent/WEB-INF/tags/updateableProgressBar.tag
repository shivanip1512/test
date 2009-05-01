<%--  updateCallback is the name of  js function that will be passed the values of --%>
<%--  completedItems and totalItems as parameters after they have been used to update the bar --%>
<%@ attribute name="countKey" required="true" type="java.lang.String"%>
<%@ attribute name="totalCount" required="true" type="java.lang.Integer"%>
<%@ attribute name="canceledKey" required="false" type="java.lang.String"%>
<%@ attribute name="hasExceptionKey" required="false" type="java.lang.String"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
    
<cti:includeScript link="/JavaScript/bulkDataUpdaterCallbacks.js"/>
<cti:uniqueIdentifier var="pbarId" prefix="pbar_"/>
        
<table cellpadding="0px" border="0px">
    <tr>
        <td>
            <div id="progressBorder_${pbarId}" class="progressBarBorder" align="left">
                <div id="progressInner_${pbarId}" class="progressBarInner">
                </div>
            </div>
        </td>
        <td>
            <span id="percentComplete_${pbarId}" class="progressBarPercentComplete">0%</span>
        </td>
        <td>
            <span class="progressBarCompletedCount"><span id="completedCount_${pbarId}"></span>/${totalCount}</span>
        </td>
    </tr>
</table>

<cti:dataUpdaterCallback function="updateProgressBar('${pbarId}', ${totalCount})" initialize="true" completedCount="${countKey}" />

<c:if test="${not empty canceledKey}">
    <cti:dataUpdaterCallback function="cancelProgressBar('${pbarId}')" initialize="true" isCanceled="${canceledKey}"/>
</c:if>

<c:if test="${not empty hasExceptionKey}">
    <cti:dataUpdaterCallback function="cancelProgressBar('${pbarId}')" initialize="true" hasException="${hasExceptionKey}"/>
</c:if>
