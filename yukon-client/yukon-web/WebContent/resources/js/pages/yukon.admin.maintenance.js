yukon.namespace('yukon.admin.maintenance');
 
/**
 * Module that manages the Maintenance tasks under Admin > Maintenance menu.
 * @module yukon.admin.multispeak
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
                var taskType = $(event.target).find("input[name='toggle']").data('task-type');
                $.post(yukon.url('/admin/maintenance/toggleMaintenanceTaskAjax'), {taskType : taskType}, function (isTaskEnabled) {
                        $(event.target).find("input[name='toggle']").prop('checked', isTaskEnabled);
                });
                $("#js-api-show-popup-" + taskType).dialog('close');
            });
            
            $(document).on('change', '.js-toggleJobEnabled .checkbox-input', function() {
                var jobId = $(this).data('jobId');
                
                $.ajax({
                    url: yukon.url('/admin/maintenance/toggleJobEnabledAjax?jobId=' + jobId)
                });
            });
            
            $(document).on('change', '.js-toggle-maintenance-task .checkbox-input', function() {
                var taskType = $(this).data('task-type'),
                    isEnabled = $(this).is(":checked"),
                    okBtnTest, 
                    dialogTitle,
                    confirmMsg;
                if (isEnabled) {
                    okBtnTest = yg.text.enable;
                    dialogTitle = $("#confirmEnableText").val();
                    confirmMsg = $('#js-api-show-popup-' + taskType).attr('data-confirm-enable-message');
                } else {
                    okBtnTest = yg.text.disable;
                    dialogTitle = $("#confirmDisableText").val();
                    confirmMsg = $('#js-api-show-popup-' + taskType).attr('data-confirm-disable-message');
                }
                yukon.ui.dialog('#js-api-show-popup-' + taskType);
                $('#js-api-show-popup-' + taskType).dialog({ title: dialogTitle });
                $('#js-api-show-popup-' + taskType).closest(".ui-dialog").find('.js-primary-action .ui-button-text').text(okBtnTest);
                $('#js-api-show-popup-' + taskType).text(confirmMsg);
            });
            
            $(document).on('yukon:maintenance:update-task', function() {
                $("#maintenance-task-form").submit();
            });
            
            $(document).on('yukon:maintenance:toggle-task', function() {
                $("#maintenance-task-form").attr('action', yukon.url('/admin/maintenance/toggleMaintenanceTask'));
                $("#maintenance-task-form").submit();
            });
            
            $(document).on('dialogclose', function(event) {
                var toggleButton = $(event.target).attr('data-target');
                toggleButton = toggleButton + " .checkbox-input";
                $(toggleButton).prop('checked', !$(toggleButton).prop("checked"));
            });
            
            _initialized = true;
        }

    };
 
    return mod;
    
})();
 
$(function () { yukon.admin.maintenance.init(); });