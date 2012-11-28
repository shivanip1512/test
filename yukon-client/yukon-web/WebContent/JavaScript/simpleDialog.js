/**
 * Adjust the given dialog's size to ensure it fits within the browser window
 * and adjust its position to center it in that window.
 */
function adjustDialogSizeAndPosition(dialogId) {
    var dialogDiv = $(dialogId);
    // If the dialog has more specific area to scroll, scroll that one instead of the whole dialog.
    var scrollAreaDiv = $(dialogId).down('.boxContainer_content');

    // Try to force things back to their natural state.
    dialogDiv.setStyle({'maxWidth': '', 'maxHeight': ''});
    if (scrollAreaDiv) {
        scrollAreaDiv.setStyle({'width': '', 'height': ''});
    }
    dialogDiv.show();

    // Now, the dialog box is a as big as it wants to be, let's make sure it fits in the window
    // and center it.
    var viewportDimensions = document.viewport.getDimensions();

    var dialogLayout = dialogDiv.getLayout();

    var minPadding = 10;
    var dialogWidth = dialogLayout.get('width');
    var dialogWidthWithBorder = dialogLayout.get('margin-box-width');
    var borderWidth = dialogWidthWithBorder - dialogWidth;
    var dialogHeight = dialogLayout.get('height');
    var dialogHeightWithBorder = dialogLayout.get('margin-box-height');
    var borderHeight = dialogHeightWithBorder - dialogHeight;
    var nonScrollWidth = 0;
    var nonScrollHeight = 0;

    if (scrollAreaDiv) {
        // Find the difference in size between the dialog box and the scroll area...we'll put
        // aside that much space when we fix the scroll area's size.
        var scrollAreaDivLayout = scrollAreaDiv.getLayout();
        nonScrollWidth = dialogWidth - scrollAreaDivLayout.get('width');
        nonScrollHeight = dialogHeight - scrollAreaDivLayout.get('height');
    }

    var needsToShrink = false;
    if (dialogWidthWithBorder > viewportDimensions.width - minPadding) {
        dialogWidth = viewportDimensions.width - minPadding - borderWidth;
        needsToShrink = true;
    }
    if (dialogHeightWithBorder > viewportDimensions.height - minPadding) {
        dialogHeight = viewportDimensions.height - minPadding - borderHeight;
        needsToShrink = true;
    }

    if (scrollAreaDiv && needsToShrink) {
        // Fix the size of the scroll area to (wholeDialogDimensions - unscrolledArea) so the
        // rest of the dialog doesn't scroll, but instead this area only does.
        scrollAreaDiv.setStyle({
            'width': (dialogWidth - nonScrollWidth) + "px",
            'height': (dialogHeight - nonScrollHeight) + "px"
        });
    }

    if (needsToShrink) {
        dialogDiv.setStyle({
            'maxWidth': dialogWidth + "px",
            'maxHeight': dialogHeight + "px"
        });
    }

    var x = (viewportDimensions.width - dialogWidth - borderWidth) / 2;
    var y = (viewportDimensions.height - dialogHeight - borderHeight) / 4;
    dialogDiv.setStyle({'left': x + "px", 'top': y + "px"});
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
    	} else {
    		if (!skipShow) {
    		    adjustDialogSizeAndPosition(dialogId);
    		    $(dialogId).show();
    		}
    	}
    	Yukon.ui.unblockPage();
    };

    jQuery.ajax({
        url: innerHtmlUrl,
        data: parameters,
        method: method ? method : 'post',
        success: function(data){
            jQuery(document.getElementById(dialogId+'_body')).html(data);
            onComplete();
        },
        error: onComplete
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
function submitFormViaAjax(dialogId, formId, url, title, method, skipShow) {
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
        Yukon.ui.unblockPage();
        if (json.action === 'reload') {
            window.location = window.location;
        }
    };

    var errorCallback = function(transport) {
        Yukon.ui.unblockPage();
        alert('error making request');
    };

    var options = {
            'evalScript': true,
            'method': 'post',
            'onSuccess': successCallback,
            'onFailure': errorCallback
            };
    Yukon.ui.blockPage();
    new Ajax.Request(url, options);
}

function showSimplePopup(popupId, initialFocus) {
    adjustDialogSizeAndPosition(popupId);
    $(popupId).show();
    if (initialFocus) {
        $(initialFocus).focus();
    } else if ($(popupId).down("input:first[type='text']")) {
        $(popupId).down("input:first[type='text']").focus();
    }
}
