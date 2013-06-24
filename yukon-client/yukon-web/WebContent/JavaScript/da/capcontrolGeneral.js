function checkPageExpire() {
    var paoIds = [];
    jQuery("input[id^='paoId_']").each(function() {
        paoIds.push(jQuery(this).val());
    });
    
    jQuery.ajax({
        url: "/capcontrol/pageExpire",
        data: {"paoIds": paoIds},
        success: function(data) {
            var expired = eval(data);
            if (expired) {
            jQuery("#updatedWarning").dialog("open");
                return;    
            }
            setTimeout(checkPageExpire, 15000);
        }
    });
}

function getCommandMenu(id, event) {
    getMenuFromURL('/capcontrol/menu/commandMenu?id=' + id, event);
}

function showDialog(title, url, options, selector) {
    var dialogArgs = {'width' : 'auto', 'height': 'auto', 'title': title};
    jQuery.extend(dialogArgs, options);
    selector = selector || "<div></div>";
    
    var dialog = jQuery(selector);
    dialog.load(url).dialog(dialogArgs);
}

function getMovedBankMenu(id, event) {
    getMenuFromURL('/capcontrol/menu/movedBankMenu?id=' + id, event);
}

function getMenuFromURL(url, event, params) {
    /*
     *  In IE the event does not pass through the ajax request
     *  so the attributes of the event need to be set and passed
     *  as variables.
     */
    
    if (typeof(params) != 'undefined') {
        if (!("position" in params)) {
            params.position = [event.pageX, event.pageY];
        }
        if (!("modal" in params)) {
            params.modal = false;
        }
    } else {
        params = {position: [event.clientX, event.clientY], modal: false};
    }
    
    jQuery.ajax({
        url: url,
        complete: function(data) {
            jQuery("#menuPopup").html(data.responseText);
            showMenuPopup(params);
        }
    });
}

function showMenuPopup(params) {
    jQuery("#menuPopup").dialog({
        resizable: false,
        closeOnEscape: true,
        width: "auto",
        modal: params["modal"],
        title: jQuery("#menuPopup input[id='dialogTitle']").val(),
        position: params["position"],
        dialogClass: "smallDialog"
    });
}

jQuery(document).on('click', 'div.dynamicTableWrapper .pointAddItem', function(event) {
    pointPicker.show();
});
jQuery(document).on('click', 'div.dynamicTableWrapper .bankAddItem', function(event) {
    bankPicker.show();
});

function hideMenu() {
    jQuery('#menuPopup').dialog("close");
}

function hideContentPopup() {
    jQuery('#contentPopup').dialog("close");
}

function checkAll(allCheckBox, selector) {
    jQuery(selector).prop("checked", allCheckBox.checked);
}

function addLockButtonForButtonGroup (groupId, secs) {
    jQuery(document).ready(function() {
        var buttons = jQuery("#" + groupId + " input");

        for (var i=0; i<buttons.length; i++) {
            lock_buttons(buttons[i].id);
        }
    });
    
}

function lock_buttons(el_id) {
    //el_id comes in looking like this: "editorForm:hdr_submit_button_1"
    jQuery(document.getElementById(el_id)).click(function() {
        jQuery("input.stdButton").each(function() {
            if (this.id != el_id) {
                this.disabled = true;
            } else {
                setTimeout("jQuery(\"[id='" + el_id + "']\")[0].disabled=true;", 1);
            }
        });
    });
}

function lockButtonsPerSubmit (groupId) {
    jQuery("#" + groupId + " input").each(function() {
        this.disabled = true;
    });
}

function showAlertMessageForAction(action, item, result, success) {
    if (action != '') {
        action = '"' + action + '"';
    }
    var message = item + ' ' + action + ' ' + result;
    showAlertMessage(message, success);
}

function showAlertMessage(message, success) {
    var contents = jQuery('#alertMessageContents');
    
    if (success) {
        contents.addClass('successMessage').removeClass('errorMessage');
    } else {
        contents.removeClass('successMessage').addClass('errorMessage');
    }
    contents.html(message);
    jQuery('#alertMessageContainer').show();
    setTimeout ('hideAlertMessage()', success ? 3000 : 8000);
}

function showMessage(message) {
    jQuery('#alertMessageContents').html(message);
    jQuery('#alertMessageContainer').show();
    setTimeout ('hideAlertMessage()', 3000);
}

function hideAlertMessage() {
    jQuery('#alertMessageContainer').hide("fade", {}, 3000);
}

//BANK MOVE JS
jQuery(function() {
    jQuery(document).on('click', 'li.toggle', function(e) {
        if (e.target == e.currentTarget) {
            var li = jQuery(this);
            var subMenu = li.find('ul:first');
            if (subMenu[0]) {
                subMenu.toggle();
                li.toggleClass("minus").toggleClass("plus");
            }
            return false;
        }
        return true;
    });
});