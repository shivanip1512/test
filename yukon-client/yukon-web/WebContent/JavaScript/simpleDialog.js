var naturalDialogSizes = {};

/**
 * Adjust the given dialog's size to ensure it fits within the browser window
 * and adjust its position to center it in that window.
 */
function adjustDialogSizeAndPosition(dialogId) {
    var dialogDiv = $(dialogId);
    var dialogDimensions = naturalDialogSizes[dialogId];
    if (!dialogDimensions) {
        dialogDimensions = {
        		'width' : dialogDiv.getStyle('width'),
        		'height' : dialogDiv.getStyle('height')
        };
        if (dialogDimensions.width == null) {
        	dialogDimensions.width = 'auto';
        }
        if (dialogDimensions.height == null) {
        	dialogDimensions.height = 'auto';
        }
        naturalDialogSizes[dialogId] = dialogDimensions;
    }
    
    //format the arguments for prototype 1.6 changes
    var width = (/auto/).test(dialogDimensions.width) ? dialogDimensions.width : ((/px/).test(dialogDimensions.width) ? dialogDimensions.width : (dialogDimensions.width + "px"));
    var height = (/auto/).test(dialogDimensions.height) ? dialogDimensions.height : ((/px/).test(dialogDimensions.height) ? dialogDimensions.height : (dialogDimensions.height + "px"));
    
    dialogDiv.setStyle({ width: width, height: height });
    
    var viewportDimensions = getViewportDimensions();
    var dialogDimensions = dialogDiv.getDimensions();

    var minPadding = 34;
    var newWidth = dialogDimensions.width;
    if (dialogDimensions.width > viewportDimensions.width - minPadding) {
    	dialogDimensions.width = viewportDimensions.width - minPadding;
        dialogDiv.setStyle({
            width: dialogDimensions.width + "px"
        });
    }
    var newHeight = dialogDimensions.height;
    if (dialogDimensions.height > viewportDimensions.height - minPadding) {
    	dialogDimensions.height = viewportDimensions.height - minPadding;
    	dialogDiv.setStyle({
    		height: dialogDimensions.height + "px"
    	});
    }

    var x = (viewportDimensions.width - dialogDimensions.width - 24) / 2;
    var y = (viewportDimensions.height - dialogDimensions.height - 24) / 4;
    dialogDiv.setStyle({
        top: y + "px",
        left: x + "px"
    });
}

/**
 * Make an AJAX request with the given URL (innerHtmlUrl) and populate the
 * dialog specified by dialogId with the response. This method will show a busy
 * cursor while waiting for the request. If the request contains JSON with an
 * "action" value of "close", the dialog box will be closed instead of
 * populated. Once the dialog box is populated, its size and position will be
 * adjusted to center and ensure it fits.
 * 
 * @param dialogId
 *            The id of the dialog box, normally created with tags:simpleDialog.
 *            Required.
 * @param innerHtmlUrl
 *            The URL which will be used in the request for what to put in the
 *            dialog box. Required.
 * @param title
 *            The title for the dialog box. If this is not specified, the title
 *            of the dialog box will not be changed.
 * @param parameters
 *            Any parameters that need to be passed along with the request.
 * @param skipShow
 *            Set this to true to skip sizing, positioning and displaying the
 *            dialog box.
 * @param method
 *            The HTTP method ('get' or 'post'). 'post' is the default.
 */
function openSimpleDialog(dialogId, innerHtmlUrl, title, parameters, skipShow, method) {
    Yukon.ui.blockPage();

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
    	Yukon.ui.unblockPage();
    }

    new Ajax.Updater($(dialogId + '_body'), innerHtmlUrl, {
            'evalScripts': true,
            'method': method ? method : 'post',
            'parameters': parameters,
            'onComplete': onComplete
        });
}

/**
 * Submit an HTML form and place the returned contents into a dialog box.
 * 
 * @param dialogId
 *            The id of the dialog box, normally created with tags:simpleDialog.
 *            Required.
 * @param formId
 *            The id of the form HTML element which is to be submitted.
 * @param url
 *            The URL which will be used in the request for what to put in the
 *            dialog box. Required.
 * @param title
 *            The title for the dialog box. If this is not specified, the title
 *            of the dialog box will not be changed.
 * @param method
 *            The HTTP method ('get' or 'post'). 'post' is the default.
 */
function submitFormViaAjax(dialogId, formId, url, title, method) {
    return submitFormViaAjaxWithSkipShow(dialogId, formId, url, title, true, method);
}

function submitFormViaAjaxWithSkipShow(dialogId, formId, url, title, skipShow, method) {
    if (arguments.length < 3 || url == null) {
        url = $(formId).action;
    }
    openSimpleDialog(dialogId, url, title, $(formId).serialize(true), skipShow, method);
    return false; // useful if we want to use this for "onsubmit" on a form
}

/**
 * Make a simple AJAX request with the given URL and possibly react. Currently,
 * the only supported reaction is "reload" which cause the page to be reloaded.
 * The action is specified by setting the JSON variable 'action'.
 * 
 * @param url
 *            The URL of the request to make.
 */
function simpleAJAXRequest(url) {
    var successCallback = function(transport, json) {
        hideBusy();
        if (json.action === 'reload') {
            window.location = window.location;
        }
    };

    var errorCallback = function(transport) {
        hideBusy();
        alert('error making request');
    };

    var options = {
            'evalScript': true,
            'method': 'post',
            'onSuccess': successCallback,
            'onFailure': errorCallback
            };
    showBusy();
    new Ajax.Request(url, options);
}

function showSimplePopup(popupId, initialFocus) {
    $(popupId).show();
    if (initialFocus) {
        $(initialFocus).focus();
    } else if ($(popupId).down("input:first[type='text']")) {
        $(popupId).down("input:first[type='text']").focus();
    }
    adjustDialogSizeAndPosition(popupId);
}
