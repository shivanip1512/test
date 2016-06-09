yukon.namespace('yukon.jobs');

/**
 * Module to handle canceling, toggling jobs
 * 
 * @module yukon.jobs
 * @requires JQUERY
 * @requires yukon
 * @requires yukon.dialog.confirm
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
                ev.preventDefault();
                //close the dialog
                yukon.dialogConfirm.cancel();
                //submit job cancellation request
                $.ajax({
                     url: yukon.url('/group/scheduledGroupRequestExecutionResults/cancelJob'),
                     dataType: 'json',
                     data: { 'jobId': jobId }
                });
            });
            
            $(".js-schedule-toggle").click(function(e){                
                var selection = $(this);
                var jobId = selection.data('jobId');
                //submit toggle request
                $.ajax({
                     url: yukon.url('/group/scheduledGroupRequestExecution/toggleJob'),
                     dataType: 'json',
                     data: { 'toggleJobId': jobId }
                });
            });
            
            $(".js-schedule-start").click(function(e){                
                var selection = $(this);
                var jobId = selection.data('jobId');
                //submit start request
                $.ajax({
                     url: yukon.url('/group/scheduledGroupRequestExecution/startJob'),
                     dataType: 'json',
                     data: { 'toggleJobId': jobId }
                });
            });
            
            $(document).on('yukon.schedule.cancel', function (ev) {
                var jobId = $(ev.target).data('jobId');
                ev.preventDefault();
                //close the dialog
                yukon.dialogConfirm.cancel();
                //submit job cancellation request
                $.ajax({
                     url: yukon.url('/group/scheduledGroupRequestExecution/cancelJob'),
                     dataType: 'json',
                     data: { 'toggleJobId': jobId }
                });
            });
            
            $(document).on('yukon:schedule:start', function (ev) {
                var jobId = $(ev.target).data('jobId');
                //set future start
                var dateTimeVisible = $('#' + jobId + '-cron-exp-one-time').is(":visible"); 
                var futureStart = dateTimeVisible ? true : false;
                $('#' + jobId + '_future-start').val(futureStart);
                 
                ev.preventDefault();
                //close the dialog
                var dialog = $("#startScheduleDialog-" + jobId);
                dialog.dialog('close');
                
                yukon.dialogConfirm.cancel();
                //submit job start request
                $.ajax({
                     url: yukon.url('/group/scheduledGroupRequestExecution/startJob?' + $('#startScheduleForm').serialize()),
                     dataType: 'json',
                     data: { 'toggleJobId': jobId} 
                });
            });
            
            $(".js-cancel").click(function(e){
                var dialog = $(this).closest("#startScheduleOptions");
                dialog.dialog('close');
            });

            $(document).on('change', '.js-toggle-job .checkbox-input', function() {
                var checkbox = $(this);
                var jobId = checkbox.data('jobId');
                
                $.ajax({
                    url: yukon.url('/group/scheduledGroupRequestExecutionResults/toggleEnabled'),
                    dataType: 'json',
                    data: { 'jobId': jobId }
                }).done(function (data, textStatus, jqXHR) {
                    if (data.jobEnabled === false) {
                        $(checkbox).closest('tr').addClass('subtle');
                    } else {
                        $(checkbox).closest('tr').removeClass('subtle');
                    }
                });
            });
            
            _initialized = true;
        },
        
        /**
         * Display jobs based on the state of the job.
         * @param  {number} jobId - JobID for the job.
         */
        setTrClassByJobState: function (jobId) {
            return function (data) {
                
                var jobRow = '#tr_' + jobId,
                    state = data.state;
                
                $(jobRow).removeClass('success subtle');
                if (state === 'Disabled') {
                    $(jobRow).addClass('subtle');
                    $('#toggle_' + jobId).removeAttr("disabled");
                    
                    $('#jobRunningSpan_' + jobId).hide();
                    $('#jobNotRunningSpan_' + jobId).show();
                    //show enable and hide other options
                    $('#enable-schedule-' + jobId).show();
                    $('#disable-schedule-' + jobId).hide();
                    $('#startScheduleButton-' + jobId).hide();
                    $('#start-schedule-' + jobId).hide();
                    $('#cancel-job-btn-' + jobId).hide();
                } else if (state === 'Running') {
                    $(jobRow).addClass('success');
                    $('#toggle_' + jobId).attr("disabled", true);
                    
                    $('#jobRunningSpan_' + jobId).show();
                    $('#jobNotRunningSpan_' + jobId).hide();
                    //hide start, enable/disable and show cancel
                    $('#enable-schedule-' + jobId).hide();
                    $('#disable-schedule-' + jobId).hide();
                    $('#startScheduleButton-' + jobId).hide();
                    $('#start-schedule-' + jobId).hide();
                    $('#cancel-job-btn-' + jobId).show();
                } else if (state === 'Scheduled') {
                    $('#toggle_' + jobId).removeAttr("disabled");
                    
                    $('#jobRunningSpan_' + jobId).hide();
                    $('#jobNotRunningSpan_' + jobId).show();
                    //show start and disable, hide other options
                    $('#startScheduleButton-' + jobId).show();
                    $('#start-schedule-' + jobId).show();
                    $('#cancel-job-btn-' + jobId).hide();
                    $('#enable-schedule-' + jobId).hide();
                    $('#disable-schedule-' + jobId).show();
                }
            };
        },

        /**
         * Build  tool tip.
         * @param {string} id - element id 
         */
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