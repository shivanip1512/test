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

    mod = {
            
        init: function () {
            
            if (_initialized) return;
            
            _initialized = true;
        },
        
        /** Adjust device collection input after user selects a device group in filter settings. */
        filter_group_selected_callback: function () {
            $('#filter-form input[name="collectionType"]').val('group');
        },
        
        /** Adjust device collection input after user selects devices individually in filter settings. */
        filter_individual_selected_callback: function () {
            $('#filter-form input[name="collectionType"]').val('idList');
        }
        
    };
    
    return mod;
})();

$(function () { yukon.ami.usageThresholdReport.init(); });