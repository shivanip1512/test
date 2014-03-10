/**
 * Singleton that manages alerts button and popup
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

yukon.namespace('yukon.alerts');

yukon.alerts = (function () {
    
        var _initialized = false,
        _countInitialized = false,
        _oldCount = 0,
        _alert_button = '#yukon-alert-button',
        _clear_button = '#yukon_clear_alerts_button',
        _viewAlertUrl = yukon.url('/common/alert/view'),
        _clearAlertUrl = yukon.url('/common/alert/clear'),
        
        /* --------------- */
        /* private methods */
        /* --------------- */
        _handleBtnClick = function() {
            if (jQuery('#yukon_alert_popup').is(':visible')) {
                jQuery('#yukon_alert_popup').dialog('close');
            } else {
                jQuery('#alert_body').load(_viewAlertUrl, function() {
                    jQuery('#yukon_alert_popup').dialog('open');
                });
            }
        },

        _updateCount = function (count) {
            var button = jQuery('#yukon-alert-button');
            button.children('.b-label').html(count);
            if (count > 0) {
                button.addClass('red');
                button.show();
                if (_countInitialized && _oldCount < count) {
                    if(jQuery('[data-alert-flash]').attr('data-alert-flash') === 'true') {
                        button.addClass('flash');
                        button.addClass('animated');
                        setTimeout(function(){ 
                                button.removeClass('flash animated'); 
                            }, 1000);
                    }
                    if (jQuery('[data-alert-sound]').attr('data-alert-sound') === 'true') {
                        jQuery('#alert-audio')[0].play();
                    }
                }
            } else {
                button.removeClass('red flash animated');
                button.hide();
            }
            _oldCount = count;
            _countInitialized = true;
        },

        _closeAlertWindow = function() {
            jQuery('#yukon_alert_popup').dialog('close');
            jQuery('#alert_body').empty();
        },
        mod;

    mod = {
        
        /* -------------- */
        /* public methods */
        /* -------------- */
        init: function() {
            if (_initialized) {
                return;
            }
            
            jQuery(_alert_button).on('click', _handleBtnClick);
            jQuery(_clear_button).on('click', function() {mod.clearAlert();});
            
            jQuery('#yukon_alert_popup').dialog({autoOpen: false, width:'600', height: 'auto', position: {my: 'top', at: 'bottom+3', of: '.outer'}});
            
            _initialized = true;
        },

        countUpdated : function (data) {
            var count = data.count;
            _updateCount(count);
        },

        clearAlert : function (alertId) {
            var alertIds = [],
                remainingAlerts;

            if (alertId) {
                alertIds.push(alertId);
                jQuery('#alertTableRow_' + alertId).remove();
                remainingAlerts = jQuery('#alertTable tbody tr').length;
                _updateCount(remainingAlerts);
            } else {
                jQuery('#alertTable tbody tr input').each(function(i, item) {
                    alertIds.push(item.value);
                });
                _updateCount(0);
                _closeAlertWindow();
            }

            jQuery.ajax({
                url: _clearAlertUrl,
                contentType: 'application/json', 
                type: 'POST',
                data: JSON.stringify(alertIds)
            });
        }
    };
    
    return mod;
}());

jQuery(function() {yukon.alerts.init();});