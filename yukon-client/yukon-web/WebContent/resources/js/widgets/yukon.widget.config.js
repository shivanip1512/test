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
    _updateTimeout = null,
    
    _updateStatus = function () {
        var widgetContainer = $('.js-config-widget'),
            widgetId = widgetContainer.closest('.widgetWrapper').attr('id'),
            widgetId = widgetId.substring(widgetId.indexOf("_") + 1),
            widget = yukon.widgets[widgetId],
            parameters = {
                'deviceId' : widgetContainer.data('device-id'),
            };
        $.getJSON(yukon.url('/widget/' + widget.shortName + '/getStatus'), parameters, function (data) {
            var status = widgetContainer.find('.js-status'),
                statusRow = widgetContainer.find('.js-status-row');
            if (data.notConfigured) {
                statusRow.addClass('dn');
            } else {
                statusRow.removeClass('dn');
                status.text(yukon.escapeXml(data.statusText));
                if (data.isInSync) {
                    status.addClass('success');
                } else {
                    status.removeClass('success');
                    if (data.isOutOfSync){
                        //show link for popup
                        status.html("<a href=javascript:void(0) class=js-out-of-sync>" + yukon.escapeXml(data.statusText) + "</a>");
                    }
                }
            }
            var configBtns = widgetContainer.find('.js-config-action-btns');
            configBtns.prop('disabled', data.isInProgress);
            if (data.isInProgress) {
                configBtns.attr('title', $('#actionsDisabledMessage').val());
            } else {
                configBtns.removeAttr('title');
            }
        });
        if (_updateTimeout) {
            clearTimeout(_updateTimeout);
        }
        _updateTimeout = setTimeout(_updateStatus, yg.rp.updater_delay);
    },
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            _updateStatus();
            
            $(document).on('click', '.js-change-config', function () {
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
                    if (data.errorMessage) {
                        $('.js-error-msg').text(data.errorMessage).removeClass('dn');
                    } else {
                        //refresh widget
                        widget.render();
                    }
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
            
            $(document).on('click', '.js-out-of-sync', function () {
                var popup = $('.js-out-of-sync-popup'),
                    title = popup.data('title'),
                    widgetContainer = $('.js-config-widget'),
                    deviceId = widgetContainer.data('device-id');
                popup.load(yukon.url('/deviceConfiguration/summary/' + deviceId + '/outOfSync'), function () {
                    popup.dialog({
                        title: title,
                        width: 550
                    });
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
        }

    
    };
    
    return mod;
})();

$(function () { yukon.widget.config.init(); });