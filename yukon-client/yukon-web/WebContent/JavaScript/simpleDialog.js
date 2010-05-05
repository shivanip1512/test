var naturalDialogSizes = {};

function adjustDialogSizeAndPosition(dialogId) {
    var dialogDiv = $(dialogId);
    var dialogDimensions = naturalDialogSizes[dialogId];
    if (!dialogDimensions) {
        dialogDimensions = dialogDiv.getDimensions();
        naturalDialogSizes[dialogId] = dialogDimensions;
    }
    dialogDiv.setStyle({
        'width': dialogDimensions.width + "px",
		'height': "auto"
    });

    var viewportDimensions = getViewportDimensions();
    var dialogDimensions = dialogDiv.getDimensions();

    var minPadding = 34;
    var newWidth = dialogDimensions.width;
    if (dialogDimensions.width > viewportDimensions.width - minPadding) {
    	dialogDimensions.width = viewportDimensions.width - minPadding;
        dialogDiv.setStyle({
            'width': dialogDimensions.width + "px"
        });
    }
    var newHeight = dialogDimensions.height;
    if (dialogDimensions.height > viewportDimensions.height - minPadding) {
    	dialogDimensions.height = viewportDimensions.height - minPadding;
    	dialogDiv.setStyle({
    		'height': dialogDimensions.height + "px"
    	});
    }

    var x = (viewportDimensions.width - dialogDimensions.width - 24) / 2;
    var y = (viewportDimensions.height - dialogDimensions.height - 24) / 4;
    dialogDiv.setStyle({
        'top': y + "px",
        'left': x + "px"
    });
}

function openSimpleDialog(dialogId, innerHtmlUrl, title, parameters) {

    if (arguments.length > 2 && title) {
        $(dialogId + '_title').innerHTML = title;
    }

    var onComplete = function(transport, json) {
    	adjustDialogSizeAndPosition(dialogId);
        $(dialogId).show();
    }

    new Ajax.Updater($(dialogId + '_body'), innerHtmlUrl, {
            'evalScripts': true,
            'method': 'post',
            'parameters': parameters,
            'onComplete': onComplete
        });
}

function submitFormViaAjax(dialogId, formId, url, title) {
    if (arguments.length > 3) {
        $(dialogId + '_title').innerHTML = title;
    }
	if (arguments.length < 3 || url == null) {
		url = $(formId).action;
	}
    new Ajax.Updater(dialogId + '_body', url, {
            'evalScripts': true,
            'method': 'post',
            'parameters': $(formId).serialize(true)
        });
    return false; // useful if we want to use this for "onsubmit" on a form
}
