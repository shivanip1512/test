yukon.namespace('yukon.ami.monitor');

/**
 * Module that manages the common monitor functionality
 * @module   yukon.ami.monitor
 * @requires JQUERY
 * @requires JQUERYUI
 */
yukon.ami.monitor = (function () {
    
    var _initialized = false,

        mod;

    mod = {
            
        init: function () {
            if (_initialized) return;
            $(".js-monitor-name").bind("keyup change", function(e) {
                var textEl=$(this).val();
                $('div.group-base').html($('div.group-base').attr('data-groupbase') + escape(textEl));
            });

            /** Toggles Monitors enabling .*/
            $(document).on('click', '#toggleMonitor', function() {
                $('#toggleEnabledForm').submit();
            });
            
            $(document).on("yukon.dialog.confirm.ok", function(ev) {
                $('.page-action-area button, .ui-dialog-buttonset button').prop('disabled', true);
                $('#deleteMonitorForm').submit();
            });
            
            $(document).on('yukon.dialog.confirm.cancel', function(ev) {
                yukon.ui.unbusy('#deleteButton');
                $('.page-action-area .button').enable();
            });

            _initialized = true;
        },
        
    };

    return mod;

})();

$(function () { yukon.ami.monitor.init(); });