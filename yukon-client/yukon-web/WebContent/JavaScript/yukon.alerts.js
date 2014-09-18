/**
 * Singleton that manages alerts button and popup
 * 
 * @module yukon.alerts
 * @requires JQUERY
 * @requires JQUERYUI
 * @requires yukon
 */

yukon.namespace('yukon.alerts');

yukon.alerts = (function () {
    
        var _initialized = false,
        
        /** @type {boolean} - flag that is set to true if there is any alert. */
        _countInitialized = false,
        
        /** @type {number} - count of the alerts. */
        _oldCount = 0,
        
        _alert_button = '#yukon-alert-button',
        
               _clear_button = '.js-clear-all-yukon-alerts',
        
        /** @type {string} - URL to view all the alerts. */
        _viewAlertUrl = yukon.url('/common/alert/view'),
        
        /** @type {string} - URL to clear all the alerts. */
        _clearAlertUrl = yukon.url('/common/alert/clear'),
        
        /** Handles the click event of alert button. */
        _handleBtnClick = function() {
            if ($('#yukon_alert_popup').is(':visible')) {
                $('#yukon_alert_popup').dialog('close');
            } else {
                $('#alert_body').load(_viewAlertUrl, function() {
                    $('#yukon_alert_popup').dialog('open');
                });
            }
        },
        
        /** Updates the alert count and animates the alert button accordingly.
        *   @param {number} count - alert count.
        */
        _updateCount = function (count) {
            var button = $('#yukon-alert-button');
            button.children('.b-label').html(count);
            if (count > 0) {
                button.addClass('red');
                button.show();
                if (_countInitialized && _oldCount < count) {
                    if($('[data-alert-flash]').attr('data-alert-flash') === 'true') {
                        button.addClass('animated shake')
                        .one(yg.events.animationend, function() { $(this).removeClass('animated shake'); });
                    }
                    if ($('[data-alert-sound]').attr('data-alert-sound') === 'true') {
                        $('#alert-audio')[0].play();
                    }
                }
            } else {
                button.removeClass('red shake animated');
                button.hide();
            }
            _oldCount = count;
            _countInitialized = true;
        },

        _closeAlertWindow = function() {
            $('#yukon_alert_popup').dialog('close');
            $('#alert_body').empty();
        },
        mod = {};

    mod = {
        
        init: function() {
            
            if (_initialized) {
                return;
            }
            
            $(_alert_button).on('click', _handleBtnClick);
            $(_clear_button).on('click', function() { mod.clearAlert(); });
            $(document).on('click', '.js-clear-yukon-alert', function (ev) {
                var alertId = $(this).closest('tr').data('alertId');
                mod.clearAlert(alertId); 
            });
            
            $('#yukon_alert_popup').dialog({
                autoOpen: false, 
                width:'600', 
                height: 'auto', 
                position: { my: 'top', at: 'bottom+3', of: '.outer' }
            });
            
            _initialized = true;
        },

        /** 
         * Handles the alert count and animates the alert button accordingly.
         * @param {Object} data - alert data 
         *       
         */
        countUpdated : function (data) {
            var count = data.count;
            _updateCount(count);
        },

        /** 
         * Clears the alert data.
         * @param {string} alertId- The id associated with the alert. 
         */
        clearAlert : function (alertId) {
            var alertIds = [],
                remainingAlerts;

            if (alertId) {
                alertIds.push(alertId);
                $('[data-alert-id="' + alertId + '"]').remove();
                remainingAlerts = $('#alert-table tbody tr').length;
                _updateCount(remainingAlerts);
            } else {
                $('#alert-table tbody tr').each(function(i, item) {
                    alertIds.push($(item).data('alertId'));
                });
                _updateCount(0);
                _closeAlertWindow();
            }

            $.ajax({
                url: _clearAlertUrl,
                contentType: 'application/json', 
                type: 'POST',
                data: JSON.stringify(alertIds)
            });
        }
    };
    
    return mod;
}());

$(function() {yukon.alerts.init();});