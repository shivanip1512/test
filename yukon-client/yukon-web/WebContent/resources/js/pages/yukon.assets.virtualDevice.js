yukon.namespace('yukon.assets.virtualDevice.js');
 
/** 
 * Module that handles the behavior on the Virtual Device page
 * @module yukon.assets.virtualDevice.js
 * @requires JQUERY 
 * @requires yukon 
 */
yukon.assets.virtualDevice = (function () {
 
    'use strict';
 
    var
    _initialized = false,
 
    mod = {
 
        /** Initialize this module. */
        init: function () {
 
            if (_initialized) return;
            
            $(document).on("yukon:virtualDevice:delete", function () {
                yukon.ui.blockPage();
                $('#delete-virtualDevice-form').submit();
            });
            
            $(document).on("yukon:virtualDevice:save", function (event) {
                var dialog = $(event.target),
                    form = dialog.find('#virtual-device-form'),
                    deviceId = dialog.find('#deviceId').val(),
                    isDisabled = dialog.find('#disableToggle').prop('checked'),
                    disableFlag = isDisabled ? 'Y' : 'N';
                dialog.find('#disableFlag').val(disableFlag);
                yukon.ui.blockPage();
                $.ajax({
                    type: "POST",
                    url: yukon.url("/widget/virtualDeviceInfoWidget/save"),
                    data: form.serialize() + "&deviceId=" + deviceId
                }).done(function () {
                    var widgetId = $('.js-virtual-device-info-widget').closest('.widgetWrapper').attr('id'),
                        widgetId = widgetId.substring(widgetId.indexOf("_") + 1),
                        widget = yukon.widgets[widgetId];
                    dialog.dialog('close');
                    dialog.empty();
                    widget.render();
                    yukon.ui.unblockPage();
                }).fail(function (xhr, status, error){
                    dialog.html(xhr.responseText);
                    yukon.ui.unblockPage();
                });
            });
            
            _initialized = true;
        }
 
    };
 
    return mod;
})();
 
$(function () { yukon.assets.virtualDevice.init(); });