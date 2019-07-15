$(document).on('click', 'li.menuOption.command', function(event) {
   yukon.da.common.hideMenu();
   var showConfirmation = false,
       menuOption = $(event.currentTarget).closest("li"),
       ul = menuOption.closest('ul'),
       itemId = ul.find("input[name='paoId']").val(),
       commandId = $(event.currentTarget).val(),
       confirmPopupLevel = ul.find("input[name='warnOnCommands']").val();
    if (confirmPopupLevel === 'ALL_COMMANDS') {
        showConfirmation = true;
    } else if (confirmPopupLevel === 'OPERATIONAL_COMMANDS') {
        if ($(event.currentTarget).data('operationalCommand')) {
            showConfirmation = true;
        }
    }
    
    if (showConfirmation) {
        var popup = $('#commandConfirmation'),
            title = popup.data('title'),
            okButtonText = popup.data('okText');
        popup.find('.js-warning').text(menuOption.find('span.confirmMessage').html());
        popup.data('itemId', itemId);
        popup.data('commandId', commandId);
        popup.dialog({
            title: title, 
            width: 'auto',
            modal: true,
            buttons: yukon.ui.buttons({okText: okButtonText, event: 'yukon:command:confirm'})
        });
        //make it so the user has to intentionally click the button
        document.activeElement.blur();
    } else {
        doItemCommand(itemId, commandId, event);
    }
});

$(document).on('click', '.js-scan-cbc-now', function(event) {
    var showConfirmation = false,
        itemId = $(this).data('paoId'),
        commandId = $(this).data('commandId'),
        confirmPopupLevel = $(this).data('warnOnCommands');
     if (confirmPopupLevel === 'ALL_COMMANDS') {
         var popup = $('#scanCommandConfirmation'),
             title = popup.data('title'),
             okButtonText = popup.data('okText');
         popup.data('itemId', itemId);
         popup.data('commandId', commandId);
         popup.dialog({
             title: title, 
             width: 'auto',
             modal: true,
             buttons: yukon.ui.buttons({okText: okButtonText, event: 'yukon:command:confirm'})
         });
         //make it so the user has to intentionally click the button
         document.activeElement.blur();
     } else {
         doItemCommand(itemId, commandId, event);
     }
 });

$(document).on('click', 'li.menuOption.stateChange', function(event) {
    yukon.da.common.hideMenu();
    doChangeState($(event.currentTarget).closest("ul").find("input[name='paoId']").val(), $(event.currentTarget).val());
});

/** User has clicked Send Command on command confirmation popup - execute the command  */
$(document).on('yukon:command:confirm', function (ev) {
    var popup = $(ev.target),
        itemId = popup.data('itemId'),
        commandId = popup.data('commandId');
    doItemCommand(itemId, commandId);
    popup.dialog('destroy');
});

/** User has clicked on a system command (Enable/Disable System or Reset All Ops **/
$(document).on('click', '.js-system-command', function (ev) {
    var commandId = $(this).data('commandId'),
        commandWarning = $(this).data('commandWarning');
    doSystemCommand(commandId, commandWarning);
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

/** This method executes the system commands.
 *  @param {number} commandId - Id of the command that needs to be executed.
 */
function doSystemCommand(commandId, commandText) {
    
    var popup = $('#systemCommandConfirmation'),
        title = popup.data('title'),
        okButtonText = popup.data('okText');
    popup.find('.js-warning').text(commandText);
    popup.data('commandId', commandId);
    popup.dialog({
        title: title, 
        width: 'auto',
        modal: true,
        buttons: yukon.ui.buttons({okText: okButtonText, event: 'yukon:system:command:confirm'})
    });
    //make it so the user has to intentionally click the button
    document.activeElement.blur();
}

/** User has clicked Send Command on system command confirmation popup - execute the command  */
$(document).on('yukon:system:command:confirm', function (ev) {
    var popup = $(ev.target),
        commandId = popup.data('commandId');
    $.ajax({
        url: yukon.url('/capcontrol/command/system'),
        type: 'POST',
        data: {'commandId' : commandId}
    }).done( function(response){
        yukon.da.common.showMessage(response);
        popup.dialog('destroy');
    });
});

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