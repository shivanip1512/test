yukon.namespace('yukon.tools.demand.reset');

/**
 * Singleton that manages the device collection demand reset feature.
 * 
 * @module yukon.tools.demand.reset
 *  @requires JQUERY
 *  @requires yukon
 */
yukon.tools.demand.reset = (function() {
    
    'use strict';
    
    var
    _initialized = false,
    _canceled = false,
    
    mod = {
        
        /** Initialize this module. Depends on DOM elements so only call after DOM is loaded. */
        init: function() {

            if (_initialized) return;
            
            $('#cancel-btn').click(function(ev) {
                var btn = $(this),
                params = {
                    key: btn.data('key'),
                    command: btn.data('command')
                };

                yukon.ui.busy(btn);
                
                _canceled = true;
                debug.log('canceling demand reset');
    
                $.post(yukon.url('/bulk/demand-reset/cancel'), params)
                .done(function (result) {
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
                $('#cancel-btn').hide();
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
    
    return mod;
})();

$(function() { yukon.tools.demand.reset.init(); });