$(document).on('click', 'li.menuOption.command', function(event) {
   yukon.da.common.hideMenu();
    var doCommand = true,
        menuOption = $(event.currentTarget).closest("li"),
        ul = menuOption.closest('ul'),
        confirmPopupLevel = ul.find("input[name='warnOnCommands']").val();
    if (confirmPopupLevel === 'ALL_COMMANDS') {
        doCommand = confirm(menuOption.find('span.confirmMessage').html());
    } else if (confirmPopupLevel === 'OPERATIONAL_COMMANDS') {
        if ($(event.currentTarget).data('operationalCommand')) {
            doCommand = confirm(menuOption.find('span.confirmMessage').html());
        }
    }
    
    if (doCommand) {
        doItemCommand(ul.find("input[name='paoId']").val(), $(event.currentTarget).val(), event);
    }
});

$(document).on('click', 'li.menuOption.stateChange', function(event) {
    yukon.da.common.hideMenu();
    doChangeState($(event.currentTarget).closest("ul").find("input[name='paoId']").val(), $(event.currentTarget).val());
});

/** This method executes the command.
 *  @param {number} itemId - PAO Id.
 *  @param {number} commandId - Id of the command that needs to be executed.
 *  @param {Object} event - Jquery event object.
 *  @param {string} reason - Comment given while executing the command.
 *  @param {string} onReasonMenu - If set to true , executes the command
 */
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
            yukon.da.common.showMessage(data);
        } else {
            yukon.da.common.showMenuPopup({});
        }
    });
}

/** This method executes the normal commands.
 *  @param {number} commandId - Id of the command that needs to be executed.
 */
function doSystemCommand(commandId) {
    $.ajax({
        url: yukon.url('/capcontrol/command/system'),
        type: 'POST',
        data: {'commandId' : commandId}
    }).done( function(response){
        yukon.da.common.showMessage(response);
    });
}

/** This method changes the state of the cap bank.
 *  @param {number} itemId - PAO ID.
 *  @param {number} stateId - Raw State Id. 
 */
function doChangeState(itemId, stateId) {
    $.ajax({
        url: yukon.url('/capcontrol/command/manualStateChange'),
        type: 'POST',
        data: {'paoId' : itemId, 'rawStateId' : stateId}
    }).done( function(response){
        yukon.da.common.showMessage(response);
    });
}

/** This method resets the cap bank op count
 *  @param {number} itemId - PAO ID.
 *  @param {number} newOpCount - New count. 
 */
function doResetBankOpCount(itemId, newOpCount) {
    yukon.da.common.hideMenu();
    $.ajax({
        url: yukon.url('/capcontrol/command/resetBankOpCount'),
        type: 'POST',
        data: {'bankId' : itemId, 'newOpCount' : newOpCount}
    }).done( function(response){
        yukon.da.common.showMessage(response);
    });
}

/** This method changes the Operational State.
 *  @param {number} bankId - PAO ID.
 *  @param {number} stateId - State Id.
 *  @param {string} reason - Reason for change . 
 *  @param {string} onReasonMenu - If set to true, executes the command.  
 */
function doChangeOpState(bankId, stateId, reason, onReasonMenu) {
    yukon.da.common.hideMenu();
    parameters = {'bankId': bankId, 'opState': stateId, 'reason': reason};
    if (onReasonMenu) parameters.onReasonMenu = onReasonMenu;
    
    $.ajax({
        url: yukon.url('/capcontrol/command/changeOpState'),
        type: 'POST',
        data: parameters
    }).done( function(response){
        yukon.da.common.showMessage(response);
    });
}

/** Adds behavior to the command.
 *  @param {Objest} selector - Option selected.
 */
function addCommandMenuBehavior(selector) {
    $(document).on('click', selector, function (event) {
        yukon.da.common.getCommandMenu($(event.currentTarget).closest("a")[0].id.split('_')[1], event);
    });
}