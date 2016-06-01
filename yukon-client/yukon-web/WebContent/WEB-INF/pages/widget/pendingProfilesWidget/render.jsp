<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%-- THIS JSP IS MEANT TO BE USED IN CONJUNCTION WITH THE pendingLoadProfileRequests.tag --%>
<%-- The tag contains the javascript to update the profile progress, and the div which will contain this jsp content --%>
<%-- That div is seperated from this jsp fragment so that it can be updated by other means within the page the tag is used --%>
<%-- It assumes only that a pendingRequests list has been made available, and an errorMsg (if any) --%>

<%-- ID OF THE DIV THAT CONTAINS THE REQUESTS CONTENT TO BE REFRESHED--%>
<cti:uniqueIdentifier var="divId" prefix="pendingProfiles_"/>

<script type="text/javascript">
    // REFRESHER
    var refreshCmd = 'refreshPending';
    var refreshParams = {'deviceId':${deviceId}};
    var refreshPeriod = 10;
    var refresher = ${widgetParameters.jsWidget}.doPeriodicRefresh(refreshCmd, refreshParams, refreshPeriod);
    
    // CANCEL FUNCTION
    function cancelLoadProfile(requestId) {
        // stop - send request to refreshPending() with a stopRequestId parameter
        ${widgetParameters.jsWidget}.setParameter('stopRequestId', requestId);
        ${widgetParameters.jsWidget}.doDirectActionRefresh('refreshPending');
    }
</script>

<%-- INITIALLY INCLUDE ONGOING JSP --%>    
<div id="${divId}">
    <jsp:include page="/WEB-INF/pages/widget/pendingProfilesWidget/ongoingProfiles.jsp"/>
</div>