yukon.namespace('yukon.tools.demand.reset');

/**
 * Singleton that manages the device collection demand reset feature.
 * 
 * @module yukon.tools.demand.reset
 * @requires JQUERY
 */
yukon.tools.demand.reset = (function() {
    
    'use strict';
    
    var
    _initialized = false,
    _canceled = false,
    
    _mod = {
        
        /** Initialize this module. Depends on DOM elements so only call after DOM is loaded. */
        init: function() {

            if (_initialized) return;
            
            $('#cancel-btn').click(function(ev) {
                _canceled = true;
                $('#cancel-btn').hide();
                debug.log('canceling demand reset');
                var args = {};
                var key = $(this).data('key');
                var command = $(this).data('command');
                args.key = key;
                args.command = command;
                $.post(yukon.url('/bulk/demand-reset/cancel'), args).done(function(result) {
                    debug.log('demand reset cancel result: ' + result);
                });
            });
            _canceled = false;
            _initialized = true;
        },
        
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
        },
        
        cancellable: function(data) {
            var done = data.value === 'true';
            if (!_canceled) {
                $('#cancel-btn').toggle(done);
                _canceled = true;
            }
        }
    };
    
    return _mod;
})();

$(function() { yukon.tools.demand.reset.init(); });