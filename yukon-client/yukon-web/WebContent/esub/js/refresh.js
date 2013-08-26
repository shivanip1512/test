var svgNS    = "http://www.w3.org/2000/svg";
var xlinkNS  = "http://www.w3.org/1999/xlink";

/* If true we'll do updates */
var doUpdates = true;

var dynamicTextID = 'dynamicText';
var stateImageID = 'stateImage';
var dynamicGraphID = 'dynamicGraph';
var alarmsTableID = 'alarmsTable';
var alarmTextID = 'alarmText';
var lineElementID = 'lineElement';

/* refresh rates of various elements in milliseconds */
var graphRefreshRate = 360 * 1000;
var pointRefreshRate  = 15 * 1000; 
var tableRefreshRate  = 15 * 1000; 
var alarmAudioCheckRate = 5 * 1000;
var blinkerRefreshRate = 15 * 1000;
var blinkerRate = 600;

var allDynamicBlinkers = new Array();
var allDynamicTextElem = new Array();
var allDynamicGraphElem = new Array();
var allAlarmTextElem = new Array();
var allStateImageElem = new Array();
var allLineElem = new Array();

/* Onload, store all the things that we might be displaying alarms for */
var allAlarmDeviceIds = "";
var allAlarmPointIds = "";
var allAlarmAlarmCategoryIds = "";

/* rectangle to indicate an image is a link */
var selectedRect = null;

/* refresh needs to be called once and it will set up
   periodic updates */   
function refresh(evt) {	
	SVGDoc = evt.getTarget().getOwnerDocument();
	
	/* Find all the dynamic elements and store them so 
	   we don't have to look them up again. */
    dynLines = SVGDoc.getElementsByTagName('path');
    for(var k=0; k<dynLines.getLength(); k++)
    {
        var elemID = dynLines.item(k).getAttribute('elementID');
        if(elemID == lineElementID) {
            allLineElem.push(dynLines.item(k));
            allDynamicBlinkers.push(dynLines.item(k));
        }
    }    
        
	dynText = SVGDoc.getElementsByTagName('text');
	for (var i=0; i<dynText.getLength(); i++) 
	{		
		var elemID = dynText.item(i).getAttribute('elementID');
    	if(elemID == dynamicTextID) {  
    		allDynamicTextElem.push(dynText.item(i));
            allDynamicBlinkers.push(dynText.item(i));
		}
		
		if(elemID == alarmTextID) {
			allAlarmTextElem.push(dynText.item(i));
			allAlarmDeviceIds += dynText.item(i).getAttribute('deviceid') + ',';
			allAlarmPointIds += dynText.item(i).getAttribute('pointid') + ',';
			allAlarmAlarmCategoryIds += dynText.item(i).getAttribute('alarmcategoryid') +',';
		}
	}
	
	dynSVG = SVGDoc.documentElement.getElementsByTagName('svg');
	for(i=0; i < dynSVG.getLength(); i++ ) {
		var svgElement = dynSVG.item(i);
		if(svgElement.getAttribute('elementID') == alarmsTableID) {
			allAlarmDeviceIds += svgElement.getAttribute('deviceid') + ',';
			allAlarmPointIds += svgElement.getAttribute('pointid') + ',';
			allAlarmAlarmCategoryIds += svgElement.getAttribute('alarmcategoryid') +',';			
		}
	}
		
	dynImages = SVGDoc.getElementsByTagName('image');	
	for(var j=0; j<dynImages.getLength(); j++)
	{
		var elemID = dynImages.item(j).getAttribute('elementID');
		if(elemID == stateImageID) {
			allStateImageElem.push(dynImages.item(j));
		}
		else
		if(elemID == dynamicGraphID) {
			allDynamicGraphElem.push(dynImages.item(j));
		}		
	}
	
	updateAllTables();
	updateAllBlinkers();
	
	setInterval('updateAllPoints()', pointRefreshRate);
	setInterval('updateAllGraphs()', graphRefreshRate); 
	setInterval('updateAllTables()', tableRefreshRate);
	setInterval('updateAllBlinkers()', blinkerRefreshRate);
	setInterval('blinkAllBlinkers()', blinkerRate);
	setInterval('checkAlarmAudio(allAlarmDeviceIds, allAlarmPointIds, allAlarmAlarmCategoryIds)', alarmAudioCheckRate);
} //end refresh

function updateAllPoints() {
	var i;
	for(i = 0; i < allDynamicTextElem.length; i++) {
		updateNode(allDynamicTextElem[i]);
	}
	
	for(i = 0; i < allAlarmTextElem.length; i++) {
		updateAlarmText(allAlarmTextElem[i]);
	}

	for(i = 0; i < allStateImageElem.length; i++) {
		updateImage(allStateImageElem[i]);
	}
    for(i = 0; i < allLineElem.length; i++){
        updateLine(allLineElem[i]);
    }  
} //end updatePoints

function updateAllGraphs() {
	var i;
	for(i = 0; i < allDynamicGraphElem.length; i++) {
		updateGraph(allDynamicGraphElem[i]);
	}
} //end updateGraphs

function updateAllBlinkers() {
	var i;
	for(i = 0; i < allDynamicBlinkers.length; i++) {
		updateBlinker(allDynamicBlinkers[i]);
	}
} //end updateAllBlinkers

function blinkAllBlinkers(){
	for(i = 0; i < allDynamicBlinkers.length; i++) {
		blinkBlinker(allDynamicBlinkers[i]);
	}
} // end blinkAllBlinkers

function updateAllTables() {
	dynSVG = SVGDoc.documentElement.getElementsByTagName('svg');
	for(i=0; i < dynSVG.getLength(); i++ ) {
		var svgElement = dynSVG.item(i);
		if(svgElement.getAttribute('elementID') == alarmsTableID) {
			updateAlarmsTable(svgElement);
		}
		else {
			continue;
		}
	}
} //end updateTables

function checkAlarmAudio(deviceIds, pointIds, alarmCategoryIds) {
	var dataStr = '{\'deviceIds\': \'' + deviceIds + '\' ,\'pointIds\': \'' + pointIds + '\' ,\'alarmCategoryIds\': \'' + alarmCategoryIds + '\'}';
	
	url = '/servlet/AlarmAudioServlet?referrer=' + window.location;
	
	postURL(url, dataStr, function(obj) {
		if(obj.content == 'true') {
			playAlarmAudio();
		}
		else {
			stopAlarmAudio();
		}
	}); 
}

/* update an individual graph */
function updateGraph(node) {

	url =   "/servlet/GraphGenerator?" + 
			"gdefid=" + node.getAttribute('gdefid') +
			"&view=" + node.getAttribute('view') +
			"&width=" + node.getAttribute('width') +
			"&height=" + node.getAttribute('height') +
			"&format=" + node.getAttribute('format') +
			"&start=" + node.getAttribute('start') + 
			"&period=" + node.getAttribute('period') +
			"&events=" + node.getAttribute('events') +
			"&db=" + node.getAttribute('db') +
			"&option=" + node.getAttribute('option') +
			"&loadfactor=" + node.getAttribute('loadfactor') +
			"&action=EncodeGraph" +
			"&rand=" + Math.random();  //The only reason this is here
			                      //is because unless the url changes
			                      //the plugin won't actually hit the server

	node.setAttributeNS(xlinkNS, 'xlink\:href', url);
} //end updateGraph

/* update an individual table */
function updateAlarmsTable(node,url) {
	var deviceIds = node.getAttribute('deviceid');
	var pointIds = node.getAttribute('pointid');
	var alarmCategoryIds = node.getAttribute('alarmcategoryid');
	
	url =		'/servlet/AlarmsTableGenerator?' +
				'deviceid=' + deviceIds +
				'&pointid=' + pointIds +
				'&alarmcategoryid=' + alarmCategoryIds +
				'&x=' + node.getAttribute('x') + 
				'&y=' + node.getAttribute('y') +
				'&width=' + node.getAttribute('width') +
				'&height=' + node.getAttribute('height') +
				'&acked=' + node.getAttribute('acked') +
				'&events=' + node.getAttribute('events') +
				'&inactive=' + node.getAttribute('inactive') +
				'&referrer=' + window.location;
				
				
	getCtiURL(url,fn2);
	
	function fn2(obj) {   
		if(!obj.success) {
			alert('Alarm table request failed.  Please restart your browser if this continues.');		
			return;
		}
			
		var Newnode = parseXML(obj.content, SVGDoc);

		if(findChild(node) != null) {
			SVGDoc.documentElement.removeChild(node);
			// svg has no z-index that I can find and appears to use the painters algorithm 
			// to draw elements.  That is svg draws the elements in the order they appear in the dom.
			// If we just append the new table we will possibly cover up some buttons
			// But we have to insert it after the black background rect, phew.
			var b = SVGDoc.getElementById("backgroundRect");
			SVGDoc.documentElement.insertBefore(Newnode, b.getNextSibling());
		}
	} // end f2
} // end updateAlarmsTable

function blinkBlinker(node){
	var isBlinking = node.getAttribute('isBlinking');
	if(isBlinking == "yes"){
		var displayState = node.getAttribute('displayState');
		if ( displayState == "none" ) {
			node.getStyle().setProperty('display', 'inline');
			node.setAttribute('displayState', 'inline');
		}else {
			node.getStyle().setProperty('display', 'none');
			node.setAttribute('displayState', 'none');
		}
	}else{
		node.getStyle().setProperty('display', 'inline');
		node.setAttribute('displayState', 'inline');
	}
}

//  Check every svg element that can blink and toggles its blink value
function updateBlinker(node){
	var blink = node.getAttribute('blink');
	var blinkPointID = node.getAttribute('blinkid');

	'&referrer=' + window.location;
	
	if(blinkPointID > 0) {
		url = '/servlet/DynamicTextServlet' + '?' + 'id=' + blinkPointID + '&dattrib=4096' + '&referrer=' + window.location;
        getCtiURL(url, bfn);
	}else if(blink == "1"){
		node.setAttribute('isBlinking', 'yes');
	}else {
		node.setAttribute('isBlinking', 'no');
	 }
	
	function bfn(obj) {
		if(obj.content) {
            var value = obj.content;
            var blink = node.getAttribute('blink'+trim(value));
            if(blink > 0){
            	node.setAttribute('isBlinking', 'yes');
            }else {
            	node.setAttribute('isBlinking', 'no');
            }
        }
    }
}// end updateAllBlinkers

function updateNode(node) {
	if (node.getAttribute('colorid')) {

		var colorPointID = node.getAttribute('colorid');
		if(colorPointID > 0){
	        url = '/servlet/DynamicTextServlet' + '?' + 'id=' + colorPointID + '&dattrib=4096' + '&referrer=' + window.location;
	        getCtiURL(url, cfn);
	    }
    }
    
    if (node.getAttribute('currentstateid')) {
    	
	    var currentStatePointID = node.getAttribute('currentstateid');
	    if(currentStatePointID > 0){
	        url = '/servlet/DynamicTextServlet' + '?' + 'id=' + currentStatePointID + '&dattrib=4096' + '&referrer=' + window.location;
	        getCtiURL(url, tfn);
	    }else { 
	    	if (node.getAttribute('dattrib')) {
		    	url = '/servlet/DynamicTextServlet' + '?' + 'id=' + node.getAttribute('id') + '&' + 'dattrib=' + node.getAttribute('dattrib') + '&referrer=' + window.location;
				getCtiURL(url, fn);
			}
		}
	}else{
		if (node.getAttribute('dattrib')) {
	    	url = '/servlet/DynamicTextServlet' + '?' + 'id=' + node.getAttribute('id') + '&' + 'dattrib=' + node.getAttribute('dattrib') + '&referrer=' + window.location;
			getCtiURL(url, fn);
		}
	}

	function fn(obj) {
		if(obj.content) {
			node.getFirstChild().setData(obj.content);
		}	
	}
	
	function cfn(obj) {
        if(obj.content) {
            var value = obj.content;
            var color = node.getAttribute('color'+trim(value));
            var color_array = color.split(",");
            var red = color_array[0];
            var green = color_array[1];
            var blue = color_array[2];
            node.getStyle().setProperty('fill', 'rgb('+red+','+green+','+blue+')');
        }
    }
	
	function tfn(obj) {
    	if(obj.content) {
    		var value = obj.content;
    		var text = node.getAttribute('string'+trim(value));
    		node.getFirstChild().setData(text);
    	}
    }
} //end updateNode

/* update a single state image */
function updateImage(node) {
	var pointId = node.getAttribute('id');
	if( !isNaN(pointId) ) {
		url = '/servlet/DynamicTextServlet' + '?' + 'id=' + pointId + '&dattrib=4096' + '&referrer=' + window.location;
		getCtiURL(url, fn);		
	}
	
	function fn(obj) {
		if(obj.content) {
			var value = obj.content;
			myString = value.toString();
			myRE = new RegExp("point", "i");
			results = myString.match(myRE);
			if(results == null){
				var imageName = node.getAttribute('image'+trim(value));			
				node.setAttributeNS(xlinkNS, 'xlink:\href', imageName);
			}else{
				// A point not found error occured.
				// Don't try to update the image.
			}
		}
	}
} //end updateImage

/* update a single line element */
function updateLine(node) {
    var colorPointID = node.getAttribute('colorid');
    var thicknessPointID = node.getAttribute('thicknessid');
    var arrowPointID = node.getAttribute('arrowid');
    var opacityPointID = node.getAttribute('opacityid');
    if(colorPointID > 0){
        url = '/servlet/DynamicTextServlet' + '?' + 'id=' + colorPointID + '&dattrib=4096' + '&referrer=' + window.location;
        getCtiURL(url, cfn);
    }
    if(thicknessPointID > 0){
        url = '/servlet/DynamicTextServlet' + '?' + 'id=' + thicknessPointID + '&dattrib=4096' + '&referrer=' + window.location;
        getCtiURL(url, tfn);
    }
    if(arrowPointID > 0){
        url = '/servlet/DynamicTextServlet' + '?' + 'id=' + arrowPointID + '&dattrib=4096' + '&referrer=' + window.location;
        getCtiURL(url, afn);
    }
    if(opacityPointID > 0){
	    url = '/servlet/DynamicTextServlet' + '?' + 'id=' + opacityPointID + '&dattrib=4096' + '&referrer=' + window.location;
	    getCtiURL(url, ofn);
    }
    function cfn(obj) {
        if(obj.content) {
            var value = obj.content;
            var color = node.getAttribute('color'+trim(value));
            var color_array = color.split(",");
            var red = color_array[0];
            var green = color_array[1];
            var blue = color_array[2];
            node.getStyle().setProperty('stroke', 'rgb('+red+','+green+','+blue+')');
        }
    }
    function tfn(obj) {
        if(obj.content){
            var value = obj.content;
            var width = node.getAttribute('width'+trim(value));
            node.getStyle().setProperty('stroke-width', width);
        }
    }
    function afn(obj) {
        if(obj.content){
            var value = obj.content;
            var arrow = node.getAttribute('d'+trim(value));
            if(arrow != null && arrow.length > 0){
                node.setAttributeNS(null, "d", arrow);
            }
        }
    }
    function ofn(obj) {
        if(obj.content){
            var value = obj.content;
            var opacity = node.getAttribute('opacity'+trim(value));
            node.getStyle().setProperty('opacity',opacity);
        }
    }
}

/* update a single alarm text element */
function updateAlarmText(node) {
	var deviceIds = node.getAttribute('deviceid');
	var pointIds = node.getAttribute('pointid');
	var alarmCategoryIds = node.getAttribute('alarmcategoryid');
	var fill1 = node.getAttribute('fill1'); // not alarming
	var fill2 = node.getAttribute('fill2'); // alarming
	var dataStr = '{\'deviceIds\': \'' + deviceIds + '\', \'pointIds\': \'' + pointIds + '\', \'alarmCategoryIds\': \'' + alarmCategoryIds + '\', \'fill1\': \'' + fill1 + '\', \'fill2\': \'' + fill2 + '\'}';
	url = '/servlet/AlarmTextStyleServlet' + '?referrer=' + window.location;
	postURL(url, dataStr, function(obj) {
		if(obj.content) {
			node.getStyle().setProperty('fill', obj.content);			
		}
	});
}

/**
 * Remove white space from a string.
 */
function trim (s) {
	return s.replace(/^\s+|\s+$/g, '');
}

function getCtiURL(url, callback) {
    callback = callback || function(){};

    getURL(url, function(obj) {
        if (obj.success) {
            callback(obj);
        } else {
            stopAlarmAudio();    
        }  
    });
}
