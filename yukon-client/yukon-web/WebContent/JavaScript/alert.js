var alert_ajaxUpdaterObject = null;
var alertUrl = '/spring/common/alert';
var viewAlertUrl = alertUrl + '/view';
var clearAlertUrl = alertUrl + '/clear';

function alert_showPopup(alertContent) {
    if (alert_ajaxUpdaterObject == null) {
        alert_ajaxUpdaterObject = new Ajax.PeriodicalUpdater('alertBody', viewAlertUrl, {
                'method': 'POST', 'frequency': 5, 'decay': 1
        });
    }
        
    alertContent.show();
}

function alert_handleOnClick() {
    var alertContent = $('alertContent');

    if (alertContent.visible()) {
        alert_closeAlertWindow();
        return;    
    }
    
    alert_showPopup(alertContent);
}

function alert_handleCountUpdate(data) {
    var alertCount = data.value;
    alert_updateCount(alertCount);
}

function alert_updateCount(alertCount) {
    if (alertCount > 0) {
        var oldCount = $('alertCountSpan').innerHTML;
        var oldCount = YukonClientPersistance.getState("alertMemory", "lastCount", $('alertCountSpan').innerHTML);
        if (alertCount > oldCount) {
            // we only care if the number is going up
            var endColor = $('alertSpan').getStyle("background-color");
            // Highlight would be nice, but it looks stupid unless I hardcode the menu background as the endcolor
            //new Effect.Highlight($('alertSpan'), {'duration': 3.5, 'startcolor': '#FFE900', 'endcolor': '#0066CC'}); 
            var doAutoPopup = stickyCheckboxes_retrieve("alert_autoPopup");
            if (doAutoPopup) {
                alert_showPopup($('alertContent'));
            }
        }
        $('alertSpan').show();
    } else {
        $('alertSpan').hide();
        if ($('alertContent').visible()) {
            alert_closeAlertWindow();
        }
    }
    $('alertCountSpan').innerHTML = alertCount;
    YukonClientPersistance.persistState("alertMemory", "lastCount", alertCount);
}

function alert_closeAlertWindow() {
    alert_ajaxUpdaterObject.stop();    
    alert_ajaxUpdaterObject = null;
    $('alertContent').hide();    
    $('alertBody').childElements().invoke("remove");
}

function alert_clearAlert(alertId) {
    var alertIds = new $A();
    
    if (alertId) {
        alertIds.push(alertId);
        $('alertTableRow_' + alertId).remove();
        var remainingAlerts = $$('table#alertTable tbody tr').size();
        alert_updateCount(remainingAlerts);
    } else {
        alertIds = $$('table#alertTable tbody tr input').pluck('value');
        alert_updateCount(0);
    }
    
    new Ajax.Request(clearAlertUrl, {
        'method': 'POST', 
        'parameters': { 'jsonString': alertIds.toJSON()}
    });    
}

