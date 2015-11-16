yukon.namespace('yukon.assets.report');

/**
 * Module for the assest report page.
 * @module yukon.assets.report
 * @requires JQUERY
 * @requires yukon
 */
yukon.assets.report = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    /** Device count */
    _count = 0,
    _threshold = 6000,
    
    /** 
     * Load the table via ajax request. 
     * This is done here via ajax because it can take a long time. 
     */
    _load = function () {
        
        var container = $('#device-report');
        var url = container.data('url');
        
        yukon.ui.block(container);
        
        $.ajax(url).done(function (table) {
            container.html(table);
            yukon.ui.unblock(container);
        });
        
    },
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            _count = +$('#device-report-collection .js-count').text();
            
            if (_count > _threshold) {
                /** Show loading glass when user clicks any of the paging controls. */
                $('#device-report').on('click', yg.selectors.paging, function (ev) {
                    yukon.ui.block($('#device-report'));
                });
                
                /** Hide loading glass when any paging action finishes. */
                $('#device-report').on(yg.events.pagingend, function (ev) {
                    yukon.ui.unblock($('#device-report'));
                });
            }
            
            _load();
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.assets.report.init(); });