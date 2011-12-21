jQuery(document).delegate("li.menuOption.command", "click", function(event) {
    hideMenu();
    var doCommand = true;
    var ul = jQuery(event.currentTarget).closest("ul");
    if (ul.find("input[name='warnOnCommands']").val() === 'true') {
        doCommand = confirm(menuOption.find('span.confirmMessage').html());
    }
    
    if (doCommand) {
        doItemCommand(ul.find("input[name='paoId']").val(), jQuery(event.currentTarget).val(), event);
    }
});

jQuery(document).delegate("li.menuOption.stateChange", "click", function(event) {
    hideMenu();
    doChangeState(jQuery(event.currentTarget).closest("ul").find("input[name='paoId']").val(), jQuery(event.currentTarget).val());
});

function doItemCommand(itemId, commandId, event, reason, onReasonMenu) {
    var parameters = {'itemId': itemId, 'commandId': commandId};
    if (reason) parameters.reason =  reason;
    if (onReasonMenu) parameters.onReasonMenu = onReasonMenu;
    
    jQuery.ajax({
        url: '/spring/capcontrol/command/itemCommand',
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
        url: '/spring/capcontrol/command/system',
		type: 'POST',
        data: {'commandId' : commandId},
        success: function(response) {
        	showMessage(response);
        }
    });
}

function doChangeState(itemId, stateId) {
    jQuery.ajax({
        url: '/spring/capcontrol/command/manualStateChange',
		type: 'POST',
        data: {'bankId' : itemId, 'rawStateId' : stateId},
        success: function(response) {
        	showMessage(response);
        }
    });
}

function doResetBankOpCount(itemId, newOpCount) {
    hideMenu();
    jQuery.ajax({
        url: '/spring/capcontrol/command/resetBankOpCount',
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
        url: '/spring/capcontrol/command/changeOpState',
        type: 'POST',
        data: parameters,
        success: function(response) {
        	showMessage(response);
        }
    });
}

function addCommandMenuBehavior(selector) {
	jQuery(document).delegate(selector, "click", function (event) {
        getCommandMenu(jQuery(event.currentTarget).closest("a")[0].id.split('_')[1], event);
    });
	jQuery(document).ready(function() {
        jQuery(selector).each(function() {
        	jQuery(this).addClass('actsAsAnchor');
        });
    });
}