<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="dev" page="rfnTest">

<script type="text/javascript">

yukon.namespace('yukon.dev.rfn');

/**
 * Module to handle behavior on the rfn dev page (localhost:8080/yukon/dev/rfn/viewBase)
 * @module yukon.dev.rfn
 * @requires JQUERY
 * @requires yukon
 */
yukon.dev.rfn = (function () {

    'use strict';
    
    var
    _initialized = false,
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            $('#startup-notif').click(function (ev) {
                $.ajax({
                    url: yukon.url('/dev/rfn/resend-startup'),
                }).done(function () {
                    console.log('sent archive startup successfully :)');
                }).fail(function () {
                    console.log('could not send archive startup :(');
                });
            });
            
            $('#calc-stress-test').click(function (ev) {
                $.ajax({
                    url: yukon.url('/dev/rfn/calc-stress-test'),
                    type: "POST"
                });
            });
            
            $('#clear-gateway-cache').click(function (ev) {
                $.ajax({
                    url: yukon.url('/dev/rfn/clear-gateway-cache'),
                    type: "POST"
                });
            });
            
            _initialized = true;
        }
    
    };
    
    return mod;
})();
 
$(function () { yukon.dev.rfn.init(); });

</script>
<div class="column-12-12 clearfix">
    <div class="column one">
        <h3>Tools</h3>
        <ul class="simple-list stacked">
            <li><a href="decipher/view"><i:inline key=".pointMappingDecipher.label"/></a></li>
            <li><a href="icd/view"><i:inline key=".pointMappingIcd.label"/></a></li>
        </ul>   
        <h3>Requests</h3>
        <ul class="simple-list stacked">
            <li><a href="viewDeviceArchiveRequest"><i:inline key=".deviceArchiveRequest.label"/></a></li>
            <li><a href="viewEventArchiveRequest"><i:inline key=".eventArchiveRequest.label"/></a></li>
            <li><a href="viewLcrReadArchiveRequest"><i:inline key=".lcrReadArchiveRequest.label"/></a></li>
            <li><a href="viewMeterReadArchiveRequest"><i:inline key=".meterReadArchiveRequest.label"/></a></li>
            <li><a href="viewLocationArchiveRequest"><i:inline key=".rfLocationArchiveRequest.label"/></a></li>
            <li><a href="viewConfigNotification"><i:inline key=".rfnConfigNotification.label"/></a></li>
            <li><a href="viewStatusArchiveRequest"><i:inline key=".statusArchiveRequest.label"/></a></li>
        </ul>
        <h3>Simulators</h3>
                <ul class="simple-list stacked">
            <li><a href="viewDataStreamingSimulator"><i:inline key=".dataStreamingSimulator.label"/></a>
            <li><a href="viewLcrDataSimulator"><i:inline key=".lcrDataSimulator.label"/></a></li>
            <li><a href="viewMappingSimulator"><i:inline key=".mappingSimulator.label"/></a>
            <li><a href="gatewaySimulator"><i:inline key=".gatewaySimulator.label"/></a></li>
            <li><a href="viewRfnMeterSimulator"><i:inline key=".rfnMeterSimulator.label"/></a></li>
        </ul>
        <div class="page-action-area stacked">
            <cti:button id="calc-stress-test" label="Calc Stress Test, DO NOT CLICK" classes="action red"/>
        </div>
        <div class="page-action-area stacked">
            <cti:button id="startup-notif" label="Resend Startup Notif"/>
            <cti:button id="clear-gateway-cache" label="Clear Gateway Cache"/>
        </div>
    </div>
</div>

</cti:standardPage>