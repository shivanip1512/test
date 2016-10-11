yukon.namespace('yukon.widget.relayInfo');

/**
 * Module for the relay info widget.
 * @module yukon.widget.relayInfo
 * @requires JQUERY
 * @requires yukon
 */
yukon.widget.relayInfo = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            $(document).on('yukon:widget:relay:info:save', function (ev) {
                var popup = $('#relay-info-popup'),
                    btns = popup.closest('.ui-dialog').find('.ui-dialog-buttonset'),
                    primary = btns.find('.js-primary-action'),
                    secondary = btns.find('.js-secondary-action');
                
                yukon.ui.busy(primary);
                secondary.prop('disabled', true);
                
                popup.find('.user-message').remove();
                
                $('#relay-info-form').ajaxSubmit({
                    type: 'post',
                    success: function (result, status, xhr, $form) {
                        
                        popup.dialog('close');
                        window.location.href = window.location.href;
                    },
                    error: function (xhr, status, error, $form) {
                        popup.html(xhr.responseText);
                    },
                    complete: function () {
                        yukon.ui.unbusy(primary);
                        secondary.prop('disabled', false);
                    }
                });
            });
            
            _initialized = true;
        }
    
    };
    
    return mod;
})();

$(function () { yukon.widget.relayInfo.init(); });