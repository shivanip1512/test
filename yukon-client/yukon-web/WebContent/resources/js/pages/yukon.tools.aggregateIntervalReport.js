yukon.namespace('yukon.tools.aggregateIntervalReport');

/**
 * Module for the Aggregate Interval Data Report page
 * @module yukon.tools.aggregateIntervalReport
 * @requires JQUERY
 * @requires yukon
 */
yukon.tools.aggregateIntervalReport = (function () {
    
    var 
    
    _initialized = false,

    mod = {

        /** Adjust device collection input after user selects a device group in criteria. */
        groupSelected: function () {
            $('#filter-form input[name="collectionType"]').val('group');
        },
        
        /** Adjust device collection input after user selects devices individually in criteria. */
        devicesSelected: function () {
            $('#filter-form input[name="collectionType"]').val('idList');
        },
            
        init: function () {
            
            if (_initialized) return;
            
            /** Display fixed value text box if Missing Interval Data is Fixed Value */
            $(document).on('change', '.js-missing-data', function (ev) {
                $('.js-missing-data-value').toggleClass('dn', $(this).val() !== $('#fixedValueOption').val());
                //remove validation error if exists
                $("span[id='missingIntervalDataValue.errors']").remove();
            });
            
            _initialized = true;
        },

        
    };
    
    return mod;
})();

$(function () { yukon.tools.aggregateIntervalReport.init(); });