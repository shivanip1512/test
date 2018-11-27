yukon.namespace('yukon.dev.nestSimulator');

/**
 * Module handling nestSimulator
 * @module yukon.dev.simulators.nestSimulator
 * @requires JQUERY
 * @requires yukon
 */

yukon.dev.nestSimulator = (function() {
    var _initialized = false,

    mod = {
        init : function() {
            if (_initialized) return;
            
            $(document).on('click', '#cancel-button', function () {
                var form = $(this).closest('form');
                form.attr("action", "cancelEvent");
                form.submit();
            });
            
            _initialized = true;
        },

    };
    return mod;
}());

$( function() { yukon.dev.nestSimulator.init(); });