/*
 * CapContol functions
 * 
 * Prerequisites: 	jQuery 1.6+
 * 					jQueryUI 1.8+ (for effects)
 */


var UPDATE_EVERY = 5000;

//onload
function initCC() {
	updateDrawing();
}

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

function updateDrawing() {
    var id = getSubId();
    var redirectURL = getRedirectURL();
    var url = '/oneline/OnelineCBCServlet';
    
	new Ajax.Request(url, { method:'POST', parameters: {'id': id, 'redirectURL': redirectURL},
        onSuccess:function (t) {
			var html = t.responseXML;
            updateHTML(html);
			callback();
		},
        onFailure:function () {
          window.location.reload();
		}
    });
}

function callback() {
	setTimeout("updateDrawing()", UPDATE_EVERY);
}
function updateHTML(xml) {
    var back = document.getElementById('backgroundRect');
    if (printableView) {
        back.setAttribute('style', 'fill:white;');
        updatePrintText();
    } else {
        back.setAttribute('style', 'fill:black;');
    	updateSub(xml);
	    updateFeeders(xml);
	    updateCaps(xml);
	    updateWarningImages(xml);
	    updateAlert(xml);
    }   
}

function updatePrintText() {
    var textElements = document.getElementsByTagName("text");
    for (var x = 0; x < textElements.length; x++) {
        var element = textElements.item(x);
        var styleAttribute = element.getAttribute("style");
        
        var match1 = startsWith(styleAttribute, 'fill:rgb(192,192,192);');
        var match2 = startsWith(styleAttribute, 'fill:rgb(255,255,255);');
        if (match1 || match2) {
            applyBlackStyle(element);
        }        
    }
    
    var legendElement = document.getElementById('legend');
    applyBlackStyle(legendElement);
    
    var printElement = document.getElementById('print');
    applyBlackStyle(printElement);       
}

function applyBlackStyle(element) {
    var currentStyle = element.getAttribute('style');
    currentStyle += ';fill:rgb(0,0,0)';
    element.setAttribute("style", currentStyle);
}

function updateAlert(xml) {
	updateVisibleText("AlertsPopup", xml);
}

function updateSub(xml) {
    var imageName = 'OnelineSub_' + getSubId();
    update_Image(imageName, xml);
	updateDynamicLineElements("Sub", xml);
	updateVisibleText("SubStat", xml);
	updateVisibleText("SubTag", xml);

}
function updateFeeders(xml) {
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
	updateVisibleText("CapStat", xml);
	updateVisibleText("CapTag", xml);

}

function updateWarningImages(xml) {
	var images = document.getElementsByTagName("image");
	for (var i = 0; i < images.length; i++) {
		image = images.item(i);
		idAttr = image.getAttribute("id");
		if (idAttr.split("_")[0] == "WarningPopup") {
			update_Image(idAttr, xml);
		}
	}
}

function updateVisibleText(prefix, xml) {
    var textEls = document.getElementsByTagName("text");
    var xmlDynamicEls = xml.getElementsByTagName("text");
    
    for (var i = 0; i < textEls.length; i++) {
        var textEl = textEls.item(i);
        var id = textEl.getAttribute("id");
        
        if (id.split("_")[0] == prefix) {
            var xmlText = xmlDynamicEls.item(i);
            var xmlID = xmlText.getAttribute("id");
            if (xmlID == id) {
                var firstChild = textEl.getFirstChild();
                if (firstChild == null) continue;
                firstChild.setData(xmlText.text);
                color = xmlText.getAttribute("style");
			    textEl.setAttribute("style", color);
			}
		}
	}
}

function getRedirectURL(){
	var redirectElem = $('regenerateElement');
	var url = new String(redirectElem.getAttribute('redirectURL'));
	var end = url.split('redirectURL=');
	return end[1];
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

function updateImages(images, xml) {
	for (i = 0; i < images.length; i++) {
		update_Image(images[i], xml);
	}
}
function update_Image(imageName, xml) {
	var images = document.getElementsByTagName("image");
    var xmlImages = xml.getElementsByTagName("image");
	var xlink = "xlink:href";
    
	for (var i = 0; i < images.length; i++) {
		image = images.item(i);
		idAttr = image.getAttribute("id");
		if (idAttr == imageName) {
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

function display_status(msg, color) {
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
		timeout = 5000;
	} else {
	 	jQuery("#cmdMessageDiv").fadeIn();
		timeout = 3000;
	}
	jQuery("#cmdMessageDiv").show();
	setTimeout ('hideMsgDiv()', timeout);	
}

function hideMsgDiv() {
	jQuery("#cmdMessageDiv").fadeOut();
}



