yukon.namespace('yukon.assets.config');

/**
 * Module for the resend config page.
 * @module yukon.assets.config
 * @requires JQUERY
 * @requires yukon
 */
yukon.assets.config = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            $(document).on('click', '.js-failed-items', function(ev) {
                $.ajax({
                    url: 'view-failed',
                    data: { 'taskId': $(this).data('taskId') }
                }).done(function (data, textStatus, jqXHR) {
                    $('#failed-container').html(data).show();
                });
            });
            
            _initialized = true;
        },
        
        taskFinished : function () {
            $('#cancel-task-btn').hide();
        }
        
    };
    
    return mod;
})();

$(function () { yukon.assets.config.init(); });