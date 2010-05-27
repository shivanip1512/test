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

function openSimpleDialog(dialogId, innerHtmlUrl, title, parameters, skipShow) {
	showBusy();

    if (arguments.length > 2 && title) {
        $(dialogId + '_title').innerHTML = title;
    }

    var onComplete = function(transport, json) {
    	if (json && json.action == 'close') {
    		$(dialogId).hide();
    	}
    	else if (!skipShow) {
    		adjustDialogSizeAndPosition(dialogId);
    		$(dialogId).show();
    	}
    	hideBusy();
    }

    new Ajax.Updater($(dialogId + '_body'), innerHtmlUrl, {
            'evalScripts': true,
            'method': 'post',
            'parameters': parameters,
            'onComplete': onComplete
        });
}

function submitFormViaAjax(dialogId, formId, url, title) {
	if (arguments.length < 3 || url == null) {
		url = $(formId).action;
	}
	openSimpleDialog(dialogId, url, title, $(formId).serialize(true), true);
    return false; // useful if we want to use this for "onsubmit" on a form
}

function showConfirm(popupId, action) {
	$('confirmDialogQuestion').innerHTML = $('confirmDialogQuestion' + popupId).innerHTML;
	var popupDiv = $('confirmDialog');
	popupDiv.getElementsBySelector('.ok')[0].onclick = function() { showBusy(); action(); };
	adjustDialogSizeAndPosition(popupDiv);
	popupDiv.show();
}
