// TODO: DEPRECATED: Rework this functionality into JSP.

/**
 * Moved here from scheduledscripts.jsp
 */
jQuery(function() {
    // DEPRECATED
    new Ajax.PeriodicalUpdater('main', '${url}?sortBy=${sortBy}&descending=${descending}', {
        "method": 'post', "frequency": 5, "decay": 1
    });
});

function macsscheduledscripts_startFocus() {
    $("startform").time[1].checked = true;
}

function macsscheduledscripts_stopFocus() {
    $("stopform").time[1].checked = true;
}

function macsscheduledscripts_updater(url, sortBy, descending) {
	// DEPRECATED
    new Ajax.Updater('main', url, {
        'method': 'POST', parameters: { 'sortBy': sortBy, 'descending': descending },
        onSuccess: function() {
            setTimeout(function() {macsscheduledscripts_updater(url,sortBy,descending); }, 5000);
        }
    });
}
