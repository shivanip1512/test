var executeURL = '/spring/capcontrol/commandexecutor?action=';
var executeTierURL = executeURL + 'executeCommandTier';
var reasonMenuURL = '/spring/capcontrol/tier/popupmenu?menu=reasonMenu';
var tempMoveBackURL = executeURL + 'executeTempMoveBack';

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
    var isReasonRequired = eval(reasonRequired);
    if (!isReasonRequired) {
        executeCommandController(paoId, cmdId, commandName, controlType);
        return;
    }

    var url = reasonMenuURL +
     '&paoId=' + paoId +
     '&cmdId=' + cmdId +
     '&controlType=' + controlType +
     '&commandName=' + commandName;
     
    getMenuFromURL(url);
}

function executeCommandController(paoId, commandId, commandName, controlType, reason, opt) {
    var parameters = {
        'paoId': paoId,
        'cmdId': commandId,
        'controlType': controlType 
    };
    
    if (reason) parameters['reason'] = reason;
    if (opt) parameters['opt'] = opt;

    new Ajax.Request(executeTierURL, {
        'method': 'POST',
        'parameters': parameters,
        'onSuccess': function (transport, result) {
            if (result) { 
                if(result.success){
                    display_status(cmd_name, "Success", "green");
                } else {
                    alert(result.errMessage);
                }
            } else {
                display_status(commandName, "Success", "green");    
            }    
        },
        'onFailure': function () {
            display_status(commandName, "Failed", "red"); 
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
            window.location.replace(redirectURL);
        },
		onFailure: function() {
            display_status('Move Bank', "Failed", "red"); 
        }
	});
} 

///////////////////////////////////////////////

//attempt to use the current ctiTitledContainer tag
function CTITitledContainer (t) {
	this.title = t;
}

CTITitledContainer.prototype.startTag = function () {
var start_tag = "  <table class='resizeRoundTable'>"; 
    start_tag += "    <tr> "; 
    start_tag += "      <td class='upperLeft'></td>";
    start_tag += "      <td class='top'>";
    start_tag += this.title;
    start_tag += "</td>"; 
	start_tag += "<td class='upperRight'></td>"; 
    start_tag += "    </tr>"; 
    start_tag += "    <tr>"; 
    start_tag += "      <td class='leftSide'></td>"; 
    start_tag += "<td>";

return start_tag;
};

CTITitledContainer.prototype.endTag = function () {
var endTag = "";
 	endTag += "</td>"; 
    endTag += "      <td class='rightSide'></td>"; 
	endTag +=     "    </tr>"; 
	endTag +=     "    <tr>"; 
	endTag +=     "      <td class='lowerLeft'></td>"; 
	endTag +=     "      <td class='bottom'></td>"; 
	endTag +=     "      <td class='lowerRight'></td>"; 
	endTag +=     "    </tr>"; 
	endTag +=     "  </table>"; 
	endTag +=     "";

return endTag;
};

//function to display wheather the call to serlvlet went through
//will pop up a div that has a header with 'cmd_name' with gray background
//'msg' in the body that with 'color' for background

function display_status(cmd_name, msg, color) {
    var msg_div = $('cmd_msg_div');
    msg_div.style.color = color;
    msg_div.innerHTML = cmd_name + " : " + msg;
    
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
    $('outerDiv').hide();
}

function getMoveBackMenu() {
var hiddens = document.getElementsByName("pf_hidden");
for (var i=0; i < hiddens.length; i++) {
	var hidden = hiddens[i];
	var cap_bank_id = hidden.id.split('_')[1];
	hidden.value = generate_CB_Move_Back (cap_bank_id, hidden.parentNode.id, MOVED_CB_REDIRECT_URL);
	}
}

function handleSystemCommand() {
	var message ='';
	systemIsOn = $('systemCommandLink').getElementsByTagName ('a')[0].id == 'systemOn';
	if (systemIsOn)
		message = 'You are turning the system off. Please confirm...';	
	else
		message = 'You are turning the system back on. Please confirm...';	

	if (confirm (message))
	{
		sendSystemEnableCommand (systemIsOn);
	}
	else
	{
		alert ('Command cancelled successfully.');
	}
}

function sendSystemEnableCommand (systemIsOn) {
	new Ajax.Request("/spring/capcontrol/cbcAjaxController?action=executeSystemCommand", 
		{
			method:"post", 
			parameters:"turnSystemOff=" + systemIsOn, 
			asynchronous:true
			});

}

