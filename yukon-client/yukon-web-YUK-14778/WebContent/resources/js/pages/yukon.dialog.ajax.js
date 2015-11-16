// Methods to help in dealing with AJAX dialog boxes and wizards.

function closeAjaxDialogAndRefresh() {
    $('#ajaxDialog').dialog('close');
    window.location = window.location;
}

$(document).on('yukonDialogSubmit', function(event) {
    $(event.target).find('form').submit();
});

$(document).on('ajaxDialogSubmit', function(event) {
    var target = $(event.target);
    var dialogId = target.attr('id');
    var formId = target.find('form').attr('id');
    
    submitFormViaAjax(dialogId, formId);
});