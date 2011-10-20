jQuery('table#jobsTable button.toggleEnabled').live('click', function(event) {
    var toggleButton = event.currentTarget;
    var jobId = toggleButton.id.substring(7); //omit "toggle_"
    jQuery.ajax({
        url: "toggleEnabled",
        dataType: 'json',
        data: {'jobId': jobId},
        success: function(data) {
            var jobEnabled = data.jobEnabled;
            if (jobEnabled == false) {
                jQuery(toggleButton).closest('tr').addClass('subtleGray');
                jQuery('#disableSpan_' + jobId).hide();
                jQuery('#enableSpan_' + jobId).show();
            } else {
                jQuery(toggleButton).closest('tr').removeClass('subtleGray');
                jQuery('#disableSpan_' + jobId).show();
                jQuery('#enableSpan_' + jobId).hide();
            }
        }
    });
});

function setTrClassByJobState(jobId) {
    //assumes data is of type Hash
    return function(data) {
        var jobRow = '#tr_' + jobId;
        jQuery(jobRow).removeClass('okGreen');
        jQuery(jobRow).removeClass('subtleGray');
        var state = data.get('state');
        if (state == 'DISABLED') {
            jQuery(jobRow).addClass('subtleGray');
            jQuery('#disableSpan_' + jobId).hide();
            jQuery('#enableSpan_' + jobId).show();
            
            jQuery('#jobRunningSpan_' + jobId).hide();
            jQuery('#jobNotRunningSpan_' + jobId).show();
        } else if (state == 'RUNNING') {
            jQuery(jobRow).addClass('okGreen');
            jQuery('#disableSpan_' + jobId).hide();
            jQuery('#enableSpan_' + jobId).hide();
            
            jQuery('#jobRunningSpan_' + jobId).show();
            jQuery('#jobNotRunningSpan_' + jobId).hide();
        } else if (state == 'ENABLED') {
            jQuery('#disableSpan_' + jobId).show();
            jQuery('#enableSpan_' + jobId).hide();
            
            jQuery('#jobRunningSpan_' + jobId).hide();
            jQuery('#jobNotRunningSpan_' + jobId).show();
        }
    };
}

function buildTooltipText(elementId) {
    //assumes data is of type Hash
    return function(data) {
        var tooltipText = data.get('tooltip');
        jQuery('#' + elementId).attr('title', tooltipText);
    };
}