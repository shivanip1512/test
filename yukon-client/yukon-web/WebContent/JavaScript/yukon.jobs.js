var stopJobId;

$(function() {
    
    $(document).on('yukon.dialog.confirm.ok', function(event) {
        event.preventDefault();
        //close the dialog
        yukon.dialogConfirm.cancel();
        //submit job cancellation request
        $.ajax({
             url: "/group/scheduledGroupRequestExecutionResults/cancelJob",
             dataType: 'json',
             data: {'jobId': stopJobId}
         });
    });

    $('.stopButton').on('click', function(event) {
        var stopButton = event.currentTarget;
        stopJobId = stopButton.id.replace('cancel_', '');
    });
    
    $(document).on('click', 'button.toggleEnabled', function(event) {
        var toggleButton = event.currentTarget;
        var jobId = toggleButton.id.substring(7); //omit "toggle_"
        $.ajax({
            url: "toggleEnabled",
            dataType: 'json',
            data: {'jobId': jobId},
            success: function(data) {
                if (data.jobEnabled === false) {
                    $(toggleButton).closest('tr').addClass('subtle');
                    $('#disableSpan_' + jobId).hide();
                    $('#enableSpan_' + jobId).show();
                } else {
                    $(toggleButton).closest('tr').removeClass('subtle');
                    $('#disableSpan_' + jobId).show();
                    $('#enableSpan_' + jobId).hide();
                }
            }
        });
    });
});

function setTrClassByJobState(jobId) {
    return function(data) {
        var jobRow = '#tr_' + jobId,
            state = data.state;
        $(jobRow).removeClass('success subtle');
        if (state === 'Disabled') {
            $(jobRow).addClass('subtle');
            $('#disableSpan_' + jobId).show();
            $('#enableSpan_' + jobId).hide();
            
            $('#jobRunningSpan_' + jobId).hide();
            $('#jobNotRunningSpan_' + jobId).show();
        } else if (state === 'Running') {
            $(jobRow).addClass('success');
            $('#disableSpan_' + jobId).hide();
            $('#enableSpan_' + jobId).show();
            
            $('#jobRunningSpan_' + jobId).show();
            $('#jobNotRunningSpan_' + jobId).hide();
        } else if (state === 'Scheduled') {
            $('#disableSpan_' + jobId).hide();
            $('#enableSpan_' + jobId).show();
            
            $('#jobRunningSpan_' + jobId).hide();
            $('#jobNotRunningSpan_' + jobId).show();
        }
    };
}

function buildTooltipText(elementId) {
    return function(data) {
        var tooltipText = data.tooltip;
        $('#' + elementId).attr('title', tooltipText);
    };
}