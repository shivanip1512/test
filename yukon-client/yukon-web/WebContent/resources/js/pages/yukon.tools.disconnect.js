yukon.namespace('yukon.tools.disconnect');

/**
 * Singleton that manages the device collection disconnect feature.
 * 
 * @module yukon.tools.disconnect
 * @requires JQUERY
 */
yukon.tools.disconnect = (function() {
    
    'use strict';
    
    var
    _initialized = false,
    _canceled,
    
    _mod = {
        
        /** Initialize this module. Depends on DOM elements so only call after DOM is loaded. */
        init: function() {

            if (_initialized) return;
            
            $('#cancel-btn').click(function(ev) {
                _canceled = true;
                $('#cancel-btn').hide();
                debug.log('canceling disconnect');
                var args = {};
                var key = $(this).data('key');
                var command = $(this).data('command');
                args.key = key;
                args.command = command;
                $.post(yukon.url('/bulk/disconnect/cancel'), args).done(function(result) {
                    debug.log('disconnect cancel result: ' + result);
                });
            });
            
            _canceled = false;
            _initialized = true;
            
        },
        
        /** Update the progress 
         *  @param {Object} data - Progress data.
         */
        progress: function(data) {
            var done = data.value === 'true';
            if (!_canceled) {
                $('#cancel-btn').toggle(!done);
            }
            if (done) {
                $('.js-result').each(function(index, elem) {
                    var result = $(elem),
                    count = result.find('.js-count'),
                    action = result.find('.js-action');
                    
                    if (parseInt(count.text(), 10) > 0) {
                        action.show();
                    }
                });
                
                $('.js-progress .js-action').show();
            }
        }
    };
    
    return _mod;
})();

$(function() { yukon.tools.disconnect.init(); });