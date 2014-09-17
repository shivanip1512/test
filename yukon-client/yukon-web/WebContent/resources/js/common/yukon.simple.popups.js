/**
 * Makes an AJAX request with the given url and populate the dialog specified 
 * by dialogId with the response. This method will show a busy cursor while 
 * waiting for the request. 
 * 
 * If the request contains JSON with an "action", the action will be performed 
 * instead of opening the dialog.
 * 
 * Supported actions: 
 *      "close", the dialog box will be closed.
 *      "reload", the page will be reloaded.
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
 * @param method
 *            The HTTP method ('get' or 'post'). 'post' is the default.
 * @param options
 *            The json object representing the dialog options.
 */
function openSimpleDialog(dialogId, url, title, parameters, method, options) {
    
    if (arguments.length > 2 && title) {
        options = $.extend(options, {title: title});
    }
    
    method = (typeof method == "undefined") ? "post" : method;
    
    $.ajax({
        type: method,
        url: url,
        data: parameters
    })
    .done(function(data) {
        var dialog = $("#" + dialogId);
        if (data.action) {
            if (data.action == "close") {
                dialog.dialog("close");
            } else if (data.action == "reload") {
                window.location = window.location;
            } else {
                throw new Error("Action not supported: " + data.action);
            }
        } else {
            dialog.html(data);
            dialog.dialog(options);
            dialog.dialog('open');
        }
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
 * @param options
 *            The json object representing the dialog options.
 */
function submitFormViaAjax(dialogId, formId, url, title, method, options) {
    
    var done = function (data, textStatus, jqXHR) {
        if (data.action) {
            if (data.action == "close") {
                $("#" + dialogId).dialog("close");
            } else if (data.action == "reload") {
                window.location = window.location;
            } else {
                throw new Error("Action not supported: " + data.action);
            }
        } else {
            var dialog = $("#" + dialogId);
            dialog.html(data);
            dialog.dialog();
            if (arguments.length > 2 && title) {
                dialog.dialog("option", "title", title);
            }
            if (arguments.length > 2 && options) {
                dialog.dialog("option", options);
            }
            dialog.dialog("open");
        }
        
    };
    
    var options = {success: done};
    if (arguments.length > 2 && url) {
        options.url = url;
    }
    
    $("#" + formId).ajaxSubmit(options);
    
    // !!! Important !!! 
    // always return false to prevent standard browser submit and page navigation 
    return false;
}

/**
 * Make a simple AJAX request with the given URL and possibly react. Currently,
 * the only supported reaction is "reload" which cause the page to be reloaded.
 * The action is specified by setting the JSON variable 'action'.
 * 
 * @param url
 *            (String) The URL of the request to make.
 * @param data
 *            (JSON) A json object of request parameters
 */
function simpleAJAXRequest(url, data) {
    yukon.ui.blockPage();
    
    $.get(url,data)
    .done(function(json, textStatus, jqXHR) {
        yukon.ui.unblockPage();
        if (json.action === 'reload') {
            window.location = window.location;
        }
    })
    .fail(function(jqXHR, textStatus, errorThrown) {
        alert('error making request');
    })
    .always(function() {yukon.ui.unblockPage();});
}

/**
 * Class the JQuery UI Dialog open method on the element with the provided id. 
 * 
 * @param popupId
 *            The id of the element to open the dialog for.
 * @param initialFocus (Optional) 
 *            The id of the element in the dialog to give focus to.
 */
function showSimplePopup(popupId, initialFocus) {
    $("#" + popupId).dialog("open");
    if (initialFocus) {
        document.getElementById(initialFocus).focus();
    }
}