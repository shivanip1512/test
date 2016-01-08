/**
 * Update the state of toggle for the scripts and jobs page.
 */
$(function() {
    $(document).on('change', '.js-scripts-toggle .checkbox-input', function() {
        var checkbox = $(this);
        var form = checkbox.closest('form');
        form.ajaxSubmit({
            error : function(xhr, status, error) {
                yukon.ui.alertError(yg.text.ajaxError);
            }
        });
    });
});
