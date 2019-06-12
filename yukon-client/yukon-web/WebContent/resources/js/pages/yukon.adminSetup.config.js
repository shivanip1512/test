yukon.namespace('yukon.adminSetup.config');

/** .
 * Handles behavior on the email test page
 * @module yukon.adminSetup.config
 * @requires JQUERY
 * @requires yukon
 */
yukon.adminSetup.config = (function () {

    'use strict';
    
    var _initialized = false;
    
    
    mod = {
    
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return; 
            
            $(document).on("yukon:adminSetup:config:sendTestEmail", function (ev) {
                $('#adminSetup-testEmail-form').ajaxSubmit({
                    success: function (data, status, xhr, $form) {
                        $('#adminSetup-testEmail-popup').dialog('close');
                        if (data.failureMessage!=null){
                            yukon.ui.alertError(data.failureMessage);
                        }
                        else {
                            yukon.ui.alertSuccess(data.successMessage);
                        }
                    },
                    error: function (xhr, status, error, $form) {
                        $('#adminSetup-testEmail-popup').html(xhr.responseText);
                    }
                });
            });
            
            _initialized = true;
        }

    };
    
    return mod;
})();
 
$(function () {yukon.adminSetup.config.init(); });