<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%-- THIS JSP IS MEANT TO BE USED IN CONJUNCTION WITH THE pendngLoadProfileRequests.tag --%>
<%-- The tag contains the javascript to update the profile progress, and the div which will contain this jsp content --%>
<%-- That div is seperated from this jsp fragment so that it can be updated by other means within the page the tag is used --%>
<%-- It assumes only that a pendingRequests list has been made available, and an errorMsg (if any) --%>

<%-- ID OF THE DIV THAT CONTAINS THE REQUESTS CONTENT TO BE REFRESHED--%>
<cti:uniqueIdentifier var="divId" prefix="pendingProfiles_"/>

<script type="text/javascript">
    
    // REFRESHER
    var refreshCmd = 'refreshPending';
    var refreshParams = {'deviceId':${deviceId}, 'evalScripts': true};
    var refreshPeriod = 6;
    var refresher = ${widgetParameters.jsWidget}.doPeriodicRefresh(refreshCmd, refreshParams, refreshPeriod);
    
    // CANCEL FUNCTION
    function cancelLoadProfile(requestId){
    
        // stop - send request to refreshPending() with a stopRequestId parameter
        var stopUrl = '/spring/widget/pendingProfilesWidget/refreshPending';
        var stopParams = $H({
            'deviceId': ${deviceId},
            'stopRequestId': requestId
        });
        
        new Ajax.Updater('${divId}',stopUrl, {
          'parameters': stopParams,
          'evalScripts': true,
          'onSuccess': function(response) {
                       },
          'onException': function(response) {
                       }
        });
    }   
</script>

<%-- INITIALLY INCLUDE ONGOING JSP --%>    
<div id="${divId}">
    <cti:url var="ongoingProfilesUrl" value="/WEB-INF/pages/widget/pendingProfilesWidget/ongoingProfiles.jsp" />
    <jsp:include page="${ongoingProfilesUrl}"/>
</div>