// Methods to help in dealing with AJAX dialog boxes and wizards.

function closeAjaxDialogAndRefresh() {
    jQuery('#ajaxDialog').dialog('close');
    window.location = window.location;
}

jQuery(document).bind('yukonDialogSubmit', function(event) {
    jQuery(this).find('form').submit();
});

jQuery(document).bind('yukonDialogHref', function(event) {
    window.location = jQuery(jQuery(this).data('on')).href;
});
