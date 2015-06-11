yukon.namespace('yukon.support.eventlog.type');

/**
 * Module for the evenlog by type page.
 * @module yukon.support.eventlog.type
 * @requires JQUERY
 * @requires yukon
 */
yukon.support.eventlog.type = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            /** Update the url for events with the filter settings. */
            $('#events-container').attr('data-url', 'viewByType?' + $('#filter-form').serialize());
            
            /** User clicked the filter button.  Submit the form after updating paging params. */
            $('#filter-btn').click(function (ev) {
                var itemsPerPage = $('#events-container .paging-area').data('pageSize');
                $('#filter-form').append('<input type="hidden" name="itemsPerPage" value="' + itemsPerPage + '">');
                $('#filter-form').submit();
            });
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.support.eventlog.type.init(); });