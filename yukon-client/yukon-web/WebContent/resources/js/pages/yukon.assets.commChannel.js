yukon.namespace('yukon.assets.commChannel.js');
 
/** 
 * Module that handles the behavior on the comm channel
 * @module yukon.assets.commChannel.js
 * @requires JQUERY 
 * @requires yukon 
 */
yukon.assets.commChannel = (function () {
 
    'use strict';

    var
    _initialized = false,
    
    _saveRfn1200 = function (popup, dialog) {
        var form = popup.find('#rfn1200-info-form');
        
        form.ajaxSubmit({
            success: function (data) {
                dialog.dialog('close');
                if (data.id) {
                    window.location.href = yukon.url('/stars/device/rfn1200/' + data.id);
                } else {
                    window.location.href = window.location.href;
                }
            },
            error: function (xhr, status, error, $form) {
                popup.html(xhr.responseText);
                yukon.ui.initContent(popup);
            }
        });
    },
    
    _saveCommChannel = function (popup, dialog) {
        var form = dialog.find('.commChannel-create-form'),
            errorMessage = popup.find('.user-message'),
            errorMessageFound = errorMessage.is(":visible"),
            globalError = popup.find('.js-global-error'),
            globalErrorFound = globalError.is(":visible"),
            userPortField = popup.find('.js-user-physical-port-value'),
            userPortEntered = userPortField.exists() && !userPortField.hasClass('dn');
    
        popup.find('.js-physical-port').prop('disabled', userPortEntered);
        popup.find('.js-user-physical-port-value').prop('disabled', !userPortEntered);
    
        if (!errorMessageFound || globalErrorFound) {
            $.ajax({
                type: "POST",
                url: yukon.url("/stars/device/commChannel/save"),
                data: form.serialize()
            }).done(function (data) {
                if (typeof data.id !== 'undefined') {
                    window.location.href = yukon.url('/stars/device/commChannel/' + data.id);
                } else {
                    window.location.href = window.location.href;
                }
                dialog.dialog('close');
                dialog.empty();
            }).fail(function (xhr, status, error){
                popup.html(xhr.responseText);
                yukon.ui.initContent(popup);
                yukon.comm.channel.loadPhysicalPort(popup);
                yukon.comm.channel.formatPhysicalPortErrors(popup);
                yukon.ui.unblockPage();
            });
        } else {
            yukon.ui.unblockPage();
            window.location.href = window.location.href;
        }
    }
 
    mod = {
 
        /** Initialize this module. */
        init: function () {
 
            if (_initialized) return;

            $(document).on("yukon:commChannel:delete", function () {
                yukon.ui.blockPage();
                $('#delete-commChannel-form').submit();
            });
            
            $(document).on("yukon:rfn1200:delete", function () {
                yukon.ui.blockPage();
                $('#delete-rfn1200-form').submit();
            });

            $(document).on('change', '#js-comm-channel-type', function (event) {
                var type = $(this).val(),
                    popup = $('#js-create-comm-channel-popup'),
                    globalError = popup.find('.js-global-error'),
                    globalErrorFound = globalError.is(":visible"),
                    name = popup.find('#js-comm-channel-name').val();
                
                yukon.ui.block($('js-commChannel-container'));

                if (globalErrorFound) {
                    globalError.remove();
                }

                $.ajax({
                    url: yukon.url('/stars/device/commChannel/create/' + type),
                    type: 'get',
                    data: {name: name}
                }).done(function(data) {
                    popup.html(data);
                    yukon.ui.unblock($('js-commChannel-container'));
               });
            });

            $(document).on('change', '.js-physical-port', function (event) {
                yukon.comm.channel.togglePhysicalPort($(this).closest('.ui-dialog'));
            });

            $(document).on("yukon:assets:commChannel:create", function(event) {
                var dialog = $(event.target),
                    popup = $('#js-create-comm-channel-popup'),
                    type = popup.find('#js-comm-channel-type').val(),
                    isRfn1200 = popup.find('#isRfn1200').val();
                
                if (isRfn1200) {
                    _saveRfn1200(popup, dialog);
                } else {
                    _saveCommChannel(popup, dialog);
                }
            });
            
            $(document).on('yukon:assets:rfn1200:save', function (ev) {
                var dialog = $(ev.target),
                    popup = $('#js-edit-rfn1200-popup');
                _saveRfn1200(popup, dialog);
            });

            _initialized = true;
        }
 
    };
 
    return mod;
})();
 
$(function () { yukon.assets.commChannel.init(); });