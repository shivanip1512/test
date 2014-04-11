$(document).on('click', 'li.menuOption.command', function(event) {
   yukon.da.hideMenu();
    var doCommand = true;
    var menuOption = $(event.currentTarget).closest("li");
    var ul = menuOption.parent("ul");
    if (ul.find("input[name='warnOnCommands']").val() === 'true') {
        doCommand = confirm(menuOption.find('span.confirmMessage').html());
    }
    
    if (doCommand) {
        doItemCommand(ul.find("input[name='paoId']").val(), $(event.currentTarget).val(), event);
    }
});

$(document).on('click', 'li.menuOption.stateChange', function(event) {
    yukon.da.hideMenu();
    doChangeState($(event.currentTarget).closest("ul").find("input[name='paoId']").val(), $(event.currentTarget).val());
});

function doItemCommand(itemId, commandId, event, reason, onReasonMenu) {
    var parameters = {'itemId': itemId, 'commandId': commandId};
    if (reason) parameters.reason =  reason;
    if (onReasonMenu) parameters.onReasonMenu = onReasonMenu;
    
    $.ajax({
        url: yukon.url('/capcontrol/command/itemCommand'),
        type: "POST",
        data: parameters,
        dataType: "html"
    }).done( function(data){
        $("#menuPopup").html(data);

        if ($("#menuPopup #isFinished").val() === "true") {
            yukon.da.showMessage(data);
        } else {
            yukon.da.showMenuPopup({});
        }
    });
}

function doSystemCommand(commandId) {
    $.ajax({
        url: yukon.url('/capcontrol/command/system'),
        type: 'POST',
        data: {'commandId' : commandId}
    }).done( function(response){
        yukon.da.showMessage(response);
    });
}

function doChangeState(itemId, stateId) {
    $.ajax({
        url: yukon.url('/capcontrol/command/manualStateChange'),
        type: 'POST',
        data: {'paoId' : itemId, 'rawStateId' : stateId}
    }).done( function(response){
        yukon.da.showMessage(response);
    });
}

function doResetBankOpCount(itemId, newOpCount) {
    yukon.da.hideMenu();
    $.ajax({
        url: yukon.url('/capcontrol/command/resetBankOpCount'),
        type: 'POST',
        data: {'bankId' : itemId, 'newOpCount' : newOpCount}
    }).done( function(response){
        yukon.da.showMessage(response);
    });
}

function doChangeOpState(bankId, stateId, reason, onReasonMenu) {
    yukon.da.hideMenu();
    parameters = {'bankId': bankId, 'opState': stateId, 'reason': reason};
    if (onReasonMenu) parameters.onReasonMenu = onReasonMenu;
    
    $.ajax({
        url: yukon.url('/capcontrol/command/changeOpState'),
        type: 'POST',
        data: parameters
    }).done( function(response){
        yukon.da.showMessage(response);
    });
}

function addCommandMenuBehavior(selector) {
    $(document).on('click', selector, function (event) {
        yukon.da.getCommandMenu($(event.currentTarget).closest("a")[0].id.split('_')[1], event);
    });
}