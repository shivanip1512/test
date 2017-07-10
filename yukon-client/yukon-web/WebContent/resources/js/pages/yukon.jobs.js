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
    
    /**
     * Display error messages
     * @param {string} id - element id 
     */
    function displayErrors (errorMessages) {
        var errors = $('#errorMessages');
        errors.html(errorMessages);
    }
    
    function enableDisableField (field, disable) {
        field.attr('disabled', disable);
        field.find('*').attr('disabled', disable);
        if (disable) {
            field.css('pointer-events', 'none');
        } else {
            field.css('pointer-events', 'auto');
        }
    }
    
    var _initialized = false,

    mod = {
        
        init: function() {
            if (_initialized) {
                return;
            }
            
            $(".js-schedule-toggle").click(function(e){
                $('#errorMessages').html("");
                var selection = $(this);
                var jobId = selection.data('jobId');
                //submit toggle request
                $.ajax({
                     url: yukon.url('/group/scheduledGroupRequestExecution/toggleJob'),
                     dataType: 'json',
                     data: { 'toggleJobId': jobId }
                }).done(function (data) {
                    if (data.error) {
                        displayErrors(data.error);
                    } else {
                        window.location.reload();
                    }
                });
            });
            
            $(".js-schedule-start-now").click(function(e){
                $('#errorMessages').html("");
                var selection = $(this);
                var jobId = selection.data('jobId');

                //submit start request
                $.ajax({
                     url: yukon.url('/group/scheduledGroupRequestExecution/startJob'),
                     dataType: 'json',
                     data: { 'toggleJobId': jobId }
                }).done(function (data) {
                    if (data.error) {
                        displayErrors(data.error);
                    }
                });
            });
            
            $(document).on('yukon:schedule:cancel', function (ev) {
                $('#errorMessages').html("");
                var jobId = $(ev.target).data('jobId');
                ev.preventDefault();
                //close the dialog
                yukon.dialogConfirm.cancel();
                //submit job cancellation request
                $.ajax({
                     url: yukon.url('/group/scheduledGroupRequestExecution/cancelJob'),
                     dataType: 'json',
                     data: { 'toggleJobId': jobId }
                }).done(function () {
                    window.location.reload();
                });
            });
            
            $(document).on('yukon:schedule:cancelScheduled', function (ev) {
                $('#errorMessages').html("");
                var jobId = $(ev.target).data('jobId');
                ev.preventDefault();
                //close the dialog
                yukon.dialogConfirm.cancel();
                //submit job cancellation request
                $.ajax({
                     url: yukon.url('/group/scheduledGroupRequestExecution/cancelScheduledJob'),
                     dataType: 'json',
                     data: { 'toggleJobId': jobId }
                }).done(function () {
                    window.location.reload();
                });
            });
            
            $(document).on('yukon:schedule:start', function (ev) {
                $('#errorMessages').html("");
                var jobId = $(ev.target).data('jobId');
                //set future start if date time was selected
                var dateTimeVisible = $('#' + jobId + '-cron-exp-one-time').is(":visible"); 
                var futureStart = dateTimeVisible ? true : false;
                $('#' + jobId + '_future-start').val(futureStart);
                var data = $('#startScheduleForm-' + jobId).serialize();
                //submit job start request
                $.ajax({
                     url: yukon.url('/group/scheduledGroupRequestExecution/startJob?' + data),
                     dataType: 'json',
                     data: { 'toggleJobId': jobId} 
                }).done(function (data) {
                    if (data.error) {
                        var errors = $('#errorMsg');
                        errors.html(data.error);
                    } else {
                        //close the dialog
                        var dialog = $("#startScheduleDialog-" + jobId);
                        dialog.dialog('destroy');
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
                    nextRun = $(jobRow).find('.nextRunDate').text().trim(),
                    state = data.state,
                    jobRunning = $('#jobRunningSpan_' + jobId),
                    jobNotRunning = $('#jobNotRunningSpan_' + jobId),
                    enableSchedule = $('#enable-schedule-' + jobId),
                    disableSchedule = $('#disable-schedule-' + jobId),
                    startScheduleNow = $('#start-schedule-' + jobId),
                    cancelJobButton = $('#cancel-job-btn-' + jobId),
                    startScheduleButton = $('#startScheduleButton-' + jobId),
                    cancelScheduleButton = $('#cancel-scheduled-job-btn-' + jobId);
                
                $(jobRow).removeClass('success subtle');
                cancelScheduleButton.hide();
                cancelJobButton.show();
                if (state === 'Disabled') {
                    $(jobRow).addClass('subtle');
                    jobRunning.hide();
                    jobNotRunning.show();
                    //allow enable and disable other options
                    enableDisableField(startScheduleNow, true);
                    enableDisableField(enableSchedule, false);
                    enableDisableField(disableSchedule, true);
                    enableDisableField(cancelJobButton, true);
                    enableDisableField(startScheduleButton, true);
                    //enableDisableField(cancelScheduleButton, true);
                } else if (state === 'Running') {
                    $(jobRow).addClass('success');
                    jobRunning.show();
                    jobNotRunning.hide();
                    //disable start, enable/disable and enable cancel
                    enableDisableField(startScheduleNow, true);
                    enableDisableField(enableSchedule, true);
                    enableDisableField(disableSchedule, true);
                    enableDisableField(cancelJobButton, false);
                    enableDisableField(startScheduleButton, true);
                    //enableDisableField(cancelScheduleButton, true);
                } else if (state === 'Scheduled') {
                    jobRunning.hide();
                    jobNotRunning.show();
                    //enable start and disable, disable other options
                    enableDisableField(startScheduleNow, false);
                    enableDisableField(enableSchedule, true);
                    enableDisableField(disableSchedule, false);
                    enableDisableField(cancelJobButton, true);
                    enableDisableField(startScheduleButton, false);
                    //allow users to cancel scheduled manual jobs
                    if (nextRun != 'N/A') {
                        cancelJobButton.hide();
                        cancelScheduleButton.show();
                        enableDisableField(cancelScheduleButton, false);
                    }
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