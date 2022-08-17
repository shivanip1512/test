yukon.namespace('yukon.assets.wifi.connection');
 
/**
 * Module to handle the Wifi Connection Status.
 * 
 * @module yukon.assets.wifi.connection
 * @requires yukon
 * @requires JQUERY
 */
yukon.assets.wifi.connection = (function () {
 
    var
    _initialized = false,
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            $("#commStatusFilter").chosen({width: '250px'});
            
            //refresh connection status for a device
            $(document).on('click', '.js-refresh-wifi', function () {
                var btn = $(this),
                    deviceId = btn.data('deviceId'),
                    refreshMsg = $('.js-refresh-msg');
                refreshMsg.addClass('dn');
                yukon.ui.busy(btn);
                
                $.ajax({
                    url: yukon.url('/stars/wifiConnection/refresh?deviceIds=' + deviceId)
                }).done(function () {
                    refreshMsg.removeClass('dn');
                    setTimeout(function() {yukon.ui.unbusy(btn); }, 1000);
                });
            });
            
            //filter devices
            $('#commStatusFilter').chosen().change(function () {
                var selectedValues = $(this).find('option:selected').text(),
                    count = 0,
                    deviceIds = [];
                $('.js-refresh-msg').addClass('dn');
                $('.js-table-row').each(function() {
                    var commStatus = $(this).find('.js-comm-status').text(),
                        deviceId = $(this).data('deviceId');
                    if (selectedValues.length == 0 || selectedValues.indexOf(commStatus) > -1) {
                        count ++;
                        $(this).removeClass('dn');
                        deviceIds.push(deviceId);
                    } else {
                        $(this).addClass('dn');
                    }
                });
                //update count
                $('.js-count').text(count);
                //update device ids
                $('#deviceIds').val(deviceIds);
                $('.js-collection-actions a').attr('href', yukon.url('/bulk/collectionActions?collectionType=idList&idList.ids=' + deviceIds.join(',')));
            });
            
            $(document).on('click', '.js-download', function () {
                var gatewayId = $('#gatewayId').val(),
                    selectedStatuses = $('#commStatusFilter').chosen().val();
                $('.js-refresh-msg').addClass('dn');
                window.location.href = yukon.url('/stars/wifiConnection/connectedDevicesDownload/' + gatewayId 
                        + '?filteredCommStatus=' + selectedStatuses);
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
                window.open(yukon.url('/stars/wifiConnection/connectedDevicesMapping?connectedIds=' + connectedIds + 
                        '&disconnectedIds=' + disconnectedIds + '&filteredCommStatus=' + selectedStatuses));
            });
            
            //refresh connection status for a device
            $(document).on('click', '.js-refresh-all', function () {
                var deviceIds = $('#deviceIds').val(),
                    refreshMsg = $('.js-refresh-msg');
                refreshMsg.addClass('dn');
                var deviceIdArray = deviceIds.split(',');
                
                $.ajax({
                    url: yukon.url('/stars/wifiConnection/refresh?deviceIds=' + deviceIdArray)
                }).done(function () {
                    refreshMsg.removeClass('dn');
                });
            });
            
            _initialized = true;
        },

    };
 
    return mod;
})();
 
$(function () { yukon.assets.wifi.connection.init(); });