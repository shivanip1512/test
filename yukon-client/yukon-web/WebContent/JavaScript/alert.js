var alertUrl = '/spring/common/alert';
var viewAlertUrl = alertUrl + '/view';
var clearAlertUrl = alertUrl + '/clear';

function alert_showPopup() {
    // this method is also called just to cause an update
    new Ajax.Updater('alertBody', viewAlertUrl);
        
    $('alertContent').show();
}

function alert_handleOnClick() {
    if ($('alertContent').visible()) {
        alert_closeAlertWindow();
        return;    
    }
    
    alert_showPopup();
}

function alert_handleCountUpdate(data) {
    var alertCount = data.count;
    var lastId = data.lastId;
    alert_updateCount(alertCount);
    var previousLastId = YukonClientPersistance.getState("alertMemory", "lastId", lastId);
    var remainingAlerts = $$('table#alertTable tbody tr').size();
    var alertCountChanged = remainingAlerts != alertCount;
    if (lastId > previousLastId) {
        var doAutoPopup = stickyCheckboxes_retrieve("alert_autoPopup");
        if (doAutoPopup) {
            alert_showPopup();
        }
    } else if (alertCountChanged) {
        if ($('alertContent').visible()) {
            // call showPopup to cause refresh
            alert_showPopup();
            return;    
        }
    }
    YukonClientPersistance.persistState("alertMemory", "lastId", lastId);
}

function alert_updateCount(alertCount) {
    if ($('alertCountSpan')) {
        $('alertCountSpan').innerHTML = alertCount;
    }
    if (alertCount > 0) {
        if ($('alertSpan')) {
            $('alertSpan').show();
        }
    } else {
        if ($('alertSpan')) {
            $('alertSpan').hide();
        }
        if ($('alertContent').visible()) {
            alert_closeAlertWindow();
        }
    }
}

function alert_closeAlertWindow() {
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

