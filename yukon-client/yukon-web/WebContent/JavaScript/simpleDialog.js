function openSimpleDialog(dialogId, innerHtmlUrl, title, parameters, width, height) {
    var dialogDiv = $(dialogId);

    if (arguments.length > 2) {
        $(dialogId + '_title').innerHTML = title;
    }

    var dlgWidth = 800;
    var dlgHeight = 0;
    if (arguments.length > 4) {
        dlgWidth = width;
    }
    if (arguments.length > 5) {
        dlgHeight = height;
    }

    var successCallback = function(transport) {
        if (dlgWidth) {
            dialogDiv.setStyle({
                'width': dlgWidth + "px"
            });
        }
        if (dlgHeight) {
            dialogDiv.setStyle({
                'height': dlgHeight + "px"
            });
        }

        var windowWidth = 790, windowHeight = 580;
        if (Prototype.Browser.IE) {
            windowWidth = document.body.offsetWidth;
            windowHeight = document.body.offsetHeight;
        } else {
            windowWidth = window.innerWidth;
            windowHeight = window.innerHeight;
        }

        var borderWidth = 12;
        var dialogWidth = dialogDiv.getWidth();
        var dialogHeight = dialogDiv.getHeight();
        var x = 0, y = 0;
        if (dialogWidth + borderWidth * 2 > windowWidth - 50) {
            dialogWidth = windowWidth - 50 - 2 * borderWidth;
            dialogDiv.setStyle({
                'width': dialogWidth + "px"
            });
        }
        var dialogWidth = dialogDiv.getWidth();
        x = (windowWidth - dialogWidth) / 2 - borderWidth;

        // For now, we hard-code y.  Once we update prototype, it would be
        // nice to take advantage of document.viewport and roughly center the
        // dialog on the browser window.
        y = 125;

        dialogDiv.setStyle({
            'top': y + "px",
            'left': x + "px"
        });

        dialogDiv.show();
    }

    var errorCallback = successCallback;

    new Ajax.Updater(dialogId + '_body', innerHtmlUrl, {
            'evalScripts': true,
            'parameters': parameters,
            'onSuccess': successCallback,
            'onFailure' : errorCallback
        });
}

function submitFormViaAjax(dialogId, formId, url) {
	if (arguments.length < 3) {
		url = $(formId).action;
	}
    new Ajax.Updater(dialogId + '_body', url, {
            'evalScripts': true,
            'method': 'post',
            'parameters': $(formId).serialize(true)
        });
    return false; // useful if we want to use this for "onsubmit" on a form
}
