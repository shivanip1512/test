<%@ attribute name="labelKey" required="true" type="java.lang.String"%>
<%@ attribute name="inProgressKey" required="true" type="java.lang.String"%>
<%@ attribute name="completeKey" required="true" type="java.lang.String"%>

<%--  updateCallback is the name of  js function that will be passed the values of --%>
<%--  completedItems and totalItems as parameters after they have been used to update the progress description --%>
<%@ attribute name="updateKey" required="true" type="java.lang.String"%>
<%@ attribute name="totalCount" required="true" type="java.lang.Integer"%>
<%@ attribute name="updateCallback" required="false" type="java.lang.String"%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:uniqueIdentifier var="bulkResultProgressId" prefix="brp_"/>

<cti:msg var="bulkResultProgressLabel" key="${labelKey}" />
<cti:msg var="bulkResultInProgress" key="${inProgressKey}" />
<cti:msg var="bulkResultComplete" key="${completeKey}" />

<script type="text/javascript">
        
    function bulkResultProgressUpdateCallback(completedItems, totalItems) {
        
        setProgressMsg(completedItems, totalItems, '${updateCallback}');
    }
        
    function setProgressMsg(completedCount, totalCount, updateCallback) {
        
        if (completedCount < totalCount) {
            $('progressDescription_${bulkResultProgressId}').innerHTML = '${bulkResultInProgress}';
        }
        else {
            $('progressDescription_${bulkResultProgressId}').innerHTML = '${bulkResultComplete}';
        }
        
        if (updateCallback != '') {
            eval(updateCallback + '(' + completedCount + ', ' + totalCount + ');');
        }
    }

</script>

<span class="normalBoldLabel">${bulkResultProgressLabel}: </span><span id="progressDescription_${bulkResultProgressId}">${bulkResultInProgress}</span>

<div style="padding:10px;">

    <tags:updateableProgressBar updateCallback="bulkResultProgressUpdateCallback" totalCount="${totalCount}" updateKey="${updateKey}" />

    <jsp:doBody />
   
</div>