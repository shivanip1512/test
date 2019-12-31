yukon.namespace('yukon.dev.mappingSimulator');

/**
 * Module handling mappingSimulator update, stop, and populate messaging
 * @module yukon.dev.simulators.mappingSimulator
 * @requires JQUERY
 * @requires yukon
 */

yukon.dev.mappingSimulator = (function() {
    var _initialized = false,

    mod = {
        init : function() {
            if (_initialized) return;
            
            $(document).on('click', '.populateDatabase', function () {
                var form = $('#mapping-form');
                form.attr("action", "populateMappingDatabase");
                form.submit();
            });
            
            $(document).on('click', '#updateSettings', function () {
                var form = $('#mapping-form');
                form.attr("action", "updateMappingSettings");
                form.submit();
            });
            
            $(document).on('click', '#refreshNetworkTree', function () {
                var form = $('#mapping-form');
                form.attr("action", "refreshNetworkTree");
                form.submit();
            });
            
            $(document).on('click', '#stopSimulator', function () {
                var form = $('#mapping-form');
                form.attr("action", "stopMappingSimulator");
                form.submit();
            });
            
            _initialized = true;
        },

    };
    return mod;
}());

$( function() { yukon.dev.mappingSimulator.init(); });