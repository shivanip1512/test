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
	childCapDBChange:"CapDBChange",
	cbcPointTimestamp: "CapPtTmstmp",
    
    legend: "legend"
};

var clkCountQueue = $H();

function openPopupWin(elem, compositeIdType) {
	currentPopup = new PopupWindow("controlrequest");
	currentPopup.offsetX = x;
	currentPopup.offsetY = y;
    
	currentPopup.autoHide();
	
	type = compositeIdType.split("_")[0];
	id = compositeIdType.split("_")[1];
	//will only be present with cap banks
	disScan = compositeIdType.split("_")[2];
	
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
					url = createCapbankMaint(id, disScan);
	}
	else if (type == ALL_POPUP_TYPES.childCapDBChange) {
						url = createCapbankDBChange(id);
	}
	else if (type == ALL_POPUP_TYPES.cbcPointTimestamp) {
						showPointTimestamps(id);
						return;
	}
    else if (type == ALL_POPUP_TYPES.legend) {
        var legendUrl = '/spring/capcontrol/oneline/legend';
        new Ajax.Request(legendUrl, {
            method: 'get',
            onSuccess: function(transport) {
                currentPopup.offsetX = window.innerWidth/2;
                currentPopup.offsetY = 0;
                
                var html = transport.responseText;
                showPopup(html);
            }
        });
        return;
    }

	showPopup(url);

}

function showPopup(html) {
	currentPopup.populate(html);
	//over-ride this function since we
	//need to adjust table headers
	currentPopup.PopupWindow_showPopup = function () {
			PopupWindow_showPopup("popupanchor");
	}
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
str+='								<a   href="javascript:void(0)" style="color=white" title="Click To Close" onclick="closePopupWindow();"> x <\/a>';
str+='							<\/td>';
str+='						<\/tr>';
for (var key in REF_LABELS)
{
	label = REF_LABELS[key];
	data  = testData[key];
	str+='<tr >';
	str+='			<td align="left" style="color:white">';
	str+='				' + label;
	str+='			<\/td>';
	str+='			<td align="right" style="color:white"> ' + data +' <\/td>';
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
	var disableOVUVReason = "";
	var disableReason = "";
	if (isDis == "true")
	{

		disableFeeder = new Command (feederID, ALL_FDR_CMDS.enable_fdr, ALL_CMD_TYPES.feeder);
		disFeederTag = new Command (feederID, "feederEnabled", ALL_CMD_TYPES.tag);
		disableReason = getState("FeederState_" + feederID, "disableFdrReason");

	}
	else
	{
		disableFeeder = new Command (feederID, ALL_FDR_CMDS.disable_fdr, ALL_CMD_TYPES.feeder);
		disFeederTag = new Command (feederID, "feederDisabled", ALL_CMD_TYPES.tag);
		
	}
	//////////////////////////
	var isOVUVDis = getState("FeederState_" + feederID, "isOVUVDis");
	var disFeederOVUV;
	var disFeederOVUVTag;
	if (isOVUVDis == "true")
	{
		disFeederOVUV = new Command (feederID, ALL_FDR_CMDS.send_all_enable_ovuv, ALL_CMD_TYPES.feeder);
		disFeederOVUVTag = new Command (feederID, ALL_TAG_CMDS.feederOVUVEnabled, ALL_CMD_TYPES.tag);
		disableOVUVReason = getState("FeederState_" + feederID, "disableOVUVFdrReason");
	}
	else 
	{
		disFeederOVUV = new Command (feederID, ALL_FDR_CMDS.send_all_disable_ovuv, ALL_CMD_TYPES.feeder);
		disFeederOVUVTag = new Command (feederID, ALL_TAG_CMDS.feederOVUVDisabled, ALL_CMD_TYPES.tag);	
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
	str+= ' 		        onclick="addCommand(this); addCommand(\'' + disFeederTag.createName() +'\'); setReason(\'' + disFeederTag.createName() + '\', \'' + disableReason + '\', this);"';
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
	str+='				<input type="checkbox"  name = "'; 
	str+= 				disFeederOVUV.createName() + '" ';
	str+=' 				onclick="addCommand(this); '; 
	str+= ' 				 	 addCommand(\'' + disFeederOVUVTag.createName() +'\'); setReason(\'' + disFeederOVUVTag.createName() + '\', \'' + disableReason + '\', this);"';
	if (isOVUVDis == "true")
		str+= ' checked ';
	str+='>'; 	
	str+='				<font color="white">Disable OVUV<\/font><\/><\/br>';
	str+='			<\/td>';
	str+='		<\/tr>';
	str+='		<tr>';
	str+= 			'<td>';
	str += 				generateReasonSpan ((isOVUVDis == "true"), disFeederOVUVTag.createName() + 'ReasonSpan', disableOVUVReason);
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
	//////////////////////////
	var isOVUVDis = getState("SubState_" + paoId, "isOVUVDis");
	var disSubOVUV;
	var disSubOVUVTag;
	if (isOVUVDis == "true")
	{
		disSubOVUV = new Command (paoId, ALL_SUB_CMDS.send_all_enable_ovuv, ALL_CMD_TYPES.sub);
		disSubOVUVTag = new Command (paoId, ALL_TAG_CMDS.subOVUVEnabled, ALL_CMD_TYPES.tag);
	}
	else 
	{
		disSubOVUV = new Command (paoId, ALL_SUB_CMDS.send_all_disable_ovuv, ALL_CMD_TYPES.sub);
		disSubOVUVTag = new Command (paoId, ALL_TAG_CMDS.subOVUVDisabled, ALL_CMD_TYPES.tag);	
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
	str+='				<input type="checkbox"  name = "'; 
	str+= 				disSubOVUV.createName() + '" ';
	str+=' 				onclick="addCommand(this); '; 
	str+= ' 				 	 addCommand(\'' + disSubOVUVTag.createName() +'\'); setReason(\'' + disSubOVUVTag.createName() + '\', \'' + disableReason + '\', this);"';
	if (isOVUVDis == "true")
		str+= ' checked ';
	str+='>'; 	
	str+='				<font color="white">Disable OVUV<\/font><\/><\/br>';
	str+='			<\/td>';
	str+='		<\/tr>';
	str+='		<tr>';
	str+= 			'<td>';
	str += 				generateReasonSpan ((isOVUVDis == "true"), disSubOVUVTag.createName() + 'ReasonSpan', disableReason);
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
	var comments = getState("CapState_" + paoID, "capbankComments");
	//variables pushed from the server
	var paoName = getState("CapState_" + paoID, "paoName"); //name of the capbank
	//capbank states
	var isDis = getState("CapState_" + paoID, "isDisable");
	var isOVUVDis = getState("CapState_" + paoID, "isOVUVDis");
	var isStandalone = getState("CapState_" + paoID, "isStandalone");
	//reasons
	var disableCapReason = getState("CapState_" + paoID, "disableCapReason");
	var disableCapOVUVReason = getState("CapState_" + paoID, "disableCapOVUVReason");
	var aloneReason = getState("CapState_" + paoID, "standAloneReason");
	
	//variables used to hold command information
	//we need to for enablement and ovuv because it is
	//a tag as well a command
	var disableCap;
	var disCapTag;
	var disCapOVUV;
	var disCapOVUVTag;
	
	if (isDis == "true")
	{
		disableCap = new Command (paoID, ALL_CAP_CMDS.enable_capbank, ALL_CMD_TYPES.cap);
		disCapTag = new Command (paoID, ALL_TAG_CMDS.capEnabled, ALL_CMD_TYPES.tag);
	}
	else 
	{
		disableCap = new Command (paoID, ALL_CAP_CMDS.disable_capbank, ALL_CMD_TYPES.cap);
		disCapTag = new Command (paoID, ALL_TAG_CMDS.capDisabled, ALL_CMD_TYPES.tag);	
	}
	if (isOVUVDis == "true")
	{
		disCapOVUV = new Command (paoID, ALL_CAP_CMDS.bank_enable_ovuv, ALL_CMD_TYPES.cap);
		disCapOVUVTag = new Command (paoID, ALL_TAG_CMDS.capOVUVEnabled, ALL_CMD_TYPES.tag);
	}
	else 
	{
		disCapOVUV = new Command (paoID, ALL_CAP_CMDS.bank_disable_ovuv, ALL_CMD_TYPES.cap);
		disCapOVUVTag = new Command (paoID, ALL_TAG_CMDS.capOVUVDisabled, ALL_CMD_TYPES.tag);	
	}
	
	if (isStandalone == "true")
	{
		aloneCap = new Command (paoID, ALL_TAG_CMDS.switched, ALL_CMD_TYPES.tag);
	}
	else
	{
		aloneCap = new Command (paoID, ALL_TAG_CMDS.standalone, ALL_CMD_TYPES.tag);
	}	
	var str='';
	str += '<html>';
	str+='<body style="background-color:black">';
	var str='';
	str+='	<table>';
	str += '<table border=2 style="border-color: black black white black; width=236">'
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
	//***********DIS/EN CAP***********//
	str+='					<input name="';
	str+=					disableCap.createName();
	str+='" type="checkbox" onclick=" '; 
	str+=' addCommand (this); addCommand(\'' + disCapTag.createName() +'\'); setReason(\'' + disCapTag.createName() + '\', \'' + disableCapReason + '\', this)"';
	if (isDis == "true")
	str+=					' checked ';
	str+='> <font color="white">Disable<\/font><\/><\/br>';
	str += generateReasonSpan((isDis == "true"),  disCapTag.createName() + 'ReasonSpan' , disableCapReason);
	//***********OV/UV***********//
	str+='					<input name="';
	str+=					disCapOVUV.createName();
	str+='" type="checkbox" onclick=" ';
	str+=' addCommand(this); addCommand(\'' + disCapOVUVTag.createName() +'\'); setReason(\'' + disCapOVUVTag.createName() + '\', \'' + disableCapOVUVReason + '\', this)"';
	if (isOVUVDis == "true")
	str+=					' checked ';
	str+='> <font color="white">Disable OVUV<\/font><\/><\/br>';
	str += generateReasonSpan((isOVUVDis == "true"),  disCapOVUVTag.createName() + 'ReasonSpan' , disableCapOVUVReason);
	
	//***********STANDALONE****************//
	str+='					<input   name="';
	str+=					aloneCap.createName();
	str+='" type="checkbox" onclick="addCommand(this);addCommand(\'' + aloneCap.createName() +'\'); setReason(this, \'' + aloneReason + '\')"';
	if (isStandalone == "true")
			str+=					' checked';
	str+='> <font color="white">Standalone<\/font><\/><\/br>';
		str += generateReasonSpan ((isStandalone == "true"), aloneCap.createName() + 'ReasonSpan', aloneReason);

	//***********COMMENTS****************//
	str+='<a href="/capcontrol/capbankcomments.jsp?capbankID=' + paoID + '&returnURL=' + window.location +'"  ><font color="white"><B>Comments</B><\/font></a><\/><\/br>';
	str+='<font color="white">';
	str+= generateCommentField("commentField_" + paoID, comments );	//str += generateReasonSpan ((isStandalone == "true"), aloneCap.createName() + 'ReasonSpan', aloneReason);
	str+='</font>';
	
	//************************	
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
	var disScan = getState("CapState_" + paoID, "scanOptionDis");
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
	str += 					ALL_POPUP_TYPES.childCapMaint + "_" + paoID + "_" + disScan;
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
	var resetOpcount = new Command (paoID, ALL_FDR_CMDS.reset_op_cnt, ALL_CMD_TYPES.feeder);
	var openAllFdr = new Command (paoID, ALL_FDR_CMDS.send_all_open, ALL_CMD_TYPES.feeder);
	var closeAllFdr = new Command (paoID, ALL_FDR_CMDS.send_all_close, ALL_CMD_TYPES.feeder);
	var enableOvUvFdr = new Command (paoID, ALL_FDR_CMDS.send_all_enable_ovuv, ALL_CMD_TYPES.feeder);
	var disableOvUvFdr = new Command (paoID, ALL_FDR_CMDS.send_all_disable_ovuv, ALL_CMD_TYPES.feeder);
	var sendAll2WayFdr = new Command (paoID, ALL_FDR_CMDS.send_all_2way_scan, ALL_CMD_TYPES.feeder);
	
	var str='';
	str+='<html>';
	str+='	<body>';
	str+='	<table style="background-color:black" name="commandTable">';
	str+='			<tr align="left" valign="top">';
	str+='				<td>';
	str+='				<input type="submit" name="';
	str+= 				resetOpcount.createName();
	str+='" value="Reset Opcount " onclick = "disableAll(); submitWithConfirm(this); reset(this)"\/><br\/>';
	str+='              </td>';
	str+='				<td align="right" valign="top">';
	str+='				<a href="javascript:void(0)" style="color=gray" title="Click To Close" onclick="closePopupWindow();"> x <\/a>';
	str+='				<\/td>';
	str+='			<tr align="left">';
	str+='				<td>';
	str+='<font color="gray">Fdr-Level CBC Commands: </font>'
	str+='						<select id="subSelect" style="background-color=gray; margin: 2px 2px 2px 2px" >';
	
	str+='							<option  value="" style="color: white"> <\/option>';
	str+='							<option  value="';
	str+=							openAllFdr.createName();
	str+='" style="color: white"> Open All CapBanks<\/option>';
	str+='							<option  value="';
	str+=							closeAllFdr.createName();
	str+='" style="color: white"> Close All CapBanks<\/option>';
	str+='							<option  value="';
	str+=							enableOvUvFdr.createName();
	str+='" style="color: white"> Enable OvUv<\/option>';
	str+='							<option  value="';
	str+=							disableOvUvFdr.createName();
	str+='" style="color: white"> Disable OvUv<\/option>';
	str+='							<option  value="';
	str+=							sendAll2WayFdr.createName();
	str+='" style="color: white"> Scan All 2way CapBanks<\/option>';
	str+='						<\/select>';
	str+='				<\/td>';
	str+='			<\/tr>';
	str+='			<tr>';
	str+='				<td>';
	str+='				<input type="submit"  name="execute" value = "Execute" onclick="disableAll(); submitWithConfirm(subSelect); reset(subSelect);"\/>';
	str+='				<input type="submit" name="cancel" value = "Cancel" onclick="disableAll(); reset(subSelect);"\/><\/br>';
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
	var openAllSub = new Command (paoId, ALL_SUB_CMDS.send_all_open, ALL_CMD_TYPES.sub);
	var closeAllSub = new Command (paoId, ALL_SUB_CMDS.send_all_close, ALL_CMD_TYPES.sub);
	var enableOvUvSub = new Command (paoId, ALL_SUB_CMDS.send_all_enable_ovuv, ALL_CMD_TYPES.sub);
	var disableOvUvSub = new Command (paoId, ALL_SUB_CMDS.send_all_disable_ovuv, ALL_CMD_TYPES.sub);
	var sendAll2WaySub = new Command (paoId, ALL_SUB_CMDS.send_all_2way_scan, ALL_CMD_TYPES.sub);

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
	str+='					<\/td>';
	str+='					<td align ="right" valign="top">';
	str+='						<a  href="#" onclick="closePopupWindow()" style="color:gray" title="Click here to close"> x <\/a><br\/>';
	str+='					<\/td>';
	str+='				<\/tr>';    
    str+='				<tr>';
	str+='					<td>';
	str+='<font color="gray">Sub-Level CBC Commands: </font>'
	str+='						<select id="subSelect" style="background-color=gray; margin: 2px 2px 2px 2px" >';
	
	str+='							<option  value="" style="color: white"> <\/option>';
	str+='							<option  value="';
	str+=							openAllSub.createName();
	str+='" style="color: white"> Open All CapBanks<\/option>';
	str+='							<option  value="';
	str+=							closeAllSub.createName();
	str+='" style="color: white"> Close All CapBanks<\/option>';
	str+='							<option  value="';
	str+=							enableOvUvSub.createName();
	str+='" style="color: white"> Enable OvUv<\/option>';
	str+='							<option  value="';
	str+=							disableOvUvSub.createName();
	str+='" style="color: white"> Disable OvUv<\/option>';
	str+='							<option  value="';
	str+=							sendAll2WaySub.createName();
	str+='" style="color: white"> Scan All 2way CapBanks<\/option>';

	if (isV != "true") {
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
		str+='							<option  value="';
		str+=							verifyStop.createName();
		str+='" style="color: white"> Stop Verify <\/option>';
	}
	str+='						<\/select>';
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
	clkCountQueue = $H();
}
function submitWithConfirm(obj) {
	var cmdStr; 
	var name;
	if (obj.tagName == "SELECT"){
		cmdStr = getCommandVerbal(obj.options[obj.selectedIndex].value);
		name = obj.options[obj.selectedIndex].value;
	}
	else{
		cmdStr = getCommandVerbal (obj.name);
		name = obj.name;
	}
	if (confirm("Are you sure you want to execute " + cmdStr + "?"))
	{
		//response = prompt("Reason:", "");
		
		var paoID = name.split("_")[1];
		var tagDesc = name.split("_")[0];
		executeReasonUpdate(paoID, tagDesc, ""); 
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
function createCapbankMaint(paoID, disScan) {
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
str+='" value="Scan" '
if (disScan == "true")
str+=				' disabled = "true" ' ;
str+='onclick="disableAll(); submit(this); reset(this)"\/>';
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


function bind (str)
{
	var html = str;
	return html;
}

function showPointTimestamps (cbcID) {
	
	new Ajax.Request ('/spring/capcontrol/pointdata', 
					{method:'post', 
					parameters: 'cbcID=' + cbcID, 
					onSuccess: function (t) { 
					showPopup(t.responseText);
					},
					onFailure: function () { alert ("Could not reach server!")}, 
					asynchronous:true });
	
}

//function borrowed from cbc_funcs.js to align oneline popup
function alignHeaders(mainTable, headerTable) {
mytable = window.parent.document.getElementById(mainTable);
hdrTable =  window.parent.document.getElementById(headerTable);
	if (hdrTable)
	{
	hdrRow=hdrTable.getElementsByTagName('tr').item(0);
	
		for (j=0; j < mytable.getElementsByTagName('tr').length; j ++ ) {
		    var myrow = mytable.getElementsByTagName('tr').item(j);  
		     if ((myrow != null) && myrow.style.display != 'none' && hdrRow.style.display != 'none') {
		        var colNum = myrow.cells.length;
		        
		        for(i=0;i < colNum - 1; i++) {
		            var hdrCell = hdrRow.getElementsByTagName('td').item(i);
		            var myrowCell = myrow.cells[i];
		            if ((hdrCell.style.display != 'none') && (myrowCell.style.display != 'none')) {
		                maxWidth = Math.max(hdrCell.offsetWidth, myrowCell.offsetWidth);
		                hdrCell.width = maxWidth;
		                myrowCell.width = maxWidth;
		                break;
		                }
		                                                                       
		            }
		    
		        }
		    }
	}
}
//over-ridden function from PopupWindow.js
function PopupWindow_showPopup (anchorname) {
	this.anchorname = anchorname;
	this.getXYPosition(anchorname);
	this.x += this.offsetX;
	this.y += this.offsetY;
	if (!this.populated && (this.contents != "")) {
		this.populated = true;
		this.refresh();
		
		}
	if (this.divName != null) {
		// Show the DIV object
		if (this.use_gebi) {
			window.parent.document.getElementById(this.divName).style.left = this.x + "px";
			window.parent.document.getElementById(this.divName).style.top = this.y + "px";
			window.parent.document.getElementById(this.divName).style.visibility = "visible";
			}
		else if (this.use_css) {
			document.all[this.divName].style.left = this.x;
			document.all[this.divName].style.top = this.y;
			document.all[this.divName].style.visibility = "visible";
			
			
			}
		else if (this.use_layers) {
			document.layers[this.divName].left = this.x;
			document.layers[this.divName].top = this.y;
			document.layers[this.divName].visibility = "visible";
			}
		}
	else {
		if (this.popupWindow == null || this.popupWindow.closed) {
			// If the popup window will go off-screen, move it so it doesn't
			if (this.x<0) { this.x=0; }
			if (this.y<0) { this.y=0; }
			if (screen && screen.availHeight) {
				if ((this.y + this.height) > screen.availHeight) {
					this.y = screen.availHeight - this.height;
					}
				}
			if (screen && screen.availWidth) {
				if ((this.x + this.width) > screen.availWidth) {
					this.x = screen.availWidth - this.width;
					}
				}
			var avoidAboutBlank = window.opera || ( document.layers && !navigator.mimeTypes['*'] ) || navigator.vendor == 'KDE' || ( document.childNodes && !document.all && !navigator.taintEnabled );
			this.popupWindow = window.open(avoidAboutBlank?"":"about:blank","window_"+anchorname,this.windowProperties+",width="+this.width+",height="+this.height+",screenX="+this.x+",left="+this.x+",screenY="+this.y+",top="+this.y+"");
			}
		this.refresh();
		}
		alignHeaders("dataTable", "headerTable");
		
	
}

