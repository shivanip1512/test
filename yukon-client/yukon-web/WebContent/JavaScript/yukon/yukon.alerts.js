
/**
 * Singleton that manages the javascript for Yukon alerts
 * 
 * @requires jQuery 1.6+
 */

if (typeof Yukon === 'undefined') {
    Yukon = {};
}
if (typeof Yukon.Alerts === 'undefined') {
    Yukon.Alerts = {
        _initialized: false,
        
        _alert_button : "#yukon_alert_button",
        _clear_button : "#yukon_clear_alerts_button",
        _viewAlertUrl : "/common/alert/view",
        _clearAlertUrl : "/common/alert/clear",
        
        init: function() {
            if(this._initialized) return;
            
            jQuery(this._alert_button).on("click", this._handleBtnClick);
            jQuery(this._clear_button).on("click", function() {Yukon.Alerts.clearAlert();});
            
            jQuery("#yukon_alert_popup").dialog({autoOpen: false, width:"auto", height: "auto"});
            
            this._initialized = true;
        },
        
        /* -------------- */
        /* public methods */
        /* -------------- */
        countUpdated : function (data) {
            var count = data.count;
            Yukon.Alerts._updateCount(count);
        },
        
        clearAlert : function (alertId) {
            var alertIds = [];
            
            if (alertId) {
                alertIds.push(alertId);
                jQuery("#alertTableRow_" + alertId).remove();
                var remainingAlerts = jQuery("#alertTable tbody tr").length;
                Yukon.Alerts._updateCount(remainingAlerts);
            } else {
                jQuery("#alertTable tbody tr input").each(function(i, item) {
                    alertIds.push(item.value);
                });
                Yukon.Alerts._updateCount(0);
                Yukon.Alerts._closeAlertWindow();
            }
            
            jQuery.ajax({url: Yukon.Alerts._clearAlertUrl, type: "POST", data: {'jsonString': JSON.stringify(alertIds)}});
        },
        
        /* --------------- */
        /* private methods */
        /* --------------- */
        _handleBtnClick : function() {
            if (jQuery("#yukon_alert_popup").is(":visible")) {
                jQuery("#yukon_alert_popup").dialog("close");
            } else {
                jQuery("#alert_body").load(Yukon.Alerts._viewAlertUrl, function() {
                    jQuery("#yukon_alert_popup").dialog("open");
                });
            }
        },
        
        _updateCount : function (count) {
            var button = jQuery("#yukon_alert_button");
            button.children(".label").html(count);
            if (count > 0) {
                button.show();
            } else {
                button.hide();
            }
        },
        
        _closeAlertWindow : function() {
            jQuery("#yukon_alert_popup").dialog("close");
            jQuery("#alert_body").empty();
        }
        
    };
}

jQuery(function() {
    Yukon.Alerts.init();
});