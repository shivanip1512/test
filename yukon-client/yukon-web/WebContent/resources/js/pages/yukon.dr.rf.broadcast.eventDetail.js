yukon.namespace('yukon.dr.rf.broadcast.eventDetail');

/**
 * Module for the RF Broadcast Event Detail page.
 * @module yukon.dr.rf.broadcast.eventDetail
 * @requires JQUERY
 * @requires yukon
 */
yukon.dr.rf.broadcast.eventDetail = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    _filterResults = function () {
        var filters = _getFilters();
        yukon.ui.blockPage();
        var url = yukon.url('/dr/rf/broadcast/eventDetail/filterResults?eventId=' + filters.eventId + '&deviceSubGroups=' + filters.deviceSubGroups + '&statuses=' + filters.statuses);
        $.get(url, function (data) {
            $("#js-filtered-results").html(data);
        }).always(function () {
            yukon.ui.unbusy($('.js-filter-results'));
            yukon.ui.unblockPage();
        });
    },
    
    _getFilters = function () {
        var statuses = [];
        var deviceSubGroups = [];
        
        $('input[name=statuses]').each(function (index, element) {
            if($(element).prop('checked')){
                statuses.push($(element).val());
            }
        });
        if ($.isEmptyObject(statuses)) {
            $('input[name=statuses]').each(function (index, element) {
                statuses.push($(element).val());
                $(element).prop( "checked", true );
            });
        }
        $("input[name=deviceSubGroups]").each(function (index, element) {
            deviceSubGroups.push($(element).val());
        });
        
        var filters = {
                eventId : $("input[name=eventId]").val(),
                deviceSubGroups : deviceSubGroups,
                statuses : statuses 
        };
        
        return filters;
    },
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;

            $(document).on('click', '.js-filter-results', function () {
                _filterResults(); 
            });

            $(document).on('click', '.js-collection-action', function () {
                var filters = _getFilters();
                var actionUrl = $(this).data('url');
                var url = yukon.url('/dr/rf/broadcast/eventDetail/collectionAction?actionUrl='
                                + actionUrl + '&eventId=' + filters.eventId
                                + '&deviceSubGroups=' + filters.deviceSubGroups
                                + '&statuses=' + filters.statuses);
                window.open(url, '_blank');
            });

            _filterResults();

            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.dr.rf.broadcast.eventDetail.init(); });