
var UPDATE_EVERY = 5000;
var SUB_IMAGES = ["sub_TransformerImg"];

//onload
function initCC() {
	updateDrawing();
}

var REF_LABELS = {
				 	maintArea: "Maintenance Area ID:",
				 	poleNum:"Pole Number:",
				 	dir:"Driving Directions:",
				 	latit:"Latitude:",
				 	longit:"Longitude:",
					config:"Cap Bank Config:",
					medium:"Comm. Medium",
					strength:"Comm.Strength",
					isAntenna:"External Antenna?",
					antType:"Antenna Type:",
					maintVis:"Last Maintenance Visit:",
					inspVis:"Last Inspection Visit:",
					opRstDt:"Op Count Reset Date:",
					potentTrfmr:"Potential Transformer:",
					maintReqPend:"Maintenance Request Pending:",
					otrCmnts:"Other Comments:",
					opTmCmnts:"Op Team Comments:",
					cbcInsDt:"CBC Install Date:"		
				};



function batchExecute (cmdStr) {
	var tags = new Array();
	var cmds = new Array();
	all = cmdStr.toString().split(",");
	question = "The following will be executed: ";
	//split the command into arrays by command type:
	//commands and tags
	//then execute commands first and tags second
	//NOTE: if command failes tag will still be executed and might change
		for(var i=0; i < all.length; i++) {
			var str = all[i];
			if (str)
			{
				if (str.split("_")[0] == "tag")
				{
					if (!isQueued (str, tags))
					{
						tags.push (str);
						question += "tagged as " + getCommandVerbal (str);
						question += ".";
					}
				}
				else
				{
					if (!(isQueued (str, cmds)))
					{
						cmds.push (str);
						question += getCommandVerbal (str);
						question += ",";
					}
				}	
			
			}
			
		}
		

	question += ". Is this OK?";
	
	if (confirm(question))
	{
			for (var i=0; i<cmds.length; i++) {
				executeCommand (cmds[i]);
			}	
			
			for (var i=0; i<tags.length; i++) {
				executeCommand (tags[i]);
			}	
					
	}

}

function isQueued (str, queue) {
	for (var i=0; i < queue.length; i++)
	{
		if (str == queue[i])
			return true;	
	}
	
	return false;
} 

function executeReasonUpdate(paoID, tagDesc, reason) {
	new Ajax.Request("/capcontrol/oneline/CBCReasonUpdaterServlet", 
		{
			method:"post", 
			parameters:"id=" + paoID + "&tagDesc=" + tagDesc + "&reason=" + reason, 
			asynchronous:true,
			onSuccess: function () { display_status(cmd_name, "Command sent successfully", "green")},
			onFailure: function () { display_status(cmd_name, "Command submission failed", "red"); }
			});
}
function executeSubCommand(paoId, command, cmd_name) {
	new Ajax.Request("/servlet/CBCServlet", 
		{
			method:"post", 
			parameters:"cmdID=" + command + "&paoID=" + paoId + "&controlType=SUB_TYPE", 
			asynchronous:true,
			onSuccess: function () { display_status(cmd_name, "Command sent successfully", "green")},
			onFailure: function () { display_status(cmd_name, "Command submission failed", "red"); }
		});
}
function executeFeederCommand(paoId, command, cmd_name) {
	new Ajax.Request("/servlet/CBCServlet", 
		{
			method:"post", 
			parameters:"cmdID=" + command + "&paoID=" + paoId + "&controlType=FEEDER_TYPE", 
			asynchronous:true,
			onSuccess: function () { display_status(cmd_name, "Command sent successfully", "green")},
			onFailure: function () { display_status(cmd_name, "Command submission failed", "red"); }
			});
}
function executeCapBankCommand(paoId, command, cmd_name, is_manual_state) {
	var RESET_OP_CNT = 12;
	if (is_manual_state) {
		new Ajax.Request("/servlet/CBCServlet", 
		{
			method:"post", 
			parameters:"opt=" + command + "&cmdID=" + 30 + "&paoID=" + paoId + "&controlType=CAPBANK_TYPE", asynchronous:true,
			onSuccess: function () { display_status(cmd_name, "Command sent successfully", "green")},
			onFailure: function () { display_status(cmd_name, "Command submission failed", "red"); }
		});
	} else {
		//special case for reset_op_counts command
		if (command == RESET_OP_CNT) {
			handleOpcountRequest(command, paoId, "", cmd_name);
		} else {
			new Ajax.Request("/servlet/CBCServlet", 
			{
			method:"post", 
			parameters:"cmdID=" + command + "&paoID=" + paoId + "&controlType=CAPBANK_TYPE", 
			asynchronous:true,
			onSuccess: function () { display_status(cmd_name, "Command sent successfully", "green")},
			onFailure: function () { display_status(cmd_name, "Command submission failed", "red"); }
			});
		}
	}
}
function handleOpcountRequest(command, paoId, cmd_name, newOpcntVal) {
//make sure that contains a valid number
	var opcount;
	if (!isValidOpcount(newOpcntVal)) {
		alert("Op Count value not specified. New Op Count value will be set to 0.");
		opcount = 0;
	} else {
		opcount = parseInt(newOpcntVal);
	}
	new Ajax.Request("/servlet/CBCServlet", 
		{	method:"post", 
			parameters:"cmdID=" + command + "&paoID=" + paoId + "&controlType=CAPBANK_TYPE&opt=" + opcount, 
			asynchronous:true,
			onSuccess: function () { display_status(cmd_name, "Command sent successfully", "green")},
			onFailure: function () { display_status(cmd_name, "Command submission failed", "red"); }
			});
}
function updateDrawing() {
	id = getSubId();
		new Ajax.Request("/capcontrol/oneline/OnelineCBCServlet", {method:"post", parameters:"id=" + id, onSuccess:function (t) {
			updateHTML(t.responseXML);
			callback();
		}, onFailure:function () {
			say("Remote call failed");
		}, asynchronous:true});
}
function callback() {
	setTimeout("updateDrawing()", UPDATE_EVERY);
}
function updateHTML(xml) {
	updateSub(xml);
//	say("after sub");
	updateFeeders(xml);
//	say("after feeder");
	updateCaps(xml);
//	say("after caps");

}
function updateSub(xml) {
	updateImages(SUB_IMAGES, xml);
	updateHiddenTextElements("SubState", xml, ["isDisable", "isVerify", "subDisableReason"]);
	updateDynamicLineElements("Sub", xml);
	updateVisibleText("SubStat", xml);
	updateVisibleText("SubTag", xml);

}
function updateFeeders(xml) {
	updateHiddenTextElements("FeederState", xml, ["isDisable", "disableFdrReason"]);
	updateDynamicLineElements("OnelineFeeder", xml);
	updateVisibleText("FeederStat", xml);
	updateVisibleText("FeederTag", xml);

}

function updateCaps(xml) {
	var images = document.getElementsByTagName("image");
	for (var i = 0; i < images.length; i++) {
		image = images.item(i);
		idAttr = image.getAttribute("id");
		if (idAttr.split("_")[0] == "CapBank") {
			update_Image(idAttr, xml);
		}
	}
	var capBankInfo = new Array();
	for(var key in REF_LABELS) {
		capBankInfo.push (key);
	}
	updateHiddenTextElements("CapHiddenInfo", xml, capBankInfo);
	updateHiddenTextElements("CapState", xml, ["isDisable", "isOVUVDis", "isStandalone", "standAloneReason", "disableCapReason", "paoName"]);
	updateVisibleText("CapStat", xml);
	updateVisibleText("CapTag", xml);

}

function updateVisibleText(prefix, xml) {
	var textEls = document.getElementsByTagName("text");
	for (var i = 0; i < textEls.length; i++) {
		textEl = textEls.item(i);
		var id = textEl.getAttribute("id");
		if (textEl.getAttribute("id").split("_")[0] == prefix) {
			xmlDynamicEls = xml.getElementsByTagName("text");
			for (var j = 0; j < xmlDynamicEls.length; j++) {
				xmlText = xmlDynamicEls.item(j);
				xmlID = xmlText.getAttribute("id");
				if ((xmlID == id) && (i == j)) {
					textEl.getFirstChild().setData(xmlText.text);
					color = xmlText.getAttribute("style");
					textEl.setAttribute("style", color);
					
				}
			}
		}
	}
}

function getSubId() {
	var textEls = document.getElementsByTagName("text");
	var subID = -1;
	for (var i = 0; i < textEls.length; i++) {
		txtNode = textEls.item(i);
		id = txtNode.getAttribute("id");
		if ((id.split("_")[0] == "SubTag") && txtNode.getAttribute("elementID") == "staticText") {
			subID = id.split("_")[1];
		}
	}
	if (subID == -1) {
		say("Could not find SUBID!!!");
	}
	return subID;
}

//update utils
function updateHiddenTextElements(grpName, xml, attrs) {
	var textEls = document.getElementsByTagName("text");
	for (var i = 0; i < textEls.length; i++) {
		txtNode = textEls.item(i);
		id = txtNode.getAttribute("id");
		if ((id.split("_")[0] == grpName) && txtNode.getAttribute("elementID") == "HiddenTextElement") {
			xmlDynamicEls = xml.getElementsByTagName("text");
			for (var j = 0; j < xmlDynamicEls.length; j++) {
				xmlText = xmlDynamicEls.item(j);
				xmlID = xmlText.getAttribute("id");
				if ((xmlID == id) && xmlText.getAttribute("elementID") == "HiddenTextElement" && i == j) {
					if (attrs == null) {
						txtNode.getFirstChild().setData(xmlText.text);
						aElement = txtNode.getParentNode();
						xmlAElement = xmlText.parentNode;
						link = xmlAElement.getAttribute("xlink:href");
						aElement.setAttribute("xlink:href", link);
						style = xmlText.getAttribute("style");
						txtNode.setAttribute("style", style);
					} else {
						for (k = 0; k < attrs.length; k++) {
							val = xmlText.getAttribute(attrs[k]);
							txtNode.setAttribute(attrs[k], val);
						}
					}
				}
			}
		}
	}
}
function updateImages(images, xml) {
	for (i = 0; i < images.length; i++) {
		update_Image(images[i], xml);
	}
}
function update_Image(imageName, xml) {
	var images = document.getElementsByTagName("image");
	var xlink = "xlink:href";
	for (var i = 0; i < images.length; i++) {
		image = images.item(i);
		idAttr = image.getAttribute("id");
		if (idAttr == imageName) {
			xmlImages = xml.getElementsByTagName("image");
			for (j = 0; j < xmlImages.length; j++) {
				xmlImage = xmlImages.item(j);
				xmlImgId = xmlImage.getAttribute("id");
				if (xmlImgId == imageName) {
					currImageName = xmlImage.getAttribute(xlink);
					image.setAttribute(xlink, currImageName);
				}
			}
		}
	}
	return;
}
function updateDynamicLineElements(lnType, xml) {
	var textEls = document.getElementsByTagName("path");
	for (var i = 0; i < textEls.length; i++) {
		txtNode = textEls.item(i);
		if ((txtNode.getAttribute("id").split("_")[0] == lnType) && txtNode.getAttribute("elementID") == "lineElement") {
			xmlDynamicEls = xml.getElementsByTagName("path");
			for (var j = 0; j < xmlDynamicEls.length; j++) {
				xmlText = xmlDynamicEls.item(j);
				if ((xmlText.getAttribute("id") == txtNode.getAttribute("id")) && xmlText.getAttribute("elementID") == "lineElement") {
					color = xmlText.getAttribute("style");
					txtNode.setAttribute("style", color);
				}
			}
		}
	}
}
function getState(name, s) {
	var textEls = document.getElementsByTagName("text");
	for (var i = 0; i < textEls.length; i++) {
		txtNode = textEls.item(i);
		id = txtNode.getAttribute("id");
		if ((id == name) && txtNode.getAttribute("elementID") == "HiddenTextElement") {
			return txtNode.getAttribute(s);
		}
	}
}

function getHiddenAttributes (name){
	var textEls = document.getElementsByTagName("text");
	for (var i = 0; i < textEls.length; i++) {
		txtNode = textEls.item(i);
		id = txtNode.getAttribute("id");
		if ((id == name) && txtNode.getAttribute("elementID") == "HiddenTextElement") {
			return txtNode.getAttributes();
		}
	}

}
function getAllManualStatesForCap(paoID) {
	allstates = window.parent.document.getElementById("capmanualstates").value.split(",");
	var commands = new Array();
	for (var i = 0; i < allstates.length; i++) {
		name = allstates[i].split(":")[0];
		id = allstates[i].split(":")[1];
		commands.push(new Command(paoID, id, ALL_CMD_TYPES.cap, true, name));
	}
	return commands;
}
function isValidOpcount(number) {
	var ret = parseInt(number);
	if (Number.NaN != ret) {
		if ((ret >= 0) && (ret < 100000)) {
			return true;
		}
	}
	return false;
}
//function that will toggle the reason span based on cb state
function toggleCB(spanID, cb, reason) {
	span = document.getElementById(spanID);
	if (cb.type == "checkbox" && cb.checked) {
		str = "";
		str = "<font style='color:gray'>Reason: <input type='text'";
		if (reason) {
			str += " value='" + reason + "'>";
		}
		str += "</input></font>";
		span.innerHTML = str;
		span.style.display = "inline";
	} else {
		span.style.display = "none";
	}
}
function setReason(obj, reason, prntCB) {
	var name = "";
	var cb;
	if (obj.name && obj.type == "checkbox") {
		name = obj.name;
		cb = obj;
	} else {
		if (obj && (prntCB.name && prntCB.type == "checkbox")) {
			name = obj;
			cb = prntCB;
		}
	}
	spanID = name + "ReasonSpan";
	toggleCB(spanID, cb, reason);
}
function generateReasonText(reason) {
	var str = "";
	str += "\" style=\"display: inline\" >";
	str += "<font style=\"color:gray\">Reason:";
	str += "<input type=\"text\"  value=\"" + reason + "\" disabled/>";
	str += "</font>";
	return str;
}
function generateReasonSpan(visible, spanName, text) {
	str = "";
	str += "\t\t\t\t\t<span id=\"";
	str += spanName;
	if (visible) {
		str += generateReasonText(text);
	} else {
		str += "\" style=\"display: none\">";
	}
	str += "</span></br>";
	return str;
}



//disables all the checked checkboxes in span
function disableAllCheckedReasons(spanPrefix, id) {
	var span = document.getElementById(spanPrefix + id);
	all = span.getElementsByTagName("span");
	for (var i = 0; i < all.length; i++) {
		el = all[i];
		if (el.style.display != "none") {
			el.getElementsByTagName("input")[0].disabled = true;
		}
	}
}
//object is a checkbox with a name that has it's id in it
//ex: tag_paoID_cmdID
function addCommand(obj) {
	if (obj.name) 
	{ //this is a checkbox
		var paoID = paoID = obj.name.split("_")[1];
		var executeQueue = document.getElementById("executeQueue_" + paoID);

	
			executeQueue.value += obj.name;
			executeQueue.value += ":";
	} else {
		if (obj) {
			addStringCommand(obj);
		}
		
	}
}


function addStringCommand(obj) {
	var paoID = paoID = obj.split("_")[1];
	var executeQueue = document.getElementById("executeQueue_" + paoID);
	executeQueue.value += obj;
	executeQueue.value += ":";
}
function findTagSpanForCB(name, allIds) {
	for (var i = 0; i < allIds.length; i++) {
		if ((allIds[i].split("_")[1] == name.split("_")[1]) && (allIds[i].split("_")[0] == ALL_CMD_TYPES.tag)) {
			return allIds[i] + "ReasonSpan";
		}
	}
	return name + "ReasonSpan";
}

//All commands are piled into an executeQueue that exists for every element
//all execute queues are located in a span to separate executeQueue for feeder and executeQueue for capbank
function executeMultipleCommands(spanPrefix, id) {
	//get the span
	var span = document.getElementById(spanPrefix + id);
	//get the executeQueue
	var executeQueue = document.getElementById("executeQueue_" + id);
	allIds = executeQueue.value.split(":");
	executeQueue.value = "";
	allTags = span.getElementsByTagName("input");
	var checkedCount = 0;
	//every checkbox has a reasonSpan attached to it. ReasonSpan contains the text area for the user input reason.
	//we need to find the reasonSpan based on the name of the checkbox (in the executeQueue)
	//get the text value in the span and construct a new command that will contain the reason so that
	//it will get passed to the servlet.
	//ex: tag_1622_capDisable_Testing%20Cap%20Bank
	//tag - command type
	//1622 - paoID
	//capDisable - tag attach
	//Testing%20Cap%20Bank - reason for attaching this tag
	for (var i = 0; i < allTags.length; i++) {
		if (allTags[i].type == "checkbox" && allTags[i].checked) {
			spanID = findTagSpanForCB(allTags[i].name, allIds);
			reasonSpan = document.getElementById(spanID);
			if (reasonSpan) {
				for (var j = 0; j < allIds.length; j++) {
					if ((allIds[j] + "ReasonSpan") == reasonSpan.id) {
						allIds[j] = allIds[j] + "_" + reasonSpan.getElementsByTagName("input")[0].value;
					}
				}
			}
		}
	}
	batchExecute(allIds);
				
}


function reflect (obj, value) {
	for (var key in obj)
	{
		if (obj[key] == value) {
			return key;
		}
	}
	return null;
}

function findValueByKey(key, map){
	for(var k in map){
		if (key == k){
			return map[k];
		}
	}
	return null;
}

function display_status(cmd_name, msg, color) {
	var msg_div = document.getElementById('cmdMessageDiv');
	msg_div.innerHTML =  '<font color="white">' + msg + '</font>';
	msg_div.style.visibility = "visible";	
	msg_div.style.width = "100";	
	msg_div.style.height = "40";	
	msg_div.style.top= 20;
	msg_div.style.left = 1050;
	msg_div.style.backgroundColor = color;
	var timeout = 0;
	if (color == "red") {
		Effect.Pulsate('cmdMessageDiv', {duration: 8});
		timeout = 8000;
		}
	 else {
	 	Effect.Appear('cmdMessageDiv');
		timeout = 2000;
		}
		setTimeout ('hideMsgDiv()', timeout);	
}

function hideMsgDiv() {
	Effect.Fade('cmdMessageDiv');
}



