var ALL_POPUP_TYPES = {
	//types that are generated by the server
	//so the server knows about them
	subCommand: "SubCommand",
	feederCommand: "FeederCommand",
	capCommand: "CapCommand",
	subTag: "SubTag",
	feederTag: "FeederTag",
	capTag: "CapTag",
	capInfo: "CapInfo",	
	//types that are not known on the server
	//because they are the children 
	childCapMaint: "CapBankMaint",
	childCapDBChange:"CapDBChange"
};




function openPopupWin(elem, compositeIdType) {
	popupx = parseInt(elem.getAttribute("x"));
	popupy = parseInt(elem.getAttribute("y"));
	if (!(popupx || popupy ))
	{
		origDiv = document.getElementById("controlrequest");
		popupx = origDiv.offsetLeft;
		popupy = origDiv.offsetTop;
	}

	currentPopup = new PopupWindow("controlrequest");
	currentPopup.offsetX = popupx + 20;
	currentPopup.offsetY = popupy - 20;
	
	currentPopup.autoHide();
	
	type = compositeIdType.split("_")[0];
	id = compositeIdType.split("_")[1];
	var url;
	if (type == ALL_POPUP_TYPES.subCommand) {
		url = createSubMenu();
	}
	else if (type == ALL_POPUP_TYPES.feederCommand) {
			url = createFeederMenu(id);

	}
	else if (type == ALL_POPUP_TYPES.capCommand) {
				url = createCapBankMenu(id);
	}
	else if (type == ALL_POPUP_TYPES.subTag) {
						url = createSubTagMenu();
	}
	else if (type == ALL_POPUP_TYPES.feederTag) {
						url = createFeederTagMenu(id);
	}
	else if (type == ALL_POPUP_TYPES.capTag) {
						url = createCapTagMenu(id);
	}
	
	else if (type == ALL_POPUP_TYPES.capInfo) {
						url = createCapInfoMenu(id);
	}
	
	else if (type == ALL_POPUP_TYPES.childCapMaint) {
					url = createCapbankMaint(id);
	}
	else if (type == ALL_POPUP_TYPES.childCapDBChange) {
						url = createCapbankDBChange(id);
	}


	currentPopup.populate(url);
	currentPopup.showPopup("popupanchor");
	window.parent.currentPopup = currentPopup;
	window.parent.document.getElementById("controlrequest").style.borderStyle = "solid";
	window.parent.document.getElementById("controlrequest").style.borderColor = "gray";
	window.parent.document.getElementById("controlrequest").style.borderWidth = "thin";
	window.parent.document.getElementById("controlrequest").style.backgroundColor = "black";
}

function createCapInfoTable (paoName, testData) {

var allRows = "";
var str = '';
str+='				<table >';
str+='						<tr style="color:#9FBBAC; font-weight: bold; font-size: 18;" >';
str+='							<td align="center" colspan="3" > <u>';
str+= paoName;
str+= '<\/u> <\/td>';
str+='							<td align="right" valign="top" id = "popupplaceholder">';
str+='								<a   href="javascript:void(0)" style="color=gray" title="Click To Close" onclick="closePopupWindow();"> x <\/a>';
str+='							<\/td>';
str+='						<\/tr>';
for (var key in REF_LABELS)
{
	label = REF_LABELS[key];
	data  = testData[key];
	str+='<tr >';
	str+='			<td align="left" style="color:gray">';
	str+='				' + label;
	str+='			<\/td>';
	str+='			<td align="right" style="color:gray"> ' + data +' <\/td>';
	str+='		<\/tr>';

}
str+='<\/table>';
return str;

}

function createCapInfoMenu (capID) {	
	var testData = new Object();
	var paoName = getState("CapState_" + capID, "paoName");
	allAttribs = getHiddenAttributes("CapHiddenInfo_" + capID);
	for(var i=0; i<allAttribs.length; i++){
		element = allAttribs.item(i);
		name = element.getName();
		val = element.getValue();
		if (findValueByKey(name, REF_LABELS)){
			testData[name] = val;
		}
	}
	return createCapInfoTable (paoName, testData);
}

function createFeederTagMenu(feederID) {
	//state var
	var isDis = getState("FeederState_" + feederID, "isDisable");
	var disFeederTag;

	if (isDis == "true")
	{

		disableFeeder = new Command (feederID, ALL_FDR_CMDS.enable_fdr, ALL_CMD_TYPES.feeder);
		disFeederTag = new Command (feederID, "feederEnabled", ALL_CMD_TYPES.tag);
		var disableReason = getState("FeederState_" + feederID, "disableFdrReason");

	}
	else
	{
		disableFeeder = new Command (feederID, ALL_FDR_CMDS.disable_fdr, ALL_CMD_TYPES.feeder);
		disFeederTag = new Command (feederID, "feederDisabled", ALL_CMD_TYPES.tag);
		
	}
	var str='';
	str += '<html>';
	str+='<body style="background-color:black">';
	str+='	<table >';
	str+='		<tr>';
	str+='			<td>';
	str+='				<span id="fdrTagSpan_';
	str+= 				feederID;
	str+='">';
	str+='				<input type="hidden" id="executeQueue_' + feederID + '" val=""/>';
	
	str+='				<input type="checkbox" name="'  ;
	str+= 			disableFeeder.createName() + '"';
	str+=' 			onclick="addCommand(this); '; 
	str+= ' 				addCommand(\'' + disFeederTag.createName() +'\'); setReason(\'' + disFeederTag.createName() + '\', \'' + disableReason + '\', this);"';
	if (isDis == "true")
			str+= ' checked ';
	str+='> <font color="white">Disable<\/font><\/><\/br>';
	str+='				<\/span>';
	str+='			<\/td>';
	str+='			<td align="right" valign="top" id = "popupplaceholder">';
	str+='				<a   href="javascript:void(0)" style="color=gray" title="Click To Close" onclick="closePopupWindow();"> x <\/a>';
	str+='			<\/td>';
	str+='		<\/tr>';
	str+='		<tr>';
	str+= 			'<td>';
	str += 				generateReasonSpan ((isDis == "true"), disFeederTag.createName() + 'ReasonSpan', disableReason);
	str+='			<\/td>';
	str+='		<\/tr>';
	str+='		<tr>';
	str+='			<td>';
	str+='				<input  type="submit" name="execute" value="Execute" onclick="disableAll(); executeMultipleCommands(\'fdrTagSpan_\','+feederID+'); reset(this); disableAllCheckedReasons(\'fdrTagSpan_\','+feederID+')"\/>';
	str+='			<\/td>';
	str+='		<\/tr>';
	str+='	<\/table>';
	str+='	<\/body>';
	str += '</html>';

return str;

}

function createSubTagMenu() {
	//state var
	paoId = getSubId();
	var disSubTag;
	var isDis = getState("SubState_" + paoId, "isDisable");
	//say (paoId +","+isDis);
	if (isDis == "true")
	{
		disableSub = new Command (paoId, ALL_SUB_CMDS.enable_sub, ALL_CMD_TYPES.sub);
		disSubTag = new Command (paoId, "subEnabled", ALL_CMD_TYPES.tag);
		var disableReason = getState("SubState_" + paoId, "subDisableReason");
		
	}
	else
	{
		disableSub = new Command (paoId, ALL_SUB_CMDS.disable_sub, ALL_CMD_TYPES.sub);
		disSubTag = new Command (paoId, "subDisabled", ALL_CMD_TYPES.tag);
		
	}
	var str='';
	str += '<html>';
	str+='<body style="background-color:black">';
	str+='	<table>';
	str+='		<tr>';
	str+='			<td>';
	str+='				<span id="subTagSpan_';
	str+= 				paoId;
	str+='">';
	str+='				<input type="hidden" id="executeQueue_' + paoId + '" val=""/>';
	str+='				<input type="checkbox"  name = "'; 
	str+= 				disableSub.createName() + '" ';
	str+=' 				onclick="addCommand(this); '; 
	str+= ' 				 	 addCommand(\'' + disSubTag.createName() +'\'); setReason(\'' + disSubTag.createName() + '\', \'' + disableReason + '\', this);"';
	if (isDis == "true")
		str+= ' checked ';
	str+='>'; 
	str+='				<font color="white">Disable<\/font><\/><\/br>';
	str+='			<td align="right" valign="top" id = "popupplaceholder">';
	str+='				<a   href="javascript:void(0)" style="color=gray" title="Click To Close" onclick="closePopupWindow();">  x <\/a>';
	str+='			<\/td>';
	str+='		<\/tr>';
	str+='		<tr>';
	str+= 			'<td>';
	str += 				generateReasonSpan ((isDis == "true"), disSubTag.createName() + 'ReasonSpan', disableReason);
	str+='			<\/td>';
	str+='		<\/tr>';
	str+='		<tr>';
	str+='			<td>';
	str+='				<input  type="submit" name="execute" value="Execute" onclick="disableAll(); executeMultipleCommands(\'subTagSpan_\','+paoId+'); reset(this); disableAllCheckedReasons(\'subTagSpan_\','+paoId+')"\/>';
	str+='			<\/td>';
	str+='		<\/tr>';
	str+='	<\/table>';
	str+='	<\/body>';
	str += '</html>';
	
	return str;
}

function createCapTagMenu (paoID) {
	//state var	
	var isDis = getState("CapState_" + paoID, "isDisable");
	var paoName = getState("CapState_" + paoID, "paoName");
	var disCapTag;
	debug = 1;
	if (isDis == "true")
	{
		disableCap = new Command (paoID, ALL_CAP_CMDS.enable_capbank, ALL_CMD_TYPES.cap);
		disCapTag = new Command (paoID, ALL_TAG_CMDS.capEnabled, ALL_CMD_TYPES.tag);
		var disableCapReason = getState("CapState_" + paoID, "disableCapReason");
	}
	else 
	{
		disableCap = new Command (paoID, ALL_CAP_CMDS.disable_capbank, ALL_CMD_TYPES.cap);
		disCapTag = new Command (paoID, ALL_TAG_CMDS.capDisabled, ALL_CMD_TYPES.tag);	
	}
	var isOVUVDis = getState("CapState_" + paoID, "isOVUVDis");
	var isStandalone = getState("CapState_" + paoID, "isStandalone");
	if (isStandalone == "true")
	{
		aloneCap = new Command (paoID, ALL_TAG_CMDS.switched, ALL_CMD_TYPES.tag);
		var aloneReason = getState("CapState_" + paoID, "standAloneReason");
	}
	else
		aloneCap = new Command (paoID, ALL_TAG_CMDS.standalone, ALL_CMD_TYPES.tag);
		
	var str='';
	str += '<html>';
	str+='<body style="background-color:black">';
	var str='';
	str+='	<table>';
	str += '<table border=2 style="border-color: black black white black; width=200">'
	str+='			<tr style="color:#9FBBAC; font-weight: bold; font-size: 16; border-color: black black white black;" >';
	str+='				<td style="border-color: black black black black;" align="center" colspan="2" > ' + paoName + ' <\/td>';
	str+='				<td style="border-color: black black black black;" align="right" valign="top" id = "popupplaceholder">';
	str+='					<a   href="javascript:void(0)" style="color=#9FBBAC" title="Click To Close" onclick="closePopupWindow();"> x <\/a>';
	str+='				<\/td>';
	str+='			<\/tr>';
	str += '<\/table>'
	str+='		<tr>';
	str+='			<td>';
	str+='				<span id="capTagSpan_';
	str+= 				paoID;
	str+='">';
	str+='				<input type="hidden" id="executeQueue_' + paoID + '" val=""/>';
	str+='					<input name="';
	str+=					disableCap.createName();
	str+='" type="checkbox" onclick="addCommand(this); '; 
	str+=' addCommand(\'' + disCapTag.createName() +'\'); setReason(\'' + disCapTag.createName() + '\', \'' + disableCapReason + '\', this)"';
	if (isDis == "true")
	str+=					' checked ';
	str+='> <font color="white">Disable<\/font><\/><\/br>';
	str += generateReasonSpan((isDis == "true"),  disCapTag.createName() + 'ReasonSpan' , disableCapReason);
	str+='					<input  name="';
	str+=					"OVUDDisPlaceHolder"
	str+='" type="checkbox" onclick="say(\'This feature is not yet implemented\');closePopupWindow()"; ';
	if (isOVUVDis == "true")
			str+=					' checked ';
	str+='> <font color="white">Disable OV\/UV<\/font><\/><\/br>';
	str+='					<span id="';
	str+=					"OVUDDisPlaceHolderReasonSpan"
	str+='" style="display: none"><\/span><\/br>';
	str+='					<input   name="';
	str+=					aloneCap.createName();
	str+='" type="checkbox" onclick="addCommand(this); setReason(this, \'' + aloneReason + '\')"';
	if (isStandalone == "true")
			str+=					' checked';
	str+='> <font color="white">Standalone<\/font><\/><\/br>';
		str += generateReasonSpan ((isStandalone == "true"), aloneCap.createName() + 'ReasonSpan', aloneReason);
	str+='<\/span><\/br>';
	str+='				<\/span>';
	str+='			<\/td>';
	str+='		<\/tr>';
	str+='		<tr>';
	str+='			<td>';
	str+='				<input  type="submit" name="execute" value="Execute" onclick="disableAll(); executeMultipleCommands(\'capTagSpan_\','+paoID+'); reset(this); disableAllCheckedReasons(\'capTagSpan_\','+paoID+')"\/>';
	str+='			<\/td>';
	str+='		<\/tr>';
	str+='	<\/table>';
	str+='	<\/body>';
	str += '</html>';

return str;
}
function createCapBankMenu(paoID) {
	var open = new Command (paoID, ALL_CAP_CMDS.open_capbank, ALL_CMD_TYPES.cap);
	var close = new Command (paoID, ALL_CAP_CMDS.close_capbank, ALL_CMD_TYPES.cap);
	var confirm = new Command (paoID, ALL_CAP_CMDS.confirm_open, ALL_CMD_TYPES.cap);
	var paoName = getState("CapState_" + paoID, "paoName");
	var str = "";
	str += "<html>";
	str += "<body>";
	str += "<table border=2 style=\"border-color: black black white black; width=200\">";
	str+="			<tr style=\"color:#9FBBAC; font-weight: bold; font-size: 16; border-color: black black white black;\" >";
	str+="				<td style=\"border-color: black black black black;\" align=\"center\" rowspan=\"2\" colspan=\"2\" > " + paoName + " <\/td>";
	str+="				<td style=\"border-color: black black black black;\" align=\"right\" valign=\"top\" id = \"popupplaceholder\">";
	str+="					<a   href=\"javascript:void(0)\" style=\"color=#9FBBAC\" title=\"Click To Close\" onclick=\"closePopupWindow();\"> x <\/a>";
	str+="				<\/td>";
	str+="			<\/tr>";
	str += "<\/table>"	
	str += "<table style=\"background-color:black\" name=\"commandTable\">";
	str += "			<tr >";
	str += "				<td align=\"center\" colspan=\"2\">";
	str += "				<input type=\"submit\" name=\"";
	str +=					open.createName();
	str +="\" value=\"open\" onclick = \"disableAll(); submit(this); reset(this)\";/>";
	str += "				<input type=\"submit\" name=\"";
	str +=					close.createName();
	str +="\" value=\"close\" onclick = \"disableAll(); submit(this); reset(this)\"/>";
	str += "				<input type=\"submit\" name=\"";
	str +=					confirm.createName();
	str +="\" value=\"confirm \" onclick = \"disableAll(); submit(this); reset(this)\"/>";
	str += "				</td>";

	str += "			</tr>";
	str += "			<tr>";
	str += "				<td  style=\"color=gray\">";
	str += "				<a href=\"javascript:void(0)\" onclick=\"disableAll();openPopupWin(this, '"; 
	str += 					ALL_POPUP_TYPES.childCapMaint + "_" + paoID;
	str += "')\" style=\"color:white\">Maintenance </a>";
	str += "				</td>";
	str += "				<td >";
	str += "				<a href=\"javascript:void(0)\" onclick=\"disableAll();openPopupWin(this, '";
	str += 					ALL_POPUP_TYPES.childCapDBChange + "_" + paoID;
	str += "')\" style=\"color:white\">DB Change </a>";
	str += "				</td>";
	str += "			</tr>";
	str += "		</table>";
	str += "		<DIV ID=\"controlrequest\" STYLE=\"position:absolute;visibility:hidden;background-color:#000000;\"></DIV>";
	str += "		<A NAME=\"popupanchor\" ID=\"popupanchor\" > </A>";
	str += "	</body>";
	str += "</html>";
	return str;
}
function createFeederMenu(paoID) {
	resetOpcount = new Command (paoID, ALL_FDR_CMDS.reset_op_cnt, ALL_CMD_TYPES.feeder);
	var str='';
	str+='<html>';
	str+='	<body>';
	str+='	<table style="background-color:black" name="commandTable">';
	str+='			<tr align="center">';
	str+='				<td>';
	str+='				<input type="submit" name="';
	str+= 				resetOpcount.createName();
	str+='" value="Reset Opcount " onclick = "disableAll(); submitWithConfirm(this); reset(this)"\/>';
	str+='				<\/td>';
	str+='				<td align="right" valign="top">';
	str+='				<a href="javascript:void(0)" style="color=gray" title="Click To Close" onclick="closePopupWindow();"> x <\/a>';
	str+='				<\/td>';
	str+='			<\/tr>';
	str+='		<\/table>';
	str+='	<\/body>';
	str+='<\/html>';
	return str;
}
function createSubMenu() {
	//state var
	paoId = getSubId();
	var resetOpcount = new Command (paoId, ALL_SUB_CMDS.reset_op_cnt, ALL_CMD_TYPES.sub);
	var confirmSub = new Command (paoId, ALL_SUB_CMDS.confirm_close, ALL_CMD_TYPES.sub);
	
	var verifyAll = new Command (paoId, ALL_SUB_CMDS.v_all_banks, ALL_CMD_TYPES.sub);
	var verifyFQ = new Command (paoId, ALL_SUB_CMDS.v_fq_banks, ALL_CMD_TYPES.sub);
	var verifyFailed = new Command (paoId, ALL_SUB_CMDS.v_failed_banks, ALL_CMD_TYPES.sub);
	var verifyQuestion = new Command (paoId, ALL_SUB_CMDS.v_question_banks, ALL_CMD_TYPES.sub);
	var verifyStandalone = new Command (paoId, ALL_SUB_CMDS.v_standalone_banks, ALL_CMD_TYPES.sub);
	var verifyStop = new Command (paoId, ALL_SUB_CMDS.v_disable_verify, ALL_CMD_TYPES.sub);
	
	var isV = getState("SubState_" + paoId, "isVerify");
	var str = "";
	str += "<html>";
	str+='	<body style="background-color:black">';
	str+='			<table >';
	str+='				<tr>';
	str+='					<td>';
	str+='						<input type ="submit"   style="margin: 2px 2px 2px 2px" name="';
	str+=						resetOpcount.createName();
	str+='" value = "Reset Opcount" onclick="disableAll(); submitWithConfirm(this); reset(this);"\/>';
	str+='						<input type ="submit"    style="margin: 2px 2px 2px 2px" name="';
	str+= 						confirmSub.createName();
	str+='" value = "Confirm All" onclick="disableAll(); submitWithConfirm(this); reset(this.name);"\/><br\/>';
	str+='<font color="gray">On-Demand Verify: </font>'
	str+='						<select id="subSelect" style="background-color=gray; margin: 2px 2px 2px 2px" >';
	if (isV != "true") {
		str+='							<option  value="" style="color: white"> <\/option>';
		str+='							<option  value="';
		str+=							verifyAll.createName();
		str+='" style="color: white"> Verify All<\/option>';
		str+='							<option  value="';
		str+=							verifyFQ.createName();
		str+='" style="color: white"> Verify Failed and Questionable<\/option>';
		str+='							<option  value="';
		str+=							verifyFailed.createName();
		str+='" style="color: white"> Verify Failed<\/option>';
		str+='							<option  value="';
		str+=							verifyQuestion.createName();
		str+='" style="color: white"> Verify Questionable<\/option>';
		str+='							<option  value="';
		str+=							verifyStandalone.createName();
		str+='" style="color: white"> Verify Standalone<\/option>';

	}
	else {
		str+='							<option  value="" style="color: white"> <\/option>';
		str+='							<option  value="';
		str+=							verifyStop.createName();
		str+='" style="color: white"> Stop Verify <\/option>';
	}
	str+='						<\/select>';
	str+='					<\/td>';
	str+='					<td align ="right" valign="top">';
	str+='						<a  href="#" onclick="closePopupWindow()" style="color:gray" title="Click here to close"> x <\/a><br\/>';
	str+='					<\/td>';
	str+='				<\/tr>';
	str+='				<tr>';
	str+='					<td>';
	str+='					<input type="submit"  name="execute" value = "Execute" onclick="disableAll(); submitWithConfirm(subSelect); reset(subSelect);"\/>';
	str+='					<input type="submit" name="cancel" value = "Cancel" onclick="disableAll(); reset(subSelect);"\/><\/br>';
	str+='					<\/td>';
	str+='				<\/tr>';
	str+='			<\/table>';
	str+='	<\/body>';
	str += "</html>";
	return str;
}
function toggleState(els, state) {
	for (i = 0; i < els.length; i++) {
		els[i].disabled = state;
	}
}
function disableAll() {
	buttons = document.getElementsByTagName("input");
	options = document.getElementsByTagName("select");
	toggleState(buttons, true);
	toggleState(options, true);
}
function submit(obj) {
	if (obj.tagName == "SELECT") {
		option = obj.options[obj.selectedIndex];
		executeCommand(option.value);
		
	}
	else {
		executeCommand(obj.name);
	}

}





function reset(select, dontCloseCurrentPopup) {
	buttons = document.getElementsByTagName("input");
	options = document.getElementsByTagName("select");
	toggleState(buttons, false);
	toggleState(options, false);
	if (select.tagName == "SELECT") {
		select.options[0].selected = true;
	} else {
		if ((select.id == "resetOpcount") && (select.tagName == "INPUT")) {
			select.checked = false;
		}
	}
	closeCurrentPopup = !dontCloseCurrentPopup;
	if (closeCurrentPopup) {
		closeCurrentPopupWindow();
	}
}
function submitWithConfirm(obj) {
	var cmdStr; 
	if (obj.tagName == "SELECT") 
		cmdStr = getCommandVerbal(obj.options[obj.selectedIndex].value);
	else
		cmdStr = getCommandVerbal (obj.name);
	
	if (confirm("Are you sure you want to execute " + cmdStr + "?"))
	{
		response = prompt("Reason:", "");
		var paoID = obj.name.split("_")[1];
		var tagDesc = obj.name.split("_")[0];
		executeReasonUpdate(paoID, tagDesc, response); 
		submit(obj);
	}
	
	
}
function closePopupWindow() {
	closeCurrentPopupWindow();
}
//debug
function say(word) {
	if (word != null) {
		alert(word);
	} else {
		alert("hello!");
	}
}
function createCapbankMaint(paoID) {
var scan = new Command (paoID, ALL_CAP_CMDS.scan_2way_dev, ALL_CMD_TYPES.cap);
var ovUVEn = new Command (paoID, ALL_CAP_CMDS.bank_enable_ovuv, ALL_CMD_TYPES.cap);

var str='';
str+='<html>';
str+='	<body style="background-color: black">';
str+='	<table>';
str+='		<tr>';
str+='			<td>';
str+='				<input type="submit"  name="';
str+=				scan.createName();
str+='" value="Scan" onclick="disableAll(); submit(this); reset(this)"\/>';
str+='			<\/td>';
str+='			<td align="right" valign="top">';
str+='				<a  href="javascript:void(0)" style="color=white;" onclick="closeCurrentPopupWindow()"title="Click To Close" > x <\/a>';
str+='			<\/td>';
str+='		<\/tr>';
str+='		<tr>';
str+='			<td>';
str+='				<input type="submit"  name="';
str+=			ovUVEn.createName();		
str+='" value="Enable OV\/UV" onclick="disableAll(); submit(this); reset(this)"\/>';
str+='			<\/td>';
str+='		<\/tr>';
str+='	<\/table>';
str+='	<\/body>';
str+='<\/html>';	

return str;
}
function closeCurrentPopupWindow() {
	if (window.parent.currentPopup) {
		window.parent.currentPopup.hidePopup();
	}
}
function createCapbankDBChange(paoID) {
	var resetOpcount = new Command (paoID, ALL_CAP_CMDS.reset_op_cnt, ALL_CMD_TYPES.cap);
	var commands = getAllManualStatesForCap(paoID);
	var str = "";
	str += "	<input id=\"resetOpcount\" name=\"";
	str += 		resetOpcount.createName();
	str +="\"  type=\"checkbox\" onclick=\"disableAll(); submit(this);";
	str += "	reset(this, true)\"> <font color=\"white\">Reset Opcount</font></input>";
	str += "	<a  href=\"javascript:void(0)\" style=\"color=white;\" onclick=\"closeCurrentPopupWindow(); reset(this)\"title=\"Click To Close\" > x </a><br/>";
	str += "	<select name=\"manCommSelect\" style=\"background-color=gray\" onchange=\"disableAll(); submit(this); reset(this);\">';";
	str += "<option value=\"\" style=\"\color: white\" > </option>;";
	for (var i = 0; i < commands.length; i++)
	{
		str += "		<option value=\"";
		str += commands[i].createName();
		str += "\" style=\"color: white\">";
		str += commands[i].name;
		str +="  </option>;";
	}
	str += "	</select>';";
	return str;
}
