yukon.namespace('yukon.assets.dashboard');

/**
 * Module for the assets dashboard page.
 * @module yukon.assets.dashboard
 * @requires JQUERY
 * @requires yukon
 */
yukon.assets.dashboard = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    _updateRecentActions = function () {
        
        var table = $('.js-recent-actions');
        
        $.ajax({
            url: 'actions/recent',
            contentType: 'application/json'
        }).done(function (actions, status, xhr) {
            
            var i, action, row, percent;
            
            for (i = 0; i < actions.length; i++) {
                action = actions[i];
                row = $(table.find('[data-task="' + action.id + '"]')[0]);
                percent = yukon.percent(action.completed, action.total, 2); 
                
                if (!row.length) {
                    row = table.find('.js-recent-action-template').clone()
                    .removeClass('js-recent-action-template')
                    .attr('data-task', action.id).data('task', action.id)
                    .prependTo(table.find('tbody'));
                    
                    row.find('td:first-child a').attr('href', yukon.url('/stars/operator/inventory/action/' + action.id))
                    .text(action.text.startedAt);
                    
                    row.find('td:nth-child(2)').text(action.text.type);
                    row.show();
                }
                
                row.find('.js-complete').toggleClass('dn', !action.complete);
                row.find('.js-progress-bar').toggleClass('dn', action.complete)
                .find('.progress-bar').css({ width: percent });
                row.find('.js-progress-text').toggleClass('dn', action.complete)
                .text(percent);
            }
            
        }).always(function () {
            setTimeout(_updateRecentActions, 4000);
        });
    },
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            _updateRecentActions();
            
            _initialized = true;
        },
        
        selectIndividually : function () {
            $('#select-individually-form').submit();
            return true;
        },
        
        addMeter : function () {
            $('#add-meter-form').submit();
            return true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.assets.dashboard.init(); });