YEvent.observeSelectorClick('li.menuOption.command', function(event) {
    hideMenu();
    var doCommand = true;
    var menuOption = event.findElement('li');
    var ul = menuOption.up('ul');
    if ($F(ul.down("input[name='warnOnCommands']")) === 'true') {
        var confirmMessage = menuOption.down('span.confirmMessage').innerHTML;
        doCommand = confirm(confirmMessage);
    }
    
    if (doCommand) {
        doItemCommand($F(ul.down("input[name='paoId']")), menuOption.value, event);
    }
});

YEvent.observeSelectorClick('li.menuOption.stateChange', function(event) {
    hideMenu();
    doChangeState($F(event.findElement('ul').down("input[name='paoId']")), event.findElement().value);
});

function doItemCommand(itemId, commandId, event, reason, onReasonMenu) {
    var url = '/spring/capcontrol/command/itemCommand';
    var mouseX = event.pointerX();
    var mouseY = event.pointerY();
    var parameters = {'itemId': itemId, 'commandId': commandId};
    
    if (reason) parameters.reason =  reason;
    if (onReasonMenu) parameters.onReasonMenu = onReasonMenu;
    
    new Ajax.Updater('menuPopup', url, {
        'evalScripts' : true,
        'method' : 'POST',
        'parameters' : parameters,
        'onSuccess' : function(response) {
            var html = response.responseText;
            var json = response.headerJSON;
            if (json.finished === true) {
                showMessage(html);
            } else {
                showMenuPopup(mouseX, mouseY);
            }
        }
    });
}

function doSystemCommand(commandId) {
    var url = '/spring/capcontrol/command/system';
    new Ajax.Request(url, {
        'method' : 'POST',
        'parameters' : {'commandId' : commandId},
        'onSuccess' : function(response) {
            showMessage(response.responseText);
        }
    });
}

function doChangeState(itemId, stateId) {
    var url = '/spring/capcontrol/command/manualStateChange';
    new Ajax.Request(url, {
        'method' : 'POST',
        'parameters' : {'bankId' : itemId, 'rawStateId' : stateId},
        'onSuccess' : function(response) {
            showMessage(response.responseText);
        }
    });
}

function doResetBankOpCount(itemId, newOpCount) {
    hideMenu();
    var url = '/spring/capcontrol/command/resetBankOpCount';
    new Ajax.Request(url, {
        'method' : 'POST',
        'parameters' : {'bankId' : itemId, 'newOpCount' : newOpCount},
        'onSuccess' : function(response) {
            showMessage(response.responseText);
        }
    });
}

function doChangeOpState(bankId, stateId, reason, onReasonMenu) {
    hideMenu();
    var url = '/spring/capcontrol/command/changeOpState';
    
    parameters = {'bankId': bankId, 'opState': stateId, 'reason': reason};
    if (onReasonMenu) parameters.onReasonMenu = onReasonMenu;
    
    new Ajax.Request(url, {
        'method' : 'POST',
        'parameters' : parameters,
        'onSuccess' : function(response) {
            showMessage(response.responseText);
        }
    });
}

function addCommandMenuBehavior(selector) {
    YEvent.observeSelectorClick(selector, function (event) {
        var anchor = event.findElement('a');
        var itemId = anchor.id.split('_')[1];
        getCommandMenu(itemId, event);
    });
    Event.observe(window, 'load', function() {
        $$(selector).each(function(anchor) {
            anchor.addClassName('actsAsAnchor');
        });
    });
}