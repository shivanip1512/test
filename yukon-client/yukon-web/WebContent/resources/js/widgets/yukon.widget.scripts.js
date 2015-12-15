/**
 * Update the state of toggle for the scripts page.
 */
$(function() {
    $(document).on('change', '.js-scripts-toggle .checkbox-input', function() {
        var checkbox = $(this);
        var scriptId = checkbox.data('scriptId');
        var form = $('#scriptsForm_' + scriptId);
        form.submit();
    });
});