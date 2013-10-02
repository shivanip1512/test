jQuery(document).on('click', 'li.menuOption.command', function(event) {
   Yukon.CapControl.hideMenu();
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
    Yukon.CapControl.hideMenu();
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
        dataType: "html"
    }).done( function(data){
        jQuery("#menuPopup").html(data);

        if (jQuery("#menuPopup #isFinished").val() === "true") {
            Yukon.CapControl.showMessage(data);
        } else {
            Yukon.CapControl.showMenuPopup({position: "center", modal: true});
        }
    });
}

function doSystemCommand(commandId) {
    jQuery.ajax({
        url: '/capcontrol/command/system',
		type: 'POST',
        data: {'commandId' : commandId}
    }).done( function(response){
        Yukon.CapControl.showMessage(response);
    });
}

function doChangeState(itemId, stateId) {
    jQuery.ajax({
        url: '/capcontrol/command/manualStateChange',
		type: 'POST',
        data: {'paoId' : itemId, 'rawStateId' : stateId}
    }).done( function(response){
        Yukon.CapControl.showMessage(response);
    });
}

function doResetBankOpCount(itemId, newOpCount) {
    Yukon.CapControl.hideMenu();
    jQuery.ajax({
        url: '/capcontrol/command/resetBankOpCount',
        type: 'POST',
        data: {'bankId' : itemId, 'newOpCount' : newOpCount}
    }).done( function(response){
        Yukon.CapControl.showMessage(response);
    });
}

function doChangeOpState(bankId, stateId, reason, onReasonMenu) {
    Yukon.CapControl.hideMenu();
    parameters = {'bankId': bankId, 'opState': stateId, 'reason': reason};
    if (onReasonMenu) parameters.onReasonMenu = onReasonMenu;
    
    jQuery.ajax({
        url: '/capcontrol/command/changeOpState',
        type: 'POST',
        data: parameters
    }).done( function(response){
        Yukon.CapControl.showMessage(response);
    });
}

function addCommandMenuBehavior(selector) {
	jQuery(document).on('click', selector, function (event) {
        Yukon.CapControl.getCommandMenu(jQuery(event.currentTarget).closest("a")[0].id.split('_')[1], event);
    });
}