yukon.namespace('yukon.assets.scheduleddataimport');

/**
 * Module that handles the behavior on the Scheduled Data Import page.
 * @module yukon.assets.scheduleddataimport
 * @requires JQUERY
 * @requires yukon
 */
yukon.assets.scheduleddataimport = (function() {
    
    'use strict';
    
    var 
    _initialized = false,

    /**
     * Enable or Disable field
     */
    _enableOrDisableField = function (field, disable) {
        field.attr('disabled', disable);
        field.find('*').attr('disabled', disable);
        if (disable) {
            field.css('pointer-events', 'none');
        } else {
            field.css('pointer-events', 'auto');
        }
    },

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
                        $('#errorMessages').html(data.error);
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
                        $('#errorMessages').html(data.error);
                    }
                });
            });
            
            $(document).on('yukon:schedule:delete', function (ev) {
                $('#errorMessages').html("");
                var jobId = $(ev.target).data('jobId');
                //close the dialog
                yukon.dialogConfirm.cancel();
                $.ajax({
                    url: yukon.url('/stars/scheduledDataImport/' + jobId + '/delete'),
                    type: 'delete'
                }).done(function () {
                    window.location.reload();
                });
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
                    _enableOrDisableField(startScheduleNow, true);
                    _enableOrDisableField(deleteJobButton, false);
                    _enableOrDisableField(enableSchedule, false);
                } else if (state === 'Running') {
                    enableSchedule.hide();
                    disableSchedule.show();
                    //disable -> start, disable and delete
                    _enableOrDisableField(startScheduleNow, true);
                    _enableOrDisableField(deleteJobButton, true);
                    _enableOrDisableField(disableSchedule, true);
                } else if (state === 'Scheduled') {
                    enableSchedule.hide();
                    disableSchedule.show();
                    //enable -> start and disable and delete
                    _enableOrDisableField(startScheduleNow, false);
                    _enableOrDisableField(deleteJobButton, false);
                    _enableOrDisableField(disableSchedule, false);
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