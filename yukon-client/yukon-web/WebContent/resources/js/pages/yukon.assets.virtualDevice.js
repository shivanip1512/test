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
                    form = dialog.find('#virtual-device-form');
                yukon.ui.blockPage();
                $.ajax({
                    type: "POST",
                    url: yukon.url("/widget/virtualDeviceInfoWidget/save"),
                    data: form.serialize()
                }).done(function (data) {
                    if (data.id) {
                        //if created, redirect user to view device
                        window.location.href = yukon.url('/stars/virtualDevice/' + data.id);
                    } else {
                        //just refresh widget if changed
                        var widgetId = $('.js-virtual-device-info-widget').closest('.widgetWrapper').attr('id'),
                            widgetId = widgetId.substring(widgetId.indexOf("_") + 1),
                            widget = yukon.widgets[widgetId];
                        dialog.dialog('close');
                        dialog.empty();
                        widget.render();
                        yukon.ui.unblockPage();
                    }
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