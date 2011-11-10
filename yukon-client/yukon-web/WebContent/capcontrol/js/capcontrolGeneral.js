function checkPageExpire() {
    var paoIds = Object.toJSON($$("input[id^='paoId_']").pluck('value'));
    var url = '/spring/capcontrol/pageExpire';
    
    new Ajax.Request(url, {
        'method': 'POST',
        'parameters' : {'paoIds': paoIds},
        onSuccess: function(transport) {
            var html = transport.responseText;
            var expired = eval(html);
            if (expired) {
                $('updatedWarning').show();    
                return;    
            }
            setTimeout(checkPageExpire, 15000);
        }
    });
}

function getCommandMenu(id, event) {
    var url = '/spring/capcontrol/menu/commandMenu?id=' + id;
    getMenuFromURL(url, event);
}

function showDialog(title, url) {
	openSimpleDialog('contentPopup', url, title, null, null, 'get');
}

function getMovedBankMenu(id, event) {
    var url = '/spring/capcontrol/menu/movedBankMenu?id=' + id;
    getMenuFromURL(url, event);
}

function getMenuFromURL(url, event, up, left) {
	/*
	 *  In IE the event does not pass through the ajax request
	 *  so the attributes of the event need to be set and passed
	 *  as variables.
	 */
	var x = event.pointerX();
    var y = event.pointerY();
    new Ajax.Updater('menuPopup', url, {
        'evalScripts' : true,
        'method' : 'POST',
        'onComplete' : function() {
            showMenuPopup(x, y, up, left);
        }
    });
}

function showMenuPopup(x, y, up, left) {
    if (!up) {
        up = false;
    }
    if (!left) {
        left = false;
    }
    
    var menu = $('menuPopup');
    var dimensions = menu.getDimensions();
    
    menu.show();
    if (up == true) {
        if (willMenuFitAbove(y, dimensions.height)) {
            y = y - dimensions.eight;
        }
    } else {
        if (!willMenuFitBelow(y, dimensions.height)) {
            y = y - dimensions.height;
        }
    }
    if (left == true) {
        x = x - dimensions.width;
    }
    menu.setStyle({top: y + "px", left: x + "px"});
}

function willMenuFitAbove(yClick, menuHeight) {
    if (yClick < menuHeight) {
        return false;
    }
    return true;
}

function willMenuFitBelow(yClick, menuHeight) {
    if ((yClick + menuHeight) > document.documentElement.clientHeight) {
        return false;
    }
    return true;
}

YEvent.observeSelectorClick('div.dynamicTableWrapper .pointAddItem', function(event) {
    pointPicker.show();
});
YEvent.observeSelectorClick('div.dynamicTableWrapper .bankAddItem', function(event) {
    bankPicker.show();
});

function hideMenu() {
	$('menuPopup').hide();
}

function hideContentPopup() {
	$('contentPopup').hide();
}

function checkAll(allCheckBox, selector) {
    $$(selector).each(function(item) {
        item.checked = allCheckBox.checked;
    });
}

function expandRow(itemId, imgId) {
    var img = $(imgId);
    var expand = false;
    if (img.src.indexOf('nav-minus.gif') > 0) {
        img.src='/capcontrol/images/nav-plus.gif';
    } else {
        img.src = '/capcontrol/images/nav-minus.gif';
        expand = true;
    }
    
    $(itemId).select('tr').each(function(row) {
        if (expand) {
            row.show();
        } else {
            row.hide();
        }
    });
}

function statusMsg(elem, message) {
    elem.onmouseout = function (e) {nd()};
    overlib(message, WIDTH, 160, CSSCLASS, TEXTFONTCLASS, 'flyover');
}

function statusMsgAbove(elem, message) {
    elem.onmouseout = function (e) {nd()};
    overlib(message, ABOVE,  WIDTH, 260, CSSCLASS, TEXTFONTCLASS, 'flyover');
}

function showDynamicPopup(containerId, popupWidth) {
    overlib($(containerId).innerHTML, WIDTH, popupWidth, CSSCLASS, TEXTFONTCLASS, 'flyover');
}

function showDynamicPopupAbove(containerId, popupWidth) {
    overlib($(containerId).innerHTML, ABOVE, WIDTH, popupWidth, CSSCLASS, TEXTFONTCLASS, 'flyover');
}

function addLockButtonForButtonGroup (groupId, secs) {
    Event.observe(window, 'load', function() {
        var button_group = $(groupId);
        var buttons = button_group.getElementsByTagName("input");
        
        for (var i=0; i<buttons.length; i++) {
            var button_el =  buttons.item(i);
            lock_buttons(button_el.id);
        }
    });
    
    if (secs != null) {
        pause (secs * 1000);
    }
}

function lock_buttons(el_id) {
    var button_el = $(el_id);
    var parent_el = $(button_el.parentNode.id);
    var button_els = parent_el.getElementsByTagName("input");
    Event.observe(button_el, 'click', function () {
        for (var i=0; i < button_els.length; i++) {
            var current_button = $(button_els.item(i).id);
            if (current_button.id != el_id) {
                current_button.disabled = true;
            } else {    
                setTimeout("$('" + el_id + "').disabled=true;", 1);
            }
        }
    });
}

function lockButtonsPerSubmit (groupId) {
    var button_group = $(groupId);
    var buttons = button_group.getElementsByTagName("input");

    for (var i=0; i<buttons.length; i++) {
        var button_el =  buttons.item(i);
        button_el.disabled = true;
    }
}

function pause(numberMillis) {
    var now = new Date();
    var exitTime = now.getTime() + numberMillis;
    while (true) {
        now = new Date();
        if (now.getTime() > exitTime) {
            return;
        }
    }
}

function onGreyBoxClose() {
    window.location.href = window.location.href;
 }

function showAlertMessageForAction(action, item, result, success) {
    if (action != '') {
        action = '"' + action + '"';
    }
    var message = item + ' ' + action + ' ' + result;
    showAlertMessage(message, success);
}

function showAlertMessage(message, success) {
    var contents = $('alertMessageContents');
    
    if (success) {
        contents.addClassName('successMessage');
        contents.removeClassName('errorMessage');
    } else {
        contents.removeClassName('successMessage');
        contents.addClassName('errorMessage');
    }
    contents.innerHTML = message;
    $('alertMessageContainer').show();
    setTimeout ('hideAlertMessage()', success ? 3000 : 8000);
}

function showMessage(message) {
    $('alertMessageContents').innerHTML = message;
    $('alertMessageContainer').show();
    setTimeout ('hideAlertMessage()', 3000);
}

function hideAlertMessage() {
    new Effect.Fade('alertMessageContainer', {duration: 3.0});
}