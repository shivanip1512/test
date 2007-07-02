//function to generate HTML for the command menu
var MOVED_CB_REDIRECT_URL = 999;
function updateCommandMenu(result) {

 if (result != null) {
 	var cmds = document.getElementsByName ('cmd_dyn');
 	for (var i = 0; i < result.length; i++) { 		
        var xmlID = getElementTextNS(result[i], 0, 'id');
        
        for (var j=0; j < cmds.length; j++) {
        	
        	//cmdName = cmd_sub_1213
        	var cmdId = cmds[j].id;
        	//['cmd','sub','1213']
        	var temp = cmdId.split('_');
        	var type = temp[1];
        	var id =   temp[2];
        	//special case since we allow field and system commands for cap banks
        	var sub_type = temp[3];
    
        	var opts;
            
            if (id == xmlID) {
 				//sub command is unique in that it requires additional params to be extracted from the result
 				if (type == 'sub')
 					{
 					 var verify = getElementTextNS(result[i], 0,'param7');
 					 var name = getElementTextNS(result[i], 0,'param8');
 					 if ( verify == 'true'){
 					 	opts = [true, name];
 					 	
 					 } 
 					 else {
 					 	opts= [false, name];
 					 } 
 					}
 				 else if (type == 'fdr') {
 				 	var name = getElementTextNS(result[i], 0,'param7');
 				 	opts = [false, name];
 				 }
 				 else if (type =='cap') {
 				 
 				 	var name = getElementTextNS(result[i], 0,'param3');
 				 	var allow_ovuv = getElementTextNS(result[i], 0,'param4');
 				 	var all_manual_sts = getElementTextNS(result[i], 0,'param5');
                    if (document.getElementById ('2_way_' + id))
                    	var is2_Way = document.getElementById ('2_way_' + id).value;
                    if (document.getElementById ('is701x_' + id))
                    	var is701x = document.getElementById ('is701x_' + id).value;
 				 	if (document.getElementById ('showFlip_' + id))
 				 		var showFlip = document.getElementById ('showFlip_' + id).value;
 				 	showFlip = (showFlip == "true") && (is701x == "true");
 				 	opts = [false, name, sub_type, allow_ovuv, all_manual_sts, is2_Way, showFlip];
 				 	document.getElementById ('cmd_cap_move_back_' + id).value = generate_CB_Move_Back (id, name);		       	
                 }
 				//otherwise the only thing left is to get state
 				var state = getElementTextNS(result[i], 0,'state');
 				
 				update_Command_Menu	(type, id, state, opts);
        	}
        }
 	}
  }
}

//switch to get the html for menu based on type
function update_Command_Menu (type, id, state, opts) {
	//all elements on the page must have a unique id if they are
	//accessed using document.getElementById
	//In case of manual and field commands options we have 2 divs for 1 cap bank
	//if we will just identify it by id it will not be unique
	//Will result in [xxx] is null or not an object error whenever the element with a non-unique id is
	//accessed using document.getElementById
	var cmd_div_uniq_id = 'cmdDiv';
	   
	var header = "<HTML><BODY><div id='" + cmd_div_uniq_id + id + "' ";
	header += " style='background-color:white; height:100%; width:100%; border:1px solid black;'>";
	//!!!!!!!!!!!!!!attempt to use ctiTitledContainer!!!!!!!!!!!!!!!!!!!!!
	//header += " class='cmdPopupMenu' >";
	//var container = new CTITitledContainer (opts[1]);
	//var _start_Tag = container.startTag();
	//var _end_Tag = container.endTag();
	//header += _start_Tag;
	//var footer = _end_Tag; 
	
	footer = " </div></BODY></HTML>";	
	var html = header;
	switch (type) {
		case 'sub':
			html += generateSubMenu (id, state, opts);
			break;
		case 'fdr':
			html += generateFeederMenu (id, state, opts);
			break;
		case 'cap':
			html += generateCapBankMenu (id, state, opts);
			break;
	}
	html += footer;
	//update document to the generated html
	var html_id = 'cmd_'+type+'_'+id;
	if (type == 'cap')
		html_id += '_' + opts[2];
	

	document.getElementById(html_id).value = html;
	 	
}

//function to generate
//html for the sub command menu
function generateSubMenu (id, state, opts) {
 var ALL_SUB_CMDS = {
 					 confirm_close:9,
 				 	 enable_sub:0,
	 				 disable_sub:1,
	 				 reset_op_cnt:12,
	 				 v_all_banks:40,
	 				 v_fq_banks:41,
	 				 v_failed_banks:42,
	 				 v_question_banks:43,
	 				 v_disable_verify:44,
	 				 v_standalone_banks:46
	 				}
 //var table_header = "<table>";
 var table_footer = "</table>";
 var table_body = "<table >";
 table_body += "<tr><td class='top'>" + opts[1] + "</td></tr>"
 
 if (id > 0) {
 	table_body += add_AJAX_Function('sub', id, ALL_SUB_CMDS.confirm_close, 'Confirm_Sub');
 	if (state != 'DISABLED') {
 		table_body += add_AJAX_Function('sub', id, ALL_SUB_CMDS.disable_sub, 'Disable_Sub');
 	}
 	else {
 		table_body += add_AJAX_Function('sub', id, ALL_SUB_CMDS.enable_sub, 'Enable_Sub');
 	}
 	
 	table_body += add_AJAX_Function('sub', id, ALL_SUB_CMDS.reset_op_cnt, 'Reset_Op_Counts');
 	if (!opts[0]){
 		table_body += add_AJAX_Function('sub', id, ALL_SUB_CMDS.v_all_banks, 'Verify_All_Banks');
 		table_body += add_AJAX_Function('sub', id, ALL_SUB_CMDS.v_fq_banks, 'Verify_Failed_And_Questionable_Banks');
 		table_body += add_AJAX_Function('sub', id, ALL_SUB_CMDS.v_failed_banks, 'Verify_Failed_Banks');
 		table_body += add_AJAX_Function('sub', id, ALL_SUB_CMDS.v_question_banks, 'Verify_Questionable_Banks');
 		table_body += add_AJAX_Function('sub', id, ALL_SUB_CMDS.v_standalone_banks, 'Verify_Standalone_Banks');
 	}
 	else {
 		table_body += add_AJAX_Function('sub', id, ALL_SUB_CMDS.v_disable_verify, 'Verify_Stop');	
 	}
 }

	//table_header+= table_body;
	table_body+= table_footer;
	return table_body;

}

//function to generate
//html for the feeder command menu
function generateFeederMenu (id, state, opts) {
 var ALL_FDR_CMDS = {
 				 	 enable_fdr:2,
	 				 disable_fdr:3,
	 				 reset_op_cnt:12
	 				}
 //var table_header = "<table>";
 var table_footer = "</table>";
 var table_body = "<table >";
 table_body += "<tr><td class='top'>" + opts[1] + "</td></tr>"

 if (id > 0) {
 	if (state != 'DISABLED') {
 		table_body += add_AJAX_Function('feeder', id, ALL_FDR_CMDS.disable_fdr, 'Disable_Feeder');
 	}
 	else {
 		table_body += add_AJAX_Function('feeder', id, ALL_FDR_CMDS.enable_fdr, 'Enable_Feeder');
 	}
 	
 	table_body += add_AJAX_Function('feeder', id, ALL_FDR_CMDS.reset_op_cnt, 'Reset_Op_Counts');
 }

	//table_header+= table_body;
	table_body+= table_footer;
	return table_body;

}

							

//function to generate the cap bank move back menu
function generate_CB_Move_Back (id, name, red) {
	 //start and end of div html
	 var div_start_tag = "<HTML><BODY><div id='cb_move_back_" + id + "' ";
	 var div_end_tag = " </div></BODY></HTML>";
	 //css for the div
	 div_start_tag += " style='background:white; height:1cm; width:1cm; border:1px solid black;'>";
	 //init return variable
	 var html = div_start_tag;
	 //generate the table
	 var table_footer = "</table>";
	 var table_body = "<table >";
	 table_body += "<tr><td class='top'>" + name + "</td></tr>"
	 table_body += add_AJAX_Function('cap_move_back', id, 11, 'Temp_Move_Back', false, red);
	 table_body+= table_footer;
	 //append table to the div
	 html += table_body;
	 //append end tag to the div
	 html += div_end_tag;
 
 return html;			  
}

//function to generate the cap bank move back menu
function generate_SubAreaMenu (id, name, enable) {
     //start and end of div html
     var div_start_tag = "<HTML><BODY><div id='area" + id + "' ";
     var div_end_tag = " </div></BODY></HTML>";
     //css for the div
     div_start_tag += " style='background:white; height:100%; width:100%; border:1px solid black;'>";
     //init return variable
     var html = div_start_tag;
     //generate the table
     var table_footer = "</table>";
     var table_body = "<table >";
         
     table_body += "<tr><td class='top'>" + name + "</td></tr>"
     table_body += add_AJAX_Function('area', id, 21, 'Confirm_Area');
     if (enable == 1)
        table_body += add_AJAX_Function('area', id, 22, 'Enable_Area');
     else
        table_body += add_AJAX_Function('area', id, 23, 'Disable_Area');
     
     table_body+= table_footer;
     //append table to the div
     html += table_body;
     //append end tag to the div
     html += div_end_tag;
 
 return html;             
}

//function to generate cap bank menu
function generateCapBankMenu (id, state, opts) {
 var ALL_CAP_CMDS = {
 	confirm_open:8,
 	open_capbank:6,
 	close_capbank:7,
 	bank_enable_ovuv:17,
 	bank_disable_ovuv:18,
 	enable_capbank:4,
 	disable_capbank:5,
 	reset_op_cnt:12,
    scan_2way_dev:24,
    flip_7010: 27
 	}
 var table_footer = "</table>";
 var table_body = "<table >";
 //opts = [false, name, sub_type, allow_ovuv, all_manual_sts];
 var allow_ovuv = opts[3];
 //contains all the possible manual states for a cap bank
 var manual_states = opts[4];
 table_body += "<tr><td class='top'>" + opts[1] + "</td></tr>"
	
	if (id > 0) {
		if (opts[2] == 'field') {
		   table_body += add_AJAX_Function('cap', id, ALL_CAP_CMDS.confirm_open, 'Confirm', false);
		   table_body += add_AJAX_Function('cap', id, ALL_CAP_CMDS.open_capbank, 'Open_Capacitor', false);
		   table_body += add_AJAX_Function('cap', id, ALL_CAP_CMDS.close_capbank, 'Close_Capacitor', false);
           if (opts[5] == 'true')
            {
                table_body += add_AJAX_Function('cap', id, ALL_CAP_CMDS.scan_2way_dev, 'Init_Scan', false);
            }
		
		if (allow_ovuv == 'true') {
		   table_body += add_AJAX_Function('cap', id, ALL_CAP_CMDS.bank_enable_ovuv, 'Enable_OV/UV', false);
		   table_body += add_AJAX_Function('cap', id, ALL_CAP_CMDS.bank_disable_ovuv, 'Disable_OV/UV', false);	
		   }
		
		if (opts[6])
			{
		   		table_body += add_AJAX_Function('cap', id, ALL_CAP_CMDS.flip_7010, 'Flip', false);	
			}
		}
		else if (opts[2] == 'system') {
		     
		     if (state.indexOf('DISABLED') > -1) {
		     	table_body += add_AJAX_Function('cap', id, ALL_CAP_CMDS.enable_capbank, 'Enable_CapBank', false);
		     }
		     else {
		     	table_body += add_AJAX_Function('cap', id, ALL_CAP_CMDS.disable_capbank, 'Disable_CapBank', false);	
		     }
		     table_body += add_AJAX_Function('cap', id, ALL_CAP_CMDS.reset_op_cnt, 'Reset_Op_Counts', false);
		//split the string up into elements
		//states = "Open:0,Close:1"
		var states = manual_states.split(',');
		for (var i=0; i < states.length; i++) {
		    //Open:1
		    var st = states[i];
		    var temp = st.split (':');
		    var state_name = temp[0];
		    var raw_st_id = temp[1];
		    table_body += add_AJAX_Function('cap', id, raw_st_id, state_name, true);
		    }
		
		}
		
	}
	table_body+= table_footer;
	return table_body;
}

//helper function to set opcounts
function enableOpCounts (pao_id) {
	window.document.getElementById ("cmdDiv"+pao_id).style.display = 'none';	
	window.document.getElementById ("cb_state_td_hdr1").style.display = '';
	window.document.getElementById ("cb_state_td_hdr2").style.display = '';
	enableDisplayAll ('capBankTable');
    var invis_idx = 4;
	//the index of the invisible input text field in the table
    //located right after CB Name(Order) column
    alignHeadersIgnoreDisplayNone ('capBankTable','capBankHeaderTable', invis_idx);	
	window.document.getElementById ("cap_opcnt_span"+pao_id).style.display = '';
}


//function that returns html string for js ajax function that will
//execute on the server when the user clicks on the 
//command menu 
function add_AJAX_Function (type, pao_id, cmd_id, cmd_name, is_manual_state, red) {
var ajax_func = "<tr><td>";
ajax_func += "<a  href='javascript:void(0);'";
ajax_func += " class='optDeselect'" 
ajax_func += " onmouseover='changeOptionStyle(this);' "
ajax_func += " onclick=";
var str_cmd = '\"' + cmd_name + '\"';
switch (type) {
 case 'sub':
	
	ajax_func +=  	"'executeSubCommand (" + pao_id + "," + cmd_id + "," + str_cmd + "); '";
	break;
 case 'feeder':
 	ajax_func +=  	"'executeFeederCommand (" + pao_id + "," + cmd_id + "," + str_cmd + "); '";
	break;
 case 'cap':
 	//special case for reset op-counts
	if (cmd_id == 12) {
		var temp_str = "'enableOpCounts(" + pao_id + ");'"; 
		ajax_func += temp_str;
	}
 	else
 		ajax_func +=  	"'executeCapBankCommand (" + pao_id + "," + cmd_id + "," + is_manual_state + "," + str_cmd + "); '";
	
	break;
 case 'cap_move_back':
    if (red == 999)
    	ajax_func +=    "'execute_CapBankMoveBack (" + 	pao_id + "," + cmd_id + "," + red + "); '";
	else
  		ajax_func +=    "'execute_CapBankMoveBack (" + 	pao_id + "," + cmd_id + "); '";
		
    break;
 case 'area':
 
    ajax_func += "'execute_SubAreaCommand (" + pao_id + "," + cmd_id + "," + str_cmd + "); '";
    break;
}
	
ajax_func += ">"+ cmd_name+"</a>";
ajax_func += "</td></tr>";
return ajax_func;
}


//AJAX command functions
///////////////////////////////////////////////
function executeSubCommand (paoId, command, cmd_name) {
	new Ajax.Request ('/servlet/CBCServlet', 
	{method:'post', 
	parameters:'cmdID='+command+'&paoID='+paoId + '&controlType=SUB_TYPE',
	onSuccess: function () { display_status(cmd_name, "Success", "green"); },
	onFailure: function () { display_status(cmd_name, "Failed", "red"); }, 
	asynchronous:true });
	var cmdDiv = document.getElementById ('cmdDiv' + paoId);
	cmdDiv.style.display = 'none';
}


function executeFeederCommand (paoId, command, cmd_name) {
	
	new Ajax.Request ('/servlet/CBCServlet', 
	{method:'post', 
	 parameters:'cmdID='+command+'&paoID='+paoId + '&controlType=FEEDER_TYPE', 
	 onSuccess: function () { display_status(cmd_name, "Success", "green"); },
	 onFailure: function () { display_status(cmd_name, "Failed", "red"); }, 
	 asynchronous:true });
	var cmdDiv = document.getElementById ('cmdDiv' + paoId);
	cmdDiv.style.display = 'none';
}

function executeCapBankCommand (paoId, command, is_manual_state, cmd_name, cmd_div_name) {
	var unique_id = 'cmdDiv'+paoId;
	var cmdDiv = document.getElementById (unique_id);
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
			cmdDiv = handleOpcountDiv (cmd_div_name);
		}
		else {
			new Ajax.Request ('/servlet/CBCServlet', 
			{method:'post', 
			parameters:'cmdID='+command+'&paoID='+paoId + '&controlType=CAPBANK_TYPE', 
			onSuccess: function () { display_status(cmd_name, "Success", "green"); },
			onFailure: function () { display_status(cmd_name, "Failed", "red"); }, 
			asynchronous:true });
			cmdDiv = document.getElementById (unique_id);
		}
	}
        cmdDiv.style.display = 'none';
}

function execute_CapBankMoveBack (paoId, command, redirect) {
	var red = '/capcontrol/feeders.jsp';
	var replace = 'feeders.jsp';
	if (redirect == MOVED_CB_REDIRECT_URL) {
		red = '/capcontrol/movedCapBanks.jsp';
		replace = 'movedCapBanks.jsp'
		}
	new Ajax.Request('/servlet/CBCServlet', 
	{ method:'post', 
	  parameters:'paoID='+ paoId +'&cmdID=' + command + '&controlType=CAPBANK_TYPE&redirectURL=' + red,
		onSuccess: function () { window.location.replace (replace);
	},
		onFailure: function () { display_status('Move Bank', "Failed", "red"); },
	asynchronous:true
	});
	var cmdDiv = document.getElementById ('cb_move_back_' + paoId);
	cmdDiv.style.display = 'none';
	
		
} 

function execute_SubAreaCommand (pao_id, cmd_id, cmd_name) {
    new Ajax.Request('/servlet/CBCServlet', 
    { method:'post', 
      parameters:'paoID='+ pao_id +'&cmdID=' + cmd_id + '&controlType=AREA_TYPE',
        onSuccess: function () { display_status(cmd_name, "Success", "green"); },
        onFailure: function () { display_status(cmd_name, "Failed", "red"); },
    asynchronous:true
    });
    var cmdDiv = document.getElementById ('area' + pao_id);
    cmdDiv.style.display = 'none';


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
$("outerDiv").style.position = 'absolute';
var last_titled_cont  = $('last_titled_container'); 
if (last_titled_cont != null) {
	$("outerDiv").style.left = last_titled_cont.style.width;
	$("outerDiv").style.top =  last_titled_cont.style.height;
}
else {
	$("outerDiv").style.left = 0;
	$("outerDiv").style.top =  0;
}
$("outerDiv").style.width = 200; 
//var titledCont = new CTITitledContainer (cmd_name);

msg_div.innerHTML = cmd_name + ":" + msg;
var timeout = 0;
if (color == "red") {
	$("outerDiv").style.display = 'block';
	Effect.Pulsate('outerDiv', {duration: 8});
	timeout = 8000;
	}
 else {
 	Effect.Appear('outerDiv');
	timeout = 2000;
	}
	setTimeout ('hideMsgDiv()', timeout);	
}

function hideMsgDiv() {
	Effect.Fade('outerDiv');
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
	document.getElementById('cb_state_td_hdr1').style.display = 'none';
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

