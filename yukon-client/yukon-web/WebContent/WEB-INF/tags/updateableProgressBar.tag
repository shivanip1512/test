<%--  updateCallback is the name of  js function that will be passed the values of --%>
<%--  completedItems and totalItems as parameters after they have been used to update the bar --%>
<%--  abortedKey should a boolean updater key, if true, the progress bar will be filled in red for remaining percentage --%>
<%@ attribute name="countKey" required="true" type="java.lang.String"%>
<%@ attribute name="totalCount" required="true" type="java.lang.Integer"%>
<%@ attribute name="isAbortedKey" required="false" type="java.lang.String"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
    
<cti:includeScript link="/JavaScript/bulkDataUpdaterCallbacks.js"/>
<cti:uniqueIdentifier var="pbarId" prefix="pbar_"/>
        
<table cellpadding="0px" border="0px" class="noStyle">
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

<c:if test="${not empty pageScope.isAbortedKey}">
    <cti:dataUpdaterCallback function="abortProgressBar('${pbarId}')" initialize="true" isAborted="${pageScope.isAbortedKey}"/>
</c:if>