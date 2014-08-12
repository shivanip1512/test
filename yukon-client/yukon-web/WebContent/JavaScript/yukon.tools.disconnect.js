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
            
            $(document).on('click', '.js-show-disconnect-info', function(event) {
                var link = $(this),
                    params = {'deviceId': link.data('deviceId'),
                              'shortName': link.data('name')};
                $('#disconnectInfo').load(yukon.url('/widget/disconnectMeterWidget/helpInfo'), params, function() {
                    $('#disconnectInfo').dialog({width: 600});
                });
            });
            
            $('.connect-btn, .disconnect-btn').prop('disabled', true);
            $('.js-show-disconnect-info').hide();
            
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
        
        toggleButtons: function (data) {
            var rawState = data.rawValue;
            
            $('.connect-btn, .disconnect-btn').prop('disabled', false);
            $('.js-show-disconnect-info').show();
            
            if (rawState === '0.0' || rawState === '2.0') {
                $('.connect-btn').show();
                $('.disconnect-btn').hide();
            } else {
                $('.connect-btn').hide();
                $('.disconnect-btn').show();
            }
        }
    };
    
    return _mod;
})();

$(function() { yukon.tools.disconnect.init(); });