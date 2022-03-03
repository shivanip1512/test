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
                //show/hide last error
                var lastErrorSpan = widgetContainer.find('.js-last-error');
                if (data.errorCode) {
                    lastErrorSpan.data('error-code', yukon.escapeXml(data.errorCode))
                    lastErrorSpan.removeClass('dn');
                } else {
                    lastErrorSpan.addClass('dn');
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
                        var popup = $('#uploadPopup');
                        popup.attr('data-widget-id', widgetId);
                        popup.find('.js-upload-msg').text(data.popupMessage);
                        yukon.ui.dialog(popup);
                    }
                });
                
            });
            
            $(document).on('click', '.js-out-of-sync', function () {
                yukon.ui.dialog($('.js-out-of-sync-popup'));
            });
            
            $(document).on('click', '.js-last-error', function () {
                var errorCode = $(this).data('errorCode'),
                    popupTitle = $(this).data('popupTitle'),
                    dialogDivJson = {
                        'data-title': popupTitle,
                        'data-width': '600',
                        'data-url': yukon.url('/deviceConfiguration/summary/' + errorCode + '/displayError')
                    };
                yukon.ui.dialog($('<div/>').attr(dialogDivJson));
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