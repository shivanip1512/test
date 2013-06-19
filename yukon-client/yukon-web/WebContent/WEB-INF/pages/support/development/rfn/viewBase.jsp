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
<div class="column_8_16">
        <div class="column one">
            <tags:sectionContainer2 nameKey="tests" styleClass="rfnTestContainer">
                <div><a href="viewMeterReadArchiveRequest"><i:inline key=".meterReadArchiveRequest.label"/></a></div>
                <div><a href="viewEventArchiveRequest"><i:inline key=".eventArchiveRequest.label"/></a></div>
                <div><a href="viewLcrArchiveRequest"><i:inline key=".lcrArchiveRequest.label"/></a></div>
                <div><a href="viewLcrReadArchiveRequest"><i:inline key=".lcrReadArchiveRequest.label"/></a></div>
            </tags:sectionContainer2>
        </div>
        <div class="column two nogutter">
            <tags:sectionContainer title="Actions">
                <div><button id="startupNotif">Resend Startup Notif</button></div>
                <div><button id="calcStressTest" class="danger">Calc Stress Test</button><span class="error">DO NOT CLICK</span></div>
                <div><button id="clearCache">Clear Cache</button></div>
            </tags:sectionContainer>
        </div>
</div>
</cti:standardPage>