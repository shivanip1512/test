yukon.namespace('yukon.assets.rfn1200.js');
 
/** 
 * Module that handles the behavior on the RFN-1200 page
 * @module yukon.assets.rfn1200.js
 * @requires JQUERY 
 * @requires yukon 
 */
yukon.assets.rfn1200 = (function () {
 
    'use strict';

    var
    _initialized = false,
 
    mod = {
 
        /** Initialize this module. */
        init: function () {
 
            if (_initialized) return;
            
            $(document).on("yukon:rfn1200:delete", function () {
                yukon.ui.blockPage();
                $('#delete-rfn1200-form').submit();
            });
            
            $(document).on('yukon:assets:rfn1200:save', function (ev) {
                var dialog = $(ev.target),
                    popup = $('#js-edit-rfn1200-popup'),
                    form = popup.find('#rfn1200-info-form');
                
                form.ajaxSubmit({
                    success: function (result, status, xhr, $form) {
                        dialog.dialog('close');
                        window.location.href = window.location.href;
                    },
                    error: function (xhr, status, error, $form) {
                        popup.html(xhr.responseText);
                        yukon.ui.initContent(popup);
                    }
                });
                
            });

            _initialized = true;
        }
 
    };
 
    return mod;
})();
 
$(function () { yukon.assets.rfn1200.init(); });