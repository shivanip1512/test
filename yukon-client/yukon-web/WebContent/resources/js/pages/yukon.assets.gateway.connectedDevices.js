yukon.namespace('yukon.assets.gateway.connectedDevices');
 
/**
 * Module to handle the Wifi Connected Devices and Cellular Connected Devices pages.
 * 
 * @module yukon.assets.gateway.connectedDevices
 * @requires yukon
 * @requires JQUERY
 */
yukon.assets.gateway.connectedDevices = (function () {
 
    var
    _initialized = false,
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            $(document).ready(function() {
                Sortable.init();
            });
            
            $("#cellTypesFilter").chosen({width: '300px'});
            $("#commStatusFilter").chosen({width: '250px'});
            var baseUrl = $('#baseUrl').val();
            
            //refresh connection status for a device
            $(document).on('click', '.js-refresh-status', function () {
                var btn = $(this),
                    deviceId = btn.data('deviceId'),
                    refreshMsg = $('.js-refresh-msg');
                refreshMsg.addClass('dn');
                yukon.ui.busy(btn);
                
                $.ajax({
                    url: yukon.url(baseUrl + '/refresh?deviceIds=' + deviceId)
                }).done(function () {
                    refreshMsg.removeClass('dn');
                    setTimeout(function() {yukon.ui.unbusy(btn); }, 1000);
                });
            });
            
            //filter devices
            $('#commStatusFilter, #cellTypesFilter').chosen().change(function () {
                var form = $('#filterConnectionDevices'),
                    gatewayId = $('#gatewayId').val(),
                    data = form.serialize();
                    url = yukon.url(baseUrl + '/filterConnectedDevices/' + gatewayId + '?' + data);
                $.get(url, function (data) {
                    $("#filtered-results").html(data);
                    Sortable.init();
                }).always(function () {
                    yukon.ui.unbusy($('.js-filter-results'));
                    yukon.ui.unblockPage();
                });
            });
            
            $(document).on('click', '.js-download', function () {
                var gatewayId = $('#gatewayId').val(),
                    selectedStatuses = $('#commStatusFilter').chosen().val(),
                    deviceTypes = $('#cellTypesFilter').chosen().val();
                $('.js-refresh-msg').addClass('dn');
                window.location.href = yukon.url(baseUrl + '/connectedDevicesDownload/' + gatewayId 
                        + '?filteredCommStatus=' + selectedStatuses + '&deviceTypes=' + deviceTypes);
            });
            
            $(document).on('click', '.js-map', function () {
                var selectedStatuses = $('#commStatusFilter').chosen().val(),
                    connectedStatusValue = parseInt($('#connectedStatusValue').val()),
                    connectedIds = [],
                    disconnectedIds = [];
                $('.js-refresh-msg').addClass('dn');
                //find connected and disconnected
                $('.js-table-row').each(function() {
                    var commStatus = $(this).find('.js-comm-status-value').text(),
                        deviceId = $(this).data('deviceId');
                    commStatus == connectedStatusValue ? connectedIds.push(deviceId) : disconnectedIds.push(deviceId);
                });
                window.open(yukon.url(baseUrl + '/connectedDevicesMapping?connectedIds=' + connectedIds + 
                        '&disconnectedIds=' + disconnectedIds + '&filteredCommStatus=' + selectedStatuses));
            });
            
            //refresh connection status for a device
            $(document).on('click', '.js-refresh-all', function () {
                var deviceIds = $('#deviceIds').val(),
                    refreshMsg = $('.js-refresh-msg');
                refreshMsg.addClass('dn');
                var deviceIdArray = deviceIds.split(',');
                
                $.ajax({
                    url: yukon.url(baseUrl + '/refresh?deviceIds=' + deviceIdArray)
                }).done(function () {
                    refreshMsg.removeClass('dn');
                });
            });
            
            _initialized = true;
        },

    };
 
    return mod;
})();
 
$(function () { yukon.assets.gateway.connectedDevices.init(); });