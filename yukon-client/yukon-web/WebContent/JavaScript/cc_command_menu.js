
//helper function to set opcounts
function enableResetOpCountSpan (pao_id) {
	window.document.getElementById ("cb_state_td_hdr2").style.display = '';
    var elem = document.getElementById('cap_opcnt_span'+pao_id);
	elem.style.display = '';
}

//AJAX command functions
///////////////////////////////////////////////

function executeSubstationCommand (paoId, command, cmd_name) {
	new Ajax.Request ('/servlet/CBCServlet', 
	{method:'post', 
	parameters:'cmdID='+command+'&paoID='+paoId + '&controlType=SUBSTATION_TYPE',
	onSuccess: function () { display_status(cmd_name, "Success", "green"); },
	onFailure: function () { display_status(cmd_name, "Failed", "red"); }, 
	asynchronous:true });
	return cClick();
}

function executeSubCommand (paoId, command, cmd_name) {
	new Ajax.Request ('/servlet/CBCServlet', 
	{method:'post', 
	parameters:'cmdID='+command+'&paoID='+paoId + '&controlType=SUB_TYPE',
	onSuccess: function () { display_status(cmd_name, "Success", "green"); },
	onFailure: function () { display_status(cmd_name, "Failed", "red"); }, 
	asynchronous:true });
	return cClick();
}

function executeFeederCommand (paoId, command, cmd_name) {
	new Ajax.Request ('/servlet/CBCServlet', 
	{method:'post', 
	 parameters:'cmdID='+command+'&paoID='+paoId + '&controlType=FEEDER_TYPE', 
	 onSuccess: function () { display_status(cmd_name, "Success", "green"); },
	 onFailure: function () { display_status(cmd_name, "Failed", "red"); }, 
	 asynchronous:true });
}

function executeCapBankCommand (paoId, command, cmd_name, is_manual_state, cmd_div_name) {
	var RESET_OP_CNT = 12;
	if (is_manual_state) {
		new Ajax.Request ('/servlet/CBCServlet', 
		{method:'post', 
		parameters:'opt='+command+'&cmdID='+30+'&paoID='+paoId + '&controlType=CAPBANK_TYPE', 
	 	onSuccess: function () { display_status(cmd_name, "Success", "green"); },
		onFailure: function () { display_status(cmd_name, "Failed", "red"); }, 
		asynchronous:true });		
	}
	else {
		//special case for reset_op_counts command
		if (command == RESET_OP_CNT) {
			handleOpcountRequest (command, paoId, cmd_name)					
			var cmdDiv = handleOpcountDiv (cmd_div_name);
            cmdDiv.style.display = 'none';
		}
		else {
			new Ajax.Request ('/servlet/CBCServlet', 
			{method:'post', 
			parameters:'cmdID='+command+'&paoID='+paoId + '&controlType=CAPBANK_TYPE', 
			onSuccess: function () { display_status(cmd_name, "Success", "green"); },
			onFailure: function () { display_status(cmd_name, "Failed", "red"); }, 
			asynchronous:true });
		}
	}
        
}

function execute_CapBankMoveBack (paoId, command, redirectURL) {
    var url = '/servlet/CBCServlet';
	new Ajax.Request(url, { 
        method: 'POST', 
	    parameters: 'paoID='+ paoId +'&cmdID=' + command + '&controlType=CAPBANK_TYPE&redirectURL=' + redirectURL,
		onSuccess: function() {
            window.location.replace(redirectURL);
        },
		onFailure: function() {
            display_status('Move Bank', "Failed", "red"); 
        }
	});
} 

function execute_SubAreaCommand (pao_id, cmd_id, cmd_name) {
    new Ajax.Request('/servlet/CBCServlet', 
    { method:'post', 
      parameters:'paoID='+ pao_id +'&cmdID=' + cmd_id + '&controlType=AREA_TYPE',
        onSuccess: function () { display_status(cmd_name, "Success", "green"); },
        onFailure: function () { display_status(cmd_name, "Failed", "red"); },
    asynchronous:true
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

function handleOpcountRequest (command, paoId, cmd_name) {
	var op_cnt = document.getElementById('opcnt_input'+paoId).value;
	//make sure that contains a valid number
	if (!isValidOpcount (op_cnt)) {
		alert ('Op Count value not specified. New Op Count value will be set to 0.');
		op_cnt = 0;
	}
	else
		op_cnt = parseInt(op_cnt);
	new Ajax.Request ('/servlet/CBCServlet', 
					{method:'post', 
					parameters:'cmdID='+command+'&paoID='+paoId + '&controlType=CAPBANK_TYPE&opt='+op_cnt, 
					onSuccess: function () { display_status(cmd_name, "Message sent successfully", "green"); },
					onFailure: function () { display_status(cmd_name, "Command failed", "red"); }, 
					asynchronous:true });

}

function handleOpcountDiv (cmd_div_name) {
	var cmdDiv = document.getElementById (cmd_div_name);
	document.getElementById('cb_state_td_hdr2').style.display = 'none';
	alignHeaders ('capBankTable','capBankHeaderTable');
	return cmdDiv;
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

