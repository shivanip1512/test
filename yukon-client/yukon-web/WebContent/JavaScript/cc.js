
var UPDATE_EVERY = 5000;
var SUB_IMAGES = ["sub_TransformerImg"];

//onload
function initCC() {
	updateDrawing();
}

var REF_LABELS = {
				 	maintArea: "Maintenance Area ID:",
				 	poleNum:"Pole Number:",
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
					cbcInsDt:"CBC Install Date:",
					address:"Cap Bank Map Address",
					dir:"Driving Directions:"
				};

function startsWith(string, pattern) {
    return string.indexOf(pattern) === 0;
}

function endsWith(string, pattern) {
    var d = string.length - pattern.length;
    return d >= 0 && string.lastIndexOf(pattern) === d;
}

function chop(string) {
    string = string.substring(0, string.length - 1);
    return string;
}

function batchExecute(listofCommandsArray) {
	var tags = new Array();
	var cmds = new Array();
	var execute = false;
    var question;
    
    var commandsLength = listofCommandsArray.length;
    
    if (commandsLength > 0) {
        execute = true;
        question = 'The following will be executed: ';
        
        for (var x = 0; x < commandsLength; x++) {
            var command = listofCommandsArray[x];
            var isTag = startsWith(command, 'tag');
            if (isTag) {
                if (!isQueued(command, tags)) {
                    tags.push(command);
                }    
            } else {
                if (!isQueued(command, cmds)) {
                    cmds.push(command);
                    question += getCommandVerbal(command) + ',';
                }
            }        
        }
        if (endsWith(question, ',')) question = chop(question);
        question += '. Is this OK?';
    } else {
        question = 'No Change Was Made.';
    }
    
	if (confirm(question) && execute) {
		for (var i=0; i < cmds.length; i++) {
		  executeCommand (cmds[i]);
		}	
		for (var i=0; i < tags.length; i++) {
            executeTag(tags[i]);        
		}
	}
}

function isQueued(str, queue) {
    var index = queue.indexOf(str);
    var result = index != -1;
    return result;
}

function executeTag(tag) {
    var array = tag.split('_');
    if (array.length != 4) return;

    var paoId = array[1];
    var reason = array[3];

    var operationalStateSplit = array[2].split('#');
    var tagDesc = (operationalStateSplit.length == 2) ? operationalStateSplit[1] : array[2];
    executeReasonUpdate(paoId, tagDesc, reason);
} 

function executeReasonUpdate(paoId, tagDesc, reason) {
	new Ajax.Request("/capcontrol/oneline/CBCReasonUpdaterServlet", 
		{
			method:"post", 
			parameters:"id=" + paoId + "&tagDesc=" + tagDesc + "&reason=" + reason, 
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
function executeCapBankCommand(paoId, command, cmd_name, is_manual_state, operationalState) {
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
			parameters:"cmdID=" + command + "&paoID=" + paoId + "&controlType=CAPBANK_TYPE" + "&operationalState=" + operationalState, 
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
    updateBackgroundColor();

	updateSub(xml);
//	say("after sub");
	updateFeeders(xml);
//	say("after feeder");
	updateCaps(xml);
//	say("after caps");

}

function updateBackgroundColor() {
    var back = document.getElementById('backgroundRect');
    if (printableView) {
        back.setAttribute('style', 'fill:white;');
    } else {
        back.setAttribute('style', 'fill:black;');
    }
}

function updateSub(xml) {
	updateImages(SUB_IMAGES, xml);
	updateHiddenTextElements("SubState", xml, ["isDisable", "isVerify", "subDisableReason", "isOVUVDis", "subDisableOVUVReason"]);
	updateDynamicLineElements("Sub", xml);
	updateVisibleText("SubStat", xml);
	updateVisibleText("SubTag", xml);

}
function updateFeeders(xml) {
	updateHiddenTextElements("FeederState", xml, ["isDisable", "disableFdrReason", "isOVUVDis", "disableOVUVFdrReason"]);
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
	updateHiddenTextElements("CapState", xml, ["isDisable", "isOVUVDis", "isStandalone", "isFixed", "isSwitched", "standAloneReason", "disableCapReason", "disableCapOVUVReason", "paoName"]);
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
            xmlText = xmlDynamicEls.item(i);
            xmlID = xmlText.getAttribute("id");
            if (xmlID == id) {
                textEl.getFirstChild().setData(xmlText.text);
                color = xmlText.getAttribute("style");
			    textEl.setAttribute("style", color);
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
    var xmlDynamicEls = xml.getElementsByTagName("text");
	for (var i = 0; i < textEls.length; i++) {
		txtNode = textEls.item(i);
		id = txtNode.getAttribute("id");
		if ((id.split("_")[0] == grpName) && txtNode.getAttribute("elementID") == "HiddenTextElement") {
            xmlText = xmlDynamicEls.item(i);
			xmlID = xmlText.getAttribute("id");
			if ((xmlID == id) && xmlText.getAttribute("elementID") == "HiddenTextElement") {
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
	var htmlTagName = cb.tagName;
    var span = document.getElementById(spanID);
    
	if ((cb.type == "checkbox" && cb.checked) || (htmlTagName == 'SELECT')) {
		var str = "<font style='color:gray'>Reason: <textarea style='vertical-align: bottom' rows=\"2\">";
		if (reason) {
			str += unescape(reason);
		}
		str += "</textarea></font>";
		span.innerHTML = str;
		span.style.display = "inline";
	} else {
		span.style.display = "none";
	}
}
function setReason(name, reason, cb) {
	var spanID = name + "ReasonSpan";
	toggleCB(spanID, cb, reason);
}

function generateReasonText(reason) {
	var str = "";
	str += "\" style=\"display: inline\" >";
	str += "<font style=\"color:gray\">Reason: ";
	str += "<textarea style='color: gray; vertical-align: bottom;' rows=\"2\" readonly>" + unescape(reason) + "</textarea>";
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

function generateCommentField( spanName, comments )
{
	str = "";
	var strList = comments.split(";");
	for (var i = 0 ; i < strList.length; i++) {
		var str2;
		
		if( strList[i].length < 1 )
			break;
		
		if (strList[i].length < 18) {
			str2 = strList[i];
		}else {
			str2 = strList[i].substring( 0, 15 );
			str2 += "...";
		}
			
		str += "<span id=\"" + spanName + "_" + i;
		str += "\" style=\"display: inline\">" + (i+1) + ") ";
		str += str2;
		str += "</span></br>";
	}
	return str;
}


//disables all the checked checkboxes in span
function disableAllCheckedReasons(spanPrefix, id) {
	var span = document.getElementById(spanPrefix + id);
	all = span.getElementsByTagName("span");
	for (var i = 0; i < all.length; i++) {
		el = all[i];
		if (el.style.display != "none") {
			var textAreaArray = el.getElementsByTagName("textarea");
            for (var x = 0; x < textAreaArray.length; x++) {
                textAreaArray[x].setAttribute('readOnly','readOnly');
            }    
		}
	}
}
//object is a checkbox with a name that has it's id in it
//ex: tag_paoID_cmdID
function addCommand(obj) {
    var htmlTagName = obj.tagName;

    if (htmlTagName == 'INPUT') {
		var paoID = paoID = obj.name.split("_")[1];
		var executeQueue = document.getElementById("executeQueue_" + paoID);
		executeQueue.value += obj.name;
		executeQueue.value += ":";
        return;
    }
    
    if (htmlTagName == 'SELECT') {
        var index = obj.selectedIndex;
        var option = obj.options[index];
        var operationState = option.value;
        var nameWithState = obj.name + '#' + operationState;
        
        //command        
        var paoID = paoID = obj.name.split("_")[1];
        var executeQueue = document.getElementById("executeQueue_" + paoID);
        executeQueue.value += nameWithState;
        executeQueue.value += ":";
        
        //tag
        var array = nameWithState.split("_");
        array[0] = 'tag';
        var stringCommand = array.join('_');
        addStringCommand(stringCommand);
        return;
    }

    //obj == String
	if (obj) {
	   addStringCommand(obj);
    }
}

function findCommand(obj, val)
{
	var result = obj.value.split(";");
	for(i = 0; i < result.length; i= i+1){
		var res2 = result[i].split(":");
		if( res2[1] == val )
		{
			return true;
		}
	}
	return false;
}

function addStringCommand(obj) {
	var paoID = paoID = obj.split("_")[1];
	var executeQueue = document.getElementById("executeQueue_" + paoID);
	var found = false;
	var newvalue = "";
	
	if( executeQueue != null ){
		found = findCommand(executeQueue, obj);
	}
	if( !found ){
		executeQueue.value += obj;
		executeQueue.value += ";";
	}else//remove
	{

		// remove the instance of this, and the previous entry.
		var result = executeQueue.value.split(";");

		if( result != null ){
			newvalue = "";
			for(i = 0; i < result.length; i= i+1){

				var res2 = result[i].split(":");
				if( res2.length == 2 ){
					if( (res2[1] != obj) && (res2[1] != "") )
					{
						newvalue += res2[0];
						newvalue += ":";
						newvalue += res2[1];
						newvalue += ";";
					}
				}
			}
			executeQueue.value = newvalue;
		}
	}
}

function findTagSpanForCB(name, allIds) {
	for (var i = 0; i < allIds.length; i= i+1) {
		if ((allIds[i].split("_")[1] == name.split("_")[1]) && (allIds[i].split("_")[0] == ALL_CMD_TYPES.tag)) {
			return allIds[i] + "ReasonSpan";
		}
	}
	return name + "ReasonSpan";
}

//All commands are piled into an executeQueue that exists for every element
//all execute queues are located in a span to separate executeQueue for feeder and executeQueue for capbank
function executeMultipleCommands(spanPrefix, id) {
	var span = document.getElementById(spanPrefix + id);
	var executeQueue = document.getElementById("executeQueue_" + id);
    
    //example: sub_3514_32:tag_3514_subOVUVDisabled;sub_3514_31:tag_3514_subDisabled
    var allGroupedCommands = executeQueue.value.split(";");  
	executeQueue.value = "";
    
    var listofCommands = new Array();
    for (var x = 0; x < allGroupedCommands.length; x++) {
        var group = allGroupedCommands[x];
        if (group == "") continue;
        var commands = group.split(':');
        for (var y = 0; y < commands.length; y++) {
            var command = commands[y];
            var isTag = startsWith(command, 'tag');
            if (isTag) {
                var spanId = stripOperationalState(command) + 'ReasonSpan';
                var reasonSpan = document.getElementById(spanId);
                if (reasonSpan != null) {
                    var textAreaArray = reasonSpan.getElementsByTagName("textarea");
                    if (textAreaArray.length > 0) {
                        var comment = textAreaArray[0].value;
                        if (comment == "") comment = 'EMPTY';
                        command += '_' + comment;
                    }  
                }       
            }
            listofCommands.push(command);    
        }         
    }

	batchExecute(listofCommands);
}

function stripOperationalState(string) {
    var array = string.split('#');
    if (array.length > 0) return array[0];
    return string;
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



