/**
 * Handles loading control history event lists
 * 
 * @requires JQUERY
 * @requires JQUERYUI
 */

yukon.namespace('yukon.assets.controlHistory.consumer');

yukon.assets.controlHistory.consumer = (function () {
    var _initialized = false, 
        mod;
    
    mod = {
        init: function () {
            if (_initialized) {
                return;
            }
            
            mod.updateControlEvents('PAST_DAY');
            
            _initialized = true;
        },
        
        updateControlEvents: function (controlPeriod) {
            var programId = $('[data-program-id]').data('programId');
            yukon.ui.block($('#controlEventsDiv'));
            $.get(yukon.url('/stars/consumer/controlhistory/innerCompleteHistoryView'),
                    {'programId': programId, 'controlPeriod': controlPeriod},
                    function (data) {
                        $('#controlEventsDiv').html(data);
                    }).always(function() {
                        yukon.ui.unblock($('#controlEventsDiv'));
                    });
        }
    };
    
    return mod;
}());

$(function () { yukon.assets.controlHistory.consumer.init(); });