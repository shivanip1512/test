yukon.namespace('yukon.jobs');

/**
 * Module to handle canceling, toggling jobs
 * 
 * @module yukon.jobs
 * @requires JQUERY
 * @requires yukon
 */
yukon.jobs = (function () {
    
    var _initialized = false,
    
    mod = {};

    mod = {
        
        init: function() {
            if (_initialized) {
                return;
            }
            
            $(document).on('yukon.job.cancel', function (ev) {
                var jobId = $(ev.target).data('jobId');
                event.preventDefault();
                //close the dialog
                yukon.dialogConfirm.cancel();
                //submit job cancellation request
                $.ajax({
                     url: yukon.url('/group/scheduledGroupRequestExecutionResults/cancelJob'),
                     dataType: 'json',
                     data: { 'jobId': jobId }
                });
            });

            $(document).on('click', '#jobs-table .js-toggle-job', function (ev) {
                
                var btn = $(ev.target),
                    jobId = btn.closest('tr').data('jobId'); 
                
                $.ajax({
                    url: 'toggleEnabled',
                    dataType: 'json',
                    data: { 'jobId': jobId }
                }).done(function (data, textStatus, jqXHR) {
                    if (data.jobEnabled === false) {
                        $(btn).closest('tr').addClass('subtle');
                        $('#disableSpan_' + jobId).hide();
                        $('#enableSpan_' + jobId).show();
                    } else {
                        $(btn).closest('tr').removeClass('subtle');
                        $('#disableSpan_' + jobId).show();
                        $('#enableSpan_' + jobId).hide();
                    }
                });
            });
            
            _initialized = true;
        },
        
        setTrClassByJobState: function (jobId) {
            return function (data) {
                
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
        },

        buildTooltipText: function (id) {
            return function (data) {
                var tooltipText = data.tooltip;
                $('#' + id).attr('title', tooltipText);
            };
        }
        
    };
    
    return mod;
}());

$(function() { yukon.jobs.init(); });