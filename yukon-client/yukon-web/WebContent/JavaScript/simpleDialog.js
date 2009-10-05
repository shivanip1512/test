function openSimpleDialog(dialogId, innerHtmlUrl, title, width, height) {
    var dialogDiv = $(dialogId);

    if (arguments.length > 2) {
        $(dialogId + '_title').innerHTML = title;
    }

    var dlgWidth = 0;
    var dlgHeight = 0;
    if (arguments.length > 3) {
        dlgWidth = width;
    }
    if (arguments.length > 4) {
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
        if (navigator.appName.indexOf("Microsoft")!=-1) {
            windowWidth = document.body.offsetWidth;
            windowHeight = document.body.offsetHeight;
        } else {
            windowWidth = window.innerWidth;
            windowHeight = window.innerHeight;
        }

        var dialogWidth = dialogDiv.getWidth();
        var dialogHeight = dialogDiv.getHeight();
        var x = 0, y = 0;
        if (windowWidth > dialogWidth) {
            x = (windowWidth - dialogWidth) / 2;
        }
        if (windowHeight > dialogHeight) {
            y = (windowHeight - dialogHeight) / 2;
        }

        // annoyingly, IE uses the document size and not actually the window
        // height, so the calculated y won't work...
        y = 150;

        dialogDiv.setStyle({
            'top': y + "px",
            'left': x + "px"
        });

        dialogDiv.show();
    }

    var errorCallback = successCallback;

    new Ajax.Updater(dialogId + '_body', innerHtmlUrl, {
            'evalScripts': true,
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
