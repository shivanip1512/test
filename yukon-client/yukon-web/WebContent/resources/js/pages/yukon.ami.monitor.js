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
                var textEl = $(this).val();
                $('span.group-base').text($('span.group-base').attr('data-groupbase') + textEl);
            });

            /** Toggles Monitors enabling .*/
            $(document).on('click', '#toggleMonitor', function() {
                $('#toggleEnabledForm').submit();
            });
            
            $(document).on("yukon:delete:monitor", function(event) {
                $('.page-action-area button, .ui-dialog-buttonset button').prop('disabled', true);
                $('#deleteMonitorForm').submit();
                yukon.ui.blockPage();
                $("#confirm-delete-monitor-popup").dialog("close");
            });
            
            $(document).on('dialogclose', '#confirm-delete-monitor-popup', function (event, ui) {
                $('.page-action-area .button').enable();
            });

            _initialized = true;
        },
        
    };

    return mod;

})();

$(function () { yukon.ami.monitor.init(); });