<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="support" page="rfnTest">

<script type="text/javascript">
jQuery(function() {
   jQuery('#startupNotif').click(function(event) {
       jQuery.ajax({
           url: '/support/development/rfn/resendStartup',
           type: "POST"
       });
   });
   jQuery('#calcStressTest').click(function(event) {
       jQuery.ajax({
           url: '/support/development/rfn/calcStressTest',
           type: "POST"
       });
   });
   jQuery('#clearCache').click(function(event) {
       jQuery.ajax({
           url: '/support/development/rfn/clearCache',
           type: "POST"
       });
   });
});
</script>
<div class="column-8-16">
        <div class="column one">
            <ul>
                <li><a href="viewMeterReadArchiveRequest"><i:inline key=".meterReadArchiveRequest.label"/></a></li>
                <li><a href="viewEventArchiveRequest"><i:inline key=".eventArchiveRequest.label"/></a></li>
                <li><a href="viewLcrArchiveRequest"><i:inline key=".lcrArchiveRequest.label"/></a></li>
                <li><a href="viewLcrReadArchiveRequest"><i:inline key=".lcrReadArchiveRequest.label"/></a></li>
            </ul>
        </div>
        <div class="column two nogutter">
            <div class="action-area">
                <button id="clearCache">Clear Cache</button>
                <button id="calcStressTest" class="action red"><span class="b-label">Calc Stress Test, DO NOT CLICK</span></button>
                <button id="startupNotif">Resend Startup Notif</button>
        </div>
</div>
</cti:standardPage>