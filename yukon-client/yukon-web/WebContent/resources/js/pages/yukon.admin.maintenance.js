yukon.namespace('yukon.admin.maintenance');
 
/**
 * Module that manages the Maintenance tasks under Admin > Maintenance menu.
 * @module yukon.admin.maintenance
 * @requires JQUERY
 * @requires yukon
 */
yukon.admin.maintenance = (function () {
 
    'use strict';
 
    var
    _initialized = false,
 
    mod = {
 
        /** Initialize this module. */
        init: function () {
 
            if (_initialized) return;
            
            $(document).on('yukon:maintenance:toggle-task-ajax', function(event) {
                var taskType = $(event.target).data('task-type');
                var checkBoxSelector = $(event.target).data('target');
                $.post(yukon.url('/admin/maintenance/toggleMaintenanceTaskAjax'), {taskType : taskType}, function (isTaskEnabled) {
                        $(checkBoxSelector + " .checkbox-input").prop('checked', isTaskEnabled);
                });
                $("#js-api-show-popup-" + taskType).dialog('close');
            });
            
            $(document).on('change', '.js-toggleJobEnabled .checkbox-input', function() {
                var jobId = $(this).data('jobId');
                $.post(yukon.url('/admin/maintenance/toggleJobEnabledAjax'), {jobId : jobId});
            });
            
            $(document).on('click', '.js-toggle-job', function() {
                $("#maintenance-job-form").attr('action', yukon.url('/admin/maintenance/toggleJobEnabled'));
                $("#maintenance-job-form").submit();
            });
            
            $(document).on('change', '.js-toggle-maintenance-task .checkbox-input', function() {
                $(this).prop('checked', !$(this).prop("checked"));
                var taskType = $(this).data('task-type'),
                    isEnabled = !$(this).is(":checked"),
                    popup = $('#js-api-show-popup-' + taskType),
                    okBtnText = isEnabled ? yg.text.enable : yg.text.disable,
                    dialogTitle = isEnabled ? $("#confirmEnableText").val() : $("#confirmDisableText").val(),
                    confirmMsg = isEnabled ? popup.attr('data-confirm-enable-message') : popup.attr('data-confirm-disable-message');
                popup.dialog({ 
                    title: dialogTitle,
                    buttons: yukon.ui.buttons({ okText: okBtnText, event: 'yukon:maintenance:toggle-task-ajax' })
                });
                popup.html(confirmMsg);
            });
            
            $(document).on('yukon:maintenance:update-task', function() {
                $("#maintenance-task-form").submit();
            });
            
            $(document).on('yukon:maintenance:toggle-task', function() {
                $("#maintenance-task-form").attr('action', yukon.url('/admin/maintenance/toggleMaintenanceTask'));
                $("#maintenance-task-form").submit();
            });
            
            _initialized = true;
        }

    };
 
    return mod;
    
})();
 
$(function () { yukon.admin.maintenance.init(); });