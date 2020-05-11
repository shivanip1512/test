yukon.namespace('yukon.widget.config');

/**
 * Module for the device config widget.
 * @module yukon.widget.configWidget
 * @requires JQUERY
 * @requires yukon
 */
yukon.widget.config = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            $(document).on('click', '.js-change-config', function (event) {
                var widgetId = $(this).closest('.widgetWrapper').attr('id'),
                    widgetId = widgetId.substring(widgetId.indexOf("_") + 1),
                    widget = yukon.widgets[widgetId],
                    deviceId = $(this).data('device-id'),
                    selectedConfig = $('#configuration').val(),
                    parameters = {
                        'deviceId' : deviceId,
                        'configuration' : selectedConfig,
                    };
                    
                $.post(yukon.url('/widget/' + widget.shortName + '/changeConfig'), parameters, function (data) {
                    //refresh widget
                    widget.render();
                    //display upload popup if needed
                    if (data.displayUploadPopup) {
                        var popup = $('#uploadPopup'),
                            title = popup.data('title'),
                            uploadBtnText = popup.data('upload-btn');
                        popup.attr('data-widget-id', widgetId);
                        $('.js-upload-msg').text(data.popupMessage);
                        popup.dialog({
                            title: title,
                            width: '400px',
                            modal: true,
                            buttons: yukon.ui.buttons({ okText: uploadBtnText, event: 'yukon:config:upload' })
                        });
                    }
                });
                
            });
            
            $(document).on('yukon:config:upload', function(event) {
                var popup = $(event.target),
                    widgetId = popup.data('widget-id'),
                    widget = yukon.widgets[widgetId],
                    args = {
                        command: 'uploadConfig'
                    }
                widget.doActionRefresh(args);
                popup.dialog('close');
            });
            
            _initialized = true;
        },
        
        inProgressUpdater: function (data) {
            var value = data.value;
            $('.js-config-action-btns').prop('disabled', value == 'true');
        }
    
    };
    
    return mod;
})();

$(function () { yukon.widget.config.init(); });