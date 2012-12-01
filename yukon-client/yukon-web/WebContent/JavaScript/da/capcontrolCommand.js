jQuery(document).on('click', 'li.menuOption.command', function(event) {
    hideMenu();
    var doCommand = true;
    var menuOption = jQuery(event.currentTarget).closest("li");
    var ul = menuOption.parent("ul");
    if (ul.find("input[name='warnOnCommands']").val() === 'true') {
        doCommand = confirm(menuOption.find('span.confirmMessage').html());
    }
    
    if (doCommand) {
        doItemCommand(ul.find("input[name='paoId']").val(), jQuery(event.currentTarget).val(), event);
    }
});

jQuery(document).on('click', 'li.menuOption.stateChange', function(event) {
    hideMenu();
    doChangeState(jQuery(event.currentTarget).closest("ul").find("input[name='paoId']").val(), jQuery(event.currentTarget).val());
});

function doItemCommand(itemId, commandId, event, reason, onReasonMenu) {
    var parameters = {'itemId': itemId, 'commandId': commandId};
    if (reason) parameters.reason =  reason;
    if (onReasonMenu) parameters.onReasonMenu = onReasonMenu;
    
    jQuery.ajax({
        url: '/capcontrol/command/itemCommand',
        type: "POST",
        data: parameters,
        dataType: "html",
        success: function(data) {
            jQuery("#menuPopup").html(data);

            if (jQuery("#menuPopup #isFinished").val() === "true") {
                showMessage(data);
            } else {
                showMenuPopup({position: "center", modal: true});
            }
        }
    });
}

function doSystemCommand(commandId) {
    jQuery.ajax({
        url: '/capcontrol/command/system',
		type: 'POST',
        data: {'commandId' : commandId},
        success: function(response) {
        	showMessage(response);
        }
    });
}

function doChangeState(itemId, stateId) {
    jQuery.ajax({
        url: '/capcontrol/command/manualStateChange',
		type: 'POST',
        data: {'paoId' : itemId, 'rawStateId' : stateId},
        success: function(response) {
        	showMessage(response);
        }
    });
}

function doResetBankOpCount(itemId, newOpCount) {
    hideMenu();
    jQuery.ajax({
        url: '/capcontrol/command/resetBankOpCount',
        type: 'POST',
        data: {'bankId' : itemId, 'newOpCount' : newOpCount},
        success: function(response) {
        	showMessage(response);
        }
    });
}

function doChangeOpState(bankId, stateId, reason, onReasonMenu) {
    hideMenu();
    parameters = {'bankId': bankId, 'opState': stateId, 'reason': reason};
    if (onReasonMenu) parameters.onReasonMenu = onReasonMenu;
    
    jQuery.ajax({
        url: '/capcontrol/command/changeOpState',
        type: 'POST',
        data: parameters,
        success: function(response) {
        	showMessage(response);
        }
    });
}

function addCommandMenuBehavior(selector) {
	jQuery(document).on('click', selector, function (event) {
        getCommandMenu(jQuery(event.currentTarget).closest("a")[0].id.split('_')[1], event);
    });
}