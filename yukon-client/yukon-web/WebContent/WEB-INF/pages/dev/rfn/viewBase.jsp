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
<div class="column-8-8-8 clearfix">
    <div class="column one">
        <h3>Tools</h3>
        <ul class="simple-list stacked">
            <li><a href="decipher/view"><i:inline key=".pointMappingDecipher.pageName"/></a></li>
            <li><a href="icd/view"><i:inline key=".pointMappingIcd.pageName"/></a></li>
        </ul>   
        <div class="page-action-area stacked">
                <cti:button id="calc-stress-test" label="Calc Stress Test, DO NOT CLICK" classes="action red"/>
            <a href="javascript:void(0);" class="dib" title="WHY NOT?!">
                <cti:icon icon="icon-help" data-popup="#calc-stress-test-help"/>
            </a>
            <div id="calc-stress-test-help" title="About Calc Stress Test" data-width="500" class="dn">
                <h3>Why shouldn't I click this?</h3>
                <p>Good question!</p>
                <p>This stress test was added in 2013 as part of <a href="https://jira-prod.tcc.etn.com/browse/YUK-9506">YUK-9506</a>.
                <p>The scary text is just a warning so you don't casually click it while exploring the dev tools, since you might then 
                wonder why everything is slow all of the sudden, why your laptop fan kicked into high gear, and/or if you need a therapeutic 
                reboot.</p>
                <p><code>RfnEventTestingServiceImpl.calculationStressTest()</code> sends 100,000 RFN meter readings as fast as possible 
                &mdash; 100 hourly kWh readings to 1,000 <code>EE A3R</code> meters (serials 10,000-10,999).</p>
                <p>If this sounds like something you want to inflict on this system, then have at it!</p>
            </div>
        </div>
        <div class="page-action-area stacked">
            <cti:button id="startup-notif" label="Resend Startup Notif"/>
        </div>
        <div class="page-action-area stacked">
            <cti:button id="clear-gateway-cache" label="Clear Gateway Cache"/>
        </div>
    </div>
    <div class="column two">
        <h3>Requests</h3>
        <ul class="simple-list stacked">
            <li><a href="viewDeviceArchiveRequest"><i:inline key=".deviceArchive.pageName"/></a></li>
            <li><a href="viewEventArchiveRequest"><i:inline key=".viewEventArchive.pageName"/></a></li>
            <li><a href="viewLcrReadArchiveRequest"><i:inline key=".lcrReadArchiveRequest.pageName"/></a></li>
            <li><a href="viewMeterReadArchiveRequest"><i:inline key=".viewMeterReadArchive.pageName"/></a></li>
            <li><a href="viewLocationArchiveRequest"><i:inline key=".rfLocationArchiveRequest.pageName"/></a></li>
            <li><a href="viewConfigNotification"><i:inline key=".rfnConfigNotification.pageName"/></a></li>
            <li><a href="viewStatusArchiveRequest"><i:inline key=".statusArchiveRequest.pageName"/></a></li>
        </ul>
    </div>
    <div class="column three nogutter">
        <h3>Simulators</h3>
        <ul class="simple-list stacked">
            <li><a href="viewDataStreamingSimulator"><i:inline key=".dataStreamingSimulator.pageName"/></a></li>
            <li><a href="viewLcrDataSimulator"><i:inline key=".viewLcrDataSimulator.pageName"/></a></li>
            <li><a href="viewMappingSimulator"><i:inline key=".mappingSimulator.pageName"/></a></li>
            <li><a href="gatewaySimulator"><i:inline key=".viewGatewayDataSimulator.pageName"/></a></li>
            <li><a href="viewRfnMeterSimulator"><i:inline key=".rfnMeterSimulator.pageName"/></a></li>
            <li><a href="viewRfnDeviceDeleteSimulator"><i:inline key=".rfnDeviceDeleteSimulator.pageName"/></a></li>
        </ul>
    </div>
</div>

</cti:standardPage>