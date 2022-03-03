/**
 * Module that manages system wide alerts on every page.
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
    
    /** @type {string} - URL to clear all the alerts. */
    _clearAlertUrl = yukon.url('/common/alert/clear'),
    
    /** 
     * Updates the alert count and animates the alert button accordingly.
     * @param {number} count - alert count.
     */
    _updateCount = function (count) {
        
        var button = $('.yukon-alert-button');
        
        button.children('.b-label').html(count);
        if (count > 0) {
            
            button.addClass('red');
            button.show();
            
            if (_countInitialized && _oldCount < count) {
                if($('[data-alert-flash]').attr('data-alert-flash') === 'true') {
                    button.addClass('animate__animated animate__shakeX')
                    .one(yg.events.animationend, function () { $(this).removeClass('animate__animated animate__shakeX'); });
                }
                if ($('[data-alert-sound]').attr('data-alert-sound') === 'true') {
                    $('#alert-audio')[0].play();
                }
            }
        } else {
            button.removeClass('red animate__shakeX animate__animated');
            button.hide();
        }
        _oldCount = count;
        _countInitialized = true;
    },
    
    _closeAlertWindow = function () {
        $('#yukon_alert_popup').dialog('close');
        $('#alert_body').empty();
    },
    
    mod = {
        
        init: function () {
            
            if (_initialized) return;
            
            $(document).on('click', '.js-clear-all-yukon-alerts', function (ev) { mod.clearAlert(); });
            
            $(document).on('click', '.js-clear-yukon-alert', function (ev) {
                var alertId = $(this).closest('tr').data('alertId');
                mod.clearAlert(alertId); 
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
            
            var data = {
                alertIds : alertIds
            };

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
                data: JSON.stringify(data)
            });
        }
    };
    
    return mod;
}());

$(function () { yukon.alerts.init(); });