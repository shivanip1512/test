yukon.namespace('yukon.admin.user');

/**
 * Module to handle the user page.
 * @module yukon.admin.user
 * @requires JQUERY
 * @requires yukon
 */
yukon.admin.user = (function () {

    'use strict';
    
    var
    _initialized = false,
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            /** User clicked 'remove login wait' button. */
            $(document).on('click', '.js-remove-login-wait', function (ev) {
                $.ajax(yukon.url('/admin/users/' + $(this).data('userId') + '/remove-login-wait'))
                .done(function () {
                    $('#login-throttle').hide();
                });
            });
            
            /** User clicked save button on change password popup, submit request. */
            $(document).on('yukon:admin:user:password:save', function (ev) {
                
                $('#change-password-form').ajaxSubmit({
                    success: function (result) {
                        $('#change-password-popup').dialog('close');
                        window.location.reload();
                    },
                    error: function (xhr) {
                        var result = $.parseJSON(xhr.responseText);
                        $('#change-password-popup').addMessage({ messageClass: 'error', message: result });
                    }
                });
                
            });
            
            _initialized = true;
        }
    
    };
    
    return mod;
})();

$(function () { yukon.admin.user.init(); });