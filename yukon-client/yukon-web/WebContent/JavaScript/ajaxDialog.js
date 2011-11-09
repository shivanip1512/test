// Methods to help in dealing with AJAX dialog boxes and wizards.

function closeAjaxDialogAndRefresh() {
    jQuery('#ajaxDialog').dialog('close');
    window.location = window.location;
}
