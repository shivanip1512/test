yukon.namespace('yukon.widget.rfBroadcast');

/**
 * Module for the RF Broadcast Performance Widget
 * @module yukon.widget.rfBroadcast.js
 * @requires JQUERY
 * @requires yukon
 */
yukon.widget.rfBroadcast = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    _updateInterval = 60000,
    
    _refreshWidget = function () {
        $.ajax({
            url: yukon.url('/widget/rfBroadcastWidget/render'),
        }).done(function (data) {
            $('.js-rf-broadcast-widget').html(data);
            var refreshButton = $('.js-rf-broadcast-widget').find('.js-update-rf-broadcast');
            refreshButton.attr('disabled', true);
            refreshButton.attr('title', $("#js-refresh-tooltip").val());
            setTimeout(function () {
                refreshButton.attr('disabled', false);
                refreshButton.attr('title', $("#js-update-tooltip").val());
                }, $("#js-force-update-interval").val());
        });
    },
    
    _update = function () {
        _refreshWidget();
        setTimeout(_update, _updateInterval);
    },
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            if (_initialized) return;
            
            _update();
            
            $(document).on('click', '.js-update-rf-broadcast', function () {
                _refreshWidget();
            });
            _initialized = true;
        },
        
    };
    
    return mod;
})();

$(function () { yukon.widget.rfBroadcast.init(); });