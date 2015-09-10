/**
 * Update the state of toggle for all monitors.
 */
$(function() {
    $(document).on('change', '.js-monitor-toggle .checkbox-input', function() {
        var checkbox = $(this);
        var monitorType = checkbox.data('monitorType');
        var monitorId = checkbox.data('monitorId');

        $.ajax(yukon.url('/amr/' + monitorType + '/' + monitorId + '/toggle'));
    });
});