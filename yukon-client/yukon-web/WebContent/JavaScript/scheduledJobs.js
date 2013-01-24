var stopJobId;
jQuery(function() {
	jQuery(document).on('yukonDialogConfirmOk', function(event) {
	    event.preventDefault();
	    //close the dialog
	    Yukon.Dialog.ConfirmationManager.cancel();
	    //submit job cancellation request
	    jQuery.ajax({
	 		url: "/group/scheduledGroupRequestExecutionResults/cancelJob",
	 		dataType: 'json',
	 		data: {'jobId': stopJobId}
	 	});
	});
	 
	jQuery('table#jobsTable button.stopButton').on('click', function(event) {	
		var stopButton = event.currentTarget;
		stopJobId = stopButton.id.replace('cancel_', '');
	});
});

jQuery('table#jobsTable button.toggleEnabled').live('click', function(event) {
    var toggleButton = event.currentTarget;
    var jobId = toggleButton.id.substring(7); //omit "toggle_"
    jQuery.ajax({
        url: "toggleEnabled",
        dataType: 'json',
        data: {'jobId': jobId},
        success: function(data) {
            if (data.jobEnabled === false) {
                jQuery(toggleButton).closest('tr').addClass('subtle');
                jQuery('#disableSpan_' + jobId).hide();
                jQuery('#enableSpan_' + jobId).show();
            } else {
                jQuery(toggleButton).closest('tr').removeClass('subtle');
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
        jQuery(jobRow).removeClass('success subtle');
        var state = data.get('state');
        if (state == 'Disabled') {
            jQuery(jobRow).addClass('subtle');
            jQuery('#disableSpan_' + jobId).hide();
            jQuery('#enableSpan_' + jobId).show();
            
            jQuery('#jobRunningSpan_' + jobId).hide();
            jQuery('#jobNotRunningSpan_' + jobId).show();
        } else if (state == 'Running') {
            jQuery(jobRow).addClass('success');
            jQuery('#disableSpan_' + jobId).hide();
            jQuery('#enableSpan_' + jobId).hide();
            
            jQuery('#jobRunningSpan_' + jobId).show();
            jQuery('#jobNotRunningSpan_' + jobId).hide();
        } else if (state == 'Scheduled') {
            jQuery('#disableSpan_' + jobId).show();
            jQuery('#enableSpan_' + jobId).hide();
            
            jQuery('#jobRunningSpan_' + jobId).hide();
            jQuery('#jobNotRunningSpan_' + jobId).show();
        }
    };
}

function buildTooltipText(elementId) {
    //assumes data is of type Hash ($H) -- this will need to change with prototype replacement
    return function(data) {
        var tooltipText = data.get('tooltip');
        jQuery('#' + elementId).attr('title', tooltipText);
    };
}