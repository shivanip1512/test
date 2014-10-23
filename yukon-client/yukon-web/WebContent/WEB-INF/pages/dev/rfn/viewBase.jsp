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
    
    _update = function () {
        $.ajax({
            url: 'data',
            contentType: 'application/json',
            dataType: 'json'
        }).done(function (data, status, xhr) {
            data.forEach(function (item, index, arr) {
                var ids = Object.keys(item), id, value, current;
                for (var i in ids) {
                     id = ids[i];
                     value = item[id].value;
                     current = $('#' + id).text().trim();
                     $('#' + id).text(value);
                     if (current != value) {
                         $('#' + id).flash();
                     }
                }
            });
        }).always(function () {
            setTimeout(_update, 500);
        });
    },
    
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
            
            /** Start the recursive updating. */
            setTimeout(_update, 200);
            
            _initialized = true;
        }
    
    };
    
    return mod;
})();
 
$(function () { yukon.dev.rfn.init(); });

</script>
<div class="column-12-12 clearfix">
    <div class="column one">
        <h3>Pages</h3>
        <ul class="simple-list stacked">
            <li><a href="viewMeterReadArchiveRequest"><i:inline key=".meterReadArchiveRequest.label"/></a></li>
            <li><a href="viewEventArchiveRequest"><i:inline key=".eventArchiveRequest.label"/></a></li>
            <li><a href="viewLcrArchiveRequest"><i:inline key=".lcrArchiveRequest.label"/></a></li>
            <li><a href="viewLcrReadArchiveRequest"><i:inline key=".lcrReadArchiveRequest.label"/></a></li>
            <li><a href="viewLcrDataSimulator"><i:inline key=".lcrDataSimulator.label"/></a></li>
        </ul>
        <div class="page-action-area stacked">
            <cti:button id="calc-stress-test" label="Calc Stress Test, DO NOT CLICK" classes="action red"/>
        </div>
        <div class="page-action-area stacked">
            <cti:button id="startup-notif" label="Resend Startup Notif"/>
            <cti:button id="clear-gateway-cache" label="Clear Gateway Cache"/>
        </div>
    </div>
    <div class="column two nogutter">
        <h3>YSM Server Broker Stats</h3>
        <c:forEach items="${data}" var="type">
            <table class="name-value-table striped stacked-md">
                <thead></thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach items="${type}" var="entry">
                        <c:set var="thing" value="${entry.value}"/>
                        <tr>
                            <td class="name">${thing.name}</td>
                            <td class="value tar"><span id="${entry.key}">${thing.value}</span></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:forEach>
    </div>
</div>

</cti:standardPage>