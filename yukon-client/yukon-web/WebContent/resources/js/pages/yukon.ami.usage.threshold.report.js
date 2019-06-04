yukon.namespace('yukon.ami.usageThresholdReport');

/**
 * Module for the Usage Threshold Report page
 * @module yukon.ami.usageThresholdReport
 * @requires JQUERY
 * @requires yukon
 */
yukon.ami.usageThresholdReport = (function () {
    
    var 
    
    _initialized = false,
    
    _filterResults = function () {
        $('#filter-results-form').ajaxSubmit({
            success: function(data, status, xhr, $form) {
                $('#usage-device-table').html(data);
            },
            error: function(xhr, status, error, $form) {
                $('#usage-device-table').html(xhr.responseText);
            }
        });
    },

    mod = {
            
        init: function () {
            
            if (_initialized) return;
            
            _filterResults();
            
            /** Filter the results */
            $(document).on('click', '.js-filter', function (ev) {
                _filterResults();
            });
            
            $(".js-primary-gateway-select").chosen({width: "300px"});
            
            $(document).on('click', '.js-download', function () {
                var form = $('#filter-results-form');
                var data = form.serialize();
                window.location = yukon.url('/amr/usageThresholdReport/download?' + data);
            });
            
            _initialized = true;
        },
        
        /** Adjust device collection input after user selects a device group in criteria. */
        criteria_group_selected_callback: function () {
            $('#filter-form input[name="collectionType"]').val('group');
        },
        
        /** Adjust device collection input after user selects devices individually in criteria. */
        criteria_individual_selected_callback: function () {
            $('#filter-form input[name="collectionType"]').val('idList');
        }
        
    };
    
    return mod;
})();

$(function () { yukon.ami.usageThresholdReport.init(); });