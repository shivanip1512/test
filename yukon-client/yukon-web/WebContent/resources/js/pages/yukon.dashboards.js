yukon.namespace('yukon.dashboards');

/**
 * Module for the dashboard pages.
 * @module yukon.dashboards
 * @requires JQUERY
 * @requires yukon
 */
yukon.dashboards = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            /** 'Save' button clicked on the dashboard details popup. */
            $(document).on('yukon:dashboard:details:save', function (ev) {
                $('#dashboard-details').submit();
/*                $('#dashboard-details').ajaxSubmit({
                    success: function (result, status, xhr, $form) {
                    },
                    error: function (xhr, status, error, $form) {
                    },
                    complete: function () {
                    }
                });*/
            });
            
            $(document).on('click', '.js-favorite-dashboard', function () {
                var icon = $(this),
                    dashboardId = icon.data('dashboard');
                $.getJSON(yukon.url('/dashboards/' + dashboardId + '/favorite')).done(function (json) {
                    if (json.isFavorite) {
                        icon.removeClass('icon-favorite-not js-favorite-dashboard')
                            .addClass('icon-star js-unfavorite-dashboard');                    
                    }
                });
            });
            
            $(document).on('click', '.js-unfavorite-dashboard', function () {
                var icon = $(this),
                dashboardId = icon.data('dashboard');
                $.getJSON(yukon.url('/dashboards/' + dashboardId + '/unfavorite')).done(function (json) {
                    if (!json.isFavorite) {
                        icon.removeClass('icon-star js-unfavorite-dashboard')             
                            .addClass('icon-favorite-not js-favorite-dashboard');
                    }
                });
            });
            
            $(document).on('yukon:dashboard:remove', function (ev) {
                var container = $(ev.target),
                dashboardId = container.data('dashboardId');
                window.location.href = yukon.url('/dashboards/' + dashboardId + '/delete');
            });
                        
            _initialized = true;
        },
        
    };
    
    return mod;
})();

$(function () { yukon.dashboards.init(); });