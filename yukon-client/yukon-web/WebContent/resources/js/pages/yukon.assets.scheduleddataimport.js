yukon.namespace('yukon.assets.scheduleddataimport');

/**
 * Module that handles the behavior on the Scheduled Data Import page.
 * @module yukon.assets.scheduleddataimport
 * @requires JQUERY
 * @requires yukon
 */
yukon.assets.scheduleddataimport = (function() {
    
    'use strict';
    
    /**
     * Display error messages
     * @param {string} id - element id 
     */
    function displayErrors (errorMessages) {
        var errors = $('#errorMessages');
        errors.html(errorMessages);
    }
    
    /**
     * Enable or Disable field
     */
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
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            $(document).on("yukon:scheduledDataImport:delete", function () {
                yukon.ui.blockPage();
                $('#delete-scheduledDataImport-form').submit();
            });
            
            $(document).on('click', '#cancel-btn', function (event) {
                window.history.back();
            });
            
            $(".js-schedule-start-now").click(function(e) {
                $('#errorMessages').html("");
                var selection = $(this);
                var jobId = selection.data('jobId');
                $.ajax({
                     url: yukon.url('/stars/scheduledDataImport/startJob'),
                     dataType: 'json',
                     data: { 'toggleJobId': jobId }
                }).done(function (data) {
                    if (data.error) {
                        displayErrors(data.error);
                    }
                });
            });
            
            $(".js-schedule-toggle").click(function(e) {
                $('#errorMessages').html("");
                var selection = $(this);
                var jobId = selection.data('jobId');
                $.ajax({
                     url: yukon.url('/stars/scheduledDataImport/toggleJob'),
                     dataType: 'json',
                     data: { 'toggleJobId': jobId }
                }).done(function (data) {
                    if (data.error) {
                        displayErrors(data.error);
                    }
                });
            });
            
            $(document).on('yukon:schedule:delete', function (ev) {
                $('#errorMessages').html("");
                var jobId = $(ev.target).data('jobId');
                ev.preventDefault();
                //close the dialog
                yukon.dialogConfirm.cancel();
                $.ajax({
                    url: yukon.url('/stars/scheduledDataImport/' + jobId + '/delete'),
                    type: 'delete'
                }).done(function () {
                    window.location.reload();
                });
            });
            
            $(document).on('click', '#cancel-btn', function (event) {
                window.history.back();
            });
            
            _initialized = true;
        },
        
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
                    deleteJobButton = $('#delete-schedule-btn-' + jobId);
                if (state === 'Disabled') {
                    enableSchedule.show();
                    disableSchedule.hide();
                    //enable -> enable and delete and disable start
                    enableDisableField(startScheduleNow, true);
                    enableDisableField(deleteJobButton, false);
                    enableDisableField(enableSchedule, false);
                } else if (state === 'Running') {
                    enableSchedule.hide();
                    disableSchedule.show();
                    //disable -> start, disable and delete
                    enableDisableField(startScheduleNow, true);
                    enableDisableField(deleteJobButton, true);
                    enableDisableField(disableSchedule, true);
                } else if (state === 'Scheduled') {
                    enableSchedule.hide();
                    disableSchedule.show();
                    //enable -> start and disable and delete
                    enableDisableField(startScheduleNow, false);
                    enableDisableField(deleteJobButton, false);
                    enableDisableField(disableSchedule, false);
                } else if (state === 'Deleted') {
                    enableSchedule.hide();
                    disableSchedule.hide();
                    startScheduleNow.hide();
                    deleteJobButton.hide();
                }
            } 
        },
    };
    
    return mod;
})();

$(function () { yukon.assets.scheduleddataimport.init(); });