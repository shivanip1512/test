// Methods to help in dealing with AJAX dialog boxes and wizards.

function closeAjaxDialogAndRefresh() {
    jQuery('#ajaxDialog').dialog('close');
    window.location = window.location;
}

jQuery(document).on('yukonDialogSubmit', function(event) {
    jQuery(event.target).find('form').submit();
});

jQuery(document).on('ajaxDialogSubmit', function(event) {
    var target = jQuery(event.target);
    var dialogId = target.attr('id');
    var formId = target.find('form').attr('id');
    
    submitFormViaAjax(dialogId, formId);
});