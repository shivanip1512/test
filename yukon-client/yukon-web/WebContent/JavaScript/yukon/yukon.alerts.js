
/**
 * Singleton that manages the javascript for Yukon alerts
 * 
 * @requires jQuery 1.6+
 */

var Yukon = (function (yukonMod) {
    return yukonMod;
})(Yukon || {});
Yukon.namespace('Yukon.Alerts');
Yukon.Alerts = (function () {
    var initialized = false, 
        _alert_button = "#yukon-alert-button",
        _clear_button = "#yukon_clear_alerts_button",
        _viewAlertUrl = "/common/alert/view",
        _clearAlertUrl = "/common/alert/clear",
        
        /* --------------- */
        /* private methods */
        /* --------------- */
        _handleBtnClick = function() {
            if (jQuery("#yukon_alert_popup").is(":visible")) {
                jQuery("#yukon_alert_popup").dialog("close");
            } else {
                jQuery("#alert_body").load(_viewAlertUrl, function() {
                    jQuery("#yukon_alert_popup").dialog("open");
                });
            }
        },

        _updateCount = function (count) {
            var button = jQuery("#yukon-alert-button");
            button.children(".label").html(count);
            if (count > 0) {
                button.addClass('red');
                button.show();
            } else {
                button.removeClass('red');  
                button.hide();
            }
        },

        _closeAlertWindow = function() {
            jQuery("#yukon_alert_popup").dialog("close");
            jQuery("#alert_body").empty();
        },
        alertMod;

    alertMod = {

        
        /* -------------- */
        /* public methods */
        /* -------------- */
        init: function() {
            if (initialized) {
                return;
            }
            
            jQuery(_alert_button).on("click", _handleBtnClick);
            jQuery(_clear_button).on("click", function() {alertMod.clearAlert();});
            
            jQuery("#yukon_alert_popup").dialog({autoOpen: false, width:"auto", height: "auto", position: {my: "top", at: "bottom+3", of: ".outer"} });
            
            initialized = true;
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
                jQuery("#alertTableRow_" + alertId).remove();
                remainingAlerts = jQuery("#alertTable tbody tr").length;
                _updateCount(remainingAlerts);
            } else {
                jQuery("#alertTable tbody tr input").each(function(i, item) {
                    alertIds.push(item.value);
                });
                _updateCount(0);
                _closeAlertWindow();
            }

            jQuery.ajax({url: _clearAlertUrl, type: "POST", data: {'jsonString': JSON.stringify(alertIds)}});
        }
    };
    return alertMod;
}());


jQuery(function() {
    Yukon.Alerts.init();
});