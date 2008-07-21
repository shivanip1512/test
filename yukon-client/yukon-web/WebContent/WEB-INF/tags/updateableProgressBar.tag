<%--  updateCallback is the name of  js function that will be passed the values of --%>
<%--  completedItems and totalItems as parameters after they have been used to update the bar --%>
<%@ attribute name="updateKey" required="true" type="java.lang.String"%>
<%@ attribute name="totalCount" required="true" type="java.lang.Integer"%>
<%@ attribute name="updateCallback" required="false" type="java.lang.String"%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

    
<cti:includeScript link="/JavaScript/updateableProgressBar.js"/>
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

<cti:dataUpdaterCallback function="updateProgressBar('${pbarId}', ${totalCount}, '${updateCallback}')" initialize="true" completedCount="${updateKey}" />