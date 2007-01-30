
var CTL_PNL_LOCK = false;
var CAP_CONTROL_PANEL = "CapControlPanel";
var FEEDER_CONTROL_PANEL = "FeederControlPanel";
var SUB_CONTROL_PANEL = "SubControlPanel";
var LOCKED_EL;
var UPDATE_EVERY = 5000;
var SUB_IMAGES = ["sub_TransformerImg"];

//onload
function initCC() {
	var arr = window.document.getElementsByTagName("text");
	hidePanels(SUB_CONTROL_PANEL);
	hidePanels(FEEDER_CONTROL_PANEL);
	hidePanels(CAP_CONTROL_PANEL);
	updateDrawing();
}
function hidePanels(pnlName) {
	CTL_PNL_LOCK = false;
	var textArr = window.document.getElementsByTagName("text");
	for (var i = 0; i < textArr.length; i++) {
		var textEl = textArr.item(i);
		idAttr = textEl.getAttribute("id");
		temp = idAttr.split("_");
		if (temp[0] == pnlName) {
			toggleControlPanel(textArr.item(0), "off", idAttr);
		}
	}
}
function tglOnClick(elem, pnlName) {
	if (!CTL_PNL_LOCK) {
		toggleControlPanel(elem, "off", pnlName);
		if (CTL_PNL_LOCK) {
			CTL_PNL_LOCK = false;
			toggleControlPanel(elem, "off", pnlName);
		} else {
			toggleControlPanel(elem, "on", pnlName);
			LOCKED_EL = pnlName;
			CTL_PNL_LOCK = true;
		}
	} else {
		if (pnlName == LOCKED_EL) {
			CTL_PNL_LOCK = false;
			LOCKED_EL = "";
			toggleControlPanel(elem, "off", pnlName);
		}
	}
}
function toggleControlPanel(elem, state, pnlName) {
	var textElems = getControlPanel(elem, pnlName);
	if (!CTL_PNL_LOCK) {
		if (state == "on") {
			addBorder(elem);
		} else {
			noBorder(elem);
		}
	}
	for (var i = 0; i < textElems.length; i++) {
		var textElem = textElems[i];
		if (textElem != null) {
			if (!CTL_PNL_LOCK) {
				if (state == "on") {
					aElement = textElem.getParentNode();
					aElement.setAttribute("style", "display:inline");
				} else {
					aElement = textElem.getParentNode();
					aElement.setAttribute("style", "display:none");
				}
			}
		}
	}
}
function getControlPanel(elem, pnlName) {
	var textArr = elem.getOwnerDocument().getElementsByTagName("text");
	var control_pnl = new Array(textArr.length);
	var cnt = 0;
	if (elem.getOwnerDocument() != null) {
		for (i = 0; i < textArr.length; i++) {
			textElem = textArr.item(i);
			idAttr = textElem.getAttribute("id");
			if (idAttr != null && idAttr == pnlName) {
				control_pnl[cnt] = textElem;
				cnt = cnt + 1;
			}
		}
	}
	return control_pnl;
}
//AJAX
function executeSubCommand(paoId, command, cmd_name) {
	new Ajax.Request("/servlet/CBCServlet", {method:"post", parameters:"cmdID=" + command + "&paoID=" + paoId + "&controlType=SUB_TYPE", asynchronous:true});
}
function executeFeederCommand(paoId, command, cmd_name) {
	new Ajax.Request("/servlet/CBCServlet", {method:"post", parameters:"cmdID=" + command + "&paoID=" + paoId + "&controlType=FEEDER_TYPE", asynchronous:true});
}
function executeCapBankCommand(paoId, command, cmd_name, is_manual_state) {
	var RESET_OP_CNT = 12;
	if (is_manual_state) {
		new Ajax.Request("/servlet/CBCServlet", {method:"post", parameters:"opt=" + command + "&cmdID=" + 30 + "&paoID=" + paoId + "&controlType=CAPBANK_TYPE", asynchronous:true});
	} else {
		//special case for reset_op_counts command
		if (command == RESET_OP_CNT) {
			handleOpcountRequest(command, paoId, cmd_name);
		} else {
			new Ajax.Request("/servlet/CBCServlet", {method:"post", parameters:"cmdID=" + command + "&paoID=" + paoId + "&controlType=CAPBANK_TYPE", asynchronous:true});
		}
	}
}
function handleOpcountRequest(command, paoId, cmd_name) {
//var op_cnt = document.getElementById('opcnt_input'+paoId).value;
//make sure that contains a valid number
//if (!isValidOpcount (op_cnt)) {
//	alert ('Op Count value not specified. New Op Count value will be set to 0.');
//	op_cnt = 0;
//}
//else
	//op_cnt = parseInt(op_cnt);
	var op_cnt = 0;
	new Ajax.Request("/servlet/CBCServlet", {method:"post", parameters:"cmdID=" + command + "&paoID=" + paoId + "&controlType=CAPBANK_TYPE&opt=" + op_cnt, asynchronous:true});
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
	updateFeeders(xml);
	updateCaps(xml);
}
function updateSub(xml) {
	updateImages(SUB_IMAGES, xml);
	updateDynamicTextElements(SUB_CONTROL_PANEL, xml);
	updateDynamicLineElements("Sub", xml);
	updateStats("SubStat", xml, false);
}
function updateFeeders(xml) {
	updateDynamicTextElements(FEEDER_CONTROL_PANEL, xml);
	updateDynamicLineElements("OnelineFeeder", xml);
	updateStats("FeederStat", xml, true);
}
function updateStats(prefix, xml, updPairs) {
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
					//id the stats are in pairs, i.e - "KVAR  1.0 / 7.0"
					//1.0 - we already updated
					//7.0 - will update if updPairs is true
					if (updPairs)
					{
						pair = textEls.item(i + 2);
						xmlPair = xmlDynamicEls.item(i + 2);
						pair.getFirstChild().setData(xmlPair.text);
						j = j + 2;
						i = i + 2;
					}
				}
				
			}
		}
	}
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
	updateDynamicTextElements(CAP_CONTROL_PANEL, xml);
	updateStats("CapStat", xml, false);
}
function getSubId() {
	var textArr = window.document.getElementsByTagName("text");
	//gets us the first link in the cap bank table
	//TODO  - make this a property of the text
	subControl = getControlPanel(textArr.item(0), SUB_CONTROL_PANEL)[1];
	aElement = subControl.getParentNode();
	link = aElement.getAttribute("xlink:href");
	return link.split("javascript:executeSubCommand(")[1].split(",")[0];
}
//update utils
function updateDynamicTextElements(grpName, xml) {
	var textEls = document.getElementsByTagName("text");
	for (var i = 0; i < textEls.length; i++) {
		txtNode = textEls.item(i);
		id = txtNode.getAttribute("id");
		if ((id.split("_")[0] == grpName) && txtNode.getAttribute("elementID") == "dynamicText") {
			xmlDynamicEls = xml.getElementsByTagName("text");
			for (var j = 0; j < xmlDynamicEls.length; j++) {
				xmlText = xmlDynamicEls.item(j);
				xmlID = xmlText.getAttribute("id");
				if ((xmlID == id) && xmlText.getAttribute("elementID") == "dynamicText" && i == j) {
					txtNode.getFirstChild().setData(xmlText.text);
					aElement = txtNode.getParentNode();
					xmlAElement = xmlText.parentNode;
					link = xmlAElement.getAttribute("xlink:href");
					aElement.setAttribute("xlink:href", link);
					style = xmlText.getAttribute("style");
					txtNode.setAttribute("style", style);
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

//debug
function say(word) {
	if (word != null) {
		alert(word);
	} else {
		alert("hello!");
	}
}

