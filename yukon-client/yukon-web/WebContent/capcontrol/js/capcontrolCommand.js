var executeURL = '/spring/capcontrol/commandexecutor?action=';
var executeTierURL = executeURL + 'executeCommandTier';
var reasonMenuURL = '/spring/capcontrol/tier/popupmenu?menu=reasonMenu';
var reasonForOpStateChangeMenuURL = '/spring/capcontrol/tier/popupmenu?menu=opStateChangeMenu';
var tempMoveBackURL = executeURL + 'executeTempMoveBack';

function showChangeOpStateMenu(paoId, event) {
    var url = reasonForOpStateChangeMenuURL +
    '&id=' + paoId +
    '&cmdId=35' +
    '&controlType=CAPBANK' +
    '&commandName=Change Operational State';
     
    getReasonMenuFromURL(url, event);
}

function showResetOpCountSpan(paoId) {
    $('cb_state_td_hdr2').show();
    $('opcount_span' + paoId).show();
}

function hideResetOpCountSpan(paoId) {
    $('cb_state_td_hdr2').hide();
    $('opcount_span' + paoId).hide();   
}

function executeCapBankResetOpCount(paoId) {
    var input = $('opcount_' + paoId);
    var count = input.value;
    
    var isValid = isValidOpcount(count);
    if (!isValid) {
        alert ('Op Count value not specified. New Op Count value will be set to 0.');
        count = 0;        
    }
    
    input.value = null;    
    executeCommandController(paoId, 12, 'Reset Op Count', 'CAPBANK', null, count);
    hideResetOpCountSpan(paoId);
}

function executeCommand(paoId, cmdId, commandName, controlType, reasonRequired) {
    executeCommand(paoId, cmdId, commandName, controlType, reasonRequired, false);
}

function executeCommand(paoId, cmdId, commandName, controlType, reasonRequired, warn) {
	executeCommand(paoId, cmdId, commandName, controlType, reasonRequired, warn, null);
}

function executeCommand(paoId, cmdId, commandName, controlType, reasonRequired, warn, event) {
    var isReasonRequired = eval(reasonRequired);
    if (!isReasonRequired) {
        if(warn){
            var yes = confirm('Are you sure you want to execute the command '+commandName+'?');
            if(yes){
                executeCommandController(paoId, cmdId, commandName, controlType);
            }
        } else {
            executeCommandController(paoId, cmdId, commandName, controlType);
        }
        return;
    }
    var url = reasonMenuURL +
     '&paoId=' + paoId +
     '&cmdId=' + cmdId +
     '&controlType=' + controlType +
     '&commandName=' + commandName;
    getReasonMenuFromURL(url,event);
}

function executeCommandController(paoId, commandId, commandName, controlType, reason, opt) {
    var parameters = {
        'paoId': paoId,
        'cmdId': commandId,
        'controlType': controlType 
    };
    
    if (reason) parameters['reason'] = reason;
    if (opt) parameters['opt'] = opt;
    
    var msgType = 'Command Message:';
    if (controlType == 'CBC') {
    	msgType = 'DB State Change:';
    }
    
    new Ajax.Request(executeTierURL, {
        'method': 'POST',
        'parameters': parameters,
        'onSuccess': function (transport, result) {
            if (result) { 
                if(result.success){
                    display_status(commandName, msgType, "Queued Successfully", "green");
                } else {
                    alert(result.errMessage);
                }
            } else {
                display_status(commandName, msgType, "Queued Successfully", "green");    
            }    
        },
        'onFailure': function () {
            display_status(commandName, msgType, "Queued Successfully", "red"); 
        }
    });
}

function executeCBCommand(paoId, commandName, opt) {
    executeCommandController(paoId, 30, commandName, 'CBC', null, opt);
}


function execute_CapBankMoveBack (paoId, cmdId, redirectURL) {
	new Ajax.Request(tempMoveBackURL, { 
        'method': 'POST', 
	    'parameters': {
            'paoId': paoId,
            'cmdId': cmdId
        }, 
		onSuccess: function() {
        	setTimeout('window.location.replace(\'' + redirectURL + '\')', 1000);
        },
		onFailure: function() {
            display_status('Move Bank', "", "Failed", "red"); 
        }
	});
} 

function display_status(cmd_name, msg_type, result, color) {
    var msg_div = $('cmd_msg_div');
    msg_div.style.color = color;
    if(cmd_name != ''){
    	cmd_name = '"' + cmd_name + '"';
    }
    msg_div.innerHTML = msg_type + ' ' + cmd_name + ' ' + result;
    
    var timeout = 0;
    if (color == "red") {
	   $("outerDiv").style.display = 'block';
        $('outerDiv').show();
	   timeout = 8000;
	}  else {
 	  $('outerDiv').show();
	   timeout = 2000;
	}
	setTimeout ('hideMsgDiv()', timeout);	
}

function hideMsgDiv() {
    new Effect.Fade('outerDiv', { duration: 3.0 });
}

function getMoveBackMenu() {
var hiddens = document.getElementsByName("pf_hidden");
for (var i=0; i < hiddens.length; i++) {
	var hidden = hiddens[i];
	var cap_bank_id = hidden.id.split('_')[1];
	hidden.value = generate_CB_Move_Back (cap_bank_id, hidden.parentNode.id, MOVED_CB_REDIRECT_URL);
	}
}

function handleSystemCommand(disable) {
	var message ='';
	if (disable) {
		message = 'You are turning the system off. Please confirm...';	
	} else {
		message = 'You are turning the system back on. Please confirm...';	
	}
	if (confirm (message)) {
		sendSystemEnableCommand (disable);
	}
}

function sendSystemEnableCommand (disable) {
	var parameters = {'turnSystemOff' : disable};
	var msgType = 'Command Message:';
	var commandName = disable ? 'Disable System' : 'Enable System';
    new Ajax.Request('/spring/capcontrol/systemActions/toggleSystemStatus', {
        'method': 'post',
        'parameters': parameters,
        asynchronous:true,
        'onSuccess': function (transport, result) {
            if(result.success){
                display_status(commandName, msgType, "Successful", "green");
            } else {
                display_status(commandName, msgType, "Failed", "red");
            }
        }
    });
}

function sendResetOpCountCommand () {
	var message = 'You are resetting all op counters on the system. Please confirm...';	
	var msgType = 'Command Message:';
	var commandName = 'Reset All Op Counters'
	
	if (confirm (message)) {
	    new Ajax.Request('/spring/capcontrol/systemActions/resetOpCount', {
	        'method': 'post',
	        'parameters': '',
	        asynchronous:true,
	        'onSuccess': function (transport, result) {
                if(result.success){
                    display_status(commandName, msgType, "Successful", "green");
                } else {
                    display_status(commandName, msgType, "Failed", "red");
                }
            }
	    });
	}
}

function submitChangeOpStateMenu() {

    var operationalStateChange = false;

    var index = $("operationalStateValue").selectedIndex;
    var state = $("operationalStateValue").options[index].value;
    var origState = $('operationalStateValue_orig').value;
    operationalStateChange = state != origState;
    
    if (!(operationalStateChange)) {
        alert("No change was made.");
        return false;   
    }
        
    params = {};
    params['paoId'] = $('paoId').value;
    params['controlType'] = $('controlType').value;
        
    params['operationalStateValue'] = $('operationalStateValue').options[$('operationalStateValue').selectedIndex].value;
        
    params['operationalStateChange'] = operationalStateChange;
        
    params['operationalStateReason'] = $('operationalStateReason').value;
        
    var confirmMessage = '';
    if (operationalStateChange) confirmMessage += 'Operational State Change';
        
    if (!confirm(confirmMessage)) return false;
        
    var url = $('url').value;
    
    new Ajax.Request(url, {
        method: 'POST',
        onSuccess: function(transport) {
            display_status("Change Operational State", "", "Success", "green");       
        },
        onFailure: function(transport) {
            display_status("Change Operational State", "", "Failed", "red");   
        },
        onException: function(transport) {
            display_status("Change Operational State", "", "Failed", "red");   
        },
        parameters:params
    });
    
    cClick();
    
}