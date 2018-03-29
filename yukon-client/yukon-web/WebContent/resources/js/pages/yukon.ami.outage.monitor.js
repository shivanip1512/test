yukon.namespace('yukon.ami.outage.monitor');

/**
 * Module that manages the outage monitor feature
 * @module   yukon.ami.outage.monitor
 * @requires JQUERY
 * @requires JQUERYUI
 */
yukon.ami.outage.monitor = (function () {
    
    var _initialized = false,

    _toggleReadFrequencyOptions = function (event) {
        if ($("#outageMonitorId").val().length === 0 ) {
            if ($('#scheduleGroupCommand').is(':checked')) {
                $('#scheduleNameTr').show();
                $('#readFrequencyTr').show();
            } else {
                $('#scheduleNameTr').hide();
                $('#readFrequencyTr').hide();
            }
        } 
    },

        mod;

    mod = {
            
        init: function () {
            
            if (_initialized) return;

            $(document).on('click', '#scheduleGroupCommand', _toggleReadFrequencyOptions);
            $(document).ready(function() {
                _toggleReadFrequencyOptions();
            });
            _initialized = true;
        },
        
    };

    return mod;

})();

$(function () { yukon.ami.outage.monitor.init(); });