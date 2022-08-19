yukon.namespace('yukon.support.eventlog.type');

/**
 * Module for the event log page.
 * @module yukon.support.eventlog.type
 * @requires JQUERY
 * @requires yukon
 */
yukon.support.eventlog.type = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    _filterResults = function () {
        $.ajax({
            type: 'POST',
            url: yukon.url('/common/eventLog/filterByCategory'),
            data: $('#filter-form').serialize()
        }).done(function (data) {
            $('#events-container').html(data);
            $('#events-container').data('url', yukon.url('/common/eventLog/filterByCategory?' + $('#filter-form').serialize()));
        }).fail(function (xhr, status) {
            $('.yukon-page').html(xhr.responseText);
        });
    },
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            /** Update the url for events with the filter settings. */
            $('#eventsByType-container').attr('data-url', 'viewByType?' + $('#filter-form').serialize());

            /** User clicked the filter button on the By Category tab.  Submit the form after updating paging params. */
            $('#filterByCategory-btn').click(function (ev) {
                _filterResults();
            });
            
            /** User clicked the filter button on the By Type tab.  Submit the form after updating paging params. */
            $('#filterByType-btn').click(function (ev) {
                var itemsPerPage = $('#eventsByType-container .paging-area').data('pageSize');
                $('#filter-form').append('<input type="hidden" name="itemsPerPage" value="' + itemsPerPage + '">');
                $('#filter-form').submit();
            });
            
            $('.js-download').click(function (ev) {
                $('#filter-form').submit();
            });
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.support.eventlog.type.init(); });